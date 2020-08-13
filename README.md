#### 该项目的作用

1. 下载功能
2. 调用系统安装器安装
3. 退出时，卸载自身

#### 关键的代码

获取全局上下文

```kotlin
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
```

通过包名调用系统安装器安装 apk

```kotlin
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
```

使用 DownloadManager 下载时，可以使用 ContentObserver 来获取进度，下载进度监听，参考[android DownloadManager获取进度并显示](https://blog.csdn.net/u014310722/article/details/50544118?utm_medium=distribute.pc_relevant.none-task-blog-baidulandingword-2&spm=1001.2101.3001.4242)

```java
public class DownloadObserver extends ContentObserver {
    private Handler mHandler;
    private DownloadManager mDownloadManager;
    private DownloadManager.Query query;
    private boolean downloading = true;

    @SuppressLint("NewApi")
    public DownloadObserver(Handler handler, Context context, long downId) {
        super(handler);
        this.mHandler = handler;
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        query = new DownloadManager.Query().setFilterById(downId);
    }

    @SuppressLint("NewApi")
    @Override
    public void onChange(boolean selfChange) {
        // 每当/data/data/com.android.providers.download/database/database.db变化后，触发onCHANGE，开始具体查询
        super.onChange(selfChange);
        if (downloading) {
            Cursor cursor = mDownloadManager.query(query);
            cursor.moveToFirst();
            int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            int progress = (bytesDownloaded * 100) / bytesTotal;
            mHandler.sendEmptyMessageDelayed(progress, 100);
            if (cursor.getInt(
                    cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                downloading = false;
                cursor.close();
            }
        }
    }
}
```

