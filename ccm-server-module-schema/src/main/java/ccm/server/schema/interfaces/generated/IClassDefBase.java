package ccm.server.schema.interfaces.generated;

import ccm.server.args.cancelArgs;
import ccm.server.args.createArgs;
import ccm.server.args.suppressibleArgs;
import ccm.server.context.CIMContext;
import ccm.server.enums.*;
import ccm.server.helper.HardCodeHelper;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.IProperty;
import ccm.server.util.ReentrantLockUtility;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
public abstract class IClassDefBase extends ISchemaObjectBase implements IClassDef {
    public IClassDefBase(boolean instantiateRequiredProperties) {
        super("IClassDef", instantiateRequiredProperties);
    }

    @Override
    public boolean isScopeWised() throws Exception {
        boolean result = false;
        IDomain domain = this.getDomainForInstantiating();
        if (domain != null)
            result = domain.ScopeWiseInd();
        return result;
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
    public String getTablePrefixForInstantiating() {
        return CIMContext.Instance.ProcessCache().getTablePrefixForClassDefinition(this.UID());
    }

    @Override
    public IObject getDomainInfo() throws Exception {
        return CIMContext.Instance.ProcessCache().getDomainForClassDef(this.UID());
    }

    @Override
    public IDomain getDomainForInstantiating() throws Exception {
        IObject domain = this.getDomainInfo();
        if (domain != null)
            return domain.toInterface(IDomain.class);
        return null;
    }

    @Override
    public List<String> getUsedDomain() {
        List<String> result = new ArrayList<>();
        result.add(this.getTablePrefixForInstantiating());
        return result;
    }

    @Override
    public String UniqueKeyPattern() {
        IProperty property = this.getProperty("IClassDef", "UniqueKeyPattern");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setUniqueKeyPattern(String uniqueKeyDefinition) throws Exception {
        this.setPropertyValue("IClassDef", "UniqueKeyPattern", uniqueKeyDefinition);
    }

    @Override
    public String SystemIDPattern() {
        IProperty property = this.getProperty("IClassDef", "SystemIDPattern");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setSystemIDPattern(String systemIDPattern) throws Exception {
        this.setPropertyValue("IClassDef", "SystemIDPattern", systemIDPattern);
    }

    @Override
    public IObject instantiate(boolean instantiateRequiredItems) throws Exception {
        return (IObject) ((ISchemaObject) this.myNext(interfaceDefinitionType.ISchemaObject.toString(), new ArrayList<>())).Instantiate(instantiateRequiredItems);
    }

    @Override
    public IObject BeginCreate() throws Exception {
        return this.BeginCreate(true);
    }

    @Override
    public IObject BeginCreate(boolean pblnInstantiateRequiredItems) throws Exception {
        IObject result = null;
        if (!CIMContext.Instance.Transaction().inTransaction())
            throw new Exception("a transaction shall be started before DML operation");
        result = this.instantiate(pblnInstantiateRequiredItems);
        ReentrantLockUtility.tryToAcquireWriteLock(result.Lock());
        result.ClassBase().setUpdateState(objectUpdateState.created);
        result.Interfaces().item(interfaceDefinitionType.IObject.toString()).Properties().item(propertyDefinitionType.ClassDefinitionUID.toString(), true).setValue(this.UID());
        return result;
    }

    private IObject onGetObjByInterfaceDefAndUID(String pstrInterfaceDef, String pstrUID) throws Exception {
        if (!StringUtils.isEmpty(pstrInterfaceDef) && !StringUtils.isEmpty(pstrUID)) {
            IObject item = CIMContext.Instance.ProcessCache().item(pstrUID, false);
            if (item != null && item.Interfaces().hasInterface(pstrInterfaceDef))
                return item;
        }
        return null;
    }

    protected void finishEndInfo(IObject pobjObject, IRelDef relDef, relCollectionTypes relCollectionTypes) throws Exception {
        if (pobjObject != null && relDef != null && pobjObject.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            IObject end = null;
            IRel rel = pobjObject.toInterface(IRel.class);
            String obid = relCollectionTypes == ccm.server.enums.relCollectionTypes.End1s ? rel.OBID1() : rel.OBID2();
            String uid = relCollectionTypes == ccm.server.enums.relCollectionTypes.End1s ? rel.UID1() : rel.UID2();
            String domainUID = relCollectionTypes == ccm.server.enums.relCollectionTypes.End1s ? rel.DomainUID1() : rel.DomainUID2();
            String classDefinitionUID = relCollectionTypes == ccm.server.enums.relCollectionTypes.End1s ? rel.ClassDefinitionUID1() : rel.ClassDefinitionUID2();
            String name = relCollectionTypes == ccm.server.enums.relCollectionTypes.End1s ? rel.Name1() : rel.Name2();

            if (!StringUtils.isEmpty(obid) && !StringUtils.isEmpty(uid) && !StringUtils.isEmpty(domainUID) && !StringUtils.isEmpty(classDefinitionUID))
                return;
            if (!StringUtils.isEmpty(obid)) {
                if (CIMContext.Instance.ProcessCache().containsByOBID(obid))
                    end = CIMContext.Instance.ProcessCache().getObjectByOBIDCache(obid);
            }
            if (end == null) {
                if (StringUtils.isEmpty(uid))
                    throw new Exception("UID1 is not set on relationship End1");

                String interfaceDefinitionUID = relCollectionTypes == ccm.server.enums.relCollectionTypes.End1s ? relDef.UID1() : relDef.UID2();
                end = CIMContext.Instance.ProcessCache().item(uid, domainUID);
                if (end == null)
                    end = CIMContext.Instance.ProcessCache().item(uid, null);
                if (end != null && !end.IsTypeOf(interfaceDefinitionUID))
                    end = null;
            }
            if (end != null) {
                switch (relCollectionTypes) {
                    case End1s:
                        if (StringUtils.isEmpty(rel.DomainUID1()))
                            rel.setDomainUID1(end.DomainUID());
                        if (StringUtils.isEmpty(rel.Name1()))
                            rel.setName1(end.Name());
                        if (StringUtils.isEmpty(rel.UID1()))
                            rel.setDomainUID1(end.UID());
                        if (StringUtils.isEmpty(rel.ClassDefinitionUID1()))
                            rel.setClassDefinitionUID1(end.ClassDefinitionUID());
                        if (StringUtils.isEmpty(rel.OBID1()))
                            rel.setOBID1(end.OBID());
                        end.GetEnd1Relationships().add(rel);
                        break;
                    case End2s:
                        if (StringUtils.isEmpty(rel.DomainUID2()))
                            rel.setDomainUID2(end.DomainUID());
                        if (StringUtils.isEmpty(rel.Name2()))
                            rel.setName2(end.Name());
                        if (StringUtils.isEmpty(rel.UID2()))
                            rel.setDomainUID2(end.UID());
                        if (StringUtils.isEmpty(rel.ClassDefinitionUID2()))
                            rel.setClassDefinitionUID2(end.ClassDefinitionUID());
                        if (StringUtils.isEmpty(rel.OBID2()))
                            rel.setOBID2(end.OBID());
                        end.GetEnd2Relationships().add(rel);
                        break;
                }
            }
        }
    }

    protected void finishEndInfo(IObject pobjObject, relCollectionTypes relCollectionTypes) throws Exception {
        if (pobjObject != null && pobjObject.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            IRel rel = pobjObject.toInterface(IRel.class);
            if (CIMContext.Instance.ProcessCache().item(rel.RelDefUID(), domainInfo.SCHEMA.toString()) == null)
                throw new Exception("invalid rel def for relationship creation");
            IObject lobjRelDef = CIMContext.Instance.ProcessCache().item(rel.RelDefUID(), domainInfo.SCHEMA.toString());
            this.finishEndInfo(pobjObject, lobjRelDef.toInterface(IRelDef.class), relCollectionTypes);
        }
    }

    @Override
    public void tryToSetIObjectConfig(IObject object) throws Exception {
        if (object != null) {
            String configInfo = "";
            boolean configControlled = false;
            ICIMConfigurationItem currentScope = CIMContext.Instance.Transaction().getConfigurationItem();
            String currentConfig = currentScope != null ? currentScope.generateIObjectConfig() : "";
            if (!StringUtils.isEmpty(currentConfig)) {
                if (!object.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                    configControlled = this.isScopeWised();
                    if (configControlled) {
                        configInfo = currentConfig;
//                        String uid = object.UID();
//                        if (!uid.contains(Objects.requireNonNull(currentScope).Name())) {
//                            uid = uid + "_" + currentScope.Name();
//                            object.setUID(uid);
//                        }
                    }
                } else {
                    IRel rel = object.toInterface(IRel.class);
                    String relDefUID = rel.RelDefUID();
                    if (!StringUtils.isEmpty(relDefUID)) {
                        if (CIMContext.Instance.ProcessCache().isDefinitionUnderConfigControl(rel.ClassDefinitionUID1()) || CIMContext.Instance.ProcessCache().isDefinitionUnderConfigControl(rel.ClassDefinitionUID2()))
                            configInfo = currentConfig;
                    }
                }
            }
            object.setConfig(configInfo);
        }
    }

    @Override
    public void FinishCreate(IObject obj) {
        if (obj != null) {
            try {
                String username = CIMContext.Instance.Transaction().getLoginUser();
                ICIMConfigurationItem configurationItem = CIMContext.Instance.Transaction().getConfigurationItem();
                if (StringUtils.isEmpty(obj.OBID()))
                    obj.setOBID(CIMContext.Instance.generateOBIDForObject());

                if (StringUtils.isEmpty(obj.UID())) {
                    String uidPattern = this.SystemIDPattern();
                    if (!StringUtils.isEmpty(uidPattern))
                        obj.ClassBase().generateUID();
                    else
                        obj.setUID(UUID.randomUUID().toString());
                }

                String classDefinitionUID = obj.ClassDefinitionUID();
                obj.ClassBase().SetObjectOBIDs();
                this.tryToSetIObjectConfig(obj);
                if (classDefinitionUID.equalsIgnoreCase(classDefinitionType.RelDef.toString())) {
                    IRelDef relDef = obj.toInterface(IRelDef.class);
                    IObject endObject1 = null;
                    IObject endObject2 = null;

                    if (!StringUtils.isEmpty(relDef.OBID1()))
                        endObject1 = CIMContext.Instance.ProcessCache().getObjectByOBIDCache(relDef.OBID1());
                    else {
                        if (StringUtils.isEmpty(relDef.UID1()))
                            throw new Exception("UID1 is not set on relationship");
                        endObject1 = CIMContext.Instance.ProcessCache().item(relDef.UID1());
                    }

                    if (!StringUtils.isEmpty(relDef.OBID2()))
                        endObject2 = CIMContext.Instance.ProcessCache().getObjectByOBIDCache(relDef.OBID2());
                    else {
                        if (StringUtils.isEmpty(relDef.UID2()))
                            throw new Exception("UID2 is not set on relationship");
                        endObject2 = CIMContext.Instance.ProcessCache().item(relDef.UID2());
                    }

                    if (StringUtils.isEmpty(relDef.DomainUID()))
                        relDef.setDomainUID(domainInfo.SCHEMA.toString());
                    if (endObject1 != null && endObject2 != null) {
                        if (StringUtils.isEmpty(relDef.DomainUID1()))
                            relDef.setDomainUID1(endObject1.DomainUID());
                        endObject1.toInterface(IInterfaceDef.class).getEnd1RelDefs().append(relDef);
                        if (StringUtils.isEmpty(relDef.DomainUID2()))
                            relDef.setDomainUID2(endObject2.DomainUID());
                        endObject2.toInterface(IInterfaceDef.class).getEnd2RelDefs().append(relDef);
                    } else
                        throw new Exception("End1 or End2 Object is not exist in system:" + relDef.UID1() + "----" + relDef.UID2());
                } else if (classDefinitionUID.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                    IRel rel = obj.toInterface(IRel.class);
                    if (CIMContext.Instance.ProcessCache().item(rel.RelDefUID(), domainInfo.SCHEMA.toString()) == null)
                        throw new Exception("invalid rel def for relationship creation " + rel.RelDefUID());
                    IObject relDef = CIMContext.Instance.ProcessCache().item(rel.RelDefUID(), domainInfo.SCHEMA.toString());
                    this.finishEndInfo(obj, relDef.toInterface(IRelDef.class), relCollectionTypes.End1s);
                    this.finishEndInfo(obj, relDef.toInterface(IRelDef.class), relCollectionTypes.End2s);
                    if (StringUtils.isEmpty(rel.DomainUID()) || rel.DomainUID().equalsIgnoreCase(domainInfo.UNKNOWN.toString())) {
                        if (!StringUtils.isEmpty(rel.DomainUID2()) && !StringUtils.isEmpty(rel.DomainUID1()) && rel.DomainUID2().equalsIgnoreCase(rel.DomainUID1()))
                            rel.setDomainUID(rel.DomainUID1());
                        if (StringUtils.isEmpty(rel.DomainUID()) || rel.DomainUID().equalsIgnoreCase(domainInfo.UNKNOWN.toString()))
                            rel.setDomainUID(rel.DomainUID2());
                    }
                }
                obj.ClassBase().addPreProcessMethod(obj, interfaceDefinitionType.IObject.toString(), deferredMethods.OnPreProcess.toString(), new Object[]{new createArgs(false, username, configurationItem)});
                obj.ClassBase().addProcessMethods(obj, interfaceDefinitionType.IObject.toString(), deferredMethods.OnCreate.toString(), new Object[]{new createArgs(false, username, configurationItem)});
                obj.ClassBase().addCancelMethod(obj, interfaceDefinitionType.IObject.toString(), deferredMethods.OnCreating.toString(), new Object[]{new cancelArgs(false, username, configurationItem)});
                obj.ClassBase().addCompletedMethod(obj, interfaceDefinitionType.IObject.toString(), deferredMethods.OnCreated.toString(), new Object[]{new suppressibleArgs(false, username, configurationItem)});
                if (CIMContext.Instance.Transaction().inTransaction())
                    CIMContext.Instance.Transaction().add(obj);
            } catch (Exception exception) {
                log.error("finish create failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(obj.Lock());
            }
        }
    }

    @Override
    public IObjectCollection getRealizedInterfaceDefs() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.realizes.toString(), true);
        if (relCollection != null && relCollection.size() > 0)
            return relCollection.GetEnd2s();
        return null;
    }

    @Override
    public IObjectCollection getENSDefinitions() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.classDefContainsENSDefs.toString(), true);
        if (relCollection != null && relCollection.size() > 0)
            return relCollection.GetEnd2s();
        return null;
    }

