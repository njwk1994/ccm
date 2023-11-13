package ccm.server.schema.interfaces.generated;

import ccm.server.args.*;
import ccm.server.context.CIMContext;
import ccm.server.entity.MetaDataRel;
import ccm.server.enums.*;
import ccm.server.helper.HardCodeHelper;
import ccm.server.schema.interfaces.*;
import ccm.server.util.ReentrantLockUtility;
import ccm.server.propertyTypes.objectTypes;
import ccm.server.schema.interfaces.defaults.IObjectDefault;
import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.IPropertyValue;
import ccm.server.utils.ValueConversionUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class IRelBase extends ISchemaObjectBase implements IRel {
    public IRelBase(boolean instantiateRequiredProperties) {
        super("IRel", instantiateRequiredProperties);
    }

    public IRelBase(String pstrInterfaceDefinitionUID, boolean instantiateRequiredProperties) {
        super(pstrInterfaceDefinitionUID, instantiateRequiredProperties);
    }

    @Override
    public String ClassDefinitionUID1() {
        IProperty property = this.getProperty("IRel", "ClassDefinitionUID1");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setClassDefinitionUID1(String value) throws Exception {
        this.setPropertyValue("IRel", "ClassDefinitionUID1", value);
    }

    @Override
    public String ClassDefinitionUID2() {
        IProperty property = this.getProperty("IRel", "ClassDefinitionUID2");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setClassDefinitionUID2(String value) throws Exception {
        this.setPropertyValue("IRel", "ClassDefinitionUID2", value);
    }

    @Override
    public void selfCheck() throws Exception {
        super.selfCheck();
        if (StringUtils.isEmpty(this.OBID()) || StringUtils.isEmpty(this.UID()) || StringUtils.isEmpty(this.UID1()) || StringUtils.isEmpty(this.UID2()) || StringUtils.isEmpty(this.RelDefUID()) || StringUtils.isEmpty(this.DomainUID1()) || StringUtils.isEmpty(this.DomainUID2()) || StringUtils.isEmpty(this.ClassDefinitionUID1()) || StringUtils.isEmpty(this.ClassDefinitionUID2()))
            throw new Exception("invalid required property values as one or more is NULL");
    }

    @Override
    public String generateUID() throws Exception {
        List<String> stringList = new ArrayList<>();
        stringList.add(this.Prefix());
        if (!StringUtils.isEmpty(OBID1())) stringList.add(OBID1());
        else if (StringUtils.isEmpty(UID1())) stringList.add(UID1());
        stringList.add(this.RelDefUID());
        if (!StringUtils.isEmpty(OBID2())) stringList.add(OBID2());
        else if (!StringUtils.isEmpty(UID2())) stringList.add(UID2());
        return stringList.stream().filter(c -> !StringUtils.isEmpty(c)).collect(Collectors.joining("."));
    }

    @Override
    public String generateUniqueKey() throws Exception {
        List<String> collect = Arrays.stream(new String[]{this.Prefix(), this.OBID1(), this.UID1(), this.ClassDefinitionUID1(), this.RelDefUID(), this.OBID2(), this.UID2(), this.ClassDefinitionUID2()}).filter(c -> !StringUtils.isEmpty(c)).collect(Collectors.toList());
        return String.join(",", collect).toUpperCase();
    }

    @Override
    public String OBID1() throws Exception {
        IProperty property = this.getProperty("IRel", "OBID1");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setOBID1(String obid) throws Exception {
        this.setPropertyValue("IRel", "OBID1", obid);
    }

    @Override
    public String OBID2() throws Exception {
        IProperty property = this.getProperty("IRel", "OBID2");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setOBID2(String obid) throws Exception {
        this.setPropertyValue("IRel", "OBID2", obid);
    }

    @Override
    public String RelDefUID() {
        IProperty property = this.getProperty("IRel", "RelDefUID");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setRelDefUID(String relDefUID) throws Exception {
        this.setPropertyValue("IRel", "RelDefUID", relDefUID);
    }

    @Override
    public String DomainUID1() throws Exception {
        IProperty property = this.getProperty("IRel", "DomainUID1");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setDomainUID1(String domainUID1) throws Exception {
        this.setPropertyValue("IRel", "DomainUID1", domainUID1);
    }

    @Override
    public String DomainUID2() throws Exception {
        IProperty property = this.getProperty("IRel", "DomainUID2");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setDomainUID2(String domainUID2) throws Exception {
        this.setPropertyValue("IRel", "DomainUID2", domainUID2);
    }

    @Override
    public String UID1() throws Exception {
        IProperty property = this.getProperty("IRel", "UID1");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setUID1(String uid1) throws Exception {
        this.setPropertyValue("IRel", "UID1", uid1);
    }

    @Override
    public String UID2() throws Exception {
        IProperty property = this.getProperty("IRel", "UID2");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setUID2(String uid2) throws Exception {
        this.setPropertyValue("IRel", "UID2", uid2);
    }

    @Override
    public String Prefix() throws Exception {
        IProperty property = this.getProperty("IRel", "Prefix");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setPrefix(String prefix) throws Exception {
        this.setPropertyValue("IRel", "Prefix", prefix);
    }

    @Override
    public boolean IsRequired() throws Exception {
        IProperty property = this.getProperty("IRel", "IsRequired");
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setIsRequired(boolean isRequired) throws Exception {
        this.setPropertyValue("IRel", "IsRequired", isRequired);
    }

    @Override
    public Integer OrderValue() {
        IProperty property = this.getProperty("IRel", "OrderValue");
        return ValueConversionUtility.toInteger(property);
    }

    @Override
    public void setOrderValue(int orderValue) throws Exception {
        this.setPropertyValue("IRel", "OrderValue", orderValue);
    }

    @Override
    public String Name1() throws Exception {
        IProperty property = this.getProperty("IRel", "Name1");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setName1(String name1) throws Exception {
        this.setPropertyValue("IRel", "Name1", name1);
    }

    @Override
    public String Name2() throws Exception {
        IProperty property = this.getProperty("IRel", "Name2");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setName2(String name2) throws Exception {
        this.setPropertyValue("IRel", "Name2", name2);
    }

    @Override
    public IObject GetEnd1() throws Exception {
        return this.GetEnd1(null);
    }

    @Override
    public IObject GetEnd1(String configurationUid) throws Exception {
        String obid = this.OBID1();
        String uid = this.UID1();
        String domainUID = this.DomainUID1();
        String classDefinitionUID = this.ClassDefinitionUID1();
        return this.doGetEnd(obid, uid, domainUID, classDefinitionUID, relCollectionTypes.End1s, configurationUid);
    }

    @Override
    public IObject GetEnd2(String configurationUid) throws Exception {
        String obid = this.OBID2();
        String uid = this.UID2();
        String domainUID = this.DomainUID2();
        String classDefinitionUID = this.ClassDefinitionUID2();
        return this.doGetEnd(obid, uid, domainUID, classDefinitionUID, relCollectionTypes.End2s, configurationUid);
    }

    protected IObject doGetEnd(String obid, String uid, String domainUID, String classDefinitionUID, relCollectionTypes collectionTypes, String configurationUid) throws Exception {
        IObject result = null;
        if (StringUtils.isEmpty(obid) && StringUtils.isEmpty(uid)) return null;

        if (!StringUtils.isEmpty(obid)) {
            if (CIMContext.Instance.Transaction().inTransaction())
                result = CIMContext.Instance.Transaction().getByObid(obid);
            if (result == null) result = CIMContext.Instance.ProcessCache().getObjectByOBIDCache(obid);
        }

        ICIMConfigurationItem myConfigurationItem = null;

        if (result == null) {
            if (StringUtils.isEmpty(configurationUid))
                myConfigurationItem = CIMContext.Instance.getMyConfigurationItem(null);
            else {
                IObject configurationItem = CIMContext.Instance.ProcessCache().item(configurationUid, null, false);
                if (configurationItem != null)
                    myConfigurationItem = configurationItem.toInterface(ICIMConfigurationItem.class);
            }

            if (!StringUtils.isEmpty(uid)) {
                if (CIMContext.Instance.ProcessCache().isDefinitionUnderConfigControl(classDefinitionUID)) {
                    if (myConfigurationItem == null) throw new Exception("current configuration item is null");
                    result = CIMContext.Instance.ProcessCache().getObjectByUIDAndDomainUIDCache(uid, domainUID, myConfigurationItem.UID(), false);
                } else result = CIMContext.Instance.ProcessCache().item(uid, domainUID);
            }
        }

        if (result != null && !result.fromDb()) result = null;

        if (result == null && !StringUtils.isEmpty(obid)) {
            result = CIMContext.Instance.QueryEngine().getObjectByOBIDAndClassDefinitionUID(obid, classDefinitionUID, myConfigurationItem);
            if (result != null) {
                CIMContext.Instance.ProcessCache().refresh(result);
            }
        }

        if (result == null && !StringUtils.isEmpty(uid)) {
            result = CIMContext.Instance.QueryEngine().getObjectByUIDAndClassDefinitionUID(uid, classDefinitionUID, myConfigurationItem);
            if (result != null) {
                CIMContext.Instance.ProcessCache().refresh(result);
            }
        }

        if (result != null) {
            switch (collectionTypes) {
                case End1s:
                    this.setEnd1(result.OBID(), result.UID(), result.DomainUID(), result.ClassDefinitionUID(), result.Name());
                    break;
                case End2s:
                    this.setEnd2(result.OBID(), result.UID(), result.DomainUID(), result.ClassDefinitionUID(), result.Name());
                    break;
            }
        }

        return result;
    }

    @Override
    public IObject GetEnd2() throws Exception {
        return this.GetEnd2(null);
    }

    @Override
    public IRelDef GetRelationshipDefinition() throws Exception {
        return CIMContext.Instance.ProcessCache().item(this.RelDefUID(), domainInfo.SCHEMA.toString()).toInterface(IRelDef.class);
    }

    protected void doSetEnd(String obid, String uid, String domainUID, String classDefinitionUID, String name, relCollectionTypes collectionTypes) throws Exception {
        log.trace("enter to do set End");
        String currentOBID = collectionTypes == relCollectionTypes.End1s ? this.OBID1() : this.OBID2();
        if (!StringUtils.isEmpty(currentOBID)) {
            IObject lobjOldEndObject = CIMContext.Instance.ProcessCache().getObjectByOBIDCache(currentOBID);
            if (lobjOldEndObject != null) {
                switch (collectionTypes) {
                    case End1s:
                        if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
                            lobjOldEndObject.GetEnd1Relationships().remove(this);
                        else if (this.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.RelDef.toString()))
                            lobjOldEndObject.toInterface(IInterfaceDef.class).getEnd1RelDefs().remove(this);
                        break;
                    case End2s:
                        if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
                            lobjOldEndObject.GetEnd2Relationships().remove(this);
                        else if (this.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.RelDef.toString()))
                            lobjOldEndObject.toInterface(IInterfaceDef.class).getEnd2RelDefs().remove(this);
                        break;
                }
            }
        }

        switch (collectionTypes) {
            case End1s:
                if (!StringUtils.isEmpty(obid) && !obid.equalsIgnoreCase(this.OBID1())) this.setOBID1(obid);
                if (!StringUtils.isEmpty(uid) && !uid.equalsIgnoreCase(this.UID1())) this.setUID1(uid);
                if (!StringUtils.isEmpty(domainUID) && !domainUID.equalsIgnoreCase(this.DomainUID1()))
                    this.setDomainUID1(domainUID);
                if (!StringUtils.isEmpty(name) && !name.equalsIgnoreCase(this.Name1())) this.setName1(name);
                if (!StringUtils.isEmpty(classDefinitionUID) && !classDefinitionUID.equalsIgnoreCase(this.ClassDefinitionUID1()))
                    this.setClassDefinitionUID1(classDefinitionUID);
                break;
            case End2s:
                if (!StringUtils.isEmpty(obid) && !obid.equalsIgnoreCase(this.OBID2())) this.setOBID2(obid);
                if (!StringUtils.isEmpty(uid) && !uid.equalsIgnoreCase(this.UID2())) this.setUID2(uid);
                if (!StringUtils.isEmpty(domainUID) && !domainUID.equalsIgnoreCase(this.DomainUID2()))
                    this.setDomainUID2(domainUID);
                if (!StringUtils.isEmpty(name) && !name.equalsIgnoreCase(this.Name2())) this.setName2(name);
                if (!StringUtils.isEmpty(classDefinitionUID) && !classDefinitionUID.equalsIgnoreCase(this.ClassDefinitionUID2()))
                    this.setClassDefinitionUID2(classDefinitionUID);
                break;
        }

        IObject lobjNewEndObject = CIMContext.Instance.ProcessCache().getObjectByOBIDCache(obid);
        if (lobjNewEndObject != null) {
            switch (collectionTypes) {
                case End1s:
                    if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
                        lobjNewEndObject.GetEnd1Relationships().add(this);
                    else if (this.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.RelDef.toString()))
                        lobjNewEndObject.toInterface(IInterfaceDef.class).getEnd1RelDefs().append(this);
                    break;
                case End2s:
                    if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
                        lobjNewEndObject.GetEnd2Relationships().add(this);
                    else if (this.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.RelDef.toString()))
                        lobjNewEndObject.toInterface(IInterfaceDef.class).getEnd2RelDefs().append(this);
                    break;
            }
        }
    }

    @Override
    public void setEnd1(String obid, String uid, String domainUID, String classDefinitionUID, String name) throws Exception {
        if (this.Lock().isWriteLocked()) {
            this.doSetEnd(obid, uid, domainUID, classDefinitionUID, name, relCollectionTypes.End1s);
        }
    }

    @Override
    public void setEnd2(String obid, String uid, String domainUID, String classDefinitionUID, String name) throws Exception {
        if (this.Lock().isWriteLocked()) {
            this.doSetEnd(obid, uid, domainUID, classDefinitionUID, name, relCollectionTypes.End2s);
        }
    }

    @Override
    public void resetWithProvidedIObjectAsNewCache(IObject iObject) {
        if (iObject != null) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.Lock());
                this.clearAllProperties();
                this.setRelProperties(iObject);
                this.syncInterfaceAndProperties(iObject);
            } catch (Exception exception) {
                log.error("reset with provided IObject as new cache");
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
            }
        }
    }

    protected void setRelProperties(IObject iObject) throws Exception {
        if (iObject != null) {
            IRel rel = iObject.toInterface(IRel.class);
            this.setPropertyValue(interfaceDefinitionType.IRel.toString(), propertyDefinitionType.OBID1.toString(), rel.OBID1());
            this.setPropertyValue(interfaceDefinitionType.IRel.toString(), propertyDefinitionType.OBID2.toString(), rel.OBID2());
            this.setPropertyValue(interfaceDefinitionType.IRel.toString(), propertyDefinitionType.Name1.toString(), rel.Name1());
            this.setPropertyValue(interfaceDefinitionType.IRel.toString(), propertyDefinitionType.Name2.toString(), rel.Name2());
            this.setPropertyValue(interfaceDefinitionType.IRel.toString(), propertyDefinitionType.UID1.toString(), rel.UID1());
            this.setPropertyValue(interfaceDefinitionType.IRel.toString(), propertyDefinitionType.UID2.toString(), rel.UID2());
            this.setPropertyValue(interfaceDefinitionType.IRel.toString(), propertyDefinitionType.RelDefUID.toString(), rel.RelDefUID());
            this.setPropertyValue(interfaceDefinitionType.IRel.toString(), propertyDefinitionType.Prefix.toString(), rel.Prefix());
            this.setPropertyValue(interfaceDefinitionType.IRel.toString(), propertyDefinitionType.IsRequired.toString(), rel.IsRequired());
            this.setPropertyValue(interfaceDefinitionType.IRel.toString(), propertyDefinitionType.OrderValue.toString(), rel.OrderValue());
            this.setPropertyValue(interfaceDefinitionType.IRel.toString(), propertyDefinitionType.ClassDefinitionUID1.toString(), rel.ClassDefinitionUID1());
            this.setPropertyValue(interfaceDefinitionType.IRel.toString(), propertyDefinitionType.ClassDefinitionUID2.toString(), rel.ClassDefinitionUID2());
        }
    }

    protected void syncInterfaceAndProperties(IObject iObject) throws Exception {
        if (iObject != null) {
            Iterator<Map.Entry<String, IInterface>> entryIterator = iObject.Interfaces().GetEnumerator();
            while (entryIterator.hasNext()) {
                IInterface anInterface = entryIterator.next().getValue();
                if (!anInterface.InterfaceDefinitionUID().equalsIgnoreCase(interfaceDefinitionType.IRel.toString())) {
                    IInterface current = null;
                    if (!this.hasInterface(anInterface.InterfaceDefinitionUID()))
                        current = this.Interfaces().item(anInterface.InterfaceDefinitionUID());
                    else current = this.Interfaces().get(anInterface.InterfaceDefinitionUID());
                    if (current != null) {
                        Iterator<Map.Entry<String, IProperty>> entryIterator1 = anInterface.Properties().GetEnumerator();
                        while (entryIterator1.hasNext()) {
                            IProperty property = entryIterator1.next().getValue();
                            current.Properties().item(property.getPropertyDefinitionUid()).setValue(property.Value());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void OnCreate(createArgs e) throws Exception {
        super.OnCreate(e);
        if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            ICIMConfigurationItem configurationItem = e.getConfigurationItem();
            String configurationUid = configurationItem != null ? configurationItem.UID() : "";
            IObject end1 = this.GetEnd1(configurationUid);
            if (end1 != null)
                end1.toInterface(IObject.class).OnRelationshipAdd(new relArgs(this, relDirection._1To2, e.getUsername(), e.getConfigurationItem()));

            IObject end2 = this.GetEnd2(configurationUid);
            if (end2 != null)
                end2.toInterface(IObject.class).OnRelationshipAdd(new relArgs(this, relDirection._2To1, e.getUsername(), e.getConfigurationItem()));
        }
    }

    @Override
    public void OnCreated(suppressibleArgs e) throws Exception {
        if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            ICIMConfigurationItem configurationItem = e.getConfigurationItem();
            String configurationUid = configurationItem != null ? configurationItem.UID() : "";
            IObject end1 = this.GetEnd1(configurationUid);
            if (end1 != null)
                end1.toInterface(IObject.class).OnRelationshipAdded(new relArgs(this, relDirection._1To2, e.getUsername(), e.getConfigurationItem()));

            IObject end2 = this.GetEnd2(configurationUid);
            if (end2 != null)
                end2.toInterface(IObject.class).OnRelationshipAdded(new relArgs(this, relDirection._2To1, e.getUsername(), e.getConfigurationItem()));
        }
        super.OnCreated(e);
    }

    @Override
    public void OnCreating(cancelArgs e) throws Exception {
        super.OnCreating(e);
        if (!e.isCancel() && this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            ICIMConfigurationItem configurationItem = e.getConfigurationItem();
            String configurationUid = configurationItem != null ? configurationItem.UID() : "";

            objectTypes objectTypes = new objectTypes(this.GetRelationshipDefinition().LinkInterfaces());
            if (objectTypes.hasValue()) {
                Iterator<Object> objectIterator = objectTypes.GetEnumerator();
                while (objectIterator.hasNext()) {
                    String interfaceDefinitionUid = objectIterator.next().toString();
                    if (!this.hasInterface(interfaceDefinitionUid)) {
                        e.setCancel(true);
                        e.setException(new Exception("link interface was missing for" + interfaceDefinitionUid));
                        e.raiseError();
                    }
                }
                relArgs relArgs1 = new relArgs(this, relDirection._1To2, e.getUsername(), e.getConfigurationItem());
                IObject end1 = this.GetEnd1(configurationUid);
                if (end1 != null) {
                    end1.toInterface(IObject.class).OnRelationshipAdding(relArgs1);
                    if (relArgs1.isCancel()) {
                        e.setCancel(true);
                        e.setCancelMessage(relArgs1.getCancelMessage());
                        e.setException(relArgs1.getException());
                        e.raiseError();
                    }
                }
                relArgs relArgs2 = new relArgs(this, relDirection._2To1, e.getUsername(), e.getConfigurationItem());
                IObject end2 = this.GetEnd2(configurationUid);
                if (end2 != null) {
                    end2.toInterface(IObject.class).OnRelationshipAdding(relArgs2);
                    if (relArgs1.isCancel()) {
                        e.setCancel(true);
                        e.setCancelMessage(relArgs2.getCancelMessage());
                        e.setException(relArgs2.getException());
                        e.raiseError();
                    }
                }
            }
        }
    }

    @Override
    public void OnCreatingValidation(cancelArgs e) throws Exception {
        if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            IRelDef relDef = this.GetRelationshipDefinition();
            if (relDef == null) {
                e.setCancel(true);
                e.setCancelMessage("invalid relationship definition UID with " + this.RelDefUID());
                e.setException(new Exception(e.getCancelMessage()));
            }
            if (relDef.IsAbstract()) {
                // to add abstract logic for validation;
                log.info("relationship is abstract to be add logic in future");
            }

            objectTypes objectTypes = new objectTypes(this.GetRelationshipDefinition().LinkInterfaces());
            if (objectTypes.hasValue()) {
                Iterator<Object> objectIterator = objectTypes.GetEnumerator();
                while (objectIterator.hasNext()) {
                    String linkInterface = objectIterator.next().toString();
                    IInterface current = this.Interfaces().item(linkInterface, true);
                }
            }
        }
        super.OnCreatingValidation(e);
    }

    @Override
    public void OnDeleted(suppressibleArgs e) throws Exception {
        if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            this.relationshipsRemove(e);
        }
        super.OnDeleted(e);
    }

    protected Map.Entry<IObject, IObject> relationshipsRemove(suppressibleArgs e) throws Exception {
        IObject lobjEnd1 = null;
        IObject lobjEnd2 = null;
        if (!StringUtils.isEmpty(this.OBID1()) && CIMContext.Instance.ProcessCache().containsByOBID(this.OBID1()))
            lobjEnd1 = CIMContext.Instance.ProcessCache().getObjectByOBIDCache(this.OBID1());
        if (!StringUtils.isEmpty(this.OBID2()) && CIMContext.Instance.ProcessCache().containsByOBID(this.OBID2()))
            lobjEnd2 = CIMContext.Instance.ProcessCache().getObjectByOBIDCache(this.OBID2());
        if (lobjEnd1 != null)
            lobjEnd1.toInterface(IObject.class).OnRelationshipRemoved(new relArgs(this, relDirection._1To2, e.getUsername(), e.getConfigurationItem()));
        if (lobjEnd2 != null)
            lobjEnd2.toInterface(IObject.class).OnRelationshipRemoved(new relArgs(this, relDirection._2To1, e.getUsername(), e.getConfigurationItem()));
        return new AbstractMap.SimpleEntry<>(lobjEnd1, lobjEnd2);
    }

    @Override
    public void OnDeleting(cancelArgs e) throws Exception {
        super.OnDeleting(e);
        if (!e.isCancel() && this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            ICIMConfigurationItem configurationItem = e.getConfigurationItem();
            String configurationUid = configurationItem != null ? configurationItem.UID() : "";

            relArgs relArgs1 = new relArgs(this, relDirection._1To2, e.getUsername(), e.getConfigurationItem());
            IObject end1 = this.GetEnd1(configurationUid);
            if (end1 != null) {
                end1.GetEnd1Relationships().remove(this);
                ((IObjectDefault) end1.toInterface(IObject.class)).End1RelsForRemoval().add(this);
                CIMContext.Instance.Transaction().addDeferredOnRelationshipRemoving(end1, interfaceDefinitionType.IObject.toString(), deferredMethods.OnRelationshipRemoving.toString(), new Object[]{relArgs1});
            }
            relArgs relArgs2 = new relArgs(this, relDirection._2To1, e.getUsername(), e.getConfigurationItem());
            IObject end2 = this.GetEnd2(configurationUid);
            if (end2 != null) {
                end2.GetEnd2Relationships().remove(this);
                ((IObjectDefault) end2.toInterface(IObject.class)).End2RelsForRemoval().add(this);
                CIMContext.Instance.Transaction().addDeferredOnRelationshipRemoving(end2, interfaceDefinitionType.IObject.toString(), deferredMethods.OnRelationshipRemoving.toString(), new Object[]{relArgs2});
            }
        }
    }

    @Override
    public void OnTerminated(suppressibleArgs e) throws Exception {
        super.OnTerminated(e);
        if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            Map.Entry<IObject, IObject> objectEntry = this.relationshipsRemove(e);
            this.onRemoveEnd1AndEnd2s(objectEntry.getKey(), objectEntry.getValue());
        }
    }

    protected void onRemoveEnd1AndEnd2s() throws Exception {
        IObject end1 = this.GetEnd1();
        if (end1 != null) end1.GetEnd1Relationships().remove(this);
        IObject end2 = this.GetEnd2();
        if (end2 != null) end2.GetEnd2Relationships().remove(this);

    }

    protected void onRemoveEnd1AndEnd2s(IObject end1, IObject end2) throws Exception {
        if (end1 != null) end1.GetEnd1Relationships().remove(this);
        if (end2 != null) end2.GetEnd2Relationships().remove(this);
    }

    @Override
    public void OnTerminating(cancelArgs e) throws Exception {
        super.OnTerminating(e);
        if (!e.isCancel() && this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            ICIMConfigurationItem configurationItem = e.getConfigurationItem();
            String configurationUid = configurationItem != null ? configurationItem.UID() : "";

            relArgs relArgs1 = new relArgs(this, relDirection._1To2, e.getUsername(), e.getConfigurationItem());
            IObject end1 = this.GetEnd1(configurationUid);
            if (end1 != null) {
                end1.GetEnd1Relationships().remove(this);
                ((IObjectDefault) end1.toInterface(IObject.class)).End1RelsForRemoval().add(this);
                CIMContext.Instance.Transaction().addDeferredOnRelationshipRemoving(end1, interfaceDefinitionType.IObject.toString(), deferredMethods.OnRelationshipRemoving.toString(), new Object[]{relArgs1});
            }
            relArgs relArgs2 = new relArgs(this, relDirection._2To1, e.getUsername(), e.getConfigurationItem());
            IObject end2 = this.GetEnd2(configurationUid);
            if (end2 != null) {
                end2.GetEnd2Relationships().remove(this);
                ((IObjectDefault) end2.toInterface(IObject.class)).End2RelsForRemoval().add(this);
                CIMContext.Instance.Transaction().addDeferredOnRelationshipRemoving(end2, interfaceDefinitionType.IObject.toString(), deferredMethods.OnRelationshipRemoving.toString(), new Object[]{relArgs2});
            }
        }
    }

    @Override
    public void OnUpdating(cancelArgs e) throws Exception {
        super.OnUpdating(e);
        if (!e.isCancel() && this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            ICIMConfigurationItem configurationItem = e.getConfigurationItem();
            String configurationUid = configurationItem != null ? configurationItem.UID() : "";

            relArgs relArgs1 = new relArgs(this, relDirection._1To2, e.getUsername(), e.getConfigurationItem());
            IObject end1 = this.GetEnd1(configurationUid);
            if (end1 != null) {
                end1.toInterface(IObject.class).OnRelationshipUpdating(relArgs1);
                if (relArgs1.isCancel()) {
                    e.setCancel(true);
                    e.setCancelMessage(relArgs1.getCancelMessage());
                    e.setException(relArgs1.getException());
                    e.raiseError();
                }
            }
            relArgs relArgs2 = new relArgs(this, relDirection._2To1, e.getUsername(), e.getConfigurationItem());
            IObject end2 = this.GetEnd2(configurationUid);
            if (end2 != null) {
                end2.toInterface(IObject.class).OnRelationshipUpdating(relArgs2);
                if (relArgs2.isCancel()) {
                    e.setCancel(true);
                    e.setCancelMessage(relArgs2.getCancelMessage());
                    e.setException(relArgs1.getException());
                    e.raiseError();
                }
            }
        }
    }

    @Override
    public void OnUpdatingValidation(cancelArgs e) throws Exception {
        if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            //to add logic of validation for updating
        }
        super.OnUpdatingValidation(e);
    }

    @Override
    public void OnValidate(validateArgs e) throws Exception {
        if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            if (this.ClassBase().UpdateState() == objectUpdateState.created || this.ClassBase().UpdateState() == objectUpdateState.none) {
                //to add logic of validation for relationship
            }
        }
        super.OnValidate(e);
    }

    @Override
    public void Terminate() throws Exception {
        super.Terminate();
    }

    private final List<String> propertiesUsedToBeIdentifiesForState = new ArrayList<String>() {{
        add(propertyDefinitionType.Name1.toString());
        add(propertyDefinitionType.Name2.toString());
        add(propertyDefinitionType.Prefix.toString());
        add(propertyDefinitionType.IsRequired.toString());
        add(propertyDefinitionType.OrderValue.toString());
    }};

    @Override
    public MetaDataRel toMetaDataRel() throws Exception {
        String lstrDomainUID = this.DomainUID();
        if (StringUtils.isEmpty(lstrDomainUID)) lstrDomainUID = this.DomainUID1();
        if (StringUtils.isEmpty(lstrDomainUID)) lstrDomainUID = this.DomainUID2();
        MetaDataRel metaDataRel = new MetaDataRel();
        metaDataRel.setIsRequired(this.IsRequired());
        metaDataRel.setRelDefUid(this.RelDefUID());
        metaDataRel.setOrderValue(this.OrderValue());
        metaDataRel.setPrefix(this.Prefix());
        metaDataRel.setUpdateState(this.ObjectUpdateState());
        metaDataRel.setObid(this.OBID());
        metaDataRel.setObid1(this.OBID1());
        metaDataRel.setObid2(this.OBID2());
        metaDataRel.setClassDefinitionUid1(this.ClassDefinitionUID1());
        metaDataRel.setClassDefinitionUid2(this.ClassDefinitionUID2());
        metaDataRel.setDomainUid(lstrDomainUID);
        metaDataRel.setDomainUid1(this.DomainUID1());
        metaDataRel.setDomainUid2(this.DomainUID2());
        metaDataRel.setUid1(this.UID1());
        metaDataRel.setUid2(this.UID2());
        metaDataRel.setObjUid(this.UID());
        metaDataRel.setName1(this.Name1());
        metaDataRel.setName2(this.Name2());
        metaDataRel.setConfig(this.getConfigForMetaData());
        metaDataRel.setCreationUser(this.CreationUser());
        metaDataRel.setCreationDate(this.CreationDate());
        metaDataRel.setTerminationUser(this.TerminationUser());
        metaDataRel.setTerminationDate(this.TerminationDate());

        if (metaDataRel.getUpdateState() != objectUpdateState.created && metaDataRel.getUpdateState() != objectUpdateState.deleted && metaDataRel.getUpdateState() != objectUpdateState.terminated) {
            for (String p : this.propertiesUsedToBeIdentifiesForState) {
                IProperty iProperty = this.Properties().get(p);
                if (iProperty != null) {
                    IPropertyValue iPropertyValue = iProperty.CurrentValue();
                    if (iPropertyValue != null && iPropertyValue.UpdateState() != propertyValueUpdateState.none) {
                        metaDataRel.setUpdateState(objectUpdateState.updated);
                        break;
                    }
                }
            }
        }
        return metaDataRel;
    }

    @Override
    public boolean isOwner(IObject pobjIObject) throws Exception {
        if (pobjIObject != null) {
            IRelDef relDef = this.GetRelationshipDefinition();
            if (this.OBID1().equalsIgnoreCase(pobjIObject.OBID())) return relDef.Owner12();
            else if (this.OBID2().equalsIgnoreCase(pobjIObject.OBID())) return relDef.Owner21();
        }
        return false;
    }

}
