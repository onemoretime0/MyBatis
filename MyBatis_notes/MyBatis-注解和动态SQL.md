# 1.理解面向接口编程

**选择面向接口编程的根本原因：解耦，可扩展，提高复用，分层开发中，不用管具体的实现，大家都遵守共同的标准，使得开发变得容易。**

面向接口的理解：

- 接口从更深层次的理解，应是定义（规范、约束）和实现（名实分离的原则）的分离
- 接口的本身反映了系统设计人员对系统的抽象理解
- 接口应有两类：
	- 第一类是对一个个体的抽象，它可对应为一个抽象体（abstract class）
	- 第二类是对一个个体某一方面的抽象，即形成一个抽象面（interface）
- 一个有可能有多个抽象面，抽象体与抽象面是有区别的，

**三个面向的区别：**

- 面向对象是指，我们考虑问题时，以对象为中心，考虑它的属性及方法
- 面向过程是指，我们考虑问题时，以一个具体的流程（事务过程）为单位，考虑它的实现
- 接口设计和非接口设计是针对复用技术而言，与面向对象（过程）不是一个问题，更多的是体现对系统整体的构架

# 2.使用注解开发

使用注解来映射简单语句会使代码显得更加简洁，但对于稍微复杂一点的语句，Java 注解不仅力不从心，还会让你本就复杂的 SQL 语句更加混乱不堪。 因此，如果你需要做一些很复杂的操作，最好用 XML 来映射语句。

**注解开发的本质：**反射机制

**注解的底层：**动态代理

## 2.1基本使用

使用注解进行开发很多东西就不再需要了：

- DAO层的Mapper.xml就不再需要了，直接在接口上使用注解即可

	- ```java
		 //使用注解
		    @Select("select * from user")
		    List<User> getUserList();
		```

- 在mybatis-config.xml中注册Mapper（这里就必须使用class属性类注册）

	- ```xml
		 <!--绑定接口-->
		    <mappers>
		        <mapper class="com.hnl.dao.UserMapper"/>
		    </mappers>
		```

- 测试代码还是相同：

	- ```java
		@Test
		    public void getUserList(){
		        SqlSession sqlSession = MyBatisUtils.getSqlSession();
		        //底层主要应用反射
		        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
		        List<User> userList = mapper.getUserList();
		        for (User user : userList) {
		            System.out.println(user);
		        }
		        sqlSession.close();
		    }
		/*
		结果：
		User{id=1, name='法外狂徒张三', password='null'}
		User{id=2, name='王司徒', password='null'}
		User{id=3, name='诸葛孔明', password='null'}
		User{id=4, name='佐仓绫音', password='null'}
		User{id=5, name='水濑祈', password='null'}
		User{id=6, name='诸葛亮', password='null'}
		*/
		```

	- ==**注意：**==如果实体类的属性与数据库中的字段不匹配就会产生上面的这种结果，获取不到对应数据


**#{} 与$的区别**：

- ${} ：无法防止sql注入。不安全

## 2.2关于@Param注解

- 基本类型的参数和Srting类型的参数，需要加上
- 引用类型不需要加
- 如果只有一个基本类型，可以不忽略，但是建议加上
- 我们在SQL中引用的就是我们在这里的@Param(" value")中设定的属性名





## 2.3 注解实现CRUD

可以在工具类创建的时候实现自动事务提交。

```java
//MyBatis中获取sqlSessiong对象的方法
public static SqlSession getSqlSession() {
        //openSession方法中传递参数，true表示自动提交事务
        return sqlSessionFactory.openSession(true);
    }
```



**接口：**

```java
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

```





# 3.MyBatis的执行流程刨析

<img src=".\image\MyBatis执行流程刨析.png" style="zoom: 80%;" />

# 4.Lombok的使用

**Lombok是什么？**

- 是一个IDEA插件，简化简化开发

**Lombok的作用：**

- 简化实体类的编写
- 使用注解代替setter和getter

**使用步骤：**

- 在IDEA中安装插件
- 在项目中导入Lombok的jar包
- 在实体类上加注解
- **注意：**在使用了@AllArgsConstructor注解之后，就没有了无参构造，所以需要在再使用 @NoArgsConstructor加上无参构造

