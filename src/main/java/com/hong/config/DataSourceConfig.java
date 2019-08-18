package com.hong.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wanghong
 * @date 2019/08/18 14:58
 **/
@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceConfig {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
}
