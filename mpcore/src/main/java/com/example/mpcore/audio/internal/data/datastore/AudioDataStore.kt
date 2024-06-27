package com.example.mpcore.audio.internal.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

internal class AudioDataStore(
    private val dataStore: DataStore<Preferences>
): IAudioDataStore {

    companion object{
        const val FILE_NAME = "mp_datastore"
    }

    override fun getIntDataStoreModifier(): IDataStoreModifier<Int> {
        return IntDataStoreModifierImpl(dataStore)
    }

    override fun getStringDataStoreModifier(): IDataStoreModifier<String> {
        return StringDataStoreModifier(dataStore)
    }

    override fun getBooleanDataStoreModifier(): IDataStoreModifier<Boolean> {
       return BooleanDataStoreModifierImpl(dataStore)
    }

    override fun getFloatDataStoreModifier(): IDataStoreModifier<Float> {
        return FloatDataStoreModifierImpl(dataStore)
    }

    override fun getLongDataStoreModifier(): IDataStoreModifier<Long> {
        return LongDataStoreModifierImpl(dataStore)
    }

}