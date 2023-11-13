package ccm.server.controller;

import ccm.server.business.IHierarchyService;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
import ccm.server.module.vo.ResultVo;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.util.CommonUtility;
import ccm.server.utils.ObjectDTOUtility;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 目录树通用方法
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/12/14 13:51
 */
@RestController
@RequestMapping("/cim/hierarchy")
@Api(tags = "目录树通用方法")
@Slf4j
public class CIMHierarchyController {

    @Autowired
    private IHierarchyService hierarchyService;

    /**
     * 类型参数检查
     *
     * @param requestBody
     */
    private String classDefCheck(JSONObject requestBody) {
        if (StringUtils.isBlank(requestBody.getString("classDef"))) {
            throw new RuntimeException("类型(classDef)不可为空!");
        }
        return requestBody.getString("classDef");
    }

    @ApiOperation(value = "获取类型对象属性定义集合", notes = "获取类型对象属性定义集合")
    @RequestMapping(value = "/getObjProperties", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "GetObjProperties", properties = {
            @DynamicParameter(name = "classDef", value = "类型名称", example = "CIMDocumentMaster", required = true, dataTypeClass = String.class)
    }))
    public String getObjProperties(@RequestBody JSONObject requestBody) {
        ResultVo<List<ObjectDTO>> result = new ResultVo<>();
        String classDef = classDefCheck(requestBody);
        try {
            List<ObjectDTO> objProperties = hierarchyService.getObjectFormPropertiesForConfigurationItem(classDef);
            result.successResult(objProperties);
        } catch (Exception e) {
            log.error("获取类型对象({})属性定义集合失败!{}", classDef, ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取类型对象(" + classDef + ")属性定义集合失败!" + ExceptionUtil.getSimpleMessage(e));
        }
        return JSON.toJSONString(result, SerializerFeature.IgnoreErrorGetter);
    }

    @ApiOperation(value = "获取当前用户的类型对象树层级配置", notes = "获取当前用户的类型对象树层级配置")
    @RequestMapping(value = "/getMyHierarchyConfigurations", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "GetMyHierarchyConfigurations", properties = {
            @DynamicParameter(name = "classDef", value = "类型名称", example = "CIMDocumentMaster", required = true, dataTypeClass = String.class)
    }))
    public String getMyHierarchyConfigurations(@RequestBody JSONObject requestBody, HttpServletRequest request) {
        ResultVo<List<ObjectDTO>> result = new ResultVo<>();
        String classDef = classDefCheck(requestBody);
        try {
            IObjectCollection myHierarchyConfigurations = hierarchyService.getMyHierarchyConfigurations(request, classDef, new PageRequest(0, 0));
            List<ObjectDTO> objectDTOS = myHierarchyConfigurations.toObjectDTOs();
            result.successResult(objectDTOS);
        } catch (Exception e) {
            log.error("获取当前用户的类型对象({})树层级配置失败!{}", classDef, ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取当前用户的类型对象(" + classDef + ")树层级配置失败!" + ExceptionUtil.getSimpleMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "获取层级配置下配置项", notes = "获取层级配置下配置项")
    @RequestMapping(value = "/getHierarchyConfigurationItems", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "GetHierarchyConfigurationItems", properties = {
            @DynamicParameter(name = "id", value = "配置OBID", example = "配置OBID", required = true, dataTypeClass = String.class)}))
    public String getHierarchyConfigurationItems(@RequestBody JSONObject requestBody) {
        ResultVo<List<ObjectDTO>> result = new ResultVo<>();
        try {
            String hierarchyConfigurationId = CommonUtility.getId(requestBody);
            IObjectCollection hierarchyConfigurationItems = hierarchyService.getItemsByHierarchyConfigurationOBID(hierarchyConfigurationId);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(hierarchyConfigurationItems);
            result.successResult(objectDTOS);
        } catch (Exception exception) {
            log.error("获取层级配置下配置项失败!{}", ExceptionUtil.getSimpleMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("获取层级配置下配置项失败!" + ExceptionUtil.getSimpleMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "获取树配置及配置项表单", notes = "获取树配置及配置项表单")
    @RequestMapping(value = "/getHierarchyConfigurationFormWithItem", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "GetHierarchyConfigurationFormWithItem", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "classDef", value = "类型名称", example = "CIMDocumentMaster", required = true, dataTypeClass = String.class)
    }))
    public String getHierarchyConfigurationFormWithItem(@RequestBody JSONObject requestBody) {
        ResultVo<Map<String, Object>> result = new ResultVo<>();
        String classDef = classDefCheck(requestBody);
        String formPurpose = CommonUtility.getFormPurpose(requestBody);
        if (StringUtils.isBlank(formPurpose)) {
            formPurpose = "create";
        }
        try {
            Map<String, Object> documentHierarchyConfigurationFormWithItem = hierarchyService.getObjectHierarchyConfigurationFormWithItem(formPurpose, classDef);
            result.successResult(documentHierarchyConfigurationFormWithItem);
        } catch (Exception exception) {
            log.error("获取类型对象({})树配置及配置项表单({})失败!{}", classDef, formPurpose, ExceptionUtil.getSimpleMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("获取类型对象(" + classDef + ")树配置及配置项表单(" + formPurpose + ")失败!" + ExceptionUtil.getSimpleMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "新增类型对象层级配置及配置项", notes = "新增类型对象层级配置及配置项")
    @RequestMapping(value = "/createHierarchyConfigurationWithItems", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "CreateHierarchyConfigurationWithItems", properties = {
            @DynamicParameter(name = "classDef", value = "类型名称", example = "CIMDocumentMaster", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "obj", value = "树配置参数", example = "{\n" +
                    "        \"items\": []\n" +
                    "    }", required = true, dataTypeClass = JSONObject.class),
            @DynamicParameter(name = "objItems", value = "树配置项参数", example = "[\n" +
                    "        {\"items\": []\n}," +
                    "        {\"items\": []\n}" +
                    "    ]", required = true, dataTypeClass = JSONObject.class)
    }))
    public String createHierarchyConfigurationWithItems(@RequestBody JSONObject requestBody) {
        ResultVo<String> result = new ResultVo<>();
        String classDef = classDefCheck(requestBody);
        try {
            IObject hierarchyConfigurationWithItems = hierarchyService.createHierarchyConfigurationWithItems(requestBody, classDef, true);
            result.successResult(hierarchyConfigurationWithItems.OBID());
        } catch (Exception exception) {
            log.error("新增类型对象({})层级配置及配置项失败!{}", classDef, ExceptionUtil.getSimpleMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("新增类型对象(" + classDef + ")层级配置及配置项失败!" + ExceptionUtil.getSimpleMessage(exception));
        }
        return JSON.toJSONString(result);
    }


    @ApiOperation(value = "删除层级配置", notes = "删除层级配置")
    @RequestMapping(value = "/deleteHierarchyConfiguration", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "DeleteHierarchyConfiguration", properties = {
            @DynamicParameter(name = "id", value = "配置OBID", example = "配置OBID", required = true, dataTypeClass = String.class)}))
    public String deleteHierarchyConfiguration(@RequestBody JSONObject requestBody) {
        ResultVo<Boolean> result = new ResultVo<>();
        String hierarchyConfigurationOBID = CommonUtility.getId(requestBody);
        try {
            hierarchyService.deleteHierarchyConfiguration(hierarchyConfigurationOBID);
            result.successResult(true);
        } catch (Exception exception) {
            log.error("删除层级配置(id:{})失败!{}", hierarchyConfigurationOBID, ExceptionUtil.getSimpleMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("删除层级配置(id:" + hierarchyConfigurationOBID + ")失败!" + ExceptionUtil.getSimpleMessage(exception));
        }
        return JSON.toJSONString(result);
    }


    @ApiOperation(value = "根据类型对象和配置生成目录树", notes = "根据类型对象和配置生成目录树")
    @RequestMapping(value = "/generateHierarchy", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "GenerateHierarchy", properties = {
            @DynamicParameter(name = "id", value = "目录树配置OBID", example = "配置OBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "classDef", value = "类型名称", example = "CIMDocumentMaster", required = true, dataTypeClass = String.class)
    }))
    public String generateHierarchy(@RequestBody JSONObject requestBody) {
        ResultVo<HierarchyObjectDTO> result = new ResultVo<>();
        String hierarchyConfigurationOBID = CommonUtility.getId(requestBody);
        String classDef = classDefCheck(requestBody);
        try {
            HierarchyObjectDTO hierarchyObjectDTO = hierarchyService.generateHierarchy(hierarchyConfigurationOBID, classDef);
            result.successResult(hierarchyObjectDTO);
        } catch (Exception exception) {
            log.error("根据类型对象({})和配置ID({})生成目录树失败!{}", classDef, hierarchyConfigurationOBID, ExceptionUtil.getSimpleMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("根据类型对象(" + classDef + ")和配置ID(" + hierarchyConfigurationOBID + ")生成目录树失败!" + ExceptionUtil.getSimpleMessage(exception));
        }
        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
    }

    @ApiOperation(value = "根据前端传入生成目录树", notes = "根据前端传入生成目录树")
    @RequestMapping(value = "/generateHierarchyWithoutConf", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "GenerateHierarchy", properties = {
            @DynamicParameter(name = "classDefinitionUid", value = "需要查询的类型UID", example = "CIMDocumentMaster", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "propertyDefinitionUids", value = "类型下需要构建成树的属性UID(按层级顺序排序,用英文逗号分隔)", example = "DesignPhase,COMArea,SNECDOCType,SNECDOCDiscipline", required = true, dataTypeClass = String.class)
    }))
    public String generateHierarchyWithoutConf(@RequestBody JSONObject requestBody) {
        ResultVo<HierarchyObjectDTO> result = new ResultVo<>();
        String classDefinitionUid = requestBody.getString("classDefinitionUid");
        if (StringUtils.isBlank(classDefinitionUid)) {
            result.errorResult("需要查询的类型不可为空!classDefinitionUid=" + classDefinitionUid);
            return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
        }
        String propertyDefinitionUids = requestBody.getString("propertyDefinitionUids");
        if (StringUtils.isBlank(propertyDefinitionUids)) {
            result.errorResult("需要查询的属性不可为空!propertyDefinitionUids=" + propertyDefinitionUids);
            return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
        }
        String[] propertyDefinitionUidsArray = propertyDefinitionUids.split(",");
        try {
            HierarchyObjectDTO hierarchyObjectDTO = hierarchyService.generateHierarchyWithoutConf(classDefinitionUid, propertyDefinitionUidsArray);
            result.successResult(hierarchyObjectDTO);
        } catch (Exception exception) {
            log.error("根据前端传入生成目录树生成目录树失败!类型:{},属性:{},异常信息:{}", classDefinitionUid, Arrays.toString(propertyDefinitionUidsArray), ExceptionUtil.getSimpleMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("根据前端传入生成目录树失败!类型:" + classDefinitionUid + ",属性:" + Arrays.toString(propertyDefinitionUidsArray) + ",异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
    }

}
