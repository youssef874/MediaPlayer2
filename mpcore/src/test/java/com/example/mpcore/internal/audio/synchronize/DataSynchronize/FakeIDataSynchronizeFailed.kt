package com.example.mpcore.internal.audio.synchronize.DataSynchronize

import android.content.Context
import com.example.mpcore.audio.internal.data.synchronize.IDataSynchronize

internal object FakeIDataSynchronizeFailed: IDataSynchronize {
    override val synchronizeModule: String
        get() = "TEST2"
    private val isRequireSynchronization = true

    override suspend fun isRequireSynchronization(context: Context): Boolean {
        return isRequireSynchronization
    }

    override suspend fun synchronize(context: Context): Boolean {
        return false
    }
}