package org.example.test.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.common.ApiController;
import org.example.common.Result;
import org.example.test.entity.TransMessage;
import org.example.test.service.TransMessageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (TransMessage)表控制层
 *
 * @author makejava
 * @since 2023-06-27 12:29:33
 */
@RestController
@RequestMapping("transMessage")
public class TransMessageController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private TransMessageService transMessageService;

    /**
     * 分页查询所有数据
     *
     * @param page         分页对象
     * @param transMessage 查询实体
     * @return 所有数据
     */
    @GetMapping("/selectAll")
    public Result selectAll(Page<TransMessage> page, TransMessage transMessage) {
        return Result.success(this.transMessageService.page(page, new QueryWrapper<>(transMessage)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Result selectOne(@PathVariable Serializable id) {
        return Result.success(this.transMessageService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param transMessage 实体对象
     * @return 新增结果
     */
    @PostMapping("/insert")
    public Result insert(@RequestBody TransMessage transMessage) {
        return Result.success(this.transMessageService.save(transMessage));
    }

    /**
     * 修改数据
     *
     * @param transMessage 实体对象
     * @return 修改结果
     */
    @PutMapping("/update")
    public Result update(@RequestBody TransMessage transMessage) {
        return Result.success(this.transMessageService.updateById(transMessage));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @PostMapping("/delete")
    public Result delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.transMessageService.removeByIds(idList));
    }
}

