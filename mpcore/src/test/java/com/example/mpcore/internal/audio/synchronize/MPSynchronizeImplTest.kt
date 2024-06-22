package com.example.mpcore.internal.audio.synchronize

import android.content.Context
import com.example.mpcore.audio.internal.data.synchronize.MPSynchronizeImpl
import com.example.mpcore.audio.internal.data.synchronize.SynchronizationState
import com.example.mpcore.internal.audio.synchronize.synchronize.FakeSynchronizationManagerAlreadyCompleted
import com.example.mpcore.internal.audio.synchronize.synchronize.FakeSynchronizationManagerFailed
import com.example.mpcore.internal.audio.synchronize.synchronize.FakeSynchronizationManagerNotAlreadyCompleted
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class MPSynchronizeImplTest {

    private val context: Context = RuntimeEnvironment.getApplication().applicationContext
    private lateinit var mpSynchronizeImplTest: MPSynchronizeImpl

    @Test
    fun `when synchronization already completed expect synchronization state to be success`()= runTest {
        mpSynchronizeImplTest = MPSynchronizeImpl(FakeSynchronizationManagerAlreadyCompleted)
        val result = mpSynchronizeImplTest.synchronize(context)
        assert(result)
        val synchronizationState = mpSynchronizeImplTest.synchronizationState()
        assert(synchronizationState == SynchronizationState.SUCCESS)
    }

    @Test
    fun `when synchronization already not completed expect synchronization state to be success`()= runTest {
        mpSynchronizeImplTest = MPSynchronizeImpl(FakeSynchronizationManagerNotAlreadyCompleted)
        val result = mpSynchronizeImplTest.synchronize(context)
        assert(result)
        val synchronizationState = mpSynchronizeImplTest.synchronizationState()
        assert(synchronizationState == SynchronizationState.SUCCESS)
    }

    @Test
    fun `when synchronization failed expect synchronization state to be failed`()= runTest {
        mpSynchronizeImplTest = MPSynchronizeImpl(FakeSynchronizationManagerFailed)
        val result = mpSynchronizeImplTest.synchronize(context)
        assert(!result)
        val synchronizationState = mpSynchronizeImplTest.synchronizationState()
        assert(synchronizationState == SynchronizationState.FAILED)
    }

    @Test
    fun `when synchronization already completed then reset expect synchronization state to be none`()= runTest {
        mpSynchronizeImplTest = MPSynchronizeImpl(FakeSynchronizationManagerAlreadyCompleted)
        val result = mpSynchronizeImplTest.synchronize(context)
        assert(result)
        val synchronizationState = mpSynchronizeImplTest.synchronizationState()
        assert(synchronizationState == SynchronizationState.SUCCESS)
        mpSynchronizeImplTest.reset()
        assert(mpSynchronizeImplTest.synchronizationState() == SynchronizationState.NONE)
    }
}