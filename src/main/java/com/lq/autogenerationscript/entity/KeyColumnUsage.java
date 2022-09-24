package com.lq.autogenerationscript.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表主键对象
 *
 * @Author: liQing
 * @Date: 2022-09-23 16:30
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class KeyColumnUsage {
    private String constraint_catalog;
    private String constraint_schema;
    private String constraint_name;
    private String table_catalog;
    private String table_schema;
    private String table_name;
    private String column_name;
    private int ordinal_position;
}
