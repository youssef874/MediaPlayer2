package com.example.mpcore.audio.internal.data.database

import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.common.internal.IBaseDatabaseDataProvider

internal interface IAudioDatabaseDataProvider: IBaseDatabaseDataProvider<AudioEntity> {

    suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Boolean
}