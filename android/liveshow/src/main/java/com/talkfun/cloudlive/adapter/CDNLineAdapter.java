package com.talkfun.cloudlive.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.sdk.module.CDNItem;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by ccy on 2017/11/10.
 */

public class CDNLineAdapter extends BAdapter<CDNItem> {

    public CDNLineAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CDNLineAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = loadView(R.layout.ht_line_select, null);
            viewHolder = new CDNLineAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CDNLineAdapter.ViewHolder) convertView.getTag();
        }
        if (selectItem == position) {
            viewHolder.tvLine.setSelected(true);
        } else {
            viewHolder.tvLine.setSelected(false);
        }
        int lineName = position + 1;
        viewHolder.tvLine.setText("线路" + lineName);

        return convertView;
    }

    static class ViewHolder {
        @BindView(R2.id.tv_line)
        TextView tvLine;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
