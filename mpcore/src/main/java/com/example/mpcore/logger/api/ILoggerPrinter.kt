package com.example.mpcore.logger.api

import com.example.mpcore.logger.api.data.MPLoggerLevel

/**
 * Structure for printing log messages
 */
interface ILoggerPrinter {

    /**
     * Call this to print logs in the console
     * @param log: all message you wat to be printed
     * @param tag: categorize you log message
     * @param logLevel: log priority level
     */
    fun writeLog( log: String,tag: String,logLevel: MPLoggerLevel)
}

interface ILogLevel{

    //structure of log in debug level
    fun d()

    //structure of log in info level
    fun i()

    //structure of log in warning level
    fun w()

    //structure of log in error level
    fun e()
}