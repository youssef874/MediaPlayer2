package com.example.mediaplayer.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mediaplayer.R
import com.example.mediaplayer.domain.model.UiPlayList
import com.example.mediaplayer.ui.Components.AddItemComponent
import com.example.mediaplayer.ui.Components.list.ListComponent
import com.example.mediaplayer.ui.Components.list.LoadingListItem
import com.example.mediaplayer.ui.Constant
import com.example.mediaplayer.ui.theme.LightBlue
import com.example.mediaplayer.ui.toItemData
import com.example.mediaplayer.ui.toUiPlaylist
import com.example.mediaplayer.viewmodel.PlayListViewModel
import com.example.mediaplayer.viewmodel.model.UiResult
import com.example.mediaplayer.viewmodel.model.playlist.PlayListUiEvent
import com.example.mpcore.audio.internal.data.DataCode
import com.example.mpcore.logger.api.MPLoggerApi

@Composable
fun PlayListScreen(songId: Long,onBack: () -> Unit) {
    MPLoggerApi.DefaultMpLogger.i(
        className = Constant.PlayListScreen.CLASS_NAME,
        tag = Constant.PlayListScreen.TAG,
        methodName = "PlayListScreen",
        msg = "display PlayListScreen"
    )
    BackHandler {
        onBack()
    }
    val viewModel: PlayListViewModel = hiltViewModel()
    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(PlayListUiEvent.LoadData)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box {
        if (state.isLoading) {
            MPLoggerApi.DefaultMpLogger.i(
                className = Constant.PlayListScreen.CLASS_NAME,
                tag = Constant.PlayListScreen.TAG,
                methodName = "PlayListScreen",
                msg = "loading data"
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(20) {
                    LoadingListItem()
                }
            }
        } else {
            if (state.dataList.isNotEmpty()) {
                MPLoggerApi.DefaultMpLogger.i(
                    className = Constant.PlayListScreen.CLASS_NAME,
                    tag = Constant.PlayListScreen.TAG,
                    methodName = "PlayListScreen",
                    msg = "list of play list: ${state.dataList}"
                )
                ListComponent(
                    dataList = state.dataList.map { it.toItemData() },
                    isEndReached = state.isEndReached,
                    isNextItemLoading = state.isNextItemLoading,
                    onListItemClick = {
                                      viewModel.onEvent(PlayListUiEvent.AttachSongToPlayList(songId = songId,it.toUiPlaylist()))
                    },
                    loadNextItem = {
                        viewModel.onEvent(PlayListUiEvent.LoadMoreData)
                    }
                )
            } else {
                MPLoggerApi.DefaultMpLogger.i(
                    className = Constant.PlayListScreen.CLASS_NAME,
                    tag = Constant.PlayListScreen.TAG,
                    methodName = "PlayListScreen",
                    msg = "there no playlist"
                )
                NoPlayListView(modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = 16.dp, end = 16.dp))
            }
        }
        var displayDialog by rememberSaveable {
            mutableStateOf(false)
        }
        FloatingActionButton(
            onClick = { displayDialog = true },
            containerColor = LightBlue,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            Icon(Icons.Filled.Add, "Add playlist")
        }

        val defaultValue = if (state.dataList.isEmpty())
            stringResource(R.string.playlist_name_string, 1)
        else
            stringResource(R.string.playlist_name_string, state.dataList.maxByOrNull { it.playListId }?.playListId?.plus(1)?:1)
        if (displayDialog && (state.attachToSongResult == null || state.attachToSongResult is UiResult.Loading)){
            MPLoggerApi.DefaultMpLogger.i(
                className = Constant.PlayListScreen.CLASS_NAME,
                methodName = "PlayListScreen",
                tag = Constant.PlayListScreen.TAG,
                msg = "display dialog"
            )

            AddItemComponent(
                itemName = stringResource(R.string.playlist_string),
                defaultValue = defaultValue,
                attachToSongResult = state.attachToSongResult,
                onAddClicked = {
                    MPLoggerApi.DefaultMpLogger.i(
                        Constant.PlayListScreen.CLASS_NAME,
                        "PlayListScreen",
                        Constant.PlayListScreen.TAG,
                        "add button clicked playListName: $it"
                    )
                    displayDialog = false
                    viewModel.onEvent(PlayListUiEvent.AttachSongToPlayList(songId = songId, uiPlayList = UiPlayList(playListName = it)))
                },
                onDismissRequest = {
                    MPLoggerApi.DefaultMpLogger.i(
                        Constant.PlayListScreen.CLASS_NAME,
                        "PlayListScreen",
                        Constant.PlayListScreen.TAG,
                        "cancel button clicked "
                    )
                    displayDialog = false
                }
            )
        }
        when(state.attachToSongResult){
            is UiResult.Success->{
                MPLoggerApi.DefaultMpLogger.i(
                    Constant.PlayListScreen.CLASS_NAME,
                    "PlayListScreen",
                    Constant.PlayListScreen.TAG,
                    "song attached to play list success "
                )
                Toast.makeText(LocalContext.current,
                    stringResource(R.string.add_song_to_playlist_success_msg),Toast.LENGTH_SHORT).show()
                viewModel.onEvent(PlayListUiEvent.AttachSongToPlayListResultHandled)
            }
            is UiResult.Error->{
                MPLoggerApi.DefaultMpLogger.i(
                    Constant.PlayListScreen.CLASS_NAME,
                    "PlayListScreen",
                    Constant.PlayListScreen.TAG,
                    "song failed to be attached to playlist "
                )

                val text = if ((state.attachToSongResult as UiResult.Error<Unit>?)?.error?.errorCode == DataCode.AUDIO_ALREADY_ATTACHED_TO_PLAYLIST){
                    stringResource(R.string.added_song_to_playlist_error_string)
                }else{
                    stringResource(R.string.add_to_play_list_error_string)
                }
                Toast.makeText(LocalContext.current,text,Toast.LENGTH_SHORT).show()
                viewModel.onEvent(PlayListUiEvent.AttachSongToPlayListResultHandled)
            }
            else-> {
                Unit
            }
        }
    }
}

@Composable
fun NoPlayListView(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.no_playlist_text_string),
        modifier = modifier,
        fontWeight = FontWeight.Bold
    )
}