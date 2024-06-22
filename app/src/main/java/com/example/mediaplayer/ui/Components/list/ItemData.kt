package com.example.mediaplayer.ui.Components.list

import android.net.Uri

data class ItemData(
    val id: Long = 0,
    val imageUri: Uri? = null,
    val title: String = "",
    val subtitle: String? = null,
    val endText: String = ""
)
