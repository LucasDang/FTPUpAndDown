package com.add.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;


/**
 * Created by Kuajing on 2017/3/7.
 */
public class MybatisSessionFactory {

    private static SqlSessionFactory sqlSessionFactory;
    private  static Reader reader;

    static {
        //配置文件
        String resource = "config/MybatisConfig.xml";
        //加载配置文件到输入流中
        try {
            reader = Resources.getResourceAsReader(resource);
            //创建会话工厂
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static SqlSessionFactory getInstance(){
        return sqlSessionFactory;
    }


}
