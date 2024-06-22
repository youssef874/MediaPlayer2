package com.example.mpcore.internal.audio.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.mpcore.audio.internal.data.datastore.AudioDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
class AudioDataStoreTest {



    private lateinit var audioDataStore: AudioDataStore
    private lateinit var dataStore: DataStore<Preferences>
    private val intValue = 1
    private val intValue2 = 2
    private val longValue = 3L
    private val floatValue = 4F
    private val booleanValue = true

    companion object{
        private const val INT_KEY = "int"
        private const val INT_KEY_2 = "int2"
        private const val STRING_KEY = "string"
        private const val LONG_KEY = "long"
        private const val FLOAT_KEY = "float"
        private const val BOOL_KEY = "bool"
    }

    @Before
    fun setup(){
        dataStore = PreferenceDataStoreFactory.create {
            RuntimeEnvironment.getApplication().applicationContext.preferencesDataStoreFile("TEST_FILE")
        }
        audioDataStore = AudioDataStore(dataStore)
    }

    @Test
    fun `when calling putInt with some key expect getInt with the exact key to return the added value`()= runTest {
        val intModifier = audioDataStore.getIntDataStoreModifier()
        intModifier.put(INT_KEY,intValue)
        val result = intModifier.observe(INT_KEY,0).first()
        assert(result == intValue)
    }

    @Test
    fun `when calling remove with some key expect getInt with the exact key to return the default value`()= runTest(timeout = 1000.seconds) {

        val intModifier = audioDataStore.getIntDataStoreModifier()
        intModifier.put(INT_KEY_2,intValue2)
        intModifier.remove(INT_KEY_2)
        val result = intModifier.observe(INT_KEY_2,0).first()
        assert(result == 0)
        assert(!intModifier.contains(INT_KEY_2))
    }

    @Test
    fun `when extracting some data with some key that don't exist in datastore  expect getting the default value`()= runTest(timeout = 100.seconds) {
        val stringModifier = audioDataStore.getStringDataStoreModifier()
        val result = stringModifier.observe(STRING_KEY,"").first()
        assert(result == "")
        assert(!stringModifier.contains(STRING_KEY))
    }

    @Test
    fun `when clearing all data datastore  expect getAll return empty map`()= runTest {
        val longModifier = audioDataStore.getLongDataStoreModifier()
        longModifier.put(LONG_KEY,longValue)
        longModifier.clear()
        assert(longModifier.getAll().first().isEmpty())
    }

    @Test
    fun `when having data in datastore  expect getAll return not empty map`()= runTest {
        val floatModifier = audioDataStore.getFloatDataStoreModifier()
        val booleanModifier = audioDataStore.getBooleanDataStoreModifier()
        floatModifier.put(FLOAT_KEY,floatValue)
        booleanModifier.put(BOOL_KEY,booleanValue)
        assert(floatModifier.getAll().first().isNotEmpty())
        assert(floatModifier.contains(FLOAT_KEY))
        assert(booleanModifier.contains(BOOL_KEY))
    }

    @After
    fun clean(){
        runTest {
            audioDataStore.getIntDataStoreModifier().clear()
        }
    }
}