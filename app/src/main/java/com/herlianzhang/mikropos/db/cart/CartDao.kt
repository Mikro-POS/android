package com.herlianzhang.mikropos.db.cart

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart")
    fun getAll(): Flow<List<Cart>>

    @Query("SELECT * FROM cart WHERE id=:id")
    suspend fun getCartById(id: Int): Cart?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: Cart)

    @Delete
    suspend fun delete(cart: Cart)

    @Query("DELETE FROM cart")
    suspend fun deleteAll()
}