package com.herlianzhang.mikropos.vo

data class Token(
    val accessToken: String,
    val tokenType: String,
    val user: User?
)
