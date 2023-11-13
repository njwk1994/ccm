package ccm.server.controller;

import ccm.server.business.ISchemaBusinessService;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.enums.classDefinitionType;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.module.vo.ResultVo;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.schema.interfaces.IObject;
import ccm.server.service.ICCMDocumentBusinessService;
import ccm.server.task.handler.DocumentRetrieveTask;
import ccm.server.util.CommonUtility;
import ccm.server.utils.DataRetrieveUtils;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;
import ccm.server.vo.DocumentRetrieveTaskVO;
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
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RestController
@Api(tags = "图文档管理")
@RequestMapping("/ccm/documentManagement")
public class CCMDocumentController {

    @Autowired
    private ICCMDocumentBusinessService documentManagementService;
    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    @GetMapping("/downloadFile")
    @ApiOperation("下载文档图纸物理文件")
    public void downloadFiles(@RequestParam("obid") String documentOBID, HttpServletResponse response) {
        this.documentManagementService.downloadFileByDocumentOBID(documentOBID, response);
    }

    @PostMapping("/getVersionObj")
    @ApiOperation("获取文档的version对象")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "obid", value = "文档obid", required = true, dataTypeClass = String.class)}))
    public String getDocumentVersionUidByDocMaster(@RequestBody JSONObject param) {
        Result<ObjectDTO> result = new Result<>();
        try {
            result.setResult(this.documentManagementService.getDocumentVersionUidByDocMaster(param.getString("obid")));
        } catch (Exception e) {
            log.error("获取文档的version对象!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("获取文档的version对象!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取系统所有的标记升版的图纸")
    @PostMapping("/getAllRevisedDocuments")
    public String getAllRevisedDocuments() {
        Result<List<ObjectDTO>> result = new Result<>();
        try {
            result.setResult(this.documentManagementService.getAllRevisedDocuments());
        } catch (Exception e) {
            log.error("获取系统所有的标记升版的图纸失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("获取系统所有的标记升版的图纸失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取升版标记的图纸中的设计对象")
    @PostMapping("/getRevisedDocDesignObjects")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "obid", value = "文档obid", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "classDefinitionUID", value = "文档obid", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "status", value = "状态", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "显示条目数", required = true, dataTypeClass = Integer.class),
            @DynamicParameter(name = "pageIndex", value = "当前页码", required = true, dataTypeClass = Integer.class)}))
    public String getRevisedDocDesignObjects(@RequestBody JSONObject jsonObject) {
        Result<ObjectDTOCollection> result = new Result<>();
        try {
            String lstrOBID = jsonObject.getString("obid");
            String lstrClassDefUID = jsonObject.getString(SchemaUtility.CLASS_DEFINITION_UID);
            Integer lintPageSize = jsonObject.getInteger("pageSize");
            Integer lintPageIndex = jsonObject.getInteger("pageIndex");
            result.setResult(this.documentManagementService.getRevisedDocDesignObjectsWithOperationState(lstrOBID, lstrClassDefUID, new PageRequest(lintPageIndex, lintPageSize)));
        } catch (Exception e) {
            log.error("获取升版标记的图纸中的设计对象失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("获取升版标记的图纸中的设计对象失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取当前使用的项目信息")
    @GetMapping("/getCurrentConfigInfo")
    public String getCurrentConfigInfo() {
        ResultVo<String> resultVo = new ResultVo<>();
        try {
            ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(null);
            resultVo.success(configurationItem != null ? configurationItem.Name() : "UNKnown");
        } catch (Exception e) {
            log.error("获取当前使用的项目信息失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            resultVo.errorResult("获取当前使用的项目信息失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(resultVo);
    }


    @ApiOperation("获取文档关联的设计对象")
    @PostMapping("/getAllCurrentDesignObjsByDocument")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "obid", value = "文档obid", required = true, dataTypeClass = String.class)}))
    public String getAllCurrentDesignObjsByDocument(@RequestBody JSONObject jsonObject) {
        ResultVo<List<ObjectDTO>> resultVo = new ResultVo<>();
        try {
            resultVo.successResult(this.documentManagementService.getAllCurrentDesignObjsByDocumentOBID(jsonObject.getString("obid")));
        } catch (Exception e) {
            log.error("获取文档关联的设计对象失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            resultVo.errorResult("获取文档关联的设计对象失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(resultVo);
    }

    @ApiOperation(value = "下载图纸导入模版", httpMethod = "POST", notes = "下载图纸导入模版", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PostMapping("/downloadDocumentExcelTemplate")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "type", value = "模版类型", example = "DD/SD", required = true, dataTypeClass = String.class)
    }))
    public String downloadDocumentExcelTemplate(@RequestBody JSONObject jsonObject, HttpServletResponse response) {
        ResultVo<Object> result = new ResultVo<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String type = jsonObject.getString("type");
            if (StringUtils.isBlank(type)) {
                result.errorResult("模版类型不可为空!");
                return JSON.toJSONString(result);
            }
            this.documentManagementService.downloadDocumentExcelTemplate(type, response);
        } catch (Exception e) {
            log.error("下载图纸导入模版失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            response.reset();
            result.errorResult("下载图纸导入模版失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取接受发布数据任务的过程信息")
    @PostMapping("/getDocumentRetrieveTaskProcessInfo")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "uuid", value = "任务的UUID", required = true, dataTypeClass = String.class)
    }))
    public String getDocumentRetrieveTaskProcessInfo(@RequestBody JSONObject jsonObject) {
        ResultVo<DocumentRetrieveTaskVO> result = new ResultVo<>();
        try {
            result.successResult(this.documentManagementService.getDocumentRetrieveTaskInfo(jsonObject.getString("uuid")));
        } catch (Exception e) {
            log.error("获取接受发布数据任务的过程信息!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取接受发布数据任务的过程信息!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取接受发布数据任务的过程信息")
    @PostMapping("/getCurrentUserAllDocumentRetrieveTaskProcessInfo")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "pageIndex", value = "当前页数", example = "1", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页显示数量", example = "30", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "filters", value = "过滤条件", example = "'[{xxxix:dsd}]'", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "orderBy", value = "排序条件", example = "'[{defUIDs:'',asc:true|false}]'", required = true, dataTypeClass = String.class)}))
    public String getCurrentUserAllDocumentRetrieveTaskProcessInfo(@RequestBody JSONObject jsonObject) {
        ResultVo<List<DocumentRetrieveTaskVO>> result = new ResultVo<>();
        try {
            int pageSize = ValueConversionUtility.toInteger(jsonObject.getString("pageSize"));
            int pageIndex = ValueConversionUtility.toInteger(jsonObject.getString("pageIndex"));
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            OrderByParam orderByParam = new OrderByParam(jsonObject);
            this.documentManagementService.getAllDocumentRetrieveTaskInfo(new PageRequest(pageIndex, pageSize), filtersParam, orderByParam, result);
        } catch (Exception e) {
            log.error("获取接受发布数据任务的过程信息!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取接受发布数据任务的过程信息!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("清除发布历史记录")
    @PostMapping("/clearDocRetrieveProcessingTasksHistory")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "startDate", value = "开始时间", required = true, dataTypeClass = String.class)}))
    public String clearDocRetrieveProcessingTasksHistory(@RequestBody JSONObject jsonObject) {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            String startDate = jsonObject.getString("startDate");
            this.documentManagementService.clearDocumentPublishHistoryData(startDate);
            result.setSuccess(true);
            result.setCode(200);
        } catch (Exception ex) {
            log.error("清除历史发布数据失败!{}", ExceptionUtil.getMessage(ex), ExceptionUtil.getRootCause(ex));
            result.errorResult("清除历史发布数据失败!" + ExceptionUtil.getMessage(ex));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取文档发布任务历史数据")
    @PostMapping("/getDocRetrieveHistoryTasks")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "pageIndex", value = "当前页数", example = "1", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页显示数量", example = "30", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "filters", value = "过滤条件", example = "'[{xxxix:dsd}]'", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "orderBy", value = "排序条件", example = "'[{defUIDs:'',asc:true|false}]'", required = true, dataTypeClass = String.class)}))
    public String getDocRetrieveHistoryTasks(@RequestBody JSONObject jsonObject) {
        ResultVo<List<DocumentRetrieveTaskVO>> result = new ResultVo<>();
        try {
            int pageSize = ValueConversionUtility.toInteger(jsonObject.getString("pageSize"));
            int pageIndex = ValueConversionUtility.toInteger(jsonObject.getString("pageIndex"));
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            OrderByParam orderByParam = new OrderByParam(jsonObject);
            this.documentManagementService.getAllDocRetrieveHisTasks(new PageRequest(pageIndex, pageSize), filtersParam, orderByParam, result);
        } catch (Exception e) {
            log.error("获取任务历史数据!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取任务历史数据!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }


    @ApiOperation(value = "导出图纸数据", httpMethod = "POST", notes = "导出图纸数据", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PostMapping("/exportDocumentToExcelTemplate")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "type", value = "模版类型(DD-详设,SD-加设)", example = "DD/SD", required = true, dataTypeClass = String.class)
    }))
    public String exportDocumentToExcelTemplate(@RequestBody JSONObject jsonObject, HttpServletResponse response) {
        ResultVo<Object> result = new ResultVo<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            String type = filtersParam.getFilters().get("DesignPhase");
            if (StringUtils.isBlank(type)) {
                throw new RuntimeException("图纸设计数据类型不能为空!");
            }
            OrderByParam orderByParam = new OrderByParam(jsonObject);
            this.documentManagementService.exportDocumentToExcelTemplate(type, filtersParam, orderByParam, response);
            response.getOutputStream().close();
        } catch (Exception e) {
            log.error("导出图纸数据失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("导出图纸数据失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "导出图纸焊口数据", httpMethod = "POST", notes = "导出图纸焊口数据", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PostMapping("/exportDocumentWeld")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "type", value = "模版类型(DD-详设,SD-加设)", example = "DD/SD", required = true, dataTypeClass = String.class)
    }))
    public String exportDocumentWeld(@RequestBody JSONObject jsonObject, HttpServletResponse response) {
        ResultVo<Object> result = new ResultVo<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String type = jsonObject.getString("type");
            /*if (StringUtils.isBlank(type)) {
                result.errorResult("模版类型不可为空!");
                return JSON.toJSONString(result);
            }*/
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            OrderByParam orderByParam = new OrderByParam(jsonObject);
            this.documentManagementService.exportDocumentWeld(type, filtersParam, orderByParam, response);
            response.getOutputStream().close();
        } catch (Exception e) {
            log.error("导出图纸焊口数据失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("导出图纸焊口数据失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "导出图纸焊口焊接当量报表", httpMethod = "POST", notes = "导出图纸焊口焊接当量报表", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PostMapping("/exportDocumentWeldEquivalent")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "type", value = "模版类型(DD-详设,SD-加设)", example = "DD/SD", required = true, dataTypeClass = String.class)
    }))
    public String exportDocumentWeldEquivalent(@RequestBody JSONObject jsonObject, HttpServletResponse response) {
        ResultVo<Object> result = new ResultVo<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            String type = jsonObject.getString("type");
            /*if (StringUtils.isBlank(type)) {
                result.errorResult("模版类型不可为空!");
                return JSON.toJSONString(result);
            }*/
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            OrderByParam orderByParam = new OrderByParam(jsonObject);
            this.documentManagementService.exportDocumentWeldEquivalent(type, filtersParam, orderByParam, response);
            response.getOutputStream().close();
        } catch (Exception e) {
            log.error("导出图纸焊口焊接当量报表失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("导出图纸焊口焊接当量报表失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "导出焊接数据", httpMethod = "POST", notes = "导出焊接数据", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PostMapping("/exportWeldData")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {}))
    public String exportWeldData(@RequestBody JSONObject jsonObject, HttpServletResponse response) {
        ResultVo<Object> result = new ResultVo<>();
        result.setCode(200);
        result.setSuccess(true);
        try {
            FiltersParam filtersParam = new FiltersParam(jsonObject);
            OrderByParam orderByParam = new OrderByParam(jsonObject);
            documentManagementService.exportWeldData(filtersParam, orderByParam, response);
            response.getOutputStream().close();
        } catch (Exception e) {
            log.error("导出焊接数据失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("导出焊接数据失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("通过Excel接受发布图纸")
    @PostMapping("/retrieveDocumentByExcel")
    public String retrieveDocumentByExcel(@RequestParam(name = "importExcel") MultipartFile file) {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            this.documentManagementService.retrieveDrawingDocumentAndDesignObjectsByExcel(file);
            result.setResult(true);
            result.setSuccess(true);
            result.setMessage("完成!");
        } catch (Exception e) {
            log.error("通过Excel接受发布图纸失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("通过Excel接受发布图纸失败!" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("通过压缩包接受发布图纸和上传图纸文件")
    @PostMapping("/retrieveDocumentByZip")
    public String retrieveDocumentByZip(@RequestParam MultipartFile file, @RequestParam String minioPath) {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            this.documentManagementService.retrieveDocumentByZip(file, minioPath);
            result.successResult(true);
        } catch (Exception e) {
            log.error("【通过Zip导入图纸】{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("【通过Zip导入图纸】" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("通过JSON文件接受发布图纸")
    @PostMapping("/retrieveDocument")
    public String retrieveDocument(@RequestParam(name = "file") MultipartFile file) {
        ResultVo<String> result = new ResultVo<>();
        try {
            result.successResult(this.documentManagementService.retrieveDrawingDocumentAndDesignObjects(file));
            result.setMessage("已提交后台进行");
        } catch (Exception e) {
            log.error("通过JSON文件接受发布图纸失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("通过JSON文件接受发布图纸失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("刷新标段信息缓存信息")
    @PostMapping("/refreshBidSectionCache")
    public String refreshBidSectionCache() {
        ResultVo<String> result = new ResultVo<>();
        try {
            DocumentRetrieveTask.initCWAAndBidSectionInfos();
            result.setSuccess(true);
            result.setCode(200);
            result.setMessage("刷新完成!");
        } catch (Exception e) {
            log.error("刷新标段信息缓存信息失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("刷新标段信息缓存信息失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取版本信息")
    @GetMapping("/getMajorRevisionByRevisionScheme")
    public String getMajorRevisionByRevisionScheme(@RequestParam(name = "UID") String pstrRevisionSchemeUID) {
        Result<String[]> result = new Result<>();
        try {
            result.setResult(this.documentManagementService.getMajorRevisionByRevisionScheme(pstrRevisionSchemeUID));
            result.success("成功!");
        } catch (Exception e) {
            log.error("获取版本信息失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("获取版本信息失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("接受发布数据")
    @PostMapping("/retrievePublishData")
    public String retrievePublishData(@RequestBody JSONObject jsonObject) {
        ResultVo<Boolean> resultVo = new ResultVo<>();
        try {
            this.documentManagementService.retrieveDrawingDocumentFromPublishTool(jsonObject);
            resultVo.successResult(true);
            resultVo.setMessage("已提交给后台处理");
        } catch (Exception e) {
            log.error("接受发布数据失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            resultVo.errorResult("接受发布数据失败!{}" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(resultVo);
    }

    @ApiOperation("获取版本规则集合")
    @PostMapping("/getRevisionScheme")
    public String getRevisionScheme() {
        ResultVo<List<ObjectDTO>> resultVo = new ResultVo<>();
        try {
            List<ObjectDTO> revisionSchemesWithDTOStyle = this.documentManagementService.getRevisionSchemesWithDTOStyle();
            resultVo.successResult(revisionSchemesWithDTOStyle);
        } catch (Exception e) {
            log.error("获取版本规则集合失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            resultVo.errorResult("获取版本规则集合失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(resultVo);
    }

    @ApiOperation("获取下一个版本")
    @PostMapping("/getNextRevisionValue")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "documentUid", value = "文档uid", required = true, dataTypeClass = String.class), @DynamicParameter(name = "isRevise", value = "是否升版", required = true, dataTypeClass = Boolean.class)}))
    public String getNextRevision(@RequestBody JSONObject jsonObject) {
        ResultVo<JSONObject> resultVo = new ResultVo<>();
        try {
            String nextRevision = this.documentManagementService.getNextRevision(jsonObject);
            JSONObject result = new JSONObject();
            JSONObject props = new JSONObject();
            props.put(propertyDefinitionType.CIMRevisionSchema.toString(), nextRevision);
            result.put(SchemaUtility.PROPERTIES, props);
            result.put(SchemaUtility.CLASS_DEFINITION_UID, classDefinitionType.CIMRevisionScheme.toString());
            log.info(JSONObject.toJSONString(result));
            resultVo.successResult(result);
        } catch (Exception e) {
            log.error("获取下一个版本失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            resultVo.errorResult("获取下一个版本失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(resultVo);
    }

    @ApiOperation("手动创建/更新图纸")
    @PostMapping("/createOrUpdateDocument")
    public String createOrUpdateDocument(@RequestBody JSONObject jsonObject) {
        ResultVo<String> result = new ResultVo<>();
        try {
            if (jsonObject == null || jsonObject.isEmpty()) {
                throw new Exception("图纸属性不能为空!");
            }
            result.successResult(documentManagementService.createOrUpdateDocument(jsonObject));
        } catch (Exception e) {
            log.error("手动创建/更新图纸失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("手动创建/更新图纸失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("手动创建设计数据")
    @PostMapping("/createDesignDataForDocument")
    public String createDesignDataForDocument(@RequestBody JSONObject jsonObject) {
        ResultVo<Object> result = new ResultVo<>();
        try {
            if (jsonObject == null || jsonObject.isEmpty()) {
                throw new Exception("设计数据属性不能为空!");
            }
            documentManagementService.createDesignDataForDocument(jsonObject);
            result.successResult(true);
        } catch (Exception e) {
            log.error("手动创建设计数据失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("手动创建设计数据失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("手动更新设计数据")
    @PostMapping("/updateDesignData")
    public String updateDesignData(@RequestBody JSONObject jsonObject) {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            if (jsonObject == null || jsonObject.isEmpty()) {
                throw new Exception("设计数据属性不能为空!");
            }
            documentManagementService.updateDesignData(jsonObject);
            result.successResult(true);
        } catch (Exception e) {
            log.error("手动更新设计数据失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("手动更新设计数据失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取图纸对应的施工数据分类")
    @PostMapping("/getDocumentConstructionTypes")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "id", value = "图纸OBID", example = "图纸OBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "showDeleted", value = "true/false", example = "true", required = true, dataTypeClass = Boolean.class),
    }))
    public String getDocumentConstructionTypes(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<>();

        String id = CommonUtility.getId(jsonObject);
        Boolean showDeleted = jsonObject.getBoolean("showDeleted");
        if (null == showDeleted) {
            showDeleted = false;
        }
        String formPurpose = ccm.server.enums.formPurpose.List.toString();
        String classDefinitionUID = DataRetrieveUtils.CCM_CONSTRUCTION_TYPE;

        if (StringUtils.isBlank(id)) {
            result.errorResult("图纸OBID不可为空!");
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
                    IObjectCollection documentConstructionTypes = documentManagementService.getDocumentConstructionTypes(id, showDeleted);
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
                    result.errorResult("获取图纸对应的施工数据分类FORM失败!");
                }
            } catch (Exception e) {
                log.error("获取图纸对应的施工数据分类失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
                result.errorResult("获取图纸对应的施工数据分类失败!" + ExceptionUtil.getMessage(e));
            }
        }

        return CommonUtility.toJsonString(result);
    }

}
