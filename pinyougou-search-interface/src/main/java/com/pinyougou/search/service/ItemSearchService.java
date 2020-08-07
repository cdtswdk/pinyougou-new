package com.pinyougou.search.service;

import com.pinyougou.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 搜索方法
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map<String,Object> searchMap);

    /**
     * 更新索引库
     * @param items
     */
    void importItems(List<Item> items);

    /**
     * 根据商品id删除item
     * @param ids
     */
    void deleteByGoodsIds(List<Long> ids);
}
