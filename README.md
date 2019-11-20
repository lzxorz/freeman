### Freeman 快速开发框架

<div style="text-align: center">

[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/elunez/eladmin/blob/master/LICENSE)
![https://img.shields.io/badge/license-Apache%202.0-blue.svg?longCache=true&style=flat-square](https://img.shields.io/badge/license-Apache%202.0-blue.svg?longCache=true&style=flat-square)
![https://img.shields.io/badge/springboot-2.1.4-yellow.svg?longCache=true&style=flat-square](https://img.shields.io/badge/springboot-2.1.4-yellow.svg?longCache=true&style=flat-square)
![https://img.shields.io/badge/shiro-1.4.0-orange.svg?longCache=true&style=flat-square](https://img.shields.io/badge/shiro-1.4.0-orange.svg?longCache=true&style=flat-square)
![https://img.shields.io/badge/vue-2.6.10-brightgreen.svg?longCache=true&style=flat-square](https://img.shields.io/badge/vue-2.6.10-brightgreen.svg?longCache=true&style=flat-square)

</div>

#### 项目简介

Freeman是基于 Spring Boot2 + Spring data Jpa + Shiro + Vue2 + JWT 的前后端分离的敏捷开发框架；以Spring Framework为核心容器，Spring data Jpa(Hibernate实现)为数据访问层， Apache Shiro为权限框架，Redis对常用数据进行缓存，前端使用Vue全家桶，前后端分离、JWT鉴权的开源框架。

角色的功能权限控制方式为基于RBAC规范的Shiro，角色数据范围控制是根据用户的所属机构、数据的创建者(用户ID)实现。项目支持前端菜单动态路由。前后端交互使用JWT验证权限，使用Redis调用lua脚本CAS的方式，并且在令牌刷新时，旧令牌续命30秒进行平滑过渡，丝般顺滑地达到了用户无感知动态刷新JWT的目的。

jpa`动态条件查询写起来麻烦`，`项目的逻辑复杂的时候，代码冗长、sql逻辑不直观`的坑，我想努力填一下，这是我写这个项目的初衷。

已内置很多优秀的基础功能和高效的 工具，包括：系统权限组件、数据权限组件、数据字典组件等。前端界面风格采用了阿里开源的`ant-design-vue`框架。密码加密、访问验证、数据权限验证。使用Maven做项目管理，提高项目的易开发性、扩展性。

目前功能模块代码生成器、权限框架、数据字典、数据缓存、数据监控、计划任务、多数据源管理、类似mybatis动态SQL、短信发送、邮件发送、统计功能等功能。


#### 技术特点
使用目前流行的WEB开发架构技术，如 SpringBoot,Mybatis, jpa(Hibernate),Apache Shiro ,JWT 等等，目前仅保证对MySQL数据库的完美支持。 分层设计：使用分层设计，分为dao，service，Controller，低耦合，高内聚。

#### 安全考虑
严格遵循了web安全的规范，前后端交互使用了JWT，参数编码传输，密码md5加密存储，shiro权限验证，XSS统一过滤器。

#### 令牌刷新方式
前端axios请求拦截器自动把令牌放入head，axios响应拦截器中检查有`newToken`标记，有就更新保存的令牌。后端shiro拦截需要权限的接口，每次都拿到JWT进行认证。采用CAS机制+刚过期令牌续期30秒，实现用户无感知刷新，
并且在令牌过期时大量并发请求不会出错。



**开发文档**  [https://lzxorz.github.io/zh-cn/docs/doc1.html](https://lzxorz.github.io/zh-cn/docs/doc1.html)

**体验地址**  [http://47.105.42.254](http://47.105.42.254)

**账号、密码** ```system/123456```

---------------------------

#### 项目源码

|  平台   |   源码链接  |
|---  |------ |
|  github   |  https://github.com/lzxorz/freeman   |
|  码云   |  https://gitee.com/lzxorz/freeman   |

---------------------------

#### 技术架构

##### 开发环境

- 语言：java8
- IDE(后端)： IDEA 或者 Eclipse
- IDE(前端)： vscode 或者 WebStorm 或者 IDEA
- 依赖管理：Maven
- 构建工具：Maven
- 数据库：MySQL5.7+
- 缓存：Redis4+

##### 技术选型

1、后端

- 核心框架：Spring boot 2.1.4
- 持久层框架：Spring Data Jpa
- 数据库连接池：hikari ~~Alibaba Druid~~
- 安全框架：Apache Shiro 1.4、java-jwt3.8.1
- 服务端验证：Hibernate Validator
- 日志框架：Logback
- 任务调度：Quartz
- 缓存框架：Redis
- 工具类：Hutool 4.5.6、fastjson 1.2.58、Mapstruct 1.3.0.Final
- 其他: lombok 1.18.8、Swagger 2.9.2、Ip2region 1.7.2、dozer 5.5.1、fst 2.5.7

2、前端

- 核心框架：Vue 2.6.10,ant-design-vue
- 路由管理：Vue Router
- 状态管理：Vuex
- http库：Axios
- 构建工具：@vue/cli 3.8.2,yarn
- 富文本编辑器：vue-ueditor-wrap
- 图表库：vue-apexcharts
- 代码检测：Eslint
- 头像裁剪组件：vue-cropper

---------------------------

####  系统功能
- 用户管理：提供用户的相关配置，新增用户后，默认密码为123456
- 角色管理：对权限与菜单进行分配，可以设置角色的数据范围
- 权限管理：权限细化到接口，可以理解成按钮权限
- 菜单管理：已实现菜单动态路由，后端可配置化，支持多级菜单
- 部门管理：可配置系统组织架构，树形表格展示
- 岗位管理：配置各个部门的职位
- 字典管理：应广大码友的要求加入字典管理，可维护常用一些固定的数据，如：状态，性别等
- 操作日志：记录用户操作的日志
- 异常日志：记录异常日志，方便开发人员定位错误
- 系统缓存：使用jedis将缓存操作可视化，并提供对redis的基本操作，可根据需求自行扩展
- 定时任务：整合Quartz做定时任务，加入任务日志，任务运行情况一目了然

#### 项目结构

项目采用分模块开发方式
```shell
freeman
├── freeman-ant-design-vue   前端vue项目
│   ├── public
│   └── src
│       ├── api
│       ├── assets
│       ├── components
│       └── views
|
├── freeman-app
│   └── com.freeman
│       ├── common 公共模块
│       │   ├── advice        统一异常处理
│       │   ├── asyncTask     异步任务
│       │   ├── auth
│       │   │   └── shiro     权限控制
│       │   │       ├── cache
│       │   │       ├── filter
│       │   │       ├── realm 支持多种登录方式
│       │   │       ├── session 用户ID=>sessionId
│       │   │       ├── token 
│       │   │       └── utils Shiro工具类
│       │   ├── base          基础父类
│       │   │   ├── controller
│       │   │   ├── domain
│       │   │   └── service
│       │   │       └── impl
│       │   ├── config         配置
│       │   ├── constants      常量
│       │   ├── dataPerm       权限过滤
│       │   ├── dataSource     动态多数据源
│       │   ├── log            系统日志切面处理
│       │   ├── runner         项目启动加载
│       │   ├── task           简单定时任务
│       ├── job                quartz任务调度
│       └── sys                后台管理
|
├── freeman-jpa-plus           jpa增强模块
|
└── freeman-common             通用模块
    └── com.freeman.common
        ├── cache.redis        缓存
        ├── file               文件上传
        ├── limit              接口限流
        ├── reflect            反射工具类
        ├── result             数据统一返回处理
        ├── utils              常用工具类
        ├── validator          bean验证
        └── xss                xss过滤

```


#### 系统预览
<table>
    <tr>
        <td><img src="https://lzxorz.github.io/docs/zh-cn/img/home.png"/></td>
        <td><img src="https://lzxorz.github.io/docs/zh-cn/img/org.png"/></td>
    </tr>
    <tr>
        <td><img src="https://lzxorz.github.io/docs/zh-cn/img/user.png"/></td>
        <td><img src="https://lzxorz.github.io/docs/zh-cn/img/userinfo.png"/></td>
    </tr>
    <tr>
        <td><img src="https://lzxorz.github.io/docs/zh-cn/img/track.png"/></td>
        <td><img src="https://lzxorz.github.io/docs/zh-cn/img/setting.png"/></td>
    </tr>
</table>


#### 反馈交流
- QQ交流群：817181470


#### 使用说明
jdk1.8,mysql5.7,redis4+,lombok1.18.8

后端
- 导入freeman目录下的freeman.sql文件到mysql数据库
- 导入项目到Idea,(项目目前使用分模块开发，建议是用IDEA开发).
- 修改数据库配置: application-dev.yml中的jdbc-url、username、password.
- 修改redis配置: application-dev.yml中的host、port、password.
- 启动项目(freeman-app中的com.freeman.FreemanApplication),管理员账号system/密码123456
前端
- freeman-ant-design-vue执行终端命令`yarn`安装依赖
- 安装完成后执行`yarn serve`启动项目,会自动打开浏览器

#### 项目部署流程
node,yarn

后端 freeman-app
- 确认修改正确yml配置文件中的spring.profiles.active参数,mysql数据库连接参数,redis连接
在freeman-parent目录终端执行maven命令 `clean package`
- 部署方式用两种：
    - 可以用freeman-app/target目录生成的jar包
    - 可以用docker, 上传文件到服务器,在服务器上构建镜像(请查看freeman-app/Dockerfile中的注释内容)
    
前端 freeman-ant-design-vue
- 确认修改正确`src.utils.request.js`文件中`baseURL`
- 在freeman-ant-design-vue目录终端执行yarn命令 `yarn build`
- 生成的dist目录下的所有文件放到静态web服务器中即可



