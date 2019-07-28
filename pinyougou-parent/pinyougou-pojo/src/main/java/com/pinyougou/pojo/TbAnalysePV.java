package com.pinyougou.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Table(name = "tb_analyse_PV")
public class TbAnalysePV implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "startTime")
    private Date startTime;


    @Column(name = "endTime")
    private Date endTime;


    @Column(name = "num")
    private Long num;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }
}