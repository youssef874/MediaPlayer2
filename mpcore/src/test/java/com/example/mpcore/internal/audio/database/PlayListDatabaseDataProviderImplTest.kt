package com.example.mpcore.internal.audio.database

import com.example.mpcore.audio.api.exception.AddAudioToPlayListThatAlreadyExist
import com.example.mpcore.audio.internal.data.database.AudioDatabaseProviderImpl
import com.example.mpcore.audio.internal.data.database.PlayListDatabaseDataProviderImpl
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.internal.audio.database.dao.FakeAudioDao
import com.example.mpcore.internal.audio.database.dao.FakePlayListDao
import com.example.mpcore.audio.internal.data.database.model.PlayListEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlayListDatabaseDataProviderImplTest {

    private lateinit var audioDatabaseProviderImpl: AudioDatabaseProviderImpl
    private lateinit var playListDatabaseDataProviderImpl: PlayListDatabaseDataProviderImpl

    @Before
    fun setup(){
        val fakeAudio = FakeAudioDao.apply {
            notify = null
            noTifyPlaylistWithSongsList = null
        }
        val fakePlaylist = FakePlayListDao(fakeAudio).apply {
            notify = null
            notifyCrossRefChanges = null
        }
        audioDatabaseProviderImpl = AudioDatabaseProviderImpl(fakeAudio)
        playListDatabaseDataProviderImpl = PlayListDatabaseDataProviderImpl(audioDao = fakeAudio, playListDao = fakePlaylist)
    }

    @Test
    fun `when calling getAll with no data added to the database expect to return empty list`()= runTest {
        withContext(Dispatchers.IO){
            val result = playListDatabaseDataProviderImpl.getAll()
            assert(result.isEmpty())
        }
    }

    @Test
    fun `when calling getAll after adding some data expect to return a no empty list`()= runTest {
        withContext(Dispatchers.IO){
            val playListEntity = PlayListEntity(name = "playlist 1")
            playListDatabaseDataProviderImpl.add(playListEntity)
            val result = playListDatabaseDataProviderImpl.getAll()
            assert(result.isNotEmpty())
        }
    }

    @Test
    fun `when updating a non existing item expect the update to return false`()= runTest {
        withContext(Dispatchers.IO){
            val playListEntity = PlayListEntity(name = "playlist 1", id = 1L)
            val result = playListDatabaseDataProviderImpl.update(playListEntity)
            assert(!result)
        }
    }

    @Test
    fun `when updating an existing item expect to return true and when extracting the item suppose to be updated`()= runTest {
        val playListEntity = PlayListEntity(name = "playlist 1")
        val id = playListDatabaseDataProviderImpl.add(playListEntity)
        val updated = playListEntity.copy(id = id, name = "test")
        val result = playListDatabaseDataProviderImpl.update(updated)
        val newData = playListDatabaseDataProviderImpl.getById(id)
        assert(result)
        assert(newData?.name == "test")
    }

    @Test
    fun `when deleting a playlist expect getById to return null`()= runTest {
        val playListEntity = PlayListEntity(name = "playlist 1")
        val id1 = playListDatabaseDataProviderImpl.add(playListEntity)
        playListDatabaseDataProviderImpl.delete(playListEntity.copy(id = id1))
        val result1 = playListDatabaseDataProviderImpl.getById(id1)
        assert(result1 == null)
    }

    @Test
    fun `when attach only one audio to an existing playList expect getFirstAudioOfPlayList to return it`()= runTest {
        val playListEntity = PlayListEntity(name = "playlist 1")
        val id1 = playListDatabaseDataProviderImpl.add(playListEntity)
        val audioEntity = AudioEntity(
            album = "album1",
            uri = "uri1",
            songName = "name1",
            artist = "artist1",
            duration = 1000,
            size = 10000
        )
        val id2 = audioDatabaseProviderImpl.add(audioEntity)
        playListDatabaseDataProviderImpl.attachPlayListToAudio(id2,playListEntity.copy(id = id1))
        val result = playListDatabaseDataProviderImpl.getFirstAudioOfPlayList(id1)
        assert(result?.id == id2)
    }

    @Test
    fun `when attach only one audio to non existing playList expect getFirstAudioOfPlayList to return it`()= runTest {
        val audioEntity = AudioEntity(
            album = "albumX",
            uri = "uriX",
            songName = "nameX",
            artist = "artistX",
            duration = 1000,
            size = 10000
        )
        val id2 = audioDatabaseProviderImpl.add(audioEntity)
        val playListEntity = PlayListEntity(name = "playlist x")
        playListDatabaseDataProviderImpl.attachPlayListToAudio(id2,playListEntity)
        val list = playListDatabaseDataProviderImpl.getAll()
        val result = playListDatabaseDataProviderImpl.getAudioListOfPlaylist(list.last().id)
        assert(result.any { it.id == id2 })
    }

    @Test
    fun `when multiple audio to playlist expect getAudioListOfPlaylist to return the added ones`()= runTest {
        val playListEntity = PlayListEntity(name = "playlist 1")
        val audioEntity = AudioEntity(
            album = "album1",
            uri = "uri1",
            songName = "name1",
            artist = "artist1",
            duration = 1000,
            size = 10000
        )
        val audioEntity2 = AudioEntity(
            album = "album2",
            uri = "uri2",
            songName = "name2",
            artist = "artist2",
            duration = 1000,
            size = 10000
        )
        val id1 = audioDatabaseProviderImpl.add(audioEntity)
        val id2 = audioDatabaseProviderImpl.add(audioEntity2)
        val id = playListDatabaseDataProviderImpl.add(playListEntity)
        playListDatabaseDataProviderImpl.attachPlayListToAudio(audioId = id1, playListEntity = playListEntity)
        playListDatabaseDataProviderImpl.attachPlayListToAudio(audioId = id2, playListEntity = playListEntity)
        val result = playListDatabaseDataProviderImpl.getAudioListOfPlaylist(id)
        assert(result.isNotEmpty())
        assert(result.any { it.id == id2 })
        assert(result.any { it.id == id1 })
    }

    @Test(expected = AddAudioToPlayListThatAlreadyExist::class)
    fun `when trying to attach same audio tow times expect exception to be thrown `()= runTest {
        val playListEntity = PlayListEntity(name = "playlist 1")
        val audioEntity = AudioEntity(
            album = "album1",
            uri = "uri1",
            songName = "name1",
            artist = "artist1",
            duration = 1000,
            size = 10000
        )
        val id1 = audioDatabaseProviderImpl.add(audioEntity)
        playListDatabaseDataProviderImpl.attachPlayListToAudio(audioId = id1, playListEntity = playListEntity)
        playListDatabaseDataProviderImpl.attachPlayListToAudio(audioId = id1, playListEntity = playListEntity)
    }
}