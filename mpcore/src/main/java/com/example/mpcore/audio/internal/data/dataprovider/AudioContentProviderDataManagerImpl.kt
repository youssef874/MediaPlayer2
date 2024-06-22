package com.example.mpcore.audio.internal.data.dataprovider

import android.content.Context
import com.example.mpcore.audio.internal.data.dataprovider.factory.AudioValidatorFactory
import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel

internal class AudioContentProviderDataManagerImpl(
    private val context: Context
) : IAudioContentProviderDataManager {


    override suspend fun getAllAudio(): List<AudioDataProviderModel> {
        val validator = AudioValidatorFactory.create(context)
        return AudioInternalStorageProviderImpl(InternalStorageExtractorImp(context.contentResolver)).getAllAudio(
            validator
        )
    }

    override fun setOnDataChangesListener(onDataChanges: () -> Unit) {
        val validator = AudioValidatorFactory.create(context)
        AudioInternalStorageProviderImpl(InternalStorageExtractorImp(context.contentResolver)).setOnDataChangesListener(
            validator,
            onDataChanges
        )
    }
}