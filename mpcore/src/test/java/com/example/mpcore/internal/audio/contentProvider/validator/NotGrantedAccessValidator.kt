package com.example.mpcore.audio.internal.validator

import com.example.mpcore.audio.internal.data.dataprovider.IAudioInternalStorageValidator

object NotGrantedAccessValidator: IAudioInternalStorageValidator {
    override fun permissionRequired(): List<String> {
        return listOf(
            "permission1",
            "permission2"
        )
    }
}