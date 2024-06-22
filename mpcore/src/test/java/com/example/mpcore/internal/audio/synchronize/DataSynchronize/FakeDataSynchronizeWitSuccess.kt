package com.example.mpcore.internal.audio.synchronize.DataSynchronize

import android.content.Context
import com.example.mpcore.audio.internal.data.synchronize.IDataSynchronize

internal object FakeDataSynchronizeWitSuccess: IDataSynchronize {
    override val synchronizeModule: String
        get() = "TEST1"
    private var isRequireSynchronization = true

    override suspend fun isRequireSynchronization(context: Context): Boolean {
        return isRequireSynchronization
    }

    override suspend fun synchronize(context: Context): Boolean {
        isRequireSynchronization = false
        return true
    }
}