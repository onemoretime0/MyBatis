package com.hnl.dao;

import com.hnl.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserMapper {
    //使用注解
    @Select("select * from user")
    List<User> getUserList();

    //通过id查询
    //方法存在多个参数的时候，所有的j基本类型参数前面必须加上@Param注解
    @Select("select * from user where id = #{id}")
    User getUserById(@Param("id") int id);

    @Insert("insert into user(id,name,pwd) values (#{id},#{name},#{pwd})")
    void addUser(User user);

    //修改
    @Update("update user set name = #{name} ,pwd = #{pwd} where id = #{id}")
    int updateUser(User user);

    @Delete("delete from user where id =#{id}")
    int deleteUser(@Param("id") int id);
}
