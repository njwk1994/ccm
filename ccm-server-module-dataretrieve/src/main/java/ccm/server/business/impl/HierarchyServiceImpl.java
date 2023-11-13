package ccm.server.business.impl;

import ccm.server.business.IHierarchyService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.domainInfo;
import ccm.server.enums.formPurpose;
import ccm.server.enums.operator;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.helper.HardCodeHelper;
import ccm.server.model.PropertyHierarchyVo;
import ccm.server.models.query.QueryRequest;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.ICCMHierarchyConfiguration;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.model.IInterface;
import ccm.server.utils.*;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/21 18:26
 */
@Service
public class HierarchyServiceImpl implements IHierarchyService {

    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    /**
     * 参数对象 obj
     */
    private static final String OBJ = "obj";

    /**
     * 参数对象 objItems
     */
    private static final String OBJ_ITEMS = "objItems";

    /**
     * 参数对象 items
     */
    private static final String ITEMS = "items";

    /* ******************************************************* 树配置 方法 Start ******************************************************* */

    /**
     * 根据表单生成属性集合
     * <p>用于给配置项设置TargetProperty</p>
     *
     * @param classDef 需要获取属性的ClassDef
     * @return
     * @throws Exception
     */
    @Override
    public List<ObjectDTO> getObjectFormPropertiesForConfigurationItem(String classDef) throws Exception {
        String fp = formPurpose.Info.toString();
        ICIMForm form = schemaBusinessService.getForm(fp, classDef);
        List<ObjectDTO> formProperties = new ArrayList<ObjectDTO>();
        if (form != null) {
            ObjectDTO formDTO = form.generatePopup(fp);
            if (formDTO != null) {
                List<ObjectItemDTO> items = formDTO.getItems();
                List<String> strings = Arrays.asList(HardCodeHelper.OBJ_QUERY_INDICATORS);
                for (ObjectItemDTO item : items) {
                    if (!strings.contains(item.getDefUID())) {
                        IObject property = CIMContext.Instance.ProcessCache().item(item.getDefUID(), domainInfo.SCHEMA.toString(), false);
                        if (null == property) {
                            continue;
                        }
                        formProperties.add(property.toObjectDTO());
                    }
                }
            }
        }
        return formProperties;
    }

    /**
     * 获取对应对象的树配置及配置项的表单
     *
     * @param formPurpose
     * @param targetClassDef
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> getObjectHierarchyConfigurationFormWithItem(String formPurpose, String targetClassDef) throws Exception {
        ObjectDTO objectHierarchyConfigurationForm = getObjectHierarchyConfigurationForm(formPurpose, targetClassDef);
        if (objectHierarchyConfigurationForm == null) {
            throw new RuntimeException("get " + targetClassDef + "'s " + HierarchyUtils.CCM_HIERARCHY_CONFIGURATION + " form error");
        }
        ObjectDTO hierarchyConfigurationItemForm = getHierarchyConfigurationItemForm(formPurpose);
        if (hierarchyConfigurationItemForm == null) {
            throw new RuntimeException("get " + HierarchyUtils.CCM_HIERARCHY_CONFIGURATION_ITEM + " form error");
        }
        // 配置参数
        Map<String, List<ObjectItemDTO>> objectHierarchyConfigurationFormItems = new HashMap<>();
        objectHierarchyConfigurationFormItems.put(ITEMS, objectHierarchyConfigurationForm.getItems());
        // 配置项参数
        Map<String, List<ObjectItemDTO>> hierarchyConfigurationItemFormItem = new HashMap<>();
        hierarchyConfigurationItemFormItem.put(ITEMS, hierarchyConfigurationItemForm.getItems());
        List<Map<String, List<ObjectItemDTO>>> objItems = new ArrayList<>();
        objItems.add(hierarchyConfigurationItemFormItem);
        // 传入参数
        Map<String, Object> objectHierarchyConfigurationFormWithItem = new HashMap<>();
        objectHierarchyConfigurationFormWithItem.put(OBJ, objectHierarchyConfigurationFormItems);
        objectHierarchyConfigurationFormWithItem.put(OBJ_ITEMS, objItems);
        return objectHierarchyConfigurationFormWithItem;
    }

    /**
     * 获取对应对象的树表单
     *
     * @param formPurpose
     * @param targetClassDef 目标对象
     * @return
     * @throws Exception
     */
    @Override
    public ObjectDTO getObjectHierarchyConfigurationForm(String formPurpose, String targetClassDef) throws Exception {
        ICIMForm form = schemaBusinessService.getForm(formPurpose, HierarchyUtils.CCM_HIERARCHY_CONFIGURATION);
        if (form != null) {
            ObjectDTO formDTO = form.generatePopup(formPurpose);
            formDTO.toSetValue(BasicTargetObjUtils.PROPERTY_TARGET_CLASS_DEF, targetClassDef);
            formDTO.toSetValue(propertyDefinitionType.ClassDefinitionUID.toString(), HierarchyUtils.CCM_HIERARCHY_CONFIGURATION);
            formDTO.toSetValue(propertyDefinitionType.Name.toString(), "");
            formDTO.toSetValue(propertyDefinitionType.Description.toString(), "");
            return formDTO;
        }
        return null;
    }

