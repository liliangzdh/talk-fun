package com.talkfun.cloudlive.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.talkfun.cloudlive.R;


public class VoteSuccessDialogFragment extends BaseDialogFragment implements View.OnClickListener {
    private TextView voteStatus;
    private ImageView cancel;
    private TextView voteSuccessTip;
    private CountDownTimer countDownTimer;
    private int time = 3;
    private String tips;
    private Resources res;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.htVoteStyle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Context context = mContext.get();
        if (context != null) {
            res = context.getResources();
            View layout = inflater.inflate(R.layout.ht_vote_success_layout, container);
            initView(layout);
            initViewEvent();
            tips = res.getString(R.string.ht_vote_success) +
                    "...%s" +
                    res.getString(R.string.ht_vote_success_tip);
            return layout;
        } else {
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        time = 4;
        countDownTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                voteSuccessTip.setText(String.format(tips, millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                dismiss();
            }
        };
        countDownTimer.start();
    }


    /**
     * 初始化控件
     *
     * @param layout
     */
    private void initView(View layout) {
        voteStatus = (TextView) layout.findViewById(R.id.tv_title);
        cancel = (ImageView) layout.findViewById(R.id.cancel);
        voteSuccessTip = (TextView) layout.findViewById(R.id.vote_success_countdown);
    }

    private void initViewEvent() {
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }


}
