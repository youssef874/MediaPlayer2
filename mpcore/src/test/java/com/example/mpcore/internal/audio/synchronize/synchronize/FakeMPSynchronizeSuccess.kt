package com.example.mpcore.internal.audio.synchronize.synchronize

import android.content.Context
import com.example.mpcore.audio.internal.data.database.IAudioDatabaseDataProvider
import com.example.mpcore.audio.internal.data.dataprovider.IAudioContentProviderDataManager
import com.example.mpcore.audio.internal.data.dataprovider.toAudioEntity
import com.example.mpcore.audio.internal.data.datastore.IAudioDatastoreManger
import com.example.mpcore.audio.internal.data.synchronize.IMPSynchronize
import com.example.mpcore.audio.internal.data.synchronize.SynchronizationState

internal class FakeMPSynchronizeSuccess(
    private val audioDatabase: IAudioDatabaseDataProvider,
    private val audioContentProviderDataManager: IAudioContentProviderDataManager,
    private val audioDatastoreManger: IAudioDatastoreManger
): IMPSynchronize {

    private var state = SynchronizationState.NONE
    override suspend fun synchronize(context: Context): Boolean {
        state = SynchronizationState.STARTED
        audioContentProviderDataManager.getAllAudio().forEach {
            audioDatabase.add(it.toAudioEntity())
        }
        audioDatastoreManger.getIsSdkInitialized().updateValue(true)
        state = SynchronizationState.SUCCESS
        return true
    }

    override fun synchronizationState(): String {
        return state
    }

    override fun reset() {
        state = SynchronizationState.NONE
    }
}