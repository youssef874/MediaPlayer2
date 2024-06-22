package com.example.mpcore.audio.internal.validator

import com.example.mpcore.audio.internal.data.dataprovider.IAudioInternalStorageValidator

object GrantedAccessValidator: IAudioInternalStorageValidator {
    override fun permissionRequired(): List<String> {
        return emptyList()
    }
}