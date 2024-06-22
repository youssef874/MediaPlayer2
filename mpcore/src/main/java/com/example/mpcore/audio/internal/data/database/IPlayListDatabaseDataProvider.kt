package com.example.mpcore.audio.internal.data.database

import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.common.internal.IBaseDatabaseDataProvider
import com.example.mpstorage.database.internal.entity.PlayListEntity

internal interface IPlayListDatabaseDataProvider: IBaseDatabaseDataProvider<PlayListEntity> {

    suspend fun attachPlayListToAudio(audioId: Long, playListEntity: PlayListEntity)

    suspend fun getFirstAudioOfPlayList(playListId: Long): AudioEntity?

    suspend fun getAudioListOfPlaylist(playListId: Long): List<AudioEntity>
}