package com.herlianzhang.mikropos.vo

enum class CustomerKey {
    NAME, PHONE_NUMBER_1, PHONE_NUMBER_2, ADDRESS, PHOTO;

    fun getValue(): String {
        return when (this) {
            NAME -> "name"
            PHONE_NUMBER_1 -> "phone_number_1"
            PHONE_NUMBER_2 -> "phone_number_2"
            ADDRESS -> "address"
            PHOTO -> "photo"
        }
    }

    companion object {
        fun fromKey(key: String): CustomerKey {
            return when (key) {
                "name" -> NAME
                "phone_number_1" -> PHONE_NUMBER_1
                "phone_number_2" -> PHONE_NUMBER_2
                "address" -> ADDRESS
                "photo" -> PHOTO
                else -> throw IllegalArgumentException("Unknown key: $key")
            }
        }
    }
}

data class CreateOrUpdateCustomer(
    val name: String? = null,
    val phoneNumber_1: String? = null,
    val phoneNumber_2: String? = null,
    val address: String? = null,
    val photo: String? = null
) {
    companion object {
        fun update(key: CustomerKey, value: String): CreateOrUpdateCustomer {
            return when (key) {
                CustomerKey.NAME -> CreateOrUpdateCustomer(name = value)
                CustomerKey.PHONE_NUMBER_1 -> CreateOrUpdateCustomer(phoneNumber_1 = value)
                CustomerKey.PHONE_NUMBER_2 -> CreateOrUpdateCustomer(phoneNumber_2 = value)
                CustomerKey.ADDRESS -> CreateOrUpdateCustomer(address = value)
                CustomerKey.PHOTO -> CreateOrUpdateCustomer(photo = value)
            }
        }
    }
}