package com.example.mpcore.internal.audio.synchronize

import android.content.Context
import com.example.mpcore.audio.internal.data.synchronize.AudioContentProviderWithDatabaseSynchronization
import com.example.mpcore.internal.audio.synchronize.contentProvider.FakeAudioContentProviderDataManagerWithException
import com.example.mpcore.internal.audio.synchronize.contentProvider.FakeEmptyAudioContentProvider
import com.example.mpcore.internal.audio.synchronize.contentProvider.FakeNotEmptyAudioContentProvider
import com.example.mpcore.internal.audio.synchronize.database.FakeAudioDatabaseDataProvider
import com.example.mpcore.internal.audio.synchronize.datastore.FakeAudioDatastoreManger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toDuration

@RunWith(RobolectricTestRunner::class)
class AudioContentProviderWithDatabaseSynchronizationTest {

    private var context: Context = RuntimeEnvironment.getApplication().applicationContext

    private lateinit var audioContentProviderWithDatabaseSynchronization: AudioContentProviderWithDatabaseSynchronization


    @Test
    fun `when content provider has no data and user has all required permission expect synchronization to be success and audio table to have no data  `()= runTest {
        audioContentProviderWithDatabaseSynchronization = AudioContentProviderWithDatabaseSynchronization(
            audioDatabaseDataProvider = FakeAudioDatabaseDataProvider,
            audioDatastoreManger = FakeAudioDatastoreManger,
            audioContentProviderDataManager = FakeEmptyAudioContentProvider
        )
        val result = audioContentProviderWithDatabaseSynchronization.synchronize(context)
        assert(result)
        assert(FakeAudioDatabaseDataProvider.getAll().isEmpty())
    }

    @Test
    fun `when content provider user has no permission to access internal storage audio expect synchronization to fail `()= runTest {
        audioContentProviderWithDatabaseSynchronization = AudioContentProviderWithDatabaseSynchronization(
            audioDatabaseDataProvider = FakeAudioDatabaseDataProvider,
            audioDatastoreManger = FakeAudioDatastoreManger,
            audioContentProviderDataManager = FakeAudioContentProviderDataManagerWithException
        )
        backgroundScope.launch {
            val result = audioContentProviderWithDatabaseSynchronization.synchronize(context)
            assert(!result)
        }.join()
    }

    @Test
    fun `when content provider has data and user has all required permission expect synchronization to be success and audio table to have all data fetched from content provider  `()= runTest(timeout = 2000.seconds) {
        audioContentProviderWithDatabaseSynchronization = AudioContentProviderWithDatabaseSynchronization(
            audioDatabaseDataProvider = FakeAudioDatabaseDataProvider,
            audioDatastoreManger = FakeAudioDatastoreManger,
            audioContentProviderDataManager = FakeNotEmptyAudioContentProvider
        )
        backgroundScope.launch {
            val result = audioContentProviderWithDatabaseSynchronization.synchronize(context)
            assert(result)
            assert(FakeAudioDatabaseDataProvider.getAll().isNotEmpty())
            assert(FakeAudioDatabaseDataProvider.getAll().size == FakeNotEmptyAudioContentProvider.getAllAudio().size)
            FakeNotEmptyAudioContentProvider.getAllAudio().forEach { data->
                assert(FakeAudioDatabaseDataProvider.getAll().any { it.externalId == data.audioId })
            }
        }.join()
    }

}