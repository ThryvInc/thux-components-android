package com.thryvinc.thux.models

import com.thryvinc.thux.network.PagedCall
import com.thryvinc.thux.network.UrlParameteredCall
import com.thryvinc.thux.network.UrlParameteredIndexCall

interface Refreshable {
    var isRefreshing: Boolean?
    fun refresh()
}

interface EmptyPageable: Refreshable {
    fun nextPage()
}

interface ModelPageable<T>: Refreshable {
    fun nextPage(model: T?)
}

open class PageableCallManager<T>(var call: PagedCall<T>? = null): EmptyPageable {
    override var isRefreshing: Boolean? = null
    var page = 0
        set(value) {
            field = value
            call?.page = value
            call?.fire()
        }

    override fun nextPage() {
        isRefreshing = false
        page++
    }

    override fun refresh() {
        isRefreshing = true
        page = 0
    }
}

abstract class ModelPageableCallManager<T, U>(var call: UrlParameteredIndexCall<T>? = null): ModelPageable<U> {
    var pagingUrlParamKey = "before"

    override fun refresh() {
        isRefreshing = true
        call?.urlParams?.remove(pagingUrlParamKey)
        call?.fire()
    }
}

abstract class PageableListCallManager<T>(call: UrlParameteredIndexCall<T>? = null): ModelPageableCallManager<T, T>(call)
