package com.example.mpcore.audio.internal.data.synchronize

import android.content.Context

internal interface IDataSynchronize {

    val synchronizeModule: String

    /**
     * This method with determine if synchronization is needed or no
     * @return true if synchronization is required otherwise it will return false
     */
    suspend fun isRequireSynchronization(context: Context): Boolean

    /**
     * Call this method to synchronize
     * @return true if synchronization was success otherwise it will return false
     */
    suspend fun synchronize(context: Context): Boolean
}