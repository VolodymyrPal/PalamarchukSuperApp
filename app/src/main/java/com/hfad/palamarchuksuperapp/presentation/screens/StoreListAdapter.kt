package com.hfad.palamarchuksuperapp.presentation.screens

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.databinding.ListItemProductBinding
import com.hfad.palamarchuksuperapp.presentation.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.viewModels.StoreViewModel


class StoreListAdapter(
    private val viewModel: StoreViewModel,
    private val fragmentManager: FragmentManager,
) : ListAdapter<ProductDomainRW, StoreListAdapter.ProductHolder> (ProductDiffItemCallback()) {

    private val asyncListDiffer = AsyncListDiffer(this, ProductDiffItemCallback())

    fun setData(productList: List<ProductDomainRW>) {
        asyncListDiffer.submitList(productList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val binding =
            ListItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductHolder(binding, viewModel, fragmentManager)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

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

    class ProductDiffItemCallback : DiffUtil.ItemCallback<ProductDomainRW>() {
        override fun areItemsTheSame(
            oldItem: ProductDomainRW,
            newItem: ProductDomainRW,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ProductDomainRW,
            newItem: ProductDomainRW,
        ): Boolean =
            oldItem.product.id == newItem.product.id
    }
}