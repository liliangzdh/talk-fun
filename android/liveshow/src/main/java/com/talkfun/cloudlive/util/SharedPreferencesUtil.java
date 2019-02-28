package com.talkfun.cloudlive.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreferencesUtil {
    private static String SP_NAME = "SP_NAME";
    public static String SP_LIVEROOM_CHECK = "sp_liveRoom_check";  //直播房间模式是否记住的idList
    public static String SP_LIVECOURSE_CHECK = "sp_liveCourse_check"; //直播课程模式是否记住的idList
    public static String SP_PLAYBACKROOM_CHECK = "sp_playbackRoom_check"; //点播房间模式是否记住的idList
    public static String SP_PLAYBACKCOURSE_CHECK = "sp_playbackCourse_check"; //点播房间模式是否记住的idList

    public static String SP_LIVEROOM_IDLIST = "sp_liveRoom_idList"; //直播的房间idList
    public static String SP_LIVECOURSE_IDLIST = "sp_liveCourse_idList"; //直播的课程idList
    public static String SP_PLAYBACKROOM_IDLIST = "sp_playbackRoom_idList"; //点播的房间idList
    public static String SP_PLAYBACKCOURSE_IDLIST = "sp_playbackCourse_idList"; //直播的课程idList

    public static String SP_LIVEROOM_NAMELIST = "sp_liveRoom_nameList"; //直播的房间用户名List
    public static String SP_LIVECOURSE_NAMELIST = "sp_liveCourse_nameList"; //直播的课程用户名List

    public static String SP_LIVEROOM_GUIDE = "sp_liveRoom_Guide";//直播间的引导

    public static String SP_LOGIN_LOGO_URL = "logo_url";

    public static String SP_LOGIN_ID_LIST = "sp_login_id_list";
    public static String SP_LOGIN_ID_LIST_CHECK = "sp_login_id_list_check";


    public static void saveBoolean(Context context, String ID, boolean Value) {
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(ID, Value).commit();
    }

    public static boolean getBoolean(Context context, String ID) {
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(ID, true);
    }

    /**
     * 存储房间ID对应的Token
     */
    public static void saveString(Context context, String ID, String Value) {
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(ID, Value).commit();
    }

    public static void removeString(Context context, String ID) {
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(ID).commit();
    }

    public static String getString(Context context, String ID) {
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return prefs.getString(ID, "");
    }

    public static void saveStringList(Context context, String key, List<String> data) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String value = data.toString().trim();
        sp.edit().putString(key, value).commit();
//        sp.edit().putInt(key, data.size());
//        for (int i = 0; i < data.size(); i++) {
//            sp.edit().putString("item_" + i, data.get(i));
//        }
//        sp.edit().commit();

    }

    public static List<String> getStringList(Context context, String key) {
        List<String> environmentList = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
//        int environNums = prefs.getInt(key, 0);
//        for (int i = 0; i < environNums; i++) {
//            String environItem = prefs.getString("item_" + i, null);
//            environmentList.add(environItem);
//        }

//
        String value = prefs.getString(key, "");
        if (!TextUtils.isEmpty(value)) {
            String[] split = value.substring(1, value.length() - 1).split(",");
            List<String> list = Arrays.asList(split);
            environmentList.addAll(list);
        }

        // Arrays.fill(resArray, true);
        /*try {
            JSONArray jsonArray = new JSONArray(prefs.getString(key, "[]"));
            if (jsonArray != null) {
                int len = jsonArray.length();

                for (int i = 0; i < len; i++) {
                    resArray.add(jsonArray.getString(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return environmentList;
    }
}
