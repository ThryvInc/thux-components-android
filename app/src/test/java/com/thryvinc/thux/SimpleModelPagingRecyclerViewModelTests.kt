package com.thryvinc.thux

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.thryvinc.thux.adapters.OnBoundModelRecyclerViewAdapter
import com.thryvinc.thux.adapters.RecyclerItemViewModelInterface
import com.thryvinc.thux.models.Refreshable
import com.thryvinc.thux.view_models.SimpleModelPagingRecyclerViewModel
import org.junit.Assert
import org.junit.Test

class SimpleModelPagingRecyclerViewModelTests: TestWithHumanCalls() {
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

    fun isRefreshingTrue(refreshManager: Refreshable) = isRefreshingEquals(true, refreshManager)
    fun isRefreshingFalse(refreshManager: Refreshable) = isRefreshingEquals(false, refreshManager)
    fun isRefreshingEquals(value: Boolean?, refreshManager: Refreshable): (Any?) -> Unit {
        return {
            Assert.assertEquals(value, refreshManager.isRefreshing)
        }
    }

    @Test
    fun testOnItemsInit() {
        val call = GetHumansCall(successListener, errorListener, simpleHumanStubHolder())
        val callManager = HumanPageableListCallManager(call)
        val adapter = OnBoundModelRecyclerViewAdapter(ArrayList<HumanRecyclerItemViewModel>())

        val vm = SimpleModelPagingRecyclerViewModel(call, ::HumanRecyclerItemViewModel, null, adapter, callManager)
        Assert.assertNotNull(vm.onItems)
    }

    @Test
    fun testRefreshManager() {
        val call = GetHumansCall(successListener, errorListener, simpleHumanStubHolder())
        val callManager = HumanPageableListCallManager(call)
        val adapter = OnBoundModelRecyclerViewAdapter(ArrayList<HumanRecyclerItemViewModel>())

        val vm = SimpleModelPagingRecyclerViewModel(call, ::HumanRecyclerItemViewModel, null, adapter, callManager)

        callManager.isRefreshing = true
        Assert.assertEquals(true, vm.refreshManager?.isRefreshing)

        callManager.isRefreshing = false
        Assert.assertEquals(false, vm.refreshManager?.isRefreshing)
    }

    @Test
    fun testSuccessListenerCalled() {
        val call = GetHumansCall(successListener, errorListener, simpleHumanStubHolder())
        val callManager = HumanPageableListCallManager(call)
        val adapter = OnBoundModelRecyclerViewAdapter(ArrayList<HumanRecyclerItemViewModel>())

        val vm = SimpleModelPagingRecyclerViewModel(call, ::HumanRecyclerItemViewModel, null, adapter, callManager)
        vm.shouldLoadMore = { true }
        Assert.assertNotNull(vm.onItems)

        vm.onItems = isRefreshingTrue(callManager)
        callManager.refresh()
    }

    @Test
    fun testSetAdapterModels() {
        val call = GetHumansCall(successListener, errorListener, simpleHumanStubHolder())
        val callManager = HumanPageableListCallManager(call)
        val adapter = OnBoundModelRecyclerViewAdapter(ArrayList<HumanRecyclerItemViewModel>())

        val vm = SimpleModelPagingRecyclerViewModel(call, ::HumanRecyclerItemViewModel, null, adapter, callManager)
        vm.shouldLoadMore = { true }

        (humans() into (vm.modelToItem intoSecond ::map)) into vm::setAdapterModels
        Assert.assertEquals(0, adapter.itemViewModels.size)

        callManager.isRefreshing = true
        (humans() into (vm.modelToItem intoSecond ::map)) into vm::setAdapterModels
        Assert.assertEquals(2, adapter.itemViewModels.size)

        callManager.isRefreshing = false
        (humans() into (vm.modelToItem intoSecond ::map)) into vm::setAdapterModels
        Assert.assertEquals(4, adapter.itemViewModels.size)

        callManager.isRefreshing = true
        (humans() into (vm.modelToItem intoSecond ::map)) into vm::setAdapterModels
        Assert.assertEquals(2, adapter.itemViewModels.size)
    }

    @Test
    fun testRefresh() {
        val call = GetHumansCall(successListener, errorListener, simpleHumanStubHolder())
        val callManager = HumanPageableListCallManager(call)
        val adapter = OnBoundModelRecyclerViewAdapter(ArrayList<HumanRecyclerItemViewModel>())

        val vm = SimpleModelPagingRecyclerViewModel(call, ::HumanRecyclerItemViewModel, null, adapter, callManager)
        vm.shouldLoadMore = { true }

        val callOnItems = isRefreshingTrue(callManager) injectBefore vm::setAdapterModels

        vm.onItems = callOnItems
        callManager.refresh()
        Assert.assertEquals(null, callManager.isRefreshing)
        Assert.assertEquals(1, adapter.itemViewModels.size)
    }

    @Test
    fun testNextPage() {
        val call = GetHumansCall(successListener, errorListener, simpleHumanStubHolder())
        val callManager = HumanPageableListCallManager(call)
        val adapter = OnBoundModelRecyclerViewAdapter(ArrayList<HumanRecyclerItemViewModel>())

        val vm = SimpleModelPagingRecyclerViewModel(call, ::HumanRecyclerItemViewModel, null, adapter, callManager)
        vm.shouldLoadMore = { true }

        callManager.isRefreshing = true
        arrayListOf(human() into vm.modelToItem) into vm::setAdapterModels
        Assert.assertEquals(1, adapter.itemViewModels.size)

        val callOnItems = isRefreshingFalse(callManager) injectBefore vm::setAdapterModels

        vm.onItems = callOnItems
        call.stubHolder = listHumanStubHolder()
        adapter.onBound?.invoke(0, 1)
        Assert.assertEquals(null, callManager.isRefreshing)
        Assert.assertEquals(3, adapter.itemViewModels.size)
    }
}

class HumanRecyclerItemViewModel(val human: Human): RecyclerItemViewModelInterface {
    override fun configureHolder(holder: RecyclerView.ViewHolder) {

    }

    override fun newViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return HumanViewHolder(parent)
    }

    override fun viewType(): Int {
        return 1
    }

}

class HumanViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
