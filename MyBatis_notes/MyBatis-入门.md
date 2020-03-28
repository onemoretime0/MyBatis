# 1.什么是MyBatis

## 1.1 MyBatis简介

- MyBatis 是一款优秀的**持久层框架**，
- 它支持自定义 SQL、存储过程以及高级映射。
- MyBatis 免除了几乎所有的 JDBC 代码以及设置参数和获取结果集的工作。MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和 Java POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录。

以下来源于百度百科：

- MyBatis 本是[apache](https://baike.baidu.com/item/apache/6265)的一个开源项目[iBatis](https://baike.baidu.com/item/iBatis), 2010年这个项目由apache software foundation 迁移到了google code，并且改名为MyBatis 。2013年11月迁移到Github。
- iBATIS一词来源于“internet”和“abatis”的组合，是一个基于Java的[持久层](https://baike.baidu.com/item/持久层/3584971)框架。iBATIS提供的持久层框架包括SQL Maps和Data Access Objects（DAOs）

**如何获得MyBatis**

- maven
- Github源码：https://github.com/mybatis/mybatis-3
- 中文文档：https://mybatis.org/mybatis-3/zh/getting-started.html

## 1.2什么是持久层

数据持久化

- 持久化就是将程序的数据在持久状态和顺势状态转化的过程

数据持久化的方式：

- 数据库（JDBC)
- IO文件持久化

**为什么要持久化？**

因为内存数据断电即失。但是有一些对象不能让他丢掉。

## 1.3持久层

- 完成持久化操作的代码块
- 层界限十分明显

## 1.4 MyBatis的特点

- 简单易学：本身就很小且简单。没有任何第三方依赖，最简单安装只要两个jar文件+配置几个sql映射文件易于学习，易于使用，通过文档和源代码，可以比较完全的掌握它的设计思路和实现。
- 灵活：**mybatis不会对应用程序或者数据库的现有设计强加任何影响。 sql写在xml里，便于统一管理和优化。通过sql语句可以满足操作数据库的所有需求。**
- **解除sql与程序代码的耦合：通过提供DAO层，将业务逻辑和数据访问逻辑分离，使系统的设计更清晰，更易维护，更易单元测试。sql和代码的分离，提高了可维护性**。
- 提供映射标签，支持对象与数据库的orm字段关系映射
- 提供对象关系映射标签，支持对象关系组建维护
- 提供xml标签，支持编写动态sql

# 2.第一个MyBatis程序

思路：搭建环境-->导入MyBatis-->编写代码-->测试

1. 编写MyBatis的核心xml配置文件（mybatis-config.xml）
2. 根据mybatis-config.xml编写工具类获取SqlSession对象，用于执行sql
3. 编写实体类
4. 编写dao层
	1. Mapper接口
	2. Mapper.xml配置文件（一定要将其注册到MyBatis核心配置文件中，如果不是在resource文件中存放，需要配置pom.xml<build>）
5. 实际调用
	1. 两种方式调用的区别
	2. SqlSession对象一定要关闭

## 2.1 搭建环境

### 2.1.1数据库

```sql
CREATE TABLE `user`
(
    `id`   int(0) NOT NULL,
    `name` varchar(30) DEFAULT NULL,
    `pwd`  varchar(30) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8;
  ------------------------------------
INSERT INTO user
VALUES (1, '法外狂徒张三', 123445),
       (2, '王司徒', 126978),
       (3, '诸葛孔明', 12355),
       (4, '佐仓大法', 12213);
```

### 2.1.2新建项目

- 新建一个普通的maven项目
- 删除src文件夹
- 导入依赖
	- MySQL连接驱动
	- MyBatis
	- junit

### 2.1.3创建一个模块

#### 2.1.3.1编写MyBatis的核心配置文件

XML 配置文件中包含了对 MyBatis 系统的核心设置，包括获取数据库连接实例的数据源（DataSource）以及决定事务作用域和控制方式的事务管理器（TransactionManager）

xml文件模板示例（xml文件名建议叫做mybatis-config.xml）：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="POOLED">
        <property name="driver" value="${driver}"/>
        <property name="url" value="${url}"/>
        <property name="username" value="${username}"/>
        <property name="password" value="${password}"/>
      </dataSource>
    </environment>
  </environments>
  <mappers>
    <mapper resource="org/mybatis/example/BlogMapper.xml"/>
  </mappers>
</configuration>
```

mybatis-config.xml配置文件详解：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--configuration:核心配置文件-->
<configuration>
    <environments default="development">
        <environment id="development">
            <!--transactionManager:事务管理-->
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                 <!--
                加载类“ com.mysql.jdbc.Driver”。 不推荐使用。 
                新的驱动程序类为“ com.mysql.cj.jdbc.Driver”。 
                通过SPI自动注册驱动程序，通常不需要手动加载驱动程序类。
                -->
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis?characterEncoding=utf8&amp;useSSL=false&amp;serverTimezone=UTC&amp;rewriteBatchedStatements=true"/>
                <property name="username" value="root"/>
                <property name="password" value="MySQLadmin"/>
            </dataSource>
        </environment>
    </environments>

</configuration>
```

当然，还有很多可以在 XML 文件中配置的选项，上面的示例仅罗列了最关键的部分。 

注意 XML 头部的声明，它用来验证 XML 文档的正确性。environment 元素体中包含了事务管理和连接池的配置。mappers 元素则包含了一组映射器（mapper），这些映射器的 XML 映射文件包含了 SQL 代码和映射定义信息

#### 2.1.3.2编写MyBatis的核心工具类

**从 XML 中构建 SqlSessionFactory**

- 每个基于 MyBatis 的应用都是以一个 SqlSessionFactory 的实例为核心的。
- SqlSessionFactory 的实例可以通过 SqlSessionFactoryBuilder 获得。而 SqlSessionFactoryBuilder 则可以从 XML 配置文件或一个预先配置的 Configuration 实例来构建出 SqlSessionFactory 实例

官方：

```java
String resource = "org/mybatis/example/mybatis-config.xml";
InputStream inputStream = Resources.getResourceAsStream(resource);
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
```

**从 SqlSessionFactory 中获取 SqlSession**

既然有了 SqlSessionFactory，顾名思义，我们可以从中获得 SqlSession 的实例。SqlSession 提供了在数据库执行 SQL 命令所需的所有方法。你可以通过 SqlSession 实例来直接执行已映射的 SQL 语句

官方：

```java
try (SqlSession session = sqlSessionFactory.openSession()) {
  Blog blog = (Blog) session.selectOne("org.mybatis.example.BlogMapper.selectBlog", 101);
}
```

==**编写工具类：封装获取SqlSession对象**==

```java
//获取SqlSessionFactory对象-->SqlSession
public class MyBatisUtils {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            //使用MyBatis的第一步：获取SqlSessionFactory对象（这三行代码时官网写死的，必须这么写）
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //既然有了 SqlSessionFactory，顾名思义，我们可以从中获得 SqlSession 的实例
    public static SqlSession getSqlSession(){
        return sqlSessionFactory.openSession();
    }
}
```

## 2.2编写代码

### 2.2.1实体类

- ```java
	public class User {
	    private int id;
	    private String name;
	    private String pwd;
	
	    public User() {
	    }
	
	    public User(int id, String name, String pwd) {
	        this.id = id;
	        this.name = name;
	        this.pwd = pwd;
	    }
	
	    public int getId() {
	        return id;
	    }
	
	    public void setId(int id) {
	        this.id = id;
	    }
	
	    public String getName() {
	        return name;
	    }
	
	    public void setName(String name) {
	        this.name = name;
	    }
	
	    public String getPwd() {
	        return pwd;
	    }
	
	    public void setPwd(String pwd) {
	        this.pwd = pwd;
	    }
	
	    @Override
	    public String toString() {
	        return "User{" +
	                "id=" + id +
	                ", name='" + name + '\'' +
	                ", pwd='" + pwd + '\'' +
	                '}';
	    }
	}
	```

### 2.2.2Mapper接口

- ```java
	public interface UserMapper {
	    List<User> getUserList();
	}
	```

### 2.2.3接口实现类由原来的UserDaoImp类变为了Mapper配置文件：

官方文档：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.mybatis.example.BlogMapper">
  <select id="selectBlog" resultType="Blog">
    select * from Blog where id = #{id}
  </select>
</mapper>
```

对于本次代码的编写：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--namespace=命名空间：绑定一个对应的DAO/Mapper接口-->
<mapper namespace="com.hnl.dao.UserMapper">
    <!--查询语句：id属性：方法名字
    这就相当于：之前的jdbc操作实现Dao接口，在DaoImp类中实现的方法
    resultType；返回值类型
	resultSetType:参数类型
    -->
    <select id="getUserList" resultType="com.hnl.pojo.User">
        select * from mybatis.user;
    </select>
</mapper>
```

**对命名空间的一点补充（官方）**：

- 在之前版本的 MyBatis 中，**命名空间（Namespaces）**的作用并不大，是可选的。 但现在，随着命名空间越发重要，你必须指定命名空间。
- 命名空间的作用有两个：
	- 一个是利用更长的全限定名来将不同的语句隔离开来，同时也实现了接口绑定
	- 长远来看，只要将命名空间置于合适的 Java 包命名空间之中，你的代码会变得更加整洁，也有利于你更方便地使用 MyBatis。

**命名解析：**

- 全限定名（比如 “com.mypackage.MyMapper.selectAllThings）将被直接用于查找及使用。
- 短名称（==尽量不要用==）（比如 “selectAllThings”）如果全局唯一也可以作为一个单独的引用。 如果不唯一，有两个或两个以上的相同名称（比如 “com.foo.selectAllThings” 和 “com.bar.selectAllThings”），那么使用时就会产生“短名称不唯一”的错误，这种情况下就必须使用全限定名。



## 2.3 测试

### 2.3.1 问题一：核心配置文件没有注册mapper的问题

MyBatis核心配置文件中没有注册mapper的问题

**报错信息：**

```tex
org.apache.ibatis.binding.BindingException: Type interface com.hnl.dao.UserMapper is not known to the MapperRegistry.
```

**MapperRegistry**是什么：核心配置文件中注册mappers

**解决：**

将mapper.xml注册到MyBatis的核心配置文件中

```xml
<mappers>
    <mapper resource="org/mybatis/example/BlogMapper.xml"/>
 </mappers>
```

### 2.3.2 问题二：配置文件无法被导出

**描述**

- 在MyBatis中注册mapper.xml无法被导出，导致无法找到只当的mapper.xml文件
- xml或properties等配置文件没有放置在resource文件夹中，导致maven无法导出或识别

**报错信息**

```tex
java.lang.ExceptionInInitializerError
	at com.hnl.dao.UserDaoTest.Test1(UserDaoTest.java:15)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
...
```

**解决**

Maven由于约定大于配置，之后可能会遇到我们写的配置文件无法被导出或则和生效的问题，解决方案：

在主项目中配置，但是可能不会生效，为了保险，在子项目中也要进行配置

```xml
 <!--在build中配置resources,来防止我们资源导出失败的问题-->
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
                <!--如果<filtering>true</filtering>
				中的true出问题,就改成false
			-->
            </resource>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
                 <!--如果<filtering>true</filtering>
				中的true出问题,就改成false
			-->
            </resource>
        </resources>
```

### 2.3.3 测试代码

```java
      @Test
    public void Test1(){

        //获取sqlSession对象，只从sql语句
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        //方式一：执行sql
        /*
        sqlSession对象执行sql语句，sql语句从哪里拿呢？
        从UserMapper.class的Class对象中获取UserMapper.xml文件中配置的信息，<mapper>标签就相当与之前的DaoImp类的功能
        UserMapper mapper就是对应的UserMapper接口对象，直接使用mapper调用对应的方法即可
         */
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = mapper.getUserList();
       
        /*
        两种方式的区别：诚然，方式二能够正常工作，对使用旧版本 MyBatis 的用户来说也比较熟悉。
        但现在有了一种更简洁的方式——使用和指定语句的参数和返回值相匹配的接口（比如 BlogMapper.class），
        现在你的代码不仅更清晰，更加类型安全，还不用担心可能出错的字符串字面值以及强制类型转换。
         */
        //方式二（不推荐）：
       // List<User> userList = sqlSession.selectList("com.hnl.dao.UserMapper.getUserList");
        for (User user : userList) {
            System.out.println(user);
        }
        //关闭sqlSession，一定要关,官方建议是放到finally块中
        sqlSession.close();
    }
/*
结果：
    User{id=1, name='法外狂徒张三', pwd='123445'}
    User{id=2, name='王司徒', pwd='126978'}
    User{id=3, name='诸葛孔明', pwd='12355'}
    User{id=4, name='佐仓大法', pwd='12213'}
*/
```

## 2.4 可能会遇到的问题

- 配置文件没有注册
- 绑定接口错误
- 方法名不对
- 返回类型不对（一定是全类名）
- Maven导出资源问题

# 3.生命周期和作用域



==**作用域和生命周期类别是至关重要的，因为错误的使用会导致非常严重的并发问题。**==

![](.\image\MyBatis执行过程.png)

## 3.1SqlSessionFactoryBuilder

- 这个类可以**被实例化**、**使用**和**丢弃**，**一旦创建了 SqlSessionFactory，就不再需要它了**。 
- 因此 SqlSessionFactoryBuilder 实例的**最佳作用域是方法作用域（也就是局部方法变量**）。 

## 3.2SqlSessionFactory

- SqlSessionFactory **一旦被创建就应该在应用的运行期间一直存在**，没有任何理由丢弃它或重新创建另一个实例。 
- 使用 SqlSessionFactory 的最佳实践是在应用运行期间不要重复创建多次，**多次重建 SqlSessionFactory 被视为一种代码“坏习惯”**。
- 因此 SqlSessionFactory 的**最佳作用域是应用作用域**。 有很多方法可以做到，最简单的就是使用单例模式或者静态单例模式

## 3.3SqlSession

- **每个线程都应该有它自己的 SqlSession 实例。**
- SqlSession 的实例**不是线程安全的**，因此是不能被共享的，所以它的最佳的**作用域是请求或方法作用域**。 
- **绝对不能将 SqlSession 实例的引用放在一个类的静态域**，甚至一个类的实例变量也不行。 也绝不能将 SqlSession 实例的引用放在任何类型的托管作用域中，比如 Servlet 框架中的 HttpSession。 
- 如果你现在正在使用一种 Web 框架，**考虑将 SqlSession 放在一个和 HTTP 请求相似的作用域中。 换句话说，每次收到 HTTP 请求，就可以打开一个 SqlSession，返回一个响应后，就关闭它**。 
- 这个关闭操作很重要，为了确保每次都能执行关闭操作，你应该把这个关闭操作放到 **finally** 块中

下面的示例就是一个确保 SqlSession 关闭的标准模式：

```java
try (SqlSession session = sqlSessionFactory.openSession()) {
  // 你的应用逻辑代码
}
```



![](.\image\生命周期和作用域.png)

# 4.CRUD

## 4.1 步骤

所有的CRUD操作都有以下操作：

1. 在Mapper接口写对应的CRUD方法
2. 在Mapper.xml中配置对应的CRUD标签
3. 在调用中：
	1. 首先获取SqlSession对象，然后通过SqlSession对象获取Mapper接口的对象
	2. 使用Mapper接口执行对应的CRUD方法（增上改操作一定要提交事务，否则不会执行成功）
	3. 关闭SqlSession对象



##  4.2 SELECT

Mapper接口方法：

```java
//根据id查询用户
    User getUserById(int id);
```

Mapper.xm配置

```xml
  <!--#{id} 接收一个变量-->
    <select id="getUserById" resultType="com.hnl.pojo.User" parameterType="int">
        select * from mybatis.user where id =#{id}
    </select>
```

测试：

```java
 @Test
    public void getUserById() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = mapper.getUserById(1);
        System.out.println(user.toString());
        sqlSession.close();
    }
```

## 4.3 INSERT

Mapper接口方法：

```JAVA
//插入一个用户
    int addUser(User user);
```

Mapper.xm配置:

```XML
   <insert id="addUser" parameterType="com.hnl.pojo.User">
        insert into mybatis.user (id,name,pwd) value (#{id},#{name},#{pwd});
    </insert>
```

测试：

```java
  //增删改需要提交事务
    @Test
    public void addUserTest() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        int res = mapper.addUser(new User(5, "新垣结衣", "234"));
        if (res > 0) {
            System.out.println("数据插入成功"); //提交事务,增删改一定要提交事务
            sqlSession.commit();
        }
        sqlSession.close();
    }

```

## 4.4UPDATE

Mapper接口方法：

```JAVA
  //修改用户
    int updateUser(User user);
```

Mapper.xm配置:

```XML
 <update id="updateUser" parameterType="com.hnl.pojo.User">
       update mybatis.user set name=#{name} ,pwd=#{pwd}  where id=#{id};
    </update>
```

测试：

```java

    @Test
    public void updateUser() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int res = mapper.updateUser(new User(4, "佐仓绫音", "12213"));
        if (res > 0) {
            System.out.println("更新成功");
            //提交事务
            sqlSession.commit();
        }
        sqlSession.close();
    }
```

## 4.5 DELETE

Mapper接口方法：

```JAVA
  //删除一个用户
    int deleteUser(int id);
```

Mapper.xm配置:

```xml
 <delete id="deleteUser" parameterType="int">
        delete from mybatis.user where id=#{id};
    </delete>
```

测试：

```java
  @Test
    public void deleteUser() {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int res = mapper.deleteUser(5);
        if (res > 0) {
            System.out.println("删除成功");
            sqlSession.commit();
        }
        sqlSession.close();
    }
```

## 4.7 错误排查

- 标签不要匹配错

- SQL语句要写对

- mybatis-config.xml中注册Mapper.xml的时候路径问题：

	- ```xml
		 <mappers>
		        <mapper resource="com/hnl/dao/UserMapper.xml"/>
		    </mappers>
		```

- 程序配置文件必须符合规范

- 输出xml文件中存在乱码问题

- mavern资源没有导出问题

# 5.Map和模糊查询扩展

## 5.1 使用Map作为参数

**应用常见：**

​	假设我们的实体类或者数据表中，属性或字段过多，如果使用实体类对象作为参数进行修改或插入的话，那么会很麻烦，这时候就可以使用Map作为参数对部分字段进行修改或插入（部分插入的前提是数据库中字段允许为NULL）。

**使用：**

Mapper接口：

```java
 //万能的Map
    int addUser2(Map<String,Object> map);
```

Mapper.xml配置：

```xml
 <!--使用Map:这样的话 value中的数据就不需要与数据库中的字段一一对应 但是map中的键要与value中的字段对应
    value中传递的map中的key，而这里的key又与sql语句前面的 mybatis.user (id,name,pwd) 一一对应
    -->
    <insert id="addUser2" parameterType="map">
        insert into mybatis.user (id,name,pwd) value (#{userId},#{userName},#{password});
    </insert>
```

使用：

```java
 @Test
    public void addUser2(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        Map<String, Object> map = new HashMap<>();
        /*
        map中的键与Mapper.xml中配置的sql语句的value要一一对应：
        <insert id="addUser2" parameterType="map">
        	insert into mybatis.user (id,name,pwd) value (#{userId},#{userName},#{password});
    	</insert>
        */
        map.put("userId",5);
        map.put("userName","水濑祈");
        map.put("password","123123");
        int res = mapper.addUser2(map);

        if (res >0){
            System.out.println("插入成功");
            sqlSession.commit();
        }
        sqlSession.close();
    }
```

**小结：**

Map传递参数：直接在sql中取出key即可！【parameterType="map"】

对象传递参数，直接sql中取出对象的属性即可！【 parameterType="com.hnl.pojo.User"】

只有一个基本类型的参数情况下，可以直接在sql取到

**多个参数用Map，或者注解**

## 5.2模糊查询

模糊查询：

Mapper:

```java
//执行模糊查询
    List<User> getUserLike(String value);
```

Mapper.xml配置

```xml
 <!--在这里拼接通配符，防止SQL注入-->
    <select id="getUserLike"  resultType="com.hnl.pojo.User">
        select * from mybatis.user where name like "%"#{value}"%";
    </select>
   <!-- <select id="getUserLike"  resultType="com.hnl.pojo.User">
        select * from mybatis.user where name like #{value};
    </select>-->
```

调用：

```java
@Test
    public void getUserLike(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        //Java代码执行的时候，传递通配符%%，不传就查不出来，但是尽量不要在这里传，要在Mapper.xml文件中进行拼接，防止SQL注入
        //List<User> userLike = mapper.getUserLike("%诸葛%");
        List<User> userLike = mapper.getUserLike("诸葛");
        for (User user : userLike) {
            System.out.println(user);
        }
    }
/*
结果：
User{id=3, name='诸葛孔明', pwd='12355'}
User{id=6, name='诸葛亮', pwd='23456'}
*/
```

**小结：**

- Java代码执行的时候需要传递%%通配符
- 在sql拼接时使用通配符：``select * from mybatis.user where name like "%"#{value}"%";``