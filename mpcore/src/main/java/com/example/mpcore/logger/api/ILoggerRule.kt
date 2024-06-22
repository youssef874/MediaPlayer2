package com.example.mpcore.logger.api

import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.data.BaseLoggerModule

/**
 * This structure of log formatting to be display in console
 */
interface ILoggerRule {

    /**
     * This method to define logs msg structure to be displayed in console
     * @param logs: collection of string can be msd,tag,class name ...
     * @param logLevel: [MPLoggerLevel]
     * @return the string formatted to be printed in the console
     */
    fun defineLoggerRule(logLevel: MPLoggerLevel, logs: BaseLoggerModule): String
}