package com.talkfun.cloudlive.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.entity.ExpressionEntity;
import com.talkfun.cloudlive.event.OnExpressionListener;
import com.talkfun.cloudlive.event.OnSendMessageListener;
import com.talkfun.cloudlive.util.DimensionUtils;
import com.talkfun.cloudlive.util.ExpressionUtil;


import java.lang.ref.WeakReference;


/**
 * Created by Wallace on 2016/12/29.
 */
public class FullScreenInputBarView extends LinearLayout implements OnExpressionListener {
    LinearLayout expressionLayout;
    private EditText etFullScreenInput;
    private ImageView ivFullScreenExpression;
    private InputMethodManager imm;
    private ImageView ivFullScreenSend;
    private PopupWindow popupWindow;
    private int expressionAreaHeight = 60;

    private long preDismissTime = 0L;
    private boolean canInput = true;
    private RelativeLayout rlInputLayout;
    private OnSendMessageListener sendMessageListener;
    private IFocusChangeListener mFocusChangeListener;

    private WeakReference<Context> contextWeak;

    public FullScreenInputBarView(Context context) {
        this(context, null);
    }

    public FullScreenInputBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FullScreenInputBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        contextWeak = new WeakReference<>(context);
        initView();
        initEvent();

    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.layout_fullscreen_edt, null);
        rlInputLayout = (RelativeLayout) view.findViewById(R.id.rl_input_fullScreen_layout);
        etFullScreenInput = (EditText) view.findViewById(R.id.et_fullScreen_input);
        ivFullScreenSend = (ImageView) view.findViewById(R.id.iv_send_fullScreen);
        ivFullScreenExpression = (ImageView) view.findViewById(R.id.iv_expression_fullScreen);
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        this.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        initExpressionPopupWindow();
    }

    public void initExpressionPopupWindow() {
        View view = View.inflate(getContext(), R.layout.popup_expression_layout, null);
        expressionLayout = (LinearLayout) view.findViewById(R.id.ll_expression_view_ipad);
        ExpressionView emotionView = new ExpressionView(getContext(), 11);
        emotionView.setOnEmotionSelectedListener(this);
        expressionLayout.addView(emotionView);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, DimensionUtils.dip2px(getContext(), expressionAreaHeight));
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);


    }

    private void initEvent() {

        etFullScreenInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mFocusChangeListener != null) {
                    mFocusChangeListener.focusChange(hasFocus);
                }
            }
        });
        //点击表情
        ivFullScreenExpression.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canInput) //禁言模式
                    return;
                if (System.currentTimeMillis() - preDismissTime > 100) {
                    showOrCloseExpressionPopupWindow();
                }
            }
        });
        //点击发送
        ivFullScreenSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canInput) //禁言模式
                    return;
                String content = etFullScreenInput.getText().toString().trim();
                sendMessage(content);
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                preDismissTime = System.currentTimeMillis();

            }
        });
    /*    //TODO 这里要监听方向改变之前先销毁弹窗（原因未找到）
        ScreenSwitchUtils.getInstance(contextWeak.get()).setOnSensorChangedListener(new ScreenSwitchUtils.OnSensorChangedListener() {
            @Override
            public void beforeOrientationChange(boolean isPortrait) {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        });*/
    }

    public String getText() {
        return etFullScreenInput.getText().toString();
    }

    public void setText(String text) {
        etFullScreenInput.setText(ExpressionUtil.getExpressionString(getContext(), text, "mipmap"));
        etFullScreenInput.setSelection(text.length());
    }

    //切换全体禁言状态 1 为禁言，0为恢复
    public void setCanInput(boolean value) {
        canInput = value;
        if (!canInput) {
            etFullScreenInput.setHint(getResources().getString(R.string.shutUp_input_tip));
            etFullScreenInput.setMaxLines(1);

            etFullScreenInput.setEnabled(false);
            ivFullScreenSend.setVisibility(INVISIBLE);
            this.setAlpha(0.5f);
        } else {
            etFullScreenInput.setHint(getResources().getString(R.string.i_want_to_chat));
            etFullScreenInput.setMaxLines(10);
            etFullScreenInput.setEnabled(true);
            ivFullScreenSend.setVisibility(VISIBLE);
            this.setAlpha(1.0f);
        }
    }

    /**
     * 重置
     */
    public void reset() {
        etFullScreenInput.setText("");
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    //-----------------------------------------发送消息和表情---------------------------------------

    /**
     * 全屏发送消息
     *
     * @param content
     */
    public void sendMessage(final String content) {
        if (!TextUtils.isEmpty(content)) {
            if (sendMessageListener != null) {
                sendMessageListener.onSendMessage(content);
            }
            etFullScreenInput.setText("");
            imm.hideSoftInputFromWindow(etFullScreenInput.getWindowToken(), 0);
        }
    }

    /**
     * 是否显示全屏表情
     */
    private void showOrCloseExpressionPopupWindow() {
        if (popupWindow == null) return;
        if (!popupWindow.isShowing()) {
            popupWindow.showAsDropDown(this, 0, -this.getHeight() - DimensionUtils.dip2px(getContext(), expressionAreaHeight));
        } else {
            popupWindow.dismiss();
        }
        if (popupWindow != null && mFocusChangeListener != null) {
            mFocusChangeListener.focusChange(popupWindow.isShowing());
        }
    }

    //添加表情
    @Override
    public void OnExpressionSelected(ExpressionEntity entity) {
        if (entity == null) return;
        //添加表情
        String content = etFullScreenInput.getText().toString();
        content += entity.character;
        etFullScreenInput.setText(ExpressionUtil.getExpressionString(getContext(), content, "mipmap"));
        etFullScreenInput.setSelection(content.length());
    }

    //删除表情
    @Override
    public void OnExpressionRemove() {
        String content = etFullScreenInput.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            int selectionStart = etFullScreenInput.getSelectionStart();
            int count = ExpressionUtil.dealContent(getContext(), content, selectionStart);
            content = etFullScreenInput.getText().delete(selectionStart - count, selectionStart).toString();
            etFullScreenInput.setText(ExpressionUtil.getExpressionString(getContext(), content, "mipmap"));
            etFullScreenInput.setSelection(selectionStart - count);
        }
    }

    public void setOnSendMessageListener(OnSendMessageListener listener) {
        this.sendMessageListener = listener;
    }

    public void setOnFocusChangeListener(IFocusChangeListener mFocusChangeListener) {
        this.mFocusChangeListener = mFocusChangeListener;
    }

    public interface IFocusChangeListener {
        void focusChange(boolean isFocus);
    }

    public void hideSoftInput() {
        if (imm == null) return;
        imm.hideSoftInputFromWindow(etFullScreenInput.getWindowToken(), 0);
    }
/*    *//**
     * 判断软键盘是否弹出
     *//*
    public boolean isShowKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        if (imm.hideSoftInputFromWindow(v.getWindowToken(), 0)) {
//            imm.showSoftInput(v, 0);
            return true;
            //软键盘已弹出
        } else {
            return false;
            //软键盘未弹出
        }
    }*/
}
