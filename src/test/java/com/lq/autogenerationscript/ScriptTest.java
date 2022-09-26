package com.lq.autogenerationscript;


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
    GeneraterTableUtils generaterTableUtils;

    @Test
    public void test() {
//        generaterTableUtils.getTableStructure(0,"mhis_biz_infusion");
        generaterTableUtils.start(null);

    }
}
