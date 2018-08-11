package com.thryvinc.thux.network

import com.thryvinc.thux.into
import com.thryvinc.thux.o
import java.util.*

interface StubHolderInterface {
    val statusCode: Int
    val stubFileName: String?
    val stubString: String?
}

open class StubHolder(override val statusCode: Int = 200,
                      override val stubFileName: String? = null,
                      override val stubString: String? = null) : StubHolderInterface

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
        string into stubber
    } else if (fileName != null) {
        val stubString = FileUtil.readStubStringFromFile(fileName, classLoader)
        stubString into stubber
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