    @Override
    public IObjectCollection getForms() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.classDefForms.toString(), true);
        if (relCollection != null && relCollection.size() > 0)
            return relCollection.GetEnd2s();
        return null;
    }


    @Override
    public IObjectCollection getRequiredRealizedInterfaceDefs() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.realizes.toString(), true);
        if (relCollection != null && relCollection.hasValue()) {
            IObjectCollection result = new ObjectCollection();
            Iterator<IObject> iObjectIterator = relCollection.GetEnumerator();
            while (iObjectIterator.hasNext()) {
                IRel rel = iObjectIterator.next().toInterface(IRel.class);
                if (rel.IsRequired()) {
                    result.append(rel.GetEnd2());
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public IObjectCollection generateForm() throws Exception {
        IObjectCollection forms = this.getForms();
        if (forms == null || !forms.hasValue()) {
            IObjectCollection result = new ObjectCollection();
            IObject form = SchemaUtility.newIObject("CIMForm", this.UID() + "'s generated form", "auto-generated by system", domainInfo.ADMIN.toString(), "FRM_" + this.UID());
            if (form == null)
                throw new Exception("form creation failed as it is NULL");
            IInterface item = form.Interfaces().item(ICIMForm.class.getSimpleName(), true);
            if (item == null)
                throw new Exception("invalid ICIMForm interface as required but missing");
            ICIMForm form1 = item.toInterface(ICIMForm.class);
            form1.setFormPurpose(formPurpose.foFillingString());
            form.ClassDefinition().FinishCreate(form);
            result.append(form);
            IObjectCollection realizedInterfaceDefs = this.getRealizedInterfaceDefs();
            if (realizedInterfaceDefs != null && realizedInterfaceDefs.hasValue()) {
                Iterator<IObject> iObjectIterator = realizedInterfaceDefs.GetEnumerator();
                int interfaceOrder = 10;
                while (iObjectIterator.hasNext()) {
                    IInterfaceDef interfaceDef = iObjectIterator.next().toInterface(IInterfaceDef.class);
                    IObject section = SchemaUtility.newIObject("CIMSection", interfaceDef.UID() + "'s section details", "auto-generated by system", domainInfo.ADMIN.toString(), "SCT_" + interfaceDef.UID());
                    if (section == null)
                        throw new Exception("create section failed as it is NULL");
                    IInterface anInterface = section.Interfaces().item(ICIMSection.class.getSimpleName(), true);
                    if (anInterface == null)
                        throw new Exception("invalid ICIMSection interface as required but missing");
                    ICIMSection section1 = section.toInterface(ICIMSection.class);
                    section1.setLabelName(interfaceDef.DisplayName());
                    section.ClassDefinition().FinishCreate(section);
                    result.append(section);

                    IRel form2Sections = SchemaUtility.newRelationship("form2Sections", form, section, true);
                    ICIMRenderInfo renderInfo = form2Sections.Interfaces().item(ICIMRenderInfo.class.getSimpleName(), true).toInterface(ICIMRenderInfo.class);
                    renderInfo.setDisplayAs(interfaceDef.DisplayName() + " 's details info");
                    renderInfo.setReadOnly(false);
                    renderInfo.setVisible(true);
                    form2Sections.setOrderValue(interfaceOrder);
                    ICIMFormSectionDetails formSectionDetailInfo = form2Sections.Interfaces().item(ICIMFormSectionDetails.class.getSimpleName(), true).toInterface(ICIMFormSectionDetails.class);
                    formSectionDetailInfo.setEffectFormPurpose(formPurpose.foFillingString());
                    form2Sections.ClassDefinition().FinishCreate(form2Sections);
                    result.append(form2Sections);

                    IObjectCollection propertyDefinitions = interfaceDef.getExposesPropertyDefinition();
                    if (propertyDefinitions != null && propertyDefinitions.hasValue()) {
                        int propertyOrder = 10;
                        Iterator<IObject> iObjectIterator1 = propertyDefinitions.GetEnumerator();
                        while (iObjectIterator1.hasNext()) {
                            IPropertyDef propertyDef = iObjectIterator1.next().toInterface(IPropertyDef.class);
                            IObject displayItem = SchemaUtility.newIObject("CIMDisplayItem", propertyDef.Name() + "'s display item", "auto-generated by system", domainInfo.ADMIN.toString(), "DI_" + propertyDef.UID());
                            if (displayItem == null)
                                throw new Exception("create display item failed as it is NULL");
                            ICIMDisplayItem displayItem1 = displayItem.Interfaces().item(ICIMDisplayItem.class.getSimpleName(), true).toInterface(ICIMDisplayItem.class);
                            displayItem1.setItemType(classDefinitionType.PropertyDef.toString());
                            displayItem1.setSchemaDefinitionUID(propertyDef.UID());
                            displayItem.ClassDefinition().FinishCreate(displayItem);
                            result.append(displayItem);

                            IRel section2DisplayItems = SchemaUtility.newRelationship("section2DisplayItems", section, displayItem, true);
                            section2DisplayItems.setOrderValue(propertyOrder);
                            section2DisplayItems.setIsRequired(true);
                            ICIMSectionDisplayItemDetails sectionDisplayItemDetails = section2DisplayItems.Interfaces().item(ICIMSectionDisplayItemDetails.class.getSimpleName(), true).toInterface(ICIMSectionDisplayItemDetails.class);
                            ICIMRenderInfo renderInfo1 = section2DisplayItems.Interfaces().item(ICIMRenderInfo.class.getSimpleName(), true).toInterface(ICIMRenderInfo.class);
                            renderInfo1.setVisible(true);
                            renderInfo1.setReadOnly(false);
                            renderInfo1.setDisplayAs(propertyDef.DisplayName());
                            renderInfo1.setWidth(100);
                            renderInfo1.setLength(100);
                            section2DisplayItems.ClassDefinition().FinishCreate(section2DisplayItems);
                            result.append(section2DisplayItems);
                            propertyOrder += 10;
                        }
                    }
                    interfaceOrder += 10;
                }
            }

            IRel rel = SchemaUtility.newRelationship(relDefinitionType.classDefForms.toString(), this, form, true);
            rel.ClassDefinition().FinishCreate(rel);
            result.append(rel);
            return result;
        }
        return null;
    }
}
