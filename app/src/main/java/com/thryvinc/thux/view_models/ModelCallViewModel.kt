package com.thryvinc.thux.view_models

import com.thryvinc.thux.adapters.ModelRecyclerViewAdapter
import com.thryvinc.thux.adapters.OnBoundModelRecyclerViewAdapter
import com.thryvinc.thux.adapters.RecyclerItemViewModelInterface
import com.thryvinc.thux.into
import com.thryvinc.thux.intoSecond
import com.thryvinc.thux.map
import com.thryvinc.thux.models.OnBoundProvider
import com.thryvinc.thux.models.PageableCallManager
import com.thryvinc.thux.network.NetworkCall
import com.thryvinc.thux.network.PagedCall

open class ModelCallViewModel<T>(open var call: NetworkCall<T>?)

open class RecyclerModelCallViewModel<T>(call: NetworkCall<List<T>>?,
                                         var modelToItem: (T) -> RecyclerItemViewModelInterface):
        ModelCallViewModel<List<T>>(call) {
    override var call: NetworkCall<List<T>>? = null
        get() = super.call
        set(value) {
            field = value
            setup()
        }
    var onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)? = null

    fun setup() {
        call?.listener = { models: List<T>? ->
            val onItems = onItems
            if (models != null && onItems != null) {
                val items = models into (modelToItem intoSecond ::map)
                onItems(items)
            }
        }
    }
}

open class PagingRecyclerViewModel<T>(call: PagedCall<List<T>>?,
                                      modelToItem: (T) -> RecyclerItemViewModelInterface,
                                      onBoundAdapter: OnBoundProvider):
        RecyclerModelCallViewModel<T>(call, modelToItem) {
    var pageSize = 20
    var pageTrigger = 5
    var callManager: PageableCallManager<List<T>> = PageableCallManager(call)

    init {
        onBoundAdapter.onBound = { position: Int, outOf: Int ->
            if (position == outOf - pageTrigger && outOf % pageSize == 0) {
                callManager.nextPage()
            }
        }
    }
}

open class SimplePagingRecyclerViewModel<T>(call: PagedCall<List<T>>?,
                                            modelToItem: (T) -> RecyclerItemViewModelInterface,
                                            onBoundModelRecyclerAdapter: OnBoundModelRecyclerViewAdapter):
        PagingRecyclerViewModel<T>(call, modelToItem, onBoundModelRecyclerAdapter) {
    init {
        onItems = {
            onBoundModelRecyclerAdapter.itemViewModels = it
            onBoundModelRecyclerAdapter.notifyDataSetChanged()
        }
    }
}
