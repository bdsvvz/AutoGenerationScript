package com.lq.autogenerationscript.controller;

import com.lq.autogenerationscript.service.ITableService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Map;

/**
 * @Author: liQing
 * @Date: 2022-10-29 15:27
 * @Version 1.0
 */
@RestController
@RequestMapping("/table")
@Validated
public class TableController {

    @Resource
    private ITableService tableService;

    /**
     * 根据前缀获取对应表的集合
     */
    @RequestMapping(value = "/getTableByPrefix", method = RequestMethod.GET)
    public Map getTableByPrefix(@RequestParam(name = "prefix") String prefix,
                                @RequestParam(name = "page") @Min(1) Integer page,
                                @RequestParam(name = "size") @Min(10) @Max(100) Integer size) {
        return tableService.getTableByPrefix(prefix, page, size);
    }
}
