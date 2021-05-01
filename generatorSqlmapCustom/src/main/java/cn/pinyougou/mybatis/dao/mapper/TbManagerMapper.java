package cn.pinyougou.mybatis.dao.mapper;

import cn.pinyougou.mybatis.po.TbManager;
import cn.pinyougou.mybatis.po.TbManagerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbManagerMapper {
    int countByExample(TbManagerExample example);

    int deleteByExample(TbManagerExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TbManager record);

    int insertSelective(TbManager record);

    List<TbManager> selectByExample(TbManagerExample example);

    TbManager selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TbManager record, @Param("example") TbManagerExample example);

    int updateByExample(@Param("record") TbManager record, @Param("example") TbManagerExample example);

    int updateByPrimaryKeySelective(TbManager record);

    int updateByPrimaryKey(TbManager record);
}