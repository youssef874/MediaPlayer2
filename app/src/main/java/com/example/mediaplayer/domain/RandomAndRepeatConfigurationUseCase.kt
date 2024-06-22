package com.example.mediaplayer.domain

import com.example.mediaplayer.data.model.RepeatMode
import com.example.mediaplayer.repository.IAudioConfigurationRepository
import com.example.mpcore.logger.api.MPLoggerApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class RandomAndRepeatConfigurationUseCase @Inject constructor(
    private val audioConfigurationRepository: IAudioConfigurationRepository
): IRandomAndRepeatConfigurationUseCase {

    companion object{
        private const val CLASS_NAME = "RandomAndRepeatConfigurationUseCase"
        private const val TAG = "CONFIGURATION"
    }

    override suspend fun updateRandomMode(isRandom: Boolean) {
        MPLoggerApi.DefaultMpLogger.d(
            className = CLASS_NAME, tag = TAG,
            methodName = "updateRandomMode", msg = "isRandom: $isRandom"
        )
        audioConfigurationRepository.updateISInRandomMode(isRandom)
    }

    override fun observeIsRandomModeChanges(): Flow<Boolean> {
        return audioConfigurationRepository.observeISInRandomChanges()
    }

    override suspend fun isInRandomMode(): Boolean {
        return audioConfigurationRepository.getIsInRandomMode()
    }

    override suspend fun updateRepeatMode(repeatMode: RepeatMode) {
        MPLoggerApi.DefaultMpLogger.d(
            className = CLASS_NAME, tag = TAG,
            methodName = "updateRepeatMode", msg = "repeatMode: $repeatMode"
        )
        audioConfigurationRepository.updateRepeatMode(repeatMode)
    }

    override fun observeRepeatMode(): Flow<RepeatMode> {
        return audioConfigurationRepository.observeRepeatMode()
    }

    override suspend fun getRepeatMode(): RepeatMode {
        return audioConfigurationRepository.getRepeatMode()
    }
}