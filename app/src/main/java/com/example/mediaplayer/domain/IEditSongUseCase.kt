package com.example.mediaplayer.domain

interface IEditSongUseCase {

    suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Boolean
}