package com.talkfun.cloudlive.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class FragmentListAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public FragmentListAdapter(Context context, List<Fragment> fragmentList) {
        super(((FragmentActivity) context).getSupportFragmentManager());
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

/*    @Override
    public void finishUpdate(ViewGroup container) {
        try {
            super.finishUpdate(container);
        } catch (NullPointerException nullPointerException) {
            LogUtil.e("FragmentListAdapter", "Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
        }
    }*/
}
