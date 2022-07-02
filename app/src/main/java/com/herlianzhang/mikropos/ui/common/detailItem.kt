package com.herlianzhang.mikropos.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DetailItem(
    key: String,
    value: String?,
    paddingHorizontal: Dp = 8.dp,
    paddingVertical: Dp = 14.dp,
    valueAlignment: TextAlign = TextAlign.Start,
    onClick: (() -> Unit)? = null
) {
    var modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
    if (onClick != null) {
        modifier = modifier
            .clickable(onClick = onClick)
    }
    modifier = modifier
        .padding(horizontal = paddingHorizontal, vertical = paddingVertical)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            key,
            modifier = Modifier.weight(1f)
        )
        Text(
            value ?: "-",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = valueAlignment
        )
    }
}