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
import com.talkfun.cloudlive.adapter.QuestionAdapter;
import com.talkfun.sdk.data.PlaybackDataManage;
import com.talkfun.sdk.event.AutoScrollListener;
import com.talkfun.sdk.event.HtDispatchPlaybackMsgListener;
import com.talkfun.sdk.module.QuestionEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlaybackQuestionFragment extends PlaybackBaseFragment implements HtDispatchPlaybackMsgListener,
        SwipeRefreshLayout.OnRefreshListener, AutoScrollListener {
    @BindView(R2.id.question_lv)
    ListView questionLv;
    @BindView(R2.id.question_input_layout)
    ViewGroup inputLayout;
    @BindView(R2.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    String tag;
    private QuestionAdapter questionAdapter;
    private List<QuestionEntity> questionEntitiesList = new ArrayList<>();

    public PlaybackQuestionFragment() {

    }

    public static PlaybackQuestionFragment create(String tag) {
        PlaybackQuestionFragment qf = new PlaybackQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("question", tag);
        qf.setArguments(bundle);
        return qf;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getString("question") != null) {
            tag = getArguments().getString("question");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.playback_question_fragment_layout, container, false);
        ButterKnife.bind(this, layout);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue,
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        // setQuestionList(PlaybackDataManage.getInstance().getQuestionList());
        setRawQuestionList(PlaybackDataManage.getInstance().getRawQuestionList());
        questionAdapter = new QuestionAdapter(getActivity());
        questionAdapter.setItems(questionEntitiesList);
        questionLv.setAdapter(questionAdapter);
        questionLv.setOnTouchListener(touchListener);

        questionLv.setOnScrollListener(scrollListener);
        inputLayout.setVisibility(View.GONE);

        PlaybackDataManage.getInstance().setQuestionListener(this);
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PlaybackDataManage.getInstance().setQuestionListener(null);
    }

    @Override
    public void scrollToItem(final int pos) {
        if (isShow && questionAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setRawQuestionList(PlaybackDataManage.getInstance().getRawQuestionList());
                    //setQuestionList(PlaybackDataManage.getInstance().getQuestionList());
                    if (pos < questionEntitiesList.size()) {
                        questionLv.setSelection(pos);
                    } else {
                        questionLv.setSelection(questionEntitiesList.size() - 1);
                    }
                }
            });
        }
    }

    private void setRawQuestionList(List<QuestionEntity> list) {
        questionEntitiesList.clear();
        if (list != null) {
            List<QuestionEntity> answerList;
            for (QuestionEntity entity : list) {
                questionEntitiesList.add(entity);
                answerList = entity.getAnswerList();
                if (answerList != null && answerList.size() > 0) {
                    for (QuestionEntity answer : answerList) {
                        questionEntitiesList.add(answer);
                    }
                }
            }
        }
        if (questionAdapter != null)
            questionAdapter.setItems(questionEntitiesList);
    }

    private void setQuestionList(List<QuestionEntity> list) {
        questionEntitiesList.clear();
        if (list != null)
            questionEntitiesList.addAll(list);
        if (questionAdapter != null)
            questionAdapter.setItems(questionEntitiesList);
    }


    @Override
    public void resetAdapterData() {
        setRawQuestionList(PlaybackDataManage.getInstance().getRawQuestionList());
        // setQuestionList(PlaybackDataManage.getInstance().getQuestionList());
    }

    @Override
    public void getPlaybackMsgSuccess(int position) {
        updateAdapter();
        if (isShow) {
            // setQuestionList(PlaybackDataManage.getInstance().getQuestionList());
            setRawQuestionList(PlaybackDataManage.getInstance().getRawQuestionList());
            if (position < questionEntitiesList.size()) {
                questionLv.setSelection(position);
            } else {
                questionLv.setSelection(questionEntitiesList.size() - 1);
            }
        }
        stopSwipeRefresh();
    }

    /**
     * 清空聊天消息
     */
    public void clearPlaybackQuestionMessage() {
        if (questionAdapter == null) return;
        questionAdapter.clearItems();
    }

    @Override
    public void getPlaybackMsgFail(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        stopSwipeRefresh();
    }

    private void stopSwipeRefresh() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        mIsLoading = false;
    }

    @Override
    public void onRefresh() {
        mIsLoading = true;
        PlaybackDataManage.getInstance().loadDownMoreData(PlaybackDataManage.DataType.QUESTION);
    }

    @Override
    public void updateAdapter() {
        if (questionAdapter != null)
            questionAdapter.notifyDataSetChanged();
    }

    @Override
    void getMoreData() {
        if (questionLv.getLastVisiblePosition() + 1 == questionEntitiesList.size()) {
            mIsLoading = true;
            PlaybackDataManage.getInstance().loadUpMordData(PlaybackDataManage.DataType.QUESTION);
        }
    }

    @Override
    public void startAutoScroll() {
        PlaybackDataManage.getInstance().startAutoScroll(this, PlaybackDataManage.DataType.QUESTION);
    }

}
