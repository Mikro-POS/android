package com.herlianzhang.mikropos.utils.extensions

import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import kotlin.jvm.Throws

@Throws(EscPosConnectionException::class, EscPosParserException::class, EscPosEncodingException::class, EscPosBarcodeException::class)
fun BluetoothConnection.printFormattedText(text: String) {
    EscPosPrinter(this, 203, 48f, 32)
        .printFormattedText(text)
}