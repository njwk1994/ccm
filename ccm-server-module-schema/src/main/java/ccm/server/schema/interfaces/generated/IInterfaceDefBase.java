package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.enums.domainInfo;
import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.relDefinitionType;
import ccm.server.enums.relDirection;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IProperty;
import ccm.server.util.CommonUtility;
import ccm.server.utils.ValueConversionUtility;
import org.springframework.util.StringUtils;

import java.util.*;

public abstract class IInterfaceDefBase extends ISchemaObjectBase implements IInterfaceDef {
    public IInterfaceDefBase(boolean instantiateRequiredProperties) {
        super("IInterfaceDef", instantiateRequiredProperties);
    }

    public int getDefaultInterfaceSequence(String interfaceDefinitionUid) {
        if (!StringUtils.isEmpty(interfaceDefinitionUid)) {
            if (interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.IObject.toString()))
                return 0;
            else if (interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.ISchemaObject.toString()))
                return 5;
            else if (interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.IInterfaceDef.toString()))
                return 10;
            else if (interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.IClassDef.toString()))
                return 10;
            else if (interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.IPropertyDef.toString()))
                return 10;
            else if (interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.IRelDef.toString()))
                return 10;
            else if (interfaceDefinitionUid.equalsIgnoreCase("IEdgeDef"))
                return 10;
            else if (interfaceDefinitionUid.equalsIgnoreCase("IGraphDef"))
                return 10;
            else if (interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.IEnumListType.toString()))
                return 10;
            else if (interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.IEnumEnum.toString()))
                return 15;
            else if (interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.IEnumListLevelType.toString()))
                return 10;
            else if (interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.IPropertyType.toString()))
                return 15;
            else if (interfaceDefinitionUid.equalsIgnoreCase("ITableInfo"))
                return 20;
            else if (interfaceDefinitionUid.equalsIgnoreCase("IDomain"))
                return 20;
            else if (interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.IRel.toString()))
                return 20;
        }
        return -1;
    }

    private final static int RESERVED_INTERFACE_SEQUENCE_HARD = 1000;

    @Override
    public int InterfaceSequence() {
        int value = this.getDefaultInterfaceSequence(this.UID());
        if (value == -1) {
            IProperty property = this.getProperty("InterfaceSequence");
            return ValueConversionUtility.toInteger(property) + RESERVED_INTERFACE_SEQUENCE_HARD;
        }
        return value;
    }

    @Override
    public void setInterfaceSequence(Integer value) throws Exception {
        this.Interfaces().item("IPropertyDef", true).Properties().item("InterfaceSequence", true).setValue(value);
    }

    @Override
    public Map<String, List<IClassDef>> getRealizedClassDefDomainInfo() throws Exception {
        Map<String, List<IClassDef>> result = new HashMap<>();
        IObjectCollection classDefinitions = this.getRealizedClassDefinition();
        if (classDefinitions != null && classDefinitions.size() > 0) {
            Iterator<IObject> iObjectIterator = classDefinitions.GetEnumerator();
            while (iObjectIterator.hasNext()) {
                IClassDef iClassDef = iObjectIterator.next().toInterface(IClassDef.class);
                IObject domainInfo = iClassDef.getDomainInfo();
                if (domainInfo != null)
                    CommonUtility.doAddElementGeneral(result, domainInfo.UID(), iClassDef);
            }
        }
        return result;
    }

    @Override
    public IObjectCollection getExposesPropertyDefinition() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.exposes.toString(), true);
        if (relCollection != null && relCollection.hasValue())
            return relCollection.GetEnd2s();
        return null;
    }

    @Override
    public List<String> getUsedDomainTablePrefix() throws Exception {
        List<String> result = new ArrayList<>();
        List<String> classDefsByInterfaceDef = CIMContext.Instance.ProcessCache().getRealizesClassDefsByInterfaceDef(this.UID());
        if (CommonUtility.hasValue(classDefsByInterfaceDef)) {
            for (String classDef : classDefsByInterfaceDef) {
                IObject item = CIMContext.Instance.ProcessCache().item(classDef, domainInfo.SCHEMA.toString(), false);
                if (item != null) {
                    IClassDef classDef1 = item.toInterface(IClassDef.class);
                    if (classDef1 != null) {
                        String tablePrefix = classDef1.getTablePrefixForInstantiating();
                        result.add(tablePrefix);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<IDomain> getUsedDomain() throws Exception {
        List<IDomain> result = new ArrayList<>();
        List<String> classDefsByInterfaceDef = CIMContext.Instance.ProcessCache().getRealizesClassDefsByInterfaceDef(this.UID());
        if (CommonUtility.hasValue(classDefsByInterfaceDef)) {
            for (String classDef : classDefsByInterfaceDef) {
                IObject item = CIMContext.Instance.ProcessCache().item(classDef, domainInfo.SCHEMA.toString(), false);
                if (item != null) {
                    IClassDef classDef1 = item.toInterface(IClassDef.class);
                    if (classDef1 != null) {
                        IDomain domain = classDef1.getDomainForInstantiating();
                        if (domain != null)
                            result.add(domain);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public IObjectCollection getEnd1RelDefs() throws Exception {
        return CIMContext.Instance.getIDefRelDefsWithDirection(this.UID(), relDirection._1To2);
    }

    @Override
    public IObjectCollection getEnd2RelDefs() throws Exception {
        return CIMContext.Instance.getIDefRelDefsWithDirection(this.UID(), relDirection._2To1);
    }

    @Override
    public boolean isImplied(String pstrInterfaceDefUID, boolean pblnDeep) {
        return false;
    }

    @Override
    public boolean isImpliedBy(String pstrInterfaceDefUID, boolean pblnDeep) {
        return false;
    }


    @Override
    public boolean checkMandatory(String classDefinitionUID) throws Exception {
        if (!StringUtils.isEmpty(classDefinitionUID)) {
            IRelCollection relCollection = this.getRealized();
            if (relCollection != null && relCollection.hasValue()) {
                Iterator<IObject> objectIterator = relCollection.GetEnumerator();
                while (objectIterator.hasNext()) {
                    IRel rel = objectIterator.next().toInterface(IRel.class);
                    if (rel.UID1().equalsIgnoreCase(classDefinitionUID))
                        return rel.IsRequired();
                }
            }
        }
        return false;
    }

    @Override
    public IRelCollection getRealized() throws Exception {
        return this.GetEnd2Relationships().GetRels(relDefinitionType.realizes.toString(), true);
    }

    @Override
    public IObjectCollection getRealizedClassDefinition() throws Exception {
        IRelCollection realized = this.getRealized();
        if (realized != null && realized.hasValue())
            return realized.GetEnd1s();
        return null;
    }
}
