package com.example.mediaplayer.domain

import com.example.mediaplayer.data.model.RepeatMode
import kotlinx.coroutines.flow.Flow

interface IRandomAndRepeatConfigurationUseCase {

    suspend fun updateRandomMode(isRandom: Boolean)

    fun observeIsRandomModeChanges(): Flow<Boolean>

    suspend fun isInRandomMode(): Boolean

    suspend fun updateRepeatMode(repeatMode: RepeatMode)

    fun observeRepeatMode(): Flow<RepeatMode>

    suspend fun getRepeatMode(): RepeatMode
}