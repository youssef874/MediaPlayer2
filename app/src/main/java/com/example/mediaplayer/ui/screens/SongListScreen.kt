package com.example.mediaplayer.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mediaplayer.ui.Components.BottomBar
import com.example.mediaplayer.ui.Components.ErrorScreen
import com.example.mediaplayer.ui.Components.RequestPermissionDialog
import com.example.mediaplayer.ui.Components.list.ListComponent
import com.example.mediaplayer.ui.Components.list.LoadingListItem
import com.example.mediaplayer.ui.Constant
import com.example.mediaplayer.ui.ObserveLifecycle
import com.example.mediaplayer.ui.RequestSinglePermission
import com.example.mediaplayer.ui.toItemData
import com.example.mediaplayer.viewmodel.SongListScreenViewModel
import com.example.mediaplayer.viewmodel.model.songList.SongListUiEvent
import com.example.mpcore.logger.api.MPLoggerApi

@Composable
fun SongListScreen(
    navigateToSongDetailScreen: (songId: Long) -> Unit
) {
    val viewModel: SongListScreenViewModel = hiltViewModel()
    MPLoggerApi.DefaultMpLogger.i(
        Constant.SongList.CLASS_NAME, Constant.SongList.TAG,
        "SongListScreen", "display song list screen"
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val permission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO
        else Manifest.permission.READ_EXTERNAL_STORAGE
    ObserveLifecycle(onEvent = { _, event ->
        MPLoggerApi.DefaultMpLogger.d(
            Constant.SongList.CLASS_NAME, Constant.SongList.TAG,
            "SongListScreen", "event: $event"
        )
        if (event == Lifecycle.Event.ON_RESUME) {
            if (
                ContextCompat.checkSelfPermission(
                    context, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                MPLoggerApi.DefaultMpLogger.d(
                    Constant.SongList.CLASS_NAME, Constant.SongList.TAG,
                    "SongListScreen", "event: $event $permission not granted"
                )
                viewModel.onEvent(SongListUiEvent.OnPermissionNotGranted)
            } else {
                viewModel.onEvent(SongListUiEvent.OnPermissionGranted)
            }
        } else if (event == Lifecycle.Event.ON_STOP) {
            viewModel.clear()
        }
    })

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    if (state.isLoading) {
        MPLoggerApi.DefaultMpLogger.d(
            Constant.SongList.CLASS_NAME, Constant.SongList.TAG,
            "SongListScreen", " screen is loading"
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(20) {
                LoadingListItem()
            }
        }
    }
    if (state.isError) {
        MPLoggerApi.DefaultMpLogger.w(
            Constant.SongList.CLASS_NAME, Constant.SongList.TAG,
            "SongListScreen", " error"
        )
        if (state.isPermissionDenied) {
            MPLoggerApi.DefaultMpLogger.w(
                Constant.SongList.CLASS_NAME, Constant.SongList.TAG,
                "SongListScreen", " request permission: $permission"
            )
            RequestSinglePermission(
                permission = permission, onPermissionGranted = {
                    LaunchedEffect(
                        key1 = lifecycleOwner.lifecycle.currentState == Lifecycle.State.STARTED
                    ) {
                        viewModel.onEvent(SongListUiEvent.LoadData)
                    }
                },
                onPermissionDenied = {
                    ErrorScreen()
                    RequestPermissionDialog(permissions = listOf(permission))
                },
                owWaitingForResponse = {}
            )
        } else {
            ErrorScreen()
        }
    }
    if (state.dataList.isNotEmpty()) {
        MPLoggerApi.DefaultMpLogger.w(
            Constant.SongList.CLASS_NAME, Constant.SongList.TAG,
            "SongListScreen", " display: ${state.dataList.map { it.songName }}"
        )
        Box {
            ListComponent(
                dataList = state.dataList.map { it.toItemData() },
                isEndReached = state.isEndReached,
                isNextItemLoading = state.isNextItemLoading,
                onListItemClick = {
                    viewModel.onEvent(SongListUiEvent.PlaySelectedSong(state.dataList.first { uiAudio -> uiAudio.id == it.id }))
                },
                selectedItem = state.currentSong?.toItemData(),
                loadNextItem = {
                    viewModel.onEvent(SongListUiEvent.LoadNextItem)
                }
            )
            if (state.currentSong != null) {
                MPLoggerApi.DefaultMpLogger.w(
                    Constant.SongList.CLASS_NAME, Constant.SongList.TAG,
                    "SongListScreen", "current song: ${state.currentSong}"
                )
                BottomBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    currentPlayingSong = state.currentSong!!,
                    onPreviousButtonClicked = {
                        viewModel.onEvent(SongListUiEvent.PlayPreviousSong)
                    },
                    onNextButtonClick = {
                        viewModel.onEvent(SongListUiEvent.PlayNextSong)
                    },
                    onPlayButtonClick = {
                        viewModel.onEvent(SongListUiEvent.PlayOrPauseCurrentSong)
                    },
                    onViewClicked = {
                        viewModel.clear()
                        navigateToSongDetailScreen(state.currentSong!!.id)
                    },
                    isPlaying = state.isPlaying
                )
            }
        }
    }
}