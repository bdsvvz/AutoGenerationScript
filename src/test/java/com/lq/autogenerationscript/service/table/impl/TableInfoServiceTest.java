package com.lq.autogenerationscript.service.table.impl;

import com.lq.autogenerationscript.mapper.table.TableMapper;
import com.lq.autogenerationscript.service.table.ITableInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @Author: liQing
 * @Date: 2023-12-28 11:18
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TableInfoServiceTest {

    @Resource
    private TableInfoService tableInfoService;

    @Test
    public void createTableInfo() throws Exception {
        tableInfoService.createTableInfo("mhis_biz_infusion");
    }
}