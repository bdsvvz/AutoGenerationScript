package com.lq.autogenerationscript;

import com.lq.autogenerationscript.utils.GeneraterProcUtils;
import com.lq.autogenerationscript.utils.GeneraterTableUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * @Author: liQing
 * @Date: 2022-09-26 16:37
 * @Version 1.0
 */
//@Component
public class ApplicationRunnerTest implements ApplicationRunner {
    @Resource
    GeneraterProcUtils generaterProcUtils;
    @Resource
    GeneraterTableUtils generaterTableUtils;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
//        String jarPath = System.getProperty("java.class.path");
        String jarPath = new File("src/main/resources/").getAbsolutePath();
        //实际文件目录
        String tableFilePath = jarPath.substring(0, jarPath.lastIndexOf("\\") + 1) + "script\\table\\";
        File f = new File(tableFilePath);
        if (!f.exists()) {//如果文件夹不存在
            //创建文件夹
            System.out.println("开始创建文件夹");
            while (true) {
                if (f.mkdirs()) {
                    System.out.println("创建文件夹成功");
                    break;
                }
            }
        }
        //建表
        generaterTableUtils.start(tableFilePath);

        //实际文件目录
        String procFilePath = jarPath.substring(0, jarPath.lastIndexOf("\\") + 1) + "script\\proc\\";
        File f1 = new File(procFilePath);
        if (!f1.exists()) {//如果文件夹不存在
            //创建文件夹
            System.out.println("开始创建文件夹");
            while (true) {
                if (f1.mkdirs()) {
                    System.out.println("创建文件夹成功");
                    break;
                }
            }
        }
        //建存储过程
        generaterProcUtils.generateProc(procFilePath);
    }
}


