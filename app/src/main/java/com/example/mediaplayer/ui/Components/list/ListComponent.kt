package com.example.mediaplayer.ui.Components.list

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.mediaplayer.ui.Components.ItemImage
import com.example.mediaplayer.ui.Components.RegularText
import com.example.mediaplayer.ui.Components.SubTitleText
import com.example.mediaplayer.ui.Components.TitleText
import com.example.mediaplayer.ui.Constant
import com.example.mediaplayer.ui.theme.ItemBackground
import com.example.mpcore.logger.api.MPLoggerApi

@Composable
fun LoadingListItem(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(100.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Box(
                modifier = modifier
                    .size(100.dp)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier.weight(3F)) {
                Box(
                    modifier = modifier
                        .shimmerEffect()
                        .height(20.dp)
                        .width(200.dp)
                        .padding(top = 8.dp, bottom = 8.dp)
                )
                Box(
                    modifier = modifier
                        .shimmerEffect()
                        .height(20.dp)
                        .width(100.dp)
                        .padding(bottom = 8.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1F))
            Box(
                modifier = modifier
                    .shimmerEffect()
                    .height(40.dp)
                    .padding(end = 4.dp)
                    .width(30.dp)
            )
        }
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffSetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = ""
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xC6E6DFDF),
                Color(0xFFA09D9D),
                Color(0xDCE6DFDF)
            ),
            start = Offset(startOffSetX, 0f),
            end = Offset(startOffSetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}

@Composable
fun Item(
    modifier: Modifier = Modifier,
    itemData: ItemData,
    isPlaying: Boolean = false,
    onItemClicked: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(100.dp)
            .clickable {
                onItemClicked()
            }, elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .background(color = ItemBackground)
                .fillMaxSize()
        ) {
            ItemImage(
                imageUri = itemData.imageUri,
                modifier = modifier
                    .height(50.dp)
                    .width(50.dp)
                    .weight(1F)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier.weight(3F)) {
                TitleText(
                    text = itemData.title,
                    modifier = modifier.padding(8.dp),
                    isPlaying = isPlaying
                )
                itemData.subtitle?.let {
                    SubTitleText(text = it, isPlaying = isPlaying)
                }
            }
            Spacer(modifier = Modifier.weight(1F))

            RegularText(text = itemData.endText)
        }
    }
}

@Composable
fun ListComponent(
    modifier: Modifier = Modifier,
    dataList: List<ItemData>,
    isEndReached: Boolean,
    isNextItemLoading: Boolean,
    selectedItem: ItemData? = null,
    onListItemClick: (ItemData) -> Unit,
    loadNextItem: () -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(dataList.size, key = {
            MPLoggerApi.DefaultMpLogger.w(
                Constant.SongList.CLASS_NAME, Constant.SongList.TAG,
                "key", "position: $it item: ${dataList[it]}, id: ${dataList[it].id}"
            )
            dataList[it].id
        }) {
            val item = dataList[it]
            if (it >= dataList.size - 1 && !isEndReached && !isNextItemLoading) {
                MPLoggerApi.DefaultMpLogger.w(
                    Constant.SongList.CLASS_NAME, Constant.SongList.TAG,
                    "SongListScreen", "load nextData"
                )
                LaunchedEffect(key1 = Unit) {
                    loadNextItem()
                }
            }
            if (selectedItem != null && selectedItem.id == item.id) {
                MPLoggerApi.DefaultMpLogger.d(
                    Constant.SongList.CLASS_NAME,
                    Constant.SongList.TAG,
                    "AudioList",
                    "selectedItem: $item"
                )
                Item(itemData = selectedItem, isPlaying = true) {
                    onListItemClick(item)
                }
            } else {
                Item(itemData = item) {
                    onListItemClick(item)
                }
            }
            MPLoggerApi.DefaultMpLogger.d(
                Constant.SongList.CLASS_NAME,
                Constant.SongList.TAG,
                "AudioList",
                "index: $it"
            )
            if (!isEndReached && it >= dataList.size - 1) {
                LoadingListItem()
            }
            if (it >= dataList.size - 1 && isEndReached && !isNextItemLoading) {
                Spacer(modifier = Modifier.padding(bottom = 50.dp))
            }
        }
    }
}