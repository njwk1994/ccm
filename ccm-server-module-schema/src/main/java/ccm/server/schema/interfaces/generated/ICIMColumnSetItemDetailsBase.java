package ccm.server.schema.interfaces.generated;

import ccm.server.schema.interfaces.ICIMColumnSetItemDetails;
import ccm.server.schema.model.IProperty;
import ccm.server.utils.ValueConversionUtility;

public abstract class ICIMColumnSetItemDetailsBase extends IRelBase implements ICIMColumnSetItemDetails {
    public ICIMColumnSetItemDetailsBase(boolean instantiateRequiredProperties) {
        super("ICIMColumnSetItemDetails", instantiateRequiredProperties);
    }



    public ICIMColumnSetItemDetailsBase(String pstrInterfaceDefinitionUID, boolean instantiateRequiredProperties) {
        super(pstrInterfaceDefinitionUID, instantiateRequiredProperties);
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
        return ValueConversionUtility.toInteger(property);
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
        return ValueConversionUtility.toInteger(property);
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
