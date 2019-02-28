package com.talkfun.cloudlive.interfaces;


import com.talkfun.sdk.module.ChapterEntity;

import java.util.List;


public interface DispatchChapter {
    void getChapterList(List<ChapterEntity> chapterEntityList);

    void switchToChapter();

}
