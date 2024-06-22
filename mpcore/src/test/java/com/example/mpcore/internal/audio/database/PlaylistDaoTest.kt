package com.example.mpcore.internal.audio.database

import androidx.room.Room
import com.example.mpcore.audio.internal.data.database.AudioDao
import com.example.mpcore.audio.internal.data.database.IPlayListDao
import com.example.mpcore.audio.internal.data.database.MPDatabase
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpstorage.database.internal.entity.PlayListEntity
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class PlaylistDaoTest {

    private lateinit var mpDatabase: MPDatabase
    private lateinit var audioDao: AudioDao
    private lateinit var playListDao: IPlayListDao

    @Before
    fun setup(){
        mpDatabase = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication().applicationContext,
            MPDatabase::class.java)
            .build()
        audioDao = mpDatabase.getAudioDao()
        playListDao = mpDatabase.getPlayListDao()
    }

    @Test
    fun `when there no playlist expect getAll to return empty list`()= runTest {
        launch(Dispatchers.IO){
            val result = playListDao.getAll()
            assert(result.isEmpty())
        }.join()
    }

    @Test
    fun `when there a playList expect getAll to return a non emptyList`()= runTest {
        val playListEntity = PlayListEntity(name = "playList1")
        withContext(Dispatchers.IO){
            playListDao.insert(playListEntity)
            assert(playListDao.getAll().isNotEmpty())
        }
    }

    @Test
    fun `when add playlist and then attach it to playlist expect getListOfPlayListForAudio to have a non empty playlist`()=
        runTest {
            val playListEntity = PlayListEntity(name = "playList1")
            val audio = AudioEntity(
                album = "Album1",
                songName = "name1",
                uri = "uri1",
                artist = "artist1",
                duration = 100,
                size = 1000
            )
            withContext(Dispatchers.IO){
                val audioId = audioDao.insert(audio)
                val playListId = playListDao.insert(playListEntity)
                playListDao.addPlaylistSongCrossRef(PlaylistSongCrossRef(playListId = playListId, songId = audioId))
                val result = playListDao.getListOfPlayListForAudio(audioId)
                assert(result?.playLists?.isNotEmpty() == true)
            }
        }

    @Test
    fun `when dd playlist and not attache it to playlist expect getListOfPlayListForAudio to have an empty playlist`()=
        runTest {
            val playListEntity = PlayListEntity(name = "playList1")
            val audio = AudioEntity(
                album = "Album1",
                songName = "name1",
                uri = "uri1",
                artist = "artist1",
                duration = 100,
                size = 1000
            )
            withContext(Dispatchers.IO){
                val audioId = audioDao.insert(audio)
                playListDao.insert(playListEntity)
                val result = playListDao.getListOfPlayListForAudio(audioId)
                assert(result?.playLists?.isEmpty() == true)
            }
        }
}