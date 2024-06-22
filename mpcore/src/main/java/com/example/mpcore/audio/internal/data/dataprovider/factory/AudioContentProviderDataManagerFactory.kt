package com.example.mpcore.audio.internal.data.dataprovider.factory

import android.content.Context
import com.example.mpcore.audio.internal.data.dataprovider.AudioContentProviderDataManagerImpl
import com.example.mpcore.audio.internal.data.dataprovider.IAudioContentProviderDataManager

internal object AudioContentProviderDataManagerFactory {

    fun create(context: Context): IAudioContentProviderDataManager{
        return AudioContentProviderDataManagerImpl(context)
    }
}