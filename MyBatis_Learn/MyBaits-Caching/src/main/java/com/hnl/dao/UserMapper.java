package com.hnl.dao;

import com.hnl.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    List<User> getUserList();

    User getUserById(@Param("id") int id);
}
