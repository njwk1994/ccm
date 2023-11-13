package ccm.server.controller;

import ccm.server.business.ISchemaBusinessService;
import ccm.server.cache.IProcessCache;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.dto.base.ObjectXmlDTO;
import ccm.server.dto.base.OptionItemDTO;
import ccm.server.enums.classDefinitionType;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.relCollectionTypes;
import ccm.server.enums.relDefinitionType;
import ccm.server.model.FiltersParam;
import ccm.server.model.KeyValuePair;
import ccm.server.model.OrderByParam;
import ccm.server.model.PropertyHierarchyVo;
import ccm.server.module.vo.ResultVo;
import ccm.server.params.PageRequest;
import ccm.server.processors.ThreadsProcessor;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.schema.interfaces.ICIMUser;
import ccm.server.schema.interfaces.IObject;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@RestController("schemaController")
@RequestMapping("/cim/schema")
@Api(tags = "CIM-Schema")
@Slf4j
public class SchemaController {
    @Autowired
    private ISchemaBusinessService schemaBusinessService;
    @Autowired
    private IProcessCache processCache;

    @ApiOperation("生成Form表单为指定的ClassDef")
    @GetMapping("/generateFormForClassDef")
    public String generateForm(String classDefinitionUID) {
        Result<ObjectDTO> result = new Result<>();
        try {
            IObject object = this.schemaBusinessService.generateForm(classDefinitionUID);
            if (object != null)
                result.setResult(object.toObjectDTO());
            else
                result.error500("form generation failed as form object failed during creation progress");
        } catch (Exception exception) {
            log.error("生成Form表单为指定的ClassDef异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("生成Form表单为指定的ClassDef异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取form表单项的为SearchRSingleRelation的选项")
    @PostMapping("/getSearchRelationshipOptions")
    public String getSearchRelationshipOptions(@RequestBody JSONObject param) {
        Result<List<OptionItemDTO>> result = new Result<>();
        try {
            result.success("成功");
            result.setResult(this.schemaBusinessService.getSearchRelationshipOptions(param));
        } catch (Exception ex) {
            log.error("生成Form表单为指定的ClassDef异常!异常信息:{}.\n", ExceptionUtil.getMessage(ex), ExceptionUtil.getRootCause(ex));
            result.error500("生成Form表单为指定的ClassDef异常!异常信息:" + ExceptionUtil.getSimpleMessage(ex));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取通用格式的表单")
    @PostMapping("/getForm")
    public String getForm(@RequestBody JSONObject jsonObject) {
        Result<ObjectDTO> result = new Result<>();
        String formPurpose = jsonObject.getString("formPurpose");
        String classDefinitionUID = jsonObject.getString("classDefinitionUID");
        try {
            ICIMForm form = this.schemaBusinessService.getForm(formPurpose, classDefinitionUID);
            ObjectDTO objectDTO = null;
            if (form != null) {
                objectDTO = form.generatePopup(formPurpose);
            } else {
                objectDTO = this.schemaBusinessService.generateDefaultPopup(classDefinitionUID);
            }
            if (objectDTO != null) {
                objectDTO.manualSetClassDefinitionUID(classDefinitionUID);
                objectDTO.manualSetFormPurpose(formPurpose);
                if (formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.Update.toString()) || formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.Revise.toString())) {
                    String obid = jsonObject.getString("obid");
                    SchemaUtility.setValueForUpdatePurpose(objectDTO, obid, classDefinitionUID);
                }
                result.setResult(objectDTO);
                result.success("form for " + classDefinitionUID + " retrieved succeeded");
            } else
                result.error500("generation form failed as nothing retrieved from database");
        } catch (Exception exception) {
            log.error("获取通用格式的表单异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("获取通用格式的表单异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("重载数据库缓存")
    @PostMapping("/reloadCache")
    public String reloadCache() {
        Result result = new Result();
        try {
            this.schemaBusinessService.reloadCache();
            result.success("reload cache successfully");
        } catch (Exception exception) {
            log.error("重载数据库缓存异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("重载数据库缓存异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("通用创建")
    @PostMapping("/generalCreate")
    public String generalCreate(@RequestBody JSONObject jsonObject) {
        Result<ObjectDTO> result = new Result<>();
        try {
            ObjectDTO objectDTO = JSON.parseObject(jsonObject.toJSONString(), ObjectDTO.class);
            IObject iObject = this.schemaBusinessService.generalCreate(objectDTO);
            if (iObject != null) {
                iObject.refreshObjectDTO(objectDTO);
                result.setResult(iObject.toObjectDTO());
            }
        } catch (Exception exception) {
            log.error("通用创建异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("通用创建异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("通用创建")
    @PostMapping("/generalBatchCreate")
    public String generalCreate(@RequestBody JSONArray jsonArray) {
        Result<List<ObjectDTO>> result = new Result<>();
        try {
            List<ObjectDTO> list = new ArrayList<>();
            for (Object o : jsonArray) {
                ObjectDTO objectDTO = JSON.parseObject(JSONObject.toJSONString(o), ObjectDTO.class);
                IObject iObject = this.schemaBusinessService.generalCreate(objectDTO);
                if (iObject != null) {
                    iObject.refreshObjectDTO(objectDTO);
                    list.add(iObject.toObjectDTO());
                }
            }
            result.setResult(list);
        } catch (Exception exception) {
            log.error("通用创建异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("通用创建异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("根据关系定义获取关联关系")
    @PostMapping("/getRelsByRelDefUIDs")
    public String getRelsByRelDefUIDs(@RequestBody JSONObject jsonObject) {
        Result<List<ObjectDTO>> result = new Result<>();
        String relDefUIDs = jsonObject.getString("relDefUIDs");
        try {
            IObjectCollection rels = this.schemaBusinessService.getRelsByRelDef(Arrays.stream(relDefUIDs.split(",")).collect(Collectors.toList()));
            if (rels != null && rels.hasValue()) {
                List<ObjectDTO> lcolCollection = new ArrayList<>();
                Iterator<IObject> objectIterator = rels.GetEnumerator();
                while (objectIterator.hasNext()) {
                    IObject current = objectIterator.next();
                    lcolCollection.add(current.toObjectDTO());
                }
                result.setResult(lcolCollection);
            }
        } catch (Exception exception) {
            log.error("根据关系定义获取关联关系异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("根据关系定义获取关联关系异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("通用更新")
    @PostMapping("/generalUpdate")
    public String generalUpdate(@RequestBody JSONObject jsonObject) {
        Result<ObjectDTO> result = new Result<>();
        try {
            ObjectDTO objectDTO = JSON.parseObject(jsonObject.toJSONString(), ObjectDTO.class);
            IObject iObject = this.schemaBusinessService.generalUpdate(objectDTO);
            if (iObject != null) {
                iObject.refreshObjectDTO(objectDTO);
                result.setResult(iObject.toObjectDTO());
            }
        } catch (Exception exception) {
            log.error("通用更新异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("通用更新异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("通用批量更新")
    @PostMapping("/generalBatchUpdate")
    public String generalBatchUpdate(@RequestBody JSONObject jsonObject) {
        Result<ObjectDTO> result = new Result<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            if (jsonArray != null) {
                List<ObjectDTO> objectDTOS = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    String jsonString = JSON.toJSONString(jsonArray.get(i));
                    JSONObject jsonObject1 = JSON.parseObject(jsonString);
                    ObjectDTO objectDTO = JSON.parseObject(jsonString, ObjectDTO.class);
                    objectDTO.addItemIfNotExist(propertyDefinitionType.OBID.toString(), jsonObject1.getString("obid"));
                    objectDTO.addItemIfNotExist(propertyDefinitionType.Name.toString(), jsonObject1.getString("name"));
                    objectDTO.addItemIfNotExist(propertyDefinitionType.UID.toString(), jsonObject1.getString("uid"));
                    objectDTOS.add(objectDTO);
                }
                IObjectCollection objectCollection = this.schemaBusinessService.generalUpdate(objectDTOS);
            }
        } catch (Exception exception) {
            log.error("通用批量更新异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("通用批量更新异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }


    @ApiOperation("通用删除关联关系")
    @PostMapping("/deleteRelationship")
    public String deleteRelationship(@RequestBody JSONObject jsonObject) {
        Result<Boolean> result = new Result<>();
        String relDefUID = jsonObject.getString("relDef");
        String obid = jsonObject.getString("obid");
        try {
            result.setResult(this.schemaBusinessService.deleteRelationship(relDefUID, obid));
        } catch (Exception exception) {
            log.error("通用删除关联关系异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("通用删除关联关系异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("通用基本查询")
    @PostMapping("/generalQuery")
    public String generalQuery(@RequestBody JSONObject jsonObject) {
        Result<ObjectDTOCollection> result = new Result<>();
        int pageSize = ValueConversionUtility.toInteger(jsonObject.getString("pageSize"));
        int pageIndex = ValueConversionUtility.toInteger(jsonObject.getString("pageIndex"));
        String classDefinitionUID = jsonObject.getString("classDefinitionUID");
        boolean expansionInd = jsonObject.getBooleanValue("expansionInd");
        String expansionPath = jsonObject.getString("expansionPath");
        String targetClassificationUid = jsonObject.getString("targetClassificationUid");
        FiltersParam filtersParam = new FiltersParam(jsonObject);
        OrderByParam orderByParam = new OrderByParam(jsonObject);
        String formPurpose = jsonObject.getString("formPurpose");
        try {
            if (StringUtils.isEmpty(classDefinitionUID))
                throw new Exception("no class definition provided for query processing");
            if (StringUtils.isEmpty(formPurpose))
                formPurpose = ccm.server.enums.formPurpose.Info.toString();
            if (expansionInd && StringUtils.isEmpty(targetClassificationUid))
                throw new Exception("no class definition provided for query expansion processing");
            StopWatch stopWatch = PerformanceUtility.start();
            ICIMForm form = null;
            if (expansionInd) {
                form = this.schemaBusinessService.getForm(formPurpose, targetClassificationUid);
            } else {
                form = this.schemaBusinessService.getForm(formPurpose, classDefinitionUID);
            }

            ObjectDTO formPopupTemplate = null;
            if (form != null)
                formPopupTemplate = form.generatePopup(formPurpose);
            else {
                if (expansionInd)
                    formPopupTemplate = this.schemaBusinessService.generateDefaultPopup(targetClassificationUid);
                else
                    formPopupTemplate = this.schemaBusinessService.generateDefaultPopup(classDefinitionUID);
            }

            log.info("form generation completed" + PerformanceUtility.stop(stopWatch));
            if (formPopupTemplate != null) {
                stopWatch = PerformanceUtility.start();
                List<String> expansionPaths = formPopupTemplate.getExpansionPaths();
                log.info("expansion path(s) is " + String.join(",", expansionPaths) + " and will add into query progress for expansion");
                IObjectCollection objectCollection = this.schemaBusinessService.generalQuery(classDefinitionUID, pageIndex, pageSize, orderByParam.getOrderByWrappers(), filtersParam.getFilters(), expansionPaths);
                log.info("query progress completed" + PerformanceUtility.stop(stopWatch));
                if (objectCollection != null && objectCollection.hasValue()) {
                    stopWatch = PerformanceUtility.start();
                    List<ObjectDTO> collections = new ArrayList<>();
                    if (expansionInd) {
                        objectCollection = this.schemaBusinessService.expandObjs(objectCollection, expansionPath, targetClassificationUid);
                    }
                    Iterator<IObject> objectIterator = objectCollection.GetEnumerator();
                    while (objectIterator.hasNext()) {
                        IObject next = objectIterator.next();
                        ObjectDTO currentForm = formPopupTemplate.copyTo();
                        next.fillingForObjectDTO(currentForm);
                        if (CommonUtility.isDocument(next.ClassDefinitionUID())) {
                            //特殊处理文档属性
                            SchemaUtility.fillDocumentPropForObjectDTO(currentForm, next);
                        }
                        collections.add(currentForm);
                    }
                    ObjectDTOCollection oc = new ObjectDTOCollection(collections);
                    oc.setCurrent(objectCollection.PageResult().getCurrent());
                    oc.setSize(objectCollection.PageResult().getSize());
                    oc.setTotal(objectCollection.PageResult().getTotal());
                    log.info("render progress completed" + PerformanceUtility.stop(stopWatch));
                    result.setResult(oc);
                    result.success("retrieved object(s) with " + classDefinitionUID + " succeeded");
                } else
                    result.success("retrieved object(s) with " + classDefinitionUID + ", and result is nothing.");
            }
        } catch (Exception exception) {
            log.error("通用基本查询异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("通用基本查询异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取对象的关联关系")
    @PostMapping("/getRelsForObject")
    public String getRelsForObject(@RequestBody JSONObject jsonObject) {
        Result<List<ObjectDTO>> result = new Result<>();
        try {
            String obid = jsonObject.getString("obid");
            String classDefinitionUID = jsonObject.getString("classDefinitionUID");
            String relDirection = jsonObject.getString("direction");
            relCollectionTypes collectionTypes = "+".equalsIgnoreCase(relDirection) ? relCollectionTypes.End1s : relCollectionTypes.End2s;
            IObjectCollection objectCollection = this.schemaBusinessService.getRelsForObject(obid, classDefinitionUID, collectionTypes);
            List<ObjectDTO> objectDTOS = objectCollection.toObjectDTOs();
            result.setResult(objectDTOS);
        } catch (Exception exception) {
            log.error("获取对象的关联关系异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("获取对象的关联关系异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("展开获取关联关系")
    @PostMapping("/expandRelationships")
    public String expandRelationships(@RequestBody JSONObject jsonObject) {
        Result<List<ObjectDTO>> result = new Result<>();
        try {
            String objectUID = jsonObject.getString("uid");
            String classDefinitionUID = jsonObject.getString("classDefinitionUID");
            String expansionPath = jsonObject.getString("path");
            IObjectCollection objectCollection = this.schemaBusinessService.expandRelationships(objectUID, classDefinitionUID, expansionPath);
            List<ObjectDTO> objectDTOS = objectCollection.toObjectDTOs();
            result.setResult(objectDTOS);
        } catch (Exception exception) {
            log.error("展开获取关联关系异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("展开获取关联关系异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }


    @ApiOperation("获取枚举列表项")
    @PostMapping("/getEnumListTypeEntries")
    public String getEnumListTypeEntries(@RequestBody JSONObject jsonObject) {
        Result<List<ObjectDTO>> result = new Result<>();
        try {
            String objectUID = jsonObject.getString("objectUID");
            IObjectCollection objectCollection = this.schemaBusinessService.expandRelationships(objectUID, classDefinitionType.EnumListType.toString(), "+" + relDefinitionType.contains.toString());
            List<ObjectDTO> objectDTOS = objectCollection.toObjectDTOs();
            result.setResult(objectDTOS);
        } catch (Exception exception) {
            log.error("获取枚举列表项异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("获取枚举列表项异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("查看缓存明细信息")
    @PostMapping("/viewCacheDetails")
    public String viewCacheDetails(@RequestBody JSONObject jsonObject) {
        Result<ObjectDTOCollection> result = new Result<>();

        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("查看Schema信息")
    @PostMapping("/viewSchemaInfo")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "uid", value = "要查看对象的uid", example = "", required = true, dataTypeClass = String.class)}))
    public String viewSchemaInfo(@RequestBody JSONObject jsonObject) {
        Result<ObjectDTO> result = new Result<>();
        try {
            IObject objectByUID = this.schemaBusinessService.getSchemaObjectByUID(jsonObject.getString("uid"));
            result.setResult(objectByUID.toObjectDTO());
            result.setSuccess(true);
        } catch (Exception exception) {
            log.error("查看Schema信息异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("查看Schema信息异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("根据OBID删除指定的项目")
    @PostMapping("/deleteProject")
    public String deleteProject(@RequestBody JSONObject jsonObject) {
        Result<Boolean> result = new Result<>();
        String obid = jsonObject.getString("obid");
        if (!StringUtils.isEmpty(obid)) {
            result.setResult(CIMContext.Instance.deleteConfigurationItem(obid));
            if (result.getResult())
                result.success("删除项目成功");
            else
                result.error500("无法删除该项目");
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("导入SchemaXml文件")
    @PostMapping("/loadSchemaXml")
    public String loadSchemaXmlFile(@RequestParam(name = "files") MultipartFile[] files) {
        ResultVo<Boolean> resultVo = new ResultVo<>();
        try {
            if (files != null && files.length > 0) {
                for (MultipartFile file : files) {
                    schemaBusinessService.loadSchemaXml(file);
                }
            }
            resultVo.successResult(true);
        } catch (Exception ex) {
            log.error("导入SchemaXml文件异常!异常信息:{}.\n", ExceptionUtil.getMessage(ex), ExceptionUtil.getRootCause(ex));
            resultVo.errorResult("导入SchemaXml文件异常!异常信息:" + ExceptionUtil.getSimpleMessage(ex));
        }
        return CommonUtility.toJsonString(resultVo);
    }


    @ApiOperation("删除指定的对象")
    @PostMapping(value = "/deleteObject")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "obid", value = "要删除的对象obid", example = "", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "classDefinitionUID", value = "对象的类型定义", example = "", required = true, dataTypeClass = String.class)}))
    public String deleteObject(@RequestBody JSONObject jsonObject) {
        Result<Boolean> result = new Result<>();
        try {
            String lstrOBID = jsonObject.getString("obid");
            String lstrClassDefinitionUID = jsonObject.getString("classDefinitionUID");
            boolean flag = this.schemaBusinessService.deleteObject(lstrOBID, lstrClassDefinitionUID, true);
            if (flag)
                result.success("delete succeeded");
            else
                result.error500("failed to delete object");
        } catch (Exception exception) {
            log.error("删除指定的对象异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("删除指定的对象异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("批量删除同类对象")
    @PostMapping(value = "/deleteObjects")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "obids", value = "要删除的对象obids", example = "XXXX,XXXX,XXXX", required = true, dataTypeClass = String.class), @DynamicParameter(name = "classDefinitionUID", value = "对象的类型定义", example = "", required = true, dataTypeClass = String.class)}))
    public String deleteObjects(@RequestBody JSONObject jsonObject) {
        Result<Boolean> result = new Result<>();
        try {
            String lstrOBIDs = jsonObject.getString("obids");
            String lstrClassDefinitionUID = jsonObject.getString("classDefinitionUID");
            boolean flag = this.schemaBusinessService.deleteObjects(lstrOBIDs, lstrClassDefinitionUID);
            if (flag)
                result.success("delete succeeded");
            else
                result.error500("failed to delete object");
        } catch (Exception exception) {
            log.error("批量删除同类对象异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("批量删除同类对象异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("展开Schema对象")
    @PostMapping(value = "/expandSchemaObj")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "ClassDefinitionUID", value = "对象类型定义", example = "", required = true, dataTypeClass = String.class), @DynamicParameter(name = "OBID", value = "对象obid", example = "不传递则表示查询所有", required = false, dataTypeClass = String.class), @DynamicParameter(name = "DomainUID", value = "对象的domain", example = "", required = true, dataTypeClass = Integer.class), @DynamicParameter(name = "UID", value = "对象的uid", example = "", required = true, dataTypeClass = Integer.class)}))
    public String expandSchemaObj(@RequestBody JSONObject jsonObject) {
        ResultVo<List<ObjectXmlDTO>> resultVo = new ResultVo<>();
        try {
            resultVo.successResult(this.schemaBusinessService.getObjRelatedObjsAndRels(jsonObject));
        } catch (Exception ex) {
            log.error("展开Schema对象异常!异常信息:{}.\n", ExceptionUtil.getMessage(ex), ExceptionUtil.getRootCause(ex));
            resultVo.errorResult("展开Schema对象异常!异常信息:" + ExceptionUtil.getSimpleMessage(ex));
        }
        return JSONObject.toJSONString(resultVo);
    }

    @ApiOperation("导出XML文件")
    @PostMapping(value = "/exportXmlFile")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONArray", properties = {
            @DynamicParameter(name = "ClassDefinitionUID", value = "对象类型定义", example = "", required = true, dataTypeClass = String.class), @DynamicParameter(name = "OBID", value = "对象obid", example = "不传递则表示查询所有", required = false, dataTypeClass = String.class), @DynamicParameter(name = "DomainUID", value = "对象的domain", example = "", required = true, dataTypeClass = Integer.class), @DynamicParameter(name = "UID", value = "对象的uid", example = "", required = true, dataTypeClass = Integer.class)}))
    public void exportXmlFile(HttpServletResponse response, @RequestBody JSONArray jsonArray) {
        try {
            this.schemaBusinessService.generateXmlFile(jsonArray, response);
        } catch (Exception ex) {
            log.error("导出XML文件异常!异常信息:{}.\n", ExceptionUtil.getMessage(ex), ExceptionUtil.getRootCause(ex));
        }
    }


    @ApiOperation("获取对象的Xml信息")
    @PostMapping(value = "/getObjXmlInfo")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "ClassDefinitionUID", value = "对象类型定义", example = "", required = true, dataTypeClass = String.class), @DynamicParameter(name = "OBID", value = "对象obid", example = "不传递则表示查询所有", required = false, dataTypeClass = String.class), @DynamicParameter(name = "DomainUID", value = "对象的domain", example = "", required = true, dataTypeClass = Integer.class), @DynamicParameter(name = "UID", value = "对象的uid", example = "", required = true, dataTypeClass = Integer.class)}))
    public String getObjsXmlInfo(@RequestBody JSONArray jsonArray) {
        ResultVo<String> result = new ResultVo<>();
        try {
            String lstrXmlInfo = this.schemaBusinessService.getObjectsXmlInfo(jsonArray);
            result.successResult(lstrXmlInfo);
        } catch (Exception ex) {
            log.error("获取对象的Xml信息异常!异常信息:{}.\n", ExceptionUtil.getMessage(ex), ExceptionUtil.getRootCause(ex));
            result.errorResult("获取对象的Xml信息异常!异常信息:" + ExceptionUtil.getSimpleMessage(ex));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取当前用户的默认设置")
    @PostMapping(value = "/getUserDefaultSettings")
    public String getUserDefaultSettings(@RequestBody JSONObject jsonObject) {
        Result<List<KeyValuePair>> result = new Result<>();
        String userName = jsonObject.getString("userName");
        try {
            List<KeyValuePair> userWithDefaultSettings = this.schemaBusinessService.getUserWithDefaultSettings(userName);
            if (userWithDefaultSettings == null) {
                result.error500("no user was found in current database");
            } else {
                result.setResult(userWithDefaultSettings);
                result.success("已成功获取当前用户的默认设置信息");
            }
        } catch (Exception e) {
            log.error("获取当前用户的默认设置异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("获取当前用户的默认设置异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("修改用户基础信息")
    @PostMapping(value = "/updateUserInfo")
    public String updateUserInfo(@RequestBody JSONObject jsonObject) {
        Result<String> result = new Result<>();
        String userName = jsonObject.getString("userName");
        try {
            this.schemaBusinessService.updateUserInfo(userName, jsonObject.getJSONArray("properties"));
        } catch (Exception exception) {
            log.error("修改用户基础信息异常!异常信息:{}.\n", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
            result.error500("修改用户基础信息异常!异常信息:" + ExceptionUtil.getSimpleMessage(exception));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("获取到全部的项目")
    @PostMapping(value = "/getConfigurationItems")
    public String getConfigurationItems(@RequestBody JSONObject jsonObject) {
        Result<ObjectDTOCollection> result = new Result<>();
        try {
            IObjectCollection configurationItems = CIMContext.Instance.getConfigurationItems();
            if (configurationItems != null && configurationItems.size() > 0) {
                result.setResult(configurationItems.toObjectDTOCollection());
            }
            result.success("已成功获取项目列表");
        } catch (Exception e) {
            log.error("获取到全部的项目异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("获取到全部的项目异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("自动创建系统表")
    @PostMapping(value = "/autoGenerateTables")
    public String autoGenerateTables(@RequestBody JSONObject jsonObject) {
        Result<Boolean> result = new Result<>();
        try {
            this.schemaBusinessService.ensureTables();
            result.success("已成功创建系统表");
        } catch (Exception e) {
            log.error("自动创建系统表异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("自动创建系统表异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("创建一个新的项目")
    @PostMapping(value = "/createConfigurationItem")
    public String createConfigurationItem(@RequestBody JSONObject jsonObject) {
        Result<ObjectDTO> result = new Result<>();
        try {
            IObject configurationItem = this.schemaBusinessService.createConfigurationItem(JSON.parseObject(jsonObject.toJSONString(), ObjectDTO.class));
            if (configurationItem != null) {
                result.setResult(configurationItem.toObjectDTO());
                result.success("已成功创建项目");
            } else
                result.error500("项目创建失败");
        } catch (Exception e) {
            log.error("创建一个新的项目异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("创建一个新的项目异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("更新指定的项目")
    @PostMapping(value = "/updateConfigurationItem")
    public String updateConfigurationItem(@RequestBody JSONObject jsonObject) {
        Result<ObjectDTO> result = new Result<>();
        try {
            IObject configurationItem = this.schemaBusinessService.updateConfigurationItem(JSON.parseObject(jsonObject.toJSONString(), ObjectDTO.class));
            if (configurationItem != null) {
                result.setResult(configurationItem.toObjectDTO());
                result.success("已成功更新项目");
            } else
                result.error500("项目更新失败");
        } catch (Exception e) {
            log.error("更新指定的项目异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("更新指定的项目异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("切换至指定的项目")
    @PostMapping(value = "/changeConfigurationItem")
    public String changeConfigurationItem(@RequestBody JSONObject jsonObject) {
        Result<Boolean> result = new Result<Boolean>();
        String obid = jsonObject.getString("obid");
        String userName = jsonObject.getString("userName");
        try {
            CIMContext.Instance.changeScope(userName, CIMContext.Instance.ProcessCache().getObjectByOBIDCache(obid));
            result.setResult(true);
            result.success("已成功切换至指定的项目");
        } catch (Exception e) {
            log.error("切换至指定的项目异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("切换至指定的项目异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("创建CIM用户")
    @PostMapping(value = "/createUser")
    public String createUser(@RequestBody JSONObject jsonObject) {
        Result<ObjectDTO> result = new Result<>();
        String loginUser = jsonObject.getString("name");
        try {
            ICIMUser user = this.schemaBusinessService.createUser(loginUser);
            if (user != null) {
                result.setResult(user.toObjectDTO());
                result.success("已成功创建用户: " + user.Name());
            }
        } catch (Exception e) {
            log.error("创建CIM用户异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("创建CIM用户异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("删除CIM用户")
    @PostMapping(value = "/dropUser")
    public String dropUser(@RequestBody JSONObject jsonObject) {
        Result<Boolean> result = new Result<>();
        String loginUser = jsonObject.getString("name");
        try {
            result.setResult(this.schemaBusinessService.dropUser(loginUser));
            result.success("已成功删除用户信息");
        } catch (Exception e) {
            log.error("删除CIM用户异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("删除CIM用户异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("清除用户的项目集")
    @PostMapping(value = "/cleanScope")
    public String cleanScope(@RequestBody JSONObject jsonObject) {
        Result<Boolean> result = new Result<>();
        String loginUser = jsonObject.getString("userName");
        try {
            this.schemaBusinessService.cleanScope(loginUser);
            result.success("当前用户的项目信息已清除");
        } catch (Exception e) {
            log.error("清除用户的项目集异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("清除用户的项目集异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("查询Schema对象")
    @PostMapping(value = "/getSchemaDefinitions")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "schemaType", value = "要搜索的schema类型", example = "ClassDef|RelDef|InterfaceDef|EnumListType|PropertyDef|EnumEnum", required = true, dataTypeClass = String.class), @DynamicParameter(name = "nameCondition", value = "schema定义的名称", example = "不传递则表示查询所有", required = false, dataTypeClass = String.class), @DynamicParameter(name = "pageSize", value = "分页大小", example = "", required = true, dataTypeClass = Integer.class), @DynamicParameter(name = "pageIndex", value = "当前页数", example = "", required = true, dataTypeClass = Integer.class), @DynamicParameter(name = "classDefinitionUID", value = "classDefinitionUID", example = "", required = false, dataTypeClass = String.class)}))
    public String getClassDefs(@RequestBody JSONObject jsonObject) {
        Result<List<ObjectDTO>> result = new Result<>();
        try {
            String lstrSchemaType = jsonObject.getString("schemaType");
            String lstrNameCriteria = jsonObject.getString("nameCondition");
            Integer pageSize = jsonObject.getInteger("pageSize");
            Integer pageIndex = jsonObject.getInteger("pageIndex");
            String lstrClassDefinitionUID = jsonObject.getString("classDefinitionUID");
            IObjectCollection schemaDefs = this.schemaBusinessService.getSchemaObjects(lstrSchemaType, lstrNameCriteria, lstrClassDefinitionUID, new PageRequest(pageIndex, pageSize));
            if (SchemaUtility.hasValue(schemaDefs)) {
                List<ObjectDTO> resultList = new ArrayList<>();
                Iterator<IObject> iObjectIterator = schemaDefs.GetEnumerator();
                while (iObjectIterator.hasNext()) {
                    IObject next = iObjectIterator.next();
                    resultList.add(next.toObjectDTO());
                }
                result.setResult(resultList);
                result.setTotal(schemaDefs.PageResult().getTotal());
                result.success("retrieved class definition(s) quantity:" + resultList.size());
            }
        } catch (Exception e) {
            log.error("查询Schema对象异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.error500("查询Schema对象异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("schema线程池状态获取")
    @PostMapping(value = "/getThreadPoolStatus")
    public String getThreadPoolStatus(@RequestBody JSONObject jsonObject) {
        ResultVo<Map<String, String>> result = new ResultVo<>();
        try {
            result.successResult(ThreadsProcessor.getThreadPoolStatus());
        } catch (Exception e) {
            log.error("schema线程池状态获取异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("schema线程池状态获取异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation("反推按照属性构造结构树")
    @PostMapping(value = "/getPropertySummary")
    public String getPropertySummary(@RequestBody JSONObject jsonObject) {
        ResultVo<List<PropertyHierarchyVo>> resultVo = new ResultVo<>();
        List<String> classDefinitionUids = jsonObject.getJSONArray("classDefinitionUids").stream().map(Object::toString).collect(Collectors.toList());
        List<String> propertyDefinitionUids = jsonObject.getJSONArray("propertyDefinitionUids").stream().map(Object::toString).collect(Collectors.toList());
        String[] arrayProperties = new String[propertyDefinitionUids.size()];
        propertyDefinitionUids.toArray(arrayProperties);
        try {
            List<PropertyHierarchyVo> propertyHierarchyVos = this.schemaBusinessService.summaryProperty(classDefinitionUids, arrayProperties);
            resultVo.setResult(propertyHierarchyVos);
        } catch (Exception e) {
            log.error("反推按照属性构造结构树异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            resultVo.errorResult("反推按照属性构造结构树异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(resultVo);
    }

    @ApiOperation("反推属性值")
    @PostMapping(value = "/getPropertySummaryWithFilter")
    public String getPropertySummaryWithFilter(@RequestBody JSONObject jsonObject) {
        ResultVo<List<PropertyHierarchyVo>> resultVo = new ResultVo<>();
        String classDefinitionUid = jsonObject.getString("classDefinitionUid");
        String propertyDefinitionUid = jsonObject.getString("propertyDefinitionUid");
        String filter = jsonObject.getString("filter");

        List<String> classDefinitionUids = new ArrayList<>();
        classDefinitionUids.add(classDefinitionUid);
        List<String> propertyDefinitionUids = new ArrayList<>();
        propertyDefinitionUids.add(propertyDefinitionUid);
        String[] arrayProperties = new String[propertyDefinitionUids.size()];
        propertyDefinitionUids.toArray(arrayProperties);
        try {
            List<PropertyHierarchyVo> result = new ArrayList<>();
            List<PropertyHierarchyVo> propertyHierarchyVos = this.schemaBusinessService.summaryProperty(classDefinitionUids, arrayProperties);
            if (StringUtils.isEmpty(filter)) {
                result.addAll(propertyHierarchyVos);
            } else {
                propertyHierarchyVos.forEach(p -> {
                    String displayName = p.getDisplayName();
                    if (displayName.contains(filter)) {
                        result.add(p);
                    }
                });
            }
            resultVo.setResult(result);
        } catch (Exception e) {
            log.error("反推属性值异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            resultVo.errorResult("反推属性值异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(resultVo);
    }

    @ApiOperation(value = "根据一端和二端UID删除关联关系")
    @PostMapping(value = "/deleteRelByUid")
    public String deleteRelByUid(@RequestBody JSONObject jsonObject) {
        ResultVo<Boolean> resultVo = new ResultVo<>();
        String relDef = jsonObject.getString("relDef");
        String uid1 = jsonObject.getString("uid1");
        String uid2 = jsonObject.getString("uid2");
        try {
            resultVo.setResult(this.schemaBusinessService.deleteRelByUid(relDef, uid1, uid2));
        } catch (Exception e) {
            log.error("根据一端和二端UID删除关联关系异常!异常信息:{}.\n", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            resultVo.errorResult("根据一端和二端UID删除关联关系异常!异常信息:" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(resultVo);
    }

    @ApiOperation(value = "通用根据Excel模板导入标准数据")
    @PostMapping("/importDataByExcelTemplate")
    public String importDataByExcelTemplate(@ApiParam(name = "file", value = "file", required = true)
                                            @RequestParam MultipartFile file,
                                            @ApiParam(name = "withSchemaUidRule", value = "withSchemaUidRule", required = false)
                                            @RequestParam(name = "withSchemaUidRule", value = "withSchemaUidRule", required = false) Boolean withSchemaUidRule) {
        ResultVo<Object> result = new ResultVo<>();
        try {
            this.schemaBusinessService.importDataByExcelTemplate(file, withSchemaUidRule);
            result.successResult(true);
        } catch (Exception e) {
            log.error("通用根据Excel模板导入标准数据失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("通用根据Excel模板导入标准数据失败!" + ExceptionUtil.getSimpleMessage(e));
        }
        return CommonUtility.toJsonString(result);
    }

    @ApiOperation(value = "根据通用查询条件导出数据", httpMethod = "POST", notes = "根据通用查询条件导出数据", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PostMapping("/exportExcelByForm")
    public String exportExcelByForm(@RequestBody JSONObject jsonObject, HttpServletResponse response) {
        ResultVo<Object> result = new ResultVo<>();
        result.successResult(true);
        try {
            this.schemaBusinessService.exportExcelByForm(jsonObject, response);
            response.getOutputStream().close();
        } catch (Exception e) {
            log.error("根据通用查询条件导出数据失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            result.errorResult("根据通用查询条件导出数据失败!" + ExceptionUtil.getMessage(e));
        }
        return JSON.toJSONString(result);
    }
}
