package com.talkfun.cloudlive.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.entity.ExpressionEntity;
import com.talkfun.cloudlive.event.OnExpressionListener;
import com.talkfun.cloudlive.event.OnSendFlowerListener;
import com.talkfun.cloudlive.event.OnSendMessageListener;
import com.talkfun.cloudlive.util.DimensionUtils;
import com.talkfun.cloudlive.util.ExpressionUtil;
import com.talkfun.cloudlive.util.ScreenSwitchUtils;

/**
 * Created by Wallace on 2017/2/9.
 */
public class InputBarView extends LinearLayout implements OnExpressionListener, TextWatcher, View.OnClickListener {
    private EditText inputEdt;
    private TextView sendBtn;
    private ImageView expressionBtn;
    private RelativeLayout sendFlower;
    private TextView flowerNumTv;
    private ImageView flowerBtn;
    private InputMethodManager imm;
    private boolean isOpen = false;
    private ViewGroup.LayoutParams inputParams;
    private LinearLayout expressionLayout;
    private String sendContent;
    private int currentItem = 0;
    private int flowerNum = 0;
    private Handler handler;
    public PopupWindow popupWindow;
    private int expressionAreaHeight = 90;
    private long preDismissTime = 0L;
    private int pptWidth = 0;
    private boolean canInput = true;
    private boolean canSendFlower = true;
    private OnSendMessageListener sendMessageListener;
    private OnSendFlowerListener sendFlowerListener;

    public InputBarView(Context context) {
        this(context, null);
    }

    public InputBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handler = new Handler();
        initView();
        initEvent();
    }

    public void initView() {
        View view = View.inflate(getContext(), R.layout.edt, null);
        inputEdt = (EditText) view.findViewById(R.id.input_edt);
        sendBtn = (TextView) view.findViewById(R.id.send_btn);
        expressionBtn = (ImageView) view.findViewById(R.id.iv_expression);
        sendFlower = (RelativeLayout) view.findViewById(R.id.btn_send);
        flowerNumTv = (TextView) view.findViewById(R.id.flower_num);
        flowerBtn = (ImageView) view.findViewById(R.id.flower_btn);
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        this.addView(view);
        initExpressionPopupWindow();
    }

    public void updateInputBarWidth(int width) {
        pptWidth = width;
    }

    /**
     * 初始化表情弹窗
     */
    public void initExpressionPopupWindow() {
        View view = View.inflate(getContext(), R.layout.popup_expression_layout, null);
        expressionLayout = (LinearLayout) view.findViewById(R.id.ll_expression_view_ipad);
        expressionLayout.setBackgroundResource(R.drawable.expresssion_bg);
        ExpressionView emotionView = new ExpressionView(getContext(), 7);
        emotionView.setOnEmotionSelectedListener(this);
        expressionLayout.addView(emotionView);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, DimensionUtils.dip2px(getContext(), expressionAreaHeight));
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
    }


    public void initEvent() {
        sendBtn.setOnClickListener(this);
        expressionBtn.setOnClickListener(this);
        inputEdt.setOnClickListener(this);
        sendFlower.setOnClickListener(this);
        inputEdt.addTextChangedListener(this);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                preDismissTime = System.currentTimeMillis();
                if (!isOpen && ScreenSwitchUtils.getInstance(getContext()).isSensorSwitchLandScreen()) {
                    switchInputAreaLength(false);
                }
            }
        });
