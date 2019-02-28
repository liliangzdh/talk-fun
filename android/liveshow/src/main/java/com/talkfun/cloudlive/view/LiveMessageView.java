package com.talkfun.cloudlive.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.utils.UnreadMsgUtils;
import com.flyco.tablayout.widget.MsgView;
import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.adapter.FragmentListAdapter;
import com.talkfun.cloudlive.consts.EventType;
import com.talkfun.cloudlive.entity.Event;
import com.talkfun.cloudlive.entity.TabEntity;
import com.talkfun.cloudlive.fragment.ChatFragment;
import com.talkfun.cloudlive.fragment.NoticeFragment;
import com.talkfun.cloudlive.fragment.QuestionFragment;
import com.talkfun.cloudlive.interfaces.IDispatchChatMessage;
import com.talkfun.cloudlive.interfaces.IDispatchNotice;
import com.talkfun.cloudlive.interfaces.IDispatchQuestion;
import com.talkfun.cloudlive.util.DimensionUtils;
import com.talkfun.cloudlive.util.EventBusUtil;
import com.talkfun.cloudlive.util.StringUtils;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.consts.BroadcastCmdType;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.event.HtDispatchChatMessageListener;
import com.talkfun.sdk.event.HtDispatchNoticeListener;
import com.talkfun.sdk.event.HtDispatchQuestionListener;
import com.talkfun.sdk.module.ChatEntity;
import com.talkfun.sdk.module.NoticeEntity;
import com.talkfun.sdk.module.QuestionEntity;
import com.talkfun.sdk.module.RoomInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 标签与滑动页
 * Created by ccy on 2017/10/21.
 */

