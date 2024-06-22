package com.example.mpcore.logger.internal.data

internal data class DefaultLoggerModule(
    val logTag: String,
    val logMsg: String,
    val className: String,
    val methodName: String
): BaseLoggerModule(logTag, logMsg)
