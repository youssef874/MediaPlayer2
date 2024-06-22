package com.example.mediaplayer.ui.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mediaplayer.domain.model.UiAudio
import com.example.mediaplayer.ui.Constant
import com.example.mediaplayer.ui.theme.LightBlue
import com.example.mpcore.logger.api.MPLoggerApi

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    currentPlayingSong: UiAudio,
    isPlaying: Boolean = false,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClick: () -> Unit,
    onPlayButtonClick: () -> Unit,
    onViewClicked: () -> Unit
) {
    MPLoggerApi.DefaultMpLogger.i(
        className = Constant.SongList.CLASS_NAME,
        tag = Constant.SongList.TAG,
        methodName = "BottomBar",
        msg = "display it with $currentPlayingSong"
    )
    BottomAppBar(modifier = modifier
        .clip(RoundedCornerShape(24.dp))
        .height(70.dp)
        .clickable {
            onViewClicked()
        }
        .scrollable(orientation = Orientation.Horizontal, state = rememberScrollableState {
            if (it > 5) {
                onViewClicked()
            }
            it
        }), tonalElevation = 4.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .background(color = LightBlue)
                .fillMaxSize()
        ) {
            ItemImage(
                imageUri = currentPlayingSong.thumbnailUri,
                modifier = modifier
                    .weight(1F)
                    .clip(CircleShape)
                    .width(50.dp)
                    .height(50.dp)
            )
            Spacer(
                modifier = Modifier
                    .width(4.dp)
                    .weight(2F)
            )
            Text(
                text = currentPlayingSong.songName,
                maxLines = 1,
                modifier = modifier.weight(3F),
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(4F))
            AudioControlButtons(action = onPlayButtonClick, onNext = onNextButtonClick, isStopped = isPlaying) {
                onPreviousButtonClicked()
            }
        }
    }
}


@Composable
fun AudioControlButtons(
    modifier: Modifier = Modifier,
    isStopped: Boolean = false,
    action: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        PreviousButton {
            MPLoggerApi.DefaultMpLogger.d(
                className = Constant.SongList.CLASS_NAME,
                tag = Constant.SongList.TAG,
                methodName = "AudioControlButtons",
                msg = "previous button is clicked"
            )
            onPrevious()
        }
        Spacer(modifier = Modifier.width(16.dp))
        PlayOrStoppedButton(modifier = modifier,isStopped= isStopped) {
            action()
        }
        Spacer(modifier = Modifier.width(16.dp))
        NextButton(modifier = modifier.padding(end = 8.dp)) {
            MPLoggerApi.DefaultMpLogger.d(
                className = Constant.SongList.CLASS_NAME,
                tag = Constant.SongList.TAG,
                methodName = "AudioControlButtons",
                msg = "play next song"
            )
            onNext()
        }
    }
}