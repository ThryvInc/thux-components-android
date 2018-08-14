package com.thryvinc.thux.network

open class ServerConfiguration(val scheme: String = "https",
                          val host: String,
                          val apiBaseRoute: String = "api/v1",
                          val shouldStub: Boolean) {

    fun urlForEndpoint(endpoint: String): String {
        return "$scheme://$host/$apiBaseRoute/$endpoint"
    }
}
