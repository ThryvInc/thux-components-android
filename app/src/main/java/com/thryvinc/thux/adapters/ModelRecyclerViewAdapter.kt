package com.thryvinc.thux.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

abstract class RecyclerItemViewModel<T>(val model: T) {
    abstract fun configureHolder(holder: RecyclerView.ViewHolder)
    abstract fun newViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun viewType(): Int
}

abstract class ModelRecyclerViewAdapter(val itemViewModels: List<RecyclerItemViewModel<*>>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
