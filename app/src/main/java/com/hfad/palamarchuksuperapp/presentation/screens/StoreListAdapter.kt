package com.hfad.palamarchuksuperapp.presentation.screens

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.databinding.ListItemProductBinding
import com.hfad.palamarchuksuperapp.presentation.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.common.ProductToProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.viewModels.StoreViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoreListAdapter(
    private val viewModel: StoreViewModel,
    private val fragmentManager: FragmentManager,
) : ListAdapter<Product, StoreListAdapter.ProductHolder> (ProductDiffItemCallback()) {

    private var products = emptyList<Product>()
    private val adapterScope = CoroutineScope(Dispatchers.Main)

    fun submitData(productFlow: Flow<List<Product>>) {
        adapterScope.launch {
            productFlow.collectLatest { newProducts ->
                withContext(Dispatchers.Default) {
                    val diffResult =
                        DiffUtil.calculateDiff(ProductDiffCallback(products, newProducts))
                    withContext(Dispatchers.Main) {
                        products = newProducts
                        diffResult.dispatchUpdatesTo(this@StoreListAdapter)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val binding =
            ListItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductHolder(binding, viewModel, fragmentManager)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.bind(ProductToProductDomainRW.map(products[position]))
    }

    override fun getItemCount(): Int = products.size

    class ProductHolder(
        private val binding: ListItemProductBinding,
        private val viewModel: StoreViewModel,
        private val parentFragmentManager: FragmentManager,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(productDomainRW: ProductDomainRW) {
            binding.apply {
                productName.text = "${productDomainRW.product.title}"
                productPrice.text = "${productDomainRW.product.price.toString()}$"
                productPriceDiscounted.text = "${(productDomainRW.product.price * 0.5)}$"
                quantity.text = productDomainRW.quantity.toString()
                productImage.load(productDomainRW.product.images.urls.getOrNull(0)) {
                    placeholder(R.drawable.lion_jpg_21)
                    this.error(R.drawable.lion_jpg_21)
                }

                quantityPlusCard.visibility = View.VISIBLE
                quantityMinusCard.visibility = View.VISIBLE
                quantity.visibility = View.VISIBLE

                quantityPlus.setOnClickListener {
                    viewModel.event(event = StoreViewModel.Event.AddProduct(product = productDomainRW))
                    quantity.text = productDomainRW.quantity.toString()
                    quantityPlusCard.visibility = View.VISIBLE
                    quantityMinusCard.visibility = View.VISIBLE
                    quantity.visibility = View.VISIBLE
                }
                quantityMinus.setOnClickListener {
                    quantityPlusCard.visibility = View.VISIBLE
                    quantityMinusCard.visibility = View.VISIBLE
                    quantity.text = ""
                    quantity.visibility = View.VISIBLE
                }
                productPrice.paintFlags =
                    productPriceDiscounted.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }

    private class ProductDiffCallback(
        private val oldList: List<Product>,
        private val newList: List<Product>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }
}

//
//private val asyncListDiffer = AsyncListDiffer(this, ProductDiffItemCallback())
//
//fun setData(productList: List<ProductDomainRW>) {
//    asyncListDiffer.submitList(productList)
//}
//
//override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
//    val binding =
//        ListItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//    return ProductHolder(binding, viewModel, fragmentManager)
//}
//
//override fun getItemCount(): Int = asyncListDiffer.currentList.size
//
//override fun onBindViewHolder(holder: ProductHolder, position: Int) {
//    val product = asyncListDiffer.currentList[position]
//    holder.bind(product)
//}
//
//class ProductHolder(
//    private val binding: ListItemProductBinding,
//    private val viewModel: StoreViewModel,
//    private val parentFragmentManager: FragmentManager,
//) : RecyclerView.ViewHolder(binding.root) {
//
//    fun bind(productDomainRW: ProductDomainRW) {
//
//        binding.apply {
//            productName.text = "${productDomainRW.product.title}"
//            productPrice.text = "${productDomainRW.product.price.toString()}$"
//            productPriceDiscounted.text = "${(productDomainRW.product.price * 0.5)}$"
//            quantity.text = productDomainRW.quantity.toString()
//            productImage.load(productDomainRW.product.images.urls.getOrNull(0)) {
//                placeholder(R.drawable.lion_jpg_21)
//                this.error(R.drawable.lion_jpg_21)
//            }
//
//            quantityPlusCard.visibility = View.VISIBLE
//            quantityMinusCard.visibility = View.VISIBLE
//            quantity.visibility = View.VISIBLE
//
//            quantityPlus.setOnClickListener {
//                viewModel.event(event = StoreViewModel.Event.AddProduct(product = productDomainRW))
//                quantity.text = productDomainRW.quantity.toString()
//                quantityPlusCard.visibility = View.VISIBLE
//                quantityMinusCard.visibility = View.VISIBLE
//                quantity.visibility = View.VISIBLE
//            }
//            quantityMinus.setOnClickListener {
//                quantityPlusCard.visibility = View.VISIBLE
//                quantityMinusCard.visibility = View.VISIBLE
//                quantity.text = ""
//                quantity.visibility = View.VISIBLE
//            }
//            productPrice.paintFlags =
//                productPriceDiscounted.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//        }
//    }
//}


class ProductDiffItemCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(
        oldItem: Product,
        newItem: Product,
    ): Boolean = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: Product,
        newItem: Product,
    ): Boolean =
        oldItem.id == newItem.id
}