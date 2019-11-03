# freeman

### Freeman 后台管理系统

<div style="text-align: center">

[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/elunez/eladmin/blob/master/LICENSE)
![https://img.shields.io/badge/license-Apache%202.0-blue.svg?longCache=true&style=flat-square](https://img.shields.io/badge/license-Apache%202.0-blue.svg?longCache=true&style=flat-square)
![https://img.shields.io/badge/springboot-2.1.4-yellow.svg?longCache=true&style=flat-square](https://img.shields.io/badge/springboot-2.1.4-yellow.svg?longCache=true&style=flat-square)
![https://img.shields.io/badge/shiro-1.4.0-orange.svg?longCache=true&style=flat-square](https://img.shields.io/badge/shiro-1.4.0-orange.svg?longCache=true&style=flat-square)
![https://img.shields.io/badge/vue-2.6.10-brightgreen.svg?longCache=true&style=flat-square](https://img.shields.io/badge/vue-2.6.10-brightgreen.svg?longCache=true&style=flat-square)

</div>

#### 项目简介

Freeman是基于 Spring Boot + Spring data Jpa + Shiro + Vue + JWT 的前后端分离的敏捷开发框架；以Spring Framework为核心容器，Spring data Jpa(Hibernate实现)为数据访问层， Apache Shiro为权限框架，Redis对常用数据进行缓存，前端使用Vue全家桶，前后端分离、JWT鉴权的开源框架。

Freeman项目是努力打造springboot框架的极致细腻的脚手架。包括一套漂亮的前台。无其他杂七杂八的功能，原生纯净。
基于springboot的一款纯净脚手架。努力打造完美注释跟文档。方便快速二次开发。

角色的功能权限控制方式为基于RBAC规范的SHiro，角色数据范围控制是根据用户的所属机构、数据的创建者(用户ID)实现。项目支持前端菜单动态路由。前后端交互使用JWT验证权限，使用Redis调用lua脚本的方式，并且在令牌刷新时，旧令牌续命30秒进行平滑过渡，丝般顺滑地达到了用户无感知动态刷新JWT的目的。

jpa`动态条件查询写起来麻烦`，`项目的逻辑复杂的时候，代码冗长、sql逻辑不直观`的坑，我想努力填一下，这是我写这个项目的初衷。

JeeWeb主要定位于企业快速开发平台建设，已内置很多优秀的基础功能和高效的 工具，包括：系统权限组件、数据权限组件、数据字典组件、核心工具组件、视图操作组件、代码生成、 UI模版标签 库等。前端界面风格采用了结构简单、性能优良、页面美观大气的Twitter Bootstrap页面展示框架。采用分层设计、提交数据安全编码、密码加密、访问验证、数据权限验证。使用Maven做项目管理，提高项目的易开发性、扩展性。

目前功能模块代码生成器、权限框架、数据字典、数据缓存、并发框架、数据监控、计划任务、多数据源管理、附件管理、类似mybatis动态SQL、UI模板标签、短信发送、邮件发送、统计功能等功能。

JeeWeb的开发方式采用（ 代码生成器快速设计生成代码->手工完善逻辑->丰富模板标签快速前端开发 ），可以快速协助java开发人员解决60%的重复工作，让开发人员更多关注业务逻辑的实现，框架使用前端模板标签，解放JAVA开发人员的开发压力，提高开发效率，为企业节省项目研发成本，减少开发周期。

#### 技术特点
JeeWeb使用目前流程的WEB开发架构技术，如 SpringBoot,Mybatis, Hibernate,Apache Shiro, Disruptor , ehcache, Jquery ,BootStrap 等等，支持多种数据库MySQL, Oracle, sqlserver等。 分层设计：使用分层设计，分为dao，service，Controller，view层，层次清楚，低耦合，高内聚。

#### 安全考虑
严格遵循了web安全的规范，前后台双重验证，参数编码传输，密码md5加密存储，shiro权限验证，从根本上避免了SQL注入，XSS攻击，CSRF攻击等常见的web攻击手段。

#### 功能特点


**开发文档**  [https://lzxorz.github.io/](https://lzxorz.github.io/)

**体验地址**  [http://139.199.112.93](http://139.199.112.93)

**账号密码** ```system/123456```

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
- 其他: lombok 1.18.8、Swagger 2.9.2、Enjoy 4.2、Ip2region 1.7.2、dozer 5.5.1、fst 2.5.7

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
- 邮件工具：配合富文本，发送html格式的邮件

#### 项目结构
项目采用分模块开发方式，将通用的配置放在公共模块，```system```模块为系统核心模块也是项目入口模块，```logging``` 模块为系统的日志模块，```tools``` 为第三方工具模块，包含了图床、邮件、七牛云、支付宝，```generator``` 为系统的代码生成模块



#### 系统预览
<table>
    <tr>
        <td><img src=""/></td>
        <td><img src=""/></td>
    </tr>
    <tr>
        <td><img src=""/></td>
        <td><img src=""/></td>
    </tr>
    <tr>
        <td><img src=""/></td>
        <td><img src=""/></td>
    </tr>
    <tr>   
 <td><img src=""/></td>
    </tr>
</table>


#### 反馈交流
- QQ交流群：



#### 使用说明

- 导入jeeweb目录下的，具体模块sql/mysql.sql文件到mysql数据库
- 导入项目到Idea,(项目目前使用分模块开发，我们建议是用IDEA开发).
- 修改数据库配置文件application.yml中的账号密码.
- 启动项目,管理员账号admin/密码123456

#### 部署流程
导入doc文件夹里面的test.sql到数据库
确认自己的mysql版本 进行修改jar 在pom.xml 73-84行
修改application-dev.yml 里面自己数据库版本对应的jdbc链接
正常启动run SpringbootSwagger2Application.java
打包发布编译流程
maven编译安装pom.xml文件即可打包成war
登陆地址
http://localhost:8081 默认帐号密码: admin/admin
swagger http://localhost:8081/swagger-ui.html
启动类
SpringbootSwagger2Application 启动类
数据库模型


