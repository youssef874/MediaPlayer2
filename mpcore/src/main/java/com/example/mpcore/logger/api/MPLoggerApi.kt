package com.example.mpcore.logger.api

import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.DefaultLogPrinter
import com.example.mpcore.logger.internal.MPLoggerConfiguration
import com.example.mpcore.logger.internal.data.BaseLoggerModule

class MPLoggerApi {

    /**
     * This hold default implementation of log which will print your message with
     * clickable link to where you write your log which will make you work easier
     */
    object DefaultMpLogger {

        private val configuration = MPLoggerConfiguration.DefaultBuilder()
            .addClassToIgnore(DefaultMpLogger::class.java.name)

        /**
         * Call this to display you log in debug mode
         * to use this call [MPLoggerApi.DefaultMpLogger.d]
         * @param className: Name of the class that you called this method from
         * @param tag: You tag to categorize you log
         * @param methodName: Name of the method you called this method
         * @param msg: the main message you want to print
         */
        fun d(className: String, tag: String, methodName: String, msg: String) {
            configuration.log(
                className = className,
                tag = tag,
                methodName = methodName,
                msg = msg,
                logLevel = MPLoggerLevel.DEBUG
            )
        }

        /**
         * Call this to display you log in info mode
         * to use this call [MPLoggerApi.DefaultMpLogger.i]
         * @param className: Name of the class that you called this method from
         * @param tag: You tag to categorize you log
         * @param methodName: Name of the method you called this method
         * @param msg: the main message you want to print
         */
        fun i(className: String, tag: String, methodName: String, msg: String) {
            configuration.log(
                className = className,
                tag = tag,
                methodName = methodName,
                msg = msg,
                logLevel = MPLoggerLevel.INFO
            )
        }

        /**
         * Call this to display you log in warning mode
         * to use this call [MPLoggerApi.DefaultMpLogger.w]
         * @param className: Name of the class that you called this method from
         * @param tag: You tag to categorize you log
         * @param methodName: Name of the method you called this method
         * @param msg: the main message you want to print
         */
        fun w(className: String, tag: String, methodName: String, msg: String) {
            configuration.log(
                className = className,
                tag = tag,
                methodName = methodName,
                msg = msg,
                logLevel = MPLoggerLevel.WARNING
            )
        }

        /**
         * Call this to display you log in error mode
         * to use this call [MPLoggerApi.DefaultMpLogger.e]
         * @param className: Name of the class that you called this method from
         * @param tag: You tag to categorize you log
         * @param methodName: Name of the method you called this method
         * @param msg: the main message you want to print
         */
        fun e(className: String, tag: String, methodName: String, msg: String) {
            configuration.log(
                className = className,
                tag = tag,
                methodName = methodName,
                msg = msg,
                logLevel = MPLoggerLevel.ERROR
            )
        }
    }

    /**
     * Call this to custom ise you log behavior
     * @param loggerRule : implementation of [ILoggerRule] to identify your log format to be printed
     * @param loggerPrinter: Implementation of [ILoggerPrinter] to specify how you going to print your logs
     * This param is optional if you did not specify it the default behavior will work which use android [Log]
     */
    class CustomLogger(loggerRule: ILoggerRule, loggerPrinter: ILoggerPrinter? = null) {

        private val configuration =
            MPLoggerConfiguration.Builder(loggerRule, loggerPrinter ?: DefaultLogPrinter)

        /**
         * Call this to display you log in debug mode
         * to use this call [MPLoggerApi.CustomLogger.d]
         * @param logs: all input you want to display the order you give is the order you receive in
         * [ILoggerRule.defineLoggerRule]
         * @param tag: You tag to categorize you log
         */
        fun d(loggerModule: BaseLoggerModule) {

            configuration.log(MPLoggerLevel.DEBUG, loggerModule)
        }

        /**
         * Call this to display you log in info mode
         * to use this call [MPLoggerApi.CustomLogger.i]
         * @param logs: all input you want to display the order you give is the order you receive in
         * [ILoggerRule.defineLoggerRule]
         * @param tag: You tag to categorize you log
         */
        fun i(loggerModule: BaseLoggerModule) {
            configuration.log(MPLoggerLevel.INFO, loggerModule)
        }

        /**
         * Call this to display you log in warning mode
         * to use this call [MPLoggerApi.CustomLogger.w]
         * @param logs: all input you want to display the order you give is the order you receive in
         * [ILoggerRule.defineLoggerRule]
         * @param tag: You tag to categorize you log
         */
        fun w(loggerModule: BaseLoggerModule) {
            configuration.log(MPLoggerLevel.WARNING, loggerModule)
        }

        /**
         * Call this to display you log in error mode
         * to use this call [MPLoggerApi.CustomLogger.e]
         * @param logs: all input you want to display the order you give is the order you receive in
         * [ILoggerRule.defineLoggerRule]
         * @param tag: You tag to categorize you log
         */
        fun e(loggerModule: BaseLoggerModule) {
            configuration.log(MPLoggerLevel.ERROR, loggerModule)
        }
    }
}