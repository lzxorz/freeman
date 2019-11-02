package com.freeman.common.dataSource;

import com.freeman.common.dataSource.DynamicDataSourceKey;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;


/**
 * 动态数据源配置
 * @author: liuzhixin
 * @email: lzxorz@163.com
 */
@Slf4j
@Configuration
public class DynamicDataSourceConfig {

    /*@Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/freeman");
        config.setUsername("root");
        config.setPassword("lzx123");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }*/

    @Bean(name = "masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "slave1DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.slave1")
    public DataSource slave1DataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "slave2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.slave2")
    public DataSource slave2DataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }


    @Bean @Primary
    public DataSource dynamicDataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
                                         @Qualifier("slave1DataSource") DataSource slave1DataSource,
                                         @Qualifier("slave2DataSource") DataSource slave2DataSource) {

        // 数据源路由器
        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource(){
            @Override protected Object determineCurrentLookupKey() { return DynamicDataSourceKey.get(); }
        };
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        //noinspection unchecked
        routingDataSource.setTargetDataSources(new HashMap(){{
            put(DynamicDataSourceKey.MASTER, masterDataSource);
            put(DynamicDataSourceKey.SLAVE1, slave1DataSource);
            put(DynamicDataSourceKey.SLAVE2, slave2DataSource);
        }});
        return routingDataSource;
    }

}
