### jpa-plus

关于jpa功能增强的开源项目，在github上可以找到几个，比如：[spring-data-jpa-extra](https://github.com/slyak/spring-data-jpa-extra)、[jpa-spec](https://github.com/wenhao/jpa-spec)、[spring-data-jpa-expansion](https://github.com/fast-family/spring-data-jpa-expansion)。

我原本拿`spring-data-jpa-extra`魔改了一番，但是后来觉得封装过于复杂，不够简单高效，弃用了。 

然后想按照`myBatis-plus`的`QueryWrapper`和`UpdateWrapper`自己封装出来jpa版本，封装`QueryWrapper`很容易想到可以使用jpa的`Specification`实现。但是，想实现`UpdateWrapper`的效果就有点儿...。封装`QueryWrapper`块的时候遇到点儿问题，在查百度时看到了`jpa-spec`这个项目。此时，我也差不多封装完了、绝不放弃。顺利写完我的版本(更接近`myBatis-plus`的`QueryWrapper`的写法)。后来觉得联查功能不够强大，并且必须在实体类中加响应的注解(orm...我不喜欢)，总之还是不够简单高效，又弃用了。

经过查资料多次尝试，发现NativeSql查询很好很强大，实现ResultTransformer可以定制查询结果转化到任意pojo，应该可以封装出我想要的`简单高效`的效果，于是又一次的努力，有了`jpa-plus`。目前，只封装了查询功能。更新功能还没封装。


### 小知识

**实体类**

实体类 顶层父类是`Model`提供了ActiveRecord的模式,支持 ActiveRecord 形式调用,实体类只需继承 Model 类即可实现基本 CRUD 操作;
实体类的 直接父类 可以是`BaseEntity`或`BaseEntity`的子类`AuditableEntity`;

`BaseEntity`中有一个`Map<String, Object> params`，前端传数据字段多于实体类属性、不想在实体类加相应的属性时(请看下面例子)。

```java
// 实体类属性: createTime 创建时间
// 需要范围查询
// 添加响应属性方式： createTimeBegin、createTimeEnd ==> 调用接口传入 createTimeBegin="2049-01-01",createTimeBegin="2049-12-12"
// 放入Map方式：  调用接口传入 params['createTime']=["2049-01-01","2049-12-12"]
// 使用方式：     实体类对象.getParams("createTime"); //创建起止时间的数组
```


`AuditableEntity` 中定义了后台管理常用到的审计字段 `createTime``createBy` `updateTime` `updateBy`, 需要这些字段的实体类应该继承`AuditableEntity`,对应的表中也要有字段 `create_time``create_by` `update_time` `update_by`;
**写代码**时，**不用关心**这四个字段有没有值,框架会自动赋值;

实体类的 类名称和属性 与 数据库表名称和字段名称 是`驼峰转下划线连接的小写形式`的关系;
如果表名称有实体类不具备的前缀(比如,表名称`t_job_log`,实体类名称`JobLog`),需要在实体类注解@Entity中明确赋值name(`@Entity(name = "t_job_log")`);

实体类上的 表别名注解 `@TableAlias("表别名")`, **每个实体类都应该加上此注解**, 注解的value值应该与NativeSqlQuery中的表别名一致。`数据范围过滤`动态生成的sql片段会插入到sql模板中;


例子: 
```java
@Data @Builder(toBuilder = true)
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity @TableAlias("su")
public class SysUser extends AuditableEntity<SysUser,Long>{
    // 上面几个注解是 lombok 的, 如果不知道 lombok 请上Google百度一下
    
    // 属性 略...
}
```

**Dao**

Dao层需要 继承 BaseRepository

例子: 
```java
@Repository
public interface SysOrgRepository extends BaseRepository<SysOrg,Long> {}
```

**Service**

Service 接口需要 继承 IBaseService, 实现类 需要继承 BaseServiceImpl 并实现 对应的接口

例子:

```java
// 接口
public interface ISysOrgService extends IBaseService<SysOrg,Long> {}

// 实现类
@Slf4j @Service @Transactional(readOnly = true)
public class SysOrgServiceImpl extends BaseServiceImpl<SysOrgRepository, SysOrg,Long> implements ISysOrgService {

    // 在BaseServiceImpl中注入了 EntityManager //Service实现类的方法中可以直接使用;
    
    // 在BaseServiceImpl中注入了 dao //泛型依赖会自动传递下来,Service实现类的方法中可以直接使用 dao.xxx(yyy);
}
```

---

### jpa-plus使用说明

    像原生sql一样简单,像原生sql一样复杂

sevice层的方法中
```java
NativeSqlQuery nativeSql = NativeSqlQuery.builder()
    .select(查询的列) //必有
    .from(表名 AS 表别名 LEFT JOIN 表名 AS 表别名 ON XXX = YYY) //必有
    // where 条件， eq/ne/lt/...可以出现0～n次，生成SQL时 默认用 AND 连接多个where条件
    // 自动判断属性不为null，才会生成对应sql片段
    .where(w -> w
        .eq(表别名.列名, java属性值/常量值)
        .ne(表别名.列名, java属性值/常量值)
        .lt(表别名.列名, java属性值/常量值)
        .lte(表别名.列名, java属性值/常量值)
        .gt(表别名.列名, java属性值/常量值)
        .gte(表别名.列名, java属性值/常量值)
        .isNull(表别名.列名) // 不需要传值
        .isNotNull(表别名.列名) // 不需要传值
        .startsWith(表别名.列名, java属性值/常量值)
        .contains(表别名.列名, java属性值/常量值)
        .endsWith(表别名.列名, java属性值/常量值)
        .notStartsWith(表别名.列名, java属性值/常量值)
        .notContains(表别名.列名, java属性值/常量值)
        .notEndsWith(表别名.列名, java属性值/常量值)
        .in(表别名.列名, java属性值/常量值)         // 传入List
        .notIn(表别名.列名, java属性值/常量值)      // 传入List
        .between(表别名.列名, java属性值/常量值)    // 传入List, 长度应该是2
        .notBetween(表别名.列名, java属性值/常量值) // 传入List, 长度应该是2
        .sqlStrPart(自定义sql字符串片段)) //追加到where条件尾部(数据权限sql片段)
    // 以下这些 也是可有可无，跟原生sql写法别无二致
    .groupBy(表别名.列名)
    .having(写法与where一致)
    .orderBy(表别名.列名 ASC[,表别名.列名 DESC])
    .orderBy( queryRequest.getOrderBy(表别名) ) // 排序参数如果是前端传进来,用QueryRequest接收的
    // build可以不写
    .build();

    // nativeSql
    // pojo.class  普通的java类即可，列名(下划线连接==自动转换==>驼峰命名，去匹配java类的属性)
    // 返回List<pojo>或Page<pojo>
    dao.findAllByNativeSql(nativeSql, JobLog.class, queryRequest.getPageNo(), queryRequest.getPageSize());
```

### jpa-plus用法示例

sevice层的方法中
```java
 
    // 像原生sql一样简单. 也可以原生sql一样写到很复杂
    
    // 模拟接收到的请求参数
    SysUser user = new SysUser();
    user.setUsername("system");
    user.setNickname("eye");
    user.setCreateTime(new DateTime());
    user.putParam("ids", Arrays.asList(1L, 2L, 3L));
    user.putParam("ages",Arrays.asList(20, 40));

    NativeSqlQuery nativeSqlQuery = NativeSqlQuery.builder()
            .select("u.id, u.username")
            .from("sys_user u")
            .where(w -> w.eq("u.username", user.getUsername())
                .contains("u.nickname", user.getNickname())
                .lt("u.create_time", new Date())
                .in("u.id", (List)user.getParams("ids"))
                .between("u.age", (List)user.getParams("ages")))
            .groupBy("u.id")
            .orderBy("u.id");

    List<SysUser> allBySQL = dao.findAllBySql(nativeSqlQuery, SysUser.class);
    //Page<SysUser> allBySQL = dao.findAllBySql(nativeSql, SysUser.class, queryRequest.getPageNo(), queryRequest.getPageSize());
    
```

---
其他简化jpa开发的工具类

**QueryRequest**

查询请求,很多时候需要 分页参数、排序参数, Controller层方法的形参写上`QueryRequest`, 自动接收分页和排序参数;
分页参数有默认备用值`pageNo`=1,`pageSize`=20;

排序字段可以设置默认备用值`QueryRequest#defaultSort("默认排序属性名称", 是否升序)`;

查询需要PageRequest对象,只需要调用一下`getPageRequest()`, 查询需要Sort对象,只需要调用一下`getSort()`;

使用 NativeSqlQuery查询 同样简单, `queryRequest.getPageNo()`、`queryRequest.getPageSize()`、`queryRequest.getOrderBy(表列名)`;


Service代码片段示例:

```java
// 如果没接收到分页参数, 有默认备用值`pageNo`=1,`pageSize`=20;
PageRequest pageRequest = queryRequest.getPageRequest();

//  如果没接收到排序参数, defaultSortField设置的默认值 生效
PageRequest pageRequest = queryRequest.defaultSortField("createTime", false).getPageRequest();

dao.findAll(pageRequest);

// 虽然使用 QueryRequest 接收 参数了, 但是只需要排序 不需要分页
Sort sort = queryRequest.defaultSortField("createTime", false).getSort();
// queryRequest.getSort(); // 当然 也可以 不设置 默认排序字段

dao.findAll(sort);

// NativeSqlQuery
NativeSqlQuery nativeSqlQuery = NativeSqlQuery.builder()
            .select(XXX).from(XXX)
            .where(w -> w.eq(XXX).contains(XXX).lt(XXX).in(XXX).between(XXX))
            .groupBy(XXX)
            .orderBy(queryRequest.getOrderBy(表列名));

Page<SysUser> allBySQL = dao.findAllBySql(nativeSql, SysUser.class, queryRequest.getPageNo(), queryRequest.getPageSize());


```
---

**PageUtil、SortUtil**

查询需要排序或分页, 但是这些参数不是前端传过来的。JPA本来就是这方面的高手, 但是, 还能更优秀。
PageUtil可以更简单构建多字段排序的PageRequest。

代码片段示例:

```java
// 如果需要分页参数, PageUtil有默认备用值`pageNo`=1,`pageSize`=20;
PageRequest pageRequest = PageUtil.builder().pageNo(1).pageSize(50).asc("age").desc("createTime").build();

// 如果只需要分页 不需要排序 // 应该用jpa 自带写法
PageRequest pageRequest = PageRequest.of(1,30);

// 如果只需要排序 不需要分页 // getSort();
Sort sort = PageUtil.builder().asc("age").desc("createTime").build().getSort();

// 如果只需要排序 不需要分页 // 也可以使用工具类 SortUtil
Sort sort = SortUtil.builder().asc("age").desc("createTime").build();
```

----
