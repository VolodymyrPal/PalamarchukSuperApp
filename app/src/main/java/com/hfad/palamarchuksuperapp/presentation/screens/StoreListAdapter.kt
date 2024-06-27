package com.hfad.palamarchuksuperapp.presentation.screens

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
) :
    ListAdapter<ProductDomainRW, StoreListAdapter.ProductHolder>(ProductDiffItemCallback()) {

    private val asyncListDiffer = AsyncListDiffer(this, ProductDiffItemCallback())

    fun setData(productList: List<ProductDomainRW>) {
        asyncListDiffer.submitList(productList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val binding =
            ListItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductHolder(binding, viewModel, fragmentManager)
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val product = asyncListDiffer.currentList[position]
        holder.bind(product)
    }

    class ProductHolder(
        private val binding: ListItemProductBinding,
        private val viewModel: StoreViewModel,
        private val parentFragmentManager: FragmentManager,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(productDomainRW: ProductDomainRW) {
            binding.quantity.text = productDomainRW.product.title
            var quantity = 0
            binding.quantityPlus.setOnClickListener {
                quantity++
                binding.quantity.text = "$quantity+" + ""
                binding.quantityPlusCard.visibility = View.VISIBLE
                binding.quantityMinusCard.visibility = View.VISIBLE
                binding.quantity.visibility = View.VISIBLE
                binding.quantity
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.quantityPlusCard.visibility = View.INVISIBLE
                    binding.quantityMinusCard.visibility = View.INVISIBLE
                    if (quantity > 0) binding.quantity.visibility =
                        View.VISIBLE else binding.quantity.visibility = View.INVISIBLE
                }, 2000)
            }
            binding.quantityMinus.setOnClickListener {
                if (quantity > 0) quantity--
                binding.quantity.text = if (quantity > 0) "${quantity}+" + "" else "$quantity"
                binding.quantityPlusCard.visibility = View.VISIBLE
                binding.quantityMinusCard.visibility = View.VISIBLE
                binding.quantity.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.quantityPlusCard.visibility = View.INVISIBLE
                    binding.quantityMinusCard.visibility = View.INVISIBLE
                    if (quantity > 0) binding.quantity.visibility =
                        View.VISIBLE else binding.quantity.visibility = View.INVISIBLE
                }, 2000)
            }
        }
    }


    class ProductDiffItemCallback : DiffUtil.ItemCallback<ProductDomainRW>() {
        override fun areItemsTheSame(
            oldItem: ProductDomainRW,
            newItem: ProductDomainRW,
        ): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ProductDomainRW,
            newItem: ProductDomainRW,
        ): Boolean =
            oldItem.product.title == newItem.product.title
    }
}