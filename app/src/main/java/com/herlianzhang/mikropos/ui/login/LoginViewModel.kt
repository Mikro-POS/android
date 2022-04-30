package com.herlianzhang.mikropos.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.repository.UserRepository
import com.herlianzhang.mikropos.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class LoginEvent {
    data class ShowErrorSnackbar(val message: String?) : LoginEvent()
    object NavigateToHome : LoginEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPref: UserPreferences
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _event = Channel<LoginEvent>()
    val event: Flow<LoginEvent>
        get() = _event.receiveAsFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            userRepository.login(username, password).collect { result ->
                when(result) {
                    is ApiResult.Success -> {
                        val accessToken = result.data?.accessToken ?: return@collect
                        userPref.accessToken = accessToken
                        _event.send(LoginEvent.NavigateToHome)
                    }
                    is ApiResult.Failed -> _event.send(LoginEvent.ShowErrorSnackbar(result.message))
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                }
            }
        }
    }
}