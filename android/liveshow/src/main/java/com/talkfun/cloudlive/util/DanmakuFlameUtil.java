package com.talkfun.cloudlive.util;

import android.graphics.Color;
import android.text.SpannableString;


import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * Created by Wallace on 2016/11/15.
 */
public class DanmakuFlameUtil {


    private DanmakuView danmakuView;

    private DanmakuContext danmakuContext;

    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };
    private Random random;

    public DanmakuFlameUtil(DanmakuView danmakuView) {
        this.danmakuView = danmakuView;
        init();
    }

    /**初始化弹幕*/
    private void init() {
        random = new Random();
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                danmakuView.start();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        danmakuContext = DanmakuContext.create();
        danmakuView.prepare(parser, danmakuContext);
    }

    /**
     * 向弹幕View中添加一条弹幕
     *
     * @param content    弹幕的具体内容
     * @param withBorder 弹幕是否有边框
     */
    public void addDanmaku(SpannableString content, boolean withBorder) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = DimensionUtils.sp2px(danmakuView.getContext(), 16);
        danmaku.textColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        danmaku.setTime(danmakuView.getCurrentTime()+1200);
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        danmakuView.addDanmaku(danmaku);
    }

    /**隐藏弹幕*/
    public void hide(){
        danmakuView.hide();
    }

    /**显示弹幕*/
    public void show(){
        danmakuView.show();
    }

    /**
     * 弹幕View是否显示
     */
    public boolean isShown() {
        return danmakuView.isShown();
    }

    public void pause() {
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    public void resume() {
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    public void destroy() {
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }

}
