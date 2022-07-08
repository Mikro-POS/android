package com.herlianzhang.mikropos.vo

data class Register(
    val username: String,
    val name: String,
    val password: String,
    val logo: String? = null,
    val address: String? = null,
)
