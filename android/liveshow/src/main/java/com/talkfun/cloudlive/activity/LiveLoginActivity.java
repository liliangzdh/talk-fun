package com.talkfun.cloudlive.activity;


import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.consts.MainConsts;
import com.talkfun.cloudlive.fragment.LiveLogInFragment;
import com.talkfun.cloudlive.util.ActivityUtil;

/**
 * Created by asus on 2018/1/12.
 */

public class LiveLoginActivity extends BaseLoginActivity {
    private LiveLogInFragment liveRoomInFragment;
    private LiveLogInFragment liveCourseInFragment;

    @Override
    protected void initView() {
        super.initView();
        tvChangeMode.setText(getResources().getString(R.string.change_to_playback));
    }

    @Override
    protected void initFragments() {
        liveRoomInFragment = LiveLogInFragment.create(MainConsts.ROOM_MODE);
        liveCourseInFragment = LiveLogInFragment.create(MainConsts.COURSE_MODE);
        loginFragments.add(liveRoomInFragment);
        loginFragments.add(liveCourseInFragment);
        loginFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    protected void performChangeMode() {
        ActivityUtil.jump(this,PlaybackLoginActivity.class);
    }

}
