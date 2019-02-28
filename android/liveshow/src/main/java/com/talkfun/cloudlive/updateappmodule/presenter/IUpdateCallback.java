package com.talkfun.cloudlive.updateappmodule.presenter;

/**
 * Created by Administrator on 2017/6/23.
 */
public interface IUpdateCallback<T> {
    void noNewVersion();

    void newVersion(T t);

    void downloadProgress(int percent);

    void downloadSuccess(String url);

    void downloadFail(String msg);
}
