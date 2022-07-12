package com.herlianzhang.mikropos.ui.printer

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.herlianzhang.mikropos.App
import com.herlianzhang.mikropos.utils.UserPreferences
import com.herlianzhang.mikropos.utils.extensions.getPrinter
import com.herlianzhang.mikropos.utils.extensions.printFormattedText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
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

    private val _isPrinterEmpty = MutableStateFlow(false)
    val isPrinterEmpty: StateFlow<Boolean>
        get() = _isPrinterEmpty

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _event = Channel<PrinterListEvent>()
    val event: Flow<PrinterListEvent>
        get() = _event.receiveAsFlow()

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

    fun getPrinters(withAlert: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            try {
                val printers = BluetoothPrintersConnections().list!!
                _printers.emit(printers.toList())
                availablePrinter = userPref.getPrinter(printers)
                _isPrinterAvailable.emit(true)
            } catch(e: Exception) {
                availablePrinter = null
                _isPrinterAvailable.emit(false)
                if (withAlert)
                    _event.send(PrinterListEvent.ShowErrorSnackbar(e.message))
            } finally {
                _isLoading.emit(false)
                _isPrinterEmpty.emit(_printers.value.isEmpty())
            }
        }
    }

    fun connect(address: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            userPref.printerAddress = address
            getPrinters(true)
        }
    }

    fun printTest() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            try {
                availablePrinter!!.printFormattedText(
                    """
                        [C]<b>================================</b>
                        [C]<b>Coba Print</b>
                        [C]<b>================================</b>
                    """.trimIndent()
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