package com.talkfun.cloudlive.interfaces;

import com.talkfun.sdk.module.ChatEntity;
import com.talkfun.sdk.module.Group;

public interface DispatchPrivateChat {
    void getPrivateChat(ChatEntity chatEntity);
    void newGroup(Group group);
    void groupDestroy(String group);
}
