package com.herlianzhang.mikropos.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionStock(
    val id: Int,
    val stockId: Int,
    val amount: Int?
) : Parcelable
