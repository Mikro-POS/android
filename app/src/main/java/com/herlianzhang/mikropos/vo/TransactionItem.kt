package com.herlianzhang.mikropos.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionItem(
    val id: Int,
    val product: ProductDetail?,
    val productName: String?,
    val amount: Int?,
    val profit: Long?,
    val price: Long?,
    val stocks: List<TransactionStock>
) : Parcelable
