package com.talkfun.cloudlive.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.talkfun.cloudlive.R;

/**
 * Created by asus on 2016/10/12.
 */

public class ClearEditText extends AppCompatEditText implements TextWatcher, View.OnFocusChangeListener {

    private Drawable clearDrawable;
    private OnFocusChangeListener mOnFocusChangeListener;
    private OnTouchListener mOnTouchListener;

    public ClearEditText(Context context) {
        super(context);
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        clearDrawable = getCompoundDrawables()[2];
        if (clearDrawable == null) {
            clearDrawable = getResources().getDrawable(R.mipmap.delete);
        }
        clearDrawable.setBounds(0, 0, clearDrawable.getIntrinsicWidth(), clearDrawable.getIntrinsicHeight());
        setClearDrawableVisible(false);
        super.setOnFocusChangeListener(this);
        super.addTextChangedListener(this);

    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        mOnFocusChangeListener = l;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        mOnTouchListener = l;
    }


    protected void setClearDrawableVisible(boolean visible) {

        clearDrawable.setVisible(visible, false);
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], visible ? clearDrawable : null, compoundDrawables[3]);
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (isFocused()) {
            setClearDrawableVisible(text.length() > 0);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            setClearDrawableVisible(getText().length() > 0);
        } else {
            setClearDrawableVisible(false);
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(v, hasFocus);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (clearDrawable != null && clearDrawable.isVisible() && event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int drawableY = (getHeight() - clearDrawable.getIntrinsicHeight()) / 2;
            int drawableWidth = clearDrawable.getIntrinsicWidth();
            boolean isTouchClearX = x >= getWidth() - getPaddingRight() - drawableWidth * 1.5 && x < getWidth() - getPaddingRight() + drawableWidth * 0.5;
            // boolean isTouchClaerY = y >= drawableY && y <= drawableY + clearDrawable.getIntrinsicHeight();
            if (isTouchClearX) {
                this.setText("");
            }
        }
        return super.onTouchEvent(event);

    }


}
