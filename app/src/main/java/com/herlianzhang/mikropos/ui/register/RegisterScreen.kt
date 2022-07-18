package com.herlianzhang.mikropos.ui.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.herlianzhang.mikropos.ui.common.DefaultSnackbar
import com.herlianzhang.mikropos.ui.common.LoadingView
import kotlinx.coroutines.flow.collectLatest

sealed class RegisterEvent {
    data class ShowErrorSnackbar(val message: String?) : RegisterEvent()
    object NavigateToHome : RegisterEvent()
}

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit
) {
    var username by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }
    var address by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var isPasswordVisible by remember {
        mutableStateOf(false)
    }
    val localFocusManager = LocalFocusManager.current
    val scaffoldState = rememberScaffoldState()
    val isLoading by viewModel.isLoading.collectAsState()
    LaunchedEffect(key1 = Unit) {
        viewModel.event.collectLatest { event ->
            when(event) {
                is RegisterEvent.ShowErrorSnackbar -> {
                    val message = event.message ?: return@collectLatest
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
                is RegisterEvent.NavigateToHome -> navigateToHome()
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            scaffoldState.snackbarHostState
        }
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        "Daftar",
                        style = MaterialTheme
                            .typography
                            .h4
                            .copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.primary
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = username,
                        shape = RoundedCornerShape(12.dp),
                        label = {
                            Text("Username")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { localFocusManager.moveFocus(FocusDirection.Down) }
                        ),
                        onValueChange = { username = it.replace(" ", "") }
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = name,
                        shape = RoundedCornerShape(12.dp),
                        label = {
                            Text("Nama")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { localFocusManager.moveFocus(FocusDirection.Down) }
                        ),
                        onValueChange = { name = it }
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = address,
                        shape = RoundedCornerShape(12.dp),
                        label = {
                            Text("Alamat")
                        },
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { localFocusManager.moveFocus(FocusDirection.Down) }
                        ),
                        onValueChange = { address = it }
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        label = {
                            Text("Password")
                        },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                isPasswordVisible = !isPasswordVisible
                            }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                localFocusManager.clearFocus()
                                if (username.isNotBlank() and password.isNotBlank() and name.isNotBlank())
                                    viewModel.register(
                                        username,
                                        name,
                                        address,
                                        password
                                    )
                            }
                        ),
                        onValueChange = { password = it.replace(" ", "") }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sudah punya akun?")
                        TextButton(onClick = navigateToLogin) {
                            Text("Masuk")
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                Button(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = username.isNotBlank() and password.isNotBlank() and name.isNotBlank(),
                    shape = CircleShape,
                    onClick = {
                        viewModel.register(
                            username,
                            name,
                            address,
                            password
                        )
                    }
                ) {
                    Text("Daftar")
                }
            }
            LoadingView(isLoading)
            DefaultSnackbar(
                snackbarHostState = scaffoldState.snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    }
}