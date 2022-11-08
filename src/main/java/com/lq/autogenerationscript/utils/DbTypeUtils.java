package com.lq.autogenerationscript.utils;

import com.lq.autogenerationscript.config.CodeCache;

import java.util.Map;

/**
 * 获取数据方言
 *
 * @Author: liQing
 * @Date: 2022-10-24 14:35
 * @Version 1.0
 */
public class DbTypeUtils {
    public static final String ORACLE = "oracle";
    public static final String KEY = "dataSource";

    /**
     * @return
     */
    public static int getDbType() {
        Map<String, Object> dbMap = CodeCache.codeMap;
        if (dbMap.containsKey(KEY)) {
            if (ORACLE.equals(dbMap.get(KEY).toString())) {
                return 2;
            }
        }
        return 1;
    }
}
