package com.talkfun.cloudlive.consts;


public class MainConsts {
    //    private static final String LIVE_LOGIN = "http://demo.talk-fun.com/login.php?key=%s&nickname=%s"; //直播登录接口
    private static final String LIVE_LOGIN = "https://open.talk-fun.com/live/mobile/login.php?id=%s&password=%s&nickname=%s&mode=%s"; //直播登录接口
    //    private static final String PLAY_BACK_LOGIN = "http://demo.talk-fun.com/api/playback.php?liveid=%s";//点播登录接口
    private static final String PLAY_BACK_LOGIN = "https://open.talk-fun.com/playback/mobile/login.php?id=%s&password=%s&mode=%s";//点播登录接口
    public static final String UPDATE_INFO_URL = "http://fp1.talk-fun.com/install_files/mobile/android/huantuoDemo/update.json";

    //新版登录接口url
    public static final String LOGIN_URL = "https://open.talk-fun.com/live/mobile/login_v2.php";
    /**
     * 临时登录URL
     */
    public static final String LOGIN_TEMP_URL = "https://open.talk-fun.com/live/mobile/tempLogin.php";
    //登录接口参数
    public static final String LIVE_LOGIN_PARAM = "id=%s&password=%s&nickname=%s&type=%s";
    public static final String LIVE_LOGIN_TEMP_PARAM = "nickname=%s&roomid=%s&role=%s&temporary=%s&et=%s&sign=%s";
    public static final String PLAYBACK_LOGIN_PARAM = "id=%s&password=%s&type=%s";

    /**
     * 二维码扫描URL
     */
    public static final String SCANQRCODE_URL = "https://open.talk-fun.com/live/mobile/scanQrcodeV2.php";
    public static final String QRCODE_URL_PARAM = "qrCodeUrl=%s";


    //-----------------------------------------通过扫码获取课程信息----------start-------------------------------------------------------
    /**
     * 直播
     */
    public static final String GET_COURSRINFO_LIVE_URL = "https://open.talk-fun.com/live/mobile/scanQrcode.php";
    public static final String GET_COURSRINFO_LIVE_PARAM = "id=%s&type=%d&sign=%s&mode=%d";
    //免密码登录
    public static final String GET_COURSRINFO_LIVE_AUTOMATIC_PARAM = "id=%s&sign=%s";
    /**
     * 点播
     */
    public static final String GET_COURSRINFO_PLAYBACK_URL = "https://open.talk-fun.com/live/mobile/scanQrcode.php";
    public static final String GET_COURSRINFO_PLAYBACK_PARAM = "id=%s&type=%d&sign=%s&mode=%d";
    //-----------------------------------------通过扫码获取课程信息----------end-----------------------------------------------------

    public static final String APPLY_FOR_URL = "https://www.talk-fun.com/static/mobile/mb_apply_try.html";
    public static final String ApplyInfoFile = "ApplyInfoFile";
    public static final String ApplyInfoContent = "content";

    /**
     * 播放模式
     */
    public static final int LIVE_MODE = 1; //直播模式
    public static final int PLAYBACK_MODE = 2; //点播模式

    public static final int ROOM_MODE = 1; //房间模式
    public static final int COURSE_MODE = 2; //课程模式

    /**
     * bugly app id
     */
    public static final String BUGLY_ID = "900042953";

    public static String PlayBackID;

    public static String getLiveLogInUrl(String id, String password, String nickname, int mode) {
        return String.format(LIVE_LOGIN, id, password, nickname, mode + "");
    }

    public static String getPlaybackLogInUrl(String id, String password, int mode) {
        return String.format(PLAY_BACK_LOGIN, id, password, mode + "");
    }
/*
    private static Boolean isConnected = false;

    public static void setIsConnected(boolean setConnected) {
        synchronized (isConnected) {
            isConnected = setConnected;
        }
    }

    public static boolean isConnected() {
        synchronized (isConnected) {
            return isConnected;
        }
    }*/

}
