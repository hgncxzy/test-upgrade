@file:Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.xzy.testupgrade

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.widget.Toast
import android.os.Build
import androidx.core.content.FileProvider
import com.xzy.testupgrade.Configs.Companion.TAG
import java.io.File

/**
 * @author xzy 2020/8/5.
 */
class DownLoadCompleteReceiver : BroadcastReceiver() {

    @SuppressLint("WrongConstant")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            LoggerHandler.i(TAG, "")
            //在广播中取出下载任务的id
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Toast.makeText(context, "任务已经下载完成！即将启动安装器进行升级！", Toast.LENGTH_LONG).show()
            LoggerHandler.i(TAG, "收到广播,任务已经下载完成！即将启动安装器进行升级！")
            val query: DownloadManager.Query = DownloadManager.Query()
            val dm: DownloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            query.setFilterById(id)
            val c: Cursor = dm.query(query)
            try {
                if (c.moveToFirst()) {
                    // 获取文件下载路径
                    val fileUriIdx = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                    val fileUri = c.getString(fileUriIdx)
                    var fileName: String? = null
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        if (fileUri != null) {
                            fileName = Uri.parse(fileUri).path
                        }
                    } else {
                        // Android 7.0以上的方式：请求获取写入权限，这一步报错
                        // 过时的方式：DownloadManager.COLUMN_LOCAL_FILENAME
                        val fileNameIdx = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)
                        fileName = c.getString(fileNameIdx)
                    }
                    LoggerHandler.i(TAG, "下载文件的文件名：$fileName")
                    val status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        // 启动更新，给安装器发送广播
                        LoggerHandler.i(TAG, "启动系统安装器安装！")
                        fileName?.let {
                            installApk(context, it)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return
            } finally {
                c.close()
            }
        }
    }

    /**
     * need permission <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
     * */
    private fun installApk(context: Context, downloadApk: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(downloadApk)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri = FileProvider.getUriForFile(
                context,
                context.packageName + ".FileProvider",
                file
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val uri = Uri.fromFile(file)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        }
        context.startActivity(intent)
    }
}