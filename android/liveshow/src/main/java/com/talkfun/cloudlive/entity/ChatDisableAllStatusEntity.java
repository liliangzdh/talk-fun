package com.talkfun.cloudlive.entity;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by asus on 2017/11/15.
 */

public class ChatDisableAllStatusEntity implements Serializable {

    private static final long serialVersionUID = 6012683945645931892L;
    private int status = 0;
    public static final int DISABLE = 1;
    public static final int ENABLE = 0;
    public void setStatus(int status){
        this.status = status;
    }

    public int getStatus(){
        return status;
    }

    public boolean isDisable(){
        return status == DISABLE;
    }

    public static ChatDisableAllStatusEntity objectFromData(String str) {

        return new Gson().fromJson(str, ChatDisableAllStatusEntity.class);
    }
}
