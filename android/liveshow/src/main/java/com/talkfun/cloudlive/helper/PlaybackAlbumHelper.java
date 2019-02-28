package com.talkfun.cloudlive.helper;

import android.support.v4.app.Fragment;
import android.view.View;

import com.talkfun.cloudlive.adapter.FragmentListAdapter;
import com.talkfun.cloudlive.fragment.PlaybackAlbumFragment;
import com.talkfun.cloudlive.util.SeekBarHelper;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.data.PlaybackDataManage;
import com.talkfun.sdk.module.PlaybackInfo;

import java.util.List;

/**
 * 专辑帮助类
 * Created by ccy on 2017/10/20.
 */

public class PlaybackAlbumHelper {
    /**
     * 专辑Fragment
     */
    private PlaybackAlbumFragment albumFragment;
    private static PlaybackAlbumHelper mInstance;

    private PlaybackAlbumHelper() {
    }

    public static PlaybackAlbumHelper getInstance() {
        if (mInstance == null) {
            synchronized (PlaybackAlbumHelper.class) {
                if (mInstance == null) {
                    mInstance = new PlaybackAlbumHelper();
                }
            }
        }
        return mInstance;
    }

    public void addPlaybackAlbum(List<Fragment> mFragmentList, FragmentListAdapter mFragmentListAdapter, View view, final SeekBarHelper seekBarUtil) {
        if (PlaybackInfo.getInstance().isAlbum()) {
            if (albumFragment == null) {
                albumFragment = PlaybackAlbumFragment.create("album");
                albumFragment.setOnAlbumItemSelectedListener(new PlaybackAlbumFragment.OnAlbumItemSelectedListener() {
                    @Override
                    public void onAlbumItemSelected(int position) {
                        PlaybackInfo.getInstance().setCurrentAlbumIndex(position);
                        seekBarUtil.resetSeekBarProgress();
                        HtSdk.getInstance().playAlbum(PlaybackDataManage.getInstance().getAlbumList().get(position));
                    }
                });
                mFragmentList.add(albumFragment);
                mFragmentListAdapter.notifyDataSetChanged();
            }
            view.setVisibility(View.VISIBLE);  //专辑显示
            albumFragment.setAlbumList(PlaybackDataManage.getInstance().getAlbumList());
            albumFragment.setCurrentIndex(PlaybackInfo.getInstance().getCurrentAlbumIndex());
        } else if (albumFragment != null) {
            /**不是专辑处理逻辑*/
            if (mFragmentList.contains(albumFragment)) {
                mFragmentList.remove(albumFragment);
                mFragmentListAdapter.notifyDataSetChanged();
            }
            view.setVisibility(View.GONE);  //专辑隐藏
            albumFragment = null;
        }
    }

}
