package com.example.mpcore.audio.internal.data.synchronize

import android.content.Context
import com.example.mpcore.audio.internal.data.synchronize.factory.AudioContentProviderWithDatabaseSynchronizationFactory
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class MPSynchronizeImpl(
    private val synchronizeManager: ISynchronizationManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IMPSynchronize {

    private var _state:@SynchronizationState String = SynchronizationState.NONE
    private val state:@SynchronizationState String get() = _state


    override suspend fun synchronize(context: Context): Boolean {
        if (synchronizeManager.isSynchronized()){
            _state = SynchronizationState.SUCCESS
            return true
        }
        val deferred = CompletableDeferred<Boolean>()
        synchronizeManager.registerCallbackListener(
            object : SynchronisationCallback {
                override fun onStarted() {
                    _state = SynchronizationState.STARTED
                }

                override fun onComplete() {
                    _state = SynchronizationState.SUCCESS
                    deferred.complete(true)
                }

                override fun onFailed() {
                    _state = SynchronizationState.FAILED
                    deferred.complete(false)
                }

            }
        )

        withContext(dispatcher){
            synchronizeManager.synchronize(
                AudioContentProviderWithDatabaseSynchronizationFactory.create(context)
            )
        }
        return deferred.await()
    }

    override fun synchronizationState(): String {
        return state
    }

    override fun reset() {
        _state = SynchronizationState.NONE

    }
}