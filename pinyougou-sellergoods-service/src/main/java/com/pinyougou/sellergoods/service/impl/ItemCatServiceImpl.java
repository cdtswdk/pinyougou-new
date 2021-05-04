package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ItemCatMapper;
import com.pinyougou.model.ItemCat;
import com.pinyougou.model.ItemCatExample;
import com.pinyougou.sellergoods.service.ItemCatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 返回ItemCat全部列表
     *
     * @return
     */
    @Override
    public List<ItemCat> getAll() {
        return itemCatMapper.selectAll();
    }


    /***
     * 分页返回ItemCat列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<ItemCat> getAll(ItemCat itemCat, int pageNum, int pageSize) {
        //执行分页
        PageHelper.startPage(pageNum, pageSize);

        //执行查询
        ItemCatExample example = new ItemCatExample();
        ItemCatExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(itemCat.getName())) {
            criteria.andNameLike("%" + itemCat.getName() + "%");
        }
        List<ItemCat> all = this.itemCatMapper.selectByExample(example);
        return new PageInfo<ItemCat>(all);
    }


    /***
     * 增加ItemCat信息
     * @param itemCat
     * @return
     */
    @Override
    public int add(ItemCat itemCat) {
        return itemCatMapper.insertSelective(itemCat);
    }


    /***
     * 根据ID查询ItemCat信息
     * @param id
     * @return
     */
    @Override
    public ItemCat getOneById(Long id) {
        return itemCatMapper.selectByPrimaryKey(id);
    }


    /***
     * 根据ID修改ItemCat信息
     * @param itemCat
     * @return
     */
    @Override
    public int updateItemCatById(ItemCat itemCat) {
        return itemCatMapper.updateByPrimaryKeySelective(itemCat);
    }


    /***
     * 根据ID批量删除ItemCat信息
     * @param ids
     * @return
     */
    @Override
    public int deleteByIds(List<Long> ids) {
        //创建Example，来构建根据ID删除数据
        Example example = new Example(ItemCat.class);
        Example.Criteria criteria = example.createCriteria();

        //所需的SQL语句类似 delete from tb_itemCat where id in(1,2,5,6)
        criteria.andIn("id", ids);
        return itemCatMapper.deleteByExample(example);
    }

    /***
     * 根据父ID查询所有子类
     * select * from tb_item_cat where parent_id=0;
     * @param id
     * @param page
     * @param size
     * @return
     */
    @Override
    public List<ItemCat> findByParentId(long id, int page, int size) {
        ItemCat itemCat = new ItemCat();
        itemCat.setParentId(id);

        //存入缓存
        List<ItemCat> itemCats = this.itemCatMapper.selectAll();
        for (ItemCat cat : itemCats) {
            this.redisTemplate.boundHashOps("ItemCat").put(cat.getName(), cat.getTypeId());
        }

        PageHelper.startPage(page, size);
        return itemCatMapper.select(itemCat);
    }
}
