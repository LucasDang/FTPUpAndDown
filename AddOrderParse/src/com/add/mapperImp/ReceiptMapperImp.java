package com.add.mapperImp;

import com.add.bean.Receipt;
import com.add.mapper.ReceiptMapper;
import com.add.utils.MybatisSessionFactory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by Kuajing on 2017/3/15.
 */
public class ReceiptMapperImp implements ReceiptMapper{

    private static SqlSessionFactory sqlSessionFactory = MybatisSessionFactory.getInstance();

    private static ReceiptMapper receiptMapper;



    /**
     * 根据批次号获取订单个数，用来检查批次号是否存在
     * @param batchNo
     * @return
     */
    @Override
    public int getOrderCountByBatchNo(@Param("batchNo")String batchNo) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ReceiptMapper receiptMapper1 = sqlSession.getMapper(ReceiptMapper.class);
        int count = receiptMapper1.getOrderCountByBatchNo(batchNo);
        return count;
    }

    /**
     * 模糊查询批次号
     * @param fuzzyBatchNo
     * @return
     */
    @Override
    public String[] getBatchNosByFuzzy(@Param("fuzzyBatchNo") String fuzzyBatchNo) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ReceiptMapper receiptMapper1 = sqlSession.getMapper(ReceiptMapper.class);
        String[] batchNos = receiptMapper1.getBatchNosByFuzzy(fuzzyBatchNo);
        return batchNos;
    }

    @Override
    public String[] getBatchNos(@Param("pageSize")String pageSize) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ReceiptMapper receiptMapper1 = sqlSession.getMapper(ReceiptMapper.class);
        String[] batchNos = receiptMapper1.getBatchNos(pageSize);
        return batchNos;
    }

    @Override
    public void addLatest(Receipt receipt) {
        receiptMapper.addLatest(receipt);
    }

    @Override
    public void add(List<Receipt> receipts) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ReceiptMapper receiptMapper1 = sqlSession.getMapper(ReceiptMapper.class);
        receiptMapper1.add(receipts);
        sqlSession.commit();
    }

    @Override
    public void updateLatest(Receipt receipt) {
        receiptMapper.updateLatest(receipt);
    }


    /**
     * 根据批次号获取回执信息
     * @param batchNo
     * @param sort
     * @param startRecord
     * @param pageSize
     * @return
     */
    @Override
    public List<Receipt> getReceiptsByBatchNo(@Param("batchNo") String batchNo,@Param("startDate")Date startDate,@Param("endDate")Date endDate, @Param("status")String status, @Param("sort") String sort, @Param("startRecord") int startRecord, @Param("pageSize") int pageSize) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ReceiptMapper receiptMapper1 = sqlSession.getMapper(ReceiptMapper.class);
        List<Receipt> receipts = receiptMapper1.getReceiptsByBatchNo(batchNo,startDate,endDate,status,sort,startRecord,pageSize);
        return receipts;
    }

    @Override
    public String getReceiptsCountByBatchNo(@Param("batchNo")String batchNo,@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("status")String status){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ReceiptMapper receiptMapper1 = sqlSession.getMapper(ReceiptMapper.class);
        String totalRecord = receiptMapper1.getReceiptsCountByBatchNo(batchNo,startDate,endDate,status);
        return totalRecord;
    }



    /**
     * 根据时间获取回执信息的总个数
     */
    @Override
    public String getReceiptsCountByDate(@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("status")String status){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ReceiptMapper receiptMapper1 = sqlSession.getMapper(ReceiptMapper.class);
        String totalRecord = receiptMapper1.getReceiptsCountByDate(startDate,endDate,status);

        return totalRecord;
    };


    /**
     * 根据时间获取回执信息
     * @param startDate
     * @param endDate
     * @param sort
     * @param startRecord
     * @param pageSize
     * @return
     */
    @Override
    public List<Receipt> getReceiptsByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("status")String status, @Param("sort") String sort, @Param("startRecord") int startRecord, @Param("pageSize") int pageSize) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ReceiptMapper receiptMapper1 = sqlSession.getMapper(ReceiptMapper.class);
        List<Receipt> receipts = receiptMapper1.getReceiptsByDate(startDate,endDate,status,sort,startRecord,pageSize);

        return receipts;
    }

    /**
     * 根据订单号查询此订单的所有回执信息
     * @param orderNo
     * @return
     */
    @Override
    public List<Receipt> getReceiptDetail(@Param("orderNo")String orderNo){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ReceiptMapper receiptMapper1 = sqlSession.getMapper(ReceiptMapper.class);
        List<Receipt> receipts = receiptMapper1.getReceiptDetail(orderNo);

        return receipts;
    }

}
