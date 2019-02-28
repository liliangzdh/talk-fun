package com.talkfun.cloudlive.bean;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by ccy on 2017/12/19.
 */

public class ScanQRLoginBean implements Serializable {

    private static final long serialVersionUID = -528108794600340250L;
    /**
     * type : tempLogin
     * access_token :
     * liveid : 0
     * roomid : 678322
     * title : 陈长衍
     * logo : http://static-1.talk-fun.com/upload/style/2017/12/073651d8f7118e75c9483915086ada9f.png
     * mode : 4
     * role : user
     * sign : 9759ef30614f874fd3b553001b9f6192
     * temporary : 1
     * et : 1513683432
     */

    private String type;
    private String access_token;
    private String liveid;
    private String roomid;
    private String title;
    private String logo;
    private int mode;
    private String role;
    private String sign;
    private String temporary;
    private String et;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getLiveid() {
        return liveid;
    }

    public void setLiveid(String liveid) {
        this.liveid = liveid;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTemporary() {
        return temporary;
    }

    public void setTemporary(String temporary) {
        this.temporary = temporary;
    }

    public String getEt() {
        return et;
    }

    public void setEt(String et) {
        this.et = et;
    }

    public static ScanQRLoginBean objectFromData(String result) {
        return new Gson().fromJson(result, ScanQRLoginBean.class);
    }
}
