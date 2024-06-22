package com.example.mpcore.audio.internal.data.factory

import android.content.Context
import com.example.mpcore.audio.internal.data.IInternalPlayListDataManager
import com.example.mpcore.audio.internal.data.InternalPlayListDataManagerImpl
import com.example.mpcore.audio.internal.data.database.factory.PlayListDataProviderFactory
import com.example.mpcore.audio.internal.data.synchronize.factory.MPSynchronizeFactory

internal object InternalPlayListDataManagerFactory {

    private var INSTANCE: IInternalPlayListDataManager? = null

    fun create(context: Context): IInternalPlayListDataManager {
        return INSTANCE?: synchronized(this){
            val instance = InternalPlayListDataManagerImpl(
                playListDatabaseDataProviderImpl = PlayListDataProviderFactory.create(context)

            )
            INSTANCE = instance
            instance
        }
    }
}