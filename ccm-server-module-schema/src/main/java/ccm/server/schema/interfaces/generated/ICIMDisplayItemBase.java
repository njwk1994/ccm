package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.enums.classDefinitionType;
import ccm.server.enums.propertyValueType;
import ccm.server.enums.relDefinitionType;
import ccm.server.enums.relDirection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.util.CommonUtility;
import ccm.server.utils.ValueConversionUtility;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ICIMDisplayItemBase extends InterfaceDefault implements ICIMDisplayItem {
    public ICIMDisplayItemBase(boolean instantiateRequiredProperties) {
        super("ICIMDisplayItem", instantiateRequiredProperties);
    }

    @Override
    public ICIMSection getSection() throws Exception {
        IRel rel = this.GetEnd2Relationships().GetRel(relDefinitionType.section2DisplayItems.toString(), true);
        if (rel != null)
            return rel.GetEnd1().toInterface(ICIMSection.class);
        return null;
    }

    //PropertyDef
    //EdgeDef
    //+/-RelDef
    //+/-RelDef_Search
    @Override
    public String ItemType() {
        IProperty property = this.getProperty("ICIMDisplayItem", "ItemType");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setItemType(String value) throws Exception {
        this.Interfaces().item("ICIMDisplayItem", true).Properties().item("ItemType", true).setValue(value);
    }

    @Override
    public String SchemaDefinitionUID() {
        IProperty property = this.getProperty("ICIMDisplayItem", "SchemaDefinitionUID");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setSchemaDefinitionUID(String value) throws Exception {
        this.Interfaces().item("ICIMDisplayItem", true).Properties().item("SchemaDefinitionUID", true).setValue(value);
    }

    @Override
    public boolean checkMandatory() throws Exception {
        String schemaDefinitionUID = this.SchemaDefinitionUID();
        IObject schemaObject = CIMContext.Instance.ProcessCache().item(CommonUtility.toActualDefinition(schemaDefinitionUID));
        if (schemaObject == null)
            throw new Exception("invalid schema definition with provided UID:" + schemaDefinitionUID + " as it is not exist in database");
        String classDefinitionUID = schemaObject.ClassDefinitionUID();
        if (!classDefinitionUID.equalsIgnoreCase(this.checkDefType().toString())) {
            throw new Exception("the definition type is not same with schema object's class definition UID, system will terminate progress as mismatch information");
        }
        try {
            classDefinitionType classDefinitionType = this.checkDefType();
            switch (classDefinitionType) {
                case RelDef:
                    return schemaObject.toInterface(IRelDef.class).checkMandatory(this.checkRelDirection());
                case PropertyDef:
                    return schemaObject.toInterface(IPropertyDef.class).checkMandatory();
                case InterfaceDef:
                case EdgeDef:
                default:
                    break;
            }
        } catch (Exception exception) {
            log.error("check mandatory failed for " + this.UID(), exception);
        }
        return false;
    }

    @Override
    public relDirection checkRelDirection() {
        classDefinitionType classDefinitionType = this.checkDefType();
        if (classDefinitionType.equals(ccm.server.enums.classDefinitionType.RelDef))
            return relDirection.toEnumByRelDef(this.SchemaDefinitionUID());
        return relDirection._1To2;
    }


    @Override
    public classDefinitionType checkDefType() {
        String itemType = this.ItemType();
        if (itemType.equalsIgnoreCase(classDefinitionType.PropertyDef.toString()))
            return classDefinitionType.PropertyDef;
        else if (itemType.equalsIgnoreCase(classDefinitionType.InterfaceDef.toString()))
            return classDefinitionType.InterfaceDef;
        else if (itemType.equalsIgnoreCase(classDefinitionType.EdgeDef.toString()))
            return classDefinitionType.EdgeDef;
        return classDefinitionType.RelDef;
    }

    @Override
    public propertyValueType checkPropertyValueType() throws Exception {
        String schemaDefinitionUID = this.SchemaDefinitionUID();
        IObject schemaObject = CIMContext.Instance.ProcessCache().item(CommonUtility.toActualDefinition(schemaDefinitionUID));
        if (schemaObject == null)
            throw new Exception("invalid schema definition with provided UID:" + schemaDefinitionUID + " as it is not exist in database");

        String classDefinitionUID = schemaObject.ClassDefinitionUID();
        if (!classDefinitionUID.equalsIgnoreCase(this.checkDefType().toString())) {
            throw new Exception("the definition type is not same with schema object's class definition UID, system will terminate progress as mismatch information");
        }
        switch (this.checkDefType()) {
            case PropertyDef:
                return schemaObject.toInterface(IPropertyDef.class).checkPropertyValueType();
            case RelDef:
                return schemaObject.toInterface(IRelDef.class).checkPropertyValueType(this.checkRelDirection());
            case InterfaceDef:
                return propertyValueType.BooleanType;
            case EdgeDef:
            default:
                break;
        }
        return propertyValueType.StringType;
    }

    @Override
    public String DefaultQueryValue() {
        IProperty property = this.getProperty("ICIMDisplayItem", "DefaultQueryValue");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setDefaultQueryValue(String value) throws Exception {
        this.Interfaces().item("ICIMDisplayItem", true).Properties().item("DefaultQueryValue", true).setValue(value);
    }

    @Override
    public String DefaultCreateValue() {
        IProperty property = this.getProperty("ICIMDisplayItem", "DefaultCreateValue");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setDefaultCreateValue(String value) throws Exception {
        this.Interfaces().item("ICIMDisplayItem", true).Properties().item("DefaultQueryValue", true).setValue(value);
    }

    @Override
    public String DefaultUpdateValue() {
        IProperty property = this.getProperty("ICIMDisplayItem", "DefaultUpdateValue");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setDefaultUpdateValue(String value) throws Exception {
        this.Interfaces().item("ICIMDisplayItem", true).Properties().item("DefaultQueryValue", true).setValue(value);
    }
}
