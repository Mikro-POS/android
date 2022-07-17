package com.herlianzhang.mikropos.utils.extensions

import com.herlianzhang.mikropos.vo.ProductDetail
import com.herlianzhang.mikropos.vo.Stock

fun Stock?.printValue(product: ProductDetail): String {
    return """
        [C]<qrcode size='30'>stock/${this?.id}</qrcode>
        
        
        [C]<b>Nama Produk: ${product.name}</b>
        [C]<b>#ID Persediaan: ${this?.id}</b>
        [C]<b>Total: ${this?.amount} kotak</b>
        
        [C]<b>${this?.createdAt.formatDate() ?: "-"}</b>
    """.trimIndent()
}