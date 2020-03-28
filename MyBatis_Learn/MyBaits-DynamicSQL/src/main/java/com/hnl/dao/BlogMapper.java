package com.hnl.dao;

import com.hnl.pojo.Blog;

import java.util.List;
import java.util.Map;

public interface BlogMapper {
    //插入数据
    int addBlog(Blog blog);

    //查询博客
    List<Blog> queryBlogIF(Map map);
    List<Blog> queryBlogChoose(Map map);

    //更新博客
    int updateBlog(Map map);

    //查询博客中编号为1,2,3 的博客
    List<Blog> getBlogForeach(Map map);
}
