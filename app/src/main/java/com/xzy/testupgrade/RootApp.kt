package com.xzy.testupgrade

import android.app.Application
import android.content.Context

/**
 * @author ：created by xzy.
 * @date ：2020/8/13
 */
class RootApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appCtx = applicationContext
    }

    companion object {
        //获取全局的上下文
        var appCtx //全局上下文
                : Context? = null
            private set

    }
}