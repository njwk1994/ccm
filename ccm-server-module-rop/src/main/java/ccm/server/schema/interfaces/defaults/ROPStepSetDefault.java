package ccm.server.schema.interfaces.defaults;

import ccm.server.schema.interfaces.generated.IROPStepSetBase;

public class ROPStepSetDefault extends IROPStepSetBase {

    public ROPStepSetDefault(boolean instantiateRequiredProperties) {
        super(instantiateRequiredProperties);
    }

    @Override
    public String getROPUOM() {
        return null;
    }

    @Override
    public void setROPUOM(String ROPUOM) throws Exception {
    }
}
