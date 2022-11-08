package com.lq.autogenerationscript;


import com.lq.autogenerationscript.utils.GeneraterCProcUtils;
import com.lq.autogenerationscript.utils.GeneraterProcUtils;
import com.lq.autogenerationscript.utils.GeneraterTableUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * @Author: liQing
 * @Date: 2022-09-20 10:44
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ScriptTest {
    @Resource
    GeneraterProcUtils generaterProcUtils;

    @Resource
    GeneraterCProcUtils generaterCProcUtils;

    @Resource
    GeneraterTableUtils generaterTableUtils;

    @Test
    public void test() {
//        generaterTableUtils.getTableStructure(0,"mhis_biz_infusion");
//        generaterTableUtils.start(null);
//        generaterProcUtils.generateProc("E:\\java-project\\AutoGenerationScript\\target\\script\\proc\\");
//        GeneraterCProcUtils generaterCProcUtils = new GeneraterCProcUtils();
        //generaterCProcUtils.generateOracleProc("PROC_OP_CHANGE_AV_STORE_DRUG", "E:\\java-project\\AutoGenerationScript\\target\\script\\CTemplate\\");
        //生成建表语句
        generaterTableUtils.start("E:\\java-project\\AutoGenerationScript\\target\\script\\table\\");
    }
}
