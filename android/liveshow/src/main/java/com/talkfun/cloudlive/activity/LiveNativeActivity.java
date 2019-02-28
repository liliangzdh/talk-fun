package com.talkfun.cloudlive.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.consts.EventType;
import com.talkfun.cloudlive.dialog.AlertDialogFactory;
import com.talkfun.cloudlive.dialog.ScoreDialogFragment;
import com.talkfun.cloudlive.entity.ChatDisableAllStatusEntity;
import com.talkfun.cloudlive.entity.Event;
import com.talkfun.cloudlive.entity.SignEndEntity;
import com.talkfun.cloudlive.entity.SignEntity;
import com.talkfun.cloudlive.event.OnSendFlowerListener;
import com.talkfun.cloudlive.event.OnSendMessageListener;
import com.talkfun.cloudlive.helper.LiveLotteryDialogHelper;
import com.talkfun.cloudlive.helper.LiveNetHelper;
import com.talkfun.cloudlive.helper.LiveRollHelper;
import com.talkfun.cloudlive.helper.LiveSignDialogHelper;
import com.talkfun.cloudlive.helper.LiveVoteDialogHelper;
import com.talkfun.cloudlive.helper.NetChoiseDiologHelper;
import com.talkfun.cloudlive.net.NetMonitor;
import com.talkfun.cloudlive.util.DanmakuFlameUtil;
import com.talkfun.cloudlive.util.DimensionUtils;
import com.talkfun.cloudlive.util.JsonUtil;
import com.talkfun.cloudlive.util.ScreenSwitchUtils;
import com.talkfun.cloudlive.util.SharedPreferencesUtil;
import com.talkfun.cloudlive.util.StringUtils;
import com.talkfun.cloudlive.view.FullScreenInputBarView;
import com.talkfun.cloudlive.view.GuideDialog;
import com.talkfun.cloudlive.view.LiveMessageView;
import com.talkfun.cloudlive.view.SoftKeyboardStateWatcher;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.consts.BroadcastCmdType;
import com.talkfun.sdk.consts.LiveStatus;
import com.talkfun.sdk.consts.PlayerLoadState;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.event.HtBroadcastListener;
import com.talkfun.sdk.event.HtDispatchFlowerListener;
import com.talkfun.sdk.event.HtDispatchRoomMemberNumListener;
import com.talkfun.sdk.event.LiveInListener;
import com.talkfun.sdk.event.OnPlayerLoadStateChangeListener;
import com.talkfun.sdk.event.OnVideoChangeListener;
import com.talkfun.sdk.event.VideoConnectListener;
import com.talkfun.sdk.module.BroadcastEntity;
import com.talkfun.sdk.module.ChatEntity;
import com.talkfun.sdk.module.ModuleConfigHelper;
import com.talkfun.sdk.module.RoomInfo;
import com.talkfun.sdk.module.VideoModeType;
import com.talkfun.sdk.module.VoteEntity;
import com.talkfun.sdk.module.VotePubEntity;
import com.talkfun.utils.PreventRepeatedUtil;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import io.socket.emitter.Emitter;
import master.flame.danmaku.ui.widget.DanmakuView;

