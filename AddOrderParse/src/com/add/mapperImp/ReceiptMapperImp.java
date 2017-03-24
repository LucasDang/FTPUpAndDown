package com.add.mapperImp;

import com.add.bean.Receipt;
import com.add.mapper.ReceiptMapper;
import com.add.utils.MybatisSessionFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * Created by Kuajing on 2017/3/15.
 */
public class ReceiptMapperImp implements ReceiptMapper{

    private static SqlSessionFactory sqlSessionFactory = MybatisSessionFactory.getInstance();

    @Override
    public Receipt checkIsExist(String orderNo) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ReceiptMapper receiptMapper = sqlSession.getMapper(ReceiptMapper.class);
        Receipt receipt = receiptMapper.checkIsExist(orderNo);

        return receipt;
    }

    @Override
    public void add(Receipt receipt) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ReceiptMapper receiptMapper = sqlSession.getMapper(ReceiptMapper.class);

        receiptMapper.add(receipt);

        sqlSession.commit();
    }

    @Override
    public void update(Receipt receipt) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        ReceiptMapper receiptMapper = sqlSession.getMapper(ReceiptMapper.class);

        receiptMapper.update(receipt);

        sqlSession.commit();
    }
}
