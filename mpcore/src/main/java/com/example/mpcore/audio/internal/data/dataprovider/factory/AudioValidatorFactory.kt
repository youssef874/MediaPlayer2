package com.example.mpcore.audio.internal.data.dataprovider.factory

import android.content.Context
import android.os.Build
import com.example.mpcore.audio.internal.data.dataprovider.IAudioInternalStorageValidator
import com.example.mpcore.audio.internal.data.dataprovider.NewAudioInternalStorageValidatorImpl
import com.example.mpcore.audio.internal.data.dataprovider.OldAudioInternalStorageValidatorImpl

internal object AudioValidatorFactory{

    fun create(context: Context): IAudioInternalStorageValidator{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            NewAudioInternalStorageValidatorImpl(context)
        }else{
            OldAudioInternalStorageValidatorImpl(context)
        }
    }
}