package com.hnl.dao;

import com.hnl.pojo.Student;
import com.hnl.pojo.Teacher;
import com.hnl.utils.MyBatisUtils;
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
            System.out.println(student.toString());
        }
        sqlSession.close();
    }

    @Test
    public void getTeacher(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        TeacherMapper mapper = sqlSession.getMapper(TeacherMapper.class);

        Teacher teacher = mapper.getTeacher(1);

        System.out.println(teacher);
        sqlSession.close();
    }

}
