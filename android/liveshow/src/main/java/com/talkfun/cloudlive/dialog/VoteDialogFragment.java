package com.talkfun.cloudlive.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.adapter.VoteAdapter;
import com.talkfun.cloudlive.imageload.GlideImageLoader;
import com.talkfun.cloudlive.util.StringUtils;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.module.VoteEntity;
import com.talkfun.sdk.module.VoteOption;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class VoteDialogFragment extends VoteBaseDialogFragment {
    private List<VoteOption> optionList = new ArrayList<>();
    private VoteEntity voteEntity;
    private boolean isSingleChoose = false;
    private VoteAdapter voteAdapter;
    private boolean isSelected = false;
    private static final String DATA_TAG = "vote";
    private static final String MODE_TAG = "mode";
    private static final String HOLDER = "holder";
    private static final String VOTE_END = "vote_end";
    private int mLastPosition = -1;
    private boolean isUseful = false;
    //多选的标志
    private List<Integer> mLastPositionList = new LinkedList<>();
    private Callback mCallback;
    private boolean voteEnd = false; //投票是否结束

    public static VoteDialogFragment create(VoteEntity voteEntity, boolean isSingleChoose, Callback callback) {
        return create(voteEntity, isSingleChoose, false, callback);
    }

    public static VoteDialogFragment create(VoteEntity voteEntity, boolean isSingleChoose, boolean voteEnd, Callback callback) {
        VoteDialogFragment voteFragment = new VoteDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA_TAG, voteEntity);
        bundle.putBoolean(MODE_TAG, isSingleChoose);
        bundle.putSerializable(HOLDER, callback);
        bundle.putBoolean(VOTE_END, voteEnd);
        voteFragment.setArguments(bundle);
        return voteFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        this.voteEntity = (VoteEntity) bundle.getSerializable(DATA_TAG);
        if (voteEntity != null && voteEntity.getOpList() != null) {
            optionList = voteEntity.getOpList();
            voteAdapter = new VoteAdapter(getActivity());
            voteAdapter.setItems(optionList);
        }
        mCallback = (Callback) bundle.getSerializable(HOLDER);
        isSingleChoose = bundle.getBoolean(MODE_TAG);
        voteEnd = bundle.getBoolean(VOTE_END, false);
        setStyle(STYLE_NO_FRAME, R.style.htVoteStyle);
        setCancelable(false);
    }

    public void initData() {
        mLastPositionList.clear();
        for (int i = 0; i < optionList.size(); i++) {
            if (optionList.get(i).isSelected()) {
                if (isSingleChoose) {
                    mLastPosition = i;
                } else {
                    mLastPositionList.add(i);
                }
            }
        }
        if (mLastPosition != -1 || mLastPositionList.size() > 0) {
            changeVoteBtnStatus(true);
        }
    }

    /**
     * 初始化控件的事件
     */
    @Override
    void initViewEvent() {
        if (voteAdapter != null) {
            chooseLv.setAdapter(voteAdapter);
        }
        voteBtn.setOnClickListener(clickListener);
        cancelImg.setOnClickListener(clickListener);
        chooseLv.setOnItemClickListener(itemClickListener);
        chooseLv.setItemsCanFocus(false);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (voteEntity != null)
            showVoteStart(voteEntity);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void showVoteStart(VoteEntity voteEntity) {

        optionList = voteEntity.getOpList();
        if (voteAdapter != null) {
            voteAdapter.setItems(optionList);
        } else {
            voteAdapter = new VoteAdapter(getActivity());
            chooseLv.setAdapter(voteAdapter);
        }
        String title = null;
        if (isSingleChoose) {
            title = context.getResources().getString(R.string.single_choice);
            chooseLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            voteAdapter.setCheckboxStyle(true);
        } else {
            title = context.getResources().getString(R.string.multiple_choice);
            chooseLv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            voteAdapter.setCheckboxStyle(false);
        }
        if (!TextUtils.isEmpty(voteEntity.getTitle()) && !voteEntity.getTitle().equals("null")) {
            title += voteEntity.getTitle();
        } else if (!TextUtils.isEmpty(voteEntity.getLabel())) {
            title += voteEntity.getLabel();
        }
        String count = String.format("\t<共%s个选项>", optionList == null ? 0 : optionList.size());
        titleTv.setText(title + count);
        tvPublishTime.setText(String.format("%s %s %s", voteEntity.getNickname(), voteEntity.getStartTime(), getString(R.string.runs_vote)));
        if (!TextUtils.isEmpty(voteEntity.getImageUrl())) {
            ivVoteImage.setVisibility(View.VISIBLE);
//            Glide.with(getActivity()).load(voteEntity.getImageUrl()).into(ivVoteImage);
            GlideImageLoader.create(ivVoteImage).load(voteEntity.getImageUrl());
        } else {
            ivVoteImage.setVisibility(View.GONE);
        }

        if (voteEnd) {
            voteBtn.setText("投票已结束");
            chooseLv.setItemsCanFocus(false);
            voteAdapter.disableAllItemChoser();
            changeVoteBtnStatus(false);
        } else if (voteEntity.isVoted()) {
            voteBtn.setText("已投票");
            chooseLv.setItemsCanFocus(false);
            //chooseLv.setEnabled(false);
            voteAdapter.disableAllItemChoser();
            changeVoteBtnStatus(false);
        } else {
            voteAdapter.enableItemChoser();
            voteBtn.setText("投票");
        }
    }


    /**
     * 将投票按钮设置为可点击状态
     */
    private void changeVoteBtnStatus(boolean isSelected) {
        Context context = getActivity();
        if (isSelected) {
            voteBtn.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ht_vote_btn_checked_bg));
            voteBtn.setClickable(true);
        } else {
            voteBtn.setBackgroundDrawable(
                    context.getResources().getDrawable(R.drawable.ht_vote_btn_unchecked_bg));
            voteBtn.setClickable(false);
        }
    }

    int count = 0;

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //判断选中的条数,并根据单选/多选 来设置投票按钮的状态
            if (isSingleChoose) {
                //先将选中的项置为相反状态
                optionList.get(position).setIsSelected(!optionList.get(position).isSelected());
                voteAdapter.notifyDataSetChanged();
                isSelected = true;
                //如果选中的项跟上一次选中的项是一样的.则将上次选中的标志位重置为-1
                if (mLastPosition == position) {
                    isSelected = false;
                    mLastPosition = -1;
                }
                // 将上次选中的项重置为为选中状态
                if (mLastPosition >= 0) {
                    optionList.get(mLastPosition).setIsSelected(false);
                }
                //将当前选中的项赋予上次选中项的标志位
                if (isSelected)
                    mLastPosition = position;
            } else {
                //用于纪录当前点击项是否点击过.若点击过.则移除该项
                boolean isChecked = false;
                //当前选项时候已被选择,如果有则移除
                if (mLastPositionList.contains(position)) {
                    isChecked = true;
                    mLastPositionList.remove(mLastPositionList.indexOf(position));
                    voteAdapter.notifyDataSetChanged();
                }
                if (mLastPositionList.size() >= voteEntity.getOptional()) {
                    //提示用户选项已达上限
                    StringUtils.tip(context, context.getResources().getString(R.string.ht_achieve_limit));
                    return;
                } else {
                    optionList.get(position).setIsSelected(!optionList.get(position).isSelected());
                    voteAdapter.notifyDataSetChanged();
                    //不添加点击过的项
                    if (!isChecked)
                        mLastPositionList.add(position);
                }
                isSelected = mLastPositionList.size() > 0;
            }
            changeVoteBtnStatus(isSelected);
        }
    };

    //点击事件
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R2.id.vote) {
                sendVote();
            } else if (id == R2.id.cancel) {
                dismiss();
            }
        }
    };

    //投票
    private void sendVote() {
        if (voteEntity != null) {
            List<Integer> integerList = new ArrayList<>();
            for (int i = 0; i < optionList.size(); i++) {
                if (optionList.get(i).isSelected()) {
                    integerList.add(i + 1);
                }
            }
            if (integerList.size() > 0) {
                if (isUseful && mCallback != null) {
                    Toast.makeText(getActivity(), "已经投票过了", Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }
                HtSdk.getInstance().sendVote(voteEntity.getVoteId(), integerList.toString(), mCallback);
                voteEntity.setVoted(true);
                dismiss();
            }
        }
    }
}