//        SoftKeyboardStateWatcher stateWatcher = new SoftKeyboardStateWatcher(this);
//        stateWatcher.addSoftKeyboardStateListener(new SoftKeyboardStateWatcher.SoftKeyboardStateListener() {
//            @Override
//            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
//                if (!ScreenSwitchUtils.getInstance(getContext()).isPortrait()) {
//                    switchInputAreaLength(true);
//                }
//
//            }
//
//            @Override
//            public void onSoftKeyboardClosed() {
//                ScreenSwitchUtils screenSwitchUtils = ScreenSwitchUtils.getInstance(getContext());
//                if (!screenSwitchUtils.isPortrait()) {
//                    switchInputAreaLength(false);
//                    if (popupWindow != null && popupWindow.isShowing()) {
//                        popupWindow.dismiss();
//                    }
//                }
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R2.id.send_btn:
                // if (!canInput && currentItem == 0)  //禁言模式
                if (!canInput)
                    return;
                sendContent = inputEdt.getText().toString().trim();
                if (!isOpen && ScreenSwitchUtils.getInstance(getContext()).isSensorSwitchLandScreen()) {
                    switchInputAreaLength(false);
                }
                sendMessage(sendContent);
                break;
            case R2.id.iv_expression:
                if (!canInput) //禁言模式
                    return;
                if (System.currentTimeMillis() - preDismissTime > 100) {
                    showOrCloseExpressionPopupWindow();
                }
                if (!isOpen && ScreenSwitchUtils.getInstance(getContext()).isSensorSwitchLandScreen()) {
                    switchInputAreaLength(popupWindow.isShowing());
                }
                break;
            case R2.id.btn_send:
                if (!canInput) //禁言模式
                    return;
                sendFlower();
                break;
        }
    }

    //切换全体禁言状态 1 为禁言，0为恢复
    public void setCanInput(boolean value) {
        canInput = value;
        // if (status == 1 && currentItem == 0) {
        if (!canInput) {
            inputEdt.setHint(getResources().getString(R.string.shutUp_input_tip));
            inputEdt.setEnabled(false);
            inputEdt.setMaxLines(1);
            this.setAlpha(0.5f);
        } else {
            inputEdt.setHint(getResources().getString(R.string.input_your_text));
            inputEdt.setEnabled(true);
            inputEdt.setMaxLines(10);
            this.setAlpha(1.0f);
        }
    }

    /**
     * 是否支持输入表情
     *
     * @param enable
     */
    public void setInputExpressionEnable(boolean enable) {
        expressionBtn.setVisibility(enable ? View.VISIBLE : View.GONE);//聊天
    }

    /**
     * 是否支持送花
     *
     * @param enable
     */
    public void setSendFlowerEnable(boolean enable) {
        canSendFlower = enable;
        setSendStatus();
    }

    /**
     * 重置
     */
    public void reset() {
        inputEdt.setText("");
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    //-----------------------------------------发送消息和表情---------------------------------------

    /**
     * 是否显示非全屏表情
     */
    public void showOrCloseExpressionPopupWindow() {
        if (!popupWindow.isShowing()) {
            popupWindow.showAsDropDown(this, 0, -this.getHeight() - DimensionUtils.dip2px(getContext(), expressionAreaHeight));
        } else {
            popupWindow.dismiss();
        }
    }


    /**
     * 变换全屏输入框的宽度
     *
     * @param isLong
     */
    public void switchInputAreaLength(boolean isLong) {
        inputParams = (ViewGroup.LayoutParams) this.getLayoutParams();
        if (isLong) {
            inputParams.width = DimensionUtils.getScreenWidth(getContext());
        } else {
            inputParams.width = DimensionUtils.getScreenWidth(getContext()) - pptWidth;
        }
        this.setLayoutParams(inputParams);
    }


    /**
     * 设置是否为发送状态
     */
    private void setSendStatus() {
        if (canSendFlower && TextUtils.isEmpty(inputEdt.getText().toString())) {
            flowerNumTv.setText(flowerNum + "");
            sendBtn.setVisibility(View.INVISIBLE);
            flowerBtn.setVisibility(View.VISIBLE);
            flowerNumTv.setVisibility(View.VISIBLE);
        } else {
            sendBtn.setVisibility(View.VISIBLE);
            flowerBtn.setVisibility(View.INVISIBLE);
            flowerNumTv.setVisibility(View.INVISIBLE);
            sendBtn.setText("发送");
        }
    }


    public void sendMessage(final String content) {
        if (!TextUtils.isEmpty(content)) {
            if (sendMessageListener != null) {
                sendMessageListener.onSendMessage(content);
            }
            inputEdt.setText("");
            imm.hideSoftInputFromWindow(inputEdt.getWindowToken(), 0);
        }
    }

    @Override
    public void OnExpressionSelected(ExpressionEntity entity) {
        if (entity == null) return;
        //添加表情
        String content = inputEdt.getText().toString();
        content += entity.character;
        inputEdt.setText(ExpressionUtil.getExpressionString(getContext(), content, "mipmap"));
        inputEdt.setSelection(content.length());
    }

    @Override
    public void OnExpressionRemove() {
        String content = inputEdt.getText().toString();
        if (!TextUtils.isEmpty(content)) {//非全屏删除
            int selectionStart = inputEdt.getSelectionStart();
            content = inputEdt.getText().delete(selectionStart - ExpressionUtil.dealContent(getContext(), content, selectionStart), selectionStart).toString();
            inputEdt.setText(ExpressionUtil.getExpressionString(getContext(), content, "mipmap"));
            inputEdt.setSelection(content.length());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        setSendStatus();
    }

    //-----------------------------------------发送花朵----------------------------------------------

    /**
     * 发送鲜花
     */
    private void sendFlower() {
        if (sendFlowerListener != null) {
            sendFlowerListener.onSendFlower();
        }
    }

    /**
     * 设置花的数目
     */
    public void setFlowerNum(int num) {
        flowerNum = num;
        flowerBtn.setSelected(num == 0);
        if (flowerNumTv != null)
            flowerNumTv.setText(flowerNum + "");
    }

    public void setOnSendMessageListener(OnSendMessageListener listener) {
        this.sendMessageListener = listener;
    }

    public void setOnSendFlowerListener(OnSendFlowerListener listener) {
        this.sendFlowerListener = listener;
    }
}
