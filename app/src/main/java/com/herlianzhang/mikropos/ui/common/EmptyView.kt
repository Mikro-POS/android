package com.herlianzhang.mikropos.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.herlianzhang.mikropos.R

@Composable
fun EmptyView(isShow: Boolean) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isShow,
        iterations = LottieConstants.IterateForever
    )
    val scale = animateFloatAsState(if (isShow) 1f else 0f)

    LottieAnimation(
        modifier = Modifier
            .scale(scale.value)
            .size(200.dp),
        composition = composition,
        progress = progress,
        contentScale = ContentScale.Crop
    )
}