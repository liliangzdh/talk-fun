package com.talkfun.cloudlive.dialog;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.consts.BroadcastCmdType;
import com.talkfun.sdk.consts.MtConsts;
import com.talkfun.sdk.event.Callback;
import com.talkfun.cloudlive.entity.SignEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 签到dialog
 */
public class SignDialogFragment extends BaseDialog {
    private SignEntity signEntity;
    private static final String DATA_TAG = "sign";
    private static final String CALLBACK = "callback";
    private Button btnCommit;
    private TextView tvCountDownTime;
    private CountDownTimer mCountDownTimer;
    private int duration;
    private Callback callback;

    public static SignDialogFragment create(SignEntity signEntity, Callback callback) {
        SignDialogFragment signDialogFragment = new SignDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA_TAG, signEntity);
        bundle.putSerializable(CALLBACK, callback);
        signDialogFragment.setArguments(bundle);
        return signDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        this.signEntity = (SignEntity) bundle.getSerializable(DATA_TAG);
        this.callback = (Callback) bundle.getSerializable(CALLBACK);
        setStyle(STYLE_NO_FRAME, R.style.htVoteStyle);
        setCancelable(false);

    }

    @Override
    protected void initEvent() {
        super.initEvent();
        btnCommit.setOnClickListener(clickListener);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.ht_sign_layout;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initView() {
        super.initView();
        icon.setImageDrawable(getResources().getDrawable(R.mipmap.sign));
        title.setVisibility(View.VISIBLE);
        title.setText("点名开始");
        tvCountDownTime = (TextView) layout.findViewById(R.id.tv_count_down_time);
        btnCommit = (Button) layout.findViewById(R.id.sign);
        initData();

    }

    public void initData() {
        if (signEntity == null) return;
        duration = signEntity.getDuration();
        String countDownTime = "倒计时:<font color='#FF0000'><small>" + duration + "</small></font>秒";
        tvCountDownTime.setText(Html.fromHtml(countDownTime));
        mCountDownTimer = new CountDownTimer(duration * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String countDownTime = "倒计时:<font color='#FF0000'>" + ((millisUntilFinished + 15) / 1000) + "</font>秒";
                tvCountDownTime.setText(Html.fromHtml(countDownTime));
            }

            @Override
            public void onFinish() {
                dismiss();
            }
        };
        mCountDownTimer.start();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
//        mCountDownTimer.cancel();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    //点击事件
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R2.id.sign){
                btnCommit.setEnabled(false);
                sendSign();
            }
        }
    };

    //发送点名
    private void sendSign() {
        dismiss();
        if (signEntity != null) {
            if (!TextUtils.isEmpty(signEntity.getSignId())) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put(MtConsts.KEY_SIGN_ID, signEntity.getSignId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HtSdk.getInstance().emit(BroadcastCmdType.SIGN_IN, obj, new Callback() {
                    @Override
                    public void success(Object result) {
                        if (callback != null) {
                            callback.success(result);
                        }
                    }

                    @Override
                    public void failed(String failed) {
                        if (getActivity() == null) return;
                        if (callback != null) {
                            callback.failed(failed);
                        }
                        if (mCountDownTimer != null) {
                            mCountDownTimer.cancel();
                        }
                    }
                });
            }
        }
    }
}
