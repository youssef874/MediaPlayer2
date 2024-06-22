package com.example.mediaplayer.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mediaplayer.data.model.RepeatMode
import com.example.mediaplayer.ui.Components.AddSongToAction
import com.example.mediaplayer.ui.Components.AudioListAction
import com.example.mediaplayer.ui.Components.FavoriteAction
import com.example.mediaplayer.ui.Components.ItemImage
import com.example.mediaplayer.ui.Components.NextButton
import com.example.mediaplayer.ui.Components.PlayOrStoppedButton
import com.example.mediaplayer.ui.Components.PreviousButton
import com.example.mediaplayer.ui.Components.RepeatButton
import com.example.mediaplayer.ui.Components.ShuffleButton
import com.example.mediaplayer.ui.Constant
import com.example.mediaplayer.ui.theme.LightBlue
import com.example.mediaplayer.ui.timeFormatter
import com.example.mediaplayer.viewmodel.SongDetailViewModel
import com.example.mediaplayer.viewmodel.model.songDetail.SongDetailUiEvent
import com.example.mpcore.logger.api.MPLoggerApi

@Composable
fun SongDetailScreen(
    songId: Long,
    onBack: () -> Unit,
    onNavigateToPlayListScreen: (songId: Long)->Unit
) {
    val viewModel: SongDetailViewModel = hiltViewModel()
    MPLoggerApi.DefaultMpLogger.i(
        className = Constant.SongDetail.CLASS_NAME, tag = Constant.SongDetail.TAG,
        methodName = "SongDetailScreen", msg = "display detail for $songId"
    )
    if (songId != -1L) {
        LaunchedEffect(key1 = Unit) {
            viewModel.onEvent(SongDetailUiEvent.Setup(songId))
        }
    }

    BackHandler {
        onBack()
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MPLoggerApi.DefaultMpLogger.i(
        className = Constant.SongDetail.CLASS_NAME, tag = Constant.SongDetail.TAG,
        methodName = "SongDetailScreen", msg = "state $state"
    )

    if (state.currentSong.id != -1L) {
        MPLoggerApi.DefaultMpLogger.i(
            className = Constant.SongDetail.CLASS_NAME, tag = Constant.SongDetail.TAG,
            methodName = "SongDetailScreen", msg = "currentSong: ${state.currentSong}"
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(color = LightBlue)
                .scrollable(
                    orientation = Orientation.Horizontal,
                    state = rememberScrollableState {
                        if (it < -5) {
                            onBack()
                        }
                        it
                    }
                )
        ) {
            Column(
                modifier = Modifier.weight(3F),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                ItemImage(
                    imageUri = state.currentSong.thumbnailUri,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = state.currentSong.songName,
                    color = Color.White,
                    maxLines = 1,
                    fontSize = 32.sp
                )
                Text(
                    text = state.currentSong.artist,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.weight(2F))
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                OtherActions(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .height(50.dp),
                    isFavorite = state.currentSong.isFavorite,
                    onFavoriteButtonClicked = {
                        viewModel.onEvent(SongDetailUiEvent.ChangeSongFavoriteStatus)
                    },
                    onAddButtonClicked = {
                        viewModel.clear()
                        onNavigateToPlayListScreen(state.currentSong.id)
                    })
                AudioRangeLabel(
                    duration = state.currentSong.duration,
                    startAt = if (state.progress == -1) 0 else state.progress
                )
                var progress by rememberSaveable {
                    mutableFloatStateOf(0F)
                }
                var move by rememberSaveable {
                    mutableStateOf(false)
                }
                Slider(
                    value = if (move) progress else{ state.progress.toFloat()},
                    onValueChange = { value ->
                        move = true
                        progress = value
                    },
                    valueRange = 0F..state.currentSong.duration.toFloat(),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    onValueChangeFinished = {
                        MPLoggerApi.DefaultMpLogger.d(
                            className = Constant.SongDetail.CLASS_NAME,
                            tag = Constant.SongDetail.TAG,
                            methodName = "SongDetailScreen",
                            msg = "seek player to progress: $progress"
                        )
                        viewModel.onEvent(SongDetailUiEvent.SeekPlayerToPosition(progress.toInt()))
                    }
                )
                DetailsSongControlsButtons(
                    playOrPause = {
                        viewModel.onEvent(SongDetailUiEvent.PlayOrPauseCurrentSong)
                    },
                    next = {
                        viewModel.onEvent(SongDetailUiEvent.PlayNextSong)
                    },
                    previous = {
                        viewModel.onEvent(SongDetailUiEvent.PlayPreviousSong)
                    },
                    shuffleAction = {
                        viewModel.onEvent(SongDetailUiEvent.ChangeIsRandomMode)
                    },
                    changeRepeatMode = {
                         viewModel.onEvent(SongDetailUiEvent.ChangeRepeatMode)
                    },
                    isPlaying = state.isPlaying,
                    isInRandomMode = state.isRandom,
                    repeatMode = state.repeatMode
                )
            }
        }
    }
}

@Composable
fun DetailsSongControlsButtons(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    isInRandomMode: Boolean = false,
    repeatMode: RepeatMode = RepeatMode.NO_REPEAT,
    playOrPause: () -> Unit,
    next: () -> Unit,
    previous: () -> Unit,
    shuffleAction: () -> Unit,
    changeRepeatMode: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        ShuffleButton(
            modifier = modifier.weight(1F), isInRandomMode = isInRandomMode
        ) {
            shuffleAction()
        }
        PreviousButton(
            modifier = modifier.weight(1F)
        ) {
            previous()
        }
        PlayOrStoppedButton(
            modifier = modifier.weight(1F), isStopped = isPlaying
        ) {
            playOrPause()
        }
        NextButton(
            modifier = modifier.weight(1F)
        ) {
            next()
        }
        RepeatButton(
            modifier = modifier.weight(1F), repeatMode = repeatMode
        ) {
            changeRepeatMode()
        }
    }
}

@Composable
fun AudioRangeLabel(modifier: Modifier = Modifier, startAt: Int = 0, duration: Int) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = startAt.timeFormatter(),
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1F))
        Text(
            text = duration.timeFormatter(),
            color = Color.White,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}

@Composable
fun OtherActions(
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteButtonClicked: () -> Unit,
    onAddButtonClicked: ()->Unit
) {
    Row(modifier = modifier) {
        AudioListAction(modifier = Modifier
            .padding(start = 16.dp)
            .width(30.dp)
            .height(30.dp)) {

        }
        Spacer(modifier = Modifier.weight(1F))
        FavoriteAction(
            modifier = Modifier
                .width(30.dp)
                .height(30.dp),
            isFavorite = isFavorite
        ) {
            onFavoriteButtonClicked()
        }
        Spacer(modifier = Modifier.weight(1F))
        AddSongToAction(modifier = Modifier
            .padding(end = 16.dp)
            .width(30.dp)
            .height(30.dp)) {
            onAddButtonClicked()
        }
    }
}