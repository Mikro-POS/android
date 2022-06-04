package com.herlianzhang.mikropos.utils.extensions

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.herlianzhang.mikropos.utils.UserPreferences
import kotlin.jvm.Throws

@Throws(Exception::class)
fun UserPreferences.getPrinter(connections: Array<BluetoothConnection>? = null): BluetoothConnection {
    val printers = connections ?: BluetoothPrintersConnections().list!!
    if (this.printerAddress.isNullOrBlank())
        throw Exception("printer not selected yet")
    for (printer in printers) {
        if (printer.device.address == this.printerAddress) {
            return printer.connect()
        }
    }
    throw Exception("printer not found")
}