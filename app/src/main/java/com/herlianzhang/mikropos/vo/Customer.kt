package com.herlianzhang.mikropos.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Customer(
    val id: Int,
    val name: String?,
    @SerializedName("phone_number_1")
    val phoneNumber: String?,
    val photo: String?
) : Parcelable