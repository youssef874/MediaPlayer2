package com.example.mpcore.internal.audio.datastore

import com.example.mpcore.audio.api.data.model.RepeatMode
import com.example.mpcore.audio.internal.data.datastore.AudioDatastoreDataProviderImpl
import com.example.mpcore.audio.internal.data.datastore.data.AudioDataStoreModel
import com.example.mpcore.internal.audio.datastore.fake.FakeAudioDataStore
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
class AudioDatastoreDataProviderImplTest {

    private val audioDatastoreDataProviderImpl = AudioDatastoreDataProviderImpl(FakeAudioDataStore)
    private val isDatabaseSynchronizedWithContentProviderModel = AudioDataStoreModel(
        "isDatabaseSynchronizedWithContentProvider",
        false
    )

    @Test
    fun `when updating IsDatabaseSynchronizedWithContentProvider expect observe and getValue to return the updated value`() =
        runTest(timeout = 100.seconds) {
            val isDatabaseSynchronizedWithContentProvider =
                audioDatastoreDataProviderImpl.getIsDatabaseSynchronizedWithContentProvider(
                    isDatabaseSynchronizedWithContentProviderModel
                )

            isDatabaseSynchronizedWithContentProvider.updateValue(true)
            val result = isDatabaseSynchronizedWithContentProvider.getValue()
            assert(result)
        }

    private val isSdkSynchronizeModel = AudioDataStoreModel(
        key = "isSdkSynchronize",
        defaultValue = false
    )

    @Test
    fun `when updating getIsSDKSynchronizedProvider expect getValue to return the updated value`() =
        runTest {
            val isSDKSynchronizedProvider =
                audioDatastoreDataProviderImpl.getIsSDKSynchronizedProvider(
                    isSdkSynchronizeModel
                )
            isSDKSynchronizedProvider.updateValue(true)
            assert(isSDKSynchronizedProvider.getValue())
        }

    @Test
    fun `when updating LastPlayingSongId expect getValue to return the updated value`() =
        runTest {
            val lastPlayingSongIdModel = AudioDataStoreModel(
                key = "last_playing_song_is",
                defaultValue = 0L
            )
            val lastPlayingSongIdProvider =
                audioDatastoreDataProviderImpl.getLastPlayingSongId(
                    lastPlayingSongIdModel
                )
            lastPlayingSongIdProvider.updateValue(1L)
            assert(lastPlayingSongIdProvider.getValue() == 1L)
        }

    @Test
    fun `when updating audioProgression expect getValue to return the updated value`() =
        runTest {
            val audioProgressionModel = AudioDataStoreModel(
                key = "audio_progress",
                defaultValue = 0
            )
            val audioProgressProvider =
                audioDatastoreDataProviderImpl.getAudioProgression(
                    audioProgressionModel
                )
            audioProgressProvider.updateValue(30)
            assert(audioProgressProvider.getValue() == 30)
            launch {
                audioProgressProvider.updateValue(10)
                assert(audioProgressProvider.getValue() == 10)
            }.join()
        }

    @Test
    fun `when updating isInRandomMode expect getValue to return the updated value`() =
        runTest {
            val isInRandomModeModel = AudioDataStoreModel(
                key = "is_in_random_mode",
                defaultValue = false
            )
            val isInRandomModeProvider =
                audioDatastoreDataProviderImpl.getIsInRandomMode(
                    isInRandomModeModel
                )
            isInRandomModeProvider.updateValue(true)
            assert(isInRandomModeProvider.getValue())
        }

    @Test
    fun `when updating repeatMode expect getValue to return the updated value`() =
        runTest {
            val repeatModeModel = AudioDataStoreModel(
                key = "repeat_mode",
                defaultValue = RepeatMode.NO_REPEAT
            )
            val repeatModeProvider =
                audioDatastoreDataProviderImpl.getRepeatMode(
                    repeatModeModel
                )
            assert(repeatModeProvider.getValue() == RepeatMode.NO_REPEAT)
            launch {
                repeatModeProvider.updateValue(RepeatMode.ALL_REPEAT)
                assert(repeatModeProvider.getValue() == RepeatMode.ALL_REPEAT)
            }.join()
            launch {
                repeatModeProvider.updateValue(RepeatMode.ONE_REPEAT)
                assert(repeatModeProvider.getValue() == RepeatMode.ONE_REPEAT)
            }.join()
        }

    @Test
    fun `when updating DatabaseVersion expect getValue to return the updated value`() =
        runTest {
            val databaseVersionModel = AudioDataStoreModel(
                key = "database_version",
                defaultValue = 0
            )
            val databaseVersionProvider =
                audioDatastoreDataProviderImpl.getDatabaseVersionController(
                    databaseVersionModel
                )
            databaseVersionProvider.updateValue(2)
            assert(databaseVersionProvider.getValue() == 2)
        }

    @After
    fun clean() {
        runTest {
            FakeAudioDataStore.getBooleanDataStoreModifier().clear()
        }
    }

}