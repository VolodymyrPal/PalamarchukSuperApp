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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.databinding.ListItemProductBinding
import com.hfad.palamarchuksuperapp.databinding.ListItemProductRecyclerBinding
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.ui.viewModels.StoreViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StoreListAdapter(
    private val viewModel: StoreViewModel,
    private val fragmentManager: FragmentManager,
    private val numOfRecyclerRows: Int = 2,
) : ListAdapter<ProductDomainRW, StoreListAdapter.ProductHolder>(ProductHolder.ProductDiffItemCallback()) {

    private val listChildRecycler: MutableList<ChildRecycler> = mutableListOf()

    fun setData(productList: List<ProductDomainRW>) {

        val uniqProducts =
            productList.map { it.product.category }.toSet()  // list of unique categories

        uniqProducts.forEachIndexed { index, s ->
            if (index < numOfRecyclerRows) {
                if (listChildRecycler.getOrNull(index) != null) {
                    listChildRecycler.forEachIndexed { i, list ->
                        list.adapter.setData(
                            productList.filter { it.product.category == s }
                        )
                    }
                } else {
                    listChildRecycler.add(
                        ChildRecycler(
                            data = productList.filter { it.product.category == s },
                            viewModel = viewModel
                        )
                    )
                }
            }
        }
        submitList(productList)
    }


    override fun getItemViewType(position: Int): Int {
        return if (position < listChildRecycler.size) position
        else -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        if (viewType == 1) {
            val binding =
                ListItemProductRecyclerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ProductHolder.ProductRecyclerHolder(binding, viewModel, fragmentManager)
        } else {
            val binding =
                ListItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ProductHolder.ProductItemHolder(binding, viewModel, fragmentManager)
        }
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {}

    override fun onBindViewHolder(holder: ProductHolder, position: Int, payloads: List<Any>) {
        if (holder is ProductHolder.ProductRecyclerHolder) {
            val adapter1 = StoreListChildAdapter(viewModel, fragmentManager)
            val adapter2 = StoreListChildAdapter(viewModel, fragmentManager)

            holder.binding.section1RecyclerView.layoutManager =
                LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            holder.binding.section1RecyclerView.adapter = adapter1

            adapter1.setData(listOne)
        } else {
            val item = getItem(position)
            if (payloads.isEmpty() || payloads[0] !is Bundle) {
                holder.bind(item)
            } else {
                val bundle = payloads[0] as Bundle
                (holder as ProductHolder.ProductItemHolder).updateQuantity(bundle, item)
            }
        }
    }


    sealed class ProductHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun bind(product: ProductDomainRW)

        class ProductRecyclerHolder(
            val binding: ListItemProductRecyclerBinding,
            private val viewModel: StoreViewModel,
            private val parentFragmentManager: FragmentManager,
        ) : ProductHolder(binding.root) {

            val adapter1: StoreListChildAdapter = StoreListChildAdapter(viewModel, parentFragmentManager)
            val adapter2: StoreListChildAdapter = StoreListChildAdapter(viewModel, parentFragmentManager)

            init {
                binding.section1RecyclerView.layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                binding.section1RecyclerView.adapter = adapter1
                Log.d("Was created", "1")
            }


            override fun bind(product: ProductDomainRW) {

            }
        }

        class ProductItemHolder(
            private val binding: ListItemProductBinding,
            private val viewModel: StoreViewModel,
            private val parentFragmentManager: FragmentManager,
        ) : ProductHolder(binding.root) {
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
            override fun bind(product: ProductDomainRW) {
                binding.apply {
                    productName.text = "${product.product.title}"
                    productPrice.text = "${product.product.price}$"
                    productPriceDiscounted.text = "${(product.product.price * 0.5)}$"
                    quantity.text = product.quantity.toString()

                    productRating.rating = product.product.rating.rate.toFloat()

                    quantityPlusCard.alpha = 0f
                    quantityMinusCard.alpha = 0f
                    quantity.alpha = 0f

                    productImage.load(product.product.image) {
                        size(50)
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

data class ChildRecycler(
    val data: List<ProductDomainRW>? = emptyList(),
    val viewModel: StoreViewModel,
    val adapter: StoreListChildAdapter = StoreListChildAdapter(viewModel),
    var holder : StoreListAdapter.ProductHolder.ProductRecyclerHolder? = null
)