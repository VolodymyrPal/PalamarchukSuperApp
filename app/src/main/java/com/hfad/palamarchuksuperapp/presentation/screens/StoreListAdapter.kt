package com.hfad.palamarchuksuperapp.presentation.screens

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
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
) : ListAdapter<ProductDomainRW, StoreListAdapter.ProductHolder>(ProductDiffItemCallback()) {

    fun setData(productList: List<ProductDomainRW>) {
        submitList(productList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val binding =
            ListItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductHolder(binding, viewModel, fragmentManager)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int, payloads: List<Any>) {
        val item = getItem(position)
        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            holder.bind(item)
        } else {
            val bundle = payloads[0] as Bundle
            holder.updateQuantity(bundle)
        }
    }

    class ProductHolder(
        private val binding: ListItemProductBinding,
        private val viewModel: StoreViewModel,
        private val parentFragmentManager: FragmentManager,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun updateQuantity(bundle: Bundle) {
            val quantity = bundle.getInt(PAYLOAD_QUANTITY)
            binding.quantity.text = quantity.toString()
        }

        fun bind(product: ProductDomainRW) {
            binding.apply {
                productName.text = "${product.product.title}"
                productPrice.text = "${product.product.price}$"
                productPriceDiscounted.text = "${(product.product.price * 0.5)}$"
                quantity.text = product.quantity.toString()

                productImage.load(product.product.images.urls.getOrNull(0)) {
                    placeholder(R.drawable.lion_jpg_21)
                    this.error(R.drawable.lion_jpg_21)
                }

                quantityPlusCard.visibility = View.VISIBLE
                quantityMinusCard.visibility = View.VISIBLE
                quantity.visibility = View.VISIBLE

                quantityPlus.setOnClickListener {
                    viewModel.event(
                        event = StoreViewModel.Event.AddProduct(
                            product = product,
                            quantity = 1
                        )
                    )
                    quantityPlusCard.visibility = View.VISIBLE
                    quantityMinusCard.visibility = View.VISIBLE
                    quantity.visibility = View.VISIBLE
                }

                quantityMinus.setOnClickListener {
                    viewModel.event(
                        event = StoreViewModel.Event.AddProduct(
                            product = product,
                            quantity = -1
                        )
                    )
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
        ): Boolean = oldItem.product.id == newItem.product.id

        override fun areContentsTheSame(
            oldItem: ProductDomainRW,
            newItem: ProductDomainRW,
        ): Boolean {
            return oldItem.quantity == newItem.quantity
        }

        override fun getChangePayload(oldItem: ProductDomainRW, newItem: ProductDomainRW): Any? {
            Log.d("Was called: ", "${oldItem.product.id}")
            if (oldItem.product.id == newItem.product.id) {
                return if (oldItem.quantity == newItem.quantity) {
                    super.getChangePayload(oldItem, newItem)
                } else {
                    val diff = Bundle()
                    diff.putInt(PAYLOAD_QUANTITY, newItem.quantity)
                    diff
                }
            }
            return super.getChangePayload(oldItem, newItem)
        }
    }

    companion object {
        private const val PAYLOAD_QUANTITY = "payload_quantity"
    }
}