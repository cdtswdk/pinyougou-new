package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.http.Result;
import com.pinyougou.model.ContentCategory;
import com.pinyougou.sellergoods.service.ContentCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/contentCategory")
public class ContentCategoryController {

    @Reference
    private ContentCategoryService contentCategoryService;


    /***
     * 根据ID批量删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/delete")
    public Result delete(@RequestBody List<Long> ids) {
        try {
            //根据ID删除数据
            int dcount = contentCategoryService.deleteByIds(ids);

            if (dcount > 0) {
                return new Result(true, "删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "删除失败");
    }

    /***
     * 修改信息
     * @param contentCategory
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result modify(@RequestBody ContentCategory contentCategory) {
        try {
            //根据ID修改ContentCategory信息
            int mcount = contentCategoryService.updateContentCategoryById(contentCategory);
            if (mcount > 0) {
                return new Result(true, "修改成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "修改失败");
    }

    /***
     * 根据ID查询ContentCategory信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ContentCategory getById(@PathVariable(value = "id") long id) {
        //根据ID查询ContentCategory信息
        ContentCategory contentCategory = contentCategoryService.getOneById(id);
        return contentCategory;
    }


    /***
     * 增加ContentCategory数据
     * @param contentCategory
     * 响应数据：success
     *                  true:成功  false：失败
     *           message
     *                  响应的消息
     *
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody ContentCategory contentCategory) {
        try {
            //执行增加
            int acount = contentCategoryService.add(contentCategory);

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
    public PageInfo<ContentCategory> list(@RequestBody ContentCategory contentCategory, @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                          @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return contentCategoryService.getAll(contentCategory, page, size);
    }


    /***
     * 查询所有
     * 获取JSON数据
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<ContentCategory> list() {
        return contentCategoryService.getAll();
    }
}
