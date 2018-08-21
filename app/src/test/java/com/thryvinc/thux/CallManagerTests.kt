package com.thryvinc.thux

import com.thryvinc.thux.models.PageableListCallManager
import org.junit.Assert
import org.junit.Test

class CallManagerTests: TestWithHumanCalls() {
    @Test
    fun testSetsIsRefreshingAppropriately() {
        val successListener: (List<Human>?) -> Unit = {
            Assert.assertNotNull(it)
            if (it != null) {
                if (it.size == 1) {
                    Assert.assertEquals("Neo", it.first().name)
                } else if (it.size == 2) {
                    Assert.assertEquals("Trinity", it.first().name)
                    Assert.assertEquals("Morpheus", it[1].name)
                } else {
                    assert(it.size == 1 || it.size == 2)
                }
            }
        }

        val call = GetHumansCall(successListener, super.errorListener, simpleHumanStubHolder())
        val callManager = HumanPageableListCallManager(call)

        val isRefreshingTrue: (List<Human>?) -> Unit = {
            Assert.assertEquals(true, callManager.isRefreshing)
        }
        val isRefreshingFalse: (List<Human>?) -> Unit = {
            Assert.assertEquals(false, callManager.isRefreshing)
        }

        Assert.assertEquals(null, callManager.isRefreshing)

        call.listener = isRefreshingTrue injectBefore successListener
        callManager.refresh()

        call.listener = isRefreshingFalse injectBefore successListener
        callManager.nextPage(null)
    }
}

class HumanPageableListCallManager(call: GetHumansCall): PageableListCallManager<Human>(call) {
    override var isRefreshing: Boolean? = null

    override fun nextPage(model: Human?) {
        isRefreshing = false
        call?.urlParams?.put(pagingUrlParamKey, "date") //this would normally be the created_at of the last model
        call?.fire()
    }

}
