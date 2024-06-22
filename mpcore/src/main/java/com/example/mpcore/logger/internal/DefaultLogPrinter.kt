package com.example.mpcore.logger.internal

import android.util.Log
import com.example.mpcore.logger.api.ILoggerPrinter
import com.example.mpcore.logger.api.data.MPLoggerLevel

object DefaultLogPrinter: ILoggerPrinter{
    override fun writeLog(log: String, tag: String, logLevel: MPLoggerLevel) {

        when(logLevel){
            MPLoggerLevel.WARNING->Log.w(tag,log)
            MPLoggerLevel.INFO->Log.i(tag,log)
            MPLoggerLevel.ERROR->Log.e(tag,log)
            MPLoggerLevel.DEBUG->Log.d(tag,log)
            MPLoggerLevel.NONE->Unit
        }
    }

}