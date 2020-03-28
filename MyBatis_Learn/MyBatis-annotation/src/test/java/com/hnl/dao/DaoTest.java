package com.hnl.dao;

import com.hnl.pojo.User;
import com.hnl.utils.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class DaoTest {
    @Test
    public void getUserList() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        //底层主要应用反射
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = mapper.getUserList();
        for (User user : userList) {
            System.out.println(user);
        }
        sqlSession.close();
    }

    @Test
    public void getUserById() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        User userById = mapper.getUserById(1);
        System.out.println(userById);

        sqlSession.close();
    }

    @Test
    public void addUser() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        //由于在获取SqlSession对象的工具类中设置的自动提交事务，所以在这里不许要在执行方法后提交事务
        mapper.addUser(new User(7, "凉宫春日", "asdgh2"));

        sqlSession.close();
    }

    @Test
    public void updateUser() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        mapper.updateUser(new User(5, "长门有希", "2345"));
        sqlSession.close();
    }

    @Test
    public void deleteUser() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        mapper.deleteUser(5);

        sqlSession.close();
    }
}
