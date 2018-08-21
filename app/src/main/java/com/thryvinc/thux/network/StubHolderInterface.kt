package com.thryvinc.thux.network

import com.thryvinc.thux.into
import com.thryvinc.thux.models.FunctionalAsyncTask
import com.thryvinc.thux.o
import java.util.*

interface StubHolderInterface {
    val statusCode: Int
    val stubFileName: String?
    val stubString: String?
    val shouldUseSameThread: Boolean
}

open class StubHolder(override val statusCode: Int = 200,
                      override val stubFileName: String? = null,
                      override val stubString: String? = null,
                      override val shouldUseSameThread: Boolean = false) : StubHolderInterface

fun <T> stubRequest(request: FunctionalJsonRequest<T>) {
    if (300 > request.stubHolder?.statusCode ?: Int.MAX_VALUE) {
        stubRequestSuccess(request)
    } else {

    }
}

fun <T> stubRequestSuccess(request: FunctionalJsonRequest<T>, classLoader: ClassLoader = FileUtil::class.java.classLoader) {
    val stubber = request.parseResponseString o request.listener
    val stubHolder = request.stubHolder ?: return

    val fileName = stubHolder.stubFileName
    val string = stubHolder.stubString
    if (string != null) {
        if (stubHolder.shouldUseSameThread) {
            string into stubber
        } else {
            FunctionalAsyncTask({ string into request.parseResponseString }, { it into request.listener }).execute()
        }
    } else if (fileName != null) {
        val stubString = FileUtil.readStubStringFromFile(fileName, classLoader)
        if (stubHolder.shouldUseSameThread) {
            stubString into stubber
        } else {
            FunctionalAsyncTask({ stubString into request.parseResponseString }, { it into request.listener }).execute()
        }
    }
}

open class FileUtil {
    companion object {
        fun readStubStringFromFile(fileName: String,
                                   classLoader: ClassLoader = FileUtil::class.java.classLoader): String {
            val raw = classLoader.getResourceAsStream("stubfiles/$fileName")
            return Scanner(raw).useDelimiter("\\A").next()
        }
    }
}
