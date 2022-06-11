package com.herlianzhang.mikropos.utils.extensions

import java.text.NumberFormat
import java.util.*

fun Int?.toRupiah(): String {
    if (this == null) return "-"
    val localId = Locale("in", "ID")
    val formatter = NumberFormat.getCurrencyInstance(localId)
    formatter.maximumFractionDigits = 0
    return formatter.format(this)
}