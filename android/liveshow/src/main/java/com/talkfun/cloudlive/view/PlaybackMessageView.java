package com.talkfun.cloudlive.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.adapter.FragmentListAdapter;
import com.talkfun.cloudlive.entity.TabEntity;
import com.talkfun.cloudlive.fragment.PlaybackAlbumFragment;
import com.talkfun.cloudlive.fragment.PlaybackChatFragment;
import com.talkfun.cloudlive.fragment.PlaybackQuestionFragment;
import com.talkfun.cloudlive.fragment.PlaybackSectionFragment;
import com.talkfun.cloudlive.interfaces.IDispatchChapter;
import com.talkfun.cloudlive.interfaces.PermissionsListener;
import com.talkfun.cloudlive.util.PopWindowUtil;
import com.talkfun.cloudlive.util.SeekBarHelper;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.data.PlaybackDataManage;
import com.talkfun.sdk.module.PlaybackInfo;
import com.talkfun.sdk.offline.PlaybackDownloader;
import com.talkfun.sdk.offline.http.PreDownLoad;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ccy on 2017/10/21.
 */

public class PlaybackMessageView extends LinearLayout implements View.OnClickListener, BaseMessageView {
    private Context context;
    private long mPreClickTime = 0;

    private String mToken;
    private String mId;
    private List<Fragment> fragmentList;
    private FragmentListAdapter fragmentListAdapter;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private IDispatchChapter mDispatchChapter;
    private SeekBarHelper seekBarUtil;
    /**
     * 专辑Fragment
     */
    private PlaybackAlbumFragment albumFragment;

    private PopWindowUtil mPopWindowUtil;
    //-------------------------常量---------------------------------
    private final String TAG = PlaybackMessageView.class.getName();
    private static final int CHAT_TAB = 0;  //聊天
    private static final int QUESTION_TAB = 1;  //提问
    private static final int SESSION_TAB = 2;  //章节
    private static final int ALBUM_TAB = 3;  //专辑


    private ArrayList<String> mTitles;
    private ArrayList<Integer> mIconUnselectIds;
    private ArrayList<Integer> mIconSelectIds;

    private PlaybackChatFragment chatFragment;
    private PlaybackQuestionFragment questionFragment;
    private PlaybackSectionFragment sectionFragment;

    //------------------------------tab标签 start-------------------------------------

    @BindView(R2.id.common_tablayout)
    CommonTabLayout mCommonTabLayout;
    @BindView(R2.id.view_pager)
    ViewPager mViewPager;
    @BindView(R2.id.iv_start_download)
    ImageView ivStartDownload;


    //--------------------------------tab标签 end--------------------------------------------
    public PlaybackMessageView(Context context) {
        super(context);
    }

