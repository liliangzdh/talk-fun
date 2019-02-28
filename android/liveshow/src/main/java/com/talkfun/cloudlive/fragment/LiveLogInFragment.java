package com.talkfun.cloudlive.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.talkfun.cloudlive.activity.LiveNativeActivity;
import com.talkfun.cloudlive.consts.MainConsts;
import com.talkfun.cloudlive.dialog.AlertDialogFactory;
import com.talkfun.cloudlive.net.HttpRequest;
import com.talkfun.cloudlive.net.HttpRequest.IHttpRequestListener;
import com.talkfun.cloudlive.util.ListPopupWindowManager;
import com.talkfun.cloudlive.net.NetMonitor;
import com.talkfun.cloudlive.util.SharedPreferencesUtil;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 直播登录Fragment
 *
 * @author admin
 */
public class LiveLogInFragment extends Fragment {
    @BindView(R2.id.edt_nickname)
    EditText etName;
    @BindView(R2.id.edt_room_id)
    EditText edt_room_id;
    @BindView(R2.id.edt_psw)
    EditText etKey;
    @BindView(R2.id.login)
    Button btnLogIn;
    @BindView(R2.id.live_login_bg)
    LinearLayout liveBg;
    @BindView(R2.id.cb_isSelected)
    CheckBox cb_isSelected;
    private static final String TAG = LiveLogInFragment.class.getName();
    private List<String> nameList, roomIdList; //昵称存储列表，秘钥存储列表
    private List<String> popupWindowListData; // 昵称存储列表，秘钥存储列表的引用
    private ListPopupWindow listPopupWindow; //列表弹出框对象引用。通过改变数据来显示昵称和密钥
    private int listMaxSize = 5; //列表显示条数最大值
    private boolean isAccountEdtFocused = false;
    private boolean isRoomIdEdtFocused = false;
    private ListPopupWindowManager listPopupWindowManager;
    private int mode;
    private boolean nameContainIllegalInput = false;
    private boolean keyContainIllegalInput = false;
    private boolean roomIdContainIllegalInput = false;
    private List<String> checkIdList;

    public LiveLogInFragment() {

    }

