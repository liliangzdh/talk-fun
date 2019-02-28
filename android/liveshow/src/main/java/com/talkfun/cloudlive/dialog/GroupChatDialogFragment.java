package com.talkfun.cloudlive.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.util.StringUtils;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.consts.BroadcastCmdType;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.module.ChatEntity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 群聊对话框
 */
public class GroupChatDialogFragment extends DialogFragment {
    @BindView(R2.id.chat_lv)
    ListView questionLv;
    @BindView(R2.id.input_edt)
    EditText inputEdt;

    String tag;
    String questionContent;
    @BindView(R2.id.input)
    RelativeLayout sendFlower;
    @BindView(R2.id.send_btn)
    TextView sendBtn;
    @BindView(R2.id.flower_btn)
    ImageView flower;
    @BindView(R2.id.flower_num)
    TextView redDot;
    private String groupId;
    private List<ChatEntity> chatEntityList = new ArrayList<>();

    public void setChatEntityList(List<ChatEntity> chatEntityList) {
        this.chatEntityList = chatEntityList;
    }

    public static GroupChatDialogFragment create(List<ChatEntity> list) {
        GroupChatDialogFragment groupChatFragment = new GroupChatDialogFragment();
        groupChatFragment.setChatEntityList(list);
        return groupChatFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
//        setCancelable(false);
//        getList = () getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.livein_chat_fragment_layout, container);
        ButterKnife.bind(this, layout);
        sendBtn.setVisibility(View.VISIBLE);
        sendBtn.setText("发送");
        flower.setVisibility(View.INVISIBLE);
        redDot.setVisibility(View.INVISIBLE);
        sendFlower.setVisibility(View.VISIBLE);
        return layout;
    }
    String sendContent ;
    @OnClick(R2.id.send_btn)
    void onClcik(){
        sendContent = inputEdt.getText().toString().trim();
        sendMsg(sendContent, msgCallback);
    }

    //发送消息
    private void sendMsg(String sendContent, Callback callback) {
        if (!TextUtils.isEmpty(sendContent)) {
            HtSdk.getInstance().emit(BroadcastCmdType.CHAT_SEND, sendContent, callback);
            inputEdt.setText("");
            InputMethodManager imm =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inputEdt.getWindowToken(), 0);
        }
    }

    private Callback msgCallback = new Callback() {
        @Override
        public void success(Object result) {
            if (result != null) {
                JSONObject obj = (JSONObject) result;
//                appendList(ChatEntity.onExplainChatMessage(obj));
            }
        }

        @Override
        public void failed(String failed) {
            if (!TextUtils.isEmpty(failed)) {
                StringUtils.tip(getActivity(), failed);
            }
        }
    };
}
