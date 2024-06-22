package com.example.mpcore.logger.internal

import com.example.mpcore.logger.api.ILoggerPrinter
import com.example.mpcore.logger.api.ILoggerRule
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.data.BaseLoggerModule
import com.example.mpcore.logger.internal.data.DefaultLoggerModule

internal class MPLoggerConfiguration private constructor(){

    private lateinit var _loggerRule: ILoggerRule
    val loggerRule: ILoggerRule get() = _loggerRule

    private lateinit var _loggerPrinter: ILoggerPrinter
    val loggerPrinter: ILoggerPrinter get() = _loggerPrinter

    fun setLoggerRule(loggerRule: ILoggerRule){
        _loggerRule = loggerRule
    }

    fun setLoggerPrinter(loggerPrinter: ILoggerPrinter){
        _loggerPrinter = loggerPrinter
    }

    class Builder(loggerRule: ILoggerRule, loggerPrinter: ILoggerPrinter){

        private val loggerConfiguration = MPLoggerConfiguration()

        init {
            loggerConfiguration.setLoggerPrinter(loggerPrinter)
            loggerConfiguration.setLoggerRule(loggerRule)
        }

        fun log(logLevel: MPLoggerLevel, logs: BaseLoggerModule) {
            val msg = loggerConfiguration.loggerRule.defineLoggerRule(logLevel, logs)
            return loggerConfiguration.loggerPrinter.writeLog(msg,logs.tag,logLevel)
        }
    }

    class DefaultBuilder{
        private val defaultLoggerRule = DefaultLoggerRule

        fun addClassToIgnore(className: String): DefaultBuilder {
            defaultLoggerRule.addClassToIgnore(className)
            return this
        }

        init {
            addClassToIgnore(DefaultBuilder::class.java.name)
        }

        fun log(className: String, tag: String,methodName: String,msg: String,logLevel: MPLoggerLevel) {
            val module = DefaultLoggerModule(logTag = tag, logMsg = msg, className = className,methodName)
            val log = defaultLoggerRule.defineLoggerRule(logLevel, module)
            return DefaultLogPrinter.writeLog(log, tag,logLevel)
        }
    }
}