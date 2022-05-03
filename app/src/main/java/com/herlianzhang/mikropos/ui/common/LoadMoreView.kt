package com.herlianzhang.mikropos.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.herlianzhang.mikropos.R

@Composable
fun LoadMoreView(isPlaying: Boolean) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loadmore))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isPlaying,
        iterations = LottieConstants.IterateForever
    )
    if (isPlaying) {
        LottieAnimation(
            modifier = Modifier.size(48.dp),
            composition = composition,
            progress = progress,
            contentScale = ContentScale.Crop
        )
    }
}