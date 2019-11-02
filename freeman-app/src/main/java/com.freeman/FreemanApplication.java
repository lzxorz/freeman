package com.freeman;

import com.freeman.common.dataSource.DynamicDataSourceConfig;
import com.freeman.spring.data.repository.BaseRepositoryFactoryBean;
import com.freeman.spring.data.repository.BaseRepositoryImpl;
import com.freeman.spring.data.sqltemplate.EnjoySqlTemplates;
import com.freeman.utils.FmOutputDirectiveFactory;
import com.jfinal.template.Engine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@EnableAsync
@EnableScheduling
@EnableJpaAuditing
@EnableTransactionManagement
@Import({DynamicDataSourceConfig.class})
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class, repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
public class FreemanApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(FreemanApplication.class, args);
        log.info("******************************************************************************************************************\n");
    }


    /**
     * web容器中进行部署
     * --参考： https://www.cnblogs.com/javabg/p/9116708.html--
     * 参考： https://blog.csdn.net/fanshukui/article/details/80258793
     * 1.  <packaging>war</packaging>
     *
     * 2.      <!--部署成war包时开启↓↓↓↓-->
     *         <dependency>
     *             <groupId>org.springframework.boot</groupId>
     *             <artifactId>spring-boot-starter-tomcat</artifactId>
     *             <scope>provided</scope>
     *         </dependency>
     *         <dependency>
     *             <groupId>org.apache.tomcat.embed</groupId>
     *             <artifactId>tomcat-embed-jasper</artifactId>
     *             <scope>provided</scope>
     *         </dependency>
     *         <!--部署成war包时开启↑↑↑↑-->
     *
     * 3. 此时打成的包的名称应该和application.properties的server.context-path=/test 保持一致
     *    <build>
     *      <finalName>test</finalName>
     *    </build>
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意这里要指向原先用main方法执行的Application启动类
        return builder.sources(FreemanApplication.class);
    }


    @Bean
    public EnjoySqlTemplates enjoySqlTemplates() {
        EnjoySqlTemplates templates = new EnjoySqlTemplates();

        Engine engine = Engine.create("jpaEngine");
        engine.setDevMode(true);
        engine.setToClassPathSourceFactory();
        engine.setOutputDirectiveFactory(FmOutputDirectiveFactory.me);

        templates.setEngine(engine);
        templates.setSuffix(".tpl");
        return templates;
    }
}
