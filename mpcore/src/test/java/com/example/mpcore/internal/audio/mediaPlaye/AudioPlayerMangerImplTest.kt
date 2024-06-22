package com.example.mpcore.internal.audio.mediaPlaye

import android.net.Uri
import com.example.mpcore.audio.internal.mediaplayer.AudioPlayerMangerImpl
import com.example.mpcore.internal.audio.synchronize.datastore.FakeAudioDatastoreManger
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AudioPlayerMangerImplTest {

    private val context = RuntimeEnvironment.getApplication().applicationContext

    private val audioPlayerManger = AudioPlayerMangerImpl(
        mediaPlayer = FakeMediaPlayer,
        audioDataStoreManger = FakeAudioDatastoreManger
    )

    @Test
    fun `when call playAudio successful expect the result the progress to contain some value above 0 `()= runTest {
        val result = audioPlayerManger.playAudio(context, Uri.parse("test"))
        assert(result)
        val list = mutableListOf<Int>()
        launch {
            audioPlayerManger.observeAudioProgress().toList(list)
            println(list)
            assert(list.any { it>0 })
        }
    }

    @Test
    fun `when call stopPlayingAudio is successful expect the progress too`()= runTest {
        val uri = Uri.parse("test")
        FakeMediaPlayer.int(uri)
        val result = audioPlayerManger.stopPlayingAudio(context)
        assert(result)
        val list = mutableListOf<Int>()
        launch {
            val progression = audioPlayerManger.getAudioProgress()
            audioPlayerManger.observeAudioProgress().toList(list)
            println(list)
            assert(progression == list.last())

        }
    }

    @Test
    fun `when call resumePlayingAudio successful expect the progress to resume too`()= runTest {
        val uri = Uri.parse("test")
        FakeMediaPlayer.int(uri, isPlaying = false, at = 10)
        val result = audioPlayerManger.resumePlayingAudio(context)
        val list = mutableListOf<Int>()
        assert(result)
        launch {
            val progression = audioPlayerManger.getAudioProgress()
            audioPlayerManger.observeAudioProgress().toList(list)
            assert(list.any { it>progression })
        }
    }

    @Test
    fun `when call pausePlayingAudio successful expect the progress to stop`() = runTest {
        val uri = Uri.parse("test")
        audioPlayerManger.playAudio(context, uri)
        val result = audioPlayerManger.pausePlayingAudio(context)
        assert(result)
        val list = mutableListOf<Int>()
        launch {
            val progression = audioPlayerManger.getAudioProgress()
            audioPlayerManger.observeAudioProgress().toList(list)
            assert(list.last() == progression)
        }
    }

    fun clean(){
        FakeMediaPlayer.reset()
    }
}