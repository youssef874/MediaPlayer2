package com.example.mpcore.audio.api.data.model

import androidx.annotation.IntDef

@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
@IntDef(
    RepeatMode.NO_REPEAT,
    RepeatMode.ONE_REPEAT,
    RepeatMode.ALL_REPEAT
)

annotation class RepeatMode{

    companion object{
        const val NO_REPEAT = 0
        const val ONE_REPEAT = 1
        const val ALL_REPEAT = 2
    }
}
