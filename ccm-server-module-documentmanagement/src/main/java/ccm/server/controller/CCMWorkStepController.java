package ccm.server.controller;

import ccm.server.business.ICCMWorkStepService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.module.vo.ResultVo;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.schema.interfaces.IObject;
import ccm.server.util.CommonUtility;
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
import org.apache.commons.lang3.StringUtils;
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
 * @since 2022/3/25 15:05
 */
@Slf4j
@RestController
@Api(tags = "工作步骤管理")
@RequestMapping("/ccm/workstep")
public class CCMWorkStepController {

    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    @Autowired
    private ICCMWorkStepService workStepService;

    @ApiOperation("根据施工阶段查询工作步骤的施工步骤")
    @RequestMapping(value = "/getROPWorkStepNameByPurpose", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "purpose", value = "施工阶段", example = "EN_Cutting", required = true, dataTypeClass = String.class)
    }))
    public String getROPWorkStepNameByPurpose(@RequestBody JSONObject jsonObject) {
        ResultVo<List<ObjectDTO>> result = new ResultVo<>();
        try {
            String purpose = jsonObject.getString("purpose");

            String classDefinitionUID = "ROPWorkStep";
            IObjectCollection workStepROPWorkStepNameByPurpose = this.workStepService.getWorkStepROPWorkStepNameByPurpose(purpose, classDefinitionUID);
            List<ObjectDTO> objectDTOS = workStepROPWorkStepNameByPurpose.toObjectDTOs();
            result.successResult(objectDTOS);
        } catch (Exception exception) {
            result.errorResult(ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }


    @ApiOperation("根据工作步骤的施工阶段和施工步骤查询已完成的设计数据对象")
    @RequestMapping(value = "/getFinishedDesignObjByPurposeAndROPWorkStepName", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "purpose", value = "施工阶段", example = "EN_Cutting", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "ropWorkStepName", value = "工作步骤的施工步骤", example = "EN_Group_Pair", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "classDefinitionUID", value = "设计数据类型", example = "CCMSupport", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getFinishedDesignObjByPurposeAndROPWorkStepName(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<>();
        try {
            String purpose = jsonObject.getString("purpose");
            String ropWorkStepName = jsonObject.getString("ropWorkStepName");
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(jsonObject);
            String classDefinitionUID = CommonUtility.getClassDefinitionUID(jsonObject);
            if (StringUtils.isEmpty(purpose) || StringUtils.isEmpty(ropWorkStepName) || StringUtils.isEmpty(classDefinitionUID)) {
                throw new Exception("施工阶段、工作步骤的施工步骤、设计数据类型不可为空!");
            }
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
            if (formBase != null) {
                IObjectCollection workStepROPWorkStepNameByPurpose = this.workStepService.getFinishedDesignObjByPurposeAndROPWorkStepName(purpose, ropWorkStepName, classDefinitionUID, pageRequest);
                if (workStepROPWorkStepNameByPurpose != null && workStepROPWorkStepNameByPurpose.hasValue()) {
                    List<ObjectDTO> collections = new ArrayList<>();
                    Iterator<IObject> objectIterator = workStepROPWorkStepNameByPurpose.GetEnumerator();
                    while (objectIterator.hasNext()) {
                        IObject next = objectIterator.next();
                        ObjectDTO currentForm = formBase.copyTo();
                        next.fillingForObjectDTO(currentForm);
                        collections.add(currentForm);
                    }
                    ObjectDTOCollection objectDTOCollection = PageUtility.pagedObjectDTOS(collections, pageRequest);
                    result.successResult(objectDTOCollection);
                } else {
                    result.successResult(null);
                }
            }
        } catch (Exception exception) {
            result.errorResult(ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

}
