package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.db.cart.Cart
import com.herlianzhang.mikropos.db.cart.CartDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val dao: CartDao
) {
    val carts = dao.getAll()

    suspend fun getCartById(id: Int) = dao.getCartById(id)

    suspend fun insertCart(cart: Cart) = dao.insertCart(cart)

    suspend fun delete(cart: Cart) = dao.delete(cart)

    suspend fun deleteAll() = dao.deleteAll()
}