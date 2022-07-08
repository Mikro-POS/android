package com.herlianzhang.mikropos.vo

enum class UserKey {
    NAME, LOGO, ADDRESS;

    fun getValue(): String {
        return when (this) {
            NAME -> "name"
            LOGO -> "logo"
            ADDRESS -> "address"
        }
    }

    companion object {
        fun fromKey(key: String): UserKey {
            return when (key) {
                "name" -> NAME
                "logo" -> LOGO
                "address" -> ADDRESS
                else -> throw IllegalArgumentException("Unknown key: $key")
            }
        }
    }
}

data class UpdateUser(
    val name: String? = null,
    val logo: String? = null,
    val address: String? = null
) {
    companion object {
        fun update(key: UserKey, value: String): UpdateUser {
            return when (key) {
                UserKey.NAME -> UpdateUser(name = value)
                UserKey.LOGO -> UpdateUser(logo = value)
                UserKey.ADDRESS -> UpdateUser(address = value)
            }
        }
    }
}
