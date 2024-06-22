package com.example.mediaplayer.repository

import com.example.mediaplayer.data.model.RepeatMode
import com.example.mpcore.audio.api.data.MPAudioDataApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object AudioConfigurationRepositoryImpl: IAudioConfigurationRepository{

    override suspend fun updateISInRandomMode(isRandom: Boolean) {
        MPAudioDataApi.getIsInRandomMode().updateValue(isRandom)
    }

    override fun observeISInRandomChanges(): Flow<Boolean> {
        return MPAudioDataApi.getIsInRandomMode().observeValue()
    }

    override suspend fun getIsInRandomMode(): Boolean {
        return MPAudioDataApi.getIsInRandomMode().getValue()
    }

    override suspend fun updateRepeatMode(repeatMode: RepeatMode) {
        MPAudioDataApi.repeatMode().updateValue(repeatMode.toAnnotation())
    }

    override suspend fun getRepeatMode(): RepeatMode {
        return MPAudioDataApi.repeatMode().getValue().toEnum()
    }

    override fun observeRepeatMode(): Flow<RepeatMode> {
        return MPAudioDataApi.repeatMode().observeValue().map { it.toEnum() }
    }
}