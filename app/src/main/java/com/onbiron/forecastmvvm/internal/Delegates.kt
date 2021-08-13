package com.onbiron.forecastmvvm.internal

import kotlinx.coroutines.*

// Custom lazy function. By default lazy can not supports coroutines in itself.
fun <T> lazyDeferred(block: suspend CoroutineScope.() -> T): Lazy<Deferred<T>>{
    return lazy{
        GlobalScope.async(start= CoroutineStart.LAZY) {
            // start= CoroutineStart.LAZY means run this like lazy.
            // Lazy initializations not working when object created. it only works when UI loaded and ui says i need this object then lazy runs.
            block.invoke(this)
        }
    }
}