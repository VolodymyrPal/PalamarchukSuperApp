package com.hfad.palamarchuksuperapp.ui.screens

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.databinding.ListItemProductBinding
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.ui.viewModels.StoreViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StoreListChildAdapter(
    private val viewModel: StoreViewModel,
) : ListAdapter<ProductDomainRW, StoreListChildAdapter.ProductItemHolder>(ProductItemHolder.ProductDiffItemCallback()) {

    fun setData(productList: List<ProductDomainRW>) {
        submitList(productList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemHolder {
        val binding =
            ListItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductItemHolder(binding, viewModel)
    }

    override fun onBindViewHolder(holder: ProductItemHolder, position: Int) {

    }

    override fun onBindViewHolder(holder: ProductItemHolder, position: Int, payloads: List<Any>) {
        val item = getItem(position)
        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            holder.bind(item)
        } else {
            val bundle = payloads[0] as Bundle
            holder.updateQuantity(bundle, item)
        }
    }


    class ProductItemHolder(
        private val binding: ListItemProductBinding,
        private val viewModel: StoreViewModel,
    ) : RecyclerView.ViewHolder(binding.root) {
        private var timerJob: Job? = null

        private fun onClick() {
            timerJob?.cancel()
            timerJob = viewModel.viewModelScope.launch {
                binding.quantityPlusCard.alpha = 1f
                binding.quantityMinusCard.alpha = 1f
                binding.quantity.alpha = 1f
                delay(2000)
                binding.quantityPlusCard.alpha = 0f
                binding.quantityMinusCard.alpha = 0f
                if (binding.quantity.text == "0") {
                    binding.quantity.alpha = 0f
                } else {
                    binding.quantity.alpha = 1f
                }
            }
        }

        fun updateQuantity(bundle: Bundle, product: ProductDomainRW) {
            val quantity = bundle.getInt(PAYLOAD_QUANTITY)
            binding.quantity.text = quantity.toString()
            binding.quantityPlus.setOnClickListener {
                viewModel.event(
                    event = StoreViewModel.Event.AddProduct(
                        product = product,
                        quantity = 1
                    )
                )
            }
            binding.quantityMinus.setOnClickListener {
                viewModel.event(
                    event = StoreViewModel.Event.AddProduct(
                        product = product,
                        quantity = -1
                    )
                )
            }
            onClick()
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(product: ProductDomainRW) {
            binding.apply {
                productName.text = "${product.product.title}"
                productPrice.text = "${product.product.price}$"
                productPriceDiscounted.text = "${(product.product.price * 0.5)}$"
                quantity.text = product.quantity.toString()

                productRating.rating = product.product.rating.rate.toFloat()

                quantityPlusCard.alpha = 0f
                quantityMinusCard.alpha = 0f
                quantity.alpha = if (product.quantity==0) 0f else 1f

                productImage.load(product.product.image) {
                    size(100)
                    crossfade(true)
                    placeholder(R.drawable.lion_jpg_21)
                    this.error(R.drawable.lion_jpg_21)
                }

                quantityPlusCard.visibility = View.VISIBLE
                quantityMinusCard.visibility = View.VISIBLE
                quantity.visibility = View.VISIBLE

                quantityPlus.setOnLongClickListener {
                    viewModel.event(
                        event = StoreViewModel.Event.SetItemToBasket(
                            product = product,
                            quantity = 1
                        )
                    )
                    false
                }

                var timerJob: Job? = null

                quantityPlus.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            timerJob = viewModel.viewModelScope.launch {
                                delay(500)
                                onClick()
                                while (true) {
                                    delay(20)
                                    viewModel.event(
                                        event = StoreViewModel.Event.AddProduct(
                                            product = product,
                                            quantity = 1
                                        )
                                    )
                                }
                            }
                            true
                        }

                        MotionEvent.ACTION_UP -> {
                            timerJob?.cancel()
                            if (event.downTime - event.eventTime < ViewConfiguration.getLongPressTimeout()) {
                                viewModel.event(
                                    event = StoreViewModel.Event.AddProduct(
                                        product = product,
                                        quantity = 1
                                    )
                                )
                            }
                            true
                        }

                        MotionEvent.ACTION_MOVE -> {
                            true
                        }

                        else -> {
                            timerJob?.cancel()
                            v.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED)
                            Log.d("MotionEvent", "$event")
                            false
                        }
                    }
                }
                productPrice.paintFlags =
                    productPriceDiscounted.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
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

            override fun getChangePayload(
                oldItem: ProductDomainRW,
                newItem: ProductDomainRW,
            ): Any? {
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
}