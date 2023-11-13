package ccm.server.business.impl;

import ccm.server.business.ICCMPTPackageMaterialSpecificationService;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.enums.*;
import ccm.server.excel.entity.ExcelDataContent;
import ccm.server.excel.util.ExcelUtility;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.models.query.QueryRequest;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IProperty;
import ccm.server.util.CommonUtility;
import ccm.server.utils.PTPMSUtil;
import ccm.server.utils.SchemaUtility;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service("ccmptPackageMaterialSpecificationService")
public class CCMPTPackageMaterialSpecificationServiceImpl implements ICCMPTPackageMaterialSpecificationService {
    @Override
    public ObjectDTO createOrUpdatePTPMaterialSpecification(String pstrPTPMS, String pstrPTPMSItems) throws Exception {
        IObject lobjPTPMS = this.createOrUpdatePTPMaterialSpecificationWithIObjectStyle(pstrPTPMS, pstrPTPMSItems);
        return lobjPTPMS != null ? lobjPTPMS.toObjectDTO() : null;
    }


    @Override
    public synchronized IObject createOrUpdatePTPMaterialSpecificationWithIObjectStyle(String pstrPTPMS, String pstrPTPMSItem) throws Exception {
        if (!StringUtils.isEmpty(pstrPTPMS) && !StringUtils.isEmpty(pstrPTPMSItem)) {
            JSONObject lobjPTPMSJSON = JSONObject.parseObject(pstrPTPMS);
            JSONObject lobjPTPMSItem = JSONObject.parseObject(pstrPTPMSItem);
            String lstrPTPMSName = lobjPTPMSItem.toJSONString();
            String lstrPTPMSOBID = lobjPTPMSJSON.getString(propertyDefinitionType.OBID.toString());
            IObject lobjSameNameObj = SchemaUtility.getObjectByClassDefinitionAndName(lstrPTPMSName, classDefinitionType.CCMPTPackageMaterialSpecification.toString());
            if (lobjSameNameObj != null) {
                if (!lobjSameNameObj.OBID().equalsIgnoreCase(lstrPTPMSOBID)) {
                    throw new Exception("已经存在相同的规格,不能重复创建!" + lstrPTPMSName);
                } else {
                    return lobjSameNameObj;
                }
            }
            SchemaUtility.beginTransaction();
            IObject lobjPTPMS;
            if (!StringUtils.isEmpty(lstrPTPMSOBID)) {
                //更新
                lobjPTPMS = CIMContext.Instance.ProcessCache().getObjectByOBID(lstrPTPMSOBID, classDefinitionType.CCMPTPackageMaterialSpecification.toString());
                if (lobjPTPMS == null) throw new Exception("未找到OBID:" + lstrPTPMSOBID + "的试压包材料规格对象!");
                lobjPTPMS.BeginUpdate();
                lobjPTPMS.setName(lstrPTPMSName);
                lobjPTPMS.setDescription(PTPMSUtil.generatePTPMaterialSpecificationDescription(lstrPTPMSName));
                ICCMPTPackageMaterialSpecification packageMaterialSpecification = lobjPTPMS.toInterface(ICCMPTPackageMaterialSpecification.class);
                packageMaterialSpecification.setCCMPTPMSCategory(lobjPTPMSJSON.getString(propertyDefinitionType.CCMPTPMSCategory.toString()));
                packageMaterialSpecification.updateRelatedMaterialTemplateMSInfo(false);
                lobjPTPMS.FinishUpdate();
            } else {
                //创建
                lobjPTPMS = this.createPTPMS(lstrPTPMSName, lobjPTPMSJSON.getString(propertyDefinitionType.CCMPTPMSCategory.toString()));
                if (lobjPTPMS == null) throw new Exception("创建试压包材料规格失败!");
            }
            SchemaUtility.commitTransaction();
            return lobjPTPMS;
        }
        return null;
    }

