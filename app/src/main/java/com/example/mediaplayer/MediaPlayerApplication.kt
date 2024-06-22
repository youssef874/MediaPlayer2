package com.example.mediaplayer

import android.app.Application
import com.example.mpcore.audio.api.MPSDKEnvironment
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MediaPlayerApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        MPSDKEnvironment.initializeMPSDK(this)
    }
}