package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.http.Result;
import com.pinyougou.manager.mq.MessageSender;
import com.pinyougou.model.Goods;
import com.pinyougou.model.Item;
import com.pinyougou.mq.MessageInfo;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.jms.Destination;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination destination;

    @Autowired
    private MessageSender messageSender;

    /****
     * http://localhost:8080/goods/changeStatus/xiaohong/1.shtml  审核通过
     * http://localhost:8080/goods/changeStatus/xiaohong/2.shtml  审核不通过
     * http://localhost:8080/goods/changeStatus/xiaohong/3.shtml  关闭
     * @return
     */
    @RequestMapping(value = "/changeStatus/{goodsId}/{sta}")
    public Result changeStatus(@PathVariable(value = "goodsId") String goodsId,
                               @PathVariable(value = "sta") String status) {
        try {
            //修改
            int mcount = goodsService.changeStatus(goodsId, status);
            if (mcount > 0) {
                //判断商品是否审核通过
                if ("1".equals(status)) {
                    //查找商品
                    List<Long> ids = new ArrayList<>();
                    ids.add(Long.valueOf(goodsId));
                    List<Item> items = this.goodsService.findItemListByGoodsIdAndStatus(ids, status);
                    //向ActiveMQ发送消息-订阅消息
                    MessageInfo messageInfo = new MessageInfo(MessageInfo.METHOD_UPDATE, items);
                    this.messageSender.sendMessage(messageInfo);
                }
                //修改成功
                return new Result(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "修改状态失败！");
    }


    /**
     * 审核操作
     *
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping(value = "/update/status")
    public Result updateStatus(@RequestBody List<Long> ids, String status) {
        try {
            int mcount = goodsService.updateStatus(ids, status);
            if (mcount > 0) {
                //判断商品是否审核通过
                if ("1".equals(status)) {
                    //查找商品
                    List<Item> items = this.goodsService.findItemListByGoodsIdAndStatus(ids, status);
                    /*//更新索引库
                    this.itemSearchService.importItems(items);

                    //审核通过，生成静态页
                    for (Long id : ids) {
                        this.itemPageService.buildHtml(id);
                    }
                    */
                    //向ActiveMQ发送消息-订阅消息
                    MessageInfo messageInfo = new MessageInfo(MessageInfo.METHOD_UPDATE, items);
                    this.messageSender.sendMessage(messageInfo);
                }
                return new Result(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "修改状态失败！");
    }

    /***
     * 根据ID批量删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/delete")
    public Result delete(@RequestBody List<Long> ids) {
        try {
            //根据ID删除数据
            int dcount = goodsService.deleteByIds(ids);

            if (dcount > 0) {
                //删除索引
                //this.itemSearchService.deleteByGoodsIds(ids);

                MessageInfo messageInfo = new MessageInfo(MessageInfo.METHOD_DELETE, ids);
                this.messageSender.sendMessage(messageInfo);

                return new Result(true, "删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "删除失败");
    }

    /***
     * 修改信息
     * @param goods
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result modify(@RequestBody Goods goods) {
        try {
            //根据ID修改Goods信息
            int mcount = goodsService.updateGoodsById(goods);
            if (mcount > 0) {
                return new Result(true, "修改成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "修改失败");
    }

    /***
     * 根据ID查询Goods信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Goods getById(@PathVariable(value = "id") long id) {
        //根据ID查询Goods信息
        Goods goods = goodsService.getOneById(id);
        return goods;
    }


    /***
     * 增加Goods数据
     * @param goods
     * 响应数据：success
     *                  true:成功  false：失败
     *           message
     *                  响应的消息
     *
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody Goods goods) {
        try {
            //执行增加
            int acount = goodsService.add(goods);

            if (acount > 0) {
                //增加成功
                return new Result(true, "增加成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "增加失败");
    }


    /***
     * 分页查询数据
     * 获取JSON数据
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public PageInfo<Goods> list(@RequestBody Goods goods, @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return goodsService.getAll(goods, page, size);
    }


    /***
     * 查询所有
     * 获取JSON数据
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Goods> list() {
        return goodsService.getAll();
    }
}
