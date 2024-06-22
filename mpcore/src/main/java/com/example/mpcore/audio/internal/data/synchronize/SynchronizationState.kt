package com.example.mpcore.audio.internal.data.synchronize

import androidx.annotation.StringDef

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
@StringDef(
    SynchronizationState.FAILED,
    SynchronizationState.STARTED,
    SynchronizationState.SUCCESS,
    SynchronizationState.NONE
)

annotation class SynchronizationState{

    companion object{
        const val STARTED = "synchronization_started"
        const val FAILED = "synchronization_failed"
        const val SUCCESS = "synchronization_success"
        const val NONE = "unknown"
    }
}
