package com.talkfun.cloudlive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.talkfun.cloudlive.activity.LoginJumpActivity;
import com.talkfun.cloudlive.activity.PlayDownLoadActivity;

import java.util.ArrayList;

public class LiveShow extends ReactContextBaseJavaModule {
    private Context mContext;
    private Promise promisee;
    private ArrayList<String> idList;
    private ArrayList<String> checkIdList;
    public static final int LIVE_TYPE = 4;  //直播
    private int type = LIVE_TYPE; //登录类型，直播/点播
    private int listMaxSize = 5;

    public LiveShow(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public boolean canOverrideExistingModule() {
        return true;
    }

    @Override
    public String getName() {
        return "LiveShow";
    }


    /***
     * 回放列表
     */
    @ReactMethod
    public void DownloadList(String text, Promise promise){
        Intent intent = new Intent(mContext, PlayDownLoadActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        promise.resolve("success");
    }

    /*
     *直播
     */
    @ReactMethod
    public void LiveShow(ReadableMap data, Promise promise) {
        Log.e("LiveShow", "调用了");
        live(data);
        promise.resolve("success");
    }

    public void live(ReadableMap data1) {
        ReadableNativeMap data = (ReadableNativeMap) data1;
        String accessToken = data.getString("accessToken");
        Log.e("tst", data.toString());
        String type = data.getString("type");
        String title  = "默认";
        int playbackId = 0;
        if (data.hasKey("title")) {
             title = data.getString("title");
        }
        if(data.hasKey("playbackId")){
            playbackId = data.getInt("playbackId");
        }

        Log.e("test","id=====?"+playbackId);
        Bundle bundle = new Bundle();
        bundle.putString(LoginJumpActivity.TOKEN_PARAM, accessToken);
        bundle.putString(LoginJumpActivity.LOG0_PARAM, "");
        bundle.putString(LoginJumpActivity.TITLE_PARAM, title);

        if (type.equals("live")) {
            bundle.putInt(LoginJumpActivity.TYPE_PARAM, 4);
        } else {
            bundle.putInt(LoginJumpActivity.TYPE_PARAM, 5);
        }

        bundle.putString(LoginJumpActivity.ID_PARAM, String.valueOf(playbackId));
        Intent intent = new Intent(mContext, LoginJumpActivity.class);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }


}




