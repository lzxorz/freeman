### freeman-jpa-extra

基于优秀的开源项目 `spring-data-jpa-extra` 大刀阔斧修改版。
关于`spring-data-jpa-extra`项目以及作者更多信息 可以查看`pom`文件 或 [点击这里](https://github.com/slyak/spring-data-jpa-extra)。


### freeman-jpa-extra 简介

完全面向对象的ORM框架的特点: `把简单的事情变得更简单; 把复杂的事情变得更复杂,复杂查询面向对象写法、SQL逻辑不直观`。
把简单的事情变得更简单,大家都能欣然接受; 但是,把复杂的事情变得更复杂, 实在有点儿忍受不了, 简直是为了`面向对象`而`面向对象`; 理论上再优秀, 降低了开发效率, 自然也是 备受诟病;
我们是谁？ `程序员`; 遇到不好用的轮子怎么办? `改轮子`! 取其精华去其糟粕,把Mybatis的优点再尽力融合进来, 让大家愉快的使用`jpa`, 是`freeman`框架的小目标之一;


### freeman-jpa-extra 功能说明

由于spring-data-jpa的`Example`实在太鸡肋,所以屏蔽掉了; 请放心,自然是有更好更强大的 操作方式 取而代之; jpa其他的写法,依旧循规踏距地使用就可以;
参考`Mybatis Plus` 基于jpa的`Specification`实现了 查询条件构造器 `QueryWrapper`; 实现方式与开源项目[jpa-spec](https://github.com/wenhao/jpa-spec)雷同,因为想实现`Mybatis Plus QueryWrapper`那样的效果,最简单最容易想到的方式就是使用`Specification`;
与之不同的是,本框架的`QueryWrapper` 更接近`Mybatis Plus` `QueryWrapper`的写法; `Mybatis Plus`的`UpdateWrapper`也很好用,但是jpa封装出类似的效果,需要写的代码量有点而大,暂时不作实现;
受`Mybatis`的启发, 基于现有开源项目[spring-data-jpa-extra](https://github.com/slyak/spring-data-jpa-extra)实现了使用sql模板文件 写sql片段的方式 直观地动态地 原生sql操作增删改查;


### freeman-jpa-extra 使用说明

**实体类**

实体类 顶层父类是`Model`提供了ActiveRecord的模式,支持 ActiveRecord 形式调用,实体类只需继承 Model 类即可实现基本 CRUD 操作;
实体类的 直接父类 可以是`BaseEntity`或`BaseEntity`的子类`AuditableEntity`;

`BaseEntity` 中定义了用于逻辑删除的属性`delFlag`,所以**新建表**的时候记得添加字段**del_flag**; 还有个Map结构的瞬态属性`params`,用于前端传参在实体类没有对应属性 但是还想在不添加属性的情况下用实体类接收参数时使用[^1]


`AuditableEntity` 中定义了后台管理常用到的审计字段 `createTime``createBy` `updateTime` `updateBy`, 需要这些字段的实体类应该继承`AuditableEntity`,对应的表中也要有字段 `create_time``create_by` `update_time` `update_by`;
写代码时 不用关心这四个字段有没有值,框架会自动赋值;

实体类的 类名称和属性 与 数据库表名称和字段名称 是`驼峰转下划线连接的小写形式`的关系[^2];
如果表名称有实体类不具备的前缀(比如,表名称`t_job_log`,实体类名称`JobLog`),需要在实体类注解@Entity中明确赋值name(`@Entity(name = "t_job_log")`);


实体类上的 表别名注解 `@TableAlias("su")`, **每个实体类都应该加上此注解**, 注解的value值应该与sql模板文件中的表别名一致, `数据范围过滤`动态生成的sql片段会插入到sql模板中;

实体类上的注解 `@Where(clause = "del_flag=0")`, 非原生sql[^3]查询时,jpa会动态地追加此注解中值到查询语句的Where条件;

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

**Controller**

Controller 可以继承 BaseController(其实啥都没有)

例子:

```java
public class SysOrgController extends BaseController {}
```

---

**查询条件构造器 QueryWrapper**

构造方法:

```java
// 空参构造, 用 and 连接 eq/like/in等 查询条件
QueryWrapper<T> queryWrapper = new QueryWrapper<>();

// 有参构造, 传入Operator.OR 或 Operator.AND 指定用 and 还是 or 连接 eq/like/in等 查询条件
QueryWrapper<T> queryWrapper = new QueryWrapper<>(Operator.OR);

```

支持的条件查询操作:

- Equal/NotEqual
- GreatThan/LessThan
- GreatThanEqual/LessThanEqual
- IsNull/IsNotNull
- In/NotIn
- Like/NotLike
- Between/NotBetween
- 嵌套AND()
- 嵌套OR
- 嵌套Specification

条件查询支持三个参数:

- condition: 该条件是否加入最后生成的sql中(可以判断属性是否有值,有值加入查询), 重载方法不传此参数,默认为true
- propertyName: 实体类的属性名称
- values: 属性对应的值 (in、notin,可以传多个值; between,notbetween,应该传两个值)
 
在Service实现类的方法中,可以调用如下使用`Specification<T>`的dao方法,可以传入子类`QueryWrapper`

```
Optional<T> findOne(@Nullable Specification<T> spec);
List<T>     findAll(@Nullable Specification<T> spec);
Page<T>     findAll(@Nullable Specification<T> spec, Pageable pageable);
List<T>     findAll(@Nullable Specification<T> spec, Sort sort);
long        count  (@Nullable Specification<T> spec);
```

Service代码片段示例:

```java
    
    @Override
    public Page<SysUser> findPage(QueryRequest queryRequest, SysUser user) {

        // 空参构造, wrapper链式调用的条件(eq/ne/lt/gt/lte/gte/like...)之间 用 and 连接
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper();
        
        String username = user.getUsername();
        String nickname = user.getNickname();
        
        Map<String, Object> params = user.getParams();
        Object createTime = null; //传进来是数组
        if (!ObjectUtils.isEmpty(params)) {
        createTime = params.get("createTime");
        }
          
        queryWrapper.eq(StrUtil.isNotBlank(username), "username", username)
            .like(StrUtil.isNotBlank(nickname), "nickname", "%"+nickname+"%")
            .between("age", 18, 60)
            .eq("idCard.name","技术部")
            //.in("age",new Integer[]{16,25,34})
            //.and(wrapper -> wrapper.isNull("age").eq("code","89757")) //嵌套的条件之间 用 and 连接
            .or(wrapper -> wrapper.isNull("age").eq("code","89757")) //嵌套的条件之间 用 or 连接
            .between(Objects.nonNull(createTime), "createTime", createTime);
        
        PageRequest pageRequest = queryRequest.setDefaultSortField("createTime", false).getPageRequest();
          
        return dao.findAll(queryWrapper, pageRequest);
    }
    
      打印sql:
           select
             sysuser0_.userId as id1_8_,
             sysuser0_.create_time as create_t2_8_,
             sysuser0_.create_by as create_b3_8_,
             sysuser0_.update_by as update_b4_8_,
             sysuser0_.update_time as update_t5_8_,
             sysuser0_.age as age6_8_,
             sysuser0_.avatar as avatar7_8_,
             sysuser0_.birthday as birthday8_8_,
             sysuser0_.company_id as company25_8_,
             sysuser0_.del_flag as del_flag9_8_,
             sysuser0_.dept_id as dept_id26_8_,
             sysuser0_.description as descrip10_8_,
             sysuser0_.email as email11_8_,
             sysuser0_.last_login_time as last_lo12_8_,
             sysuser0_.nickname as nicknam13_8_,
             sysuser0_.password as passwor17_8_,
             sysuser0_.phone as phone18_8_,
             sysuser0_.realname as realnam19_8_,
             sysuser0_.salt as salt20_8_,
             sysuser0_.sex as sex21_8_,
             sysuser0_.code as status22_8_,
             sysuser0_.code as user_co23_8_,
             sysuser0_.username as usernam24_8_
         from
             sys_user sysuser0_
         left outer join
             sys_org sysorg1_
                 on sysuser0_.dept_id=sysorg1_.userId
         where
             sysuser0_.username=?
             and (
                 sysuser0_.nickname like ?
             )
             and (
                 sysuser0_.age between 18 and 60
             )
             and sysorg1_.name=?
             and (
                 sysuser0_.age is null
                 or sysuser0_.code=?
             )
             and (
                 sysuser0_.create_time between ? and ?
             )
         order by sysuser0_.create_time desc
     

```

除了这些常规的条件查询操作,在[jpa-spec](https://github.com/wenhao/jpa-spec)中还有个`Mixed And and Or`, 
再嵌套个Specification? 有必要么? 一时 想不到 什么时候 需要这样的操作, 不管了, 先抄为敬 O(∩_∩)O哈哈~

Service代码片段示例1:
```java
public List<Person> findAll(SearchRequest request) {
    QueryWrapper<Person> queryWrapper = new QueryWrapper<>()
        .like("name", "%ac%")
        .predicate(new QueryWrapper<Person>(Operator.OR).isNull("age").gt("age", 18))
        .between(Objects.nonNull(createTime), "createTime", '2019-01-02 13:45:56', '2019-10-22 13:45:56');

    return personRepository.findAll(queryWrapper);
}
```

Service代码片段示例2:
```java
//  JPA Specification原有写法, 当然也ok
public List<Phone> findAll(SearchRequest request) {
    QueryWrapper<Person> queryWrapper = new QueryWrapper<>()
        .between("age", 10, 35)
        .predicate(StrUtil.isNotBlank(jack.getName()), ((root, query, cb) -> {
            Join address = root.join("addresses", JoinType.LEFT);
            return cb.equal(address.get("street"), "Chengdu");
        }));

    return phoneRepository.findAll(queryWrapper);
}
```

---

**QueryRequest**

查询请求,很多时候需要 分页参数、排序参数, Controller层方法的形参写上`QueryRequest`, 自动接收分页和排序参数;
查询需要PageRequest对象,只需要调用一下`getPageRequest()`, 查询需要Sort对象,只需要调用一下`getSort()`;
调用getPageRequest()时, 分页参数有默认备用值`pageNo`=0,`pageSize`=20;
排序字段可以设置默认备用值`QueryRequest#setDefaultSortField("默认排序属性名称", 是否升序)`;

Service代码片段示例:

```java
// 如果没接收到分页参数, 有默认备用值`pageNo`=0,`pageSize`=20;
PageRequest pageRequest = queryRequest.getPageRequest();

//  如果没接收到排序参数, setDefaultSortField设置的默认值 生效
PageRequest pageRequest = queryRequest.setDefaultSortField("createTime", false).getPageRequest();

dao.findAll(pageRequest);

// 虽然使用 QueryRequest 接收 参数了, 但是只需要排序 不需要分页
Sort sort = queryRequest.setDefaultSortField("createTime", false).getSort();

// queryRequest.getSort(); // 当然 也可以 不设置 默认排序字段

dao.findAll(sort);
```
---

**PageUtil、SortUtil**

查询需要排序或分页, 但是这些参数不是前端传过来的, 本来JPA干这事儿就是个高手, 但是, 还能更优秀;
支持多字段排序是必须的, 而且做到了 看一眼就知道怎么用;

代码片段示例:

```java
// 如果分页参数, 有默认备用值`pageNo`=0,`pageSize`=20;
PageRequest pageRequest = PageUtil.builder().pageNo(0).pageSize(20).asc("age").desc("createTime").build();

// 如果只需要分页 不需要排序 // 当然是jpa 自带写法 更简单
//PageRequest pageRequest = PageUtil.builder().pageNo(0).pageSize(20).build();
PageRequest pageRequest = PageRequest.of(1,30);

// 如果只需要排序 不需要分页 // getSort();
Sort sort = PageUtil.builder().asc("age").desc("createTime").build().getSort();

// 如果只需要排序 不需要分页 // 也可以使用只处理工具类 SortUtil
Sort sort = SortUtil.builder().asc("age").desc("createTime").build();
```

----

**sql模板查询**

模板引擎用的enjoy,因为`减少项目依赖,使用更简单,更好用的第三方工具`是本框架的努力方向之一;

Dao中需要使用模板查询的方法,加上注解@TemplateQuery("这个值对应模板文件中的 sql片段名称"),
注解中不给value赋值,会用方法名去对应模板中匹配;
sql模板文件应该放到`resources/sql`的目录下,命名方式`实体类.tpl`, sql模板文件中的`sql片段名称`在同一个模板文件中要`唯一`,
只支持同一个模板文件`#include("sql片段名称")`调用其他sql片段,不支持跨模板文件调用(不难,只是没写代码去实现)

模板语法:

可以[点击这里](https://www.jfinal.com/doc/6-3)看一下enjoy模板引擎的`表达式`和`指令`语法;
由于只是以`sql代码片段`为单位,使用enjoy模板引擎解析的语义的并缓存的,所以作用域范围局限与此;


| 语法 | 释义 | 说明 |
| -------- | --------- | -------- |
| ### | 注释 | 3个#, 只支持行内注释, 不支持块注释 |
| #@@# 名称 | sql片段名称 | 中间有没有空格都行, 名称在此模板中唯一 |
| #include("sql片段名称") | 引用sql片段 | 不支持跨模板文件引用 |
| #(变量名称) | 取值 | 变量名称不需要引号 |
| ?? | 空安全 | 参考https://www.jfinal.com/doc/6-4 |
| #if | if 指令 | 参考https://www.jfinal.com/doc/6-4 |
| #for | for 指令 | 参考https://www.jfinal.com/doc/6-4 |

`###`、`#@@#`、`#include`, 非enjoy自带语法, `#include`是写java代码实现的,
enjoy的`#set`、`#define`指令 在模板文件中是无效的;


Dao代码片段示例:

```java
@Repository
public interface SysRoleRepository extends BaseRepository<SysRole,Long> {
    // 以下几个方法都会调用 对应模板文件中的 名称为 findAllByTemplate 的sql代码片段
    
    // 注解中的value匹配 对应模板文件中的 sql代码片段名称
	@TemplateQuery("findAllByTemplate")
	List<SysRole> findAbc123(); // 不传参数

    // 注解没有value值,会用这个方法名去模板中匹配
    // 模板中使用的变量名 ids 判断/取值
	@TemplateQuery
	List<SysRole> findAllByTemplate(@Param("ids")List<Long> ids);


    // 模板中使用SysRole的变属性做为变量名判断/取值
	@TemplateQuery
    List<SysRole> findAllByTemplate(SysRole sysRole); 

    // 排序查询: 用List接收,参数传入Sort, 模板中 不需要写 排序的sql代码
	@TemplateQuery
    List<SysRole> findAllByTemplate(SysRole sysRole, Sort sort);

    // 分页排序查询: 用Page接收,参数传入Pageable, 模板中 不需要写 分页排序的sql代码
	@TemplateQuery
	Page<SysRole> findAllByTemplate(SysRole sysRole, Pageable pageable);

}
```

sql模板文件代码示例:

```sql
#@@# columns
sr.id, sr.parent_id, sr.name, sr.code, sr.remark, sr.sort_no, sr.status, sr.data_scope,
    (SELECT GROUP_CONCAT(srd.dept_id) as data_dept_ids FROM sys_role_dept srd WHERE srd.role_id = sr.id) as data_dept_ids,
    sr.create_time,sr.create_by,sr.update_by,sr.update_time,sr.del_flag


### sql片段名称为 findAllByTemplate
#@@# findAllByTemplate
SELECT
    #include("columns")
FROM
    sys_role sr ###LEFT JOIN sys_user su ON sr.create_by = su.user_id
WHERE 1=1 AND sr.del_flag = '0'
    #if(name??)　### 属性: name　值: '户'
     AND sr.name LIKE CONCAT('%',#(name),'%')
    #end
    #if(params?? && params.createTime??) ### params(Map), key: 'createTime' , value: {'2019-06-01','2019-09-29'}
     AND date_format(sr.create_time,'%Y-%m-%d') BETWEEN #(params.createTime[0]) AND #(params.createTime[1])
    #end
    #if(params?? && params.dataScope??)
     #(params.dataScope)
    #end
    AND 1 IN #(dataDeptIds) ### 传入参数 List<Long> dataDeptIds = Arrays.asList(1L,2L,3L,4L);
    AND 'a' IN #(stringsIds) ### 传入参数 List<String> stringsIds = Arrays.asList("a","b","c");

### 控制台打印sql如下:
### SELECT
###     sr.*
### FROM
###     sys_role sr
### LEFT JOIN
###     sys_user su
###         ON sr.create_by = su.user_id
### WHERE
###     1=1
###     AND sr.name LIKE CONCAT('%','户','%')
###     AND date_format(sr.create_time,'%Y-%m-%d') BETWEEN '2019-06-01' AND '2019-09-29'
###     AND 1 IN (1,2,3,4)
###     AND 'a' IN ('a','b','c')
```

Dao接收模板查询结果数据方式:

String/基本数据类型以及包装类/实体类/List<实体类>/List<pojo>/Page<实体类>/Page<pojo>/Map<String,Object>/List<Map<String,Object>>

Map方式比较特殊, 举个例子:

Dao
```java
/**
* 获取系统近七天来的访问记录
*/
@TemplateQuery
List<Map<String, Object>> findLastSevenDaysVisitCount(SysUser user);
```
模板文件
```sql
#@@# findLastSevenDaysVisitCount
SELECT
    date_format(l.login_time, '%m-%d') days, count(1) count
FROM
    (SELECT * FROM sys_login_log where date_sub(curdate(), interval 7 day) <= date(login_time) ) as l
WHERE 1=1
    #if(userId??)
        AND l.user_id = #(userId)
    #end
GROUP BY days


### 变量 userId 值 传入 1
### 打印sql: SELECT date_format(l.login_time, '%m-%d') days,  count(1) count 
###         FROM (SELECT * FROM sys_login_log where date_sub(curdate(), interval 7 day) <= date(login_time) ) as l
###         WHERE 1=1  AND l.user_id = 1 GROUP BY days
```



[1] 传入参数名`params.xxx`,`params.yyy`, 会 接收到Map属性`params`中

[2] 非首字母如果是大写转换成`_小写`,实体类名称`SysUser`,表名称`sys_user`;属性名称`firstName`,表字段名称`first_name`

[3] @Query(value="...",nativeQuery = true)或sql模板文件,使用的是原生sql查询