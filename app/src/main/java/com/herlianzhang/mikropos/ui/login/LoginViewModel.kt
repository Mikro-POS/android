package com.herlianzhang.mikropos.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.repository.UserRepository
import com.herlianzhang.mikropos.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                        userPref.user = result.data.user
                        _event.send(LoginEvent.NavigateToHome)
                    }
                    is ApiResult.Failed -> _event.send(LoginEvent.ShowErrorSnackbar(result.message))
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                }
            }
        }
    }
}