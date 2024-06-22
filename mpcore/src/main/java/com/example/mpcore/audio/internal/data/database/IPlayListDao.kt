package com.example.mpcore.audio.internal.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.common.internal.IBaseDao
import com.example.mpcore.audio.internal.data.database.model.PlayListEntity
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import com.example.mpcore.audio.internal.data.database.model.SongWithPlaylists
import kotlinx.coroutines.flow.Flow

@Dao
internal interface IPlayListDao: IBaseDao<PlayListEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylistSongCrossRef(playlistSongCrossRef: PlaylistSongCrossRef)

    @Query("SELECT * FROM ${PlayListEntity.TABLE_NAME} WHERE ${PlayListEntity.ID} = :id")
    suspend fun getPlayListById(id: Long): PlayListEntity?

    @Query("SELECT * FROM ${PlayListEntity.TABLE_NAME} WHERE ${PlayListEntity.ID} = :id")
    fun observeById(id: Long): Flow<PlayListEntity>

    @Query("SELECT * FROM ${PlayListEntity.TABLE_NAME}")
    fun getAll(): List<PlayListEntity>

    @Query("SELECT * FROM ${PlayListEntity.TABLE_NAME}")
    fun observeAll(): Flow<List<PlayListEntity>>

    @Query("DELETE FROM ${PlayListEntity.TABLE_NAME}")
    suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM ${AudioEntity.TABLE_NAME} WHERE ${AudioEntity.ID} =:audioId")
    suspend fun getListOfPlayListForAudio(audioId: Long): SongWithPlaylists?

    @Transaction
    @Query("SELECT * FROM ${AudioEntity.TABLE_NAME} WHERE ${AudioEntity.ID} =:audioId")
    fun observeListOfPlayListForAudio(audioId: Long): Flow<SongWithPlaylists?>

    @Transaction
    @Query("SELECT * FROM audio")
    suspend fun getListOfPlayListOfSongs(): List<SongWithPlaylists>

    @Transaction
    @Query("SELECT * FROM audio")
    fun observeListOfPlayListOfSongs(): Flow<List<SongWithPlaylists>>
}