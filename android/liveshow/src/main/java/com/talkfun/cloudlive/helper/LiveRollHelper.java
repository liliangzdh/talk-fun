package com.talkfun.cloudlive.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.event.HtDispatchRollAnnounceListener;
import com.talkfun.sdk.module.RollEntity;

import java.lang.ref.WeakReference;

/**
 * 滚动通知
 * Created by ccy on 2017/10/21.
 */

public class LiveRollHelper implements HtDispatchRollAnnounceListener {
    private LinearLayout rollLayout;
    private LinearLayout roll;
    private TextView rollContentHolder;
    private ImageView stopRollIv;
    private WeakReference<Context> weakReference;
    private boolean isInitRollingLayout = false;
    private RollEntity rollEntity = null;
    private ViewGroup pptLayout;
    private final int SPEED = 5;//移动一个像素要5毫秒

    public LiveRollHelper(Context context, ViewGroup layout) {
        this.weakReference = new WeakReference<>(context);
        this.pptLayout = layout;
//        initRollinglayout();
        initEvent();

    }

    private void initEvent() {
        /**设置获取滚动通知事件监听*/
        HtSdk.getInstance().setHtDispatchRollAnnounceListener(this);
    }

    /**
     * 初始化滚动通知的布局
     */
    private void initRollinglayout() {
        if (weakReference == null && weakReference.get() == null) {
            return;
        }
        View view = LayoutInflater.from(weakReference.get()).inflate(R.layout.rolling_layout, null);
        rollLayout = (LinearLayout) view.findViewById(R.id.roll_layout);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rollLayout.setLayoutParams(lp);
        roll = (LinearLayout) view.findViewById(R.id.roll);
        rollContentHolder = (TextView) view.findViewById(R.id.roll_inner_tv);
        stopRollIv = (ImageView) view.findViewById(R.id.stop_roll);
        stopRollIv.setOnClickListener(rollClick);
        rollLayout.setOnClickListener(rollClick);
        if (pptLayout != null) {
            pptLayout.removeView(rollLayout);
            pptLayout.addView(rollLayout);
        }
        isInitRollingLayout = true;
    }

    /**
     * 初始化滚动通知
     *
     * @param roll
     */
    private void initRollAnnounce(RollEntity roll) {
        if (roll != null)
            this.rollEntity = roll;
    }

    /**
     * 显示滚动通知
     */
    private void showRollAnnounce() {
        if (rollEntity != null && !TextUtils.isEmpty(rollEntity.getContent())) {
            rollContentHolder.setText(rollEntity.getContent());
            rollContentHolder.post(new Runnable() {
                @Override
                public void run() {
                    TranslateAnimation translateAnimation = new TranslateAnimation(roll.getWidth(),
                            -rollContentHolder.getWidth(), 0, 0);
                    translateAnimation.setInterpolator(new LinearInterpolator());
                    translateAnimation.setDuration(roll.getWidth() * SPEED);
                    translateAnimation.setRepeatCount(Animation.INFINITE);
                    translateAnimation.setRepeatMode(Animation.RESTART);
                    rollContentHolder.startAnimation(translateAnimation);
                    if (rollEntity.getDuration() > 0)
                        autoDismissRollAnnounceBar(rollEntity.getDuration());
                }
            });
        }
    }

    private CountDownTimer rollAnnounceTimer;

    /**
     * 自动隐藏滚动通知栏
     *
     * @param duration
     */
    private void autoDismissRollAnnounceBar(long duration) {
        if (rollAnnounceTimer != null)
            rollAnnounceTimer.cancel();
        duration *= 1000;
        rollAnnounceTimer = new CountDownTimer(duration, duration) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (rollLayout != null) {
                    rollLayout.setVisibility(View.GONE);
                    rollContentHolder.clearAnimation();
                    rollAnnounceTimer = null;
                }
            }
        };
        rollAnnounceTimer.start();
    }

    /**
     * 滚动通知点击事件监听
     */
    private View.OnClickListener rollClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R2.id.stop_roll:
                    rollLayout.setVisibility(View.GONE);
                    break;
                case R2.id.roll_layout:
                    if (!TextUtils.isEmpty(rollEntity.getLink())) {
                        //Toast.makeText(getApplicationContext(), rollEntity.getLink(), Toast.LENGTH_SHORT).show();
                        //跳转到浏览器打开
                        Uri uri = Uri.parse(rollEntity.getLink());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (weakReference == null && weakReference.get() == null) {
                            return;
                        }
                        weakReference.get().startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void cancel() {
        if (rollAnnounceTimer != null) {
            rollAnnounceTimer.cancel();
            if (rollLayout != null) {
                rollLayout.setVisibility(View.GONE);
            }
        }
    }

    //接受滚动通知
    @Override
    public void receiveRollAnnounce(RollEntity rollEntity) {
        if (rollEntity == null)
            return;
        if (!isInitRollingLayout) {
            initRollinglayout();
        }
        initRollAnnounce(rollEntity);
        rollLayout.setVisibility(View.VISIBLE);
        showRollAnnounce();
    }
}
