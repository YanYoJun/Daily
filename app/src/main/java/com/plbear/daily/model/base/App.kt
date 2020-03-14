package com.plbear.daily.model.base

import android.app.Application

/**
 * created by yanyongjun on 2020-03-14
 */
class App : Application() {
    companion object {
        private var sInstance: Application? = null
        fun instance(): Application {
            return sInstance!!
        }
    }

    override fun onCreate() {
        sInstance = this
        super.onCreate()
    }
}