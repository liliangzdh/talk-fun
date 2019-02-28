package com.talkfun.cloudlive.module;

import com.talkfun.cloudlive.entity.ExpressionEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2015/11/24.
 */
public class ExpressionData {
    /** 每一页表情的个数 */
    private int pageSize = 10;
    /**表情列表*/
    private List<ExpressionEntity> expEntity;
    /**分页表情列表*/
    private List<List<ExpressionEntity>> pageExpEntitys;

    public ExpressionData(){
        expEntity = new ArrayList<ExpressionEntity>();
        pageExpEntitys = new ArrayList<List<ExpressionEntity>>();
    }
    /**
     * 解析数据
     * */
    public void parseData(String[] emoCharacters,int[] emoResIds) throws  Exception{
        if(emoCharacters.length != emoResIds.length)
            throw new Exception("参数emoCharacters与emoResIds长度不一致");
        int length = emoCharacters.length;
        expEntity.clear();
        ExpressionEntity expEentity;
        for(int i=0;i<length;i++){
            expEentity = new ExpressionEntity();
            expEentity.resId = emoResIds[i];
            expEentity.character = emoCharacters[i];
            expEntity.add(expEentity);
        }
        int pageCount = (int) Math.ceil(expEntity.size() / pageSize );

        for (int i = 0; i < pageCount; i++) {
            pageExpEntitys.add(getPageData(i));
        }
    }

    public List<List<ExpressionEntity>> getPageEmoEntitys(){
       return pageExpEntitys;
    }

    /**
     * 获取分页数据
     * @param page
     * @return
     */
    private List<ExpressionEntity> getPageData(int page){
        int startIndex = page*pageSize;
        int endIndex = startIndex + pageSize;
        if(endIndex > expEntity.size()){
            endIndex = expEntity.size();
        }
        List<ExpressionEntity> list = new ArrayList<ExpressionEntity>();
        list.addAll(expEntity.subList(startIndex, endIndex));
        return list;
    }
}
