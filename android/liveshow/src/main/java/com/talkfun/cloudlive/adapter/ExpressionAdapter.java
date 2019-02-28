package com.talkfun.cloudlive.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.entity.ExpressionEntity;

/**
 * Created by asus on 2015/11/24.
 */
public class ExpressionAdapter extends BAdapter<ExpressionEntity> {
    //    private List<ExpressionEntity> mDatas;
//    private Context mContext;
    private int columnNum = 7;
    private boolean isFullScreen = false;

    public ExpressionAdapter(Context context, int columnNum) {
        super(context);
//        mContext = context;
//        mDatas = datas;
        this.columnNum = columnNum;
        isFullScreen = columnNum > itemList.size();
    }

    @Override
    public int getCount() {
        if (isFullScreen) {  //全屏表情列数
            return itemList.size() + 1;
        } else {  //非全屏表情列数
            return itemList.size() + itemList.size() % columnNum + 1;
        }
    }

    @Override
    public Object getItem(int position) {
        if (isFullScreen && position == itemList.size() || !isFullScreen && position == itemList.size() + itemList.size() % columnNum) {
            ExpressionEntity expressionEntity = new ExpressionEntity();
            expressionEntity.resId = R.mipmap.iv_delete_expression;
            expressionEntity.character = "[delete]";
            return expressionEntity;
        }
        if (itemList == null || itemList.size() <= position)
            return null;
        return itemList.get(position);
    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_expression_layout, null);
            viewHolder.ivExpression = (ImageView) convertView.findViewById(R.id.iv_expression_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position < itemList.size())
            viewHolder.ivExpression.setImageResource(itemList.get(position).resId);
        else if (isFullScreen && position == itemList.size() || !isFullScreen && position == itemList.size() + itemList.size() % columnNum) {
            viewHolder.ivExpression.setImageResource(R.mipmap.iv_delete_expression);
        } else {
            viewHolder.ivExpression.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView ivExpression;
    }
}
