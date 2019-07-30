package com.pinyougou.pojo;

import java.io.Serializable;

public class OrderTotal implements Serializable {
    private Long id;
    private String sellerId;
    private String goodsName;
    private int num;
    private Double totalFee;

    public OrderTotal() {
    }

    public OrderTotal(Long id, String sellerId, String goodsName, int num, Double totalFee) {
        this.id = id;
        this.sellerId = sellerId;
        this.goodsName = goodsName;
        this.num = num;
        this.totalFee = totalFee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Double totalFee) {
        this.totalFee = totalFee;
    }
}
