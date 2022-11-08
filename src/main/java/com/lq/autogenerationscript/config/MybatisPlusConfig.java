package com.lq.autogenerationscript.config;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @Author: liQing
 * @Date: 2022-10-24 14:23
 * @Version 1.0
 */
@Component
public class MybatisPlusConfig {
    /**
     * 为了配置管理我把这段代码加入到了 MybatisPlusConfig 中
     */
    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.put("Oracle", "oracle");
        properties.put("MsSQL", "mssql");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }

}
