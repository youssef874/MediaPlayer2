package com.example.mpcore.audio.internal.data.datastore.factory

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.mpcore.audio.internal.data.datastore.AudioDataStore
import com.example.mpcore.audio.internal.data.datastore.IAudioDataStore

internal object AudioDataStoreFactory {

    @Volatile
    private var dataStore: DataStore<Preferences>? = null

    fun create(context: Context): IAudioDataStore{
        val sDataStore = dataStore ?: synchronized(this) {
            val data = PreferenceDataStoreFactory.create {
                val path = AudioDataStore.FILE_NAME
                context.preferencesDataStoreFile(path)
            }
            dataStore = data
            data
        }
        return AudioDataStore(sDataStore)
    }
}