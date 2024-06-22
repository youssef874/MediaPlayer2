package com.example.mpcore.audio.internal.data

import android.net.Uri
import com.example.mpcore.audio.api.data.model.MPAudio
import com.example.mpcore.audio.api.data.model.MPPlayList
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpstorage.database.internal.entity.PlayListEntity


internal fun AudioEntity.toMPAudio(): MPAudio = MPAudio(
    id = id,
    audioName = songName,
    album = album,
    artistName = artist,
    duration = duration,
    size = size,
    uri = if (uri.isNotEmpty()) Uri.parse(uri) else Uri.EMPTY,
    songThumbnail = if (albumThumbnailUri.isNotEmpty()) Uri.parse(albumThumbnailUri) else null,
    isFavorite = isFavorite
)

internal fun PlayListEntity.toMPPlayList(): MPPlayList = MPPlayList(
    id = id,
    name = name
)

internal fun MPPlayList.toPlayListEntity(): PlayListEntity = PlayListEntity(
    id = id,
    name = name
)