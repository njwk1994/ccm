package ccm.server.schema.interfaces;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface IROPWorkStep extends IObject {

    IROPRuleGroup getROPRuleGroup() throws Exception;

    String ROPWorkStepTPPhase();

    void setROPWorkStepTPPhase(String value) throws Exception;

    String ROPWorkStepWPPhase();

    void  setROPWorkStepWPPhase(String workStepWPPhase) throws Exception;

    void setROPWorkStepMaterialIssue(String materialIssue) throws Exception;

    String ROPWorkStepMaterialIssue();

    String ROPWorkStepGenerateMode();

    void setROPWorkStepGenerateMode(String generateMode) throws Exception;

    String ROPWorkStepName();

    void setROPWorkStepName(String value) throws Exception;

    String ROPWorkStepType();

    void setROPWorkStepType(String value) throws Exception;

    boolean ROPWorkStepAllowInd();

    void setROPWorkStepAllowInd(boolean value) throws Exception;

    boolean ROPWorkStepConsumeMaterialInd();

    void setROPWorkStepConsumeMaterialInd(boolean value) throws Exception;

    String ROPWorkStepWeightCalculateProperty();

    void setROPWorkStepWeightCalculateProperty(String value) throws Exception;

    double ROPWorkStepBaseWeight();

    void setROPWorkStepBaseWeight(double value) throws Exception;

    int ROPWorkStepOrderValue();

    void setROPWorkStepOrderValue(int value) throws Exception;

    boolean isNormalGenerated();

    boolean isReserveGenerated();

    boolean isForceGenerated();

    IObject generateWorkStepObject() throws Exception;

    String getWorkStepDisplayName() throws Exception;

    IObject generateWorkStepObject(String infoString) throws Exception;

    Map<String,Object> generateIdentity();
}
