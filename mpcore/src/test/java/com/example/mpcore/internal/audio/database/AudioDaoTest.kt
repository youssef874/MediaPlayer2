package com.example.mpcore.internal.audio.database

import androidx.room.Room
import com.example.mpcore.audio.internal.data.database.AudioDao
import com.example.mpcore.audio.internal.data.database.IPlayListDao
import com.example.mpcore.audio.internal.data.database.MPDatabase
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.audio.internal.data.database.model.PlayListEntity
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
class AudioDaoTest {

    private lateinit var mpDatabase: MPDatabase
    private lateinit var audioDao: AudioDao
    private lateinit var playListDao: IPlayListDao

    @Before
    fun setup(){
        mpDatabase = Room.inMemoryDatabaseBuilder(RuntimeEnvironment.getApplication().applicationContext,MPDatabase::class.java)
            .build()
        audioDao = mpDatabase.getAudioDao()
        playListDao = mpDatabase.getPlayListDao()
    }

    @Test
    fun `when inserting AudioEntity expect getAllAudio to provide list contain the inserted data`() = runTest {
        val audio = AudioEntity(
            album = "Album1",
            songName = "name1",
            uri = "uri1",
            artist = "artist1",
            duration = 100,
            size = 1000
        )
        audioDao.insert(
            audio
        )
        with(audioDao.getAll()){
            assert(isNotEmpty())
            assert(any { it.songName==audio.songName })
            assert(any { it.id == 1L })
        }
    }

    @Test
    fun `when updating AudioEntity expect getAudioById`() = runTest {
        val id = audioDao.insert(AudioEntity(
            album = "Album1",
            songName = "name1",
            uri = "uri1",
            artist = "artist1",
            duration = 100,
            size = 1000
        ))
        val updatedAudio = audioDao.getById(id)?.copy(album = "album2")
        if (updatedAudio != null) {
            audioDao.update(updatedAudio)
        }
        with(audioDao.getById(id)){
            assert(this != null)
            assert(this?.album == "album2")
            assert(this?.songName == "name1")
        }
    }

    @Test
    fun `when deleting AudioEntity expect observeAll to return list does not contain the deleted item`()= runTest(timeout = 1000.seconds) {
        val audio1 = AudioEntity(
            album = "Album1",
            songName = "name1",
            uri = "uri1",
            artist = "artist1",
            duration = 100,
            size = 1000,
            externalId = 1L
        )
        val audio2 = AudioEntity(
            album = "Album2",
            songName = "name2",
            uri = "uri2",
            artist = "artist2",
            duration = 200,
            size = 2000,
            externalId = 2L
        )
        val id1 = audioDao.insert(audio1)
        val  id2 = audioDao.insert(audio2)
        val audioToBeDeleted = audioDao.getById(id2)
        if (audioToBeDeleted != null) {
            audioDao.delete(audioToBeDeleted)
        }
        with(audioDao.getAll()){
            assert(size == 1)
            assert(none { it.id == id2 })
            assert(any { it.id == id1 })
        }
    }

    @Test
    fun `when call changeAudioFavoriteStatus and the update was success expect return number of updated raw`()= runTest(timeout = 300.seconds) {
        val audio1 = AudioEntity(
            album = "Album1",
            songName = "name1",
            uri = "uri1",
            artist = "artist1",
            duration = 100,
            size = 1000,
            externalId = 1L
        )
        val id1 = audioDao.insert(audio1)
        val result = audioDao.changeAudioFavoriteStatus(true,id1)
        assert(result == 1)
        val result1 = audioDao.changeAudioFavoriteStatus(true,-1)
        assert(result1 == 0)
    }

    @Test
    fun `when call getListOfAudioListOfPlayList when there no playlist yet expect the result to be empty list`()= runTest {
        val audio1 = AudioEntity(
            album = "Album1",
            songName = "name1",
            uri = "uri1",
            artist = "artist1",
            duration = 100,
            size = 1000,
            externalId = 1L
        )
        audioDao.insert(audio1)
        val result = audioDao.getListOfAudioListOfPlayList()
        assert(result.isEmpty())
    }

    @Test
    fun `when getListOfAudioForPlayList and there are some playlist but they no audio in them yet expect to return PlaylistWithSongs with empty song list `()=
        runTest {
            val audio1 = AudioEntity(
                album = "Album1",
                songName = "name1",
                uri = "uri1",
                artist = "artist1",
                duration = 100,
                size = 1000,
                externalId = 1L
            )
            audioDao.insert(audio1)
            val playListEntity = PlayListEntity(name = "playlist 1")
            val playListId = playListDao.insert(playListEntity)
            val result = audioDao.getListOfAudioForPlayList(playListId)
            assert(result?.songs?.isEmpty() == true)
        }

    @Test
    fun `when getListOfAudioListOfPlayList called and there are some playlist ant contain son in them expect to return PlaylistWithSongs with not empty song list `()= runTest {
        val audio1 = AudioEntity(
            album = "Album1",
            songName = "name1",
            uri = "uri1",
            artist = "artist1",
            duration = 100,
            size = 1000,
            externalId = 1L
        )
        val id = audioDao.insert(audio1)
        val playListEntity = PlayListEntity(name = "playlist 1")
        val playListId = playListDao.insert(playListEntity)
        playListDao.addPlaylistSongCrossRef(PlaylistSongCrossRef(playListId = playListId, songId = id))
        val result = audioDao.getListOfAudioForPlayList(playListId)
        assert(result?.songs?.isNotEmpty() == true)
    }
}