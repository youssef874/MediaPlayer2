package com.example.mpcore.audio.internal.data.datastore.data

/**
 * A generic data store model to contain the key and the default value of each datastore
 */
internal data class AudioDataStoreModel<T>(
    val key:String,
    val defaultValue: T
)
