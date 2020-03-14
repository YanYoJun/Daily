package com.plbear.daily.utils

import android.util.Log
import kotlinx.coroutines.*

/**
 * created by yanyongjun on 2020-03-14
 */

fun runUI(block: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch(Dispatchers.Main, block = block)
}

fun runIO(block: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch(Dispatchers.IO, block = block)
}

fun <T> asyncUI(block: suspend CoroutineScope.() -> T): Deferred<T> {
    return GlobalScope.async(Dispatchers.Main, block = block)
}

fun <T> asyncIO(block: suspend CoroutineScope.() -> T): Deferred<T> {
    return GlobalScope.async(Dispatchers.IO, block = block)
}

fun logcat(msg:String){
    Log.e("mydaily",msg)
}