package com.talkfun.cloudlive.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.TFApplication;
import com.talkfun.cloudlive.consts.MainConsts;
import com.talkfun.cloudlive.imageload.GlideImageLoader;
import com.talkfun.cloudlive.manager.PopWindowManager;
import com.talkfun.cloudlive.net.HttpRequest;
import com.talkfun.cloudlive.util.ActivityUtil;
import com.talkfun.cloudlive.util.CacheUtils;
import com.talkfun.cloudlive.util.DimensionUtils;
import com.talkfun.cloudlive.util.SharedPreferencesUtil;
import com.talkfun.cloudlive.view.CustomSpinnerView;
import com.talkfun.sdk.offline.PlaybackDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/19 0019.
 */
public class LoginActivity extends BaseActivity {
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.login_userId_layout)
    LinearLayout llUserIdLayout;
    @BindView(R2.id.ll_login_label)
    LinearLayout llLoginLabel;
    @BindView(R2.id.login_userId_label)
    TextView tvUserIdLabel;
    @BindView(R2.id.login_userId_edit)
    EditText etUserIdEdit;
    @BindView(R2.id.login_password_layout)
    LinearLayout llPasswordLayout;
    //    @BindView(R2.id.login_password_label)
//    TextView tvPasswordLabel;
    @BindView(R2.id.login_password_edit)
    EditText etPasswordEdit;
    //    @BindView(R2.id.login_password_hint_tv)
