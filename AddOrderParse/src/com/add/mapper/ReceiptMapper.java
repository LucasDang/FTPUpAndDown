package com.add.mapper;

import com.add.bean.Receipt;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by Kuajing on 2017/3/15.
 */
public interface ReceiptMapper {


    /**
     * 更新
     * @param receipt
     */
    void updateLatest(Receipt receipt);

    /**
     * 添加
     * @param receipt
     */
    void addLatest(Receipt receipt);


    //上面为暂没用到的功能，下面为已用功能

    /**
     * 添加全部
     * @param receipts
     */
    void add(List<Receipt> receipts);


    /**
     * 获取批次号
     * @param
     * @return
     */
    String[] getBatchNos(@Param("pageSize")String pageSize);

    /**
     * 根据批次号获取订单个数，用来检查批次号是否存在
     * @param batchNo
     * @return
     */
    int getOrderCountByBatchNo(@Param("batchNo") String batchNo);

    /**
     * 模糊查询批次号
     * @param fuzzyBatchNo
     * @return
     */
    String[] getBatchNosByFuzzy(@Param("fuzzyBatchNo") String fuzzyBatchNo);

    /**
     * 根据批次号获取回执信息的总个数
     * @param batchNo
     */
    String getReceiptsCountByBatchNo(@Param("batchNo")String batchNo,@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("status")String status);

    /**
     * 根据批次号获取回执信息
     * @param batchNo
     * @param pageSize
     * @param startRecord
     */
    List<Receipt> getReceiptsByBatchNo(@Param("batchNo")String batchNo,@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("status")String status,@Param("sort")String sort, @Param("startRecord")int startRecord, @Param("pageSize")int pageSize);


    /**
     * 根据时间获取回执信息的总个数
     */
    String getReceiptsCountByDate(@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("status")String status);

    /**
     * 根据时间获取回执信息
     * @param pageSize
     * @param startRecord
     */
    List<Receipt> getReceiptsByDate(@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("status")String status,@Param("sort")String sort,@Param("startRecord")int startRecord, @Param("pageSize")int pageSize);


    /**
     * 获取回执的所有详细信息
     * @param orderNo
     * @return
     */
    List<Receipt> getReceiptDetail(@Param("orderNo")String orderNo);

}
