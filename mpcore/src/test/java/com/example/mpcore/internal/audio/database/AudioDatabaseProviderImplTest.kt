package com.example.mpcore.internal.audio.database

import com.example.mpcore.audio.internal.data.database.AudioDatabaseProviderImpl
import com.example.mpcore.audio.internal.data.database.PlayListDatabaseDataProviderImpl
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.internal.audio.database.dao.FakeAudioDao
import com.example.mpcore.internal.audio.database.dao.FakePlayListDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AudioDatabaseProviderImplTest {

    private lateinit var audioDatabaseProviderImpl: AudioDatabaseProviderImpl
    private lateinit var playListDatabaseDataProviderImpl: PlayListDatabaseDataProviderImpl

    @Before
    fun setup(){
        val fakeAudio = FakeAudioDao.apply {
            notify = null
            noTifyPlaylistWithSongsList = null
        }
        val fakePlaylist = FakePlayListDao.getInstance(fakeAudio).apply {
            notify = null
            notifyCrossRefChanges = null
        }
        audioDatabaseProviderImpl = AudioDatabaseProviderImpl(fakeAudio)
        playListDatabaseDataProviderImpl = PlayListDatabaseDataProviderImpl(audioDao = fakeAudio, playListDao = fakePlaylist)
    }


    @Test
    fun `when calling add expect it return auto generated id which is the previous id plus 1`()= runTest {
        val id1 = audioDatabaseProviderImpl.add(
            AudioEntity(
                album = "album1",
                uri = "uri1",
                songName = "name1",
                artist = "artist1",
                duration = 1000,
                size = 10000
            )
        )

        val id2 = audioDatabaseProviderImpl.add(
            AudioEntity(
                album = "album2",
                uri = "uri2",
                songName = "name2",
                artist = "artist2",
                duration = 1000,
                size = 10000
            )
        )
        assert(audioDatabaseProviderImpl.getById(id1) != null)
        assert(id2 == id1+1)
    }

    @Test
    fun `when calling add expect the get by id with the returned id from add method to give the added data`()= runTest {
        val id1 = audioDatabaseProviderImpl.add(
            AudioEntity(
                album = "album1",
                uri = "uri1",
                songName = "name1",
                artist = "artist1",
                duration = 1000,
                size = 10000
            )
        )

        with(audioDatabaseProviderImpl.getById(id1)){
            assert(this != null)
            assert(this?.id == id1)
            assert(this?.songName == "name1")
        }
    }

    @Test
    fun `when calling update expect the observeById with the returned id from add method to give the added data`()= runTest {
        val id1 = audioDatabaseProviderImpl.add(
            AudioEntity(
                album = "album1",
                uri = "uri1",
                songName = "name1",
                artist = "artist1",
                duration = 1000,
                size = 10000
            )
        )
        val id2 = audioDatabaseProviderImpl.add(
            AudioEntity(
                album = "album2",
                uri = "uri2",
                songName = "name2",
                artist = "artist2",
                duration = 1000,
                size = 10000
            )
        )
        val audio1 = audioDatabaseProviderImpl.getById(id2)
        if (audio1 != null) {
            audioDatabaseProviderImpl.delete(audio1)
        }
        with(audioDatabaseProviderImpl.getAll()){
            assert(isNotEmpty())
            assert(none { it.id == id2 })
            assert(any { it.id == id1 })
        }
    }

    @Test
    fun `when calling update expect the getAll to return list does not contain the updated item`()= runTest {
        val id1 = audioDatabaseProviderImpl.add(
            AudioEntity(
                album = "album1",
                uri = "uri1",
                songName = "name1",
                artist = "artist1",
                duration = 1000,
                size = 10000
            )
        )
        val audioTobeUpdated = audioDatabaseProviderImpl.getById(id1)?.copy(artist = "artist2")
        if (audioTobeUpdated != null) {
            audioDatabaseProviderImpl.update(audioTobeUpdated)
        }

        with(audioDatabaseProviderImpl.observeById(id1)){
            assert(this.single() != null)
            assert(this.single()?.id == id1)
            assert(this.single()?.artist == "artist2")
        }
    }

    @Test
    fun `when changing favorite status expect to return true or false depending of the existing of the audio`()= runTest {
        withContext(Dispatchers.IO){
            val id1 = audioDatabaseProviderImpl.add(
                AudioEntity(
                    album = "album1",
                    uri = "uri1",
                    songName = "name1",
                    artist = "artist1",
                    duration = 1000,
                    size = 10000
                )
            )
            val result = audioDatabaseProviderImpl.changeAudioFavoriteStatus(true,id1)
            assert(result)
            val result1 = audioDatabaseProviderImpl.changeAudioFavoriteStatus(true,-1)
            assert(!result1)
        }
    }

}