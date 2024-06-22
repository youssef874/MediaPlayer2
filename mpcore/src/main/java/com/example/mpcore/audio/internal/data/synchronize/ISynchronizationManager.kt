package com.example.mpcore.audio.internal.data.synchronize

internal interface ISynchronizationManager {


    suspend fun synchronize(vararg dataSynchronize: IDataSynchronize)

    fun registerCallbackListener(synchronisationCallback: SynchronisationCallback)

    suspend fun isSynchronized(vararg dataSynchronize: IDataSynchronize): Boolean
}