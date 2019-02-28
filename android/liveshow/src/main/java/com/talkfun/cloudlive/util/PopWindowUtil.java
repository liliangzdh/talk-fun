package com.talkfun.cloudlive.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.talkfun.cloudlive.R;

import static android.os.Looper.getMainLooper;

/**
 * 提示框工具类
 * Created by ccy on 2017/10/20.
 */

public class PopWindowUtil {
    private PopupWindow popupWindow;
    private Handler handler;
    private TextView tvDownloadTip;
    private Runnable task;
    private long delayTime = 3000L;
    private Context context;

    public PopWindowUtil(Context context) {
        this.context = context;
    }

//    private static PopWindowUtil instance;

//    public static PopWindowUtil getInstance() {
//        if (instance == null) {
//            synchronized (PopWindowUtil.class) {
//                if (instance == null) {
//                    instance = new PopWindowUtil();
//                }
//            }
//        }
//        return instance;
//    }


    /**
     * 显示延迟性质pop
     *
     * @param content
     * @param view
     */
    public void showDelayPop(final String content, final View view) {
        if (popupWindow == null && context != null) {
            handler = new Handler(getMainLooper());
            tvDownloadTip = new TextView(context);
            tvDownloadTip.setTextColor(Color.WHITE);
            tvDownloadTip.setGravity(Gravity.CENTER);
            tvDownloadTip.setPadding(DimensionUtils.dip2px(context, 5), DimensionUtils.dip2px(context, 5), DimensionUtils.dip2px(context, 5), DimensionUtils.dip2px(context, 5));
            tvDownloadTip.setBackgroundResource(R.drawable.downloadpop_bg);
            popupWindow = new PopupWindow(tvDownloadTip, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setOutsideTouchable(false);
        }
        tvDownloadTip.setText(content);
        if (task == null) {
            task = new Runnable() {
                @Override
                public void run() {
                    if (popupWindow != null && popupWindow.isShowing() && !((Activity) context).isFinishing())
                        popupWindow.dismiss();
                }
            };
        }
        if (handler != null && !popupWindow.isShowing() && !((Activity) context).isFinishing()) {
            popupWindow.showAsDropDown(view);
            handler.removeCallbacks(task);
            handler.postDelayed(task, delayTime);  //提示停留三秒
        }
    }

    /**
     * 解除pop
     */
    public void dismissDelayPop() {
        if (handler != null) {
            if (task != null) {
                handler.removeCallbacks(task);
            }
        }
        if (popupWindow != null && popupWindow.isShowing() && !((Activity) context).isFinishing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * 设置延迟时间
     *
     * @param delayTime
     */
    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

}
