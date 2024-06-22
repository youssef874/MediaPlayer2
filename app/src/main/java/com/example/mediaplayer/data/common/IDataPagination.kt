package com.example.mediaplayer.data.common

interface IDataPagination<ITEM,KEY> {

    suspend fun loadNextItem()

    fun reset()
}