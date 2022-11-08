package com.lq.autogenerationscript.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * oracle 索引实体类
 *
 * @Author: liQing
 * @Date: 2022-10-25 13:36
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class OracleSysIndexes {
    /**
     * 表空间
     */
    private String table_owner;
    /**
     * 索引名称
     */
    private String index_name;
    /**
     * 表名
     */
    private String table_name;
    /**
     * 字段名称
     */
    private String column_name;
    /**
     * 排序
     */
    private String descend;

}