```java
@Getter and @Setter
@FieldNameConstants
@ToString
@EqualsAndHashCode
@AllArgsConstructor, @RequiredArgsConstructor and @NoArgsConstructor //全参数构造&无参构造
@Log, @Log4j, @Log4j2, @Slf4j, @XSlf4j, @CommonsLog, @JBossLog, @Flogger, @CustomLog
@Data
@Builder
@SuperBuilder
@Singular
@Delegate
@Value
@Accessors
@Wither
@With
@SneakyThrows
@val
@var
experimental @var
@UtilityClass
```

# 5.复杂查询



## 5.1集合与关联

**多对一：关联（association）**

**一对多：集合（collection）**

## 5.2复杂查询环境搭建

**SQL:**

```sql
CREATE TABLE teacher (
  id INT(10) NOT NULL PRIMARY KEY ,
  name VARCHAR(30) DEFAULT NULL
) ENGINE =INNODB DEFAULT CHAR SET =utf8;

INSERT INTO teacher values (1,'静老师');

CREATE TABLE student(
  id INT(100) NOT NULL PRIMARY KEY,
  name VARCHAR(30) DEFAULT  NULL,
  tid INT(10) DEFAULT NULL,
  KEY fktid (tid),
  CONSTRAINT fktid FOREIGN KEY (tid) REFERENCES teacher (id)
) ENGINE =INNODB CHAR SET =utf8;

INSERT INTO student(id,name ,tid) VALUES
 (1,'比丘谷八幡',1 ),
 (2,'一色',1),
 (3,'团子',1),
(4,'法外狂徒张三',1),
(5,'王司徒',1);
```

实体类：

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    private int id ;
    private String name;
}
//--------------------------------------
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private int id;
    private String name;
    //每个学生需要关联一个老师
    private Teacher teacher;
}
```

- 创建实体类对应的Mapper接口
- 创建Mapper接口对应的Mapper.xml（在resource文件夹下创建与Mapper接口同名的目录，并在此目录下创建Mapper.xml）
- 在核心配置文件中绑定Mapper.xml

## 5.2 多对一的处理

**需求：**

- 查询所有的学生信息，以及对应的老师信息

如果实在SQL中是这样处理的：

```SQL
SELECT s.id,s.name,t.name FROM student s ,teacher t where s.tid=t.id;
/*
1	比丘谷八幡	静老师
2	一色	静老师
3	团子	静老师
4	法外狂徒张三	静老师
5	王司徒	静老师
*/
```

### 5.2.1  按照查询嵌套处理

**步骤：**

- Mapper接口中的定义方法
- 配置Mapper.xml文件
	- 首先先查询student中所有的信息
	- 根据查询到的student的tid查询teacher表中对应的信息

**重点的是对于student表返回的结果使用resultMap结果集映射，在resultMap中配置<association>**

接口：

```java
 //查询所有的学生信息，以及对应的老师信息
    List<Student> getStudentList();
```

Mapper.xml配置：

```xml
  <!--查询所有的学生信息，以及对应的老师信息:
      思路：
        1.查询所有的学生信息
        2.根据查询出来的学生的tid，寻找对应的老师  (子查询)
        -->
    <select id="getStudentList" resultMap="StudentTeacher">
        select * from student
    </select>
    <resultMap id="StudentTeacher" type="Student">
        <result column="id" property="id"/>
        <result column="name" property="name"/>
        <!--复杂属性，需要单独处理
             对象使用：association
             集合使用：collection
        -->
        <!--根据student表中返回的tid属性，查询表teacher中对应的id的结果返回并配置给Student对象的teacher属性-->
        <association column="tid" property="teacher" javaType="Teacher" select="getTeacher"/>
    </resultMap>
    <select id="getTeacher" resultType="Teacher">
    select * from teacher where id = #{tid};
    </select>
