package com.talkfun.cloudlive.activity;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.util.AppUtil;
import com.talkfun.cloudlive.util.CacheUtils;
import com.talkfun.cloudlive.util.ListPopupWindowManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class BaseLoginActivity extends BaseActivity implements OnClickListener{
    @BindView(R2.id.tv_room)
    TextView tvTabDefault;
    @BindView(R2.id.tv_course)
    TextView tvTabCustom;
    @BindView(R2.id.viewpager)
    ViewPager viewPager;
    @BindView(R2.id.package_tv)
    TextView packageTv;
    @BindView(R2.id.apply_for)
    TextView applyForTv;
    @BindView(R2.id.tv_change_mode)
    TextView tvChangeMode;


    public FragmentPagerAdapter loginFragmentAdapter;
    private Resources res;
    int currentPageIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_log_in);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        res = this.getResources();
        String versionTip = res.getString(R.string.version_tip);
        packageTv.setText(versionTip + AppUtil.getVerName(getApplicationContext()));
        applyForTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        initView();
        initEvent();
    }

    protected List<Fragment> loginFragments = new ArrayList<>();
   /* private LiveLogInFragment liveRoomInFragment;
    private LiveLogInFragment liveCourseInFragment;
    private PlaybackLogInFragment playbackRoomInFragment;
    private PlaybackLogInFragment playbackCourseInFragment;*/

    protected void initView() {
        loginFragmentAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return loginFragments.get(position);
            }

            @Override
            public int getCount() {
                return loginFragments.size();
            }
        };
        viewPager.setAdapter(loginFragmentAdapter);
        initFragments();
        RoomMode();
    }

    private void initEvent() {
        tvTabDefault.setOnClickListener(this);
        tvTabCustom.setOnClickListener(this);
        applyForTv.setOnClickListener(this);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        tvChangeMode.setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ListPopupWindowManager.getInstance().destroy();
        CacheUtils.deleteCache(getApplicationContext());
    }

    class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 0://状态无变化
                    break;
                case 1://正在滑动
                    ListPopupWindowManager.getInstance().dismissLpw();
                    break;
                case 2://滑动结束
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {

            resetTextColor();
            switch (arg0) {
                case 0:
                    tvTabDefault.setTextColor(res.getColor(R.color.white));
                    tvTabDefault.setBackgroundResource(R.drawable.tab_left_pressed);
                    break;
                case 1:
                    tvTabCustom.setTextColor(res.getColor(R.color.white));
                    tvTabCustom.setBackgroundResource(R.drawable.tab_right_pressed);
                    break;
            }
            currentPageIndex = arg0;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R2.id.tv_room://直播
                RoomMode();
                break;
            case R2.id.tv_course://点播
                CourseMode();
                break;
            case R2.id.apply_for:
                Intent applyForIntent = new Intent(this, ApplyForActivity.class);
                startActivity(applyForIntent);
                break;
            case R2.id.tv_change_mode:
                changeMode();
                break;
        }
    }

    /**
     * 切换播放模式
     */

    void changeMode() {
        ListPopupWindowManager.getInstance().dismissLpw();
        performChangeMode();
        finish();
    }

    protected abstract void performChangeMode();

    /**
     * 初始化播放的状态
     */
    protected abstract void initFragments();

    /**
     * 退后
     */
    @OnClick(R2.id.iv_chose_back)
    public void back() {
        ListPopupWindowManager.getInstance().dismissLpw();
        finish();
    }


    /**
     * 房间模式
     */
    private void RoomMode() {
        resetTextColor();
        tvTabDefault.setTextColor(res.getColor(R.color.white));
        tvTabDefault.setBackgroundResource(R.drawable.tab_left_pressed);
        viewPager.setCurrentItem(0);
    }

    /**
     * 课程模式
     */
    private void CourseMode() {
        resetTextColor();
        tvTabCustom.setTextColor(res.getColor(R.color.white));
        tvTabCustom.setBackgroundResource(R.drawable.tab_right_pressed);
        viewPager.setCurrentItem(1);
    }

    /**
     * 重置Text Tag标签背景颜色和文本颜色
     */
    private void resetTextColor() {
        ListPopupWindowManager.getInstance().dismissLpw();
        tvTabDefault.setBackgroundResource(R.drawable.tab_left_not_pressed);
        tvTabCustom.setBackgroundResource(R.drawable.tab_right_not_pressed);
        tvTabDefault.setTextColor(res.getColor(R.color.secondaryTextColor));
        tvTabCustom.setTextColor(res.getColor(R.color.secondaryTextColor));
    }

}
