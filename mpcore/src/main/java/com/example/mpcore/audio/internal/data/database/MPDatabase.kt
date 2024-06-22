package com.example.mpcore.audio.internal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.audio.internal.data.datastore.factory.AudioDatastoreDataManagerFactory
import com.example.mpcore.audio.internal.data.database.model.PlayListEntity
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef

@Database(
    entities = [AudioEntity::class, PlayListEntity::class,PlaylistSongCrossRef::class],
    version = MPDatabase.DATA_BASE_VERSION,
    exportSchema = true
)
internal abstract class MPDatabase: RoomDatabase() {

    abstract fun getAudioDao(): AudioDao

    abstract fun getPlayListDao(): IPlayListDao

    companion object{

        private var INSTANCE: MPDatabase? = null

        fun create(context: Context): MPDatabase{
            return INSTANCE?:synchronized(this){
                val builder = Room.databaseBuilder(
                    context,
                    MPDatabase::class.java,
                    "mp_database"
                )
                DatabaseMigratorImpl(builder,AudioDatastoreDataManagerFactory.create(context)).addMigration()
                val instance =  builder.build()
                INSTANCE = instance
                instance
            }
        }

        const val DATA_BASE_VERSION = 2
    }
}