package com.talkfun.cloudlive.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.adapter.ListPopWindowAdapter;
import com.talkfun.cloudlive.view.CustomPopWindow;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ccy on 2017/11/16.
 */

public class PopWindowManager {
    private static PopWindowManager instance;
    private Context mContext;
    private CustomPopWindow mListPopWindow;
    private List<String> list;
    private ListView lvCustomView;

    private PopWindowManager(Context context) {
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        mContext = contextWeakReference.get();
    }

    public static PopWindowManager getInstance(Context context) {
        if (instance == null) {
            synchronized (PopWindowManager.class) {
                if (instance == null)
                    instance = new PopWindowManager(context);
            }

        }
        return instance;
    }

    public void showPopListView(View view, List<String> list) {
        showPopListView(view, list, 15, 0);
    }

    public void showPopListView(View view, List<String> list, int offX, int offY) {
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (mListPopWindow != null && mListPopWindow.isShowing()) {
            mListPopWindow.dissmiss();
        }
        this.list = list;
        if (view == null) {
            return;
        }
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_list, null);
        ListView lvCustom = (ListView) contentView.findViewById(R.id.lv_custom);
        int width = view.getWidth();
        if (list != null) {
            height = list.size() >= 3 ? view.getHeight() * 4 : ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        lvCustom.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        handleListView(lvCustom);
        //创建并显示popWindow
        mListPopWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                .setFocusable(false)
                .setView(contentView)
                .setIgnoreCheekPress(false)
                .size(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
                .create()
                .showAsDropDown(view, offX, offX);
    }

    public boolean isShowing() {
        if (mListPopWindow != null) {
            return mListPopWindow.isShowing();
        }
        return false;
    }

    public void dismissPop() {
        if (mListPopWindow != null) {
            mListPopWindow.dissmiss();
        }
    }

    private void handleListView(View listView) {
        lvCustomView = (ListView) listView;
        ListPopWindowAdapter adapter = new ListPopWindowAdapter(mContext);
        lvCustomView.setAdapter(adapter);
        adapter.setItems(list);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {

        if (lvCustomView == null || onItemClickListener == null) {
            return;
        }
        lvCustomView.setOnItemClickListener(onItemClickListener);

    }


}
