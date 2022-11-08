package com.lq.autogenerationscript.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * oracle 主键实体类
 *
 * @Author: liQing
 * @Date: 2022-10-25 13:36
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class OracleSysPrimary {
    /**
     * 表空间
     */
    private String owner;
    /**
     * 主键名
     */
    private String constraint_name;
    /**
     * 表名
     */
    private String table_name;
    /**
     * 字段名
     */
    private String column_name;
}
