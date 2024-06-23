package com.example.mpcore.internal.audio

import com.example.mpcore.audio.api.data.MPApiResult
import com.example.mpcore.audio.internal.data.DataCode
import com.example.mpcore.audio.internal.data.InternalPlayListDataManagerImpl
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.audio.internal.data.database.model.PlayListEntity
import com.example.mpcore.audio.internal.data.toMPPlayList
import com.example.mpcore.internal.audio.database.dao.FakeAudioDao
import com.example.mpcore.internal.audio.database.dao.FakePlayListDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InternalPlayListDataManagerImplTest {

    private val audioDao = FakeAudioDao
    private val playListDao = FakePlayListDao.getInstance(audioDao)
    private val playListDatabaseDataProviderImpl = FakePlayListDatabaseDataProvider(
        playListDao = playListDao,
        audioDao = audioDao
    )

    private lateinit var internalPlayListDataManagerImpl: InternalPlayListDataManagerImpl

    @Before
    fun setup(){
        internalPlayListDataManagerImpl = InternalPlayListDataManagerImpl(playListDatabaseDataProviderImpl)
    }

    @Test
    fun `when no data in the database expect getAllPlayList to return an empty list`()= runTest {
        val result = internalPlayListDataManagerImpl.getAllPlayList()
        assert(result is MPApiResult.Success)
        assert((result as MPApiResult.Success).data.isEmpty())
    }

    @Test
    fun `when there same data in the database expect getAllPlayList to return a non empty list`()= runTest {
        val playlist = PlayListEntity(name = "play list 1")
        playListDatabaseDataProviderImpl.add(playlist)
        val result = internalPlayListDataManagerImpl.getAllPlayList()
        assert(result is MPApiResult.Success)
        assert((result as MPApiResult.Success).data.isNotEmpty())
    }

    @Test
    fun `when no audio attached to playlist expect getFirstAudioOfPlayList to return null`()= runTest {
        val playlist = PlayListEntity(name = "play list 1")
        val id = playListDatabaseDataProviderImpl.add(playlist)
        val result = internalPlayListDataManagerImpl.getFirstAudioOfPlayList(id)
        assert((result as MPApiResult.Success).data == null)
    }


    @Test
    fun `when attach a non attached and existed audio to existing playlist expect getFirstAudioOfPlayList to return a non null value`()= runTest {
        val playlist = PlayListEntity(name = "play list 1")
        val id = playListDatabaseDataProviderImpl.add(playlist)
        val audioEntity = AudioEntity(
            album = "album1",
            uri = "uri1",
            songName = "name1",
            artist = "artist1",
            duration = 1000,
            size = 10000
        )
        val audioId = playListDatabaseDataProviderImpl.addAudio(audioEntity)
        internalPlayListDataManagerImpl.attachPlayListToAudio(audioId,playlist.copy(id = id).toMPPlayList())
        val result = internalPlayListDataManagerImpl.getFirstAudioOfPlayList(id)
        assert((result as MPApiResult.Success).data != null)
        assert(result.data?.id == audioId)
    }

    @Test
    fun `when attach a non attached and existed audio to non existing playlist expect getFirstAudioOfPlayList to return a non null value`()=
        runTest {
            val playlist = PlayListEntity(name = "play list 1")
            val audioEntity = AudioEntity(
                album = "album1",
                uri = "uri1",
                songName = "name1",
                artist = "artist1",
                duration = 1000,
                size = 10000
            )
            val audioId = playListDatabaseDataProviderImpl.addAudio(audioEntity)
            internalPlayListDataManagerImpl.attachPlayListToAudio(audioId,playlist.toMPPlayList())
            val id = (internalPlayListDataManagerImpl.getAllPlayList() as MPApiResult.Success).data.first().id
            val result = internalPlayListDataManagerImpl.getFirstAudioOfPlayList(id)
            assert((result as MPApiResult.Success).data != null)
            assert(result.data?.id == audioId)
        }

    @Test
    fun `when attach a non attached and non existed audio to existing playlist expect to have error`()= runTest {
        val playlist = PlayListEntity(name = "play list 1")
        val id = playListDatabaseDataProviderImpl.add(playlist)
        val result = internalPlayListDataManagerImpl.attachPlayListToAudio(1L,playlist.copy(id = id).toMPPlayList())
        assert(result is MPApiResult.Error)
        assert((result as MPApiResult.Error).error.errorCode == DataCode.ELEMENT_NOT_FOUND)

    }

    @Test
    fun `when  attach an existing audio to existing playlist tow time expect to have error`()= runTest {
        val playlist = PlayListEntity(name = "play list 1")
        val id = playListDatabaseDataProviderImpl.add(playlist)
        val audioEntity = AudioEntity(
            album = "album1",
            uri = "uri1",
            songName = "name1",
            artist = "artist1",
            duration = 1000,
            size = 10000
        )
        val audioId = playListDatabaseDataProviderImpl.addAudio(audioEntity)
        internalPlayListDataManagerImpl.attachPlayListToAudio(audioId,playlist.copy(id = id).toMPPlayList())
        val result = internalPlayListDataManagerImpl.attachPlayListToAudio(audioId,playlist.copy(id = id).toMPPlayList())
        assert(result is MPApiResult.Error)
        assert((result as MPApiResult.Error).error.errorCode == DataCode.AUDIO_ALREADY_ATTACHED_TO_PLAYLIST)
    }

    @After
    fun cleanup(){
        CoroutineScope(Dispatchers.IO).launch {
            playListDatabaseDataProviderImpl.deleteAll()
            playListDatabaseDataProviderImpl.deleteAllAudio()
        }
    }
}