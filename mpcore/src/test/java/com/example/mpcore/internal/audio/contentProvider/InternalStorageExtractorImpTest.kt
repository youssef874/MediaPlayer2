package com.example.mpcore.internal.audio.contentProvider

import android.content.ContentResolver
import android.database.Cursor
import com.example.mpcore.audio.internal.data.dataprovider.InternalStorageExtractorImp
import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InternalStorageExtractorImpTest {

    @Mock
    private lateinit var contentResolver: ContentResolver

    private val audioList = listOf(
        AudioDataProviderModel(
            audioId = 1L,
            artistName = "artist1",
            album = "album1",
            audioDuration = 1000,
            audioName = "name1",
            audioSize = 50
        ),
        AudioDataProviderModel(
            audioId = 2L,
            artistName = "artist2",
            album = "album2",
            audioDuration = 1500,
            audioName = "name2",
            audioSize = 80
        )
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun when_ContentProviderIsEmpty_Expect_getAllAudioInStorage_ToReturnEmptyList() = runTest {
        val extractor = InternalStorageExtractorImp(contentResolver)
        val mockCursor = mock(Cursor::class.java)
        `when`(mockCursor.moveToNext()).thenReturn(false)
        `when`(
            contentResolver.query(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
            )
        )
            .thenReturn(mockCursor)
        val allAudio = extractor.getAllAudioInStorage()
        assert(allAudio.isEmpty())
    }

    @Test
    fun when_ContentProviderIsNotEmpty_Expect_getAllAudioInStorage_ToReturnNoEmptyList() = runTest {
        val extractor = InternalStorageExtractorImp(contentResolver)

        // Mock cursor with multiple entries
        val mockCursor = mock(Cursor::class.java)
        `when`(mockCursor.moveToNext()).thenReturn(true, true, false)
        `when`(mockCursor.getLong(Mockito.anyInt())).thenReturn(
            audioList.first().audioId,
            audioList.last().audioId
        )
        `when`(mockCursor.getString(Mockito.anyInt())).thenReturn(
            audioList.first().artistName,
            audioList.last().audioName,
            audioList.first().album,
            audioList.last().album,
            audioList.first().audioName,
            audioList.last().audioName
        )
        `when`(mockCursor.getInt(Mockito.anyInt())).thenReturn(
            audioList.first().audioDuration,
            audioList.last().audioDuration,
            audioList.first().audioSize,
            audioList.last().audioSize
        )
        `when`(contentResolver.query(Mockito.any(), Mockito.any(), any(), any(), any()))
            .thenReturn(mockCursor)
        with(extractor.getAllAudioInStorage()){
            assert(isNotEmpty())
            assert(any { audioList.first().audioId == it.audioId })
            assert(any { audioList.last().audioId == it.audioId })
            assert(size == audioList.size)
        }

    }
}