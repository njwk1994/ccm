package ccm.server.utils;

import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.entity.MetaDataObj;
import ccm.server.entity.MetaDataObjInterface;
import ccm.server.entity.MetaDataObjProperty;
import ccm.server.entity.MetaDataRel;
import ccm.server.enums.*;
import ccm.server.helper.HardCodeHelper;
import ccm.server.models.LiteObject;
import ccm.server.processors.ThreadsProcessor;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.collections.impl.RelCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;
import ccm.server.schema.interfaces.defaults.IObjectDefault;
import ccm.server.schema.model.*;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import ccm.server.util.ReentrantLockUtility;
import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class IObjectConversion {

    private final ConcurrentMap<String, String> propertyTypeCache = new ConcurrentHashMap<>();

    public IObjectConversion() {

    }

    public IObject convert(LiteObject liteObject) throws Exception {
        List<LiteObject> liteObjectList = new ArrayList<>();
        liteObjectList.add(liteObject);
        return this.convert(liteObjectList).firstOrDefault();
    }

    public List<LiteObject> convertToLiteObject(IObjectCollection pcolItems) throws Exception {
        return this.convertToLiteObject(pcolItems, false);
    }

    public List<LiteObject> convertToLiteObject(IObjectCollection pcolItems, boolean pblnIncludeUoM) throws Exception {
        List<LiteObject> result = new ArrayList<>();
        if (pcolItems != null && pcolItems.hasValue()) {
            Iterator<IObject> iObjectIterator = pcolItems.GetEnumerator();
            while (iObjectIterator.hasNext()) {
                IObject iObject = iObjectIterator.next();
                LiteObject liteObject = this.convertToLiteObject(iObject);
                result.add(liteObject);
            }
        }
        return result;
    }

    public LiteObject convertToLiteObject(IObject item) throws Exception {
        return this.convertToLiteObject(item, false);
    }

    public LiteObject convertToLiteObject(IObject item, boolean pblnIncludeUoM) throws Exception {
        if (item == null)
            throw new Exception("invalid provided IObject as it is NULL");
        if (StringUtils.isEmpty(item.OBID()))
            throw new Exception("invalid OBID info as it is NULL");
        if (StringUtils.isEmpty(item.UID()))
            throw new Exception("invalid UID info as it is NULL");
        LiteObject liteObject = null;
        if (item.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            MetaDataRel metaDataRel = IObjectDefault.toRelEntity(item);
            liteObject = new LiteObject(metaDataRel);
        } else {
            MetaDataObj metaDataObj = IObjectDefault.toObjEntity(item);
            liteObject = new LiteObject(metaDataObj);
        }
        IObjectDefault.filling(liteObject, item.Interfaces());
        return liteObject;
    }

    public IObjectCollection convertThread(Collection<LiteObject> liteObjects) {
        IObjectCollection result = new ObjectCollection();
        StopWatch stopWatch = PerformanceUtility.start();
        log.trace("enter to convert " + CommonUtility.getSize(liteObjects) + " object(s)");
        List<List<LiteObject>> listList = CommonUtility.createList(new ArrayList<>(liteObjects));
        List<Callable<IObjectCollection>> callables = new ArrayList<>();
        for (List<LiteObject> items : listList) {
            Callable<IObjectCollection> callable = () -> convert(items);
            callables.add(callable);
        }
        List<IObjectCollection> objectCollections = ThreadsProcessor.Instance.execute(callables);
        if (CommonUtility.hasValue(objectCollections)) {
            for (IObjectCollection objectCollection : objectCollections) {
                result.addRange(objectCollection);
            }
        }
        log.trace("complete to convert with thread" + PerformanceUtility.stop(stopWatch));
        return result;
    }

    public IObjectCollection convert(Collection<LiteObject> liteObjects) throws Exception {
        IObjectCollection result = new ObjectCollection();
        log.trace("enter to convert " + CommonUtility.getSize(liteObjects) + " object(s)");
        StopWatch stopWatch = PerformanceUtility.start();
        if (CommonUtility.hasValue(liteObjects)) {
            ConcurrentHashSet<String> createdOBIDs = new ConcurrentHashSet<>();
            for (LiteObject liteObject : liteObjects) {
                IObject object = this.onConvertToIObject(liteObject, createdOBIDs);
                result.append(object);
            }
            if (result.size() > 1) {
                Iterator<IObject> e = result.GetEnumerator();
                while (e.hasNext()) {
                    IObject current = e.next();
                    if (createdOBIDs.contains(current.OBID()))
                        ReentrantLockUtility.tryToUnlockWriteLock(current.Lock());
                }
            } else if (result.size() == 1) {
                result.firstOrDefault().Interfaces().setAllowSorting(true);
                if (createdOBIDs.contains(result.firstOrDefault().OBID())) {
                    ReentrantLockUtility.tryToUnlockWriteLock(result.firstOrDefault().Lock());
                }

            }
        }
        log.trace("complete to convert " + result.size() + PerformanceUtility.stop(stopWatch));
        return result;
    }

    private IObject onConvertToIObject(LiteObject liteObject, ConcurrentHashSet<String> createdOBIDs) throws Exception {
        IObject result = null;
        if (liteObject != null) {
            result = CIMContext.Instance.ProcessCache().getObjectByOBIDCache(liteObject.getOBID());
            if (result != null && result.fromDb())
                return result;
            if (liteObject.getClassDefinitionUid().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
                result = this.onCreateIRel(liteObject);
            else
                result = this.onCreateIObject(liteObject);
            createdOBIDs.add(result.OBID());
        }
        return result;
    }

    public void outputAsLog(IObject result) throws Exception {
        if (result != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("********\r\n").append(result.Name()).append(",").append(result.ClassDefinitionUID()).append(",").append(result.UID()).append(",").append(result.OBID()).append("\r\n");
            stringBuilder.append("Interfaces:").append("\r\n");
            Iterator<Map.Entry<String, IInterface>> entryIterator = result.Interfaces().GetEnumerator();
            while (entryIterator.hasNext()) {
                IInterface anInterface = entryIterator.next().getValue();
                stringBuilder.append("[").append(anInterface.InterfaceDefinitionUID()).append("]'s STATUS:").append(anInterface.UpdateState().toString()).append("\r\n");
            }
            stringBuilder.append("Properties:").append("\r\n");
            Iterator<Map.Entry<String, IInterface>> entryIterator1 = result.Interfaces().GetEnumerator();
            while (entryIterator1.hasNext()) {
                IInterface anInterface = entryIterator1.next().getValue();
                Iterator<Map.Entry<String, IProperty>> entryIterator2 = anInterface.Properties().GetEnumerator();
                while (entryIterator2.hasNext()) {
                    IProperty property = entryIterator2.next().getValue();
                    Object value = property.Value();
                    String strValue = value != null ? value.toString() : "NULL";
                    IPropertyValue propertyValue = property.CurrentValue();
                    if (propertyValue == null)
                        propertyValue = property.PropertyValues() != null ? property.PropertyValues().latestValue() : null;
                    if (propertyValue != null)
                        strValue = propertyValue.Value() != null ? propertyValue.Value().toString() : "NULL";
                    stringBuilder.append("[").append(property.getPropertyDefinitionUid()).append("]'s Value:").append(strValue).append(", [STATUS]:").append(propertyValue != null ? propertyValue.UpdateState().toString() : "NULL STATUS").append(", [OBID]:").append(propertyValue != null ? propertyValue.OBID() : "NULL OBID").append("\r\n");
                }
            }
            stringBuilder.append("*******");
            log.info("Convert IObject log:\r\n" + stringBuilder.toString());
        }
    }

    private void setDefaultPropertyForIObject(IInterface pobjInterface, LiteObject liteObject) throws Exception {
        this.onCreateProperty(pobjInterface, propertyDefinitionType.OBID.toString(), liteObject.getOBID());
        this.onCreateProperty(pobjInterface, propertyDefinitionType.UID.toString(), liteObject.getObjUID());
        this.onCreateProperty(pobjInterface, propertyDefinitionType.DomainUID.toString(), liteObject.getDomainUID());
        this.onCreateProperty(pobjInterface, propertyDefinitionType.Name.toString(), liteObject.getName(), liteObject.getPropertyOBID(propertyDefinitionType.Name.toString()));
        this.onCreateProperty(pobjInterface, propertyDefinitionType.Description.toString(), liteObject.getDescription(), liteObject.getPropertyOBID(propertyDefinitionType.Description.toString()));
        this.onCreateProperty(pobjInterface, propertyDefinitionType.Config.toString(), liteObject.getConfig());
        this.onCreateProperty(pobjInterface, propertyDefinitionType.ClassDefinitionUID.toString(), liteObject.getClassDefinitionUid());
        this.onCreateProperty(pobjInterface, propertyDefinitionType.CreationDate.toString(), liteObject.getCreationDate());
        this.onCreateProperty(pobjInterface, propertyDefinitionType.CreationUser.toString(), liteObject.getCreationUser());
        this.onCreateProperty(pobjInterface, propertyDefinitionType.LastUpdateDate.toString(), liteObject.getLastUpdateDate());
        this.onCreateProperty(pobjInterface, propertyDefinitionType.LastUpdateUser.toString(), liteObject.getLastUpdateUser());
        this.onCreateProperty(pobjInterface, propertyDefinitionType.TerminationDate.toString(), liteObject.getTerminationDate());
        this.onCreateProperty(pobjInterface, propertyDefinitionType.TerminationUser.toString(), liteObject.getTerminationUser());
    }

    private void initializeRelationshipCache(IObject lobjResult) throws Exception {
        ((InterfaceBase) lobjResult).ClassBase().SetEnd1Relationships(new RelCollection(relCollectionTypes.End1s));
        ((InterfaceBase) lobjResult).ClassBase().SetEnd2Relationships(new RelCollection(relCollectionTypes.End2s));
    }

    private void initializeInterfaces(IObject lobjResult, LiteObject liteObject) throws Exception {
        if (liteObject.hasInterface()) {
            for (MetaDataObjInterface anInterface : liteObject.getINTERFACES()) {
                if (!lobjResult.Interfaces().hasInterface(anInterface.getInterfaceDefUid())) {
                    IInterface anInterface1 = this.onCreateInterface(anInterface);
                    if (anInterface1 != null) {
                        lobjResult.Interfaces().add(anInterface1);
                    }
                } else {
                    IInterface anInterface1 = lobjResult.Interfaces().get(anInterface.getInterfaceDefUid());
                    this.fillPropertiesForIInterfaceWithProvidedMetaInterface(anInterface1, anInterface);
                }
            }
        }
    }

    private void initializeProperties(IObject result, LiteObject liteObject) throws Exception {
        if (liteObject.hasProperty()) {
            for (Map.Entry<String, List<MetaDataObjProperty>> listEntry : liteObject.getPROPERTIES().entrySet()) {
                String interfaceDefUID = listEntry.getKey();
                if (!result.Interfaces().hasInterface(interfaceDefUID)) {
                    IInterface anInterface1 = this.onCreateInterface(liteObject.getInterface(interfaceDefUID));
                    if (anInterface1 != null) {
                        result.Interfaces().add(anInterface1);
                    }
                }
                IInterface anInterface = result.Interfaces().get(interfaceDefUID);
                for (MetaDataObjProperty objProperty : listEntry.getValue()) {
                    IProperty property = this.onCreateProperty(anInterface, objProperty.getPropertyDefUid(), objProperty.getStrValue(), objProperty.getObid());
                    if (property != null) {
                        anInterface.Properties().add(property);
                    }
                }
            }
        }
    }

    private IObject onCreateIObject(LiteObject liteObject) throws Exception {
        String lstrUID = liteObject.getObjUID();
        IObject result = ((ClassBase) CIMContext.Instance.getSchemaActivator().newInstance(
                PRIMARY_TYPE_OBJECT_PREFIX + liteObject.getClassDefinitionUid() + "." + lstrUID,
                PRIMARY_TYPE_OBJECT_PREFIX + liteObject.getClassDefinitionUid() + ".Default",
                THIRD_TYPE_CLASS_DEFAULT, new Object[]{false})).IObject();
        ((IObjectDefault) result.Interfaces().item(interfaceDefinitionType.IObject.toString())).setNeedsInflation(false);
        result.setClassDefinitionUID(liteObject.getClassDefinitionUid());
        ReentrantLockUtility.tryToAcquireWriteLock(result.Lock());
        ((IInterface) result).setClass(result.Interfaces().GetClass());
        ((InterfaceBase) result).ClassBase().setUpdateState(objectUpdateState.none);
        result.Interfaces().setAllowSorting(false);
        IInterface lobjIInterface = result.Interfaces().get(interfaceDefinitionType.IObject.toString());
        if (lobjIInterface == null) {
            lobjIInterface = this.onCreateInterface(liteObject.getInterface(interfaceDefinitionType.IObject.toString()));
            result.Interfaces().add(lobjIInterface, true);
        } else {
            MetaDataObjInterface anInterface = liteObject.getInterface(interfaceDefinitionType.IObject.toString());
            this.fillPropertiesForIInterfaceWithProvidedMetaInterface(lobjIInterface, anInterface);
        }
        this.setDefaultPropertyForIObject(lobjIInterface, liteObject);
        this.initializeInterfaces(result, liteObject);
        this.initializeProperties(result, liteObject);
        this.initializeRelationshipCache(result);
        return result;
    }

    protected void fillPropertiesForIInterfaceWithProvidedMetaInterface(IInterface anInterface, MetaDataObjInterface metaDataObjInterface) {
        if (anInterface != null && metaDataObjInterface != null) {
            anInterface.setInterfaceOBID(metaDataObjInterface.getObid());
            anInterface.setInterfaceCreationUser(metaDataObjInterface.getCreationUser());
            if (metaDataObjInterface.getCreationDate() != null)
                anInterface.setInterfaceCreationDate(metaDataObjInterface.getCreationDate().toString());
            anInterface.setInterfaceUpdateState(interfaceUpdateState.none);
            if (metaDataObjInterface.getTerminationDate() != null)
                anInterface.setInterfaceTerminationDate(metaDataObjInterface.getTerminationDate().toString());
            anInterface.setInterfaceTerminationUser(metaDataObjInterface.getTerminationUser());
        }
    }

    public static final String PRIMARY_TYPE_OBJECT_PREFIX = "ccm.server.schema.classes.";
    public static final String PRIMARY_TYPE_REL_PREFIX = "ccm.server.schema.classes.rel.";
    public static final String THIRD_TYPE_CLASS_DEFAULT = "ccm.server.schema.model.ClassDefault";

    private IObject onCreateIRel(LiteObject liteObject) throws Exception {
        String relDef = liteObject.getREL().getRelDefUid();
        IObject result = ((ClassBase) CIMContext.Instance.getSchemaActivator().newInstance(
                PRIMARY_TYPE_REL_PREFIX + relDef,
                PRIMARY_TYPE_REL_PREFIX + "Default",
                THIRD_TYPE_CLASS_DEFAULT, new Object[]{false})).IObject();
        result.setClassDefinitionUID(HardCodeHelper.CLASSDEF_REL);
        ReentrantLockUtility.tryToAcquireWriteLock(result.Lock());
        ((InterfaceBase) result).ClassBase().setUpdateState(objectUpdateState.none);
        ((IInterface) result).setClass(result.Interfaces().GetClass());
        IInterface objInterface = result.Interfaces().get(interfaceDefinitionType.IObject.toString());
        if (objInterface == null) {
            objInterface = this.onCreateInterface(liteObject.getInterface(interfaceDefinitionType.IObject.toString()));
            result.Interfaces().add(objInterface);
        } else {
            MetaDataObjInterface anInterface = liteObject.getInterface(interfaceDefinitionType.IObject.toString());
            this.fillPropertiesForIInterfaceWithProvidedMetaInterface(objInterface, anInterface);
        }
        this.onCreateProperty(objInterface, propertyDefinitionType.OBID.toString(), liteObject.getOBID());
        this.onCreateProperty(objInterface, propertyDefinitionType.UID.toString(), liteObject.getObjUID());
        this.onCreateProperty(objInterface, propertyDefinitionType.DomainUID.toString(), liteObject.getDomainUID());
        this.onCreateProperty(objInterface, propertyDefinitionType.CreationUser.toString(), liteObject.getCreationUser());
        this.onCreateProperty(objInterface, propertyDefinitionType.CreationDate.toString(), liteObject.getCreationDate());
        this.onCreateProperty(objInterface, propertyDefinitionType.TerminationUser.toString(), liteObject.getTerminationUser());
        this.onCreateProperty(objInterface, propertyDefinitionType.TerminationDate.toString(), liteObject.getTerminationDate());
        this.onCreateProperty(objInterface, propertyDefinitionType.Config.toString(), liteObject.getConfig());
        IInterface objIRel = result.Interfaces().item(interfaceDefinitionType.IRel.toString(), true);
        objIRel.setInterfaceUpdateState(interfaceUpdateState.none);
        objIRel.setInterfaceOBID(liteObject.getOBID());
        this.onCreateProperty(objIRel, propertyDefinitionType.RelDefUID.toString(), liteObject.getREL().getRelDefUid());
        this.onCreateProperty(objIRel, propertyDefinitionType.UID1.toString(), liteObject.getREL().getUid1());
        this.onCreateProperty(objIRel, propertyDefinitionType.UID2.toString(), liteObject.getREL().getUid2());
        this.onCreateProperty(objIRel, propertyDefinitionType.Name1.toString(), liteObject.getREL().getName1());
        this.onCreateProperty(objIRel, propertyDefinitionType.Name2.toString(), liteObject.getREL().getName2());
        this.onCreateProperty(objIRel, propertyDefinitionType.DomainUID1.toString(), liteObject.getREL().getDomainUid1());
        this.onCreateProperty(objIRel, propertyDefinitionType.DomainUID2.toString(), liteObject.getREL().getDomainUid2());
        this.onCreateProperty(objIRel, propertyDefinitionType.IsRequired.toString(), liteObject.getREL().getIsRequired() != 0);
        this.onCreateProperty(objIRel, propertyDefinitionType.OBID1.toString(), liteObject.getREL().getObid1());
        this.onCreateProperty(objIRel, propertyDefinitionType.OBID2.toString(), liteObject.getREL().getObid2());
        this.onCreateProperty(objIRel, propertyDefinitionType.OrderValue.toString(), liteObject.getREL().getOrderValue().toString());
        this.onCreateProperty(objIRel, propertyDefinitionType.Prefix.toString(), liteObject.getREL().getPrefix());
        this.onCreateProperty(objIRel, propertyDefinitionType.ClassDefinitionUID1.toString(), liteObject.getREL().getClassDefinitionUid1());
        this.onCreateProperty(objIRel, propertyDefinitionType.ClassDefinitionUID2.toString(), liteObject.getREL().getClassDefinitionUid2());
        this.initializeInterfaces(result, liteObject);
        this.initializeProperties(result, liteObject);
        this.initializeRelationshipCache(result);
        return result;
    }

    private IInterface onCreateInterface(MetaDataObjInterface anInterface) throws Exception {
        if (anInterface != null) {
            IInterface objInterface = (IInterface) CIMContext.Instance.getSchemaActivator().newInstance(
                    "ccm.server.schema.interfaces.defaults." + anInterface.getInterfaceDefUid() + "Default",
                    "ccm.server.schema.model.InterfaceDefault", new Object[]{false});
            if (objInterface instanceof InterfaceDefault)
                ((InterfaceDefault) objInterface).setInterfaceDefinitionUID(anInterface.getInterfaceDefUid());
            this.fillPropertiesForIInterfaceWithProvidedMetaInterface(objInterface, anInterface);
            return objInterface;
        }
        return null;
    }

    private IProperty onCreateProperty(IInterface objInterface, String pstrPropertyDefUid, Object value) throws Exception {
        return this.onCreateProperty(objInterface, pstrPropertyDefUid, value, null, null);
    }

    private IProperty onCreateProperty(IInterface objInterface, String pstrPropertyDefUid, Object value, String pstrPropertyOBID) throws Exception {
        return this.onCreateProperty(objInterface, pstrPropertyDefUid, value, null, pstrPropertyOBID);
    }

    private IProperty onCreateProperty(IInterface objInterface, String propertyDefUid, Object value, Double floatValue, String pstrPropertyOBID) {
        if (objInterface != null && !StringUtils.isEmpty(propertyDefUid)) {
            String propertyTypeCacheOrDefault = this.propertyTypeCache.getOrDefault(propertyDefUid, "");
            if (StringUtils.isEmpty(propertyTypeCacheOrDefault)) {
                String valueType = CIMContext.Instance.ProcessCache().getPropertyValueTypeClassDefForPropertyDefinition(propertyDefUid);
                this.propertyTypeCache.putIfAbsent(propertyDefUid, valueType);
            }
            IProperty property = new PropertyDefault(propertyDefUid);
            property.setParent(objInterface);
            IPropertyValue newPropertyValue = new PropertyValue(property, value, floatValue, null, null, null, propertyValueUpdateState.none);
            newPropertyValue.setOBID(pstrPropertyOBID);
            property.PropertyValues().add(newPropertyValue);
            objInterface.Properties().add(property);
            return property;
        }
        return null;
    }

    private final List<String> interfacesNoNeedToAddIntoIFTable = new ArrayList<String>() {{
        add(interfaceDefinitionType.IObject.toString());
        add(interfaceDefinitionType.IRel.toString());
    }};


    public LiteObject createLiteObjectForTransaction(IObject pobjValue) throws Exception {
        if (pobjValue != null) {
            String classDefinition = pobjValue.ClassDefinitionUID();
            LiteObject result = null;
            boolean isRelationships = false;
            if (classDefinition.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                isRelationships = true;
                IRel rel = pobjValue.toInterface(IRel.class);
                result = this.toRelLite(rel);
            } else {
                result = this.toObjLite(pobjValue);
            }
            objectUpdateState objectUpdateState = result.getObjectUpdateState();
            Iterator<Map.Entry<String, IInterface>> iterator = pobjValue.Interfaces().GetEnumerator();
            while (iterator.hasNext()) {
                IInterface anInterface = iterator.next().getValue();
                String interfaceDefinitionUID = anInterface.InterfaceDefinitionUID();
                if (isRelationships) {
                    if (interfacesNoNeedToAddIntoIFTable.contains(interfaceDefinitionUID))
                        continue;
                }
                if (anInterface.UpdateState() != interfaceUpdateState.none) {
                    result.setObjectUpdateState(anInterface.ObjectUpdateState());
                    result.collectInterface(anInterface.toDataInterface());
                }
                Iterator<Map.Entry<String, IProperty>> entryIterator = anInterface.Properties().GetEnumerator();
                while (entryIterator.hasNext()) {
                    IProperty property = entryIterator.next().getValue();
                    String propertyDefinitionUID = property.getPropertyDefinitionUid();
                    if (!LiteObject.isObjOrRelTableProperty(propertyDefinitionUID, pobjValue.ClassDefinitionUID())) {
                        Iterator<IPropertyValue> valueIterator = property.PropertyValues().GetEnumerator();
                        while (valueIterator.hasNext()) {
                            IPropertyValue propertyValue = valueIterator.next();
                            if (propertyValue.UpdateState() != propertyValueUpdateState.none) {
                                MetaDataObjProperty metaDataObjProperty = propertyValue.toDataProperty();
                                if (objectUpdateState == ccm.server.enums.objectUpdateState.terminated)
                                    metaDataObjProperty.setUpdateState(propertyValueUpdateState.terminated);
                                else if (objectUpdateState == ccm.server.enums.objectUpdateState.deleted)
                                    metaDataObjProperty.setUpdateState(propertyValueUpdateState.deleted);
                                else
                                    metaDataObjProperty.setUpdateState(propertyValue.UpdateState());
                                result.collectProperty(metaDataObjProperty);
                                result.setObjectUpdateState(ccm.server.enums.objectUpdateState.updated);
                            }
                        }
                    }
                }
            }
            return result;
        }
        return null;
    }

    public LiteObject toObjLite(IObject object) throws Exception {
        if (object != null) {
            //object.selfCheck();
            MetaDataObj metaDataObj = object.toMetaDataObject();
            return new LiteObject(metaDataObj);
        }
        return null;
    }


    public LiteObject toRelLite(IRel rel) throws Exception {
        if (rel != null) {
            //rel.selfCheck();
            MetaDataRel metaDataRel = rel.toMetaDataRel();
            return new LiteObject(metaDataRel);
        }
        return null;
    }

    public IObject createFromObjectDTO(ObjectDTO objectDTO) throws Exception {
        if (objectDTO != null) {
            String classDefinitionUID = objectDTO.getClassDefinitionUID();
            IObject item = CIMContext.Instance.ProcessCache().item(classDefinitionUID, domainInfo.SCHEMA.toString());
        }
        return null;
    }
}
