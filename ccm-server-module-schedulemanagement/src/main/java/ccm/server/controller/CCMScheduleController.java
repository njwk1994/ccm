package ccm.server.controller;

import ccm.server.business.ICCMSchedulePolicyService;
import ccm.server.business.ICCMScheduleService;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.model.LoaderReport;
import ccm.server.module.vo.ResultVo;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.util.CommonUtility;
import ccm.server.utils.ObjectDTOUtility;
import ccm.server.utils.PageUtility;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/1/11 15:32
 */
@Slf4j
@RestController
@Api(tags = "计划管理")
@RequestMapping("/ccm/schedule")
public class CCMScheduleController {

    @Autowired
    private ICCMScheduleService scheduleService;
    @Autowired
    private ICCMSchedulePolicyService schedulePolicyService;

    @ApiOperation(value = "获取计划管理表单", notes = "获取计划管理表单")
    @PostMapping("/getScheduleForm")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String getScheduleForm(@RequestBody JSONObject requestBody) {
        ResultVo<ObjectDTO> result = new ResultVo<ObjectDTO>();
        try {
            String formPurpose = CommonUtility.getFormPurpose(requestBody);
            ObjectDTO items = this.scheduleService.getScheduleForm(formPurpose);
            result.successResult(items);
        } catch (Exception exception) {
            result.errorResult("获取计划管理表单失败," + ExceptionUtil.getSimpleMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "下载计划Excel模版", httpMethod = "GET", notes = "下载计划Excel模版", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @GetMapping("/downloadScheduleExcelTemplate")
    public String downloadScheduleExcelTemplate(HttpServletRequest request, HttpServletResponse response) {
        ResultVo<Object> result = new ResultVo<>();
        try {
            this.scheduleService.downloadScheduleExcelTemplate(response);
        } catch (Exception e) {
            log.error("下载计划模版失败!" + ExceptionUtil.getSimpleMessage(e));
            result.errorResult("下载计划模版失败!" + ExceptionUtil.getSimpleMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "导入计划Excel数据")
    @PostMapping("/importScheduleByExcel")
    public String importScheduleByExcel(@ApiParam(name = "importExcel", value = "importExcel", required = true) MultipartFile importExcel) {
        ResultVo<Object> result = new ResultVo<>();
        try {
            LoaderReport importScheduleByExcel = scheduleService.importScheduleByExcelTemplate(importExcel);
            result.successResult(true);
        } catch (Exception e) {
            log.error("导入计划Excel数据失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("导入计划Excel数据失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取所有计划数据")
    @PostMapping("/getAllSchedules")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "page", value = "分页参数", example = "", required = true, dataTypeClass = PageRequest.class)}))
    public String getAllSchedules(@RequestBody JSONObject requestBody) {
        ResultVo<List<ObjectDTO>> result = new ResultVo<List<ObjectDTO>>();
        try {
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(requestBody);
            IObjectCollection schedulesByItems = this.scheduleService.getSchedulesByItems(new ObjectDTO(), pageRequest);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(schedulesByItems);
            result.successResult(objectDTOS);
        } catch (Exception exception) {
            result.errorResult(ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("添加计划")
    @PostMapping(value = "/createSchedule")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String createSchedule(@RequestBody JSONObject requestBody) {
        ResultVo<String> result = new ResultVo<String>();
        try {
            IObject schedule = this.scheduleService.createSchedule(CommonUtility.parseObjectDTOFromJSON(requestBody));
            result.successResult(schedule.OBID());
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            result.errorResult(ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("更新计划")
    @PostMapping(value = "/updateSchedule")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String updateSchedule(@RequestBody JSONObject requestBody) {
        ResultVo<String> result = new ResultVo<String>();
        try {
            this.scheduleService.updateSchedule(CommonUtility.parseObjectDTOFromJSON(requestBody));
            result.successResult("");
        } catch (Exception exception) {
            log.error("更新计划失败,{}.", ExceptionUtil.getSimpleMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("更新计划失败," + ExceptionUtil.getSimpleMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("删除计划")
    @PostMapping(value = "/deleteSchedule")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String deleteSchedule(@RequestBody JSONObject requestBody) {
        ResultVo<String> result = new ResultVo<String>();
        boolean flag = false;
        String message = "";
        try {
            String scheduleId = CommonUtility.getId(requestBody);
            this.scheduleService.deleteSchedule(scheduleId);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = ExceptionUtil.getMessage(exception);
        } finally {
            if (flag) {
                result.setSuccess(true);
                result.setCode(200);

            } else {
                result.setCode(200);
                result.setSuccess(false);
                result.setMessage(message);
            }
        }
        return JSON.toJSONString(result);
    }

    /* *********************************************************** 计划策略 Start ****************************************************** */
    @ApiOperation("删除计划策略条件")
    @PostMapping(value = "/deleteSchedulePolicyItems")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "scheduleOBID", value = "计划OBID", example = "计划OBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "policyItemOBIDs", value = "用','分割的计划策略OBID", example = "用','分割的计划策略OBID", required = true, dataTypeClass = String.class)
    }))
    public String deleteSchedulePolicyItems(@RequestBody JSONObject requestBody) {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            String scheduleOBID = requestBody.getString("scheduleOBID");
            String policyItemOBIDs = requestBody.getString("policyItemOBIDs");
            if (StringUtils.isEmpty(scheduleOBID) || StringUtils.isEmpty(policyItemOBIDs)) {
                throw new Exception("删除计划策略条件失败,计划OBID和策略条件OBID不可为空!");
            }
            this.schedulePolicyService.deleteSchedulePolicyItems(scheduleOBID, policyItemOBIDs);
            result.successResult(true);
        } catch (Exception e) {
            log.error("数据归集失败,{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("数据归集失败," + ExceptionUtil.getSimpleMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("数据归集")
    @PostMapping(value = "/dataCollection")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "scheduleOBID", value = "计划OBID", example = "计划OBID", required = true, dataTypeClass = String.class)}))
    public String dataCollection(@RequestBody JSONObject requestBody) {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            String scheduleOBID = requestBody.getString("scheduleOBID");
            this.schedulePolicyService.dataCollection(scheduleOBID);
            result.successResult(true);
        } catch (Exception e) {
            log.error("数据归集失败,{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("数据归集失败," + ExceptionUtil.getSimpleMessage(e));
        }
        return JSON.toJSONString(result);
    }

    /* *********************************************************** 计划策略  End  ****************************************************** */

}
