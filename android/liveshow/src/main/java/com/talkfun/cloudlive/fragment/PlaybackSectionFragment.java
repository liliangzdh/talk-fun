package com.talkfun.cloudlive.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.adapter.SectionAdapter;
import com.talkfun.cloudlive.interfaces.IDispatchChapter;
import com.talkfun.sdk.data.PlaybackDataManage;
import com.talkfun.sdk.event.AutoScrollListener;
import com.talkfun.sdk.event.HtDispatchPlaybackMsgListener;
import com.talkfun.sdk.module.ChapterEntity;

import java.util.ArrayList;
import java.util.List;

public class PlaybackSectionFragment extends PlaybackBaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, IDispatchChapter, HtDispatchPlaybackMsgListener {
    private ListView sectionLv;
    private List<ChapterEntity> chapterList = new ArrayList<>();
    private SectionAdapter sectionAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PlaybackSectionInterface mPlaybackSectionInterface;

    public PlaybackSectionFragment() {

    }

    public static PlaybackSectionFragment create(String tag) {
        PlaybackSectionFragment sectionFragment = new PlaybackSectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        sectionFragment.setArguments(bundle);

        return sectionFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPlaybackSectionInterface = (PlaybackSectionInterface) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.section_fragment_layout, null);
        sectionLv = (ListView) view.findViewById(R.id.section_list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.blue, android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        //设置获取消息的监听
        PlaybackDataManage.getInstance().setChapterListener(this);
        //初始化 消息列表
        setChapterList(PlaybackDataManage.getInstance().getChapterList());
        sectionAdapter = new SectionAdapter(getActivity());
        sectionLv.setAdapter(sectionAdapter);
        sectionAdapter.setItems(chapterList);
        sectionLv.setOnScrollListener(scrollListener);
        sectionLv.setOnItemClickListener(onItemClickListener);
        sectionLv.setOnTouchListener(touchListener);
        return view;
    }


    private void setChapterList(List<ChapterEntity> list) {
        chapterList.clear();
        if (list != null)
            chapterList.addAll(list);
    }

    @Override
    public void getPlaybackMsgSuccess(int position) {
        if (isShow && sectionAdapter != null) {

            resetAdapterData();
            if (position < chapterList.size()) {
                sectionLv.setSelection(position);
            } else {
                sectionLv.setSelection(chapterList.size() - 1);
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
    public void onRefresh() {
        mIsLoading = true;
        PlaybackDataManage.getInstance().loadDownMoreData(PlaybackDataManage.DataType.CHAPTER);
    }

    /**
     * 章节跟随的监听
     * 重置章节的标志位 并且 更新当前章节
     */
    private AutoScrollListener autoScrollListener = new AutoScrollListener() {
        @Override
        public void scrollToItem(final int pos) {
            if (sectionLv != null && isShow) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetAdapterData();
                        int tmpPos = (pos < 0) ? 0 : ((pos > chapterList.size() - 1) ? chapterList.size() - 1 : pos);
                        updateCurrentItem(tmpPos);
                    }
                });
            }
        }
    };

    /**
     * 传入更新的position,更新当前项并重置标志位
     *
     * @param position
     */
    private void updateCurrentItem(int position) {
        if (position >= 0) {
            // 存在标记的播放项
            anchorChapter = chapterList.get(position);
            sectionAdapter.setSelectItem(position);
            if (position > 0)
                sectionLv.setSelection(position);
        }
    }

    @Override
    public void getChapterList(List<ChapterEntity> chapterEntityList) {

    }

    @Override
    public void switchToChapter() {

    }

    //标记显示播放中的项,用于选中其它项时,将原来项重置;
    private ChapterEntity anchorChapter = null;

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
            if (chapterList.size() > 0) {

                updateCurrentItem(position);

                String startTime = chapterList.get(position).getTime();
                if (startTime.contains("."))
                    startTime = startTime.substring(0, startTime.indexOf("."));
                long duration = Long.valueOf(startTime);
                if (mPlaybackSectionInterface != null)
                    mPlaybackSectionInterface.seekTo(duration);
            }
        }
    };

    @Override
    public void updateAdapter() {
        if (sectionAdapter != null)
            sectionAdapter.notifyDataSetChanged();
    }

    @Override
    void getMoreData() {
        if (sectionLv.getLastVisiblePosition() + 1 == chapterList.size()) {
            mIsLoading = true;
            PlaybackDataManage.getInstance().loadUpMordData(PlaybackDataManage.DataType.CHAPTER);
        }
    }

    /**
     * 清空聊天消息
     */
    public void clearPlaybackSectionMessage() {
        if (sectionAdapter == null) return;
        sectionAdapter.clearItems();
    }


    @Override
    public void startAutoScroll() {
        PlaybackDataManage.getInstance().startAutoScroll(autoScrollListener, PlaybackDataManage.DataType.CHAPTER);
    }

    @Override
    void resetAdapterData() {
        setChapterList(PlaybackDataManage.getInstance().getChapterList());
    }

    public interface PlaybackSectionInterface {
        void seekTo(long progress);
    }

}