    public PlaybackMessageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlaybackMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        View parentView = View.inflate(context, R.layout.tab_container, null);
        ButterKnife.bind(this, parentView);
        init();
        this.addView(parentView);
    }

    @Override
    public void init() {
        initTabLayout();
        initViewPager();
        initEvent();
        mPopWindowUtil = new PopWindowUtil(context);
    }

    @Override
    public void initTabLayout() {
        if (mTitles == null) {
            mTitles = new ArrayList<>();
            mTitles.add("聊天");
            mTitles.add("提问");
            mTitles.add("章节");
        }
        if (mIconSelectIds == null) {
            mIconSelectIds = new ArrayList<>();
            mIconSelectIds.add(R.mipmap.chat_default);
            mIconSelectIds.add(R.mipmap.ask_default);
            mIconSelectIds.add(R.mipmap.session_default);
        }

        if (mIconUnselectIds == null) {
            mIconUnselectIds = new ArrayList<>();
            mIconUnselectIds.add(R.mipmap.chat_click);
            mIconUnselectIds.add(R.mipmap.ask_click);
            mIconUnselectIds.add(R.mipmap.session_clicked);
        }

        for (int i = 0; i < mTitles.size(); i++) {
            mTabEntities.add(new TabEntity(mTitles.get(i), mIconSelectIds.get(i), mIconUnselectIds.get(i)));
        }
        mCommonTabLayout.setTabData(mTabEntities);
       // ivStartDownload.setVisibility(View.VISIBLE);
        ivStartDownload.setVisibility(View.GONE);
    }

    /**
     * 初始化聊天、问答、章节tab页
     */
    public void initViewPager() {
        fragmentList = new ArrayList<>();
        chatFragment = PlaybackChatFragment.create("chat_info");
        fragmentList.add(chatFragment);
        questionFragment = PlaybackQuestionFragment.create("question_info");
        fragmentList.add(questionFragment);
        sectionFragment = PlaybackSectionFragment.create("section");
        mDispatchChapter = sectionFragment;
        fragmentList.add(sectionFragment);
        fragmentListAdapter = new FragmentListAdapter(context, fragmentList);
        mViewPager.setAdapter(fragmentListAdapter);
        mViewPager.setCurrentItem(CHAT_TAB);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCommonTabLayout.setCurrentTab(position);
                if (position == SESSION_TAB) {
                    mDispatchChapter.switchToChapter();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mCommonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }


    /**
     * 注册监听器
     */
    @Override
    public void initEvent() {
        if (ivStartDownload == null) return;
        ivStartDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.e("test","asasasa");
        if(v.getId() == R.id.iv_start_download){//下载
            if(permissionsListener != null){
                permissionsListener.requestPermissions();
            }
        }
    }

    /**
     * 如是有专辑，则在viewpaper添加PlaybackAlbumFragment,在Tab层添加专辑Tab
     */
    public void addAlbumFragment() {
        if (PlaybackInfo.getInstance().isAlbum()) {
            if (albumFragment == null) {
                mTabEntities.add(new TabEntity("专辑", R.mipmap.album_clicked, R.mipmap.album_default));
                mCommonTabLayout.setTabData(mTabEntities);
                albumFragment = PlaybackAlbumFragment.create("album");
                albumFragment.setOnAlbumItemSelectedListener(new PlaybackAlbumFragment.OnAlbumItemSelectedListener() {
                    @Override
                    public void onAlbumItemSelected(int position) {
                        PlaybackInfo.getInstance().setCurrentAlbumIndex(position);
                        seekBarUtil.resetSeekBarProgress();
                        HtSdk.getInstance().playAlbum(PlaybackDataManage.getInstance().getAlbumList().get(position));
                    }
                });
                fragmentList.add(albumFragment);
                fragmentListAdapter.notifyDataSetChanged();
            }
            albumFragment.setAlbumList(PlaybackDataManage.getInstance().getAlbumList());
            albumFragment.setCurrentIndex(PlaybackInfo.getInstance().getCurrentAlbumIndex());
        } else if (albumFragment != null) {
            /**不是专辑处理逻辑*/
            if (fragmentList.contains(albumFragment)) {
                fragmentList.remove(albumFragment);
                fragmentListAdapter.notifyDataSetChanged();
            }
            albumFragment = null;
        }

    }

    @Override
    public void clear() {
        if (mPopWindowUtil != null) {
            mPopWindowUtil.dismissDelayPop();
        }
    }

    /**
     * 下载某个access_token,添加到下载列表
     */
    public void clickToDownLoad() {
        if (!TextUtils.isEmpty(mToken)) {
            //判断下载列表是否已经存在该ID
            if (PlaybackDownloader.getInstance().containsID(mId)) {
                if (System.currentTimeMillis() - mPreClickTime > 2000) {
                    if (mPopWindowUtil != null) {
                        mPopWindowUtil.showDelayPop(getResources().getString(R.string.Downloading_tip), ivStartDownload);
                    }
                    mPreClickTime = System.currentTimeMillis();
                }
                return;
            }
            PlaybackDownloader.getInstance().appendDownloadTask(mToken, mId, null, null, new PreDownLoad.OnappendDownloadListener() {
                @Override
                public void success() {
                    PlaybackDownloader.getInstance().startDownload(mId);
                    if (mPopWindowUtil != null) {
                        mPopWindowUtil.showDelayPop(getResources().getString(R.string.ToDownload_tip), ivStartDownload);
                    }
                }

                @Override
                public void fail(int code, String msg) {
                    if (code == PlaybackDownloader.Callback_Code.OutMemory_Code) {
                        if (mPopWindowUtil != null) {
                            mPopWindowUtil.showDelayPop(getResources().getString(R.string.Not_enough_memory), ivStartDownload);
                        }
                    }
                }
            });
        }
    }

    public void addSeekBarUtil(SeekBarHelper seekBarUtil) {
        this.seekBarUtil = seekBarUtil;
    }

/*    public void setStartDownLoadIsVisibility(boolean isSensorNotFullLandScreen) {
        if (ivStartDownload == null) return;
        ivStartDownload.setVisibility(isSensorNotFullLandScreen == true ? View.GONE : View.VISIBLE);
    }*/

    public void addTokenAndId(String mToken, String mId) {
        this.mToken = mToken;
        this.mId = mId;
    }

    public void clearAllMessage() {
        if (chatFragment != null) {
            chatFragment.clearPlaybackChatMessage();
        }
        if (questionFragment != null) {
            questionFragment.clearPlaybackQuestionMessage();
        }
        if (sectionFragment != null) {
            sectionFragment.clearPlaybackSectionMessage();
        }
    }

    public void showDownloadButton(){
        ivStartDownload.setVisibility(View.VISIBLE);
    }

    public void hideDownloadButton(){
        ivStartDownload.setVisibility(View.GONE);
    }



    public PermissionsListener permissionsListener;

    public void setPermissionsListener(PermissionsListener permissionsListener){
        this.permissionsListener = permissionsListener;
    }
}
