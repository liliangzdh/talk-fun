package com.talkfun.cloudlive.entity;

import java.io.Serializable;

/**
 * 点名开始实体类
 */
public class SignEntity implements Serializable {
    private static final long serialVersionUID = 3476114840885073837L;
    private int duration;
    private String signId;
    private String role;
    private String nickname;
    private String time;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSignId() {
        return signId;
    }

    public void setSignId(String signId) {
        this.signId = signId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "SignEntity{" +
                "duration=" + duration +
                ", signId='" + signId + '\'' +
                ", role='" + role + '\'' +
                ", nickname='" + nickname + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
