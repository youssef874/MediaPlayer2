package com.example.mpcore.logger.internal

import com.example.mpcore.logger.api.ILoggerRule
import com.example.mpcore.logger.api.data.MPLoggerLevel
import com.example.mpcore.logger.internal.data.BaseLoggerModule
import com.example.mpcore.logger.internal.data.DefaultLoggerModule

internal object DefaultLoggerRule: ILoggerRule {

    private val classesToIgnore = mutableListOf<String>()

    fun addClassToIgnore(className: String){
        if (classesToIgnore.contains(className)){
            return
        }
        classesToIgnore.add(className)
    }

    override fun defineLoggerRule(logLevel: MPLoggerLevel, logs: BaseLoggerModule): String {
        val trace = Thread.currentThread().getStackTrace()
        val index =getStackOffset(trace) + 1
        if (index >= trace.size){
            return ""
        }
        val log = logs as DefaultLoggerModule
        val stringBuilder = StringBuilder()
        stringBuilder.append(log.className)
        stringBuilder.append(" | ")
        stringBuilder.append(log.methodName)
        stringBuilder.append(" | ")
        stringBuilder.append(log.logTag)
        stringBuilder.append(" | ")
        stringBuilder.append(logLevel.description)
        stringBuilder.append(" | ")
        stringBuilder.append("[")
        stringBuilder.append(trace[index].fileName)
        stringBuilder.append(":")
        stringBuilder.append(trace[index].lineNumber)
        stringBuilder.append("]")
        stringBuilder.append(trace[index].methodName)
        stringBuilder.append(" | ")
        stringBuilder.append(log.logMsg)
        return stringBuilder.toString()
    }

    private fun getStackOffset(
        trace: Array<StackTraceElement>
    ): Int{
        var i = 3
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
            if ( !isContainIgnored(name) && DefaultLoggerRule::class.java.name != name) {
                return --i
            }
            i++
        }
        return -1
    }

    private fun isContainIgnored(name: String): Boolean{
        if (name.isEmpty()){
            return false
        }
        if (classesToIgnore.isEmpty()){
            return false
        }else{
            classesToIgnore.forEach {
                if (it == name){
                    return true
                }
            }
        }
        return false
    }
}