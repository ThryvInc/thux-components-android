package com.thryvinc.thux.models

import com.thryvinc.thux.network.PagedCall

open class PageableCallManager<T>(var call: PagedCall<T>? = null) {
    var page = 0
    set(value) {
        field = value
        call?.page = value
        call?.fire()
    }

    fun nextPage() {
        page++
    }

    fun refresh() {
        page = 0
    }
}