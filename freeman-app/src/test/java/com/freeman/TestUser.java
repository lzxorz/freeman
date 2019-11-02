package com.freeman;


import cn.hutool.core.lang.Console;
import com.freeman.common.utils.BeanUtil;
import com.freeman.common.utils.StrUtil;
import com.freeman.domain.Phone;
import com.freeman.domain.User;
import com.freeman.repository.PhoneRepository;
import com.freeman.repository.UserRepository;
import com.freeman.spring.data.domain.Model;
import com.freeman.spring.data.repository.specquery.QueryWrapper;
import com.github.wenhao.jpa.Specifications;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

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





    // @Autowired
    // private SysUserRepository sysUserRepository;
    @Autowired
    private UserRepository userRepository;
    // @Autowired
    // private AddressRepository addressRepository;
    @Autowired
    private PhoneRepository phoneRepository;

    @Test
    public void should_be_able_to_find_by_using_many_to_one_query() {

        String username  = "Jack";

        Specification<Phone> specification = Specifications.<Phone>and()
                .eq("brand", "HuaWei")
                .eq(StrUtil.isNotBlank(username), "user.username", username)
                .build();

        List<Phone> phones = phoneRepository.findAll(specification);

        assertThat(phones.size()).isEqualTo(2);
    }

    @Test
    public void testNestedPath() {

        String username = "Jack";

        Specification<User> wrapper = Specifications.<User>and()
                .eq(cn.hutool.core.util.StrUtil.isNotBlank(username), "username", username)
                // .eq("idCard.number",123456)
                .build();

        List<User> all = userRepository.findAll(wrapper);
        Console.log(all);

        QueryWrapper<User> wrapper1 = new QueryWrapper<User>();
        wrapper1.eq(cn.hutool.core.util.StrUtil.isNotBlank(username), "username", username)
                .eq("idCard.number",123456);

        List<User> all1 = userRepository.findAll(wrapper1);
        Console.log(all1);
    }



}
