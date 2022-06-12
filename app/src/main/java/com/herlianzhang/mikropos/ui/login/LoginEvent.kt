package com.herlianzhang.mikropos.ui.login

sealed class LoginEvent {
    data class ShowErrorSnackbar(val message: String?) : LoginEvent()
    object NavigateToHome : LoginEvent()
}