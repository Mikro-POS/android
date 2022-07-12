package com.herlianzhang.mikropos.utils

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.herlianzhang.mikropos.vo.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context,
    val gson: Gson
) {
    private val sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    var accessToken: String?
        get() = sharedPreferences.getString(ACCESS_TOKEN, null)
        set(value) {
            sharedPreferences.edit {
                putString(ACCESS_TOKEN, value)
            }
        }

    var printerAddress: String?
        get() = sharedPreferences.getString(PRINTER_ADDRESS, null)
        set(value) {
            sharedPreferences.edit {
                putString(PRINTER_ADDRESS, value)
            }
        }

    var isAuthenticated: Boolean = !accessToken.isNullOrBlank()

    var user: User?
        get() {
            sharedPreferences.getString(USER_OBJECT, null)?.let { json ->
                return gson.fromJson(json, User::class.java)
            }
            return null
        }
        set(value) {
            sharedPreferences.edit {
                putString(USER_OBJECT, gson.toJson(value))
            }
        }

    fun clearUser() {
        sharedPreferences.edit {
            clear()
        }
    }

    companion object {
        const val NAME = "user"
        const val ACCESS_TOKEN = "access_token"
        const val PRINTER_ADDRESS = "printer_address"
        const val USER_OBJECT = "user_object"
    }
}