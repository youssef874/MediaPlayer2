package com.example.mpcore.audio.internal.data.datastore

import kotlinx.coroutines.flow.Flow

/**
 * A structure for datastore action
 * (update,observe and get)
 */
interface IAudioDataStoreController<T> {

    /**
     * Call this to update the data store
     * @param data: the data to be update with
     */
    suspend fun updateValue(data: T)

    /**
     * Observe the data store change
     * @return [Flow]
     */
    fun observeValue(): Flow<T>

    /**
     * Call this to get the value from datastore
     */
    suspend fun getValue(): T
}