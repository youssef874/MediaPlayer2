package com.example.mediaplayer.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.IJobScheduler
import com.example.mediaplayer.data.model.RepeatMode
import com.example.mediaplayer.domain.IEditSongUseCase
import com.example.mediaplayer.domain.IFetchDataUseCase
import com.example.mediaplayer.domain.INextOrPreviousUseCase
import com.example.mediaplayer.domain.IPlayOrStopAudioUseCase
import com.example.mediaplayer.domain.IRandomAndRepeatConfigurationUseCase
import com.example.mediaplayer.viewmodel.delegate.ViewModelJobSchedulerDelegate
import com.example.mediaplayer.viewmodel.model.songDetail.SongDetailUiEvent
import com.example.mediaplayer.viewmodel.model.songDetail.SongDetailUiState
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
class SongDetailViewModel @Inject constructor(
    private val playOrStopAudioUseCase: IPlayOrStopAudioUseCase,
    private val nextOrStopAudioUseCase: INextOrPreviousUseCase,
    private val fetchDataUseCase: IFetchDataUseCase,
    private val randomAndRepeatConfigurationUseCase: IRandomAndRepeatConfigurationUseCase,
    private val editSongUseCase: IEditSongUseCase
) : BaseViewModel<SongDetailUiEvent, SongDetailUiState>() {

    private val _uiState = MutableStateFlow(SongDetailUiState())
    override val uiState: StateFlow<SongDetailUiState> = _uiState.asStateFlow()

    private var listenToPlayingChangesJob: IJobScheduler by ViewModelJobSchedulerDelegate { _, job ->
        playOrStopAudioUseCase.onAudioPlayedChangeListener(
            onAudioPlayChanges = { uiAudio, isPlaying ->
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

    private val observeAudioProgress: IJobScheduler by ViewModelJobSchedulerDelegate { _, _ ->
        playOrStopAudioUseCase.observeAudioProgress().stateIn(viewModelScope).collectLatest {
            _uiState.update { state ->
                state.copy(progress = it)
            }
        }
    }

    private val observeIsInRandomMode by ViewModelJobSchedulerDelegate { _, _ ->
        randomAndRepeatConfigurationUseCase.observeIsRandomModeChanges().stateIn(viewModelScope)
            .collectLatest { isRandom ->
                _uiState.update {
                    it.copy(isRandom = isRandom)
                }
            }
    }

    private val observeRepeatMode by ViewModelJobSchedulerDelegate { _, _ ->
        randomAndRepeatConfigurationUseCase.observeRepeatMode().stateIn(viewModelScope)
            .collectLatest {repeatMode->
                _uiState.update {
                    it.copy(repeatMode = repeatMode)
                }
            }
    }

    override fun clear() {
        MPLoggerApi.DefaultMpLogger.i(
            className = CLASS_NAME, tag = TAG, methodName = "clear",
            msg = "clear all unfinished jobs"
        )
        listenToPlayingChangesJob.cancelJob()
        observeAudioProgress.cancelJob()
        observeIsInRandomMode.cancelJob()
        observeRepeatMode.cancelJob()
    }

    private fun getNewRepeatMode(): RepeatMode {
        return when (uiState.value.repeatMode) {
            RepeatMode.NO_REPEAT -> RepeatMode.ONE_REPEAT
            RepeatMode.ONE_REPEAT -> RepeatMode.REPEAT_ALL
            RepeatMode.REPEAT_ALL -> RepeatMode.NO_REPEAT
        }
    }

    override fun handleEvent(event: SongDetailUiEvent) {
        when (event) {
            is SongDetailUiEvent.Setup -> {
                handleSetupEvent(event.audioId)
            }
            is SongDetailUiEvent.PlayOrPauseCurrentSong -> {
                handlePlayOrPauseCurrentSongEvent()
            }
            is SongDetailUiEvent.PlayNextSong -> {
                handlePlayNextSongEvent()
            }
            is SongDetailUiEvent.PlayPreviousSong -> {
                handlePlayPreviousSongEvent()
            }
            is SongDetailUiEvent.ChangeIsRandomMode -> {
                handleChangeIsRandomModeEvent()
            }
            is SongDetailUiEvent.SeekPlayerToPosition -> {
                handleSeekPlayerToPositionEvent(event)
            }
            is SongDetailUiEvent.ChangeRepeatMode->{
                handleChangeRepeatModeEvent()
            }
            is SongDetailUiEvent.ChangeSongFavoriteStatus->{
                handleChangeSongFavoriteStatusEvent()
            }
        }
    }

    private fun handleChangeSongFavoriteStatusEvent() {
        val isFavorite = uiState.value.currentSong.isFavorite
        MPLoggerApi.DefaultMpLogger.i(
            className = CLASS_NAME, tag = TAG, methodName = "handleChangeRepeatModeEvent",
            msg = "current isFavorite: $isFavorite"
        )
        viewModelScope.launch {
            val result = editSongUseCase.changeAudioFavoriteStatus(isFavorite = !isFavorite, audioId = uiState.value.currentSong.id)
            if (result){
                _uiState.update {
                    it.copy(currentSong = uiState.value.currentSong.copy(isFavorite = !isFavorite))
                }
            }

        }
    }

    private fun handleChangeRepeatModeEvent() {
        MPLoggerApi.DefaultMpLogger.i(
            className = CLASS_NAME, tag = TAG, methodName = "handleChangeRepeatModeEvent",
            msg = "current repeat mode: ${uiState.value.repeatMode}"
        )
        viewModelScope.launch {
            randomAndRepeatConfigurationUseCase.updateRepeatMode(getNewRepeatMode())
        }
    }

    private fun handleSeekPlayerToPositionEvent(event: SongDetailUiEvent.SeekPlayerToPosition) {
        MPLoggerApi.DefaultMpLogger.i(
            className = CLASS_NAME, tag = TAG, methodName = "handleSeekPlayerToPositionEvent",
            msg = "position: ${event.position}"
        )
        playOrStopAudioUseCase.updatePlayerPosition(event.position)
    }

    private fun handleChangeIsRandomModeEvent() {
        MPLoggerApi.DefaultMpLogger.d(
            className = CLASS_NAME, tag = TAG, methodName = "handleChangeIsRandomModeEvent",
            msg = "isRandom: ${uiState.value.isRandom}"
        )
        viewModelScope.launch {
            randomAndRepeatConfigurationUseCase.updateRandomMode(isRandom = !uiState.value.isRandom)
        }
    }

    private fun handlePlayPreviousSongEvent() {
        MPLoggerApi.DefaultMpLogger.d(
            className = CLASS_NAME, tag = TAG, methodName = "handlePlayPreviousSongEvent",
            msg = "currentAudio,: ${uiState.value.currentSong}, isPlaying: ${uiState.value.isPlaying}"
        )
        if (listenToPlayingChangesJob.job == null) {
            listenToPlayingChangesJob.startJob()
        }
        viewModelScope.launch {
            val previousSong = nextOrStopAudioUseCase.playPrevious(uiState.value.currentSong)
            MPLoggerApi.DefaultMpLogger.i(
                CLASS_NAME,
                TAG,
                "handlePlayNextSongEvent",
                "previousSong: $previousSong"
            )
        }
    }

    private fun handlePlayNextSongEvent() {
        MPLoggerApi.DefaultMpLogger.d(
            className = CLASS_NAME, tag = TAG, methodName = "handlePlayNextSongEvent",
            msg = "currentAudio,: ${uiState.value.currentSong}, isPlaying: ${uiState.value.isPlaying}"
        )
        if (listenToPlayingChangesJob.job == null) {
            listenToPlayingChangesJob.startJob()
        }
        viewModelScope.launch {
            val nextSong = nextOrStopAudioUseCase.playNext(uiState.value.currentSong)
            MPLoggerApi.DefaultMpLogger.i(
                CLASS_NAME,
                TAG,
                "handlePlayNextSongEvent",
                "nextSong: $nextSong"
            )
        }
    }

    private fun handlePlayOrPauseCurrentSongEvent() {
        MPLoggerApi.DefaultMpLogger.d(
            className = CLASS_NAME, tag = TAG, methodName = "handlePlayOrPauseCurrentSongEvent",
            msg = "currentAudio,: ${uiState.value.currentSong}, isPlaying: ${uiState.value.isPlaying}"
        )
        if (listenToPlayingChangesJob.job == null) {
            listenToPlayingChangesJob.startJob()
        }
        viewModelScope.launch {
            if (uiState.value.isPlaying) {
                playOrStopAudioUseCase.pauseAudio()
            } else {
                uiState.value.currentSong.let {
                    playOrStopAudioUseCase.resumeCurrentSong(it)
                }
            }
        }
    }

    private fun handleSetupEvent(audioId: Long) {
        MPLoggerApi.DefaultMpLogger.i(
            className = CLASS_NAME, tag = TAG, methodName = "handleSetupEvent",
            msg = "audioId: $audioId, current: ${playOrStopAudioUseCase.currentAudio}"
        )
        observeAudioProgress.startJob()
        observeIsInRandomMode.startJob()
        observeRepeatMode.startJob()
        playOrStopAudioUseCase.currentAudio?.let { uiAudio ->
            if (uiAudio.id != audioId) {
                MPLoggerApi.DefaultMpLogger.e(
                    className = CLASS_NAME, tag = TAG, methodName = "handleSetupEvent",
                    msg = "this not the current audio"
                )
            }
            _uiState.update {
                it.copy(currentSong = uiAudio)
            }
        } ?: run {
            MPLoggerApi.DefaultMpLogger.i(
                className = CLASS_NAME, tag = TAG, methodName = "handleSetupEvent",
                msg = "no current yet search in the cached List: ${fetchDataUseCase.cachedAudioList.map { it.id }}"
            )
            fetchDataUseCase.cachedAudioList.firstOrNull { it.id == audioId }?.let { uiAudio ->
                MPLoggerApi.DefaultMpLogger.i(
                    className = CLASS_NAME, tag = TAG, methodName = "handleSetupEvent",
                    msg = "cached current audio"
                )
                _uiState.update {
                    it.copy(currentSong = uiAudio)
                }
            }
        }
        _uiState.update {
            it.copy(isPlaying = playOrStopAudioUseCase.isPlaying)
        }
    }

    companion object {
        private const val CLASS_NAME = "SongDetailViewModel"
        private const val TAG = "SONG_DETAIL"
    }
}