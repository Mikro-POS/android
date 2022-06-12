package com.herlianzhang.mikropos.utils.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

fun String.toCurrency(): String {
    val original = this.replace(".", "").replace("R", "").replace("p", "")
    val prefix = if (original.isNotEmpty()) "Rp" else ""
    var result = ""
    for ((index, value) in original.reversed().withIndex()) {
        result += value
        if ((index + 1) % 3 == 0 && index + 1 != original.length)
            result += "."
    }

    return prefix + result.reversed()
}

fun String.inputCurrency(): String {
    val tmp = this.toLongOrNull() ?: return ""
    return min(tmp, 9999999999).toString()
}

fun String.formatDate(): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
    val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale("in", "ID"))
    val date = inputFormat.parse(this) ?: return null
    return outputFormat.format(date)
}