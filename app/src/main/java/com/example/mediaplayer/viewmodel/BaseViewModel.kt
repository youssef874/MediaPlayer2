package com.example.mediaplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpcore.logger.api.MPLoggerApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseViewModel<EVENT,STATE>: ViewModel() {

    abstract val uiState: StateFlow<STATE>

    private val _uiEvent = MutableSharedFlow<EVENT>()
    protected val uiEvent: SharedFlow<EVENT> = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            uiEvent.collectLatest { event->
                MPLoggerApi.DefaultMpLogger.d(
                    CLASS_NAME, TAG,
                    "init", "handle event: $event"
                )
                handleEvent(event)
            }
        }
    }

    abstract fun handleEvent(event: EVENT)

    fun onEvent(event: EVENT){
        MPLoggerApi.DefaultMpLogger.i(
            CLASS_NAME, TAG,
            "onEvent","event: $event"
        )
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    abstract fun clear()

    override fun onCleared() {
        clear()
        super.onCleared()
    }

    companion object{
        private const val CLASS_NAME = "BaseViewModel"
        private const val TAG = "APPLICATION"
    }
}