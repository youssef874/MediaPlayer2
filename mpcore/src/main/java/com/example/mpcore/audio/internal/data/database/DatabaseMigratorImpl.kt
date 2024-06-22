package com.example.mpcore.audio.internal.data.database

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mpcore.audio.internal.data.database.model.AudioEntity
import com.example.mpcore.audio.internal.data.datastore.IAudioDatastoreManger
import com.example.mpcore.common.internal.SDKComponent
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import com.example.mpstorage.database.internal.entity.PlayListEntity
import com.example.mpstorage.database.internal.entity.PlaylistSongCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class DatabaseMigratorImpl(
    private val builder: RoomDatabase.Builder<MPDatabase>,
    private val audioDatastoreManger: IAudioDatastoreManger
): IDatabaseMigrator {

    companion object{
        private const val CLASS_NAME = "DatabaseMigratorImpl"
        private const val TAG = "DATA_BASE"
    }

    

    private val migration1To2 = object: Migration(1,2){
        override fun migrate(db: SupportSQLiteDatabase) {
            with(db){
                execSQL("CREATE TABLE IF NOT EXISTS `${PlayListEntity.TABLE_NAME}` (`${PlayListEntity.ID}` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0, `${PlayListEntity.NAME}` TEXT NOT NULL DEFAULT '')")
                execSQL("CREATE TABLE IF NOT EXISTS `${PlaylistSongCrossRef.TABLE_NAME}` (`${PlayListEntity.ID}` INTEGER NOT NULL DEFAULT 0, `${AudioEntity.ID}` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`${PlayListEntity.ID}`, `${AudioEntity.ID}`))")
            }
            SDKComponent.CoroutineComponent.sdkCoroutineScope.launch (Dispatchers.IO){
                val oldVersion = audioDatastoreManger.getDatabaseVersionController().getValue()
                if (oldVersion < 2){
                    audioDatastoreManger.getDatabaseVersionController().updateValue(2)
                }
            }
        }

    }

    override fun addMigration() {
        MPLoggerConfiguration.DefaultBuilder().log(
            className = CLASS_NAME, tag = TAG, methodName = "addMigration",
            msg = "database migration", logLevel = MPLoggerLevel.INFO
        )
        builder.addMigrations(
            migration1To2
        )
    }
}