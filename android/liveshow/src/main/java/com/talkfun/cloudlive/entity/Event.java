package com.talkfun.cloudlive.entity;

/**
 * eventBus
 * Created by ccy on 2017/11/14.
 */

public class Event<T> {
    private int type;
    private T data;

    public Event(int type) {
        this.type = type;
    }

    public Event(int type, T data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
