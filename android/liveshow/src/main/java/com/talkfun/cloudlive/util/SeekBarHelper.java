package com.talkfun.cloudlive.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.module.PlaybackInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wallace on 2017/1/22.
 * <p/>
 * 快进快退
 */
public class SeekBarHelper {
    private TextView forwardDetailTime;
    private TextView forwardTime;
    private ImageView ivForward;
    private PopupWindow forwardPopup;
    public int deviceWidth; //设备宽度
    private int seekBarProgress = 0;
    private static final int seconds = 250;//固定秒数
    float ratioSeconds;  //滑动的秒数
    float downX = 0;

    private SeekBar mSeekBar;
    private Context mContext;
    private int mPopWidth;  //弹窗宽
    private int mPopHeight;  //弹窗高
    private static float touchSlop = 10.0f;

    public boolean isShowPoped = false;

    public SeekBarHelper(Context context, SeekBar seekBar) {
        deviceWidth = DimensionUtils.getScreenWidth(context);
        this.mSeekBar = seekBar;
        mContext = context;
        mPopWidth = DimensionUtils.dip2px(mContext, 160);
        mPopHeight = DimensionUtils.dip2px(mContext, 100);
        //ViewConfiguration.get(getContext().getScaledTouchSlop())

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        initPopWindow();
    }

    /**
     * 初始化快进快退的弹窗popupWindow
     */
    private void initPopWindow() {
        View forwardView = View.inflate(mContext, R.layout.popupwindow_forward, null);
        forwardDetailTime = (TextView) forwardView.findViewById(R.id.tv_forwardDetailTime);
        forwardTime = (TextView) forwardView.findViewById(R.id.tv_forwardTime);
        ivForward = (ImageView) forwardView.findViewById(R.id.iv_forward);
        forwardPopup = new PopupWindow(forwardView, mPopWidth, mPopHeight);
        forwardPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        forwardPopup.setOutsideTouchable(false);
    }


    /**
     * 添加滑动监听事件
     *
     * @param pptLayout
     */
    public void addTouchSlidSeekEvent(final ViewGroup pptLayout) {
        pptLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getX();

                        if (forwardPopup == null || (!forwardPopup.isShowing() && Math.abs(moveX - downX) < touchSlop))
                            return false;
                        ratioSeconds = (moveX - downX) * 1.0f / deviceWidth * seconds;
                        if (!forwardPopup.isShowing()) {
                            isShowPoped = true;
                            if (ScreenSwitchUtils.getInstance(mContext).isPortrait()) {  //竖屏
                                forwardPopup.showAsDropDown(pptLayout, (pptLayout.getWidth() - mPopWidth) / 2,
                                        -(pptLayout.getWidth() / 4 * 3 + mPopHeight) / 2);
                            } else {   //横屏拿宽的比例求高又误差，所以要直接拿高
                                forwardPopup.showAsDropDown(pptLayout, (pptLayout.getWidth() - mPopWidth) / 2,
                                        -(pptLayout.getHeight() + mPopHeight) / 2);
                            }
                        }
                        int currentProgress = (int) (mSeekBar.getProgress() + ratioSeconds);
                        currentProgress = currentProgress < 0 ? 0 : currentProgress;
                        currentProgress = currentProgress >= mSeekBar.getMax() ? mSeekBar.getMax() : currentProgress;
                        forwardDetailTime.setText(TimeUtil.displayHHMMSS(currentProgress) + "/" + TimeUtil.displayHHMMSS(mSeekBar.getMax()));
                        if (currentProgress > 0) {
                            if (ratioSeconds >= 0) {
                                ivForward.setSelected(false);
                                if (currentProgress < mSeekBar.getMax()) {
                                    forwardTime.setText("+" + (int) +(ratioSeconds) + "秒");
                                }
                            } else {
                                forwardTime.setText((int) +(ratioSeconds) + "秒");
                                ivForward.setSelected(true);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (forwardPopup != null & forwardPopup.isShowing()) {
                            forwardPopup.dismiss();

                            float upX = event.getX();
                            ratioSeconds = (upX - downX) * 1.0f / deviceWidth * seconds;
                            int progress = (int) (mSeekBar.getProgress() + ratioSeconds);
                            progress = progress > 0 ? progress : 0;
                            if (Math.abs(seekBarProgress - progress) > 10) {
                                mSeekBar.setProgress(progress);
                                seekTo(progress);
                            }
                            forwardTime.setText("-0秒");
                        }
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 跳转到某个时间点
     *
     * @param progress
     */
    public void seekTo(long progress) {
        HtSdk.getInstance().playbackSeekTo(progress);
        seekBarProgress = (int) progress;
        Log.d("seek", "seek time:" + seekBarProgress);
        updateSeekBar();
    }


    /**
     * 重置
     */
    public void resetSeekBarProgress() {
        seekBarProgress = 0;
        if (mSeekBar != null)
            mSeekBar.setProgress(seekBarProgress);
    }


    //------------------------------------------定时更新进度条---------------------------------------
    /**
     * 周期执行线程池
     */
    private ScheduledExecutorService scheduledPool;
    private ScheduledFuture<?> loadingHandle;

    /**
     * 周期计时更新进度条进度
     */
    public void updateSeekBar() {
        if (scheduledPool != null && !scheduledPool.isShutdown()) {
            /**停止上一次周期计时执行*/
            stopUpdateSeekBar();
        }
        Runnable updateSeekBar = new Runnable() {
            @Override
            public void run() {
                /**获得当前已播放秒数*/
                int currentTime = (int) HtSdk.getInstance().getVideoCurrentTime();
                /**更新进度条进度*/
                if (seekBarProgress >= 0 && currentTime >= seekBarProgress) {
                    seekBarProgress = currentTime;
                    //Log.d("seek", "seekBarProgress:" + seekBarProgress);
                    if (seekBarProgress >= 0 && seekBarProgress <= Integer.valueOf(PlaybackInfo.getInstance().getDuration())) {
                        //Log.d("seek", "setProgress:" + seekBarProgress);
                        mSeekBar.setProgress(seekBarProgress);
                    }
                }
            }
        };
        /**获取线程池，周期执行更新进度条进度*/
        if (scheduledPool == null) {
            scheduledPool = Executors.newSingleThreadScheduledExecutor();
        }
        loadingHandle = scheduledPool.scheduleAtFixedRate(updateSeekBar, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * 停止进度条进度更新
     */
    public void stopUpdateSeekBar() {
        if (loadingHandle != null)
            loadingHandle.cancel(true);
        loadingHandle = null;
        //scheduledPool = null;
    }
}
