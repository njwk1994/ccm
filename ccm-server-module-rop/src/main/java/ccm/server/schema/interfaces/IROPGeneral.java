package ccm.server.schema.interfaces;

public interface IROPGeneral extends IObject {

    boolean getROPConsumesMaterial();

    void setROPConsumesMaterial(String ROPConsumesMaterial) throws Exception;

    String getROPGroupName();

    void setROPGroupName(String ROPGroupName) throws Exception;

    String getROPConstructionPurpose();

    void setROPConstructionPurpose(String ROPConstructionPurpose) throws Exception;

    String getROPPurposeOrder();

    void setROPPurposeOrder(String stageOrder) throws Exception;

    String getROPStepName();

    void setROPStepName(String ROPStepName) throws Exception;

    String getROPStepOrder();

    void setROPStepOrder(String ROPStepOrder) throws Exception;

    String getROPCalculateProperty();

    void setROPCalculateProperty(String ROPCalculateProperty) throws Exception;

    String getROPUOM();

    void setROPUOM(String ROPUOM) throws Exception;

    String getROPProgressWeight();

    void setROPProgressWeight(String ROPProgressWeight) throws Exception;
}
