package com.herlianzhang.mikropos

import app.cash.turbine.test
import com.herlianzhang.mikropos.db.cart.Cart
import com.herlianzhang.mikropos.ui.cart.CartViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private lateinit var cartViewModel: CartViewModel
    private lateinit var fakeCardRepository: FakeCardRepository
    @Before
    fun setupViewModel() {
        fakeCardRepository = FakeCardRepository()

        cartViewModel = CartViewModel(fakeCardRepository)
    }

    @Test
    fun addition_isCorrect() = runBlocking {
        cartViewModel.isCartEmpty.test {
            fakeCardRepository.testaja.send(emptyList())
            assertEquals(true, awaitItem())
            fakeCardRepository.testaja.send(listOf(Cart(1, "", null, "", 1)))
            assertEquals(false, awaitItem())
        }
    }
}