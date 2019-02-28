package com.talkfun.cloudlive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.imageload.GlideImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginJumpActivity extends BaseActivity {
    @BindView(R2.id.iv_logo)
    ImageView ivLogo;
    @BindView(R2.id.tv_course_name)
    TextView tvCourseName;
    private String logoUrl;
    private String token;
    private String title;
    private int type;
    private String id;

    public static final String TOKEN_PARAM = "token";
    public static final String LOG0_PARAM = "logo";
    public static final String TITLE_PARAM = "title";
    public static final String TYPE_PARAM = "type";
    public static final String ID_PARAM = "id";
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_jump);
        ButterKnife.bind(this);
        init();
        initView();
        initEvent();
    }

    private void init() {
        Intent intent = getIntent();
        logoUrl = intent.getStringExtra(LOG0_PARAM);
        token = intent.getStringExtra(TOKEN_PARAM);
        title = intent.getStringExtra(TITLE_PARAM);
        type = intent.getIntExtra(TYPE_PARAM, 4);
        id = intent.getStringExtra(ID_PARAM);
    }

    private void initView() {
        if (!TextUtils.isEmpty(logoUrl)) {
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.centerCrop();
//            requestOptions.placeholder(R.mipmap.huan_tuo_icon);
//            Glide.with(this).load(logoUrl).apply(requestOptions).into(ivLogo);
            GlideImageLoader.create(ivLogo).loadImage(logoUrl, R.mipmap.huan_tuo_icon);
        }
        if (!TextUtils.isEmpty(title))
            tvCourseName.setText(title);
    }

    private void initEvent() {
        handler.postDelayed(myRunnale, 2000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (handler != null && myRunnale != null) {
            handler.removeCallbacks(myRunnale);
        }
    }

    private Runnable myRunnale = new Runnable() {
        @Override
        public void run() {
            Intent intent = null;
            if (type == LoginActivity.LIVE_TYPE)
                intent = new Intent(LoginJumpActivity.this, LiveNativeActivity.class);
            else
                intent = new Intent(LoginJumpActivity.this, PlaybackNativeActivity.class);
            intent.putExtra(TOKEN_PARAM, token);
            intent.putExtra(ID_PARAM, id);
            startActivity(intent);
            finish();


        }
    };
}
