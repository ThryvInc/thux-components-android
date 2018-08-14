package com.thryvinc.thux.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

open class VolleyManager {
    companion object {
        private var queue: RequestQueue? = null
        var shouldStub: (FunctionalJsonRequest<*>) -> Boolean = { false }

        fun init(context: Context) {
            if (queue == null) queue = Volley.newRequestQueue(context)
        }

        fun <T> addToQueue(request: Request<T>) {
            if (request is FunctionalJsonRequest<*>) {
                if (shouldStub(request)) {
                    stubRequest(request)
                    return
                }
            }
            queue?.add(request)
        }
    }
}
