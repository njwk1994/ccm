package ccm.server.schema.interfaces.defaults;

import ccm.server.schema.interfaces.ICIMSectionDisplayItemDetails;
import ccm.server.schema.interfaces.generated.ICIMSectionDisplayItemDetailsBase;
import ccm.server.schema.model.IProperty;
import ccm.server.utils.ValueConversionUtility;

public class ICIMSectionDisplayItemDetailsDefault  extends ICIMSectionDisplayItemDetailsBase {
    public ICIMSectionDisplayItemDetailsDefault(boolean instantiateRequiredProperties) {
        super(instantiateRequiredProperties);
    }

    @Override
    public int ColumnSpan() {
        IProperty property = this.getProperty("ICIMRenderInfo", "ColumnSpan");
        int result = ValueConversionUtility.toInteger(property);
        if (result > 0)
            return result;
        return 1;
    }

    @Override
    public void setColumnSpan(Integer value) throws Exception {
        this.Interfaces().item("ICIMRenderInfo", true).Properties().item("ColumnSpan", true).setValue(value);
    }
}
