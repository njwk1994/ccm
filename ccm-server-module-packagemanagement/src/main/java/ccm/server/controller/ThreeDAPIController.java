package ccm.server.controller;

import ccm.server.business.CCMThreeDAPIService;
import ccm.server.module.vo.ResultVo;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/7/12 11:09
 */
@Slf4j
@RestController
@Api(tags = "3D服务接口管理")
@RequestMapping("/ccm/3D")
public class ThreeDAPIController {

    @Autowired
    private CCMThreeDAPIService threeDAPIService;

    @ApiOperation("根据时间范围获取对应工作包施工阶段与步骤已完成的设计数据")
    @PostMapping(value = "/getFinishedDesignByDate")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "wpPurpose", value = "工作包施工阶段", example = "EN_WPCutting", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "ropWorkStepName", value = "工作步骤", example = "EN_WS_Cutting", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "startDate", value = "完成日期开始时间", example = "2022-01-01 00:00:00", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "endDate", value = "完成日期结束时间", example = "2022-01-31 23:59:59", required = true, dataTypeClass = String.class),
    }))
    public String getFinishedDesignByDate(@RequestBody JSONObject jsonObject) {
        ResultVo<List<Map<String, String>>> result = new ResultVo<>();
        try {
            String wpPurpose = jsonObject.getString("wpPurpose");
            String ropWorkStepName = jsonObject.getString("ropWorkStepName");
            String startDate = jsonObject.getString("startDate");
            String endDate = jsonObject.getString("endDate");

            List<Map<String, String>> finishedDesignByDate = threeDAPIService.getFinishedDesignByDate(wpPurpose, ropWorkStepName, startDate, endDate);
            result.successResult(finishedDesignByDate);
        } catch (Exception exception) {
            log.error("根据时间范围获取对应工作包施工阶段与步骤已完成的设计数据失败!错误信息:{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("根据时间范围获取对应工作包施工阶段与步骤已完成的设计数据失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取对应施工阶段和工作步骤下日期最大值和最小值")
    @PostMapping(value = "/getMaxAndMinDate")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "wpPurpose", value = "工作包施工阶段", example = "EN_WPCutting", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "ropWorkStepName", value = "工作步骤", example = "EN_WS_Cutting", required = true, dataTypeClass = String.class),
    }))
    public String getMaxAndMinDate(@RequestBody JSONObject jsonObject) {
        ResultVo<Map<String, Object>> result = new ResultVo<>();
        try {
            String wpPurpose = jsonObject.getString("wpPurpose");
            String ropWorkStepName = jsonObject.getString("ropWorkStepName");

            Map<String, Object> maxAndMinDate = threeDAPIService.getMaxAndMinDate(wpPurpose, ropWorkStepName);
            result.successResult(maxAndMinDate);
        } catch (Exception exception) {
            log.error("获取对应施工阶段和工作步骤下日期最大值和最小值失败!错误信息:{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("获取对应施工阶段和工作步骤下日期最大值和最小值失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

}
