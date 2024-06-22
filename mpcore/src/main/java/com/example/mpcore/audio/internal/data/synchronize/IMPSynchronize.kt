package com.example.mpcore.audio.internal.data.synchronize

import android.content.Context

internal interface IMPSynchronize {

    suspend fun synchronize(context: Context): Boolean

    @SynchronizationState
    fun synchronizationState(): String

    fun reset()
}