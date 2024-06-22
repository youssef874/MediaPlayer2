package com.example.mpcore.audio.internal.data.dataprovider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

internal class OldAudioInternalStorageValidatorImpl(private val context: Context): IAudioInternalStorageValidator {

    override fun permissionRequired(): List<String> {
        val list = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED){
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        return list
    }
}