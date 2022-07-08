package com.herlianzhang.mikropos.db.cart

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cart(
    @PrimaryKey val id: Int,
    val name: String?,
    val price: Long?,
    val photo: String?,
    val amount: Int
)