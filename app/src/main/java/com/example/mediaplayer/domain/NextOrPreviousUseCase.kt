package com.example.mediaplayer.domain

import com.example.mediaplayer.domain.model.UiAudio
import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.logger.api.MPLoggerApi
import javax.inject.Inject

class NextOrPreviousUseCase @Inject constructor(
    private val fetchDataUseCase: IFetchDataUseCase,
    private val playOrStopAudioUseCase: IPlayOrStopAudioUseCase,
    private val randomAndRepeatConfigurationUseCase: IRandomAndRepeatConfigurationUseCase
) : INextOrPreviousUseCase {

    companion object {
        private const val CLASS_NAME = "NextOrPreviousUseCase"
        private const val TAG = "AUDIO"
    }

    override suspend fun playNext(currentSong: UiAudio): UiAudio {
        MPLoggerApi.DefaultMpLogger.i(
            className = CLASS_NAME, tag = TAG, methodName = "playNext",
            msg = "currentSong: $currentSong"
        )

        val audioList = fetchDataUseCase.cachedAudioList.ifEmpty {
            fetchDataUseCase.getAllSong().let {
                if (it is MPApiResult.Success) {
                    it.data
                } else {
                    emptyList()
                }
            }
        }
        val isRandom = randomAndRepeatConfigurationUseCase.isInRandomMode()
        val currentIndex = audioList.indexOf(currentSong)
        val nextIndex =
            if (!isRandom)
                if (currentIndex + 1 <= audioList.size - 1) currentIndex + 1 else 0
            else
                audioList.indices.random()
        val nextItem = audioList[nextIndex]
        playOrStopAudioUseCase.playAudio(nextItem)
        return nextItem
    }

    override suspend fun playPrevious(currentSong: UiAudio): UiAudio {
        MPLoggerApi.DefaultMpLogger.i(
            className = CLASS_NAME, tag = TAG, methodName = "playPrevious",
            msg = "currentSong: $currentSong"
        )
        val audioList = fetchDataUseCase.cachedAudioList.ifEmpty {
            fetchDataUseCase.getAllSong().let {
                if (it is MPApiResult.Success) {
                    it.data
                } else {
                    emptyList()
                }
            }
        }
        val isRandom = randomAndRepeatConfigurationUseCase.isInRandomMode()
        val currentIndex = audioList.indexOf(currentSong)
        val nextIndex = if (!isRandom)
            if (currentIndex - 1 >= 0) currentIndex - 1 else audioList.size - 1
        else
            audioList.indices.random()
        val nextItem = audioList[nextIndex]
        playOrStopAudioUseCase.playAudio(nextItem)
        return nextItem
    }
}