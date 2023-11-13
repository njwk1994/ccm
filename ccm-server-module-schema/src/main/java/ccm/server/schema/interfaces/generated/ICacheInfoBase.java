package ccm.server.schema.interfaces.generated;

import ccm.server.schema.interfaces.ICacheInfo;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.ValueConversionUtility;

public abstract class ICacheInfoBase extends InterfaceDefault implements ICacheInfo {
    public ICacheInfoBase(boolean instantiateRequiredProperties) {
        super(ICacheInfo.class.getSimpleName(), instantiateRequiredProperties);
    }

    @Override
    public boolean CachedInd() {
        IProperty property = this.getProperty("ICacheInfo", "CachedInd");
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setCachedInd(boolean value) throws Exception {
        this.Interfaces().item("ICacheInfo", true).Properties().item("CachedInd", true).setValue(value);
    }

    @Override
    public String CachedKey() {
        IProperty property = this.getProperty("ICacheInfo", "CachedKey");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setCachedKey(String value) throws Exception {
        this.Interfaces().item("ICacheInfo", true).Properties().item("CachedKey", true).setValue(value);
    }
}
