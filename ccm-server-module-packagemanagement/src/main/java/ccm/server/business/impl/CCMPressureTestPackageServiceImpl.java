package ccm.server.business.impl;

import ccm.server.business.*;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.*;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.models.query.QueryRequest;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.ICCMPressureTestPackage;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;
import ccm.server.schema.interfaces.IWorkStep;
import ccm.server.util.CommonUtility;
import ccm.server.utils.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.CommonUtils;
import org.jeecg.common.util.MinioUtil;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/1/11 14:37
 */
@Service
public class CCMPressureTestPackageServiceImpl implements ICCMPressureTestPackageService {

    @Autowired
    private IHierarchyService hierarchyService;

    @Autowired
    private ICCMDocumentService documentService;

    @Autowired
    private ICCMDesignService designService;

    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    /* *****************************************  树结构方法 start  ***************************************** */
    @Override
    public ObjectDTO getPressureTestPackageHierarchyConfigurationForm(String formPurpose) throws Exception {
        return null;
    }

    @Override
    public List<ObjectDTO> getPressureTestPackageFormPropertiesForConfigurationItem() throws Exception {
        return hierarchyService.getObjectFormPropertiesForConfigurationItem(PackagesUtils.CCM_PRESSURE_TEST_PACKAGE);
    }

    @Override
    public Map<String, Object> getPressureTestPackageHierarchyConfigurationFormWithItem(String formPurpose) throws Exception {
        return hierarchyService.getObjectHierarchyConfigurationFormWithItem(formPurpose, PackagesUtils.CCM_PRESSURE_TEST_PACKAGE);
    }

