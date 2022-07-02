package com.herlianzhang.mikropos.vo

import androidx.compose.ui.graphics.Color

enum class TransactionStatus(val value: String, val color: Color) {
    COMPLETED("Selesai", Color(0xFF03DAC5)),
    DEBT("Belum Lunas", Color(0xFFB00020)),
    LOST("Hilang", Color(0xFFB00020)),
}