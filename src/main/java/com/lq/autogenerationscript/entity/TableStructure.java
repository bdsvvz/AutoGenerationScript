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
public class TableStructure {
    /**
     * 唯一标识
     */
    private int object_id;
    /**
     * 字段类型
     */
    private String name;
    /**
     * 字段名称
     */
    private String column_name;
    /**
     * 字段大小
     */
    private int max_length;
    /**
     * 字段整数位（numeric 使用）
     */
    private int precision;
    /**
     * 字段小数位（numeric 使用）
     */
    private int scale;
    /**
     * 字段是否为null(0-否 ;1-是)
     */
    private int is_nullable;
    /**
     * 字段是否为标识
     */
    private int is_identity;
    /**
     * 字段默认值
     */
    private String definition;
    /**
     * 标识
     */
    private Object seed_value;
    /**
     * 标识
     */
    private Object increment_value;
}