    @Override
    public IObject createPressureTestPackageHierarchyConfigurationWithItems(JSONObject requestBody) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject hierarchyConfigurationWithItems = hierarchyService.createHierarchyConfigurationWithItems(requestBody, PackagesUtils.CCM_PRESSURE_TEST_PACKAGE,false);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return hierarchyConfigurationWithItems;
    }

    @Override
    public void deletePressureTestPackageHierarchyConfiguration(String obid) throws Exception {
        hierarchyService.deleteHierarchyConfiguration(obid);
    }

    @Override
    public void updatePressureTestPackageHierarchyConfiguration(ObjectDTO hierarchyConfiguration) throws Exception {
        hierarchyService.updateHierarchyConfiguration(hierarchyConfiguration);
    }

    @Override
    public IObjectCollection getMyPressureTestPackageHierarchyConfigurations(HttpServletRequest request, PageRequest pageRequest) throws Exception {
        return hierarchyService.getMyHierarchyConfigurations(request, PackagesUtils.CCM_PRESSURE_TEST_PACKAGE, pageRequest);
    }

    @Override
    public IObjectCollection getPressureTestPackageHierarchyConfigurationItems(String obid) throws Exception {
        return hierarchyService.getItemsByHierarchyConfigurationOBID(obid);
    }

    @Override
    public IObject createPressureTestPackageHierarchyConfigurationItem(String hierarchyConfigurationOBID, ObjectDTO hierarchyConfigurationItemDTO) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject hierarchyConfigurationItemWithConfigurationOBID = hierarchyService.createHierarchyConfigurationItemWithConfigurationOBID(hierarchyConfigurationOBID, hierarchyConfigurationItemDTO);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return hierarchyConfigurationItemWithConfigurationOBID;
    }

    @Override
    public HierarchyObjectDTO generateHierarchyByPressureTestPackagesAndConfiguration(String hierarchyConfigurationOBID) throws Exception {

        // 获取 配置项
        ObjectDTO hierarchyConfiguration = hierarchyService
                .getHierarchyConfigurationByClassDefAndOBID(hierarchyConfigurationOBID, PackagesUtils.CCM_PRESSURE_TEST_PACKAGE);

        IObjectCollection hierarchyConfigurationItemsCollection = getPressureTestPackageHierarchyConfigurationItems(hierarchyConfiguration.getObid());
        List<ObjectDTO> itemsDTOs = ObjectDTOUtility.convertToObjectDTOList(hierarchyConfigurationItemsCollection);
        List<ObjectDTO> hierarchyConfigurationItems = HierarchyUtils.sortByHierarchyLevel(itemsDTOs);
        HierarchyObjectDTO rootTree = new HierarchyObjectDTO();
        List<HierarchyObjectDTO> children = rootTree.getChildren();
        // 旧树生成方法
        /*List<ObjectDTO> topEnumListTypes = new ArrayList<>();
        IObjectCollection allPressureTestPackages = getPressureTestPackages(new PageRequest(0, 0));
        List<ObjectDTO> allPressureTestPackageDTOs = ObjectDTOUtility.convertToObjectDTOList(allPressureTestPackages);
        HierarchyUtils.generateTreeByObjectsAndConfiguration(children, allPressureTestPackageDTOs, hierarchyConfigurationItems, topEnumListTypes);*/
        // 2022.08.04 HT 替换新树生成方法
        List<String> classDefUIDList = new ArrayList<>();
        classDefUIDList.add(PackagesUtils.CCM_PRESSURE_TEST_PACKAGE);
        HierarchyUtils.generateTreeByConfiguration(children, classDefUIDList, hierarchyConfigurationItems);
        return rootTree;
    }

    @Override
    public IObjectCollection getPressureTestPackagesFromHierarchyNode(HierarchyObjectDTO selectedNode, PageRequest pageRequest) throws Exception {
        List<HierarchyObjectDTO> parents = HierarchyUtils.getParents(new ArrayList<>(), selectedNode);
        ObjectDTO objectDTO = new ObjectDTO();
        for (HierarchyObjectDTO parent : parents) {
            ObjectItemDTO objectItemDTO = new ObjectItemDTO();
            objectItemDTO.setDefUID(parent.getName());
            objectItemDTO.setDisplayValue(parent.getId() + ":" + parent.getName());
            objectDTO.add(objectItemDTO);
        }
        return getPressureTestPackagesWithItems(objectDTO, pageRequest);
    }
    /* *****************************************  树结构方法 end  ***************************************** */

    /* *****************************************  试压包方法 start  ***************************************** */
    @Override
    public ObjectDTO getPressureTestPackageForm(operationPurpose formPurpose, ObjectDTO mainObject) throws Exception {
        return null;
    }

    @Override
    public IObjectCollection getPressureTestPackages(PageRequest pageRequest) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_PRESSURE_TEST_PACKAGE);
        // 分页
        if (PageUtility.verifyPage(pageRequest)) {
            CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        }
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getPressureTestPackagesWithItems(ObjectDTO items, PageRequest pageRequest) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_PRESSURE_TEST_PACKAGE);
        // 添加条件
        for (ObjectItemDTO item : items.getItems()) {
            CIMContext.Instance.QueryEngine().addPropertyForQuery(queryRequest, "",
                    item.getDefUID(), operator.equal, item.getDisplayValue().toString());
        }
        // 分页
        if (PageUtility.verifyPage(pageRequest)) {
            CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        }
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObject getPressureTestPackagesByOBID(String obid) throws Exception {
        if (StringUtils.isBlank(obid)) {
            throw new Exception("试压包OBID不可为空!");
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_PRESSURE_TEST_PACKAGE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, obid);
        IObject iObject = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        if (iObject == null) {
            throw new Exception("未找到OBID为" + obid + "的试压包!");
        }
        return iObject;
    }

    @Override
    public IObject getPressureTestPackagesByUID(String uid) throws Exception {
        if (StringUtils.isBlank(uid)) {
            throw new Exception("UID不可为空!");
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_PRESSURE_TEST_PACKAGE);
        CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, operator.equal, uid);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    @Override
    public IObject createPressureTestPackage(ObjectDTO toCreatePressureTestPackage) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject newPressureTestPackage = SchemaUtility.newIObject(PackagesUtils.CCM_PRESSURE_TEST_PACKAGE,
                toCreatePressureTestPackage.getName(),
                toCreatePressureTestPackage.getDescription(),
                "", "");
        /*InterfaceDefUtility.addInterface(newPressureTestPackage,
                PackagesUtils.I_PRESSURE_TEST_PACKAGE);*/
        for (ObjectItemDTO item : toCreatePressureTestPackage.getItems()) {
            newPressureTestPackage.setValue(item.getDefUID(), item.getDisplayValue());
        }
        // 结束创建
        newPressureTestPackage.ClassDefinition().FinishCreate(newPressureTestPackage);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return newPressureTestPackage;
    }

    @Override
    public void updatePressureTestPackage(ObjectDTO toUpdatePressureTestPackage) throws Exception {
        // 获取已存在的设计类型
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_PRESSURE_TEST_PACKAGE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, toUpdatePressureTestPackage.getObid());
        IObjectCollection query = CIMContext.Instance.QueryEngine().query(queryRequest);
        if (query.Size() > 0) {
            Iterator<IObject> existDesignTypes = query.GetEnumerator();
            // 开启事务
            if (!CIMContext.Instance.Transaction().inTransaction()) {
                CIMContext.Instance.Transaction().start();
            }
            while (existDesignTypes.hasNext()) {
                // 更新设计类型属性
                IObject existDesignType = existDesignTypes.next();
                existDesignType.BeginUpdate();
                for (ObjectItemDTO item : toUpdatePressureTestPackage.getItems()) {
                    existDesignType.setValue(item.getDefUID(), item.toValue());
                }
                existDesignType.FinishUpdate();
            }
            // 提交事务
            CIMContext.Instance.Transaction().commit();
        }
    }

    @Override
    public void deletePressureTestPackage(String pressureTestPackageOBID) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_PRESSURE_TEST_PACKAGE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, pressureTestPackageOBID);
        IObjectCollection query = CIMContext.Instance.QueryEngine().query(queryRequest);
        if (query.Size() > 0) {
            Iterator<IObject> existPressureTestPackages = query.GetEnumerator();
            // 开启事务
            if (!CIMContext.Instance.Transaction().inTransaction()) {
                CIMContext.Instance.Transaction().start();
            }
            while (existPressureTestPackages.hasNext()) {
                IObject existPressureTestPackage = existPressureTestPackages.next();
                existPressureTestPackage.Delete();
            }
            // 提交事务
            CIMContext.Instance.Transaction().commit();
        }
    }

    /**
     * 更新试压包状态
     *
     * @param uid
     * @param tptpStatus
     * @throws Exception
     */
    @Override
    public void updateTPTPStatusByUID(String uid, String tptpStatus) throws Exception {
        IObject taskPackagesByOBID = getPressureTestPackagesByUID(uid);
        ICCMPressureTestPackage iccmPressureTestPackage = taskPackagesByOBID.toInterface(ICCMPressureTestPackage.class);
        iccmPressureTestPackage.updateTPTPStatus(tptpStatus, true);
    }
    /* *****************************************  试压包方法 end  ***************************************** */

    /* *****************************************  试压包图纸方法 start  ***************************************** */
    @Override
    public IObjectCollection getRelatedDocuments(String pressureTestPackageOBID) throws Exception {
        IObject pressureTestPackagesByOBID = getPressureTestPackagesByOBID(pressureTestPackageOBID);
        return pressureTestPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_DOCUMENT).GetEnd2s();
    }

    @Override
    public IObjectCollection getSelectableDocuments(String obid, FiltersParam filtersParam, OrderByParam orderByParam, PageRequest pageRequest) throws Exception {
        /*IObjectCollection relatedDocuments = getRelatedDocuments(obid);
        List<String> relatedOBIDs = relatedDocuments.listOfOBID();*/
        /*IQueryEngine queryEngine = CIMContext.Instance.QueryRequest();
        queryEngine.start();
        queryEngine.addClassDefForQuery(DocumentUtils.CCM_DOCUMENT);
        queryEngine.addPropertyForQuery(DocumentUtils.I_DOCUMENT, DocumentUtils.PROPERTY_DESIGN_PHASE, operator.equal, "EN_ShopDesign");
        queryEngine.addPropertyForQuery("", propertyDefinitionType.CIMDocState.toString(), operator.equal, "EN_IFC");
        queryEngine.addPropertyForQuery("", propertyDefinitionType.OBID.toString(),
                operator.notIn, String.join(",", relatedOBIDs));
        queryEngine.query();*/
        Map<String, String> filters = filtersParam.getFilters();
        // 2022.04.07 HT 详设过滤问题
        String designPhase = filters.get(DocumentUtils.PROPERTY_DESIGN_PHASE);
        // 当查询过滤只有详设时返回空数据
        if (StringUtils.isNotBlank(designPhase)) {
            if (designPhase.contains("EN_DetailDesign") && !designPhase.contains("EN_ShopDesign")) {
                ObjectCollection objectCollection = new ObjectCollection();
                objectCollection.PageResult().setCurrent(pageRequest.getPageIndex());
                objectCollection.PageResult().setSize(pageRequest.getPageSize());
                objectCollection.PageResult().setTotal(0L);
                return objectCollection;
            }
        }
        // 2022.04.07 HT 详设过滤问题
        filters.put(DocumentUtils.PROPERTY_DESIGN_PHASE, "(EN_ShopDesign)");
        filters.put(propertyDefinitionType.CIMDocState.toString(), "(EN_IFC)");
        // filters.put(propertyDefinitionType.OBID.toString(), "!(" + String.join(",", relatedOBIDs) + ")");
        return schemaBusinessService.generalQuery(DocumentUtils.CCM_DOCUMENT, pageRequest.getPageIndex(), pageRequest.getPageSize()
                , orderByParam.getOrderByWrappers(), filters);
    }

    @Override
    public void assignDocumentsToPressureTestPackage(String pressureTestPackageOBID, List<String> providedDocuments) throws Exception {
        IObject pressureTestPackagesByOBID = getPressureTestPackagesByOBID(pressureTestPackageOBID);
        if (pressureTestPackagesByOBID == null) {
            throw new Exception("未能获取到试压包:" + pressureTestPackageOBID);
        }
        List<String> relatedDocuments = pressureTestPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_DOCUMENT).GetEnd2s().listOfOBID();
        SchemaUtility.beginTransaction();
        for (String providedDocument : providedDocuments) {
            IObject documentByOBID = documentService.getDocumentByOBID(providedDocument);
            if (!relatedDocuments.contains(providedDocument)) {
                IRel iRel = SchemaUtility.newRelationship(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_DOCUMENT, pressureTestPackagesByOBID, documentByOBID, true);
                iRel.Interfaces().addDynInterface(interfaceDefinitionType.ICIMRevisionItem.name());
                iRel.ClassDefinition().FinishCreate(iRel);
            }
        }
        SchemaUtility.commitTransaction();
    }

    @Override
    public Boolean removeDocumentsFromPressureTestPackage(String packageId, String documentIds) throws Exception {
        IObject workPackagesByOBID = getPressureTestPackagesByOBID(packageId);
        List<String> toRemoveDocs = Arrays.asList(documentIds.split(","));
        IRelCollection ptpDocRelColl = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_DOCUMENT);
        IRelCollection ptpDesignRelColl = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_DESIGN_OBJ);
        IRelCollection ptpWorkStepColl = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_WORK_STEP);

        if (ptpDocRelColl != null) {
            Iterator<IObject> ptpDocRelIter = ptpDocRelColl.GetEnumerator();
            SchemaUtility.beginTransaction();
            while (ptpDocRelIter.hasNext()) {
                IObject ptpDocRelObj = ptpDocRelIter.next();
                IRel ptpDocRel = ptpDocRelObj.toInterface(IRel.class);
                IObject documentObj = ptpDocRel.GetEnd2();
                if (toRemoveDocs.contains(documentObj.OBID())) {
                    // 图纸匹配
                    List<String> designOBIDUnderDocument = documentObj.GetEnd1Relationships().GetRels(DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd2s().listOfOBID();
                    Iterator<IObject> ptpDesignRelIter = ptpDesignRelColl.GetEnumerator();
                    while (ptpDesignRelIter.hasNext()) {
                        IObject ptpDesignRelObj = ptpDesignRelIter.next();
                        IRel ptpDesignRel = ptpDesignRelObj.toInterface(IRel.class);
                        IObject designObj = ptpDesignRel.GetEnd2();
                        if (designOBIDUnderDocument.contains(designObj.OBID())) {
                            // 设计数据匹配
                            List<String> wsUnderDocDesign = designObj.GetEnd1Relationships().GetRels(PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP).GetEnd2s().listOfOBID();
                            Iterator<IObject> ptpWorkStepIter = ptpWorkStepColl.GetEnumerator();
                            while (ptpWorkStepIter.hasNext()) {
                                IObject ptpWorkStepObj = ptpWorkStepIter.next();
                                IRel ptpWorkStepRel = ptpWorkStepObj.toInterface(IRel.class);
                                IObject workStepObj = ptpWorkStepRel.GetEnd2();
                                if (wsUnderDocDesign.contains(workStepObj.OBID())) {
                                    ptpWorkStepObj.Delete();
                                    // 删除状态的工作步骤解除关联时直接删除
                                    IWorkStep workStep = workStepObj.toInterface(IWorkStep.class);
                                    String wsStatus = workStep.WSStatus();
                                    if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                                        workStep.Delete();
                                    }
                                }
                            }
                            ptpDesignRelObj.Delete();
                        }
                    }
                    ptpDocRelObj.Delete();
                }
            }
            SchemaUtility.commitTransaction();
        }
        return true;
    }
    /* *****************************************  试压包图纸方法 end  ***************************************** */

    /* *****************************************  试压包材料方法 start  ***************************************** */
    @Override
    public ObjectDTO getComponentForm(operationPurpose formPurpose, ObjectDTO mainObject) throws Exception {
        return null;
    }

    @Override
    public IObjectCollection getRelatedComponents(String pressureTestPackageOBID) throws Exception {
        IObject pressureTestPackagesByOBID = getPressureTestPackagesByOBID(pressureTestPackageOBID);
        return pressureTestPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_DESIGN_OBJ).GetEnd2s();
    }

    @Override
    public IObjectCollection getRelatedWorkSteps(String pressureTestPackageOBID) throws Exception {
        IObject pressureTestPackagesByOBID = getPressureTestPackagesByOBID(pressureTestPackageOBID);
        return pressureTestPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_WORK_STEP).GetEnd2s();
    }

    @Override
    public IObjectCollection getSelectableComponentsForPressureTestPackage(String pressureTestPackageOBID, String documentOBID, String classDefinitionUID, FiltersParam filtersParam, OrderByParam orderByParam, PageRequest pageRequest) throws Exception {
        IObject pressureTestPackagesByOBID = getPressureTestPackagesByOBID(pressureTestPackageOBID);
        ICCMPressureTestPackage iccmPressureTestPackage = pressureTestPackagesByOBID.toInterface(ICCMPressureTestPackage.class);
        String pressureTestPackagePurpose = iccmPressureTestPackage.getPurpose();

        // 获取试压包关联的设计数据
        IObjectCollection relatedComponents = getRelatedComponents(pressureTestPackageOBID);
        List<String> relatedComponentOBIDs = relatedComponents.listOfOBID();

        // 获取试压包关联的工作步骤
        IObjectCollection relatedWorkSteps = getRelatedWorkSteps(pressureTestPackageOBID);
        List<String> workStepsUnderPTP = relatedWorkSteps.listOfOBID();


        IObject documentByOBID = documentService.getDocumentByOBID(documentOBID);
        IObjectCollection iObjectCollection = documentByOBID.GetEnd1Relationships().GetRels(DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd2s();
        List<String> designDataUnderDoc = iObjectCollection.listOfOBID();

        // 当未找到设计数据时
        if (designDataUnderDoc == null || designDataUnderDoc.isEmpty()) {
            return null;
        }

        /*IQueryEngine queryEngine = CIMContext.Instance.QueryRequest();
        queryEngine.start();
        queryEngine.addClassDefForQuery(classDefinitionUID);
        queryEngine.addPropertyForQuery("", propertyDefinitionType.OBID.toString(),
                operator.in, String.join(",", designDataUnderDoc));
        IObjectCollection allDesignDataUnderDocColl = queryEngine.query();*/
        // 过滤已在试压包中的设计数据
        designDataUnderDoc.removeAll(relatedComponentOBIDs);

        Map<String, String> filters = filtersParam.getFilters();
        filters.put(propertyDefinitionType.OBID.toString(), "(" + String.join(",", designDataUnderDoc) + ")");
        filters.put(propertyDefinitionType.CIMRevisionItemOperationState.toString(), "!" + operationState.EN_Deleted.name());
        IObjectCollection allDesignDataUnderDocColl = schemaBusinessService.generalQuery(classDefinitionUID, 0, 0,
                orderByParam.getOrderByWrappers(), filters);

        ObjectCollection selectableComponentsForPressureTestPackage = new ObjectCollection();
        int pageIndex = pageRequest.getPageIndex();
        int pageSize = pageRequest.getPageSize();
        selectableComponentsForPressureTestPackage.PageResult().setCurrent(pageIndex);
        selectableComponentsForPressureTestPackage.PageResult().setSize(pageSize);

        long total = 0L;

        Iterator<IObject> allDesignDataUnderDoc = allDesignDataUnderDocColl.GetEnumerator();
        while (allDesignDataUnderDoc.hasNext()) {
            IObject designDataObj = allDesignDataUnderDoc.next();
            IObjectCollection workStepsColl = designDataObj.GetEnd1Relationships().GetRels(PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP).GetEnd2s();
            Iterator<IObject> workSteps = workStepsColl.GetEnumerator();
            while (workSteps.hasNext()) {
                IObject workStepObj = workSteps.next();
                IWorkStep workStep = workStepObj.toInterface(IWorkStep.class);
                // 不计算删除状态的工作步骤
                String wsStatus = workStep.WSStatus();
                if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                    continue;
                }
                String workStepPhase = workStep.getProperty("ROPWorkStepWPPhase").Value().toString();
                // 当试压包施工阶段和工作步骤给自己的相同 并且 工作步骤不在试压包中 时为可选择的设计数据
                if (pressureTestPackagePurpose.equalsIgnoreCase(workStepPhase) && !workStepsUnderPTP.contains(workStep.OBID())) {
                    selectableComponentsForPressureTestPackage.addRangeUniquely(designDataObj);
                    total++;
                }
            }
        }
        selectableComponentsForPressureTestPackage.PageResult().setTotal(total);
        return selectableComponentsForPressureTestPackage;
    }

    /**
     * 获取最新版加设图纸中同名组件
     *
     * @param componentNames
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, List<String>> getLatestComponentOBIDsAndDocOBIDsByNamesInShopDesignDocument(String componentNames) throws Exception {
        if (StringUtils.isBlank(componentNames)) {
            throw new Exception("组件名称不可为空!");
        }
        String[] nameList = componentNames.split(",");
        Map<String, List<String>> designToOBID = new HashMap<>();
        Map<String, List<String>> docToOBID = new HashMap<>();
        for (String name : nameList) {
            IQueryEngine iQueryEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = iQueryEngine.start();
            iQueryEngine.addInterfaceForQuery(queryRequest, DataRetrieveUtils.I_COMPONENT);
            iQueryEngine.addNameForQuery(queryRequest, operator.equal, name);
            iQueryEngine.addPropertyForQuery(queryRequest, "", propertyDefinitionType.CIMRevisionItemOperationState.toString(), operator.notEqual, operationState.EN_Deleted.toString());
            IObjectCollection designDataColl = iQueryEngine.query(queryRequest);
            if (designDataColl == null || !designDataColl.hasValue()) {
                throw new Exception("获取组件失败!未找到名为[" + name + "]的组件!");
            }
            // 处理查询到的设计数据
            Iterator<IObject> designDataIter = designDataColl.GetEnumerator();
            List<String> shopDesignDocOBIDWithDesign = new ArrayList<String>();
            List<String> shopDesignDocNameWithDesign = new ArrayList<String>();
            List<String> shopDesignOBID = new ArrayList<String>();
            while (designDataIter.hasNext()) {
                IObject designDataObj = designDataIter.next();
                IObject docObj = designDataObj.GetEnd2Relationships().GetRel(DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd1();
                // 获取图纸设计阶段
                String docDesignPhase = docObj.getProperty(DocumentUtils.PROPERTY_DESIGN_PHASE).Value().toString();
                // 只查询加设图纸下的
                if ("EN_ShopDesign".equals(docDesignPhase)) {
                    shopDesignDocOBIDWithDesign.add(docObj.OBID());
                    shopDesignDocNameWithDesign.add(docObj.Name());
                    shopDesignOBID.add(designDataObj.OBID());
                }
            }
            if (shopDesignDocOBIDWithDesign.isEmpty()) {
                throw new Exception("未找到包含组件[" + name + "]的加设图纸!");
            }
            if (shopDesignDocOBIDWithDesign.size() > 1) {
                throw new Exception("查询到[" + shopDesignDocOBIDWithDesign.size() + "]个包含组件[" + name + "]的加设图纸!图纸名称:[" + String.join(",", shopDesignDocNameWithDesign) + "]");
            }
            designToOBID.put(name, shopDesignOBID);
            docToOBID.put(name, shopDesignDocOBIDWithDesign);
        }
        List<String> designOBIDs = new ArrayList<>();
        List<String> docOBIDs = new ArrayList<>();
        for (String s : designToOBID.keySet()) {
            designOBIDs.addAll(designToOBID.get(s));
        }
        for (String s : docToOBID.keySet()) {
            docOBIDs.addAll(docToOBID.get(s));
        }
        Map<String, List<String>> docDesignOBIDs = new HashMap<>();
        docDesignOBIDs.put("document", docOBIDs);
        docDesignOBIDs.put("design", designOBIDs);
        return docDesignOBIDs;
    }

    @Override
    public void assignComponentsToPressureTestPackage(String pressureTestPackageOBID, List<String> providedComponents) throws Exception {
        IObject pressureTestPackagesByOBID = getPressureTestPackagesByOBID(pressureTestPackageOBID);
        if (pressureTestPackagesByOBID == null) {
            throw new Exception("未找到对应试压包!");
        }
        ICCMPressureTestPackage pressureTestPackage = pressureTestPackagesByOBID.toInterface(ICCMPressureTestPackage.class);
        String pressureTestPackagePurpose = pressureTestPackage.getPurpose();

        List<String> relatedDesignData = pressureTestPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_DESIGN_OBJ).GetEnd2s().listOfOBID();
        List<String> relatedWorkStep = pressureTestPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_WORK_STEP).GetEnd2s().listOfOBID();
        SchemaUtility.beginTransaction();
        for (String providedComponent : providedComponents) {
            IObject designByOBID = designService.getComponentByOBID(providedComponent);
            IObjectCollection workStepCollection = designByOBID.GetEnd1Relationships().GetRels(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd2s();
            Iterator<IObject> iObjectIterator = workStepCollection.GetEnumerator();
            while (iObjectIterator.hasNext()) {
                IObject workStepObj = iObjectIterator.next();
                IWorkStep workStep = workStepObj.toInterface(IWorkStep.class);
                IObjectCollection iObjectCollection = workStepObj.GetEnd2Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_WORK_STEP).GetEnd1s();
                // 当工作步骤和试压包有关联时,只添加材料
                if (iObjectCollection.size() > 0) {
                    continue;
                }
                // 不计算删除状态的工作步骤
                String wsStatus = workStep.WSStatus();
                if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                    continue;
                }
                String workStepPurpose = workStepObj.getProperty("ROPWorkStepWPPhase").Value() == null ? "" : workStepObj.getProperty("ROPWorkStepWPPhase").Value().toString();
                // 只添加和试压包相同阶段的工作步骤
                if (!relatedWorkStep.contains(workStepObj.OBID()) && pressureTestPackagePurpose.equalsIgnoreCase(workStepPurpose)) {
                    IRel iRel = SchemaUtility.newRelationship(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_WORK_STEP, pressureTestPackagesByOBID, workStepObj, true);
                    iRel.Interfaces().addDynInterface(interfaceDefinitionType.ICIMRevisionItem.name());
                    iRel.ClassDefinition().FinishCreate(iRel);

                }
            }
            if (!relatedDesignData.contains(providedComponent)) {
                IRel iRel = SchemaUtility.newRelationship(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_DESIGN_OBJ, pressureTestPackagesByOBID, designByOBID, true);
                iRel.Interfaces().addDynInterface(interfaceDefinitionType.ICIMRevisionItem.name());
                iRel.ClassDefinition().FinishCreate(iRel);
            }
        }
        SchemaUtility.commitTransaction();
    }

    @Override
    public Boolean removeComponentsFromPackage(String packageId, String componentIds) throws Exception {
        IObject pressureTestPackageByOBID = getPressureTestPackagesByOBID(packageId);
        IRelCollection ptp2docRel = pressureTestPackageByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_DOCUMENT);
        IRelCollection ptp2designRel = pressureTestPackageByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_DESIGN_OBJ);
        IRelCollection ptp2wsRel = pressureTestPackageByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_WORK_STEP);
        List<String> wsInPtp = ptp2wsRel.GetEnd2s().listOfOBID();

        List<String> strings = Arrays.asList(componentIds.split(","));
        if (ptp2designRel != null) {
            List<String> designInPtp = ptp2designRel.GetEnd2s().listOfOBID();

            Iterator<IObject> ptp2designRelObjs = ptp2designRel.GetEnumerator();
            SchemaUtility.beginTransaction();
            while (ptp2designRelObjs.hasNext()) {
                IObject relObj = ptp2designRelObjs.next();
                IRel iRel = relObj.toInterface(IRel.class);
                IObject designData = iRel.GetEnd2();
                if (strings.contains(designData.OBID())) {
                    // 判断删除工作步骤关联关系
                    IObjectCollection iObjectCollection = designData.GetEnd1Relationships().GetRels(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_WORK_STEP).GetEnd2s();
                    List<String> wsInDesign = iObjectCollection.listOfOBID();
                    // 当存在交集时
                    List<String> intersection = wsInPtp.stream().filter(wsInDesign::contains).collect(Collectors.toList());
                    if (intersection.size() > 0) {
                        for (String wsOBID : intersection) {
                            IObject wsObj = ptp2wsRel.GetEnd2s().itemByOBID(wsOBID);
                            IWorkStep workStep = wsObj.toInterface(IWorkStep.class);
                            // 删除状态的工作步骤解除关联时直接删除
                            String wsStatus = workStep.WSStatus();
                            if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                                workStep.Delete();
                            }
                            // 删除工作步骤关联关系
                            wsObj.GetEnd2Relationships().GetRel(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_WORK_STEP).Delete();
                        }
                    }

                    // 判断删除图纸关联关系
                    IObject docObj = designData.GetEnd2Relationships().GetRel(DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd1();
                    List<String> designInDoc = docObj.GetEnd1Relationships().GetRels(DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd2s().listOfOBID();
                    // 删除当前设计数据OBID 检查是否还有该图纸下设计数据
                    designInPtp.remove(designData.OBID());
                    List<String> collect = designInPtp.stream().filter(designInDoc::contains).collect(Collectors.toList());
                    // 当不存在图纸中的设计数据时 删除图纸关联关系
                    if (collect.isEmpty()) {
                        Iterator<IObject> p2dRelIter = ptp2docRel.GetEnumerator();
                        while (p2dRelIter.hasNext()) {
                            IRel p2dRel = p2dRelIter.next().toInterface(IRel.class);
                            if (p2dRel.GetEnd2().OBID().equals(docObj.OBID())) {
                                p2dRel.Delete();
                            }
                        }
                    }
                    // 当设计数据 OBID 在待删除中时删除
                    relObj.Delete();
                }
            }
            SchemaUtility.commitTransaction();
        }
        return true;
    }
    /* *****************************************  试压包材料方法 end  ***************************************** */

    /* *****************************************  试压包文件方法 start  ***************************************** */
    @Override
    public ObjectDTO toPressureTestPackageFile(String fileVersion, String fileCount, String fileNotes) {
        ObjectDTO packageFileDTO = CommonUtility.generateBaseObjectDTO(null, "", PackagesUtils.CCM_PRESSURE_TEST_PACKAGE_FILE);
        packageFileDTO.toSetValue(PackagesUtils.PROPERTY_VERSION, fileVersion);
        packageFileDTO.toSetValue(BasicNoteObjUtils.PROPERTY_NOTES, fileNotes);
        packageFileDTO.toSetValue(PackagesUtils.PROPERTY_PAGE_COUNT, fileCount);
        return packageFileDTO;
    }

    @Override
    public IObject getPressureTestPackagesFileByOBID(String obid) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_PRESSURE_TEST_PACKAGE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, obid);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    @Override
    public IObjectCollection getPressureTestPackagesFilesByOBIDs(String obids) throws Exception {
        if (StringUtils.isEmpty(obids) || obids.split(",").length < 1) {
            throw new Exception("试压包文件OBID不可为空!");
        }
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_PRESSURE_TEST_PACKAGE);
        CIMContext.Instance.QueryEngine().addPropertyForQuery(queryRequest,
                "", propertyDefinitionType.OBID.toString(), operator.in, obids);
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObject createPressureTestPackageFile(ObjectDTO toCreatePressureTestPackageFile) throws Exception {
        IObject newPressureTestPackageFile = SchemaUtility.newIObject(PackagesUtils.CCM_PRESSURE_TEST_PACKAGE_FILE,
                toCreatePressureTestPackageFile.getName(),
                toCreatePressureTestPackageFile.getDescription(),
                "", "");
        /*InterfaceDefUtility.addInterface(newPressureTestPackageFile,PackagesUtils.CCM_PRESSURE_TEST_PACKAGE_FILE);*/
        for (ObjectItemDTO item : toCreatePressureTestPackageFile.getItems()) {
            newPressureTestPackageFile.setValue(item.getDefUID(), item.getDisplayValue());
        }
        // 结束创建
        newPressureTestPackageFile.ClassDefinition().FinishCreate(newPressureTestPackageFile);
        return newPressureTestPackageFile;
    }

    /**
     * 试压包上传文件并创建关联关系
     *
     * @param file                    上传文件
     * @param bizPath                 文件夹路径
     * @param pressureTestPackageFile 试压包
     * @return 文件路径
     * @throws Exception
     */
    @Override
    public String saveFile(MultipartFile file, String bizPath, String pressureTestPackageId, ObjectDTO pressureTestPackageFile) throws Exception {
        // 获取试压包
        IObject pressureTestPackagesByOBID = getPressureTestPackagesByOBID(pressureTestPackageId);

        // 设置默认文件夹路径
        if (oConvertUtils.isEmpty(bizPath)) {
            bizPath = pressureTestPackagesByOBID.Name();
        }

        String file_url = MinioUtil.upload(file, bizPath);
        if (oConvertUtils.isEmpty(file_url)) {
            throw new Exception("上传失败,请检查配置信息是否正确!");
        }

        // 添加地址并保存
        String orgName = file.getOriginalFilename();// 获取文件名
        orgName = pressureTestPackagesByOBID.Name() + CommonUtils.getFileName(orgName);
        pressureTestPackageFile.toSetValue(propertyDefinitionType.Name.toString(), orgName);
        pressureTestPackageFile.toSetValue(PackagesUtils.PROPERTY_FILE_PATH, file_url);

        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject createdPressureTestPackageFile = createPressureTestPackageFile(pressureTestPackageFile);
        SchemaUtility.createRelationShip(PackagesUtils.REL_PRESSURE_TEST_PACKAGE_2_FILE
                , pressureTestPackagesByOBID, createdPressureTestPackageFile, false);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return file_url;
    }

    @Override
    public Boolean deleteFile(String pressureTestPackageFileIds) throws Exception {
        boolean result = false;

        IObjectCollection pressureTestPackagesFileByOBIDs = getPressureTestPackagesFilesByOBIDs(pressureTestPackageFileIds);
        List<ObjectDTO> objectDTOS = ObjectDTOUtility.convertToObjectDTOList(pressureTestPackagesFileByOBIDs);
        Iterator<IObject> iObjectIterator = pressureTestPackagesFileByOBIDs.GetEnumerator();
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        while (iObjectIterator.hasNext()) {
            IObject objectDTO = iObjectIterator.next();
            objectDTO.Delete();
        }
        // 数据删除成功后删除文件
        if (!result) {
            for (ObjectDTO pressureTestPackageFile : objectDTOS) {
                String filePath = pressureTestPackageFile.toGetValue(PackagesUtils.PROPERTY_FILE_PATH);
                String[] split = filePath.split("/");
                String bucketName = split[split.length - 2];
                String objectName = split[split.length - 1];
                MinioUtil.removeObject(bucketName, objectName);
            }
        }
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return result;
    }
    /* *****************************************  试压包文件方法 end  ***************************************** */

    /* *****************************************  试压包报告方法 start  ***************************************** */

    /* *****************************************  试压包报告方法 end  ***************************************** */

    /* *****************************************  试压包审批方法 start  ***************************************** */

    /* *****************************************  试压包审批方法 end  ***************************************** */
    /* ******************************************************* 试压包-升版方法 Start ******************************************************* */

    /**
     * 试压包升版处理
     *
     * @param pressureTestPackageOBID
     * @param mode
     * @throws Exception
     */
    @Override
    public void pressureTestPackageRevisionHandler(String pressureTestPackageOBID, PackageRevProcessingMode mode) throws Exception {
        IObject pressureTestPackageByOBID = getPressureTestPackagesByOBID(pressureTestPackageOBID);
        PackageRevisionUtils.packageRevisionHandler(pressureTestPackageByOBID, mode);
    }

    /* ******************************************************* 试压包-升版方法 End ******************************************************* */
}
