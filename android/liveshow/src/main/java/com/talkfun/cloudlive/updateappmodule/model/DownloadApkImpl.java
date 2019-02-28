package com.talkfun.cloudlive.updateappmodule.model;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.talkfun.cloudlive.updateappmodule.VersionInfo;


public class DownloadApkImpl {
    private DownloadListener mListener;
    private long downloadId = -1;
    private DownloadManager dm;
    private Context mContext;
    private String cacheName = VersionInfo.AppName;
    private String CACHE_PATH = "content://downloads/";

    private final DownloadHandler downloadHandler = new DownloadHandler();

    public DownloadApkImpl(Context context) {
        mContext = context;
    }


    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    /**
     * 开始缓存 app
     */
    public void startCache(Uri uri, DownloadListener listener) {
        mListener = listener;
        DownloadManager.Request req = new DownloadManager.Request(uri);
        //Wi-Fi环境下下载
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //不显示
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        req.setDestinationInExternalPublicDir(CACHE_PATH, cacheName);
        req.setMimeType("application/vnd.android.package-archive");

        dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = dm.enqueue(req);
        mContext.getContentResolver().registerContentObserver(Uri.parse(CACHE_PATH),
                true,
                new DownloadObserver(dm, downloadId, downloadHandler));

    }

    /**
     * 停止缓存 app
     */
    public void stopCache() {
        if (dm == null)
            dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadId > 0)
            dm.remove(downloadId);
    }



    public interface DownloadListener {
        void downloading(int percent);

        void success(String url);

        void failed(String error);
    }

    class DownloadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.d("tony", "downloading");
                    if (mListener != null)
                        mListener.downloading(msg.arg1);
                    break;
                case 2:
                    if (mListener != null) {
                        mListener.success((String) msg.obj);
                    }
                    break;
                case 3:
                    if (mListener != null)
                        mListener.failed((String) msg.obj);
                    Toast.makeText(mContext, "failed", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
}
