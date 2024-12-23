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
import com.hfad.palamarchuksuperapp.domain.models.Product
import com.hfad.palamarchuksuperapp.ui.viewModels.StoreViewModel

class StoreBasketAdapter (val viewModel: StoreViewModel)
    : ListAdapter<Product, StoreBasketAdapter.ProductBasketItemHolder>(
    ProductBasketItemHolder.ProductDiffItemCallback()
) {

    fun setData(productList: List<Product>) {
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

        fun bind(product: Product) {
            binding.apply {
                quantityToBuy.minValue = 1
                quantityToBuy.maxValue = 255
                quantityToBuy.wrapSelectorWheel = false
                quantityToBuy.setOnValueChangedListener { _, _, newVal ->
                    viewModel.event(
                        event = StoreViewModel.Event.SetItemToBasket(
                            product = product,
                            quantity = newVal
                        )
                    )
                }
            }
            val summ = "%.2f".format(product.productDTO.price/2)
            binding.productPrice.text = binding.root.context.getString(
                R.string.product_sum,
                summ)
            binding.quantityToBuy.value = product.quantity
            binding.productDescription.text = product.productDTO.description

            binding.shapeableImageView.load(product.productDTO.image)
        }

        class ProductDiffItemCallback : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(
                oldItem: Product,
                newItem: Product,
            ): Boolean = oldItem.productDTO.id == newItem.productDTO.id

            override fun areContentsTheSame(
                oldItem: Product,
                newItem: Product,
            ): Boolean {
                return oldItem.quantity == newItem.quantity
            }
        }
    }
}