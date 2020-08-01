package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/item")
public class SearchController {

    @Reference
    private ItemSearchService itemSearchService;

    /**
     * 商品搜索
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "search",method = RequestMethod.POST)
    public Map<String,Object> search(@RequestBody(required = false) Map searchMap){
        return this.itemSearchService.search(searchMap);
    }
}
