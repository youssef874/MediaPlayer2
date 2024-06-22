package com.example.mpcore.logger.internal.data

/**
 * This will be the default module of the logs if you want to log addition information
 * you can inherit from this class and add the the information as properties
 */
open class BaseLoggerModule(
    val tag: String,
    val msg: String
){
    override fun toString(): String {
        return "BaseLoggerModule [tag: $tag, msg: $msg]"
    }
}
