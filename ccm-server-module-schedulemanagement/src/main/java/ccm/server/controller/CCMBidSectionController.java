package ccm.server.controller;

import ccm.server.business.ICCMBidSectionService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.enums.classDefinitionType;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.module.vo.ResultVo;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.schema.interfaces.IObject;
import ccm.server.util.CommonUtility;
import ccm.server.utils.ICCMBidSectionUtils;
import ccm.server.utils.PageUtility;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author HuangTao
 * @version 1.0
 */
@Slf4j
@RestController
@Api(tags = "标段管理")
@RequestMapping("/ccm/bidSection")
public class CCMBidSectionController {

    @Autowired
    private ICCMBidSectionService bidSectionService;
    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    @ApiOperation(value = "获取标段", notes = "获取标段")
    @PostMapping("/getBidSections")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "classDefinitionUID", value = ICCMBidSectionUtils.CCM_BID_SECTION, example = ICCMBidSectionUtils.CCM_BID_SECTION, required = false, dataTypeClass = String.class),
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getBidSections(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<ObjectDTOCollection>();
        String classDefinitionUID = ICCMBidSectionUtils.CCM_BID_SECTION;
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
            if (formPurpose != null) {
                IObjectCollection bidSections = this.bidSectionService.getBidSections(filtersParam, orderByParam, pageRequest);
                if (bidSections != null && bidSections.hasValue()) {
                    List<ObjectDTO> collections = new ArrayList<>();
                    Iterator<IObject> objectIterator = bidSections.GetEnumerator();
                    while (objectIterator.hasNext()) {
                        IObject next = objectIterator.next();
                        ObjectDTO currentForm = formBase.copyTo();
                        next.fillingForObjectDTO(currentForm);
                        collections.add(currentForm);
                    }
                    ObjectDTOCollection oc = new ObjectDTOCollection(collections);
                    oc.setCurrent(bidSections.PageResult().getCurrent());
                    oc.setSize(bidSections.PageResult().getSize());
                    oc.setTotal(bidSections.PageResult().getTotal());
                    result.successResult(oc);
                    result.setMessage("retrieved object(s) with " + classDefinitionUID + " succeeded");
                } else {
                    result.successResult(null);
                    result.setMessage("retrieved object(s) with " + classDefinitionUID + ", and result is nothing.");
                }
            }
        } catch (Exception exception) {
            log.error("获取标段失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("获取标段失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "建立标段和施工区域关联关系", notes = "建立标段和施工区域关联关系")
    @PostMapping("/genRelBidSection2CWA")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "bsOBID", value = "BS OBID", example = "BS OBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "cwaOBID", value = "CWA OBID", example = "CWA OBID", required = true, dataTypeClass = String.class)
    }))
    public String genRelBidSection2CWA(@RequestBody JSONObject jsonObject) {
        ResultVo<Object> result = new ResultVo<Object>();
        try {
            String bsOBID = jsonObject.getString("bsOBID");
            String cwaOBID = jsonObject.getString("cwaOBID");
            if (StringUtils.isEmpty(bsOBID)) {
                result.errorResult("建立标段和施工区域关联关系失败,未选择标段!");
            } else if (StringUtils.isEmpty(cwaOBID)) {
                result.errorResult("建立标段和施工区域关联关系失败,未选择施工区域!");
            } else {
                this.bidSectionService.genRelBidSection2CWA(bsOBID, cwaOBID);
                result.successResult(true);
                result.setMessage("建立标段和施工区域关联关系成功.");
            }
        } catch (Exception e) {
            log.error("建立标段和施工区域关联关系失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("建立标段和施工区域关联关系失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "删除标段和施工区域关联关系", notes = "删除标段和施工区域关联关系")
    @PostMapping("/deleteRelBidSection2CWA")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "bsOBID", value = "BS OBID", example = "BS OBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "cwaOBID", value = "CWA OBID", example = "CWA OBID", required = true, dataTypeClass = String.class)
    }))
    public String deleteRelBidSection2CWA(@RequestBody JSONObject jsonObject) {
        ResultVo<Object> result = new ResultVo<Object>();
        try {
            this.bidSectionService.deleteRelBidSection2CWA(jsonObject.getString("bsOBID"), jsonObject.getString("cwaOBID"));
            result.successResult(true);
            result.setMessage("删除标段和施工区域关联关系成功.");
        } catch (Exception e) {
            log.error("删除标段和施工区域关联关系失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("删除标段和施工区域关联关系失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取可选择的施工区域")
    @RequestMapping(value = "/getSelectableCWA", method = RequestMethod.POST)
    @ApiOperationSupport(params = @DynamicParameters(name = "jsonObject", properties = {
            @DynamicParameter(name = "filters", value = "", example = "", required = true, dataTypeClass = JSONArray.class),
            @DynamicParameter(name = "formPurpose", value = "info", example = "list", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getSelectableCWA(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<ObjectDTOCollection>();
        String classDefinitionUID = classDefinitionType.EnumEnum.name();
        try {
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
            PageRequest pageRequest = PageUtility.parsePageRequestFromJSON(jsonObject);
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            OrderByParam orderByParam = new OrderByParam(jsonObject);
            if (formBase != null) {
                IObjectCollection selectableDocuments = bidSectionService.getSelectableCWA();
                if (selectableDocuments != null && selectableDocuments.hasValue()) {
                    List<ObjectDTO> collections = new ArrayList<>();
                    Iterator<IObject> objectIterator = selectableDocuments.GetEnumerator();
                    while (objectIterator.hasNext()) {
                        IObject next = objectIterator.next();
                        ObjectDTO currentForm = formBase.copyTo();
                        next.fillingForObjectDTO(currentForm);
                        collections.add(currentForm);
                    }
                    ObjectDTOCollection oc = PageUtility.pagedObjectDTOS(collections, filtersParam, orderByParam, pageRequest);
                    result.successResult(oc);
                    result.setMessage("获取可选择的施工区域成功!");
                } else {
                    result.successResult(null);
                    result.setMessage("未查询到可选择的施工区域.");
                }
            }
        } catch (Exception exception) {
            log.error("获取可选择的施工区域失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("获取可选择的施工区域失败!" + ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("新建施工区域")
    @PostMapping("/createCWA")
    public String createCWA(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTO> result = new ResultVo<>();
        try {
            ObjectDTO objectDTO = JSON.parseObject(jsonObject.toJSONString(), ObjectDTO.class);
            IObject iObject = this.bidSectionService.createCWA(objectDTO);
            CIMContext.Instance.ProcessCache().refresh(iObject);
            if (iObject != null) {
                iObject.refreshObjectDTO(objectDTO);
                result.successResult(iObject.toObjectDTO());
            }
        } catch (Exception exception) {
            log.error("新建施工区域失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.errorResult("新建施工区域失败!" + ExceptionUtil.getMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }
}
