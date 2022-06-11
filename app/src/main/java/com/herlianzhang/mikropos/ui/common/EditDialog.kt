package com.herlianzhang.mikropos.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.*
import com.herlianzhang.mikropos.R
import com.herlianzhang.mikropos.utils.CurrencyVisualTransformation
import com.herlianzhang.mikropos.utils.extensions.inputCurrency

enum class EditDialogType {
    Default,
    QrCode,
    Currency
}

@Composable
fun EditDialog(
    value: String,
    title: String,
    type: EditDialogType,
    keyboardType: KeyboardType? = null,
    isLoading: Boolean,
    changeValue: (String) -> Unit,
    isDismiss: Boolean,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    navigateToScanner: (() -> Unit)? = null
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isLoading,
        iterations = LottieConstants.IterateForever
    )
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
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            title,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .padding(horizontal = 24.dp)
                        )
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                enabled = !isLoading,
                                modifier = Modifier.weight(1f),
                                value = value,
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = keyboardType
                                        ?: if (type == EditDialogType.Default) KeyboardType.Text else KeyboardType.NumberPassword,
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        onSubmit()
                                    }
                                ),
                                visualTransformation = if (type == EditDialogType.Currency) CurrencyVisualTransformation() else VisualTransformation.None,
                                onValueChange = {
                                    if (type == EditDialogType.Currency)
                                        changeValue(it.inputCurrency())
                                    else
                                        changeValue(it)
                                }
                            )
                            if (type == EditDialogType.QrCode) {
                                IconButton(
                                    enabled = !isLoading,
                                    onClick = { navigateToScanner?.invoke() }
                                ) {
                                    Icon(Icons.Rounded.QrCodeScanner, contentDescription = null)
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colors.primarySurface)
                                .height(56.dp)
                        ) {
                            TextButton(
                                onClick = { onDismiss() },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                Text(
                                    "Batal",
                                    color = Color.White
                                )
                            }
                            TextButton(
                                enabled = !isLoading,
                                onClick = { onSubmit() },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                Text(
                                    "Ubah",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
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
    }
}
