package com.hnl.dao;

import dao.StudentMapper;
import pojo.Student;
import utils.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class MyTest {
    @Test
    public void getStudentList(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        List<Student> studentList = mapper.getStudentList();
        for (Student student : studentList) {
            System.out.println(student);
        }
        sqlSession.close();

    }
}
