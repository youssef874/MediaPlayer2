package com.example.mpcore.audio.internal.data.database.factory

import android.content.Context
import com.example.mpcore.audio.internal.data.database.AudioDatabaseProviderImpl
import com.example.mpcore.audio.internal.data.database.IAudioDatabaseDataProvider
import com.example.mpcore.audio.internal.data.database.MPDatabase

internal object AudioDataBaseProviderFactory {

    fun create(context: Context): IAudioDatabaseDataProvider{
       return AudioDatabaseProviderImpl(MPDatabase.create(context).getAudioDao())
    }
}