package com.thryvinc.thux.models

import java.text.SimpleDateFormat
import java.util.*

fun isoFormatter(): SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Locale.US)
fun Date.isoString(): String = isoFormatter().format(this)
fun String.isoDate(): Date = isoFormatter().parse(this)
