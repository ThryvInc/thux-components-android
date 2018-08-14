package com.thryvinc.thux.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.thryvinc.thux.models.OnBoundProvider

interface RecyclerItemViewModelInterface {
    fun configureHolder(holder: RecyclerView.ViewHolder)
    fun newViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun viewType(): Int
}

abstract class RecyclerItemViewModel<T>(val model: T): RecyclerItemViewModelInterface

open class ModelRecyclerViewAdapter(var itemViewModels: List<RecyclerItemViewModelInterface>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return itemViewModels.size
    }

    override fun getItemViewType(position: Int): Int {
        return itemViewModels[position].viewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return itemViewModels.first { it.viewType() == viewType }.newViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewModel = itemViewModels[position]
        itemViewModel.configureHolder(holder)
    }
}

open class OnBoundModelRecyclerViewAdapter(itemViewModels: List<RecyclerItemViewModel<*>>): ModelRecyclerViewAdapter(itemViewModels), OnBoundProvider {
    var _onBound: ((Int, Int) -> Unit)? = null
    override var onBound: ((Int, Int) -> Unit)?
        get() = _onBound
        set(value) {
            _onBound = value
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val onBound = _onBound
        if (onBound != null) {
            onBound(position, itemViewModels.size)
        }
    }

}
