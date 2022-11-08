package com.lq.autogenerationscript.config;

/**
 * @Author: liQing
 * @Date: 2022-10-24 14:45
 * @Version 1.0
 */

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CodeCache {

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    public static Map<String, Object> codeMap = new HashMap<>();

    @PostConstruct
    public void init() {
        codeMap.put("dataSource", driverClassName.indexOf("sqlserver") >= 0 ? "mssql" : "oracle");
    }

    @PreDestroy
    public void destroy() {
        //系统运行结束
        codeMap.clear();
    }


}
