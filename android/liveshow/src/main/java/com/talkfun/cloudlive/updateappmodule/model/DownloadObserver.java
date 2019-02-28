package com.talkfun.cloudlive.updateappmodule.model;

import android.app.DownloadManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Message;
import android.util.Log;

import java.io.File;


public class DownloadObserver extends ContentObserver {
    private DownloadManager dm;
    private long downloadId;
    private DownloadApkImpl.DownloadHandler handle;
    private boolean isDone = false;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public DownloadObserver(DownloadManager dm, long downloadId, DownloadApkImpl.DownloadHandler handler) {
        super(handler);
        this.dm = dm;
        this.downloadId = downloadId;
        this.handle = handler;
        isDone = false;
    }


    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = dm.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            Message msg = new Message();
            switch (status) {
                case DownloadManager.STATUS_RUNNING:
                    msg.what = 1;
                    double totalSizeBytes = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    double downloadSizeBytes = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    msg.arg1 = (int) ((downloadSizeBytes / totalSizeBytes) * 100);
                    Log.d("tony", "percent : " + msg.arg1);
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    if (!isDone) {
                        String successFilePath = Uri.parse(c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))).getPath();
                        msg.what = 2;
                        msg.obj = successFilePath;
                        isDone = true;
                    }
                    break;
                case DownloadManager.STATUS_FAILED:
                    String filePath = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    msg.what = 3;
                    msg.obj = "failed";
                    break;
                default:
                    break;

            }
            handle.sendMessage(msg);
            c.close();
        }
    }
}
