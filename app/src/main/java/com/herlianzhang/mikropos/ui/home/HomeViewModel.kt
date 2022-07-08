package com.herlianzhang.mikropos.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.repository.CartRepository
import com.herlianzhang.mikropos.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPref: UserPreferences,
    private val cartRepository: CartRepository
) : ViewModel() {
    private val _event = Channel<HomeEvent>()
    val event: Flow<HomeEvent>
        get() = _event.receiveAsFlow()

    fun navigateToSelectProduct() {
        viewModelScope.launch {
            _event.send(HomeEvent.NavigateToSelectProduct)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPref.clearUser()
            cartRepository.deleteAll()
            _event.send(HomeEvent.Logout)
        }
    }
}