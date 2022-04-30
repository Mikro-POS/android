package com.herlianzhang.mikropos.ui.home

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(viewModel: HomeViewModel, navigateToLogin: () -> Unit) {
    LaunchedEffect(key1 = Unit) {
        viewModel.event.collectLatest { event ->
            when(event) {
                HomeEvent.Logout -> navigateToLogin()
            }
        }
    }
    Button(onClick = {
        viewModel.logout()
    }) {
        Text("Logout")
    }
}