    private IObject createPTPMS(JSONObject jsonObject, IObjectCollection pcolExistPTPMS) throws Exception {
        String lstrName = SchemaUtility.getSpecialPropertyValue(jsonObject, propertyDefinitionType.Name.toString());
        String lstrUID = SchemaUtility.getSpecialPropertyValue(jsonObject, propertyDefinitionType.UID.toString());
        String lstrDesc = SchemaUtility.getSpecialPropertyValue(jsonObject, propertyDefinitionType.Description.toString());
        String lstrCategory = SchemaUtility.getSpecialPropertyValue(jsonObject, propertyDefinitionType.CCMPTPMSCategory.toString());
        if (SchemaUtility.hasValue(pcolExistPTPMS)) {
            IObject item = pcolExistPTPMS.item(lstrUID, domainInfo.PTPACKAGEMATERIAL.toString());
            if (item != null) {
                return item;
            }
        }
        IObject lobjPTPMS = SchemaUtility.newIObject(classDefinitionType.CCMPTPackageMaterialSpecification.toString(), lstrName, lstrDesc, null, lstrUID);
        if (lobjPTPMS == null) throw new Exception("创建试压包材料规格失败!");
        ICCMPTPackageMaterialSpecification materialSpecification = lobjPTPMS.toInterface(ICCMPTPackageMaterialSpecification.class);
        materialSpecification.setCCMPTPMSCategory(lstrCategory);
        lobjPTPMS.ClassDefinition().FinishCreate(lobjPTPMS);
        return lobjPTPMS;
    }

    private IObject createPTPMS(String pstrName, String pstrCategory) throws Exception {
        if (!StringUtils.isEmpty(pstrCategory) && !StringUtils.isEmpty(pstrName)) {
            IObject lobjPTPMS = SchemaUtility.newIObject(classDefinitionType.CCMPTPackageMaterialSpecification.toString(), pstrName, PTPMSUtil.generatePTPMaterialSpecificationDescription(pstrName), null, null);
            if (lobjPTPMS == null) throw new Exception("创建试压包材料规格失败!");
            ICCMPTPackageMaterialSpecification materialSpecification = lobjPTPMS.toInterface(ICCMPTPackageMaterialSpecification.class);
            materialSpecification.setCCMPTPMSCategory(pstrCategory);
            lobjPTPMS.ClassDefinition().FinishCreate(lobjPTPMS);
            return lobjPTPMS;
        }
        return null;
    }


    @Override
    public ObjectDTO createOrUpdatePTPMaterialTemplate(String pstrProperties, String pstrPTPMSOBID) throws Exception {
        IObject lobjPTPMaterial = this.createOrUpdatePTPMaterialTemplateWithIObjectStyle(pstrProperties, pstrPTPMSOBID);
        return lobjPTPMaterial != null ? lobjPTPMaterial.toObjectDTO() : null;
    }

    @Override
    public synchronized IObject createOrUpdatePTPMaterialTemplateWithIObjectStyle(String pstrProperties, String pstrPTPMSOBID) throws Exception {
        if (!StringUtils.isEmpty(pstrProperties) && !StringUtils.isEmpty(pstrPTPMSOBID)) {
            IObject lobjPTPMS = CIMContext.Instance.ProcessCache().getObjectByOBID(pstrPTPMSOBID, classDefinitionType.CCMPTPackageMaterialSpecification.toString());
            if (lobjPTPMS == null) throw new Exception("未找到试压包材料规格对象,OBID:" + pstrPTPMSOBID);
            List<ObjectItemDTO> lcolProps = CommonUtility.converterPropertiesToItemDTOList(pstrProperties);
            if (!CommonUtility.hasValue(lcolProps)) throw new Exception("未解析到对象的属性信息!");
            //先找出同名的材料对象
            IObject lobjSameNameObj = this.getPTPMaterialTemplateByProps(lcolProps);
            if (lobjSameNameObj != null) {//存在同名材料,不创建,判断与规则对象是否已经有关联,没有关键则建立关联
                if (!checkPTPMaterialTemplateHasRelatedPTPMaterialSpecialfication(lobjSameNameObj, pstrPTPMSOBID)) {
                    SchemaUtility.beginTransaction();
                    SchemaUtility.createRelationShip(relDefinitionType.CCMPTPackageMaterialSpecification2Material.toString(), lobjPTPMS, lobjSameNameObj, false);
                    SchemaUtility.commitTransaction();
                }
            } else {
                SchemaUtility.beginTransaction();
                String lstrOBID = CommonUtility.getSpecialValueFromProperties(lcolProps, propertyDefinitionType.OBID.toString());
                String lstrClassDefUID = CommonUtility.getSpecialValueFromProperties(lcolProps, propertyDefinitionType.ClassDefinitionUID.toString());
                if (StringUtils.isEmpty(lstrClassDefUID)) throw new Exception("未解析到对象的对象定义信息!");
                IObject lobj;
                if (!StringUtils.isEmpty(lstrOBID)) {
                    lobj = CIMContext.Instance.ProcessCache().getObjectByOBID(lstrOBID, lstrClassDefUID);
                    if (lobj == null)
                        throw new Exception("未找到OBID:" + lstrOBID + ",classDef:" + lstrClassDefUID + "的对象信息!");
                    lobj.BeginUpdate();
                    lobj.fillingProperties(lcolProps, true);
                    lobj.FinishUpdate();
                } else {
                    lobj = SchemaUtility.newIObject(lstrClassDefUID, "", "", null, null);
                    if (lobj == null)
                        throw new Exception("创建对象失败,classDef:" + lstrClassDefUID);
                    lobj.fillingProperties(lcolProps, false);
                    lobj.ClassDefinition().FinishCreate(lobj);
                    SchemaUtility.createRelationShip(relDefinitionType.CCMPTPackageMaterialSpecification2Material.toString(), lobjPTPMS, lobj, false);
                    SchemaUtility.commitTransaction();
                }
            }
        }
        return null;
    }

