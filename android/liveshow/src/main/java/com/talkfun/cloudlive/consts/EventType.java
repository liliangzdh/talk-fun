package com.talkfun.cloudlive.consts;

/**
 * eventbus回调类型
 * Created by ccy on 2017/11/14.
 */
public class EventType {
    /**
     * 插入到聊天的信息
     */
    public static final int INSERTCHAT = 0x7f050000;
    /**
     * 添加消息到弹幕
     */
    public static final int ADDDANMAKU = 0x7f050001;
    /**
     * 添加消息到弹幕
     */
    public static final int SHOWTITLEBAR = 0x7f050002;
    /**
     * 查看投票
     */
    public static final int LOOKOVERVOTES = 0x7f050003;
    /**
     * 查看投票结果
     */
    public static final int LOOKOVERVOTERESULTS = 0x7f050004;

    /**
     * 网络状态变化
    */
    public static final int NETWORK_STATE_CHANGE = 0X7F050005;
}


