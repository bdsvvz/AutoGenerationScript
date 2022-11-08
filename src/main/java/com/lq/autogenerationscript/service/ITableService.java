package com.lq.autogenerationscript.service;

import java.util.Map;

/**
 * @Author: liQing
 * @Date: 2022-10-29 15:31
 * @Version 1.0
 */
public interface ITableService {
    /**
     * 获取表集合
     * @param prefix 表的前缀模糊匹配
     * @param page 页码
     * @param size 每页大小
     * @return
     */
    Map getTableByPrefix(String prefix, int page, int size);
}
