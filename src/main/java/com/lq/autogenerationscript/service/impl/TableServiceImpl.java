package com.lq.autogenerationscript.service.impl;

import com.lq.autogenerationscript.mapper.sqlScript.SqlScriptMapper;
import com.lq.autogenerationscript.mapper.tableScript.TableScriptMapper;
import com.lq.autogenerationscript.service.ITableService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liQing
 * @Date: 2022-10-29 15:31
 * @Version 1.0
 */
@Service
public class TableServiceImpl implements ITableService {

    @Resource
    private TableScriptMapper tableScriptMapper;

    @Override
    public Map getTableByPrefix(String prefix, int page, int size) {
        Map<String, Object> data = new HashMap<>(4);
        int offset = (page - 1) * size;
        Map<String, Object> params = new HashMap<>(4);
        params.put("prefix", prefix);
        params.put("offset", offset);
        params.put("size", size);
        data.put("data", tableScriptMapper.getTableList(params));
        data.put("total", tableScriptMapper.getTablesCount(prefix));
        return data;
    }
}
