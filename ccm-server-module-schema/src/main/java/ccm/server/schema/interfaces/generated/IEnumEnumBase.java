package ccm.server.schema.interfaces.generated;

import ccm.server.enums.relDefinitionType;
import ccm.server.schema.interfaces.ICIMObjClass;
import ccm.server.schema.interfaces.IEnumEnum;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.model.IProperty;
import ccm.server.utils.ValueConversionUtility;
import org.springframework.util.StringUtils;

public abstract class IEnumEnumBase extends ISchemaObjectBase implements IEnumEnum {
    public IEnumEnumBase(boolean instantiateRequiredProperties) {
        super("IEnumEnum", instantiateRequiredProperties);
    }

    public IEnumEnumBase(String interfaceDefinitionUid, boolean instantiateRequiredProperties) {
        super(interfaceDefinitionUid, instantiateRequiredProperties);
    }

    @Override
    public boolean isHint(String value) {
        if (!StringUtils.isEmpty(value)) {
            return this.Name().equalsIgnoreCase(value) || this.UID().equalsIgnoreCase(value) || this.generateDisplayAs().equalsIgnoreCase(value) || this.Description().equalsIgnoreCase(value);
        }
        return false;
    }

    @Override
    public void setEnumNumber(int value) throws Exception {
        this.Interfaces().item("IEnumEnum").Properties().item("EnumNumber").setValue(value);
    }

    @Override
    public int EnumNumber() {
        IProperty property = this.getProperty("IEnumEnum", "EnumNumber");
        return ValueConversionUtility.toInteger(property);
    }

    @Override
    public ICIMObjClass getObjClass() throws Exception {
        IObject objClass = this.GetEnd2Relationships().GetRel(relDefinitionType.CIMObjClassEnumEnum.toString(), false).GetEnd1();
        return objClass != null ? objClass.toInterface(ICIMObjClass.class) : null;
    }
}
