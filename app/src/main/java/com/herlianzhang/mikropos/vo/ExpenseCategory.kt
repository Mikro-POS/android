package com.herlianzhang.mikropos.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExpenseCategory(
    val id: Int,
    val name: String?
) : Parcelable