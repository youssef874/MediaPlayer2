package com.example.mpcore.audio.internal.data.dataprovider

/**
 * This abstract of validating access to the internal storage
 */
internal interface IAudioInternalStorageValidator {

    /**
     * This will check for any permission needed to access
     * the device internal storage
     * @return list of the permission needed in if it return an empty list
     * means all permission granted
     */
    fun permissionRequired(): List<String>
}