package com.herlianzhang.mikropos.ui.change_password

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.ui.common.DefaultSnackbar
import com.herlianzhang.mikropos.ui.common.LoadingView
import com.herlianzhang.mikropos.vo.ChangePassword
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading.collectAsState()
    val localFocusManager = LocalFocusManager.current

    var oldPassword by remember {
        mutableStateOf("")
    }
    var isOldPasswordVisible by remember {
        mutableStateOf(false)
    }
    var newPassword by remember {
        mutableStateOf("")
    }
    var isNewPasswordVisible by remember {
        mutableStateOf(false)
    }
    var confirmationPassword by remember {
        mutableStateOf("")
    }
    var isConfirmationPasswordVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ChangePasswordEvent.ShowErrorSnackbar -> {
                    val message = event.message ?: return@collectLatest
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
                is ChangePasswordEvent.ClearUserInput -> {
                    oldPassword = ""
                    newPassword = ""
                    confirmationPassword = ""
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            scaffoldState.snackbarHostState
        },
        topBar = {
            TopAppBar {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                }
                Text(
                    "Ganti Password",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = {
                        if (newPassword != confirmationPassword) {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    "Konfirmasi kata sandi tidak cocok"
                                )
                            }
                            return@IconButton
                        }
                        viewModel.changePassword(
                            ChangePassword(
                                oldPassword = oldPassword,
                                newPassword = newPassword
                            )
                        )
                    },
                    enabled = oldPassword.isNotBlank() && newPassword.isNotBlank() && confirmationPassword.isNotBlank()
                ) {
                    Icon(Icons.Rounded.Done, contentDescription = null)
                }
            }
        }
    ) {
        Box {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = oldPassword,
                    visualTransformation = if (isOldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    label = {
                        Text("Password Lama")
                    },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            isOldPasswordVisible = !isOldPasswordVisible
                        }) {
                            Icon(
                                imageVector = if (isOldPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { localFocusManager.moveFocus(FocusDirection.Down) }
                    ),
                    onValueChange = { oldPassword = it }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = newPassword,
                    visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    label = {
                        Text("Password Baru")
                    },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            isNewPasswordVisible = !isNewPasswordVisible
                        }) {
                            Icon(
                                imageVector = if (isNewPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { localFocusManager.moveFocus(FocusDirection.Down) }
                    ),
                    onValueChange = { newPassword = it }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = confirmationPassword,
                    visualTransformation = if (isConfirmationPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    label = {
                        Text("Konfirmasi Password Baru")
                    },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            isConfirmationPasswordVisible = !isConfirmationPasswordVisible
                        }) {
                            Icon(
                                imageVector = if (isConfirmationPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (oldPassword.isNotBlank() && newPassword.isNotBlank() && confirmationPassword.isNotBlank()) {
                                if (newPassword != confirmationPassword) {
                                    coroutineScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            "Konfirmasi Password tidak cocok"
                                        )
                                    }
                                    return@KeyboardActions
                                }
                                viewModel.changePassword(
                                    ChangePassword(
                                        oldPassword = oldPassword,
                                        newPassword = newPassword
                                    )
                                )
                            }
                        }
                    ),
                    onValueChange = { confirmationPassword = it }
                )
            }
            LoadingView(isLoading)
            DefaultSnackbar(
                snackbarHostState = scaffoldState.snackbarHostState,
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    }
}