package com.example.mpcore.internal.audio.synchronize

import android.content.Context
import com.example.mpcore.audio.api.exception.MissingPermissionException
import com.example.mpcore.audio.internal.data.synchronize.AudioContentProviderWithDatabaseSynchronization
import com.example.mpcore.internal.audio.database.dao.FakeAudioDao
import com.example.mpcore.internal.audio.synchronize.contentProvider.FakeAudioContentProviderDataManagerWithException
import com.example.mpcore.internal.audio.synchronize.contentProvider.FakeEmptyAudioContentProvider
import com.example.mpcore.internal.audio.synchronize.contentProvider.FakeNotEmptyAudioContentProvider
import com.example.mpcore.internal.audio.synchronize.database.FakeAudioDatabaseDataProvider
import com.example.mpcore.internal.audio.synchronize.datastore.FakeAudioDatastoreManger
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
class AudioContentProviderWithDatabaseSynchronizationTest {

    private var context: Context = RuntimeEnvironment.getApplication().applicationContext

    private lateinit var audioContentProviderWithDatabaseSynchronization: AudioContentProviderWithDatabaseSynchronization

    private val fakeAudioDatabaseDataProvider = FakeAudioDatabaseDataProvider(FakeAudioDao)


    @Test
    fun `when content provider has no data and user has all required permission expect synchronization to be success and audio table to have no data  `()= runTest {
        audioContentProviderWithDatabaseSynchronization = AudioContentProviderWithDatabaseSynchronization(
            audioDatabaseDataProvider = fakeAudioDatabaseDataProvider,
            audioDatastoreManger = FakeAudioDatastoreManger,
            audioContentProviderDataManager = FakeEmptyAudioContentProvider
        )
        fakeAudioDatabaseDataProvider.deleteAll()
        val result = audioContentProviderWithDatabaseSynchronization.synchronize(context)
        assert(result)
        val list = fakeAudioDatabaseDataProvider.getAll()
        println("$list")
        assert(list.isEmpty())
    }

    @Test(expected = MissingPermissionException::class)
    fun `when content provider user has no permission to access internal storage audio expect synchronization to fail `()= runTest {
        audioContentProviderWithDatabaseSynchronization = AudioContentProviderWithDatabaseSynchronization(
            audioDatabaseDataProvider = fakeAudioDatabaseDataProvider,
            audioDatastoreManger = FakeAudioDatastoreManger,
            audioContentProviderDataManager = FakeAudioContentProviderDataManagerWithException
        )
        launch {
            audioContentProviderWithDatabaseSynchronization.synchronize(context)
        }.join()
    }

    @Test
    fun `when content provider has data and user has all required permission expect synchronization to be success and audio table to have all data fetched from content provider  `()= runTest(timeout = 2000.seconds) {
        audioContentProviderWithDatabaseSynchronization = AudioContentProviderWithDatabaseSynchronization(
            audioDatabaseDataProvider = fakeAudioDatabaseDataProvider,
            audioDatastoreManger = FakeAudioDatastoreManger,
            audioContentProviderDataManager = FakeNotEmptyAudioContentProvider
        )
        launch {
            fakeAudioDatabaseDataProvider.deleteAll()
            val result = audioContentProviderWithDatabaseSynchronization.synchronize(context)
            assert(result)
            assert(fakeAudioDatabaseDataProvider.getAll().isNotEmpty())
            assert(fakeAudioDatabaseDataProvider.getAll().size == FakeNotEmptyAudioContentProvider.getAllAudio().size)
            FakeNotEmptyAudioContentProvider.getAllAudio().forEach { data->
                assert(fakeAudioDatabaseDataProvider.getAll().any { it.externalId == data.audioId })
            }
        }.join()
    }

}