@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")

package com.xzy.testupgrade

import android.app.DownloadManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Environment

/**
 *
 * @author ：created by xzy.
 * @date ：2020/8/5
 */
@Suppress("unused")
class DownloadHandler {

    @Suppress("DEPRECATION")
    fun download(context: Context,downloadUrl: String) :Long{
        val uri = Uri.parse(downloadUrl)
        val request = DownloadManager.Request(uri)
        request.setTitle("测试升级")
        request.setDescription("测试升级程序正在下载.....")
        request.setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE
                    or DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        )
        // 设置漫游状态下是否可以下载
        request.setAllowedOverRoaming(false)
        // 设置文件存放目录
        request.setDestinationInExternalFilesDir(
            context,
            Environment.getExternalStorageDirectory().absolutePath,
            Configs.FILE_SAVE_SUB_PATH
        )
        LoggerHandler.i(Configs.TAG, "构建文件保存路径：" + Environment.getExternalStorageDirectory().absolutePath + "")
        LoggerHandler.i(Configs.TAG, "获取系统下载服务：downloadManager")
        // 获取系统服务
        val downloadManager: DownloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        LoggerHandler.i(Configs.TAG, "开始下载高版本测试升级程序")
        // 进行下载
        val downloadId = downloadManager.enqueue(request)
        LoggerHandler.i(Configs.TAG, "测试升级程序正在下载......")
        return downloadId
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @param context Context
     * @return true 表示网络可用
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivity: ConnectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info: NetworkInfo = connectivity.activeNetworkInfo
        if (info.isConnected) {
            // 当前网络是连接的
            if (info.state === NetworkInfo.State.CONNECTED) {
                // 当前所连接的网络可用
                return true
            }
        }
        return false
    }
}