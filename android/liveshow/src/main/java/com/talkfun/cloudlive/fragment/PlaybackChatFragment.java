package com.talkfun.cloudlive.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.adapter.PlaybackChatAdapter;
import com.talkfun.sdk.data.PlaybackDataManage;
import com.talkfun.sdk.event.AutoScrollListener;
import com.talkfun.sdk.event.HtDispatchPlaybackMsgListener;
import com.talkfun.sdk.module.ChatEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlaybackChatFragment extends PlaybackBaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        HtDispatchPlaybackMsgListener, AutoScrollListener {
    @BindView(R2.id.chat_lv)
    ListView chatLv;
    @BindView(R2.id.play_back_input)
    ViewGroup inputLayout;
    @BindView(R2.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;

    private String tag;
    private PlaybackChatAdapter chatAdapter;
    private List<ChatEntity> chatMessageEntityList = new ArrayList<>();

    public PlaybackChatFragment() {

    }

    public static PlaybackChatFragment create(String tag) {
        PlaybackChatFragment cf = new PlaybackChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("chat", tag);
        cf.setArguments(bundle);
        return cf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getString("chat") != null) {
            tag = getArguments().getString("chat");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.playback_chat_fragment_layout, container, false);
        ButterKnife.bind(this, layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.blue, android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        chatAdapter = new PlaybackChatAdapter(getActivity());
        chatAdapter.setItems(chatMessageEntityList);
        chatLv.setAdapter(chatAdapter);
        chatLv.setOnScrollListener(scrollListener);
        chatLv.setOnTouchListener(touchListener);

        inputLayout.setVisibility(View.GONE);
        PlaybackDataManage.getInstance().setChatListener(this);
        setChatList(PlaybackDataManage.getInstance().getChatList());
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PlaybackDataManage.getInstance().setChatListener(null);
    }

    private void setChatList(List<ChatEntity> list) {
        chatMessageEntityList.clear();
        if (list != null)
            chatMessageEntityList.addAll(list);
        if (chatAdapter != null)
            chatAdapter.setItems(chatMessageEntityList);
    }

    @Override
    public void onRefresh() {
        mIsLoading = true;
        PlaybackDataManage.getInstance().loadDownMoreData(PlaybackDataManage.DataType.CHAT);
    }

    @Override
    public void getPlaybackMsgSuccess(int position) {
        if (isShow && chatAdapter != null) {
            setChatList(PlaybackDataManage.getInstance().getChatList());
            if (position < chatMessageEntityList.size()) {
                chatLv.setSelection(position);
            } else {
                chatLv.setSelection(chatMessageEntityList.size() - 1);
            }
        }
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        mIsLoading = false;
    }

    @Override
    public void getPlaybackMsgFail(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        mIsLoading = false;
    }

    @Override
    public void scrollToItem(final int pos) {
        if (isShow && chatAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setChatList(PlaybackDataManage.getInstance().getChatList());
                    if (pos < chatMessageEntityList.size()) {
                        chatLv.setSelection(pos);
                    } else {
                        chatLv.setSelection(chatMessageEntityList.size() - 1);
                    }

                }
            });
        }
    }

    @Override
    public void updateAdapter() {
        if (chatAdapter != null)
            chatAdapter.notifyDataSetChanged();
    }

    /**
     * 清空聊天消息
     */
    public void clearPlaybackChatMessage() {
        if (chatAdapter == null) return;
        chatAdapter.clearItems();
    }

    @Override
    void getMoreData() {
        if (chatLv.getLastVisiblePosition() + 1 == chatMessageEntityList.size()) {
            mIsLoading = true;
            PlaybackDataManage.getInstance().loadUpMordData(PlaybackDataManage.DataType.CHAT);
        }
    }

    @Override
    public void startAutoScroll() {
        PlaybackDataManage.getInstance().startAutoScroll(this, PlaybackDataManage.DataType.CHAT);
    }

    @Override
    void resetAdapterData() {
        setChatList(PlaybackDataManage.getInstance().getChatList());
    }

}
