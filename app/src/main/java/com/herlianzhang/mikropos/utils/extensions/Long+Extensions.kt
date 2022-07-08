package com.herlianzhang.mikropos.utils.extensions

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Long?.toRupiah(): String {
    if (this == null) return "Rp-"
    val localId = Locale("in", "ID")
    val formatter = NumberFormat.getCurrencyInstance(localId)
    formatter.maximumFractionDigits = 0
    return formatter.format(this)
}

fun Long?.formatDate(pattern: String = "EEEE, dd MMMM yyyy HH:mm"): String? {
    if (this == null) return null
    val format = SimpleDateFormat(pattern, Locale("in", "ID"))
    return format.format(this)
}