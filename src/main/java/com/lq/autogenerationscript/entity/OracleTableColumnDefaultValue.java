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
public class OracleTableColumnDefaultValue {
    /**
     * 字段默认值
     */
    private String data_default;

}
