package com.herlianzhang.mikropos.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.herlianzhang.mikropos.R

@Composable
fun UploadImageLoadingView(isShow: Boolean, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isShow,
        iterations = LottieConstants.IterateForever
    )
    val scale = animateFloatAsState(if (isShow) 1f else 0f)
    val alpha = animateFloatAsState(if (isShow) 0.3f else 0f)
    val mModifier = modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha.value))

    Box(
        modifier = if (!isShow) mModifier else mModifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isShow,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            modifier = Modifier
                .scale(scale.value)
                .size(84.dp),
            composition = composition,
            progress = progress,
            contentScale = ContentScale.Crop
        )
    }
}