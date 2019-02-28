package com.talkfun.cloudlive.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.consts.EventType;
import com.talkfun.cloudlive.entity.Event;
import com.talkfun.cloudlive.util.EventBusUtil;
import com.talkfun.utils.checkNetUtil.CheckNetSpeed;

/**
 * 网络监察及弹框帮助类
 * Created by ccy on 2017/10/24.
 */

public class LiveNetHelper {
    private String TAG = "LiveNetHelper";

    private Context context;
    private PopupWindow badNetPopupWindow;
    private static final String POOR_STATE = "POOR";
    private TextView tvBadNetTip;


    public LiveNetHelper(Context context) {
        this.context = context;
        initPopWindow(context);
    }

    public void initPopWindow(Context context) {
        tvBadNetTip = (TextView) View.inflate(context, R.layout.popupview_badnet_layout, null);
        badNetPopupWindow = new PopupWindow(tvBadNetTip, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        badNetPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        badNetPopupWindow.setOutsideTouchable(true);
        tvBadNetTip.setVisibility(View.INVISIBLE);
    }

    //TODO----------------------------------------------网络检测--------------------------------------------

    /**
     * 开始网络检测
     */
    private String currentState = "";

    public void startCheckNetStatus(final ImageView ivNetWorkChoice) {
        if (badNetPopupWindow == null) {
            return;
        }
        badNetPopupWindow.showAsDropDown(ivNetWorkChoice, 0, 0);
        CheckNetSpeed.getInstance().startCheckNetSpeed(new CheckNetSpeed.OnNetSpeedChangeListener() {
            @Override
            public void getNetSpeedAndState(int speed, String netState) {
                boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
                if (netState.equals(POOR_STATE) && !netState.equals(currentState)) {
                    EventBusUtil.postEvent(new Event(EventType.SHOWTITLEBAR));
                    if (ivNetWorkChoice == null && ivNetWorkChoice.getVisibility() == View.GONE && badNetPopupWindow != null && !badNetPopupWindow.isShowing()) {
                        tvBadNetTip.setSelected(!isPortrait);
                        tvBadNetTip.setVisibility(View.VISIBLE);
                        badNetPopupWindow.showAsDropDown(ivNetWorkChoice, -tvBadNetTip.getWidth() / 2 + ivNetWorkChoice.getWidth() / 2, isPortrait == true ? 0 : -ivNetWorkChoice.getHeight() - tvBadNetTip.getHeight());
                        badNetPopupWindow.update();
                    }
                }
                currentState = netState;
            }
        });
    }

    /**
     * 停止网络检测
     */
    public void stopCheckNetStatus() {
        CheckNetSpeed.getInstance().stopCheckNetSpeed();
    }

    /**
     * 销毁网络提示弹窗
     */
    public void dismissPop() {
        if (badNetPopupWindow != null && badNetPopupWindow.isShowing()) {
            badNetPopupWindow.dismiss();
        }
    }


}
