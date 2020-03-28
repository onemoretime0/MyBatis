package com.hnl.dao;

import com.hnl.pojo.User;
import com.hnl.utils.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDaoTest {
    @Test
    public void Test1() {

        //获取sqlSession对象，只从sql语句
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        //方式一：执行sql
        /*
        sqlSession对象执行sql语句，sql语句从哪里拿呢？
        从UserMapper.class的Class对象中获取UserMapper.xml文件中配置的信息，<mapper>标签就相当与之前的DaoImp类的功能
          UserMapper mapper就是对应的UserMapper接口对象，直接使用mapper调用对应的方法即可
         */
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = mapper.getUserList();

        /*
        两种方式的区别：诚然，方式二能够正常工作，对使用旧版本 MyBatis 的用户来说也比较熟悉。
        但现在有了一种更简洁的方式——使用和指定语句的参数和返回值相匹配的接口（比如 BlogMapper.class），
        现在你的代码不仅更清晰，更加类型安全，还不用担心可能出错的字符串字面值以及强制类型转换。
         */
        //方式二（不推荐）：
        // List<User> userList = sqlSession.selectList("com.hnl.dao.UserMapper.getUserList");
        for (User user : userList) {
            System.out.println(user);
        }
        //关闭sqlSession，一定要关,官方建议是放到finally块中
        sqlSession.close();
    }

    @Test
    public void getUserById() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = mapper.getUserById(1);
        System.out.println(user.toString());

        sqlSession.close();
    }

    //增删改需要提交事务
    @Test
    public void addUserTest() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        int res = mapper.addUser(new User(6, "诸葛亮", "23456"));
        if (res > 0) {
            System.out.println("数据插入成功"); //提交事务,增删改一定要提交事务
            sqlSession.commit();
        }
        sqlSession.close();
    }

    @Test
    public void updateUser() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        int res = mapper.updateUser(new User(4, "佐仓绫音", "12213"));

        if (res > 0) {
            System.out.println("更新成功");
            //提交事务
            sqlSession.commit();
        }

        sqlSession.close();
    }

    @Test
    public void deleteUser() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int res = mapper.deleteUser(5);
        if (res > 0) {
            System.out.println("删除成功");
            sqlSession.commit();
        }
        sqlSession.close();
    }

    @Test
    public void addUser2(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        Map<String, Object> map = new HashMap<>();
        map.put("userId",5);
        map.put("userName","水濑祈");
        map.put("password","123123");
        int res = mapper.addUser2(map);

        if (res >0){
            System.out.println("插入成功");
            sqlSession.commit();
        }
        sqlSession.close();
    }

    @Test
    public void getUserLike(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        //Java代码执行的时候，传递通配符%%，不传就查不出来，但是尽量不要在这里传，要在Mapper.xml文件中进行拼接，防止SQL注入
        //List<User> userLike = mapper.getUserLike("%诸葛%");
        List<User> userLike = mapper.getUserLike("诸葛");
        for (User user : userLike) {
            System.out.println(user);
        }
    }
}
