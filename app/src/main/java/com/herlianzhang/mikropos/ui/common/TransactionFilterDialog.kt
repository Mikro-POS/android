package com.herlianzhang.mikropos.ui.common

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.herlianzhang.mikropos.utils.extensions.formatDate
import java.util.*

@Composable
fun TransactionFilterDialog(
    isDismiss: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (Long?, Long?) -> Unit
) {
    val context = LocalContext.current

    var startDate: Long? by rememberSaveable {
        mutableStateOf(null)
    }
    var endDate: Long? by rememberSaveable {
        mutableStateOf(null)
    }

    var startCalendar by rememberSaveable {
        mutableStateOf(Calendar.getInstance())
    }
    val startYear: Int = startCalendar.get(Calendar.YEAR)
    val startMonth: Int = startCalendar.get(Calendar.MONTH)
    val startDay: Int = startCalendar.get(Calendar.DAY_OF_MONTH)
    val startDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            startCalendar.set(Calendar.HOUR_OF_DAY, 0)
            startCalendar.set(Calendar.MINUTE, 0)
            startCalendar.set(Calendar.SECOND, 0)
            startCalendar.set(Calendar.MILLISECOND, 0)
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            startCalendar.set(Calendar.YEAR, year)
            startCalendar.set(Calendar.MONTH, month)
            startDate = startCalendar.timeInMillis
        }, startYear, startMonth, startDay
    ).also { dialog ->
        endDate?.let { maxDate ->
            dialog.datePicker.maxDate = maxDate
        }
    }

    var endCalendar by rememberSaveable {
        mutableStateOf(Calendar.getInstance())
    }
    val endYear: Int = endCalendar.get(Calendar.YEAR)
    val endMonth: Int = endCalendar.get(Calendar.MONTH)
    val endDay: Int = endCalendar.get(Calendar.DAY_OF_MONTH)
    val endDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            endCalendar.set(Calendar.HOUR_OF_DAY, 0)
            endCalendar.set(Calendar.MINUTE, 0)
            endCalendar.set(Calendar.SECOND, 0)
            endCalendar.set(Calendar.MILLISECOND, 0)
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            endCalendar.set(Calendar.YEAR, year)
            endCalendar.set(Calendar.MONTH, month)
            endDate = endCalendar.timeInMillis
        }, endYear, endMonth, endDay
    ).also {  dialog ->
        startDate?.let { minDate ->
            dialog.datePicker.minDate = minDate
        }
    }

    if (isDismiss) {
        Dialog(onDismissRequest = {
            onDismiss()
        }) {
            Card(
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        "Saring Waktu",
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
                        Text(
                            startDate?.formatDate("dd MMMM yyyy") ?: "Mulai",
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    1.dp,
                                    if (startDate != null) Color.Black else Color.Gray,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp),
                            color = if (startDate != null) Color.Black else Color.Gray
                        )

                        IconButton(onClick = {
                            startDatePickerDialog.show()
                        }) {
                            Icon(Icons.Rounded.Event, contentDescription = null)
                        }
                    }

                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            endDate?.formatDate("dd MMMM yyyy") ?: "Sampai",
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    1.dp,
                                    if (endDate != null) Color.Black else Color.Gray,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp),
                            color = if (endDate != null) Color.Black else Color.Gray
                        )

                        IconButton(onClick = {
                            endDatePickerDialog.show()
                        }) {
                            Icon(Icons.Rounded.Event, contentDescription = null)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colors.primarySurface)
                            .height(56.dp)
                    ) {
                        TextButton(
                            onClick = {
                                startCalendar = Calendar.getInstance()
                                endCalendar = Calendar.getInstance()
                                startDate = null
                                endDate = null
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Text(
                                "Reset",
                                color = Color.White
                            )
                        }

                        TextButton(
                            onClick = { onSubmit(startDate, endDate) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Text(
                                "Saring",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}