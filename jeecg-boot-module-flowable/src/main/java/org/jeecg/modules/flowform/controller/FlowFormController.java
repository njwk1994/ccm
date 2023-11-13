package org.jeecg.modules.flowform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.flowform.entity.FormInfo;
import org.jeecg.modules.flowform.model.DicItemModel;
import org.jeecg.modules.flowform.model.FormInfoModel;
import org.jeecg.modules.flowform.model.TreeModel;
import org.jeecg.modules.flowform.service.IFormInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "表单设计")
@RestController
@RequestMapping("/flowable/flowForm")
@Slf4j
public class FlowFormController extends JeecgController<FormInfo, IFormInfoService> {

    @Autowired
    private IFormInfoService formInfoService;

    @ApiOperation(value = "分页列表查询", notes = "分页列表查询")
    @GetMapping(value = "/queryDicTree")
    public Result<?> queryDicTree(DicItemModel dicItemModel, HttpServletRequest req) {
        try {
            QueryWrapper<DicItemModel> queryWrapper = QueryGenerator.initQueryWrapper(dicItemModel, req.getParameterMap());
            List<TreeModel> result = formInfoService.queryDicTree(queryWrapper);
            return Result.OK(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation(value = "分页列表查询", notes = "分页列表查询")
    @GetMapping(value = "/queryPageList")
    public Result<?> queryPageList(FormInfoModel formInfoModel, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        try {
            QueryWrapper<FormInfoModel> queryWrapper = QueryGenerator.initQueryWrapper(formInfoModel, req.getParameterMap());
            Page<FormInfoModel> page = new Page<>(pageNo, pageSize);
            IPage<FormInfoModel> pageList = formInfoService.queryPageList(page, queryWrapper);
            return Result.OK(pageList);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
