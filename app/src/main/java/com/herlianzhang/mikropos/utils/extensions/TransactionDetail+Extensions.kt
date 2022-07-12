package com.herlianzhang.mikropos.utils.extensions

import com.herlianzhang.mikropos.vo.TransactionDetail
import com.herlianzhang.mikropos.vo.TransactionStatus
import com.herlianzhang.mikropos.vo.User

fun TransactionDetail.printTransactionValue(user: User?, image: String? = null): String {
    fun getData(): String {
        var result = ""

        if (image != null) {
            result += "[C]<img>$image</img>\n\n"
        }
        result += "[C]${user?.address ?: "-"}\n\n"

        result += "[C]================================\n\n"

        result += "[L]${this.createdAt.formatDate("dd MMMM yyyy")}"
        result += "[R]${this.createdAt.formatDate("HH:mm")}\n"

        result += "[L]Nama Pelanggan"
        result += "[R]${this.customer?.name ?: "-"}\n"

        result += "[L]Status"
        result += "[R]${this.status.value}\n\n"

        result += "[C]================================\n\n"

        for (item in this.items) {
            result += "[L]${item.product?.name ?: item.productName} x ${item.amount}"
            result += "[R]${item.price.toRupiah()}\n"
        }

        result += "\n[C]================================\n\n"

        result += "[L]<b>Total</b>"
        result += "[R]<b>${this.totalPrice.toRupiah()}</b>\n\n"

        return result
    }

    fun getQR(): String {
        var result = ""

        result += "[C]================================\n\n"

        result += "[C]<qrcode size='30'>transaction/${this.id}</qrcode>\n"
        result += "[L]\n\n"

        return result
    }

    var result = ""

    result += getData()
    result += getQR()

    if (this.status == TransactionStatus.DEBT) {
        result += "[C]================================\n"

        result += "[C]<b>Sobek</b>\n"

        result += "[C]================================\n\n"

        result += getData()

        result += "[C]================================\n\n\n\n"

        result += "[C]<b>(____________________________)</b>\n"
        result += "[C]<b>Tanda Tangan Penerima</b>\n\n"

        result += getQR()
    }

    return result
}

fun TransactionDetail.printOrderTicketValue(): String {
    var result = ""

    for (item in this.items) {
        result += "[L]<b>#${item.product?.name ?: item.productName}</b>"
        result += "[R]<b>${item.amount}</b>\n\n"

        for (stock in item.stocks) {
            result += "[L]#id: ${stock.stockId}"
            result += "[R]${stock.amount}\n"
        }
        result += "\n"
    }

    result += "[C]================================\n\n"

    result += "[C]<qrcode size='30'>transaction/${this.id}</qrcode>\n"

    result += "[L]\n\n"

    return result
}