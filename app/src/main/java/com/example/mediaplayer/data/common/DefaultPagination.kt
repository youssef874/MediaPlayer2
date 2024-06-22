package com.example.mediaplayer.data.common

import com.example.mpcore.audio.api.data.MPApiResult

class DefaultPagination<ITEM,KEY>(
    private val initialKey: KEY,
    private inline val onLoadUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: KEY)-> MPApiResult<List<ITEM>>,
    private inline val getNextKey: suspend (List<ITEM>)->KEY,
    private inline val onError: suspend (Int,String)->Unit,
    private inline val onSuccess: suspend (items: List<ITEM>,newKey: KEY)->Unit
): IDataPagination<ITEM, KEY> {

    private var currentKey = initialKey
    private var isMakingRequest = false
    override suspend fun loadNextItem() {
        if (isMakingRequest){
            return
        }
        isMakingRequest = true
        onLoadUpdated(true)
        val result = onRequest(currentKey)
        isMakingRequest = false
        val items = result.getOrElse {errorCode,message->
            onError(errorCode,message)
            onLoadUpdated(false)
            return@getOrElse
        }
        items?.let {
            currentKey = getNextKey(it)
            onSuccess(it,currentKey)
            onLoadUpdated(false)
        }
    }

    private  suspend fun <T> MPApiResult<T>.getOrElse(onError: suspend (errorCode: Int, message: String)->Unit): T?{
        return when(this){
            is MPApiResult.Success->{
                data
            }

            is MPApiResult.Error->{
                onError(error.errorCode,error.errorMessage)
                null
            }

        }
    }

    override fun reset() {
        currentKey = initialKey
    }
}