```

Test：

```java
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
/*
结果：
Student(id=1, name=比丘谷八幡, teacher=Teacher(id=1, name=静老师))
Student(id=2, name=一色, teacher=Teacher(id=1, name=静老师))
Student(id=3, name=团子, teacher=Teacher(id=1, name=静老师))
Student(id=4, name=法外狂徒张三, teacher=Teacher(id=1, name=静老师))
Student(id=5, name=王司徒, teacher=Teacher(id=1, name=静老师))
*/
```

### 5.2.2按照结果嵌套处理

按照结果处理也需要配置resultMap结果集映射，不同的是，按照结果嵌套处理直接对结果进行映射，而不是对一个查询进行映射。

Mapepr.xml配置：

```xml
<!-- 按照结果嵌套处理
        思路：
        1.查询所有的学生信息
        2.根据查询出来的学生的tid，寻找对应的老师-->
<!--是直接使用SQL一次性将结果查询出来，然后再对结果集进行映射处理-->
<select id="getStudentList" resultMap="studentTeacher">
    SELECT s.id sid,s.name sname,t.name tname
    FROM student s ,teacher t
    where s.tid=t.id;
</select>
<resultMap id="studentTeacher" type="Student">
    <result property="id" column="sid"/>
    <result property="name" column="sname"/>
    <!--复杂类型-->
    <!--
property="teacher"：student的属性名
javaType="Teacher"：Student中teacher属性对应的类型
 <result property="name" column="tname"/>：对 Teacher类型中name属性映射 查询的tname结果
-->
    <association property="teacher" javaType="Teacher">
        <result property="name" column="tname"/>
    </association>
</resultMap>
```

## 5.3一对多的处理

一个老师拥有多个学生，对于老师而言，就是一对多的关系！

环境：

```java
public class Student {
    private int id ;
    private String name;
    private int tid;
}
//---------------------------
public class Teacher {
    private int id;
    private String name;

    //一个老师拥有多个学生
    private List<Student> students;
}
```

### 5.3.1 按结果嵌套处理

```java
//接口
  //获取指定老师下的所有学生及老师的信息
    Teacher getTeacher(@Param("teacherId") int id);
```

Mapper.xml

```xml
<select id="getTeacher" resultMap="TeacherStudent">
        select s.id sid ,s.name sname ,t.name tname,t.id teacherId
        from student s ,teacher t
        where s.tid = t.id and t.id =#{teacherId};
    </select>
    <resultMap id="TeacherStudent" type="Teacher">
        <result property="id" column="teacherId"/>
        <result property="name" column="tname"/>
        <!-- 复杂属性，需要单独处理, 对象使用：association  集合使用：collection
            javaType：指定属性的类型
            ofType：集合中泛型的类型
        -->
        <collection property="students" ofType="Student">
            <result property="id" column="sid"/>
            <result property="name" column="sname"/>
            <result property="tid" column="tid"/>
        </collection>
    </resultMap>
```

Test:

```java
@Test
    public void getTeacher(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        TeacherMapper mapper = sqlSession.getMapper(TeacherMapper.class);

        Teacher teacher = mapper.getTeacher(1);
        System.out.println(teacher);
        sqlSession.close();
    }
/*
结果：
<==    Columns: sid, sname, tname, teacherId
<==        Row: 1, 比丘谷八幡, 静老师, 1
<==        Row: 2, 一色, 静老师, 1
<==        Row: 3, 团子, 静老师, 1
<==        Row: 4, 法外狂徒张三, 静老师, 1
<==        Row: 5, 王司徒, 静老师, 1
<==      Total: 5
Teacher{id=1, name='静老师', students=[Student{id=1, name='比丘谷八幡', tid=0}, Student{id=2, name='一色', tid=0}, Student{id=3, name='团子', tid=0}, Student{id=4, name='法外狂徒张三', tid=0}, Student{id=5, name='王司徒', tid=0}]}
*/
```

### 5.3.2 按查询嵌套处理

理解了子查询的概念，就理解了按查询嵌套处理的意义

Mapper.xml配置：

```xml
 <!--按查询处理-->
    <select id="getTeacher" resultMap="TeacherStudent">
        select * from teacher where id =#{teacherId};
    </select>
    <resultMap id="TeacherStudent" type="Teacher">
        <!--这个标签的意思就是，在teacher表中的查询到的结果的column=id 传给子查询getStudentByTeacherId
        并将子查询的结果集映射给Teacher类的students属性
        -->
        <collection property="students" javaType="ArrayList" ofType="Student" select="getStudentByTeacherId" column="id"/>
    </resultMap>
    <!--这个查询的意思就是根据在teacher表中的id 的值，查询student表中对应的tid = teacher.id的结果
        tid =#{tid} 中参数是在teacher表中查询到的并由<collection>标签中的column属性传入的
    -->
    <select id="getStudentByTeacherId" resultType="Student">
        select * from student where tid =#{tid}
    </select>
