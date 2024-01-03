package com.lq.autogenerationscript;

import com.lq.autogenerationscript.utils.GeneraterTableUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cglib.beans.BeanCopier;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@ServletComponentScan
@EnableCaching
public class AutoGenerationScriptApplication {

    public static void main(String[] args) throws IOException, TemplateException {
        SpringApplication.run(AutoGenerationScriptApplication.class, args);

    }

}
