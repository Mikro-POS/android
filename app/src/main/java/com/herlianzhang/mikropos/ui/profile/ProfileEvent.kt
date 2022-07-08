package com.herlianzhang.mikropos.ui.profile

sealed class ProfileEvent {
    data class ShowErrorSnackbar(val message: String?) : ProfileEvent()
    object HideDialog : ProfileEvent()
}