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
import com.talkfun.cloudlive.consts.EventType;
import com.talkfun.cloudlive.entity.ChatDisableAllStatusEntity;
import com.talkfun.cloudlive.entity.Event;
import com.talkfun.cloudlive.entity.SignEndEntity;
import com.talkfun.cloudlive.entity.SignEntity;
import com.talkfun.cloudlive.imageload.GlideImageLoader;
import com.talkfun.cloudlive.util.DimensionUtils;
import com.talkfun.cloudlive.util.EventBusUtil;
import com.talkfun.cloudlive.util.ExpressionUtil;
import com.talkfun.cloudlive.util.TimeUtil;
import com.talkfun.sdk.consts.MemberRole;
import com.talkfun.sdk.module.BroadcastEntity;
import com.talkfun.sdk.module.ChatEntity;
import com.talkfun.sdk.module.LotteryResult;
import com.talkfun.sdk.module.VoteEntity;
import com.talkfun.sdk.module.VotePubEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChatAdapter extends BAdapter<Object> {
    private String message;
    private int icon;

    private final int TYPE_COUNT = 4;
    /**
     * 聊天
     */
    private final int CHAT = 0;
    /**
     * 广播或者是通知
     */
    private final int NOTICEORBROADCAST = 1;
    /**
     * 投票
     */
    private final int VOTE = 2;
    /**
     * 某一些固定值:本地自定的值插入聊天的
     */
    private int FIXEDVALUE = 3;

    public ChatAdapter(Context context) {
        super(context);
        ExpressionUtil.tvImgWidth = DimensionUtils.dip2px(context, 55);
        ExpressionUtil.tvImgHeight = DimensionUtils.dip2px(context, 45);
    }

    public void appendList(Object chatMessageEntity) {
        itemList.add(chatMessageEntity);
        if (itemList.size() >= MXA_LENGTH) {
            itemList.remove(0);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Object object = itemList.get(position);
        if (object instanceof ChatEntity) {
            return CHAT;
        } else if (object instanceof VotePubEntity || object instanceof VoteEntity) {
            return VOTE;
        } else if (object instanceof String) {
            return FIXEDVALUE;
        }
        return NOTICEORBROADCAST;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleViewHolder simpleViewHolder = null;
        SystemViewHolder systemViewHolder = null;
        SystemVoteViewHolder systemVoteViewHolder = null;
        int type = getItemViewType(position);
        Object object = itemList.get(position);
        //聊天
        if (type == CHAT) {
            //-------------------------------------------------聊天-----------------------------------------------------------------
            if (convertView == null) {
                convertView = loadView(R.layout.simple_chat_item_layout, null);
                simpleViewHolder = new SimpleViewHolder(convertView);
                convertView.setTag(simpleViewHolder);
            } else {
                simpleViewHolder = (SimpleViewHolder) convertView.getTag();
            }
            setChatMessage(simpleViewHolder, object);
        } else if (type == VOTE) {
            //-------------------------------------------------投票-----------------------------------------------------------------
            if (convertView == null) {
                convertView = loadView(R.layout.vote_chat_item_layout, null);
                systemVoteViewHolder = new SystemVoteViewHolder(convertView);
                convertView.setTag(systemVoteViewHolder);
            } else {
                systemVoteViewHolder = (SystemVoteViewHolder) convertView.getTag();
            }
            setVoteMessage(systemVoteViewHolder, object);

        } else {
            //---------------------------------------------广播与通知--------------------------------------------------------------------------------
            if (convertView == null) {
                convertView = loadView(R.layout.system_chat_item_layout, null);
                systemViewHolder = new SystemViewHolder(convertView);
                convertView.setTag(systemViewHolder);
            } else {
                systemViewHolder = (SystemViewHolder) convertView.getTag();
            }
            setNoticeOrBroadCastMessage(systemViewHolder, object);

        }
        return convertView;
    }

    private void setNoticeOrBroadCastMessage(SystemViewHolder systemViewHolder, Object object) {
        if (object instanceof BroadcastEntity) {
            //系统广播
            BroadcastEntity entity = (BroadcastEntity) object;
            message = context.getResources().getString(R.string.public_broadcast) + entity.getMessage();
            icon = R.mipmap.ht_broadcast;
        } else if (object instanceof ChatDisableAllStatusEntity) {
            ChatDisableAllStatusEntity chatDisableAllStatusEntity = (ChatDisableAllStatusEntity) object;
            if (chatDisableAllStatusEntity.isDisable()) {
                message = context.getString(R.string.public_broadcast) + context.getString(R.string.shutUp_all_open);
            } else {
                message = context.getString(R.string.public_broadcast) + context.getString(R.string.shutUp_all_close);
            }
            icon = R.mipmap.ht_broadcast;
        } else {
            //------------------------------------通知-----------------------------------------------------------
            icon = R.mipmap.ht_notify;
            if (object instanceof LotteryResult) {
                //抽奖结束
                LotteryResult mLotteryResult = (LotteryResult) object;
                List<LotteryResult.ResultItem> resultItemList = mLotteryResult.getResult();
                StringBuilder nickName = new StringBuilder();
                for (LotteryResult.ResultItem resultItem : resultItemList) {
                    nickName.append(resultItem.nickname + "、");
                }
                nickName.deleteCharAt(nickName.length() - 1);
                String notify = context.getResources().getString(R.string.ht_notify);
                message = String.format(notify, resultItemList.get(0).launch_nickname, nickName);
            } else if (object instanceof SignEntity) {
                //签到开始
                SignEntity signEntity = (SignEntity) object;
                String notify = context.getResources().getString(R.string.ht_sign_start);
                message = String.format(notify, signEntity.getNickname(), signEntity.getTime());
            } else if (object instanceof SignEndEntity) {
                //签到结束
                String notify = context.getResources().getString(R.string.ht_sign_stop);
                SignEndEntity signEndEntity = (SignEndEntity) object;
                message = String.format(notify, signEndEntity.getSignTotal(), signEndEntity.getTotal());
            } else if (object instanceof String) {
                message = (String) object;
            }
        }
        systemViewHolder.systemImg.setImageResource(icon);
        systemViewHolder.systemMsg.setText(message);
    }

    /**
     * 设置投票信息
     *
     * @param systemVoteViewHolder
     * @param object
     */
    private void setVoteMessage(SystemVoteViewHolder systemVoteViewHolder, final Object object) {
        if (object instanceof VoteEntity) {
            VoteEntity mVoteEntity = (VoteEntity) object;
            systemVoteViewHolder.checkVote.setText("查看");
            String notify = context.getResources().getString(R.string.ht_vote_new_notify);
            message = String.format(notify, mVoteEntity.getNickname(), mVoteEntity.getNoticeTime());
        } else {
            VotePubEntity mVotePubEntity = (VotePubEntity) object;
            systemVoteViewHolder.checkVote.setText("查看结果");
            String notify = context.getResources().getString(R.string.ht_vote_result_notify);
            message = String.format(notify, mVotePubEntity.getNickname(), mVotePubEntity.getNoticeTime());
        }
        systemVoteViewHolder.systemMsg.setText(message);

        systemVoteViewHolder.checkVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (object instanceof VoteEntity) {
                    EventBusUtil.postEvent(new Event(EventType.LOOKOVERVOTES, object));
//                    LiveVoteDialogHelper.getInstance(context).voteStart((VoteEntity) object);
                } else {
                    EventBusUtil.postEvent(new Event(EventType.LOOKOVERVOTERESULTS, object));
//                    LiveVoteDialogHelper.getInstance(context).voteStop((VotePubEntity) object);
                }
            }
        });
    }

    /**
     * 设置聊天信息
     *
     * @param simpleViewHolder
     * @param object
     */
    private void setChatMessage(SimpleViewHolder simpleViewHolder, Object object) {
        ChatEntity chatMessageEntity = (ChatEntity) object;
        if (simpleViewHolder != null) {
            simpleViewHolder.nameTv.setText("");
            String identity = chatMessageEntity.getRole();
            if (identity.equals("user")) {
                simpleViewHolder.identityTv.setVisibility(View.GONE);
            } else if (identity.equals(MemberRole.MEMBER_ROLE_SUPER_ADMIN)) {
                simpleViewHolder.identityTv.setVisibility(View.VISIBLE);
                simpleViewHolder.identityTv.setText(context.getResources().getString(R.string.teacher));
            } else if (identity.equals(MemberRole.MEMBER_ROLE_ADMIN)) {
                simpleViewHolder.identityTv.setVisibility(View.VISIBLE);
                simpleViewHolder.identityTv.setText(context.getResources().getString(R.string.assistants));
            } else {
                simpleViewHolder.identityTv.setVisibility(View.GONE);
            }
            String name = chatMessageEntity.getNickname();
            if (name != null & !TextUtils.isEmpty(name)) {
                simpleViewHolder.nameTv.setText(name);
            }
            String time = chatMessageEntity.getTime();
            simpleViewHolder.timeTv.setText("");
            if (!TextUtils.isEmpty(time) && TextUtils.isDigitsOnly(time)) {
                time = TimeUtil.displayTime(time);
                if (time != null)
                    simpleViewHolder.timeTv.setText(time);
            } else {
                simpleViewHolder.timeTv.setText(time);
            }
            String content = chatMessageEntity.getMsg();
            if (content != null & !TextUtils.isEmpty(content)) {
                SpannableString spannableString = ExpressionUtil.getExpressionString(context, content, "mipmap");
                simpleViewHolder.contentTv.setText(spannableString);
            }
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.mipmap.head);
//            requestOptions.circleCrop();
//            Glide.with(context).load(chatMessageEntity.getAvatar()).apply(requestOptions)
//                    .into(simpleViewHolder.ivAvatar);

            GlideImageLoader.create(simpleViewHolder.ivAvatar).loadCircleImage(chatMessageEntity.getAvatar(), R.mipmap.head);


        }

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

    class SystemViewHolder {
        @BindView(R2.id.system_msg)
        TextView systemMsg;
        @BindView(R2.id.icon)
        ImageView systemImg;

        SystemViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

    class SystemVoteViewHolder {
        @BindView(R2.id.check_vote)
        TextView checkVote;
        @BindView(R2.id.system_msg)
        TextView systemMsg;

        SystemVoteViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
