package com.add.mapperImp;

import com.add.bean.Receipt;
import com.add.mapper.ReceiptMapper;
import com.add.utils.MybatisSessionFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 * Created by Kuajing on 2017/3/15.
 */
public class ReceiptMapperImp implements ReceiptMapper{

    private static SqlSessionFactory sqlSessionFactory = MybatisSessionFactory.getInstance();

    private static ReceiptMapper receiptMapper;

    /**
     * 添加回执订单至表1，先判断是否在表中存在，不存在就add，存在的话则判断时间来决定是否更新
     *
     * 这个就先不弄了。。一个一个插入速度简直慢爆了。。
     * @param receipt
     * @return
     */
    @Override
    public String checkIsExist(Receipt receipt) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        receiptMapper = sqlSession.getMapper(ReceiptMapper.class);
        String  returnTime = receiptMapper.checkIsExist(receipt);
        if (returnTime == null){
            String batchNo = getBatchNo(receipt.getOrderNo());
            if (batchNo == null){
                batchNo = "0";
            }
            receipt.setBatchNo(batchNo);
            addLatest(receipt);
        }else if(Long.parseLong(receipt.getReturnTime()) > Long.parseLong(returnTime)){
            updateLatest(receipt);
        }
        sqlSession.commit();

        return null;
    }

    @Override
    public String getBatchNo(String orderNo) {
        String batchNo = receiptMapper.getBatchNo(orderNo);
        return batchNo;
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
}
