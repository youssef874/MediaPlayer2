package com.example.mpcore.audio.internal.data.synchronize

import android.content.Context
import android.database.SQLException
import com.example.mpcore.audio.api.exception.MissingPermissionException
import com.example.mpcore.audio.internal.data.database.IAudioDatabaseDataProvider
import com.example.mpcore.audio.internal.data.database.MPDatabase
import com.example.mpcore.audio.internal.data.dataprovider.IAudioContentProviderDataManager
import com.example.mpcore.audio.internal.data.dataprovider.toAudioEntity
import com.example.mpcore.audio.internal.data.datastore.IAudioDatastoreManger
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration

internal class AudioContentProviderWithDatabaseSynchronization(
    private val audioContentProviderDataManager: IAudioContentProviderDataManager,
    private val audioDatastoreManger: IAudioDatastoreManger,
    private val audioDatabaseDataProvider: IAudioDatabaseDataProvider
):IDataSynchronize {

    companion object{
        private const val CLASS_NAME = "AudioContentProviderWithDatabaseSynchronization"
        private const val TAG = "SYNCHRONIZATION"
    }

    override val synchronizeModule: String = "ContentProviderWithDatabase"

    override suspend fun isRequireSynchronization(context: Context): Boolean {
        if (audioDatastoreManger.getDatabaseVersionController().getValue() != MPDatabase.DATA_BASE_VERSION){
            audioDatastoreManger.getIsDatabaseSynchronizedWithContentProvider().updateValue(false)
        }
        return !audioDatastoreManger.getIsDatabaseSynchronizedWithContentProvider().getValue()
    }

    override suspend fun synchronize(context: Context): Boolean {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME, tag = TAG,
            methodName = "isRequireSynchronization", msg = "start synchronization, isRequireSynchronization: ${isRequireSynchronization(context)}",
            logLevel = MPLoggerLevel.INFO
        )
        if (!isRequireSynchronization(context)){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME, tag = TAG,
                methodName = "isRequireSynchronization", msg = "is already synchronized",
                logLevel = MPLoggerLevel.INFO
            )
            return true
        }
        return try {
            val internalStorageAudioList = audioContentProviderDataManager.getAllAudio()
            internalStorageAudioList.forEach {
                audioDatabaseDataProvider.add(
                    it.toAudioEntity()
                )
            }
            audioDatastoreManger.getIsDatabaseSynchronizedWithContentProvider().updateValue(true)
            true
        }catch (e: SQLException){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME, tag = TAG,
                methodName = "isRequireSynchronization", msg = "synchronization failed message: ${e.message}",
                logLevel = MPLoggerLevel.ERROR
            )
            false
        }catch (e: MissingPermissionException){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME, tag = TAG,
                methodName = "isRequireSynchronization", msg = "synchronization failed message: ${e.message}",
                logLevel = MPLoggerLevel.ERROR
            )
            throw e
        }

    }
}