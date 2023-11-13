package ccm.server.schema.interfaces.generated;

import ccm.server.enums.relDefinitionType;
import ccm.server.schema.interfaces.ICIMColumnItem;
import ccm.server.schema.interfaces.ICIMColumnSet;
import ccm.server.schema.interfaces.IRel;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.ValueConversionUtility;

public abstract class ICIMColumnItemBase extends InterfaceDefault implements ICIMColumnItem {
    public ICIMColumnItemBase(String interfaceDefinitionUid, boolean instantiateRequiredProperties) {
        super(interfaceDefinitionUid, instantiateRequiredProperties);
    }

    public ICIMColumnItemBase(boolean instantiateRequiredProperties) {
        super("ICIMColumnItem", instantiateRequiredProperties);
    }

    @Override
    public String RelOrEdgeDefUID() {
        IProperty property = this.getProperty("ICIMColumnItem", "RelOrEdgeDefUID");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setRelOrEdgeDefUID(String value) throws Exception {
        this.Interfaces().item("ICIMColumnItem", true).Properties().item("RelOrEdgeDefUID", true).setValue(value);
    }

    @Override
    public String PropertyAsValueSource() {
        IProperty property = this.getProperty("ICIMColumnItem", "PropertyAsValueSource");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setPropertyAsValueSource(String value) throws Exception {
        this.Interfaces().item("ICIMColumnItem", true).Properties().item("PropertyAsValueSource", true).setValue(value);
    }

    @Override
    public String ValuePattern() {
        IProperty property = this.getProperty("ICIMColumnItem", "ValuePattern");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setValuePattern(String value) throws Exception {
        this.Interfaces().item("ICIMColumnItem", true).Properties().item("ValuePattern", true).setValue(value);
    }

    @Override
    public ICIMColumnSet getColumnSet() throws Exception {
        IRel rel = this.GetEnd2Relationships().GetRel(relDefinitionType.columnSet2Items.toString(), true);
        if (rel != null)
            return rel.GetEnd1().toInterface(ICIMColumnSet.class);
        return null;
    }
}
