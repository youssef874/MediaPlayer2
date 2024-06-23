package com.example.mpcore.audio.internal.data.dataprovider

import android.content.ContentResolver
import android.content.ContentUris
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.mpcore.audio.internal.data.dataprovider.model.AudioDataProviderModel
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class InternalStorageExtractorImp(
    private val contentResolver: ContentResolver,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IInternalStorageExtractor {

    companion object {
        private const val CLASS_NAME = "InternalStorageExtractorImp"
        private const val TAG = "CONTENT_PROVIDER"
    }

    override suspend fun getAllAudioInStorage(): List<AudioDataProviderModel> {
        MPLoggerConfiguration.DefaultBuilder().log(
            CLASS_NAME,
            TAG,
            "getAllAudioInStorage",
            "start loading all audio",
            MPLoggerLevel.INFO
        )
        return withContext(dispatcher){
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val projections = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ALBUM_ID
            )


            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

            val query = contentResolver.query(
                collection,
                projections,
                selection,
                null,
                null
            )
            val allAudio = mutableListOf<AudioDataProviderModel>()
            query?.let { cursor ->
                val idIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val albumIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val artistIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val displayNameIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val albumIdIndex = query.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val album = cursor.getString(albumIndex)
                    val artist = cursor.getString(artistIndex)
                    val duration = cursor.getInt(durationIndex)
                    val size = cursor.getInt(sizeIndex)
                    val displayName = cursor.getString(displayNameIndex)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val albumId = cursor.getLong(albumIdIndex)
                    val albumCover = getAlbumThumbnailUri(albumId)
                    val audio = AudioDataProviderModel(
                        audioId = id, artistName = artist, album = album, audioDuration = duration,
                        audioUri = contentUri, audioThumbnailUri = albumCover, audioName = displayName, audioSize = size
                    )
                    allAudio += audio
                }
                query.close()
                MPLoggerConfiguration.DefaultBuilder().log(
                    CLASS_NAME,
                    TAG,
                    "getAllAudioInStorage",
                    "allAudio: ${allAudio.map { it.audioName }}",
                    MPLoggerLevel.INFO
                )
            }
            allAudio
        }
    }

    override fun setOnDataChangesListener(onDataChanges: () -> Unit) {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        contentResolver.registerContentObserver(collection,true, object : ContentObserver(null){
            override fun onChange(selfChange: Boolean) {
                MPLoggerConfiguration.DefaultBuilder().log(
                    className = CLASS_NAME,
                    tag = TAG,
                    methodName = "setOnDataChangesListener",
                    msg = "changes in content uri selfChange: $selfChange",
                    MPLoggerLevel.INFO
                )
                onDataChanges()
            }
        })
    }

    private fun getAlbumThumbnailUri(albumId: Long): Uri? {
        return Uri.parse(getAlbumThumbnailPath(albumId))
    }

   private fun getAlbumThumbnailPath(albumId: Long): String{
        return "content://media/external/audio/albumart/$albumId"
    }
}