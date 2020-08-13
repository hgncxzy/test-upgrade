package com.xzy.testupgrade

/**
 * @author xzy 2020/8/5.
 */
@Suppress("unused")
class Configs {
    companion object {
        // 日志
        const val TAG = "【升级测试】"

        // 应用的包名和启动 Activity
        const val APP_PKG_NAME = "com.xzy.testupgrade"
        const val APP_LAUNCH_ACTIVITY = "com.xzy.testupgrade.MainActivity"

        // 下载
        const val DOWNLOAD_URL = "https://sourl.cn/9aNppJ"
        const val FILE_SAVE_SUB_PATH = "test-upgrade-2.0.0.apk"
    }
}
