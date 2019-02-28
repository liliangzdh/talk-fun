package com.talkfun.cloudlive.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.activity.PlayDownLoadActivity;
import com.talkfun.cloudlive.activity.PlaybackNativeActivity;
import com.talkfun.cloudlive.consts.MainConsts;
import com.talkfun.cloudlive.dialog.AlertDialogFactory;
import com.talkfun.cloudlive.net.HttpRequest;
import com.talkfun.cloudlive.net.HttpRequest.IHttpRequestListener;
import com.talkfun.cloudlive.util.ListPopupWindowManager;
import com.talkfun.cloudlive.net.NetMonitor;
import com.talkfun.cloudlive.util.SharedPreferencesUtil;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaybackLogInFragment extends Fragment {
    private List<String> idList;
    @BindView(R2.id.etID)
    EditText editText;
    @BindView(R2.id.edt_playback_psw)
    EditText edt_playback_psw;
    @BindView(R2.id.btnLogin)
    Button btnLogin;
    @BindView(R2.id.btn_downloadLogin)
    Button btn_downloadLogin;
    @BindView(R2.id.play_login_bg)
    LinearLayout playBg;
    @BindView(R2.id.cb_isSelected)
    CheckBox cb_isSelected;
    private ListPopupWindow lpw;
    private int listMaxSize = 5;
    private static final String TAG = PlaybackLogInFragment.class.getName();
    private ListPopupWindowManager listPopupWindowManager;
    private boolean roomIdContainIllegalInput = false;
    private boolean pswContainIllegalInput = false;
    private boolean isRoomIdEdtFocused = false;
    private int mode;
    private List<String> quickInputListData; // 昵称存储列表，秘钥存储列表的引用
    private List<String> checkIdList;

    public PlaybackLogInFragment() {

    }

    public static PlaybackLogInFragment create(int m) {
        PlaybackLogInFragment playbackLogInFragment = new PlaybackLogInFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("mode", m);
        playbackLogInFragment.setArguments(bundle);
        return playbackLogInFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
        // Code here
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }
    protected void onAttachToContext(Context context) {
        listPopupWindowManager = ListPopupWindowManager.getInstance();
        lpw = listPopupWindowManager.getLpw(context.getApplicationContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = getArguments().getInt("mode");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        final View v = inflater.inflate(R.layout.play_back_log_in, container, false);
        ButterKnife.bind(this, v);
        if (mode == MainConsts.ROOM_MODE) {
            editText.setHint("直播ID");
            edt_playback_psw.setHint("点播密码");
            idList = SharedPreferencesUtil.getStringList(getActivity(), SharedPreferencesUtil.SP_PLAYBACKROOM_IDLIST);
            checkIdList = SharedPreferencesUtil.getStringList(getActivity(), SharedPreferencesUtil.SP_PLAYBACKROOM_CHECK);
        } else {
            editText.setHint("课程ID");
            edt_playback_psw.setHint("课程密码");
            idList = SharedPreferencesUtil.getStringList(getActivity(), SharedPreferencesUtil.SP_PLAYBACKCOURSE_IDLIST);
            checkIdList = SharedPreferencesUtil.getStringList(getActivity(), SharedPreferencesUtil.SP_PLAYBACKCOURSE_CHECK);
        }

        if (checkIdList.size() > 0) {
            editText.setText(checkIdList.get(0));
        }


        editText.setOnFocusChangeListener(mEditorFocusChangeListener);
        editText.setOnClickListener(mEditorClickListener);
        editText.addTextChangedListener(mTextWatcher);

        edt_playback_psw.setOnFocusChangeListener(mEditorFocusChangeListener);
        edt_playback_psw.addTextChangedListener(mTextWatcher);
        edt_playback_psw.addTextChangedListener(mTextWatcher);

        btn_downloadLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent = intent.setClass(getActivity(), PlayDownLoadActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String liveID = editText.getText().toString();
                /**ID判断是否为空*/
                if (TextUtils.isEmpty(liveID)) {
                    Toast.makeText(getActivity(), getString(R.string.roomId_cannot_empty), Toast.LENGTH_SHORT).show();
                    return;
                }
                String psw = edt_playback_psw.getText().toString();
                /**密码判断是否为空*/
                if (TextUtils.isEmpty(psw)) {
                    Toast.makeText(getActivity(),  getString(R.string.password_cannot_empty), Toast.LENGTH_SHORT).show();
                    return;
                }
                /**判断是否输入了非法字符*/
                if (roomIdContainIllegalInput || pswContainIllegalInput) {
                    Toast.makeText(getActivity(), getString(R.string.illegal_input), Toast.LENGTH_SHORT).show();
                    return;
                }
                /**判断是否联网*/
                if (!NetMonitor.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getActivity(), getString(R.string.not_connect), Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = MainConsts.getPlaybackLogInUrl(liveID, psw, mode);
                HttpRequest request = new HttpRequest(getActivity());
                request.setRequestListener(new IHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(String responseStr) {
                        // TODO Auto-generated method stub
                        parseJson(responseStr);
                    }

                    @Override
                    public void onIOError(String errorStr) {
                        // TODO Auto-generated method stub
                        logInCancelCallback();
                        Toast.makeText(getActivity(), getString(R.string.offline_login_failed), Toast.LENGTH_SHORT).show();
                    }
                });
                logInStartCallback();
                request.sendRequestWithGET(url);
            }
        });

        v.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                v.getWindowVisibleDisplayFrame(r);
                int rootViewHeight = v.getRootView().getHeight();
                int heightDiff = rootViewHeight - (r.bottom - r.top);
                if (heightDiff > rootViewHeight / 4) {
                    //显示虚拟键盘事件
                } else {
                    //虚拟键盘隐藏
                    listPopupWindowManager.dismissLpw();
                }
            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listPopupWindowManager.dismissLpw();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (lpw!=null) {
                lpw.setOnItemClickListener(listpopupOnItemClickListener);
                listPopupWindowManager.refreshData(idList, getActivity());
            }

        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (btnLogin != null) {
            btnLogin.setEnabled(true);
        }
    }

    @OnClick({R2.id.play_login_bg})
    public void onClick(View v) {
        switch (v.getId()) {
            case R2.id.play_login_bg:
                playBg.setFocusable(true);
                playBg.setFocusableInTouchMode(true);
                playBg.requestFocus();
                break;
            default:
                break;
        }
    }


    /**
     * 显示秘钥弹出列表
     */
    private void showListPopWindow(List<String> listDatas) {
        quickInputListData = listDatas;
        listPopupWindowManager.dismissLpw();
        if (!listDatas.isEmpty() && listDatas.size() > 0) {
            listPopupWindowManager.refreshData(listDatas, getActivity());
            if (!listPopupWindowManager.isShow()) {
                if (isRoomIdEdtFocused) {
                    listPopupWindowManager.setAnchorView(editText);
                }
                listPopupWindowManager.showLpw();
            }
        }
    }


    /**
     * list popup window click listener
     */
    private OnItemClickListener listpopupOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String item = quickInputListData.get(position);
            if (position > 0) {
                idList.remove(position);
                idList.add(0, item);
            }
            if (isRoomIdEdtFocused) {
                editText.setText(item);
                editText.setSelection(editText.getText().toString().length());
            }
            listPopupWindowManager.dismissLpw();
        }
    };
    private OnFocusChangeListener mEditorFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R2.id.edt_playback_psw:
                    if (!hasFocus) {
                        listPopupWindowManager.dismissLpw();
                        if (edt_playback_psw.getText().toString().length() > 0) {
                            setEditLogo(edt_playback_psw, R.mipmap.key_logo_focus);
                        } else {
                            setEditLogo(edt_playback_psw, R.mipmap.key_logo_normal);
                        }
                    } else {
                        isRoomIdEdtFocused = false;
                        setEditLogo(edt_playback_psw, R.mipmap.key_logo_focus);
                    }
                    break;
                case R2.id.etID:
                    if (!hasFocus) {
                        listPopupWindowManager.dismissLpw();
                        if (editText.getText().toString().length() > 0) {
                            setEditLogo(editText, R.mipmap.down_icon_focus);
                        } else {
                            setEditLogo(editText, R.mipmap.down_icon_normal);
                        }
                    } else {
                        isRoomIdEdtFocused = true;
                        setEditLogo(editText, R.mipmap.down_icon_focus);
                        //    showListPopWindow(idList);
                    }
                    break;
            }
        }
    };

    private OnClickListener mEditorClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.etID) {
                isRoomIdEdtFocused = true;
                showListPopWindow(idList);
            }
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            if (editText.isFocused())
                roomIdContainIllegalInput = s.toString().contains(" ");
            if (edt_playback_psw.isFocused())
                pswContainIllegalInput = s.toString().contains(" ");

            listPopupWindowManager.dismissLpw();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };


    private void parseJson(String strResult) {
        try {
            JSONObject jsonObj = new JSONObject(strResult);
            if (jsonObj.getInt("code") == 0) {

                JSONObject dataObj = jsonObj.optJSONObject("data");
                String token = dataObj.optString("access_token");

                String id = editText.getText().toString();
                String psw = edt_playback_psw.getText().toString();

                //保存数据
                insertListValueUniq(id);
                logInCompletedCallback();
                if (token != null) {
                    Intent intent = new Intent();
                    intent = intent.setClass(getActivity(), PlaybackNativeActivity.class);
                    intent.putExtra("token", token);
                    intent.putExtra("id", id);
                    startActivity(intent);

                    //清空
                    if (!cb_isSelected.isChecked()) {
                        editText.setText("");
                    }
                    edt_playback_psw.setText("");
                }
            } else {
                logInCancelCallback();
                AlertDialogFactory.showAlertDialog(getFragmentManager(),"", getString(R.string.login_failed_check_play_id),null);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
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

        if (cb_isSelected.isChecked()) {
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

        if (mode == MainConsts.ROOM_MODE) {
            SharedPreferencesUtil.saveStringList(getActivity(), SharedPreferencesUtil.SP_PLAYBACKROOM_IDLIST, idList);
            SharedPreferencesUtil.saveStringList(getActivity(), SharedPreferencesUtil.SP_PLAYBACKROOM_CHECK, checkIdList);
        } else {
            SharedPreferencesUtil.saveStringList(getActivity(), SharedPreferencesUtil.SP_PLAYBACKCOURSE_IDLIST, idList);
            SharedPreferencesUtil.saveStringList(getActivity(), SharedPreferencesUtil.SP_PLAYBACKCOURSE_CHECK, checkIdList);
        }
    }


    /**
     * 开始登录回调
     */
    private void logInStartCallback() {
        btnLogin.setEnabled(false);
    }

    /**
     * 取消登录
     */
    private void logInCancelCallback() {
        btnLogin.setEnabled(true);
    }

    /**
     * 登录完成
     */
    private void logInCompletedCallback() {
        btnLogin.setEnabled(true);
    }

    private void setEditLogo(EditText et, int resourceId) {
        et.setCompoundDrawablesWithIntrinsicBounds(0, 0, resourceId, 0);
    }
}
