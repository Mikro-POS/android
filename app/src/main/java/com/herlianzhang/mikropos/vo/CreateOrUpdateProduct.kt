package com.herlianzhang.mikropos.vo

enum class ProductKey {
    NAME, PRICE, SKU, PHOTO;

    fun getValue(): String {
        return when (this) {
            NAME -> "name"
            PRICE -> "price"
            SKU -> "sku"
            PHOTO -> "photo"
        }
    }

    companion object {
        fun fromKey(key: String): ProductKey {
            return when (key) {
                "name" -> NAME
                "price" -> PRICE
                "sku" -> SKU
                "photo" -> PHOTO
                else -> throw IllegalArgumentException("Unknown key: $key")
            }
        }
    }
}

data class CreateOrUpdateProduct(
    val name: String? = null,
    val price: Long? = null,
    val sku: String? = null,
    val photo: String? = null
) {
    companion object {
        fun update(key: ProductKey, value: String): CreateOrUpdateProduct {
            return when(key) {
                ProductKey.NAME -> CreateOrUpdateProduct(name = value)
                ProductKey.PRICE -> CreateOrUpdateProduct(price = value.toLong())
                ProductKey.SKU -> CreateOrUpdateProduct(sku = value)
                ProductKey.PHOTO -> CreateOrUpdateProduct(photo = value)
            }
        }
    }
}