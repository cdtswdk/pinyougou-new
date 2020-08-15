package com.pinyougou.page.service;

import java.io.IOException;
import java.util.List;

public interface ItemPageService {

    /**
     * 根据GoodsId生成静态页
     *
     * @param goodsId
     * @return
     */
    Boolean buildHtml(Long goodsId) throws Exception;

    /**
     * 根据商品id删除静态页
     * @param id
     */
    void deleteHtml(Long id);
}
