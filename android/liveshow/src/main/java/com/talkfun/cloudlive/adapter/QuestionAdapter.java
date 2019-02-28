package com.talkfun.cloudlive.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.imageload.GlideImageLoader;
import com.talkfun.cloudlive.util.TimeUtil;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.module.QuestionEntity;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;


public class QuestionAdapter extends BAdapter<QuestionEntity> {
//    private List<QuestionEntity> questionEntityList = new LinkedList<>();
//    private LayoutInflater layoutInflater;
//    private int MAX_LENGTH = 200;
    /***
     * 记录上个点
     */
    private String flagNow;

    public QuestionAdapter(Context context) {
        super(context);
    }

//    public void setData(List<QuestionEntity> list) {
//        questionEntityList.clear();
//        if (list != null)
//            questionEntityList.addAll(list);
//        notifyDataSetChanged();
//    }

    public void appendQuestion(QuestionEntity questionEntity) {
        // String sn = questionEntity.getSn();
        if (questionEntity.isAnswer()) {   // 说明是答案 ,找到插入答案的位置，并插入
            int temp = itemList.size();
            //String qid = questionEntity.getQid();
            String replyId = questionEntity.getReplyId();
            QuestionEntity tmpQuestion;
            for (int i = temp - 1; i >= 0; i--) {
                tmpQuestion = itemList.get(i);
                if (tmpQuestion.getId().equals(replyId) || (!TextUtils.isEmpty(tmpQuestion.getReplyId()) && tmpQuestion.getReplyId().equals(replyId))) {
                    temp = ++i;
                    break;
                }
            }
            itemList.add(temp, questionEntity);
        } else { // 说明是 问题.如果是问题还要根据 sn  排序
       /*     if (questionEntityList.size() > 0) {
                for (int i = 0, j = questionEntityList.size(); i < j; i++) {
                    QuestionEntity tempQuestionEntity = questionEntityList.get(i);
                    int tempSn = Integer.valueOf(tempQuestionEntity.getSn());
                    //如果是问题.则通过比较 sn 来排序
                    //如果是回答,则无需比较,直接对比下一条
                    if (tempSn > 0) {
                        int newEntitySn = Integer.valueOf(sn);
                        // 新的问题号小于该项的问题号,说明新问题项在该项问题的前面
                        if (newEntitySn < tempSn) {
                            if (i == 0) {
                                questionEntityList.add(0, questionEntity);
                            } else {
                                questionEntityList.add(i, questionEntity);
                            }
                            break;
                        }
                    }
                    if (i == j - 1)
                        questionEntityList.add(questionEntity);
                }
            } else {
                questionEntityList.add(questionEntity);
            }*/
            itemList.add(questionEntity);
        }
        if (itemList.size() >= MXA_LENGTH) {
            itemList.remove(0).release();
        }
        notifyDataSetChanged();
    }

    public void deleteQuestion(String qId) {
        if (TextUtils.isEmpty(qId) || itemList == null || itemList.size() == 0) {
            return;
        }
        Iterator<QuestionEntity> iterable = itemList.iterator();
        while (iterable.hasNext()) {
            QuestionEntity questionEntity = iterable.next();
            if (qId.equals(questionEntity.getId()) || qId.equals(questionEntity.getReplyId())) {
                iterable.remove();

            }
        }
        notifyDataSetChanged();
    }

//    @Override
//    public int getCount() {
//        return questionEntityList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return questionEntityList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = loadView(R.layout.question_item_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        QuestionEntity questionEntity = itemList.get(position);
//        QuestionEntity questionEntity = questionEntityList.get(position);
        //if (!questionEntity.getSn().equals("-1") && !questionEntity.getSn().equals("0")) {
        if (!questionEntity.isAnswer()) {
            viewHolder.emptyView.setVisibility(View.GONE);
            convertView.setPadding(0, 18, 0, 0);
        } else {
//            viewHolder.dividLine.setVisibility(View.GONE);
//            viewHolder.questionNum.setVisibility(View.GONE);
            viewHolder.emptyView.setVisibility(View.VISIBLE);
            convertView.setPadding(0, 22, 0, 0);
        }
        flagNow = questionEntity.getSn();
        viewHolder.nameTv.setText(questionEntity.getNickname());
        String identity = questionEntity.getRole();
        if (identity.equals("spadmin")) {
            viewHolder.identityTv.setVisibility(View.VISIBLE);
            viewHolder.identityTv.setText(convertView.getContext().getResources().getString(R.string.teacher));

        } else if (identity.equals("admin")) {
            viewHolder.identityTv.setVisibility(View.VISIBLE);
            viewHolder.identityTv.setText(convertView.getContext().getResources().getString(R.string.assistants));
        } else {
            viewHolder.identityTv.setVisibility(View.GONE);
        }
        String name = questionEntity.getNickname();
        if (name != null & !TextUtils.isEmpty(name)) {
            viewHolder.nameTv.setText(name);
        }
        String content = questionEntity.getContent();
        if (content != null & !TextUtils.isEmpty(content)) {
            viewHolder.contentTv.setText(content);
        }
        viewHolder.timeTv.setText("");
        String time = questionEntity.getTime();
        if (!TextUtils.isEmpty(time)) {
            if (HtSdk.getInstance().isPlayback())
                time = TimeUtil.displayHHMMSS(time);
            else time = TimeUtil.displayTime(time);
            viewHolder.timeTv.setText(time);
        }
        //头像
//        RequestOptions requestOptions = new RequestOptions();
//        requestOptions.circleCrop();
//        requestOptions.placeholder(R.mipmap.head);
//        Glide.with(convertView.getContext()).load(questionEntity.getAvatar())
//                .apply(requestOptions)
//                .into(viewHolder.ivAvatar);
        GlideImageLoader.create(viewHolder.ivAvatar).loadCircleImage(questionEntity.getAvatar(), R.mipmap.head);
        return convertView;
    }

    class ViewHolder {
        @BindView(R2.id.identity)
        TextView identityTv;
        @BindView(R2.id.name)
        TextView nameTv;
        @BindView(R2.id.content)
        TextView contentTv;
        @BindView(R2.id.question_num)
        TextView questionNum;
        @BindView(R2.id.divider)
        View dividLine;
        @BindView(R2.id.time)
        TextView timeTv;
        @BindView(R2.id.empty_view)
        View emptyView;
        @BindView(R2.id.iv_avatar)
        ImageView ivAvatar;

        ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }

    }
}
