package com.talkfun.cloudlive.entity;

import java.io.Serializable;

/**
 * 点名结束实体类
 */
public class SignEndEntity implements Serializable {
    private static final long serialVersionUID = 3476114840885073836L;

    private String total;
    private String signTotal;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSignTotal() {
        return signTotal;
    }

    public void setSignTotal(String signTotal) {
        this.signTotal = signTotal;
    }

    @Override
    public String toString() {
        return "SignEndEntity{" +
                "total='" + total + '\'' +
                ", signTotal='" + signTotal + '\'' +
                '}';
    }
}
