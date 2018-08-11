package com.thryvinc.thux.models

import com.thryvinc.thux.network.ServerConfiguration

class Environment {
    var serverConfiguration: ServerConfiguration? = null

    companion object {
        var current = Environment()
    }
}