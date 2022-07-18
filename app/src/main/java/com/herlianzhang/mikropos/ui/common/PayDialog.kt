package com.herlianzhang.mikropos.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.*
import com.herlianzhang.mikropos.R
import com.herlianzhang.mikropos.utils.CurrencyVisualTransformation
import com.herlianzhang.mikropos.utils.extensions.inputCurrency

@Composable
fun PayDialog(
    isLoading: Boolean,
    isDismiss: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isLoading,
        iterations = LottieConstants.IterateForever
    )
    var value by remember {
        mutableStateOf("")
    }

    if (isDismiss) {
        Dialog(onDismissRequest = {
            onDismiss()
        }) {
            Card(
                shape = RoundedCornerShape(16.dp),
            ) {
                Box {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            "Bayar Cicilan",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                        )

                        OutlinedTextField(
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = value,
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    onSubmit(value)
                                }
                            ),
                            visualTransformation =  CurrencyVisualTransformation(),
                            onValueChange = {
                                value = it.inputCurrency()
                            }
                        )

                        Button(
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = CircleShape,
                            enabled = !isLoading && value.isNotEmpty(),
                            onClick = { onSubmit(value) }
                        ) {
                            Text("Bayar")
                        }
                    }
                    if (isLoading) {
                        LottieAnimation(
                            modifier = Modifier
                                .size(56.dp)
                                .align(Alignment.TopEnd)
                                .padding(12.dp),
                            composition = composition,
                            progress = progress,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    } else {
        LaunchedEffect(true) {
            value = ""
        }
    }
}