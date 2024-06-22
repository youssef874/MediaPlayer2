package com.example.mediaplayer.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.IJobScheduler
import com.example.mediaplayer.data.common.DefaultPagination
import com.example.mediaplayer.domain.IFetchDataUseCase
import com.example.mediaplayer.domain.INextOrPreviousUseCase
import com.example.mediaplayer.domain.IPlayOrStopAudioUseCase
import com.example.mediaplayer.domain.model.UiAudio
import com.example.mediaplayer.ui.Constant
import com.example.mediaplayer.viewmodel.delegate.ViewModelJobSchedulerDelegate
import com.example.mediaplayer.viewmodel.model.songList.SongListUiEvent
import com.example.mediaplayer.viewmodel.model.songList.SongListUiState
import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.audio.internal.data.DataCode
import com.example.mpcore.logger.api.MPLoggerApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongListScreenViewModel @Inject constructor(
    private val fetchDataUseCase: IFetchDataUseCase,
    private val playOrStopAudioUseCase: IPlayOrStopAudioUseCase,
    private val nextOrStopAudioUseCase: INextOrPreviousUseCase
) : BaseViewModel<SongListUiEvent, SongListUiState>() {

    private val _uiState = MutableStateFlow(SongListUiState())
    override val uiState: StateFlow<SongListUiState> = _uiState.asStateFlow()

    private val collectSongListJobScheduler: IJobScheduler by ViewModelJobSchedulerDelegate{_,_->
        fetchDataUseCase.observeAllAudio().stateIn(viewModelScope).collectLatest { result ->
            MPLoggerApi.DefaultMpLogger.i(
                CLASS_NAME, TAG,
                "handleLoadDataEvent", "result: $result"
            )
            when (result) {
                is MPApiResult.Success -> {
                    MPLoggerApi.DefaultMpLogger.i(
                        CLASS_NAME, TAG,
                        "handleLoadDataEvent", "all songs: ${result.data.map { it.id }}, size: ${result.data.size}"
                    )
                    dataList.addAll(result.data)
                    fetchDataUseCase.updateCachedAudioList(result.data)
                    loadNextData()
                    val lastPlayedSong = fetchDataUseCase.getLastPlayingSongId()
                    MPLoggerApi.DefaultMpLogger.i(
                        CLASS_NAME, TAG,
                        "handleLoadDataEvent", "lastPlayedSong: $lastPlayedSong"
                    )
                    if (lastPlayedSong != -1L) {
                        _uiState.update {
                            it.copy(currentSong = result.data.firstOrNull { uiAudio -> uiAudio.id == lastPlayedSong })
                        }
                    }
                }

                is MPApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, isError = true, dataList = emptyList())
                    }
                    if (result.error.errorCode == DataCode.PERMISSION_MISSED) {
                        _uiState.update {
                            it.copy(isPermissionDenied = true)
                        }
                    }
                }
            }
        }
    }

    private val dataList = mutableListOf<UiAudio>()

    private val pagination = DefaultPagination(
        initialKey = uiState.value.page,
        onLoadUpdated = { isLoading ->
            MPLoggerApi.DefaultMpLogger.d(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "onLoadUpdated",
                msg = "isLoading: $isLoading"
            )
            _uiState.update {
                it.copy(isNextItemLoading = isLoading)
            }
        },
        onRequest = { nextKey: Int ->
            MPLoggerApi.DefaultMpLogger.d(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "onRequest",
                msg = "nextKey: $nextKey"
            )
            val staringIndex = nextKey * Constant.Utils.PAGE_SIZE
            if (staringIndex + Constant.Utils.PAGE_SIZE < dataList.size) {
                MPApiResult.Success(
                    dataList.slice(staringIndex until staringIndex + Constant.Utils.PAGE_SIZE)
                )
            } else if (dataList.isNotEmpty()) {
                val list = dataList.slice(staringIndex until dataList.size - 1)
                dataList.clear()
                MPApiResult.Success(list)
            } else {
                MPApiResult.Success(emptyList())
            }
        },
        getNextKey = { list: List<UiAudio> ->
            MPLoggerApi.DefaultMpLogger.d(
                className = CLASS_NAME, tag = TAG, methodName = "getNextKey", msg = "items: $list"
            )
            if (list.isEmpty()) {
                0
            } else {
                uiState.value.page + 1
            }
        },
        onError = { _, message ->
            MPLoggerApi.DefaultMpLogger.e(
                className = CLASS_NAME, tag = TAG, methodName = "onError", msg = "message: $message"
            )
        },
        onSuccess = { items, newKey ->
            MPLoggerApi.DefaultMpLogger.d(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "onSuccess",
                msg = "items: ${items.map { it.songName }}, nextKey: $newKey"
            )
            _uiState.update {
                it.copy(
                    dataList = uiState.value.dataList + items,
                    page = newKey,
                    isEndReached = items.isEmpty(),
                    isLoading = false,
                    isError = false
                )
            }
        }
    )

    private var listenToPlayingChangesJob: IJobScheduler by ViewModelJobSchedulerDelegate{_,job->
        playOrStopAudioUseCase.onAudioPlayedChangeListener(
            onAudioPlayChanges = { uiAudio,isPlaying ->
                MPLoggerApi.DefaultMpLogger.d(
                    className = CLASS_NAME, tag = TAG, methodName = "onAudioPlayChanges",
                    msg = "uiAudio: $uiAudio, isPlaying: $isPlaying"
                )
                _uiState.update {
                    it.copy(currentSong = uiAudio, isPlaying = isPlaying)
                }
            }
        ) { job?.isActive == true }
    }

    override fun handleEvent(event: SongListUiEvent) {
        when (event) {
            is SongListUiEvent.OnPermissionNotGranted -> {
                handleOnPermissionNotGrantedEvent()
            }

            is SongListUiEvent.LoadData -> {
                handleLoadDataEvent()
            }

            is SongListUiEvent.OnPermissionGranted -> {
                handleOnPermissionGranted()
            }

            is SongListUiEvent.LoadNextItem -> {
                handleLoadNextItemEvent()
            }

            is SongListUiEvent.PlaySelectedSong -> {
                handePlaySelectedSongEvent(event.uiAudio)
            }

            is SongListUiEvent.PlayOrPauseCurrentSong -> {
                handlePlayOrPauseCurrentSongEvent()
            }
            is SongListUiEvent.PlayNextSong->{
                handlePlayNextSongEvent()
            }
            is SongListUiEvent.PlayPreviousSong->{
                handlePlayPreviousSongEvent()
            }
        }
    }

    private fun handlePlayPreviousSongEvent() {
        MPLoggerApi.DefaultMpLogger.i(
            CLASS_NAME,
            TAG,
            "handlePlayPreviousSongEvent",
            "currentSong: ${uiState.value.currentSong}, isPlaying: ${uiState.value.isPlaying}"
        )
        if (listenToPlayingChangesJob.job == null){
            listenToPlayingChangesJob.startJob()
        }
        viewModelScope.launch {
            uiState.value.currentSong?.let {
                val previousSong = nextOrStopAudioUseCase.playPrevious(it)
                MPLoggerApi.DefaultMpLogger.i(
                    CLASS_NAME,
                    TAG,
                    "handlePlayNextSongEvent",
                    "previousSong: $previousSong"
                )
            }
        }
    }

    private fun handlePlayNextSongEvent() {
        MPLoggerApi.DefaultMpLogger.i(
            CLASS_NAME,
            TAG,
            "handlePlayNextSongEvent",
            "currentSong: ${uiState.value.currentSong}, isPlaying: ${uiState.value.isPlaying}"
        )
        if (listenToPlayingChangesJob.job == null){
            listenToPlayingChangesJob.startJob()
        }
        viewModelScope.launch {
            uiState.value.currentSong?.let {
                val nextSong = nextOrStopAudioUseCase.playNext(it)
                MPLoggerApi.DefaultMpLogger.i(
                    CLASS_NAME,
                    TAG,
                    "handlePlayNextSongEvent",
                    "nextSong: $nextSong"
                )
            }
        }
    }

    private fun handlePlayOrPauseCurrentSongEvent() {
        MPLoggerApi.DefaultMpLogger.i(
            CLASS_NAME,
            TAG,
            "handlePlayOrPauseCurrentSongEvent",
            "currentSong: ${uiState.value.currentSong}, isPlaying: ${uiState.value.isPlaying}"
        )
        if (listenToPlayingChangesJob.job == null){
            listenToPlayingChangesJob.startJob()
        }
        viewModelScope.launch {
            if (uiState.value.isPlaying) {
                playOrStopAudioUseCase.pauseAudio()
            } else {
                uiState.value.currentSong?.let {
                    playOrStopAudioUseCase.resumeCurrentSong(it)
                }
            }
        }
    }

    private fun handePlaySelectedSongEvent(uiAudio: UiAudio) {
        MPLoggerApi.DefaultMpLogger.i(
            CLASS_NAME, TAG,
            "handePlaySelectedSongEvent", "uiAudio: $uiAudio"
        )
        if (listenToPlayingChangesJob.job == null){
            listenToPlayingChangesJob.startJob()
        }
        viewModelScope.launch {
            playOrStopAudioUseCase.playAudio(uiAudio)
        }
    }

    private fun handleLoadNextItemEvent() {
        viewModelScope.launch {
            loadNextData()
        }
    }

    private suspend fun loadNextData() {
        pagination.loadNextItem()
    }

    private fun handleOnPermissionGranted() {
        MPLoggerApi.DefaultMpLogger.i(
            CLASS_NAME, TAG,
            "handleOnPermissionGranted", "permission is granted"
        )
        _uiState.update {
            it.copy(isLoading = true, isError = false, isPermissionDenied = false)
        }
        onEvent(SongListUiEvent.LoadData)
    }

    private fun handleLoadDataEvent() {
        MPLoggerApi.DefaultMpLogger.i(
            CLASS_NAME, TAG,
            "handleLoadDataEvent", "load all songs"
        )
        collectSongListJobScheduler.startJob()
    }

    private fun handleOnPermissionNotGrantedEvent() {
        MPLoggerApi.DefaultMpLogger.i(
            CLASS_NAME,
            TAG,
            "handleOnPermissionNotGrantedEvent",
            "permission to read audio files fom content provider not granted"
        )
        _uiState.update {
            it.copy(isLoading = false, isError = true, isPermissionDenied = true)
        }
    }


    override fun clear() {
        MPLoggerApi.DefaultMpLogger.i(
            className = CLASS_NAME, tag = TAG, methodName = "clear",
            msg = "clear all unfinished jobs"
        )
        collectSongListJobScheduler.cancelJob()
        listenToPlayingChangesJob.cancelJob()
    }

    companion object {
        private const val CLASS_NAME = "SongListScreenViewModel"
        private const val TAG = "SONG_LIST"
    }
}