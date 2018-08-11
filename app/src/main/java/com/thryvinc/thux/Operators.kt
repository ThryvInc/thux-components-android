package com.thryvinc.thux

inline infix fun <T, U, V> ((T) -> U).o(crossinline g: (U) -> V): (T) -> V {
    return { g(this(it)) }
}

inline infix fun <T, U> T.into(crossinline f: (T) -> U): U {
    return f(this)
}

inline infix fun <T, U, V> T.intoFirst(crossinline f: (T, U) -> V): (U) -> V {
    return { f(this, it) }
}

inline infix fun <T, U, V> U.intoSecond(crossinline f: (T, U) -> V): (T) -> V {
    return { f(it, this) }
}

fun <T, U> map(array: List<T>, f: (T) -> U): List<U> {
    return array.map(f)
}

fun <T> identity(): (T) -> T {
    return { it }
}

fun <T> doNothing(t: T) {}

fun <T, U> toNull(t: T): U? {
    return null
}
