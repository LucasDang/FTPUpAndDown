package com.add.bean;

import java.util.Date;

/**
 * Created by Kuajing on 2017/3/15.
 */
public class Receipt {
    /**
     * 这个对象对应的是两张表：
     * 表一是订单号唯一的回执信息表。字段有：主键、批次号、订单号（唯一）、状态、回执时间、回执内容、上传时间。
     * 表二是所有表的详细信息。字段包含全部信息。
     */

    private int receiptId;

    private String guid;

    private String ebpCode;

    private String ebcCode;

    private String orderNo;

    private String returnStatus;

    private String returnTime;

    private String returnInfo;

    private Date uploadTime;

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getEbpCode() {
        return ebpCode;
    }

    public void setEbpCode(String ebpCode) {
        this.ebpCode = ebpCode;
    }

    public String getEbcCode() {
        return ebcCode;
    }

    public void setEbcCode(String ebcCode) {
        this.ebcCode = ebcCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public String getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }


}
