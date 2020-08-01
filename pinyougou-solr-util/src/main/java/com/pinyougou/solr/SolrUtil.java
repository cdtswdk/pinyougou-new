package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 批量增加数据到索引库
     */
    public void batchAdd(){
        Item item = new Item();
        item.setStatus("1");
        List<Item> items = this.itemMapper.select(item);

        //设置动态域
        for (Item itemMap : items) {
            itemMap.setSpecMap(JSON.parseObject(itemMap.getSpec(), Map.class));
        }

        //导入数据到索引库
        this.solrTemplate.saveBeans(items);
        //提交
        this.solrTemplate.commit();
    }

    /**
     * 条件查询
     */
    public void SearchByCondition(String fieldName,String con){
        Query query = new SimpleQuery("*:*");

        //设置条件
        Criteria criteria = new Criteria("item_spec_"+fieldName).is(con);
        query.addCriteria(criteria);
        //设置分页
        query.setOffset(0);
        query.setRows(10);
        ScoredPage<Item> scoredPage = this.solrTemplate.queryForPage(query, Item.class);
        System.out.println("总记录数："+scoredPage.getTotalElements());
        List<Item> content = scoredPage.getContent();
        for (Item item : content) {
            System.out.println(item);
        }
    }
}
