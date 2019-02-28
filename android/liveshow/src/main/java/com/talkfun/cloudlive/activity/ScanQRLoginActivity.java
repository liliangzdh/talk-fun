package com.talkfun.cloudlive.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.bean.ScanQRLoginBean;
import com.talkfun.cloudlive.consts.MainConsts;
import com.talkfun.cloudlive.imageload.GlideImageLoader;
import com.talkfun.cloudlive.net.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.talkfun.cloudlive.activity.ScanQRCodeActivity.LIVELOGIN;
import static com.talkfun.cloudlive.activity.ScanQRCodeActivity.PLAYBACKLOGIN;
import static com.talkfun.cloudlive.activity.ScanQRCodeActivity.TEMPLOGIN;


public class ScanQRLoginActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.login_password_layout)
    LinearLayout llPasswordLayout;
    @BindView(R2.id.login_password_label)
    TextView tvPasswordLabel;
    @BindView(R2.id.login_password_edit)
    EditText etPasswordEdit;
    @BindView(R2.id.login_password_hint_tv)
    TextView tvPasswordHint;
    @BindView(R2.id.tv_error_tip)
    TextView tvErrorTip;
    @BindView(R2.id.tv_course_name)
    TextView tvCourseName;
    @BindView(R2.id.ll_nickname_layout)
    LinearLayout llNicknameLayout;
    @BindView(R2.id.ed_nickname_edit)
    EditText edNicknameEdit;
    @BindView(R2.id.tv_nickname)
    TextView tvNickname;
    @BindView(R2.id.iv_logo)
    ImageView ivLogo;
    private String logoUrl;
    private String title;
    private int type;
    private String id;

    public static final String LOG0_PARAM = "logo";
    public static final String TITLE_PARAM = "title";
    public static final String TYPE_PARAM = "type";
    public static final String ID_PARAM = "id";

    private String params;
    private String url;
    private String loginType;
    private ScanQRLoginBean qrBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_qr_login);
        ButterKnife.bind(this);
        getIntentData();
        initView();
        initEvent();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        qrBean = (ScanQRLoginBean) intent.getSerializableExtra(ScanQRCodeActivity.QRBEAN);
        if (qrBean == null && TextUtils.isEmpty(qrBean.getType())) {
            return;
        }
        loginType = qrBean.getType();

    }

    private void initView() {
        tvCourseName.setVisibility(View.VISIBLE);
        toolbar.setTitle("扫一扫");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.mipmap.go_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!TextUtils.isEmpty(title))
            tvCourseName.setText(title);
        if (!TextUtils.isEmpty(logoUrl))
            GlideImageLoader.create(ivLogo).loadImage(logoUrl, R.mipmap.huan_tuo_icon);
//            Glide.with(this).load(logoUrl).apply(new RequestOptions().placeholder(R.mipmap.huan_tuo_icon)).into(ivLogo);
        showView();
//        if (ScanQRCodeActivity.loginType == ScanQRCodeActivity.LIVELOGIN) {
//            llNicknameLayout.setVisibility(View.VISIBLE);
//        } else {
//            llNicknameLayout.setVisibility(View.GONE);
//        }
    }

    /**
     *
     */
    private void showView() {
        switch (loginType) {
            //直播登录显示密码昵称
            case LIVELOGIN:
                llPasswordLayout.setVisibility(View.VISIBLE);
                llNicknameLayout.setVisibility(View.VISIBLE);
                break;
            //临时直播登录显示昵称
            case TEMPLOGIN:
                llPasswordLayout.setVisibility(View.GONE);
                llNicknameLayout.setVisibility(View.VISIBLE);
                break;
            //点播登录页隐藏昵称
            case PLAYBACKLOGIN:
                llPasswordLayout.setVisibility(View.VISIBLE);
                llNicknameLayout.setVisibility(View.GONE);
                break;
        }
    }

    private void initEvent() {
        /**监听密码输入--焦点获取*/
        etPasswordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                llPasswordLayout.setSelected(b);
                tvPasswordLabel.setTextColor(b == true ? getResources().getColor(R.color.login_blue) : getResources().getColor(R.color.login_gray));
            }
        });

        etPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    tvPasswordHint.setVisibility(View.VISIBLE);
                } else {
                    tvPasswordHint.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**监听昵称输入--焦点获取*/
        edNicknameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                llNicknameLayout.setSelected(b);
                tvNickname.setTextColor(b == true ? getResources().getColor(R.color.login_blue) : getResources().getColor(R.color.login_gray));
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @OnClick({R2.id.login_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R2.id.login_btn:
                login();
                break;
        }
    }

    /**
     * 登录
     */
    public void login() {
        url = MainConsts.LOGIN_URL;
        String password = etPasswordEdit.getText().toString().trim();
        String nickname = "";
        if (loginType.equals(ScanQRCodeActivity.LIVELOGIN) || loginType.equals(TEMPLOGIN)) {
            nickname = edNicknameEdit.getText().toString().trim();
            if (TextUtils.isEmpty(nickname)) {
                tvErrorTip.setText("昵称不能为空");
                return;
            }
        }
//
        //服务器请求
        if (loginType.equals(TEMPLOGIN)) {
            url = MainConsts.LOGIN_TEMP_URL;
            params = String.format(MainConsts.LIVE_LOGIN_TEMP_PARAM, nickname, qrBean.getRoomid(), qrBean.getRole(), qrBean.getTemporary(), qrBean.getEt(), qrBean.getSign());
            id = qrBean.getRoomid();
        } else if (loginType.equals(PLAYBACKLOGIN)) {
            params = String.format(MainConsts.PLAYBACK_LOGIN_PARAM, qrBean.getLiveid(), password, qrBean.getMode());
            id = qrBean.getLiveid();
        } else {
            params = String.format(MainConsts.LIVE_LOGIN_PARAM, qrBean.getRoomid(), password, nickname, qrBean.getMode());
            id = qrBean.getRoomid();
        }
        HttpRequest request = new HttpRequest(this);
        request.sendRequestWithPost(url, params, new HttpRequest.IHttpRequestListener() {
            @Override
            public void onRequestCompleted(String responseStr) {
                try {
                    JSONObject jsonObject = new JSONObject(responseStr);
                    int code = jsonObject.optInt("code");
                    if (code == 0) {
                        JSONObject data = jsonObject.optJSONObject("data");
                        if (data != null) {
                            tvErrorTip.setText("");
                            String token = data.optString("access_token");
                            String logo = data.optString("logo");
                            String title = data.optString("title");
                            Intent intent = new Intent(ScanQRLoginActivity.this, LoginJumpActivity.class);
                            intent.putExtra(LoginJumpActivity.TOKEN_PARAM, token);
                            intent.putExtra(LoginJumpActivity.LOG0_PARAM, TextUtils.isEmpty(logo) ? qrBean.getLogo() : logo);
                            intent.putExtra(LoginJumpActivity.TITLE_PARAM, TextUtils.isEmpty(title) ? qrBean.getTitle() : title);
                            intent.putExtra(LoginJumpActivity.TYPE_PARAM, qrBean.getMode());
                            intent.putExtra(LoginJumpActivity.ID_PARAM, id);
                            startActivity(intent);
                            setResult(RESULT_OK);
                            ScanQRLoginActivity.this.finish();
                        }
                    } else {
                        tvErrorTip.setText(jsonObject.optString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onIOError(String errorStr) {
                tvErrorTip.setText(errorStr);
            }
        });
    }

}
