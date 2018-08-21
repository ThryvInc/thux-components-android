package com.thryvinc.thux

import com.android.volley.VolleyError
import com.google.gson.Gson
import com.thryvinc.thux.network.StubHolder
import com.thryvinc.thux.network.StubHolderInterface
import com.thryvinc.thux.network.VolleyManager
import org.junit.Before

open class TestWithHumanCalls {
    var errorListener: (VolleyError?) -> Unit = {}

    @Before
    fun setup() {
        VolleyManager.shouldStub = { it.stubHolder != null }
    }

    fun human(): Human {
        return Human("Neo")
    }

    fun humans(): List<Human> {
        val humans = arrayListOf(Human("Trinity"), Human("Morpheus"))
        return humans
    }

    fun simpleHumanStubHolder(): StubHolderInterface {
        val humans = listOf(human())
        val response = mapOf(Pair("humans", humans))
        return StubHolder(stubString = Gson().toJson(response), shouldUseSameThread = true)
    }

    fun listHumanStubHolder(): StubHolderInterface {
        val response = mapOf(Pair("humans", humans()))
        return StubHolder(stubString = Gson().toJson(response), shouldUseSameThread = true)
    }
}