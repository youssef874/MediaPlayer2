package com.example.mpcore.audio.internal.data.factory

import android.content.Context
import com.example.mpcore.audio.internal.data.IInternalDataManager
import com.example.mpcore.audio.internal.data.InternalDataManagerImpl
import com.example.mpcore.audio.internal.data.database.factory.AudioDataBaseProviderFactory
import com.example.mpcore.audio.internal.data.dataprovider.factory.AudioContentProviderDataManagerFactory
import com.example.mpcore.audio.internal.data.datastore.factory.AudioDatastoreDataManagerFactory
import com.example.mpcore.audio.internal.data.synchronize.factory.MPSynchronizeFactory

internal object InternalDataManagerFactory {

    @Volatile
    private var internalDataManager: IInternalDataManager? = null

    fun create(context: Context): IInternalDataManager {
        return internalDataManager ?: synchronized(this) {
            val audioDataManagerImpl =
                AudioDatastoreDataManagerFactory.create(context)
            val instance = InternalDataManagerImpl(
                synchronizer = MPSynchronizeFactory.create(context),
                audioDatabase = AudioDataBaseProviderFactory.create(context),
                audioContentProviderDataManager = AudioContentProviderDataManagerFactory.create(
                    context
                ),
                audioDatastoreManger = audioDataManagerImpl,
                context = context
            )
            internalDataManager = instance
            instance
        }
    }
}