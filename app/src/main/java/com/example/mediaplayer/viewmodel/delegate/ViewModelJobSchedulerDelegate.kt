package com.example.mediaplayer.viewmodel.delegate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.IJobScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ViewModelJobSchedulerDelegate(
    private val block: suspend (args: List<Any>,job: Job?)->Unit
): ReadWriteProperty<ViewModel,IJobScheduler>, IJobScheduler {

    private var _job: Job? = null
    override val job: Job?
        get() = _job
    private lateinit var scope: CoroutineScope

    override fun getValue(thisRef: ViewModel, property: KProperty<*>): IJobScheduler {
        scope = thisRef.viewModelScope
        return this
    }

    override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: IJobScheduler) {
        scope = thisRef.viewModelScope
    }

    override fun startJob(dispatcher: CoroutineDispatcher, vararg args: Any) {
        _job = scope.launch(dispatcher){
            block(args.toList(),job)
        }
    }

    override fun cancelJob() {
        if (job?.isActive == true){
            job?.cancel()
            _job = null
        }
    }
}