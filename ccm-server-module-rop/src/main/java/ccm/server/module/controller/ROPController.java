package ccm.server.module.controller;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.module.business.IROPRunningBusinessService;
import ccm.server.module.vo.ROPReviseTaskVo;
import ccm.server.module.vo.ResultVo;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.util.CommonUtility;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/ccm/rop")
@Api(tags = "ROP模块接口")
@Slf4j
public class ROPController {
    @Autowired
    IROPRunningBusinessService iropRunningBusinessService;
    private static final String PROPERTIES = "properties";
    private static final String ROP_GROUP_OBID = "ROPGroupOBID";
    private static final String PAGE_SIZE = "pageSize";
    private static final String PAGE_INDEX = "pageIndex";

    @ApiOperation("re-generate rop work steps for design object(s) under specified document")
    @RequestMapping(value = "/refreshROPStepsForDocument", method = RequestMethod.POST)
    public String refreshROPStepsForDocument(@RequestBody JSONObject jsonObject) {
        Result<List<ObjectDTO>> result = new Result<>();
        String documentId = jsonObject.getString("obid");
        String classDefinitionUID = jsonObject.getString("classDefinitionUID");
        if (StringUtils.isEmpty(classDefinitionUID))
            classDefinitionUID = "CIMDocumentMaster";
        try {
            IObjectCollection objectCollection = this.iropRunningBusinessService.generateWorkStepByDocument(documentId, classDefinitionUID);
            if (objectCollection != null) {
                IObjectCollection workSteps = objectCollection.Items("CCMWorkStep");
                if (workSteps != null) {
                    result.setResult(workSteps.toObjectDTOs());
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("执行ROP模板升版")
    @PostMapping("/processROPTemplateRevise")
    public String processROPTemplateRevise() {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            this.iropRunningBusinessService.refreshObjStatusByROPChanged();
            result.successResult(true);
            result.setMessage("已提交后台处理!");
        } catch (Exception ex) {
            log.error("执行ROP模板升版失败!{}", ExceptionUtil.getMessage(ex), ExceptionUtil.getRootCause(ex));
            result.errorResult("执行ROP模板升版失败!{}" + ExceptionUtil.getMessage(ex));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("清除ROP升版任务历史")
    @PostMapping("/clearProcessingTasksHistory")
    public String clearProcessingTasksHistory() {
        Result<Boolean> result = new Result<>();
        try {
            this.iropRunningBusinessService.clearProcessingTasks();
            result.setResult(true);
            result.setMessage("清除完成");
        } catch (Exception exception) {
            log.error(exception.toString());
            exception.printStackTrace();
            result.error500(exception.toString());
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取ROP模板升版执行过程的状态信息")
    @PostMapping("/getROPReviseTaskStatus")
    public String getROPReviseTaskStatus(@RequestBody JSONObject jsonObject) {
        Result<ROPReviseTaskVo> result = new Result<>();
        try {
            String uuid = jsonObject.getString("taskUUID");
            result.setResult(this.iropRunningBusinessService.getROPProcessingTaskStatus(uuid));
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
            result.error500(ex.toString());
        }
        return CommonUtility.toJsonString(result);
    }


    @ApiOperation("获取ROP模板升版执行过程任务对象")
    @PostMapping("/getAllROPReviseTask")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "pageIndex", value = "当前页数", example = "1", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页显示数量", example = "30", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "filters", value = "过滤条件", example = "'[{xxxix:dsd}]'", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "orderBy", value = "排序条件", example = "'[{defUIDs:'',asc:true|false}]'", required = true, dataTypeClass = String.class)}))
    public String getAllROPReviseTask(@RequestBody JSONObject jsonObject) {
        Result<List<ROPReviseTaskVo>> result = new Result<>();
        try {
            int pageSize = ValueConversionUtility.toInteger(jsonObject.getString("pageSize"));
            int pageIndex = ValueConversionUtility.toInteger(jsonObject.getString("pageIndex"));
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            OrderByParam orderByParam = new OrderByParam(jsonObject);
            this.iropRunningBusinessService.getAllROPProcessingTasks(new PageRequest(pageIndex, pageSize), filtersParam, orderByParam, result);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
            result.error500(ex.toString());
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取ROP任务历史数据")
    @PostMapping("/getROPTemplateReviseHistoryTasks")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "pageIndex", value = "当前页数", example = "1", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页显示数量", example = "30", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "filters", value = "过滤条件", example = "'[{xxxix:dsd}]'", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "orderBy", value = "排序条件", example = "'[{defUIDs:'',asc:true|false}]'", required = true, dataTypeClass = String.class)}))
    public String getROPTemplateReviseHistoryTasks(@RequestBody JSONObject jsonObject) {
        ResultVo<List<ROPReviseTaskVo>> result = new ResultVo<>();
        try {
            int pageSize = ValueConversionUtility.toInteger(jsonObject.getString("pageSize"));
            int pageIndex = ValueConversionUtility.toInteger(jsonObject.getString("pageIndex"));
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            OrderByParam orderByParam = new OrderByParam(jsonObject);
            this.iropRunningBusinessService.getROPTemplateReviseHistoryTasks(new PageRequest(pageIndex, pageSize), filtersParam, orderByParam, result);
        } catch (Exception e) {
            log.error("获取任务历史数据!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取任务历史数据!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }


    @ApiOperation("创建或更新ROPStep模板")
    @PostMapping("/createOrUpdateROPWorkStep")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "properties", value = "对象属性集合", example = "'[]'", required = true, dataTypeClass = String.class)}))
    public String createOrUpdateROPWorkStep(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTO> resultVo = new ResultVo<>();
        try {
            String lstrProperties = jsonObject.getString(PROPERTIES);
            String lstrROPGroupOBID = jsonObject.getString(ROP_GROUP_OBID);
            resultVo.successResult(this.iropRunningBusinessService.createOUpdateROPWorkStepObjectDTOStyle(lstrProperties, lstrROPGroupOBID));
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
            resultVo.errorResult(ex.toString());
        }
        return CommonUtility.toJsonString(resultVo);
    }

    @ApiOperation("刷新ROP缓存")
    @PostMapping("/refreshROPCache")
    public String refreshROPCache() {
        Result<Boolean> result = new Result<>();
        try {
            this.iropRunningBusinessService.refreshROPCache();
            result.success("重载完成!");
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
            result.error500(ex.toString());
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("创建或更新ROP规则组")
    @PostMapping("/createOrUpdateROPGroup")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "properties", value = "对象属性集合", example = "'[]'", required = true, dataTypeClass = String.class)}))
    public String createOrUpdateROPGroup(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTO> resultVo = new ResultVo<>();
        try {
            String lstrProperties = jsonObject.getString(PROPERTIES);
            resultVo.successResult(this.iropRunningBusinessService.createOUpdateROPRuleGroupWithDTOStyle(lstrProperties));
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
            resultVo.errorResult(ex.toString());
        }
        return CommonUtility.toJsonString(resultVo);
    }


    @ApiOperation("创建或更新ROP规则组条目")
    @PostMapping("/createOUpdateROPRuleGroupItem")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "properties", value = "对象属性集合", example = "'[]'", required = true, dataTypeClass = String.class), @DynamicParameter(name = "ROPGroupOBID", value = "ROPGroupOBID", example = "'[]'", required = true, dataTypeClass = String.class)}))
    public String createROPGroupItem(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTO> resultVo = new ResultVo<>();
        try {
            String lstrProperties = jsonObject.getString(PROPERTIES);
            String lstrROPGroupOBID = jsonObject.getString("ROPGroupOBID");
            resultVo.successResult(this.iropRunningBusinessService.createOUpdateROPRuleGroupItemWithObjectDTOStyle(lstrProperties, lstrROPGroupOBID));
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
            resultVo.errorResult(ex.toString());
        }
        return CommonUtility.toJsonString(resultVo);
    }


    @ApiOperation("根据规则组获取规则条目")
    @PostMapping("/getROPGroupItemsByROPGroup")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "ClassDefinitionUID", value = "对象定义", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "OBID", value = "对象OBID", required = true, dataTypeClass = String.class)}))
    public String getROPGroupItemsByConstructionType(@RequestBody JSONObject jsonObject) {
        ResultVo<List<ObjectDTO>> resultVo = new ResultVo<>();
        try {
            IObject lobjRopGroup = SchemaUtility.getObjectByJSONObject(jsonObject);
            if (lobjRopGroup == null)
                throw new Exception("未找到指定的ROP规则组对象,OBID" + jsonObject.getString(propertyDefinitionType.OBID.toString()));
            IObjectCollection lcolROPGroupItems = this.iropRunningBusinessService.getROPGroupItemsByROPGroup(lobjRopGroup);
            if (SchemaUtility.hasValue(lcolROPGroupItems)) {
                resultVo.successResult(SchemaUtility.converterIObjectCollectionToDTOList(lcolROPGroupItems));
            } else {
                resultVo.successResult(new ArrayList<>());
            }
            resultVo.setTotal(SchemaUtility.hasValue(lcolROPGroupItems) ? lcolROPGroupItems.size() : 0);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
            resultVo.errorResult(ex.toString());
        }
        return JSON.toJSONString(resultVo);

    }

    @ApiOperation("获取可选属性列表")
    @GetMapping("/getPropDefsForROPGroupItem")
    public String getROPPropertyDefsForROPGroupItem(@RequestParam(name = "ROPGroupOBID") String ROPGroupOBID) {
        ResultVo<List<ObjectDTO>> resultVo = new ResultVo<>();
        try {
            resultVo.successResult(this.iropRunningBusinessService.getPropDefsForROPGroupItemByROPGroupOBID(ROPGroupOBID));
        } catch (Exception ex) {
            ex.printStackTrace();
            resultVo.errorResult(ex.toString());
        }
        return JSON.toJSONString(resultVo);
    }

    @ApiOperation("导入ROP模板信息")
    @PostMapping("/loadROPTemplateInfo")
    public String loadROPTemplateInfo(@RequestParam(name = "file") MultipartFile file) {
        ResultVo<Boolean> resultVo = new ResultVo<>();
        try {
            this.iropRunningBusinessService.loadROPTemplateIntoSystem(file);
            resultVo.successResult(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultVo.errorResult(ex.toString());
        }
        return JSON.toJSONString(resultVo);
    }


    @ApiOperation("导出ROP模板数据")
    @PostMapping("/exportROPTemplateInfo")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "ROPGroupOBID", value = "ROPGroupOBID", required = true, dataTypeClass = String.class)}))
    public void exportXmlFile(HttpServletResponse response, @RequestBody JSONObject jsonObject) {
        try {
            String lstrROPGroupOBID = jsonObject.getString("ROPGroupOBID");
            this.iropRunningBusinessService.generateROPTemplateData(response, lstrROPGroupOBID);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
        }
    }

    @ApiOperation("删除ROP信息")
    @PostMapping("/deleteROPTemplateInfo")
    public String deleteROPTemplateInfo(@RequestBody JSONObject jsonObject) {
        Result<Boolean> result = new Result<>();
        try {
            String lstrOBID = jsonObject.getString(CommonUtility.REQUEST_BODY_OBID);
            String lstrClassDef = jsonObject.getString(CommonUtility.REQUEST_BODY_CLASS_DEFINITION_UID);
            String lstrROPGroupOBID = jsonObject.getString("ROPGroupOBID");
            result.setResult(this.iropRunningBusinessService.deleteROPTemplateInfo(lstrOBID, lstrClassDef, lstrROPGroupOBID));
            result.success("删除成功!");
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
            result.error500(ex.toString());
        }
        return JSON.toJSONString(result);
    }


    @ApiOperation("根据施工分类获取ROP规则组")
    @PostMapping("/getROPGroupsByConstructionType")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "ClassDefinitionUID", value = "对象定义", required = true, dataTypeClass = String.class), @DynamicParameter(name = "OBID", value = "对象OBID", required = true, dataTypeClass = String.class)}))
    public String getROPGroupsByConstructionType(@RequestBody JSONObject jsonObject) {
        ResultVo<List<ObjectDTO>> resultVo = new ResultVo<>();
        try {
            IObject lobjConstructionType = SchemaUtility.getObjectByJSONObject(jsonObject);
            if (lobjConstructionType == null)
                throw new Exception("未找到指定的施工分类对象,OBID" + jsonObject.getString(propertyDefinitionType.OBID.toString()));
            IObjectCollection lcolROPGroups = this.iropRunningBusinessService.getROPGroupsByConstructionType(lobjConstructionType);
            List<ObjectDTO> objectDTOS = SchemaUtility.converterIObjectCollectionToDTOList(lcolROPGroups);
            objectDTOS.sort(Comparator.comparingInt(x -> Integer.parseInt(x.getPointedPropValue(propertyDefinitionType.ROPGroupOrder.toString()).toString())));
            resultVo.successResult(objectDTOS);
            resultVo.setTotal(SchemaUtility.hasValue(lcolROPGroups) ? lcolROPGroups.size() : 0);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
            resultVo.errorResult(ex.toString());
        }
        return JSON.toJSONString(resultVo);
    }

    @ApiOperation("根据ROP规则组获取ROP步骤")
    @PostMapping("/getROPWorkStepTemplatesByROPRuleGroup")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "ClassDefinitionUID", value = "对象定义", required = true, dataTypeClass = String.class), @DynamicParameter(name = "OBID", value = "对象OBID", required = true, dataTypeClass = String.class)}))
    public String getROPWorkStepTemplatesByROPRuleGroup(@RequestBody JSONObject jsonObject) {
        ResultVo<List<ObjectDTO>> resultVo = new ResultVo<>();
        try {
            IObject lobjROPGroup = SchemaUtility.getObjectByJSONObject(jsonObject);
            if (lobjROPGroup == null)
                throw new Exception("未找到指定的ROP规则组对象,OBID" + jsonObject.getString(propertyDefinitionType.OBID.toString()));
            IObjectCollection lcolROPGroups = this.iropRunningBusinessService.getROPWorkStepTemplatesByROPRuleGroup(lobjROPGroup);
            resultVo.successResult(SchemaUtility.converterIObjectCollectionToDTOList(lcolROPGroups));
            resultVo.setTotal(SchemaUtility.hasValue(lcolROPGroups) ? lcolROPGroups.size() : 0);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
            resultVo.errorResult(ex.toString());
        }
        return JSON.toJSONString(resultVo);
    }

    @ApiOperation("根据施工分类获取设计对象分类")
    @PostMapping("/getComponentCategoryByConstructionType")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "ClassDefinitionUID", value = "对象定义", required = true, dataTypeClass = String.class), @DynamicParameter(name = "OBID", value = "对象OBID", required = true, dataTypeClass = String.class)}))
    public String getComponentCategoryByConstructionType(@RequestBody JSONObject jsonObject) {
        ResultVo<List<ObjectDTO>> resultVo = new ResultVo<>();
        try {
            IObject lobjConstructionType = SchemaUtility.getObjectByJSONObject(jsonObject);
            if (lobjConstructionType == null)
                throw new Exception("未找到指定的施工分类对象,OBID" + jsonObject.getString(propertyDefinitionType.OBID.toString()));
            IObjectCollection lcolROPGroups = this.iropRunningBusinessService.getComponentCategoriesByConstructionType(lobjConstructionType);
            resultVo.successResult(SchemaUtility.converterIObjectCollectionToDTOList(lcolROPGroups));
            resultVo.setTotal(SchemaUtility.hasValue(lcolROPGroups) ? lcolROPGroups.size() : 0);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
            resultVo.errorResult(ex.toString());
        }
        return JSON.toJSONString(resultVo);
    }

    @ApiOperation(value = "获取施工分类", notes = "获取所有施工分类")
    @GetMapping("/getConstructionTypes")
    public String getConstructionTypes() {
        Result<List<ObjectDTO>> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            result.setResult(iropRunningBusinessService.getConstructionTypes());
        } catch (Exception exception) {
            result.setCode(500);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

}
