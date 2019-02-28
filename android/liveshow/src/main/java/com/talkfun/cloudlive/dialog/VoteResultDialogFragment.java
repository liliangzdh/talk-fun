package com.talkfun.cloudlive.dialog;


import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.adapter.VoteResultAdapter;
import com.talkfun.cloudlive.imageload.GlideImageLoader;
import com.talkfun.sdk.module.BriefVoteEntity;
import com.talkfun.sdk.module.VotePubEntity;

import java.util.ArrayList;
import java.util.List;


public class VoteResultDialogFragment extends VoteBaseDialogFragment {
    private static final String TAG = "tag";
    private List<BriefVoteEntity> briefVoteEntityList = new ArrayList<>();
    private VoteResultAdapter voteResultAdapter;
    private VotePubEntity votePubEntity;

    public static VoteResultDialogFragment create(VotePubEntity votePubEntity) {
        VoteResultDialogFragment voteResultFragment = new VoteResultDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, votePubEntity);
        voteResultFragment.setArguments(bundle);
        return voteResultFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        this.votePubEntity = (VotePubEntity) bundle.getSerializable(TAG);
        if (votePubEntity != null && votePubEntity.getBriefVoteEntityList() != null) {
            briefVoteEntityList = votePubEntity.getBriefVoteEntityList();
            voteResultAdapter = new VoteResultAdapter(getActivity());
            voteResultAdapter.setItems(briefVoteEntityList);

        }
        setStyle(STYLE_NO_FRAME, R.style.htVoteStyle);
        setCancelable(false);
    }

    @Override
    void initViewEvent() {
        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showResult(votePubEntity);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (llVoteBody != null)
            llVoteBody.setOrientation(newConfig.orientation);
    }

    private void showResult(VotePubEntity votePubEntity) {
        if (voteResultAdapter == null) {
            briefVoteEntityList = votePubEntity.getBriefVoteEntityList();
            voteResultAdapter = new VoteResultAdapter(getActivity());
            voteResultAdapter.setItems(briefVoteEntityList);
        }
        chooseLv.setAdapter(voteResultAdapter);
        voteBtn.setVisibility(View.GONE);
        String title = votePubEntity.getTitle();
        if (!TextUtils.isEmpty(title) && !title.equals("null")) {
            titleTv.setText(title);
        } else if (!TextUtils.isEmpty(votePubEntity.getLabel())) {
            titleTv.setText(votePubEntity.getLabel());
        }


        //是否有公布正确答案
        String answer = votePubEntity.getAnswer();
        if (!TextUtils.isEmpty(answer)) {
            answerCorrect.setVisibility(View.VISIBLE);
            answerCorrect.setText("正确答案是:" + transformAsciiToChar(answer, 0));

            //自己的选项
            String options = votePubEntity.getOptions();
            if (!TextUtils.isEmpty(options)) {
                answerSelf.setVisibility(View.VISIBLE);
                options = options.substring(1, options.length() - 1);
                answerSelf.setText("你的答案是:" + transformAsciiToChar(options, 1));
            }
        }

        tvPublishTime.setText(String.format("%s %s %s",votePubEntity.getNickname(),votePubEntity.getStartTime(),getString(R.string.runs_vote)));
        //头像
        if (!TextUtils.isEmpty(votePubEntity.getImageUrl())) {
            ivVoteImage.setVisibility(View.VISIBLE);
//            Glide.with(getActivity()).load(votePubEntity.getImageUrl()).into(ivVoteImage);
            GlideImageLoader.create(ivVoteImage).load(votePubEntity.getImageUrl());
        } else {
            ivVoteImage.setVisibility(View.GONE);
        }
    }


    /**
     * 将ascii码转字符显示
     */
    public String transformAsciiToChar(String answer, int l) {
        String correctAnswer = "";
        String[] answers;
        if (TextUtils.isEmpty(answer))
            return null;
        answer = answer.trim();
        if (!answer.contains(",")) {
            answers = new String[1];
            answers[0] = answer;
        } else
            answers = answer.split(",");

        String item = "";
        for (int i = 0; i < answers.length; i++) {
            item = answers[i];
            if (TextUtils.isEmpty(item)) continue;
            item = item.trim();
            char c = (char) (Integer.parseInt(item) + 65 - l);
            if (i != 0)
                correctAnswer += "," + c;
            else
                correctAnswer += c;
        }
        return correctAnswer;
    }
}
