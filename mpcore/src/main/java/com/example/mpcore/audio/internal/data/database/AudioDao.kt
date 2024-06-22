package com.example.mpcore.audio.internal.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.common.internal.IBaseDao
import com.example.mpcore.audio.internal.data.database.model.PlayListEntity
import com.example.mpcore.audio.internal.data.database.model.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AudioDao: IBaseDao<AudioEntity> {

    @Query("SELECT * FROM ${AudioEntity.TABLE_NAME} WHERE ${AudioEntity.ID} = :id")
    suspend fun getById(id: Long): AudioEntity?

    @Query("SELECT * FROM ${AudioEntity.TABLE_NAME} WHERE ${AudioEntity.ID} = :id")
    fun observeById(id: Long): Flow<AudioEntity?>

    @Query("SELECT * FROM ${AudioEntity.TABLE_NAME}")
    suspend fun getAll(): List<AudioEntity>

    @Query("SELECT * FROM ${AudioEntity.TABLE_NAME}")
    fun observeAll(): Flow<List<AudioEntity>>

    @Query("DELETE FROM ${AudioEntity.TABLE_NAME}")
    suspend fun deleteAll()

    @Query("UPDATE ${AudioEntity.TABLE_NAME} SET ${AudioEntity.IS_FAVORITE} = :isFavorite WHERE ${AudioEntity.ID} = :audioId")
    suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Int

    @Transaction
    @Query("SELECT * FROM ${PlayListEntity.TABLE_NAME}")
    suspend fun getListOfAudioListOfPlayList(): List<PlaylistWithSongs>

    @Transaction
    @Query("SELECT * FROM ${PlayListEntity.TABLE_NAME} WHERE ${PlayListEntity.ID} =:playListId")
    suspend fun getListOfAudioForPlayList(playListId: Long): PlaylistWithSongs?

    @Transaction
    @Query("SELECT * FROM ${PlayListEntity.TABLE_NAME} WHERE ${PlayListEntity.ID} =:playListId")
    fun observeListOfAudioForPlayList(playListId: Long): Flow<PlaylistWithSongs?>

    @Transaction
    @Query("SELECT * FROM ${PlayListEntity.TABLE_NAME}")
    fun observeListOfAudioListOfPlayList(): Flow<List<PlaylistWithSongs>>
}