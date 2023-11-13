package ccm.server.schema.interfaces;

import ccm.server.schema.collections.IObjectCollection;

import java.util.List;
import java.util.Map;

//设计对象都实现的统一接口
public interface IROPExecutableItem extends IObject {
    void ropPerformWithUpdateCase() throws Exception;

    void ropPerformWithNewCase() throws Exception;

    void ropPerformWithRemoveCase() throws Exception;

    void ropPerform() throws Exception;

    String operateStatus();

    String revStatus();

    IObjectCollection generateWorkStepObjects(boolean pblnNeedTransaction, IObjectCollection ROPRuleGroups) throws Exception;

    IObjectCollection generateWorkStepObjects(boolean pblnNeedTransaction, IObjectCollection ROPRuleGroups,IObjectCollection pcolExistObjs) throws Exception;

    IObjectCollection generateWorkStepObjects(boolean transactionProgressOrNot) throws Exception;

    void updateWorkStepForDeletedDesignObj(boolean pblnNeedTransaction) throws Exception;

    IObjectCollection getWorkSteps(IObjectCollection pcolExistObjs) throws Exception;

    IObjectCollection getWorkSteps() throws Exception;

    Map<IEnumEnum, List<IObject>> getWorkStepsGroupByROPPhase() throws Exception;

    Map<IEnumEnum, List<IObject>> getWorkStepsGroupByProcessPhase() throws Exception;

    Map<IEnumEnum, IObject> getIssueSteps(IObjectCollection pcolRelatedWorkStep) throws Exception;

    boolean hasIssueSteps() throws Exception;

    Map<IEnumEnum,Boolean> validateIssuePhaseHasCompleted(IObjectCollection pcolRelatedWorkSteps) throws Exception;

    //获取当前阶段或最后阶段
    Map<IEnumEnum, List<IObject>> getActivePhase() throws Exception;

    boolean validateAllIssueStepsHasActualCompletedDate() throws Exception;

    IObject getFirstIssueStep() throws Exception;

    Boolean hasRelatedWorkPackage() throws Exception;

    Boolean hasRelatedPTPackage() throws Exception;

    Boolean hasRelatedTaskPackage() throws Exception;

    IObject getParentObject() throws Exception;

    boolean isNew();

    boolean isUpdate();

    boolean isDeleted();

    /*
     * @Description: 获取设计对象能够匹配的ROP规则组
     * @param null
     * @return: 符合的ROP规则组集合
     * @Author: Chen Jing
     * @Date: 2022-05-129 10:20:10
     */
    IROPRuleGroup getAccordWithROPRuleGroups(IObjectCollection ropGroups) throws Exception;

    IWorkStep generateTemporaryWorkStepObject(IEnumEnum activePhase, String stepName, String stepStatus) throws Exception;

    void syncInfoToStepObject(IWorkStep stepObject, boolean updateOrNot) throws Exception;

    void cleanupMarkOfRevise() throws Exception;

}
