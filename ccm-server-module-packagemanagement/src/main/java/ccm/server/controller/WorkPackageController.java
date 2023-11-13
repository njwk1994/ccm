package ccm.server.controller;

import ccm.server.business.ICCMPackageService;
import ccm.server.business.ICCMScheduleService;
import ccm.server.business.ICCMWorkPackageService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.PackageRevProcessingMode;
import ccm.server.enums.PackageTypeEnum;
import ccm.server.enums.ProcedureTypeEnum;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.models.query.QueryRequest;
import ccm.server.module.vo.ResultVo;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.schema.interfaces.ICIMProjectConfig;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.model.IProperty;
import ccm.server.util.CommonUtility;
import ccm.server.utils.*;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/1/11 14:49
 */
@Slf4j
@RestController
@Api(tags = "工作包管理")
@RequestMapping("/ccm/workPackageManagement")
public class WorkPackageController {

    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    @Autowired
    private ICCMWorkPackageService workPackageService;

    @Autowired
    private ICCMScheduleService scheduleService;
    @Autowired
    private ICCMPackageService packageService;

    /* *****************************************  树结构方法 start  ***************************************** */

    /**
     * 获取当前用户树层级配置
     *
     * @return
     */
    @ApiOperation(value = "获取当前用户工作包树层级配置", notes = "获取当前用户工作包树层级配置")
    @RequestMapping(value = "/getMyWorkPackageHierarchyConfigurations", method = RequestMethod.POST)
    public String getMyWorkPackageHierarchyConfigurations(HttpServletRequest request) {
        Result<List<ObjectDTO>> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            IObjectCollection myWorkPackageHierarchyConfigurations = workPackageService.getMyWorkPackageHierarchyConfigurations(request, new PageRequest(0, 0));
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(myWorkPackageHierarchyConfigurations);
            result.setResult(objectDTOS);
        } catch (Exception exception) {
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "获取工作包属性关联的form", notes = "获取工作包属性关联的form")
    @PostMapping("/getWorkPackagePropertiesForm")
    public String getTaskPackagePropertiesForm() {
        Result<ObjectDTO> lobjResult = new Result<>();
        try {
            // TODO FORM
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
            lobjResult.setMessage(e.getLocalizedMessage());
        }
        return JSON.toJSONString(lobjResult, SerializerFeature.IgnoreErrorGetter);
    }

    @ApiOperation(value = "获取目录树工作包属性定义集合", notes = "获取目录树工作包属性定义集合")
    @RequestMapping(value = "/getWorkPackageProperties", method = RequestMethod.POST)
    public String getTaskPackageProperties() {
        Result<List<ObjectDTO>> result = new Result<>();
        try {
            List<ObjectDTO> workPackageFormPropertiesForConfigurationItem = workPackageService.getWorkPackageFormPropertiesForConfigurationItem();
            result.setResult(workPackageFormPropertiesForConfigurationItem);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            result.setMessage(e.getLocalizedMessage());
        }
        return JSON.toJSONString(result, SerializerFeature.IgnoreErrorGetter);
    }

