package com.herlianzhang.mikropos.ui.change_password

sealed class ChangePasswordEvent {
    data class ShowErrorSnackbar(val message: String?) : ChangePasswordEvent()
    object ClearUserInput : ChangePasswordEvent()
}