    @Override
    public IObjectCollection getMyHierarchyConfigurations(HttpServletRequest request, String targetClassDef, PageRequest pageRequest) throws Exception {
        String loginUserName = JwtUtil.getUsername(TokenUtils.getTokenByRequest(request));
        IQueryEngine iQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = iQueryEngine.start();
        iQueryEngine.addClassDefForQuery(queryRequest, HierarchyUtils.CCM_HIERARCHY_CONFIGURATION);
        iQueryEngine.addPropertyForQuery(queryRequest, "", propertyDefinitionType.CreationUser.toString(), operator.equal, loginUserName);
        iQueryEngine.addPropertyForQuery(queryRequest, BasicTargetObjUtils.I_BASIC_TARGET_OBJ, BasicTargetObjUtils.PROPERTY_TARGET_CLASS_DEF, operator.equal, targetClassDef);
        PageUtility.verifyPageAddPageParam(pageRequest, queryRequest);
        IObjectCollection query = iQueryEngine.query(queryRequest);
        IObject defaultHierarchyConfiguration = getDefaultHierarchyConfiguration(targetClassDef);
        if (defaultHierarchyConfiguration != null) {
            IObject iObject = query.itemByOBID(defaultHierarchyConfiguration.OBID());
            if (iObject != null) {
                query.addRangeUniquely(defaultHierarchyConfiguration);
            }
        }
        return query;
    }

