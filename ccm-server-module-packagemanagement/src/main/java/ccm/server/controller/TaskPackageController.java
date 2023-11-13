package ccm.server.controller;

import ccm.server.business.ICCMPackageService;
import ccm.server.business.ICCMTaskPackageService;
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
import ccm.server.enums.operationPurpose;
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
import ccm.server.utils.DocumentUtils;
import ccm.server.utils.ICIMProjectConfigUtils;
import ccm.server.utils.ObjectDTOUtility;
import ccm.server.utils.PageUtility;
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
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/1/11 15:22
 */
@Slf4j
@RestController
@Api(tags = "任务包管理")
@RequestMapping("/ccm/taskpackagemanagement")
public class TaskPackageController {

    @Autowired
    private ICCMTaskPackageService taskPackageService;
    @Autowired
    private ISchemaBusinessService schemaBusinessService;
    @Autowired
    private ICCMPackageService packageService;

    /* ******************************************************* 任务包方法 Start ******************************************************* */
    @ApiOperation("获取任务包列表")
    @RequestMapping(value = "/taskPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "page", value = "分页参数", example = "", required = true, dataTypeClass = PageRequest.class)}))
    public String getTaskPackages(@RequestBody JSONObject requestBody) {
        Result<List<ObjectDTO>> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(requestBody);
            IObjectCollection taskPackages = this.taskPackageService.getTaskPackages(pageRequest);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(taskPackages);
            result.setResult(objectDTOS);
        } catch (Exception exception) {
            exception.printStackTrace();
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("新增任务包")
    @RequestMapping(value = "/createTaskPackage", method = RequestMethod.POST)
    public String createTaskPackage(@RequestBody JSONObject jsonObject) {
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
                log.error("新增任务包失败!{}", ExceptionUtil.getMessage(exception));
                result.errorResult("新增任务包失败,数据写入失败!请检查必填参数或唯一参数!");
                result.setCode(10000);
                if (exception.getMessage().contains("唯一索引") || exception.getMessage().contains("unique index")) {
                    result.errorResult("新增任务包失败,编号禁止重复!");
                    result.setCode(10001);
                }
            } else {
                log.error("新增任务包失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
                result.errorResult("新增任务包失败!" + ExceptionUtil.getMessage(exception));
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("删除任务包")
    @RequestMapping(value = "/deleteTaskPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String deleteTaskPackage(@RequestBody JSONObject requestBody) throws Exception {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            String taskPackageId = CommonUtility.getId(requestBody);
            this.taskPackageService.deleteTaskPackage(taskPackageId);
            flag = true;
        } catch (Exception exception) {
            exception.printStackTrace();
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


    @ApiOperation("更新任务包")
    @RequestMapping(value = "/updateTaskPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String updateTaskPackage(@RequestBody JSONObject taskPackage) throws Exception {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            this.taskPackageService.updateTaskPackage(CommonUtility.parseObjectDTOFromJSON(taskPackage));
            flag = true;
        } catch (Exception exception) {
            exception.printStackTrace();
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
    /* ******************************************************* 任务包方法 End ******************************************************* */
    /* ******************************************************* 任务包-材料方法 Start ******************************************************* */

    /**
     * 获取和任务包相同阶段并且有材料消耗的设计数据
     *
     * @return
     * @throws Exception
     */
    @ApiOperation("获取和任务包相同阶段并且有材料消耗的设计数据")
    @RequestMapping(value = "/getDesignDataByPurposeAndConsumeMaterial", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "OBID", example = "task package's OBID in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "classDefinitionUID", value = "CCMSupport", example = "CCMSupport", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getDesignDataByPurposeAndConsumeMaterial(@RequestBody JSONObject jsonObject) {
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
                IObjectCollection packageId = this.taskPackageService.getDesignDataByPurposeAndConsumeMaterial(id, classDefinitionUID, pageRequest);
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
            log.error("获取和任务包相同阶段并且有材料消耗的设计数据失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("获取和任务包相同阶段并且有材料消耗的设计数据失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    /* ******************************************************* 任务包-材料方法 End ******************************************************* */
    /* ******************************************************* 任务包-图纸方法 Start ******************************************************* */

    @ApiOperation("获取任务包下升版图纸")
    @RequestMapping(value = "/getTPRelatedRevisedDocuments", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "task package DTO 's ID", example = "taskPackageOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getTPRelatedRevisedDocuments(@RequestBody JSONObject jsonObject) throws Exception {
        ResultVo<ObjectDTOCollection> result = new ResultVo<ObjectDTOCollection>();
        try {
            String workPackageId = CommonUtility.getId(jsonObject);
            if (org.apache.commons.lang3.StringUtils.isBlank(workPackageId)) {
                result.errorResult("任务包OBID不可以为空!");
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
            IObjectCollection relatedRevisedDocuments = taskPackageService.getRelatedRevisedDocuments(workPackageId, pageRequest);
            if (null != formBase) {
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
                    result.setMessage("获取任务包下升版图纸成功!");
                } else {
                    result.successResult(null);
                    result.setMessage("获取任务包下升版图纸成功,未找到对应数据.");
                }
            }
        } catch (Exception exception) {
            log.error("获取任务包下升版图纸失败!{}", ExceptionUtil.getMessage(exception));
            result.errorResult("获取任务包下升版图纸失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("移除任务包下图纸")
    @RequestMapping(value = "/removeDocumentsFromTaskPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageId", value = "OBID", example = "task package's OBID in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "documentsId", value = "OBID", example = "document's OBID join with comma", required = true, dataTypeClass = String.class)}))
    public String removeDocumentsFromPackage(@RequestBody JSONObject pobjJson) {
        Result result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            result.setSuccess(this.taskPackageService.removeDocumentsFromTaskPackage(pobjJson.getString("packageId"), pobjJson.getString("documentsId")));
        } catch (Exception exception) {
            result.setMessage(exception.getMessage());
            result.setSuccess(false);
            result.setCode(200);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取任务包可选择添加的图纸")
    @RequestMapping(value = "/getSelectableDocumentsForTaskPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "UUID", example = "task package's id in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getSelectableDocumentsForTaskPackage(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<ObjectDTOCollection>();
        try {
            String id = CommonUtility.getId(jsonObject);
            if (org.apache.commons.lang3.StringUtils.isBlank(id)) {
                result.errorResult("任务包OBID不可以为空!");
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
            if (formPurpose != null) {
                IObjectCollection selectableDocumentsForTaskPackage = this.taskPackageService.getSelectableDocumentsForTaskPackage(id, filtersParam, orderByParam, pageRequest);
                if (selectableDocumentsForTaskPackage != null && selectableDocumentsForTaskPackage.hasValue()) {
                    List<ObjectDTO> collections = new ArrayList<>();
                    Iterator<IObject> objectIterator = selectableDocumentsForTaskPackage.GetEnumerator();
                    while (objectIterator.hasNext()) {
                        IObject next = objectIterator.next();
                        ObjectDTO currentForm = formBase.copyTo();
                        next.fillingForObjectDTO(currentForm);
                        collections.add(currentForm);
                    }
                    ObjectDTOCollection oc = new ObjectDTOCollection(collections);
                    oc.setCurrent(selectableDocumentsForTaskPackage.PageResult().getCurrent());
                    oc.setSize(selectableDocumentsForTaskPackage.PageResult().getSize());
                    oc.setTotal(selectableDocumentsForTaskPackage.PageResult().getTotal());
                    result.successResult(oc);
                    result.setMessage("retrieved object(s) with " + DocumentUtils.CCM_DOCUMENT + " succeeded");
                } else {
                    result.successResult(null);
                    result.setMessage("retrieved object(s) with " + DocumentUtils.CCM_DOCUMENT + ", and result is nothing.");
                }
            }
        } catch (Exception exception) {
            log.error("获取任务包可选择添加的图纸失败!{}", ExceptionUtil.getMessage(exception));
            exception.printStackTrace();
            result.errorResult(ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }


    @ApiOperation("Get Document Form")
    @RequestMapping(value = "/documentForm", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String getDocumentForm(@RequestBody JSONObject pobjJson) {
        Result<ObjectDTO> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String formPurpose = CommonUtility.getFormPurpose(pobjJson);
            IObject documentForm = this.taskPackageService.getDocumentForm(operationPurpose.valueOf(formPurpose), CommonUtility.parseObjectDTOFromJSON(pobjJson));
            result.setResult(documentForm.toObjectDTO());
        } catch (Exception exception) {
            exception.printStackTrace();
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }


    @ApiOperation("获取任务包下图纸")
    @RequestMapping(value = "/relatedDocumentsFromTaskPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "task package DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "page", value = "分页参数", example = "", required = true, dataTypeClass = PageRequest.class)}))
    public String getRelatedDocuments(@RequestBody JSONObject requestBody) throws Exception {
        Result<List<ObjectDTO>> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String taskPackageId = CommonUtility.getId(requestBody);
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(requestBody);
            IObjectCollection ccmTaskPackageDocuments = this.taskPackageService.getRelatedDocuments(taskPackageId);
            result.setResult(ObjectDTOUtility.convertToObjectDTOList(ccmTaskPackageDocuments));
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage(), exception);
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(exception.getMessage());
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("Assign Documents into Task Package")
    @RequestMapping(value = "/assignDocumentsIntoTaskPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "task package id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "objs", required = true, dataTypeClass = ObjectDTO[].class)}))
    public String assignDocumentsIntoTaskPackage(@RequestBody JSONObject documentDTOs) throws Exception {
        Result result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String taskPackageId = CommonUtility.getId(documentDTOs);
            List<ObjectDTO> selectedDocuments = CommonUtility.parseObjectDTOsFromJSON(documentDTOs);
            this.taskPackageService.assignDocumentsToTaskPackage(taskPackageId, selectedDocuments);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage(), exception);
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(exception.getMessage());
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }
    /* ******************************************************* 任务包-图纸方法 End ******************************************************* */
    /* ******************************************************* 任务包-树方法 Start ******************************************************* */

    /**
     * 自定义层级 - 添加属性
     *
     * @return
     */
    @ApiOperation(value = "Get Task Package Doc Properties", notes = "获取目录树任务包属性定义集合")
    @RequestMapping(value = "/getTaskPackageProperties", method = RequestMethod.POST)
    public String getTaskPackageProperties() {
        Result<List<ObjectDTO>> result = new Result<>();
        try {
            List<ObjectDTO> taskPackageFormPropertiesForConfigurationItem = taskPackageService.getTaskPackageFormPropertiesForConfigurationItem();
            result.setResult(taskPackageFormPropertiesForConfigurationItem);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            result.setMessage(e.getLocalizedMessage());
        }
        return JSON.toJSONString(result, SerializerFeature.IgnoreErrorGetter);
    }

    @ApiOperation(value = "获取任务包树配置及配置项表单", notes = "获取任务包树配置及配置项表单")
    @RequestMapping(value = "/getTaskPackageHierarchyConfigurationFormWithItem", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class)
    }))
    public String getTaskPackageHierarchyConfigurationFormWithItem(@RequestBody JSONObject requestBody) {
        Result<Map<String, Object>> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String formPurpose = CommonUtility.getFormPurpose(requestBody);
            Map<String, Object> taskPackageHierarchyConfigurationFormWithItem = taskPackageService.getTaskPackageHierarchyConfigurationFormWithItem(formPurpose);
            result.setResult(taskPackageHierarchyConfigurationFormWithItem);
        } catch (Exception exception) {
            result.error500(ExceptionUtil.getMessage(exception));
            result.setSuccess(false);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "Get Task Package Properties Form", notes = "获取任务包属性关联的form")
    @PostMapping("/getTaskPackagePropertiesForm")
    public String getTaskPackagePropertiesForm() {
        String lstrClassDefName = "CCMTaskPackage";
        String lstrPurpose = "create";
        Result<ObjectDTO> result = new Result<>();
        ObjectDTO lobjDTO = null;
        try {

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
            result.setMessage(e.getLocalizedMessage());
        }
        return JSON.toJSONString(result, SerializerFeature.IgnoreErrorGetter);
    }

    /**
     * 获取当前用户树层级配置
     *
     * @return
     */
    @ApiOperation(value = "Get My Task Package Hierarchy Configurations", notes = "获取当前用户任务包树层级配置")
    @RequestMapping(value = "/getMyTaskPackageHierarchyConfigurations", method = RequestMethod.POST)
    public String getMyTaskPackageHierarchyConfigurations(HttpServletRequest request) {
        Result<List<ObjectDTO>> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            IObjectCollection myTaskPackageHierarchyConfigurations = this.taskPackageService.getMyTaskPackageHierarchyConfigurations(request, new PageRequest(0, 0));
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(myTaskPackageHierarchyConfigurations);
            result.setResult(objectDTOS);
        } catch (Exception exception) {
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "Get Task Package Hierarchy Configuration Form", notes = "获取任务包树层级配置表单")
    @RequestMapping(value = "/getTaskPackageHierarchyConfigurationForm", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String getTaskPackageHierarchyConfigurationForm(@RequestBody JSONObject requestBody) {
        Result<ObjectDTO> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String formPurpose = CommonUtility.getFormPurpose(requestBody);
            IObject taskPackageHierarchyConfigurationForm = this.taskPackageService.getTaskPackageHierarchyConfigurationForm(operationPurpose.valueOf(formPurpose), CommonUtility.parseObjectDTOFromJSON(requestBody));

            result.setResult(taskPackageHierarchyConfigurationForm.toObjectDTO());
        } catch (Exception exception) {
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    /**
     * 自定义层级 - 添加
     *
     * @return
     */
    @ApiOperation(value = "新增任务包树层级配置及配置项", notes = "新增任务包树层级配置及配置项")
    @RequestMapping(value = "/createTaskPackageHierarchyConfigurationWithItems", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "obj", value = "object DTO to be updated", example = "{\n" +
                    "        \"items\": []\n" +
                    "    }", required = true, dataTypeClass = JSONObject.class),
            @DynamicParameter(name = "objItems", value = "object DTO to be updated", example = "[\n" +
                    "        {\"items\": []\n}," +
                    "        {\"items\": []\n}" +
                    "    ]", required = true, dataTypeClass = JSONObject.class)}))
    public String createTaskPackageHierarchyConfigurationWithItems(@RequestBody JSONObject requestBody) {
        ResultVo<String> result = new ResultVo<>();
        try {
            IObject taskPackageHierarchyConfigurationWithItems = this.taskPackageService.createTaskPackageHierarchyConfigurationWithItems(requestBody);
            result.successResult(taskPackageHierarchyConfigurationWithItems.OBID());
        } catch (Exception exception) {
            log.error("新增任务包树层级配置及配置项失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("新增任务包树层级配置及配置项失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "Delete Task Package Hierarchy Configuration", notes = "删除层级配置")
    @RequestMapping(value = "/deleteTaskPackageHierarchyConfiguration", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String deleteTaskPackageHierarchyConfiguration(@RequestBody JSONObject requestBody) {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            String priorityId = CommonUtility.getId(requestBody);
            this.taskPackageService.deleteTaskPackageHierarchyConfiguration(priorityId);
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

    @ApiOperation(value = "Update Hierarchy Configuration", notes = "更新层级配置")
    @RequestMapping(value = "/updateHierarchyConfiguration", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String updateHierarchyConfiguration(@RequestBody JSONObject hierarchyConfiguration) {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        result.setCode(200);
        try {
            this.taskPackageService.updateTaskPackageHierarchyConfiguration(CommonUtility.parseObjectDTOFromJSON(hierarchyConfiguration));
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            if (flag) {
                result.setSuccess(true);
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
    @ApiOperation(value = "Get Task Package Hierarchy Configuration Items", notes = "获取层级配置下配置项")
    @RequestMapping(value = "/getTaskPackageHierarchyConfigurationItems", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String getTaskPackageHierarchyConfigurationItems(@RequestBody JSONObject requestBody) {
        ResultVo<List<ObjectDTO>> result = new ResultVo();
        try {
            String hierarchyConfigurationId = CommonUtility.getId(requestBody);
            IObjectCollection taskPackageHierarchyConfigurationItems = this.taskPackageService.getTaskPackageHierarchyConfigurationItems(hierarchyConfigurationId);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(taskPackageHierarchyConfigurationItems);
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
    @ApiOperation(value = "Create Task Package Hierarchy Configuration Item", notes = "新增层级配置项")
    @RequestMapping(value = "/createTaskPackageHierarchyConfigurationItem", method = RequestMethod.POST)
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
    public String createTaskPackageHierarchyConfigurationItem(@RequestBody JSONObject requestBody) {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            String obid = CommonUtility.getId(requestBody);
            IObject taskPackageHierarchyConfigurationItem = this.taskPackageService.createTaskPackageHierarchyConfigurationItem(obid, CommonUtility.parseObjectDTOFromJSON(requestBody));
            ObjectDTO objectDTO = taskPackageHierarchyConfigurationItem.toObjectDTO();
            result.setResult(objectDTO.getObid());
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
     * 设计数据浏览 - 设计数据目录树
     * 根据图纸和配置获取设计数据浏览目录树
     *
     * @param pobjJson
     * @return
     */
    @ApiOperation(value = "根据任务包和配置获取设计数据浏览目录树", notes = "根据任务包和配置获取设计数据浏览目录树")
    @RequestMapping(value = "/generateHierarchyByTaskPackagesAndConfiguration", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "configurationId", value = "目录树配置规则ID", example = "X000111", required = true, dataTypeClass = String.class)}))
    public String generateHierarchyByTaskPackagesAndConfiguration(@RequestBody JSONObject pobjJson) {
        ResultVo<HierarchyObjectDTO> result = new ResultVo<>();
        String lstrConfigurationId = pobjJson.getString("configurationId");
        try {
            HierarchyObjectDTO ccmPriority = taskPackageService.generateHierarchyByTaskPackagesAndConfiguration(lstrConfigurationId);
            result.successResult(ccmPriority);
        } catch (Exception exception) {
            log.error("根据任务包和配置获取目录树失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("根据任务包和配置获取目录树失败!" + ExceptionUtil.getMessage(exception));
        }

        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 点击目录树叶节点获取对应Task Packages
     *
     * @param selectedNode
     * @return
     */
    @ApiOperation(value = "Get Task Packages From HierarchyNode", notes = "获取目录树任务包信息")
    @RequestMapping(value = "/getTaskPackagesFromHierarchyNode", method = RequestMethod.POST)
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
    public String getTaskPackagesFromHierarchyNode(@RequestBody JSONObject selectedNode) {
        Result<List<ObjectDTO>> result = new Result<>();
        boolean flag = false;
        String message = "";
        List<ObjectDTO> documentsFromHierarchyNode = new ArrayList<>();
        try {
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(selectedNode);
            HierarchyObjectDTO hierarchyObjectDTO = CommonUtility.parseHierarchyObjectDTOFromJSON(selectedNode);
            taskPackageService.getTaskPackagesFromHierarchyNode(hierarchyObjectDTO, pageRequest);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            if (flag) {
                result.setSuccess(true);
                result.setCode(200);
                result.setResult(documentsFromHierarchyNode);
            } else {
                result.setCode(200);
                result.setSuccess(false);
                result.setMessage(message);
            }
        }
        return JSON.toJSONString(result);
    }

    /* ******************************************************* 任务包-树方法 End ******************************************************* */
    /* ******************************************************* 任务包-父计划方法 Start ******************************************************* */
    @ApiOperation("获取父计划和任务包属性")
    @RequestMapping(value = "/getTaskPackageFatherPlan", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "UUID", example = "work package's id in database", required = true, dataTypeClass = String.class)}))
    public String getTaskPackageFatherPlan(@RequestBody JSONObject pobjJson) {
        Result<Map<String, Object>> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String id = CommonUtility.getId(pobjJson);
            Map<String, Object> items = this.taskPackageService.getTaskPackageFatherPlan(id);
            result.setResult(items);
        } catch (Exception exception) {
            exception.printStackTrace();
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("刷新任务包计划权重")
    @RequestMapping(value = "/refreshTaskPackagePlanWeight", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "UUID", example = "work package's id in database", required = true, dataTypeClass = String.class)}))
    public String refreshTaskPackagePlanWeight(@RequestBody JSONObject pobjJson) {
        Result<Double> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String id = CommonUtility.getId(pobjJson);
            Double planWeight = this.taskPackageService.refreshPlanWeight(id);
            result.setResult(planWeight);
        } catch (Exception exception) {
            exception.printStackTrace();
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("刷新任务包进度")
    @RequestMapping(value = "/refreshTaskPackageProgress", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "OBID", example = "task package's obid in database", required = true, dataTypeClass = String.class)}))
    public String refreshTaskPackageProgress(@RequestBody JSONObject pobjJson) {
        Result<Double> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String id = CommonUtility.getId(pobjJson);
            Double planWeight = this.taskPackageService.newRefreshProgress(id);
            result.setResult(planWeight);
        } catch (Exception exception) {
            exception.printStackTrace();
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    /* ******************************************************* 任务包-父计划方法 End ******************************************************* */
    /* ******************************************************* 任务包-预测预留方法 Start ******************************************************* */
    @ApiOperation(value = "基于整张图纸的任务包预测/预留并获取预测结果", notes = "检测图纸是否存在,不存在则提示SPM不存在此图纸,存在的进行预测预留操作")
    @PostMapping("existAndCreateNewStatusRequest")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "projectId", value = "目前固定一个项目号 POC_A", example = "POC_A", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestName", value = "任务包的Name", required = true, dataTypeClass = String.class),
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
            Map<String, Object> procedureResults = taskPackageService
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

    @ApiOperation(value = "任务包部分预测/预留并获取预测结果", notes = "检测图纸是否存在,不存在则提示SPM不存在此图纸,存在的进行预测预留操作")
    @PostMapping("existAndCreatePartialStatusRequestForTP")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageId", value = "任务包的OBID", example = "TPOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "projectId", value = "从项目配置中获取", example = "POC_A", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestName", value = "预测单号", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "drawingNumbers", value = "用\",\"拼接的图纸OBID", example = "docOBID01,docOBID02", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "warehouses", value = "仓库", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "searchColumn", dataTypeClass = String.class),
            @DynamicParameter(name = "searchValue", dataTypeClass = String.class)
    }))
    public String existAndCreatePartialStatusRequestForTP(@RequestBody JSONObject jsonObject) {
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
            if (StringUtils.isEmpty(projectId)) {
                throw new RuntimeException("获取项目配置的项目号失败!");
            }
            // 存储过程类型
            IProperty procedureTypeProperty = icimProjectConfig.getProperty(ICIMProjectConfigUtils.PROCEDURE_TYPE);
            if (null == procedureTypeProperty) {
                throw new RuntimeException("获取项目配置的存储过程类型失败!");
            }
            Object value = procedureTypeProperty.Value();
            if (value == null){
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

            // 使用项目配置切换请求的存储过程
            // Map<String, Object> procedureResults = taskPackageService.existAndCreatePartialStatusRequest33(packageId, projectId, requestName, requestType, "", drawingNumbers, searchColumn, searchValue);
            Map<String, Object> procedureResults = packageService
                    .existAndCreateRequest(PackageTypeEnum.TP, packageId, projectId, requestName, requestType,
                            warehouses, drawingNumbers, searchColumn, searchValue, procedureType);

            JSONArray data = (JSONArray) procedureResults.get("data");
            result.setTotal(data.size());
            result.successResult(procedureResults);
        } catch (Exception exception) {
            log.error("任务包部分预测/预留并获取预测结果失败!{}{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("任务包部分预测/预留并获取预测结果失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "任务包部分预测/预留并获取预测结果33", notes = "检测图纸是否存在,不存在则提示SPM不存在此图纸,存在的进行预测预留操作33")
    @PostMapping("existAndCreatePartialStatusRequestForTP33")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageId", value = "任务包的OBID", example = "TPOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "projectId", value = "目前固定一个项目号 POC_A", example = "POC_A", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestName", value = "预测单号", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "drawingNumbers", value = "用\",\"拼接的图纸OBID", example = "docOBID01,docOBID02", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "searchColumn", dataTypeClass = String.class),
            @DynamicParameter(name = "searchValue", dataTypeClass = String.class)
    }))
    public String existAndCreatePartialStatusRequestForTP33(@RequestBody JSONObject jsonObject) {
        ResultVo<Map<String, Object>> result = new ResultVo<Map<String, Object>>();
        try {
            String packageId = jsonObject.getString("packageId");
            String projectId = jsonObject.getString("projectId");
            String requestName = jsonObject.getString("requestName");
            String requestType = jsonObject.getString("requestType");
            String warehouses = jsonObject.getString("warehouses");
            String searchColumn = jsonObject.getString("searchColumn");
            String searchValue = jsonObject.getString("searchValue");
            String drawingNumbers = jsonObject.getString("drawingNumbers") == null ? "" : jsonObject.getString("drawingNumbers");

            Map<String, Object> procedureResults = taskPackageService
                    .existAndCreatePartialStatusRequest33(packageId, projectId, requestName, requestType, warehouses, drawingNumbers, searchColumn, searchValue);
            result.successResult(procedureResults);
        } catch (Exception exception) {
            log.error("任务包部分预测/预留并获取预测33结果失败!{}{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("任务包部分预测/预留并获取预测结果33失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "任务包按阶段进行材料预测预留并获取预测结果", notes = "检测图纸是否存在,不存在则提示SPM不存在此图纸,存在的进行预测预留操作")
    @PostMapping("createFAWithExtraFilterForTP")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageId", value = "任务包的OBID", example = "TPOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "projectId", value = "目前固定一个项目号 POC_A", example = "POC_A", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "lpAttrCode", value = "如果为空则默认填写 LP_ATTR_CODE", example = "LP_ATTR_CODE", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestName", value = "预测单号", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "drawingNumbers", value = "用\",\"拼接的图纸OBID", example = "docOBID01,docOBID02", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "searchColumn", dataTypeClass = String.class),
            @DynamicParameter(name = "searchValue", dataTypeClass = String.class)
    }))
    public String createFAWithExtraFilterForTP(@RequestBody JSONObject jsonObject) {

        ResultVo<Map<String, Object>> result = new ResultVo<Map<String, Object>>();
        try {
            String packageId = jsonObject.getString("packageId");
            String projectId = jsonObject.getString("projectId");
            String requestName = jsonObject.getString("requestName");
            String requestType = jsonObject.getString("requestType");
            String searchColumn = jsonObject.getString("searchColumn");
            String searchValue = jsonObject.getString("searchValue");
            String drawingNumbers = jsonObject.getString("drawingNumbers") == null ? "" : jsonObject.getString("drawingNumbers");
            String lpAttrCode = StringUtils.isEmpty(jsonObject.getString("lpAttrCode")) ? "LP_ATTR_CODE" : jsonObject.getString("lpAttrCode");

            Map<String, Object> procedureResults = taskPackageService
                    .createFAWithExtraFilter(packageId, projectId, lpAttrCode, requestName, requestType, drawingNumbers, searchColumn, searchValue);
            result.successResult(procedureResults);
        } catch (Exception exception) {
            log.error("任务包按阶段进行材料预测预留并获取预测结果失败!{}{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("任务包按阶段进行材料预测预留并获取预测结果失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }
    /* ******************************************************* 任务包-预测预留方法 End ******************************************************* */
    /* ******************************************************* 任务包-升版方法 Start ******************************************************* */

    /**
     * 任务包升版处理
     */
    @ApiOperation(value = "任务包升版处理", notes = "任务包升版处理")
    @PostMapping("taskPackageRevisionHandler")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "任务包的OBID", example = "TPOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "mode", value = "ELT_PackageRevProcessingMode:EN_DeleteMode-批量删除工作步骤,EN_UpdateMode-批量关联工作步骤,EN_DeleteUpdateMode-批量删除并关联工作步骤",
                    example = "EN_DeleteMode", required = true, dataTypeClass = String.class)
    }))
    public String taskPackageRevisionHandler(@RequestBody JSONObject jsonObject) {
        ResultVo<Object> result = new ResultVo<>();
        String id = CommonUtility.getId(jsonObject);
        String mode = jsonObject.getString("mode");
        try {
            taskPackageService.taskPackageRevisionHandler(id, PackageRevProcessingMode.valueOf(mode));
            result.successResult(true);
        } catch (Exception exception) {
            log.error("任务包升版处理失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("任务包升版处理失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }
    /* ******************************************************* 任务包-升版方法 End ******************************************************* */
}
