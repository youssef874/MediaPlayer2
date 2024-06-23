package com.example.mpcore.internal.audio.synchronize

import android.content.Context
import com.example.mpcore.audio.internal.data.synchronize.SynchronisationCallback
import com.example.mpcore.audio.internal.data.synchronize.SynchronizationManagerImpl
import com.example.mpcore.internal.audio.synchronize.DataSynchronize.FakeDataSynchronizeWitSuccess
import com.example.mpcore.internal.audio.synchronize.DataSynchronize.FakeIDataSynchronizeFailed
import com.example.mpcore.internal.audio.synchronize.datastore.FakeAudioDatastoreManger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SynchronizationManagerImplTest {

    private val context: Context = RuntimeEnvironment.getApplication().applicationContext

    @Test
    fun when_thereAtLeastOneDataSynchronizeFailed_expect_SynchronizeManagerToBeFailed()= runTest {
        val synchronizationManagerImpl = SynchronizationManagerImpl(context,FakeAudioDatastoreManger)
        val deferred = CompletableDeferred<Boolean>()
        synchronizationManagerImpl.registerCallbackListener(object : SynchronisationCallback{
            override fun onStarted() {
                //TODO("Not yet implemented")
            }

            override fun onComplete() {
                deferred.complete(true)
            }

            override fun onFailed() {
                deferred.complete(false)
            }

        })
        FakeAudioDatastoreManger.getIsSdkInitialized().updateValue(false)
        synchronizationManagerImpl.synchronize(FakeIDataSynchronizeFailed,FakeDataSynchronizeWitSuccess)
        val result = deferred.await()
        assert(!result)
        assert(!synchronizationManagerImpl.isSynchronized())
    }

    @Test
    fun when_allDataSynchronizationSuccess_expect_synchronizeManagerToBeSuccess()= runTest {
        val synchronizationManagerImpl = SynchronizationManagerImpl(context,FakeAudioDatastoreManger)
        val deferred = CompletableDeferred<Boolean>()
        synchronizationManagerImpl.registerCallbackListener(object : SynchronisationCallback{
            override fun onStarted() {
                //TODO("Not yet implemented")
            }

            override fun onComplete() {
                deferred.complete(true)
            }

            override fun onFailed() {
                deferred.complete(false)
            }

        })
        synchronizationManagerImpl.synchronize(FakeDataSynchronizeWitSuccess)
        assert(deferred.await())
        assert(synchronizationManagerImpl.isSynchronized())
    }
}