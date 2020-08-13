
package com.xzy.testupgrade

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent

/**
 * @author xzy 2020/8/5.
 */
object LoggerHandler {
    val lives = mutableListOf<MutableLiveData<String>>()
    fun d(tag: String, info: Any?) {
        Log.d(tag, info?.toString() ?: "")
        subscribe(info)
    }

    fun i(tag: String, info: Any?) {
        Log.i(tag, info?.toString() ?: "")
        subscribe(info)
    }

    fun w(tag: String, info: Any?) {
        Log.w(tag, info?.toString() ?: "")
        subscribe(info)
    }

    fun e(tag: String, info: Any?) {
        Log.e(tag, info?.toString() ?: "")
        subscribe(info)
    }

    private fun subscribe(info: Any?) {
        Handler(Looper.getMainLooper()).post {
            lives.forEach {
                it.value = (info?.toString() ?: "")
            }
        }
    }

    /** auto release onDestroy**/
    fun registerObserver(ctx: LifecycleOwner, listener: (Boolean, String) -> Unit) {
        lives.add(
            Test(
                ctx,
                listener
            )
        )
    }

    class Test(ctx: LifecycleOwner, listener: (Boolean, String) -> Unit) :
        MutableLiveData<String>(), LifecycleObserver {
        private var isActive = false
        private var o = Observer<String> { it ->
            listener.invoke(isActive, it)
        }

        init {
            ctx.lifecycle.addObserver(this)
            observeForever(o)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            isActive = true
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            isActive = false
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            removeObserver(o)
            lives.remove(this)
        }
    }
}
