package com.example.mpcore.internal.audio.synchronize.datastore

import com.example.mpcore.audio.api.data.model.RepeatMode
import com.example.mpcore.audio.internal.data.datastore.IAudioDataStoreController
import com.example.mpcore.audio.internal.data.datastore.IAudioDatastoreManger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow

internal object FakeAudioDatastoreManger: IAudioDatastoreManger {
    private var isDatabaseSynchronizedWithContent = false
    private var notifyIsDatabaseSynchronizedWithContent: (suspend (Boolean)->Unit)? = null

    override fun getIsDatabaseSynchronizedWithContentProvider(): IAudioDataStoreController<Boolean> {
        return object :IAudioDataStoreController<Boolean>{
            override suspend fun updateValue(data: Boolean) {
                isDatabaseSynchronizedWithContent = data
                notifyIsDatabaseSynchronizedWithContent?.invoke(data)
            }

            override fun observeValue(): Flow<Boolean> {
                return channelFlow {
                    send(isDatabaseSynchronizedWithContent)
                    notifyIsDatabaseSynchronizedWithContent = {
                        send(it)
                    }
                }
            }

            override suspend fun getValue(): Boolean {
                return isDatabaseSynchronizedWithContent
            }

        }
    }

    private var isSdkInitialized = false
    private var notifyIsSdkInitialized: (suspend (Boolean)->Unit)? = null

    override fun getIsSdkInitialized(): IAudioDataStoreController<Boolean> = object : IAudioDataStoreController<Boolean>{
        override suspend fun updateValue(data: Boolean) {
            isSdkInitialized = data
            notifyIsSdkInitialized?.invoke(data)
        }

        override fun observeValue(): Flow<Boolean> {
            return channelFlow {
                send(isSdkInitialized)
                notifyIsSdkInitialized = {
                    send(it)
                }
            }
        }

        override suspend fun getValue(): Boolean {
            return isSdkInitialized
        }

    }

    private var lastPlayingSong = 0L
    private var notifyLastPlayingSong: (suspend (Long)->Unit)? = null

    override fun getLastPlayingSongId(): IAudioDataStoreController<Long> {
        return object :IAudioDataStoreController<Long>{
            override suspend fun updateValue(data: Long) {
                lastPlayingSong = data
                notifyLastPlayingSong?.invoke(data)
            }

            override fun observeValue(): Flow<Long> {
                return flow {
                    emit(lastPlayingSong)
                    notifyLastPlayingSong = {
                        emit(it)
                    }
                }
            }

            override suspend fun getValue(): Long {
                return lastPlayingSong
            }

        }
    }

    private var audioProgress = 0
    private var notifyAudioProgress: (suspend (Int)->Unit)? = null

    override fun getAudioProgression(): IAudioDataStoreController<Int> {
        return object : IAudioDataStoreController<Int>{
            override suspend fun updateValue(data: Int) {
                audioProgress = data
            }

            override fun observeValue(): Flow<Int> {
                return flow {
                    emit(audioProgress)
                    notifyAudioProgress = {
                        emit(it)
                    }
                }
            }

            override suspend fun getValue(): Int {
                return audioProgress
            }

        }
    }

    private var isInRandomMode = false
    private var notifyIsInRandom: (suspend (Boolean)->Unit)? = null

    override fun getIsInRandomMode(): IAudioDataStoreController<Boolean> {
        return object :IAudioDataStoreController<Boolean>{
            override suspend fun updateValue(data: Boolean) {
                isInRandomMode = data
                notifyIsInRandom?.invoke(data)
            }

            override fun observeValue(): Flow<Boolean> {
                return flow {
                    emit(isInRandomMode)
                    notifyIsInRandom = {
                        emit(it)
                    }
                }
            }

            override suspend fun getValue(): Boolean {
                return isInRandomMode
            }

        }
    }

    private var repeatMode = RepeatMode.NO_REPEAT
    private var notifyRepeatMode: (suspend (@RepeatMode Int)->Unit)? = null

    override fun getRepeatMode(): IAudioDataStoreController<Int> {
        return object :IAudioDataStoreController<Int>{
            override suspend fun updateValue(data: Int) {
                repeatMode = data
                notifyRepeatMode?.invoke(data)
            }

            override fun observeValue(): Flow<Int> {
                return flow {
                    emit(repeatMode)
                    notifyRepeatMode = {
                        emit(it)
                    }
                }
            }
            override suspend fun getValue(): Int {
                return repeatMode
            }

        }
    }

    private var databaseVersion = 0
    private var notifyDatabase: (suspend (Int)->Unit)? = null

    override fun getDatabaseVersionController(): IAudioDataStoreController<Int> {
        return object :IAudioDataStoreController<Int>{
            override suspend fun updateValue(data: Int) {
                databaseVersion = data
                notifyDatabase?.invoke(data)
            }

            override fun observeValue(): Flow<Int> {
                return flow {
                    emit(databaseVersion)
                    notifyDatabase= {
                        emit(it)
                    }
                }
            }

            override suspend fun getValue(): Int {
                return databaseVersion
            }

        }
    }
}