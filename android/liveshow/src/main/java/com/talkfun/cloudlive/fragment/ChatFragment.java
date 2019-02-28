package com.talkfun.cloudlive.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.adapter.ChatAdapter;
import com.talkfun.cloudlive.interfaces.IDispatchChatMessage;
import com.talkfun.cloudlive.util.DimensionUtils;
import com.talkfun.cloudlive.util.ExpressionUtil;
import com.talkfun.sdk.module.ChatEntity;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 聊天Fragment
 */
public class ChatFragment extends Fragment implements IDispatchChatMessage {
    @BindView(R2.id.chat_lv)
    ListView chatLv;
    @BindView(R2.id.ll_tip_layout)
    LinearLayout llReplyTip;

    private ChatAdapter chatAdapter;
    private ArrayList<Object> chatMessageEntityList = new ArrayList<>();
    private Unbinder unbinder;

    public ChatFragment() {
    }

    public static ChatFragment create(ArrayList<Object> list) {
        ChatFragment cf = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", list);
        cf.setArguments(bundle);
        return cf;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && chatAdapter != null)
            chatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ExpressionUtil.edtImgWidth = DimensionUtils.dip2px(activity, 24);
        ExpressionUtil.edtImgHeight = DimensionUtils.dip2px(activity, 24);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getSerializable("list") != null) {
            chatMessageEntityList = (ArrayList<Object>) getArguments().getSerializable("list");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.livein_chat_fragment_layout, container, false);
        unbinder = ButterKnife.bind(this, layout);
        chatAdapter = new ChatAdapter(getActivity());
        chatLv.setAdapter(chatAdapter);
        chatAdapter.setItems(chatMessageEntityList);
        // InputBarView.setDispatchChatMessage(this);
        // FullScreenInputBarView.setDispatchChatMessage(this);
        return layout;
    }

    /**
     * 是否显示恢复提示
     *
     * @param isShow
     */
    public void showReplyTip(boolean isShow) {
        if (llReplyTip == null) return;
        llReplyTip.setVisibility(isShow == true ? View.VISIBLE : View.GONE);
    }

    @OnClick({R2.id.ll_tip_layout, R2.id.iv_remove})
    void onClick(View v) {
        switch (v.getId()) {
            case R2.id.ll_tip_layout:
                llReplyTip.setVisibility(View.GONE);
                if (listener != null) {
                    listener.jumpToQuestionPage();
                }
                break;
            case R2.id.iv_remove:
                llReplyTip.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("list", chatMessageEntityList);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        try {
            if (savedInstanceState == null)
                return;
            ArrayList<Object> list = (ArrayList<Object>) savedInstanceState.getSerializable("list");
            if (list != null && list.size() > 0) {
                if (chatAdapter != null) {
                    chatAdapter.setItems(list);
                    chatMessageEntityList = list;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 该回调用于直播
     *
     * @param chatMessageEntity 消息实例
     */
    @Override
    public void setChatMessage(Object chatMessageEntity) {
        if (chatMessageEntity != null && chatAdapter != null) {
            chatAdapter.appendList(chatMessageEntity);
            if (listener != null) {
                if (chatMessageEntity instanceof ChatEntity) {
                    ChatEntity chatEntity = (ChatEntity) chatMessageEntity;
                    SpannableString spannableString = ExpressionUtil.removeExpression(getActivity(), chatEntity.getMsg());
                    listener.appendNewChatMes(spannableString);
                }
            }
            if (chatLv != null)
                chatLv.setSelection(chatAdapter.getCount() - 1);
        }
    }

    public void appendList(final ChatEntity chatEntity) {
        chatAdapter.appendList(chatEntity);
        if (chatLv != null)
            chatLv.setSelection(chatAdapter.getCount() - 1);

    }

    /**
     * 清空聊天信息
     */
    public void clearAllMessage() {
        if (chatAdapter == null) return;
        chatAdapter.clearItems();
    }

    /**
     * 添加聊天信息
     */
    public void appChatMessage(Object result) {
        if (result != null) {

            JSONObject obj = (JSONObject) result;
            ChatEntity entity = ChatEntity.onExplainChatMessage(obj);
            appendList(entity);
            if (listener != null) {
                String msg = entity.getMsg();
                SpannableString spannableString = ExpressionUtil.removeExpression(getActivity(), msg);
                listener.appendNewChatMes(spannableString);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public OnChatOperationListener listener;

    public interface OnChatOperationListener {
        // 通知有新的消息的接口
        void appendNewChatMes(SpannableString msg);

        //切换到提问界面
        void jumpToQuestionPage();
    }

    public void setOnChatOperationListener(OnChatOperationListener listener) {
        this.listener = listener;
    }
}
