package com.example.mpcore.audio.internal.data

import com.example.mpcore.audio.api.MPApiError
import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.audio.api.data.model.MPAudio
import com.example.mpcore.audio.api.data.model.MPPlayList
import com.example.mpcore.audio.api.exception.AddAudioToPlayListThatAlreadyExist
import com.example.mpcore.audio.internal.data.database.IPlayListDatabaseDataProvider
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import com.example.mpcore.audio.internal.data.database.model.PlayListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.sql.SQLException

//TODO add some unit test
internal class InternalPlayListDataManagerImpl(
    private val playListDatabaseDataProviderImpl: IPlayListDatabaseDataProvider
): IInternalPlayListDataManager {

    companion object{
        private const val CLASS_NAME = "InternalPlayListDataManagerImpl"
        private const val TAG = "DATA_BASE"
    }

    override suspend fun getAllPlayList(): MPApiResult<List<MPPlayList>> {
        try {
            val result = playListDatabaseDataProviderImpl.getAll()
            return MPApiResult.Success(result.map { it.toMPPlayList() })
        }catch (e: SQLException){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "getAllAudio",
                logLevel = MPLoggerLevel.ERROR,
                msg = "message: ${e.message}"
            )
            return MPApiResult.Error(MPApiError(DataCode.SQL_FAILED,"could not get all play list"))
        }
    }

    override fun observeAllPlayList(): Flow<MPApiResult<List<MPPlayList>>> {
        try {
            return playListDatabaseDataProviderImpl.observeAll().map { value: List<PlayListEntity> -> MPApiResult.Success(value.map { it.toMPPlayList() }) }
        }catch (e: SQLException){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "getAllAudio",
                logLevel = MPLoggerLevel.ERROR,
                msg = "message: ${e.message}"
            )
            return flow {
                emit(MPApiResult.Error(MPApiError(DataCode.SQL_FAILED,"could not get all play list")))
            }
        }
    }

    override suspend fun attachPlayListToAudio(audioId: Long, mpPlayList: MPPlayList): MPApiResult<Unit> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "attachPlayListToAudio",
            logLevel = MPLoggerLevel.INFO,
            msg = "try to attach mpPlayList: $mpPlayList to audioId: $audioId"
        )
        try {
            playListDatabaseDataProviderImpl.attachPlayListToAudio(audioId,mpPlayList.toPlayListEntity())
            return MPApiResult.Success(Unit)
        }catch (e: SQLException){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "attachPlayListToAudio",
                logLevel = MPLoggerLevel.ERROR,
                msg = "message: ${e.message}"
            )
            return MPApiResult.Error(MPApiError(DataCode.SQL_FAILED,"could not attach audi: $audioId, to playlist ${mpPlayList.id}"))
        }catch (e: AddAudioToPlayListThatAlreadyExist){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "attachPlayListToAudio",
                logLevel = MPLoggerLevel.ERROR,
                msg = "message: ${e.message}"
            )
            return MPApiResult.Error(MPApiError(DataCode.AUDIO_ALREADY_ATTACHED_TO_PLAYLIST,"$audioId already attached"))
        }
    }

    override suspend fun getFirstAudioOfPlayList(playListId: Long): MPApiResult<MPAudio?> {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getFirstAudioOfPlayList",
            logLevel = MPLoggerLevel.INFO,
            msg = "playListId: $playListId"
        )
        try {
            val result = playListDatabaseDataProviderImpl.getFirstAudioOfPlayList(playListId)
            return MPApiResult.Success(result?.toMPAudio())
        }catch (e: SQLException){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "getFirstAudioOfPlayList",
                logLevel = MPLoggerLevel.ERROR,
                msg = "message: ${e.message}"
            )
            return MPApiResult.Error(
                MPApiError(DataCode.SQL_FAILED,"failed when trying to get first audio of playlist")
            )
        }
    }
}