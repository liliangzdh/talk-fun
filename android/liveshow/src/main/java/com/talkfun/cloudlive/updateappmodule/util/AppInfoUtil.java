package com.talkfun.cloudlive.updateappmodule.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.talkfun.cloudlive.updateappmodule.VersionInfo;


public class AppInfoUtil {

    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return verCode;
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            //  Log.e(TAG, e.getMessage());
        }
        return verName;
    }

    /**
     * 获取本地缓存版本的信息
     *
     * @return
     */
    public static int getLocalCacheAppVersionCode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                UpdateManagerConst.CacheAppInfoFileName,
                Context.MODE_PRIVATE);
        return sharedPreferences.getInt(VersionInfo.VersionCode, -1);
    }

    /**
     *  获取apk信息
     * @param context
     * @param apkPath apk路径
     * @return
     */
    public static ApplicationInfo getApplicationInfoByApk(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        return info.applicationInfo;
    }

    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getSystemSdkVersion() {
        return Build.VERSION.SDK_INT + "";
    }

    public static String getMobile() {
        return Build.MODEL;
    }
}
