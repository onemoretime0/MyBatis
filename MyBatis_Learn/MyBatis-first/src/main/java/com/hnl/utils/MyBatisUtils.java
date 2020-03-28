package com.hnl.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

//获取SqlSessionFactory对象-->SqlSession
public class MyBatisUtils {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            //使用MyBatis的第一步：获取SqlSessionFactory对象（这三行代码时官网写死的，必须这么写）
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //既然有了 SqlSessionFactory，顾名思义，我们可以从中获得 SqlSession 的实例
    public static SqlSession getSqlSession(){
        return sqlSessionFactory.openSession();
    }

}
