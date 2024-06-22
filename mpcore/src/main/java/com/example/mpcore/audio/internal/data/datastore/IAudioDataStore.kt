package com.example.mpcore.audio.internal.data.datastore

/**
 * Abstract representation of data sore
 */
internal interface IAudioDataStore {

    fun getIntDataStoreModifier(): IDataStoreModifier<Int>

    fun getStringDataStoreModifier(): IDataStoreModifier<String>

    fun getBooleanDataStoreModifier() : IDataStoreModifier<Boolean>

    fun getFloatDataStoreModifier(): IDataStoreModifier<Float>

    fun getLongDataStoreModifier(): IDataStoreModifier<Long>
}