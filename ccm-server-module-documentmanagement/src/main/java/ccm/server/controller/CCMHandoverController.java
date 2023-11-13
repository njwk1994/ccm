package ccm.server.controller;

import ccm.server.business.ICCMHandoverMailService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.enums.MailBoxType;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.module.vo.ResultVo;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.schema.interfaces.IObject;
import ccm.server.util.CommonUtility;
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
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/9/21 10:20
 */
@Slf4j
@RestController
@Api(tags = "移交管理")
@RequestMapping("/ccm/handover")
public class CCMHandoverController {

    @Autowired
    private ICCMHandoverMailService handoverMailService;
    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    @ApiOperation(value = "获取移交邮件类型", httpMethod = "POST", notes = "获取移交邮件类型")
    @PostMapping("/getMailBoxType")
    public String getMailBoxType() {
        ResultVo<List<ObjectDTO>> result = new ResultVo<>();
        try {
            IObjectCollection mailBoxType = handoverMailService.getMailBoxType();
            List<ObjectDTO> objectDTOS = mailBoxType.toObjectDTOs();
            result.successResult(objectDTOS);
        } catch (Exception e) {
            log.error("获取移交邮件类型失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取移交邮件类型失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "获取对应的邮件", httpMethod = "POST", notes = "获取对应的邮件")
    @PostMapping("/getMails")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "formPurpose", value = "info", example = "info", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "classDefinitionUID", value = "CCMHandoverMail", example = "CCMHandoverMail", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageSize", value = "每页条数", example = "5", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "pageIndex", value = "页码", example = "1", required = true, dataTypeClass = String.class)
    }))
    public String getMails(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTOCollection> result = new ResultVo<>();
        try {
            String formPurpose = jsonObject.getString("formPurpose");
            String classDefinitionUID = jsonObject.getString("classDefinitionUID");
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

            Map<String, String> filters = filtersParam.getFilters();
            String mailBoxTypeStr = filters.get("MailBoxType");
            MailBoxType mailBoxType;
            try {
                mailBoxType = MailBoxType.valueOf(mailBoxTypeStr);
            } catch (IllegalArgumentException e) {
                throw new Exception("未知文件类型[" + mailBoxTypeStr + "]");
            }

            IObjectCollection mails = handoverMailService.getMails(filtersParam, orderByParam, pageRequest.getPageIndex(), pageRequest.getPageSize());
            ObjectDTOCollection oc;
            if (MailBoxType.EN_Inbox.equals(mailBoxType)) {
                oc = PageUtility.pagedObjectDTOS(mails.toObjectDTOs(), null, orderByParam, pageRequest);
            } else {
                oc = SchemaUtility.toObjectDTOCollection(formPurpose, formBase, mails);
            }
            result.successResult(oc);

        } catch (Exception e) {
            log.error("获取对应的邮件失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取对应的邮件失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("发送移交单")
    @PostMapping(value = "/sendMail")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "mailOBIDs", value = "用','分割的mailOBID", example = "mailOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "userIds", value = "用','分割的userId", example = "userIds", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "roleIds", value = "用','分割的roleId", example = "roleIds", required = true, dataTypeClass = String.class)
    }))
    public String sendMail(@RequestBody JSONObject requestBody) {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            String mailOBIDs = requestBody.getString("mailOBIDs");
            String userIds = requestBody.getString("userIds");
            String roleIds = requestBody.getString("roleIds");
            if (StringUtils.isEmpty(mailOBIDs)) {
                throw new Exception("发送移交单失败,mailOBIDs不可为空!");
            }
            this.handoverMailService.sendMails(mailOBIDs, userIds, roleIds);
            result.successResult(true);
        } catch (Exception e) {
            log.error("发送移交单失败,{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("发送移交单失败," + ExceptionUtil.getSimpleMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("接收移交单")
    @PostMapping(value = "/receiveMails")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "mailOBIDs", value = "用','分割的mailOBID", example = "mailOBID", required = true, dataTypeClass = String.class)
    }))
    public String receiveMails(@RequestBody JSONObject requestBody) {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            String mailOBIDs = requestBody.getString("mailOBIDs");
            if (StringUtils.isEmpty(mailOBIDs)) {
                throw new Exception("接收移交单失败,mailOBIDs不可为空!");
            }
            this.handoverMailService.receiveMails(mailOBIDs);
            result.successResult(true);
        } catch (Exception e) {
            log.error("接收移交单失败,{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("接收移交单失败," + ExceptionUtil.getSimpleMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("新建移交单")
    @PostMapping("/createMail")
    public String createMail(@RequestBody JSONObject jsonObject) {
        ResultVo<ObjectDTO> result = new ResultVo<>();
        try {
            ObjectDTO objectDTO = JSON.parseObject(jsonObject.toJSONString(), ObjectDTO.class);
            IObject iObject = this.handoverMailService.createMail(objectDTO);
            if (iObject != null) {
                iObject.refreshObjectDTO(objectDTO);
                result.success(iObject.toObjectDTO());
            }
        } catch (Exception exception) {
            result.errorResult(exception.getMessage());
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("删除移交单")
    @PostMapping(value = "/removeMail")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "mailOBIDs", value = "用','分割的mailOBID", example = "mailOBID", required = true, dataTypeClass = String.class)
    }))
    public String removeMail(@RequestBody JSONObject requestBody) {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            String mailOBIDs = requestBody.getString("mailOBIDs");
            if (StringUtils.isEmpty(mailOBIDs)) {
                throw new Exception("删除移交单失败,mailOBIDs不可为空!");
            }
            this.handoverMailService.removeMail(mailOBIDs);
            result.successResult(true);
        } catch (Exception e) {
            log.error("删除移交单败,{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("删除移交单失败," + ExceptionUtil.getSimpleMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "上传文件")
    @PostMapping("/uploadFile")
    public String uploadFile(@ApiParam(name = "file", value = "file", required = true) @RequestParam MultipartFile file,
                             @ApiParam(name = "fileOBID", value = "fileOBID", required = true) @RequestParam String fileOBID) {
        ResultVo<Object> result = new ResultVo<>();
        try {

            if (StringUtils.isEmpty(fileOBID)) {
                throw new Exception("上传文件失败,对应文件数据OBID不可为空!");
            }
            handoverMailService.uploadFile(file, fileOBID);
            result.successResult(true);
        } catch (Exception e) {
            log.error("上传文件失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("上传文件失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation(value = "下载文件")
    @PostMapping("/downloadFile")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "fileOBID", value = "fileOBID", example = "fileOBID", required = true, dataTypeClass = String.class)
    }))
    public String downloadFile(@RequestBody JSONObject requestBody, HttpServletResponse response) {
        ResultVo<Object> result = new ResultVo<>();
        try {
            String fileOBID = requestBody.getString("fileOBID");
            if (StringUtils.isEmpty(fileOBID)) {
                throw new Exception("下载文件失败,对应文件数据OBID不可为空!");
            }
            handoverMailService.downloadFile(fileOBID,response);
            result.successResult(true);
        } catch (Exception e) {
            log.error("下载文件失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("下载文件失败!" + ExceptionUtil.getMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("删除文件")
    @PostMapping(value = "/deleteFile")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "mailOBID", value = "mailOBID", example = "mailOBID", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "fileOBIDs", value = "用','分割的fileOBID", example = "用','分割的fileOBID", required = true, dataTypeClass = String.class)
    }))
    public String deleteFile(@RequestBody JSONObject requestBody) {
        ResultVo<Boolean> result = new ResultVo<>();
        try {
            String mailOBID = requestBody.getString("mailOBID");
            String fileOBIDs = requestBody.getString("fileOBIDs");
            if (StringUtils.isEmpty(mailOBID) || StringUtils.isEmpty(fileOBIDs)) {
                throw new Exception("删除文件失败,mailOBID和fileOBIDs不可为空!");
            }
            handoverMailService.deleteFile(mailOBID, fileOBIDs);
            result.successResult(true);
        } catch (Exception e) {
            log.error("删除文件失败,{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("删除文件失败," + ExceptionUtil.getSimpleMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation("获取可打包的移交单")
    @PostMapping(value = "/getToPackage")
    public String getToPackage() {
        ResultVo<List<ObjectDTO>> result = new ResultVo<>();
        try {
            IObjectCollection toPackage = handoverMailService.getToPackage();
            List<ObjectDTO> objectDTOS = toPackage.toObjectDTOs();
            result.successResult(objectDTOS);
        } catch (Exception e) {
            log.error("获取可打包的移交单失败,{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取可打包的移交单失败," + ExceptionUtil.getSimpleMessage(e));
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "打包并下载移交包", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PostMapping(value = "/packageToDownload")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "mailOBID", value = "mailOBID", example = "mailOBID", required = true, dataTypeClass = String.class),
    }))
    public String packageToDownload(@RequestBody JSONObject requestBody, HttpServletResponse response) {
        ResultVo<List<ObjectDTO>> result = new ResultVo<>();
        try {
            String mailOBID = requestBody.getString("mailOBID");
            if (StringUtils.isEmpty(mailOBID)) {
                throw new Exception("删除文件失败,mailOBID不可为空!");
            }
            handoverMailService.packageToDownload(mailOBID, response);
        } catch (Exception e) {
            log.error("获取可打包的移交单失败,{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("获取可打包的移交单失败," + ExceptionUtil.getSimpleMessage(e));
        }
        return JSON.toJSONString(result);
    }
}
