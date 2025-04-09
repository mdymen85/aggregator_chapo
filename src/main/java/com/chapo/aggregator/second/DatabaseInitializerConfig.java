package com.chapo.aggregator.second;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Slf4j
public class DatabaseInitializerConfig {

    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        var x = DataSourceBuilder.create();
//            .driverClassName("com.mysql.cj.jdbc.Driver")
//            .url("jdbc:mysql://localhost:3306/aggregator")
//            .username("root")
//            .password("mdymen_password");
        return x.build();
    }

}