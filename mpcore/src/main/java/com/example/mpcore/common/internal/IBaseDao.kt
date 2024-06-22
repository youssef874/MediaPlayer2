package com.example.mpcore.common.internal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * This represent a base structure for any dao in the sdk, so all the dao will inherit from this interface
 * This is generic to give flexibility to the the dao that inherit from this interface to specify which entity
 * to be dealing with
 */
@Dao
internal interface IBaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: T): Long

    @Update
    suspend fun update(data: T): Int

    @Delete
    suspend fun delete(data: T)
}