package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.dto.base.OptionItemDTO;
import ccm.server.enums.domainInfo;
import ccm.server.enums.propertyValueType;
import ccm.server.enums.relCollectionTypes;
import ccm.server.enums.relDirection;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IDomain;
import ccm.server.schema.interfaces.IInterfaceDef;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRelDef;
import ccm.server.schema.model.IProperty;
import ccm.server.util.CommonUtility;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class IRelDefBase extends IRelBase implements IRelDef {
    public IRelDefBase(boolean instantiateRequiredProperties) {
        super("IRelDef", instantiateRequiredProperties);
    }

    @Override
    public boolean CachedInd() {
        IProperty property = this.getProperty("ICacheInfo", "CachedInd");
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setCachedInd(boolean value) throws Exception {
        this.Interfaces().item("ICacheInfo", true).Properties().item("CachedInd", true).setValue(value);
    }

    @Override
    public String CachedKey() {
        IProperty property = this.getProperty("ICacheInfo", "CachedKey");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setCachedKey(String value) throws Exception {
        this.Interfaces().item("ICacheInfo", true).Properties().item("CachedKey", true).setValue(value);
    }

    @Override
    public String getTargetInterface(relDirection relDirection) throws Exception {
        switch (relDirection) {
            case _1To2:
                return this.UID2();
            case _2To1:
                return this.UID1();
        }
        return "";
    }

    @Override
    public List<OptionItemDTO> generateOptions(relDirection relDirection) throws Exception {
        String targetInterface = this.getTargetInterface(relDirection);
        if (StringUtils.isEmpty(targetInterface)) {
            throw new Exception("未找到目标端的接口定义");
        }
        IObjectCollection otherEndObjs = SchemaUtility.getObjectsWithInterfaceDefAndCriteria(targetInterface, null);
        if (!SchemaUtility.hasValue(otherEndObjs)) {
            return null;
        }
        return otherEndObjs.toList(IObject.class).stream().map(r -> new OptionItemDTO(r.UID(), r.Name(), r.Description())).collect(Collectors.toList());
    }

    @Override
    public List<String> getUsedDomainsTablePrefix(relCollectionTypes collectionTypes) throws Exception {
        List<IDomain> usedDomains = this.getUsedDomains(collectionTypes);
        List<String> result = new ArrayList<>();
        if (CommonUtility.hasValue(usedDomains)) {
            List<String> stringList = usedDomains.stream().map(IDomain::TablePrefix).filter(c -> !StringUtils.isEmpty(c)).distinct().collect(Collectors.toList());
            result.addAll(stringList);
        }
        return result;
    }

    @Override
    public List<IDomain> getUsedDomains(relCollectionTypes collectionTypes) throws Exception {
        String forceInterface = "";
        List<IDomain> result = new ArrayList<>();
        switch (collectionTypes) {
            case End1s:
                forceInterface = this.UID1();
                break;
            case End2s:
                forceInterface = this.UID2();
                break;
        }
        if (!StringUtils.isEmpty(forceInterface)) {
            IObject item = CIMContext.Instance.ProcessCache().item(forceInterface, domainInfo.SCHEMA.toString());
            if (item == null)
                throw new Exception("invalid interface definition for UID:" + forceInterface + " under " + this.UID());

            IInterfaceDef interfaceDef = item.toInterface(IInterfaceDef.class);
            List<IDomain> usedDomains = interfaceDef.getUsedDomain();
            if (usedDomains != null && usedDomains.size() > 0)
                result.addAll(usedDomains);
            else {
                result.addAll(CIMContext.Instance.getDefaultDomains());
            }
        }
        return result;
    }

    @Override
    public String Role1() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Role1", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setRole1(String role1) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Role1", true).setValue(role1);
    }

    @Override
    public String Role1DisplayName() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Role1DisplayName", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setRole1DisplayName(String role1DisplayName) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Role1", true).setValue(role1DisplayName);
    }

    @Override
    public String End1Locality() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("End1Locality", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setEnd1Locality(String end1Locality) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("End1Locality", true).setValue(end1Locality);
    }

    @Override
    public int Min1() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Min1", false);
        return ValueConversionUtility.toInteger(property);
    }

    @Override
    public void setMin1(int min1) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Min1", true).setValue(min1);
    }

    @Override
    public String Max1() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Max1", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setMax1(String max1) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Max1", true).setValue(max1);
    }

    @Override
    public String SpecRelDefUID() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("SpecRelDefUID", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setSpecRelDefUID(String specRelDefUID) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("SpecRelDefUID", true).setValue(specRelDefUID);
    }

    @Override
    public boolean IsAbstract() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("IsAbstract", false);
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setIsAbstract(boolean isAbstract) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("IsAbstract", true).setValue(isAbstract);
    }

    @Override
    public String LinkInterfaces() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("LinkInterfaces", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setLinkInterfaces(String linkInterfaces) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("LinkInterfaces", true).setValue(linkInterfaces);
    }

    @Override
    public int Min2() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Min2", false);
        return ValueConversionUtility.toInteger(property);
    }

    @Override
    public void setMin2(String min2) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Min2", true).setValue(min2);
    }

    @Override
    public String Max2() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Max2", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setMax2(String max2) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Max2", true).setValue(max2);
    }

    @Override
    public String End2Locality() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("End2Locality", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setEnd2Locality(String end2Locality) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("End2Locality", true).setValue(end2Locality);
    }

    @Override
    public String Role2() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Role2", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setRole2(String role2) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Role2", true).setValue(role2);
    }

    @Override
    public String Role2DisplayName() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Role2DisplayName", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setRole2DisplayName(String role2DisplayName) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Role2DisplayName", true).setValue(role2DisplayName);
    }

    @Override
    public IRelDef getSpecializationOf() {
        return null;
    }

    @Override
    public IObjectCollection getSpecializesRelDefs() {
        return null;
    }

    @Override
    public IObjectCollection getLinkInterfaceDefs() {
        return null;
    }

    @Override
    public boolean Delete12() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Delete12", false);
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setDelete12(boolean delete12) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Delete12", true).setValue(delete12);
    }

    @Override
    public boolean Delete21() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Delete21", false);
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setDelete21(boolean delete21) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Delete21", true).setValue(delete21);
    }

    @Override
    public String generateUniqueKey() {
        return null;
    }

    @Override
    public String OBID1() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("OBID1", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setOBID1(String obid) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("OBID1", true).setValue(obid);
    }

    @Override
    public String OBID2() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("OBID2", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setOBID2(String obid) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("OBID2", true).setValue(obid);
    }

    @Override
    public String Copy12() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Copy12", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setCopy12(String copy12) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Copy12", true).setValue(copy12);
    }

    @Override
    public String Copy21() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Copy21", false);
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setCopy21(String copy21) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Copy21", true).setValue(copy21);
    }

    @Override
    public boolean Owner12() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Owner12", false);
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public boolean Owner21() throws Exception {
        IProperty property = this.Interfaces().item("IRelDef").Properties().item("Owner21", false);
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setOwner12(boolean owner12) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Owner12", true).setValue(owner12);
    }

    @Override
    public void setOwner21(boolean owner21) throws Exception {
        this.Interfaces().item("IRelDef", true).Properties().item("Owner21", true).setValue(owner21);
    }

    @Override
    public propertyValueType checkPropertyValueType(relDirection direction) throws Exception {
        if (direction == null)
            direction = relDirection._1To2;
        switch (direction) {
            case _2To1:
                if (this.Max1().equalsIgnoreCase("*"))
                    return propertyValueType.SearchRelationship;
                else if (this.Max1().equalsIgnoreCase("1"))
                    return propertyValueType.SearchSingleRelationship;
                break;
            case _1To2:
                if (this.Max2().equalsIgnoreCase("*"))
                    return propertyValueType.SearchRelationship;
                else if (this.Max2().equalsIgnoreCase("1"))
                    return propertyValueType.SearchSingleRelationship;
                break;
        }
        return propertyValueType.StringType;
    }

    @Override
    public boolean checkMandatory(relDirection relDirection) throws Exception {
        if (relDirection == null)
            relDirection = ccm.server.enums.relDirection._1To2;
        boolean result = false;
        switch (relDirection) {
            case _1To2:
                if (this.Min2() == 1)
                    result = true;
                break;
            case _2To1:
                if (this.Min1() == 1)
                    result = true;
                break;
        }
        return result;
    }
}
