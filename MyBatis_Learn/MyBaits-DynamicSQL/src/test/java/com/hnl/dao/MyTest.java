package com.hnl.dao;

import com.hnl.pojo.Blog;
import com.hnl.utils.IDUtils;
import com.hnl.utils.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.*;

public class MyTest {

    @Test
    public void addBlog() {

        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);

        Blog blog = new Blog();
       /* blog.setId(IDUtils.getId());
        blog.setTitle("MyBatis如此简单");
        blog.setAuthor("hnl");
        blog.setCreateTime(new Date());
        blog.setViews(9999);
        mapper.addBlog(blog);

        blog.setId(IDUtils.getId());
        blog.setTitle("Spring如此简单");
        blog.setCreateTime(new Date());
        mapper.addBlog(blog);


        blog.setId(IDUtils.getId());
        blog.setTitle("SpringBoot 如此简单");
        blog.setCreateTime(new Date());
        mapper.addBlog(blog);*/

        blog.setId(IDUtils.getId());
        blog.setAuthor("hnl");
        blog.setViews(9999);
        blog.setTitle("趣学算法");
        blog.setCreateTime(new Date());
        mapper.addBlog(blog);

        blog.setId(IDUtils.getId());
        blog.setTitle("java 从入门到放弃");
        blog.setAuthor("hnl");
        blog.setViews(9999);
        blog.setCreateTime(new Date());
        mapper.addBlog(blog);


        sqlSession.close();
    }

    @Test
    public void queryBlogIF() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        Map map = new HashMap();
       /* map.put("title", "算法");*/
        map.put("author","hnl");
        List<Blog> blogs = mapper.queryBlogIF(map);
        for (Blog blog : blogs) {
            System.out.println(blog);
        }
        sqlSession.close();
    }

    @Test
    public void updateBlog(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        Map map = new HashMap();
        //map.put("title", "SQL必知必会");
        map.put("author","法外狂徒张三");
        map.put("id","d25e89e90a234b89832cffa480a4fdde");
        mapper.updateBlog(map);

        sqlSession.close();

    }

    @Test
    public void getBlogForeach(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        Map map = new HashMap();
        map.put("ids",ids);
        List<Blog> blogs = mapper.getBlogForeach(map);

        for (Blog blog : blogs) {
            System.out.println(blog);
        }

        sqlSession.close();

    }
}
