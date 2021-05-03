package com.pinyougou.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.manager.service.ManagerService;
import com.pinyougou.mapper.TbManagerMapper;
import com.pinyougou.model.TbManager;
import com.pinyougou.model.TbManagerExample;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Auther: chendongtao
 * @Date: 2021/5/2 9:00
 * @Description:
 */
@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private TbManagerMapper managerMapper;

    @Override
    public List<TbManager> getAll() {
        return this.managerMapper.selectAll();
    }

    @Override
    public PageInfo<TbManager> getAll(TbManager manager, int pageNum, int pageSize) {

        //执行分页
        PageHelper.startPage(pageNum, pageSize);

        //执行查询
        List<TbManager> select = this.managerMapper.select(manager);

        return new PageInfo<>(select);
    }

    @Override
    public int add(TbManager manager) {
        return this.managerMapper.insertSelective(manager);
    }

    @Override
    public TbManager getOneById(Long id) {
        return this.managerMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateManagerById(TbManager manager) {
        return this.managerMapper.updateByPrimaryKey(manager);
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        return 0;
    }

    @Override
    public TbManager findByManagerId(Integer manageId) {

        try {
            TbManagerExample example = new TbManagerExample();
            TbManagerExample.Criteria criteria = example.createCriteria();
            if (manageId != null) {
                criteria.andManagerIdEqualTo(manageId);
            }

            List<TbManager> list = this.managerMapper.selectByExample(example);
            if (CollectionUtils.isNotEmpty(list)) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