```

## 5.4 小结

- 关联 - assocication (多对一)
- 集合 - collection （一对多）
- javaType & ofType
	- javaType： 用来指定实体类中属性的类型
	- ofType：用来指定集合中泛型的类型



**注意点：**

- 保证SQL的可读性，尽量保证通俗易懂
- 注意一对一多和多对一中，属性名和字段的问题

# 6.动态SQL

## 6.1 概述

动态 SQL 是 MyBatis 的强大特性之一。

如果你使用过 JDBC 或其它类似的框架，你应该能理解根据不同条件拼接 SQL 语句有多痛苦，例如拼接时要确保不能忘记添加必要的空格，还要注意去掉列表最后一个列名的逗号。利用动态 SQL，可以彻底摆脱这种痛苦。

**什么是动态SQL?**

==**动态SQL是指根据不同的条件生成不同的SQL语句。**==

**动态SQL相关元素：**：

- if
- choose(when ,otherwise)
- trim(where ,set)
- foreach

**使用动态SQL的建议：**

- 现在MySQL中写出完整的SQL，再对应的去修改称为我们的动态SQL ,实现业务

## 6.2 搭建环境

SQL:

```sql
CREATE table blog (
    id VARCHAR(50) NOT NULL COMMENT '博客id',
    title VARCHAR(100) NOT NULL COMMENT '博客标题',
    author VARCHAR(30) NOT NULL COMMENT '博客作者',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    views INT(30) NOT NULL COMMENT '浏览量'
)ENGINE =  INNODB DEFAULT CHAR SET =utf8;
```

实体类：

```java
@Data
public class Blog {
    private String id;
    private String title;
    private String author;
    private Date createTime;// 在核心配置文件中设置mapUnderscoreToCamelCase解决属性名和子字段名不一致的问题
    private int views;
}
```

工具类：

```java
//生成随机ID的工具类
public class IDUtils {
    public static String getId(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
```

给数据库增加数据：

```java
@Test
    public void addBlog(){

        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);

        Blog blog =new Blog();
        blog.setId(IDUtils.getId());
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
        mapper.addBlog(blog);

        blog.setId(IDUtils.getId());
        blog.setTitle("算法");
        blog.setCreateTime(new Date());
        mapper.addBlog(blog);

        sqlSession.close();
    }
```

## 6.3 动态SQL-IF语句

使用动态 SQL 最常见情景是根据条件包含 where 子句的一部分，比如：

```xml
<select id="findActiveBlogWithTitleLike"
     resultType="Blog">
  SELECT * FROM BLOG
  WHERE state = ‘ACTIVE’
  <if test="title != null">
    AND title like #{title}
  </if>
</select>
<!--这条语句提供了可选的查找文本功能。如果不传入 “title”，那么所有处于 “ACTIVE” 状态的 BLOG 都会返回；如果传入了 “title” 参数，那么就会对 “title” 一列进行模糊查找并返回对应的 BLOG 结果(“title” 的参数值需要包含查找掩码或通配符字符)-->
```

**实例：**

Mapper接口：

```java
 //查询博客
    List<Blog> queryBlogIF(Map map);
```

Mapper.xml:

```xml
 <select id="queryBlogIF" parameterType="map" resultType="blog">
        select * from blog where 1=1
        <if test="title != null">
            and title=#{title}
        </if>
        <if test="author != null">
            and author = #{author}
         </if>
    </select>
<!--在这里配置的信息说明：
	1.select * from blog where 1=1 :如果!(title != null && author != null) 则执行此SQL 
	2.if (title != null)执行：select * from blog where title=#{title}
	3.if (title != null && author != null) 执行select * from blog where title=#{title} and author = #{author}
-->
```

Test:

```java
 Map map = new HashMap();
//  map.put("title","算法");
map.put("author","hnl");
//SQL执行的结果取决于map中的值
List<Blog> blogs = mapper.queryBlogIF(map);
```

## 6.4 动态SQL-``<where>``的使用

先看上面的实例，正常情况下``select * from blog where 1=1``是不会这么写的，会这么写：

```xml
 <select id="queryBlogIF" parameterType="map" resultType="blog">
        select * from blog where
        <if test="title != null">
            and title=#{title}
        </if>
        <if test="author != null">
            and author = #{author}
         </if>
    </select>
```

但是如果这么写了，当所有的IF条件都不匹配的情况下，SQL就会变成这样：

```SQL
select * from blog where 
```

这会导致查询失败。如果匹配的了条件，这条 SQL 会是这样:

```SQL
select * from blog where and title=#{title}
```

这个查询也会失败。

这个问题不能简单地用条件元素来解决。这个问题是如此的难以解决，以至于解决过的人不会再想碰到这种问题。

MyBatis 有一个简单且适合大多数场景的解决办法。而在其他场景中，可以对其进行自定义以符合需求。而这，只需要一处简单的改动

```xml
 <select id="queryBlogIF" parameterType="map" resultType="blog">
        select * from blog
        <where>
        <if test="title != null">
             title=#{title}
        </if>
        <if test="author != null">
            and author = #{author}
         </if>
        </where>
    </select>
```

*where* 元素只会在子元素返回任何内容的情况下才插入 “WHERE” 子句。而且，若子句的开头为 “AND” 或 “OR”，*where* 元素也会将它们去除。



## 6.5 动态SQL - choose(when ,otherwise)

有时候，我们不想使用所有的条件，而只是想从多个条件中选择一个使用。针对这种情况，MyBatis 提供了 choose 元素，它有点像 Java 中的 switch 语句。

官方示例：

```xml
<select id="findActiveBlogLike"
     resultType="Blog">
  SELECT * FROM BLOG WHERE state = ‘ACTIVE’
  <choose>
    <when test="title != null">
      AND title like #{title}
    </when>
    <when test="author != null and author.name != null">
      AND author_name like #{author.name}
    </when>
    <otherwise>
      AND featured = 1
    </otherwise>
  </choose>
</select>
<!--传入了 “title” 就按 “title” 查找，传入了 “author” 就按 “author” 查找的情形。若两者都没有传入，就返回标记为 featured 的 BLOG（这可能是管理员认为，与其返回大量的无意义随机 Blog，还不如返回一些由管理员挑选的 Blog）。-->
```

**实例：**

```xml
  <select id="queryBlogChoose" parameterType="map" resultType="blog">
        select * from blog
        <where>
          <choose>
              <when test="title != null">
                  title = #{title}
              </when>
              <when test="author != null">
                  and author =#{author}
              </when>
              <otherwise>
                  and views= #{views}
              </otherwise>
          </choose>
        </where>
    </select>
<!--
	在添加了choose(when ,otherwise)标签之后：
		1.if (title != null) 会执行：select * from blog where title = #{title}
		2.if ( title != null && author != null) 还是会执行：select * from blog where title = #{title}
		3.if (! ( title != null && author != null)) 会执行：select * from blog where views= #{views}
	总之，这里的所有的条件中只会执行其中一个（Java中的 swich）
-->
```

## 6.6 动态SQL - trim(where ,set)

用于动态更新语句的类似解决方案叫做 *set*。*set* 元素可以用于动态包含需要更新的列，忽略其它不更新的列。比如：

```xml
<update id="updateAuthorIfNecessary">
  update Author
    <set>
      <if test="username != null">username=#{username},</if>
      <if test="password != null">password=#{password},</if>
      <if test="email != null">email=#{email},</if>
      <if test="bio != null">bio=#{bio}</if>
    </set>
  where id=#{id}
</update>
<!--这个例子中，*set* 元素会动态地在行首插入 SET 关键字，并会删掉额外的逗号（这些逗号是在使用条件语句给列赋值时引入的）。-->
```

**实例：**

```xml
 <update id="updateBlog" parameterType="map">
        update mybatis.blog
        <set>
            <if test="title != null">
                title = #{title},
            </if>
            <if test="author != null">
                author = #{author},
            </if>
        </set>
        where id =#{id}
    </update>
<!--
	执行情况(前提是id != null):
		1.if (title != null)：执行update mybatis.blog title = #{title} where id =#{id}
		2.if (author != null): 执行update mybatis.blog author = #{author} where id =#{id}
		3.if (id == null)  :此时拼接的SQL为 执行update mybatis.blog  where id =#{id}即使前面的条件符合也是会报错，因为，UPDATE语句已经错了
-->
```

**trim：**

通过自定义 trim 元素来定制 *where* 元素的功能。比如，和 *where* 元素等价的自定义 trim 元素为：

```xml
<trim prefix="WHERE" prefixOverrides="AND |OR ">
  ...
</trim>
<!--prefixOverrides 属性会忽略通过管道符分隔的文本序列（注意此例中的空格是必要的）。上述例子会移除所有 prefixOverrides 属性中指定的内容，并且插入 prefix 属性中指定的内容。-->
```

与 *set* 元素等价的自定义 *trim* 元素：

```xml
<trim prefix="SET" suffixOverrides=",">
  ...
</trim>
<!--我们覆盖了后缀值设置，并且自定义了前缀值-->
```

==**所谓的动态SQL，本质还是SQL ，只是我们可以在SQL层面上去执行一个逻辑代码**==



## 6.7  动态SQL - foreach

动态 SQL 的另一个常见使用场景是对集合进行遍历（尤其是在构建 IN 条件语句的时候）。官方示例：

```xml
<select id="selectPostIn" resultType="domain.blog.Post">
  SELECT *
  FROM POST P
  WHERE ID in
  <foreach item="item" index="index" collection="list"
      open="(" separator="," close=")">
        #{item}
  </foreach>
</select>
```

```xml
<!--查询表中id =1 ,2,3 的记录-->
<!--原SQL:select * from user where 1=1 and (id=1 or id =2 or id =3)-->
select * from user where 1=1 and 
<!--返回的集合是id的集合，集合中的元素是id 分割符是or -->
<foreach item="id" index="index" collection="ids"
      open="(" separator="or" close=")">
        #{id}
  </foreach>
(id=1 or id =2 or id =3)
```

**实例：**

Mapper接口：

```java
//查询博客中编号为1,2,3 的博客
    List<Blog> getBlogForeach(Map map);
```

Mapper.xml配置：

```xml
  <!--
		原SQL：
        select * from blog where 1=1 and (id =1 or id =2 or id =3);
        我们现在可以传递一个万能的map,这个map中可以存在一个集合
		<foreach>标签里面的是对上面的SQL进行拼接，以达到动态性
		首先：
			1.getBlogForeach方法的参数是一个map集合
			2.collection="ids" 表示map中的需要存入一个集合，并且集合的key是ids
			3.集合中的元素是item="id"
			4.使用open="and (" close=")" separator="or" ，并遍历集合中的id的值拼成原来SQL的样子进行查询
			5.由于使用了<where>标签, 所以最后的SQL语句变成了：
				select * from blog where (id =#{id} or id =#{id} or id =#{id});
				其中的id参数就是从ids中遍历取出的
    -->
    <select id="getBlogForeach" parameterType="map" resultType="blog">
        select * from blog
        <where>
            <foreach collection="ids" item="id" open="(" close=")" separator="or">
                id =#{id}
            </foreach>
        </where>
    </select>
```



# 7.SQL 片段



将一些功能部分的代码抽取出来，方便复用。

前面例子我们不难看出，对于title 与author的判断 非常的频繁，所以可以将这部分的代码抽取出来放到<sql>标签中，在需要的地方使用<include>引入即可：

```xml
<sql id="if-TitleAndAuthor" >
        <if test="title != null">
            title=#{title}
        </if>
        <if test="author != null">
            and author = #{author}
        </if>
    </sql>
    <select id="queryBlogIF" parameterType="map" resultType="blog">
        select * from blog
        <where>
           <include refid="if-TitleAndAuthor"/>
        </where>
    </select>
```

注意事项：

- 最好基于单表来定义sql片段
- 不要存在where标签

# 8.数据库面试面试高频

- MySQL引擎
- InnoDB底层原理
- 索引
- 索引优化



