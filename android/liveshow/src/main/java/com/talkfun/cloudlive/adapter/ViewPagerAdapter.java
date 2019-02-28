package com.talkfun.cloudlive.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by asus on 2015/11/24.
 */
public class ViewPagerAdapter extends PagerAdapter {
    List<View> mPages;

    public ViewPagerAdapter(List<View> pages){
        mPages = pages;
    }
    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mPages.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v =  mPages.get(position);
        container.addView(v);
        return  v;
    }

}
