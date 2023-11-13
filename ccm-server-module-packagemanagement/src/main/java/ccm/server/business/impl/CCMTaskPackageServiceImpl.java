package ccm.server.business.impl;

import ccm.server.business.*;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
import ccm.server.engine.IQueryEngine;
import ccm.server.entity.ThreadResult;
import ccm.server.enums.*;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.models.query.QueryRequest;
import ccm.server.module.materials.service.IMaterialService;
import ccm.server.params.PageRequest;
import ccm.server.pojo.WorkPackageData;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IInterface;
import ccm.server.utils.*;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/1/11 14:37
 */
@Slf4j
@Service
public class CCMTaskPackageServiceImpl implements ICCMTaskPackageService {

    @Autowired
    private IHierarchyService hierarchyService;

    @Autowired
    private ICCMDocumentService documentService;

    @Autowired
    private ICCMScheduleService scheduleService;

    @Autowired
    private IMaterialService materialService;

    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    /* ******************************************************* 任务包方法 Start ******************************************************* */
    @Override
    public IObject getTaskPackageForm(operationPurpose formPurpose, ObjectDTO mainObject) throws Exception {
        return null;
    }

    @Override
    public IObjectCollection getTaskPackages(PageRequest pageRequest) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_TASK_PACKAGE);
        // 分页
        if (PageUtility.verifyPage(pageRequest)) {
            CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        }
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getTaskPackagesWithItems(ObjectDTO items, PageRequest pageRequest) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_TASK_PACKAGE);
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
    public IObject getTaskPackagesByUID(String uid) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_TASK_PACKAGE);
        CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, operator.equal, uid);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    @Override
    public IObject getTaskPackagesByOBID(String obid) throws Exception {
        if (StringUtils.isBlank(obid)) {
            throw new Exception("任务包OBID不可为空!");
        }
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_TASK_PACKAGE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, obid);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    public IObject getTaskPackagesByName(String name) throws Exception {
        if (StringUtils.isBlank(name)) {
            throw new Exception("任务包名称不可为空!");
        }
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_TASK_PACKAGE);
        CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.equal, name);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    @Override
    public void updateTaskPackage(ObjectDTO toUpdateTaskPackage) throws Exception {
        // 获取已存在的任务包
        IObject existTaskPackage = getTaskPackagesByOBID(toUpdateTaskPackage.getObid());
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        existTaskPackage.BeginUpdate();
        for (ObjectItemDTO item : toUpdateTaskPackage.getItems()) {
            existTaskPackage.setValue(item.getDefUID(), item.toValue());
        }
        existTaskPackage.FinishUpdate();
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public void deleteTaskPackage(String taskPackageOBID) throws Exception {
        IObject existTaskPackage = getTaskPackagesByOBID(taskPackageOBID);
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        existTaskPackage.Delete();
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public IObject createTaskPackage(ObjectDTO toCreateTaskPackage) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject newTaskPackage = SchemaUtility.newIObject(PackagesUtils.CCM_TASK_PACKAGE,
                toCreateTaskPackage.getName(),
                toCreateTaskPackage.getDescription(),
                "", "");

        for (ObjectItemDTO item : toCreateTaskPackage.getItems()) {
            newTaskPackage.setValue(item.getDefUID(), item.getDisplayValue());
        }
        // 结束创建
        newTaskPackage.ClassDefinition().FinishCreate(newTaskPackage);
        // 创建 计划-任务包 关联关系
        if (!StringUtils.isEmpty(newTaskPackage.getValue(BasicPackageObjUtils.PROPERTY_PARENT_PLAN))) {
            IObject scheduleByOBID = scheduleService.getScheduleByOBID(newTaskPackage.getValue(BasicPackageObjUtils.PROPERTY_PARENT_PLAN));
            SchemaUtility.createRelationShip(ScheduleUtils.REL_SCHEDULE_2_TASK_PACKAGE,
                    scheduleByOBID, newTaskPackage, false);
        }
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return newTaskPackage;
    }

    /**
     * 更新任务包状态
     *
     * @param obid
     * @param ttpStatus
     * @throws Exception
     */
    @Override
    public void updateTTPStatus(String obid, String ttpStatus) throws Exception {
        IObject taskPackagesByOBID = getTaskPackagesByOBID(obid);
        ICCMTaskPackage iccmTaskPackage = taskPackagesByOBID.toInterface(ICCMTaskPackage.class);
        iccmTaskPackage.updateTTPStatus(ttpStatus, true);
    }

    /**
     * 更新任务包状态
     *
     * @param uid
     * @param ttpStatus
     * @throws Exception
     */
    @Override
    public void updateTTPStatusByUID(String uid, String ttpStatus) throws Exception {
        IObject taskPackagesByOBID = getTaskPackagesByUID(uid);
        ICCMTaskPackage iccmTaskPackage = taskPackagesByOBID.toInterface(ICCMTaskPackage.class);
        iccmTaskPackage.updateTTPStatus(ttpStatus, true);
    }

    @Override
    public List<IObject> getWorkStepsWithSamePurposeAndConsumeMaterial(IObject taskPackages) throws Exception {
        ICCMTaskPackage iccmTaskPackage = taskPackages.toInterface(ICCMTaskPackage.class);
        String taskPackagePurpose = iccmTaskPackage.getPurpose();
        IObjectCollection workStepsCollection = iccmTaskPackage.getWorkStepsWithoutDeleted();
        List<IObject> workSteps = workStepsCollection.toList();
        return workSteps.stream().filter(w -> {
            String workStepPurpose = w.getProperty("ROPWorkStepTPPhase").Value().toString();
            IWorkStep iWorkStep = w.toInterface(IWorkStep.class);
            // 返回相同施工阶段并且有材料消耗的
            return taskPackagePurpose.equals(workStepPurpose) && iWorkStep.WSConsumeMaterial();
        }).collect(Collectors.toList());
    }

    /* ******************************************************* 任务包方法 End ******************************************************* */
    /* ******************************************************* 任务包-材料方法 Start ******************************************************* */

    /**
     * 获取和任务包相同阶段并且有材料消耗的设计数据
     *
     * @param taskPackageOBID
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection getDesignDataByPurposeAndConsumeMaterial(String taskPackageOBID, String classDefinitionUID, PageRequest pageRequest) throws Exception {
        IObject taskPackagesByOBID = getTaskPackagesByOBID(taskPackageOBID);
        ICCMTaskPackage iccmTaskPackage = taskPackagesByOBID.toInterface(ICCMTaskPackage.class);
        String taskPackagePurpose = iccmTaskPackage.getPurpose();
        IObjectCollection designDataCollection = iccmTaskPackage.getDesignData();
        Iterator<IObject> designDataIterator = designDataCollection.GetEnumerator();

        ObjectCollection designDataConsumeMaterial = new ObjectCollection();
        int pageIndex = pageRequest.getPageIndex();
        int pageSize = pageRequest.getPageSize();
        designDataConsumeMaterial.PageResult().setCurrent(pageIndex);
        designDataConsumeMaterial.PageResult().setSize(pageSize);

        long total = 0L;
        while (designDataIterator.hasNext()) {
            IObject designData = designDataIterator.next();
            if (!designData.ClassDefinitionUID().equalsIgnoreCase(classDefinitionUID)) {
                continue;
            }
            // 设计数据 版本状态
            String designRevisionItemOperationState = String.valueOf(designData.getProperty(propertyDefinitionType.CIMRevisionItemOperationState.toString()).Value());
            // 不计算删除状态的设计数据
            if (operationState.EN_Deleted.toString().equalsIgnoreCase(designRevisionItemOperationState)) {
                continue;
            }
            IObjectCollection workSteps = designData.GetEnd1Relationships().GetRels(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd2s();
            Iterator<IObject> workStepIterator = workSteps.GetEnumerator();
            while (workStepIterator.hasNext()) {
                IObject workStep = workStepIterator.next();
                IWorkStep iWorkStep = workStep.toInterface(IWorkStep.class);
                String ropWorkStepPhase = iWorkStep.getProperty("ROPWorkStepTPPhase").Value().toString();
                if (taskPackagePurpose.equalsIgnoreCase(ropWorkStepPhase) && iWorkStep.WSConsumeMaterial()) {
                    designDataConsumeMaterial.addRangeUniquely(designData);
                    total++;
                }
            }
        }
        designDataConsumeMaterial.PageResult().setTotal(total);
        return designDataConsumeMaterial;
    }

    /**
     * 验证是否存在和任务包相同阶段并且有材料消耗的设计数据
     *
     * @param taskPackageOBID
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection verifyDesignDataByPurposeAndConsumeMaterial(String taskPackageOBID, String classDefinitionUID, PageRequest pageRequest, boolean needConsumeMaterial) throws Exception {
        /*IObject taskPackagesByOBID = getTaskPackagesByOBID(taskPackageOBID);
        ICCMTaskPackage iccmTaskPackage = taskPackagesByOBID.toInterface(ICCMTaskPackage.class);
        List<String> docsUnderTP = iccmTaskPackage.getDocuments().listOfOBID();
        String taskPackagePurpose = iccmTaskPackage.getPurpose();

        IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = queryEngine.start();
        queryEngine.addClassDefForQuery(queryRequest, classDefinitionUID);
        // 任务包下图纸中的设计数据
        queryEngine.addRelOrEdgeDefForQuery(queryRequest, "-" + PackagesUtils.REL_DOCUMENT_2_DESIGN_OBJ,
                "", propertyDefinitionType.OBID.name(), operator.in, String.join(",", docsUnderTP));
        // 非删除状态设计数据
        queryEngine.addPropertyForQuery(queryRequest, "",
                propertyDefinitionType.CIMRevisionItemOperationState.name(), operator.notEqual, operationState.EN_Deleted.name());
        // 和任务包同阶段
        queryEngine.addRelOrEdgeDefForQuery(queryRequest, "+" + relDefinitionType.CCMDesignObj2WorkStep.name(),
                "", propertyDefinitionType.ROPWorkStepTPPhase.name(), operator.equal, taskPackagePurpose);
        // 有材料消耗
        queryEngine.addRelOrEdgeDefForQuery(queryRequest, "+" + relDefinitionType.CCMDesignObj2WorkStep.name(),
                "", propertyDefinitionType.WSConsumeMaterial.name(), operator.equal, "true");
        queryEngine.setPageRequest(queryRequest, pageRequest);

        return queryEngine.query(queryRequest);*/
        IObject taskPackagesByOBID = getTaskPackagesByOBID(taskPackageOBID);
        ICCMTaskPackage iccmTaskPackage = taskPackagesByOBID.toInterface(ICCMTaskPackage.class);
        String taskPackagePurpose = iccmTaskPackage.getPurpose();
        IObjectCollection designDataCollection = iccmTaskPackage.getDesignData();
        Iterator<IObject> designDataIterator = designDataCollection.GetEnumerator();

        ObjectCollection designDataConsumeMaterial = new ObjectCollection();
        int pageIndex = pageRequest.getPageIndex();
        int pageSize = pageRequest.getPageSize();
        designDataConsumeMaterial.PageResult().setCurrent(pageIndex);
        designDataConsumeMaterial.PageResult().setSize(pageSize);

        long total = 0L;
        while (designDataIterator.hasNext()) {
            IObject designData = designDataIterator.next();
            if (!designData.ClassDefinitionUID().equalsIgnoreCase(classDefinitionUID)) {
                continue;
            }
            // 设计数据 版本状态
            String designRevisionItemOperationState = String.valueOf(designData.getProperty(propertyDefinitionType.CIMRevisionItemOperationState.toString()).Value());
            // 不计算删除状态的设计数据
            if (operationState.EN_Deleted.toString().equalsIgnoreCase(designRevisionItemOperationState)) {
                continue;
            }
            IObjectCollection workSteps = designData.GetEnd1Relationships().GetRels(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd2s();
            Iterator<IObject> workStepIterator = workSteps.GetEnumerator();
            while (workStepIterator.hasNext()) {
                IObject workStep = workStepIterator.next();
                IWorkStep iWorkStep = workStep.toInterface(IWorkStep.class);
                String ropWorkStepPhase = iWorkStep.getProperty("ROPWorkStepTPPhase").Value().toString();
                if (taskPackagePurpose.equalsIgnoreCase(ropWorkStepPhase)) {
                    if (needConsumeMaterial) {
                        if (!iWorkStep.WSConsumeMaterial()) {
                            continue;
                        }
                    }
                    designDataConsumeMaterial.addRangeUniquely(designData);
                    total++;
                }
            }
        }
        designDataConsumeMaterial.PageResult().setTotal(total);
        return designDataConsumeMaterial;
    }

    /* ******************************************************* 任务包-材料方法 End ******************************************************* */
    /* ******************************************************* 任务包-图纸方法 Start ******************************************************* */
    @Override
    public void assignDocumentsToTaskPackage(String taskPackageOBID, List<ObjectDTO> providedDocuments) throws Exception {
        IObject taskPackagesByOBID = getTaskPackagesByOBID(taskPackageOBID);
        if (taskPackagesByOBID == null) {
            throw new Exception("查询任务包失败!");
        }
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        for (ObjectDTO providedDocument : providedDocuments) {
            IObject documentByOBID = documentService.getDocumentByOBID(providedDocument.getObid());
            // IRel relationShip = SchemaUtility.createRelationShip(PackagesUtils.REL_TASK_PACKAGE_2_DOCUMENT, taskPackagesByOBID, documentByOBID, false);
            IRel iRel = SchemaUtility.newRelationship(PackagesUtils.REL_TASK_PACKAGE_2_DOCUMENT, taskPackagesByOBID, documentByOBID, true);
            iRel.Interfaces().addDynInterface(interfaceDefinitionType.ICIMRevisionItem.name());
            iRel.ClassDefinition().FinishCreate(iRel);

        }
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public IObjectCollection getRelatedDocuments(String taskPackageOBID) throws Exception {
        IObject taskPackagesByOBID = getTaskPackagesByOBID(taskPackageOBID);
        IInterface iInterface = InterfaceDefUtility.verifyInterface(taskPackagesByOBID, PackagesUtils.I_TASK_PACKAGE);
        ICCMTaskPackage iccmTaskPackage = iInterface.toInterface(ICCMTaskPackage.class);
        return iccmTaskPackage.getDocuments();
    }

    @Override
    public IObject getDocumentForm(operationPurpose formPurpose, ObjectDTO mainObject) throws Exception {
        return null;
    }

    @Override
    public IObjectCollection getRelatedRevisedDocuments(String obid, PageRequest pageRequest) throws Exception {
        IObject taskPackagesByOBID = getTaskPackagesByOBID(obid);
        ICCMTaskPackage iccmTaskPackage = taskPackagesByOBID.toInterface(ICCMTaskPackage.class);
        IObjectCollection documents = iccmTaskPackage.getDocuments();

        ObjectCollection relatedRevisedDocuments = new ObjectCollection();
        int pageIndex = pageRequest.getPageIndex();
        int pageSize = pageRequest.getPageSize();
        relatedRevisedDocuments.PageResult().setCurrent(pageIndex);
        relatedRevisedDocuments.PageResult().setSize(pageSize);

        long total = 0L;

        Iterator<IObject> docIter = documents.GetEnumerator();
        while (docIter.hasNext()) {
            IObject documentObj = docIter.next();
            ICIMRevisionItem icimRevisionItem = documentObj.toInterface(ICIMRevisionItem.class);
            String cimRevisionItemRevState = icimRevisionItem.getCIMRevisionItemRevState();
            // 只获取升版的图纸
            if (revState.EN_Revised.toString().equalsIgnoreCase(cimRevisionItemRevState)) {
                relatedRevisedDocuments.addRangeUniquely(documentObj);
                total++;
            }
        }
        relatedRevisedDocuments.PageResult().setTotal(total);
        return relatedRevisedDocuments;
    }

    @Override
    public IObjectCollection getSelectableDocumentsForTaskPackage(String taskPackageOBID, FiltersParam filtersParam, OrderByParam orderByParam, PageRequest pageRequest) throws Exception {
        IObject taskPackagesByOBID = getTaskPackagesByOBID(taskPackageOBID);
        ICCMTaskPackage iccmTaskPackage = taskPackagesByOBID.toInterface(ICCMTaskPackage.class);
        List<String> relatedDoc = iccmTaskPackage.getDocuments().listOfOBID();
        String taskPackagePurpose = iccmTaskPackage.getPurpose();
        String taskPackageCWA = iccmTaskPackage.getCWA();

        // 排除已经有关联关系的图纸
        IQueryEngine relQuery = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = relQuery.start();
        relQuery.setQueryForRelationship(queryRequest, true);
        relQuery.addPropertyForQuery(queryRequest, "", propertyDefinitionType.RelDefUID.toString(), operator.equal, PackagesUtils.REL_TASK_PACKAGE_2_DOCUMENT);
        IObjectCollection taskPackage2DocumentColl = relQuery.query(queryRequest);
        Iterator<IObject> taskPackage2DocumentIter = taskPackage2DocumentColl.GetEnumerator();
        List<String> notInList = new ArrayList<>();
        while (taskPackage2DocumentIter.hasNext()) {
            IObject rel = taskPackage2DocumentIter.next();
            IRel iRel = rel.toInterface(IRel.class);
            if (relatedDoc.contains(iRel.OBID2())) {
                // 跳过本包中的图纸
                continue;
            }
            IObject relatedTPObj = iRel.GetEnd1();
            ICCMTaskPackage relatedTP = relatedTPObj.toInterface(ICCMTaskPackage.class);
            String relatedTPPurpose = relatedTP.getPurpose();
            if (taskPackagePurpose.equalsIgnoreCase(relatedTPPurpose)) {
                // 相同阶段的任务包 检测是否有施工区域
                if (StringUtils.isBlank(taskPackageCWA)) {
                    // 不存在施工区域的直接根据阶段过滤这个图纸
                    notInList.add(iRel.OBID2());
                } else {
                    // 存在施工区域的判断区域是否一样,一样则过滤
                    if (taskPackageCWA.equalsIgnoreCase(relatedTPPurpose)) {
                        notInList.add(iRel.OBID2());
                    }
                }
            }
        }
        notInList.addAll(relatedDoc);

        // 从工作步骤展开关联关系进行施工阶段和施工区域的过滤
        /*IQueryEngine queryWSEngine = CIMContext.Instance.QueryRequest();
        queryWSEngine.start();
        queryWSEngine.addClassDefForQuery(PackagesUtils.CCM_WORK_STEP);
        queryWSEngine.addPropertyForQuery("", "ROPWorkStepPhase", operator.equal, taskPackagePurpose);
        if (StringUtils.isNotBlank(taskPackageCWA)) {
            // 当有施工区域时进行施工区域过滤
            queryWSEngine.addPropertyForQuery("", "-" + PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP, operator.equal, taskPackageCWA);
        }
        IObjectCollection wsWithTPPurpose = queryWSEngine.query();
        IObjectCollection designWithTPPurpose = wsWithTPPurpose.GetEnd2Relationships().GetRels(PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP).GetEnd1s();
        List<String> inDesignOBIDs = designWithTPPurpose.listOfOBID();

        Map<String, String> filters = filtersParam.getFilters();
        filters.put(propertyDefinitionType.CIMDocState.toString(), "(EN_IFC)");
        filters.put(propertyDefinitionType.OBID.toString(), "!(" + String.join(",", notInList) + ")");

        return schemaBusinessService.generalQuery(DocumentUtils.CCM_DOCUMENT,
                pageRequest.getPageIndex(), pageRequest.getPageSize(),
                orderByParam.getOrderMode(), orderByParam.getDefinitionUIDs(), filters);*/
        Map<String, String> filters = filtersParam.getFilters();
        filters.put(propertyDefinitionType.CIMDocState.toString(), "(EN_IFC)");
        filters.put(propertyDefinitionType.OBID.toString(), "!(" + String.join(",", notInList) + ")");
        return schemaBusinessService.generalQuery(DocumentUtils.CCM_DOCUMENT,
                pageRequest.getPageIndex(), pageRequest.getPageSize(),
                orderByParam.getOrderByWrappers(), filters);

        /*IQueryEngine iQueryEngine = CIMContext.Instance.QueryRequest();
        iQueryEngine.start();
        iQueryEngine.addClassDefForQuery(DocumentUtils.CCM_DOCUMENT);
        iQueryEngine.addPropertyForQuery("", propertyDefinitionType.CIMDocState.toString(), operator.equal, "EN_IFC");
        iQueryEngine.addPropertyForQuery("", propertyDefinitionType.OBID.toString(), operator.notIn, String.join(",", notInList));
        PageUtility.verifyPageAddPageParam(pageRequest, iQueryEngine);
        iQueryEngine.serOrderMode(orderByParam.getOrderMode());
        iQueryEngine.setOrderBy(orderByParam.getDefinitionUIDs());

        return iQueryEngine.query();*/
    }

    @Override
    public Boolean removeDocumentsFromTaskPackage(String packageId, String documentIds) throws Exception {
        IObject workPackagesByOBID = getTaskPackagesByOBID(packageId);
        List<String> toRemoveDocs = Arrays.asList(documentIds.split(","));
        IRelCollection iRelCollection = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_TASK_PACKAGE_2_DOCUMENT);

        if (iRelCollection != null) {
            Iterator<IObject> relObjs = iRelCollection.GetEnumerator();
            SchemaUtility.beginTransaction();
            while (relObjs.hasNext()) {
                IObject relObj = relObjs.next();
                IRel iRel = relObj.toInterface(IRel.class);
                IObject end2Obj = iRel.GetEnd2();
                if (toRemoveDocs.contains(end2Obj.OBID())) {
                    relObj.Delete();
                }
            }
            SchemaUtility.commitTransaction();
        }
        return true;
    }

    /* ******************************************************* 任务包-图纸方法 End ******************************************************* */
    /* ******************************************************* 任务包-树方法 Start ******************************************************* */

    @Override
    public IObject getTaskPackageHierarchyConfigurationForm(operationPurpose formPurpose, ObjectDTO existItemForInfoAndUpdatePurpose) throws Exception {
        return null;
    }

    @Override
    public List<ObjectDTO> getTaskPackageFormPropertiesForConfigurationItem() throws Exception {
        return hierarchyService.getObjectFormPropertiesForConfigurationItem(PackagesUtils.CCM_TASK_PACKAGE);
    }

    @Override
    public Map<String, Object> getTaskPackageHierarchyConfigurationFormWithItem(String formPurpose) throws Exception {
        return hierarchyService.getObjectHierarchyConfigurationFormWithItem(formPurpose, PackagesUtils.CCM_TASK_PACKAGE);
    }

    @Override
    public IObject createTaskPackageHierarchyConfigurationWithItems(JSONObject requestBody) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject hierarchyConfigurationWithItems = hierarchyService.createHierarchyConfigurationWithItems(requestBody, PackagesUtils.CCM_TASK_PACKAGE,false);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return hierarchyConfigurationWithItems;
    }

    @Override
    public void deleteTaskPackageHierarchyConfiguration(String obid) throws Exception {
        hierarchyService.deleteHierarchyConfiguration(obid);
    }

    @Override
    public void updateTaskPackageHierarchyConfiguration(ObjectDTO hierarchyConfiguration) throws Exception {
        hierarchyService.updateHierarchyConfiguration(hierarchyConfiguration);
    }

    @Override
    public IObjectCollection getMyTaskPackageHierarchyConfigurations(HttpServletRequest request, PageRequest pageRequest) throws Exception {
        return hierarchyService.getMyHierarchyConfigurations(request, PackagesUtils.CCM_TASK_PACKAGE, pageRequest);
    }

    @Override
    public IObjectCollection getTaskPackageHierarchyConfigurationItems(String obid) throws Exception {
        return hierarchyService.getItemsByHierarchyConfigurationOBID(obid);
    }

    @Override
    public IObject createTaskPackageHierarchyConfigurationItem(String hierarchyConfigurationOBID, ObjectDTO hierarchyConfigurationItemDTO) throws Exception {
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
    public HierarchyObjectDTO generateHierarchyByTaskPackagesAndConfiguration(String hierarchyConfigurationOBID) throws Exception {
        // 获取 配置项
        ObjectDTO hierarchyConfiguration = hierarchyService
                .getHierarchyConfigurationByClassDefAndOBID(hierarchyConfigurationOBID, PackagesUtils.CCM_TASK_PACKAGE);

        IObjectCollection hierarchyConfigurationItemsCollection = getTaskPackageHierarchyConfigurationItems(hierarchyConfiguration.getObid());
        List<ObjectDTO> itemsDTOs = ObjectDTOUtility.convertToObjectDTOList(hierarchyConfigurationItemsCollection);
        List<ObjectDTO> hierarchyConfigurationItems = HierarchyUtils.sortByHierarchyLevel(itemsDTOs);
        HierarchyObjectDTO rootTree = new HierarchyObjectDTO();
        List<HierarchyObjectDTO> children = rootTree.getChildren();
        // 旧树生成方法
        /*List<ObjectDTO> topEnumListTypes = new ArrayList<>();
        IObjectCollection allTaskPackages = getTaskPackages(new PageRequest(0, 0));
        List<ObjectDTO> allTaskPackageDTOs = ObjectDTOUtility.convertToObjectDTOList(allTaskPackages);
        HierarchyUtils.generateTreeByObjectsAndConfiguration(children, allTaskPackageDTOs, hierarchyConfigurationItems, topEnumListTypes);*/
        // 2022.08.04 HT 替换新树生成方法
        List<String> classDefUIDList = new ArrayList<>();
        classDefUIDList.add(PackagesUtils.CCM_TASK_PACKAGE);
        HierarchyUtils.generateTreeByConfiguration(children, classDefUIDList, hierarchyConfigurationItems);
        return rootTree;
    }

    @Override
    public IObjectCollection getTaskPackagesFromHierarchyNode(HierarchyObjectDTO selectedNode, PageRequest pageRequest) throws Exception {
        List<HierarchyObjectDTO> parents = HierarchyUtils.getParents(new ArrayList<>(), selectedNode);
        ObjectDTO objectDTO = new ObjectDTO();
        for (HierarchyObjectDTO parent : parents) {
            ObjectItemDTO objectItemDTO = new ObjectItemDTO();
            objectItemDTO.setDefUID(parent.getName());
            objectItemDTO.setDisplayValue(parent.getId() + ":" + parent.getName());
            objectDTO.add(objectItemDTO);
        }
        return getTaskPackagesWithItems(objectDTO, pageRequest);
    }
    /* ******************************************************* 任务包-树方法 End ******************************************************* */
    /* ******************************************************* 任务包-父计划方法 Start ******************************************************* */


    @Override
    public Map<String, Object> getTaskPackageFatherPlan(String packageId) throws Exception {
        Map<String, Object> result = new HashMap<>();
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_TASK_PACKAGE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, packageId);
        IObject iObject = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        IObjectCollection iObjectCollection = iObject.GetEnd2Relationships().GetRels(ScheduleUtils.REL_SCHEDULE_2_TASK_PACKAGE).GetEnd1s();
        ObjectDTO plan = null;
        if (iObjectCollection.Size() > 0) {
            IObject planObj = iObjectCollection.get(0);
            plan = planObj.toObjectDTO();
        }
        result.put(PackagesUtils.CCM_TASK_PACKAGE, iObject.toObjectDTO());
        result.put(ScheduleUtils.CCM_SCHEDULE, plan);
        return result;
    }

    @Override
    public Double refreshPlanWeight(String packageOBID) throws Exception {
        IObject taskPackageByOBID = getTaskPackagesByOBID(packageOBID);
        String oldPlannedWeightValue = taskPackageByOBID.getValue(BasicPackageObjUtils.PROPERTY_PLANNED_WEIGHT);

        BigDecimal oldPlannedWeight = StringUtils.isBlank(oldPlannedWeightValue) ? new BigDecimal("0.0") : new BigDecimal(oldPlannedWeightValue);
        ICCMTaskPackage taskPackage = taskPackageByOBID.toInterface(ICCMTaskPackage.class);
        List<IObject> workStepsWithSamePurpose = taskPackage.getWorkStepsWithSamePurpose();
        BigDecimal weight = new BigDecimal("0.0");
        for (IObject workStep : workStepsWithSamePurpose) {
            IWorkStep iWorkStep = workStep.toInterface(IWorkStep.class);
            BigDecimal toAddWeight = BigDecimal.valueOf(iWorkStep.WSWeight());
            weight = NumberUtil.add(weight, toAddWeight);
        }
        if (0 != weight.compareTo(oldPlannedWeight)) {
            SchemaUtility.beginTransaction();
            taskPackageByOBID.BeginUpdate();
            taskPackageByOBID.setValue(BasicPackageObjUtils.PROPERTY_PLANNED_WEIGHT, weight);
            taskPackageByOBID.FinishUpdate();
            SchemaUtility.commitTransaction();
        }
        return weight.doubleValue();
    }

    /**
     * 刷新进度
     *
     * @param packageOBID
     * @return
     * @throws Exception
     */
    @Deprecated
    @Override
    public Double refreshProgress(String packageOBID) throws Exception {
        // 任务包计划权重
        double planWeight = refreshPlanWeight(packageOBID);
        // 详设所有已完成权重
        BigDecimal detailDesignTruncatedAllWeight = new BigDecimal("0.0");
        // JSONArray jsonObject = new JSONArray();
        IObject taskPackageByOBID = getTaskPackagesByOBID(packageOBID);
        ICCMTaskPackage iccmTaskPackage = taskPackageByOBID.toInterface(ICCMTaskPackage.class);
        String taskPackagePurpose = iccmTaskPackage.getPurpose();
        double oldProgress = iccmTaskPackage.getProgress();
        String taskPackageCWA = iccmTaskPackage.getCWA();

        IObjectCollection documents = iccmTaskPackage.getDocuments();
        Iterator<IObject> documentsIterator = documents.GetEnumerator();
        List<String> docNames = new ArrayList<>();
        Map<String, BigDecimal> documentNameToWeight = new HashMap<>();
        while (documentsIterator.hasNext()) {
            IObject detailDocumentObj = documentsIterator.next();
            String name = detailDocumentObj.Name();
            // 获取详设图纸名称
            docNames.add(name);
            // 计算详设图纸各图纸的权重
            BigDecimal detailDesignWeight = new BigDecimal("0.0");
            IObjectCollection designDataColl = detailDocumentObj.GetEnd1Relationships().GetRels(DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd2s();
            Iterator<IObject> designDataIterator = designDataColl.GetEnumerator();
            while (designDataIterator.hasNext()) {
                IObject designData = designDataIterator.next();
                // 设计数据 版本状态
                String designRevisionItemOperationState = String.valueOf(designData.getProperty(propertyDefinitionType.CIMRevisionItemOperationState.toString()).Value());
                // 不计算删除状态的设计数据
                if (operationState.EN_Deleted.toString().equalsIgnoreCase(designRevisionItemOperationState)) {
                    continue;
                }
                if (StringUtils.isNotBlank(taskPackageCWA)) {
                    String designDataCWA = designData.getProperty(ICCMWBSUtils.PROPERTY_CWA).Value() == null ? "" : designData.getProperty(ICCMWBSUtils.PROPERTY_CWA).Value().toString();
                    if (!taskPackageCWA.equalsIgnoreCase(designDataCWA)) {
                        continue;
                    }
                }
                IObjectCollection workStepColl = designData.GetEnd1Relationships().GetRels(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd2s();
                Iterator<IObject> workStepIterator = workStepColl.GetEnumerator();
                while (workStepIterator.hasNext()) {
                    IObject workStepObj = workStepIterator.next();
                    IWorkStep iWorkStep = workStepObj.toInterface(IWorkStep.class);
                    // 不计算删除状态的工作步骤
                    String wsStatus = iWorkStep.WSStatus();
                    if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                        continue;
                    }
                    String workStepPurpose = iWorkStep.getProperty("ROPWorkStepTPPhase").Value().toString();
                    if (taskPackagePurpose.equals(workStepPurpose)) {
                        double toAddWeight = iWorkStep.WSWeight();
                        // 计算当前详设图纸权重
                        detailDesignWeight = NumberUtil.add(detailDesignWeight, toAddWeight);
                    }
                }
            }
            documentNameToWeight.put(name, detailDesignWeight);
        }
        if (docNames.isEmpty()) {
            throw new Exception("未找到任务包下图纸,刷新进度失败!");
        }
        // 获取加设图纸
        IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = queryEngine.start();
        queryEngine.addClassDefForQuery(queryRequest, DocumentUtils.CCM_DOCUMENT);
        queryEngine.addPropertyForQuery(queryRequest, DocumentUtils.I_DOCUMENT, DocumentUtils.PROPERTY_DESIGN_PHASE, operator.equal, "EN_ShopDesign");
        queryEngine.addNameForQuery(queryRequest, operator.in, String.join(",", docNames));
        IObjectCollection shopDesignDocsColl = queryEngine.query(queryRequest);
        Iterator<IObject> shopDesignDocsIterator = shopDesignDocsColl.GetEnumerator();
        while (shopDesignDocsIterator.hasNext()) {
            IObject shopDesignDoc = shopDesignDocsIterator.next();
            // 详设和加设同名的权重
            BigDecimal detailDesignWeightByName = documentNameToWeight.get(shopDesignDoc.Name());
            if (null == detailDesignWeightByName) {
                throw new Exception("通过加设图纸名称获取权重失败!未找到加设图纸在任务包中同名图纸的权重!");
            }
            // 每张图纸加设总权重
            BigDecimal shopDesignAllWeight = new BigDecimal("0.0");
            // 每张图纸加设已完成权重
            BigDecimal truncatedWeight = new BigDecimal("0.0");

            IObjectCollection designDataColl = shopDesignDoc.GetEnd1Relationships().GetRels(DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd2s();
            Iterator<IObject> designDataIterator = designDataColl.GetEnumerator();
            while (designDataIterator.hasNext()) {
                IObject designData = designDataIterator.next();
                // 设计数据 版本状态
                String designRevisionItemOperationState = String.valueOf(designData.getProperty(propertyDefinitionType.CIMRevisionItemOperationState.toString()).Value());
                // 不计算删除状态的设计数据
                if (operationState.EN_Deleted.toString().equalsIgnoreCase(designRevisionItemOperationState)) {
                    continue;
                }
                IObjectCollection workStepColl = designData.GetEnd1Relationships().GetRels(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd2s();
                Iterator<IObject> workStepIterator = workStepColl.GetEnumerator();
                while (workStepIterator.hasNext()) {
                    IObject workStepObj = workStepIterator.next();
                    IWorkStep iWorkStep = workStepObj.toInterface(IWorkStep.class);
                    // 不计算删除状态的工作步骤
                    String wsStatus = iWorkStep.WSStatus();
                    if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                        continue;
                    }
                    String workStepPurpose = iWorkStep.getProperty("ROPWorkStepTPPhase").Value().toString();
                    if (taskPackagePurpose.equals(workStepPurpose)) {
                        double toAddWeight = iWorkStep.WSWeight();
                        // 计算加设总权重
                        shopDesignAllWeight = NumberUtil.add(shopDesignAllWeight, toAddWeight);
                        // 计算加设已完成的权重
                        if (iWorkStep.hasActualCompletedDate()) {
                            truncatedWeight = NumberUtil.add(truncatedWeight, toAddWeight);
                        }
                    }
                }
            }
            // 每张图纸加设进度 = 每张图纸加设已完成权重/每张图纸加设总权重
            BigDecimal shopDesignProgress = NumberUtil.div(truncatedWeight, shopDesignAllWeight);
            // 每张图纸详设已完成权重 = 每张图纸加设进度 * 详设和加设同名的权重
            BigDecimal detailDesignTruncatedWeight = NumberUtil.mul(shopDesignProgress, detailDesignWeightByName);
            // 详设所有已完成权重 = 每张图纸已完成权重累加
            detailDesignTruncatedAllWeight = NumberUtil.add(detailDesignTruncatedAllWeight, detailDesignTruncatedWeight);
            /*JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("图纸名称",shopDesignDoc.Name());
            jsonObject1.put("每张图纸加设总权重",shopDesignAllWeight);
            jsonObject1.put("每张图纸加设已完成权重",truncatedWeight);
            jsonObject1.put("详设和加设同名的权重",detailDesignWeightByName);
            jsonObject.add(jsonObject1);*/

        }

        // 任务包实际进度 详设实际完成权重/详设图纸总权重
        double progress = NumberUtil.div(detailDesignTruncatedAllWeight, planWeight, 4).doubleValue();
        // 计算和原进度不等时更新
        if (0 != NumberUtil.compare(progress, oldProgress)) {
            iccmTaskPackage.updateProgress(progress, true);
        }
        return progress;
    }

    /**
     * 新刷新进度方法
     *
     * @param packageOBID
     * @throws Exception
     */
    @Override
    public Double newRefreshProgress(String packageOBID) throws Exception {
        // 工作包信息
        Map<String, WorkPackageData> workPackageDataMap = new HashMap<>();
        // 任务包计划权重
        double planWeight = refreshPlanWeight(packageOBID);
        // 详设所有已完成权重
        BigDecimal detailDesignTruncatedAllWeight = new BigDecimal("0.0");

        IObject taskPackageByOBID = getTaskPackagesByOBID(packageOBID);
        ICCMTaskPackage iccmTaskPackage = taskPackageByOBID.toInterface(ICCMTaskPackage.class);
        String taskPackagePurpose = iccmTaskPackage.getPurpose();
        double oldProgress = iccmTaskPackage.getProgress();
        String taskPackageCWA = iccmTaskPackage.getCWA();

        IObjectCollection documents = iccmTaskPackage.getDocuments();
        Iterator<IObject> documentsIterator = documents.GetEnumerator();
        List<String> docNames = new ArrayList<>();
        Map<String, BigDecimal> documentNameToWeight = new HashMap<>();
        while (documentsIterator.hasNext()) {
            IObject detailDocumentObj = documentsIterator.next();
            String name = detailDocumentObj.Name();
            // 获取详设图纸名称
            docNames.add(name);
            // 计算详设图纸各图纸的权重
            BigDecimal detailDesignWeight = new BigDecimal("0.0");

            IQueryEngine designDataEngine = CIMContext.Instance.QueryEngine();
            QueryRequest designDataQR = designDataEngine.start();
            designDataEngine.addInterfaceForQuery(designDataQR, DataRetrieveUtils.I_COMPONENT);
            designDataEngine.addRelOrEdgeDefForQuery(designDataQR, "-" + PackagesUtils.REL_DOCUMENT_2_DESIGN_OBJ, "",
                    propertyDefinitionType.OBID.name(), operator.equal, detailDocumentObj.OBID());
            designDataEngine.addPropertyForQuery(designDataQR, "",
                    propertyDefinitionType.CIMRevisionItemOperationState.name(), operator.notEqual, operationState.EN_Deleted.name());
            if (StringUtils.isNotBlank(taskPackageCWA)) {
                designDataEngine.addPropertyForQuery(designDataQR, "",
                        ICCMWBSUtils.PROPERTY_CWA, operator.equal, taskPackageCWA);
            }
            IObjectCollection designDataCollection = designDataEngine.query(designDataQR);
            Iterator<IObject> designDataIterator = designDataCollection.GetEnumerator();

            // 计算详设图纸权重
            while (designDataIterator.hasNext()) {
                IObject designData = designDataIterator.next();
                ThreadResult<BigDecimal> designWeight = getDesignWeight(designData, taskPackagePurpose);
                if (!designWeight.isSuccess()) {
                    throw new RuntimeException(designWeight.getMessage());
                }
                // 累加当前详设图纸的权重
                detailDesignWeight = NumberUtil.add(detailDesignWeight, designWeight.getData());
            }
            // 详设图纸名称及对应设计数据
            documentNameToWeight.put(name, detailDesignWeight);
        }
        if (docNames.isEmpty()) {
            throw new Exception("未找到任务包下图纸,刷新进度失败!");
        }
        // 获取加设图纸
        IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = queryEngine.start();
        queryEngine.addClassDefForQuery(queryRequest, DocumentUtils.CCM_DOCUMENT);
        queryEngine.addPropertyForQuery(queryRequest, DocumentUtils.I_DOCUMENT, DocumentUtils.PROPERTY_DESIGN_PHASE, operator.equal, "EN_ShopDesign");
        queryEngine.addNameForQuery(queryRequest, operator.in, String.join(",", docNames));
        IObjectCollection shopDesignDocsColl = queryEngine.query(queryRequest);
        Iterator<IObject> shopDesignDocsIterator = shopDesignDocsColl.GetEnumerator();
        while (shopDesignDocsIterator.hasNext()) {
            IObject shopDesignDoc = shopDesignDocsIterator.next();
            // 详设和加设同名的权重
            BigDecimal detailDesignWeightByName = documentNameToWeight.get(shopDesignDoc.Name());
            if (null == detailDesignWeightByName) {
                throw new Exception("通过加设图纸名称获取权重失败!未找到加设图纸在任务包中同名图纸的权重!");
            }
            // 每张图纸加设总权重
            BigDecimal shopDesignAllWeight = new BigDecimal("0.0");
            // 每张图纸加设已完成权重
            BigDecimal truncatedWeight = new BigDecimal("0.0");

            IObjectCollection designDataColl = shopDesignDoc.GetEnd1Relationships().GetRels(DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd2s();
            Iterator<IObject> designDataIterator = designDataColl.GetEnumerator();
            while (designDataIterator.hasNext()) {
                IObject designData = designDataIterator.next();
                BigDecimal designTruncatedWeight = new BigDecimal("0.0");
                // 设计数据 版本状态
                String designRevisionItemOperationState = String.valueOf(designData.getProperty(propertyDefinitionType.CIMRevisionItemOperationState.toString()).Value());
                // 不计算删除状态的设计数据
                if (operationState.EN_Deleted.toString().equalsIgnoreCase(designRevisionItemOperationState)) {
                    continue;
                }
                IObjectCollection workStepColl = designData.GetEnd1Relationships().GetRels(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd2s();
                Iterator<IObject> workStepIterator = workStepColl.GetEnumerator();
                while (workStepIterator.hasNext()) {
                    IObject workStepObj = workStepIterator.next();
                    IWorkStep iWorkStep = workStepObj.toInterface(IWorkStep.class);
                    // 不计算删除状态的工作步骤
                    String wsStatus = iWorkStep.WSStatus();
                    if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                        continue;
                    }
                    String workStepPurpose = iWorkStep.getProperty("ROPWorkStepTPPhase").Value().toString();
                    if (taskPackagePurpose.equals(workStepPurpose)) {
                        // 工作步骤权重
                        double toAddWeight = iWorkStep.WSWeight();
                        BigDecimal toAddWeightBigDecimal = new BigDecimal(toAddWeight);
                        // 计算加设总权重
                        shopDesignAllWeight = NumberUtil.add(shopDesignAllWeight, toAddWeightBigDecimal);
                        // 计算加设已完成的权重
                        if (iWorkStep.hasActualCompletedDate()) {
                            // 检查工作步骤是否在工作包中,并且检测工作包是否有修正进度,如果有修正进度则实际完成权重应为修正进度
                            IRel wsWp = workStepObj.GetEnd2Relationships().GetRel(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP);
                            if (null != wsWp) {
                                IObject wpObj = wsWp.GetEnd1();
                                if (null != wpObj) {
                                    WorkPackageData workPackageData = workPackageDataMap.get(wpObj.OBID());
                                    if (null == workPackageData) {
                                        workPackageData = new WorkPackageData(wpObj);
                                        workPackageDataMap.put(wpObj.OBID(), new WorkPackageData(wpObj));
                                    }
                                    workPackageData.hasNull();
                                    // 修正进度不为空并且不为负数的时候计算修正进度
                                    if (null != workPackageData.getEstimatedProgress() && 0 <= workPackageData.getEstimatedProgress().compareTo(BigDecimal.ZERO)) {
                                        BigDecimal estimatedProgress = workPackageData.getEstimatedProgress();

                                        // 如果有修正进度则修正已完成权重根据进度计算
                                        // 修正完成工作项权重 = 计划权重*修正进度/已完成工作项总权重*工作项权重
                                        if (!workPackageData.getInnerWorkStepOBIDs().contains(iWorkStep.OBID())) {
                                            workPackageData.getInnerWorkStepOBIDs().add(iWorkStep.OBID());
                                            workPackageData.setCompletedWeightCount(NumberUtil.add(workPackageData.getCompletedWeightCount(), toAddWeightBigDecimal));
                                            workPackageData.setPlanWeight(NumberUtil.add(workPackageData.getPlanWeight(), toAddWeightBigDecimal));
                                        }
                                        BigDecimal totalEstimatedWeight = NumberUtil.mul(workPackageData.getPlanWeight(), estimatedProgress);
                                        BigDecimal perEstimatedWeight = NumberUtil.div(totalEstimatedWeight, workPackageData.getCompletedWeightCount());
                                        toAddWeightBigDecimal = NumberUtil.mul(perEstimatedWeight, toAddWeightBigDecimal);

                                    }
                                }
                            }
                            // 单个设计数据已完成权重
                            designTruncatedWeight = NumberUtil.add(designTruncatedWeight, toAddWeightBigDecimal);
                        }
                    }
                }
                // 汇总加设已完成权重
                truncatedWeight = NumberUtil.add(truncatedWeight, designTruncatedWeight);
            }
            // 每张图纸加设进度 = 每张图纸加设已完成权重/每张图纸加设总权重
            BigDecimal shopDesignProgress = NumberUtil.div(truncatedWeight, shopDesignAllWeight);
            log.warn("每张图纸加设进度:{}=每张图纸加设已完成权重:{}/每张图纸加设总权重:{}", shopDesignProgress, truncatedWeight, shopDesignAllWeight);
            // 每张图纸详设已完成权重 = 每张图纸加设进度 * 详设和加设同名的权重
            BigDecimal detailDesignTruncatedWeight = NumberUtil.mul(shopDesignProgress, detailDesignWeightByName);
            log.warn("每张图纸详设已完成权重:{}=每张图纸加设进度:{}*详设和加设同名的权重:{}", detailDesignTruncatedWeight, shopDesignProgress, detailDesignWeightByName);
            // 详设所有已完成权重 = 每张图纸已完成权重累加
            detailDesignTruncatedAllWeight = NumberUtil.add(detailDesignTruncatedAllWeight, detailDesignTruncatedWeight);
            log.warn("详设所有已完成权重:{}=已统计图纸已完成权重:{}+当前图纸已完成权重:{}",
                    detailDesignTruncatedAllWeight.toString(), NumberUtil.sub(detailDesignTruncatedAllWeight, detailDesignTruncatedWeight).doubleValue(), detailDesignTruncatedWeight);
        }

        // 任务包实际进度 详设实际完成权重/详设图纸总权重
        double progress = NumberUtil.div(detailDesignTruncatedAllWeight, planWeight, 4).doubleValue();
        log.warn("任务包实际进度:{}=详设实际完成权重:{}/详设图纸总权重:{}", progress, detailDesignTruncatedAllWeight, planWeight);
        // 计算和原进度不等时更新
        if (0 != NumberUtil.compare(progress, oldProgress)) {
            iccmTaskPackage.updateProgress(progress, true);
        }
        return progress;
    }


    /**
     * 获取单个设计数据权重
     *
     * @param designData
     * @param taskPackagePurpose
     * @return
     */
    private ThreadResult<BigDecimal> getDesignWeight(IObject designData, String taskPackagePurpose) {
        ThreadResult<BigDecimal> result = new ThreadResult<>();
        BigDecimal detailDesignWeight = new BigDecimal("0.0");
        IObjectCollection workStepColl = null;
        try {
            IQueryEngine workStepEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = workStepEngine.start();
            workStepEngine.addClassDefForQuery(queryRequest, classDefinitionType.CCMWorkStep.name());
            // 工作步骤不为删除状态
            workStepEngine.addPropertyForQuery(queryRequest, "",
                    propertyDefinitionType.WSStatus.name(), operator.notIn, workStepStatus.EN_RevisedDelete.name() + "," + workStepStatus.EN_ROPDelete.name());
            // 和任务包同阶段
            workStepEngine.addPropertyForQuery(queryRequest, "",
                    propertyDefinitionType.ROPWorkStepTPPhase.name(), operator.equal, taskPackagePurpose);
            // 在设计数据下的
            workStepEngine.addRelOrEdgeDefForQuery(queryRequest, "-" + relDefinitionType.CCMDesignObj2WorkStep.name(),
                    "", propertyDefinitionType.OBID.name(), operator.equal, designData.OBID());
            workStepColl = workStepEngine.query(queryRequest);
        } catch (Exception e) {
            log.error("获取设计数据{}下不为删除状态并且和任务包同阶段的工作步骤失败!", designData.OBID());
            result.errorResult("获取设计数据{}下不为删除状态并且和任务包同阶段的工作步骤失败!", designData.OBID());
        }
        if (null != workStepColl) {
            Iterator<IObject> workStepIterator = workStepColl.GetEnumerator();
            while (workStepIterator.hasNext()) {
                IObject workStepObj = workStepIterator.next();
                IWorkStep iWorkStep = workStepObj.toInterface(IWorkStep.class);
                double toAddWeight = iWorkStep.WSWeight();
                // 计算当前详设图纸权重
                detailDesignWeight = NumberUtil.add(detailDesignWeight, toAddWeight);
            }
            result.successResult(detailDesignWeight);
        }
        return result;
    }

    /* ******************************************************* 任务包-父计划方法End ******************************************************* */
    /* ******************************************************* 任务包-预测预留方法 Start ******************************************************* */

    /**
     * 检测图纸 并预测/预留 获取预测数据
     *
     * @param projectId    项目号
     * @param requestName  任务包名称
     * @param requestType  FR是预测,RR是预留
     * @param searchColumn
     * @param searchValue
     * @throws Exception
     */
    @Override
    public Map<String, Object> existAndCreateNewStatusRequest(String projectId,
                                                              String requestName, String requestType,
                                                              String searchColumn, String searchValue) throws Exception {
        IObject taskPackagesByName = getTaskPackagesByName(requestName);
        ICCMTaskPackage iccmTaskPackage = taskPackagesByName.toInterface(ICCMTaskPackage.class);
        Iterator<IObject> documents = iccmTaskPackage.getDocuments().GetEnumerator();
        List<String> drawingNumberList = new ArrayList<String>();
        while (documents.hasNext()) {
            IObject document = documents.next();
            drawingNumberList.add(document.Name());
        }
        String drawingNumbers = String.join(",", drawingNumberList);
        return materialService.existAndCreateNewStatusRequest(projectId, requestName, requestType, drawingNumbers, searchColumn, searchValue);
    }


    /**
     * 获取包用于预测预留的参数
     *
     * @param packageId
     * @param drawingNumbers
     * @return
     * @throws Exception
     */
    private Map<String, String> getRequestParams(String packageId, String drawingNumbers) throws Exception {
        IObject taskPackagesByName = getTaskPackagesByOBID(packageId);
        ICCMTaskPackage iccmTaskPackage = taskPackagesByName.toInterface(ICCMTaskPackage.class);
        String taskPackagePurpose = iccmTaskPackage.getPurpose();
        String taskPackageCWA = iccmTaskPackage.getCWA();

        Set<String> drawingNumberList = new HashSet<String>();
        List<String> materialCodeList = new ArrayList<String>();
        List<String> pSize1List = new ArrayList<>();
        List<String> pSize2List = new ArrayList<String>();

        //  MaterialCode+PSize1+PSize2 唯一
        List<String> filters = new ArrayList<String>();
        // 添加选择图纸支持
        if (StringUtils.isNotBlank(drawingNumbers)) {
            // 手动选择图纸时 根据包消耗材料进行预测预留
            IQueryEngine documentQueryEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = documentQueryEngine.start();
            documentQueryEngine.addClassDefForQuery(queryRequest, DocumentUtils.CCM_DOCUMENT);
            documentQueryEngine.addOBIDForQuery(queryRequest, operator.in, drawingNumbers);
            IObjectCollection selectDocumentColl = documentQueryEngine.query(queryRequest);
            Iterator<IObject> selectDocumentIter = selectDocumentColl.GetEnumerator();
            while (selectDocumentIter.hasNext()) {
                IObject selectDocument = selectDocumentIter.next();
                IQueryEngine designObjQueryEngine = CIMContext.Instance.QueryEngine();
                QueryRequest queryRequest1 = designObjQueryEngine.start();
                designObjQueryEngine.addInterfaceForQuery(queryRequest1, DataRetrieveUtils.I_COMPONENT);
                designObjQueryEngine.addRelOrEdgeDefForQuery(queryRequest1,
                        "-" + DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ, "", propertyDefinitionType.OBID.toString(), operator.equal, selectDocument.OBID(), ExpansionMode.relatedObject);
                designObjQueryEngine.addPropertyForQuery(queryRequest1,
                        "", propertyDefinitionType.CIMRevisionItemOperationState.toString(), operator.notEqual, operationState.EN_Deleted.toString());
                // 施工区域过滤
                if (StringUtils.isNotBlank(taskPackageCWA)) {
                    designObjQueryEngine.addPropertyForQuery(queryRequest1,
                            ICCMWBSUtils.I_CCM_WBS, ICCMWBSUtils.PROPERTY_CWA, operator.equal, taskPackageCWA);
                }
                IObjectCollection designObjColl = designObjQueryEngine.query(queryRequest1);

                Iterator<IObject> designObjIter = designObjColl.GetEnumerator();
                while (designObjIter.hasNext()) {
                    IObject designObj = designObjIter.next();
                    String materialCode = designObj.getValue("MaterialCode");
                    // 材料编码为空的跳过
                    if (StringUtils.isEmpty(materialCode)) {
                        continue;
                    }
                    String pSize1 = designObj.getValue("PSize1");
                    pSize1 = StringUtils.isBlank(pSize1) ? "0" : pSize1;
                    String pSize2 = designObj.getValue("PSize2");
                    pSize2 = StringUtils.isBlank(pSize2) ? "0" : pSize2;

                    // 通过唯一标识判断过滤
                    String filter = materialCode + pSize1 + pSize2;
                    if (filters.contains(filter)) {
                        continue;
                    }
                    filters.add(filter);

                    IQueryEngine workStepQueryEngine = CIMContext.Instance.QueryEngine();
                    QueryRequest queryRequest2 = workStepQueryEngine.start();
                    workStepQueryEngine.addClassDefForQuery(queryRequest2, "CCMWorkStep");
                    workStepQueryEngine.addRelOrEdgeDefForQuery(queryRequest2,
                            "-" + PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP, "", propertyDefinitionType.OBID.toString(), operator.equal, designObj.OBID(), ExpansionMode.relatedObject);
                    workStepQueryEngine.addPropertyForQuery(queryRequest2,
                            "", propertyDefinitionType.WSStatus.toString(),
                            operator.notIn, workStepStatus.EN_RevisedDelete + "," + workStepStatus.EN_ROPDelete);
                    workStepQueryEngine.addPropertyForQuery(queryRequest2,
                            "", "ROPWorkStepTPPhase", operator.equal, taskPackagePurpose);
                    IObjectCollection workStepColl = workStepQueryEngine.query(queryRequest2);
                    Iterator<IObject> workStepIter = workStepColl.GetEnumerator();
                    while (workStepIter.hasNext()) {
                        IObject workStepObj = workStepIter.next();
                        IWorkStep workStep = workStepObj.toInterface(IWorkStep.class);
                        if (!workStep.WSConsumeMaterial()) {
                            continue;
                        }

                        drawingNumberList.add(selectDocument.Name());
                        materialCodeList.add(materialCode);
                        pSize1List.add(pSize1);
                        pSize2List.add(pSize2);
                    }
                }
            }
        } else {
            List<IObject> workStepsWithSamePurposeAndConsumeMaterial = getWorkStepsWithSamePurposeAndConsumeMaterial(taskPackagesByName);
            for (IObject workStepObj : workStepsWithSamePurposeAndConsumeMaterial) {
                // 获取材料编码
                IObject designDataObj = workStepObj.GetEnd2Relationships().GetRel(PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP).GetEnd1();
                // 设计数据 版本状态
                String designRevisionItemOperationState = String.valueOf(designDataObj.getProperty(propertyDefinitionType.CIMRevisionItemOperationState.toString()).Value());
                // 过滤删除状态的设计数据
                if (operationState.EN_Deleted.toString().equalsIgnoreCase(designRevisionItemOperationState)) {
                    continue;
                }
                IObject documentObj = designDataObj.GetEnd2Relationships().GetRel(DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd1();
                String materialCode = designDataObj.getValue("MaterialCode");
                // 材料编码为空的跳过
                if (StringUtils.isEmpty(materialCode)) {
                    continue;
                }
                String pSize1 = designDataObj.getValue("PSize1");
                pSize1 = StringUtils.isBlank(pSize1) ? "0" : pSize1;
                String pSize2 = designDataObj.getValue("PSize2");
                pSize2 = StringUtils.isBlank(pSize2) ? "0" : pSize2;
                // 通过唯一标识判断过滤
                String filter = materialCode + pSize1 + pSize2;
                if (filters.contains(filter)) {
                    continue;
                }
                filters.add(filter);

                drawingNumberList.add(documentObj.Name());
                materialCodeList.add(materialCode);
                pSize1List.add(pSize1);
                pSize2List.add(pSize2);
            }
        }
        drawingNumbers = String.join(",", drawingNumberList);
        String commodityCodes = String.join(",", materialCodeList);
        String pSize1s = String.join(",", pSize1List);
        String pSize2s = String.join(",", pSize2List);
        String lpAttrValue = taskPackagePurpose.replace("EN_", "").trim();

        Map<String, String> resultParams = new HashMap<>();
        resultParams.put(PackageRequestUtils.DRAWING_NUMBERS, drawingNumbers);
        resultParams.put(PackageRequestUtils.COMMODITY_CODES, commodityCodes);
        resultParams.put(PackageRequestUtils.P_SIZE1S, pSize1s);
        resultParams.put(PackageRequestUtils.P_SIZE2S, pSize2s);
        resultParams.put(PackageRequestUtils.LP_ATTR_VALUE, lpAttrValue);

        return resultParams;
    }


    /**
     * 检测图纸 并部分预测/预留 获取预测数据
     *
     * @param packageId    任务包OBID
     * @param projectId    项目号
     * @param requestName  预测单号
     * @param requestType  FR是预测,RR是预留
     * @param searchColumn
     * @param searchValue
     * @throws Exception
     */
    @Override
    public Map<String, Object> existAndCreatePartialStatusRequest(String packageId, String projectId, String requestName, String requestType,
                                                                  String drawingNumbers, String searchColumn, String searchValue) throws Exception {
        Map<String, String> requestParams = getRequestParams(packageId, drawingNumbers);
        return materialService.existAndCreatePartialStatusRequest(projectId, requestName, requestType,
                requestParams.get(PackageRequestUtils.DRAWING_NUMBERS),
                requestParams.get(PackageRequestUtils.COMMODITY_CODES),
                requestParams.get(PackageRequestUtils.P_SIZE1S),
                requestParams.get(PackageRequestUtils.P_SIZE2S),
                searchColumn, searchValue);
    }

    /**
     * 检测图纸 并部分预测/预留 获取预测数据
     *
     * @param packageId    任务包OBID
     * @param projectId    项目号
     * @param requestName  预测单号
     * @param requestType  FR是预测,RR是预留
     * @param searchColumn
     * @param searchValue
     * @throws Exception
     */
    @Override
    public Map<String, Object> existAndCreatePartialStatusRequest33(String packageId, String projectId, String requestName, String requestType, String warehouses,
                                                                    String drawingNumbers, String searchColumn, String searchValue) throws Exception {
        Map<String, String> requestParams = getRequestParams(packageId, drawingNumbers);
        return materialService.existAndCreatePartialStatusRequest33(projectId, requestName, requestType,
                warehouses,
                requestParams.get(PackageRequestUtils.DRAWING_NUMBERS),
                requestParams.get(PackageRequestUtils.COMMODITY_CODES),
                searchColumn, searchValue);
    }

    /**
     * 按阶段进行材料预测预留
     *
     * @param packageId
     * @param projectId      项目ID
     * @param lpAttrCode     SPM中阶段对于的属性字段
     * @param requestName    TWP编号
     * @param requestType    FR是预测，RR是预留
     * @param drawingNumbers 图纸号集合数组
     * @param searchColumn
     * @param searchValue
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> createFAWithExtraFilter(String packageId, String projectId, String lpAttrCode, String requestName, String requestType, String drawingNumbers, String searchColumn, String searchValue) throws Exception {
        Map<String, String> requestParams = getRequestParams(packageId, drawingNumbers);
        return materialService.existAndCreateFAWithExtraFilter(projectId, requestName, requestType, lpAttrCode,
                requestParams.get(PackageRequestUtils.LP_ATTR_VALUE),
                requestParams.get(PackageRequestUtils.DRAWING_NUMBERS),
                requestParams.get(PackageRequestUtils.COMMODITY_CODES),
                requestParams.get(PackageRequestUtils.P_SIZE1S),
                requestParams.get(PackageRequestUtils.P_SIZE2S),
                searchColumn, searchValue);
    }
    /* ******************************************************* 任务包-预测预留方法 End ******************************************************* */
    /* ******************************************************* 任务包-升版方法 Start ******************************************************* */

    /**
     * 任务包升版处理
     *
     * @param taskPackageOBID
     * @param mode
     * @throws Exception
     */
    @Override
    public void taskPackageRevisionHandler(String taskPackageOBID, PackageRevProcessingMode mode) throws Exception {
        IObject taskPackageByOBID = getTaskPackagesByOBID(taskPackageOBID);
        PackageRevisionUtils.packageRevisionHandler(taskPackageByOBID, mode);
    }
    /* ******************************************************* 任务包-升版方法 End ******************************************************* */
}
