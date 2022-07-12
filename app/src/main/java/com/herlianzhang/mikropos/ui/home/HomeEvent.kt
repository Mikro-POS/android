package com.herlianzhang.mikropos.ui.home

sealed class HomeEvent {
    object Logout: HomeEvent()
    object NavigateToSelectProduct: HomeEvent()
    object NavigateToQRScan: HomeEvent()
}