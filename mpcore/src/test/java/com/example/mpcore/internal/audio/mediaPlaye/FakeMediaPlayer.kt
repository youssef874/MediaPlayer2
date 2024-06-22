package com.example.mpcore.internal.audio.mediaPlaye

import android.content.Context
import android.net.Uri
import com.example.mpcore.audio.internal.mediaplayer.IMediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal object FakeMediaPlayer: IMediaPlayer {

    private var uri: Uri? = null

    private var _isPlaying = false
    override val isPlaying: Boolean
        get() = _isPlaying

    var progress = 0

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null
    private var onAudioProgress: ((Int)->Unit)? = null

    private fun starProgress(){
        job?.cancel()
        job = null
        job = scope.launch {
            while (isPlaying){
                progress++
                onAudioProgress?.invoke(progress)
            }
        }
    }


    override fun playAudio(context: Context, uri: Uri, playAt: Int): Boolean {
        if (uri == Uri.EMPTY){
            _isPlaying = false
            return false
        }
        this.uri = uri
        _isPlaying = true
        starProgress()
        return true
    }

    override fun stopPlayingAudio(context: Context): Boolean {
      if (uri != null){
          _isPlaying = false
          stopProgress()
          return true
      }
        _isPlaying = false
        return false
    }

    override fun resumePlyingAudio(context: Context): Boolean {
       if (uri != null){
           _isPlaying = true
           starProgress()
           return true
       }
        return false
    }

    override fun pausePlayer(context: Context): Boolean {
        if (uri != null){
            _isPlaying = false
            stopProgress()
            return true
        }
        return false
    }

    private fun stopProgress(){
        job?.cancel()
        job = null
    }

    override fun setOnAudioCompletedListener(onAudioCompleted: () -> Unit) {
        //TODO("Not yet implemented")
    }

    override fun onAudioProgressionListener(onAudioProgress: (Int) -> Unit) {
        this.onAudioProgress = onAudioProgress
    }

    override fun unregisterToAudioProgress() {
       onAudioProgress = null
    }

    override fun getCurrentProgress(): Int {
        return progress
    }

    override fun updateCurrentPlayerPosition(position: Int): Boolean {
        TODO("Not yet implemented")
    }

    fun int(uri: Uri, at: Int = 0,isPlaying: Boolean? = null){
        if (this.uri == null){
            this.uri = uri
            if (at != 0){
                progress = at
            }
            if (isPlaying != null){
                this._isPlaying = isPlaying
            }
        }
    }

    fun reset(){
        progress = 0
        uri = null
        stopProgress()
        _isPlaying = false
        unregisterToAudioProgress()
    }
}