package ccm.server.schema.interfaces;

import ccm.server.enums.workStepStatus;
import com.alibaba.fastjson.JSONObject;

public interface IWorkStep extends IObject {

    double WSWeight();

    void setWSWeight(double value) throws Exception;

    boolean hasActualCompletedDate();

    void setROPRule(String ropRule) throws Exception;

    String ROPRule();

    boolean WSConsumeMaterial();

    void setWSStatus(String wsStatus) throws Exception;

    String WSStatus();

    void setWSConsumeMaterial(boolean value) throws Exception;

    String WSComponentName();

    void setWSComponentName(String value) throws Exception;

    String WSComponentDesc();

    void setWSComponentDesc(String value) throws Exception;

    String WSTPProcessPhase();

    void setWSTPProcessPhase(String value) throws Exception;

    String WSWPProcessPhase();

    void  setWSWPProcessPhase(String wswpProcessPhase)throws Exception;
    void saveGeneralInfo(IObject pobjDesignObj, String propertyDefinitionForWeightCalculation) throws Exception;

    double calculateWeight(String targetPropertyDefinition);

    boolean hasRelatedWorkPackage() throws Exception;

    JSONObject getROPWorkStepRuleJSON();

    boolean hasRelatedPTPPackage() throws Exception;

    void setWorkStepStatus(workStepStatus workStepStatus, boolean needTransaction) throws Exception;

    boolean isNormal();

    boolean isForce();

    boolean isLegacy();

    void syncInfoFromSource(IObject sourceObj, boolean updateOrNot) throws Exception;

    boolean isDeletable();

    boolean isMyPhaseAlreadyCompleted() throws Exception;

    void markAsDeleteWhenRevise() throws Exception;

    void markAsBequeathWhenRevise() throws Exception;

    void moveToSpecifiedPhase(String targetPhase) throws Exception;

    IObject getDesignObject() throws Exception;

    /**
     * 是否为图纸/ROP升版删除状态
     *
     * @return
     * @throws Exception
     */
    boolean isDeleteStatus() throws Exception;

    /**
     * true-ROP升版删除;false-图纸升版删除;
     *
     * @return
     * @throws Exception 非删除状态
     */
    boolean isROPDelete() throws Exception;
}
