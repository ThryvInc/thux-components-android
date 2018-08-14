package com.thryvinc.thux

import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thryvinc.thux.network.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class IndexCallTests {
    var errorListener: (VolleyError?) -> Unit = {}

    @Before
    fun setup() {
        VolleyManager.shouldStub = { it.stubHolder != null }
    }

    fun simpleHumanStubHolder(): StubHolderInterface {
        val human = Human("Neo")
        val humans = listOf(human)
        val response = mapOf(Pair("humans", humans))
        return StubHolder(stubString = Gson().toJson(response))
    }

    @Test
    fun testGetHumansCall() {
        val successListener: (List<Human>?) -> Unit = {
            Assert.assertNotNull(it)
            Assert.assertEquals(1, it?.size)
            Assert.assertEquals("Neo", it?.first()?.name)
        }
        val call = GetHumansCall(successListener, errorListener, simpleHumanStubHolder())
        call.fire()
    }
}

class GetHumansCall(listener: (List<Human>?) -> Unit,
                    errorListener: (VolleyError?) -> Unit,
                    stubHolder: StubHolderInterface):
        IndexCall<Human>(ServerConfiguration(host = "localhost", shouldStub = true),
                endpoint = "endpoint",
                parseResponse = { responseToModels(it, type = object: TypeToken<List<Human>>() {}.type) },
                listener = listener,
                errorListener = errorListener,
                stubHolder = stubHolder
        )
