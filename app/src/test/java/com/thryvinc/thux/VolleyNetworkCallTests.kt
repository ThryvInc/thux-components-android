package com.thryvinc.thux

import com.android.volley.VolleyError
import com.google.gson.Gson
import com.thryvinc.thux.network.*
import org.junit.Assert
import org.junit.Test

class VolleyNetworkCallTests {

    var successListener: (Human?) -> Unit = {}
    var errorListener: (VolleyError?) -> Unit = {}

    fun setup() {
        VolleyManager.shouldStub = { it.stubHolder != null }
    }

    fun simpleNetworkCall(stubHolder: StubHolderInterface): NetworkCall<Human> {
        setup()

        val call = NetworkCall<Human>(serverConfiguration = ServerConfiguration(host = "localhost", shouldStub = true),
                endpoint = "endpoint", parseResponseString = Gson()::fromJsonString,
                listener = successListener, errorListener = errorListener, stubHolder = stubHolder)

        return call
    }

    fun simpleHumanStubHolder(): StubHolderInterface {
        val human = Human("Neo")
        return StubHolder(stubString = Gson().toJson(human))
    }

    @Test
    fun setHeaders_succeeds() {
        val call = simpleNetworkCall(simpleHumanStubHolder())
        call.applyHeaders = lambda@{
            it["key"] = "value"
            return@lambda it
        }
        val request = call.createRequest()

        Assert.assertEquals(request.headers["key"], "value")
    }

    @Test
    fun url_isCorrect() {
        val call = simpleNetworkCall(simpleHumanStubHolder())
        val request = call.createRequest()

        Assert.assertEquals(request.url, "https://localhost/api/v1/endpoint")
    }

    @Test
    fun stubbing_succeeds() {
        successListener = {
            Assert.assertEquals(it?.name, "Neo")
        }

        val call = simpleNetworkCall(simpleHumanStubHolder())
        call.fire()
    }
}

data class Human(var name: String?)
