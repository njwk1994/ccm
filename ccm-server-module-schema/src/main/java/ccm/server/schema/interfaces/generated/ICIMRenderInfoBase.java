package ccm.server.schema.interfaces.generated;

import ccm.server.schema.interfaces.ICIMRenderInfo;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.ValueConversionUtility;

public abstract class ICIMRenderInfoBase extends InterfaceDefault implements ICIMRenderInfo {

    public ICIMRenderInfoBase(String interfaceDefinitionUid, boolean instantiateRequiredProperties) {
        super(interfaceDefinitionUid, instantiateRequiredProperties);
    }

    public ICIMRenderInfoBase(boolean instantiateRequiredProperties) {
        super("ICIMRenderInfo", instantiateRequiredProperties);
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

    @Override
    public String DisplayAs() {
        IProperty property = this.getProperty("ICIMRenderInfo", "DisplayAs");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setDisplayAs(String value) throws Exception {
        this.Interfaces().item("ICIMRenderInfo", true).Properties().item("DisplayAs", true).setValue(value);
    }

    @Override
    public double Width() {
        IProperty property = this.getProperty("ICIMRenderInfo", "Width");
        return ValueConversionUtility.toDouble(property);
    }

    @Override
    public void setWidth(double value) throws Exception {
        this.Interfaces().item("ICIMRenderInfo", true).Properties().item("Width", true).setValue(value);
    }

    @Override
    public boolean Visible() {
        IProperty property = this.getProperty("ICIMRenderInfo", "Visible");
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setVisible(boolean value) throws Exception {
        this.Interfaces().item("ICIMRenderInfo", true).Properties().item("Visible", true).setValue(value);
    }

    @Override
    public double Length() {
        IProperty property = this.getProperty("ICIMRenderInfo", "Length");
        return ValueConversionUtility.toDouble(property);
    }

    @Override
    public void setLength(double value) throws Exception {
        this.Interfaces().item("ICIMRenderInfo", true).Properties().item("Length", true).setValue(value);
    }

    @Override
    public boolean ReadOnly() {
        IProperty property = this.getProperty("ICIMRenderInfo", "ReadOnly");
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setReadOnly(boolean value) throws Exception {
        this.Interfaces().item("ICIMRenderInfo", true).Properties().item("ReadOnly", true).setValue(value);
    }
}
