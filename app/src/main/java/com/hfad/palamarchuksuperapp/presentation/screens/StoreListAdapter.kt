package com.hfad.palamarchuksuperapp.presentation.screens

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hfad.palamarchuksuperapp.databinding.ListItemProductBinding
import com.hfad.palamarchuksuperapp.presentation.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.viewModels.StoreViewModel

class StoreListAdapter(private val viewModel: StoreViewModel, private val fragmentManager: FragmentManager) :
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
        private val viiewModel: StoreViewModel,
        private val parentFragmentManager: FragmentManager,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductDomainRW) {

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