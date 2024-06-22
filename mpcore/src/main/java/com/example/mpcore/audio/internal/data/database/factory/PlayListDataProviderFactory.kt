package com.example.mpcore.audio.internal.data.database.factory

import android.content.Context
import com.example.mpcore.audio.internal.data.database.IPlayListDatabaseDataProvider
import com.example.mpcore.audio.internal.data.database.MPDatabase
import com.example.mpcore.audio.internal.data.database.PlayListDatabaseDataProviderImpl

internal object PlayListDataProviderFactory {

    fun create(context: Context): IPlayListDatabaseDataProvider {
        return PlayListDatabaseDataProviderImpl(MPDatabase.create(context).getPlayListDao(),MPDatabase.create(context).getAudioDao())
    }
}