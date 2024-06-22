package com.example.mpcore.audio.internal.data.synchronize.factory

import android.content.Context
import com.example.mpcore.audio.internal.data.database.factory.AudioDataBaseProviderFactory
import com.example.mpcore.audio.internal.data.dataprovider.AudioContentProviderDataManagerImpl
import com.example.mpcore.audio.internal.data.datastore.factory.AudioDatastoreDataManagerFactory
import com.example.mpcore.audio.internal.data.synchronize.AudioContentProviderWithDatabaseSynchronization

internal object AudioContentProviderWithDatabaseSynchronizationFactory {

    @Volatile
    private var instance: AudioContentProviderWithDatabaseSynchronization? = null

    fun create(context: Context): AudioContentProviderWithDatabaseSynchronization{
        return instance?: synchronized(this){
            val result = AudioContentProviderWithDatabaseSynchronization(
                AudioContentProviderDataManagerImpl(context),
                AudioDatastoreDataManagerFactory.create(context),
                AudioDataBaseProviderFactory.create(context)
            )
            instance = result
            result
        }
    }
}