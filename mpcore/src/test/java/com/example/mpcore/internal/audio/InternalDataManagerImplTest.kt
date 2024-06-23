package com.example.mpcore.internal.audio

import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.audio.api.data.model.MPAudio
import com.example.mpcore.audio.internal.data.DataCode
import com.example.mpcore.audio.internal.data.InternalDataManagerImpl
import com.example.mpcore.internal.audio.database.dao.FakeAudioDao
import com.example.mpcore.internal.audio.synchronize.contentProvider.FakeAudioContentProviderDataManagerWithException
import com.example.mpcore.internal.audio.synchronize.contentProvider.FakeEmptyAudioContentProvider
import com.example.mpcore.internal.audio.synchronize.contentProvider.FakeNotEmptyAudioContentProvider
import com.example.mpcore.internal.audio.synchronize.database.FakeAudioDatabaseDataProvider
import com.example.mpcore.internal.audio.synchronize.datastore.FakeAudioDatastoreManger
import com.example.mpcore.internal.audio.synchronize.synchronize.FakeMPSynchronizeSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class InternalDataManagerImplTest {

    private val context = RuntimeEnvironment.getApplication().applicationContext
    private lateinit var internalDataManagerImpl: InternalDataManagerImpl
    private val fakeAudioDatabaseDataProvider = FakeAudioDatabaseDataProvider(FakeAudioDao)

    @Before
    fun setup(){
        CoroutineScope(Dispatchers.IO).launch{
            fakeAudioDatabaseDataProvider.deleteAll()
        }
    }

    @Test
    fun `when content provider had data and have permission and synchronization completed expect getAllAudio to get list not empty`()= runTest {
        internalDataManagerImpl = InternalDataManagerImpl(
            FakeMPSynchronizeSuccess(
                fakeAudioDatabaseDataProvider,
                FakeNotEmptyAudioContentProvider,
                FakeAudioDatastoreManger
            ),
            fakeAudioDatabaseDataProvider,
            FakeNotEmptyAudioContentProvider,
            FakeAudioDatastoreManger,context
        )
        FakeNotEmptyAudioContentProvider.removeChangesIfExist()
        val result = internalDataManagerImpl.getAllAudio()
        assert(result is MPApiResult.Success)
        val list = (result as MPApiResult.Success<List<MPAudio>>).data
        println("$list")
        assert(list.isNotEmpty())
        assert(list.first().album == "album1")
        assert(list.last().album == "album2")
    }

    @Test
    fun `when content provider no data and have permission and synchronization completed expect getAllAudio to get empty list`()= runTest {
        internalDataManagerImpl = InternalDataManagerImpl(
            FakeMPSynchronizeSuccess(
                fakeAudioDatabaseDataProvider,
                FakeEmptyAudioContentProvider,
                FakeAudioDatastoreManger
            ),
            fakeAudioDatabaseDataProvider,
            FakeEmptyAudioContentProvider,
            FakeAudioDatastoreManger,context
        )
        val result = internalDataManagerImpl.getAllAudio()
        assert(result is MPApiResult.Success)
        val list = (result as MPApiResult.Success<List<MPAudio>>).data
        println("list: $list")
        assert(list.isEmpty())
    }

    @Test
    fun `when content provider has data and don't have permission and synchronization completed expect getAllAudio to get error`()= runTest {
        internalDataManagerImpl = InternalDataManagerImpl(
            FakeMPSynchronizeSuccess(
                fakeAudioDatabaseDataProvider,
                FakeAudioContentProviderDataManagerWithException,
                FakeAudioDatastoreManger
            ),
            fakeAudioDatabaseDataProvider,
            FakeEmptyAudioContentProvider,
            FakeAudioDatastoreManger,context
        )
        val result = internalDataManagerImpl.getAllAudio()
        assert(result is MPApiResult.Error)
        val error = (result as MPApiResult.Error)
        assert(error.error.errorCode == DataCode.PERMISSION_MISSED)
    }

    @Test
    fun `when content provider data changes and have permission and synchronization completed expect getAllAudio to contain the new added data`()= runTest {
        internalDataManagerImpl = InternalDataManagerImpl(
            FakeMPSynchronizeSuccess(
                fakeAudioDatabaseDataProvider,
                FakeNotEmptyAudioContentProvider,
                FakeAudioDatastoreManger
            ),
            fakeAudioDatabaseDataProvider,
            FakeNotEmptyAudioContentProvider,
            FakeAudioDatastoreManger,context
        )
        FakeNotEmptyAudioContentProvider.changeData()
        val result = internalDataManagerImpl.getAllAudio()
        assert(result is MPApiResult.Success)
        val list = (result as MPApiResult.Success<List<MPAudio>>).data
        assert(list.isNotEmpty())
        assert(list.last().album == "album3")
    }
}