    @Override
    public IObject getHierarchyConfigurationByOBID(String obid) throws Exception {
        if (!org.springframework.util.StringUtils.isEmpty(obid)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, HierarchyUtils.I_HIERARCHY_CONFIGURATION);
            CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, obid);
            return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        }
        return null;
    }

    /* ******************************************************* 树配置 方法 End ******************************************************* */
    /* ******************************************************* 树配置项 方法 Start ******************************************************* */

    /**
     * 获取对应对象的树表单
     *
     * @param formPurpose
     * @return
     * @throws Exception
     */
    @Override
    public ObjectDTO getHierarchyConfigurationItemForm(String formPurpose) throws Exception {
        ICIMForm form = schemaBusinessService.getForm(formPurpose, HierarchyUtils.CCM_HIERARCHY_CONFIGURATION_ITEM);
        if (form != null) {
            ObjectDTO formDTO = form.generatePopup(formPurpose);
            formDTO.toSetValue(propertyDefinitionType.ClassDefinitionUID.toString(), HierarchyUtils.CCM_HIERARCHY_CONFIGURATION_ITEM);
            formDTO.toSetValue(propertyDefinitionType.Name.toString(), "");
            formDTO.toSetValue(propertyDefinitionType.Description.toString(), "");
            return formDTO;
        }
        return null;
    }


    @Override
    public IObjectCollection getItemsByHierarchyConfigurationOBID(String hierarchyConfigurationOBID) throws Exception {
        IObject hierarchyConfigurationByOBID = getHierarchyConfigurationByOBID(hierarchyConfigurationOBID);
        if (hierarchyConfigurationByOBID != null) {
            IRelCollection iRelCollection = hierarchyConfigurationByOBID.GetEnd1Relationships().GetRels(HierarchyUtils.REL_HIERARCHY_CONFIGURATION_2_ITEM);
            return iRelCollection.GetEnd2s();
        }
        return null;
    }

    @Override
    public IObject getDefaultHierarchyConfiguration(String targetDef) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, HierarchyUtils.I_HIERARCHY_CONFIGURATION);
        CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.equal, HierarchyUtils.DEFAULT_NAME);
        CIMContext.Instance.QueryEngine().addPropertyForQuery(queryRequest, "", BasicTargetObjUtils.PROPERTY_TARGET_CLASS_DEF, operator.equal, targetDef);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    @Override
    public ObjectDTO getHierarchyConfigurationByClassDefAndOBID(String obid, String classDef) throws Exception {
        IObject hierarchyConfigurationObj;
        // 例 阶段->专业->图纸类型
        if (StringUtils.isEmpty(obid)) {
            // 获取默认内置配置
            hierarchyConfigurationObj = getDefaultHierarchyConfiguration(classDef);
            if (hierarchyConfigurationObj == null) {
                throw new RuntimeException("未找到默认树配置,生成树失败!");
            }
        } else {
            hierarchyConfigurationObj = getHierarchyConfigurationByOBID(obid);
            if (hierarchyConfigurationObj == null) {
                throw new RuntimeException("根据OBID未能获取到树配置,生成树失败!");
            }
        }
        return hierarchyConfigurationObj.toObjectDTO();
    }

    @Override
    public IObject createHierarchyConfiguration(JSONObject requestBody, String targetDef) throws Exception {
        // 生成 HierarchyConfiguration
        ObjectDTO toCreateHierarchy = HierarchyUtils.parseHierarchyConfigurationFromJSON(requestBody);
        // 当名称为Default时 验证是否已经存在Default配置
        if (toCreateHierarchy.getName().equalsIgnoreCase(HierarchyUtils.DEFAULT_NAME)) {
            IObject defaultHierarchyConfiguration = null;
            try {
                defaultHierarchyConfiguration = getDefaultHierarchyConfiguration(targetDef);
            } catch (Exception e) {
                if (!ExceptionUtil.getMessage(e).contains("未找到默认树配置项")) {
                    throw new RuntimeException("查询默认树配置项失败!");
                }
            }

            if (defaultHierarchyConfiguration != null) {
                throw new RuntimeException("已存在名为\"" + HierarchyUtils.DEFAULT_NAME + "\"的配置项,禁止重复创建默认配置!");
            }
        }
        boolean b = toCreateHierarchy.getItems().stream().anyMatch(h -> h.getDefUID().equalsIgnoreCase(BasicTargetObjUtils.PROPERTY_TARGET_CLASS_DEF));
        if (b) {
            for (int i = 0; i < toCreateHierarchy.getItems().size(); i++) {
                if (toCreateHierarchy.getItems().get(i).getDefUID().equalsIgnoreCase(BasicTargetObjUtils.PROPERTY_TARGET_CLASS_DEF)) {
                    toCreateHierarchy.getItems().get(i).setDisplayValue(targetDef);
                }
            }
        } else {
            toCreateHierarchy.toSetValue(BasicTargetObjUtils.PROPERTY_TARGET_CLASS_DEF, targetDef);
        }
        IObject newCreateHierarchy = SchemaUtility.newIObject(HierarchyUtils.CCM_HIERARCHY_CONFIGURATION,
                toCreateHierarchy.getName(),
                toCreateHierarchy.getDescription(),
                "", "");
        /*InterfaceDefUtility.addInterface(newCreateHierarchy,
                HierarchyUtils.CCM_HIERARCHY_CONFIGURATION);*/
        for (ObjectItemDTO item : toCreateHierarchy.getItems()) {
            if (item.getDisplayValue() != null && StringUtils.isNotBlank(item.getDisplayValue().toString())) {
                newCreateHierarchy.setValue(item.getDefUID(), item.getDisplayValue());
            }
        }
        // 结束创建
        newCreateHierarchy.ClassDefinition().FinishCreate(newCreateHierarchy);
        return newCreateHierarchy;
    }

    /**
     * 生成树配置和配置项
     *
     * @param requestBody
     * @param targetDef
     * @return
     * @throws Exception
     */
    public IObject createHierarchyConfigurationWithItems(JSONObject requestBody, String targetDef, boolean withTransaction) throws Exception {
        if (withTransaction) {
            SchemaUtility.beginTransaction();
        }
        // 生成 HierarchyConfiguration
        JSONObject hierarchyConfigurationJson = requestBody.getJSONObject(HierarchyUtils.OBJ);
        JSONArray hierarchyConfigurationItemsJsonArray = requestBody.getJSONArray(HierarchyUtils.OBJ_ITEMS);
        ObjectDTO hierarchyConfigurationObj = HierarchyUtils.parseHierarchyConfigurationFromJSON(hierarchyConfigurationJson);

        for (ObjectItemDTO item : hierarchyConfigurationObj.getItems()) {
            if (item.getDefUID().equalsIgnoreCase(BasicTargetObjUtils.PROPERTY_TARGET_CLASS_DEF)) {
                item.setDisplayValue(targetDef);
            }
        }
        IObject hierarchyConfiguration = createHierarchyConfiguration(hierarchyConfigurationJson, targetDef);
        // 生成 HierarchyConfigurationItems
        ArrayList<ObjectDTO> itemDTOs = new ArrayList<>();
        for (int i = 0; i < hierarchyConfigurationItemsJsonArray.size(); i++) {
            JSONObject jsonObject = hierarchyConfigurationItemsJsonArray.getJSONObject(i);
            ObjectDTO itemDTO = HierarchyUtils.parseHierarchyConfigurationFromJSON(jsonObject);
            itemDTOs.add(itemDTO);
        }
        createHierarchyConfigurationItems(hierarchyConfiguration, itemDTOs);
        if (withTransaction) {
            SchemaUtility.commitTransaction();
        }
        return hierarchyConfiguration;
    }

    @Override
    public void deleteHierarchyConfiguration(String obid) throws Exception {
        // 获取已存在的树配置
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, HierarchyUtils.I_HIERARCHY_CONFIGURATION);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, obid);
        IObject existHierarchyConfiguration = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        if (existHierarchyConfiguration.Name().equalsIgnoreCase(HierarchyUtils.DEFAULT_NAME)) {
            throw new RuntimeException("默认配置禁止删除!");
        }
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        existHierarchyConfiguration.Delete();
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public void updateHierarchyConfiguration(ObjectDTO toUpdateHierarchyConfiguration) throws Exception {
        // 获取已存在的树配置
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, HierarchyUtils.I_HIERARCHY_CONFIGURATION);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, toUpdateHierarchyConfiguration.getObid());
        Iterator<IObject> existConstructionTypes = CIMContext.Instance.QueryEngine().query(queryRequest).GetEnumerator();
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        while (existConstructionTypes.hasNext()) {
            // 更新树配置
            IObject existConstructionType = existConstructionTypes.next();
            existConstructionType.BeginUpdate();
            for (ObjectItemDTO item : toUpdateHierarchyConfiguration.getItems()) {
                existConstructionType.Interfaces().item(HierarchyUtils.I_HIERARCHY_CONFIGURATION, true)
                        .Properties().item(item.getDefUID(), true).setValue(item.toValue());
            }
            existConstructionType.FinishUpdate();
        }
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public IObject getHierarchyConfigurationItemByOBID(String obid) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, HierarchyUtils.I_HIERARCHY_CONFIGURATION_ITEM);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, obid);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    @Override
    public IObject createHierarchyConfigurationItemWithConfigurationOBID(String hierarchyConfigurationOBID, ObjectDTO itemDTO) throws Exception {
        IObject hierarchyConfigurationByOBID = getHierarchyConfigurationByOBID(hierarchyConfigurationOBID);
        IInterface iInterface = InterfaceDefUtility.verifyInterface(hierarchyConfigurationByOBID, HierarchyUtils.I_HIERARCHY_CONFIGURATION);
        ICCMHierarchyConfiguration iccmHierarchyConfiguration = iInterface.toInterface(ICCMHierarchyConfiguration.class);
        return iccmHierarchyConfiguration.createHierarchyConfigurationItem(itemDTO);
    }

    @Override
    public IObject createHierarchyConfigurationItems(String configurationOBID, ObjectDTO toCreateHierarchyItem) throws Exception {
        IObject newCreateHierarchyItem = SchemaUtility.newIObject(HierarchyUtils.CCM_HIERARCHY_CONFIGURATION_ITEM,
                toCreateHierarchyItem.getName(),
                toCreateHierarchyItem.getDescription(),
                "", "");
        /*InterfaceDefUtility.addInterface(newCreateHierarchyItem,
                HierarchyUtils.I_HIERARCHY_CONFIGURATION_ITEM,
                BasicTargetObjUtils.I_BASIC_TARGET_OBJ);*/
        for (ObjectItemDTO item : toCreateHierarchyItem.getItems()) {
            newCreateHierarchyItem.setValue(item.getDefUID(), item.getDisplayValue());
        }

        // 结束创建
        newCreateHierarchyItem.ClassDefinition().FinishCreate(newCreateHierarchyItem);
        return newCreateHierarchyItem;
    }

    @Override
    public void deleteHierarchyConfigurationItem(String itemOBID) throws Exception {
        // 获取已存在的树配置
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, HierarchyUtils.I_HIERARCHY_CONFIGURATION_ITEM);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, itemOBID);
        IObjectCollection query = CIMContext.Instance.QueryEngine().query(queryRequest);
        if (query != null) {
            Iterator<IObject> toDeleteHierarchyConfigurationItems = query.GetEnumerator();
            // 开启事务
            if (!CIMContext.Instance.Transaction().inTransaction()) {
                CIMContext.Instance.Transaction().start();
            }
            while (toDeleteHierarchyConfigurationItems.hasNext()) {
                // 删除施工分类
                IObject existHierarchyConfigurationItem = toDeleteHierarchyConfigurationItems.next();
                existHierarchyConfigurationItem.Delete();
            }
            // 提交事务
            CIMContext.Instance.Transaction().commit();
        }
    }

    @Override
    public void updateHierarchyConfigurationItem(ObjectDTO toUpdateItem) throws Exception {
        // 获取已存在的树配置
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, HierarchyUtils.I_HIERARCHY_CONFIGURATION_ITEM);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, toUpdateItem.getObid());
        Iterator<IObject> existConstructionTypes = CIMContext.Instance.QueryEngine().query(queryRequest).GetEnumerator();
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        while (existConstructionTypes.hasNext()) {
            // 更新树配置
            IObject existConstructionType = existConstructionTypes.next();
            existConstructionType.BeginUpdate();
            for (ObjectItemDTO item : toUpdateItem.getItems()) {
                existConstructionType.Interfaces().item(HierarchyUtils.I_HIERARCHY_CONFIGURATION, true)
                        .Properties().item(item.getDefUID(), true).setValue(item.toValue());
            }
            existConstructionType.FinishUpdate();
        }
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    /**
     * 生成配置项
     *
     * @param hierarchyConfigurationByOBID
     * @param itemDTOs
     * @return
     * @throws Exception
     */
    @Override
    public void createHierarchyConfigurationItems(IObject hierarchyConfigurationByOBID, List<ObjectDTO> itemDTOs) throws Exception {
        List<IObject> itemOBJs = new ArrayList<>();
        for (ObjectDTO toCreateHierarchyConfigurationItem : itemDTOs) {
            IObject iObject = SchemaUtility.newIObject(HierarchyUtils.CCM_HIERARCHY_CONFIGURATION_ITEM,
                    toCreateHierarchyConfigurationItem.getName(),
                    toCreateHierarchyConfigurationItem.getDescription(),
                    "", "");
            /*InterfaceDefUtility.addInterface(iObject,
                    HierarchyUtils.I_HIERARCHY_CONFIGURATION_ITEM,
                    BasicTargetObjUtils.I_BASIC_TARGET_OBJ);*/
            for (ObjectItemDTO item : toCreateHierarchyConfigurationItem.getItems()) {
                if (item.getDisplayValue() != null && StringUtils.isNotBlank(item.getDisplayValue().toString())) {
                    iObject.setValue(item.getDefUID(), item.getDisplayValue());
                }
            }
            // 结束创建
            iObject.ClassDefinition().FinishCreate(iObject);
            itemOBJs.add(iObject);
        }
        // 创建关联关系
        for (IObject itemOBJ : itemOBJs) {
            SchemaUtility.createRelationShip(HierarchyUtils.REL_HIERARCHY_CONFIGURATION_2_ITEM, hierarchyConfigurationByOBID, itemOBJ, false);
        }
    }
    /* ******************************************************* 树配置项 方法 End ******************************************************* */

    /**
     * 生成树
     *
     * @param hierarchyConfigurationOBID
     * @return
     */
    @Override
    public HierarchyObjectDTO generateHierarchy(String hierarchyConfigurationOBID, String classDef) throws Exception {
        ObjectDTO hierarchyConfiguration;
        // 例 阶段->专业->图纸类型
        if (StringUtils.isEmpty(hierarchyConfigurationOBID)) {
            // 获取默认内置配置
            hierarchyConfiguration = this.getDefaultHierarchyConfiguration(classDef).toObjectDTO();
            if (null == hierarchyConfiguration) {
                throw new RuntimeException("未获取到默认配置,请先创建默认配置或者选择自定义配置!");
            }
        } else {
            hierarchyConfiguration = this.getHierarchyConfigurationByOBID(hierarchyConfigurationOBID).toObjectDTO();
        }
        IObjectCollection hierarchyConfigurationItemsCollection = this.getItemsByHierarchyConfigurationOBID(hierarchyConfiguration.getObid());
        List<ObjectDTO> itemsDTOs = ObjectDTOUtility.convertToObjectDTOList(hierarchyConfigurationItemsCollection);
        List<ObjectDTO> hierarchyConfigurationItems = HierarchyUtils.sortByHierarchyLevel(itemsDTOs);
        HierarchyObjectDTO rootTree = new HierarchyObjectDTO();
        List<HierarchyObjectDTO> children = rootTree.getChildren();
        // 2022.08.04 HT 替换新树生成方法
        List<String> classDefUIDList = new ArrayList<>();
        classDefUIDList.add(classDef);
        HierarchyUtils.generateTreeByConfiguration(children, classDefUIDList, hierarchyConfigurationItems);
        // 旧树生成方法
        /*List<ObjectDTO> topEnumListTypes = new ArrayList<>();
        IObjectCollection allDocuments = documentService.getDocumentsWithPage(new PageRequest(0, 0));
        List<ObjectDTO> allDocumentDTOs = ObjectDTOUtility.convertToObjectDTOList(allDocuments);
        HierarchyUtils.generateTreeByObjectsAndConfiguration(children, allDocumentDTOs, hierarchyConfigurationItems, topEnumListTypes);*/
        return rootTree;
    }

    /**
     * 根据前端传入生成目录树
     *
     * @param classDefinitionUid
     * @return
     */
    @Override
    public HierarchyObjectDTO generateHierarchyWithoutConf(String classDefinitionUid, String[] propertyDefinitionUidArray) throws Exception {
        HierarchyObjectDTO rootTree = new HierarchyObjectDTO();
        List<HierarchyObjectDTO> children = rootTree.getChildren();
        List<String> classDefUIDList = new ArrayList<>();
        classDefUIDList.add(classDefinitionUid);
        List<PropertyHierarchyVo> propertyHierarchyVos = schemaBusinessService.summaryProperty(classDefUIDList, propertyDefinitionUidArray);
        HierarchyUtils.generateTree(children, propertyHierarchyVos);
        return rootTree;
    }
}
