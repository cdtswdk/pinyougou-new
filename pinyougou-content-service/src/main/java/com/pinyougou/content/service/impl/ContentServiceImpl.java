package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.ContentCategoryMapper;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.model.Content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 返回Content全部列表
     *
     * @return
     */
    @Override
    public List<Content> getAll() {
        return contentMapper.selectAll();
    }


    /***
     * 分页返回Content列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<Content> getAll(Content content, int pageNum, int pageSize) {
        //执行分页
        PageHelper.startPage(pageNum, pageSize);

        //执行查询
        List<Content> all = contentMapper.select(content);
        PageInfo<Content> pageInfo = new PageInfo<Content>(all);
        return pageInfo;
    }


    /***
     * 增加Content信息
     * @param content
     * @return
     */
    @Override
    public int add(Content content) {
        int acount = contentMapper.insertSelective(content);
        if (acount > 0) {
            this.redisTemplate.boundHashOps("Content").delete(content.getCategoryId());
        }
        return acount;
    }


    /***
     * 根据ID查询Content信息
     * @param id
     * @return
     */
    @Override
    public Content getOneById(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }


    /***
     * 根据ID修改Content信息
     * @param content
     * @return
     */
    @Override
    public int updateContentById(Content content) {
        //查询该广告原来所属分类
        Content oldContent = this.contentMapper.selectByPrimaryKey(content.getId());
        //执行修改
        int mcount = contentMapper.updateByPrimaryKeySelective(content);
        if (mcount > 0) {
            this.redisTemplate.boundHashOps("Content").delete(oldContent.getCategoryId());
        }
        if (oldContent.getCategoryId().longValue() != content.getCategoryId().longValue()) {
            this.redisTemplate.boundHashOps("Content").delete(content.getCategoryId());
        }
        return mcount;
    }

    /***
     * 根据ID批量删除Content信息
     * @param ids
     * @return
     */
    @Override
    public int deleteByIds(List<Long> ids) {
        //查询原来的数据
        Example example1 = new Example(Content.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andIn("id", ids);
        List<Content> contents = this.contentMapper.selectByExample(example1);

        //创建Example，来构建根据ID删除数据
        Example example = new Example(Content.class);
        Example.Criteria criteria = example.createCriteria();

        //所需的SQL语句类似 delete from tb_content where id in(1,2,5,6)
        criteria.andIn("id", ids);
        int dcount = this.contentMapper.deleteByExample(example);
        if (dcount > 0) {
            for (Content content : contents) {
                this.redisTemplate.boundHashOps("Content").delete(content.getCategoryId());
            }
        }
        return dcount;
    }

    @Override
    public List<Content> findByCategoryId(long categoryId) {
        //判断当前缓存是否存在
        Object result = this.redisTemplate.boundHashOps("Content").get(categoryId);
        if (result != null) {
            return (List<Content>) result;
        }

        Example example = new Example(Content.class);
        Example.Criteria criteria = example.createCriteria();
        //设置分类ID
        criteria.andEqualTo("categoryId", categoryId);
        //状态
        criteria.andEqualTo("status", "1");
        //排序
        example.orderBy("sortOrder").asc();
        List<Content> contents = this.contentMapper.selectByExample(example);
        //第一次查询，存入redis
        if (contents != null && contents.size() > 0) {
            this.redisTemplate.boundHashOps("Content").put(categoryId, contents);
        }
        return contents;
    }
}
