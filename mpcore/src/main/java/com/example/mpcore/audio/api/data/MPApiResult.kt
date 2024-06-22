package com.example.mpcore.audio.api.data

import com.example.mpcore.audio.api.MPApiError

sealed class MPApiResult<out T> {

    data class Success<out T>(val data: T): MPApiResult<T>()

    data class Error(val error: MPApiError): MPApiResult<Nothing>()
}