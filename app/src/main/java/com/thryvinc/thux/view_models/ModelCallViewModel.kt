package com.thryvinc.thux.view_models

import com.thryvinc.thux.adapters.ModelRecyclerViewAdapter
import com.thryvinc.thux.adapters.OnBoundAdapter
import com.thryvinc.thux.adapters.OnBoundModelRecyclerViewAdapter
import com.thryvinc.thux.adapters.RecyclerItemViewModelInterface
import com.thryvinc.thux.injectBefore
import com.thryvinc.thux.into
import com.thryvinc.thux.intoSecond
import com.thryvinc.thux.map
import com.thryvinc.thux.models.ModelPageable
import com.thryvinc.thux.models.PageableCallManager
import com.thryvinc.thux.models.Refreshable
import com.thryvinc.thux.network.NetworkCall
import com.thryvinc.thux.network.PagedCall
import com.thryvinc.thux.network.UrlParameteredIndexCall

open class ModelCallViewModel<T>(open var call: NetworkCall<T>?)

open class RecyclerItemCallViewModel<T>(call: NetworkCall<List<T>>?,
                                        var modelToItem: (T) -> RecyclerItemViewModelInterface,
                                        var onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)?):
        ModelCallViewModel<List<T>>(call) {
    override var call: NetworkCall<List<T>>? = null
        get() = super.call
        set(value) {
            field = value
            setup()
        }

    init {
        setup()
    }

    fun setup() {
        val listener = call?.listener
        if (listener != null) {
            val transformModels: (List<T>?) -> Unit = { models: List<T>? ->
                val onItems = onItems
                if (models != null && onItems != null) {
                    val items = models into (modelToItem intoSecond ::map)
                    onItems(items)
                }
            }
            call?.listener = transformModels injectBefore listener
        }
    }
}

open class RecyclerItemAdapterCallViewModel<T>(call: NetworkCall<List<T>>?,
                                               modelToItem: (T) -> RecyclerItemViewModelInterface,
                                               onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)?,
                                               val adapter: ModelRecyclerViewAdapter):
        RecyclerItemCallViewModel<T>(call, modelToItem, onItems) {

    open fun notifyAdapter() {
        adapter.notifyDataSetChanged()
    }
}

open class RefreshableRecyclerItemAdapterViewModel<T>(call: NetworkCall<List<T>>?,
                                                      modelToItem: (T) -> RecyclerItemViewModelInterface,
                                                      onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)? = null,
                                                      adapter: ModelRecyclerViewAdapter,
                                                      var refreshManager: Refreshable?):
        RecyclerItemAdapterCallViewModel<T>(call, modelToItem, onItems, adapter) {

    init {
        super.onItems = onItems ?: ::setAdapterModels injectBefore ::notifyAdapter
    }

    open fun setAdapterModels(items: List<RecyclerItemViewModelInterface>?) {
        if (items != null) {
            if (refreshManager?.isRefreshing == true) {
                adapter.itemViewModels = items
            } else if (refreshManager?.isRefreshing == false) {
                val arrayList = ArrayList(adapter.itemViewModels)
                arrayList.addAll(items)
                adapter.itemViewModels = arrayList
            }
        }
    }

    open fun resetRefreshManager() {
        refreshManager?.isRefreshing = null
    }
}

open class PagingRecyclerViewModel<T>(call: PagedCall<List<T>>?,
                                      modelToItem: (T) -> RecyclerItemViewModelInterface,
                                      onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)? = null,
                                      onBoundAdapter: OnBoundAdapter,
                                      var callManager: PageableCallManager<List<T>> = PageableCallManager(call)):
        RefreshableRecyclerItemAdapterViewModel<T>(call, modelToItem, onItems, onBoundAdapter, callManager) {
    var pageSize = 20
    var pageTrigger = 5
    var shouldLoadMore: (Int, Int) -> Boolean = { position: Int, outOf: Int ->
        position == outOf - pageTrigger && outOf % pageSize == 0
    }

    init {
        onBoundAdapter.onBound = { position: Int, outOf: Int ->
            if (shouldLoadMore(position, outOf)) {
                callManager.nextPage()
            }
        }
    }
}

open class SimplePagingRecyclerViewModel<T>(call: PagedCall<List<T>>?,
                                            modelToItem: (T) -> RecyclerItemViewModelInterface,
                                            onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)? = null,
                                            onBoundModelRecyclerAdapter: OnBoundAdapter):
        PagingRecyclerViewModel<T>(call, modelToItem, onItems, onBoundModelRecyclerAdapter) {

    init {
        setupRefreshManager()
    }

    fun setupRefreshManager() {
        val listener = call?.listener
        if (listener != null) {
            call?.listener = listener injectBefore ::resetRefreshManager
        }
    }
}

open class ModelPagingRecyclerViewModel<T>(call: UrlParameteredIndexCall<T>?,
                                           modelToItem: (T) -> RecyclerItemViewModelInterface,
                                           onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)? = null,
                                           onBoundAdapter: OnBoundAdapter,
                                           var callManager: ModelPageable<T>? = null):
        RefreshableRecyclerItemAdapterViewModel<T>(call, modelToItem, onItems, onBoundAdapter, callManager) {
    var shouldLoadMore: (Int) -> Boolean = { false }
    var modelThatDeterminesNextPage: (() -> T?)? = null

    init {
        onBoundAdapter.onBound = { position: Int, _ ->
            if (shouldLoadMore(position)) {
                val lastModel = modelThatDeterminesNextPage?.invoke()
                callManager?.nextPage(lastModel)
            }
        }
    }
}

open class SimpleModelPagingRecyclerViewModel<T>(call: UrlParameteredIndexCall<T>?,
                                                 modelToItem: (T) -> RecyclerItemViewModelInterface,
                                                 onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)? = null,
                                                 onBoundModelRecyclerAdapter: OnBoundModelRecyclerViewAdapter,
                                                 callManager: ModelPageable<T>? = null):
        ModelPagingRecyclerViewModel<T>(call, modelToItem, onItems, onBoundModelRecyclerAdapter, callManager) {

    init {
        setupRefreshManager()
    }

    fun setupRefreshManager() {
        val listener = call?.listener
        if (listener != null) {
            call?.listener = (listener injectBefore ::resetRefreshManager)
        }
    }
}
