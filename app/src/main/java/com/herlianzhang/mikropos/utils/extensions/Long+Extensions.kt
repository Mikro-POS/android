package com.herlianzhang.mikropos.utils.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Long?.formatDate(pattern: String = "EEEE, dd MMMM yyyy HH:mm"): String {
    if (this == null) return "-"
    val format = SimpleDateFormat(pattern, Locale("in", "ID"))
    return format.format(this)
}