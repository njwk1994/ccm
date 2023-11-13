package ccm.server.controller;

import ccm.server.business.ICCMDesignService;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
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
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/22 8:59
 */

@RestController
@RequestMapping("/ccm/designData")
@Api(tags = "设计数据浏览入口")
@Slf4j
public class CCMDesignController {

    @Autowired
    private ICCMDesignService designService;

    /* ******************************************************* 设计数据-树方法 Start ******************************************************* */

    /**
     * 设计数据浏览 - 自定义层级 - 添加属性
     *
     * @return
     */
    @ApiOperation(value = "获取目录树图纸属性定义集合", notes = "获取目录树图纸属性定义集合")
    @RequestMapping(value = "/getDocProperties", method = RequestMethod.POST)
    public String getDocProperties() {
        Result<List<ObjectDTO>> result = new Result<>();
        result.setSuccess(true);
        result.setCode(200);
        try {
            List<ObjectDTO> documentFormProperties = designService.getDocumentFormPropertiesForConfigurationItem();
            result.setResult(documentFormProperties);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return JSON.toJSONString(result, SerializerFeature.IgnoreErrorGetter);
    }

    @PostMapping("/getDocPropertiesForm")
    @ApiOperation(value = "获取图纸属性关联的form", notes = "获取图纸属性关联的form")
    public String getDocPropertiesForm() {
        Result<ObjectDTO> lobjResult = new Result<>();
        ObjectDTO lobjDTO = null;
        try {

        } catch (Exception e) {
            log.error(e.toString());
            lobjResult.error500(e.getLocalizedMessage());
        }
        return JSON.toJSONString(lobjResult, SerializerFeature.IgnoreErrorGetter);
    }

    @ApiOperation(value = "获取图纸树配置及配置项表单", notes = "获取图纸树配置及配置项表单")
    @RequestMapping(value = "/getDocumentHierarchyConfigurationFormWithItem", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class)
    }))
    public String getDocumentHierarchyConfigurationFormWithItem(@RequestBody JSONObject requestBody) {
        Result<Map<String, Object>> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String formPurpose = CommonUtility.getFormPurpose(requestBody);
            Map<String, Object> documentHierarchyConfigurationFormWithItem = designService.getDocumentHierarchyConfigurationFormWithItem(formPurpose);
            result.setResult(documentHierarchyConfigurationFormWithItem);
        } catch (Exception exception) {
            result.error500(ExceptionUtil.getMessage(exception));
            result.setSuccess(false);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "获取层级配置表单", notes = "获取层级配置表单")
    @RequestMapping(value = "/getHierarchyConfigurationForm", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class)
    }))
    public String getHierarchyConfigurationForm(@RequestBody JSONObject requestBody) {
        Result<ObjectDTO> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String formPurpose = CommonUtility.getFormPurpose(requestBody);
            ObjectDTO items = designService.getHierarchyConfigurationForm(formPurpose);
            result.setResult(items);
        } catch (Exception exception) {
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    /**
     * 设计数据浏览 - 设计数据目录树
     *
     * @param pobjJson
     * @return
     */

    @ApiOperation(value = "获取设计数据浏览目录树", notes = "获取设计数据浏览目录树")
    @RequestMapping(value = "/generateHierarchy", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "configurationId", value = "目录树配置规则OBID", example = "X000111", required = true, dataTypeClass = String.class)}))
    public String generateHierarchy(@RequestBody JSONObject pobjJson) {
        Result<HierarchyObjectDTO> result = new Result<>();
        String configurationId = pobjJson.getString("configurationId");
        try {
            HierarchyObjectDTO ccmPriority = designService.generateHierarchy(configurationId);
            result.setSuccess(true);
            result.setResult(ccmPriority);
            result.setCode(200);
        } catch (Exception exception) {
            result.error500(ExceptionUtil.getMessage(exception));
        }

        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 设计数据浏览 - 设计数据目录树
     * 根据图纸和配置获取设计数据浏览目录树
     *
     * @param pobjJson
     * @return
     */

    @ApiOperation(value = "根据图纸和配置获取设计数据浏览目录树", notes = "根据图纸和配置获取设计数据浏览目录树")
    @RequestMapping(value = "/generateHierarchyByDocumentsAndConfiguration", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "configurationId", value = "目录树配置规则OBID", example = "X000111", required = true, dataTypeClass = String.class)}))
    public String generateHierarchyByDocumentsAndConfiguration(@RequestBody JSONObject pobjJson) {
        ResultVo<HierarchyObjectDTO> result = new ResultVo<>();
        String lstrConfigurationId = pobjJson.getString("configurationId");
        try {
            HierarchyObjectDTO ccmPriority = designService.generateHierarchyByDocumentsAndConfiguration(lstrConfigurationId);
            result.successResult(ccmPriority);
        } catch (Exception exception) {
            log.error("根据图纸和配置获取设计数据浏览目录树失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("根据图纸和配置获取设计数据浏览目录树失败!" + ExceptionUtil.getMessage(exception));
        }

        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 获取当前用户树层级配置
     *
     * @return
     */

    @ApiOperation(value = "获取当前用户树层级配置", notes = "获取当前用户树层级配置")
    @RequestMapping(value = "/getMyHierarchyConfigurations", method = RequestMethod.POST)
    public String getMyHierarchyConfigurations(HttpServletRequest request) {
        Result<List<ObjectDTO>> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            IObjectCollection myHierarchyConfigurations = designService.getMyHierarchyConfigurations(request, new PageRequest(0, 0));
            List<ObjectDTO> objectDTOS = myHierarchyConfigurations.toObjectDTOs();
            result.setResult(objectDTOS);
        } catch (Exception exception) {
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    /**
     * 设计数据浏览 - 自定义层级 - 添加
     *
     * @return
     */
    @ApiOperation(value = "新增设计数据层级配置及配置项", notes = "新增设计数据层级配置及配置项")
    @RequestMapping(value = "/createHierarchyConfigurationWithItems", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "obj", value = "object DTO to be updated", example = "{\n" +
                    "        \"items\": []\n" +
                    "    }", required = true, dataTypeClass = JSONObject.class),
            @DynamicParameter(name = "objItems", value = "object DTO to be updated", example = "[\n" +
                    "        {\"items\": []\n}," +
                    "        {\"items\": []\n}" +
                    "    ]", required = true, dataTypeClass = JSONObject.class)}))
    public String createHierarchyConfigurationWithItems(@RequestBody JSONObject requestBody) {
        ResultVo<String> result = new ResultVo<>();
        try {
            IObject hierarchyConfigurationWithItems = designService.createHierarchyConfigurationWithItems(requestBody);
            result.successResult(hierarchyConfigurationWithItems.OBID());
        } catch (Exception exception) {
            log.error("新增设计数据层级配置及配置项失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("新增设计数据层级配置及配置项失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    /**
     * 设计数据浏览 - 自定义层级 - 添加
     *
     * @return
     */
    @ApiOperation(value = "新增层级配置", notes = "新增层级配置")
    @RequestMapping(value = "/createHierarchyConfiguration", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "items", value = "object DTO to be updated", example = "[{},{}]", required = true, dataTypeClass = JSONObject[].class)}))
    public String createHierarchyConfiguration(@RequestBody JSONObject requestBody) {
        Result result = new Result();
        Boolean flag = false;
        String message = "";
        try {
            IObject hierarchyConfiguration = designService.createHierarchyConfiguration(requestBody);
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

    @ApiOperation(value = "删除层级配置", notes = "删除层级配置")
    @RequestMapping(value = "/deleteHierarchyConfiguration", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's OBID", example = "TIME STAMP OBID", required = true, dataTypeClass = String.class)}))
    public String deleteHierarchyConfiguration(@RequestBody JSONObject requestBody) {
        Result result = new Result();
        Boolean flag = false;
        String message = "";
        try {
            designService.deleteHierarchyConfiguration(CommonUtility.getId(requestBody));
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
            designService.updateHierarchyConfiguration(CommonUtility.parseObjectDTOFromJSON(hierarchyConfiguration));
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

    @ApiOperation(value = "获取层级配置项表单", notes = "获取层级配置项表单")
    @RequestMapping(value = "/getHierarchyConfigurationItemForm", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String getHierarchyConfigurationItemForm(@RequestBody JSONObject requestBody) {
        Result<ObjectDTO> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String formPurpose = CommonUtility.getFormPurpose(requestBody);
            ObjectDTO items = designService.getHierarchyConfigurationItemForm(formPurpose);
            result.setResult(items);
        } catch (Exception exception) {
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
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
    @RequestMapping(value = "/getHierarchyConfigurationItems", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String getHierarchyConfigurationItems(@RequestBody JSONObject requestBody) {
        ResultVo<List<ObjectDTO>> result = new ResultVo();
        try {
            String hierarchyConfigurationId = CommonUtility.getId(requestBody);
            IObjectCollection hierarchyConfigurationItems = designService.getHierarchyConfigurationItems(hierarchyConfigurationId);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(hierarchyConfigurationItems);
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
    @RequestMapping(value = "/createHierarchyConfigurationItem", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "HierarchyConfiguration id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "obj", value = "object DTO to be updated", example =
                    "        \"items\": [\n" +
                            "            {\n" +
                            "                \"defUID\": \"\",\n" +
                            "                \"displayValue\": \"\"\n" +
                            "            }\n" +
                            "        ]\n", required = true, dataTypeClass = JSONObject.class)}))
    public String createHierarchyConfigurationItem(@RequestBody JSONObject requestBody) {
        Result result = new Result();
        Boolean flag = false;
        String message = "";
        try {
            String hierarchyConfiguration = CommonUtility.getId(requestBody);
            IObject hierarchyConfigurationItemByConfigurationOBID = designService
                    .createHierarchyConfigurationItemByConfigurationOBID(hierarchyConfiguration,
                            CommonUtility.parseObjectDTOFromJSON(requestBody));
            result.setResult(hierarchyConfigurationItemByConfigurationOBID.OBID());
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

    @ApiOperation(value = "删除层级配置项", notes = "删除层级配置项")
    @RequestMapping(value = "/deleteHierarchyConfigurationItem", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's OBID", example = "TIME STAMP OBID", required = true, dataTypeClass = String.class)}))
    public String deleteHierarchyConfigurationItem(@RequestBody JSONObject requestBody) throws Exception {
        Result result = new Result();
        Boolean flag = false;
        String message = "";
        try {
            String itemOBID = CommonUtility.getId(requestBody);
            designService.deleteHierarchyConfigurationItem(itemOBID);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(200);
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "更新层级配置项", notes = "更新层级配置项")
    @RequestMapping(value = "/updateHierarchyConfigurationItem", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "items", value = "object DTO to be updated", example = "\"items\": [\n" +
                    "        {\n" +
                    "            \"defUID\": \"\",\n" +
                    "            \"displayValue\": \"\"\n" +
                    "        }\n" +
                    "    ]", required = true, dataTypeClass = JSONObject.class)}))
    public String updateHierarchyConfigurationItem(@RequestBody JSONObject requestBody) {
        Result result = new Result();
        Boolean flag = false;
        String message = "";
        try {
            designService.updateHierarchyConfigurationItem(CommonUtility.parseObjectDTOFromJSON(requestBody));
            flag = true;

        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(200);
            }
        }
        return JSON.toJSONString(result);
    }

    /**
     * 设计数据浏览 - 点击目录树叶节点获取对应doc
     *
     * @param selectedNode
     * @return
     */

    @ApiOperation(value = "根据选择节点获取图纸", notes = "获取目录树图纸文档信息")
    @RequestMapping(value = "/getDocumentsFromHierarchyNode", method = RequestMethod.POST)
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
            @DynamicParameter(name = "page", value = "分页参数", example = "", required = true, dataTypeClass = PageRequest.class)
    }))
    public String getDocumentsFromHierarchyNode(@RequestBody JSONObject selectedNode) {
        Result<ObjectDTOCollection> result = new Result<>();
        result.setCode(200);
        try {
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(selectedNode);
            IObjectCollection documentsFromHierarchyNode = designService
                    .getDocumentsFromHierarchyNode(CommonUtility.parseHierarchyObjectDTOFromJSON(selectedNode), pageRequest);
            List<ObjectDTO> documents = ObjectDTOUtility.convertToObjectDTOList(documentsFromHierarchyNode);
            ObjectDTOCollection oc = new ObjectDTOCollection(documents);
            oc.setCurrent(documentsFromHierarchyNode.PageResult().getCurrent());
            oc.setTotal(documentsFromHierarchyNode.PageResult().getTotal());
            oc.setSize(documentsFromHierarchyNode.PageResult().getSize());
            result.setResult(oc);
            result.setSuccess(true);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            result.setSuccess(false);
            result.setMessage(exception.getMessage());
        }
        return JSON.toJSONString(result);
    }
    /* ******************************************************* 设计数据-树方法 End ******************************************************* */
    /* ******************************************************* 设计数据方法 Start ******************************************************* */



    /* ******************************************************* 设计数据方法 End ******************************************************* */
}
