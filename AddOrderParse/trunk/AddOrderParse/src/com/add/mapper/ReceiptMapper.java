package com.add.mapper;

import com.add.bean.Receipt;

import java.util.List;

/**
 * Created by Kuajing on 2017/3/15.
 */
public interface ReceiptMapper {


    /**
     * 根据订单号查询当前订单主键来判断是否已经存过,并取出时间戳来进行判断是否需要更新
     * @param receipt
     * @return
     */
    String checkIsExist(Receipt receipt);

    /**
     * 根据订单号查询订单表，获取批次号
     * @param orderNo
     * @return
     */
    String getBatchNo(String orderNo);

    /**
     * 添加
     * @param receipt
     */
    void addLatest(Receipt receipt);

    /**
     * 更新
     * @param receipt
     */
    void updateLatest(Receipt receipt);

    /**
     * 添加全部
     * @param receipts
     */
    void add(List<Receipt> receipts);



    //void getReceipts();




}
