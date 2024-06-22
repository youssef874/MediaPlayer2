package com.example.mpcore.common.internal

import kotlinx.coroutines.flow.Flow

/**
 * This represent the base structure as intermediate from dao to the respective data manager
 */
interface IBaseDatabaseDataProvider<T> {

    /**
     * Add data to database
     * @param data: data to be added
     * @return the added data id
     */
    suspend fun add(data: T): Long

    /**
     * Update data in database
     * @param data: the new data to be updated with
     */
    suspend fun update(data: T): Boolean

    /**
     * Delete data from database
     * @param data: the data to be deleted
     */
    suspend fun delete(data: T)

    /**
     * Get data from database by id
     * @param id: the entity id
     * @return the data if there any data with the requested is otherwise will return null
     */
    suspend fun getById(id: Long): T?

    /**
     * Get data from database by id as [Flow]
     * @param id: the entity id
     * @return the data if there any data with the requested is otherwise will return null
     */
    fun observeById(id: Long): Flow<T?>

    /**
     * Get all existed data in the T table in database
     * @return list of data if there any otherwise it will return empty list
     */
    suspend fun getAll(): List<T>

    /**
     * Get all existed data in the T table in database as [Flow]
     * @return list of data if there any otherwise it will return empty list
     */
    fun observeAll(): Flow<List<T>>

    suspend fun deleteAll()
}