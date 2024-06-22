package com.example.mpcore.audio.internal.data

import android.content.Context
import com.example.mpcore.audio.api.MPApiError
import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.audio.api.exception.MissingPermissionException
import com.example.mpcore.audio.api.data.model.MPAudio
import com.example.mpcore.audio.internal.data.database.IAudioDatabaseDataProvider
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.audio.internal.data.dataprovider.IAudioContentProviderDataManager
import com.example.mpcore.audio.internal.data.datastore.IAudioDataStoreController
import com.example.mpcore.audio.internal.data.datastore.IAudioDatastoreManger
import com.example.mpcore.audio.internal.data.synchronize.IMPSynchronize
import com.example.mpcore.audio.internal.data.synchronize.SynchronizationState
import com.example.mpcore.common.internal.SDKComponent
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class InternalDataManagerImpl(
    private val synchronizer: IMPSynchronize,
    private val audioDatabase: IAudioDatabaseDataProvider,
    audioContentProviderDataManager: IAudioContentProviderDataManager,
    private val audioDatastoreManger: IAudioDatastoreManger,
    private val context: Context
) : IInternalDataManager {

    companion object {
        private const val CLASS_NAME = "InternalDataManagerImpl"
        private const val TAG = "DATA"
    }

    init {
        try {
            audioContentProviderDataManager.setOnDataChangesListener {
                MPLoggerConfiguration.DefaultBuilder().log(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "setOnDataChangesListener",
                    logLevel = MPLoggerLevel.INFO,
                    msg = "there changes in internal storage data need to resynchronize"
                )
                synchronizer.reset()
                SDKComponent.CoroutineComponent.sdkCoroutineScope.launch(Dispatchers.IO) {
                    audioDatastoreManger.getIsSdkInitialized().updateValue(false)
                    audioDatastoreManger.getIsDatabaseSynchronizedWithContentProvider()
                        .updateValue(false)
                    synchronizer.synchronize(context = context)
                }
            }
        } catch (_: MissingPermissionException) {

        }
    }

    override suspend fun deleteAllAudio() {
        if (synchronizer.synchronizationState() == SynchronizationState.SUCCESS) {
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "deleteAllAudio",
                logLevel = MPLoggerLevel.INFO,
                msg = "delete all data in audio table"
            )
            audioDatabase.deleteAll()
        }
    }

    override suspend fun getAllAudio(): MPApiResult<List<MPAudio>> {
        try {
            val result = synchronizer.synchronize(context)
            if (!result) {
                MPLoggerConfiguration.DefaultBuilder().log(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "getAllAudio",
                    logLevel = MPLoggerLevel.ERROR,
                    msg = "synchronization failed"
                )
                return MPApiResult.Error(
                    MPApiError(
                        errorCode = DataCode.SYNCHRONIZATION_NOT_COMPLETE,
                        errorMessage = "synchronizationFailed"
                    )
                )
            } else {
                return MPApiResult.Success(audioDatabase.getAll().map { it.toMPAudio() })
            }
        } catch (e: MissingPermissionException) {
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "getAllAudio",
                logLevel = MPLoggerLevel.ERROR,
                msg = "message: ${e.message}"
            )
            return MPApiResult.Error(
                MPApiError(
                    errorCode = DataCode.PERMISSION_MISSED,
                    errorMessage = "permissionNeeded: ${e.permissionNeeded}"
                )
            )
        }

    }

    override fun observeAllAudio(): Flow<MPApiResult<List<MPAudio>>> {
        try {
            SDKComponent.CoroutineComponent.sdkCoroutineScope.launch {
                val result = synchronizer.synchronize(context)
                if (!result){
                    MPLoggerConfiguration.DefaultBuilder().log(
                        className = CLASS_NAME,
                        tag = TAG,
                        methodName = "observeAllAudio",
                        logLevel = MPLoggerLevel.ERROR,
                        msg = "synchronization failed"
                    )
                    throw IllegalAccessException("synchronization failed")
                }
            }
            return audioDatabase.observeAll().map { value ->
                MPApiResult.Success(value.map { it.toMPAudio() })
            }
        } catch (e: MissingPermissionException) {
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "getAllAudio",
                logLevel = MPLoggerLevel.ERROR,
                msg = "message: ${e.message}"
            )
            return flow {
                emit(
                    MPApiResult.Error(
                        MPApiError(
                            errorCode = DataCode.PERMISSION_MISSED,
                            errorMessage = "permissionNeeded: ${e.permissionNeeded}"
                        )
                    )
                )
            }
        }catch (e: IllegalAccessException){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "getAllAudio",
                logLevel = MPLoggerLevel.ERROR,
                msg = "message: ${e.message}"
            )
            return flow {
                emit(
                    MPApiResult.Error(
                        MPApiError(
                            errorCode = DataCode.SYNCHRONIZATION_NOT_COMPLETE,
                            errorMessage = "synchronization incomplete"
                        )
                    )
                )
            }
        }
    }

    override fun observeAudioById(audioId: Long): Flow<MPApiResult<MPAudio?>> {
        try {
            SDKComponent.CoroutineComponent.sdkCoroutineScope.launch {
                val result = synchronizer.synchronize(context)
                if (!result){
                    MPLoggerConfiguration.DefaultBuilder().log(
                        className = CLASS_NAME,
                        tag = TAG,
                        methodName = "observeAudioById",
                        logLevel = MPLoggerLevel.ERROR,
                        msg = "synchronization failed"
                    )
                    throw IllegalAccessException("synchronization failed")
                }
            }
            return audioDatabase.observeById(audioId).map {
                MPApiResult.Success(it?.toMPAudio())
            }
        } catch (e: MissingPermissionException) {
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "observeAudioById",
                logLevel = MPLoggerLevel.ERROR,
                msg = "message: ${e.message}"
            )
            return flow {
                emit(
                    MPApiResult.Error(
                        MPApiError(
                            errorCode = DataCode.PERMISSION_MISSED,
                            errorMessage = "permissionNeeded: ${e.permissionNeeded}"
                        )
                    )
                )
            }
        }catch (e: IllegalAccessException){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG,
                methodName = "observeAudioById",
                logLevel = MPLoggerLevel.ERROR,
                msg = "message: ${e.message}"
            )
            return flow {
                emit(
                    MPApiResult.Error(
                        MPApiError(
                            errorCode = DataCode.SYNCHRONIZATION_NOT_COMPLETE,
                            errorMessage = "synchronization incomplete"
                        )
                    )
                )
            }
        }
    }

    override fun getLastPlayingSongId(): IAudioDataStoreController<Long> {
        return audioDatastoreManger.getLastPlayingSongId()
    }

    override suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG,
            methodName = "getAllAudio",
            logLevel = MPLoggerLevel.DEBUG,
            msg = "isFavorite: $isFavorite, audioId: $audioId"
        )
        return audioDatabase.changeAudioFavoriteStatus(isFavorite, audioId)
    }

    override fun isInRandomMode(): IAudioDataStoreController<Boolean> {
        return audioDatastoreManger.getIsInRandomMode()
    }

    override fun repeatMode(): IAudioDataStoreController<Int> {
        return audioDatastoreManger.getRepeatMode()
    }
}