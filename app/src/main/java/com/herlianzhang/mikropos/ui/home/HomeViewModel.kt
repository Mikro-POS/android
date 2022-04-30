package com.herlianzhang.mikropos.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeEvent {
    object Logout: HomeEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPref: UserPreferences
) : ViewModel() {
    private val _event = Channel<HomeEvent>()
    val event: Flow<HomeEvent>
        get() = _event.receiveAsFlow()

    fun logout() {
        viewModelScope.launch {
            userPref.clearUser()
            _event.send(HomeEvent.Logout)
        }
    }
}