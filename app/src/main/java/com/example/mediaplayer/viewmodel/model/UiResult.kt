package com.example.mediaplayer.viewmodel.model

import com.example.mpcore.audio.api.MPApiError

sealed class UiResult<T> {

    class Loading<T>(): UiResult<T>()
    data class Success<T>(val data: T): UiResult<T>()
    data class Error<T>(val error: MPApiError): UiResult<T>()
}