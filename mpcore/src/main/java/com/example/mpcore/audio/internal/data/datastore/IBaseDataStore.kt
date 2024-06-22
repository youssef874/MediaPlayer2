package com.example.mpcore.audio.internal.data.datastore

import kotlinx.coroutines.flow.Flow

internal interface IBaseDataStore {

    /**
     * Call this method to get all existed value wither key in the datastore
     * @return [Flow] of [Map] of data key and their value
     */
    fun getAll(): Flow<Map<String, Any>>

    /**
     * Delete all data in the datastore
     */
    suspend fun clear()
}