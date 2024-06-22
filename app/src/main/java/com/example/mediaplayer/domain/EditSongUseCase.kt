package com.example.mediaplayer.domain

import com.example.mediaplayer.repository.IAudioRepository
import com.example.mpcore.logger.api.MPLoggerApi
import javax.inject.Inject

class EditSongUseCase @Inject constructor(
    private val audioRepository: IAudioRepository
): IEditSongUseCase {

    companion object{
        private const val CLASS_NAME = "EditSongUseCase"
        private const val TAG = "AUDIO"
    }

    override suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Boolean {
        MPLoggerApi.DefaultMpLogger.i(
            className = CLASS_NAME, tag = TAG, methodName = "changeAudioFavoriteStatus",
            msg = "isFavorite: $isFavorite, audioId: $audioId"
        )
        return audioRepository.changeAudioFavoriteStatus(isFavorite, audioId)
    }
}