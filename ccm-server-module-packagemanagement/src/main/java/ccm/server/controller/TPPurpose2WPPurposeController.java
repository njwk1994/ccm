package ccm.server.controller;

import ccm.server.business.ICCMTPPurpose2WPPurposeService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.enums.classDefinitionType;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.module.vo.ResultVo;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.utils.PageUtility;
import ccm.server.utils.SchemaUtility;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/8/16 9:45
 */
@Slf4j
@Api(tags = "任务包施工阶段和工作包施工阶段管理")
@RequestMapping("/ccm/tpp2wpp")
@RestController
public class TPPurpose2WPPurposeController {

    @Autowired
    private ICCMTPPurpose2WPPurposeService tpPurpose2WPPurposeService;
    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    @ApiOperation(value = "获取任务包施工阶段", httpMethod = "POST", notes = "获取任务包施工阶段")
    @PostMapping("/getTPPurpose")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getTPPurpose(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<>();
        try {
            String formPurpose = jsonObject.getString("formPurpose");
            String classDefinitionUID = classDefinitionType.EnumEnum.name();
            ICIMForm form = this.schemaBusinessService.getForm(formPurpose, classDefinitionUID);
            ObjectDTO formBase = null;
            if (form != null) {
                formBase = form.generatePopup(formPurpose);
            } else {
                formBase = this.schemaBusinessService.generateDefaultPopup(classDefinitionUID);
            }
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(jsonObject);
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            OrderByParam orderByParam = new OrderByParam(jsonObject);

            IObjectCollection tpPurpose = tpPurpose2WPPurposeService.getTPPurpose(filtersParam, orderByParam, pageRequest.getPageIndex(), pageRequest.getPageSize());
            ObjectDTOCollection oc = SchemaUtility.toObjectDTOCollection(formPurpose, formBase, tpPurpose);

            if (oc != null) {
                result.successResult(oc);
                result.setMessage("retrieved object(s) with " + classDefinitionUID + " succeeded");
            } else {
                result.successResult(null);
                result.setMessage("未查询到任务包施工阶段数据.");
            }

        } catch (Exception e) {
            log.error("获取任务包施工阶段失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取任务包施工阶段失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "获取所有工作包施工阶段", httpMethod = "POST", notes = "获取所有工作包施工阶段")
    @PostMapping("/getWPPurpose")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getWPPurpose(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<>();
        try {
            String formPurpose = jsonObject.getString("formPurpose");
            String classDefinitionUID = classDefinitionType.EnumEnum.name();
            ICIMForm form = this.schemaBusinessService.getForm(formPurpose, classDefinitionUID);
            ObjectDTO formBase = null;
            if (form != null) {
                formBase = form.generatePopup(formPurpose);
            } else {
                formBase = this.schemaBusinessService.generateDefaultPopup(classDefinitionUID);
            }
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(jsonObject);
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            OrderByParam orderByParam = new OrderByParam(jsonObject);

            IObjectCollection wpPurpose = tpPurpose2WPPurposeService.getWPPurpose(filtersParam, orderByParam, pageRequest.getPageIndex(), pageRequest.getPageSize());

            ObjectDTOCollection oc = SchemaUtility.toObjectDTOCollection(formPurpose, formBase, wpPurpose);
            if (oc != null) {
                ObjectDTOCollection objectDTOCollection = PageUtility.pagedObjectDTOS(oc.getItems(), pageRequest);
                result.successResult(objectDTOCollection);
                result.setMessage("retrieved object(s) with " + classDefinitionUID + " succeeded");
            } else {
                result.successResult(null);
                result.setMessage("未查询到工作包施工阶段数据.");
            }

        } catch (Exception e) {
            log.error("获取所有工作包施工阶段失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取所有工作包施工阶段失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "获取工作包施工阶段对应的任务包施工阶段", httpMethod = "POST", notes = "获取工作包施工阶段对应的任务包施工阶段")
    @PostMapping("/getTPPurposeByWPPurpose")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "wpPurpose", value = "EN_WPCutting", example = "EN_WPCutting", required = true, dataTypeClass = String.class),
    }))
    public String getTPPurposeByWPPurpose(@RequestBody JSONObject jsonObject) {
        ResultVo<String> result = new ResultVo<>();
        try {

            String wpPurpose = jsonObject.getString("wpPurpose");
            if (StringUtils.isEmpty(wpPurpose)) {
                throw new RuntimeException("传入的工作包施工阶段不能为空!");
            }
            String tpPurpose = tpPurpose2WPPurposeService.getTPPurposeByWPPurpose(wpPurpose);
            if (StringUtils.isEmpty(tpPurpose)) {
                throw new RuntimeException("根据工作包施工阶段(" + wpPurpose + ")未获取到对应的任务包施工阶段,请检查施工阶段配置!");
            }
            result.successResult(tpPurpose);
        } catch (Exception e) {
            log.error("获取所有工作包施工阶段失败!{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取所有工作包施工阶段失败!" + ExceptionUtil.getSimpleMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "获取任务包阶段下的工作包施工阶段", httpMethod = "POST", notes = "获取任务包阶段下的工作包施工阶段")
    @PostMapping("/getWPPurposeInTPPurpose")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "tpPurpose", value = "tpPurposeOBID", example = "tpPurposeOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getWPPurposeInTPPurpose(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<>();
        try {
            String formPurpose = jsonObject.getString("formPurpose");
            String tpPurpose = jsonObject.getString("tpPurpose");
            String classDefinitionUID = classDefinitionType.EnumEnum.name();
            ICIMForm form = this.schemaBusinessService.getForm(formPurpose, classDefinitionUID);
            ObjectDTO formBase = null;
            if (form != null) {
                formBase = form.generatePopup(formPurpose);
            } else {
                formBase = this.schemaBusinessService.generateDefaultPopup(classDefinitionUID);
            }
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(jsonObject);
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            OrderByParam orderByParam = new OrderByParam(jsonObject);

            IObjectCollection wpPurpose = tpPurpose2WPPurposeService.getWPPurposeInTPPurpose(tpPurpose, filtersParam, orderByParam, pageRequest.getPageIndex(), pageRequest.getPageSize());

            ObjectDTOCollection oc = SchemaUtility.toObjectDTOCollection(formPurpose, formBase, wpPurpose);
            if (oc != null) {
                result.successResult(oc);
                result.setMessage("retrieved object(s) with " + classDefinitionUID + " succeeded");
            } else {
                result.successResult(null);
                result.setMessage("未获取任务包阶段下的工作包施工阶段数据.");
            }

        } catch (Exception e) {
            log.error("获取任务包阶段下的工作包施工阶段失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取任务包阶段下的工作包施工阶段失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "创建任务包施工阶段和工作包施工阶段关联关系", httpMethod = "POST", notes = "创建任务包施工阶段和工作包施工阶段关联关系")
    @PostMapping("/createTPPurpose2WPPurposeRel")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "tpPurpose", value = "tpPurposeOBID", example = "tpPurposeOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "wpPurpose", value = "wpPurposeOBID", example = "wpPurposeOBID", required = true, dataTypeClass = String.class),
    }))
    public String createTPPurpose2WPPurposeRel(@RequestBody JSONObject jsonObject) {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            String tpPurpose = jsonObject.getString("tpPurpose");
            String wpPurpose = jsonObject.getString("wpPurpose");

            tpPurpose2WPPurposeService.createTPPurpose2WPPurposeRel(tpPurpose, wpPurpose);
            result.successResult(true);
        } catch (Exception e) {
            log.error("创建任务包施工阶段和工作包施工阶段关联关系失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("创建任务包施工阶段和工作包施工阶段关联关系失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "解除任务包施工阶段和工作包施工阶段关联关系", httpMethod = "POST", notes = "解除任务包施工阶段和工作包施工阶段关联关系")
    @PostMapping("/removeTPPurpose2WPPurposeRel")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "tpPurpose", value = "tpPurposeOBID", example = "tpPurposeOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "wpPurpose", value = "wpPurposeOBID", example = "wpPurposeOBID", required = true, dataTypeClass = String.class),
    }))
    public String removeTPPurpose2WPPurposeRel(@RequestBody JSONObject jsonObject) {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            String tpPurpose = jsonObject.getString("tpPurpose");
            String wpPurpose = jsonObject.getString("wpPurpose");

            tpPurpose2WPPurposeService.removeTPPurpose2WPPurposeRel(tpPurpose, wpPurpose);
            result.successResult(true);
        } catch (Exception e) {
            log.error("解除任务包施工阶段和工作包施工阶段关联关系失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("解除任务包施工阶段和工作包施工阶段关联关系失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

}
