package com.example.mpcore.internal.audio.contentProvider

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import com.example.mpcore.audio.api.exception.MissingPermissionException
import com.example.mpcore.audio.internal.data.dataprovider.AudioContentProviderDataManagerImpl
import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R])
class AudioDataManagerTestR {

    @Mock
    private lateinit var context: Context

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
        Mockito.`when`(context.contentResolver).thenReturn(contentResolver)
    }

    @Test
    fun `when permission granted and data empty expect getAllAudio return empty list`() = runTest {
        Mockito.`when`(
            context.checkPermission(
                ArgumentMatchers.eq(Manifest.permission.READ_EXTERNAL_STORAGE),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(
            PackageManager.PERMISSION_GRANTED
        )
        val mockCursor = Mockito.mock(Cursor::class.java)
        Mockito.`when`(mockCursor.moveToNext()).thenReturn(false)
        Mockito.`when`(
            contentResolver.query(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
            )
        )
            .thenReturn(mockCursor)
        val audioContentProviderDataManagerImpl = AudioContentProviderDataManagerImpl(context)
        assert(audioContentProviderDataManagerImpl.getAllAudio().isEmpty())
    }

    @Test
    fun `when permission granted and data is not empty expect getAllAudio return no empty list`()= runTest {
        Mockito.`when`(
            context.checkPermission(
                ArgumentMatchers.eq(Manifest.permission.READ_EXTERNAL_STORAGE),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(
            PackageManager.PERMISSION_GRANTED
        )
        val mockCursor = Mockito.mock(Cursor::class.java)
        Mockito.`when`(mockCursor.moveToNext()).thenReturn(true, true, false)
        Mockito.`when`(mockCursor.getLong(Mockito.anyInt())).thenReturn(
            audioList.first().audioId,
            audioList.last().audioId
        )
        Mockito.`when`(mockCursor.getString(Mockito.anyInt())).thenReturn(
            audioList.first().artistName,
            audioList.last().audioName,
            audioList.first().album,
            audioList.last().album,
            audioList.first().audioName,
            audioList.last().audioName
        )
        Mockito.`when`(mockCursor.getInt(Mockito.anyInt())).thenReturn(
            audioList.first().audioDuration,
            audioList.last().audioDuration,
            audioList.first().audioSize,
            audioList.last().audioSize
        )
        Mockito.`when`(contentResolver.query(
            Mockito.any(), Mockito.any(),
            ArgumentMatchers.any(),
            ArgumentMatchers.any(),
            ArgumentMatchers.any()
        ))
            .thenReturn(mockCursor)
        val audioContentProviderDataManagerImpl = AudioContentProviderDataManagerImpl(context)
        with(audioContentProviderDataManagerImpl.getAllAudio()){
            assert(isNotEmpty())
            assert(size == audioList.size)
            assert(any { audioList.first().audioId == it.audioId })
            assert(any { audioList.last().audioId == it.audioId })
        }
    }

    @Test(expected = MissingPermissionException::class)
    fun `when permission not granted and data empty expect getAllAudio to throw exception`() = runTest {
        Mockito.`when`(
            context.checkPermission(
                ArgumentMatchers.eq(Manifest.permission.READ_EXTERNAL_STORAGE),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(
            PackageManager.PERMISSION_DENIED
        )
        val mockCursor = Mockito.mock(Cursor::class.java)
        Mockito.`when`(mockCursor.moveToNext()).thenReturn(false)
        Mockito.`when`(
            contentResolver.query(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
            )
        )
            .thenReturn(mockCursor)
        AudioContentProviderDataManagerImpl(context).getAllAudio()
    }

    @Test(expected = MissingPermissionException::class)
    fun `when permission not granted and data is not empty expect getAllAudio to throw exception`() = runTest {
        Mockito.`when`(
            context.checkPermission(
                ArgumentMatchers.eq(Manifest.permission.READ_EXTERNAL_STORAGE),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(
            PackageManager.PERMISSION_DENIED
        )
        val mockCursor = Mockito.mock(Cursor::class.java)
        Mockito.`when`(mockCursor.moveToNext()).thenReturn(true, true, false)
        Mockito.`when`(mockCursor.getLong(Mockito.anyInt())).thenReturn(
            audioList.first().audioId,
            audioList.last().audioId
        )
        Mockito.`when`(mockCursor.getString(Mockito.anyInt())).thenReturn(
            audioList.first().artistName,
            audioList.last().audioName,
            audioList.first().album,
            audioList.last().album,
            audioList.first().audioName,
            audioList.last().audioName
        )
        Mockito.`when`(mockCursor.getInt(Mockito.anyInt())).thenReturn(
            audioList.first().audioDuration,
            audioList.last().audioDuration,
            audioList.first().audioSize,
            audioList.last().audioSize
        )
        Mockito.`when`(contentResolver.query(
            Mockito.any(), Mockito.any(),
            ArgumentMatchers.any(),
            ArgumentMatchers.any(),
            ArgumentMatchers.any()
        ))
            .thenReturn(mockCursor)
        AudioContentProviderDataManagerImpl(context).getAllAudio()
    }

    @Test(expected = MissingPermissionException::class)
    fun `when permission not granted and data is not empty expect setOnDataChangesListener to throw exception`() = runTest {
        Mockito.`when`(
            context.checkPermission(
                ArgumentMatchers.eq(Manifest.permission.READ_EXTERNAL_STORAGE),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenReturn(
            PackageManager.PERMISSION_DENIED
        )
        val mockCursor = Mockito.mock(Cursor::class.java)
        Mockito.`when`(mockCursor.moveToNext()).thenReturn(true, true, false)
        Mockito.`when`(mockCursor.getLong(Mockito.anyInt())).thenReturn(
            audioList.first().audioId,
            audioList.last().audioId
        )
        Mockito.`when`(mockCursor.getString(Mockito.anyInt())).thenReturn(
            audioList.first().artistName,
            audioList.last().audioName,
            audioList.first().album,
            audioList.last().album,
            audioList.first().audioName,
            audioList.last().audioName
        )
        Mockito.`when`(mockCursor.getInt(Mockito.anyInt())).thenReturn(
            audioList.first().audioDuration,
            audioList.last().audioDuration,
            audioList.first().audioSize,
            audioList.last().audioSize
        )
        Mockito.`when`(contentResolver.query(
            Mockito.any(), Mockito.any(),
            ArgumentMatchers.any(),
            ArgumentMatchers.any(),
            ArgumentMatchers.any()
        ))
            .thenReturn(mockCursor)
        AudioContentProviderDataManagerImpl(context).setOnDataChangesListener {  }
    }
}