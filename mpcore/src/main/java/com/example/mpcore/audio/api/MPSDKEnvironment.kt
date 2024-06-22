package com.example.mpcore.audio.api

import android.content.Context
import androidx.startup.AppInitializer
import com.example.mpcore.common.internal.ContextInitializer

object MPSDKEnvironment {

    fun initializeMPSDK(context: Context){
        AppInitializer.getInstance(context).initializeComponent(ContextInitializer::class.java)
    }
}