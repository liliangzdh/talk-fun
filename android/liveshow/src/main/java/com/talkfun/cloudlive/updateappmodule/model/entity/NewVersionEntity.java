package com.talkfun.cloudlive.updateappmodule.model.entity;

/**
 * Created by Administrator on 2017/6/23.
 */
public class NewVersionEntity {
    private int versionCode;
    private String content;
    private String url;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