//    TextView tvPasswordHint;
    @BindView(R2.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R2.id.ll_nickname_layout)
    LinearLayout llNicknameLayout;
    @BindView(R2.id.ed_nickname_edit)
    EditText etNicknameEdit;
    @BindView(R2.id.tv_nickname)
    TextView tvNickname;
    @BindView(R2.id.tv_error_tip)
    TextView tvErrorTip;
    @BindView(R2.id.iv_logo)
    ImageView ivLogo;
    @BindView(R2.id.cb_isSelected)
    CheckBox cbRememberId;
    private static final int QR_CODE_CODE = 0;
    public static final int LIVE_TYPE = 4;  //直播
    public static final int PLAYBACK_TYPE = 5; //点播
    private CustomSpinnerView customSpinnerView;
    private int type = LIVE_TYPE; //登录类型，直播/点播
    private ArrayList<String> idList;
    private ArrayList<String> checkIdList;
    private int listMaxSize = 5;
    private List<String> popupWindowListData;
    private long preClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getCache();
        initView();
        initEvent();

    }

    private void getCache() {
        idList = (ArrayList<String>) SharedPreferencesUtil.getStringList(this, SharedPreferencesUtil.SP_LOGIN_ID_LIST);
        checkIdList = (ArrayList<String>) SharedPreferencesUtil.getStringList(this, SharedPreferencesUtil.SP_LOGIN_ID_LIST_CHECK);
    }

    private void initView() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        customSpinnerView = new CustomSpinnerView(this);

        if (checkIdList.size() > 0) {
            etUserIdEdit.setText(checkIdList.get(0));
        }
    }

    public void initLogo() {
        String logoUrl = SharedPreferencesUtil.getString(this, SharedPreferencesUtil.SP_LOGIN_LOGO_URL);
        if (!TextUtils.isEmpty(logoUrl)) {
            ivLogo.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
            ivLogo.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.mipmap.huan_tuo_icon);
//            Glide.with(this).load(logoUrl).apply(requestOptions).into(ivLogo);
            GlideImageLoader.create(ivLogo).loadImage(logoUrl, R.mipmap.huan_tuo_icon);
        }
    }

    private void initEvent() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R2.id.video_save_list:
                        ActivityUtil.jump(LoginActivity.this, PlayDownLoadActivity.class);
                        break;
                    case R2.id.about_us:
                        ActivityUtil.jump(LoginActivity.this, AboutUsActivity.class);
                        break;
                }
                return true;
            }
        });

        /**监听ID输入--焦点获取*/
        etUserIdEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                llUserIdLayout.setSelected(b);
                ivArrow.setSelected(false);
                tvUserIdLabel.setTextColor(b == true ? getResources().getColor(R.color.login_blue) : getResources().getColor(R.color.login_gray));
            }
        });

        /**监听密码输入--焦点获取*/
        etPasswordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                llPasswordLayout.setSelected(b);
            }
        });

        /**监听昵称输入--焦点获取*/
        etNicknameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                llNicknameLayout.setSelected(b);
                tvNickname.setTextColor(b == true ? getResources().getColor(R.color.login_blue) : getResources().getColor(R.color.login_gray));
            }
        });

        /**ID模式选择*/
        customSpinnerView.setOnSpinnerListener(new CustomSpinnerView.OnSpinnerListener() {
            @Override
            public void onItemClick(int position) {
				setType((position == 0) ? LIVE_TYPE : PLAYBACK_TYPE);
            }

            @Override
            public void onDismiss() {
                ivArrow.setSelected(false);
            }
        });

        etUserIdEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                PopWindowManager.getInstance(LoginActivity.this).dismissPop();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void setType(int type){
        this.type = type;
        tvUserIdLabel.setText(getResources().getString(type == LIVE_TYPE? R.string.live_ID : R.string.playback_ID));
        llNicknameLayout.setVisibility(type == LIVE_TYPE ? View.VISIBLE : View.GONE);
        int position = (type == LIVE_TYPE) ? 0 : 1;
        if(customSpinnerView.getSelectedPosition() != position)
            customSpinnerView.setSelectedPosition(position);
    }

    @OnClick({R2.id.iv_scan, R2.id.ll_login_label, R2.id.iv_arrow, R2.id.login_btn, R2.id.tv_login_old_version,
            R2.id.tv_apply_for_try, R2.id.login_userId_edit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R2.id.iv_scan:  //扫码
                ActivityUtil.jump(this, ScanQRCodeActivity.class);
                break;
            case R2.id.iv_arrow:
            case R2.id.ll_login_label:
                ivArrow.setSelected(true);
                customSpinnerView.showAsDropDown(llLoginLabel, -DimensionUtils.dip2px(this, 10), 0);
                break;
            case R2.id.login_btn: //登录
                login();
                break;
            case R2.id.tv_login_old_version: //旧版本登录
                ActivityUtil.jump(this, LiveLoginActivity.class);
                break;
            case R2.id.tv_apply_for_try: //申请试用
                ActivityUtil.jump(this, ApplyForActivity.class);
                break;
            case R2.id.login_userId_edit:  //点击ID输入框
                showListPopWindow(idList);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 登录
     */
    public void login() {
        //id
        final String id = etUserIdEdit.getText().toString().trim();
        if (TextUtils.isEmpty(id)) {
            tvErrorTip.setText("ID不能为空");
            return;
        }
        //password
        String password = etPasswordEdit.getText().toString().trim();

        String nickname = etNicknameEdit.getText().toString().trim();
        if (type == LIVE_TYPE) {
            //nickname
            if (TextUtils.isEmpty(nickname)) {
                tvErrorTip.setText("昵称不能为空");
                return;
            }
        }

        //服务器请求
        String params = type == LIVE_TYPE ? String.format(MainConsts.LIVE_LOGIN_PARAM, id, password, nickname, type) : String.format(MainConsts.PLAYBACK_LOGIN_PARAM, id, password, type);
        HttpRequest request = new HttpRequest(this);
        request.sendRequestWithPost(MainConsts.LOGIN_URL, params, new HttpRequest.IHttpRequestListener() {
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
                            insertListValueUniq(id.trim());
                            SharedPreferencesUtil.saveString(LoginActivity.this, SharedPreferencesUtil.SP_LOGIN_LOGO_URL, logo);
                            Bundle bundle = new Bundle();
                            bundle.putString(LoginJumpActivity.TOKEN_PARAM, token);
                            bundle.putString(LoginJumpActivity.LOG0_PARAM, logo);
                            bundle.putString(LoginJumpActivity.TITLE_PARAM, title);
                            bundle.putInt(LoginJumpActivity.TYPE_PARAM, type);
                            bundle.putString(LoginJumpActivity.ID_PARAM, id);
                            ActivityUtil.jump(LoginActivity.this, LoginJumpActivity.class, bundle);
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


    /**
     * 插入列表值
     *
     * @param value
     */
    private void insertListValueUniq(String value) {
        if (idList.contains(value)) {
            idList.remove(value);
        }

        if (checkIdList.contains(value)) {
            checkIdList.remove(value);
        }

        if (cbRememberId.isChecked()) {
            idList.add(0, value);
            checkIdList.add(0, value);
        } else {
            idList.add(value);
        }

        while (checkIdList.size() > listMaxSize) {
            checkIdList.remove(checkIdList.size() - 1);
        }
        while (idList.size() > listMaxSize) {
            idList.remove(idList.size() - 1);
        }

        SharedPreferencesUtil.saveStringList(LoginActivity.this, SharedPreferencesUtil.SP_LOGIN_ID_LIST, idList);
        SharedPreferencesUtil.saveStringList(LoginActivity.this, SharedPreferencesUtil.SP_LOGIN_ID_LIST_CHECK, checkIdList);
    }

    private void showListPopWindow(List<String> listDatas) {
        popupWindowListData = listDatas;
        if (!listDatas.isEmpty() && listDatas.size() > 0) {
            if (etUserIdEdit.isFocusable()) {
                PopWindowManager popWindowManager = PopWindowManager.getInstance(this);
                if (!popWindowManager.isShowing()) {
                    popWindowManager.showPopListView(etUserIdEdit, listDatas);
                    /**监听下拉id列表的点击事件*/
                    popWindowManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String item = popupWindowListData.get(position);
                            if (position > 0) {
                                popupWindowListData.remove(position);
                                popupWindowListData.add(0, item);
                            }
                            if (etUserIdEdit.isFocusable()) {
                                etUserIdEdit.setText(item);
                                etUserIdEdit.setSelection(etUserIdEdit.getText().toString().length());
                            }
                            PopWindowManager.getInstance(LoginActivity.this).dismissPop();
                        }
                    });
                }

            }
        }
    }


    private void release() {
        /**退出移除所有的下载任务*/
        PlaybackDownloader.getInstance().destroy();
        CacheUtils.deleteCache(this);
    }

    @Override
    protected void onResume() {
        initLogo();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - preClickTime > 2000) {
            Toast.makeText(getApplicationContext(), getString(R.string.press_again_exit),
                    Toast.LENGTH_SHORT).show();
            preClickTime = System.currentTimeMillis();
            return;
        }
        super.onBackPressed();
        TFApplication.exit();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            boolean hideInputResult = isShouldHideInput(v, ev);
            Log.v("hideInputResult", "zzz-->>" + hideInputResult);
            if (hideInputResult) {
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) LoginActivity.this
                        .getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (v != null) {
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            //之前一直不成功的原因是,getX获取的是相对父视图的坐标,getRawX获取的才是相对屏幕原点的坐标！！！
//            Log.v("leftTop[]","zz--left:"+left+"--top:"+top+"--bottom:"+bottom+"--right:"+right);
//            Log.v("event","zz--getX():"+event.getRawX()+"--getY():"+event.getRawY());
            if (event.getRawX() > left && event.getRawX() < right
                    && event.getRawY() > top && event.getRawY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}
