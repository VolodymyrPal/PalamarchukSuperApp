package com.hfad.palamarchuksuperapp.ui.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class ViewModelAdapter : RecyclerView.Adapter<ViewModelAdapter.ViewHolder>() {

    private val cellMap = hashMapOf<Class<out Any>, CellInfo>()

    protected var detectDiffMoves = false

    var items = arrayOf<Any>()
        set(value) {
            try {
                val callback = DiffCallback(field, value)
                val diffResult = DiffUtil.calculateDiff(callback, detectDiffMoves)
                field = value
                diffResult.dispatchUpdatesTo(this)
            } catch (ex: NotImplementedError) {
                field = value
                notifyDataSetChanged()
            }
        }

    fun cell(
        modelClass: Class<out Any>,
        @LayoutRes layoutId: Int,
        bindingClass: Class<out ViewBinding>,
        binder: (ViewBinding, Any) -> Unit,
    ) {
        cellMap[modelClass] = CellInfo(layoutId, bindingClass, binder)
    }

    protected open fun getViewModel(position: Int) = items[position]

    protected open fun getCellInfo(viewModel: Any): CellInfo {
        return cellMap[viewModel::class.java]
            ?: cellMap.entries.find { it.key.isInstance(viewModel) }?.value
            ?: throw Exception("Cell info for class ${viewModel::class.java.name} not found.")
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return getCellInfo(getViewModel(position)).layoutId
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val cellInfo = cellMap.values.find { it.layoutId == viewType }
            ?: throw IllegalArgumentException("Unknown view type: $viewType")
        val view = inflater.inflate(viewType, parent, false)
        return ViewHolder(view, cellInfo)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getViewModel(position))
    }


    class ViewHolder(view: View, private val cellInfo: CellInfo) :
        RecyclerView.ViewHolder(view) {
        private val binding: ViewBinding = cellInfo.bindingClass.getMethod("bind", View::class.java)
            .invoke(null, view) as ViewBinding

        fun bind(item: Any) {
            @Suppress("UNCHECKED_CAST")
            cellInfo.binder.invoke(binding, item)
        }
    }

    private inner class DiffCallback(
        private val oldItems: Array<Any>,
        private val newItems: Array<Any>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size
        override fun getNewListSize(): Int = newItems.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            areItemsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            areContentsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? =
            getChangePayload(oldItems[oldItemPosition], newItems[newItemPosition])
    }

    protected open fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean = oldItem == newItem
    protected open fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean = oldItem == newItem
    protected open fun getChangePayload(oldItem: Any, newItem: Any): Any? = null

    data class CellInfo(
        val layoutId: Int,
        val bindingClass: Class<out ViewBinding>,
        val binder: (ViewBinding, Any) -> Unit,
    )

}