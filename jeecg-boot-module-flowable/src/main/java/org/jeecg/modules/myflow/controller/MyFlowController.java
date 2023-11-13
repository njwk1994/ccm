package org.jeecg.modules.myflow.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.myflow.entity.MyFlow;
import org.jeecg.modules.myflow.service.IMyFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 测试用户表
 * @Author: jeecg-boot
 * @Date: 2021-11-30
 * @Version: V1.0
 */
@Api(tags = "业务流程")
@RestController
@RequestMapping("/flowable/myflow")
@Slf4j
public class MyFlowController extends JeecgController<MyFlow, IMyFlowService> {

    @Autowired
    private IMyFlowService myFlowService;

    @ApiOperation("下发创建流程实例")
    @RequestMapping(value = "/createProcess", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {@DynamicParameter(name = "dataId", value = "dataId", required = true, dataTypeClass = String.class), @DynamicParameter(name = "processDef", value = "processDef", required = true, dataTypeClass = String.class)}))
    public String relationAct(@RequestBody JSONObject requestBody) {
        String dataId = requestBody.getString("dataId");
        String processDef = requestBody.getString("processDef");
        return myFlowService.createBusinessProcess(dataId, processDef);
    }

    @ApiOperation("获取审批节点的用户按钮权限")
    @RequestMapping(value = "/getPresentTaskNodeName", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {@DynamicParameter(name = "dataId", value = "dataId", required = true, dataTypeClass = String.class)}))
    public String getPresentTaskNodeName(@RequestBody JSONObject requestBody) {
        String dataId = requestBody.getString("dataId");
        return myFlowService.getPresentTaskNodeName(dataId);
    }
}
