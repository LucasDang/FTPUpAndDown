package com.add.bean;

import java.util.List;

/**
 * Created by Kuajing on 2017/4/11.
 */
public class ReceiptList {

    public List<Receipt> receipts;

    public String totalRecord;

    public List<Receipt> getReceipts() {
        return receipts;
    }

    public void setReceipts(List<Receipt> receipts) {
        this.receipts = receipts;
    }

    public String getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(String totalRecord) {
        this.totalRecord = totalRecord;
    }
}
