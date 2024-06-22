package com.example.mpcore.audio.internal.data.datastore

import com.example.mpcore.audio.api.data.model.RepeatMode
import com.example.mpcore.audio.internal.data.datastore.data.AudioDataStoreModel

internal class AudioDatastoreMangerImpl(
    private val audioDataProvider: IAudioDatastoreDataProvider
) : IAudioDatastoreManger {

    private val isDatabaseSynchronizedWithContentProviderModel = AudioDataStoreModel(
        key = "isDatabaseSynchronizedWithContentProvider",
        defaultValue = false
    )

    override fun getIsDatabaseSynchronizedWithContentProvider(): IAudioDataStoreController<Boolean> {
        return audioDataProvider.getIsDatabaseSynchronizedWithContentProvider(
            isDatabaseSynchronizedWithContentProviderModel
        )
    }

    private val isSDKSynchronized = AudioDataStoreModel(
        key = "is_sdk_synchronised",
        defaultValue = false
    )

    override fun getIsSdkInitialized(): IAudioDataStoreController<Boolean> {
        return audioDataProvider.getIsSDKSynchronizedProvider(
            isSDKSynchronized
        )
    }

    private val lastPlayingSongIdModel = AudioDataStoreModel(
        key = "last_playing_song_id",
        defaultValue = -1L
    )

    override fun getLastPlayingSongId(): IAudioDataStoreController<Long> {
       return audioDataProvider.getLastPlayingSongId(lastPlayingSongIdModel)
    }

    private val audioProgress = AudioDataStoreModel(
        key = "audio_progress",
        defaultValue = 0
    )

    override fun getAudioProgression(): IAudioDataStoreController<Int> {
        return audioDataProvider.getAudioProgression(audioProgress)
    }

    private val isInRandomModeModel = AudioDataStoreModel(
        key = "is_in_random_mode",
        defaultValue = false
    )

    override fun getIsInRandomMode(): IAudioDataStoreController<Boolean> {
        return audioDataProvider.getIsInRandomMode(isInRandomModeModel)
    }

    private val repeatModeModel = AudioDataStoreModel(
        key = "repeat_mode",
        defaultValue = RepeatMode.NO_REPEAT
    )

    override fun getRepeatMode(): IAudioDataStoreController<Int> {
        return audioDataProvider.getRepeatMode(repeatModeModel)
    }

    private val databaseVersionModel = AudioDataStoreModel(
        key = "database_version",
        defaultValue = 0
    )

    override fun getDatabaseVersionController(): IAudioDataStoreController<Int> {
        return audioDataProvider.getDatabaseVersionController(databaseVersionModel)
    }

}