public class LiveNativeActivity extends BasePlayActivity implements
        LiveInListener, HtDispatchRoomMemberNumListener, HtDispatchFlowerListener, HtBroadcastListener, VideoConnectListener, OnSendMessageListener,
        View.OnTouchListener, OnSendFlowerListener, LiveMessageView.IPageChange {
    private final String TAG = LiveNativeActivity.class.getName();
    //标签顶部导航页
    @BindView(R2.id.tab_container)
    LiveMessageView mLiveMessageView;

    @BindView(R2.id.change_tip)
    TextView changeTip;
    @BindView(R2.id.video_visibility_iv)
    ImageView videoVisibleIv;
    @BindView(R2.id.iv_danmu_switch)
    ImageView ivDanmuSwitch;

    @BindView(R2.id.title_bar)
    RelativeLayout titlebarContainer;

    @BindView(R2.id.danmaku_view)
    DanmakuView danmakuView;


    @BindView(R2.id.network_choice_iv)
    ImageView ivNetWorkChoice;

    @BindView(R2.id.operation_btn_container)
    RelativeLayout operationContainer;

    //输入框 -全屏
    @BindView(R2.id.ll_input_fullScreen)
    FullScreenInputBarView fullScreenInputBarView;

    @BindView(R2.id.fab_float_window)
    TextView memberFloatTV;
    /**
     * 播放时网络，弹幕,放大的布局
     */
    @BindView(R2.id.ll_bottom_menu)
    LinearLayout llBottomMenu;

    //    @BindView(R2.id.mongolia_layer)
//    FrameLayout mongoliaLayer;   //蒙层

    //------------------------------------------数据---------------------------------------------------
    private int currentMode;
    private DanmakuFlameUtil danmakuFlameUtil;  //弹幕
    private boolean isLiveStart = false;
    private RoomInfo roomInfo; //房间信息
    private ModuleConfigHelper moduleConfigHelper; //模块配置帮助类
    private boolean isNeedToGuide = true;

    //-------------------------帮助类--------------------------------------
    /**
     *
     */
    private HtSdk mHtSdk;
    /**
     * 网络监测
     */
    private LiveNetHelper mNetCheckHelper;
    /**
     * 网络选择弹框
     */
    private NetChoiseDiologHelper mNetChoiseDiologHelper;
    /**
     * 滚动通知
     */
    private LiveRollHelper mRollHelper;

    /**
     * 点名
     */
    private LiveSignDialogHelper mLiveSignDialogHelper;

    private boolean chatEnable = true;//是否可以聊天或被禁言
    /**
     * 投票
     */
    private LiveVoteDialogHelper mLiveVoteDialogHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEvent();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.playing_activity_layout;
    }

    @Override
    protected void init() {
        super.init();
    }

    /**
     * 初始化布局
     */
    protected void initView() {
        super.initView();
        initGuideLayout();
        initDanmaku();
        mHtSdk = HtSdk.getInstance();
//        mHtSdk.setLogEnable(true);
//        mHtSdk.setLogLevel(3);
        mHtSdk.init(pptContainer, videoViewContainer, mToken);
        /**设置桌面分享/插播视频容器
         * 如果没调用该方法设置容器，默认使用PPT白板容器，桌面分享的视频会添加到白板的上一层
         * */
        mHtSdk.setDesktopVideoContainer(desktopVideoContainer);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View liveWaitView = layoutInflater.inflate(R.layout.live_wait_layout, null);
        View liveOverView = layoutInflater.inflate(R.layout.live_over_layout, null);
        View loadingView = layoutInflater.inflate(R.layout.loading_layout, null);
        View loadFailView = layoutInflater.inflate(R.layout.load_fail_layout, null);

        mHtSdk.setLiveWaitView(liveWaitView);//设置直播未开始显示view
        mHtSdk.setLiveOverView(liveOverView);//设置直播结束显示view
        mHtSdk.setLoadingView(loadingView); //设置正在加载初始化数据显示view
        mHtSdk.setLoadFailView(loadFailView);//设置加载初始化数据失败显示view
        mHtSdk.setWhiteboardLoadFailDrawable(getResources().getDrawable(R.mipmap.image_broken));
        showVideoContainer(videoVisibleIv, false);
        //HtSdk.getInstance().setPauseInBackground(false);
        operationContainer.bringToFront();
        titlebarContainer.bringToFront();
        hideTitleBar();
        initHelper();
    }

    /**
     * 初始化帮助类
     */
    private void initHelper() {
        mNetCheckHelper = new LiveNetHelper(this);
        mNetChoiseDiologHelper = new NetChoiseDiologHelper(this);
        mRollHelper = new LiveRollHelper(this, pptLayout);
        mLiveSignDialogHelper = new LiveSignDialogHelper(LiveNativeActivity.this);


    }

    /**
     * 初始化弹幕库
     */
    private void initDanmaku() {
        ivDanmuSwitch.setSelected(false);
        danmakuFlameUtil = new DanmakuFlameUtil(danmakuView);
        danmakuFlameUtil.hide();
    }

  /*  @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }*/

    /**
     * 初始化引导
     */
    private void initGuideLayout() {
        //是否需要引导
        isNeedToGuide = SharedPreferencesUtil.getBoolean(this, SharedPreferencesUtil.SP_LIVEROOM_GUIDE);
//        ScreenSwitchUtils.getInstance(this).isOpenSwitchAuto(true);
        if (isNeedToGuide) {
            GuideDialog mGuideDialog = new GuideDialog(this);
            mGuideDialog.show();
        }
    }

    private void initEvent() {
        /**设置直播事件监听*/
        mHtSdk.setLiveListener(this);

        /**设置视频连接错误事件监听*/
        mHtSdk.setVideoConnectListener(this);
        /**设置获取房间人数更新监听*/
        mHtSdk.setHtDispatchRoomMemberNumListener(this);
        mHtSdk.setHtBroadcastListener(this);
        /**
         * 设置视频切换事件监听
         * 当由课件模式或桌面分享/视频插播模式切换时，会调用该事件监听的onVideoModeChanging和onVideoModeChanged方法
         * 当视频（摄像头视频和桌面分享/插播视频）开始、停止播放时调用onVideoStart和onVideoStop方法
         * 当摄像头视频显示隐藏时调用onCameraShow和onCamerahide方法
         *
         * */
        mHtSdk.setOnVideoChangeListener(new OnPlayVideoChangeLister());

        videoViewContainer.setOnTouchListener(this);

        /***
         * 获取缓冲状态
         */
        mHtSdk.setOnPlayerLoadStateChangeListener(new OnPlayerLoadStateChangeListener() {
            @Override
            public void onPlayerLoadStateChange(int loadState) {
                if (loadState == PlayerLoadState.MEDIA_INFO_BUFFERING_START) {
                    Log.d(TAG, "缓冲开始");
                } else if (loadState == PlayerLoadState.MEDIA_INFO_BUFFERING_END) {
                    Log.d(TAG, "缓冲结束");
                }
            }
        });
        /**
         * 滑动页监听
         */
        mLiveMessageView.addIPageChangeListener(this);
        mLiveMessageView.initListener();
        //首次进入时，无法获取到vgInputLayout的高度，所以加了这个方法
        vgInputLayout.post(new Runnable() {
            @Override
            public void run() {
                updateLayout();
            }
        });


        fullScreenInputBarView.setOnSendMessageListener(new OnSendMessageListener() {
            @Override
            public void onSendMessage(String content) {
                isLongShowTitleBar = false;
                if (mLiveMessageView != null) {
                    mLiveMessageView.sendChatMessage(content);
                }
            }
        });
        fullScreenInputBarView.setOnFocusChangeListener(new FullScreenInputBarView.IFocusChangeListener() {
            @Override
            public void focusChange(boolean isFocus) {
                if (isFocus) {
                    isLongShowTitleBar = true;

                } else {
                    isLongShowTitleBar = false;
                }
            }
        });

        SoftKeyboardStateWatcher stateWatcher = new SoftKeyboardStateWatcher(linearContainer);
        stateWatcher.addSoftKeyboardStateListener(new SoftKeyboardStateWatcher.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                if (vgInputLayout == null) {
                    return;
                }
                if (ScreenSwitchUtils.getInstance(LiveNativeActivity.this).isPortrait()) {
                    //竖屏情况下：如果软键盘弹起，就将摄像头移到屏幕右上角
                    int videoViewX = DimensionUtils.getScreenWidth(LiveNativeActivity.this) - videoViewContainer.getLayoutParams().width;
                    updateVideoPosition(videoViewX, 0);
                } else {
                    vgInputLayout.switchInputAreaLength(true);

                }

            }

            @Override
            public void onSoftKeyboardClosed() {
                if (vgInputLayout == null) {
                    return;
                }
                ScreenSwitchUtils screenSwitchUtils = ScreenSwitchUtils.getInstance(LiveNativeActivity.this);
                if (screenSwitchUtils.isPortrait()) {
                    //竖屏情况下：如果软键盘弹起，就将摄像头移到标签栏下面
                    FrameLayout.LayoutParams videoViewParams = (FrameLayout.LayoutParams) videoViewContainer.getLayoutParams();
                    int videoViewContainerX = DimensionUtils.getScreenWidth(LiveNativeActivity.this) - videoViewContainer.getLayoutParams().width;
                    int height = 3 * DimensionUtils.getScreenWidth(LiveNativeActivity.this) / 4;
                    videoViewParams.leftMargin = videoViewContainerX;
                    videoViewParams.topMargin = height + getVideoYOffset();
                    videoViewContainer.setLayoutParams(videoViewParams);

                } else {
                    vgInputLayout.switchInputAreaLength(false);
                    if (vgInputLayout.popupWindow != null && vgInputLayout.popupWindow.isShowing()) {
                        vgInputLayout.popupWindow.dismiss();
                    }
                }
            }
        });

        if (vgInputLayout != null) {
            vgInputLayout.setOnSendMessageListener(this);
            vgInputLayout.setOnSendFlowerListener(this);
        }

        /**设置鲜花信息事件监听*/
        mHtSdk.setHtDispatchFlowerListener(this);
        mLiveVoteDialogHelper = new LiveVoteDialogHelper(this);
        mLiveVoteDialogHelper.registerListener();

        LiveLotteryDialogHelper mLiveLotteryDialogHelper = new LiveLotteryDialogHelper(this);
        mLiveLotteryDialogHelper.registerListener();

        mLiveSignDialogHelper.setOnSignInCallBack(new Callback() {
            @Override
            public void success(Object result) {
                if (mLiveMessageView != null) {
                    mLiveMessageView.insertChatMessage(LiveNativeActivity.this.getResources().getString(R.string.ht_sign_in));
                }
            }

            @Override
            public void failed(String failed) {

            }
        });

        //接收点名开始的通知
        mHtSdk.on(BroadcastCmdType.SIGN_NEW, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args == null || args.length == 0)
                    return;
                for (int i = 0; i < args.length; i++) {
                    final JSONObject obj = (JSONObject) args[i];
                    if (obj == null)
                        continue;
                    final SignEntity signEntity = JsonUtil.transferSignEntiy(obj);

                    //弹出签名弹框
                    if (mLiveSignDialogHelper != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mLiveSignDialogHelper.signStart(signEntity);
                            }
                        });
                    }
                }
            }
        });


        //接收点名结束的通知
        mHtSdk.on(BroadcastCmdType.SIGN_END, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args == null || args.length == 0)
                    return;
                for (int i = 0; i < args.length; i++) {
                    final JSONObject obj = (JSONObject) args[i];
                    if (obj == null)
                        continue;
                    final SignEndEntity signEndEntity = JsonUtil.transferSignEndEntity(obj);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //签名弹框消失
                            if (mLiveSignDialogHelper != null) {
                                mLiveSignDialogHelper.signStop();
                            }
                            //插入到聊天界面
                            if (mLiveMessageView != null) {
                                mLiveMessageView.insertChatMessage(signEndEntity);
                            }
                        }
                    });
                }
            }
        });


        //监听全体禁言/解禁事件广播
        mHtSdk.on(BroadcastCmdType.CHAT_DISABLE_ALL, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (args == null || args.length == 0)
                    return;
                for (int i = 0; i < args.length; i++) {
                    final JSONObject obj = (JSONObject) args[i];
                    if (obj == null)
                        continue;
                    final JSONObject argsObj = obj.optJSONObject("args");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ChatDisableAllStatusEntity entity = ChatDisableAllStatusEntity.objectFromData(argsObj.toString());
                            if(mLiveMessageView != null)
                                mLiveMessageView.insertChatMessage(entity);
                            chatEnable = !entity.isDisable();
                            setCanInput(chatEnable);
                        }
                    });
                }
            }
        });

        //socket连接失败提示
        mHtSdk.on(BroadcastCmdType.SOCKET_CONNECT_FAIL, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LiveNativeActivity.this);
                builder.setMessage("socket连接失败，请检测网络是否正常，并重新刷新试试");
                builder.setTitle(R.string.tips);
                builder.setPositiveButton(R.string.refresh, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HtSdk.getInstance().reload();
                    }
                }).setNegativeButton(R.string.goback, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LiveNativeActivity.this.finish();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCancelable(true);
                dialog.show();
                //Toast.makeText(LiveNativeActivity.this, "socket连接失败，请重新刷新", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long preClickTime = 0L;

    @OnClick({
            R2.id.fullScreen_iv, R2.id.iv_go_back, R2.id.exchange,
            R2.id.video_visibility_iv, R2.id.ppt_Layout, R2.id.network_choice_iv, R2.id.iv_danmu_switch, R2.id.iv_refresh/*, R.id.btn_guide_start*/})
    void onClick(View v) {

        int id = v.getId();

        if(id == R.id.fullScreen_iv){  //全屏
            switchFullScreen();
        }else if(id == R.id.video_visibility_iv){ //摄像头开关
            onVideoVisible(videoVisibleIv);
        }else if(id == R.id.iv_go_back){
            gobackAction();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                showInputLayout();
            }
        }else if(id == R.id.ppt_Layout){
            if (System.currentTimeMillis() - preClickTime < 300) {  //双击全屏
                switchFullScreen();
                return;
            }
            preClickTime = System.currentTimeMillis();
            if (isTitleBarShow) {
                isLongShowTitleBar = false;
                hideTitleBar();
            } else {
                showTitleBar();
            }
        }else if(id == R.id.network_choice_iv){
            if (isLiveStart) {
                if(PreventRepeatedUtil.canClickable("networkChoice")){
                    if (mNetChoiseDiologHelper != null) {
                        mNetChoiseDiologHelper.showNetworkChoiceDialog();
                    }
                }

            }
        }else  if(id == R.id.exchange){
            if (isVideoViewContainerVisiable()) {
                //先注释掉这行.该控件改作用来测试桌面分享
                HtSdk.getInstance().exchangeVideoAndWhiteboard();
                isExchangeViewContainer = !isExchangeViewContainer;
            }
        }else if(id == R.id.iv_danmu_switch){
            boolean selected = ivDanmuSwitch.isSelected();
            ivDanmuSwitch.setSelected(!selected);
            if (!selected) {
                danmakuFlameUtil.show();
            } else {
                danmakuFlameUtil.hide();
            }
        }else if(id == R.id.iv_refresh){
            if(!PreventRepeatedUtil.canClickable("refresh")){
                return;
            }
            if (mNetChoiseDiologHelper != null) {
                mNetChoiseDiologHelper.resetSelectPosition();
            }
            exchangeViewContainer();
            HtSdk.getInstance().reload();
        }
    }

    @Override
    public void layoutChanged() {  //布局切换
        super.layoutChanged();
        vgInputLayout.reset();
        fullScreenInputBarView.reset();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) { //tip:横向非全屏切换至全屏不走这个方法
        super.onConfigurationChanged(newConfig);
        if (mNetCheckHelper != null) {
            mNetCheckHelper.dismissPop();
        }
        if (!ScreenSwitchUtils.getInstance(LiveNativeActivity.this).isFullScreen()) { //不是全屏
            if (mLiveMessageView != null) {
                mLiveMessageView.pageChanged();
            }
        }
        layoutChanged();
    }

    /**
     * 全屏。非全屏切换
     */
    public void switchFullScreen() {
        onFullScreenChange();
        showInputLayout();
    }

    private void showInputLayout() {
        if (handler == null) {
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!DimensionUtils.isPad(LiveNativeActivity.this) && ScreenSwitchUtils.getInstance(LiveNativeActivity.this).isFullScreen()) {
                    if (vgInputLayout != null) {
                        vgInputLayout.setVisibility(View.GONE);
                    }
                } else {
                    if (mLiveMessageView != null) {
                        if (mLiveMessageView.getCurrentItem() != 2) {
                            if (vgInputLayout != null) {
                                vgInputLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                }
            }
        }, 100);
    }

    /**
     * 是否显示全屏输入框
     *
     * @param isShow
     */
    @Override
    public void showFullScreenInput(boolean isShow) {
        fullScreenInputBarView.setVisibility(isShow == true ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onVideoVisible(ImageView videoVisibleIv) {
        if (isLiveStart && currentMode != VideoModeType.DESKTOP_MODE) {
            super.onVideoVisible(videoVisibleIv);
        }
    }


    @Override
    void showController() {
        titlebarContainer.setVisibility(View.VISIBLE);
        operationContainer.setVisibility(View.VISIBLE);
    }

    @Override
    void hideController() {
        if (titlebarContainer == null)
            return;
        titlebarContainer.setVisibility(View.GONE);
        operationContainer.setVisibility(View.GONE);
        fullScreenInputBarView.hideSoftInput();
        if (mNetCheckHelper != null) {
            mNetCheckHelper.dismissPop();
        }
    }

    @Override
    public void updateLayout() {
        super.updateLayout();
        setMemberFloatTVWH(width, screenHeight);
    }

    /**
     * 获取人数浮窗宽高并设置位置
     *
     * @param screenWidth
     * @param screenHeight
     */
    private void setMemberFloatTVWH(final int screenWidth, final int screenHeight) {
        if (memberFloatTV == null) return;
        memberFloatTV.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                memberFloatTV.removeOnLayoutChangeListener(this);
                int width = memberFloatTV.getWidth();
                memberFloatTV.setX(screenWidth - width);
                memberFloatTV.setY(screenHeight * 4 / 5);

            }
        });
    }

    //TODO-------------------------------------------房间直播状态----------------------------------------

    /**
     * SDK初始化完成事件
     */
    @Override
    public void onLaunch() {
        if (mHtSdk != null) {
            if (LiveStatus.STOP.equals(mHtSdk.getInitLiveStatus())) {
                Log.i(TAG, "直播已结束");
            } else {
                if (LiveStatus.START.equals(mHtSdk.getInitLiveStatus())) {
                    Log.i(TAG, "直播已开始");
                } else if (LiveStatus.WAIT.equals(mHtSdk.getInitLiveStatus())) {
                    Log.i(TAG, "直播未开始");
                }

                roomInfo = mHtSdk.getRoomInfo();
                if (roomInfo != null)
                    setCanInput(roomInfo.getDisableall() == 0);

                moduleConfigHelper = mHtSdk.getModuleConfigHelper();
            }

        }
        userVideoShow = true;
        showTitleBar();
    }

    /**
     * 初始化失败
     *
     * @param msg
     */
    @Override
    public void onInitFail(String msg) {
        Log.d(TAG, "onInitFail: " + msg);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(R.string.tips);
        builder.setPositiveButton((R.string.goback), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                LiveNativeActivity.this.finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    //直播开始
    @Override
    public void onLiveStart() {
        if (isFinish)
            return;
        isLiveStart = true;
        if (mNetCheckHelper != null) {
            mNetCheckHelper.startCheckNetStatus(ivNetWorkChoice);
        }
        if (mHtSdk == null || (roomInfo = mHtSdk.getRoomInfo())== null)
            return;

        if (mLiveMessageView != null) {
            //mLiveMessageView.clearChatAndQuestionMessage();
            mLiveMessageView.addRoomInfo(roomInfo);
            if (roomInfo.getNoticeEntity() != null) {
                mLiveMessageView.showNotice();
            }
        }
        if (roomInfo.getRollEntity() != null) {
            if (mRollHelper != null) {
                mRollHelper.receiveRollAnnounce(roomInfo.getRollEntity());
            }
        }

        //防盗录设置
        if(moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_THEFTPROOF)){
            if(roomInfo.getUser() != null)
                startShowWatermark(roomInfo.getUser().getUid());
            else
                startShowWatermark(roomInfo.getRoomid());
        }
    }

    //直播结束
    @Override
    public void onLiveStop() {
        isLiveStart = false;
        if (mNetCheckHelper != null) {
            mNetCheckHelper.stopCheckNetStatus();
        }
        if (mRollHelper != null) {
            mRollHelper.cancel();
        }
        stopShowWatermark();
      /*  //网络选择item重置
        if (mNetChoiseDiologHelper != null) {
            mNetChoiseDiologHelper.resetNetworkItem(0);
        }
*/
        if (isExchangeViewContainer) {
            HtSdk.getInstance().exchangeVideoAndWhiteboard();
            isExchangeViewContainer = !isExchangeViewContainer;
            showVideoContainer(videoVisibleIv, false);
        }
        if (memberFloatTV == null) {
            return;
        }
        memberFloatTV.setVisibility(View.GONE);

        showScoreDialogIfEnable();
    }

    /**
     * 评分窗口
     */
    private  void showScoreDialogIfEnable(){
        if(moduleConfigHelper == null)
            return;

        if( ! moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_SCORE_VISIBLE))
            return;

        ScoreDialogFragment scoreDialogFragment = ScoreDialogFragment.create();
        scoreDialogFragment.show(((FragmentActivity) this).getSupportFragmentManager(),"score");
    }

    //TODO-----------------------------------------房间成员动态------------------------------------

    //用户被强制退出
    @Override
    public void memberForceout() {
        String reason = getResources().getString(R.string.member_forceout);
        memberOut(reason);
    }

    //用户被踢
    @Override
    public void memberKick() {
        String reason = getResources().getString(R.string.member_kick);
        memberOut(reason);
    }

    //用户退出
    private void memberOut(String reason) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(reason);
        builder.setTitle(R.string.tips);
        builder.setPositiveButton((R.string.goback), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                LiveNativeActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    //房间人数变化
    public void updateMemberTotal(int total) {
        if (moduleConfigHelper != null && memberFloatTV != null) {
            if (moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_VISITORINFO) && isLiveStart) {
                if (memberFloatTV != null) {
                    setMemberFloatTVWH(width, screenHeight);
                    memberFloatTV.setVisibility(View.VISIBLE);
                    memberFloatTV.setText(total + "人");
                }
            }else{
                memberFloatTV.setVisibility(View.GONE);
            }
        }
//        Log.d("total", total + "");
//        if (total == 0) {
//            memberFloatTV.setVisibility(View.GONE);
//            return;
//        }
//        memberFloatTV.setVisibility(View.VISIBLE);

        //tvMemberTotal.setText(total + "人");
    }

    @Override
    public void receiveBroadcast(BroadcastEntity broadcastEntity) {
        mLiveMessageView.insertChatMessage(broadcastEntity);
    }

    /**
     * @param message
     */
    @Override
    public void eventCallback(Event message) {
        Log.i("eventCallback","LiveNativeActivity");
        if (message != null) {
            int type = message.getType();
            switch (type) {
                case EventType.INSERTCHAT:
                    if (mLiveMessageView != null) {
                        mLiveMessageView.insertChatMessage(message.getData());
                    }
                    break;
                case EventType.ADDDANMAKU:
                    if (danmakuFlameUtil == null) return;
                    if (danmakuFlameUtil.isShown()) {   //如果弹幕 view 显示才添加弹幕信息
                        danmakuFlameUtil.addDanmaku((SpannableString) message.getData(), false);
                    }
                    break;
                case EventType.SHOWTITLEBAR:
                    showTitleBar();
                    break;
                case EventType.LOOKOVERVOTES:
                    if (mLiveVoteDialogHelper != null) {
                        mLiveVoteDialogHelper.voteStart((VoteEntity) message.getData());
                    }
                    break;
                case EventType.LOOKOVERVOTERESULTS:
                    if (mLiveVoteDialogHelper != null) {
                        mLiveVoteDialogHelper.voteStop((VotePubEntity) message.getData());
                    }
                    break;
                case EventType.NETWORK_STATE_CHANGE:
                    int netStatus = (int) message.getData();
                    if(netStatus == NetMonitor.NETWORK_NONE){
                        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(),getResources().getString(R.string.tips),getResources().getString(R.string.not_connect),null);
                    }
                    break;
            }
        }
    }

    /**
     * 切換禁言狀態
     */
    public void setCanInput(boolean value) {
        vgInputLayout.setCanInput(value);
        fullScreenInputBarView.setCanInput(value);
//        fullScreenInputBarViewOpen.setCanInput(value);
    }


    @Override
    public void connectVideoError(String error) {
        StringUtils.tip(getApplicationContext(), error);
    }


    @Override
    public void pageChange(int position) {
        switch (position) {
            case LiveMessageView.CHAT_TAB:  //聊天
                if (vgInputLayout != null) {
                    if (vgInputLayout.getVisibility() != View.VISIBLE) {
                        vgInputLayout.setVisibility(View.VISIBLE);
                    }
                    vgInputLayout.setSendFlowerEnable(true);
                    vgInputLayout.setInputExpressionEnable(true);
                }
                break;
            case LiveMessageView.QUESTION_TAB:  //提问
                if (vgInputLayout != null) {
                    if (vgInputLayout.getVisibility() != View.VISIBLE) {
                        vgInputLayout.setVisibility(View.VISIBLE);
                    }
                    vgInputLayout.setSendFlowerEnable(false);
                    vgInputLayout.setInputExpressionEnable(false);
                }
                break;
            case LiveMessageView.NOTIFY_TAB:  //通知
                if (vgInputLayout != null && vgInputLayout.getVisibility() == View.VISIBLE) {
                    vgInputLayout.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    //TODO-----------------------------------视频播放切换事件监听------------------------------------

    /**
     * 视频播放切换事件监听
     */
    class OnPlayVideoChangeLister implements OnVideoChangeListener {
        /**
         * 视频开始播放
         *
         * @param mode 视频类型（VideoModeType.CAMERA_MODE：摄像头类型视频;VideoModeType.DESKTOP_MODE：桌面分享类型视频)
         */
        @Override
        public void onVideoStart(int mode) {
            currentMode = mode;
          /*  if (mode == VideoModeType.CAMERA_MODE) {
                showVideoContainer();
            }*/
        }

        /**
         * 视频停止播放
         *
         * @param mode 视频类型（VideoModeType.CAMERA_MODE：摄像头类型视频;VideoModeType.DESKTOP_MODE：桌面分享类型视频)
         */
        @Override
        public void onVideoStop(int mode) {
            if (mode == VideoModeType.CAMERA_MODE && !isExchangeViewContainer) {
                showVideoContainer(videoVisibleIv, false);
            }
        }

        /**
         * 视频播放模式类型切换
         *
         * @param beforeMode  切换前类型
         * @param currentMode 切换后类型
         *                    视频类型（VideoModeType.CAMERA_MODE：摄像头类型视频;VideoModeType.DESKTOP_MODE：桌面分享类型视频)
         */
        @Override
        public void onVideoModeChanging(int beforeMode, int currentMode) {
            changeTip.setVisibility(View.VISIBLE);
            if (mNetCheckHelper != null) {
                mNetCheckHelper.dismissPop();
            }
        }

        /**
         * 视频切换完成
         */
        @Override
        public void onVideoModeChanged() {
            changeTip.setVisibility(View.GONE);
        }

        /**
         * 摄像头视频显示
         */
        @Override
        public void onCameraShow() {
            if (!userVideoShow) return;
            showVideoContainer(videoVisibleIv, true);
            if (DimensionUtils.isPad(LiveNativeActivity.this) && !ScreenSwitchUtils.getInstance(LiveNativeActivity.this).isPortrait())
                updateLayout();
        }

        /**
         * 摄像头视频隐藏
         */
        @Override
        public void onCameraHide() {
            exchangeViewContainer();
            showVideoContainer(videoVisibleIv, false);
            if (DimensionUtils.isPad(LiveNativeActivity.this) && !ScreenSwitchUtils.getInstance(LiveNativeActivity.this).isPortrait())
                updateLayout();
        }

    }

    //TODO----------------------------------------------送花-------------------------------------------
    @Override
    public void onSendFlower() {
        sendFlower();
    }

    /**
     * 送花
     */

    public void sendFlower() {
        if (HtSdk.getInstance().isLiving()) {
            HtSdk.getInstance().sendFlower();
            if (vgInputLayout != null) {
                vgInputLayout.setFlowerNum(0);
            }

        } else {
            StringUtils.tip(this, "还没上课");
        }
    }

    //设置鲜花数
    @Override
    public void setFlowerNum(int num) {
        if (vgInputLayout != null) {
            vgInputLayout.setFlowerNum(num);
        }
    }

    //获取第一朵鲜花剩余的时间
    @Override
    public void setFlowerLeftTime(int time) {
        String tips = String.format("剩余%s秒可以获取第一朵花", time);
        StringUtils.tip(this, tips);
    }


    //送花成功
    @Override
    public void sendFlowerSuccess(String args) {
        try {
            JSONObject jsonObject = new JSONObject(args);
            int amount = jsonObject.optInt("amount");
            if (amount <= 0) {
                return;
            } else {
                String flower = "";
                for (int i = 0; i < amount; i++) {
                    flower += "[flower]";
                }
                ChatEntity chatEntity = new ChatEntity();
                chatEntity.setAvatar(jsonObject.optString("avatar"));
                chatEntity.setNickname(jsonObject.optString("nickname"));
                chatEntity.setXid(jsonObject.optInt("xid"));
                chatEntity.setRole(jsonObject.optString("role"));
                chatEntity.setTime(jsonObject.optString("sendtime"));
                chatEntity.setMsg(flower);
                chatEntity.setAvatar(jsonObject.optString("avatar"));
                if (mLiveMessageView != null) {
                    mLiveMessageView.insertChatMessage(chatEntity);
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onSendMessage(String content) {
        if (mLiveMessageView != null) {
            mLiveMessageView.onSendMessage(content);
        }

    }

    //TODO---------------------------------------------activity life-------------------------------------------
    @Override
    public void onBackPressed() {
        gobackAction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLayout();
        danmakuFlameUtil.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        danmakuFlameUtil.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        HtSdk.getInstance().onStop();
        if (mNetCheckHelper != null) {
            mNetCheckHelper.stopCheckNetStatus();
        }
    }

    boolean isFinish = false;

    @Override
    public void finish() {
        isFinish = true;
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        danmakuFlameUtil.destroy();
        if (mNetCheckHelper != null) {
            mNetCheckHelper.dismissPop();
        }
    }

}
