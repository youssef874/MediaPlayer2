package com.example.mediaplayer.repository

import android.net.Uri
import com.example.mpcore.audio.api.audioplayer.MPAudioPlayerApi
import com.example.mpcore.audio.api.data.MPAudioDataApi
import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.audio.api.data.model.MPAudio
import com.example.mpcore.audio.api.data.model.MPPlayList
import com.example.mpcore.audio.internal.data.datastore.IAudioDataStoreController
import kotlinx.coroutines.flow.Flow

object AudioRepositoryImpl: IAudioRepository {

    override suspend fun getAllSong(): MPApiResult<List<MPAudio>> {
        return MPAudioDataApi.getAllAudio()
    }

    override fun observeAllAudio(): Flow<MPApiResult<List<MPAudio>>> {
        return MPAudioDataApi.observeAllAudio()
    }

    override fun observeAllPlayList(): Flow<MPApiResult<List<MPPlayList>>> {
        return MPAudioDataApi.observeAllPlayList()
    }

    override suspend fun attachPlayListToAudio(audioId: Long, mpPlayList: MPPlayList): MPApiResult<Unit> {
        return MPAudioDataApi.attachPlayListToAudio(audioId, mpPlayList)
    }

    override suspend fun getFirstAudioOfPlayList(playListId: Long): MPApiResult<MPAudio?> {
        return MPAudioDataApi.getFirstAudioOfPlayList(playListId)
    }

    override fun observeSongById(songId: Long): Flow<MPApiResult<MPAudio?>> {
        return MPAudioDataApi.observeAudioById(songId)
    }

    override suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Boolean {
        return MPAudioDataApi.changeAudioFavoriteStatus(isFavorite, audioId)
    }

    override fun getLastPlayingSongId(): IAudioDataStoreController<Long> {
        return MPAudioDataApi.getLastPlayingSongId()
    }

    override fun getIsPlaying(): Boolean {
        return MPAudioPlayerApi.isPlaying
    }

    override fun playSong(uri: Uri, playAt: Int): Boolean {
        return MPAudioPlayerApi.playAudio(uri,playAt)
    }

    override fun pauseSong(): Boolean {
        return MPAudioPlayerApi.pausePlayingAudio()
    }

    override fun resumeSong(): Boolean {
        return MPAudioPlayerApi.resumePlayingAudio()
    }

    override fun stopCurrentPlayingSong(): Boolean {
        return MPAudioPlayerApi.stopPlayingAudio()
    }

    override fun setOnSongCompletionListener(onSongCompletion: () -> Unit) {
        MPAudioPlayerApi.listenToAudioCompletion(onSongCompletion)
    }

    override suspend fun getAudioProgress(): Int {
        return MPAudioPlayerApi.getAudioProgress()
    }

    override fun observeAudioProgress(): Flow<Int> {
        return MPAudioPlayerApi.observeAudioProgress()
    }

    override fun updateCurrentPlayerPosition(position: Int): Boolean {
        return MPAudioPlayerApi.updateCurrentPlayerPosition(position)
    }
}