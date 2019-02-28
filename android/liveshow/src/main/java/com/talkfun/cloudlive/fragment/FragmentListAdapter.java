package com.talkfun.cloudlive.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 良好的代码风格能给人带来一个美好的下午
 * Created by tony on 2015/9/16.
 */
public class FragmentListAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public FragmentListAdapter(Context context , List<Fragment> fragmentList) {
        super(((FragmentActivity)context).getSupportFragmentManager());
        this.fragments = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
