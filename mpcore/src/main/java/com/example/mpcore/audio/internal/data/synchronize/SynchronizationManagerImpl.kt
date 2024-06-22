package com.example.mpcore.audio.internal.data.synchronize

import android.content.Context
import com.example.mpcore.audio.internal.data.datastore.IAudioDatastoreManger
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration

internal class SynchronizationManagerImpl(
    private val context: Context,
    private val audioDatastoreManger: IAudioDatastoreManger
) : ISynchronizationManager {

    companion object {
        private const val CLASS_NAME = "SynchronizationManagerImpl"
        private const val TAG = "SYNCHRONIZATION"
    }

    private var synchronisationCallback: SynchronisationCallback? = null

    override suspend fun synchronize(vararg dataSynchronize: IDataSynchronize) {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME,
            tag = TAG, methodName = "synchronize",
            msg = "synchronization started",
            logLevel = MPLoggerLevel.INFO
        )
        synchronisationCallback?.onStarted()
        if (isSynchronized()){
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG, methodName = "synchronize",
                msg = "synchronization already done",
                logLevel = MPLoggerLevel.INFO
            )
            synchronisationCallback?.onComplete()
        }
        dataSynchronize.forEach {
            MPLoggerConfiguration.DefaultBuilder().log(
                className = CLASS_NAME,
                tag = TAG, methodName = "synchronize",
                msg = "isRequireSynchronization: ${it.isRequireSynchronization(context)}",
                logLevel = MPLoggerLevel.INFO
            )
            if (it.isRequireSynchronization(context)) {
                val result = it.synchronize(context)
                if (!result){
                    MPLoggerConfiguration.DefaultBuilder().log(
                        className = CLASS_NAME,
                        tag = TAG, methodName = "synchronize",
                        msg = "synchronization failed ${it.synchronizeModule}",
                        logLevel = MPLoggerLevel.WARNING
                    )
                    synchronisationCallback?.onFailed()
                    audioDatastoreManger.getIsSdkInitialized().updateValue(false)
                    return
                }
            }
        }
        synchronisationCallback?.onComplete()
        audioDatastoreManger.getIsSdkInitialized().updateValue(true)
    }

    override fun registerCallbackListener(synchronisationCallback: SynchronisationCallback) {
        this.synchronisationCallback = synchronisationCallback
    }

    override suspend fun isSynchronized(vararg dataSynchronize: IDataSynchronize): Boolean {
        dataSynchronize.forEach {
            if (!it.synchronize(context)){
                audioDatastoreManger.getIsSdkInitialized().updateValue(false)
                return false
            }
        }
        return audioDatastoreManger.getIsSdkInitialized().getValue()
    }
}