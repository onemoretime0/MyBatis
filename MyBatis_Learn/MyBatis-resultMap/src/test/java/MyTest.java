import com.hnl.dao.UserMapper;
import com.hnl.pojo.User;
import com.hnl.utils.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class MyTest {
    @Test
     public void Test1(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User userById = mapper.getUserById(1);

        System.out.println(userById);
        sqlSession.close();
    }
}
