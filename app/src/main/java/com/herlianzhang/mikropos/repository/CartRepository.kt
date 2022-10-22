package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.db.cart.Cart
import com.herlianzhang.mikropos.db.cart.CartDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface CardRepository {
    val carts: Flow<List<Cart>>
    suspend fun getCartById(id: Int): Cart?
    suspend fun insertCart(cart: Cart)
    suspend fun delete(cart: Cart)
    suspend fun deleteAll()
}

@Singleton
class CartRepository @Inject constructor(
    private val dao: CartDao
) : CardRepository {
    override val carts = dao.getAll()

    override suspend fun getCartById(id: Int) = dao.getCartById(id)

    override suspend fun insertCart(cart: Cart) = dao.insertCart(cart)

    override suspend fun delete(cart: Cart) = dao.delete(cart)

    override suspend fun deleteAll() = dao.deleteAll()
}