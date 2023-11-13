package ccm.server.schema.interfaces.generated;

import ccm.server.schema.interfaces.IPropertyType;

public abstract class IPropertyTypeBase extends ISchemaObjectBase implements IPropertyType {
    public IPropertyTypeBase(boolean instantiateRequiredProperties) {
        super("IPropertyType", instantiateRequiredProperties);
    }

    public IPropertyTypeBase(String interfaceDefinitionUid, boolean instantiateRequiredProperties) {
        super(interfaceDefinitionUid, instantiateRequiredProperties);
    }
}
