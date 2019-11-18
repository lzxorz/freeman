package com.freeman;


import cn.hutool.core.date.DateTime;
import com.freeman.common.utils.BeanUtil;
import com.freeman.domain.User;
import com.freeman.spring.data.domain.Model;
import com.freeman.spring.data.repository.NativeSqlQuery;
import com.freeman.sys.domain.SysUser;
import com.freeman.sys.repository.SysUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUser {

    @Autowired
    private static ApplicationContext applicationContext;

    @BeforeClass
    public static void init() {
        Model.setApplicationContext(applicationContext);
    }


    //保存
    @Test
    public void testAdd() {
        User user = User.builder()
                .username("helloword")
                .realName("掌声呢")
                .realName("大家一起来嗨吧~")
                .nickname("霸王别急.")
                .age(123)
                .avatar("http://phone.baidu.com/ninainaigetuier.jpg")
                .birthday(LocalDate.now())
                .email("hao123@163.com")
                // .phone("18051015487")
                .password("2738wsdfs9fas9")
                .wxId("23456ugfd").build();

        //user.addAddress(Address.builder().user(user).country("中国").province("北京").city("北京市").district("海淀区").street("xxx街xxx号").label("公司").build())
        //    .addAddress(Address.builder().user(user).country("中国").province("山南").city("山顶市").district("崎岖区").street("xxx街xxx号").label("休闲").build());


        user.save();
        if (Objects.nonNull(user.getId())) {
            System.out.println("添加成功==> "+ user.toString());
        } else {
            System.out.println("添加失败");
        }
    }

    //查询
    @Test
    public void testRetrieve() {

        Long uid = 1123968831853826048L;
        User user = User.builder().id(uid).build();
        user = user.findById();
        if (Objects.nonNull(user)) {
            System.out.println("查询成功==> "+ user);
        } else {
            System.out.println("查询失败");
        }

        System.out.println("///=========///=========///=========///=========///=========///=========///");

        //long aid = 1120604477305524224L;
        //Address address = Address.builder().id(aid).build();
        //address = address.find();
        //if (Objects.nonNull(address)) {
        //    System.out.println("查询成功==> "+ address);
        //} else {
        //    System.out.println("查询失败");
        //}
    }

    //更新
    @Test
    public void testUpdate() {
        Long id = 1123968831853826048L;
        User newWebuser = User.builder().id(id).nickname("急急急吧").build(); //前端传过来 部分字段 新值 , 用实体类接收
        User user = User.builder().id(id).build().findById(); // 数据库查出来 记录
        BeanUtil.copyProperties(newWebuser, user); //新值覆盖旧值

        user.save(); //保存

        log.info(user.toString());
    }

    //删除
    @Test
    public void testDelete() {
        Long id = 1120604477217443840L;
        User.builder().id(id).build().delete();
        if (Objects.isNull(User.builder().id(id).build().findById())) {
            System.out.println("删除成功");
        } else {
            System.out.println("删除失败");
        }
    }

    //@Autowired
    //private Environment env;
    @Test
    public void testyml() {
        // "application.yml" "spring.profiles.active" "spring.profiles.include"

        YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
        ClassPathResource classPathResource = new ClassPathResource("application.yml");
        if (!classPathResource.exists()) {
            classPathResource = new ClassPathResource("config/application.yml");
        }
        yamlFactory.setResources(classPathResource);
        Properties object = yamlFactory.getObject();
        System.out.println(object);
    }

    //@Autowired
    //private Environment env;
    @Autowired
    private SysUserRepository userRepository;

    @Test
    public void testNativeSQL() {
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

        List<SysUser> allBySQL = userRepository.findAllBySql(nativeSqlQuery, SysUser.class);
        System.out.println("ok");
    }

    @Test
    public void testNativeSQL1() {
        String sql = "select u.id, u.username from sys_user u where u.username = ?1 and u.create_time < ?2 and u.id in ?3";
        List<?> allBySQL = userRepository.findAllBySql(sql, SysUser.class, "system", new Date(), Arrays.asList(1L, 2L, 3L));
        System.out.println("ok");
    }

    // @Test
    // public void testNativeSQL2() {
    //     String sql = "select u.id, u.username from sys_user u where u.username = :u.username and u.create_time < :u.create_time";
    //     HashMap<String, Object> params = MapUtil.newHashMap();
    //     params.put("u.username", "system");
    //     params.put("u.create_time", new Date());
    //     List<?> allBySQL = userRepository.findAllBySQL(sql, SysUser.class, params);
    //     System.out.println("ok");
    // }

}
