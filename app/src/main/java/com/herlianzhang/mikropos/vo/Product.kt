package com.herlianzhang.mikropos.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int,
    val name: String?,
    val price: Int?,
    val photo: String?,
    val totalStock: Int?
) : Parcelable
