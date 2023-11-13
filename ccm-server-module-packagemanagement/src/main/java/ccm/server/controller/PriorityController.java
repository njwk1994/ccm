package ccm.server.controller;

import ccm.server.business.ICCMPriorityService;
import ccm.server.context.CIMContext;
import ccm.server.context.PriorityContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.base.OptionItemDTO;
import ccm.server.enums.propertyValueType;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
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
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/25 8:57
 */
@RestController
@RequestMapping("/ccm/prioritypolicymanagement")
@Api(tags = "策略管理")
@Slf4j
public class PriorityController {
    @Autowired
    private ICCMPriorityService priorityService;

    @ApiOperation("Get Priority Policy Item Form")
    @RequestMapping(value = "/prioritypolicyitemform", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
        @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class),
        @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String getPriorityPolicyItemForm(@RequestBody JSONObject requestBody) {
        Result<ObjectDTO> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            /*String formPurpose = CommonUtility.getFormPurpose(requestBody);
            ObjectDTO items = priorityService.getPriorityItemForm(operationPurpose.valueOf(formPurpose), CommonUtility.parseObjectDTOFromJSON(requestBody));
            result.setResult(items);*/
        } catch (Exception exception) {
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("Get Priority Policy Form")
    @RequestMapping(value = "/prioritypolicyform", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
        @DynamicParameter(name = "formPurpose", value = "create/update/query/info", example = "create", required = true, dataTypeClass = String.class),
        @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String getPriorityPolicyForm(@RequestBody JSONObject requestBody) {
        Result<ObjectDTO> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String formPurpose = CommonUtility.getFormPurpose(requestBody);
            ObjectDTO items = priorityService.getPriorityForm(formPurpose);
            result.setResult(items);
        } catch (Exception exception) {
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("Get Priority Policy List")
    @RequestMapping(value = "/prioritypolicy", method = RequestMethod.POST)
    public String getAllPriorityPolicyList(@RequestBody JSONObject requestBody) {
        Result<List<ObjectDTO>> result = new Result();
        result.setCode(200);
        result.setSuccess(true);
        try {
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(requestBody);
            IObjectCollection priorities = priorityService.getPriorities(pageRequest);
            List<ObjectDTO> items = ObjectDTOUtility.convertToObjectDTOList(priorities);
            result.setResult(items);
        } catch (Exception exception) {
            result.setCode(200);
            result.setSuccess(false);
            result.setMessage(ExceptionUtil.getMessage(exception));
            result.setResult(null);
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("Add Priority Policy")
    @RequestMapping(value = "/addprioritypolicy", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
        @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String addPriorityPolicy(@RequestBody JSONObject requestBody) {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            priorityService.createPriority(CommonUtility.parseObjectDTOFromJSON(requestBody));
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

    @ApiOperation("Delete Priority Policy")
    @RequestMapping(value = "/deleteprioritypolicy", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
        @DynamicParameter(name = "id", value = "object DTO 's id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String deletePriorityPolicy(@RequestBody JSONObject requestBody) {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            String priorityId = CommonUtility.getId(requestBody);
            priorityService.deletePriority(priorityId);
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

    @ApiOperation("Update Priority Policy")
    @RequestMapping(value = "/updateprioritypolicy", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
        @DynamicParameter(name = "items", value = "object DTO to be updated", required = true, dataTypeClass = ObjectItemDTO[].class)}))
    public String updatePriorityPolicy(@RequestBody JSONObject priorityPolicy) {
        Result<String> result = new Result<>();
        try {
            priorityService.updatePriority(CommonUtility.parseObjectDTOFromJSON(priorityPolicy));
            result.setSuccess(true);
        } catch (Exception exception) {
            result.setMessage(exception.getMessage());
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("Get Priority Policy Items")
    @RequestMapping(value = "/prioritypolicyitems", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
        @DynamicParameter(name = "id", value = "object DTO 's ID", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String getPriorityPolicyItems(@RequestBody JSONObject requestBody) {
        Result<List<ObjectDTO>> result = new Result<>();
        result.setCode(200);
        result.setSuccess(true);
        String priorityId = CommonUtility.getId(requestBody);
        IObjectCollection priorityItems = null;
        try {
            priorityItems = priorityService.getPriorityItems(priorityId);
            List<ObjectDTO> items = ObjectDTOUtility.convertToObjectDTOList(priorityItems);
            result.setResult(items);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            result.setMessage(exception.getMessage());
        }

        return JSON.toJSONString(result);
    }

    @ApiOperation("Add Priority Policy Item")
    @RequestMapping(value = "/addprioritypolicyitem", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
        @DynamicParameter(name = "id", value = "priority id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class),
        @DynamicParameter(name = "name", value = "priority name", example = "XXXXX", required = true, dataTypeClass = String.class),
        @DynamicParameter(name = "obj", value = "object DTO to be updated", required = true, dataTypeClass = ObjectDTO.class)}))
    public String addPriorityPolicyItem(@RequestBody JSONObject requestBody) {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            String id = CommonUtility.getId(requestBody);
            priorityService.createPriorityItem(id, CommonUtility.parseObjectDTOFromJSON(requestBody));
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

    @ApiOperation("Update Priority Policy Items")
    @RequestMapping(value = "/updateprioritypolicyitem", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
        @DynamicParameter(name = "items", value = "object DTO to be updated", example = "\"items\": [\n" +
                                                                                            "        {\n" +
                                                                                            "            \"defUID\": \"\",\n" +
                                                                                            "            \"displayValue\": \"\"\n" +
                                                                                            "        }\n" +
                                                                                            "    ]", required = true, dataTypeClass = JSONObject.class)}))
    public String updatePriorityPolicyItem(@RequestBody JSONObject requestBody) {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            priorityService.updatePriorityItem(CommonUtility.parseObjectDTOFromJSON(requestBody));
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

    @ApiOperation("Delete Priority Policy Items")
    @RequestMapping(value = "/deleteprioritypolicyitem", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
        @DynamicParameter(name = "id", value = "object DTO 's id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String deletePriorityPolicyItem(@RequestBody JSONObject requestBody) throws Exception {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            String priorityItemId = CommonUtility.getId(requestBody);
            priorityService.deletePriorityItem(priorityItemId);
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

    @ApiOperation("Run priority on task package with provided priority rule")
    @RequestMapping(value = "/executePriorityForTaskPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
        @DynamicParameter(name = "packageId", value = "Package id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class),
        @DynamicParameter(name = "id", value = "priority id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String executePriorityForTaskPackage(@RequestBody JSONObject requestBody) throws Exception {
        Result<ObjectDTOCollection> result = new Result<>();
        try {
            String priorityId = requestBody.getString("id");
            String packageId = requestBody.getString("packageId");
            boolean fromCache = requestBody.getBoolean("fromCache");
            Integer pageIndex = requestBody.getInteger("pageIndex");
            Integer pageSize = requestBody.getInteger("pageSize");
            String uniqueId = requestBody.getString("token");
            OrderByParam orderByParam = new OrderByParam(requestBody);
            FiltersParam filtersParam = new FiltersParam(requestBody);
            CIMContext.Instance.ProcessCache().addDynamicalPropertyDefinition("DynWeight", "优先级", propertyValueType.DoubleType);
            IObjectCollection objectCollection = PriorityContext.Instance.calculatePriorityForTaskPackage(uniqueId, packageId, priorityId, fromCache);
            List<ObjectDTO> documents = new ArrayList<>();
            if (objectCollection != null && objectCollection.hasValue()) {
                Iterator<IObject> e = objectCollection.GetEnumerator();
                while (e.hasNext()) {
                    documents.add(e.next().toObjectDTO());
                }
            }

            orderByParam.insertFirstly("DynWeight", false);
            ObjectDTOCollection objectDTOCollection = new ObjectDTOCollection(documents);
            objectDTOCollection.initOrderByParam(orderByParam);
            objectDTOCollection.initFilterParam(filtersParam);
            objectDTOCollection.setCurrent(pageIndex);
            objectDTOCollection.setSize(pageSize);
            objectDTOCollection.setToken(uniqueId);
            objectDTOCollection.adjust();
            result.success("execute priority for task package successfully");
            result.setResult(objectDTOCollection);
        } catch (Exception exception) {
            result.error500(exception.getMessage());
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("Run priority on work package with provided priority rule")
    @RequestMapping(value = "/executePriorityForWorkPackage", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
        @DynamicParameter(name = "packageId", value = "Package id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class),
        @DynamicParameter(name = "id", value = "priority id", example = "TIME STAMP ID", required = true, dataTypeClass = String.class)}))
    public String executePriorityForWorkPackage(@RequestBody JSONObject requestBody) throws Exception {
        Result<ObjectDTOCollection> result = new Result<>();
        try {
            String priorityId = requestBody.getString("id");
            String packageId = requestBody.getString("packageId");
            boolean fromCache = requestBody.getBoolean("fromCache");
            Integer pageIndex = requestBody.getInteger("pageIndex");
            Integer pageSize = requestBody.getInteger("pageSize");
            OrderByParam orderByParam = new OrderByParam(requestBody);
            FiltersParam filtersParam = new FiltersParam(requestBody);
            String uniqueId = requestBody.getString("token");
            CIMContext.Instance.ProcessCache().addDynamicalPropertyDefinition("DynWeight", "优先级", propertyValueType.DoubleType);
            IObjectCollection objectCollection = PriorityContext.Instance.calculatePriorityForWorkPackage(uniqueId, packageId, priorityId, fromCache);
            List<ObjectDTO> documents = new ArrayList<>();
            if (objectCollection != null && objectCollection.hasValue()) {
                Iterator<IObject> iterator = objectCollection.GetEnumerator();
                while (iterator.hasNext()) {
                    documents.add(iterator.next().toObjectDTO());
                }
            }

            orderByParam.insertFirstly("DynWeight", false);
            ObjectDTOCollection objectDTOCollection = new ObjectDTOCollection(documents);
            objectDTOCollection.initOrderByParam(orderByParam);
            objectDTOCollection.initFilterParam(filtersParam);
            objectDTOCollection.setToken(uniqueId);
            objectDTOCollection.setCurrent(pageIndex);
            objectDTOCollection.setSize(pageSize);
            objectDTOCollection.adjust();
            result.success("execute priority for work package successfully");
            result.setResult(objectDTOCollection);
        } catch (Exception exception) {
            result.error500(exception.getMessage());
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("Get properties for priority policy item")
    @RequestMapping(value = "/getPropertiesFoPriorityItemCreation", method = RequestMethod.POST)
    public String getPropertiesForPriorityPolicyItem() {
        Result<List<OptionItemDTO>> result = new Result<>();
        try {
            List<OptionItemDTO> properties = priorityService.getPropertiesForPriorityItem();
            result.success("get properties for priority item");
            result.setResult(properties);
        } catch (Exception exception) {
            result.error500(exception.getMessage());
        }
        return JSON.toJSONString(result);
    }
}
