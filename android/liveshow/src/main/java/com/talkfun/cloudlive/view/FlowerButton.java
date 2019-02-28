package com.talkfun.cloudlive.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class FlowerButton extends RelativeLayout {

    //按钮背景
    private ImageView buttonBg;
    //红点
    private TextView redPoint;

    public FlowerButton(Context context) {
        this(context, null);
    }

    public FlowerButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void initView() {

    }



    public void setButtonBg(int resourceId) {
        buttonBg.setImageResource(resourceId);
    }


}
