package com.herlianzhang.mikropos.utils

import java.text.NumberFormat
import java.util.*

fun Int?.toRupiah(): String {
    if (this == null) return "-"
    val localId = Locale("in", "ID")
    val formatter = NumberFormat.getCurrencyInstance(localId)
    return formatter.format(this)
}