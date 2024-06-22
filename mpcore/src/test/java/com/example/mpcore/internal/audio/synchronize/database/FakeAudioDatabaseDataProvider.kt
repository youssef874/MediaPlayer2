package com.example.mpcore.internal.audio.synchronize.database

import com.example.mpcore.audio.internal.data.database.IAudioDatabaseDataProvider
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

internal object FakeAudioDatabaseDataProvider: IAudioDatabaseDataProvider {
    private val dataList = mutableListOf<AudioEntity>()
    var notify: (suspend ()->Unit)? = null
    override suspend fun changeAudioFavoriteStatus(isFavorite: Boolean, audioId: Long): Boolean {
        dataList.firstOrNull { it.id == audioId }?.let {
            dataList[dataList.indexOf(it)] = it.copy(isFavorite = isFavorite)
        }
        notify?.invoke()
        return true
    }

    override suspend fun add(data: AudioEntity): Long {
        var id: Long = 0
        dataList.firstOrNull { it.externalId == data.externalId }?.let {
            dataList[dataList.indexOf(it)] = data
            id = it.id
        }?:run {
            id = generateId()
            val audioEntity = data.copy(id = id)
            dataList.add(audioEntity)
        }
        notify?.invoke()
        return id
    }

    private fun generateId(): Long{
        val copy = ArrayList(dataList)
        return if (copy.isEmpty()){
            0L
        }else{
            copy.sortBy { it.id }
            copy.last().id+1
        }
    }

    override suspend fun update(data: AudioEntity): Boolean {
        dataList.firstOrNull { it.id == data.id }?.let {
            dataList[dataList.indexOf(it)] = data
            notify?.invoke()
            return true
        }?:return false
    }

    override suspend fun delete(data: AudioEntity) {
        dataList.firstOrNull { it.id == data.id }?.let {
            dataList.remove(it)
            notify?.invoke()
        }
    }

    override suspend fun getById(id: Long): AudioEntity? {
        return dataList.firstOrNull { it.id == id }
    }

    override fun observeById(id: Long): Flow<AudioEntity?> {
        return channelFlow {
            send(dataList.firstOrNull { it.id == id })
            notify = {
                send(dataList.firstOrNull { it.id == id })
            }
        }
    }

    override suspend fun getAll(): List<AudioEntity> {
        return ArrayList(dataList)
    }

    override fun observeAll(): Flow<List<AudioEntity>> {
        return channelFlow {
            send(dataList)
            notify = {
                send(dataList)
            }
        }
    }

    override suspend fun deleteAll() {
        dataList.clear()
    }
}