    @Override
    public Boolean relatePTPMaterialsForPTPMS(String pstrPTPMaterialOBIDs, String pstrPTPMSOBID) throws Exception {
        if (!StringUtils.isEmpty(pstrPTPMSOBID)) {
            IObject lobjPTPMS = CIMContext.Instance.ProcessCache().getObjectByOBID(pstrPTPMSOBID, classDefinitionType.CCMPTPackageMaterialSpecification.toString());
            if (lobjPTPMS == null) throw new Exception("未找到试压包材料规格对象,OBID:" + pstrPTPMSOBID);
            List<String> lcolPTPMSOBIDs = !StringUtils.isEmpty(pstrPTPMSOBID) ? Arrays.asList(pstrPTPMaterialOBIDs.split(",")) : null;
            SchemaUtility.beginTransaction();
            //先删除本身与材料的所有关联
            IRelCollection relOfHasRelated = lobjPTPMS.GetEnd1Relationships().GetRels(relDefinitionType.CCMPTPackageMaterialSpecification2Material.toString(), false);
            List<String> lcolUnRelatedObjOBIDs = new ArrayList<>();
            if (SchemaUtility.hasValue(relOfHasRelated)) {
                if (CommonUtility.hasValue(lcolPTPMSOBIDs)) {
                    for (String obid : lcolPTPMSOBIDs) {
                        if (!relOfHasRelated.containsOBID2(obid)) {
                            lcolUnRelatedObjOBIDs.add(obid);
                        }
                    }
                }
            }
            if (CommonUtility.hasValue(lcolPTPMSOBIDs)) {
                if (SchemaUtility.hasValue(relOfHasRelated)) {
                    Iterator<IObject> e = relOfHasRelated.GetEnumerator();
                    while (e.hasNext()) {
                        IRel rel = e.next().toInterface(IRel.class);
                        if (!lcolPTPMSOBIDs.contains(rel.OBID2())) {
                            rel.Delete();
                        }
                    }
                }
            }
            if (CommonUtility.hasValue(lcolUnRelatedObjOBIDs)) {
                IObjectCollection lcolPTPMaterials = CIMContext.Instance.ProcessCache().queryObjectsByOBIDAndClassDefinition(lcolUnRelatedObjOBIDs, classDefinitionType.CCMPTPackageMaterialTemplate.toString());
                if (SchemaUtility.hasValue(lcolPTPMaterials)) {
                    Iterator<IObject> e = lcolPTPMaterials.GetEnumerator();
                    while (e.hasNext()) {
                        IObject lobjPTPMaterial = e.next();
                        SchemaUtility.createRelationShip(relDefinitionType.CCMPTPackageMaterialSpecification2Material.toString(), lobjPTPMS, lobjPTPMaterial, false);
                    }
                }
            }
            SchemaUtility.commitTransaction();
        }
        return true;
    }

