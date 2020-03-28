package com.hnl.dao;

import com.hnl.pojo.User;
import com.hnl.utils.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class MyTest {
    @Test
    public void getUserList(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user1 = mapper.getUserById(1);
        System.out.println(user1);


        sqlSession.close();
    }
}
