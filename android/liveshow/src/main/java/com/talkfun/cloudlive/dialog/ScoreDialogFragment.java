package com.talkfun.cloudlive.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.event.Callback;

import butterknife.BindView;
import butterknife.OnClick;

public class ScoreDialogFragment extends BaseDialog implements RatingBar.OnRatingBarChangeListener,Callback<Void> {
    @BindView(R2.id.rating_bar_content)
    RatingBar rbContent;
    @BindView(R2.id.rating_bar_method)
    RatingBar rbMethod;
    @BindView(R2.id.rating_bar_effect)
    RatingBar rbEffect;
    @BindView(R2.id.et_msg)
    EditText etMsg;

    private static final int MAX_CONTENT_SCORE = 30;
    private static final int MAX_METHOD_SCORE = 30;
    private static final int MAX_EFFECT_SCORE = 40;

    public static ScoreDialogFragment create() {
        ScoreDialogFragment dialog = new ScoreDialogFragment();
      /*  Bundle bundle = new Bundle();
        bundle.putSerializable(DATA_TAG, signEntity);
        bundle.putSerializable(CALLBACK, callback);
        signDialogFragment.setArguments(bundle);*/
        return dialog;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.dialog_score;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    /**
     * 初始化控件
     */
    protected void initView() {
        super.initView();
        icon.setImageDrawable(getResources().getDrawable(R.mipmap.score_icon));
        title.setVisibility(View.VISIBLE);
        title.setText("评分");

        rbContent.setOnRatingBarChangeListener(this);
        rbMethod.setOnRatingBarChangeListener(this);
        rbEffect.setOnRatingBarChangeListener(this);
    }

    @OnClick({R2.id.btn_submit})
    void onClickHandle(View v){
        if (v.getId() == R2.id.btn_submit){
            sendScore();
        }
    }

    private void sendScore(){
        int contentScore = (int)(rbContent.getRating() * MAX_CONTENT_SCORE / rbContent.getNumStars());
        int methodScore = (int)(rbMethod.getRating() * MAX_METHOD_SCORE / rbMethod.getNumStars());
        int effectScore = (int)(rbEffect.getRating() * MAX_EFFECT_SCORE / rbEffect.getNumStars());
        String msg = etMsg.getText().toString();
        HtSdk.getInstance().sendScore(contentScore,methodScore,effectScore,msg,this);
    }


    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if(rating < 1.0f){
            ratingBar.setRating(1.0f);
        }
    }

    @Override
    public void success(Void result) {
        Context context = getContext();
        if(context != null){
            Toast.makeText(context, "发送评分成功", Toast.LENGTH_SHORT).show();
        }
        this.dismissAllowingStateLoss();
    }

    @Override
    public void failed(String failed) {
        Context context = getContext();
        if(context == null)
            return;
        Toast.makeText(context, failed, Toast.LENGTH_SHORT).show();
    }
}
