package com.talkfun.cloudlive.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.sdk.module.NetItem;

import java.lang.reflect.Field;


/**
 * Created by ccy on 2017/11/10.
 */

public class NetChoiceGridViewAdapter extends BAdapter<NetItem> {

    public NetChoiceGridViewAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NetChoiceGridViewAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = loadView(R.layout.ht_network_item, null);
            viewHolder = new NetChoiceGridViewAdapter.ViewHolder();
            viewHolder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
            viewHolder.iconIv = (ImageView) convertView.findViewById(R.id.icon_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NetChoiceGridViewAdapter.ViewHolder) convertView.getTag();
        }
        NetItem item = (NetItem) getItem(position);
        if (item != null) {
            viewHolder.nameTv.setText(item.getName());
            int iconName = getResourceByReflect(item.getKey());
            if (iconName != 0) {
                viewHolder.iconIv.setImageResource(iconName);
                viewHolder.iconIv.setVisibility(View.VISIBLE);
            } else {
                viewHolder.iconIv.setVisibility(View.GONE);
            }
        }
        if (selectItem == position) {
            convertView.setBackgroundResource(R.drawable.ht_change_network_selected_item_bg);
        } else {
            convertView.setBackgroundResource(R.drawable.ht_change_network_unselected_item_bg);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView nameTv;
        ImageView iconIv;
    }

    /**
     * 获取图片名称获取图片的资源id的方法
     *
     * @param imageName
     * @return
     */
    public int getResourceByReflect(String imageName) {
        Class drawable = R.mipmap.class;
        Field field = null;
        int r_id = 0;
        try {
            field = drawable.getField(imageName);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
            r_id = 0;
            Log.e("ERROR", "PICTURE NOT　FOUND！");
        }
        return r_id;
    }
}
