package com.talkfun.cloudlive.util;

import com.talkfun.cloudlive.entity.SignEndEntity;
import com.talkfun.cloudlive.entity.SignEntity;

import org.json.JSONObject;

/**
 * Created by ccy on 2017/11/6.
 */

public class JsonUtil {
    public static SignEntity transferSignEntiy(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        SignEntity signEntity = new SignEntity();
        JSONObject args = jsonObject.optJSONObject("args");
        JSONObject data = args.optJSONObject("data");
        signEntity.setDuration(data.optInt("duration"));
        signEntity.setNickname(data.optString("nickname"));
        signEntity.setRole(data.optString("role"));
        signEntity.setSignId(data.optString("signId"));
        signEntity.setTime(data.optString("time"));
        return signEntity;


    }

    public static SignEndEntity transferSignEndEntity(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        SignEndEntity signEndEntity = new SignEndEntity();
        JSONObject args = jsonObject.optJSONObject("args");
        JSONObject data = args.optJSONObject("data");
        signEndEntity.setSignTotal(data.optString("signTotal"));
        signEndEntity.setTotal(data.optString("total"));
        return signEndEntity;


    }
}
