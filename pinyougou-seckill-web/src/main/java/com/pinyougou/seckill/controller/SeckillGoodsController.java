package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.model.SeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/seckill/goods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    @RequestMapping(value = "/list")
    public List<SeckillGoods> list() {
        return this.seckillGoodsService.list();
    }

    @RequestMapping(value = "/one")
    public SeckillGoods getOne(Long id) {
        return this.seckillGoodsService.getOne(id);
    }
}
