package com.xzy.testupgrade

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.xzy.testupgrade.Configs.Companion.TAG

/**
 *
 * @author ：created by xzy.
 * @date ：2020/8/5
 */
class ApkUninstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals("android.intent.action.PACKAGE_REMOVED")) {
            Toast.makeText(context, "升级测试验证程序已卸载", Toast.LENGTH_LONG).show()
            LoggerHandler.i(TAG,"升级测试验证程序已卸载")
        }
    }
}