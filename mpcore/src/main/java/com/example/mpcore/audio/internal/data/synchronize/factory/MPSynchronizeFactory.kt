package com.example.mpcore.audio.internal.data.synchronize.factory

import android.content.Context
import com.example.mpcore.audio.internal.data.datastore.factory.AudioDatastoreDataManagerFactory
import com.example.mpcore.common.internal.SDKComponent
import com.example.mpcore.audio.internal.data.synchronize.IMPSynchronize
import com.example.mpcore.audio.internal.data.synchronize.MPSynchronizeImpl
import com.example.mpcore.audio.internal.data.synchronize.SynchronizationManagerImpl

internal object MPSynchronizeFactory {

    fun create(context: Context): IMPSynchronize {
        return MPSynchronizeImpl(SynchronizationManagerImpl(context, AudioDatastoreDataManagerFactory.create(context)))
    }
}