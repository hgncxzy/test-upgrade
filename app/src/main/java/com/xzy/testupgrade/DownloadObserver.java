package com.xzy.testupgrade;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;

/**
 * @author ：created by xzy.
 * @date ：2020/8/5
 * 参考 https://blog.csdn.net/u014310722/article/details/50544118?utm_medium=distribute.pc_relevant.none-task-blog-baidulandingword-2&spm=1001.2101.3001.4242
 */
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