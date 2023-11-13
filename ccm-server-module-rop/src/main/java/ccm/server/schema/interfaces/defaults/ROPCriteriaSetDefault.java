package ccm.server.schema.interfaces.defaults;

import ccm.server.schema.interfaces.generated.IROPCriteriaSetBase;

public class ROPCriteriaSetDefault extends IROPCriteriaSetBase {

    public ROPCriteriaSetDefault(boolean instantiateRequiredProperties) {
        super(instantiateRequiredProperties);
    }

    @Override
    public boolean getROPConsumesMaterial() {
        return false;
    }

    @Override
    public void setROPConsumesMaterial(String ROPConsumesMaterial) throws Exception {
    }

    @Override
    public String getROPConstructionPurpose() {
        return null;
    }

    @Override
    public void setROPConstructionPurpose(String ROPConstructionPurpose) throws Exception {
    }

    @Override
    public String getROPPurposeOrder() {
        return null;
    }

    @Override
    public void setROPPurposeOrder(String stageOrder) throws Exception {
    }

    @Override
    public String getROPStepName() {
        return null;
    }

    @Override
    public void setROPStepName(String ROPStepName) throws Exception {
    }

    @Override
    public String getROPStepOrder() {
        return null;
    }

    @Override
    public void setROPStepOrder(String ROPStepOrder) throws Exception {
    }

    @Override
    public String getROPProgressWeight() {
        return null;
    }

    @Override
    public void setROPProgressWeight(String ROPProgressWeight) throws Exception {
    }
}
