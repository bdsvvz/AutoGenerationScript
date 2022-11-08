package com.lq.autogenerationscript;

import com.lq.autogenerationscript.utils.DbTypeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: liQing
 * @Date: 2022-10-24 14:49
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DbTypeUtilsTets {

    @Test
    public void Test() {
        System.out.println(DbTypeUtils.getDbType());
    }
}
