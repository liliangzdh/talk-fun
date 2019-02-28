package com.talkfun.cloudlive.helper;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.talkfun.cloudlive.consts.EventType;
import com.talkfun.cloudlive.dialog.LotteryDialogFragment;
import com.talkfun.cloudlive.entity.Event;
import com.talkfun.cloudlive.util.EventBusUtil;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.event.HtLotteryListener;
import com.talkfun.sdk.module.LotteryResult;

import java.lang.ref.WeakReference;

/**
 * 抽奖
 * Created by ccy on 2017/10/30.
 */

public class LiveLotteryDialogHelper implements HtLotteryListener {
    private Context context;
    private WeakReference<LotteryDialogFragment> weakReference;  // 抽奖

    public LiveLotteryDialogHelper(Context context) {
        this.context = context;


    }

    public void registerListener() {
        /**设置抽奖事件监听*/
        HtSdk.getInstance().setHtLotteryListener(this);

    }

    //抽奖开始
    @Override
    public void lotteryStart() {
        LotteryDialogFragment dialogFragment = null;
        if (weakReference != null) {
            dialogFragment = weakReference.get();
            if (dialogFragment != null)
                dialogFragment.dismiss();
        }
        dialogFragment = LotteryDialogFragment.create();
        weakReference = new WeakReference<>(dialogFragment);
        dialogFragment.lotteryStart();
        dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "lottery");
    }

    //抽奖结束
    @Override
    public void lotteryStop(LotteryResult lotteryResult) {
        LotteryDialogFragment dialogFragment = null;
        if (weakReference != null) {
            dialogFragment = weakReference.get();
            if (dialogFragment != null)
                dialogFragment.dismiss();
        }

        dialogFragment = LotteryDialogFragment.create();
        weakReference = new WeakReference<>(dialogFragment);
        dialogFragment.lotteryStop(lotteryResult);
        dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "lottery");
        //发送抽奖结束通知
        EventBusUtil.postEvent(new Event(EventType.INSERTCHAT, lotteryResult));
    }
}
