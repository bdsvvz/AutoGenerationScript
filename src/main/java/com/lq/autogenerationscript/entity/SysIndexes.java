package com.lq.autogenerationscript.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统索引表
 *
 * @Author: liQing
 * @Date: 2022-09-23 17:58
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class SysIndexes {
    /**
     * 索引唯一标识
     */
    private String object_id;
    /**
     * 索引名称
     */
    private String name;
    /**
     * 列名称
     */
    private String column_name;
    /**
     * 索引序号
     */
    private int index_id;
    /**
     * 索引类型（聚集和非聚集）
     */
    private int type;
    /**
     * 索引类型描述
     */
    private String type_desc;
    /**
     * 索引是否唯一
     */
    private int is_unique;
    /**
     * 索引是否主键
     */
    private int is_primary_key;
    /**
     * 索引排序方式是否desc
     */
    private int is_descending_key;
}
