package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.model.Item;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {

        //高亮配置 把Query换成HighlightQuery
        HighlightQuery highlightQuery = new SimpleHighlightQuery(new SimpleStringCriteria("*:*"));

        //高亮域设置
        highlightSettings(highlightQuery);

        //如果searchMap为空，也就是没有传入任何条件，则搜索所有数据
        //如果不为空，则搜索对应关键词的数据
        if (searchMap != null) {
            //关键词的key是keyword
            String keyword = (String) searchMap.get("keyword");
            if (StringUtils.isNotBlank(keyword)) {
                Criteria criteria = new Criteria("item_keywords").is(keyword.replace(" ", ""));
                highlightQuery.addCriteria(criteria);
            }
            //分类过滤
            String category = (String) searchMap.get("category");
            if (StringUtils.isNotBlank(category)) {
                Criteria criteria = new Criteria("item_category").is(category);
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                highlightQuery.addFilterQuery(filterQuery);
            }
            //品牌过滤
            String brand = (String) searchMap.get("brand");
            if (StringUtils.isNotBlank(brand)) {
                Criteria criteria = new Criteria("item_brand").is(brand);
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                highlightQuery.addFilterQuery(filterQuery);
            }

            //规格过滤
            Object spec = searchMap.get("spec");
            if (spec != null) {
                Map<String, String> specMap = JSON.parseObject(spec.toString(), Map.class);
                for (Map.Entry<String, String> entry : specMap.entrySet()) {
                    //获取键
                    String key = entry.getKey();
                    //获取值
                    String value = entry.getValue();
                    Criteria criteria = new Criteria("item_spec_" + key).is(value);
                    FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                    highlightQuery.addFilterQuery(filterQuery);
                }
            }

            //价格区间过滤
            String price = (String) searchMap.get("price");
            if (StringUtils.isNotBlank(price)) {
                //分割价格区间
                String[] split = price.split("-");
                //创建criteria对象用于封装匹配条件
                Criteria criteria = null;
                //如果不包含*号，则直接按区间查询
                if (!price.contains("*")) {
                    criteria = new Criteria("item_price").
                            between(Long.parseLong(split[0]), Long.parseLong(split[1]), true, false);
                } else {
                    //包含*号则按照>x执行
                    criteria = new Criteria("item_price").greaterThan(Long.parseLong(split[0]));
                }
                //创建过滤搜索对象
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                //加入到query中
                highlightQuery.addFilterQuery(filterQuery);
            }

            //分页
            Integer pageNum = (Integer) searchMap.get("pageNum");
            Integer size = (Integer) searchMap.get("size");

            //防止空数据
            if (pageNum == null) {
                pageNum = 1;
            }
            if (size == null) {
                size = 10;
            }
            highlightQuery.setOffset((pageNum - 1) * size);
            highlightQuery.setRows(size);

            //排序搜索
            String sortType = (String) searchMap.get("sortType");
            String sortField = (String) searchMap.get("sortField");

            if (StringUtils.isNotBlank(sortType) && StringUtils.isNotBlank(sortField)) {
                Sort orders = null;
                if ("asc".equalsIgnoreCase(sortType)) {
                    //升序
                    orders = new Sort(Sort.Direction.ASC, sortField);
                } else {
                    //降序
                    orders = new Sort(Sort.Direction.DESC, sortField);
                }
                highlightQuery.addSort(orders);
            }
        }

        //分页查询
        HighlightPage<Item> highlightPage = this.solrTemplate.queryForHighlightPage(highlightQuery, Item.class);
        //高亮数据替换
        highlightReplace(highlightPage);

        //获取返回结果，封装到Map中
        Map<String, Object> dataMap = new HashMap<String, Object>();

        //查找分类数据
        List<String> categoryList = searchCategory(highlightQuery);

        //当用户选择了分类的时候，则根据分类检索规格和品牌
        String category = (String) searchMap.get("category");
        if (StringUtils.isNotBlank(category)) {
            dataMap.putAll(findBrandAndSpec(category));
        } else {
            //如果没有选中分类，则根据第一个分类查询品牌和规格信息
            if (categoryList != null && categoryList.size() > 0) {
                dataMap.putAll(findBrandAndSpec(categoryList.get(0)));
            }
        }

        //总记录数
        long totalElements = highlightPage.getTotalElements();
        dataMap.put("total", totalElements);

        //数据
        List<Item> content = highlightPage.getContent();
        dataMap.put("rows", content);

        //分类数据集合
        dataMap.put("categoryList", categoryList);

        //分页数据
        //dataMap.put("pageNum",pageNum);

        return dataMap;
    }

    @Override
    public void importItems(List<Item> items) {
        this.solrTemplate.saveBeans(items);
        this.solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List<Long> ids) {
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        Query query = new SimpleQuery(criteria);
        this.solrTemplate.delete(query);
        this.solrTemplate.commit();
    }

    /**
     * 查找分类数据
     *
     * @param highlightQuery
     * @return
     */
    public List<String> searchCategory(HighlightQuery highlightQuery) {

        //重置分页
        highlightQuery.setOffset(0);
        highlightQuery.setRows(100);

        //分类分组查询
        GroupOptions groupOptions = new GroupOptions();
        //分组域
        groupOptions.addGroupByField("item_category");
        //设置查询条件
        highlightQuery.setGroupOptions(groupOptions);
        //执行查询
        GroupPage<Item> groupPage = this.solrTemplate.queryForGroupPage(highlightQuery, Item.class);
        GroupResult<Item> itemCategory = groupPage.getGroupResult("item_category");
        //存储分类数据集合
        List<String> categoryList = new ArrayList<>();
        for (GroupEntry<Item> groupEntry : itemCategory.getGroupEntries()) {
            String groupValue = groupEntry.getGroupValue();
            categoryList.add(groupValue);
        }
        return categoryList;
    }

    /**
     * 根据分类名称查找模板id，再根据模板id查找品牌和规格信息
     *
     * @param category
     * @return
     */
    public Map<String, Object> findBrandAndSpec(String category) {

        Map<String, Object> dataMap = new HashMap<>();

        //获得模板ID
        Long typeId = (Long) this.redisTemplate.boundHashOps("ItemCat").get(category);
        if (typeId != null) {
            List<Map> brandList = (List<Map>) this.redisTemplate.boundHashOps("brandList").get(typeId);
            List<Map> specList = (List<Map>) this.redisTemplate.boundHashOps("specList").get(typeId);
            dataMap.put("brandList", brandList);
            dataMap.put("specList", specList);
        }
        return dataMap;
    }


    /**
     * 高亮数据替换
     *
     * @param highlightPage
     */
    public void highlightReplace(HighlightPage<Item> highlightPage) {
        //获取高亮数据和非高亮数据的集合
        List<HighlightEntry<Item>> highlighted = highlightPage.getHighlighted();
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            //获取SKU信息，非高亮数据
            Item item = itemHighlightEntry.getEntity();
            //高亮数据
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
            if (highlights != null && highlights.size() > 0 && highlights.get(0).getSnipplets() != null && highlights.get(0).getSnipplets().size() > 0) {
                String snipplet = highlights.get(0).getSnipplets().get(0);
                item.setTitle(snipplet);
            }
        }
    }

    /**
     * 高亮域设置
     *
     * @param highlightQuery
     */
    public void highlightSettings(HighlightQuery highlightQuery) {
        //配置高亮选项
        HighlightOptions highlightOptions = new HighlightOptions();
        //高亮域
        highlightOptions.addField("item_title");
        //前缀
        highlightOptions.setSimplePrefix("<span style=\"color:red;\">");
        //后缀
        highlightOptions.setSimplePostfix("</span>");

        //设置高亮选项
        highlightQuery.setHighlightOptions(highlightOptions);
    }

    /*@Override
    public Map<String, Object> searchNoHl(Map<String, Object> searchMap) {

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
    }*/
}
