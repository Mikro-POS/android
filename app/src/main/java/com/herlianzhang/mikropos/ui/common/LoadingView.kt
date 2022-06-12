package com.herlianzhang.mikropos.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.herlianzhang.mikropos.R

@Composable
fun LoadingView(isShow: Boolean) {
    val interactionSource = remember { MutableInteractionSource() }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isShow,
        iterations = LottieConstants.IterateForever
    )
    val scale = animateFloatAsState(if (isShow) 1f else 0f)
    val alpha = animateFloatAsState(if (isShow) 0.3f else 0f)
    val modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.onBackground.copy(alpha.value))

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
        LottieAnimation(
            modifier = Modifier
                .scale(scale.value)
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colors.background),
            composition = composition,
            progress = progress,
            contentScale = ContentScale.Crop
        )
    }
}