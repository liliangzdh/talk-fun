package com.talkfun.cloudlive.activity;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.consts.MainConsts;
import com.talkfun.cloudlive.fragment.PlaybackLogInFragment;
import com.talkfun.cloudlive.util.ActivityUtil;

/**
 * Created by asus on 2018/1/12.
 */

public class PlaybackLoginActivity extends BaseLoginActivity {

    private PlaybackLogInFragment playbackRoomInFragment;
    private PlaybackLogInFragment playbackCourseInFragment;

    @Override
    protected void initView() {
        super.initView();
        tvChangeMode.setText(getResources().getString(R.string.change_to_live));
    }

    @Override
    protected void performChangeMode() {
        ActivityUtil.jump(this,LiveLoginActivity.class);
    }

    @Override
    protected void initFragments() {
        playbackRoomInFragment = PlaybackLogInFragment.create(MainConsts.ROOM_MODE);
        playbackCourseInFragment = PlaybackLogInFragment.create(MainConsts.COURSE_MODE);
        loginFragments.add(playbackRoomInFragment);
        loginFragments.add(playbackCourseInFragment);
        loginFragmentAdapter.notifyDataSetChanged();
    }
}
