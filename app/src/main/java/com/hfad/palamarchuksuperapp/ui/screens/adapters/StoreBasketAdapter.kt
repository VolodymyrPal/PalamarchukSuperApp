package com.hfad.palamarchuksuperapp.ui.screens.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.databinding.ListItemBasketProductBinding
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.ui.viewModels.StoreViewModel

class StoreBasketAdapter (val viewModel: StoreViewModel)
    : ListAdapter<ProductDomainRW, StoreBasketAdapter.ProductBasketItemHolder>(
    ProductBasketItemHolder.ProductDiffItemCallback()
) {

    fun setData(productList: List<ProductDomainRW>) {
        Log.d("STORE BASKET ADAPTER", "setData: $productList")
        submitList(productList)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProductBasketItemHolder {

        val binding =
            ListItemBasketProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductBasketItemHolder(viewModel, binding)

    }

    override fun onBindViewHolder(holder: ProductBasketItemHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ProductBasketItemHolder(
        val viewModel: StoreViewModel,
        private val binding: ListItemBasketProductBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductDomainRW) {
            binding.apply {
                quantityToBuy.minValue = 1
                quantityToBuy.maxValue = 255
                quantityToBuy.wrapSelectorWheel = false
                quantityToBuy.setOnValueChangedListener { _, _, newVal ->
                    viewModel.event(
                        event = StoreViewModel.Event.SetItemToBasket(
                            productDomainRW = product,
                            quantity = newVal
                        )
                    )
                }
            }
            val summ = "%.2f".format(product.product.price/2)
            binding.productPrice.text = binding.root.context.getString(
                R.string.product_sum,
                summ)
            binding.quantityToBuy.value = product.quantity
            binding.productDescription.text = product.product.description

            binding.shapeableImageView.load(product.product.image)
        }

        class ProductDiffItemCallback : DiffUtil.ItemCallback<ProductDomainRW>() {
            override fun areItemsTheSame(
                oldItem: ProductDomainRW,
                newItem: ProductDomainRW,
            ): Boolean = oldItem.product.id == newItem.product.id

            override fun areContentsTheSame(
                oldItem: ProductDomainRW,
                newItem: ProductDomainRW,
            ): Boolean {
                return oldItem.quantity == newItem.quantity
            }
        }
    }
}