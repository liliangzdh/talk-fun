package com.talkfun.cloudlive.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.talkfun.cloudlive.consts.EventType;
import com.talkfun.cloudlive.entity.Event;
import com.talkfun.cloudlive.util.EventBusUtil;

/**
 * Created by asus on 2018/1/11.
 */

public class NetWorkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = NetMonitor.getNetWorkState(context);
        EventBusUtil.postEvent(new Event(EventType.NETWORK_STATE_CHANGE,state));
    }
}
