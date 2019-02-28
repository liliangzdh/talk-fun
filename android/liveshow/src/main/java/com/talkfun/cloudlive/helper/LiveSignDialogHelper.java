package com.talkfun.cloudlive.helper;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.talkfun.cloudlive.dialog.SignDialogFragment;
import com.talkfun.sdk.event.Callback;
import com.talkfun.cloudlive.entity.SignEntity;

import java.lang.ref.WeakReference;

/**
 * 签名
 * Created by ccy on 2017/11/06
 */

public class LiveSignDialogHelper {
    private Context context;
    private WeakReference<SignDialogFragment> signDialogFragment;  //签名
    private String TAG = "LiveSignDialogHelper";
    private Callback callBack;

    public LiveSignDialogHelper(Context context) {
        this.context = context;
    }

    /**
     * 点名开始
     *
     * @param signEntity
     */
    public void signStart(SignEntity signEntity) {
        if (signEntity == null)
            return;
        signDialogFragment = new WeakReference<>(SignDialogFragment.create(signEntity, callBack));
        signDialogFragment.get().show(((FragmentActivity) context).getSupportFragmentManager(), "sign");
    }

    /**
     * 点名结束
     */
    public void signStop() {
        if (signDialogFragment == null) return;
        signDialogFragment.get().dismiss();
    }

    /**
     * 签到回调
     *
     * @param callBack
     */
    public void setOnSignInCallBack(Callback callBack) {
        this.callBack = callBack;
    }
}
