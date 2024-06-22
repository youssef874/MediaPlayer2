package com.example.mpcore.audio.internal.data.dataprovider

import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel


internal fun AudioDataProviderModel.toAudioEntity(): AudioEntity{
    return AudioEntity(
        album = album,
        uri = audioUri.toString(),
        songName = audioName,
        artist = artistName,
        duration = audioDuration,
        size = audioSize,
        externalId = audioId,
        albumThumbnailUri = audioThumbnailUri?.toString()?:""
    )
}