package com.example.mpcore.common.internal

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * This represent provider for some util components for the sdk
 */
class SDKComponent {

    /**
     * This represent the [Context] provider
     */
    class SDKContext private constructor(){

        private var isInitialized = false

        private lateinit var context: Context

        /**
         * Call this to initialise the context to be provided
         */
        fun initializeContext(context: Context){
            if (!isInitialized){
                this.context = context
                isInitialized = true
            }
        }

        companion object{

            @SuppressLint("StaticFieldLeak")
            private lateinit var sdkContext: SDKContext

            fun initialize(context: Context){
                sdkContext = SDKContext()
                sdkContext.initializeContext(context)
            }

            fun getContext(): Context{
                if (!sdkContext.isInitialized){
                    throw UninitializedPropertyAccessException("Could access this object is not initialized please call MPSDKEnvironment.initializeMPSDK(context)")
                }
                return sdkContext.context
            }
        }
    }

    /**
     * This represent the [CoroutineScope] provider
     */
    object CoroutineComponent{

        val sdkCoroutineScope = CoroutineScope(SupervisorJob() +Dispatchers.Default)
    }
}