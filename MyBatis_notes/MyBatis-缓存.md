# 1.缓存简介

**什么是缓存？**：

- 存在内存中的临时数据
- 将用户经常查询的数据存放在缓存（内存）中，用户去查询数据就不用从磁盘上（关系型数据库数据文件）查询，从缓存中查询，从而提高查询效率，解决了高并发系统的性能问题。

**为什么使用缓存？**

- 减少和数据库的交互次数，减少系统开销，提高系统效率

**什么样的数据能使用缓存？**

- 经常查询并且不经常改变的数据

# 2.MyBatis缓存

MyBatis包含一个非常强大的查询缓存特性，它可以非常方便的定制和配置缓存。缓存可以极大的提升查询效率。

MyBatis系统中默认定义了两级缓存：__一级缓存__和__二级缓存__

- 默认情况下只有一级缓存开启。（SqlSession级别的缓存，也称为本地缓存）
- 二级缓存需要手动开启和配置，它是基于namespace级别的缓存
- 为了提高扩展性，MyBatis定义了缓存接口Cache。我们可以通过实现Cache接口来自定义二级缓存

# 3.一级缓存

一级缓存也叫本地缓存：SqlSession

- 与数据库同一次会话查询到的数据放再本地缓存中
- 以后如果有需要获取相同的数据，直接从缓存里面拿，没必要再去查询数据库
- 一级缓存只存在于获取到SqlSession对象到SqlSession.close之间

测试一级缓存，首先开启日志

```java
 @Test
    public void getUserList(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user1 = mapper.getUserById(1);
        System.out.println(user1);
        System.out.println("===============================");
        User user2 = mapper.getUserById(1);
        System.out.println(user2);
        System.out.println(user1==user2);
        sqlSession.close();
    }
```

![](.\image\一级缓存.png)

**缓存失效的情况**

- 查询不同的东西
- 增删改操作，可能会改变原来的数据，所以必定会刷新数据
- 通过不同的Mapper.xml
- 手动清理缓存：sqlSession.clearCache()

**小结：**

- 一级缓存是默认开启的，只在一次SqlSession中有效，也就是拿到连接到关闭连接这个区间。



# 4.二级缓存

## 4.1 概述

- 二级缓存也叫全局缓存，一级缓存作用域太低了，所以诞生了二级缓存
- 基于namespace级别的二级缓存，一个命名空间，对应一个二级缓存
- 工作机制：
	- 一个会话查询一条数据，这个数据就会被放在当前会话的一级缓存中
	- 如果当前会话关闭了，这个会话对应的一级缓存就没有了；但是我们想要的是，会话关闭了，一级缓存中的数据被保存到二级缓存中
	- 新的会话查询信息，就可以从二级缓存中获取信息
	- 不同的mapper查出的数据会被放到自己对应的缓存（map）中



默认情况下，只启用了本地的会话缓存，它仅仅对一个会话中的数据进行缓存。 要启用全局的二级缓存，只需要在Mapper.xml 映射文件中添加一行：

```xml
<cache/>
```

这个简单语句的效果如下:

- 映射语句文件中的所有 select 语句的结果将会被缓存。
- 映射语句文件中的所有 insert、update 和 delete 语句会刷新缓存。
- 缓存会使用最近最少使用算法（LRU, Least Recently Used）算法来清除不需要的缓存。
- 缓存不会定时进行刷新（也就是说，没有刷新间隔）。
- 缓存会保存列表或对象（无论查询方法返回哪种）的 1024 个引用。
- 缓存会被视为读/写缓存，这意味着获取到的对象并不是共享的，可以安全地被调用者修改，而不干扰其他调用者或线程所做的潜在修改。

这些属性可以通过 cache 元素的属性来修改：

```xml
<cache
  eviction="FIFO"
  flushInterval="60000"
  size="512"
  readOnly="true"/>
<!--这个更高级的配置创建了一个 FIFO 缓存，每隔 60 秒刷新，最多可以存储结果对象或列表的 512 个引用，而且返回的对象被认为是只读的，因此对它们进行修改可能会在不同线程中的调用者产生冲突。-->
```



## 4.2 使用步骤

1. 开启全局缓存

	1. ```xml
		<!--默认是开启的，显式的开启全局缓存-->
		<setting name="cacheEnabled" value="true"/>
		```

2. 在Mapper.xml中配置<cache/>

缓存只作用于 cache 标签所在的映射文件中的语句。如果你混合使用 Java API 和 XML 映射文件，在共用接口中的语句将不会被默认缓存。你需要使用 @CacheNamespaceRef 注解指定缓存作用域。

```xml
<cache
  eviction="FIFO"
  flushInterval="60000"
  size="512"
  readOnly="true"/>
<!--这个更高级的配置创建了一个 FIFO 缓存，每隔 60 秒刷新，最多可以存储结果对象或列表的 512 个引用，而且返回的对象被认为是只读的，因此对它们进行修改可能会在不同线程中的调用者产生冲突-->
```

可用的清除策略有：

- `LRU` – 最近最少使用：移除最长时间不被使用的对象。
- `FIFO` – 先进先出：按对象进入缓存的顺序来移除它们。
- `SOFT` – 软引用：基于垃圾回收器状态和软引用规则移除对象。
- `WEAK` – 弱引用：更积极地基于垃圾收集器状态和弱引用规则移除对象。

默认的清除策略是 LRU。

flushInterval：缓存刷新是时间间隔

