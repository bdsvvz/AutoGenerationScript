package com.lq.autogenerationscript.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表结构实体类定义
 *
 * @Author: liQing
 * @Date: 2022-09-23 15:26
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class OracleTableStructure {
    /**
     * 表字段名称
     */
    private String column_name;
    /**
     * 表字段类型
     */
    private String data_type;
    /**
     * 表字段长度
     */
    private int data_length;
    /**
     * 整数位数
     */
    private int data_precision;
    /**
     * 小数精度
     */
    private int data_scale;
    /**
     * 表字段是否允许为空
     */
    private int nullable;
    /**
     * 字段默认值
     */
    private Object data_default;
    /**
     * 字段是否是字符类型
     */
    private String character_set_name;
}
