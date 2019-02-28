package com.talkfun.cloudlive.util;

import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {

    public static void tip(Context context , int contentId){
        Toast.makeText(context , getString(context ,contentId ) , Toast.LENGTH_SHORT).show();
    }

    public static void tip(Context context , String content){
        Toast.makeText(context , content , Toast.LENGTH_SHORT).show();
    }

    private static String getString(Context context , int id){
        return context.getResources().getString(id);
    }

    /**
     *  格式化时间
     *  yyyy-MM-dd HH:mm:ss
     * @param date 需要格式化的时间
     * @return  时间的字符串形式
     */
    public static String DataToString(Date date){
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
        return sdformat.format(date);
    }

    /**
     *  当前的时间
     *   yyyy-MM-dd HH:mm:ss
     * @return 当前时间的字符串
     */
    public static String getCurrentTime(){
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
        return sdformat.format(System.currentTimeMillis());
    }


}
