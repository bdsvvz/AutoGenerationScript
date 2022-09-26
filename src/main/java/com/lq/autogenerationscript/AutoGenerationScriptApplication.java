package com.lq.autogenerationscript;

import com.lq.autogenerationscript.utils.GeneraterTableUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class AutoGenerationScriptApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoGenerationScriptApplication.class, args);
    }

}
