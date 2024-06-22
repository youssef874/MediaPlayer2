package com.example.mediaplayer.repository

import com.example.mpcore.audio.api.data.model.RepeatMode

fun @RepeatMode Int.toEnum(): com.example.mediaplayer.data.model.RepeatMode{
    return when(this){
        RepeatMode.NO_REPEAT->com.example.mediaplayer.data.model.RepeatMode.NO_REPEAT
        RepeatMode.ONE_REPEAT->com.example.mediaplayer.data.model.RepeatMode.ONE_REPEAT
        RepeatMode.ALL_REPEAT->com.example.mediaplayer.data.model.RepeatMode.REPEAT_ALL
        else -> com.example.mediaplayer.data.model.RepeatMode.NO_REPEAT
    }
}

@RepeatMode
fun com.example.mediaplayer.data.model.RepeatMode.toAnnotation(): Int{
    return when(this){
        com.example.mediaplayer.data.model.RepeatMode.NO_REPEAT->RepeatMode.NO_REPEAT
        com.example.mediaplayer.data.model.RepeatMode.REPEAT_ALL->RepeatMode.ALL_REPEAT
        com.example.mediaplayer.data.model.RepeatMode.ONE_REPEAT->RepeatMode.ONE_REPEAT
    }
}