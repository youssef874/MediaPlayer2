package com.example.mediaplayer.repository

import com.example.mediaplayer.data.model.RepeatMode
import kotlinx.coroutines.flow.Flow

interface IAudioConfigurationRepository {

    suspend fun updateISInRandomMode(isRandom: Boolean)

    fun observeISInRandomChanges(): Flow<Boolean>

    suspend fun getIsInRandomMode(): Boolean

    suspend fun updateRepeatMode(repeatMode: RepeatMode)

    suspend fun getRepeatMode(): RepeatMode

    fun observeRepeatMode(): Flow<RepeatMode>
}