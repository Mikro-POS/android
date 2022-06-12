package com.herlianzhang.mikropos.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.herlianzhang.mikropos.db.cart.Cart
import com.herlianzhang.mikropos.db.cart.CartDao

@Database(entities = [Cart::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}