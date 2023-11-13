package ccm.server.schema.interfaces.generated;

import ccm.server.schema.interfaces.ICIMFormSectionDetails;
import ccm.server.schema.model.IProperty;
import ccm.server.utils.ValueConversionUtility;

public abstract class ICIMFormSectionDetailsBase extends IRelBase implements ICIMFormSectionDetails {
    public ICIMFormSectionDetailsBase(boolean instantiateRequiredProperties) {
        super("ICIMFormSectionDetails", instantiateRequiredProperties);
    }

    @Override
    public String EffectFormPurpose() {
        IProperty property = this.getProperty("ICIMFormSectionDetails", "EffectFormPurpose");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setEffectFormPurpose(String value) throws Exception {
        this.Interfaces().item("ICIMFormSectionDetails", true).Properties().item("EffectFormPurpose", true).setValue(value);
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
