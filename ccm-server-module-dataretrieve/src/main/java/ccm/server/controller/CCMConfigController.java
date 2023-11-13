package ccm.server.controller;

import ccm.server.business.ICCMConfigService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.module.vo.ResultVo;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.schema.interfaces.IObject;
import ccm.server.util.CommonUtility;
import ccm.server.utils.ICIMProjectConfigUtils;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/7/6 16:09
 */
@RestController
@RequestMapping("/ccm/config")
@Api(tags = "系统&项目级配置管理")
@Slf4j
public class CCMConfigController {

    @Autowired
    private ISchemaBusinessService schemaBusinessService;
    @Autowired
    private ICCMConfigService configService;

    @AutoLog(value = "系统&项目级配置管理-获取当前项目配置")
    @ApiOperation("获取当前项目配置")
    @PostMapping("/getProjectConfig")
    public String getProjectConfig(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<>();

        String id = CommonUtility.getId(jsonObject);
        String formPurpose = jsonObject.getString("formPurpose");
        String classDefinitionUID = ICIMProjectConfigUtils.CIM_PROJECT_CONFIG;

        try {
            ICIMForm form = this.schemaBusinessService.getForm(formPurpose, classDefinitionUID);
            ObjectDTO formBase;
            if (form != null) {
                formBase = form.generatePopup(formPurpose);
            } else {
                formBase = this.schemaBusinessService.generateDefaultPopup(classDefinitionUID);
            }

            if (formBase != null) {
                IObject projectConfig = configService.getProjectConfig();
                List<ObjectDTO> collections = new ArrayList<>();
                if (null != projectConfig) {
                    ObjectDTO currentForm = formBase.copyTo();
                    projectConfig.fillingForObjectDTO(currentForm);
                    collections.add(currentForm);
                }
                ObjectDTOCollection oc = new ObjectDTOCollection(collections);
                result.successResult(oc);
                result.setTotal(Math.toIntExact(oc.getTotal()));
            } else {
                result.errorResult("获取当前项目配置FORM失败!");
            }
        } catch (Exception e) {
            log.error("获取当前项目配置失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取当前项目配置失败!" + ExceptionUtil.getMessage(e));
        }

        return CommonUtility.toJsonString(result);
    }

    @AutoLog(value = "系统&项目级配置管理-创建或更新当前项目配置")
    @ApiOperation("创建或更新当前项目配置")
    @PostMapping("/createOrUpdateProjectConfig")
    public String createOrUpdateProjectConfig(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTO> result = new ResultVo<>();
        ObjectDTO objectDTO = JSON.parseObject(jsonObject.toJSONString(), ObjectDTO.class);

        try {
            IObject iObject = this.configService.createOrUpdateProjectConfig(objectDTO);
            if (iObject != null) {
                iObject.refreshObjectDTO(objectDTO);
                result.successResult(iObject.toObjectDTO());
            }
        } catch (Exception e) {
            log.error("创建或更新当前项目配置失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("创建或更新当前项目配置失败!" + ExceptionUtil.getMessage(e));
        }

        return CommonUtility.toJsonString(result);
    }

    @AutoLog(value = "系统&项目级配置管理-固定属性创建或更新当前项目配置")
    @ApiOperation("固定属性创建或更新当前项目配置")
    @PostMapping("/createOrUpdateProjectConfigTest")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "SPMDBHost", value = "SPM数据库地址", example = "localhost", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "SPMDBPort", value = "SPM数据库端口", example = "1522", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "SPMDatabaseName", value = "SPM数据库实例名称", example = "SMAT01", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "SPMDBUsername", value = "SPM数据库账号", example = "m_sys", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "SPMDBPassword", value = "SPM数据库密码", example = "Oracle123", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "SPMProject", value = "SPM项目名称", example = "POC_A", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "ProcedureType", value = "存储过程类型", example = "EN_NewDocIC", required = true, dataTypeClass = String.class),
    }))
    public String createOrUpdateProjectConfigTest(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTO> result = new ResultVo<>();
        ObjectDTO objectDTO = JSON.parseObject(jsonObject.toJSONString(), ObjectDTO.class);

        try {
            String spmdbHost = jsonObject.getString("SPMDBHost");
            String spmdbPort = jsonObject.getString("SPMDBPort");
            String spmDatabaseName = jsonObject.getString("SPMDatabaseName");
            String spmdbUsername = jsonObject.getString("SPMDBUsername");
            String spmdbPassword = jsonObject.getString("SPMDBPassword");
            String spmProject = jsonObject.getString("SPMProject");
            String procedureType = jsonObject.getString("ProcedureType");
            IObject iObject = this.configService.createOrUpdateProjectConfigTest(spmdbHost, spmdbPort, spmDatabaseName, spmdbUsername, spmdbPassword, spmProject, procedureType);
            if (iObject != null) {
                iObject.refreshObjectDTO(objectDTO);
                result.successResult(iObject.toObjectDTO());
            }
        } catch (Exception e) {
            log.error("创建或更新当前项目配置失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("创建或更新当前项目配置失败!" + ExceptionUtil.getMessage(e));
        }

        return CommonUtility.toJsonString(result);
    }


}
