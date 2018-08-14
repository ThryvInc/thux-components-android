package com.thryvinc.thux

import com.google.gson.Gson
import com.thryvinc.thux.network.responseToModel
import com.thryvinc.thux.network.responseToModels
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test

class ResponseTests {

    @Test
    fun testCreateResponse() {
        val string = "{\"human\":{\"name\": \"Neo\"}}"
        val json = JSONObject(string)
        Assert.assertNotNull(json)

        val jsonString = json.toString()
        val neo = responseToModel<Human>(jsonString)

        Assert.assertEquals("Neo", neo?.name)
    }

    @Test
    fun testIndexResponse() {
        val string = "{\"humans\":[{\"name\": \"Neo\"}]}"
        val json = JSONObject(string)
        Assert.assertNotNull(json)

        val jsonString = json.toString()
        val humans = responseToModels<Human>(jsonString)
        Assert.assertNotNull(humans)
        Assert.assertEquals(1, humans?.size)
        val neo = humans?.first()

        Assert.assertEquals("Neo", neo?.name)
    }
}
