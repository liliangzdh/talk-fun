package com.talkfun.cloudlive.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

/**
 * 版本相关
 */
public class AppUtil {

    public static int getVerCode(Context context) {
        int verCode = -1;

        try {
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            // Log.e(TAG, e.getMessage());
        }
        return verCode;
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            //  Log.e(TAG, e.getMessage());
        }
        return verName;
    }

    public static String getSystemVersion(){
        return Build.VERSION.RELEASE;
    }

    public static String getSystemSdkVersion(){
        return Build.VERSION.SDK_INT +"";
    }

    public static String getMobile(){
        return Build.MODEL ;
    }
}
