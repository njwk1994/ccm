package ccm.server.schema.interfaces.generated;

import ccm.server.schema.interfaces.ICIMOrderInfo;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.ValueConversionUtility;

public abstract class ICIMOrderInfoBase extends InterfaceDefault implements ICIMOrderInfo {

    public ICIMOrderInfoBase(String interfaceDefinitionUid, boolean instantiateRequiredProperties) {
        super(interfaceDefinitionUid, instantiateRequiredProperties);
    }

    public ICIMOrderInfoBase(boolean instantiateRequiredProperties) {
        super("ICIMOrderInfo", instantiateRequiredProperties);
    }

    @Override
    public int SequenceNumber() {
        IProperty property = this.getProperty("ICIMOrderInfo", "SequenceNumber");
        return ValueConversionUtility.toInteger(property);
    }

    @Override
    public void setSequenceNumber(int value) throws Exception {
        this.Interfaces().item("ICIMOrderInfo", true).Properties().item("SequenceNumber", true).setValue(value);
    }
}