public class LiveMessageView extends LinearLayout implements BaseMessageView, HtDispatchChatMessageListener,
        HtDispatchQuestionListener, HtDispatchNoticeListener, ChatFragment.OnChatOperationListener {
    private Context context;
    private IPageChange mIPageChange;

    private List<Fragment> fragmentList;
    private FragmentListAdapter fragmentListAdapter;

    private ChatFragment chatFragment; //聊天Fragment
    private QuestionFragment questionFragment; //问答Fragment
    private NoticeFragment noticeFragment;  //公告Fragment

    public IDispatchChatMessage dispatchChatMessage;
    private IDispatchQuestion dispatchQuestion;
    private IDispatchNotice dispatchNotice;

    private ArrayList<Object> chatMessageEntityList = new ArrayList<>(); //聊天消息列表
    private ArrayList<QuestionEntity> questionEntitiesList = new ArrayList<>();  //问答消息列表
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private RoomInfo roomInfo;
    private HtSdk mHtSdk;
    //-------------------------常量---------------------------------
    private final String TAG = LiveMessageView.class.getName();
    public static final int CHAT_TAB = 0;  //聊天
    public static final int QUESTION_TAB = 1;  //提问
    public static final int NOTIFY_TAB = 2;  //公告

    private String[] mTitles = {"聊天", "提问", "公告"};
    private int[] mIconUnselectIds = {
            R.mipmap.chat_click, R.mipmap.ask_click,
            R.mipmap.broadcast_click};
    private int[] mIconSelectIds = {
            R.mipmap.chat_default, R.mipmap.ask_default,
            R.mipmap.broadcast_default};


    //------------------------------tab标签 start-------------------------------------
    //@BindView(R.id.common_tablayout)
    CommonTabLayout mCommonTabLayout;
   // @BindView(R.id.view_pager)
    ViewPager mViewPager;

    //--------------------------------tab标签 end--------------------------------------------

    public LiveMessageView(Context context) {
        super(context);
    }

    public LiveMessageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        View parentView = View.inflate(getContext(), R.layout.tab_container, null);
        mCommonTabLayout = parentView.findViewById(R.id.common_tablayout);
        mViewPager = parentView.findViewById(R.id.view_pager);
       // ButterKnife.bind(this, parentView);
        init();
        this.addView(parentView);
    }

    /**
     * 添加滑动监听
     */
    public void addIPageChangeListener(IPageChange mIPageChange) {
        this.mIPageChange = mIPageChange;
    }

    /**
     * 添加房间信息
     *
     * @param roomInfo
     */
    public void addRoomInfo(RoomInfo roomInfo) {
        this.roomInfo = roomInfo;
    }

    @Override
    public void init() {
        initTabLayout();
        initViewPager();
        initEvent();
    }

    @Override
    public void initTabLayout() {

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        mCommonTabLayout.setTabData(mTabEntities);
    }

    @Override
    public void initEvent() {
        if (chatFragment != null) {
            chatFragment.setOnChatOperationListener(this);
        }
    }

    @Override
    public void initViewPager() {
        fragmentList = new LinkedList<>();
        createChatFragment();
        createQuestionFragment();
        createNoticeFragment();
        fragmentListAdapter = new FragmentListAdapter(context, fragmentList);
        mViewPager.setAdapter(fragmentListAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCommonTabLayout.setCurrentTab(position);
                mIPageChange.pageChange(position);
                mCommonTabLayout.hideMsg(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mCommonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
                mCommonTabLayout.hideMsg(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    /**
     * 注册监听器
     */
    public void initListener() {
        mHtSdk = HtSdk.getInstance();
        /**设置聊天信息事件监听*/
        mHtSdk.setHtDispatchChatMessageListener(this);
        /**设置公告事件监听*/
        mHtSdk.setHtDispatchNoticeListener(this);
        /**设置问答信息事件监听*/
        mHtSdk.setHtDispatchQuestionListener(this);


    }

    /**
     * 创建聊天Fragment
     */
    private void createChatFragment() {
        chatFragment = ChatFragment.create(chatMessageEntityList);
        dispatchChatMessage = chatFragment;
        fragmentList.add(chatFragment);
    }

    /**
     * 创建问答Fragment
     */
    private void createQuestionFragment() {
        questionFragment = QuestionFragment.create(questionEntitiesList);
        dispatchQuestion = questionFragment;
        fragmentList.add(questionFragment);
    }

    /**
     * 创建公告Fragment
     */
    private void createNoticeFragment() {
        noticeFragment = new NoticeFragment();
        dispatchNotice = noticeFragment;
        fragmentList.add(noticeFragment);
    }

    /**
     * 获取页码
     *
     * @return
     */
    public int getCurrentItem() {
        if (mViewPager == null) {
            return CHAT_TAB;
        }
        return mViewPager.getCurrentItem();
    }


    @Override
    public void clear() {

    }


    //TODO------------------------------------------接受聊天与提问-------------------------------------
    //接收聊天消息
    @Override
    public void receiveChatMessage(ChatEntity chatMessageEntity) {
        dispatchChatMessage.setChatMessage(chatMessageEntity);
        showNewChatMsg();
    }

    //接受公告消息
    @Override
    public void receiveNotice(NoticeEntity noticeEntity) {
        if (TextUtils.isEmpty(noticeEntity.getContent()))
            return;
        dispatchNotice.getNotice(noticeEntity);
        if (getCurrentItem() != NOTIFY_TAB) {
            setRedDot(NOTIFY_TAB);
        }
    }

    //显示有新的聊天信息
    public void showNewChatMsg() {
        if (getCurrentItem() != CHAT_TAB) {
            setRedDot(CHAT_TAB);
        }
    }

    private List<QuestionEntity> notAnswerQuestions = new ArrayList<>();

    //接受问答消息  --ccy
    @Override
    public void receiveQuestion(QuestionEntity questionEntity) {
        if (questionEntity != null) {
            //如果是自己的问题,或者该问题有答案,则直接显示
            if (questionEntity.isHasAnswer() || (roomInfo != null && roomInfo.getUser().getXid().equals(questionEntity.getXid() + ""))) {
                dispatchQuestion.setQuestion(questionEntity);
                showNewQuestionTips(questionEntity);
            } else {
                //如果是答案,则在没有回答的列表中查询是否包含该问题的答案.
                //如果该答案的问题在没有回答的列表中, 则将问题和答案都传给 QuestionFragment .
                //String sn = questionEntity.getSn();
                // if (sn.equals("-1") || sn.equals("0")) {
                if (questionEntity.isAnswer()) {
                    String replyId = questionEntity.getReplyId();
                    QuestionEntity tmpQuestionEntity;
                    for (int i = notAnswerQuestions.size() - 1; i >= 0; i--) {
                        tmpQuestionEntity = notAnswerQuestions.get(i);
                        if (tmpQuestionEntity.getId().equals(replyId)) {
                            tmpQuestionEntity.setHasAnswer(true);
                            //找到该答案的问题.则将该问题传入QuestionFragment .并把问题从没有回答的问题列表中移除
                            dispatchQuestion.setQuestion(tmpQuestionEntity);
                            notAnswerQuestions.remove(i);
                            break;
                        }
                    }
                    //无论是否找到该答案的问题.都将该答案传给QuestionFragment
                    dispatchQuestion.setQuestion(questionEntity);
                    showNewQuestionTips(questionEntity);
                } else {
                    // 如果是老师的提问,则直接显示
                  /*  if (questionEntity.getRole().equals("admin") || questionEntity.getRole().equals("spadmin")) {
                        dispatchQuestion.setQuestion(questionEntity);
                        showNewQuestionTips(questionEntity);
                    } else {
                        //如果是问题.则直接插入没有回答的问题列表
                        notAnswerQuestions.add(questionEntity);
                    }*/
                    dispatchQuestion.setQuestion(questionEntity);
                    showNewQuestionTips(questionEntity);
                }
            }
        }
    }

    /**
     * 提示有新的提问回答。
     *
     * @param questionEntity
     */
    private void showNewQuestionTips(QuestionEntity questionEntity) {
        if (getCurrentItem() != QUESTION_TAB) {
            setRedDot(QUESTION_TAB);
        }
        if (getCurrentItem() == CHAT_TAB && roomInfo.getUser().getXid().equals(questionEntity.getQuestionXid())) {  //有人回复自己的问题
            chatFragment.showReplyTip(true);
        }
    }

    /**
     * 发送聊天消息
     *
     * @param content
     */
    public void sendChatMessage(final String content) {
        if (!TextUtils.isEmpty(content)) {
            if (mHtSdk == null) {
                return;
            }
            mHtSdk.emit(BroadcastCmdType.CHAT_SEND, content, new Callback() {
                @Override
                public void success(Object result) {
                /*    if (chatFragment != null) {
                        chatFragment.appChatMessage(result);
                    }*/
                }

                @Override
                public void failed(String failed) {
                    if (!TextUtils.isEmpty(failed) && context != null) {
                        Toast.makeText(context, failed, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * 发送提问消息
     *
     * @param content
     */
    public void sendQuestion(String content) {
        if (!TextUtils.isEmpty(content)) {
            if (mHtSdk == null) return;
            mHtSdk.emit(BroadcastCmdType.QUESTION_ASK, content, new Callback() {
                @Override
                public void success(Object result) {
                }

                @Override
                public void failed(String failed) {
                    if (!TextUtils.isEmpty(failed)) {
                        StringUtils.tip(context, failed);
                    }
                }
            });
        }
    }

    /**
     * 发送聊天消息
     *
     * @param content
     */
    public void onSendMessage(final String content) {
        if (mViewPager == null)
            return;
        if (getCurrentItem() == CHAT_TAB) {
            sendChatMessage(content);
        } else {
            sendQuestion(content);
        }
    }

    /**
     * 显示通知消息
     */
    public void showNotice() {
        dispatchNotice.getNotice(roomInfo.getNoticeEntity());
        if (getCurrentItem() != NOTIFY_TAB) {
            setRedDot(NOTIFY_TAB);
        }
    }

    /**
     * 插入到聊天信息
     *
     * @param parcelable
     */
    public void insertChatMessage(Object parcelable) {
        dispatchChatMessage.setChatMessage(parcelable);
        showNewChatMsg();

    }

    /**
     * 设置提示红点
     *
     * @param position
     */
    private void setRedDot(int position) {
        //设置未读消息红点
        mCommonTabLayout.showDot(position);
        MsgView rtv_2_2 = mCommonTabLayout.getMsgView(position);
        if (rtv_2_2 != null) {
            UnreadMsgUtils.setSize(rtv_2_2, DimensionUtils.dip2px(context, 8f));
        }
    }

    /**
     * 添加到弹幕中
     *
     * @param msg
     */
    @Override
    public void appendNewChatMes(SpannableString msg) {

        EventBusUtil.postEvent(new Event(EventType.ADDDANMAKU, msg));
    }

    /**
     * 切换到提问页
     */
    @Override
    public void jumpToQuestionPage() {
        if (mViewPager == null) return;
        mViewPager.setCurrentItem(QUESTION_TAB);
    }

    /**
     * 页面切换
     */
    public void pageChanged() {
        if (mCommonTabLayout != null && mViewPager != null) {
            mCommonTabLayout.setCurrentTab(mViewPager.getCurrentItem());
        }
    }

    /***
     * 清空聊天及提问信息
     */
    public void clearChatAndQuestionMessage() {
        if (chatFragment != null) {
            chatFragment.clearAllMessage();
        }
        if (questionFragment != null) {
            questionFragment.clearAllQuestionMessage();
        }
    }

    public interface IPageChange {
        void pageChange(int position);

    }

}
