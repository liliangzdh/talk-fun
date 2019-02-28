package com.talkfun.cloudlive.activity;

import android.Manifest;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.consts.MainConsts;
import com.talkfun.cloudlive.dialog.SwitchLineDialogFragment;
import com.talkfun.cloudlive.fragment.PlaybackSectionFragment;
import com.talkfun.cloudlive.helper.NetChoiseDiologHelper;
import com.talkfun.cloudlive.interfaces.PermissionsListener;
import com.talkfun.cloudlive.util.DimensionUtils;
import com.talkfun.cloudlive.util.LogUtil;
import com.talkfun.cloudlive.util.ScreenSwitchUtils;
import com.talkfun.cloudlive.util.SeekBarHelper;
import com.talkfun.cloudlive.util.StringUtils;
import com.talkfun.cloudlive.util.TimeUtil;
import com.talkfun.cloudlive.view.PlaybackMessageView;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.consts.PlayerLoadState;
import com.talkfun.sdk.data.PlaybackDataManage;
import com.talkfun.sdk.event.ErrorEvent;
import com.talkfun.sdk.event.OnPlayerLoadStateChangeListener;
import com.talkfun.sdk.event.OnVideoChangeListener;
import com.talkfun.sdk.event.OnVideoStatusChangeListener;
import com.talkfun.sdk.event.PlaybackListener;
import com.talkfun.sdk.module.AlbumItemEntity;
import com.talkfun.sdk.module.ModuleConfigHelper;
import com.talkfun.sdk.module.PlaybackInfo;
import com.talkfun.sdk.module.User;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PlaybackNativeActivity extends BasePlayActivity implements
        PlaybackListener, PlaybackSectionFragment.PlaybackSectionInterface, View.OnTouchListener, OnVideoStatusChangeListener, ErrorEvent.OnErrorListener {

    //-------------------------------ui---------------------------------
    @BindView(R2.id.operation_btn_container)
    LinearLayout operationContainer;
    @BindView(R2.id.video_visibility_iv)
    ImageView videoVisibleIv;
    @BindView(R2.id.seek_bar_layout)
    LinearLayout seekbarLayout;
    @BindView(R2.id.seek_bar)
    SeekBar seekBar;
    @BindView(R2.id.controller_iv)
    ImageView controlIv;
    @BindView(R2.id.total_duration)
    TextView totalDuration;
    @BindView(R2.id.current_duration)
    TextView currentDuration;
    @BindView(R2.id.iv_go_back)
    ImageView goBack;
    @BindView(R2.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R2.id.tab_container)
    PlaybackMessageView mPlaybackMessageView;
    //    @BindView(R2.id.iv_start_download)
//    ImageView ivStartDownload;
    @BindView(R2.id.tv_speed)
    TextView tvSpeed;

    //--------------------------------------------------------------------------------

    private SwitchLineDialogFragment switchLineDialogFragment;
    private boolean mIsPlaying = true;
    private String mId;
    private SeekBarHelper seekBarUtil;
    private ListPopupWindow playSpeedlpw;
    //    private long mPreClickTime = 0;
    //------------------------------常量------------------------------------------------
    private static final String TAG = PlaybackNativeActivity.class.getName();
    private final String[] playSpeedStrs = {"0.75X", "1.0X", "1.5X", "2.0X"};
    private final float[] playSpeeds = {0.75f, 1.0f, 1.5f, 2.0f};
    private HtSdk mHtSdk;

    //-----------------------------帮助类------------------------------------
//    private PlaybackTabAndViewPagerView mPlaybackInteractionHelper;
//    private PopWindowUtil mPopWindowUtil;
    /**
     * 网络选择弹框
     */
    private NetChoiseDiologHelper mNetChoiseDiologHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEvent();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.playback_layout;
    }

    @Override
    protected void init() {
        super.init();
        mId = getIntent().getStringExtra("id");
        MainConsts.PlayBackID = mId;
//        ScreenSwitchUtils.getInstance(this).isOpenSwitchAuto(true);
    }


    /**
     * 初始化布局
     */
    protected void initView() {
        super.initView();
//        ivStartDownload.setVisibility(View.VISIBLE);
        showVideoContainer(videoVisibleIv, false);
        operationContainer.bringToFront();  //将控件移动到前面
        seekbarLayout.bringToFront();
        goBack.bringToFront();

        hideTitleBar();  // 隐藏标题栏和操作按钮
        updateLayout();
        seekBarUtil = new SeekBarHelper(this, seekBar);
        mHtSdk = HtSdk.getInstance();
        mHtSdk.init(pptContainer, videoViewContainer, mToken, true);
        /**设置桌面分享/插播视频容器
         * 如果没调用该方法设置容器，默认使用PPT白板容器，桌面分享的视频会添加到白板的上一层
         * */
        mHtSdk.setDesktopVideoContainer(desktopVideoContainer);
        mHtSdk.setFilterQuestionFlag(false);
        mHtSdk.setPauseInBackground(false);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View loadingView = layoutInflater.inflate(R.layout.loading_layout, null);
        View loadFailView = layoutInflater.inflate(R.layout.load_fail_layout, null);
        mHtSdk.setLoadingView(loadingView); //设置正在加载数据显示view
        mHtSdk.setLoadFailView(loadFailView);//设置加载数据失败显示view
        mHtSdk.setWhiteboardLoadFailDrawable(getResources().getDrawable(R.mipmap.image_broken));
        //启动本地服务器和播放设置
        //如果为true,在连网情况下，如果点播下载完成播放本地点播，未下载或下载未完成则播放网络点播，如果本地下载完成断网播放本地点播
        //如果为false,在连网情况下播放网络点播，如果本地下载完成断网播放本地点播
        //默认为true
        mHtSdk.setIsPlayOffline(mId, true);
        mHtSdk.setPauseInBackground(true);
        initHelper();
    }


    private void initHelper() {
        //下载权限
        mPlaybackMessageView.setPermissionsListener(new PermissionsListener() {
            @Override
            public void requestPermissions() {
                MPermissions.requestPermissions(PlaybackNativeActivity.this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });
        mPlaybackMessageView.addTokenAndId(mToken, mId);
        mPlaybackMessageView.addSeekBarUtil(seekBarUtil);
    }

    /**
     * 监听初始化
     */
    private void initEvent() {
        videoViewContainer.setOnTouchListener(this);

        //点播必须初始化的接口
        mHtSdk.setPlaybackListener(this);


        //视频播放状态改变的监听回调
        mHtSdk.setOnVideoStatusChangeListener(this);
        /**设置视频切换事件监听*/
        mHtSdk.setOnVideoChangeListener(new OnPlayVideoChangeLister());
        //错误监听
        mHtSdk.setOnErrorListener(this);
        //添加快进快退的滑动监听事件
        seekBarUtil.addTouchSlidSeekEvent(pptLayout);

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
    }

    private long preClickTime = 0L;

    @OnClick({R2.id.fullScreen_iv, R2.id.video_visibility_iv, R2.id.controller_iv, R2.id.iv_go_back,
            R2.id.ppt_Layout, R2.id.exchange/*, R.id.iv_start_download*/, R2.id.network_choice_iv, R2.id.iv_refresh, R2.id.tv_speed})
    void onClick(View v) {
        Log.d(getClass().getName(), v.getClass().getName());
        //在android module 里面 switch 无效
        int id = v.getId();
        if (id == R.id.fullScreen_iv) {//全屏
            onFullScreenChange();
        } else if (id == R.id.video_visibility_iv) {//开关摄像头
            onVideoVisible(videoVisibleIv);
        } else if (R.id.controller_iv == id) {  //开始或暂停
            if (mIsPlaying) {
                pause();
                mIsPlaying = false;
            } else {
                play();
                mIsPlaying = true;
            }
        } else if (R.id.iv_go_back == id) {  //返回
            gobackAction();
        } else if (id == R.id.ppt_Layout) { //点击ppt区域
            if (System.currentTimeMillis() - preClickTime < 300) {  //双击全屏
                onFullScreenChange();
                return;
            }
            preClickTime = System.currentTimeMillis();
            if (seekBarUtil.isShowPoped) {
                seekBarUtil.isShowPoped = false;
                return;
            }
            if (isTitleBarShow) {
                hideTitleBar();
            } else {
                showTitleBar();
            }

        } else if (id == R.id.exchange) {
            /**切换ppt容器与摄像头视频容器*/
            if (!isVideoViewContainerVisiable()) {
                return;
            }
            isExchangeViewContainer = !isExchangeViewContainer;
            mHtSdk.exchangeVideoAndWhiteboard();
        } else if (id == R.id.network_choice_iv) { //切换视频路线
            //showSwitchLineDialog();
            if (mNetChoiseDiologHelper == null) {
                mNetChoiseDiologHelper = new NetChoiseDiologHelper(PlaybackNativeActivity.this);
            }
            mNetChoiseDiologHelper.showNetworkChoiceDialog();
        } else if (R.id.iv_refresh == id) { //刷新
            //seekBarUtil.resetSeekBarProgress();
            exchangeViewContainer();
            videoViewContainer.setVisibility(View.INVISIBLE);
            mHtSdk.reload();
        } else if (id == R.id.tv_speed) {
            showOrHideSpeedList(v);
        }


    }

    public void switchClick(View v) {
        switch (v.getId()) {
            case R2.id.fullScreen_iv:  //全屏
                onFullScreenChange();
                break;
            case R2.id.video_visibility_iv:  //开关摄像头
                onVideoVisible(videoVisibleIv);
                break;
            case R2.id.controller_iv:  //开始或暂停
                if (mIsPlaying) {
                    pause();
                    mIsPlaying = false;
                } else {
                    play();
                    mIsPlaying = true;
                }
                break;
            case R2.id.iv_go_back:  //返回
                gobackAction();
                break;
            case R2.id.ppt_Layout:   //点击ppt区域
                if (System.currentTimeMillis() - preClickTime < 300) {  //双击全屏
                    onFullScreenChange();
                    return;
                }
                preClickTime = System.currentTimeMillis();
                if (seekBarUtil.isShowPoped) {
                    seekBarUtil.isShowPoped = false;
                    return;
                }
                if (isTitleBarShow) {
                    hideTitleBar();
                } else {
                    showTitleBar();
                }
                break;
            case R2.id.exchange:
                /**切换ppt容器与摄像头视频容器*/
                if (!isVideoViewContainerVisiable()) {
                    return;
                }

                isExchangeViewContainer = !isExchangeViewContainer;
                mHtSdk.exchangeVideoAndWhiteboard();
                break;
            case R2.id.network_choice_iv: //切换视频路线
                //showSwitchLineDialog();
                if (mNetChoiseDiologHelper == null) {
                    mNetChoiseDiologHelper = new NetChoiseDiologHelper(PlaybackNativeActivity.this);
                }
                mNetChoiseDiologHelper.showNetworkChoiceDialog();
                break;
            case R2.id.iv_refresh: //刷新
                //seekBarUtil.resetSeekBarProgress();
                exchangeViewContainer();
                videoViewContainer.setVisibility(View.INVISIBLE);
                mHtSdk.reload();
                break;
            case R2.id.tv_speed:
                showOrHideSpeedList(v);
                break;
            default:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    //下载功能
    private static final int REQUECT_CODE_SDCARD = 2;
    @PermissionGrant(REQUECT_CODE_SDCARD)
    public void requestSdcardSuccess()
    {
        //权限获取成功。去下载。
        Toast.makeText(this, "下载权限获取成功", Toast.LENGTH_SHORT).show();
        mPlaybackMessageView.clickToDownLoad();
    }

    @PermissionDenied(REQUECT_CODE_SDCARD)
    public void requestSdcardFailed()
    {
        Toast.makeText(this, "下载权限获取失败!不能下载", Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示或隐藏播放倍速列表
     *
     * @param anchor
     */
    private void showOrHideSpeedList(View anchor) {
        stopDismissTitleBar();
        if (playSpeedlpw != null && playSpeedlpw.isShowing()) {
            playSpeedlpw.dismiss();
            playSpeedlpw = null;
            autoDismissTitleBar();
            return;
        }
        if (playSpeedlpw == null) {
            playSpeedlpw = new ListPopupWindow(this);
            playSpeedlpw.setAdapter(new ArrayAdapter<String>(this, R.layout.speed_popup_window_item, R.id.list_pop_item, playSpeedStrs));
            playSpeedlpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tvSpeed.setText(playSpeedStrs[position]);
                    mHtSdk.setPlaybackPlaySpeed(playSpeeds[position]);
                    playSpeedlpw.dismiss();
                    playSpeedlpw = null;
                    autoDismissTitleBar();
                }
            });
            playSpeedlpw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    autoDismissTitleBar();
                }
            });
            playSpeedlpw.setBackgroundDrawable(null);
        }

        int lpwWidth = DimensionUtils.dip2px(this, 100);
        int lpwHeight = DimensionUtils.dip2px(this, 150);
        if (anchor != null) {
            playSpeedlpw.setAnchorView(anchor);
            playSpeedlpw.setVerticalOffset(-lpwHeight);
            playSpeedlpw.setHorizontalOffset(-(lpwWidth - anchor.getWidth()) / 2);
        }

        playSpeedlpw.setModal(true);
        playSpeedlpw.setWidth(lpwWidth);
        playSpeedlpw.setHeight(lpwHeight);
        playSpeedlpw.show();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (playSpeedlpw != null && playSpeedlpw.isShowing()) {
            playSpeedlpw.dismiss();
            // playSpeedlpw.setAnchorView(null);
            playSpeedlpw = null;
            autoDismissTitleBar();
            return;
        }
        super.onConfigurationChanged(newConfig);
    }

    //TODO-------------------------------------------view changed------------------------------------

    /**
     * 显示控制按钮和进度条
     */
    @Override
    void showController() {
        seekbarLayout.setVisibility(View.VISIBLE);
        operationContainer.setVisibility(View.VISIBLE);
        titleBar.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏控制按钮和进度条
     */
    @Override
    void hideController() {
        if (seekbarLayout == null)
            return;
        seekbarLayout.setVisibility(View.INVISIBLE);
        operationContainer.setVisibility(View.GONE);
        titleBar.setVisibility(View.GONE);
    }


    /**
     * 切换到全屏
     */
    @Override
    public void layoutChanged() {
        super.layoutChanged();
        //竖屏不显示时长
        boolean isPortrait = ScreenSwitchUtils.getInstance(this).isPortrait();
        boolean isSensorNotFullLandScreen = ScreenSwitchUtils.getInstance(this).isSensorNotFullLandScreen();
        totalDuration.setVisibility(isPortrait == false ? View.VISIBLE : View.GONE);
        if ((mHtSdk != null && mHtSdk.isPlayLocal()) || isSensorNotFullLandScreen) {
            mPlaybackMessageView.hideDownloadButton();
        } else {
            mPlaybackMessageView.showDownloadButton();
        }
        // currentDuration.setVisibility(isPortrait == false ? View.VISIBLE : View.GONE);

    }

    //TODO-------------------------------------------点播初始化以及状态提示------------------------------
//    /**
//     * 专辑Fragment
//     */
//    private PlaybackAlbumFragment albumFragment;

    /**
     * 点播初始化完成回调
     */
    @Override
    public void initSuccess() {
        showTitleBar();
//        showVideoContainer();
        userVideoShow = true;
//        showVideoContainer(videoVisibleIv, true);
        videoVisibleIv.setSelected(mHtSdk.isVideoShow());
        setSeekBar();
        if (mPlaybackMessageView != null) {
            /**
             * 如是有专辑，则在viewpaper添加PlaybackAlbumFragment,在Tab层添加专辑Tab
             */
            mPlaybackMessageView.addAlbumFragment();
        }
        if (mHtSdk.isPlayLocal()) {
            mPlaybackMessageView.hideDownloadButton();
        } else {
            mPlaybackMessageView.showDownloadButton();
        }

        ModuleConfigHelper moduleConfigHelper = mHtSdk.getModuleConfigHelper();
        if (moduleConfigHelper != null && moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_THEFTPROOF_PLAYBACK)) {
            User user = PlaybackInfo.getInstance().getUser();
            startShowWatermark(user != null ? user.getUid() : PlaybackInfo.getInstance().getLiveId());
        }

    }

    private void setSeekBar() {
        seekBar.setClickable(true);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        long duration = PlaybackInfo.getInstance().getDurationLong();

        seekBar.setMax((int) duration);
        totalDuration.setText(TimeUtil.displayHHMMSS((int) duration));
    }

    /**
     * 初始化失败
     *
     * @param msg
     */
    @Override
    public void onInitFail(String msg) {
        Log.d(TAG, "onInitFail: msg");
    }

    protected void registerNetWorkStateReceiver() {
        //如果是离线播放本地回放，不需要网络状态监测
        if (mHtSdk != null && mHtSdk.isPlayLocal()) {
            return;
        }
        super.registerNetWorkStateReceiver();
    }

    //TODO-----------------------------------------activity life------------------------------------------------
    @Override
    public void onBackPressed() {
        gobackAction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        seekBarUtil.updateSeekBar();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mHtSdk.onPause();
        seekBarUtil.stopUpdateSeekBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlaybackMessageView != null) {
            mPlaybackMessageView.clear();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }


    //TODO--------------------------------------------视频播放状态-----------------------------------------

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            currentDuration.setText(TimeUtil.displayHHMMSS(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            seekTo(progress);
        }
    };


    private void play() {
        setPlayingStatus();
        mHtSdk.playbackResume();

        seekBarUtil.updateSeekBar();
    }

    private void pause() {
        setPauseStatus();
        mHtSdk.playbackPause();
        seekBarUtil.stopUpdateSeekBar();
    }

    private void setPlayingStatus() {
        controlIv.setImageResource(R.mipmap.pause);
        mIsPlaying = true;
    }

    private void setPauseStatus() {
        controlIv.setImageResource(R.mipmap.play);
        mIsPlaying = false;
    }

    private void setStopStatus() {
        setPauseStatus();
        seekBarUtil.stopUpdateSeekBar();
        seekBarUtil.resetSeekBarProgress();
    }

    /**
     * 跳转到指定时间点
     */
    @Override
    public void seekTo(long progress) {
        seekBarUtil.seekTo(progress);
    }

    /**
     * 视频播放状态
     *
     * @param status 当前状态
     * @param msg    信息
     */
    @Override
    public void onVideoStatusChange(int status, String msg) {
        switch (status) {
            case OnVideoStatusChangeListener.STATUS_PAUSE:
                setPauseStatus();
                break;
            case OnVideoStatusChangeListener.STATUS_PLAYING:
                setPlayingStatus();
                break;
            case OnVideoStatusChangeListener.STATUS_ERROR:
                StringUtils.tip(getApplicationContext(), msg);
                break;
            case OnVideoStatusChangeListener.STATUS_IDLE:
                setStopStatus();
                break;
            case OnVideoStatusChangeListener.STATUS_COMPLETED:
                Log.d(TAG, "completed");
                // 播放完毕后重新播放
                if (PlaybackInfo.getInstance().isAlbum()) {
                    int currentIndex = PlaybackInfo.getInstance().getCurrentAlbumIndex();
                    List<AlbumItemEntity> albumItemEntities = PlaybackDataManage.getInstance().getAlbumList();
                    if ((albumItemEntities.size() <= 1) || (currentIndex >= albumItemEntities.size() - 1)) {
                        currentIndex = 0;
                    } else {
                        currentIndex++;
                    }
                    seekBarUtil.resetSeekBarProgress();
                    mHtSdk.playAlbum(albumItemEntities.get(currentIndex));
                    return;
                }
                mHtSdk.replayVideo();
                seekBarUtil.resetSeekBarProgress();
                seekBarUtil.updateSeekBar();
                break;
        }
    }

    @Override
    public void error(int code, String msg) {
        Toast.makeText(this, code + "------>>" + msg, Toast.LENGTH_SHORT).show();
        LogUtil.e("error:", code + "------>>" + msg);
    }


    /**
     * 视频播放切换事件监听
     */
    //TODO--------------------------------------------视频播放切换事件监听-------------------------------
    class OnPlayVideoChangeLister implements OnVideoChangeListener {
        /**
         * 视频开始播放
         *
         * @param mode 视频类型
         */
        @Override
        public void onVideoStart(int mode) {
        }

        /**
         * 视频停止播放
         *
         * @param mode 视频类型
         */
        @Override
        public void onVideoStop(int mode) {

        }

        /**
         * 视频播放模式类型切换
         *
         * @param beforeMode  切换前类型
         * @param currentMode 切换后类型
         */
        @Override
        public void onVideoModeChanging(int beforeMode, int currentMode) {

        }

        /**
         * 视频切换完成
         */
        @Override
        public void onVideoModeChanged() {

        }

        /**
         * 摄像头视频显示
         */
        @Override
        public void onCameraShow() {
            if (!userVideoShow) return;
            showVideoContainer(videoVisibleIv, true);
        }

        /**
         * 摄像头视隐藏
         */
        @Override
        public void onCameraHide() {
            exchangeViewContainer();
            showVideoContainer(videoVisibleIv, false);
        }
    }


}
