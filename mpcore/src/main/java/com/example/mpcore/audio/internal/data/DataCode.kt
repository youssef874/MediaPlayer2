package com.example.mpcore.audio.internal.data

import androidx.annotation.IntDef

@IntDef(
    DataCode.PERMISSION_MISSED,
    DataCode.SYNCHRONIZATION_NOT_COMPLETE,
    DataCode.SQL_FAILED,
    DataCode.AUDIO_ALREADY_ATTACHED_TO_PLAYLIST,
    DataCode.ELEMENT_NOT_FOUND
)

annotation class DataCode{

    companion object{
        const val PERMISSION_MISSED = 1
        const val SYNCHRONIZATION_NOT_COMPLETE = 2
        const val SQL_FAILED = 3
        const val AUDIO_ALREADY_ATTACHED_TO_PLAYLIST = 4
        const val ELEMENT_NOT_FOUND = 5
    }
}
