package com.example.mediaplayer

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

interface IJobScheduler {

    val job: Job?

    fun startJob(dispatcher: CoroutineDispatcher = Dispatchers.IO,vararg args: Any)

    fun cancelJob()
}