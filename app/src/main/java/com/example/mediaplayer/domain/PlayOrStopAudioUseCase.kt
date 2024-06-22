package com.example.mediaplayer.domain

import com.example.mediaplayer.data.model.RepeatMode
import com.example.mediaplayer.domain.model.UiAudio
import com.example.mediaplayer.repository.IAudioRepository
import com.example.mpcore.logger.api.MPLoggerApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayOrStopAudioUseCase @Inject constructor(
    private val audioRepository: IAudioRepository,
    private val fetchDataUseCase: IFetchDataUseCase,
    private val randomAndRepeatConfigurationUseCase: IRandomAndRepeatConfigurationUseCase
): IPlayOrStopAudioUseCase {

    companion object{
        private const val CLASS_NAME = "PlayOrStopAudioUseCase"
        private const val TAG = "AUDIO"
    }

    private var _currentAudio: UiAudio? = null
    override val currentAudio: UiAudio?
        get() = _currentAudio

    override val isPlaying: Boolean
        get() = audioRepository.getIsPlaying()

    private var onSongCompletion: ( suspend ()->Unit)? = null

    private val scope = CoroutineScope(Dispatchers.Default)

    private var songCompletionJob: Job? = null
    private val _playingSongChangeListener = mutableListOf<(UiAudio, Boolean)->Unit>()

    init {
        audioRepository.setOnSongCompletionListener {
            songCompletionJob?.cancel()
            songCompletionJob = null
            songCompletionJob = scope.launch {
                onSongCompletion?.invoke()
            }
        }
    }

    private fun getNextSongIndex(
        currentSongIndex: Int,
        audioList: List<UiAudio>,
        repeatMode: RepeatMode,
        isRandom: Boolean
    ) = when (repeatMode) {
        RepeatMode.NO_REPEAT -> {
            if (!isRandom) {
                if (currentSongIndex + 1 <= audioList.size - 1) currentSongIndex + 1 else audioList.size - 1
            } else {
                audioList.indices.random()
            }
        }

        RepeatMode.ONE_REPEAT -> {
            if (!isRandom) {
                if (currentSongIndex + 1 <= audioList.size - 1) currentSongIndex + 1 else 0
            } else {
                audioList.indices.random()
            }
        }

        RepeatMode.REPEAT_ALL -> {
            currentSongIndex
        }
    }

    override suspend fun playAudio(uiAudio: UiAudio, playAt: Int) {
        if (isPlaying){
            if (currentAudio?.id == uiAudio.id){
                MPLoggerApi.DefaultMpLogger.w(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "playAudio",
                    msg = "the ${uiAudio.uri} is already playing no need to be playing"
                )
                return
            }

        }

        MPLoggerApi.DefaultMpLogger.d(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "playAudio",
            msg = "uiAudio: $uiAudio"
        )
        _currentAudio = uiAudio
        val result = audioRepository.playSong(uri = uiAudio.uri,playAt)
        if (result){
            MPLoggerApi.DefaultMpLogger.d(
                className = CLASS_NAME, tag = TAG, methodName = "playAudio",
                msg = "the ${uiAudio.uri} is successfully played"
            )
            audioRepository.getLastPlayingSongId().updateValue(uiAudio.id)
            _playingSongChangeListener.forEach {
                it.invoke(uiAudio,true)
            }
            onSongCompletion = {
                currentAudio?.let { audio->
                    fetchDataUseCase.cachedAudioList.takeIf { it.isNotEmpty() }?.let {list->
                        val index = list.indexOf(audio)
                        val isRandom = randomAndRepeatConfigurationUseCase.isInRandomMode()
                        val repeatMode = randomAndRepeatConfigurationUseCase.getRepeatMode()
                        val nextIndex = getNextSongIndex(
                            currentSongIndex = index,
                            audioList = list,
                            repeatMode = repeatMode,
                            isRandom = isRandom
                        )

                        val nextSong = list[nextIndex]
                        MPLoggerApi.DefaultMpLogger.d(
                            className = CLASS_NAME, tag = TAG, methodName = "playAudio",
                            msg = "the $uiAudio is completed play next song $nextSong"
                        )
                        playAudio(nextSong)
                    }
                }
            }
        }
    }

    override suspend fun stopCurrentPlayingSong() {
        MPLoggerApi.DefaultMpLogger.d(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "stopCurrentPlayingSong",
            msg = "uiAudio: $currentAudio"
        )
        val result = audioRepository.stopCurrentPlayingSong()
        if (result){
            MPLoggerApi.DefaultMpLogger.d(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "stopCurrentPlayingSong",
                msg = "the current playing audio is successfully stopped"
            )
            _currentAudio = null
            onSongCompletion = null
        }
    }

    override suspend fun resumeCurrentSong(uiAudio: UiAudio) {
        MPLoggerApi.DefaultMpLogger.d(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "resumeCurrentSong",
            msg = "uiAudio: $currentAudio"
        )
        if (currentAudio == null){
            val lastProgress = getAudioProgress()
            playAudio(uiAudio,lastProgress)
        }else{
            if (isPlaying){
                MPLoggerApi.DefaultMpLogger.w(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "resumeCurrentSong",
                    msg = "the song already playing, no need to resume"
                )
                return
            }
            val result = audioRepository.resumeSong()
            if (result){
                MPLoggerApi.DefaultMpLogger.d(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "resumeCurrentSong",
                    msg = "the current audio is successfully resumed"
                )
                _playingSongChangeListener.forEach {
                    it.invoke(currentAudio!!,true)
                }
            }
        }
    }


    override suspend fun pauseAudio() {
        currentAudio?.let {
            MPLoggerApi.DefaultMpLogger.d(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "pauseAudio",
                msg = "pauseAudio: $it"
            )
            if (!isPlaying){
                MPLoggerApi.DefaultMpLogger.w(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "pauseAudio",
                    msg = "the current audio already paused no need to pause again"
                )
                return
            }
            val result = audioRepository.pauseSong()
            if (result){
                MPLoggerApi.DefaultMpLogger.d(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "pauseAudio",
                    msg = "the current playing audio is successfully paused"
                )
                _playingSongChangeListener.forEach { function ->
                    function(it,false)
                }
            }
        }?:run {
            MPLoggerApi.DefaultMpLogger.w(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "pauseAudio",
                msg = "currentAudio is null there nothing to pause"
            )
        }
    }

    override fun updatePlayerPosition(position: Int) {
        MPLoggerApi.DefaultMpLogger.i(
            className = CLASS_NAME, tag = TAG, methodName = "updatePlayerPosition",
            msg = "position: $position"
        )
        audioRepository.updateCurrentPlayerPosition(position)
    }

    override suspend fun onAudioPlayedChangeListener(
        onAudioPlayChanges: (UiAudio, Boolean) -> Unit,
        predicate: () -> Boolean
    ) {
        MPLoggerApi.DefaultMpLogger.d(
            className = CLASS_NAME, tag = TAG, methodName = "onAudioPlayedChangeListener",
            msg = "add listener for playing audioChanges"
        )
        _playingSongChangeListener
            .add(onAudioPlayChanges)
        while (predicate()){
            //wait
        }
        MPLoggerApi.DefaultMpLogger.d(
            className = CLASS_NAME, tag = TAG, methodName = "onAudioPlayedChangeListener",
            msg = "remove listener for playing audioChanges"
        )
        _playingSongChangeListener.remove(onAudioPlayChanges)
    }

    override suspend fun getAudioProgress(): Int {
        return audioRepository.getAudioProgress()
    }

    override fun observeAudioProgress(): Flow<Int> {
        return audioRepository.observeAudioProgress()
    }


}