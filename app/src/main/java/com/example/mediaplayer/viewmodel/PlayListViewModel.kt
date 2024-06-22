package com.example.mediaplayer.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.common.DefaultPagination
import com.example.mediaplayer.domain.IFetchDataUseCase
import com.example.mediaplayer.domain.model.UiPlayList
import com.example.mediaplayer.ui.Constant
import com.example.mediaplayer.viewmodel.delegate.ViewModelJobSchedulerDelegate
import com.example.mediaplayer.viewmodel.model.UiResult
import com.example.mediaplayer.viewmodel.model.playlist.PlayListUIState
import com.example.mediaplayer.viewmodel.model.playlist.PlayListUiEvent
import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.logger.api.MPLoggerApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayListViewModel @Inject constructor(
    private val fetchDataUseCase: IFetchDataUseCase
): BaseViewModel<PlayListUiEvent,PlayListUIState>() {

    private val _uiState = MutableStateFlow(PlayListUIState(isEndReached = false))
    override val uiState: StateFlow<PlayListUIState> = _uiState.asStateFlow()

    private val dataList = mutableListOf<UiPlayList>()

    private val pagination = DefaultPagination(
        initialKey = uiState.value.page,
        onLoadUpdated = { isLoading ->
            MPLoggerApi.DefaultMpLogger.d(
                className = CLASS_NAME,
                methodName = "onLoadUpdated",
                tag = TAG,
                msg = "isLoading: $isLoading"
            )
            _uiState.update {
                it.copy(isNextItemLoading = isLoading)
            }
        },
        onRequest = { nextKey ->
            MPLoggerApi.DefaultMpLogger.d(
                className = CLASS_NAME,
                methodName = "onRequest",
                tag = TAG,
                msg = "nextKey: $nextKey"
            )
            val staringIndex = nextKey * Constant.Utils.PAGE_SIZE
            if (staringIndex + Constant.Utils.PAGE_SIZE < dataList.size) {
                val list = dataList.slice(staringIndex until staringIndex + Constant.Utils.PAGE_SIZE)
                list.forEach {
                    dataList.remove(it)
                }
                MPApiResult.Success(
                    list
                )
            }else if (dataList.isNotEmpty()){
                val list = mutableListOf<UiPlayList>()
                list.addAll(dataList)
                dataList.clear()
                MPApiResult.Success(
                    list
                )
            } else {
                MPApiResult.Success(emptyList())
            }
        },
        getNextKey = { list ->
            MPLoggerApi.DefaultMpLogger.d(
                className = CLASS_NAME,
                methodName = "getNextKey",
                tag = TAG,
                msg = "items: $list"
            )
            uiState.value.page + 1
        },
        onError = { errorCode,msg ->
            MPLoggerApi.DefaultMpLogger.w(
                className = CLASS_NAME,
                methodName = "onError",
                tag = TAG,
                msg = "message: $msg, errorCode: $errorCode"
            )
            _uiState.update {
                it.copy( dataList = emptyList(),isLoading = false)
            }
        },
        onSuccess = { items, newKey ->
            MPLoggerApi.DefaultMpLogger.d(
                className = CLASS_NAME,
                methodName = "onSuccess",
                tag = TAG,
                msg = "items: $items, nextKey: $newKey"
            )
            _uiState.update {
                val newListToAdd = items.filter { uiPlayList -> uiState.value.dataList.none { item-> item.playListId == uiPlayList.playListId } }
                it.copy(
                    dataList = uiState.value.dataList.plus(newListToAdd),
                    page = newKey,
                    isEndReached = items.isEmpty(),
                    isLoading = false
                )
            }
        }
    )

    private val collectPlayList by ViewModelJobSchedulerDelegate{_,_->
        fetchDataUseCase.observeAllPlayList().collectLatest {
            MPLoggerApi.DefaultMpLogger.i(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "handleLoadDataEvent",
                msg = "result: $it"
            )
            when(it){
                is MPApiResult.Success->{
                    dataList.addAll(it.data)
                    loadNexData()
                }
                else->_uiState.update { playListUIState ->
                    playListUIState.copy(dataList = emptyList(),isLoading = false)
                }
            }
        }
    }

    private fun loadNexData() {
        viewModelScope.launch {
            pagination.loadNextItem()
        }
    }

    override fun clear() {
        MPLoggerApi.DefaultMpLogger.i(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "clear",
            msg = "stop unfinished jobs"
        )
        collectPlayList.cancelJob()
    }

    override fun handleEvent(event: PlayListUiEvent) {
        when(event){
            is PlayListUiEvent.LoadData->{
                handleLoadDataEvent()
            }
            is PlayListUiEvent.AttachSongToPlayList->{
                handleAttachSongToPlayListEvent(event)
            }
            is PlayListUiEvent.LoadMoreData->{
                handleLoadMoreDataEvent()
            }
            is PlayListUiEvent.AttachSongToPlayListResultHandled->{
                handleAttachSongToPlayListResultHandledEvent()
            }
        }
    }

    private fun handleAttachSongToPlayListResultHandledEvent() {
        MPLoggerApi.DefaultMpLogger.d(
            className = CLASS_NAME, tag = TAG, methodName = "handleAttachSongToPlayListResultHandledEvent",
            msg = "reset attachToSongResult"
        )
        _uiState.update {
            it.copy(attachToSongResult = null)
        }
    }

    private fun handleLoadMoreDataEvent() {
        loadNexData()
    }

    private fun handleAttachSongToPlayListEvent(event: PlayListUiEvent.AttachSongToPlayList) {
        MPLoggerApi.DefaultMpLogger.i(
            className = CLASS_NAME, tag = TAG, methodName = "handleAttachSongToPlayListEvent",
            msg = "songId: ${event.songId}, play list name: ${event.uiPlayList.playListName}"
        )
        viewModelScope.launch {
            _uiState.update {
                it.copy(attachToSongResult = UiResult.Loading())
            }
            val result =fetchDataUseCase.attachPlayListToAudio(event.songId, event.uiPlayList)
            when(result){
                is MPApiResult.Success->{
                    _uiState.update {
                        it.copy(attachToSongResult = UiResult.Success(Unit))
                    }
                }
                is MPApiResult.Error->{
                    _uiState.update {
                        it.copy(attachToSongResult = UiResult.Error(result.error))
                    }
                }
            }
        }
    }

    private fun handleLoadDataEvent() {
        MPLoggerApi.DefaultMpLogger.i(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "handleLoadDataEvent",
            msg = "load playlists"
        )
        _uiState.update {
            it.copy(isLoading = true)
        }
        collectPlayList.startJob()
    }

    companion object{
        private const val CLASS_NAME = "PlayListViewModel"
        private const val TAG = "PLAY_LIST"
    }
}