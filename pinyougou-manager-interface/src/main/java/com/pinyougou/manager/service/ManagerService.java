package com.pinyougou.manager.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.model.TbManager;

import java.util.List;

public interface ManagerService {

    /**
     * 返回Manager全部列表
     *
     * @return
     */
    public List<TbManager> getAll();

    /***
     * 分页返回Manager列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<TbManager> getAll(TbManager manager, int pageNum, int pageSize);

    /***
     * 增加Manager信息
     * @param manager
     * @return
     */
    int add(TbManager manager);

    /***
     * 根据ID查询Manager信息
     * @param id
     * @return
     */
    TbManager getOneById(Long id);

    /***
     * 根据ID修改Manager信息
     * @param manager
     * @return
     */
    int updateManagerById(TbManager manager);

    /***
     * 根据ID批量删除Manager信息
     * @param ids
     * @return
     */
    int deleteByIds(List<Long> ids);

    /**
     * 根据manageId查找manager
     * @param manageId
     * @return
     */
    TbManager findByManagerId(Integer manageId);
}
