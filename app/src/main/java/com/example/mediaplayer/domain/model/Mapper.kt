package com.example.mediaplayer.domain.model

import android.net.Uri
import com.example.mpcore.audio.api.data.model.MPAudio
import com.example.mpcore.audio.api.data.model.MPPlayList


fun MPAudio.toUIAudio(): UiAudio {
    return UiAudio(
        id = id,
        songName = audioName,
        artist = artistName,
        album = album,
        uri = uri,
        size = size,
        duration = duration,
        thumbnailUri = songThumbnail,
        isFavorite = isFavorite

    )
}

fun MPPlayList.toUiPlayList(thumbnailUri: Uri? = null): UiPlayList = UiPlayList(
    playListId = id,
    playListName = name,
    thumbnailUri = thumbnailUri
)

fun UiPlayList.toMPAppPlayList(): MPPlayList = MPPlayList(
    id = playListId,
    name = playListName
)


