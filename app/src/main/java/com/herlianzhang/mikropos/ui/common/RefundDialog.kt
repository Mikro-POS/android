package com.herlianzhang.mikropos.ui.common

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.*
import com.herlianzhang.mikropos.R
import com.herlianzhang.mikropos.utils.extensions.formatDate
import com.herlianzhang.mikropos.vo.RefundStock
import com.herlianzhang.mikropos.vo.Stock
import java.util.*

@Composable
fun RefundDialog(
    stock: Stock?,
    isLoading: Boolean,
    isDismiss: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (Int, Int, RefundStock) -> Unit
) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isLoading,
        iterations = LottieConstants.IterateForever
    )
    var value by remember {
        mutableStateOf("")
    }
    var date: Long? by remember {
        mutableStateOf(null)
    }
    val mCalendar by remember {
        mutableStateOf(Calendar.getInstance())
    }
    val mYear: Int = mCalendar.get(Calendar.YEAR)
    val mMonth: Int = mCalendar.get(Calendar.MONTH)
    val mDay: Int = mCalendar.get(Calendar.DAY_OF_MONTH)
    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            mCalendar.set(Calendar.HOUR_OF_DAY, 0)
            mCalendar.set(Calendar.MINUTE, 0)
            mCalendar.set(Calendar.SECOND, 0)
            mCalendar.set(Calendar.MILLISECOND, 0)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, month)
            date = mCalendar.timeInMillis
        }, mYear, mMonth, mDay
    )

    LaunchedEffect(stock) {
        value = stock?.supplierName ?: ""
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
                            "Retur Persediaan",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = value,
                            shape = RoundedCornerShape(12.dp),
                            label = {
                                Text("Nama Pemasok")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            onValueChange = { value = it }
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .weight(1f),
                                value = date?.formatDate("dd MMMM yyyy") ?: "",
                                enabled = false,
                                shape = RoundedCornerShape(12.dp),
                                label = {
                                    Text("Tanggal kedaluwarsa")
                                },
                                onValueChange = {},
                                colors = TextFieldDefaults.textFieldColors(
                                    disabledLabelColor = Color.Gray,
                                    backgroundColor = Color.Transparent,
                                    disabledTextColor = Color.Black
                                )
                            )

                            IconButton(onClick = {
                                mDatePickerDialog.show()
                            }) {
                                Icon(Icons.Rounded.Event, contentDescription = null)
                            }
                        }

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = CircleShape,
                            enabled = !isLoading && value.isNotEmpty() && date != null,
                            onClick = {
                                onSubmit(
                                    stock?.productId ?: 0,
                                    stock?.id ?: 0,
                                    RefundStock(
                                        supplierName = value,
                                        expiredDate = date ?: 0
                                    )
                                )
                            }
                        ) {
                            Text("Retur")
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