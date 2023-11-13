package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.enums.*;
import ccm.server.module.context.ROPCache;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.util.CommonUtility;
import ccm.server.util.GeneralUtility;
import ccm.server.util.PerformanceUtility;
import ccm.server.utils.PackagesUtils;
import ccm.server.utils.SchemaUtility;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class IROPExecutableItemBase extends InterfaceDefault implements IROPExecutableItem {
    public IROPExecutableItemBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.IROPExecutableItem.toString(), instantiateRequiredProperties);
    }

    protected void setExistStepStatus(IObjectCollection existSteps) throws Exception {
        if (SchemaUtility.hasValue(existSteps)) {
            Iterator<IObject> iObjectIterator = existSteps.GetEnumerator();
            while (iObjectIterator.hasNext()) {
                IObject lobjWorkStep = iObjectIterator.next();
                IWorkStep iWorkStep = lobjWorkStep.toInterface(IWorkStep.class);
                //没有实际完成时间的,没有关联包的都删除,关联包的,添加标记 ,有实际完成时间的不做任何处理
                if (!iWorkStep.hasActualCompletedDate()) {
                    if (!iWorkStep.hasRelatedWorkPackage()) {
                        iWorkStep.Delete();
                    } else {
                        iWorkStep.BeginUpdate();
                        iWorkStep.setWSStatus(workStepStatus.EN_RevisedDelete.toString());
                        iWorkStep.FinishUpdate();
                    }
                }
            }
        }
    }

    /*
     * @Description: 生成临时步骤
     * @param activePhase 当前阶段
     * @param stepName 步骤名称
     * @param stepStatus 步骤状态
     * @Return: ccm.server.schema.interfaces.IWorkStep
     * @Author: Chen Ning
     * @Date: 2022/5/12 19:35:00
     */
    @Override
    public IWorkStep generateTemporaryWorkStepObject(IEnumEnum activePhase, String stepName, String stepStatus) throws Exception {
        // 生成图纸升版临时处理步骤
        IObject templateWorkStep = SchemaUtility.newIObject(classDefinitionType.CCMWorkStep.toString(), stepName, stepName, null, null);
        assert templateWorkStep != null;
        templateWorkStep.setValue(propertyDefinitionType.WSStatus.toString(), stepStatus);
        templateWorkStep.ClassDefinition().FinishCreate(templateWorkStep);
        // 创建关联关系
        IRel rel = SchemaUtility.createRelationShip(relDefinitionType.CCMDesignObj2WorkStep.toString(), this, templateWorkStep, false);
        rel.ClassDefinition().FinishCreate(rel);
        return templateWorkStep.toInterface(IWorkStep.class);
    }

    private List<String> propertiesNotSyncFromComponent() {
        return new ArrayList<String>() {
            {
                add("ROPWorkStepPhase");
            }
        };
    }

    /*
     * @Description: 同步设计对象属性到工作步骤上
     * @param stepObject 工作步骤对象
     * @param updateOrNot 是都是需要开启BeginUpdate
     * @Return: void
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:56:39
     */
    @Override
    public void syncInfoToStepObject(IWorkStep stepObject, boolean updateOrNot) throws Exception {
        if (stepObject != null) {
            IObjectCollection realizedDefinitionFromDesignObj = CIMContext.Instance.ProcessCache().item(this.ClassDefinitionUID(), domainInfo.SCHEMA.toString()).toInterface(IClassDef.class).getRealizedInterfaceDefs();
            if (realizedDefinitionFromDesignObj != null && realizedDefinitionFromDesignObj.hasValue()) {
                if (updateOrNot) stepObject.BeginUpdate();
                Iterator<IObject> iterator = realizedDefinitionFromDesignObj.GetEnumerator();
                while (iterator.hasNext()) {
                    IInterfaceDef interfaceDef = iterator.next().toInterface(IInterfaceDef.class);
                    if (!interfaceDef.UID().equalsIgnoreCase(interfaceDefinitionType.IObject.toString())) {
                        String interfaceDefinitionUID = interfaceDef.UID();
                        IInterface stepAnInterface = this.Interfaces().item(interfaceDefinitionUID, true);
                        if (stepAnInterface != null) {
                            IInterface objectAnInterface = this.Interfaces().get(interfaceDefinitionUID);
                            if (objectAnInterface != null) {
                                Iterator<Map.Entry<String, IProperty>> e = objectAnInterface.Properties().GetEnumerator();
                                while (e.hasNext()) {
                                    IProperty property = e.next().getValue();
                                    if (!propertiesNotSyncFromComponent().contains(property.getPropertyDefinitionUid())) {
                                        Object value = property.Value();
                                        stepAnInterface.Properties().item(property.getPropertyDefinitionUid(), true).setValue(value);
                                    }
                                }
                            }
                        }
                    }
                }
                if (updateOrNot) stepObject.FinishUpdate();
            }
        }
    }

    /*
     * @Description: 更新状态的ROP生成
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:56:26
     */
    @Override
    public void ropPerformWithUpdateCase() throws Exception {
        IROPRuleGroup ropRuleGroup = this.getAccordWithROPRuleGroups(null);
        if (ropRuleGroup != null) {
            IObjectCollection lcolExistedWorkSteps = this.getWorkSteps();
            IObjectCollection lcolROPSteps = new ObjectCollection();

            IObjectCollection ropWorkSteps = ropRuleGroup.getROPWorkSteps();
            lcolROPSteps.addRangeUniquely(ropWorkSteps);
            Iterator<IObject> iObjectIterator = ropWorkSteps.GetEnumerator();
            while (iObjectIterator.hasNext()) {
                IROPWorkStep ropWorkStep = iObjectIterator.next().toInterface(IROPWorkStep.class);
                this.getWorkStepByROPRule(lcolExistedWorkSteps, ropWorkStep);
            }
        }
    }

    /*
     * @Description: 新增状态的ROP生成
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:56:04
     */
    @Override
    public void ropPerformWithNewCase() throws Exception {
        //1.获取符合的ROP规则组
        IROPRuleGroup accordWithROPRuleGroup = this.getAccordWithROPRuleGroups(null);
        if (accordWithROPRuleGroup != null) {

        }
    }

    /*
     * @Description: 执行ROP删除
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:55:56
     */
    @Override
    public void ropPerformWithRemoveCase() throws Exception {
        SchemaUtility.beginTransaction();
        IObjectCollection workSteps = this.getWorkSteps();
        Iterator<IObject> iObjectIterator = workSteps.GetEnumerator();
        while (iObjectIterator.hasNext()) {
            IObject iObject = iObjectIterator.next();
            IWorkStep iWorkStep = iObject.toInterface(IWorkStep.class);

            // 任务包关联查询
            IObject doc = this.GetEnd2Relationships().GetRel(PackagesUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd1();
            IRel tp2doc = doc.GetEnd2Relationships().GetRel(PackagesUtils.REL_TASK_PACKAGE_2_DOCUMENT);

            // 工作包关联查询
            IRel wp2ws = iObject.GetEnd2Relationships().GetRel(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP);
            if (tp2doc != null || wp2ws != null) {
                // 挂接任务包或工作包的标记为升版删除
                iWorkStep.BeginUpdate();
                iWorkStep.setWSStatus(workStepStatus.EN_ROPDelete.name());
                iWorkStep.FinishUpdate();
                Map<IEnumEnum, List<IObject>> activePhase = getActivePhase();
                Set<IEnumEnum> iEnumEnums = activePhase.keySet();
                TreeSet<IEnumEnum> sortSet = new TreeSet<>(Comparator.comparingInt(IEnumEnum::EnumNumber));
                sortSet.addAll(iEnumEnums);
                // 生成临时步骤
                generateTemporaryWorkStepObject(sortSet.last(), iWorkStep.Name() + "(升版修改)", workStepStatus.EN_RevisedTempProcess.name());
                // 任务包单独处理
                if (tp2doc != null) {
                    if (iWorkStep.WSConsumeMaterial()) {
                        // 如果有材料消耗
                        IObject tpObj = tp2doc.GetEnd1();
                        ICCMTaskPackage iccmTaskPackage = tpObj.toInterface(ICCMTaskPackage.class);
                        iccmTaskPackage.BeginUpdate();
                        // 任务包添加升版提醒
                        iccmTaskPackage.setValue(propertyDefinitionType.CIMRevisionItemRevState.toString(), revState.EN_Revised.name());
                        iccmTaskPackage.FinishUpdate();

                        // 任务包和图纸关联关系上添加升版提醒
                        tp2doc.BeginUpdate();
                        tp2doc.setValue(propertyDefinitionType.CIMRevisionItemRevState.toString(), revState.EN_Revised.name());
                        tp2doc.FinishUpdate();
                    }
                }
                // 工作包单独处理
                if (wp2ws != null) {
                    ICCMWorkPackage iccmWorkPackage = wp2ws.toInterface(ICCMWorkPackage.class);
                    if (!iccmWorkPackage.isClosed()) {
                        iccmWorkPackage.BeginUpdate();
                        // 未关闭的工作包添加升版提醒
                        iccmWorkPackage.setValue(propertyDefinitionType.CIMRevisionItemRevState.toString(), revState.EN_Revised.name());
                        iccmWorkPackage.FinishUpdate();
                        IRel wp2doc = doc.GetEnd2Relationships().GetRel(PackagesUtils.REL_WORK_PACKAGE_2_DOCUMENT);

                        // 工作包和图纸关联关系上添加升版提醒
                        wp2doc.BeginUpdate();
                        wp2doc.setValue(propertyDefinitionType.CIMRevisionItemRevState.toString(), revState.EN_Revised.name());
                        wp2doc.FinishUpdate();
                    }

                }
            } else {
                // 未挂接任务包或工作包的直接删除
                iWorkStep.Delete();
            }
        }
        SchemaUtility.commitTransaction();
    }

    /*
     * @Description: 是否有放行步骤
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:55:38
     */
    @Override
    public boolean hasIssueSteps() throws Exception {
        IObjectCollection workSteps = this.getWorkSteps();
        if (workSteps != null && workSteps.size() > 0) {
            return workSteps.toList(IROPWorkStep.class).stream().anyMatch(IROPWorkStep::ROPWorkStepAllowInd);
        }
        return false;
    }

    /*
     * @Description: 操作状态
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:55:22
     */
    @Override
    public String operateStatus() {
        return this.getValue(propertyDefinitionType.CIMRevisionItemOperationState.toString());
    }

    /*
     * @Description: 升版状态
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:55:22
     */
    @Override
    public String revStatus() {
        return this.getValue(propertyDefinitionType.CIMRevisionItemRevState.toString());
    }

    /*
     * @Description: 是否为新增对象
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:54:53
     */
    @Override
    public boolean isNew() {
        return ccm.server.enums.revState.EN_New.toString().equalsIgnoreCase(revStatus()) && ccm.server.enums.operationState.EN_Created.toString().equalsIgnoreCase(operateStatus());
    }

    /*
     * @Description: 是否为更新升版对象
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:54:53
     */
    @Override
    public boolean isUpdate() {
        return ccm.server.enums.revState.EN_Revised.toString().equalsIgnoreCase(revStatus()) && ccm.server.enums.operationState.EN_Updated.toString().equalsIgnoreCase(operateStatus());
    }

    /*
     * @Description: 是否为删除对象
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:54:53
     */
    @Override
    public boolean isDeleted() {
        return ccm.server.enums.revState.EN_Superseded.toString().equalsIgnoreCase(revStatus()) && ccm.server.enums.operationState.EN_Deleted.toString().equalsIgnoreCase(operateStatus());
    }

    /*
     * @Description:执行ROP
     * @param
     * @Return: void
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:54:22
     */
    @Override
    public void ropPerform() throws Exception {
        if (isNew()) {
            this.ropPerformWithNewCase();
        } else if (isUpdate()) {
            this.ropPerformWithUpdateCase();
        } else if (isDeleted()) {
            this.ropPerformWithRemoveCase();
        }
    }

    /*
     * @Description: 清除升版标记
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:57:43
     */
    @Override
    public void cleanupMarkOfRevise() throws Exception {
        this.BeginUpdate();
        this.setValue(propertyDefinitionType.CIMRevisionItemRevState.toString(), revState.EN_Current.toString());
        this.setValue(propertyDefinitionType.CIMRevisionItemOperationState.toString(), operationState.EN_None.toString());
        this.FinishUpdate();
    }

    /*
     * @Description: 获取设计对象的父对象
     * @param null
     * @return:
     * @Author: Chen Jing
     * @Date: 2022-05-10 11:18:05
     */
    @Override
    public IObject getParentObject() throws Exception {
        return this.GetEnd2Relationships().GetRel(relDefinitionType.CCMDesignObjHierarchy.toString(), false).GetEnd1();
    }

    protected IObject isExistStepShallBeRegenerated(List<IObject> reservedSteps, String stepName) throws Exception {
        if (reservedSteps != null && !StringUtils.isEmpty(stepName)) {
            for (IObject step : reservedSteps) {
                if (step.toInterface(IROPWorkStep.class).getWorkStepDisplayName().equalsIgnoreCase(stepName))
                    return step;
            }
        }
        return null;
    }

    /*
     * @Description: 判断对象的工作步骤中, 是否所有的放行步骤都有完成日期
     * @Return: boolean
     * @Author: Chen Jing
     * @Date: 2022/5/12 09:32:04
     */
    @Override
    public boolean validateAllIssueStepsHasActualCompletedDate() throws Exception {
        Map<IEnumEnum, IObject> issueSteps = this.getIssueSteps(null);
        if (CommonUtility.hasValue(issueSteps)) {
            return issueSteps.values().stream().allMatch(r -> r.toInterface(IWorkStep.class).hasActualCompletedDate());
        }
        return false;
    }

    /*
     * @Description: 获取对象的当前阶段信息
     * @param null
     * @Return: Map<IEnumEnum, List<IObject>> Key:阶段  Value:阶段下的步骤信息
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:36:00
     */
    @Override
    public Map<IEnumEnum, List<IObject>> getActivePhase() throws Exception {
        Map<IEnumEnum, List<IObject>> workStepsGroupByPhase = this.getWorkStepsGroupByROPPhase();
        if (CommonUtility.hasValue(workStepsGroupByPhase)) {
            List<IEnumEnum> sortedKeys = workStepsGroupByPhase.keySet().stream().sorted(Comparator.comparingInt(IEnumEnum::EnumNumber)).collect(Collectors.toList());
            for (IEnumEnum enumEnum : sortedKeys) {
                List<IObject> lcolWorkSteps = this.getValueByEnumEnum(workStepsGroupByPhase, enumEnum);
                assert lcolWorkSteps != null;
                if (lcolWorkSteps.stream().anyMatch(r -> !r.toInterface(IWorkStep.class).hasActualCompletedDate())) {
                    return new HashMap<IEnumEnum, List<IObject>>() {{
                        put(enumEnum, lcolWorkSteps);
                    }};
                }
            }
        }
        return null;
    }

    /*
     * @Description: 获取对象关联的工作步骤信息
     * @param
     * @Return:  Map<IEnumEnum, List<IObject>> Key:阶段  Value:步骤集合
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:36:39
     */
    @Override
    public Map<IEnumEnum, List<IObject>> getWorkStepsGroupByROPPhase() throws Exception {
        IObjectCollection workSteps = this.getWorkSteps();
        if (SchemaUtility.hasValue(workSteps)) {
            Map<IEnumEnum, List<IObject>> lcolResult = new HashMap<>();
            Iterator<IObject> e = workSteps.GetEnumerator();
            while (e.hasNext()) {
                IObject workStep = e.next();
                IROPWorkStep iropWorkStep = workStep.toInterface(IROPWorkStep.class);
                IEnumEnum enumEnum = CIMContext.Instance.ProcessCache().getEnumListLevelType(propertyDefinitionType.ROPWorkStepTPPhase.toString(), iropWorkStep.ROPWorkStepTPPhase());
                if (enumEnum == null) throw new Exception("未找到EnumEnum对象信息,UID:" + iropWorkStep.ROPWorkStepTPPhase());
                if (this.containsByOBID(lcolResult, enumEnum)) {
                    List<IObject> keyByEnumEnum = this.getValueByEnumEnum(lcolResult, enumEnum);
                    assert keyByEnumEnum != null;
                    keyByEnumEnum.add(enumEnum);
                } else {
                    lcolResult.put(enumEnum, new ArrayList<IObject>() {{
                        add(workStep);
                    }});
                }
            }
            return lcolResult;
        }
        return null;
    }

    /*
     * @Description: 根据执行阶段分组已经生成的工作步骤
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:58:45
     */
    @Override
    public Map<IEnumEnum, List<IObject>> getWorkStepsGroupByProcessPhase() throws Exception {
        IObjectCollection workSteps = this.getWorkSteps();
        if (SchemaUtility.hasValue(workSteps)) {
            Map<IEnumEnum, List<IObject>> lcolResult = new HashMap<>();
            Iterator<IObject> e = workSteps.GetEnumerator();
            while (e.hasNext()) {
                IObject workStep = e.next();
                IWorkStep ccmWorkStep = workStep.toInterface(IWorkStep.class);
                IEnumEnum enumEnum = CIMContext.Instance.ProcessCache().getEnumListLevelType(propertyDefinitionType.WSTPProcessPhase.toString(), ccmWorkStep.WSTPProcessPhase());
                if (enumEnum == null) throw new Exception("未找到EnumEnum对象信息,UID:" + ccmWorkStep.WSTPProcessPhase());
                if (this.containsByOBID(lcolResult, enumEnum)) {
                    List<IObject> keyByEnumEnum = this.getValueByEnumEnum(lcolResult, enumEnum);
                    assert keyByEnumEnum != null;
                    keyByEnumEnum.add(enumEnum);
                } else {
                    lcolResult.put(enumEnum, new ArrayList<IObject>() {{
                        add(workStep);
                    }});
                }
            }
            return lcolResult;
        }
        return null;
    }

    /*
     * @Description: 从map中根据EnumEnum获取对应的值
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:37:28
     */
    private List<IObject> getValueByEnumEnum(Map<IEnumEnum, List<IObject>> container, IEnumEnum enumEnum) {
        if (CommonUtility.hasValue(container) && enumEnum != null) {
            for (Map.Entry<IEnumEnum, List<IObject>> entry : container.entrySet()) {
                if (enumEnum.OBID().equalsIgnoreCase(entry.getKey().OBID())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /*
     * @Description: 判断Map集合中是否包含EnumEnum的Key
     * @param null
     * @Return:  boolean
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:37:45
     */
    private boolean containsByOBID(Map<IEnumEnum, List<IObject>> container, IEnumEnum enumEnum) {
        if (container != null && enumEnum != null) {
            return container.keySet().stream().anyMatch(r -> r.OBID().equalsIgnoreCase(enumEnum.OBID()));
        }
        return false;
    }


    @Override
    public IObjectCollection generateWorkStepObjects(boolean pblnNeedTransaction, IObjectCollection pcolROPRuleGroups, IObjectCollection pcolExistObjs) throws Exception {
        log.trace("enter to generate work steps");
        StopWatch stopWatch = PerformanceUtility.start();
        IObjectCollection result = new ObjectCollection();
        //获取设计对象的版本状态,操作状态
        Object operationState = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionItem.toString(), propertyDefinitionType.CIMRevisionItemOperationState.toString());
        Object revState = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionItem.toString(), propertyDefinitionType.CIMRevisionItemRevState.toString());
        if (revState == null) throw new Exception("设计对象:" + this.Name() + "的版本状态未设置!");
        if (operationState == null) throw new Exception("设计对象:" + this.Name() + "的操作状态未设置!");
        Exception ex = null;
        //查找已经关联的工步
        IObjectCollection existSteps = this.getWorkSteps(pcolExistObjs);
        //判断ROP规则,找出符合的规则
        IROPRuleGroup accordWithROPRuleGroup = this.getAccordWithROPRuleGroups(pcolROPRuleGroups);
        if (accordWithROPRuleGroup != null) {
            try {
                //有符合的ROP规则
                if (pblnNeedTransaction) SchemaUtility.beginTransaction();
                IObjectCollection ropWorkSteps = accordWithROPRuleGroup.getROPWorkSteps();
                if (SchemaUtility.hasValue(ropWorkSteps)) {
                    Iterator<IObject> e = ropWorkSteps.GetEnumerator();
                    while (e.hasNext()) {
                        IROPWorkStep lobjWorkStepTemplate = e.next().toInterface(IROPWorkStep.class);
                        generateWorkStepByWSTemplateForDesignObj(result, lobjWorkStepTemplate, operationState.toString(), revState.toString(), existSteps);
                    }
                }
                setExistWorkStepStatus(revState, existSteps);
                if (pblnNeedTransaction) SchemaUtility.commitTransaction();
            } catch (Exception exception) {
                ex = exception;
                log.error("generate work steps failed", exception);
                if (pblnNeedTransaction) SchemaUtility.rollBackTransaction();
            }
        } else {
            setExistWorkStepStatus(revState, existSteps);
        }
        if (ex != null) throw ex;
        log.trace("compete to generate work steps for " + this.toErrorPop() + PerformanceUtility.stop(stopWatch));
        return result;
    }

    @Override
    public IObjectCollection generateWorkStepObjects(boolean pblnNeedTransaction, IObjectCollection pcolROPRuleGroups) throws Exception {
        return generateWorkStepObjects(pblnNeedTransaction, pcolROPRuleGroups, null);
    }

    @Override
    public IObjectCollection generateWorkStepObjects(boolean needBeginTransaction) throws Exception {
        return this.generateWorkStepObjects(needBeginTransaction, null);
    }

    //这些步骤都没有匹配上ROP的规则
    private void setExistWorkStepStatus(Object pstrRevState, IObjectCollection pcolExistSteps) throws Exception {
        if (SchemaUtility.hasValue(pcolExistSteps)) {
            //如果经过上述步骤还有存在的工步,说明这些还存在的工步已经无效,不符合ROP规则,需要对其变更状态
            Iterator<IObject> e = pcolExistSteps.GetEnumerator();
            while (e.hasNext()) {
                IWorkStep lobjWorkStep = e.next().toInterface(IWorkStep.class);
                if (lobjWorkStep.hasRelatedWorkPackage()) {
                    lobjWorkStep.BeginUpdate();
                    if (pstrRevState.toString().equalsIgnoreCase(revState.EN_Revised.toString())) {
                        //设计对象变化的了
                        lobjWorkStep.setWSStatus(workStepStatus.EN_RevisedDelete.toString());
                    } else {
                        //设计对象属性没变,ROP变了,导致没有匹配
                        lobjWorkStep.setWSStatus(workStepStatus.EN_ROPDelete.toString());
                    }
                    lobjWorkStep.FinishUpdate();
                } else {
                    lobjWorkStep.Delete();
                }
            }
        }
    }

    /*
     * @Description: 生成工作步骤通过工作步骤模板
     * @param pcolContainer 存放对象的容器
     * @param pobjWorkStepTemplate 工作步骤模板对象
     * @param pstrOperationState 设计对象的操作状态 新建,Created 未改动,None 更新,Updated 删除,Deleted
     * @param pstrRevState 设计对象的升版状态 当前,Current 新增,New 迁移,Migration 升版,Revised 过期,Superseded 工作中,Working 预留,Reserved
     * @param pcolExistSteps 设计对象已经关联的工作步骤
     * @Return: void
     * @Author: Chen Jing
     * @Date: 2022/5/17 10:36:36
     */
    private void generateWorkStepByWSTemplateForDesignObj(IObjectCollection pcolContainer, IROPWorkStep pobjWorkStepTemplate, String pstrOperationState, String pstrRevState, IObjectCollection pcolExistSteps) throws Exception {
        // DELETE的对象在发布时已经判断并且设置了对象关联的工步状态,在这里传递过来的设计对象中,不会存在该状态的对象
        //2022/5/17 DELETE对象也有可能,从模板开始同步就会存在DELETE状态的
        //新的设计对象,状态为NEW,生成新的工步
        if (pstrOperationState.equalsIgnoreCase(operationState.EN_Created.toString())) {
            createWorkStepObject(pcolContainer, pobjWorkStepTemplate);
        } else {
            //对象状态为UPDATE 的 ,说明是存在且更新了的对象,要判断对象的属性是否变化 根据对象的版本状态判断,如果属性没变为CURRENT,变化了为Revised
            //存在已经关联的工步,需要根据状态修改工作步骤状态
            IWorkStep lobjExistWorkStep = null;
            if (SchemaUtility.hasValue(pcolExistSteps)) {
                lobjExistWorkStep = getWorkStepByROPRule(pcolExistSteps, pobjWorkStepTemplate);
            }
            if (lobjExistWorkStep != null) {
                //已经存在工作步骤符合当前ROP步骤规则
                //设计对象属性已经发生改变,但是没有实际完成时间的,更新他的属性,属性没变的不进行操作
                // //手动更新设计对象的属性时,由于revState 没有设置为Revised,但是实际对象属性已经变了,还是要刷新下工步的属性
                if (!lobjExistWorkStep.hasActualCompletedDate()) {
                    lobjExistWorkStep.syncInfoFromSource(this, true);
                }
            } else {
                //当前的ROP步骤在已经生成的步骤中,不存在,创建新的步骤
                createWorkStepObject(pcolContainer, pobjWorkStepTemplate);
            }
        }

    }

    /*
     * @Description: 创建工作步骤以及与对象的关联关系
     * @param pcolContainer 容器
     * @param pobjWorkStepTemplate 工作步骤模板
     * @return: void
     * @Author: Chen Jing
     * @Date: 2022-05-9 09:41:59
     */
    private void createWorkStepObject(IObjectCollection pcolContainer, IROPWorkStep pobjWorkStepTemplate) throws Exception {
        String propertyDefinitionForWeightCalculation = pobjWorkStepTemplate.ROPWorkStepWeightCalculateProperty();
        IObject lobjCCMWorkStep = pobjWorkStepTemplate.generateWorkStepObject(this.toErrorPop());
        lobjCCMWorkStep.toInterface(IWorkStep.class).saveGeneralInfo(this, propertyDefinitionForWeightCalculation);
        pcolContainer.append(lobjCCMWorkStep);
        IRel rel = SchemaUtility.createRelationShip(relDefinitionType.CCMDesignObj2WorkStep.toString(), this, lobjCCMWorkStep, false);
        pcolContainer.append(rel);
    }

    /*
     * @Description: 通过工步模板找到使用同种规则的工步信息
     * @param pcolWorkSteps 工作步骤
     * @param pobjWorkStepTemplate 工作步骤模板
     * @return: ccm.server.schema.interfaces.IWorkStep
     * @Author: Chen Jing
     * @Date: 2022-05-9 09:39:38
     */
    private IWorkStep getWorkStepByROPRule(@NotNull IObjectCollection pcolWorkSteps, @NotNull IROPWorkStep pobjWorkStepTemplate) {
        IObject lobjPointedWorkStep = null;
        IWorkStep lobjResult = null;
        Iterator<IObject> e = pcolWorkSteps.GetEnumerator();
        while (e.hasNext()) {
            IObject next = e.next();
            IWorkStep workStep = next.toInterface(IWorkStep.class);
            JSONObject lJsonRopRule = workStep.getROPWorkStepRuleJSON();
            Map<String, Object> lJsonROPRuleTemplate = pobjWorkStepTemplate.generateIdentity();
            if (JSON.parse(JSON.toJSONString(lJsonROPRuleTemplate)).equals(JSON.parse(lJsonRopRule.toJSONString()))) {
                lobjPointedWorkStep = next;
                lobjResult = workStep;
                break;
            }
        }
        if (lobjPointedWorkStep != null) {
            pcolWorkSteps.remove(lobjPointedWorkStep);
        }
        return lobjResult;
    }

    /*
     * @Description: 更新工作步骤状态 为已经删除的设计对象
     * @param pblnNeedTransaction 是否需要开启事务
     * @return: void
     * @Author: Chen Jing
     * @Date: 2022-05-9 09:37:22
     */
    @Override
    public void updateWorkStepForDeletedDesignObj(boolean pblnNeedTransaction) throws Exception {
        if (pblnNeedTransaction) SchemaUtility.beginTransaction();
        IObjectCollection lcolWorkSteps = this.getWorkSteps();
        if (SchemaUtility.hasValue(lcolWorkSteps)) {
            Iterator<IObject> e = lcolWorkSteps.GetEnumerator();
            while (e.hasNext()) {
                IWorkStep lobjWorkStep = e.next().toInterface(IWorkStep.class);
                if (lobjWorkStep.hasRelatedWorkPackage()) {
                    lobjWorkStep.BeginUpdate();
                    lobjWorkStep.setWSStatus(workStepStatus.EN_RevisedDelete.toString());
                    lobjWorkStep.FinishUpdate();
                } else {
                    lobjWorkStep.Delete();
                }
            }
        }
        if (pblnNeedTransaction) SchemaUtility.commitTransaction();
    }

    /**
     * @param pcolExistObjs 系统中存在的步骤信息
     *                      获取设计对象的工作步骤
     *                      Chen Jing
     *                      2022/4/24 11:55
     */
    @Override
    public IObjectCollection getWorkSteps(IObjectCollection pcolExistObjs) throws Exception {
        if (!SchemaUtility.hasValue(pcolExistObjs)) {
            IRelCollection lcolRels = this.GetEnd1Relationships().GetRels(relDefinitionType.CCMDesignObj2WorkStep.toString());
            if (SchemaUtility.hasValue(lcolRels)) {
                return lcolRels.GetEnd2s();
            }
            return null;
        }
        IObjectCollection lcolRels = pcolExistObjs.getItemsByInterfaceDefUID(interfaceDefinitionType.IRel.name());
        if (SchemaUtility.hasValue(lcolRels)) {
            //找到当前设计对象的步骤信息
            List<IRel> lcolWSRels = lcolRels.toList(IRel.class).stream().filter(r -> {
                try {
                    return r.RelDefUID().equalsIgnoreCase(relDefinitionType.CCMDesignObj2WorkStep.name()) && r.OBID1().equalsIgnoreCase(this.OBID());
                } catch (Exception e) {
                    log.error("获取RelDefUID或OBID1时失败:{}", ExceptionUtil.getMessage(e));
                    return false;
                }
            }).collect(Collectors.toList());
            if (CommonUtility.hasValue(lcolWSRels)) {
                return pcolExistObjs.itemsByOBIDs(lcolWSRels.stream().map(r -> {
                    try {
                        return r.OBID2();
                    } catch (Exception e) {
                        log.error("获取OBID2时失败:{}", ExceptionUtil.getMessage(e));
                        return "";
                    }
                }).collect(Collectors.toList()));
            }
        }
        return null;
    }

    @Override
    public IObjectCollection getWorkSteps() throws Exception {
        return getWorkSteps(null);
    }

    /*
     * @Descriptions : 获取设计对象放行步骤
     * @Author: Chen Jing
     * @Date: 2022/4/24 11:52
     * @param pcolRelatedWorkSteps 默认传入的关联的工作步骤
     * @Return:java.util.Map 键:步骤的阶段, 值:步骤对象
     */
    @Override
    public Map<IEnumEnum, IObject> getIssueSteps(IObjectCollection pcolRelatedWorkStep) throws Exception {
        Map<IEnumEnum, IObject> result = new HashMap<>();
        Map<IEnumEnum, List<IObject>> workStepsGroupByPhase = this.getWorkStepsGroupByROPPhase();
        if (CommonUtility.hasValue(workStepsGroupByPhase)) {
            for (Map.Entry<IEnumEnum, List<IObject>> entry : workStepsGroupByPhase.entrySet()) {
                List<IObject> lcolWorkSteps = entry.getValue();
                lcolWorkSteps.stream().filter(r -> r.toInterface(IROPWorkStep.class).ROPWorkStepAllowInd()).findFirst().ifPresent(lobjIssueStep -> result.put(entry.getKey(), lobjIssueStep));
            }
        }
        return result;
    }

    /*
     * @Description: 获取第一个放行步骤
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/17 11:00:18
     */
    @Override
    public IObject getFirstIssueStep() throws Exception {
        Map<IEnumEnum, IObject> issueSteps = this.getIssueSteps(null);
        if (CommonUtility.hasValue(issueSteps)) {
            List<IEnumEnum> sorted = issueSteps.keySet().stream().sorted(Comparator.comparingInt(IEnumEnum::EnumNumber)).collect(Collectors.toList());
            IEnumEnum lobjFirstPhase = sorted.get(0);
            for (Map.Entry<IEnumEnum, IObject> entry : issueSteps.entrySet()) {
                if (lobjFirstPhase.OBID().equalsIgnoreCase(entry.getKey().OBID())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /*
     * @Description: 校验设计对象的放行阶段是否完成
     * @param null
     * @return:
     * @Author: Chen Jing
     * @Date: 2022/5/10 11:39:26
     */
    @Override
    public Map<IEnumEnum, Boolean> validateIssuePhaseHasCompleted(IObjectCollection pcolRelatedWorkSteps) throws Exception {
        Map<IEnumEnum, IObject> issueSteps = this.getIssueSteps(pcolRelatedWorkSteps);
        if (CommonUtility.hasValue(issueSteps)) {
            Map<IEnumEnum, Boolean> result = new HashMap<>();
            for (Map.Entry<IEnumEnum, IObject> entry : issueSteps.entrySet()) {
                IWorkStep value = entry.getValue().toInterface(IWorkStep.class);
                result.put(entry.getKey(), value.hasActualCompletedDate());
            }
            return result;
        }
        return null;
    }

    /*
     * @Descriptions : 判断设计对象是否关联了工作包
     * @Author: Chen Jing
     * @Date: 2022/4/24 11:53
     * @param
     * @Return:java.lang.Boolean
     */
    @Override
    public Boolean hasRelatedWorkPackage() throws Exception {
        IRelCollection rels = this.GetEnd2Relationships().GetRels(relDefinitionType.CCMWorkPackage2DesignObj.toString());
        return SchemaUtility.hasValue(rels);
    }

    /*
     * @Descriptions : 判断设计对象是否关联了试压包
     * @Author: Chen Jing
     * @Date: 2022/4/24 11:54
     * @param
     * @Return:java.lang.Boolean
     */
    @Override
    public Boolean hasRelatedPTPackage() throws Exception {
        IRelCollection rels = this.GetEnd2Relationships().GetRels(relDefinitionType.CCMPressureTestPackage2DesignObj.toString());
        return SchemaUtility.hasValue(rels);
    }

    /*
     * @Descriptions : 判断设计对象是否关联的任务包
     * @Author: Chen Jing
     * @Date: 2022/4/24 11:54
     * @param
     * @Return:java.lang.Boolean
     */
    @Override
    public Boolean hasRelatedTaskPackage() throws Exception {
        IRelCollection rels = this.GetEnd2Relationships().GetRels(relDefinitionType.CCMTaskPackage2DesignObj.toString());
        return SchemaUtility.hasValue(rels);
    }

    /*
     * @Description: 获取设计对象能够匹配的ROP规则组
     * @param pcolROPGroups ROP规则组
     * @return: 符合的ROP规则组
     * @Author: Chen Jing
     * @Date: 2022-05-9 10:22:25
     */
    @Override
    public IROPRuleGroup getAccordWithROPRuleGroups(IObjectCollection pcolROPGroups) throws Exception {
        IObjectCollection lcolROPRuleGroups = pcolROPGroups;
        if (!SchemaUtility.hasValue(lcolROPRuleGroups)) {
            String lstrClassDefUID = this.ClassDefinitionUID();
            lcolROPRuleGroups = ROPCache.Instance.ropGroupsByTargetClassDefinitionUID(lstrClassDefUID);
        }
        if (SchemaUtility.hasValue(lcolROPRuleGroups)) {
            lcolROPRuleGroups.sort(interfaceDefinitionType.IROPRuleGroup.toString(), propertyDefinitionType.ROPGroupOrder.toString());
            Iterator<IObject> e = lcolROPRuleGroups.GetEnumerator();
            while (e.hasNext()) {
                IROPRuleGroup ruleGroup = e.next().toInterface(IROPRuleGroup.class);
                boolean hintForProvidedObject = ruleGroup.isHintForProvidedObject(this);
                if (hintForProvidedObject) {
                    return ruleGroup;
                }
            }
        }
        return null;
    }
}
