package com.hnl.dao;

import com.hnl.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper {


    //根据id查询用户
    User getUserById(int id);
    //Limit分页查询
    List<User> getUserByLimit(Map<String,Integer> map);
    //RowBounds分页查询
    List<User> getUserByRowBounds();
}
