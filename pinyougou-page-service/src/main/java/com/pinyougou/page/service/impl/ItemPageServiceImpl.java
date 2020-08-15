package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.GoodsDescMapper;
import com.pinyougou.mapper.GoodsMapper;
import com.pinyougou.mapper.ItemCatMapper;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.model.Goods;
import com.pinyougou.model.GoodsDesc;
import com.pinyougou.model.Item;
import com.pinyougou.page.service.ItemPageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import tk.mybatis.mapper.entity.Example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private FreeMarkerConfigurationFactoryBean factoryBean;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;

    @Value("${ITEM_PATH}")
    private String ITEM_PATH;

    @Value("${ITEM_SUFFIX}")
    private String ITEM_SUFFIX;

    @Override
    public Boolean buildHtml(Long goodsId) throws Exception {
        //查询数据
        Goods goods = this.goodsMapper.selectByPrimaryKey(goodsId);
        GoodsDesc goodsDesc = this.goodsDescMapper.selectByPrimaryKey(goodsId);
        List<Item> items = skuList(goodsId);

        //要封装的数据
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("goods", goods);
        dataMap.put("goodsDesc", goodsDesc);
        dataMap.put("items", items);
        //分类查询
        dataMap.put("itemCat1",this.itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()));
        dataMap.put("itemCat2",this.itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()));
        dataMap.put("itemCat3",this.itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()));

        //通过Freemarker生成Html
        Configuration configuration = this.factoryBean.createConfiguration();

        //创建模板对象
        Template template = configuration.getTemplate("item.ftl");
        //准备输出
        //FileWriter writer = new FileWriter(new File("C:/Users/Administrator/Desktop/pinyougou/pinyougou-page-service/src/main/webapp/" + goodsId + ".html"));
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ITEM_PATH + goodsId + ITEM_SUFFIX), StandardCharsets.UTF_8));

        //合成输出文件
        template.process(dataMap, writer);

        //关闭资源
        writer.flush();
        writer.close();
        return true;
    }

    @Override
    public void deleteHtml(Long id) {
        //创建一个要删除的文件对象
        File file = new File(ITEM_PATH + id + ITEM_SUFFIX);
        if(file.exists()){
            //文件存在则删除
            file.delete();
        }
    }

    /**
     * 查找sku
     *
     * @param goodsId
     * @return
     */
    public List<Item> skuList(Long goodsId) {
        Example example = new Example(Item.class);
        Example.Criteria criteria = example.createCriteria();
        //商品状态
        criteria.andEqualTo("status", "1");
        //商品id
        criteria.andEqualTo("goodsId", goodsId);

        //排序
        //example.setOrderByClause("is_default desc");
        example.orderBy("isDefault").desc();

        return this.itemMapper.selectByExample(example);
    }
}
