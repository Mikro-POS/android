package com.herlianzhang.mikropos.di

import android.app.Application
import androidx.room.Room
import com.herlianzhang.mikropos.db.AppDatabase
import com.herlianzhang.mikropos.db.cart.CartDao
import com.herlianzhang.mikropos.repository.CardRepository
import com.herlianzhang.mikropos.repository.CartRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideAppDB(app: Application): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "mikropos.db")
            .build()

    @Provides
    fun provideCartDao(db: AppDatabase): CartDao =
        db.cartDao()

    @Provides
    fun provideCardRepository(dao: CartDao): CardRepository = CartRepository(dao)
}