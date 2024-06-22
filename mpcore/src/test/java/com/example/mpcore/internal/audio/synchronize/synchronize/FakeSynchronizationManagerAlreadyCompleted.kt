package com.example.mpcore.internal.audio.synchronize.synchronize

import com.example.mpcore.audio.internal.data.synchronize.IDataSynchronize
import com.example.mpcore.audio.internal.data.synchronize.ISynchronizationManager
import com.example.mpcore.audio.internal.data.synchronize.SynchronisationCallback

internal object FakeSynchronizationManagerAlreadyCompleted: ISynchronizationManager {

    private var synchronisationCallback: SynchronisationCallback? = null

    override suspend fun synchronize(vararg dataSynchronize: IDataSynchronize) {
        synchronisationCallback?.onComplete()
    }

    override fun registerCallbackListener(synchronisationCallback: SynchronisationCallback) {
        this.synchronisationCallback = synchronisationCallback
    }

    override suspend fun isSynchronized(vararg dataSynchronize: IDataSynchronize): Boolean {
        return true
    }
}