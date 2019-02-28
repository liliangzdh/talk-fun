package com.talkfun.cloudlive.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.adapter.AutoScrollAdapter;
import com.talkfun.cloudlive.manager.ScrollSpeedLinearLayoutManger;
import com.talkfun.sdk.module.LotteryResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 抽奖对话框
 */
public class LotteryDialogFragment extends BaseDialogFragment implements View.OnClickListener {
    @BindView(R2.id.rv_scroll_left)
    RecyclerView recyclerLeft;
    @BindView(R2.id.rv_scroll_center)
    RecyclerView recyclerCenter;
    @BindView(R2.id.rv_scroll_right)
    RecyclerView recyclerRight;
    @BindView(R2.id.lottery_area)
    LinearLayout lotteryLinearLayout;
    @BindView(R2.id.cancel)
    ImageView cancelImg;
    @BindView(R2.id.winner)
    TextView textView;
    @BindView(R2.id.lottery_bg)
    RelativeLayout lotteryBg;
    @BindView(R2.id.my_cancel)
    ImageView centerCancel;
    //private TextView myName;
    //private boolean lotteryStatus = false;
    private LotteryResult lotteryResult;
    private int status = 0;
    private static final int LOTTERY_IDLE = 0;
    private static final int LOTTERY_START = 1;
    private static final int LOTTERY_STOP = 2;

    private Integer[] imags = {R.mipmap.red_bag, R.mipmap.red_bag, R.mipmap.red_bag, R.mipmap.red_bag, R.mipmap.red_bag};
    private AutoScrollAdapter[] autoScrollAdapter = new AutoScrollAdapter[3];
    private int playAdds = 10000;

    public static LotteryDialogFragment create() {
        LotteryDialogFragment lotteryFragment = new LotteryDialogFragment();
        Bundle bundle = new Bundle();
        lotteryFragment.setArguments(bundle);
        return lotteryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.loadingFragment);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.lottery_layout, container);
        ButterKnife.bind(this, layout);
        init();
        initData();
        cancelImg.setOnClickListener(this);
        centerCancel.setOnClickListener(this);
        return layout;
    }

    private void init() {
        ScrollSpeedLinearLayoutManger scrollSpeedLinearLayoutManger = new ScrollSpeedLinearLayoutManger(getActivity());
        scrollSpeedLinearLayoutManger.setSpeedSlow();
        ScrollSpeedLinearLayoutManger scrollSpeedLinearLayoutManger2 = new ScrollSpeedLinearLayoutManger(getActivity());
        scrollSpeedLinearLayoutManger2.setSpeedSlow();
        ScrollSpeedLinearLayoutManger scrollSpeedLinearLayoutManger3 = new ScrollSpeedLinearLayoutManger(getActivity());
        scrollSpeedLinearLayoutManger3.setSpeedSlow();
        recyclerLeft.setLayoutManager(scrollSpeedLinearLayoutManger);
        recyclerCenter.setLayoutManager(scrollSpeedLinearLayoutManger2);
        recyclerRight.setLayoutManager(scrollSpeedLinearLayoutManger3);
    }

    private void initData() {

        ArrayList<Integer> playSequence = new ArrayList<>(Arrays.asList(imags));
        for (int scrollView = 0; scrollView < autoScrollAdapter.length; scrollView++) {
            autoScrollAdapter[scrollView] = new AutoScrollAdapter(playSequence, getActivity());
        }

        recyclerLeft.setAdapter(autoScrollAdapter[0]);
        recyclerCenter.setAdapter(autoScrollAdapter[1]);
        recyclerRight.setAdapter(autoScrollAdapter[2]);
    }


    public void setLotteryInfo(LotteryResult result) {
        lotteryResult = result;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (status == LOTTERY_START)
            performLotteryStart();
        else if (status == LOTTERY_STOP && lotteryResult != null)
            performLotteryStop(lotteryResult);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void lotteryStart() {
        this.lotteryResult = null;
        status = LOTTERY_START;
    }

    public void lotteryStop(LotteryResult lotteryResult) {
        this.lotteryResult = lotteryResult;
        status = LOTTERY_STOP;
    }

    private void performLotteryStart() {
        resetView();
        start();
    }

    private void start() {
        recyclerLeft.smoothScrollToPosition(playAdds);
        recyclerCenter.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerCenter.smoothScrollToPosition(playAdds);
                recyclerRight.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerRight.smoothScrollToPosition(playAdds);
                        playAdds = playAdds + imags.length * 4;
                    }
                }, 300);
            }
        }, 300);

    }

    private void stop() {
        if (recyclerLeft != null) {
            recyclerLeft.stopScroll();
            recyclerLeft = null;
        }
        if (recyclerCenter != null) {
            recyclerCenter.stopScroll();
            recyclerCenter = null;
        }
        if (recyclerRight != null) {
            recyclerRight.stopScroll();
            recyclerRight = null;
        }
        if (lotteryLinearLayout != null) {
            lotteryLinearLayout.setVisibility(View.INVISIBLE);
        }

    }

    private void resetView() {
        if (lotteryLinearLayout != null) {
            lotteryLinearLayout.setVisibility(View.VISIBLE);
        }
        textView.setVisibility(View.INVISIBLE);
//        cancelImg.setVisibility(View.INVISIBLE);
    }


    public void performLotteryStop(LotteryResult lotteryResult) {
        stop();
        if (lotteryResult == null || lotteryResult.getResult() == null || lotteryResult.getResult().isEmpty()) {
            dismissAllowingStateLoss();
            return;
        }
        String resultStr = "";
        List<LotteryResult.ResultItem> list = lotteryResult.getResult();
        LotteryResult.ResultItem item = null;
        for (int i = 0, len = list.size(); i < len; i++) {
            item = list.get(i);
            if (item != null) {
                resultStr += (i + 1) + "." + item.nickname;
            }
            if (i < len - 1) {
                resultStr += " ";
            }
        }
        textView.setText(resultStr);
        textView.setVisibility(View.VISIBLE);
        cancelImg.setVisibility(View.VISIBLE);
        cancelImg.setClickable(true);
        centerCancel.setVisibility(View.INVISIBLE);
        centerCancel.setClickable(false);
        lotteryBg.setBackgroundResource(R.mipmap.lottery_result);
    }


    @Override
    public void onClick(View v) {
        stop();
        dismissAllowingStateLoss();
    }

}