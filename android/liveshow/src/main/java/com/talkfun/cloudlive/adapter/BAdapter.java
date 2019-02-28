package com.talkfun.cloudlive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 泛型基础适配器
 * ccy
 *
 * @param <T>
 */
public abstract class BAdapter<T> extends BaseAdapter {
    protected Context context;
    protected LayoutInflater inflater;
    protected List<T> itemList = new CopyOnWriteArrayList<>();
    protected int selectItem = 0;
    //这个数组用来存放item的点击状态,默认0为点中
    protected int[] clickedList;
    protected int MXA_LENGTH = 200;

    public BAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 判断集合中是否有数据
     */
    public boolean isEmpty() {
        return itemList.isEmpty();
    }

    /**
     * 在原有数据上添加新数据并更新适配器
     *
     * @param itemList
     */
    public void addItems(List<T> itemList) {
        this.itemList.addAll(itemList);
        setClickedList(itemList);
        this.notifyDataSetChanged();
    }

    /**
     * 清空旧数据,设置新的数据并更新适配器
     *
     * @param itemList
     */
    public void setItems(List<T> itemList) {
//        this.itemList.clear();
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }

    /**
     * 清空数据并更新适配器
     */
    public void clearItems() {
        this.itemList.clear();
        this.notifyDataSetChanged();
    }

    /***
     * 设置listView 选中状态
     * @param selectItem
     */
    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (itemList == null || itemList.size() == 0) {
            return 0;
        }
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    /**
     * 布局加载
     * [注:ViewGroup root = null, boolean attachToRoot = false]
     *
     * @param resource 需要加载布局文件的id
     * @return
     */
    protected View loadView(int resource) {
        View convertView = inflater.inflate(resource, null);
        return convertView;
    }

    /**
     * 布局加载
     * [注:boolean attachToRoot = false]
     *
     * @param resource 需要加载布局文件的id
     * @param root     需要附加到resource资源文件的根控件
     * @return
     */
    protected View loadView(int resource, ViewGroup root) {
        View convertView = inflater.inflate(resource, root);
        return convertView;
    }

    /**
     * 布局加载
     * [当需要用到第三个参数的时候一般会将其设为true]
     *
     * @param resource     需要加载布局文件的id
     * @param root         需要附加到resource资源文件的根控件
     * @param attachToRoot 是否将root附加到布局文件的根视图上
     * @return
     */
    protected View loadView(int resource, ViewGroup root, boolean attachToRoot) {
        View convertView = inflater.inflate(resource, root, attachToRoot);
        return convertView;
    }

    public void setClickedList(List<T> clickedList) {
        this.clickedList = new int[clickedList.size()];
        for (int i = 0; i < clickedList.size(); i++) {
            this.clickedList[i] = 0;
        }
    }
}