    @Override
    public synchronized void importPTPMSFormExcel(@NotNull MultipartFile file) throws Exception {
        JSONObject jsonObject = PTPMSUtil.converterExcelFileToJSONObject(file);
        PTPMSUtil.validatePTPMSTemplateInfo(jsonObject);
        List<JSONObject> lcolObjects = CommonUtility.toJSONObjList(jsonObject.getJSONArray(CommonUtility.JSON_FORMAT_ITEMS));
        //先创建材料规格
        Map<String, List<JSONObject>> groupByClassDef = lcolObjects.stream().collect(Collectors.groupingBy(r -> r.getString(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID)));
        List<JSONObject> lcolPTPMSJSONObjects = groupByClassDef.get(classDefinitionType.CCMPTPackageMaterialSpecification.toString());
        if (!CommonUtility.hasValue(lcolPTPMSJSONObjects)) {
            throw new Exception("未解析到试压包材料规格对象信息!");
        }
        List<JSONObject> lcolRels = groupByClassDef.get(classDefinitionType.Rel.toString());
        List<JSONObject> lcolMaterialTemplateJSONObjs = groupByClassDef.get(classDefinitionType.CCMPTPackageMaterialTemplate.toString());
        //根据关联解析数据结构
        Map<JSONObject, List<JSONObject>> ldicPTPMSAndMaterialTemplates = new HashMap<>();
        for (JSONObject ptpMS : lcolPTPMSJSONObjects) {
            ldicPTPMSAndMaterialTemplates.put(ptpMS, getOtherEndObjs(ptpMS, lcolRels, lcolMaterialTemplateJSONObjs));
        }
        //根据UID 查询已经存在的试压包材料规格
        IObjectCollection lcolExistPTPMSObjects = SchemaUtility.getObjectsByClassDef(classDefinitionType.CCMPTPackageMaterialSpecification.toString());
        SchemaUtility.beginTransaction();
        this.createOrUpdatePTPMaterialSpecification(ldicPTPMSAndMaterialTemplates, lcolExistPTPMSObjects);
        SchemaUtility.commitTransaction();
    }

