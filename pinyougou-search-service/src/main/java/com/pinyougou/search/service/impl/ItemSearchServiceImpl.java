package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.model.Item;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {

        //条件搜索Query
        Query query = new SimpleQuery("*:*");

        //如果searchMap为空，也就是没有传入任何条件，则搜索所有数据
        //如果不为空，则搜索对应关键词的数据
        if(searchMap!=null){
            //关键词的key是keyword
            String keyword = (String) searchMap.get("keyword");
            if(StringUtils.isNotBlank(keyword)){
                Criteria criteria = new Criteria("item_keywords").is(keyword);
                query.addCriteria(criteria);
            }
        }

        //分页
        query.setOffset(0);
        query.setRows(10);

        //分页查询
        ScoredPage<Item> scoredPage = this.solrTemplate.queryForPage(query, Item.class);

        //获取返回结果，封装到Map中
        Map<String,Object> dataMap = new HashMap<String,Object>();

        //总记录数
        long totalElements = scoredPage.getTotalElements();
        dataMap.put("total",totalElements);

        //数据
        List<Item> content = scoredPage.getContent();
        dataMap.put("rows",content);

        return dataMap;
    }
}
