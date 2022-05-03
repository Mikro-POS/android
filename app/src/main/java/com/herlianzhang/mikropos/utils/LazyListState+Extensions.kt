package com.herlianzhang.mikropos.utils

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.isScrollToTheEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1