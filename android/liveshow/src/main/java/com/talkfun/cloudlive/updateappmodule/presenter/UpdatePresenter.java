package com.talkfun.cloudlive.updateappmodule.presenter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.talkfun.cloudlive.updateappmodule.model.CheckVersionIpml;
import com.talkfun.cloudlive.updateappmodule.model.DownloadApkImpl;
import com.talkfun.cloudlive.updateappmodule.model.entity.NewVersionEntity;


/**
 * Created by Administrator on 2017/6/21.
 */
public class UpdatePresenter implements IUpdatePresenter {
    private static final int NEW_VERSION = 1;
    private static final int NO_VERSION = 2;
    private static final int DOWNLOAD_PROGRESS = 3;
    private static final int DOWNLOAD_SUCCESS = 4;
    private static final int DOWNLOAD_FAIL = 5;
    private CheckVersionIpml checkVersionIpml;
    private DownloadApkImpl downloadApkIpml;
    private IUpdateCallback updateCallback;

    public UpdatePresenter(Context context, IUpdateCallback callback) {
        updateCallback = callback;
        checkVersionIpml = new CheckVersionIpml(context);  //检查版本
        downloadApkIpml = new DownloadApkImpl(context);   //下载apk
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null && updateCallback != null) {
                switch (msg.what) {
                    case NEW_VERSION:
                        NewVersionEntity newVersionEntity = (NewVersionEntity) msg.obj;
                        updateCallback.newVersion(newVersionEntity);  //有新的版本
                        break;
                    case NO_VERSION:
                        updateCallback.noNewVersion();
                        break;
                    case DOWNLOAD_SUCCESS:
                        String downloadUrl = (String) msg.obj;
                        updateCallback.downloadSuccess(downloadUrl);
                        break;
                    case DOWNLOAD_FAIL:
                        String errorMsg = (String) msg.obj;
                        updateCallback.downloadFail(errorMsg);
                        break;
                    case DOWNLOAD_PROGRESS:
                        int progress = (int) msg.obj;
                        updateCallback.downloadProgress(progress);
                        break;
                    default:
                        break;
                }
            }
        }
    };


    @Override
    public void checkVersion() {
        checkVersionIpml.checkVersion(new CheckVersionIpml.CheckVersionListener() {
            @Override
            public void newVersion( NewVersionEntity newVersionEntity) {   //url -- app 下载的路径
                Message message = handler.obtainMessage(NEW_VERSION);
                message.obj = newVersionEntity;
                handler.sendMessage(message);
            }

            @Override
            public void noNewVersion() {
                handler.obtainMessage(NO_VERSION).sendToTarget();
            }
        });
    }


    @Override
    public void downloadApk(String url) {
        downloadApkIpml.startCache(Uri.parse(url), new DownloadApkImpl.DownloadListener() {
            @Override
            public void downloading(int percent) {
                Message message = handler.obtainMessage(DOWNLOAD_PROGRESS);
                message.obj = percent;
                handler.sendMessage(message);
            }

            @Override
            public void success(String url) {
                checkVersionIpml.setLocalCacheAppInfo();
                Message message = handler.obtainMessage(DOWNLOAD_SUCCESS);
                message.obj = url;
                handler.sendMessage(message);
            }

            @Override
            public void failed(String error) {
                Message message = handler.obtainMessage(DOWNLOAD_FAIL);
                message.obj = error;
                handler.obtainMessage(DOWNLOAD_FAIL).sendToTarget();
            }
        });
    }

    @Override
    public void stopDownloadApk() {
        downloadApkIpml.stopCache();
    }


    public void setCacheName(String name) {
        downloadApkIpml.setCacheName(name);
    }



    public void destroy() {
        checkVersionIpml = null;
        downloadApkIpml = null;
        updateCallback = null;
    }
}
