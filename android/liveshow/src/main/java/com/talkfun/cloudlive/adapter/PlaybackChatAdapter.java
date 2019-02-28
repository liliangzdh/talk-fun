package com.talkfun.cloudlive.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.imageload.GlideImageLoader;
import com.talkfun.cloudlive.util.DimensionUtils;
import com.talkfun.cloudlive.util.ExpressionUtil;
import com.talkfun.cloudlive.util.TimeUtil;
import com.talkfun.sdk.module.ChatEntity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlaybackChatAdapter extends BAdapter<ChatEntity> {


    public PlaybackChatAdapter(Context context) {
        super(context);
        ExpressionUtil.tvImgWidth = DimensionUtils.dip2px(context, 55);
        ExpressionUtil.tvImgHeight = DimensionUtils.dip2px(context, 45);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleViewHolder simpleViewHolder = null;

        if (convertView == null) {
            convertView = loadView(R.layout.simple_chat_item_layout, null);
            simpleViewHolder = new SimpleViewHolder(convertView);
            convertView.setTag(simpleViewHolder);
        } else {
            simpleViewHolder = (SimpleViewHolder) convertView.getTag();
        }
        ChatEntity chatMessageEntity = (ChatEntity) itemList.get(position);

        if (simpleViewHolder != null) {
            simpleViewHolder.nameTv.setText("");
            String identity = chatMessageEntity.getRole();
            if (identity.equals("user")) {
                simpleViewHolder.identityTv.setVisibility(View.GONE);
            } else if (identity.equals("admin")) {
                simpleViewHolder.identityTv.setVisibility(View.VISIBLE);
                simpleViewHolder.identityTv.setText(convertView.getContext().getResources().getString(R.string.assistants));
            } else if (identity.equals("spadmin")) {
                simpleViewHolder.identityTv.setVisibility(View.VISIBLE);
                simpleViewHolder.identityTv.setText(convertView.getContext().getResources().getString(R.string.teacher));
            } else {
                simpleViewHolder.identityTv.setVisibility(View.GONE);
            }
            String name = chatMessageEntity.getNickname();
            if (name != null & !TextUtils.isEmpty(name)) {
                simpleViewHolder.nameTv.setText(name);
            }
            String time = chatMessageEntity.getTime();
            if (!TextUtils.isEmpty(time)) {
                time = TimeUtil.displayHHMMSS(time);
                if (time != null)
                    simpleViewHolder.timeTv.setText(time);
            }
            String content = chatMessageEntity.getMsg();
            if (content != null & !TextUtils.isEmpty(content)) {
                //  viewHolder.contentTv.setText(content);
                SpannableString spannableString = ExpressionUtil.getExpressionString(convertView.getContext(), content, "mipmap");
                simpleViewHolder.contentTv.setText(spannableString);
            }
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.mipmap.head);
//            requestOptions.circleCrop();
//            Glide.with(context).load(chatMessageEntity.getAvatar()).apply(requestOptions)
//                    .into(simpleViewHolder.ivAvatar);
            GlideImageLoader.create(simpleViewHolder.ivAvatar).loadCircleImage(chatMessageEntity.getAvatar(), R.mipmap.head);
        }
        return convertView;
    }

    class SimpleViewHolder {
        @BindView(R2.id.identity)
        TextView identityTv;
        @BindView(R2.id.name)
        TextView nameTv;
        @BindView(R2.id.content)
        TextView contentTv;
        @BindView(R2.id.time)
        TextView timeTv;
        @BindView(R2.id.iv_avatar)
        ImageView ivAvatar;

        SimpleViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

}
