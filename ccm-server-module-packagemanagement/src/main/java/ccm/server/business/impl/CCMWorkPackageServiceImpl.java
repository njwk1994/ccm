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
import ccm.server.module.materials.service.IMaterialService;
import ccm.server.params.PageRequest;
import ccm.server.processors.ThreadsProcessor;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IInterface;
import ccm.server.util.CommonUtility;
import ccm.server.utils.*;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/1/11 14:36
 */
@Slf4j
@Service
public class CCMWorkPackageServiceImpl implements ICCMWorkPackageService {

    @Autowired
    private IHierarchyService hierarchyService;

    @Autowired
    private ICCMDocumentService documentService;

    @Autowired
    private ICCMTaskPackageService taskPackageService;

    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    @Autowired
    private ICCMScheduleService scheduleService;

    @Autowired
    private IMaterialService materialService;

    /* ******************************************************* 工作包方法 Start ******************************************************* */
    @Override
    public ObjectDTO getWorkPackageForm(String formPurpose) throws Exception {
        IObject form = schemaBusinessService.generateForm(PackagesUtils.CCM_WORK_PACKAGE);
        return form.toObjectDTO();
    }

    @Override
    public IObjectCollection getWorkPackages(PageRequest pageRequest) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_WORK_PACKAGE);
        // 分页
        if (PageUtility.verifyPage(pageRequest)) {
            CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        }
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getWorkPackagesWithItems(ObjectDTO items, PageRequest pageRequest) throws Exception {
        return null;
    }

    @Override
    public IObject getWorkPackageByOBID(String obid) throws Exception {
        if (StringUtils.isBlank(obid)) {
            throw new Exception("工作包OBID不可为空!");
        }
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_WORK_PACKAGE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, obid);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    @Override
    public IObject getWorkPackageByUID(String uid) throws Exception {
        if (StringUtils.isBlank(uid)) {
            throw new Exception("工作包uid不可为空!");
        }
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_WORK_PACKAGE);
        CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, operator.equal, uid);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    @Override
    public IObject getWorkPackagesByName(String name) throws Exception {
        if (StringUtils.isBlank(name)) {
            throw new Exception("工作包名称不可为空!");
        }
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_WORK_PACKAGE);
        CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.equal, name);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    @Override
    public void updateWorkPackage(ObjectDTO toUpdateWorkPackage) throws Exception {
        // 获取已存在的工作包
        IObject existWorkPackage = getWorkPackageByOBID(toUpdateWorkPackage.getObid());
        String existParentPlan = existWorkPackage.getValue(BasicPackageObjUtils.PROPERTY_PARENT_PLAN);
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        existWorkPackage.BeginUpdate();
        for (ObjectItemDTO item : toUpdateWorkPackage.getItems()) {
            existWorkPackage.setValue(item.getDefUID(), item.toValue());
        }
        existWorkPackage.FinishUpdate();
        String newParentPlan = existWorkPackage.getValue(BasicPackageObjUtils.PROPERTY_PARENT_PLAN);
        if (!existParentPlan.equals(newParentPlan)) {
            if (!StringUtils.isEmpty(existParentPlan)) {
                IRel iRel = existWorkPackage.GetEnd2Relationships().GetRel(ScheduleUtils.REL_SCHEDULE_2_WORK_PACKAGE);
                iRel.Delete();
            }
            if (!StringUtils.isEmpty(newParentPlan)) {
                IObject newParentPlanObj = scheduleService.getScheduleByOBID(newParentPlan);
                SchemaUtility.createRelationShip(ScheduleUtils.REL_SCHEDULE_2_WORK_PACKAGE, newParentPlanObj, existWorkPackage, false);
            }
        }

        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public void deleteWorkPackage(String workPackageOBID) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_WORK_PACKAGE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, workPackageOBID);
        IObject existWorkPackage = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        existWorkPackage.Delete();
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public IObject createWorkPackage(ObjectDTO toCreateWorkPackage) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject newWorkPackage = SchemaUtility.newIObject(PackagesUtils.CCM_WORK_PACKAGE,
                toCreateWorkPackage.getName(),
                toCreateWorkPackage.getDescription(),
                "", "");
        /*InterfaceDefUtility.addInterface(newWorkPackage,
                PackagesUtils.I_WORK_PACKAGE);*/
        for (ObjectItemDTO item : toCreateWorkPackage.getItems()) {
            newWorkPackage.setValue(item.getDefUID(), item.getDisplayValue());
        }
        // 结束创建
        newWorkPackage.ClassDefinition().FinishCreate(newWorkPackage);
        // 创建 计划-工作包 关联关系
        if (!StringUtils.isEmpty(newWorkPackage.getValue(BasicPackageObjUtils.PROPERTY_PARENT_PLAN))) {
            IObject scheduleByOBID = scheduleService.getScheduleByOBID(newWorkPackage.getValue(BasicPackageObjUtils.PROPERTY_PARENT_PLAN));
            SchemaUtility.createRelationShip(ScheduleUtils.REL_SCHEDULE_2_WORK_PACKAGE,
                    scheduleByOBID, newWorkPackage, false);
        }
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return newWorkPackage;
    }

    @Override
    public IObject createWorkPackageWithRelFromTaskPackage(String taskPackageOBID, ObjectDTO toCreateWorkPackage) throws Exception {

        IObject taskPackagesByOBID = taskPackageService.getTaskPackagesByOBID(taskPackageOBID);

        if (taskPackagesByOBID == null) {
            throw new Exception("未查询到对应任务包!");
        }
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject newWorkPackage = SchemaUtility.newIObject(PackagesUtils.CCM_WORK_PACKAGE,
                toCreateWorkPackage.getName(),
                toCreateWorkPackage.getDescription(),
                "", "");
        for (ObjectItemDTO item : toCreateWorkPackage.getItems()) {
            newWorkPackage.setValue(item.getDefUID(), item.getDisplayValue());
        }
        // 结束创建
        newWorkPackage.ClassDefinition().FinishCreate(newWorkPackage);
        // 创建 任务包-工作包 关联关系
        SchemaUtility.createRelationShip(PackagesUtils.REL_TASK_PACKAGE_2_WORK_PACKAGE, taskPackagesByOBID, newWorkPackage, false);
        // 创建 计划-工作包 关联关系
        if (!StringUtils.isEmpty(newWorkPackage.getValue(BasicPackageObjUtils.PROPERTY_PARENT_PLAN))) {
            IObject scheduleByOBID = scheduleService.getScheduleByOBID(newWorkPackage.getValue(BasicPackageObjUtils.PROPERTY_PARENT_PLAN));
            SchemaUtility.createRelationShip(ScheduleUtils.REL_SCHEDULE_2_WORK_PACKAGE,
                    scheduleByOBID, newWorkPackage, false);
        }
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return newWorkPackage;
    }

    @Override
    public void removeTaskPackage2WorkPackageRel(String taskPackageOBIDs, String workPackageOBID) throws Exception {
        List<String> toRemove = Arrays.asList(taskPackageOBIDs.split(","));
        IObject workPackageByOBID = getWorkPackageByOBID(workPackageOBID);
        IRelCollection iRelCollection = workPackageByOBID.GetEnd2Relationships().GetRels(PackagesUtils.REL_TASK_PACKAGE_2_WORK_PACKAGE);
        Iterator<IObject> iObjectIterator = iRelCollection.GetEnumerator();
        SchemaUtility.beginTransaction();
        while (iObjectIterator.hasNext()) {
            IObject relObj = iObjectIterator.next();
            IRel iRel = relObj.toInterface(IRel.class);
            if (toRemove.contains(iRel.GetEnd1().OBID())) {
                iRel.Delete();
            }
        }
        SchemaUtility.commitTransaction();
    }

    /**
     * 更新工作包状态
     *
     * @param obid
     * @param twpStatus
     * @throws Exception
     */
    @Override
    public void updateTWPStatus(String obid, String twpStatus) throws Exception {
        IObject workPackagesByOBID = getWorkPackageByOBID(obid);
        ICCMWorkPackage iccmWorkPackage = workPackagesByOBID.toInterface(ICCMWorkPackage.class);
        iccmWorkPackage.updateTWPStatus(twpStatus, true);
    }

    @Override
    public void updateTWPStatusByUID(String uid, String twpStatus) throws Exception {
        IObject workPackagesByUID = getWorkPackageByUID(uid);
        ICCMWorkPackage iccmWorkPackage = workPackagesByUID.toInterface(ICCMWorkPackage.class);
        iccmWorkPackage.updateTWPStatus(twpStatus, true);
    }

    /* ******************************************************* 工作包方法 End ******************************************************* */
    /* ******************************************************* 工作包-图纸方法 Start ******************************************************* */
    @Override
    public void assignDocumentsToWorkPackage(String workPackageOBID, List<String> documentOBIDs) throws Exception {
        IObject workPackagesByOBID = getWorkPackageByOBID(workPackageOBID);
        if (workPackagesByOBID == null) {
            throw new Exception("未能获取到工作包:" + workPackageOBID);
        }
        ICCMWorkPackage workPackage = workPackagesByOBID.toInterface(ICCMWorkPackage.class);
        // 获取已存在关联关系
        List<String> relatedDocuments = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_DOCUMENT).GetEnd2s().listOfOBID();
        List<String> relatedDesign = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_DESIGN_OBJ).GetEnd2s().listOfOBID();
        List<String> relatedWorkStep = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP).GetEnd2s().listOfOBID();

        String workPackagePurposeValue = workPackage.getPurpose();
        String workPackageCWA = workPackage.getCWA();

        // 2022.08.17 HT 优化效率,改为一次性查询及部分多线程处理
        if (documentOBIDs.size() > 0) {
            List<String> toCreateRelDocumentOBIDs = new ArrayList<>(documentOBIDs);
            toCreateRelDocumentOBIDs.removeAll(relatedDocuments);
            String documentOBIDStr = String.join(",", documentOBIDs);
            // 获取指定范围图纸i
            log.debug("工作包添加图纸,开始获取指定图纸.");
            long getDocStart = System.currentTimeMillis();
            IObjectCollection documentByOBIDs = documentService.getDocumentByOBIDs(String.join(",", toCreateRelDocumentOBIDs));
            log.debug("工作包添加图纸,获取指定图纸耗时:{}ms", System.currentTimeMillis() - getDocStart);
            // 获取指定范围设计数据
            log.debug("工作包添加图纸,开始获取指定范围设计数据.");
            long getDDStart = System.currentTimeMillis();
            IQueryEngine designObjEngine = CIMContext.Instance.QueryEngine();
            QueryRequest designObjQueryRequest = designObjEngine.start();
            designObjEngine.addInterfaceForQuery(designObjQueryRequest, DataRetrieveUtils.I_COMPONENT);
            // 升版状态不为删除
            designObjEngine.addPropertyForQuery(designObjQueryRequest, "",
                    propertyDefinitionType.CIMRevisionItemOperationState.name(), operator.notEqual, operationState.EN_Deleted.name());
            // 指定范围
            designObjEngine.addRelOrEdgeDefForQuery(designObjQueryRequest, "-" + PackagesUtils.REL_DOCUMENT_2_DESIGN_OBJ, "",
                    propertyDefinitionType.OBID.name(), operator.in, documentOBIDStr);
            // 工作包施工区域不为空时,需要同施工区域
            if (!StringUtils.isEmpty(workPackageCWA)) {
                designObjEngine.addPropertyForQuery(designObjQueryRequest, "",
                        ICCMWBSUtils.PROPERTY_CWA, operator.equal, workPackageCWA);
            }
            IObjectCollection designDataCollection = designObjEngine.query(designObjQueryRequest);
            log.debug("工作包添加图纸,获取指定范围设计数据耗时:{}ms", System.currentTimeMillis() - getDDStart);
            // 获取可关联的设计数据和工作步骤
            log.debug("工作包添加图纸,开始获取可关联的设计数据和工作步骤.");
            long getWSStart = System.currentTimeMillis();
            List<IObject> designDataObjects = designDataCollection.toList();
            List<List<IObject>> splitDesignDataLists = CommonUtility.createList(designDataObjects, ThreadsProcessor.AVAILABLE_PROCESSORS);
            List<Callable<Map<IObject, List<IObject>>>> designAndWsHandlers = new ArrayList<>();
            for (List<IObject> splitDesignDataList : splitDesignDataLists) {
                Callable<Map<IObject, List<IObject>>> designAndWs = () -> handleDesignData(splitDesignDataList, workPackagePurposeValue, relatedWorkStep);
                // 绑定subject到任务上
                Subject subject = ThreadContext.getSubject();
                subject.associateWith(designAndWs);
                designAndWsHandlers.add(designAndWs);
            }
            List<Map<IObject, List<IObject>>> resultDesignDataAndWsList = ThreadsProcessor.Instance.execute(designAndWsHandlers, PackageThreadUtil.execPool);
            log.debug("工作包添加图纸,获取可关联的设计数据和工作步骤耗时:{}ms", System.currentTimeMillis() - getWSStart);


            // 开始进行关联
            log.debug("工作包添加图纸,开始进行关联.");
            long createRel = System.currentTimeMillis();
            SchemaUtility.beginTransaction();
            // 关联图纸
            log.debug("工作包添加图纸,开始关联图纸.");
            long createDocRel = System.currentTimeMillis();
            handleCreateWP2DocRel(workPackagesByOBID, documentByOBIDs);
            log.debug("工作包添加图纸,关联图纸耗时:{}ms", System.currentTimeMillis() - createDocRel);
            // 关联设计数据和工作步骤
            log.debug("工作包添加图纸,开始关联设计数据和工作步骤.");
            long createDDWSRel = System.currentTimeMillis();
            int ddCount = 0;
            int wsCount = 0;
            for (Map<IObject, List<IObject>> resultDesignAndWs : resultDesignDataAndWsList) {
                for (IObject designDataObject : resultDesignAndWs.keySet()) {
                    // 关联设计数据(只做未关联的设计数据和工作包关联)
                    if (!relatedDesign.contains(designDataObject.OBID())) {
                        handleCreateWP2DesignData(workPackagesByOBID, designDataObject);
                        ddCount++;
                    }
                    // 关联工作步骤
                    List<IObject> wsObjects = resultDesignAndWs.get(designDataObject);
                    handleCreateWP2Ws(workPackagesByOBID, wsObjects);
                    wsCount += wsObjects.size();
                }
            }
            log.debug("工作包添加图纸,关联设计数据和工作步骤总耗时:{}ms", System.currentTimeMillis() - createDDWSRel);
            log.debug("工作包添加图纸,开始提交事务.");
            long commit = System.currentTimeMillis();
            SchemaUtility.commitTransaction();
            log.debug("工作包添加图纸,提交事务耗时:{}ms", System.currentTimeMillis() - commit);
            log.warn("工作包添加图纸,关联图纸{}张,关联设计数据{}个,关联工作步骤{}个,进行关联总耗时:{}ms", documentByOBIDs.size(), ddCount, wsCount, System.currentTimeMillis() - createRel);
        }

        /*SchemaUtility.beginTransaction();
        // 开始关联 图纸
        for (String documentOBID : documentOBIDs) {
            IObject documentByOBID = documentService.getDocumentByOBID(documentOBID);
            if (!relatedDocuments.contains(documentByOBID.OBID())) {
                // 当图纸和工作包不存在关联关系时才创建关联关系
                IRel iRel = SchemaUtility.newRelationship(PackagesUtils.REL_WORK_PACKAGE_2_DOCUMENT, workPackagesByOBID, documentByOBID, true);
                iRel.Interfaces().addDynInterface(interfaceDefinitionType.ICIMRevisionItem.name());
                iRel.ClassDefinition().FinishCreate(iRel);
            }
            // 获取图纸下的材料
            IObjectCollection designObjCollection = documentByOBID.GetEnd1Relationships().GetRels(DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd2s();
            Iterator<IObject> designObjIter = designObjCollection.GetEnumerator();
            while (designObjIter.hasNext()) {
                IObject designData = designObjIter.next();
                // 设计数据 版本状态
                String designRevisionItemOperationState = String.valueOf(designData.getProperty(propertyDefinitionType.CIMRevisionItemOperationState.toString()).Value());
                // 不计算删除状态的设计数据
                if (operationState.EN_Deleted.toString().equalsIgnoreCase(designRevisionItemOperationState)) {
                    continue;
                }
                // 当工作包施工区域不为空时 需要根据施工区域过滤
                if (!StringUtils.isEmpty(workPackageCWA)) {
                    // 设计数据施工区域
                    IProperty propertyCWA = designData.getProperty(ICCMWBSUtils.PROPERTY_CWA);
                    if (null == propertyCWA) {
                        throw new Exception("工作包存在施工区域,但未找到设计数据施工区域,请检查数据!");
                    }
                    String designCWAValue = propertyCWA.Value() == null ? "" : propertyCWA.Value().toString();
                    // 当设计数据施工区域与工作包不同时不添加
                    if (!workPackageCWA.equalsIgnoreCase(designCWAValue)) {
                        continue;
                    }
                }
                // 获取设计数据的工作步骤
                IObjectCollection workStepCollection = designData.GetEnd1Relationships().GetRels(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd2s();
                Iterator<IObject> workStepIter = workStepCollection.GetEnumerator();
                List<IObject> addedWS = new ArrayList<>();
                List<Callable<List<IObject>>> handleWorkStepsList = new ArrayList<>();
                while (workStepIter.hasNext()) {
                    IObject workStepObj = workStepIter.next();
                    IWorkStep workStep = workStepObj.toInterface(IWorkStep.class);
//                    // 不计算删除状态的工作步骤
//                    String wsStatus = workStep.WSStatus();
//                    if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
//                        continue;
//                    }
//                    // 工作步骤阶段
//                    String workStepPurpose = workStepObj.getProperty("ROPWorkStepWPPhase").Value() == null ? "" : workStepObj.getProperty("ROPWorkStepWPPhase").Value().toString();
//                    // 只添加相同阶段的工作步骤
//                    if (!workStepPurpose.equals(workPackagePurposeValue)) {
//                        continue;
//                    }
//                    IObjectCollection iObjectCollection = workStepObj.GetEnd2Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP).GetEnd1s();
//                    if (iObjectCollection.size() > 0) {
//                        // 已经关联的忽略
//                        continue;
//                    }
//                    // 当和工作包不存在关联关系时添加工作步骤关联
//                    if (!relatedWorkStep.contains(workStepObj.OBID())) {
//                        IRel iRel = SchemaUtility.newRelationship(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, workPackagesByOBID, workStepObj, true);
//                        iRel.Interfaces().addDynInterface(interfaceDefinitionType.ICIMRevisionItem.name());
//                        iRel.ClassDefinition().FinishCreate(iRel);
//                        addedWS.add(workStepObj);
//                    }
                    // 2022.08.09 HT 添加多线程处理工作步骤
                    Callable<List<IObject>> toAddWs = () -> handleWorkSteps(workStep, workPackagePurposeValue, relatedWorkStep);
                    // 绑定subject到任务上
                    Subject subject = ThreadContext.getSubject();
                    subject.associateWith(toAddWs);
                    handleWorkStepsList.add(toAddWs);
                }
                List<List<IObject>> toAddWs = ThreadsProcessor.Instance.execute(handleWorkStepsList);
                for (List<IObject> wsList : toAddWs) {
                    for (IObject workStepObj : wsList) {
                        IRel iRel = SchemaUtility.newRelationship(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, workPackagesByOBID, workStepObj, true);
                        iRel.Interfaces().addDynInterface(interfaceDefinitionType.ICIMRevisionItem.name());
                        iRel.ClassDefinition().FinishCreate(iRel);
                    }
                    addedWS.addAll(wsList);
                }

                // 当和工作包不存在关联关系时 并且 有工作步骤 添加设计数据关联
                if (!relatedDesign.contains(designData.OBID()) && !addedWS.isEmpty()) {
                    IRel iRel = SchemaUtility.newRelationship(PackagesUtils.REL_WORK_PACKAGE_2_DESIGN_OBJ, workPackagesByOBID, designData, true);
                    iRel.Interfaces().addDynInterface(interfaceDefinitionType.ICIMRevisionItem.name());
                    iRel.ClassDefinition().FinishCreate(iRel);
                }
            }
        }
        SchemaUtility.commitTransaction();*/
    }

    /**
     * 创建图纸和工作包关联关系
     *
     * @param workPackagesByOBID
     * @param documentByOBIDs
     */
    private void handleCreateWP2DocRel(IObject workPackagesByOBID, IObjectCollection documentByOBIDs) throws Exception {
        Iterator<IObject> docIter = documentByOBIDs.GetEnumerator();
        while (docIter.hasNext()) {
            IObject documentByOBID = docIter.next();
            IRel iRel = SchemaUtility.newRelationship(PackagesUtils.REL_WORK_PACKAGE_2_DOCUMENT, workPackagesByOBID, documentByOBID, true);
            //CHEN JING UPDATE 2023/2/17 12:24
            IInterface item = iRel.Interfaces().item(interfaceDefinitionType.ICIMRevisionItem.name(), true);
            if (item == null) {
                throw new Exception("实例化接口:" + interfaceDefinitionType.ICIMRevisionItem.name() + "失败");
            }
            //iRel.Interfaces().addDynInterface(interfaceDefinitionType.ICIMRevisionItem.name());
            iRel.ClassDefinition().FinishCreate(iRel);
        }
    }

    /**
     * 创建设计数据和工作包关联关系
     *
     * @param workPackagesByOBID
     * @param designData
     * @throws Exception
     */
    private void handleCreateWP2DesignData(IObject workPackagesByOBID, IObject designData) throws Exception {
        IRel iRel = SchemaUtility.newRelationship(PackagesUtils.REL_WORK_PACKAGE_2_DESIGN_OBJ, workPackagesByOBID, designData, true);
        //CHEN JING UPDATE 2023/2/17 12:24
        IInterface item = iRel.Interfaces().item(interfaceDefinitionType.ICIMRevisionItem.name(), true);
        if (item == null) {
            throw new Exception("实例化接口:" + interfaceDefinitionType.ICIMRevisionItem.name() + "失败");
        }
        //  iRel.Interfaces().addDynInterface(interfaceDefinitionType.ICIMRevisionItem.name());
        iRel.ClassDefinition().FinishCreate(iRel);
    }

    /**
     * 创建工作步骤和工作包关联关系
     *
     * @param workPackagesByOBID
     * @param wsObjects
     * @throws Exception
     */
    private void handleCreateWP2Ws(IObject workPackagesByOBID, List<IObject> wsObjects) throws Exception {
        for (IObject workStepObj : wsObjects) {
            IRel iRel = SchemaUtility.newRelationship(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, workPackagesByOBID, workStepObj, true);
            //CHEN JING UPDATE 2023/2/17 12:24
            IInterface item = iRel.Interfaces().item(interfaceDefinitionType.ICIMRevisionItem.name(), true);
            if (item == null) {
                throw new Exception("实例化接口:" + interfaceDefinitionType.ICIMRevisionItem.name() + "失败");
            }
           // iRel.Interfaces().addDynInterface(interfaceDefinitionType.ICIMRevisionItem.name());
            iRel.ClassDefinition().FinishCreate(iRel);
        }
    }

    private Map<IObject, List<IObject>> handleDesignData(List<IObject> designDataList, String workPackagePurposeValue, List<String> relatedWorkStep) {
        Map<IObject, List<IObject>> result = new HashMap<>();
        try {
            log.debug("开始处理{}个设计数据", designDataList.size());
            long dd = System.currentTimeMillis();
            int wsCount = 0;
            for (IObject designData : designDataList) {
                List<IObject> wsList = new ArrayList<>();
                IObjectCollection workStepCollection = designData.GetEnd1Relationships().GetRels(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd2s();
                Iterator<IObject> workStepIter = workStepCollection.GetEnumerator();
                List<Callable<List<IObject>>> handleWorkStepsList = new ArrayList<>();
                log.debug("开始处理单个设计数据下工作步骤.");
                long ws = System.currentTimeMillis();
                while (workStepIter.hasNext()) {
                    IObject workStepObj = workStepIter.next();
                    IWorkStep workStep = workStepObj.toInterface(IWorkStep.class);
                    Callable<List<IObject>> toAddWs = () -> handleWorkSteps(workStep, workPackagePurposeValue, relatedWorkStep);
                    // 绑定subject到任务上
                    Subject subject = ThreadContext.getSubject();
                    subject.associateWith(toAddWs);
                    handleWorkStepsList.add(toAddWs);
                }
                List<List<IObject>> toAddWsLists = ThreadsProcessor.Instance.execute(handleWorkStepsList, PackageThreadUtil.execPool);
                for (List<IObject> toAddWs : toAddWsLists) {
                    wsList.addAll(toAddWs);
                }
                if (wsList.size() > 0) {
                    result.put(designData, wsList);
                    wsCount += wsList.size();
                }
                log.debug("单个设计数据下工作步骤处理耗时{}ms.", System.currentTimeMillis() - ws);
            }
            log.debug("处理{}个设计数据耗时{}ms,获取到{}个设计数据,{}个工作步骤.",
                    designDataList.size(), System.currentTimeMillis() - dd, result.keySet().size(), wsCount);
        } catch (Exception exception) {
            log.error("处理设计数据失败!{}", ExceptionUtil.getMessage(exception), ExceptionUtil.getRootCause(exception));
        }
        return result;
    }

    /**
     * 处理工作步骤
     *
     * @param workStep
     * @param workPackagePurposeValue
     * @param relatedWorkStep
     * @return
     */
    private List<IObject> handleWorkSteps(IWorkStep workStep, String workPackagePurposeValue, List<String> relatedWorkStep) {
        List<IObject> result = new ArrayList<>();
        // 不计算删除状态的工作步骤
        String wsStatus = workStep.WSStatus();
        if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
            return result;
        }
        // 工作步骤阶段
        String workStepPurpose = workStep.getProperty("ROPWorkStepWPPhase").Value() == null ? "" : workStep.getProperty("ROPWorkStepWPPhase").Value().toString();
        // 只添加相同阶段的工作步骤
        if (!workStepPurpose.equals(workPackagePurposeValue)) {
            return result;
        }
        IObjectCollection iObjectCollection = null;
        try {
            iObjectCollection = workStep.GetEnd2Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP).GetEnd1s();
        } catch (Exception e) {
            log.error("工作包添加图纸,子线程获取工作步骤关联关系失败");
            return result;
        }
        if (iObjectCollection.size() > 0) {
            // 已经关联的忽略
            return result;
        }
        // 当和工作包不存在关联关系时添加工作步骤关联
        if (!relatedWorkStep.contains(workStep.OBID())) {
            result.add(workStep);
        }
        return result;
    }

    @Override
    public IObjectCollection getRelatedDocuments(String workPackageOBID) throws Exception {
        IObject workPackagesByOBID = getWorkPackageByOBID(workPackageOBID);
        if (workPackagesByOBID == null) {
            throw new Exception("未找到OBID为[" + workPackageOBID + "]的工作包!");
        }
        return workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_DOCUMENT).GetEnd2s();
    }

    @Override
    public IObjectCollection getRelatedRevisedDocuments(String obid, PageRequest pageRequest) throws Exception {
        IObject workPackageByOBID = getWorkPackageByOBID(obid);
        ICCMWorkPackage iccmWorkPackage = workPackageByOBID.toInterface(ICCMWorkPackage.class);
        IObjectCollection documents = iccmWorkPackage.getDocuments();

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
    public IObjectCollection getSelectableDocuments(String obid, FiltersParam filtersParam, OrderByParam orderByParam, PageRequest pageRequest) throws Exception {
        IObjectCollection relatedDocumentsColl = getRelatedDocuments(obid);
        List<String> relatedDocumentOBIDs = relatedDocumentsColl.listOfOBID();
        List<IObject> relatedDocuments = relatedDocumentsColl.toList();
        List<String> relatedDocumentNames = new ArrayList<>();
        for (IObject relatedDocument : relatedDocuments) {
            relatedDocumentNames.add(relatedDocument.Name());
        }

        IObject workPackagesByOBID = getWorkPackageByOBID(obid);

        ICCMWorkPackage iccmWorkPackage = workPackagesByOBID.toInterface(ICCMWorkPackage.class);
        IObjectCollection taskPackages = iccmWorkPackage.getTaskPackages();

        String join = "";
        boolean flag = false;

        if (taskPackages.size() > 0) {// 当存在任务包关联时从任务包图纸中查询
            Iterator<IObject> taskPackageIter = taskPackages.GetEnumerator();
            List<String> documentNames = new ArrayList<>();
            while (taskPackageIter.hasNext()) {
                IObject taskPackageObj = taskPackageIter.next();
                ICCMTaskPackage taskPackage = taskPackageObj.toInterface(ICCMTaskPackage.class);
                IObjectCollection documentsColl = taskPackage.getDocuments();
                List<IObject> documents = documentsColl.toList();
                // 获取所有任务包图纸名称
                for (IObject document : documents) {
                    documentNames.add(document.Name());
                }
            }
            // 去除已添加的图纸OBID
            documentNames.removeAll(relatedDocumentNames);
            join = String.join(",", documentNames);
            if (StringUtils.isNotBlank(join)) {
                // 当任务包有图纸时 查询任务包下同名图纸的加设图纸
                IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
                QueryRequest queryRequest = queryEngine.start();
                queryEngine.addClassDefForQuery(queryRequest, DocumentUtils.CCM_DOCUMENT);
                queryEngine.addPropertyForQuery(queryRequest, DocumentUtils.I_DOCUMENT, DocumentUtils.PROPERTY_DESIGN_PHASE, operator.equal, "EN_ShopDesign");
                queryEngine.addPropertyForQuery(queryRequest, "", propertyDefinitionType.CIMDocState.toString(), operator.equal, "EN_IFC");
                queryEngine.addNameForQuery(queryRequest, operator.in, join);
                IObjectCollection shopDocuments = queryEngine.query(queryRequest);
                List<String> shopDocumentOBIDInTP = shopDocuments.listOfOBID();
                join = String.join(",", shopDocumentOBIDInTP);
            }

            flag = true;
        } else {
            join = String.join(",", relatedDocumentOBIDs);
        }
        if (StringUtils.isBlank(join) && flag) {
            return null;
        }

        /*IQueryEngine queryEngine = CIMContext.Instance.QueryRequest();
        queryEngine.start();
        queryEngine.addClassDefForQuery(DocumentUtils.CCM_DOCUMENT);
        // 工作包获取加设图纸
        queryEngine.addPropertyForQuery(DocumentUtils.I_DOCUMENT, DocumentUtils.PROPERTY_DESIGN_PHASE, operator.equal, "EN_ShopDesign");
        queryEngine.addPropertyForQuery("", propertyDefinitionType.CIMDocState.toString(), operator.equal, "EN_IFC");
        if (flag) {
            queryEngine.addNameForQuery(operator.in, join);
        } else {
            // 当不存在任务包关联时从所有图纸中查询
            queryEngine.addNameForQuery(operator.notIn, join);
        }
        queryEngine.serOrderMode(orderByParam.getOrderMode());
        queryEngine.setOrderBy(orderByParam.getDefinitionUIDs());
        PageUtility.verifyPageAddPageParam(pageRequest, queryEngine);
        return queryEngine.query();*/

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

        if (flag) {
            // 当存在任务包关联时 从任务包图纸对应的加设图纸查询
            filters.put(propertyDefinitionType.OBID.toString(), "(" + join + ")");
        } else {
            // 当不存在任务包关联时从所有图纸中查询 排除已添加图纸
            if (StringUtils.isNotBlank(join)) {
                filters.put(propertyDefinitionType.OBID.toString(), "!(" + join + ")");
            }
        }
        return schemaBusinessService.generalQuery(DocumentUtils.CCM_DOCUMENT, pageRequest.getPageIndex(), pageRequest.getPageSize(), orderByParam.getOrderByWrappers(), filters);
    }

    private static final String TYPE = "TYPE";
    private static final String DATA = "DATA";
    private static final String DESIGN_OBJ = "DESIGN_OBJ";
    private static final String REL = "REL";

    @Override
    public Boolean removeDocumentsFromWorkPackage(String packageId, String documentIds) throws Exception {
        log.debug("开始移除工作包下图纸.");
        long start = System.currentTimeMillis();
        int docCount = 0;
        int designCount = 0;
        int wsCount = 0;

        Subject subject = ThreadContext.getSubject();
        List<Callable<Map<String, Object>>> callables = new ArrayList<>();

        log.debug("开始获取图纸和工作包的关联关系及工作包下的设计数据.");
        long docDDStart = System.currentTimeMillis();
        Callable<Map<String, Object>> getDocInWPRelTask = () -> {
            Map<String, Object> result = new HashMap<>();
            IObjectCollection docInWPRel = getRelObjs(PackagesUtils.REL_WORK_PACKAGE_2_DOCUMENT, packageId, documentIds);
            result.put(TYPE, REL);
            result.put(REL, docInWPRel);
            return result;
        };
        subject.associateWith(getDocInWPRelTask);
        callables.add(getDocInWPRelTask);

        Callable<Map<String, Object>> getDesignInWPTask = () -> {
            Map<String, Object> result = new HashMap<>();
            IQueryEngine wpDesignEngine = CIMContext.Instance.QueryEngine();
            QueryRequest wpDesignQR = wpDesignEngine.start();
            wpDesignEngine.addInterfaceForQuery(wpDesignQR, DataRetrieveUtils.I_COMPONENT);
            wpDesignEngine.addRelOrEdgeDefForQuery(wpDesignQR, "-" + PackagesUtils.REL_WORK_PACKAGE_2_DESIGN_OBJ, "",
                    propertyDefinitionType.OBID.name(), operator.equal, packageId);
            wpDesignEngine.addRelOrEdgeDefForQuery(wpDesignQR, "-" + PackagesUtils.REL_DOCUMENT_2_DESIGN_OBJ, "",
                    propertyDefinitionType.OBID.name(), operator.in, documentIds);
            IObjectCollection designInWP = wpDesignEngine.query(wpDesignQR);
            result.put(TYPE, DESIGN_OBJ);
            result.put(DESIGN_OBJ, designInWP);
            return result;
        };
        subject.associateWith(getDesignInWPTask);
        callables.add(getDesignInWPTask);

        List<Map<String, Object>> result = ThreadsProcessor.Instance.execute(callables, PackageThreadUtil.execPool);
        log.debug("获取图纸和工作包的关联关系及工作包下的设计数据结束,耗时:{}ms.", System.currentTimeMillis() - docDDStart);

        ArrayList<Callable<IObjectCollection>> getDDAndWSTasks = new ArrayList<>();
        SchemaUtility.beginTransaction();
        for (Map<String, Object> dataMap : result) {
            String type = dataMap.get(TYPE).toString();
            Object o = dataMap.get(type);
            if (null == o) {
                continue;
            }
            IObjectCollection objectCollection = (IObjectCollection) o;
            // 如果是关系类型直接删除
            if (REL.equalsIgnoreCase(type)) {
                docCount = objectCollection.size();
                log.debug("开始处理图纸关联关系删除.");
                long docRelStart = System.currentTimeMillis();
                Iterator<IObject> relIter = objectCollection.GetEnumerator();
                while (relIter.hasNext()) {
                    IObject relObj = relIter.next();
                    relObj.Delete();
                }
                log.debug("删除图纸关联关系结束,耗时:{}ms.", System.currentTimeMillis() - docRelStart);
            }
            // 如果是设计数据则进行进一步处理
            if (DESIGN_OBJ.equalsIgnoreCase(type)) {
                designCount = objectCollection.size();
                log.debug("开始处理设计数据及工作步骤相关内容.");
                long designStart = System.currentTimeMillis();
                List<String> designOBIDs = objectCollection.listOfOBID();
                String designOBIDsStr = String.join(",", designOBIDs);

                log.debug("开始获取工作步骤.");
                long wsStart = System.currentTimeMillis();
                IQueryEngine wpWsEngine = CIMContext.Instance.QueryEngine();
                QueryRequest wpWsQR = wpWsEngine.start();
                wpWsEngine.addClassDefForQuery(wpWsQR, PackagesUtils.CCM_WORK_STEP);
                wpWsEngine.addRelOrEdgeDefForQuery(wpWsQR, "-" + PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, "",
                        propertyDefinitionType.OBID.name(), operator.equal, packageId);
                wpWsEngine.addRelOrEdgeDefForQuery(wpWsQR, "-" + PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP, "",
                        propertyDefinitionType.OBID.name(), operator.in, designOBIDsStr);
                IObjectCollection wsInWP = wpWsEngine.query(wpWsQR);
                log.debug("获取工作步骤结束,耗时:{}ms.", System.currentTimeMillis() - wsStart);
                List<String> wsOBIDs = wsInWP.listOfOBID();
                String wsOBIDsStr = String.join(",", wsOBIDs);

                wsCount = wsInWP.size();

                Callable<IObjectCollection> ddTask = () -> getRelObjs(PackagesUtils.REL_WORK_PACKAGE_2_DESIGN_OBJ, packageId, designOBIDsStr);
                Callable<IObjectCollection> wsTask = () -> getRelObjs(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, packageId, wsOBIDsStr);
                subject.associateWith(ddTask);
                subject.associateWith(wsTask);

                getDDAndWSTasks.add(ddTask);
                getDDAndWSTasks.add(wsTask);
                log.debug("设计数据及工作步骤相关内容处理结束,耗时:{}ms.", System.currentTimeMillis() - designStart);
            }

        }
        log.debug("开始获取设计数据及工作步骤和工作包的关联关系.");
        long ddAndWsRel2WP = System.currentTimeMillis();
        List<IObjectCollection> toDeleteDDAndWSList = ThreadsProcessor.Instance.execute(getDDAndWSTasks, PackageThreadUtil.execPool);
        log.debug("获取设计数据及工作步骤和工作包的关联关系结束,耗时:{}ms.", System.currentTimeMillis() - ddAndWsRel2WP);

        log.debug("开始处理设计数据及工作步骤和工作包的关联关系删除.");
        long docRelStart = System.currentTimeMillis();
        for (IObjectCollection toDeleteDDAndWS : toDeleteDDAndWSList) {
            if (null != toDeleteDDAndWS) {
                Iterator<IObject> objectIterator = toDeleteDDAndWS.GetEnumerator();
                while (objectIterator.hasNext()) {
                    IObject relObject = objectIterator.next();
                    relObject.Delete();
                }
            }
        }
        log.debug("删除设计数据及工作步骤和工作包的关联关系结束,耗时:{}ms.", System.currentTimeMillis() - docRelStart);

        log.debug("开始提交事务.");
        long trans = System.currentTimeMillis();
        SchemaUtility.commitTransaction();
        log.debug("提交事务结束,耗时:{}ms.", System.currentTimeMillis() - trans);
        log.warn("移除工作包下图纸结束,耗时:{}ms,移除{}张图纸,{}个设计数据,{}个工作步骤.", System.currentTimeMillis() - start, docCount, designCount, wsCount);

        /*log.info("开始获取工作包下图纸 设计数据 工作步骤 数据");
        long s1 = System.currentTimeMillis();
        IObject workPackagesByOBID = getWorkPackageByOBID(packageId);
        IRelCollection workPackageDocumentRelCollection = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_DOCUMENT);
        IRelCollection workPackageDesignRelCollection = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_DESIGN_OBJ);
        IRelCollection workPackageWorkStepRelCollection = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP);
        log.info("获取工作包下图纸 设计数据 工作步骤 数据耗时:{}ms", System.currentTimeMillis() - s1);

        List<String> toRemoveDocs = Arrays.asList(documentIds.split(","));

        if (workPackageDocumentRelCollection == null) {
            throw new RuntimeException("工作包图纸查询失败");
        }

        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        // 工作包下图纸

        Iterator<IObject> workPackageDocumentRels = workPackageDocumentRelCollection.GetEnumerator();
        log.info("开始处理图纸移出");
        long s2 = System.currentTimeMillis();
        while (workPackageDocumentRels.hasNext()) {
            IObject workPackageDocumentRel = workPackageDocumentRels.next();
            IRel wdRel = workPackageDocumentRel.toInterface(IRel.class);
            IObject document = wdRel.GetEnd2();
            // 当图纸存在工作包下时
            if (toRemoveDocs.contains(document.OBID())) {
                log.info("开始单个图纸处理");
                long s4 = System.currentTimeMillis();
                // 获取图纸下所有的设计数据
                log.info("开始获取图纸下所有的设计数据");
                long s5 = System.currentTimeMillis();
                IObjectCollection designObjCollection = document.GetEnd1Relationships().GetRels(DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd2s();
                log.info("获取图纸下所有的设计数据耗时:{}ms", System.currentTimeMillis() - s5);
                List<String> documentDesignOBIDs = designObjCollection.listOfOBID();
                // 工作包下设计数据
                Iterator<IObject> workPackageDesignRels = workPackageDesignRelCollection.GetEnumerator();
                log.info("开始工作包下设计数据处理");
                long s6 = System.currentTimeMillis();
                List<Callable<Map<String, Object>>> workPackageSingleDesignHandlerCallableList = new ArrayList<>();
                while (workPackageDesignRels.hasNext()) {
                    IObject designWorkPackageRel = workPackageDesignRels.next();
                    IRel ddRel = designWorkPackageRel.toInterface(IRel.class);
                    IObject designUnderWorkPackage = ddRel.GetEnd2();
                    Callable<Map<String, Object>> workPackageSingleDesignHandlerCallable = () -> workPackageSingleDesignHandler(documentDesignOBIDs, designUnderWorkPackage, workPackageWorkStepRelCollection, designWorkPackageRel);
                    // 绑定subject到任务上
                    Subject subject = ThreadContext.getSubject();
                    subject.associateWith(workPackageSingleDesignHandlerCallable);
                    workPackageSingleDesignHandlerCallableList.add(workPackageSingleDesignHandlerCallable);
                }
                List<Map<String, Object>> workPackageSingleDesignHandlerExecute = ThreadsProcessor.Instance.execute(workPackageSingleDesignHandlerCallableList);

                log.info("开始删除多线程返回需要删除的对象");
                long s7 = System.currentTimeMillis();
                for (Map<String, Object> stringObjectMap : workPackageSingleDesignHandlerExecute) {
                    boolean status = (boolean) stringObjectMap.get("status");
                    if (!status) {
                        throw new RuntimeException("工作包下单个设计数据处理存在异常!");
                    }
                    List<IObject> toDeleteObj = (List<IObject>) stringObjectMap.get("objects");
                    for (IObject object : toDeleteObj) {
                        object.Delete();
                    }

                }
                log.info("删除多线程返回需要删除的对象耗时:{}ms", System.currentTimeMillis() - s7);


                log.info("工作包下设计数据处理总耗时:{}ms", System.currentTimeMillis() - s6);
                // 删除工作包关联图纸的关联关系
                log.info("开始删除工作包关联图纸的关联关系");
                long s8 = System.currentTimeMillis();
                workPackageDocumentRel.Delete();
                log.info("删除工作包关联图纸的关联关系耗时:{}ms", System.currentTimeMillis() - s8);
                log.info("单个图纸处理总耗时:{}ms", System.currentTimeMillis() - s4);
            }
        }
        log.info("图纸移出总耗时:{}ms", System.currentTimeMillis() - s2);
        log.info("开始提交事务");
        long s3 = System.currentTimeMillis();
        CIMContext.Instance.Transaction().commit();
        log.info("提交事务耗时:{}ms", System.currentTimeMillis() - s3);*/
        return true;
    }

    private IObjectCollection getRelObjs(String relDefUID, String packageId, String end2Ids) throws Exception {
        IQueryEngine wpDocEngine = CIMContext.Instance.QueryEngine();
        QueryRequest wpDocQR = wpDocEngine.start();
        wpDocEngine.setQueryForRelationship(wpDocQR, true);
        wpDocEngine.addRelDefUidForQuery(wpDocQR, operator.equal, relDefUID);
        wpDocEngine.addPropertyForQuery(wpDocQR, "", propertyDefinitionType.OBID1.name(), operator.equal, packageId);
        wpDocEngine.addPropertyForQuery(wpDocQR, "", propertyDefinitionType.OBID2.name(), operator.in, end2Ids);
        return wpDocEngine.query(wpDocQR);
    }

    private Map<String, Object> workPackageSingleDesignHandler(List<String> documentDesignOBIDs, IObject designUnderWorkPackage, IRelCollection workPackageWorkStepRelCollection, IObject designWorkPackageRel) {
        List<IObject> objects = new ArrayList<IObject>();
        Map<String, Object> result = new HashMap<>();
        result.put("objects", objects);
        result.put("status", true);
        try {
            log.info("开始工作包下单个设计数据处理");
            long s7 = System.currentTimeMillis();
            if (documentDesignOBIDs.contains(designUnderWorkPackage.OBID())) {
                log.info("开始获取设计数据下工作步骤");
                long s12 = System.currentTimeMillis();
                IObjectCollection workStepCollection = designUnderWorkPackage.GetEnd1Relationships().GetRels(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd2s();
                log.info("获取设计数据下工作步骤耗时:{}ms", System.currentTimeMillis() - s12);
                List<String> designWorkStepOBIDs = workStepCollection.listOfOBID();
                // 工作包下工作步骤
                Iterator<IObject> workPackageWorkStepRels = workPackageWorkStepRelCollection.GetEnumerator();
                List<Callable<Map<String, Object>>> workPackageSingleWorkStepHandlerCallableList = new ArrayList<>();
                while (workPackageWorkStepRels.hasNext()) {
                    IObject workPackageWorkStepRel = workPackageWorkStepRels.next();
                    IRel wwRel = workPackageWorkStepRel.toInterface(IRel.class);
                    // 2022.08.10 HT 移除时用户获取异常
                    IObject ws = wwRel.GetEnd2();
                    Callable<Map<String, Object>> workPackageSingleWorkStepHandlerCallable = () -> workPackageSingleWorkStepHandler(wwRel, ws, designWorkStepOBIDs);
                    // 绑定subject到任务上
                    Subject subject = ThreadContext.getSubject();
                    subject.associateWith(workPackageSingleWorkStepHandlerCallable);
                    workPackageSingleWorkStepHandlerCallableList.add(workPackageSingleWorkStepHandlerCallable);
                }
                List<Map<String, Object>> workPackageSingleDesignHandlerExecute = ThreadsProcessor.Instance.execute(workPackageSingleWorkStepHandlerCallableList, PackageThreadUtil.execPool);

                for (Map<String, Object> stringObjectMap : workPackageSingleDesignHandlerExecute) {
                    boolean status = (boolean) stringObjectMap.get("status");
                    if (!status) {
                        throw new RuntimeException("工作包下单个工作步骤处理存在异常!");
                    }
                    List<IObject> toDeleteObj = (List<IObject>) stringObjectMap.get("objects");
                    objects.addAll(toDeleteObj);
                }

                // 删除工作包关联设计数据的关联关系
                log.info("开始删除工作包关联设计数据的关联关系");
                long s9 = System.currentTimeMillis();
                objects.add(designWorkPackageRel);
                // designWorkPackageRel.Delete();
                log.info("删除工作包关联设计数据的关联关系耗时:{}ms", System.currentTimeMillis() - s9);
            }
            log.info("工作包下单个设计数据处理总耗时:{}ms", System.currentTimeMillis() - s7);
        } catch (Exception e) {
            result.put("status", false);
            log.error("工作包下单个设计数据处理存在异常!设计数据信息:OBID-{},ClassDefinitionUID-{};", designUnderWorkPackage.OBID(), designUnderWorkPackage.ClassDefinitionUID(), ExceptionUtil.getRootCause(e));

        }
        return result;
    }

    private Map<String, Object> workPackageSingleWorkStepHandler(IRel wwRel, IObject ws, List<String> designWorkStepOBIDs) {
        List<IObject> objects = new ArrayList<IObject>();
        Map<String, Object> result = new HashMap<>();
        result.put("objects", objects);
        result.put("status", true);
        IObject workPackageWorkStep = null;
        try {
            log.info("开始工作包下单个设计数据的工作步骤处理");
            long s = System.currentTimeMillis();
            workPackageWorkStep = ws;
            log.info("获取对应工作步骤耗时:{}ms", System.currentTimeMillis() - s);
            if (designWorkStepOBIDs.contains(workPackageWorkStep.OBID())) {
                // 删除工作包下工作步骤
                objects.add(wwRel);
                // workPackageWorkStepRel.Delete();
                // 删除状态的工作步骤解除关联时直接删除
                IWorkStep workStep = workPackageWorkStep.toInterface(IWorkStep.class);
                String wsStatus = workStep.WSStatus();
                if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                    objects.add(workStep);
                    // workStep.Delete();
                }
            }
            log.info("工作包下单个设计数据的工作步骤处理耗时:{}ms", System.currentTimeMillis() - s);
        } catch (Exception e) {
            result.put("status", false);
            log.error("工作包下单个设计数据的工作步骤处理存在异常!工作步骤信息:OBID-{},ClassDefinitionUID-{};", workPackageWorkStep != null ? workPackageWorkStep.OBID() : "null", workPackageWorkStep != null ? workPackageWorkStep.ClassDefinitionUID() : "null", ExceptionUtil.getRootCause(e));
        }

        return result;
    }
    /* ******************************************************* 工作包-图纸方法 End ******************************************************* */
    /* *****************************************  工作包材料方法 start  ***************************************** */

    @Override
    public IObjectCollection getRelatedComponents(String workPackageOBID) throws Exception {
        IObject workPackagesByOBID = getWorkPackageByOBID(workPackageOBID);
        IRelCollection iRelCollection = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_DESIGN_OBJ);
        return iRelCollection.GetEnd2s();
    }

    /**
     * 获取和任务包相同阶段并且有材料消耗的设计数据
     *
     * @param workPackageOBID
     * @param classDefinitionUID
     * @param pageRequest
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection getDesignDataByPurposeAndConsumeMaterial(String workPackageOBID, String classDefinitionUID, PageRequest pageRequest) throws Exception {
        IObject workPackagesByOBID = getWorkPackageByOBID(workPackageOBID);
        ICCMWorkPackage iccmWorkPackage = workPackagesByOBID.toInterface(ICCMWorkPackage.class);
        String workPackagePurpose = iccmWorkPackage.getPurpose();
        // 获取工作包下设计数据
        IObjectCollection designDataCollection = iccmWorkPackage.getDesignData();
        List<String> designInWp = designDataCollection.listOfOBID();
        // 获取工作包下工作步骤
        IObjectCollection wsInWpColl = iccmWorkPackage.getWorkStepsWithoutDeleted();
        Iterator<IObject> wsInWpIter = wsInWpColl.GetEnumerator();

        ObjectCollection designDataConsumeMaterial = new ObjectCollection();
        int pageIndex = pageRequest.getPageIndex();
        int pageSize = pageRequest.getPageSize();
        designDataConsumeMaterial.PageResult().setCurrent(pageIndex);
        designDataConsumeMaterial.PageResult().setSize(pageSize);

        long total = 0L;

        while (wsInWpIter.hasNext()) {
            IObject wsInWp = wsInWpIter.next();
            IWorkStep iWorkStep = wsInWp.toInterface(IWorkStep.class);
            // 不计算删除状态的工作步骤
            String wsStatus = iWorkStep.WSStatus();
            if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                continue;
            }
            String ropWorkStepPhase = iWorkStep.getProperty("ROPWorkStepWPPhase").Value().toString();
            // 当工作包下同阶段工作步骤有材料消耗时
            if (workPackagePurpose.equalsIgnoreCase(ropWorkStepPhase) && iWorkStep.WSConsumeMaterial()) {
                IObject designData = iWorkStep.GetEnd2Relationships().GetRel(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd1();
                /*if (!designInWp.contains(designData.OBID())) {
                    throw new Exception("工作步骤所在组件不存在于工作包中!");
                }*/
                // 设计数据 版本状态
                String designRevisionItemOperationState = String.valueOf(designData.getProperty(propertyDefinitionType.CIMRevisionItemOperationState.toString()).Value());
                // 不计算删除状态的设计数据
                if (operationState.EN_Deleted.toString().equalsIgnoreCase(designRevisionItemOperationState)) {
                    continue;
                }
                // 匹配对应的ClassDef
                if (classDefinitionUID.equalsIgnoreCase(designData.ClassDefinition().Name())) {
                    designDataConsumeMaterial.addRangeUniquely(designData);
                    total++;
                }
            }
        }
        designDataConsumeMaterial.PageResult().setTotal(total);
        return designDataConsumeMaterial;
    }

    /**
     * 获取和任务包相同阶段并且有材料消耗的设计数据
     *
     * @param workPackageOBID
     * @param classDefinitionUID
     * @param pageRequest
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection verifyDesignDataByPurposeAndConsumeMaterial(String workPackageOBID, String classDefinitionUID, PageRequest pageRequest, boolean needConsumeMaterial) throws Exception {
        /*IObject workPackagesByOBID = getWorkPackageByOBID(workPackageOBID);
        ICCMWorkPackage iccmWorkPackage = workPackagesByOBID.toInterface(ICCMWorkPackage.class);
        List<String> wsUnderWP = iccmWorkPackage.getWorkSteps().listOfOBID();
        String workPackagePurpose = iccmWorkPackage.getPurpose();

        IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = queryEngine.start();
        queryEngine.addClassDefForQuery(queryRequest, classDefinitionUID);
        // 非删除状态设计数据
        queryEngine.addPropertyForQuery(queryRequest, "",
                propertyDefinitionType.CIMRevisionItemOperationState.name(), operator.notEqual, operationState.EN_Deleted.name());
        queryEngine.addRelOrEdgeDefForQuery(queryRequest, "-" + PackagesUtils.REL_WORK_PACKAGE_2_DESIGN_OBJ,
                "", propertyDefinitionType.OBID.name(), operator.equal, workPackageOBID);
        // 工作包下的工作步骤
        queryEngine.addRelOrEdgeDefForQuery(queryRequest, "+" + relDefinitionType.CCMDesignObj2WorkStep,
                "", propertyDefinitionType.OBID.name(), operator.in, String.join(",", wsUnderWP));
        // 非删除状态工作步骤
        queryEngine.addRelOrEdgeDefForQuery(queryRequest, "+" + relDefinitionType.CCMDesignObj2WorkStep.name(),
                "", propertyDefinitionType.WSStatus.name(), operator.notIn, workStepStatus.EN_RevisedDelete.name() + "," + workStepStatus.EN_ROPDelete.name());
        // 和工作包同阶段
        queryEngine.addRelOrEdgeDefForQuery(queryRequest, "+" + relDefinitionType.CCMDesignObj2WorkStep.name(),
                "", propertyDefinitionType.ROPWorkStepWPPhase.name(), operator.equal, workPackagePurpose);
        // 有材料消耗
        if (needConsumeMaterial) {
            queryEngine.addRelOrEdgeDefForQuery(queryRequest, "+" + relDefinitionType.CCMDesignObj2WorkStep.name(),
                    "", propertyDefinitionType.WSConsumeMaterial.name(), operator.equal, "true");
        }
        queryEngine.setPageRequest(queryRequest, pageRequest);
        return queryEngine.query(queryRequest);*/
        IObject workPackagesByOBID = getWorkPackageByOBID(workPackageOBID);
        ICCMWorkPackage iccmWorkPackage = workPackagesByOBID.toInterface(ICCMWorkPackage.class);
        String workPackagePurpose = iccmWorkPackage.getPurpose();
        // 获取工作包下设计数据
        IObjectCollection designDataCollection = iccmWorkPackage.getDesignData();
        List<String> designInWp = designDataCollection.listOfOBID();
        // 获取工作包下工作步骤
        IObjectCollection wsInWpColl = iccmWorkPackage.getWorkStepsWithoutDeleted();
        Iterator<IObject> wsInWpIter = wsInWpColl.GetEnumerator();

        ObjectCollection designDataConsumeMaterial = new ObjectCollection();
        int pageIndex = pageRequest.getPageIndex();
        int pageSize = pageRequest.getPageSize();
        designDataConsumeMaterial.PageResult().setCurrent(pageIndex);
        designDataConsumeMaterial.PageResult().setSize(pageSize);

        long total = 0L;

        while (wsInWpIter.hasNext()) {
            IObject wsInWp = wsInWpIter.next();
            IWorkStep iWorkStep = wsInWp.toInterface(IWorkStep.class);
            // 不计算删除状态的工作步骤
            String wsStatus = iWorkStep.WSStatus();
            if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                continue;
            }
            String ropWorkStepPhase = iWorkStep.getProperty("ROPWorkStepWPPhase").Value().toString();
            // 当工作包下同阶段工作步骤有材料消耗时
            if (workPackagePurpose.equalsIgnoreCase(ropWorkStepPhase)) {
                if (needConsumeMaterial) {
                    if (!iWorkStep.WSConsumeMaterial()) {
                        continue;
                    }
                }
                IObject designData = iWorkStep.GetEnd2Relationships().GetRel(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd1();
                /*if (!designInWp.contains(designData.OBID())) {
                    throw new Exception("工作步骤所在组件不存在于工作包中!");
                }*/
                // 设计数据 版本状态
                String designRevisionItemOperationState = String.valueOf(designData.getProperty(propertyDefinitionType.CIMRevisionItemOperationState.toString()).Value());
                // 不计算删除状态的设计数据
                if (operationState.EN_Deleted.toString().equalsIgnoreCase(designRevisionItemOperationState)) {
                    continue;
                }
                // 匹配对应的ClassDef
                if (classDefinitionUID.equalsIgnoreCase(designData.ClassDefinition().Name())) {
                    designDataConsumeMaterial.addRangeUniquely(designData);
                    total++;
                }
            }
        }
        designDataConsumeMaterial.PageResult().setTotal(total);
        return designDataConsumeMaterial;
    }

    @Override
    public Boolean removeComponentsUnderWorkPackage(String packageId, String componentIds) throws Exception {
        IObject workPackagesByOBID = getWorkPackageByOBID(packageId);
        IRelCollection iRelCollection = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_DESIGN_OBJ);
        // 工作包下设计数据
        IRelCollection wpWSRelColl = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_DESIGN_OBJ);
        IObjectCollection wpWSColl = wpWSRelColl.GetEnd2s();
        List<String> wpWSOBIDs = wpWSColl.listOfOBID();

        List<String> strings = Arrays.asList(componentIds.split(","));
        if (iRelCollection == null) {
            throw new Exception("未查询到工作包关联关系!");
        }
        Iterator<IObject> relObjs = iRelCollection.GetEnumerator();
        SchemaUtility.beginTransaction();
        while (relObjs.hasNext()) {
            IObject relObj = relObjs.next();
            IRel iRel = relObj.toInterface(IRel.class);
            IObject end2Obj = iRel.GetEnd2();
            if (strings.contains(end2Obj.OBID())) {
                // 设计数据下工作步骤
                IObjectCollection designWSColl = end2Obj.GetEnd1Relationships().GetRels(PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP).GetEnd2s();
                List<String> designWSOBIDs = designWSColl.listOfOBID();
                Iterator<IObject> wpWSRelIter = wpWSRelColl.GetEnumerator();
                while (wpWSRelIter.hasNext()) {
                    IObject wpWSRel = wpWSRelIter.next();
                    IRel iWPWSRel = wpWSRel.toInterface(IRel.class);
                    IObject workPackageWorkStep = iWPWSRel.GetEnd2();
                    if (designWSOBIDs.contains(iWPWSRel.OBID2())) {
                        // 当设计数据下存在 和工作包关联的工作步骤OBID时 删除对应工作步骤
                        wpWSRel.Delete();
                        // 删除状态的工作步骤解除关联时直接删除
                        IWorkStep workStep = workPackageWorkStep.toInterface(IWorkStep.class);
                        String wsStatus = workStep.WSStatus();
                        if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                            workStep.Delete();
                        }
                    }
                }
                relObj.Delete();
            }
        }
        SchemaUtility.commitTransaction();
        return true;
    }
    /* *****************************************  工作包材料方法 end  ***************************************** */
    /* *****************************************  工作包工作步骤方法 start  ***************************************** */

    @Override
    public IObjectCollection getRelatedWorkStep(String workPackageOBID) throws Exception {
        IObject workPackageByOBID = getWorkPackageByOBID(workPackageOBID);
        IRelCollection iRelCollection = workPackageByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP);
        return iRelCollection.GetEnd2s();
    }

    @Override
    public void removeWorkStepUnderWorkPackage(String workPackageOBID, String workStepOBIDs) throws Exception {
        IObject workPackageByOBID = getWorkPackageByOBID(workPackageOBID);
        IRelCollection iRelCollection = workPackageByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP);
        Iterator<IObject> rels = iRelCollection.GetEnumerator();
        List<String> toDeleteWorkSteps = Arrays.asList(workStepOBIDs.split(","));
        SchemaUtility.beginTransaction();
        while (rels.hasNext()) {
            IObject next = rels.next();
            IRel iRel = next.toInterface(IRel.class);
            IObject workStepUnderWP = iRel.GetEnd2();
            if (toDeleteWorkSteps.contains(workStepUnderWP.OBID())) {
                iRel.Delete();
                // 删除状态的工作步骤解除关联时直接删除
                IWorkStep workStep = workStepUnderWP.toInterface(IWorkStep.class);
                String wsStatus = workStep.WSStatus();
                if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                    workStep.Delete();
                }
            }
        }
        SchemaUtility.commitTransaction();
    }

    /**
     * 刷新工作步骤
     *
     * @param packageOBID
     * @return
     * @throws Exception
     */
    @Override
    public void refreshWorkStep(String packageOBID) throws Exception {
        IObject workPackageByOBID = getWorkPackageByOBID(packageOBID);
        ICCMWorkPackage workPackage = workPackageByOBID.toInterface(ICCMWorkPackage.class);
        String workPackagePurpose = workPackage.getPurpose();
        List<String> workStepOBIDsUnderWorkPackage = workPackage.getWorkSteps().listOfOBID();

        IObjectCollection documents = workPackage.getDocuments();
        IObjectCollection designsUnderDocuments = documents.GetEnd1Relationships().GetRels(DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd2s();
        Iterator<IObject> designsUnderDocumentIter = designsUnderDocuments.GetEnumerator();
        while (designsUnderDocumentIter.hasNext()) {
            IObject designData = designsUnderDocumentIter.next();
            // 设计数据 版本状态
            log.debug(designData.OBID() + ":" + designData.Name());
            String designRevisionItemOperationState = String.valueOf(designData.getProperty(propertyDefinitionType.CIMRevisionItemOperationState.toString()).Value());
            // 过滤删除状态的设计数据
            if (operationState.EN_Deleted.toString().equalsIgnoreCase(designRevisionItemOperationState)) {
                designsUnderDocuments.remove(designData);
            }
        }
        IObjectCollection workStepsUnderDesigns = designsUnderDocuments.GetEnd1Relationships().GetRels(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd2s();
        Iterator<IObject> iObjectIterator = workStepsUnderDesigns.GetEnumerator();
        SchemaUtility.beginTransaction();
        while (iObjectIterator.hasNext()) {
            IObject next = iObjectIterator.next();
            IWorkStep workStep = next.toInterface(IWorkStep.class);
            // 不计算删除状态的工作步骤
            String wsStatus = workStep.WSStatus();
            if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                continue;
            }
            String workStepPurpose = workStep.getProperty("ROPWorkStepWPPhase").Value().toString();
            IObjectCollection relatedWp = workStep.GetEnd2Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP).GetEnd1s();
            // 只添加未被关联的工作步骤
            if (!relatedWp.hasValue()) {
                if (!workStepOBIDsUnderWorkPackage.contains(workStep.OBID()) && workPackagePurpose.equals(workStepPurpose)) {
                    IRel iRel = SchemaUtility.newRelationship(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, workPackageByOBID, next, true);
                    //CHEN JING UPDATE 2023/2/17 12:24
                    IInterface item = iRel.Interfaces().item(interfaceDefinitionType.ICIMRevisionItem.name(), true);
                    if (item == null) {
                        throw new Exception("实例化接口:" + interfaceDefinitionType.ICIMRevisionItem.name() + "失败");
                    }
                   // iRel.Interfaces().addDynInterface(interfaceDefinitionType.ICIMRevisionItem.name());
                    iRel.ClassDefinition().FinishCreate(iRel);
                }
            }
        }
        SchemaUtility.commitTransaction();
    }

    /* *****************************************  工作包工作步骤方法 end  ***************************************** */
    /* *****************************************  资源方法 start  ***************************************** */

    @Override
    public ObjectDTO getResourcesForm(String formPurpose) throws Exception {
        IObject form = schemaBusinessService.generateForm(PackagesUtils.CCM_WORK_PACKAGE_RESOURCES);
        return form.toObjectDTO();
    }

    @Override
    public IObjectCollection getRelatedResources(String workPackageOBID) throws Exception {
        IObject workPackagesByOBID = getWorkPackageByOBID(workPackageOBID);
        IRelCollection iRelCollection = workPackagesByOBID.GetEnd1Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_RESOURCES);
        return iRelCollection.GetEnd2s();
    }

    @Override
    public IObject createResources(String packageId, ObjectDTO toCreateResource) throws Exception {
        IObject workPackageByOBID = getWorkPackageByOBID(packageId);
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject newResource = SchemaUtility.newIObject(PackagesUtils.CCM_WORK_PACKAGE_RESOURCES,
                toCreateResource.getName(),
                toCreateResource.getDescription(),
                "", "");
        /*InterfaceDefUtility.addInterface(newResource,PackagesUtils.I_WORK_PACKAGE_RESOURCES);*/
        for (ObjectItemDTO item : toCreateResource.getItems()) {
            newResource.setValue(item.getDefUID(), item.getDisplayValue());
        }
        // 结束创建
        newResource.ClassDefinition().FinishCreate(newResource);
        // 创建关联关系
        SchemaUtility.createRelationShip(PackagesUtils.REL_WORK_PACKAGE_2_RESOURCES,
                workPackageByOBID, newResource, false);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return newResource;
    }

    @Override
    public Boolean deleteResources(String resourcesId) throws Exception {
        // 获取已存在的设计数据
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_WORK_PACKAGE_RESOURCES);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, resourcesId);
        IObject iObject = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        iObject.Delete();
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return true;
    }

    /* *****************************************  资源方法 end  ***************************************** */
    /* ******************************************************* 工作包-树方法 Start ******************************************************* */
    @Override
    public List<ObjectDTO> getWorkPackageFormPropertiesForConfigurationItem() throws Exception {
        return hierarchyService.getObjectFormPropertiesForConfigurationItem(PackagesUtils.CCM_WORK_PACKAGE);
    }

    @Override
    public Map<String, Object> getWorkPackageHierarchyConfigurationFormWithItem(String formPurpose) throws Exception {
        return hierarchyService.getObjectHierarchyConfigurationFormWithItem(formPurpose, PackagesUtils.CCM_WORK_PACKAGE);
    }

    @Override
    public IObject createWorkPackageHierarchyConfigurationWithItems(JSONObject requestBody) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject hierarchyConfigurationWithItems = hierarchyService.createHierarchyConfigurationWithItems(requestBody, PackagesUtils.CCM_WORK_PACKAGE, false);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return hierarchyConfigurationWithItems;

    }

    @Override
    public void deleteWorkPackageHierarchyConfiguration(String obid) throws Exception {
        hierarchyService.deleteHierarchyConfiguration(obid);
    }

    @Override
    public void updateWorkPackageHierarchyConfiguration(ObjectDTO hierarchyConfiguration) throws Exception {
        hierarchyService.updateHierarchyConfiguration(hierarchyConfiguration);

    }

    @Override
    public IObjectCollection getMyWorkPackageHierarchyConfigurations(HttpServletRequest request, PageRequest pageRequest) throws Exception {
        return hierarchyService.getMyHierarchyConfigurations(request, PackagesUtils.CCM_WORK_PACKAGE, pageRequest);

    }

    @Override
    public IObjectCollection getWorkPackageHierarchyConfigurationItems(String obid) throws Exception {
        return hierarchyService.getItemsByHierarchyConfigurationOBID(obid);

    }

    @Override
    public IObject createWorkPackageHierarchyConfigurationItem(String hierarchyConfigurationOBID, ObjectDTO hierarchyConfigurationItemDTO) throws Exception {
        return hierarchyService.createHierarchyConfigurationItemWithConfigurationOBID(hierarchyConfigurationOBID, hierarchyConfigurationItemDTO);

    }

    @Override
    public HierarchyObjectDTO generateHierarchyByWorkPackagesAndConfiguration(String hierarchyConfigurationOBID) throws Exception {
        // 获取 配置项
        ObjectDTO hierarchyConfiguration = hierarchyService.getHierarchyConfigurationByClassDefAndOBID(hierarchyConfigurationOBID, PackagesUtils.CCM_WORK_PACKAGE);

        IObjectCollection hierarchyConfigurationItemsCollection = getWorkPackageHierarchyConfigurationItems(hierarchyConfiguration.getObid());
        List<ObjectDTO> itemsDTOs = ObjectDTOUtility.convertToObjectDTOList(hierarchyConfigurationItemsCollection);
        List<ObjectDTO> hierarchyConfigurationItems = HierarchyUtils.sortByHierarchyLevel(itemsDTOs);
        HierarchyObjectDTO rootTree = new HierarchyObjectDTO();
        List<HierarchyObjectDTO> children = rootTree.getChildren();

        // 旧树生成方法
        /*List<ObjectDTO> topEnumListTypes = new ArrayList<>();
        IObjectCollection allWorkPackages = getWorkPackages(new PageRequest(0, 0));
        List<ObjectDTO> allWorkPackageDTOs = ObjectDTOUtility.convertToObjectDTOList(allWorkPackages);
        HierarchyUtils.generateTreeByObjectsAndConfiguration(children, allWorkPackageDTOs, hierarchyConfigurationItems, topEnumListTypes);*/

        // 2022.08.04 HT 替换新树生成方法
        List<String> classDefUIDList = new ArrayList<>();
        classDefUIDList.add(PackagesUtils.CCM_WORK_PACKAGE);
        HierarchyUtils.generateTreeByConfiguration(children, classDefUIDList, hierarchyConfigurationItems);
        return rootTree;
    }

    @Override
    public IObjectCollection getWorkPackagesFromHierarchyNode(HierarchyObjectDTO selectedNode, PageRequest pageRequest) throws Exception {
        List<HierarchyObjectDTO> parents = HierarchyUtils.getParents(new ArrayList<>(), selectedNode);
        ObjectDTO objectDTO = new ObjectDTO();
        for (HierarchyObjectDTO parent : parents) {
            ObjectItemDTO objectItemDTO = new ObjectItemDTO();
            objectItemDTO.setDefUID(parent.getName());
            objectItemDTO.setDisplayValue(parent.getId() + ":" + parent.getName());
            objectDTO.add(objectItemDTO);
        }
        return getWorkPackagesWithItems(objectDTO, pageRequest);
    }

    /* ******************************************************* 工作包-树方法 End ******************************************************* */
    /* ******************************************************* 工作包-父计划方法 Start ******************************************************* */
    @Override
    public Map<String, Object> getWorkPackageFatherPlan(String packageId) throws Exception {
        Map<String, Object> result = new HashMap<>();
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_WORK_PACKAGE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, packageId);
        IObject iObject = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        IObjectCollection iObjectCollection = iObject.GetEnd2Relationships().GetRels(ScheduleUtils.REL_SCHEDULE_2_WORK_PACKAGE).GetEnd1s();
        ObjectDTO plan = null;
        if (iObjectCollection.Size() > 0) {
            IObject planObj = iObjectCollection.get(0);
            plan = planObj.toObjectDTO();
        }
        result.put(PackagesUtils.CCM_WORK_PACKAGE, iObject.toObjectDTO());
        result.put(ScheduleUtils.CCM_SCHEDULE, plan);
        return result;
    }

    @Override
    public Double refreshPlanWeight(String packageOBID) throws Exception {
        IObject workPackageByOBID = getWorkPackageByOBID(packageOBID);
        ICCMWorkPackage workPackage = workPackageByOBID.toInterface(ICCMWorkPackage.class);
        String oldPlannedWeightValue = workPackageByOBID.getValue(BasicPackageObjUtils.PROPERTY_PLANNED_WEIGHT);
        BigDecimal oldPlannedWeight = StringUtils.isBlank(oldPlannedWeightValue) ? new BigDecimal("0.0") : new BigDecimal(oldPlannedWeightValue);
        IObjectCollection relatedWorkStep = workPackage.getWorkStepsWithoutDeleted();
        Iterator<IObject> iObjectIterator = relatedWorkStep.GetEnumerator();
        BigDecimal weight = new BigDecimal("0.0");
        while (iObjectIterator.hasNext()) {
            IObject next = iObjectIterator.next();
            IWorkStep iWorkStep = next.toInterface(IWorkStep.class);
            // 不计算删除状态的工作步骤
            String wsStatus = iWorkStep.WSStatus();
            if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                continue;
            }
            BigDecimal toAddWeight = BigDecimal.valueOf(iWorkStep.WSWeight());
            weight = NumberUtil.add(weight, toAddWeight);
        }
        if (0 != weight.compareTo(oldPlannedWeight)) {
            SchemaUtility.beginTransaction();
            workPackageByOBID.BeginUpdate();
            workPackageByOBID.setValue(BasicPackageObjUtils.PROPERTY_PLANNED_WEIGHT, weight);
            workPackageByOBID.FinishUpdate();
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
    @Override
    public Double refreshProgress(String packageOBID) throws Exception {
        double planWeight = refreshPlanWeight(packageOBID);
        IObject workPackageByOBID = getWorkPackageByOBID(packageOBID);
        ICCMWorkPackage iccmWorkPackage = workPackageByOBID.toInterface(ICCMWorkPackage.class);
        Double oldProgress = iccmWorkPackage.getProgress();

        List<IObject> workStepsWithSamePurpose = iccmWorkPackage.getWorkStepsWithoutDeleted().toList();
        BigDecimal truncatedWeight = new BigDecimal("0.0");
        for (IObject iObject : workStepsWithSamePurpose) {
            IWorkStep iWorkStep = iObject.toInterface(IWorkStep.class);
            // 不计算删除状态的工作步骤
            String wsStatus = iWorkStep.WSStatus();
            if (wsStatus.equalsIgnoreCase(workStepStatus.EN_RevisedDelete.toString()) || wsStatus.equalsIgnoreCase(workStepStatus.EN_ROPDelete.toString())) {
                continue;
            }
            if (iWorkStep.hasActualCompletedDate()) {
                BigDecimal toAddWeight = BigDecimal.valueOf(iWorkStep.WSWeight());
                truncatedWeight = NumberUtil.add(truncatedWeight, toAddWeight);
            }
        }
        double progress = NumberUtil.div(truncatedWeight, planWeight, 4).doubleValue();
        if (0 != NumberUtil.compare(progress, oldProgress)) {
            iccmWorkPackage.updateProgress(progress, true);
        }
        return progress;
    }

    /* ******************************************************* 工作包-树方法End ******************************************************* */
    /* ******************************************************* 工作包-预测预留方法 Start ******************************************************* */

    /**
     * 检测图纸 并预测/预留 获取预测数据
     *
     * @param projectId    项目号
     * @param requestName  工作包名称
     * @param requestType  FR是预测,RR是预留
     * @param searchColumn
     * @param searchValue
     * @throws Exception
     */
    @Override
    public Map<String, Object> existAndCreateNewStatusRequest(String projectId, String requestName, String requestType,
                                                              String searchColumn, String searchValue) throws Exception {
        IObject workPackagesByName = getWorkPackageByOBID(requestName);
        ICCMWorkPackage iccmWorkPackage = workPackagesByName.toInterface(ICCMWorkPackage.class);
        Iterator<IObject> documents = iccmWorkPackage.getDocuments().GetEnumerator();
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
        IObject workPackagesByName = getWorkPackageByOBID(packageId);
        ICCMWorkPackage iccmWorkPackage = workPackagesByName.toInterface(ICCMWorkPackage.class);
        String workPackageCWA = iccmWorkPackage.getCWA();
        String workPackagePurpose = iccmWorkPackage.getPurpose();

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
            QueryRequest queryRequest2 = documentQueryEngine.start();
            documentQueryEngine.addClassDefForQuery(queryRequest2, DocumentUtils.CCM_DOCUMENT);
            documentQueryEngine.addOBIDForQuery(queryRequest2, operator.in, drawingNumbers);
            IObjectCollection selectDocumentColl = documentQueryEngine.query(queryRequest2);
            Iterator<IObject> selectDocumentIter = selectDocumentColl.GetEnumerator();
            while (selectDocumentIter.hasNext()) {
                IObject selectDocument = selectDocumentIter.next();
                IQueryEngine designObjQueryEngine = CIMContext.Instance.QueryEngine();
                QueryRequest queryRequest = designObjQueryEngine.start();
                designObjQueryEngine.addInterfaceForQuery(queryRequest, DataRetrieveUtils.I_COMPONENT);
                designObjQueryEngine.addRelOrEdgeDefForQuery(queryRequest,
                        "-" + DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ, "", propertyDefinitionType.OBID.toString(), operator.equal, selectDocument.OBID(), ExpansionMode.relatedObject);
                designObjQueryEngine.addPropertyForQuery(queryRequest,
                        "", propertyDefinitionType.CIMRevisionItemOperationState.toString(), operator.notEqual, operationState.EN_Deleted.toString());
                // 施工区域过滤
                if (StringUtils.isNotBlank(workPackageCWA)) {
                    designObjQueryEngine.addPropertyForQuery(queryRequest,
                            ICCMWBSUtils.I_CCM_WBS, ICCMWBSUtils.PROPERTY_CWA, operator.equal, workPackageCWA);
                }
                IObjectCollection designObjColl = designObjQueryEngine.query(queryRequest);

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
                    QueryRequest queryRequest1 = workStepQueryEngine.start();
                    workStepQueryEngine.addClassDefForQuery(queryRequest1, "CCMWorkStep");
                    workStepQueryEngine.addRelOrEdgeDefForQuery(queryRequest1,
                            "-" + PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP, "", propertyDefinitionType.OBID.toString(), operator.equal, designObj.OBID(), ExpansionMode.relatedObject);
                    workStepQueryEngine.addPropertyForQuery(queryRequest1,
                            "", propertyDefinitionType.WSStatus.toString(),
                            operator.notIn, workStepStatus.EN_RevisedDelete + "," + workStepStatus.EN_ROPDelete);
                    workStepQueryEngine.addPropertyForQuery(queryRequest1,
                            "", "ROPWorkStepWPPhase", operator.equal, workPackagePurpose);
                    IObjectCollection workStepColl = workStepQueryEngine.query(queryRequest1);
                    Iterator<IObject> workStepIter = workStepColl.GetEnumerator();
                    while (workStepIter.hasNext()) {
                        IObject workStepObj = workStepIter.next();
                        IRel wp2wsRel = workStepObj.GetEnd2Relationships().GetRel(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP);
                        if (null != wp2wsRel) {
                            IObject relatedWorkPackage = wp2wsRel.GetEnd1();
                            if (!iccmWorkPackage.OBID().equalsIgnoreCase(relatedWorkPackage.OBID())) {
                                continue;
                            }
                        }
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
            IObjectCollection workSteps = iccmWorkPackage.getWorkStepsWithoutDeleted();
            List<IObject> workStepsWithSamePurposeAndConsumeMaterial = workSteps.toList().stream().filter(w -> {
                IWorkStep iWorkStep = w.toInterface(IWorkStep.class);
                // 返回有材料消耗的
                return iWorkStep.WSConsumeMaterial();
            }).collect(Collectors.toList());
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
                // 获取材料编码
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
        String lpAttrValue = workPackagePurpose.replace("EN_", "").trim();

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
     * @param projectId    项目号
     * @param requestName  工作包名称
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
     * 检测图纸 并部分预测/预留 获取预测数据 33
     *
     * @param projectId    项目号
     * @param requestName  工作包名称
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

    /* ******************************************************* 工作包-预测预留方法 End ******************************************************* */

    /* ******************************************************* 工作包-升版方法 Start ******************************************************* */

    /**
     * 工作包升版处理
     *
     * @param workPackageOBID
     * @param mode
     * @throws Exception
     */
    @Override
    public void workPackageRevisionHandler(String workPackageOBID, PackageRevProcessingMode mode) throws Exception {
        IObject workPackageByOBID = getWorkPackageByOBID(workPackageOBID);
        PackageRevisionUtils.packageRevisionHandler(workPackageByOBID, mode);
    }
    /* ******************************************************* 工作包-升版方法 End ******************************************************* */
}
