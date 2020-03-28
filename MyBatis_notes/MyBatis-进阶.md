# 1.配置解析

## 1.1核心配置文件结构

MyBatis 的配置文件包含了会深深影响 MyBatis 行为的设置和属性信息。

配置文档的顶层结构如下：

- configuration（配置）
	- [properties（属性）](https://mybatis.org/mybatis-3/zh/configuration.html#properties)
	- [settings（设置）](https://mybatis.org/mybatis-3/zh/configuration.html#settings)
	- [typeAliases（类型别名）](https://mybatis.org/mybatis-3/zh/configuration.html#typeAliases)
	- [typeHandlers（类型处理器）](https://mybatis.org/mybatis-3/zh/configuration.html#typeHandlers)
	- [objectFactory（对象工厂）](https://mybatis.org/mybatis-3/zh/configuration.html#objectFactory)
	- [plugins（插件）](https://mybatis.org/mybatis-3/zh/configuration.html#plugins)
	- environments（环境配置）
		- environment（环境变量）
			- transactionManager（事务管理器）
			- dataSource（数据源）
	- [databaseIdProvider（数据库厂商标识）](https://mybatis.org/mybatis-3/zh/configuration.html#databaseIdProvider)
	- [mappers（映射器）](https://mybatis.org/mybatis-3/zh/configuration.html#mappers)

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <environments default="development">
    <environment id="development">
       <!---事务管理器-->
      <transactionManager type="JDBC"/>
         <!--数据源-->
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

## 1.2环境配置（environments）



### 1.2.1.配置多个环境

MyBatis 可以配置成适应多种环境，这种机制有助于将 SQL 映射应用于多种数据库之中， 现实情况下有多种理由需要这么做。例如，开发、测试和生产环境需要有不同的配置；或者想在具有相同 Schema 的多个生产数据库中使用相同的 SQL 映射。还有许多类似的使用场景。

**不过要记住：尽管可以配置多个环境，但每个 SqlSessionFactory 实例只能选择一种环境。**

所以，如果你想连接两个数据库，就需要创建两个 SqlSessionFactory 实例，每个数据库对应一个

- **每个数据库对应一个 SqlSessionFactory 实例**

为了指定创建哪种环境，只要将它作为可选的参数传递给 SqlSessionFactoryBuilder 即可。可以接受环境配置的两个方法签名是：

```java
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment);
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment, properties);
```

如果忽略了环境参数，那么将会加载默认环境，如下所示：

```java
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, properties);
```



````xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!--这里将默认的环境改为了test-->
    <environments default="test">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis?characterEncoding=utf8&amp;useSSL=false&amp;serverTimezone=UTC&amp;rewriteBatchedStatements=true"/>
                <property name="username" value="root"/>
                <property name="password" value="MySQLadmin"/>
            </dataSource>
        </environment>
        <!--第二套环境-->
        <environment id="test">
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
            <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
            <property name="url" value="jdbc:mysql://localhost:3306/mybatis?characterEncoding=utf8&amp;useSSL=false&amp;serverTimezone=UTC&amp;rewriteBatchedStatements=true"/>
            <property name="username" value="root"/>
            <property name="password" value="MySQLadmin"/>
        </dataSource>
        </environment>
    </environments>
<mappers>
    <mapper resource="com\hnl\dao\UserMapper.xml"/>
</mappers>

````

### 1.2.2 事务管理器**（transactionManager）**了解即可

在 MyBatis 中有两种类型的事务管理器（也就是 type="[JDBC|MANAGED]"）：

- JDBC – 这个配置直接使用了 JDBC 的提交和回滚设施，它依赖从数据源获得的连接来管理事务作用域。
- MANAGED – （不重要，仅仅直到MyBatis还有MANGED就可以了）

**注意：** ==如果正在使用 Spring + MyBatis，则没有必要配置事务管理器，因为 Spring 模块会使用自带的管理器来覆盖前面的配置。==

### 1.2.3数据源**（dataSource）**了解即可

作用：连接数据库

dataSource 元素使用标准的 JDBC 数据源接口来配置 JDBC 连接对象的资源。

**有三种内建的数据源类型（也就是 type="[UNPOOLED|POOLED|JNDI]"）**

- **UNPOOLED：**这个数据源的实现会每次请求时打开和关闭连接，慢
	- 没有连接池
- **POOLED（默认）：**实现利用“池”的概念将 JDBC 连接对象组织起来，避免了创建新的连接实例时所必需的初始化和认证时间。
- **JNDI：**这个数据源实现是为了能在如 EJB 或应用服务器这类容器中使用，容器可以集中或在外部配置数据源，然后放置一个 JNDI 上下文的数据源引用

## 1.2 属性（properties）

**作用：**我们可以通过properties属性来实现引用配置文件

这些属性可以在外部进行配置，并可以进行动态替换。你既可以在典型的 Java 属性文件中配置这些属性，也可以在 properties 元素的子元素中设置。【db.peoperties】

**使用：**

编写配置文件：

```properties
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/mybatis?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true
username=root
password=MySQLadmin
```

在核心配置文件中引入：

==注意：在XML中标签的顺序是严格约束的==<properties>标签要在<configuration>标签中第一个位置

```xml
<!--官方的模板-->
<properties resource="org/mybatis/example/config.properties">
    <!--<property>标签的内容可以补充配置文件中没有配置的信息
	其中配置的数据，外部文件的优先级要高于此处的优先级
-->
  <property name="username" value="dev_user"/>
  <property name="password" value="F2Fa3!33TYyg"/>
</properties>
```

引入：

```xml
<configuration>
    <!--引入外部配置文件-->
    <properties resource="db.properties"/>

    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <!--在<property>标签中的value就不用写死了，直接引入的是db.properties配日志文件中的信息
				-->
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>
<mappers>
    <mapper resource="com\hnl\dao\UserMapper.xml"/>
</mappers>

</configuration>
```

## 1.3类型别名（typeAliases）

**作用：**

​	类型别名可为 Java 类型设置一个缩写名字。 它仅用于 XML 配置，意在降低冗余的全限定类名书写。

**配置方式：**

- 直接使用在<typeAliases>中使用 <typeAlias>为每一个类取别名：

	- ```xml
		<typeAliases>
		  <typeAlias alias="Author" type="domain.blog.Author"/>
		  <typeAlias alias="Blog" type="domain.blog.Blog"/>
		</typeAliases>
		```

- 指定一个包名，MyBatis 会在包名下面搜索需要的 Java Bean：

	- ：扫描实体类的包，塔地默认别名就为这个类的类名，首字母小写（大小写都行，官方建议是小写，但是我不接受他的建议）

	- 这种方式可以配合@Alias注解给实体类diy别名

	- ```xml
		<typeAliases>
		  <package name="domain.blog"/>
		</typeAliases>
		```

在实体类较少的情况下使用第一种，实体类较多的情况下使用第二种。

**完成<typeAliases>配置之后，就可以在Mapper.xml中直接使用别名进行配置了**：

**<typeAlias>配置**

```xml
<!--别名配置-->
<typeAliases>
    <typeAlias type="com.hnl.pojo.User" alias="User"/>
</typeAliases>
<!--Mapper.xml配置简化-->
<mapper namespace="com.hnl.dao.UserMapper">
    <select id="getUserList" resultType="User">
        select * from mybatis.user;
    </select>
    <!--直接使用别名User,就不再使用全类名-->
    <select id="getUserById" resultType="User" parameterType="int">
        select * from mybatis.user where id =#{id};
</mapper>
```

### 注意

在官网查看常见的Java类型别名，比如： Integer的别名为int，int的别名为_int

基本类型的别名在前面加一个下划线，常见的引用类型是用小写



## 1.4设置（settings）

这是 MyBatis 中极为重要的调整设置，它们会改变 MyBatis 的运行时行为。

几个需要注意的：

| 设置名                       | 描述                                                         | 有效值                                                       | 默认值 |
| ---------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------ |
| mapUnderscoreToCamelCase     | 是否开启驼峰命名自动映射，即从经典数据库列名 A_COLUMN 映射到经典 Java 属性名 aColumn。 | true\|false                                                  | false  |
| logImpl                      | 指定 MyBatis 所用日志的具体实现，未指定时将自动查找。        | SLF4J \ LOG4J \ LOG4J2 \ JDK_LOGGING \ COMMONS_LOGGING \ STDOUT_LOGGING \ NO_LOGGING | 未设置 |
| cacheEnabled                 | 全局性地开启或关闭所有映射器配置文件中已配置的任何缓存。     | true\false                                                   | true   |
| lazyLoadingEnabled（懒加载） | 延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。 特定关联关系中可通过设置 `fetchType` 属性来覆盖该项的开关状态。 | true\false                                                   | false  |

## 1.5插件（plugins）

- MyBatis Generator Core：自动生成增删改查代码（不一定准确）
- MyBatis Plus：简化MyBatis
- 通用mapper

## 1.6映射器（mappers）

在核心配置文件中注册Mapper.xml

- 方式一【推荐使用】：

	- ```xml
		<!-- 使用相对于类路径的资源引用 -->
		<mappers>
		  <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
		  <mapper resource="org/mybatis/builder/BlogMapper.xml"/>
		  <mapper resource="org/mybatis/builder/PostMapper.xml"/>
		</mappers>
		```

- 方式二：

	- 接口和它的Mapper.xml配置文件必须同名

	- ```xml
		<!-- 将包内的映射器接口实现全部注册为映射器 -->
		<mappers>
		  <package name="org.mybatis.builder"/>
		</mappers>
		```

- 方式三：

	- 此方式注意点：

		- 接口和它的Mapper.xml配置文件必须同名
		- 接口和它的Mapper.xml配置文件必须在同一个包下

	- ```xml
		<!-- 使用映射器接口实现类的完全限定类名 -->
		<mappers>
		  <mapper class="org.mybatis.builder.AuthorMapper"/>
		  <mapper class="org.mybatis.builder.BlogMapper"/>
		  <mapper class="org.mybatis.builder.PostMapper"/>
		</mappers>
		```

# 2.resultMap结果集映射



**解决属性名和字段不一致的问题。**

## 2.1问题描述

**环境（修改前面项目中实体类的属性pwd->password）：**

```java
public class User {

    private int id;
    private String name;
    private String password;
}
```

**执行查询操作：**

```java
   @Test
     public void Test1(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User userById = mapper.getUserById(1);

        System.out.println(userById);
        sqlSession.close();
    }
/*
结果：
User{id=1, name='法外狂徒张三', password='null'}
*/
```

**由于属性名与数据库中的字段名不一致，导致password属性为空**

## 2.2问题分析

```xml
<select id="getUserById" resultType="User" parameterType="int">
        select * from mybatis. user where id = #{id};
    </select>
<!--
Mapper.xml中配置的查询语句完整的应该是：select id ,name, pwd from mybatis. user where id = #{id};
由于类型处理器找不到与pwd对应的属性名，所以password为空
-->
```

## 2.3 解决方案

- **起别名：修改Mapper.xml中的sql**

	- ```xml
		 <select id="getUserById" resultType="User" parameterType="int">
		        select id,name,pwd as password from mybatis. user where id = #{id};
		    </select>
		```

- **resultMap**

## 2.4 resultMap结果集映射

在Mapper.xml中配置返回值类型，不配置具体的返回值类型，而是配置一个resultMap结果集映射

```xml
<mapper namespace="com.hnl.dao.UserMapper">
    <!--结果映射，将结果集映射为Map-->
    <resultMap id="UserMap" type="User">
         <!--column：对应数据库中的字段 property：对应实体类中的属性
        也可以只映射属性与字段不匹配的地方，属性字段匹配的完全不需要进行配置
        -->
       <!-- <result column="id" property="id"/>
        <result column="name" property="name"/>-->
        <result column="pwd" property="password"/>
    </resultMap>
    <select id="getUserById" resultMap="UserMap" parameterType="int">
        select * from mybatis.user where id = #{id};
    </select>
</mapper>
```

Test:

```java
 @Test
     public void Test1(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User userById = mapper.getUserById(1);

        System.out.println(userById);
        sqlSession.close();
    }
/*
结果：User{id=1, name='法外狂徒张三', password='123445'}
*/
```

**resultMap介绍**

- `resultMap` 元素是 MyBatis 中最重要最强大的元素
- ResultMap 的设计思想是，对简单的语句做到零配置，对于复杂一点的语句，只需要描述语句之间的关系就行了
-  `ResultMap` 的优秀之处在于完全可以不用显式地配置它们
- 对应现在简单的情况这些已经够用了，但是对于一对多、多对一的情况要进行更加复杂的配置



# 3.日志

## 3.1日志工厂

**为什么要日志？**

如果一个数据库操作出现了异常，我们就需要进行排错，日志就是最好的助手。

MyBatis日志实现

![](.\image\mybatis日志实现.png)

- SLF4J 
- LOG4J【掌握】
-  LOG4J2
- JDK_LOGGING
- COMMONS_LOGGING
- STDOUT_LOGGING【掌握】
- NO_LOGGING

在MyBaits中具体使用哪个实现，在设置中决定

## 3.2STDOUT_LOGGING标准日志输出

配置（核心配置文件）：

```xml
<!--注意：配置中的：logImpl 和STDOUT_LOGGING 一点都不能错，必须仔细检查空格和大小写的问题-->
<settings>
    <!--标准的日志工厂实现-->
        <setting name="logImpl" value="STDOUT_LOGGING"/>
    </settings>
```

日志信息：

```tex
Logging initialized using 'class org.apache.ibatis.logging.stdout.StdOutImpl' adapter.
Class not found: org.jboss.vfs.VFS
JBoss 6 VFS API is not available in this environment.
Class not found: org.jboss.vfs.VirtualFile
VFS implementation org.apache.ibatis.io.JBoss6VFS is not valid in this environment.
Using VFS adapter org.apache.ibatis.io.DefaultVFS
Find JAR URL: file:/F:/Project/IDEAProject/MyBatis_Learn/MyBatis-log/target/classes/com/hnl/pojo
Not a JAR: file:/F:/Project/IDEAProject/MyBatis_Learn/MyBatis-log/target/classes/com/hnl/pojo
Reader entry: User.class
Listing file:/F:/Project/IDEAProject/MyBatis_Learn/MyBatis-log/target/classes/com/hnl/pojo
Find JAR URL: file:/F:/Project/IDEAProject/MyBatis_Learn/MyBatis-log/target/classes/com/hnl/pojo/User.class
Not a JAR: file:/F:/Project/IDEAProject/MyBatis_Learn/MyBatis-log/target/classes/com/hnl/pojo/User.class
Reader entry: ����   7 :
Checking to see if class com.hnl.pojo.User matches criteria [is assignable to Object]
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
##################上面的都是初始化以及执行信息，下面的是具体的MyBatis操作信息#######################
Opening JDBC Connection
Created connection 511832416.
Setting autocommit to false on JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@1e81f160]
==>  Preparing: select * from mybatis.user where id = ?; 
==> Parameters: 1(Integer)
<==    Columns: id, name, pwd
<==        Row: 1, 法外狂徒张三, 123445
<==      Total: 1
User{id=1, name='法外狂徒张三', password='123445'}
Resetting autocommit to true on JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@1e81f160]
Closing JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@1e81f160]
Returned connection 511832416 to pool.

Process finished with exit code 0

```

## 3.3LOG4J

### 3.3.1LOG4J基本介绍

- Log4j是[Apache](https://baike.baidu.com/item/Apache/8512995)的一个开源项目，通过使用Log4j，我们可以控制日志信息输送的目的地是[控制台](https://baike.baidu.com/item/控制台/2438626)、文件、[GUI](https://baike.baidu.com/item/GUI)组件，甚至是套接口服务器、[NT](https://baike.baidu.com/item/NT/3443842)的事件记录器、[UNIX](https://baike.baidu.com/item/UNIX) [Syslog](https://baike.baidu.com/item/Syslog)[守护进程](https://baike.baidu.com/item/守护进程/966835)等
- 我们也可以控制每一条日志的输出格式
- 通过定义每一条日志信息的级别，我们能够更加细致地控制日志的生成过程
- 这些可以通过一个[配置文件](https://baike.baidu.com/item/配置文件/286550)来灵活地进行配置，而不需要修改应用的代码。

### 3.3.2使用

- **导入maven依赖：**

	- ```xml
		<!-- https://mvnrepository.com/artifact/log4j/log4j -->
		<dependency>
		    <groupId>log4j</groupId>
		    <artifactId>log4j</artifactId>
		    <version>1.2.17</version>
		</dependency>
		
		```

- **建立log4j.properties**

	- ```properties
		#将等级为DEBUG的日志信息输出到console和file这两个目的地，console和file的定义在下面的代码
		log4j.rootLogger=DEBUG,console,file
		
		#控制台输出的相关设置
		log4j.appender.console = org.apache.log4j.ConsoleAppender
		log4j.appender.console.Target = System.out
		log4j.appender.console.Threshold=DEBUG
		log4j.appender.console.layout = org.apache.log4j.PatternLayout
		log4j.appender.console.layout.ConversionPattern=【%c】-%m%n
		
		#文件输出的相关设置
		log4j.appender.file = org.apache.log4j.RollingFileAppender
		log4j.appender.file.File=./log/kuang.log
		log4j.appender.file.MaxFileSize=10mb
		log4j.appender.file.Threshold=DEBUG
		log4j.appender.file.layout=org.apache.log4j.PatternLayout
		log4j.appender.file.layout.ConversionPattern=【%p】【%d{yy-MM-dd}】【%c】%m%n
		
		#日志输出级别
		log4j.logger.org.mybatis=DEBUG
		log4j.logger.java.sql=DEBUG
		log4j.logger.java.sql.Statement=DEBUG
		log4j.logger.java.sql.ResultSet=DEBUG
		log4j.logger.java.sql.PreparedStatement=DEBUG
		```

- **配置LOG4J为日志的实现**

	- ```xml
		<settings>
			<setting name="logImpl" value="LOG4J"/>
		</settings>
		```

- **直接测试刚才的代码**

	- ![](.\image\LOG4J日志信息.png)

### 3.3.3LOG4J日志的应用

- 在要使用LOG4J的类中，导入包``import org.apache.log4j.Logger;``

- 获取日志对象（参数为当前类的class对象）：``static Logger logger = Logger.getLogger(LogDemo.class); //LogDemo为相关的类``

- 使用logger对象：

	- ```java
		  @Test
		    public void testLog4J() {
		
		        logger.info("info: 进入了testLog4J");
		        logger.debug("debug:进入了testLog4J");
		        logger.error("error: 进入了testLog4J");
		    }
		/*
		结果：
		¡¾MyTest¡¿-info: 进入了testLog4J
		¡¾MyTest¡¿-debug:进入了testLog4J
		¡¾MyTest¡¿-error: 进入了testLog4J
		
		*/
		```

	- 生成log文件![](.\image\log文件.png)

# 4.分页

**为什么要使用分页？**

- 减少数据的处理量

## 4.1Limit实现分页

**基本语法：**

```SQL
SELECT * FROM user LIMIT starIndex,pageSize;
SELECT * FROM user LIMIT index;  #[0,index]
```

使用MyBatis实现分页，核心是SQL。

```java
 //Limit分页查询
    List<User> getUserByLimit(Map<String,Integer> map);
```

Mapper.xml配置：

```xml
<select id="getUserByLimit" resultMap="UserMap" parameterType="map">
        select * from mybatis.user limit #{startIndex},#{pageSize};
    </select>
```

Test:

```java
 @Test
    public void getUserByLimit(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        Map<String, Integer> map = new HashMap<>();
        map.put("startIndex",0);
        map.put("pageSize",2);
        List<User> userByLimit = mapper.getUserByLimit(map);
        for (User user : userByLimit) {
            System.out.println(user);
        }
        sqlSession.close();
    }
/*
结果：
User{id=1, name='法外狂徒张三', password='123445'}
User{id=2, name='王司徒', password='126978'}
*/
```

## 4.2RowBounds分页查询（了解）

limit查询没有体现出Java的面向对象，RowBound体现了Java的面向对象，但是速度要慢于Limit查询

使用RwoBounds实现分页查询，不再SQL层面实现分页，而是在Java代码使用RowBounds实现分页

UserMapper接口：

```java
  //RowBounds分页查询
    List<User> getUserByRowBounds();
```

Mapper.xml配置

```xml
<!--不在SQL层面实现分页，配置中的SQL语句查询所有-->
<select id="getUserByRowBounds" resultMap="UserMap" >
    select * from mybatis.user ;
</select>
```

Test:

```java
  @Test
    public void getUserByRowBounds(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        //RowBounds实现
        RowBounds rowBounds = new RowBounds(0, 2);

        //通过Java代码层面实现分页
        List<User> userList = sqlSession.selectList("com.hnl.dao.UserMapper.getUserByRowBounds",null,rowBounds);
        for (User user : userList) {
            System.out.println(user);
        }
        sqlSession.close();
    }
/*
结果：
User{id=1, name='法外狂徒张三', password='123445'}
User{id=2, name='王司徒', password='126978'}
*/
```

## 4.3分页插件

![](.\image\MyBatis分页插件.png)

了解即可