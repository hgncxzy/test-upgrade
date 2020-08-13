package com.xzy.testupgrade

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.xzy.testupgrade.Configs.Companion.DOWNLOAD_URL
import com.xzy.testupgrade.Configs.Companion.TAG
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_status_result.*
import java.lang.ref.WeakReference


/**
 * @author xzy 2020/8/5.
 */
@Suppress("unused")
class MainActivity : AppCompatActivity() {
    private var downloadObserver: DownloadObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerObserver()
        btn_upgrade.setOnClickListener {
            upgrade()
        }
        btn_exit.setOnClickListener { exit() }
    }

    @SuppressLint("SetTextI18n")
    private fun registerObserver() {
        val sb = StringBuilder()
        LoggerHandler.registerObserver(this, { b: Boolean, s: String ->
            sb.append(s + "\n")
            tvResult.text = sb.toString()
            tvResult.post {
                findViewById<ScrollView>(R.id.sv_result).smoothScrollBy(0, 1000)
            }
            if (tvResult.text.contains("广播发送完毕") || tvResult.text.contains("升级测试验证程序已卸载")) {
                finish()
            }
        })
        tv_version.text =
            "版本：" + this.packageManager.getPackageInfo(this.packageName, 0).versionName
    }

    internal class MyHandler(ref: WeakReference<MainActivity>?) :
        Handler() {
        private val activity: MainActivity? = ref?.get()

        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            activity?.runOnUiThread {
                activity.btn_upgrade.text = "升级（已下载" + msg.what.toString() + "%）"
                activity.btn_upgrade.isEnabled = activity.btn_upgrade.text.contains("100%")
            }
        }
    }

    private fun registerContentObserver(downloadId: Long) {
        unregisterContentObserver()
        downloadObserver = DownloadObserver(MyHandler(WeakReference(this)), this, downloadId)
        downloadObserver?.let {
            contentResolver.registerContentObserver(
                Uri.parse("content://downloads/"),
                true,
                it
            )
        }
    }

    private fun upgrade() {
        val downloadHandler = DownloadHandler()
        if (downloadHandler.isNetworkAvailable(this)) {
            LoggerHandler.i(TAG, "检测到网络可用")
            val downloadId = downloadHandler.download(this, DOWNLOAD_URL)
            registerContentObserver(downloadId)
        } else {
            LoggerHandler.i(TAG, "检测到网络不可可用")
        }
    }

    /**
     * 退出并卸载
     *参考 https://blog.csdn.net/zhouwengong/article/details/73374361
     * */
    private fun exit() {
        val packageURI = Uri.parse("package:$packageName")
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = packageURI
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterContentObserver()
    }

    private fun unregisterContentObserver() {
        downloadObserver?.let {
            contentResolver.unregisterContentObserver(it)
        }
    }
}
