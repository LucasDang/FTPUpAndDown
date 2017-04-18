package com.add.mapperImp;

import com.add.mapper.OrderMapper;
import com.add.utils.MybatisSessionFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 * Created by Kuajing on 2017/3/31.
 */
public class OrderMapperImp implements OrderMapper{

    private static SqlSessionFactory sqlSessionFactory = MybatisSessionFactory.getInstance();

    @Override
    public List<String> getBatchNos() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        OrderMapper orderMapper = sqlSession.getMapper(OrderMapper.class);
        List<String> batchNos = orderMapper.getBatchNos();

        return batchNos;
    }


}