size（引用数目）属性可以被设置为任意正整数，要注意欲缓存对象的大小和运行环境中可用的内存资源。默认值是 1024。

readOnly（只读）属性可以被设置为 true 或 false。只读的缓存会给所有调用者返回缓存对象的相同实例。 因此这些对象不能被修改。这就提供了可观的性能提升。而可读写的缓存会（通过序列化）返回缓存对象的拷贝。 速度上会慢一些，但是更安全，因此默认值是 false。

**select的usecahce属性**：

- 决定是否使用缓存

- ```xml
	<select id="getUserList" resultType="User" useCache="true">
	        select * from user;
	</select>
	<!--当某个查询的结果修改频繁的时候，可以将usecache设置为false-->
	```

- 



**测试**： 

- 问题1 ：我们需要将实体类序列化，否则会报错

	- ```java
		Caused by : java.io.NoSerializableException : com.hnl.pojo.User
		```



## 4.3 小结

- 只要开启了二级缓存，只要是在同一个mapper下就有效
- 所有的数据都会先放在一级缓存中
- 只有当会话提交，或者会话关闭的时候，才会提交到二级缓存中



# 5.MyBatis缓存原理



![](.\image\MyBatis缓存原理.png)



# 6.自动缓存 - ehcache

## 6.1EhCache 介绍

EhCache 是一个`纯Java`的进程内`缓存框架`，具有快速、精干等特点，是Hibernate中默认CacheProvider。Ehcache是一种广泛使用的开源Java分布式缓存。主要面向通用缓存,Java EE和轻量级容器。它具有`内存`和`磁盘`存储，缓存加载器,缓存扩展,缓存异常处理程序,一个gzip缓存servlet过滤器,支持REST和SOAP api等特点。

EhCache 是一种广泛使用的开源Java分布式缓存，主要面向通用缓存。

## 6.2 使用

要在程序中使用，要先在项目中导入jar包

```xml
<!-- https://mvnrepository.com/artifact/org.mybatis.caches/mybatis-ehcache -->
<dependency>
    <groupId>org.mybatis.caches</groupId>
    <artifactId>mybatis-ehcache</artifactId>
    <version>1.2.0</version>
</dependency>
```

在Mapper.xml中使用cache标签导入ehcacha的实现

```xml
   <cache type="org.mybatis.caches.ehcache.EhcacheCache"/>
```

ehcache.xml配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
         updateCheck="false">
    <!--
       diskStore：为缓存路径，ehcache分为内存和磁盘两级，此属性定义磁盘的缓存位置。参数解释如下：
       user.home – 用户主目录
       user.dir  – 用户当前工作目录
       java.io.tmpdir – 默认临时文件路径
     -->
    <diskStore path="java.io.tmpdir/Tmp_EhCache"/>
    <!--
       defaultCache：默认缓存策略，当ehcache找不到定义的缓存时，则使用这个缓存策略。只能定义一个。
     -->
    <!--
      name:缓存名称。
      maxElementsInMemory:缓存最大数目
      maxElementsOnDisk：硬盘最大缓存个数。
      eternal:对象是否永久有效，一但设置了，timeout将不起作用。
      overflowToDisk:是否保存到磁盘，当系统当机时
      timeToIdleSeconds:设置对象在失效前的允许闲置时间（单位：秒）。仅当eternal=false对象不是永久有效时使用，可选属性，默认值是0，也就是可闲置时间无穷大。
      timeToLiveSeconds:设置对象在失效前允许存活时间（单位：秒）。最大时间介于创建时间和失效时间之间。仅当eternal=false对象不是永久有效时使用，默认是0.，也就是对象存活时间无穷大。
      diskPersistent：是否缓存虚拟机重启期数据 Whether the disk store persists between restarts of the Virtual Machine. The default value is false.
      diskSpoolBufferSizeMB：这个参数设置DiskStore（磁盘缓存）的缓存区大小。默认是30MB。每个Cache都应该有自己的一个缓冲区。
      diskExpiryThreadIntervalSeconds：磁盘失效线程运行时间间隔，默认是120秒。
      memoryStoreEvictionPolicy：当达到maxElementsInMemory限制时，Ehcache将会根据指定的策略去清理内存。默认策略是LRU（最近最少使用）。你可以设置为FIFO（先进先出）或是LFU（较少使用）。
      clearOnFlush：内存数量最大时是否清除。
      memoryStoreEvictionPolicy:可选策略有：LRU（最近最少使用，默认策略）、FIFO（先进先出）、LFU（最少访问次数）。
      FIFO，first in first out，这个是大家最熟的，先进先出。
      LFU， Less Frequently Used，就是上面例子中使用的策略，直白一点就是讲一直以来最少被使用的。如上面所讲，缓存的元素有一个hit属性，hit值最小的将会被清出缓存。
      LRU，Least Recently Used，最近最少使用的，缓存的元素有一个时间戳，当缓存容量满了，而又需要腾出地方来缓存新的元素的时候，那么现有缓存元素中时间戳离当前时间最远的元素将被清出缓存。
   -->
    <defaultCache
            eternal="false"
            maxElementsInMemory="10000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="1800"
            timeToLiveSeconds="259200"
            memoryStoreEvictionPolicy="LRU"/>
 
    <cache
            name="cloud_user"
            eternal="false"
            maxElementsInMemory="5000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="1800"
            timeToLiveSeconds="1800"
            memoryStoreEvictionPolicy="LRU"/>
 
</ehcache>
```

