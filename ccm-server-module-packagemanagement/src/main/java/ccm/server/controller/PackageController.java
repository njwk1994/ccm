package ccm.server.controller;

import ccm.server.business.ICCMPackageService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.PackageTypeEnum;
import ccm.server.enums.ProcedureTypeEnum;
import ccm.server.models.query.QueryRequest;
import ccm.server.module.vo.ResultVo;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.schema.interfaces.ICIMProjectConfig;
import ccm.server.schema.interfaces.IObject;
import ccm.server.util.CommonUtility;
import ccm.server.utils.DataRetrieveUtils;
import ccm.server.utils.ICIMProjectConfigUtils;
import ccm.server.utils.PackagesUtils;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/7/8 7:47
 */
@Slf4j
@RestController
@Api(tags = "通用包管理")
@RequestMapping("/ccm/packages")
public class PackageController {
    @Autowired
    private ISchemaBusinessService schemaBusinessService;
    @Autowired
    private ICCMPackageService packageService;


    @ApiOperation("获取包对应的施工数据分类")
    @PostMapping("/getPackageConstructionTypes")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "任务包/工作包OBID", example = "任务包/工作包OBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "packageClassDefinitionUID", value = PackagesUtils.CCM_TASK_PACKAGE + "/" + PackagesUtils.CCM_WORK_PACKAGE + "/" + PackagesUtils.CCM_PRESSURE_TEST_PACKAGE,
                    example = PackagesUtils.CCM_TASK_PACKAGE + "/" + PackagesUtils.CCM_WORK_PACKAGE + "/" + PackagesUtils.CCM_PRESSURE_TEST_PACKAGE,
                    required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "needConsumeMaterial", value = "是否需要验证材料消耗", example = "true", required = true, dataTypeClass = Boolean.class),
    }))
    public String getPackageConstructionTypes(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<>();

        String id = CommonUtility.getId(jsonObject);
        Boolean needConsumeMaterial = jsonObject.getBoolean("needConsumeMaterial");
        if (needConsumeMaterial == null) {
            needConsumeMaterial = true;
        }
        String formPurpose = ccm.server.enums.formPurpose.List.toString();
        String classDefinitionUID = DataRetrieveUtils.CCM_CONSTRUCTION_TYPE;
        String packageClassDefinitionUID = jsonObject.getString("packageClassDefinitionUID");

        if (StringUtils.isBlank(id) || StringUtils.isBlank(packageClassDefinitionUID)) {
            result.errorResult("包OBID或包classDefinitionUID不可为空!");
        } else {
            try {
                ICIMForm form = this.schemaBusinessService.getForm(formPurpose, classDefinitionUID);
                ObjectDTO formBase;
                if (form != null) {
                    formBase = form.generatePopup(formPurpose);
                } else {
                    formBase = this.schemaBusinessService.generateDefaultPopup(classDefinitionUID);
                }

                if (formBase != null) {
                    IObjectCollection documentConstructionTypes = packageService.getPackageConstructionTypes(id, packageClassDefinitionUID, needConsumeMaterial);
                    List<ObjectDTO> collections = new ArrayList<>();
                    Iterator<IObject> objectIterator = documentConstructionTypes.GetEnumerator();
                    while (objectIterator.hasNext()) {
                        IObject next = objectIterator.next();
                        ObjectDTO currentForm = formBase.copyTo();
                        next.fillingForObjectDTO(currentForm);
                        collections.add(currentForm);
                    }
                    ObjectDTOCollection oc = new ObjectDTOCollection(collections);
                    oc.setCurrent(documentConstructionTypes.PageResult().getCurrent());
                    oc.setSize(documentConstructionTypes.PageResult().getSize());
                    oc.setTotal(documentConstructionTypes.PageResult().getTotal());
                    result.successResult(oc);
                } else {
                    result.errorResult("获取包对应的施工数据分类FORM失败!");
                }
            } catch (Exception e) {
                log.error("获取包对应的施工数据分类失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
                result.errorResult("获取包对应的施工数据分类失败!" + ExceptionUtil.getMessage(e));
            }
        }
        return CommonUtility.toJsonString(result);
    }

    /* ************************************************* 预测预留 start ******************************************************* */
    @ApiOperation(value = "包预测预留并获取预测结果", notes = "检测图纸是否存在,不存在则提示SPM不存在此图纸,存在的进行预测预留操作")
    @PostMapping("existAndCreateRequest")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "packageType", value = "任务包/工作包", example = "TP/WP", required = true, dataTypeClass = PackageTypeEnum.class),
            @DynamicParameter(name = "packageId", value = "任务包/工作包的OBID", example = "OBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "projectId", value = "如未传入则从项目配置中获取", example = "如未传入则从项目配置中获取", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestName", value = "预测单号", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "warehouses", value = "仓库", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "drawingNumbers", value = "用\",\"拼接的图纸OBID", example = "docOBID01,docOBID02", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "searchColumn", dataTypeClass = String.class),
            @DynamicParameter(name = "searchValue", dataTypeClass = String.class),
            @DynamicParameter(name = "procedureType", value = "存储过程类型", example = "EN_DefaultDoc", required = true, dataTypeClass = ProcedureTypeEnum.class),
    }))
    public String existAndCreatePartialStatusRequestForTP33(@RequestBody JSONObject jsonObject) {
        ResultVo<Map<String, Object>> result = new ResultVo<Map<String, Object>>();
        try {
            // 包类型
            PackageTypeEnum packageType = PackageTypeEnum.valueOf(jsonObject.getString("packageType"));

            String packageId = jsonObject.getString("packageId");
            String projectId = jsonObject.getString("projectId");
            if (StringUtils.isBlank(projectId)) {
                IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
                QueryRequest queryRequest = queryEngine.start();
                queryEngine.addClassDefForQuery(queryRequest, ICIMProjectConfigUtils.CIM_PROJECT_CONFIG);
                IObject projectConfig = queryEngine.queryOne(queryRequest);
                if (null == projectConfig) {
                    throw new RuntimeException("获取项目配置失败!");
                }
                ICIMProjectConfig icimProjectConfig = projectConfig.toInterface(ICIMProjectConfig.class);
                projectId = icimProjectConfig.getSPMProject();
                if (StringUtils.isBlank(projectId)) {
                    throw new RuntimeException("获取项目配置的项目号失败!");
                }
            }
            String requestName = jsonObject.getString("requestName");
            String requestType = jsonObject.getString("requestType");
            String searchColumn = jsonObject.getString("searchColumn");
            String searchValue = jsonObject.getString("searchValue");
            String drawingNumbers = jsonObject.getString("drawingNumbers") == null ? "" : jsonObject.getString("drawingNumbers");
            String warehouses = jsonObject.getString("warehouses") == null ? "" : jsonObject.getString("warehouses");
            // 存储过程类型
            String procedureTypeStr = jsonObject.getString("procedureType");
            ProcedureTypeEnum procedureType = ProcedureTypeEnum.valueOf(procedureTypeStr);

            Map<String, Object> procedureResults = packageService
                    .existAndCreateRequest(packageType, packageId, projectId, requestName, requestType, warehouses, drawingNumbers, searchColumn, searchValue, procedureType);
            result.successResult(procedureResults);
        } catch (Exception exception) {
            log.error("任务包部分预测/预留并获取预测33结果失败!{}{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("任务包部分预测/预留并获取预测结果33失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }
    /* ************************************************* 预测预留  end  ******************************************************* */
    /* ************************************************* 升版处理  start  ******************************************************* */

    /**
     * 任务包升版处理
     */
    @ApiOperation(value = "确认升版", notes = "确认升版")
    @PostMapping("confirmRevision")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "包的OBID", example = "OBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "packageType", value = "TP,WP,PTP", example = "TP", required = true, dataTypeClass = PackageTypeEnum.class)
    }))
    public String confirmRevision(@RequestBody JSONObject jsonObject) {
        ResultVo<Object> result = new ResultVo<>();
        String id = CommonUtility.getId(jsonObject);
        String packageType = jsonObject.getString("packageType");
        PackageTypeEnum packageTypeEnum = PackageTypeEnum.valueOf(packageType);
        try {
            packageService.confirmRevision(id, packageTypeEnum);
            result.successResult(true);
        } catch (Exception exception) {
            log.error("确认升版失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("确认升版失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    /* ************************************************* 升版处理  end  ******************************************************* */
    /* ************************************************* 任务包阶段与工作包阶段关联关系  start  ******************************************************* */
    @ApiOperation(value = "关联任务包和工作包施工阶段", notes = "关联任务包和工作包施工阶段")
    @PostMapping("createTPPurpose2WPPurpose")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "TPPurpose", value = "任务包施工阶段", example = "OBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "WPPurpose", value = "工作包施工阶段", example = "TP", required = true, dataTypeClass = PackageTypeEnum.class)
    }))
    public String createTPPurpose2WPPurpose(@RequestBody JSONObject jsonObject) {
        ResultVo<Object> result = new ResultVo<>();
        String id = CommonUtility.getId(jsonObject);
        String packageType = jsonObject.getString("packageType");
        PackageTypeEnum packageTypeEnum = PackageTypeEnum.valueOf(packageType);
        try {
            packageService.confirmRevision(id, packageTypeEnum);
            result.successResult(true);
        } catch (Exception exception) {
            log.error("确认升版失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("确认升版失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }
    /* ************************************************* 任务包阶段与工作包阶段关联关系  end  ******************************************************* */
}
