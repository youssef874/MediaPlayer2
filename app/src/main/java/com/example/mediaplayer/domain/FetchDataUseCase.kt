package com.example.mediaplayer.domain

import com.example.mediaplayer.domain.model.UiAudio
import com.example.mediaplayer.domain.model.UiPlayList
import com.example.mediaplayer.domain.model.toMPAppPlayList
import com.example.mediaplayer.domain.model.toUIAudio
import com.example.mediaplayer.domain.model.toUiPlayList
import com.example.mediaplayer.repository.IAudioRepository
import com.example.mpcore.audio.api.MPApiError
import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.logger.api.MPLoggerApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchDataUseCase @Inject constructor(private val audioRepository: IAudioRepository) :
    IFetchDataUseCase {

        companion object{
            private const val CLASS_NAME = "FetchDataUseCase"
            private const val TAG = "AUDIO"
        }


    private val _cachedAudioList = mutableListOf<UiAudio>()
    override val cachedAudioList: List<UiAudio>
        get() = _cachedAudioList

    override fun updateCachedAudioList(list: List<UiAudio>) {
        _cachedAudioList.clear()
        _cachedAudioList.addAll(list)
    }

    override suspend fun getAllSong(): MPApiResult<List<UiAudio>> {
        MPLoggerApi.DefaultMpLogger.i(
            CLASS_NAME, TAG,"getAllSong","Request loading all available song"
        )
        return audioRepository.getAllSong().run {
            when(this){
                is MPApiResult.Success->{
                    val uiList = data.map { it.toUIAudio() }
                    MPApiResult.Success(uiList)
                }
                is MPApiResult.Error->{
                    MPApiResult.Error(MPApiError(error.errorCode,error.errorMessage))
                }
            }
        }
    }

    override fun observeAllAudio(): Flow<MPApiResult<List<UiAudio>>> {
        MPLoggerApi.DefaultMpLogger.i(
            CLASS_NAME, TAG,"observeAllAudio","Request loading all available song"
        )
        return audioRepository.observeAllAudio().map { value ->
            when(value){
                is MPApiResult.Success->{
                    MPApiResult.Success(value.data.map { it.toUIAudio() })
                }
                is MPApiResult.Error->{
                    MPApiResult.Error(MPApiError(value.error.errorCode,value.error.errorMessage))
                }
            }
        }
    }

    override fun observeAllPlayList(): Flow<MPApiResult<List<UiPlayList>>> {
        return audioRepository.observeAllPlayList().map {value->
            when(value){
                is MPApiResult.Success->{
                    MPApiResult.Success(
                        value.data.map {
                            val audio = audioRepository.getFirstAudioOfPlayList(it.id)
                            if (audio is MPApiResult.Success){
                                it.toUiPlayList(audio.data?.songThumbnail)
                            }else{
                                it.toUiPlayList()
                            }
                        }
                    )
                }
                else->MPApiResult.Error((value as MPApiError))
            }
        }
    }

    override suspend fun attachPlayListToAudio(audioId: Long, uiPlayList: UiPlayList): MPApiResult<Unit> {
        return audioRepository.attachPlayListToAudio(audioId,uiPlayList.toMPAppPlayList())
    }

    override fun observeSongById(songId: Long): Flow<MPApiResult<UiAudio?>> {
        return audioRepository.observeSongById(songId).map { value ->
            when(value){
                is MPApiResult.Success->{
                    MPApiResult.Success(value.data?.toUIAudio())
                }
                is MPApiResult.Error->{
                    MPApiResult.Error(MPApiError(value.error.errorCode,value.error.errorMessage))
                }
            }
        }
    }

    override suspend fun getLastPlayingSongId(): Long {
        return audioRepository.getLastPlayingSongId().getValue()
    }

    override suspend fun getLastPlayingSong(): UiAudio? {
        val lastPlayingSongId = getLastPlayingSongId()
        return getAllSong().run {
            when(this){
                is MPApiResult.Success-> data.firstOrNull { it.id == lastPlayingSongId }
                is MPApiResult.Error->null
            }
        }
    }
}