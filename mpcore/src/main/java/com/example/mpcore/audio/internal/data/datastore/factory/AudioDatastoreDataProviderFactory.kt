package com.example.mpcore.audio.internal.data.datastore.factory

import android.content.Context
import com.example.mpcore.audio.internal.data.datastore.AudioDatastoreDataProviderImpl
import com.example.mpcore.audio.internal.data.datastore.IAudioDatastoreDataProvider

internal object AudioDatastoreDataProviderFactory {


    fun create(context: Context): IAudioDatastoreDataProvider{
        return AudioDatastoreDataProviderImpl(AudioDataStoreFactory.create(context))
    }
}