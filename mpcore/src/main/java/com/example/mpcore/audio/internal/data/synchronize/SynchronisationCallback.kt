package com.example.mpcore.audio.internal.data.synchronize

internal interface SynchronisationCallback {

    fun onStarted()

    fun onComplete()

    fun onFailed()
}