package com.herlianzhang.mikropos.ui.printer

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.herlianzhang.mikropos.App
import com.herlianzhang.mikropos.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PrinterListViewModel @Inject constructor(
    private val userPref: UserPreferences,
    app: Application
) : AndroidViewModel(app) {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var availablePrinter: BluetoothConnection? = null

    private val _isBluetoothOn = MutableStateFlow(false)
    val isBluetoothOn: StateFlow<Boolean>
        get() = _isBluetoothOn

    private val _printers = MutableStateFlow<List<BluetoothConnection>>(listOf())
    val printers: StateFlow<List<BluetoothConnection>>
        get() = _printers

    private val _isPrinterAvailable = MutableStateFlow(false)
    val isPrinterAvailable: StateFlow<Boolean>
        get() = _isPrinterAvailable

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    init {
        viewModelScope.launch(Dispatchers.IO) {
            bluetoothAdapter = (getApplication<App>().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
            checkBluetooth()
        }
    }

    fun checkBluetooth() {
        viewModelScope.launch(Dispatchers.IO) {
            _isBluetoothOn.emit(bluetoothAdapter?.isEnabled == true)
        }
    }

    fun getPrinters() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            val printers = BluetoothPrintersConnections().list ?: return@launch
            for (printer in printers) {
                if (printer.device.address == userPref.printerAddress) {
                    try {
                        printer.connect()
                        _isPrinterAvailable.emit(true)
                        availablePrinter = printer
                    } catch(_: Exception) {
                        _isPrinterAvailable.emit(false)
                        availablePrinter = null
                        Timber.d("masuk fail connect")

                    }
                }
            }
            _printers.emit(printers.toList())
            _isLoading.emit(false)
        }
    }

    fun connect(address: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            userPref.printerAddress = address
            getPrinters()
        }
    }

    fun printTest() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            try {
                val printer = availablePrinter?.connect()!!
                EscPosPrinter(printer, 203, 48f, 32)
                    .printFormattedText(
                        "[C]<b>================================</b>\n" +
                                "[C]<b>Coba Print</b>\n" +
                                "[C]<b>================================</b>"
                    )
                _isLoading.emit(false)
            } catch(_: Exception) {
                availablePrinter = null
                _isPrinterAvailable.emit(false)
                getPrinters()
            }
        }
    }
}