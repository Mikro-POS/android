package com.herlianzhang.mikropos.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductDetail(
    val id: Int,
    val name: String?,
    val price: Long?,
    val sku: String?,
    val photo: String?,
    val totalStock: Int?
) : Parcelable
