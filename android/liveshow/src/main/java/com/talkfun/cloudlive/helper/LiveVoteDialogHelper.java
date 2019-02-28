package com.talkfun.cloudlive.helper;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.talkfun.cloudlive.consts.EventType;
import com.talkfun.cloudlive.dialog.VoteDialogFragment;
import com.talkfun.cloudlive.dialog.VoteResultDialogFragment;
import com.talkfun.cloudlive.dialog.VoteSuccessDialogFragment;
import com.talkfun.cloudlive.entity.Event;
import com.talkfun.cloudlive.util.EventBusUtil;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.event.HtVoteListener;
import com.talkfun.sdk.module.VoteEntity;
import com.talkfun.sdk.module.VotePubEntity;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * 投票
 * Created by ccy on 2017/10/21.
 */

public class LiveVoteDialogHelper implements HtVoteListener {
    private WeakReference<Context> contextWeakReference;
    private WeakReference<VoteDialogFragment> voteDialogFragment;  //投票
    private WeakReference<VoteResultDialogFragment> voteResultDialogFragment;  //投票结果
    private static LiveVoteDialogHelper instance;
    private HashMap<String, VoteEntity> voteMap = new HashMap<>();
    private HashMap<String, VotePubEntity> votePubMap = new HashMap<>();

    public LiveVoteDialogHelper(Context context) {
        this.contextWeakReference = new WeakReference<>(context);
    }

//    public static LiveVoteDialogHelper getInstance(Context context) {
//        if (instance == null) {
//            synchronized (LiveVoteDialogHelper.class) {
//                if (instance == null) {
//                    instance = new LiveVoteDialogHelper(context);
//                }
//            }
//        }
//        return instance;
//
//    }

    public void registerListener() {
        /**设置投票事件监听*/
        HtSdk.getInstance().setHtVoteListener(this);
    }
    //投票开始
    @Override
    public void voteStart(VoteEntity voteEntity) {
        if (voteEntity == null)
            return;
        String voteId = voteEntity.getVid();
        boolean voteEnd = false;
        if (!TextUtils.isEmpty(voteId)) {
            if (!voteMap.containsKey(voteId)) {
                voteMap.put(voteId, voteEntity);
                EventBusUtil.postEvent(new Event(EventType.INSERTCHAT, voteEntity));
            }else{
                voteEnd = votePubMap.containsKey(voteId);
            }
        }

        boolean isSingleChoice = !(voteEntity.getOptional() > 1);
        resetVoteFragment();
        voteDialogFragment = new WeakReference<>(VoteDialogFragment.create(voteEntity, isSingleChoice,voteEnd, mCallback));
        if (contextWeakReference.get() != null)
            voteDialogFragment.get().show(((FragmentActivity) contextWeakReference.get()).getSupportFragmentManager(), "vote");
    }

    //投票结束
    @Override
    public void voteStop(VotePubEntity votePubEntity) {
        if (votePubEntity == null || votePubEntity.getIsShow() == 0)
            return;
        String voteId = votePubEntity.getVid();
        if (!TextUtils.isEmpty(voteId)) {
            if (!votePubMap.containsKey(voteId)) {
                votePubMap.put(voteId, votePubEntity);
                EventBusUtil.postEvent(new Event(EventType.INSERTCHAT, votePubEntity));
            }

          /*  if(voteMap.containsKey(voteId)){
                VoteEntity voteEntity = voteMap.get(voteId);
                voteEntity.setVoted(true);
            }*/
        }
        resetVoteFragment();
        voteResultDialogFragment = new WeakReference<>(VoteResultDialogFragment.create(votePubEntity));
        if (contextWeakReference.get() != null)
            voteResultDialogFragment.get().show(((FragmentActivity) contextWeakReference.get()).getSupportFragmentManager(), "vote_success");
    }

    //重置投票
    private void resetVoteFragment() {
        if (voteDialogFragment != null && voteDialogFragment.get() != null && voteDialogFragment.get().isVisible()) {
            voteDialogFragment.get().dismissAllowingStateLoss();
        }
        if (voteResultDialogFragment != null && voteResultDialogFragment.get() != null && voteResultDialogFragment.get().isVisible())
            voteResultDialogFragment.get().dismissAllowingStateLoss();

    }

    //投票回调
    private Callback mCallback = new Callback() {
        @Override
        public void success(Object result) {
            VoteSuccessDialogFragment voteSuccess = new VoteSuccessDialogFragment();
            if (contextWeakReference.get() != null)
                voteSuccess.show(((FragmentActivity) contextWeakReference.get()).getSupportFragmentManager(), "vote_success");
        }

        @Override
        public void failed(String failed) {
            try {
                String content = URLEncoder.encode(failed, "utf-8");
                if (contextWeakReference.get() != null)
                    Toast.makeText(contextWeakReference.get(), content, Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };


}
