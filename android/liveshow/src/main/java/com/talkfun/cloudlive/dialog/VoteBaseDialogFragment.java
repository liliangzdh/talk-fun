package com.talkfun.cloudlive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.util.DimensionUtils;
import com.talkfun.cloudlive.view.LargeImagePopupWindow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public abstract class VoteBaseDialogFragment extends BaseDialogFragment {
    protected Context context;
    protected View layout;
    @BindView(R2.id.cancel)
    ImageView cancelImg;
    @BindView(R2.id.tv_sub_title)
    TextView tvPublishTime;
    @BindView(R2.id.tv_content_title)
    TextView titleTv;
    @BindView(R2.id.vote)
    Button voteBtn;
    @BindView(R2.id.lv_option)
    ListView chooseLv;
    @BindView(R2.id.iv_vote_image)
    ImageView ivVoteImage;
    @BindView(R2.id.ll_vote_body)
    LinearLayout llVoteBody;
    @BindView(R2.id.answer_yourself)
    TextView answerSelf;
    @BindView(R2.id.answer_correct)
    TextView answerCorrect;

    private FragmentManager fragmentManager;
    private String flag;
    private LargeImagePopupWindow largeImagePopupWindow;
    private Unbinder unbind;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        context = getActivity();
        layout = inflater.inflate(R.layout.ht_vote_layout, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        unbind = ButterKnife.bind(this, layout);
        initViewEvent();
        initPopupWindow();
        return layout;
    }

    public void initPopupWindow() {
        largeImagePopupWindow = new LargeImagePopupWindow(getActivity());
        largeImagePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (fragmentManager != null && !TextUtils.isEmpty(flag))
                    show(fragmentManager, flag);
            }
        });
    }

    abstract void initViewEvent();

    @OnClick(R2.id.iv_vote_image)
    public void onClick(View v) {
        showLargeImage();
    }


    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        fragmentManager = manager;
        flag = tag;
        super.show(manager, tag);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (largeImagePopupWindow != null) {
            largeImagePopupWindow = null;
            initPopupWindow();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbind.unbind();
    }

    private void showLargeImage() {
        final Dialog dialog = new Dialog(getContext());
        Window win = dialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
        win.setWindowAnimations(R.style.popwin_anim_style);
        win.setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View viewDialog = inflater.inflate(R.layout.dialog_large_picture, null);
        ImageView imageView = (ImageView) viewDialog.findViewById(R.id.iv_large_image);
        imageView.setImageDrawable(ivVoteImage.getDrawable());
        int width = DimensionUtils.getScreenWidth(getContext());
        int height = DimensionUtils.getScreenHeight(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        dialog.setContentView(viewDialog, layoutParams);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

            }
        });

    }
}
