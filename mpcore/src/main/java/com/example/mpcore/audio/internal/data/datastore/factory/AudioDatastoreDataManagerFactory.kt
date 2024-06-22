package com.example.mpcore.audio.internal.data.datastore.factory

import android.content.Context
import com.example.mpcore.audio.internal.data.datastore.AudioDatastoreMangerImpl
import com.example.mpcore.audio.internal.data.datastore.IAudioDatastoreManger

internal object AudioDatastoreDataManagerFactory {

    fun create(context: Context): IAudioDatastoreManger{
        return AudioDatastoreMangerImpl(AudioDatastoreDataProviderFactory.create(context))
    }
}