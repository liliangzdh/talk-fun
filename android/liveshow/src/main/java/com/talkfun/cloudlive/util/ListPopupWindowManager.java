package com.talkfun.cloudlive.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;

import com.talkfun.cloudlive.adapter.ListPopWindowAdapter;

import java.util.ArrayList;
import java.util.List;


public class ListPopupWindowManager {
    private ListPopupWindow lpw;
    private ListPopWindowAdapter listPopWindowAdapter;
    private List<String> listData = new ArrayList<>();
    private static ListPopupWindowManager instance;

    public static ListPopupWindowManager getInstance() {
        if (instance == null) {
            instance = new ListPopupWindowManager();
        }
        return instance;
    }


    /**
     * 创建弹出列表
     * 注意，list还未设置显示的位置
     */
    @SuppressLint("RestrictedApi")
    public ListPopupWindow getLpw(Context context) {
        if (lpw == null) {
            lpw = new ListPopupWindow(context.getApplicationContext());
            lpw.setModal(false);
            refreshData(listData, context.getApplicationContext());
        }
        return lpw;
    }

    /**
     * 刷新是数据*
     *
     * @param listDatas
     * @param context
     */
    public void refreshData(List<String> listDatas, Context context) {
        if (listDatas == null)
            return;
        this.listData.clear();
        this.listData.addAll(listDatas);
        if (lpw == null)
            getLpw(context.getApplicationContext());
        if (listPopWindowAdapter == null)
            listPopWindowAdapter = new ListPopWindowAdapter(context.getApplicationContext());
        lpw.setAdapter(listPopWindowAdapter);
        listPopWindowAdapter.setItems(listData);
    }

    public void setAnchorView(View view) {
        lpw.setAnchorView(view);

    }

    /**
     * 显示列表
     */
    public void showLpw() {
        dismissLpw();
        if (lpw != null && !lpw.isShowing())
            lpw.show();
    }


    /**
     * 隐藏列表
     */
    public void dismissLpw() {
        if (lpw != null && lpw.isShowing()) {
            lpw.dismiss();
        }
    }


    public boolean isShow() {
        if (lpw != null)
            return lpw.isShowing();
        return false;
    }

    public void destroy() {
        dismissLpw();
        instance.listPopWindowAdapter = null;
        instance.listData.clear();
    }
}
