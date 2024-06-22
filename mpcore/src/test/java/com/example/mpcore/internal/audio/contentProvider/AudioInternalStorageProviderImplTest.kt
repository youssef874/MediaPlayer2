package com.example.mpcore.internal.audio.contentProvider

import com.example.mpcore.audio.api.exception.MissingPermissionException
import com.example.mpcore.audio.internal.data.dataprovider.AudioInternalStorageProviderImpl
import com.example.mpcore.internal.audio.contentProvider.extractor.ExtractorWithEmptyData
import com.example.mpcore.internal.audio.contentProvider.extractor.ExtractorWithNoEmptyData
import com.example.mpcore.audio.internal.validator.GrantedAccessValidator
import com.example.mpcore.audio.internal.validator.NotGrantedAccessValidator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AudioInternalStorageProviderImplTest {

    private var audioInternalStorageProviderImpl: AudioInternalStorageProviderImpl? = null

    @Test
    fun when_allPermissionGrantedAndWithNoEmptyData_expect_NoEmptyDataFrom_getAllAudio(){
        audioInternalStorageProviderImpl = AudioInternalStorageProviderImpl(ExtractorWithNoEmptyData)
        runTest {
            audioInternalStorageProviderImpl?.getAllAudio(GrantedAccessValidator)?.run {
                assert(isNotEmpty())
                assert(size == 2)
                assert(this == ExtractorWithNoEmptyData.allAudio)
            }
        }
    }

    @Test
    fun when_allPermissionGrantedAndWithEmptyData_expect_EmptyDataFrom_getAllAudio(){
        audioInternalStorageProviderImpl = AudioInternalStorageProviderImpl(ExtractorWithEmptyData)
        runTest {
            audioInternalStorageProviderImpl?.getAllAudio(GrantedAccessValidator)?.run {
                assert(isEmpty())
            }
        }
    }

    @Test(expected = MissingPermissionException::class)
    fun when_allPermissionNotGrantedAndWithNOEmptyData_expect_exceptionThrown_getAllAudio()= runTest {
        audioInternalStorageProviderImpl = AudioInternalStorageProviderImpl(ExtractorWithNoEmptyData)
        audioInternalStorageProviderImpl?.getAllAudio(NotGrantedAccessValidator)
    }

    @Test(expected = MissingPermissionException::class)
    fun when_allPermissionNotGrantedAndWithEmptyData_expect_exceptionThrown_getAllAudio()= runTest {
        audioInternalStorageProviderImpl = AudioInternalStorageProviderImpl(ExtractorWithNoEmptyData)
        audioInternalStorageProviderImpl?.getAllAudio(NotGrantedAccessValidator)
    }

    @Test(expected = MissingPermissionException::class)
    fun when_allPermissionNotGrantedAndWithNOEmptyData_expect_exceptionThrown_setOnDataChangesListener()= runTest {
        audioInternalStorageProviderImpl = AudioInternalStorageProviderImpl(ExtractorWithNoEmptyData)
        audioInternalStorageProviderImpl?.setOnDataChangesListener(NotGrantedAccessValidator){}
    }
}