package ccm.server.controller;

import ccm.server.business.ICCMBasicInfoService;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.params.PageRequest;
import ccm.server.util.CommonUtility;
import ccm.server.schema.collections.IObjectCollection;
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
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2021/12/29 12:58
 */
@RestController
@RequestMapping("/ccm/basicInfoManagement")
@Api(tags = "施工数据管理")
@Slf4j
public class CCMBasicInfoController {

    @Autowired
    private ICCMBasicInfoService basicInfoService;

    /* *****************************************  施工分类 start  ***************************************** */
    @ApiOperation("获取施工分类表单")
    @RequestMapping(value = "/constructionTypeForm", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class),
    }))
    public String getConstructionTypeForm(@RequestBody JSONObject requestBody) {
        Result<ObjectDTO> result = new Result<ObjectDTO>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String formPurpose = CommonUtility.getFormPurpose(requestBody);
            ObjectDTO form = basicInfoService.getConstructionTypeForm(formPurpose);
            result.setResult(form);
        } catch (Exception exception) {
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取所有施工分类")
    @RequestMapping(value = "/constructionType", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "page", value = "分页参数", example = "", required = true, dataTypeClass = PageRequest.class)}))
    public String getConstructionTypes(@RequestBody JSONObject requestBody) {
        Result<List<ObjectDTO>> result = new Result<List<ObjectDTO>>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(requestBody);
            IObjectCollection items = basicInfoService.getConstructionTypes(pageRequest);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(items);
            result.setResult(objectDTOS);
        } catch (Exception exception) {
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("新增施工分类")
    @RequestMapping(value = "/addConstructionType", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String addConstructionType(@RequestBody JSONObject requestBody) {
        Result<String> result = new Result<String>();
        boolean flag = false;
        String message = "";
        try {
            ObjectDTO constructionType = CommonUtility.parseObjectDTOFromJSON(requestBody);
            String constructionTypeId = basicInfoService.createConstructionType(constructionType).OBID();
            flag = true;
            result.setResult(constructionTypeId);
        } catch (Exception exception) {
            log.error(ExceptionUtil.getMessage(exception), exception);
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

    @ApiOperation("删除施工分类")
    @RequestMapping(value = "/deleteConstructionType", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's OBID", example = "TIME STAMP OBID", required = true, dataTypeClass = String.class)}))
    public String deleteConstructionType(@RequestBody JSONObject requestBody) {
        Result<String> result = new Result<String>();
        boolean flag = false;
        String message = "";
        try {
            this.basicInfoService.deleteConstructionType(requestBody.getString("id"));
            flag = true;
        } catch (Exception exception) {
            log.error(ExceptionUtil.getMessage(exception), exception);
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

    @ApiOperation("更新施工分类")
    @RequestMapping(value = "/updateConstructionType", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String updateConstructionType(@RequestBody JSONObject requestBody) {
        Result<Object> result = new Result<Object>();
        boolean flag = false;
        String message = "";
        try {
            ObjectDTO objectDTO = CommonUtility.parseObjectDTOFromJSON(requestBody);
            this.basicInfoService.updateConstructionType(objectDTO);
            flag = true;
        } catch (Exception exception) {
            log.error(ExceptionUtil.getMessage(exception), exception);
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

    @ApiOperation("获取施工分类下设计类型")
    @RequestMapping(value = "/designTypesUnderConstructionType", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's OBID", example = "TIME STAMP OBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "page", value = "分页参数", example = "", required = true, dataTypeClass = PageRequest.class)
    }))
    public String getDesignTypesUnderConstructionType(@RequestBody JSONObject requestBody) {
        Result<List<ObjectDTO>> result = new Result<List<ObjectDTO>>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String id = requestBody.getString("id");
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(requestBody);
            IObjectCollection items = this.basicInfoService.getDesignTypesUnderConstructionType(id, pageRequest);
            List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(items);
            result.setResult(objectDTOS);
        } catch (Exception exception) {
            log.error(ExceptionUtil.getMessage(exception), exception);
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("新增施工分类下设计类型")
    @RequestMapping(value = "/addDesignType", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "priority uid", example = "TIME STAMP uid", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "name", value = "priority name", example = "XXXXX", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "obj", value = "object DTO to be updated", required = true, dataTypeClass = ObjectDTO.class)}))
    public String addDesignType(@RequestBody JSONObject requestBody) throws Exception {
        Result<Object> result = new Result<>();
        boolean flag = false;
        String message = "";
        try {
            String constructionTypeUID = requestBody.getString("id");
            ObjectDTO designType = CommonUtility.parseObjectDTOFromJSON(requestBody);
            this.basicInfoService.addDesignTypeIntoConstructionType(constructionTypeUID, designType);
            flag = true;
        } catch (Exception exception) {
            log.error(ExceptionUtil.getMessage(exception), exception);
            message = ExceptionUtil.getMessage(exception);
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

    /* *****************************************  施工分类 end  ***************************************** */
    /* *****************************************  设计类型 start  ***************************************** */
    @ApiOperation("获取设计类型表单")
    @RequestMapping(value = "/designTypeForm", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class),
    }))
    public String getDesignTypeForm(@RequestBody JSONObject requestBody) {
        Result<ObjectDTO> result = new Result<ObjectDTO>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String formPurpose = CommonUtility.getFormPurpose(requestBody);
            ObjectDTO items = this.basicInfoService.getDesignTypeForm(formPurpose);
            result.setResult(items);
        } catch (Exception exception) {
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("更新设计类型")
    @RequestMapping(value = "/updateDesignType", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String updateDesignType(@RequestBody JSONObject requestBody) {
        Result result = new Result();
        Boolean flag = false;
        String message = "";
        try {
            ObjectDTO toUpdateDesignType = new ObjectDTO();
            ObjectItemDTO[] items = requestBody.getObject("items", ObjectItemDTO[].class);
            toUpdateDesignType.getItems().addAll(Arrays.asList(items));
            this.basicInfoService.updateDesignType(toUpdateDesignType);
            flag = true;

        } catch (Exception exception) {
            log.error(ExceptionUtil.getMessage(exception), exception);
            message = ExceptionUtil.getMessage(exception);
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

    @ApiOperation("删除设计类型")
    @RequestMapping(value = "/deleteDesignType", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "object DTO 's id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String deleteDesignType(@RequestBody JSONObject requestBody) throws Exception {
        Result result = new Result();
        Boolean flag = false;
        String message = "";
        try {
            this.basicInfoService.deleteDesignType(requestBody.getString("id"));
            flag = true;
        } catch (Exception exception) {
            log.error(ExceptionUtil.getMessage(exception), exception);
            message = ExceptionUtil.getMessage(exception);
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
    /* *****************************************  设计类型 end  ***************************************** */
}
