import com.hnl.dao.UserMapper;
import com.hnl.pojo.User;
import com.hnl.utils.MyBatisUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTest {
    static  Logger logger = org.apache.log4j.Logger.getLogger(MyTest.class);

    @Test
    public void Test1() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User userById = mapper.getUserById(1);

        System.out.println(userById);
        sqlSession.close();
    }

    @Test
    public void testLog4J() {

        logger.info("info: 进入了testLog4J");
        logger.debug("debug:进入了testLog4J");
        logger.error("error: 进入了testLog4J");
    }

    @Test
    public void getUserByLimit(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        Map<String, Integer> map = new HashMap<>();
        map.put("startIndex",0);
        map.put("pageSize",2);
        List<User> userByLimit = mapper.getUserByLimit(map);
        for (User user : userByLimit) {
            System.out.println(user);
        }
        sqlSession.close();
    }
    @Test
    public void getUserByRowBounds(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        //RowBounds实现
        RowBounds rowBounds = new RowBounds(0, 2);

        //通过Java代码层面实现分页
        List<User> userList = sqlSession.selectList("com.hnl.dao.UserMapper.getUserByRowBounds",null,rowBounds);
        for (User user : userList) {
            System.out.println(user);
        }

        sqlSession.close();
    }
}
