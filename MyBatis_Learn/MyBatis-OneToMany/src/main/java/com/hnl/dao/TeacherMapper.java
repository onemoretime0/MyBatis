package com.hnl.dao;

import com.hnl.pojo.Teacher;
import org.apache.ibatis.annotations.Param;

public interface TeacherMapper {

    //获取指定老师下的所有学生及老师的信息
    Teacher getTeacher(@Param("teacherId") int id);

}
