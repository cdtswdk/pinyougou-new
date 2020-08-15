package com.pinyougou.test;

import com.itcast.model.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/spring-solr.xml")
public class solrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 测试增加
     */
    @Test
    public void testAdd() {
        Item item = new Item();
        item.setId(1L);
        item.setBrand("华为");
        item.setCategory("手机");
        item.setGoodsId(1L);
        item.setSeller("华为2号专卖店");
        item.setTitle("华为Mate9");
        item.setPrice(new BigDecimal(2000));

        this.solrTemplate.saveBean(item);
        this.solrTemplate.commit();
    }

    /**
     * 测试增加
     */
    @Test
    public void testBatchAdd() {
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < 10; i++) {
            Item item = new Item();
            item.setId((long) i);
            item.setBrand("华为" + (int) (Math.random() * 100));
            item.setCategory("手机" + (int) (Math.random() * 100));
            item.setGoodsId(1L);
            item.setSeller("华为2号专卖店" + (int) (Math.random() * 100));
            item.setTitle("华为Mate9" + (int) (Math.random() * 100));
            item.setPrice(new BigDecimal(2000));
            items.add(item);
        }


        this.solrTemplate.saveBeans(items);
        this.solrTemplate.commit();
    }

    /**
     * 根据ID查询
     */
    @Test
    public void testGetById() {
        Item item = this.solrTemplate.getById("1", Item.class);
        System.out.println(item);
    }

    /**
     * 根据主键删除
     */
    @Test
    public void testDeleteById() {
        this.solrTemplate.deleteById("1369364");
        this.solrTemplate.commit();
    }

    /**
     * 分页查询
     */
    @Test
    public void testSearchByPage() {
        Query query = new SimpleQuery("*:*");
        //设置分页
        query.setOffset(5);
        query.setRows(7);
        ScoredPage<Item> scoredPage = this.solrTemplate.queryForPage(query, Item.class);
        System.out.println("总记录数：" + scoredPage.getTotalElements());
        List<Item> content = scoredPage.getContent();
        for (Item item : content) {
            System.out.println(item);
        }
    }

    /**
     * 条件查询
     */
    @Test
    public void testSearchByCondition() {
        Query query = new SimpleQuery("*:*");

        //设置条件
        Criteria criteria = new Criteria("item_title").contains("华为");
        query.addCriteria(criteria);
        //设置分页
        query.setOffset(0);
        query.setRows(10);
        ScoredPage<Item> scoredPage = this.solrTemplate.queryForPage(query, Item.class);
        System.out.println("总记录数：" + scoredPage.getTotalElements());
        List<Item> content = scoredPage.getContent();
        for (Item item : content) {
            System.out.println(item);
        }
    }

    /**
     * 删除全部
     */
    @Test
    public void deleteAll() {
        Query query = new SimpleQuery("*:*");
        this.solrTemplate.delete(query);
        this.solrTemplate.commit();
    }
}