    @ApiOperation(value = "获取工作包树配置及配置项表单", notes = "获取工作包树配置及配置项表单")
    @RequestMapping(value = "/getWorkPackageHierarchyConfigurationFormWithItem", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class)
    }))
    public String getWorkPackageHierarchyConfigurationFormWithItem(@RequestBody JSONObject requestBody) {
        Result<Map<String, Object>> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String formPurpose = CommonUtility.getFormPurpose(requestBody);
            Map<String, Object> workPackageHierarchyConfigurationFormWithItem = workPackageService.getWorkPackageHierarchyConfigurationFormWithItem(formPurpose);
            result.setResult(workPackageHierarchyConfigurationFormWithItem);
        } catch (Exception exception) {
            result.error500(ExceptionUtil.getMessage(exception));
            result.setSuccess(false);
        }
        return JSON.toJSONString(result);
    }

    /**
     * 自定义层级 - 添加
     *
     * @return
     */
    @ApiOperation(value = "新增工作包树层级配置及配置项失败", notes = "新增工作包树层级配置及配置项失败")
    @RequestMapping(value = "/createWorkPackageHierarchyConfigurationWithItems", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "obj", value = "object DTO to be updated", example = "{\n" +
                    "        \"items\": []\n" +
                    "    }", required = true, dataTypeClass = JSONObject.class),
            @DynamicParameter(name = "objItems", value = "object DTO to be updated", example = "[\n" +
                    "        {\"items\": []\n}," +
                    "        {\"items\": []\n}" +
                    "    ]", required = true, dataTypeClass = JSONObject.class)}))
    public String createWorkPackageHierarchyConfigurationWithItems(@RequestBody JSONObject requestBody) {
        ResultVo<String> result = new ResultVo<>();
        try {
            IObject workPackageHierarchyConfigurationWithItems = workPackageService.createWorkPackageHierarchyConfigurationWithItems(requestBody);
            result.successResult(workPackageHierarchyConfigurationWithItems.OBID());
        } catch (Exception exception) {
            log.error("新增工作包树层级配置及配置项失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("新增工作包树层级配置及配置项失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "删除层级配置", notes = "删除层级配置")
    @RequestMapping(value = "/deleteWorkPackageHierarchyConfiguration", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String deleteWorkPackageHierarchyConfiguration(@RequestBody JSONObject requestBody) {
        Result result = new Result();
        Boolean flag = false;
        String message = "";
        try {
            String workPackageOBID = CommonUtility.getId(requestBody);
            workPackageService.deleteWorkPackageHierarchyConfiguration(workPackageOBID);
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

    @ApiOperation(value = "更新层级配置", notes = "更新层级配置")
    @RequestMapping(value = "/updateHierarchyConfiguration", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String updateHierarchyConfiguration(@RequestBody JSONObject hierarchyConfiguration) {
        Result result = new Result();
        Boolean flag = false;
        String message = "";
        try {
            workPackageService.updateWorkPackageHierarchyConfiguration(CommonUtility.parseObjectDTOFromJSON(hierarchyConfiguration));
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
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

    /**
     * 获取层级配置下配置项
     *
     * @param requestBody
     * @return
     */
    @ApiOperation(value = "获取层级配置下配置项", notes = "获取层级配置下配置项")
    @RequestMapping(value = "/getWorkPackageHierarchyConfigurationItems", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String getWorkPackageHierarchyConfigurationItems(@RequestBody JSONObject requestBody) {
        ResultVo<List<ObjectDTO>> result = new ResultVo();
        try {
            String hierarchyConfigurationId = CommonUtility.getId(requestBody);
            IObjectCollection workPackageHierarchyConfigurationItems = workPackageService.getWorkPackageHierarchyConfigurationItems(hierarchyConfigurationId);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(workPackageHierarchyConfigurationItems);
            objectDTOS.sort((o1, o2) -> {
                int level1 = Integer.parseInt(o1.toGetValue(HierarchyUtils.HIERARCHY_LEVEL));
                int level2 = Integer.parseInt(o2.toGetValue(HierarchyUtils.HIERARCHY_LEVEL));
                if (level1 < level2) {
                    return 1;
                } else {
                    return 0;
                }
            });
            result.successResult(objectDTOS);
        } catch (Exception exception) {
            log.error("获取层级配置下配置项失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("获取层级配置下配置项失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    /**
     * 新增层级配置项
     *
     * @param requestBody
     * @return
     */
    @ApiOperation(value = "新增层级配置项", notes = "新增层级配置项")
    @RequestMapping(value = "/createWorkPackageHierarchyConfigurationItem", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "HierarchyConfiguration id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "name", value = "HierarchyConfiguration name", example = "XXXXX", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "obj", value = "object DTO to be updated", example = "    \"obj\": {\n" +
                    "        \"items\": [\n" +
                    "            {\n" +
                    "                \"defUID\": \"\",\n" +
                    "                \"displayValue\": \"\"\n" +
                    "            }\n" +
                    "        ]\n" +
                    "    }", required = true, dataTypeClass = JSONObject.class)}))
    public String createWorkPackageHierarchyConfigurationItem(@RequestBody JSONObject requestBody) {
        Result<String> result = new Result<String>();
        Boolean flag = false;
        String message = "";
        try {
            String obid = CommonUtility.getId(requestBody);
            ObjectDTO objectDTO = CommonUtility.parseObjectDTOFromJSON(requestBody);
            IObject workPackageHierarchyConfigurationItem = workPackageService.createWorkPackageHierarchyConfigurationItem(obid, objectDTO);
            result.setResult(workPackageHierarchyConfigurationItem.OBID());
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
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

    /**
     * 根据工作包和配置获取目录树
     *
     * @param pobjJson
     * @return
     */
    @ApiOperation(value = "根据工作包和配置获取目录树", notes = "根据工作包和配置获取目录树")
    @RequestMapping(value = "/generateHierarchyByWorkPackagesAndConfiguration", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "configurationId", value = "目录树配置规则ID", example = "X000111", required = true, dataTypeClass = String.class)}))
    public String generateHierarchyByWorkPackagesAndConfiguration(@RequestBody JSONObject pobjJson) {
        ResultVo<HierarchyObjectDTO> result = new ResultVo<>();
        String configurationId = pobjJson.getString("configurationId");
        try {
            HierarchyObjectDTO tree = workPackageService.generateHierarchyByWorkPackagesAndConfiguration(configurationId);
            result.successResult(tree);
        } catch (Exception exception) {
            log.error("根据工作包和配置获取目录树失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("根据工作包和配置获取目录树失败!" + ExceptionUtil.getMessage(exception));
        }

        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 点击目录树叶节点获取对应Work Packages
     *
     * @param selectedNode
     * @return
     */
    @ApiOperation(value = "获取目录树节点工作包信息", notes = "获取目录树节点工作包信息")
    @RequestMapping(value = "/getWorkPackagesFromHierarchyNode", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "obj", value = "选取的树层级得值",
                    example = "\"id\":\"2a5176f6ac465fbd59f48bd49fc558f1\",\n" +
                            "        \"name\": \"Purpose\",\n" +
                            "        \"parent\": {\n" +
                            "            \"id\":\"4264240d22615d1e1cd21a6833664955\",\n" +
                            "            \"name\": \"Discipline\",\n" +
                            "            \"parent\":{\n" +
                            "                \"id\":\"ddaa5895aa6ec55f90cd8c3aab341bce\",\n" +
                            "                \"name\": \"DocType\"\n" +
                            "            }\n" +
                            "        }",
                    required = true, dataTypeClass = JSONObject.class),
    }))
    public String getWorkPackagesFromHierarchyNode(@RequestBody JSONObject selectedNode) {
        Result<List<ObjectDTO>> result = new Result<>();
        result.setCode(200);
        try {
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(selectedNode);
            IObjectCollection workPackagesFromHierarchyNode = workPackageService.getWorkPackagesFromHierarchyNode(CommonUtility.parseHierarchyObjectDTOFromJSON(selectedNode), pageRequest);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(workPackagesFromHierarchyNode);
            result.setResult(objectDTOS);
            result.setSuccess(true);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            result.setMessage(exception.getMessage());
            result.setSuccess(false);
        }
        return JSON.toJSONString(result);
    }
    /* *****************************************  树结构方法 end  ***************************************** */

    /* *****************************************  工作包方法 start  ***************************************** */
    @ApiOperation("获取工作包表单")
    @RequestMapping(value = "/getWorkPackageForm", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String getWorkPackageForm(@RequestBody JSONObject pobjJson) {
        Result<ObjectDTO> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String formPurpose = CommonUtility.getFormPurpose(pobjJson);
            ObjectDTO items = workPackageService.getWorkPackageForm(formPurpose);
            result.setResult(items);
        } catch (Exception exception) {

            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取所有工作包")
    @RequestMapping(value = "/getWorkPackages", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = Integer.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = Integer.class)
    }))
    public String getWorkPackages(@RequestBody JSONObject requestBody) {
        Result<List<ObjectDTO>> result = new Result();
        result.setCode(200);
        result.setSuccess(true);

        try {
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(requestBody);
            IObjectCollection workPackages = workPackageService.getWorkPackages(pageRequest);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(workPackages);
            result.setResult(objectDTOS);
        } catch (Exception exception) {

            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("新建工作包")
    @RequestMapping(value = "/createWorkPackage", method = RequestMethod.POST)
    public String createWorkPackage(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTO> result = new ResultVo<>();
        try {
            ObjectDTO objectDTO = JSON.parseObject(jsonObject.toJSONString(), ObjectDTO.class);
            IObject iObject = this.schemaBusinessService.generalCreate(objectDTO);
            if (iObject != null) {
                iObject.refreshObjectDTO(objectDTO);
                result.successResult(iObject.toObjectDTO());
            }
        } catch (Exception exception) {
            if (exception.getMessage().contains("BatchUpdateException")) {
                result.errorResult("新增工作包失败,数据写入失败!请检查必填参数或唯一参数!");
                result.setCode(10000);
                if (exception.getMessage().contains("唯一索引") || exception.getMessage().contains("unique index")) {
                    log.error("新增工作包失败!{}", ExceptionUtil.getMessage(exception));
                    result.errorResult("新增工作包失败,编号禁止重复!");
                    result.setCode(10001);
                }
            } else {
                log.error("新增工作包失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
                result.errorResult("新增工作包失败!" + ExceptionUtil.getMessage(exception));
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("新建工作包并关联任务包")
    @RequestMapping(value = "/createWorkPackageWithRelFromTaskPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "task package obid", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "items", value = "work package to be created", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String createWorkPackageWithRelFromTaskPackage(@RequestBody JSONObject pobjJson) {
        Result<String> result = new Result<String>();
        result.setCode(200);
        try {
            String taskPackageOBID = CommonUtility.getId(pobjJson);
            IObject workPackage = workPackageService.createWorkPackageWithRelFromTaskPackage(taskPackageOBID, CommonUtility.parseObjectDTOFromJSON(pobjJson));
            if (null != workPackage) {
                result.setResult(workPackage.OBID());
                result.setMessage("工作包创建成功!");
            } else {
                result.setMessage("工作包创建失败!");
            }
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            result.error500(exception.getMessage());
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("移除任务包工作包关联")
    @RequestMapping(value = "/removeTaskPackage2WorkPackageRel", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "taskPackageOBIDs", value = "object DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "workPackageOBID", value = "object DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String removeTaskPackage2WorkPackageRel(@RequestBody JSONObject requestBody) throws Exception {
        ResultVo<Object> result = new ResultVo<Object>();
        try {
            workPackageService.removeTaskPackage2WorkPackageRel(requestBody.getString("taskPackageOBIDs"), requestBody.getString("workPackageOBID"));
            result.successResult(true);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            result.errorResult(ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("删除工作包")
    @RequestMapping(value = "/deleteWorkPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String deleteWorkPackage(@RequestBody JSONObject requestBody) throws Exception {
        Result result = new Result();
        Boolean flag = false;
        String message = "";
        try {
            String workPackageId = CommonUtility.getId(requestBody);
            workPackageService.deleteWorkPackage(workPackageId);
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

    @ApiOperation("更新工作包")
    @RequestMapping(value = "/updateWorkPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String updateWorkPackage(@RequestBody JSONObject taskPackage) throws Exception {
        Result result = new Result();
        Boolean flag = false;
        String message = "";
        try {
            workPackageService.updateWorkPackage(CommonUtility.parseObjectDTOFromJSON(taskPackage));
            flag = true;
        } catch (Exception exception) {

            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
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
    /* *****************************************  工作包方法 end  ***************************************** */

    /* *****************************************  工作包图纸方法 start  ***************************************** */
    @ApiOperation("获取工作包下图纸")
    @RequestMapping(value = "/relatedDocumentsFromWorkPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "work package DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String getRelatedDocuments(@RequestBody JSONObject requestBody) throws Exception {
        Result<List<ObjectDTO>> result = new Result<List<ObjectDTO>>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String workPackageId = CommonUtility.getId(requestBody);
            IObjectCollection relatedDocuments = workPackageService.getRelatedDocuments(workPackageId);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(relatedDocuments);
            result.setResult(objectDTOS);
        } catch (Exception exception) {

            log.error(exception.getMessage(), exception);
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(exception.getMessage());
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取工作包下升版图纸")
    @RequestMapping(value = "/getWPRelatedRevisedDocuments", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "work package DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getWPRelatedRevisedDocuments(@RequestBody JSONObject jsonObject) throws Exception {
        ResultVo<ObjectDTOCollection> result = new ResultVo<ObjectDTOCollection>();
        try {
            String workPackageId = CommonUtility.getId(jsonObject);
            if (StringUtils.isBlank(workPackageId)) {
                result.errorResult("工作包OBID不可以为空!");
                return JSON.toJSONString(result);
            }
            String formPurpose = CommonUtility.getFormPurpose(jsonObject);
            if (org.springframework.util.StringUtils.isEmpty(formPurpose)) {
                formPurpose = ccm.server.enums.formPurpose.Info.toString();
            }
            ICIMForm form = this.schemaBusinessService.getForm(formPurpose, DocumentUtils.CCM_DOCUMENT);
            ObjectDTO formBase = null;
            if (form != null) {
                formBase = form.generatePopup(formPurpose);
            } else {
                formBase = this.schemaBusinessService.generateDefaultPopup(DocumentUtils.CCM_DOCUMENT);
            }
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(jsonObject);
            if (null != formBase) {
                IObjectCollection relatedRevisedDocuments = workPackageService.getRelatedRevisedDocuments(workPackageId, pageRequest);
                if (relatedRevisedDocuments != null && relatedRevisedDocuments.hasValue()) {
                    List<ObjectDTO> collections = new ArrayList<>();
                    Iterator<IObject> objectIterator = relatedRevisedDocuments.GetEnumerator();
                    while (objectIterator.hasNext()) {
                        IObject next = objectIterator.next();
                        ObjectDTO currentForm = formBase.copyTo();
                        next.fillingForObjectDTO(currentForm);
                        collections.add(currentForm);
                    }
                    ObjectDTOCollection oc = PageUtility.pagedObjectDTOS(collections, pageRequest);
                    result.successResult(oc);
                    result.setMessage("获取工作包下升版图纸成功!");
                } else {
                    result.successResult(null);
                    result.setMessage("获取工作包下升版图纸成功,未找到对应数据.");
                }
            }
        } catch (Exception exception) {
            log.error("获取工作包下升版图纸失败!{}", ExceptionUtil.getMessage(exception));
            result.errorResult("获取工作包下升版图纸失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("添加图纸到工作包")
    @RequestMapping(value = "/assignDocumentsToWorkPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "work package id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "documentOBIDs", value = "documentOBIDs", example = "docOBID1,docOBID2", required = true, dataTypeClass = String.class)}))
    public String assignDocumentsToWorkPackage(@RequestBody JSONObject jsonObject) throws Exception {
        ResultVo<Object> result = new ResultVo<>();
        try {
            String workPackageId = CommonUtility.getId(jsonObject);
            List<String> documentOBIDs = Arrays.asList(jsonObject.getString("documentOBIDs").split(","));
            workPackageService.assignDocumentsToWorkPackage(workPackageId, documentOBIDs);
            result.successResult(true);
        } catch (Exception exception) {
            log.error("添加图纸到工作包失败!{}{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("添加图纸到工作包失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("移除工作包下图纸")
    @RequestMapping(value = "/removeDocumentsFromWorkPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageId", value = "UUID", example = "work package's id in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "documentsId", value = "UUID", example = "document's Id join with comma", required = true, dataTypeClass = String.class)}))
    public String removeDocumentsFromWorkPackage(@RequestBody JSONObject pobjJson) {
        ResultVo<Object> result = new ResultVo<>();
        try {
            result.setSuccess(workPackageService.removeDocumentsFromWorkPackage(pobjJson.getString("packageId"), pobjJson.getString("documentsId")));
            result.successResult(true);
        } catch (Exception exception) {
            log.error("移除工作包下图纸失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("移除工作包下图纸失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取工作包可选择添加的图纸(根据关联任务包过滤)")
    @RequestMapping(value = "/getSelectableDocumentsForWorkPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "getSelectableDocuments", properties = {
            @DynamicParameter(name = "id", value = "UUID", example = "work package's id in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getSelectableDocuments(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<ObjectDTOCollection>();
        try {
            String id = CommonUtility.getId(jsonObject);
            if (StringUtils.isBlank(id)) {
                result.errorResult("工作包OBID不可以为空!");
                return JSON.toJSONString(result);
            }
            String formPurpose = CommonUtility.getFormPurpose(jsonObject);
            if (org.springframework.util.StringUtils.isEmpty(formPurpose)) {
                formPurpose = ccm.server.enums.formPurpose.Info.toString();
            }
            ICIMForm form = this.schemaBusinessService.getForm(formPurpose, DocumentUtils.CCM_DOCUMENT);
            ObjectDTO formBase = null;
            if (form != null) {
                formBase = form.generatePopup(formPurpose);
            } else {
                formBase = this.schemaBusinessService.generateDefaultPopup(DocumentUtils.CCM_DOCUMENT);
            }
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(jsonObject);
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            OrderByParam orderByParam = new OrderByParam(jsonObject);
            if (formBase != null) {
                IObjectCollection selectableDocuments = workPackageService.getSelectableDocuments(id, filtersParam, orderByParam, pageRequest);
                if (selectableDocuments != null && selectableDocuments.hasValue()) {
                    List<ObjectDTO> collections = new ArrayList<>();
                    Iterator<IObject> objectIterator = selectableDocuments.GetEnumerator();
                    while (objectIterator.hasNext()) {
                        IObject next = objectIterator.next();
                        ObjectDTO currentForm = formBase.copyTo();
                        next.fillingForObjectDTO(currentForm);
                        collections.add(currentForm);
                    }
                    ObjectDTOCollection oc = new ObjectDTOCollection(collections);
                    oc.setCurrent(selectableDocuments.PageResult().getCurrent());
                    oc.setSize(selectableDocuments.PageResult().getSize());
                    oc.setTotal(selectableDocuments.PageResult().getTotal());
                    result.successResult(oc);
                    result.setMessage("retrieved object(s) with " + DocumentUtils.CCM_DOCUMENT + " succeeded");
                } else {
                    result.successResult(null);
                    result.setMessage("retrieved object(s) with " + DocumentUtils.CCM_DOCUMENT + ", and result is nothing.");
                }
            }
        } catch (Exception exception) {
            result.errorResult(ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }
    /* *****************************************  工作包图纸方法 end  ***************************************** */

    /* *****************************************  工作包材料方法 start  ***************************************** */

    /**
     * 获取和工作包相同阶段并且有材料消耗的设计数据
     *
     * @return
     * @throws Exception
     */
    @ApiOperation("获取和工作包相同阶段并且有材料消耗的设计数据")
    @RequestMapping(value = "/getDesignDataByPurposeAndConsumeMaterialForWP", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "工作包OBID", example = "work package's OBID in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "classDefinitionUID", value = "设计数据类型", example = "CCMSupport", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getDesignDataByPurposeAndConsumeMaterialForWP(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<>();
        try {
            String id = CommonUtility.getId(jsonObject);
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(jsonObject);
            String classDefinitionUID = CommonUtility.getClassDefinitionUID(jsonObject);
            String formPurpose = CommonUtility.getFormPurpose(jsonObject);

            if (StringUtils.isEmpty(formPurpose)) {
                formPurpose = ccm.server.enums.formPurpose.Info.toString();
            }
            ICIMForm form = this.schemaBusinessService.getForm(formPurpose, classDefinitionUID);
            ObjectDTO formBase = null;
            if (form != null) {
                formBase = form.generatePopup(formPurpose);
            } else {
                formBase = this.schemaBusinessService.generateDefaultPopup(classDefinitionUID);
            }
            log.info("form generation completed");
            if (formBase != null) {
                IObjectCollection packageId = this.workPackageService.getDesignDataByPurposeAndConsumeMaterial(id, classDefinitionUID, pageRequest);
                if (packageId != null && packageId.hasValue()) {
                    List<ObjectDTO> collections = new ArrayList<>();
                    Iterator<IObject> objectIterator = packageId.GetEnumerator();
                    while (objectIterator.hasNext()) {
                        IObject next = objectIterator.next();
                        ObjectDTO currentForm = formBase.copyTo();
                        next.fillingForObjectDTO(currentForm);
                        collections.add(currentForm);
                    }
                    ObjectDTOCollection oc = PageUtility.pagedObjectDTOS(collections, pageRequest);
                    result.successResult(oc);
                    result.setMessage("retrieved object(s) with " + classDefinitionUID + " succeeded");
                } else {
                    result.successResult(null);
                    result.setMessage("retrieved object(s) with " + classDefinitionUID + ", and result is nothing.");
                }
            }
        } catch (Exception exception) {
            log.error("获取和工作包相同阶段并且有材料消耗的设计数据失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("获取和工作包相同阶段并且有材料消耗的设计数据失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取工作包下材料")
    @RequestMapping(value = "/relatedComponentsFromWorkPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "work package DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String getRelatedComponents(@RequestBody JSONObject requestBody) throws Exception {
        Result<List<ObjectDTO>> result = new Result<List<ObjectDTO>>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String workPackageId = CommonUtility.getId(requestBody);
            IObjectCollection relatedComponents = workPackageService.getRelatedComponents(workPackageId);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(relatedComponents);
            result.setResult(objectDTOS);
        } catch (Exception exception) {

            log.error(exception.getMessage(), exception);
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(exception.getMessage());
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("移除工作包下组件")
    @RequestMapping(value = "/removeComponentsUnderWorkPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageOBID", value = "UUID", example = "WPOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "componentOBIDs", value = "UUID", example = "OBID1,OBID2", required = true, dataTypeClass = String.class)}))
    public String removeComponentsUnderWorkPackage(@RequestBody JSONObject pobjJson) {
        ResultVo<Object> result = new ResultVo();
        result.setCode(200);
        result.setSuccess(true);
        try {
            workPackageService.removeComponentsUnderWorkPackage(pobjJson.getString("packageOBID"), pobjJson.getString("componentOBIDs"));
            result.successResult(true);
        } catch (Exception exception) {
            result.errorResult("移除工作包下材料失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    /* *****************************************  工作包材料方法 end  ***************************************** */
    /* *****************************************  工作包工作步骤方法 start  ***************************************** */
    @ApiOperation("获取工作包下工作步骤")
    @RequestMapping(value = "/getRelatedWorkStep", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageOBID", value = "obid", example = "work package's id in database", required = true, dataTypeClass = String.class)
    }))
    public String getRelatedWorkStep(@RequestBody JSONObject requestBody) {
        ResultVo<List<ObjectDTO>> result = new ResultVo<>();
        try {
            IObjectCollection relatedWorkStep = workPackageService.getRelatedWorkStep(requestBody.getString("packageOBID"));
            List<ObjectDTO> objectDTOS = relatedWorkStep.toObjectDTOs();
            result.successResult(objectDTOS);
        } catch (Exception exception) {
            log.error("获取工作包下工作步骤失败!{},{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("获取工作包下工作步骤失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("移除工作包下工作步骤")
    @RequestMapping(value = "/removeWorkStepUnderWorkPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageOBID", value = "obid", example = "work package's id in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "stepOBIDs", value = "obid", example = "work step's Id join with comma", required = true, dataTypeClass = String.class)}))
    public String removeWorkStepUnderWorkPackage(@RequestBody JSONObject requestBody) {
        ResultVo<Object> result = new ResultVo();
        try {
            workPackageService.removeWorkStepUnderWorkPackage(requestBody.getString("packageOBID"),
                    requestBody.getString("stepOBIDs"));
            result.successResult(true);
        } catch (Exception exception) {
            result.errorResult("移除工作包下工作步骤失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("刷新工作包工作步骤")
    @RequestMapping(value = "/refreshWorkPackageWorkStep", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "UUID", example = "work package's id in database", required = true, dataTypeClass = String.class)}))
    public String refreshWorkPackageWorkStep(@RequestBody JSONObject pobjJson) {
        ResultVo<Object> result = new ResultVo<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String id = CommonUtility.getId(pobjJson);
            this.workPackageService.refreshWorkStep(id);
            result.successResult(true);
        } catch (Exception exception) {
            log.error("刷新工作包工作步骤失败!{},{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("刷新工作包工作步骤失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    /* *****************************************  工作包工作步骤方法 end  ***************************************** */
    /* *****************************************  资源方法 start  ***************************************** */
    @ApiOperation("获取资源表单")
    @RequestMapping(value = "/resourcesForm", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String getResourcesForm(@RequestBody JSONObject pobjJson) {
        Result<ObjectDTO> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String formPurpose = CommonUtility.getFormPurpose(pobjJson);
            ObjectDTO resourcesForm = workPackageService.getResourcesForm(formPurpose);
            result.setResult(resourcesForm);
        } catch (Exception exception) {
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取工作包下资源")
    @RequestMapping(value = "/relatedResourcesFromWorkPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "work package DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String getRelatedResources(@RequestBody JSONObject requestBody) throws Exception {
        Result<List<ObjectDTO>> result = new Result<List<ObjectDTO>>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String workPackageId = CommonUtility.getId(requestBody);
            IObjectCollection relatedResources = workPackageService.getRelatedResources(workPackageId);
            List<ObjectDTO> items = ObjectDTOUtility.convertToObjectDTOList(relatedResources);
            result.setResult(items);
        } catch (Exception exception) {

            log.error(exception.getMessage(), exception);
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(exception.getMessage());
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("新增资源")
    @RequestMapping(value = "/createResources", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageId", value = "UUID", example = "work package's id in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "obj", value = "object DTO to be updated", example = "", required = true, dataTypeClass = JSONObject.class)
    }))
    public String createResources(@RequestBody JSONObject requestBody) {
        Result<String> result = new Result<String>();
        boolean flag = false;
        String message = "";
        try {
            IObject packageId = workPackageService.createResources(requestBody.getString("packageId"), CommonUtility.parseObjectDTOFromJSON(requestBody));
            flag = true;
            result.setResult(packageId.OBID());
        } catch (Exception exception) {

            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
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

    @ApiOperation("删除资源")
    @RequestMapping(value = "/deleteResources", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageId", value = "UUID", example = "work package's id in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "resourcesIds", value = "UUID", example = "resource's Id join with comma", required = true, dataTypeClass = String.class)}))
    public String deleteResources(@RequestBody JSONObject requestBody) {
        Result<Object> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            workPackageService.deleteResources(requestBody.getString("resourcesIds"));
            result.setSuccess(true);
        } catch (Exception exception) {
            result.setMessage(exception.getMessage());
            result.setSuccess(false);
        }
        return JSON.toJSONString(result);
    }
    /* *****************************************  资源方法 end  ***************************************** */

    /* *****************************************  工作包父计划方法 start  ***************************************** */
    @ApiOperation("获取父计划和工作包属性")
    @RequestMapping(value = "/getWorkPackageFatherPlan", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "UUID", example = "work package's id in database", required = true, dataTypeClass = String.class)}))
    public String getWorkPackageFatherPlan(@RequestBody JSONObject jsonObject) {
        Result<Map<String, Object>> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String id = CommonUtility.getId(jsonObject);
            Map<String, Object> items = workPackageService.getWorkPackageFatherPlan(id);
            result.setResult(items);
        } catch (Exception exception) {

            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("刷新工作包计划权重")
    @RequestMapping(value = "/refreshWorkPackagePlanWeight", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "UUID", example = "work package's id in database", required = true, dataTypeClass = String.class)}))
    public String refreshWorkPackagePlanWeight(@RequestBody JSONObject jsonObject) {
        Result<Double> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String id = CommonUtility.getId(jsonObject);
            Double aDouble = workPackageService.refreshPlanWeight(id);
            result.setResult(aDouble);
        } catch (Exception exception) {

            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("刷新工作包进度")
    @RequestMapping(value = "/refreshWorkPackageProgress", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "UUID", example = "work package's id in database", required = true, dataTypeClass = String.class)}))
    public String refreshWorkPackageProgress(@RequestBody JSONObject pobjJson) {
        Result<Double> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String id = CommonUtility.getId(pobjJson);
            Double planWeight = this.workPackageService.refreshProgress(id);
            result.setResult(planWeight);
        } catch (Exception exception) {
            exception.printStackTrace();
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取计划下工作包")
    @RequestMapping(value = "/getWorkPackagesUnderSchedule", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "ParentPlan's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String getWorkPackagesUnderSchedule(@RequestBody JSONObject requestBody) throws Exception {
        Result<List<ObjectDTO>> result = new Result<List<ObjectDTO>>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String parentPlanId = CommonUtility.getId(requestBody);
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(requestBody);
            IObjectCollection packagesUnderSchedules = scheduleService.getPackagesUnderSchedules(parentPlanId, ScheduleUtils.REL_SCHEDULE_2_WORK_PACKAGE);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(packagesUnderSchedules);
            result.setResult(objectDTOS);
        } catch (Exception exception) {

            log.error(exception.getMessage(), exception);
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(exception.getMessage());
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    /* *****************************************  工作包父计划方法 end  ***************************************** */
    /* ******************************************************* 工作包-预测预留方法 Start ******************************************************* */
    @ApiOperation(value = "基于整张图纸的工作包预测/预留并获取预测结果", notes = "检测图纸是否存在,不存在则提示SPM不存在此图纸,存在的进行预测预留操作")
    @PostMapping("existAndCreateNewStatusRequest")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "projectId", value = "目前固定一个项目号 POC_A", example = "POC_A", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestName", value = "工作包的Name", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "searchColumn", dataTypeClass = String.class),
            @DynamicParameter(name = "searchValue", dataTypeClass = String.class)
    }))
    public String existAndCreateNewStatusRequest(@RequestBody JSONObject jsonObject) {

        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        boolean flag = false;
        String message = "";
        result.setCode(200);
        try {
            String projectId = jsonObject.getString("projectId");
            String requestName = jsonObject.getString("requestName");
            String requestType = jsonObject.getString("requestType");
            String searchColumn = jsonObject.getString("searchColumn");
            String searchValue = jsonObject.getString("searchValue");
            Map<String, Object> procedureResults = workPackageService
                    .existAndCreateNewStatusRequest(projectId, requestName, requestType, searchColumn, searchValue);
            result.setResult(procedureResults);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
            result.setMessage(message);
        } finally {
            result.setSuccess(flag);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "工作包部分预测/预留并获取预测结果", notes = "检测图纸是否存在,不存在则提示SPM不存在此图纸,存在的进行预测预留操作")
    @PostMapping("existAndCreatePartialStatusRequestForWP")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageId", value = "工作包的OBID", example = "WPOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "projectId", value = "从项目配置中获取", example = "POC_A", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestName", value = "预测单号", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "drawingNumbers", value = "用\",\"拼接的图纸OBID", example = "docOBID01,docOBID02", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "warehouses", value = "仓库", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "searchColumn", dataTypeClass = String.class),
            @DynamicParameter(name = "searchValue", dataTypeClass = String.class)
    }))
    public String existAndCreatePartialStatusRequestForWP(@RequestBody JSONObject jsonObject) {
        ResultVo<Map<String, Object>> result = new ResultVo<Map<String, Object>>();
        try {
            String packageId = jsonObject.getString("packageId");

            // 获取项目配置
            IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = queryEngine.start();
            queryEngine.addClassDefForQuery(queryRequest, ICIMProjectConfigUtils.CIM_PROJECT_CONFIG);
            IObject projectConfig = queryEngine.queryOne(queryRequest);
            if (null == projectConfig) {
                throw new RuntimeException("获取项目配置失败!");
            }
            ICIMProjectConfig icimProjectConfig = projectConfig.toInterface(ICIMProjectConfig.class);

            String projectId = icimProjectConfig.getSPMProject();
            if (org.springframework.util.StringUtils.isEmpty(projectId)) {
                throw new RuntimeException("获取项目配置的项目号失败!");
            }
            // 存储过程类型
            IProperty procedureTypeProperty = icimProjectConfig.getProperty(ICIMProjectConfigUtils.PROCEDURE_TYPE);
            if (null == procedureTypeProperty) {
                throw new RuntimeException("获取项目配置的存储过程类型失败!");
            }
            Object value = procedureTypeProperty.Value();
            if (value == null) {
                throw new RuntimeException("存储过程类型配置值获取失败!请检查项目配置!");
            }
            String procedureTypeStr = value.toString();
            ProcedureTypeEnum procedureType = ProcedureTypeEnum.valueOf(procedureTypeStr);

            String warehouses = jsonObject.getString("warehouses") == null ? "" : jsonObject.getString("warehouses");

            String requestName = jsonObject.getString("requestName");
            String requestType = jsonObject.getString("requestType");
            String searchColumn = jsonObject.getString("searchColumn");
            String searchValue = jsonObject.getString("searchValue");
            String drawingNumbers = jsonObject.getString("drawingNumbers") == null ? "" : jsonObject.getString("drawingNumbers");

            // Map<String, Object> procedureResults = workPackageService.existAndCreatePartialStatusRequest33(packageId, projectId, requestName, requestType, "", drawingNumbers, searchColumn, searchValue);
            Map<String, Object> procedureResults = packageService
                    .existAndCreateRequest(PackageTypeEnum.WP, packageId, projectId,
                            requestName, requestType, warehouses, drawingNumbers, searchColumn, searchValue, procedureType);

            JSONArray data = (JSONArray) procedureResults.get("data");
            result.setTotal(data.size());
            result.successResult(procedureResults);
        } catch (Exception exception) {
            log.error("工作包部分预测/预留并获取预测结果失败!{}{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("工作包部分预测/预留并获取预测结果失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "工作包部分预测/预留并获取预测结果33", notes = "检测图纸是否存在,不存在则提示SPM不存在此图纸,存在的进行预测预留操作33")
    @PostMapping("existAndCreatePartialStatusRequestForWP33")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageId", value = "工作包的OBID", example = "WPOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "projectId", value = "目前固定一个项目号 POC_A", example = "POC_A", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestName", value = "预测单号", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "drawingNumbers", value = "用\",\"拼接的图纸名称", example = "docName01,docName02", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "searchColumn", dataTypeClass = String.class),
            @DynamicParameter(name = "searchValue", dataTypeClass = String.class)
    }))
    public String existAndCreatePartialStatusRequestForWP33(@RequestBody JSONObject jsonObject) {
        ResultVo<Map<String, Object>> result = new ResultVo<Map<String, Object>>();
        try {
            String packageId = jsonObject.getString("packageId");
            String projectId = jsonObject.getString("projectId");
            String warehouses = jsonObject.getString("warehouses");
            String requestName = jsonObject.getString("requestName");
            String requestType = jsonObject.getString("requestType");
            String searchColumn = jsonObject.getString("searchColumn");
            String searchValue = jsonObject.getString("searchValue");
            String drawingNumbers = jsonObject.getString("drawingNumbers") == null ? "" : jsonObject.getString("drawingNumbers");

            Map<String, Object> procedureResults = workPackageService
                    .existAndCreatePartialStatusRequest33(packageId, projectId, requestName, requestType, warehouses, drawingNumbers, searchColumn, searchValue);
            result.successResult(procedureResults);
        } catch (Exception exception) {
            log.error("工作包部分预测/预留并获取预测结果33失败!{}{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("工作包部分预测/预留并获取预测结果33失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "工作包按阶段进行材料预测预留并获取预测结果", notes = "检测图纸是否存在,不存在则提示SPM不存在此图纸,存在的进行预测预留操作")
    @PostMapping("createFAWithExtraFilterForWP")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageId", value = "工作包的OBID", example = "WPOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "projectId", value = "目前固定一个项目号 POC_A", example = "POC_A", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "lpAttrCode", value = "如果为空则默认填写 LP_ATTR_CODE", example = "LP_ATTR_CODE", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestName", value = "预测单号", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "drawingNumbers", value = "用\",\"拼接的图纸名称", example = "docName01,docName02", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "searchColumn", dataTypeClass = String.class),
            @DynamicParameter(name = "searchValue", dataTypeClass = String.class)
    }))
    public String createFAWithExtraFilterForWP(@RequestBody JSONObject jsonObject) {
        ResultVo<Map<String, Object>> result = new ResultVo<Map<String, Object>>();
        try {
            String packageId = jsonObject.getString("packageId");
            String projectId = jsonObject.getString("projectId");
            String requestName = jsonObject.getString("requestName");
            String requestType = jsonObject.getString("requestType");
            String searchColumn = jsonObject.getString("searchColumn");
            String searchValue = jsonObject.getString("searchValue");
            String drawingNumbers = jsonObject.getString("drawingNumbers") == null ? "" : jsonObject.getString("drawingNumbers");
            String lpAttrCode = org.springframework.util.StringUtils.isEmpty(jsonObject.getString("lpAttrCode")) ? "LP_ATTR_CODE" : jsonObject.getString("lpAttrCode");

            Map<String, Object> procedureResults = workPackageService
                    .createFAWithExtraFilter(packageId, projectId, lpAttrCode, requestName, requestType, drawingNumbers, searchColumn, searchValue);
            result.successResult(procedureResults);
        } catch (Exception exception) {
            log.error("工作包按阶段进行材料预测预留并获取预测结果失败!{}{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("工作包按阶段进行材料预测预留并获取预测结果失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }
    /* ******************************************************* 工作包-预测预留方法 End ******************************************************* */
    /* ******************************************************* 工作包-升版方法 Start ******************************************************* */

    /**
     * 工作包升版处理
     */
    @ApiOperation(value = "工作包升版处理", notes = "工作包升版处理")
    @PostMapping("workPackageRevisionHandler")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "工作包的OBID", example = "WPOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "mode", value = "ELT_PackageRevProcessingMode:EN_DeleteMode-批量删除工作步骤,EN_UpdateMode-批量关联工作步骤,EN_DeleteUpdateMode-批量删除并关联工作步骤",
                    example = "EN_DeleteMode", required = true, dataTypeClass = String.class)
    }))
    public String workPackageRevisionHandler(@RequestBody JSONObject jsonObject) {
        ResultVo<Object> result = new ResultVo<>();
        String id = CommonUtility.getId(jsonObject);
        String mode = jsonObject.getString("mode");
        try {
            workPackageService.workPackageRevisionHandler(id, PackageRevProcessingMode.valueOf(mode));
            result.successResult(true);
        } catch (Exception exception) {
            log.error("工作包升版处理失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("工作包升版处理失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }
    /* ******************************************************* 工作包-升版方法 End ******************************************************* */
}
