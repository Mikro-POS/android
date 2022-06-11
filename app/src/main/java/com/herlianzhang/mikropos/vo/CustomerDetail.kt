package com.herlianzhang.mikropos.vo

import com.google.gson.annotations.SerializedName

data class CustomerDetail(
    val id: Int,
    val name: String?,
    @SerializedName("phone_number_1")
    val phoneNumber: String?,
    @SerializedName("phone_number_2")
    val phoneNumber2: String?,
    val address: String?,
    val photo: String?
)