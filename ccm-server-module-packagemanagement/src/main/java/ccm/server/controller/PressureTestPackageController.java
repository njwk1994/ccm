package ccm.server.controller;

import ccm.server.business.ICCMPressureTestPackageService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
import ccm.server.enums.PackageRevProcessingMode;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.module.vo.ResultVo;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.schema.interfaces.IObject;
import ccm.server.util.CommonUtility;
import ccm.server.utils.DocumentUtils;
import ccm.server.utils.ObjectDTOUtility;
import ccm.server.utils.PageUtility;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/1/11 14:38
 */
@Slf4j
@RestController
@Api(tags = "试压包管理")
@RequestMapping("/ccm/pressureTestPackageManagement")
public class PressureTestPackageController {

    @Autowired
    private ICCMPressureTestPackageService pressureTestPackageService;
    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    /* *****************************************  树结构方法 start  ***************************************** */

    /**
     * 获取当前用户树层级配置
     *
     * @return
     */
    @ApiOperation(value = "获取当前用户试压包树层级配置", notes = "获取当前用户试压包树层级配置")
    @RequestMapping(value = "/getMyPressureTestPackageHierarchyConfigurations", method = RequestMethod.POST)
    public String getMyPressureTestPackageHierarchyConfigurations(HttpServletRequest request) {
        Result<List<ObjectDTO>> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            IObjectCollection myPressureTestPackageHierarchyConfigurations = this.pressureTestPackageService.getMyPressureTestPackageHierarchyConfigurations(request, new PageRequest(0, 0));
            List<ObjectDTO> items = ObjectDTOUtility.convertToObjectDTOList(myPressureTestPackageHierarchyConfigurations);
            result.setResult(items);
        } catch (Exception exception) {
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "获取试压包树配置及配置项表单", notes = "获取任务包树配置及配置项表单")
    @RequestMapping(value = "/getPressureTestPackageHierarchyConfigurationFormWithItem", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class)
    }))
    public String getPressureTestPackageHierarchyConfigurationFormWithItem(@RequestBody JSONObject requestBody) {
        Result<Map<String, Object>> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String formPurpose = CommonUtility.getFormPurpose(requestBody);
            Map<String, Object> pressureTestPackageHierarchyConfigurationFormWithItem = pressureTestPackageService.getPressureTestPackageHierarchyConfigurationFormWithItem(formPurpose);
            result.setResult(pressureTestPackageHierarchyConfigurationFormWithItem);
        } catch (Exception exception) {
            result.error500(ExceptionUtil.getMessage(exception));
            result.setSuccess(false);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "获取目录树试压包属性定义集合", notes = "获取目录树试压包属性定义集合")
    @RequestMapping(value = "/getPressureTestPackageProperties", method = RequestMethod.POST)
    public String getTaskPackageProperties() {
        Result<List<ObjectDTO>> result = new Result<>();
        try {
            List<ObjectDTO> pressureTestPackageFormPropertiesForConfigurationItem = pressureTestPackageService.getPressureTestPackageFormPropertiesForConfigurationItem();
            result.setResult(pressureTestPackageFormPropertiesForConfigurationItem);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
            result.setMessage(e.getLocalizedMessage());
        }
        return JSON.toJSONString(result, SerializerFeature.IgnoreErrorGetter);
    }


    @ApiOperation(value = "获取试压包树层级配置表单", notes = "获取试压包树层级配置表单")
    @RequestMapping(value = "/getPressureTestPackageHierarchyConfigurationForm", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String getPressureTestPackageHierarchyConfigurationForm(@RequestBody JSONObject requestBody) {
        Result<ObjectDTO> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            /*String formPurpose = CommonUtility.getFormPurpose(requestBody);
            this.pressureTestPackageService.getPressureTestPackageHierarchyConfigurationForm(formPurpose);
            result.setResult(items);*/
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
    @ApiOperation(value = "新增试压包层级配置及配置项失败", notes = "新增试压包层级配置及配置项失败")
    @RequestMapping(value = "/createPressureTestPackageHierarchyConfigurationWithItems", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "obj", value = "object DTO to be updated", example = "{\n" +
                    "        \"items\": []\n" +
                    "    }", required = true, dataTypeClass = JSONObject.class),
            @DynamicParameter(name = "objItems", value = "object DTO to be updated", example = "[\n" +
                    "        {\"items\": []\n}," +
                    "        {\"items\": []\n}" +
                    "    ]", required = true, dataTypeClass = JSONObject.class)}))
    public String createPressureTestPackageHierarchyConfigurationWithItems(@RequestBody JSONObject requestBody) {
        ResultVo<String> result = new ResultVo<>();
        try {
            IObject pressureTestPackageHierarchyConfigurationWithItems = this.pressureTestPackageService.createPressureTestPackageHierarchyConfigurationWithItems(requestBody);
            result.successResult(pressureTestPackageHierarchyConfigurationWithItems.OBID());
        } catch (Exception exception) {
            log.error("新增试压包层级配置及配置项失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("新增试压包层级配置及配置项失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "删除层级配置", notes = "删除层级配置")
    @RequestMapping(value = "/deletePressureTestPackageHierarchyConfiguration", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String deletePressureTestPackageHierarchyConfiguration(@RequestBody JSONObject requestBody) {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            String pressureTestPackageOBID = CommonUtility.getId(requestBody);
            this.pressureTestPackageService.deletePressureTestPackageHierarchyConfiguration(pressureTestPackageOBID);
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
        boolean flag = false;
        String message = "";
        try {
            this.pressureTestPackageService.updatePressureTestPackageHierarchyConfiguration(CommonUtility.parseObjectDTOFromJSON(hierarchyConfiguration));
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
    @RequestMapping(value = "/getPressureTestPackageHierarchyConfigurationItems", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String getPressureTestPackageHierarchyConfigurationItems(@RequestBody JSONObject requestBody) {
        ResultVo<List<ObjectDTO>> result = new ResultVo();
        try {
            String hierarchyConfigurationId = CommonUtility.getId(requestBody);
            IObjectCollection pressureTestPackageHierarchyConfigurationItems = this.pressureTestPackageService
                    .getPressureTestPackageHierarchyConfigurationItems(hierarchyConfigurationId);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(pressureTestPackageHierarchyConfigurationItems);
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
    @RequestMapping(value = "/createPressureTestPackageHierarchyConfigurationItem", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "HierarchyConfiguration id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "obj", value = "object DTO to be updated", example = "    \"obj\": {\n" +
                    "        \"items\": [\n" +
                    "            {\n" +
                    "                \"defUID\": \"\",\n" +
                    "                \"displayValue\": \"\"\n" +
                    "            }\n" +
                    "        ]\n" +
                    "    }", required = true, dataTypeClass = JSONObject.class)}))
    public String createPressureTestPackageHierarchyConfigurationItem(@RequestBody JSONObject requestBody) {
        Result<String> result = new Result<String>();
        boolean flag = false;
        String message = "";
        try {
            ObjectDTO configuration = new ObjectDTO();
            String obid = CommonUtility.getId(requestBody);
            IObject pressureTestPackageHierarchyConfigurationItem = this.pressureTestPackageService.createPressureTestPackageHierarchyConfigurationItem(obid, CommonUtility.parseObjectDTOFromJSON(requestBody));
            result.setResult(pressureTestPackageHierarchyConfigurationItem.OBID());
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
     * 根据试压包和配置获取目录树
     *
     * @param pobjJson
     * @return
     */
    @ApiOperation(value = "根据试压包和配置获取目录树", notes = "根据试压包和配置获取目录树")
    @RequestMapping(value = "/generateHierarchyByPressureTestPackagesAndConfiguration", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "configurationId", value = "目录树配置规则ID", example = "X000111", required = true, dataTypeClass = String.class)}))
    public String generateHierarchyByPressureTestPackagesAndConfiguration(@RequestBody JSONObject pobjJson) {
        ResultVo<HierarchyObjectDTO> result = new ResultVo<>();
        try {
            String configurationId = pobjJson.getString("configurationId");
            HierarchyObjectDTO pressureTestPackagesHierarchy = pressureTestPackageService
                    .generateHierarchyByPressureTestPackagesAndConfiguration(configurationId);
            result.successResult(pressureTestPackagesHierarchy);
        } catch (Exception exception) {
            log.error("根据试压包和配置获取目录树失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("根据试压包和配置获取目录树失败!" + ExceptionUtil.getMessage(exception));
        }

        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 点击目录树叶节点获取对应Work Packages
     *
     * @param selectedNode
     * @return
     */
    @ApiOperation(value = "获取目录树节点试压包信息", notes = "获取目录树节点试压包信息")
    @RequestMapping(value = "/getPressureTestPackagesFromHierarchyNode", method = RequestMethod.POST)
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
    public String getPressureTestPackagesFromHierarchyNode(@RequestBody JSONObject selectedNode) {
        Result<List<ObjectDTO>> result = new Result<>();
        try {
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(selectedNode);
            IObjectCollection pressureTestPackagesFromHierarchyNode = pressureTestPackageService.getPressureTestPackagesFromHierarchyNode(CommonUtility.parseHierarchyObjectDTOFromJSON(selectedNode), pageRequest);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(pressureTestPackagesFromHierarchyNode);
            result.setSuccess(true);
            result.setCode(200);
            result.setResult(objectDTOS);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            result.setMessage(exception.toString());
            result.setSuccess(false);
        }
        return JSON.toJSONString(result);
    }
    /* *****************************************  树结构方法 end  ***************************************** */

    /* *****************************************  试压包方法 start  ***************************************** */
    @ApiOperation("获取所有试压包")
    @RequestMapping(value = "/getPressureTestPackages", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "page", value = "分页参数", example = "", required = true, dataTypeClass = PageRequest.class)}))
    public String getPressureTestPackages(@RequestBody JSONObject requestBody) {
        Result<List<ObjectDTO>> result = new Result();
        result.setCode(200);
        result.setSuccess(true);

        try {
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(requestBody);
            IObjectCollection pressureTestPackages = this.pressureTestPackageService.getPressureTestPackages(pageRequest);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(pressureTestPackages);
            result.setResult(objectDTOS);
        } catch (Exception exception) {
            exception.printStackTrace();
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(exception.toString());
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("新建试压包")
    @RequestMapping(value = "/createPressureTestPackage", method = RequestMethod.POST)
    public String createPressureTestPackage(@RequestBody JSONObject jsonObject) {
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
                result.errorResult("新增试压包失败,数据写入失败!请检查必填参数或唯一参数!");
                result.setCode(10000);
                if (exception.getMessage().contains("唯一索引") || exception.getMessage().contains("unique index")) {
                    log.error("新增试压包失败!{}", ExceptionUtil.getMessage(exception));
                    result.errorResult("新增试压包失败,编号禁止重复!");
                    result.setCode(10001);
                }
            } else {
                log.error("新增试压包失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
                result.errorResult("新增试压包失败!" + ExceptionUtil.getMessage(exception));
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("删除试压包")
    @RequestMapping(value = "/deletePressureTestPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String deletePressureTestPackage(@RequestBody JSONObject requestBody) throws Exception {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            String pressureTestPackageId = CommonUtility.getId(requestBody);
            this.pressureTestPackageService.deletePressureTestPackage(pressureTestPackageId);
            flag = true;
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage(), exception);
            message = exception.toString();
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

    @ApiOperation("更新试压包")
    @RequestMapping(value = "/updatePressureTestPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String updatePressureTestPackage(@RequestBody JSONObject taskPackage) throws Exception {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            this.pressureTestPackageService.updatePressureTestPackage(CommonUtility.parseObjectDTOFromJSON(taskPackage));
            flag = true;
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage(), exception);
            message = exception.toString();
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
    /* *****************************************  试压包方法 end  ***************************************** */

    /* *****************************************  试压包图纸方法 start  ***************************************** */
    @ApiOperation("获取图纸表单")
    @RequestMapping(value = "/documentForm", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String getDocumentForm(@RequestBody JSONObject pobjJson) {
        Result<ObjectDTO> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            /*String formPurpose = CommonUtility.getFormPurpose(pobjJson);
            ObjectDTO items = this.pressureTestPackageService.getDocumentForm(operationPurpose.valueOf(formPurpose), CommonUtility.parseObjectDTOFromJSON(pobjJson));
            result.setResult(items);*/
        } catch (Exception exception) {
            exception.printStackTrace();
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(exception.toString());
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取试压包下图纸")
    @RequestMapping(value = "/relatedDocumentsFromPressureTestPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "pressure test package DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String getRelatedDocuments(@RequestBody JSONObject requestBody) throws Exception {
        Result<List<ObjectDTO>> result = new Result<List<ObjectDTO>>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String pressureTestPackageId = CommonUtility.getId(requestBody);
            IObjectCollection relatedDocuments = this.pressureTestPackageService.getRelatedDocuments(pressureTestPackageId);
            List<ObjectDTO> items = ObjectDTOUtility.convertToObjectDTOList(relatedDocuments);
            result.setResult(items);
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

    @ApiOperation("添加图纸到试压包")
    @RequestMapping(value = "/assignDocumentsToPressureTestPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "pressure test package obid", example = "PressureTestPackageOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "documentOBIDs", value = "documents' obid with ,", example = "docOBID1,docOBID2", required = true, dataTypeClass = String.class)}))
    public String assignDocumentsToPressureTestPackage(@RequestBody JSONObject jsonObject) throws Exception {
        ResultVo<Object> result = new ResultVo<>();
        try {
            String pressureTestPackageId = CommonUtility.getId(jsonObject);
            String documentOBIDs = jsonObject.getString("documentOBIDs");
            if (!StringUtils.isEmpty(documentOBIDs)) {
                result.errorResult("参数documentOBIDs不可为空!");
            }
            List<String> strings = Arrays.asList(documentOBIDs.split(","));
            this.pressureTestPackageService.assignDocumentsToPressureTestPackage(pressureTestPackageId, strings);
            result.successResult(true);
        } catch (Exception exception) {
            log.error("添加图纸到试压包失败!{},错误信息:{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("添加图纸到试压包失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("移除试压包下图纸")
    @RequestMapping(value = "/removeDocumentsFromPressureTestPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageId", value = "UUID", example = "pressure test package's id in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "documentsId", value = "UUID", example = "document's Id join with comma", required = true, dataTypeClass = String.class)}))
    public String removeDocumentsFromPressureTestPackage(@RequestBody JSONObject pobjJson) {
        ResultVo<Object> result = new ResultVo<>();
        try {
            result.successResult(this.pressureTestPackageService.removeDocumentsFromPressureTestPackage(pobjJson.getString("packageId"), pobjJson.getString("documentsId")));
        } catch (Exception exception) {
            log.error("移除试压包下图纸失败!{},错误信息:{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("移除试压包下图纸失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取试压包可选择添加的图纸")
    @RequestMapping(value = "/getSelectableDocumentsForPressureTestPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "UUID", example = "pressure test package's id in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getSelectableDocuments(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<>();
        try {
            String id = CommonUtility.getId(jsonObject);
            if (StringUtils.isBlank(id)) {
                result.errorResult("试压包OBID不可以为空!");
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
                IObjectCollection selectableDocuments = this.pressureTestPackageService.getSelectableDocuments(id, filtersParam, orderByParam, pageRequest);
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
            log.error("试压包获取可选择图纸失败!{}", ExceptionUtil.getMessage(exception));
            exception.printStackTrace();
            result.errorResult(ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }
    /* *****************************************  试压包图纸方法 end  ***************************************** */

    /* *****************************************  试压包材料方法 start  ***************************************** */
    @ApiOperation("获取试压包下材料")
    @RequestMapping(value = "/relatedComponentsFromPressureTestPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "pressure test package DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String getRelatedComponents(@RequestBody JSONObject requestBody) throws Exception {
        Result<List<ObjectDTO>> result = new Result<List<ObjectDTO>>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String pressureTestPackageId = CommonUtility.getId(requestBody);
            IObjectCollection relatedComponents = this.pressureTestPackageService.getRelatedComponents(pressureTestPackageId);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(relatedComponents);
            result.setResult(objectDTOS);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage(), exception);
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(exception.toString());
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取试压包可选择的材料(根据已选择的图纸)")
    @RequestMapping(value = "/getSelectableComponentsForPressureTestPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageOBID", value = "UUID", example = "pressure test package's id in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "documentOBID", value = "UUID", example = "document's id in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "classDefinitionUID", value = "CCMSupport", example = "CCMSupport", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getSelectableComponents(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<ObjectDTOCollection>();
        try {
            String packageOBID = jsonObject.getString("packageOBID");
            String documentOBID = jsonObject.getString("documentOBID");
            if (StringUtils.isBlank(packageOBID) || StringUtils.isBlank(documentOBID)) {
                result.errorResult("试压包或图纸OBID不可以为空!");
                return JSON.toJSONString(result);
            }
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(jsonObject);
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            OrderByParam orderByParam = new OrderByParam(jsonObject);

            String classDefinitionUID = CommonUtility.getClassDefinitionUID(jsonObject);
            String formPurpose = CommonUtility.getFormPurpose(jsonObject);

            if (org.springframework.util.StringUtils.isEmpty(formPurpose)) {
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
                IObjectCollection selectableComponents = this.pressureTestPackageService.getSelectableComponentsForPressureTestPackage(packageOBID, documentOBID, classDefinitionUID, filtersParam, orderByParam, pageRequest);
                if (selectableComponents != null && selectableComponents.hasValue()) {
                    List<ObjectDTO> collections = new ArrayList<>();
                    Iterator<IObject> objectIterator = selectableComponents.GetEnumerator();
                    while (objectIterator.hasNext()) {
                        IObject next = objectIterator.next();
                        ObjectDTO currentForm = formBase.copyTo();
                        next.fillingForObjectDTO(currentForm);
                        collections.add(currentForm);
                    }
                    ObjectDTOCollection oc = PageUtility.pagedObjectDTOS(collections, filtersParam, orderByParam, pageRequest);
                    result.successResult(oc);
                } else {
                    result.successResult(null);
                }
            }
        } catch (Exception exception) {
            log.error("根据已选择的图纸获取试压包可选择的组件失败:{}", ExceptionUtil.getMessage(exception));
            result.errorResult("根据已选择的图纸获取试压包可选择的组件失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("添加组件到试压包")
    @RequestMapping(value = "/assignComponentsToPressureTestPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "pressure test package id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "componentOBIDs", required = true, dataTypeClass = String.class)}))
    public String assignComponentsToPressureTestPackage(@RequestBody JSONObject jsonObject) throws Exception {
        ResultVo<Object> result = new ResultVo<>();
        try {
            String pressureTestPackageId = CommonUtility.getId(jsonObject);
            String componentOBIDs = jsonObject.getString("componentOBIDs");
            List<String> strings = Arrays.asList(componentOBIDs.split(","));
            this.pressureTestPackageService.assignComponentsToPressureTestPackage(pressureTestPackageId, strings);
            result.successResult(true);
        } catch (Exception exception) {
            log.error("添加组件到试压包失败!失败信息:{},错误原因:{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCauseMessage(exception));
            result.errorResult("添加组件到试压包失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("添加组件到试压包(根据组件名称)")
    @RequestMapping(value = "/assignComponentsToPressureTestPackageByName", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "ptpOBID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "componentNames", example = "component001,component002", required = true, dataTypeClass = String.class)}))
    public String assignComponentsToPressureTestPackageByName(@RequestBody JSONObject jsonObject) throws Exception {
        ResultVo<Object> result = new ResultVo<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String pressureTestPackageId = CommonUtility.getId(jsonObject);
            String componentNames = jsonObject.getString("componentNames");
            Map<String, List<String>> latestComponentOBIDsAndDocOBIDsByNamesInShopDesignDocument = pressureTestPackageService.getLatestComponentOBIDsAndDocOBIDsByNamesInShopDesignDocument(componentNames);
            List<String> documentOBIDs = latestComponentOBIDsAndDocOBIDsByNamesInShopDesignDocument.get("document");
            List<String> designOBIDs = latestComponentOBIDsAndDocOBIDsByNamesInShopDesignDocument.get("design");
            pressureTestPackageService.assignDocumentsToPressureTestPackage(pressureTestPackageId, documentOBIDs);
            pressureTestPackageService.assignComponentsToPressureTestPackage(pressureTestPackageId, designOBIDs);
            result.successResult(true);
        } catch (Exception exception) {
            log.error("根据组件名称添加组件到试压包失败!失败信息:{},错误原因:{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("根据组件名称添加组件到试压包失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("移除试压包下材料")
    @RequestMapping(value = "/removeComponentsFromPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageId", value = "UUID", example = "pressure test package's id in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "componentsId", value = "UUID", example = "component's Id join with comma", required = true, dataTypeClass = String.class)}))
    public String removeComponentsFromPackage(@RequestBody JSONObject pobjJson) {
        Result result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            result.setSuccess(pressureTestPackageService.removeComponentsFromPackage(pobjJson.getString("packageId"), pobjJson.getString("componentsId")));
        } catch (Exception exception) {
            result.setMessage(exception.toString());
            result.setSuccess(false);
            result.setCode(200);
        }
        return JSON.toJSONString(result);
    }
    /* *****************************************  试压包材料方法 end  ***************************************** */

    /* *****************************************  试压包文件方法 start  ***************************************** */

    /**
     * 试压包上传文件
     *
     * @return
     */
    @ApiOperation("试压包上传文件")
    @PostMapping(value = "/uploadFile")
    public String uploadFile(
            @ApiParam(name = "file", value = "上传文件", required = true) MultipartFile file,
            @ApiParam(name = "bizPath", value = "文件夹路径(非必须)", required = false) String bizPath,
            @ApiParam(name = "pressureTestPackageId", value = "试压包ID", required = true) String pressureTestPackageId,
            @ApiParam(name = "fileVersion", value = "试压包文件版本", required = false) String fileVersion,
            @ApiParam(name = "fileCount", value = "试压包文件页数", required = false) String fileCount,
            @ApiParam(name = "fileNotes", value = "试压包文件备注", required = false) String fileNotes) {
        Result<String> result = new Result<>();
        try {
            ObjectDTO pressureTestPackageFile = pressureTestPackageService.toPressureTestPackageFile(fileVersion, fileCount, fileNotes);
            String file_url = pressureTestPackageService.saveFile(file, bizPath, pressureTestPackageId, pressureTestPackageFile);
            result.setResult(file_url);
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(e.getLocalizedMessage());
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    /**
     * 试压包删除文件
     *
     * @return
     */
    @ApiOperation("试压包删除文件")
    @PostMapping(value = "/deleteFile")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageId", value = "UUID", example = "pressure test package's id in database", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pressureTestPackageFileIds", value = "UUID", example = "pressureTestPackageFile's Id join with comma", required = true, dataTypeClass = String.class)
    }))
    public String deleteFile(@RequestBody JSONObject jsonObject) {
        Result<String> result = new Result<>();
        try {
            boolean b = pressureTestPackageService.deleteFile(jsonObject.getString("pressureTestPackageFileIds"));
            result.setSuccess(b);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(e.getLocalizedMessage());
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }
    /* *****************************************  试压包文件方法 end  ***************************************** */

    /* *****************************************  试压包报告方法 start  ***************************************** */

    /* *****************************************  试压包报告方法 end  ***************************************** */

    /* *****************************************  试压包审批方法 start  ***************************************** */

    /* *****************************************  试压包审批方法 end  ***************************************** */
    /* ******************************************************* 试压包-升版方法 Start ******************************************************* */

    /**
     * 试压包升版处理
     */
    @ApiOperation(value = "试压包升版处理", notes = "试压包升版处理")
    @PostMapping("pressureTestPackageRevisionHandler")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "试压包的OBID", example = "PTPOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "mode", value = "ELT_PackageRevProcessingMode:EN_DeleteMode-批量删除工作步骤,EN_UpdateMode-批量关联工作步骤,EN_DeleteUpdateMode-批量删除并关联工作步骤",
                    example = "EN_DeleteMode", required = true, dataTypeClass = String.class)
    }))
    public String pressureTestPackageRevisionHandler(@RequestBody JSONObject jsonObject) {
        ResultVo<Object> result = new ResultVo<>();
        String id = CommonUtility.getId(jsonObject);
        String mode = jsonObject.getString("mode");
        try {
            pressureTestPackageService.pressureTestPackageRevisionHandler(id, PackageRevProcessingMode.valueOf(mode));
            result.successResult(true);
        } catch (Exception exception) {
            log.error("试压包升版处理失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("试压包升版处理失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }
    /* ******************************************************* 试压包-升版方法 End ******************************************************* */
}