    private List<JSONObject> getOtherEndObjs(JSONObject end1, List<JSONObject> rels, List<JSONObject> pcolOtherEndObjs) {
        if (CommonUtility.hasValue(rels) && CommonUtility.hasValue(pcolOtherEndObjs)) {
            List<JSONObject> lcolHitRels = rels.stream().filter(r -> SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.UID1.toString()).equalsIgnoreCase(SchemaUtility.getSpecialPropertyValue(end1, propertyDefinitionType.UID.toString()))).collect(Collectors.toList());
            if (CommonUtility.hasValue(lcolHitRels)) {
                List<String> lcolUID2s = lcolHitRels.stream().map(r -> SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.UID2.toString())).collect(Collectors.toList());
                return pcolOtherEndObjs.stream().filter(r -> lcolUID2s.contains(SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.UID.toString()))).collect(Collectors.toList());
            }
        }
        return null;
    }

    private void createOrUpdatePTPMaterialSpecification(@NotNull Map<JSONObject, List<JSONObject>> pcolPTPMSJSONObjs, IObjectCollection pcolExistPTPMS) throws Exception {
        for (Map.Entry<JSONObject, List<JSONObject>> entry : pcolPTPMSJSONObjs.entrySet()) {
            IObject lobjPTPMS = this.createPTPMS(entry.getKey(), pcolExistPTPMS);
            List<JSONObject> lcolMaterialTemplates = entry.getValue();
            this.createOrUpdatePTPMaterialTemplate(lcolMaterialTemplates, lobjPTPMS);
        }

    }

    private void createOrUpdatePTPMaterialTemplate(List<JSONObject> pcolMaterialTemplateJSONObjs, @NotNull IObject pobjPTPMS) throws Exception {
        if (CommonUtility.hasValue(pcolMaterialTemplateJSONObjs)) {
            ICCMPTPackageMaterialSpecification packageMaterialSpecification = pobjPTPMS.toInterface(ICCMPTPackageMaterialSpecification.class);
            IObjectCollection ptpMaterialTemplates = packageMaterialSpecification.getPTPMaterialTemplates();
            for (JSONObject materialTemplateJSON : pcolMaterialTemplateJSONObjs) {
                String lstrName = SchemaUtility.getSpecialPropertyValue(materialTemplateJSON, propertyDefinitionType.Name.toString());
                IObject lobjExistObj = this.getPTPMaterialTemplateByProps(materialTemplateJSON);
                if (lobjExistObj != null) {
                    if (!SchemaUtility.hasValue(ptpMaterialTemplates) || (SchemaUtility.hasValue(ptpMaterialTemplates) && !ptpMaterialTemplates.containsByOBID(lobjExistObj.OBID()))) {
                        SchemaUtility.createRelationShip(relDefinitionType.CCMPTPackageMaterialSpecification2Material.toString(), packageMaterialSpecification, lobjExistObj, false);
                    }
                } else {
                    IObject newObject = SchemaUtility.newIObject(classDefinitionType.CCMPTPackageMaterialTemplate.toString(), lstrName, "", null, null);
                    if (newObject == null) throw new Exception("创建试压包材料模板失败!" + lstrName);
                    newObject.fillingInterfaces(materialTemplateJSON);
                    newObject.fillingProperties(materialTemplateJSON);
                    newObject.ClassDefinition().FinishCreate(newObject);
                    SchemaUtility.createRelationShip(relDefinitionType.CCMPTPackageMaterialSpecification2Material.toString(), packageMaterialSpecification, newObject, false);
                }
            }
        }
    }

    private IObject getPTPMaterialTemplateByProps(JSONObject materialTemplateJSON) {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionType.CCMPTPackageMaterialTemplate.toString());
        for (Map.Entry<String, Object> entry : materialTemplateJSON.getJSONObject(CommonUtility.JSON_FORMAT_PROPERTIES).entrySet()) {
            String lstrPropDef = entry.getKey();
            if (propertyDefinitionType.Name.toString().equalsIgnoreCase(lstrPropDef) || propertyDefinitionType.UID.toString().equalsIgnoreCase(lstrPropDef))
                continue;
            String lstrValue = entry.getValue() != null ? entry.getValue().toString() : "";
            CIMContext.Instance.QueryEngine().addPropertyForQuery(queryRequest, interfaceDefinitionType.ICCMPTPackageMaterialTemplate.toString(), lstrPropDef, operator.equal, lstrValue);
        }
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    private IObject getPTPMaterialTemplateByProps(@NotNull List<ObjectItemDTO> pcolProps) {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionType.CCMPTPackageMaterialTemplate.toString());
        for (ObjectItemDTO prop : pcolProps) {
            String lstrPropDef = prop.getDefUID();
            if (propertyDefinitionType.Name.toString().equalsIgnoreCase(lstrPropDef) || propertyDefinitionType.UID.toString().equalsIgnoreCase(lstrPropDef) || propertyDefinitionType.ClassDefinitionUID.toString().equalsIgnoreCase(lstrPropDef) || propertyDefinitionType.OBID.toString().equalsIgnoreCase(lstrPropDef))
                continue;
            String lstrValue = prop.getDisplayValue() != null ? prop.getDisplayValue().toString() : "";
            CIMContext.Instance.QueryEngine().addPropertyForQuery(queryRequest, interfaceDefinitionType.ICCMPTPackageMaterialTemplate.toString(), lstrPropDef, operator.equal, lstrValue);
        }
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    @Override
    public IObjectCollection getPTPMaterialsTemplatesForDesignObjs(String pstrOBIDs, String pstrClassDefUID) throws Exception {
        if (!StringUtils.isEmpty(pstrClassDefUID) && !StringUtils.isEmpty(pstrOBIDs)) {
            IObjectCollection lcolDesignObjs = CIMContext.Instance.ProcessCache().queryObjectsByOBIDAndClassDefinition(Arrays.asList(pstrOBIDs.split(",")), pstrClassDefUID);
            if (SchemaUtility.hasValue(lcolDesignObjs)) {
                return getPTPMaterialSpecificationsByDesignObjs(lcolDesignObjs);
            }
        }
        return null;
    }

    private IObjectCollection getPTPMaterialSpecificationsByDesignObjs(IObjectCollection pcolDesignObjs) throws Exception {
        //根据规格获取规格条目信息的计算属性定义
        List<String> lcolPTPMSItemCalculatePropDefs = this.getPTPMaterialSpecificationCalculateProps();
        //根据获取的过滤属性定义,生成由设计属性生成的过滤条件
        List<String> lcolFilterPropValues = this.generateFilterPropValues(lcolPTPMSItemCalculatePropDefs, pcolDesignObjs);
        //判断符合条件的规格对象 过滤条件的值就是材料规格的名称
        IObjectCollection lcolAllPTPMS = SchemaUtility.getObjectsByClassDef(classDefinitionType.CCMPTPackageMaterialSpecification.toString());
        IObjectCollection lcolContainer = new ObjectCollection();
        if (SchemaUtility.hasValue(lcolAllPTPMS)) {
            Iterator<IObject> e = lcolAllPTPMS.GetEnumerator();
            while (e.hasNext()) {
                IObject lobjPTPMS = e.next();
                if (lcolFilterPropValues.contains(lobjPTPMS.Name())) {
                    lcolContainer.append(lobjPTPMS);
                }
            }
        }
        return lcolContainer;
    }

    private List<String> getPTPMaterialSpecificationCalculateProps() throws Exception {
        List<String> lcolResult = new ArrayList<>();
        IObject lobjELTProp = CIMContext.Instance.ProcessCache().item("ELT_PTPackageMaterialSpecialficationCategory", domainInfo.SCHEMA.toString());
        if (lobjELTProp == null)
            throw new Exception("未在系统找到UID:ELT_PTPackageMaterialSpecialficationCategory的EnumListType对象");
        IEnumListType enumListType = lobjELTProp.toInterface(IEnumListType.class);
        IObjectCollection entries = enumListType.getEntries();
        if (!SchemaUtility.hasValue(entries)) {
            throw new Exception("未找到有效的Enum信息!");
        }
        Iterator<IObject> e = entries.GetEnumerator();
        while (e.hasNext()) {
            IObject lobjEnum = e.next();
            lcolResult.addAll(Arrays.asList(lobjEnum.Name().split(",")));
        }
        return lcolResult.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public synchronized ObjectDTOCollection createPTPackageMaterials(String ptpackageOBID, String ptpMaterialTemplateOBIDs, String count) throws Exception {
        if (!StringUtils.isEmpty(ptpackageOBID) && !StringUtils.isEmpty(ptpMaterialTemplateOBIDs) && !StringUtils.isEmpty(count)) {
            //获取试压包对象
            IObject lobjPTPackage = CIMContext.Instance.ProcessCache().getObjectByOBID(ptpackageOBID, classDefinitionType.CCMPressureTestPackage.toString());
            if (lobjPTPackage == null) throw new Exception("未找到OBID:" + ptpackageOBID + "的试压包对象!");
            //获取试压包已经关联的材料
            IObjectCollection lcolHasRelatedMaterials = lobjPTPackage.GetEnd1Relationships().GetRels(relDefinitionType.CCMPTPackage2PTPMaterial.toString(), false).GetEnd2s();
            String[] larrPTPMTemplateOBIDs = ptpMaterialTemplateOBIDs.split(",");
            String[] counts = count.split(",");
            //根据OBID获取选择的材料模板
            IObjectCollection lcolPTPMaterialTemplate = CIMContext.Instance.ProcessCache().queryObjectsByOBIDAndClassDefinition(Arrays.asList(larrPTPMTemplateOBIDs), classDefinitionType.CCMPTPackageMaterialTemplate.toString());
            IObjectCollection container = new ObjectCollection();
            if (SchemaUtility.hasValue(lcolPTPMaterialTemplate)) {
                SchemaUtility.beginTransaction();
                int index = 0;
                for (String lstrPTPMaterialTemplateOBID : larrPTPMTemplateOBIDs) {
                    IObject lobjPTPMaterialTemplate = lcolPTPMaterialTemplate.itemByOBID(lstrPTPMaterialTemplateOBID);
                    //创建材料前,应该判断该试压包已经关联的材料是否与要创建的重复,相同的规格材料应当只有一份
                    //判断依据为材料对象的Name :规格名称  材料的内容:模板内容比对
                    if (lobjPTPMaterialTemplate == null)
                        throw new Exception("未找到OBID:" + lstrPTPMaterialTemplateOBID + "的材料模板对象");
                    ICCMPTPackageMaterialTemplate materialTemplate = lobjPTPMaterialTemplate.toInterface(ICCMPTPackageMaterialTemplate.class);
                    IObject lobjHasRelatedMaterial = this.getSameMaterialFromContainerByMaterialTemplate(lcolHasRelatedMaterials, materialTemplate);
                    if (lobjHasRelatedMaterial != null) {
                        lobjHasRelatedMaterial.BeginUpdate();
                        lobjHasRelatedMaterial.toInterface(ICCMPTPackageMaterial.class).setCCMPTPMaterialLength(Integer.parseInt(counts[index]));
                        lobjHasRelatedMaterial.FinishUpdate();
                    } else {
                        lobjHasRelatedMaterial = SchemaUtility.newIObject(classDefinitionType.CCMPTPackageMaterial.toString(), "", "", "", "");
                        if (lobjHasRelatedMaterial == null) throw new Exception("创建试压包材料对象失败!");
                        lobjHasRelatedMaterial.toInterface(ICCMPTPackageMaterialTemplate.class).copyTemplatePropValue(materialTemplate);
                        lobjHasRelatedMaterial.toInterface(ICCMPTPackageMaterial.class).setCCMPTPMaterialLength(Integer.parseInt(counts[index]));
                        lobjHasRelatedMaterial.ClassDefinition().FinishCreate(lobjHasRelatedMaterial);
                        SchemaUtility.createRelationShip(relDefinitionType.CCMPTPackage2PTPMaterial.toString(), lobjPTPackage, lobjHasRelatedMaterial, false);
                    }
                    container.append(lobjHasRelatedMaterial);
                    index++;
                }
                SchemaUtility.commitTransaction();
            }
            if (container.size() > 0) {
                return new ObjectDTOCollection(SchemaUtility.toObjectDTOList(container));
            }
        }
        return null;
    }

    @Override
    public Boolean deletePTPMaterials(String ptpMaterialTemplateOBIDs) throws Exception {
        if (!StringUtils.isEmpty(ptpMaterialTemplateOBIDs)) {
            String[] larrMaterialTemplates = ptpMaterialTemplateOBIDs.split(",");
            IObjectCollection lcolPTPMaterials = CIMContext.Instance.ProcessCache().queryObjectsByOBIDAndClassDefinition(Arrays.asList(larrMaterialTemplates), classDefinitionType.CCMPTPackageMaterial.toString());
            if (SchemaUtility.hasValue(lcolPTPMaterials)) {
                SchemaUtility.beginTransaction();
                lcolPTPMaterials.Delete();
                SchemaUtility.commitTransaction();
            }
        }
        return true;
    }

    @Override
    public ObjectDTOCollection getPTPMaterialTemplatesByPTPackage(String pstrPTPackageOBID, PageRequest pageRequest, FiltersParam filtersParam, OrderByParam orderByParam) throws Exception {
        if (!StringUtils.isEmpty(pstrPTPackageOBID)) {
            IObject lobjPTPackage = CIMContext.Instance.ProcessCache().getObjectByOBID(pstrPTPackageOBID, classDefinitionType.CCMPressureTestPackage.toString());
            if (lobjPTPackage == null)
                throw new Exception("未找到试压包对象,OBID:" + pstrPTPackageOBID);
            ICCMPressureTestPackage pressureTestPackage = lobjPTPackage.toInterface(ICCMPressureTestPackage.class);
            IObjectCollection lcolRelatedMaterials = pressureTestPackage.getMaterials();
            IObjectCollection lcolDesignObjects = pressureTestPackage.getRelatedDesignObjects();
            IObjectCollection lcolPTPMS = this.getPTPMaterialSpecificationsByDesignObjs(lcolDesignObjects);
            return getMaterialTemplateByPTPMS(pageRequest, filtersParam, orderByParam, lcolPTPMS, lcolRelatedMaterials);
        }
        return null;
    }

    @Override
    public void generatePTPMSTemplateData(HttpServletResponse response, String pstrPTPMSOBID) throws Exception {
        List<ExcelDataContent> ptpMSTemplateInfo = PTPMSUtil.generateROPTemplateDefaultSheetContent();
        IObjectCollection ptpMS;
        if (!StringUtils.isEmpty(pstrPTPMSOBID)) {
            IObject lobjPTPMS = CIMContext.Instance.ProcessCache().getObjectByOBID(pstrPTPMSOBID, classDefinitionType.CCMPTPackageMaterialSpecification.toString());
            if (lobjPTPMS == null) throw new Exception("未找到试压包材料规格对象,OBID:" + pstrPTPMSOBID + "!");
            ptpMS = new ObjectCollection();
            ptpMS.append(lobjPTPMS);
        } else {
            ptpMS = SchemaUtility.getObjectsByClassDef(classDefinitionType.CCMPTPackageMaterialSpecification.toString());
        }
        ExcelDataContent ptpMSContent = PTPMSUtil.generatePTPMSContent(ptpMS);
        ptpMSTemplateInfo.add(ptpMSContent);
        ExcelDataContent ptpMaterialTemplate = PTPMSUtil.generatePTPMaterialTemplateContentHeader();
        PTPMSUtil.setPTPMaterialTemplateContent(ptpMaterialTemplate, ptpMS);
        ptpMSTemplateInfo.add(ptpMaterialTemplate);
        XSSFWorkbook workBook = ExcelUtility.getWorkBook(ptpMSTemplateInfo);
        ExcelUtility.writeFileIntoHttpResponse(response, "PTPMSTemplateInformation.xlsx", workBook);
    }

    @Nullable
    private ObjectDTOCollection getMaterialTemplateByPTPMS(PageRequest pageRequest, FiltersParam filtersParam, OrderByParam orderByParam, IObjectCollection pcolMS, IObjectCollection pcolPackageRelatedMaterials) throws Exception {
        if (SchemaUtility.hasValue(pcolMS)) {
            List<ObjectDTO> lcolItems = new ArrayList<>();
            Iterator<IObject> e = pcolMS.GetEnumerator();
            while (e.hasNext()) {
                ICCMPTPackageMaterialSpecification materialSpecification = e.next().toInterface(ICCMPTPackageMaterialSpecification.class);
                IObjectCollection ptpMaterialTemplates = materialSpecification.getPTPMaterialTemplates();
                if (SchemaUtility.hasValue(ptpMaterialTemplates)) {
                    Iterator<IObject> e1 = ptpMaterialTemplates.GetEnumerator();
                    while (e1.hasNext()) {
                        IObject lobjPTPMSTemplate = e1.next();
                        ObjectDTO r = lobjPTPMSTemplate.toObjectDTO();
                        IObject sameMaterial = this.getSameMaterialFromContainerByMaterialTemplate(pcolPackageRelatedMaterials, lobjPTPMSTemplate.toInterface(ICCMPTPackageMaterialTemplate.class));
                        if (sameMaterial != null) {
                            ICCMPTPackageMaterial packageMaterial = sameMaterial.toInterface(ICCMPTPackageMaterial.class);
                            r.addItemIfNotExist("count", packageMaterial.CCMPTPMaterialLength());
                        }
                        lcolItems.add(r);
                    }
                }
            }
            ObjectDTOCollection lcolResult = new ObjectDTOCollection(lcolItems);
            if (filtersParam != null) {
                lcolResult.initFilterParam(filtersParam);
            }
            if (orderByParam != null) {
                lcolResult.initOrderByParam(orderByParam);
            }
            if (pageRequest != null) {
                lcolResult.initPageInfo(pageRequest.getPageIndex(), pageRequest.getPageSize());
            }
            lcolResult.adjust();
            return lcolResult;
        }
        return null;
    }

    /**
     * create by: Chen Jing
     * description: 获取相同的材料对象
     * create time: 2022/5/5 13:37
     *
     * @return a
     * @Param: pstrMaterialName 材料名称,与 规则名称一致
     */
    private IObject getSameMaterialFromContainerByMaterialTemplate(IObjectCollection pcolContainer, ICCMPTPackageMaterialTemplate materialTemplate) {
        if (SchemaUtility.hasValue(pcolContainer) && materialTemplate != null) {
            Iterator<IObject> e = pcolContainer.GetEnumerator();
            while (e.hasNext()) {
                IObject next = e.next();
                ICCMPTPackageMaterialTemplate template = next.toInterface(ICCMPTPackageMaterialTemplate.class);
                if (template.sameAsOtherTemplate(materialTemplate)) {
                    return next;
                }
            }
        }
        return null;
    }

    @Override
    public ObjectDTOCollection getPTPMaterialsTemplatesForDesignObjsWithDTOStyle(String pstrOBIDs, String pstrClassDefUID, PageRequest pageRequest, FiltersParam filtersParam, OrderByParam orderByParam) throws Exception {
        IObjectCollection lcolPTPMS = this.getPTPMaterialsTemplatesForDesignObjs(pstrOBIDs, pstrClassDefUID);
        return getMaterialTemplateByPTPMS(pageRequest, filtersParam, orderByParam, lcolPTPMS, lcolPTPMS);
    }

    private List<String> generateFilterPropValues(@NotNull List<String> pcolPTPMSItemCalculatePropDefs, IObjectCollection pcolDesignObjs) {
        List<String> lcolResult = new ArrayList<>();
        Iterator<IObject> e = pcolDesignObjs.GetEnumerator();
        while (e.hasNext()) {
            IObject lobjDesignObj = e.next();
            String lstrFilter = this.generateFilterForDesign(lobjDesignObj, pcolPTPMSItemCalculatePropDefs);
            lcolResult.add(lstrFilter);
        }
        return lcolResult.stream().distinct().collect(Collectors.toList());
    }

    private String generateFilterForDesign(@NotNull IObject pobjDesignObj, @NotNull List<String> pcolPTPMSItemCalculatePropDefs) {
        JSONObject jsonObject = new JSONObject();
        for (String pstrPropDef : pcolPTPMSItemCalculatePropDefs) {
            IProperty property = pobjDesignObj.getProperty(pstrPropDef);
            jsonObject.put(pstrPropDef, property != null && property.Value() != null ? property.Value().toString() : "");
        }
        return jsonObject.toJSONString();
    }

    private boolean checkPTPMaterialTemplateHasRelatedPTPMaterialSpecialfication(IObject pobjMaterialTemplate, String pstrPTPMSOBID) throws Exception {
        if (!StringUtils.isEmpty(pstrPTPMSOBID) && pobjMaterialTemplate != null) {
            IObjectCollection lcolPTPMS = pobjMaterialTemplate.GetEnd2Relationships().GetRels(relDefinitionType.CCMPTPackageMaterialSpecification2Material.toString()).GetEnd1s();
            return SchemaUtility.hasValue(lcolPTPMS) && lcolPTPMS.containsByOBID(pstrPTPMSOBID);
        }
        return false;
    }


}
