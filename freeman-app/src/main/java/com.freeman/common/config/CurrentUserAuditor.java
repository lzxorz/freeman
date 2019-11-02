package com.freeman.common.config;

import com.freeman.common.auth.shiro.utils.ShiroUtil;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class CurrentUserAuditor implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(ShiroUtil.getUserId());
    }

}
/*
 也可以去掉@Component,改用下面的方式
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class Springboot2JpaAuditingApplication {

   @Bean
   public AuditorAware<String> auditorAware() {
      return new CurrentUserAuditor();
   }

   public static void main(String[] args) {
      SpringApplication.run(Springboot2JpaAuditingApplication.class, args);
   }
}*/
