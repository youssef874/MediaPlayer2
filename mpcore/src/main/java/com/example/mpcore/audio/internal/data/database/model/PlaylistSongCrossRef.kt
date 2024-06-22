package com.example.mpstorage.database.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.audio.internal.data.database.model.PlayListEntity

@Entity( primaryKeys = [PlayListEntity.ID,AudioEntity.ID])
data class PlaylistSongCrossRef(
    @ColumnInfo(name = PlayListEntity.ID, defaultValue = "0")
    val playListId: Long = 0L,
    @ColumnInfo(name = AudioEntity.ID, defaultValue = "0")
    val songId: Long = 0L
){

    companion object{
        const val TABLE_NAME = "PlaylistSongCrossRef"
        const val PLAY_LIST_ID = PlayListEntity.ID
        const val SONG_ID = AudioEntity.ID
    }
}