    public static LiveLogInFragment create(int mode) {
        LiveLogInFragment liveLogInFragment = new LiveLogInFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("mode", mode);
        liveLogInFragment.setArguments(bundle);
        return liveLogInFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        listPopupWindowManager = ListPopupWindowManager.getInstance();
        listPopupWindow = listPopupWindowManager.getLpw(getActivity().getApplicationContext());
        Bundle bundle = getArguments();
        mode = bundle.getInt("mode");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.live_log_in, container, false);
        ButterKnife.bind(this, v);
        if (mode == MainConsts.ROOM_MODE) {
            etKey.setHint("房间密码");
            edt_room_id.setHint("房间ID");
            nameList = SharedPreferencesUtil.getStringList(getActivity(), SharedPreferencesUtil.SP_LIVEROOM_NAMELIST);
            roomIdList = SharedPreferencesUtil.getStringList(getActivity(), SharedPreferencesUtil.SP_LIVEROOM_IDLIST);
            checkIdList = SharedPreferencesUtil.getStringList(getActivity(), SharedPreferencesUtil.SP_LIVEROOM_CHECK);
        } else {
            etKey.setHint("课程密码");
            edt_room_id.setHint("课程ID");
            nameList = SharedPreferencesUtil.getStringList(getActivity(), SharedPreferencesUtil.SP_LIVECOURSE_NAMELIST);
            roomIdList = SharedPreferencesUtil.getStringList(getActivity(), SharedPreferencesUtil.SP_LIVECOURSE_IDLIST);
            checkIdList = SharedPreferencesUtil.getStringList(getActivity(), SharedPreferencesUtil.SP_LIVECOURSE_CHECK);
        }
        if (checkIdList.size() > 0) {
            edt_room_id.setText(checkIdList.get(0));
        }
        return v;
    }

    @OnClick({R2.id.live_login_bg})
    public void onClick(View v) {
        switch (v.getId()) {
            case R2.id.live_login_bg:
                liveBg.setFocusable(true);
                liveBg.setFocusableInTouchMode(true);
                liveBg.requestFocus();
                break;
            default:
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && listPopupWindow != null) {
            listPopupWindow.setOnItemClickListener(listpopupOnItemClickListener);
            if (nameList == null)
                nameList = new ArrayList<>();
            listPopupWindowManager.refreshData(nameList, getActivity());
        }
    }

    private void addListeners() {
        etName.setOnFocusChangeListener(mEditorFocusChangeListener);
        etName.setOnClickListener(mEditorClickListener);
        etName.addTextChangedListener(mTextWatcher);
        etKey.setOnFocusChangeListener(mEditorFocusChangeListener);
        etKey.setOnClickListener(mEditorClickListener);
        etKey.addTextChangedListener(mTextWatcher);
        edt_room_id.setOnFocusChangeListener(mEditorFocusChangeListener);
        edt_room_id.setOnClickListener(mEditorClickListener);
        edt_room_id.addTextChangedListener(mTextWatcher);
        btnLogIn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!NetMonitor.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getActivity(), getString(R.string.not_connect), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (nameContainIllegalInput || keyContainIllegalInput || roomIdContainIllegalInput) {
                    Toast.makeText(getActivity(), getString(R.string.illegal_input), Toast.LENGTH_SHORT).show();
                    return;
                }

                //str = URLEncoder.encode(str, "utf-8");
                String name = etName.getText().toString();
                try {
                    name = URLEncoder.encode(name, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String key = etKey.getText().toString();
                String roomId = edt_room_id.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getActivity(), getString(R.string.account_cannot_empty), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(roomId)) {
                    Toast.makeText(getActivity(), getString(R.string.roomId_cannot_empty), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(key)) {
                    Toast.makeText(getActivity(), getString(R.string.password_cannot_empty), Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = MainConsts.getLiveLogInUrl(roomId, key, name, mode);
                Log.i(TAG, "url:" + url);
                HttpRequest request = new HttpRequest(getContext());
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
                        AlertDialogFactory.showAlertDialog(getFragmentManager(),"",getString(R.string.offline_login_failed),null);
                    }
                });
                logInStartCallback();
                request.sendRequestWithGET(url);
            }
        });
        View v = getView();
        v.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    OnGlobalLayoutListener globalLayoutListener = new OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            final View v = getView();
            v.getWindowVisibleDisplayFrame(r);
            int rootViewHeight = v.getRootView().getHeight();
            int heightDiff = rootViewHeight - (r.bottom - r.top);
            if (heightDiff > rootViewHeight / 4) {
                //显示虚拟键盘事件
            } else {
                //不显示虚拟键盘或虚拟键盘隐藏

            }
        }
    };

    private void removeListeners() {
        etName.setOnFocusChangeListener(null);
        etName.setOnClickListener(null);
        etName.removeTextChangedListener(mTextWatcher);
        etKey.setOnFocusChangeListener(null);
        etKey.setOnClickListener(null);
        etKey.removeTextChangedListener(mTextWatcher);
        edt_room_id.setOnFocusChangeListener(null);
        edt_room_id.setOnClickListener(null);
        edt_room_id.removeTextChangedListener(mTextWatcher);
        btnLogIn.setOnClickListener(null);
        View v = getView();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        addListeners();
        if (btnLogIn != null) {
            btnLogIn.setEnabled(true);
        }
    }

    @Override
    public void onPause() {
        removeListeners();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listPopupWindowManager.dismissLpw();
    }


    //文本输入框焦点变化事件监听
    private OnFocusChangeListener mEditorFocusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R2.id.edt_nickname:
                    if (!hasFocus) {
                        listPopupWindowManager.dismissLpw();
                        if (etName.getText().toString().length() > 0) {
                            setEditLogo(etName, R.mipmap.name_logo_focus);
                        } else {
                            setEditLogo(etName, R.mipmap.name_logo_normal);
                        }
                    } else {
                        isAccountEdtFocused = true;
                        isRoomIdEdtFocused = false;
                        setEditLogo(etName, R.mipmap.name_logo_focus);
                        //   showListPopWindow(nameList);
                    }
                    break;
                case R2.id.edt_psw:
                    if (!hasFocus) {

                        listPopupWindowManager.dismissLpw();
                        if (etKey.getText().toString().length() > 0) {
                            setEditLogo(etKey, R.mipmap.key_logo_focus);
                        } else {
                            setEditLogo(etKey, R.mipmap.key_logo_normal);
                        }
                    } else {
                        isAccountEdtFocused = false;
                        isRoomIdEdtFocused = false;
                        setEditLogo(etKey, R.mipmap.key_logo_focus);
                    }
                    break;
                case R2.id.edt_room_id:
                    if (!hasFocus) {
                        listPopupWindowManager.dismissLpw();
                        if (edt_room_id.getText().toString().length() > 0) {
                            setEditLogo(edt_room_id, R.mipmap.down_icon_focus);
                        } else {
                            setEditLogo(edt_room_id, R.mipmap.down_icon_normal);
                        }
                    } else {
                        isAccountEdtFocused = false;
                        isRoomIdEdtFocused = true;
                        setEditLogo(edt_room_id, R.mipmap.down_icon_focus);
                        //   showListPopWindow(roomIdList);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //文本输入框点击事件监听
    private OnClickListener mEditorClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
//             TODO Auto-generated method stub
            if (v.getId() == etName.getId()) {
                isAccountEdtFocused = true;
                isRoomIdEdtFocused = false;
                showListPopWindow(nameList);
            } else if (v.getId() == edt_room_id.getId()) {
                isAccountEdtFocused = false;
                isRoomIdEdtFocused = true;
                showListPopWindow(roomIdList);
            }
        }
    };

    //文本输入框文本变化事件监听
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            if (etName.isFocused())
                nameContainIllegalInput = s.toString().contains(" ");
            if (etKey.isFocused())
                keyContainIllegalInput = s.toString().contains(" ");
            if (edt_room_id.isFocused())
                roomIdContainIllegalInput = s.toString().contains(" ");
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

    /**
     * tony create this new method
     */
    private void showListPopWindow(List<String> listDatas) {
        popupWindowListData = listDatas;
        listPopupWindowManager.dismissLpw();
        if (!listDatas.isEmpty() && listDatas.size() > 0) {
            listPopupWindowManager.refreshData(listDatas,getContext());
            if (!listPopupWindowManager.isShow()) {
                if (isAccountEdtFocused) {
                    listPopupWindowManager.setAnchorView(etName);
                }
                if (isRoomIdEdtFocused) {
                    listPopupWindowManager.setAnchorView(edt_room_id);
                }
                listPopupWindowManager.showLpw();
            }
        }
    }

    private void setEditLogo(EditText et, int resourceId) {
        et.setCompoundDrawablesWithIntrinsicBounds(0, 0, resourceId, 0);
    }

    /**
     * list popup window click listener
     */
    private OnItemClickListener listpopupOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String item = popupWindowListData.get(position);
            if (position > 0) {
                popupWindowListData.remove(position);
                popupWindowListData.add(0, item);
            }
            // 如果是输入账号的edit获取了焦点，则说明目前是在输入账号
            // 应该设置账号的将数据赋给etName
            if (isAccountEdtFocused) {
                etName.setText(item);
                etName.setSelection(etName.getText().toString().length());
            }
            // 如果是输入账号的edit获取了焦点，则说明目前是在输入账号
            // 应该设置账号的将数据赋给etKey
            /*if (isPasswordEdtFocused) {
                etKey.setText(item);
                etKey.setSelection(etKey.getText().toString().length());
            }*/
            if (isRoomIdEdtFocused) {
                edt_room_id.setText(item);
                edt_room_id.setSelection(edt_room_id.getText().toString().length());
            }
            listPopupWindowManager.dismissLpw();
        }
    };

    /**
     * 解析服务器返回结果
     *
     * @param strResult
     */
    private void parseJson(String strResult) {
        try {
            JSONObject jsonObj = new JSONObject(strResult);
            if (jsonObj.getInt("code") == 0) {
                JSONObject dataObj = jsonObj.optJSONObject("data");
                String token = dataObj.optString("access_token");
                //nameList.add(editText1.getText().toString());
                String name = etName.getText().toString();
                String key = etKey.getText().toString();
                String roomId = edt_room_id.getText().toString();

                insertListValueUniq(roomIdList, roomId, cb_isSelected.isChecked(), false);
                insertListValueUniq(nameList, name, cb_isSelected.isChecked(), false);
                insertListValueUniq(checkIdList, roomId, cb_isSelected.isChecked(), true);
                if (mode == MainConsts.ROOM_MODE) {
                    SharedPreferencesUtil.saveStringList(getActivity(), SharedPreferencesUtil.SP_LIVEROOM_IDLIST, roomIdList);
                    SharedPreferencesUtil.saveStringList(getActivity(), SharedPreferencesUtil.SP_LIVEROOM_NAMELIST, nameList);
                    SharedPreferencesUtil.saveStringList(getActivity(), SharedPreferencesUtil.SP_LIVEROOM_CHECK, checkIdList);
                } else {
                    SharedPreferencesUtil.saveStringList(getActivity(), SharedPreferencesUtil.SP_LIVECOURSE_IDLIST, roomIdList);
                    SharedPreferencesUtil.saveStringList(getActivity(), SharedPreferencesUtil.SP_LIVECOURSE_NAMELIST, nameList);
                    SharedPreferencesUtil.saveStringList(getActivity(), SharedPreferencesUtil.SP_LIVECOURSE_CHECK, checkIdList);
                }

                logInCompletedCallback();
                if (token != null) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LiveNativeActivity.class);
                    intent.putExtra("token", token);
                    startActivity(intent);

                    //清空数据
                    etName.setText("");
                    etKey.setText("");
                    if (!cb_isSelected.isChecked()) {
                        edt_room_id.setText("");
                    }
                }
            } else {
                logInCancelCallback();
                AlertDialogFactory.showAlertDialog(getFragmentManager(),"",getString(R.string.login_failed_check_the_key),null);
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 插入列表值
     *
     * @param list
     * @param value
     */
    private void insertListValueUniq(List<String> list, String value, boolean isCheck, boolean isCheckIdList) {
        if (list.contains(value)) {
            list.remove(value);
        }

        if (isCheck) {
            list.add(0, value);
        } else {
            if (!isCheckIdList) {
                list.add(value);
            }
        }

        while (list.size() > listMaxSize) {
            list.remove(list.size() - 1);
        }
    }


    /**
     * 开始登录回调
     */
    private void logInStartCallback() {
        btnLogIn.setEnabled(false);
    }

    /**
     * 取消登录
     */
    private void logInCancelCallback() {
        btnLogIn.setEnabled(true);
    }

    /**
     * 登录完成
     */
    private void logInCompletedCallback() {
        btnLogIn.setEnabled(true);
    }
}
