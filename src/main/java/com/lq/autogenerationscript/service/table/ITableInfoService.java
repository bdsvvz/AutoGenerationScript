package com.lq.autogenerationscript.service.table;

import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * @Author: liQing
 * @Date: 2023-12-28 11:03
 * @Version 1.0
 */
public interface ITableInfoService {
    /**
     * 获取表信息
     *
     * @param tableName 表名
     */
    void createTableInfo(String tableName) throws IOException, TemplateException;
}
