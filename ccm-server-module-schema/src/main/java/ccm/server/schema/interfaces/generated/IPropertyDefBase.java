package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.dto.base.OptionItemDTO;
import ccm.server.enums.classDefinitionType;
import ccm.server.enums.propertyValueType;
import ccm.server.enums.relDefinitionType;
import ccm.server.model.DynamicalDefinitionObj;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.PropertyDefault;
import ccm.server.utils.ValueConversionUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class IPropertyDefBase extends ISchemaObjectBase implements IPropertyDef {
    public IPropertyDefBase(boolean instantiateRequiredProperties) {
        super("IPropertyDef", instantiateRequiredProperties);
    }

    @Override
    public List<OptionItemDTO> generateOptions() throws Exception {
        IObject scopedBy = this.getScopedBy();
        List<OptionItemDTO> result = new ArrayList<>();
        if (scopedBy != null) {
            if (scopedBy.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.EnumListType.toString())) {
                IEnumListType enumListType = scopedBy.toInterface(IEnumListType.class);
                IObjectCollection entries = enumListType.getEntries();
                if (entries != null && entries.hasValue()) {
                    Iterator<IObject> iObjectIterator = entries.GetEnumerator();
                    while (iObjectIterator.hasNext()) {
                        IObject next = iObjectIterator.next();
                        OptionItemDTO optionItemDTO = new OptionItemDTO();
                        optionItemDTO.setDescription(next.Description());
                        optionItemDTO.setName(next.Name());
                        optionItemDTO.setDisplayAs(next.generateDisplayAs());
                        optionItemDTO.setUid(next.UID());
                        result.add(optionItemDTO);
                    }
                }
            } else if (scopedBy.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.EnumListLevelType.toString())) {

            } else if (scopedBy.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.UoMListType.toString())) {

            }
        }
        return result;
    }

    @Override
    public Object DefaultValue() throws Exception {
        IProperty property = this.Interfaces().item("IPropertyDef", false).Properties().item("DefaultValue", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setDefaultValue(Object value) throws Exception {
        this.Interfaces().item("IPropertyDef", true).Properties().item("DefaultValue", true).setValue(value);
    }

    @Override
    public String TypeData() throws Exception {
        IProperty property = this.Interfaces().item("IPropertyDef", false).Properties().item("TypeData", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setTypeData(String value) throws Exception {
        this.Interfaces().item("IPropertyDef", true).Properties().item("TypeData", true).setValue(value);
    }

    @Override
    public IPropertyType getScopedByPropertyType() {
        IObject scopedBy = CIMContext.Instance.ProcessCache().getScopedByForPropertyDefinition(this.UID());
        if (scopedBy != null)
            return scopedBy.toInterface(IPropertyType.class);
        return null;
    }

    @Override
    public Object Instantiate(boolean instantiateRequiredItems) throws Exception {
        return this.Instantiate(this.UID(), this.UID(), instantiateRequiredItems);
    }

    @Override
    public Object Instantiate(String pstrOBID, String pstrUID, boolean instantiateRequiredItems) throws Exception {
        return new PropertyDefault(pstrUID);
    }

    @Override
    public IInterfaceDef getExposesInterfaceDef() throws Exception {
        IRel rel = this.GetEnd2Relationships().GetRel(relDefinitionType.exposes.toString(), true);
        if (rel != null)
            return rel.GetEnd1().toInterface(IInterfaceDef.class);
        return null;
    }

    @Override
    public boolean HistoryNotRetained() throws Exception {
        IProperty property = this.Interfaces().item("IPropertyDef", false).Properties().item("HistoryNotRetained", false);
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setHistoryNotRetained(boolean value) throws Exception {
        this.Interfaces().item("IPropertyDef", true).Properties().item("HistoryNotRetained", true).setValue(value);
    }

    @Override
    public IObject getScopedBy() throws Exception {
        IRel iRel = this.GetEnd1Relationships().GetRel(relDefinitionType.scopedBy.toString(), true);
        if (iRel != null)
            return iRel.GetEnd2();
        return null;
    }

    @Override
    public propertyValueType checkPropertyValueType() {
        IObject scopedBy = CIMContext.Instance.ProcessCache().getScopedByForPropertyDefinition(this.UID());
        if (scopedBy != null) {
            if (scopedBy.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.PropertyType.toString())) {
                return propertyValueType.valueOf(scopedBy.Name());
            } else if (scopedBy.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.EnumListType.toString()))
                return propertyValueType.EnumListType;
            else if (scopedBy.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.EnumListLevelType.toString()))
                return propertyValueType.EnumListLevelType;
            else if (scopedBy.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.UoMListType.toString()))
                return propertyValueType.UoMListType;
        }
        DynamicalDefinitionObj dynamicalDefObj = CIMContext.Instance.ProcessCache().getDynamicalDefinitionObj(this.UID());
        if (dynamicalDefObj != null)
            return dynamicalDefObj.getPropertyValueType();
        return propertyValueType.StringType;
    }

    @Override
    public boolean checkMandatory() throws Exception {
        return CIMContext.Instance.ProcessCache().getRequiredOrNotForPropertyExposesInterfaceDef(this.UID());
    }

    @Override
    public IRel getExposedInterfaceDefReturnRel() throws Exception {
        return this.GetEnd2Relationships().GetRel(relDefinitionType.exposes.toString(), true);
    }

    @Override
    public IInterfaceDef getExposedInterfaceDef() throws Exception {
        IRel rel = this.getExposedInterfaceDefReturnRel();
        if (rel != null)
            return rel.GetEnd1().toInterface(IInterfaceDef.class);
        return null;
    }

    @Override
    public boolean isEnumListType() throws Exception {
        IPropertyType lobjPropType = this.getScopedByPropertyType();
        if (lobjPropType == null) throw new Exception(this.ClassDefinitionUID() + ":未找到实现属性的值类型信息!");
        return lobjPropType.ClassDefinitionUID().equalsIgnoreCase(propertyValueType.EnumListType.toString());
    }
}
