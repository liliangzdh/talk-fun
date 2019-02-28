package com.talkfun.cloudlive.fragment;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

import com.talkfun.sdk.data.PlaybackDataManage;



public abstract class PlaybackBaseFragment extends Fragment {

    public boolean isShow = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isShow = isVisibleToUser;
        if (isVisibleToUser) {
            //updateAdapter();
            resetAdapterData();
            startAutoScroll();
        } else {
            stopAutoScroll();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShow) {
            resetAdapterData();
            startAutoScroll();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isShow) {
            stopAutoScroll();
        }
    }

  /*  @Override
    public void onDestroyView() {
        super.onDestroyView();
        PlaybackDataManage.getInstance().stopAutoScroll();
    }*/

    private int listTotalItemCount = 0;
    protected boolean mIsLoading = false;
    // list view 上拉加载更多
    public AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            int lastVisibleIndex=view.getLastVisiblePosition();
            if (!mIsLoading&&scrollState == SCROLL_STATE_IDLE//停止滚动
                    && lastVisibleIndex ==  listTotalItemCount-1) {//滑动到最后一项
                mIsLoading=true;
                getMoreData();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            listTotalItemCount = totalItemCount;
        }
    };

    //滑动 list view时停止跟随
    public Handler handler = new Handler();
    public View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.removeCallbacksAndMessages(null);
                    pauseAutoScroll();
                    break;
                case MotionEvent.ACTION_UP:

                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resumeAutoScroll();
                        }
                    }, 1000);
                    break;
            }
            return false;
        }
    };

    public void stopAutoScroll() {
        PlaybackDataManage.getInstance().stopAutoScroll();
    }

    public void pauseAutoScroll() {
        PlaybackDataManage.getInstance().pauseAutoScroll();
    }

    public void resumeAutoScroll() {
        PlaybackDataManage.getInstance().resumeAutoScroll();
    }

    abstract void updateAdapter();

    abstract void getMoreData();

    abstract void startAutoScroll();

    abstract void resetAdapterData();
}
