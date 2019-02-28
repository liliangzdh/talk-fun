package com.talkfun.cloudlive.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.util.DimensionUtils;
import com.talkfun.cloudlive.util.SharedPreferencesUtil;


/**
 * 引导页弹框
 * Created by ccy on 2017/10/27.
 */

public class GuideDialog extends Dialog {
    CheckBox cbGuideTip;
    TextView btnGuideStart;
    private Context context;

    public GuideDialog(@NonNull Context context) {
        super(context, R.style.Dialog_Fullscreen);
        init(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth(); //设置dialog的宽度为当前手机屏幕的宽度
        getWindow().setAttributes(p);
    }

    public GuideDialog(@NonNull Context context, int themeResId) {
        super(context);
        init(context);
    }

    protected GuideDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }


    private void init(Context context) {
        this.context = context;
        View view = View.inflate(context, R.layout.layout_guide, null);
        cbGuideTip = (CheckBox) view.findViewById(R.id.cb_guide_tip);
        btnGuideStart = (TextView) view.findViewById(R.id.btn_guide_start);
        if (DimensionUtils.isPad(context)) {
            view.setBackgroundResource(R.mipmap.guide_ipad);
        } else {
            view.setBackgroundResource(R.mipmap.guide_mobile);
        }
        initEvent();
        super.setContentView(view);
    }

    private void initEvent() {
        btnGuideStart.setFocusable(true);
        btnGuideStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuideDialog.super.dismiss();
//                ScreenSwitchUtils.getInstance(context).isOpenSwitchAuto(true);
            }
        });
        cbGuideTip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.saveBoolean(context, SharedPreferencesUtil.SP_LIVEROOM_GUIDE, !isChecked);
            }
        });
    }

}
