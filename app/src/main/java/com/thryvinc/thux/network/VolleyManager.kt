package com.thryvinc.thux.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.thryvinc.thux.models.Environment

open class VolleyManager {
    companion object {
        private var queue: RequestQueue? = null

        fun init(context: Context) {
            if (queue == null) queue = Volley.newRequestQueue(context)
        }

        fun <T> addToQueue(request: Request<T>) {
            if (request is FunctionalJsonRequest<*>) {
                if (request.stubHolder != null && Environment.current.serverConfiguration?.shouldStub == true) {
                    stubRequest(request)
                    return
                }
            }
            queue?.add(request)
        }
    }
}