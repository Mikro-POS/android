package com.herlianzhang.mikropos.db.cart

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cart(
    @PrimaryKey val id: Int,
    val name: String?,
    val price: Int?,
    val photo: String?,
    val amount: Int
)