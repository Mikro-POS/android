package com.herlianzhang.mikropos.vo

import com.google.gson.annotations.SerializedName

data class Customer(
    val id: Int,
    val name: String?,
    @SerializedName("phone_number_1")
    val phoneNumber: String?,
    val photo: String?
)