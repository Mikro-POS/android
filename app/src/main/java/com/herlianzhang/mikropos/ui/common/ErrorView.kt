package com.herlianzhang.mikropos.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.herlianzhang.mikropos.R

@Composable
fun ErrorView(isShow: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isShow,
        iterations = LottieConstants.IterateForever
    )
    val scale = animateFloatAsState(if (isShow) 1f else 0f)
    val alpha = animateFloatAsState(if (isShow) 0.3f else 0f)
    val modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha.value))

    Box(
        modifier = if (!isShow) modifier else modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isShow,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .scale(scale.value)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LottieAnimation(
                modifier = Modifier
                    .size(150.dp),
                composition = composition,
                progress = progress,
                contentScale = ContentScale.Crop
            )
            Text(
                "Oppss",
                style = MaterialTheme
                    .typography
                    .h4
                    .copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "Terjadi kesalahan, tenang ini hanya sementara. Coba lagi dengan cara menekan tombol dibawah ini",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            TextButton(
                modifier = Modifier.padding(bottom = 24.dp),
                onClick = { onClick() }
            ) {
                Text("Coba Lagi")
            }
        }
    }
}