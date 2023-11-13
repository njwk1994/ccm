package ccm.server.cache.impl;

import ccm.server.cache.IApplicationCache;
import ccm.server.cache.ICache;
import ccm.server.context.CIMContext;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.*;
import ccm.server.models.LiteObject;
import ccm.server.models.query.QueryRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.shared.ISharedCacheService;
import ccm.server.util.CommonUtility;
import ccm.server.util.ReentrantLockUtility;
import ccm.server.utils.IObjectConversion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service("applicationCache")
@Slf4j
public class ApplicationCache extends CacheBase implements IApplicationCache {
    @Autowired
    private ISharedCacheService sharedCacheService;
    @Autowired
    private IQueryEngine queryEngine;

    @Override
    public boolean isStringTypeThatCannotBeZeroLength(String propertyDefinitionUID) {
        if (!StringUtils.isEmpty(propertyDefinitionUID)) {
            try {
                IObject item = this.item(propertyDefinitionUID, domainInfo.SCHEMA.toString(), false);
                if (item == null)
                    throw new Exception("invalid property definition and not found in database");

                IRel rel = item.GetEnd1Relationships().GetRel(relDefinitionType.scopedBy.toString(), true);
                return rel != null && rel.IsRequired() && rel.UID2().equalsIgnoreCase(propertyDefinitionType.StringType.toString());
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
        return false;
    }

    @Override
    public IObject getScopedByForPropertyDefinition(String propertyDefinitionUID) {
        if (!StringUtils.isEmpty(propertyDefinitionUID)) {
            try {
                IObject item = this.item(propertyDefinitionUID, null, false);
                if (item != null) {
                    IRel rel = item.GetEnd1Relationships().GetRel(relDefinitionType.scopedBy.toString(), true);
                    if (rel != null) {
                        return this.item(rel.UID2(), null, false);
                    }
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
        return null;
    }

    @Override
    public List<String> getImpliedByIDef(String pstrIDef) {
        List<String> result = new ArrayList<>();
        if (!StringUtils.isEmpty(pstrIDef)) {
            try {
                IObject item = this.item(pstrIDef, null, false);
                if (item != null) {
                    IRelCollection relCollection = item.GetEnd2Relationships().GetRels(relDefinitionType.implies.toString(), true);
                    if (relCollection != null && relCollection.hasValue()) {
                        Iterator<IObject> iObjectIterator = relCollection.GetEnumerator();
                        while (iObjectIterator.hasNext()) {
                            IRel rel = iObjectIterator.next().toInterface(IRel.class);
                            result.add(rel.UID1());
                        }
                    }
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
        return result;
    }

    @Override
    public boolean getRequiredOrNotForPropertyExposesInterfaceDef(String propertyDefinitionUID) {
        if (!StringUtils.isEmpty(propertyDefinitionUID)) {
            try {
                IObject item = this.item(propertyDefinitionUID, null, false);
                if (item != null) {
                    IRel rel = item.GetEnd2Relationships().GetRel(relDefinitionType.exposes.toString(), true);
                    if (rel != null) {
                        return rel.IsRequired();
                    }
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
        return false;
    }

    @Override
    public List<String> getRealizedInterfaceDefByClassDef(String classDefUID, boolean onlyRequired) {
        if (!StringUtils.isEmpty(classDefUID)) {
            List<String> result = new ArrayList<>();
            try {
                IObject item = this.item(classDefUID, null, false);
                if (item != null) {
                    IRelCollection relCollection = item.GetEnd1Relationships().GetRels(relDefinitionType.realizes.toString(), true);
                    if (relCollection != null && relCollection.hasValue()) {
                        Iterator<IObject> iObjectIterator = relCollection.GetEnumerator();
                        while (iObjectIterator.hasNext()) {
                            IRel rel = iObjectIterator.next().toInterface(IRel.class);
                            boolean isRequired = rel.IsRequired();
                            if (onlyRequired) {
                                if (isRequired)
                                    result.add(rel.UID2());
                            } else
                                result.add(rel.UID2());
                        }
                    }
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
            return result;
        }
        return null;
    }

    @Override
    public List<String> getRealizesClassDefsByInterfaceDef(String interfaceDefinitionUID) {
        if (!StringUtils.isEmpty(interfaceDefinitionUID)) {
            List<String> result = new ArrayList<>();
            try {
                IObject item = this.item(interfaceDefinitionUID, null, false);
                if (item != null) {
                    IRelCollection relCollection = item.GetEnd2Relationships().GetRels(relDefinitionType.realizes.toString(), true);
                    if (relCollection != null && relCollection.hasValue()) {
                        Iterator<IObject> iObjectIterator = relCollection.GetEnumerator();
                        while (iObjectIterator.hasNext()) {
                            IRel rel = iObjectIterator.next().toInterface(IRel.class);
                            result.add(rel.UID1());
                        }
                    }
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
            return result;
        }
        return null;
    }

    @Override
    public String getExposedInterfaceByPropertyDef(String propertyDef) {
        if (!StringUtils.isEmpty(propertyDef)) {
            try {
                IObject item = this.item(propertyDef, null, false);
                if (item != null) {
                    IRel rel = item.GetEnd2Relationships().GetRel(relDefinitionType.exposes.toString(), true);
                    if (rel != null)
                        return rel.UID1();
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
        return "";
    }

    public String getPropertyValueType(IObject object) {
        if (object != null) {
            if (object.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.PropertyType.toString()))
                return object.Name();
            return object.ClassDefinitionUID();
        }
        return "";
    }

    @Override
    public String getPropertyValueTypeClassDefForPropertyDefinition(String propertyDefinitionUid) {
        if (!StringUtils.isEmpty(propertyDefinitionUid)) {
            if (this.isIObjectOrIRelProperty(propertyDefinitionUid)) {
                if (propertyDefinitionUid.equalsIgnoreCase(propertyDefinitionType.CreationDate.toString()))
                    return propertyValueType.DateTimeType.toString();
                else if (propertyDefinitionUid.equalsIgnoreCase(propertyDefinitionType.LastUpdateDate.toString()))
                    return propertyValueType.DateTimeType.toString();
                else if (propertyDefinitionUid.equalsIgnoreCase(propertyDefinitionType.TerminationDate.toString()))
                    return propertyDefinitionType.DateTimeType.toString();
            }
            try {
                IObject item = this.item(propertyDefinitionUid, null, false);
                if (item != null) {
                    IRel rel = item.GetEnd1Relationships().GetRel(relDefinitionType.scopedBy.toString(), true);
                    if (rel != null) {
                        String propertyValueType = this.getPropertyValueType(rel.GetEnd2());
                        if (!StringUtils.isEmpty(propertyValueType))
                            return propertyValueType;
                    }
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
        return propertyValueType.StringType.toString();
    }

    @Override
    public Map<String, String> schemaObjectImpliedBy() {
        Map<String, String> result = new HashMap<>();
        try {
            IObjectCollection interfaceDefinitions = this.getObjectsByClassDefCache(classDefinitionType.InterfaceDef.toString());
            if (interfaceDefinitions != null && interfaceDefinitions.hasValue()) {
                IRelCollection relCollection = interfaceDefinitions.GetEnd1Relationships().GetRels(relDefinitionType.implies.toString(), true);
                if (relCollection != null && relCollection.size() > 0) {
                    Iterator<IObject> iObjectIterator = relCollection.GetEnumerator();
                    while (iObjectIterator.hasNext()) {
                        IRel rel = iObjectIterator.next().toInterface(IRel.class);
                        String uid1 = rel.UID1();
                        String uid2 = rel.UID2();
                        result.putIfAbsent(uid1, uid2);
                    }
                }
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return result;
    }

    @Override
    public String identity() {
        String currentIdentity = "";
        Class<?>[] interfaces = this.getClass().getInterfaces();
        if (interfaces.length > 0)
            currentIdentity = interfaces[0].getSimpleName();
        else
            currentIdentity = this.getClass().getSimpleName();
        if (this.members().size() > 0) {
            for (ICache member : this.members()) {
                String identity = member.identity();
                if (!StringUtils.isEmpty(identity)) {
                    if (!StringUtils.isEmpty(currentIdentity))
                        currentIdentity = currentIdentity + "," + identity;
                    else
                        currentIdentity = identity;
                }
            }
        }
        return currentIdentity;
    }

    @Override
    public boolean initialized() {
        return super.initialized();
    }

    protected void onAmount(List<LiteObject> liteObjects) throws Exception {
        if (liteObjects != null && liteObjects.size() > 0) {
            List<LiteObject> sortedLiteObjectsByClassDefinition = liteObjects.stream().sorted(Comparator.comparing(LiteObject::getClassDefinitionUid)).collect(Collectors.toList());
            IObjectConversion conversion = this.queryEngine.getIObjectConversion();
            if (conversion != null) {
                IObjectCollection cachedObjects = new ObjectCollection();
                ReentrantLockUtility.tryToAcquireWriteLock(cachedObjects.Lock());
                for (LiteObject liteObject : sortedLiteObjectsByClassDefinition.stream().filter(c -> c.getOBJ() != null).collect(Collectors.toList())) {
                    IObject iObject = conversion.convert(liteObject);
                    liteObjects.remove(liteObject);
                    log.info("add into with " + iObject.toErrorPop());
                    cachedObjects.onAdd(iObject, iObject.OBID(), iObject.UID());
                }
                ReentrantLockUtility.tryToUnlockWriteLock(cachedObjects.Lock());

                if (cachedObjects.hasValue())
                    this.onAddLocally(cachedObjects);
            } else
                throw new Exception("invalid conversion class found for progress");

            if (sortedLiteObjectsByClassDefinition.size() > 0) {
                log.info("try to cache relationship:" + sortedLiteObjectsByClassDefinition.size());
                for (LiteObject liteObject : sortedLiteObjectsByClassDefinition) {
                    if (liteObject.getREL() != null) {
                        IObject iObject = conversion.convert(liteObject);
                        if (iObject.IsTypeOf(IRel.class.getSimpleName())) {
                            IRel rel = iObject.toInterface(IRel.class);
                            IObject end1 = null;
                            IObject end2 = null;
                            String end1Info = rel.OBID1();
                            end1 = this.item(rel.UID1(), rel.DomainUID1());
                            if (end1 == null && !StringUtils.isEmpty(end1Info))
                                end1 = this.getObjectByOBIDCache(end1Info);

                            String end2Info = rel.OBID2();
                            end2 = this.item(rel.UID2(), rel.DomainUID2());
                            if (end2 == null && !StringUtils.isEmpty(end2Info))
                                end2 = this.getObjectByOBIDCache(end2Info);

                            if (end1 == null)
                                log.error("no object found with " + end1Info + ":" + rel.Name1() + " in cache or database with " + end2Info + ":" + rel.Name2());
                            if (end2 == null)
                                log.error("no object found with " + end2Info + ":" + rel.Name2() + " in cache or database with " + end1Info + ":" + rel.Name1());

                            if (end1 != null)
                                end1.GetEnd1Relationships().add(rel);
                            else
                                log.error("end1 is not found again:" + rel.Name1() + "," + rel.OBID1() + "->" + rel.UID1() + "," + rel.DomainUID1());

                            if (end2 != null)
                                end2.GetEnd2Relationships().add(rel);
                            else
                                log.error("end2 is not found again:" + rel.Name2() + "," + rel.OBID2() + "->" + rel.UID2() + "," + rel.DomainUID2());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void initialize() throws Exception {
        this.addCachedClassDefs(this.sharedCacheService.getForceCachedClassDefs());
        this.addCachedRelDefs(this.sharedCacheService.getForceCachedRelDefs());
        super.initialize();
    }

    @Override
    public void reset() {
        CacheConfigurationService.Instance.resetApplicationCacheConfiguration();
        super.reset();
    }

    @Override
    public void amount() throws Exception {
        if (this.initialized()) {
            log.trace("enter to amount schema information into process cache");
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.Lock());
                super.amountCacheConfigurationFromSharedCacheService();
            } catch (Exception exception) {
                log.error("amount schema info into process cached failed", exception);
            } finally {
                log.info("amount schema into process cache completed");
                ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
            }
        } else
            throw new Exception("shared cached service is not initialized yet");
    }

    @Override
    public boolean isSchemaControlledByConfig(String definitionUID) {
        try {
            IObject item = this.item(definitionUID, null, false);
            if (item != null) {
                if (item.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.ClassDef.toString()))
                    return item.toInterface(IClassDef.class).isScopeWised();
                else {
                }
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return false;
    }

    @Override
    public String getTablePrefixForClassDefinition(String classDefinitionUID) {
        if (!StringUtils.isEmpty(classDefinitionUID)) {
            IObject domain = null;
            try {
                domain = this.getDomainForClassDef(classDefinitionUID);
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
            if (domain != null) {
                return domain.toInterface(IDomain.class).TablePrefix();
            }
        }
        return "";
    }

    public static Map<String, IObject> defaultClassDefDomainInfo = null;

    @Override
    public IObject getDomainForClassDef(String classDefinitionUID) {
        if (!StringUtils.isEmpty(classDefinitionUID)) {
            try {
                if (defaultClassDefDomainInfo == null) {
                    IObject item = this.itemByUIDOrOBID(domainInfo.SCHEMA.toString(), classDefinitionType.Domain.toString());
                    defaultClassDefDomainInfo = new HashMap<String, IObject>() {{
                        put(classDefinitionType.ClassDef.toString(), item);
                        put(classDefinitionType.RelDef.toString(), item);
                        put(classDefinitionType.PropertyDef.toString(), item);
                        put(classDefinitionType.InterfaceDef.toString(), item);
                        put(classDefinitionType.EnumEnum.toString(), item);
                        put(classDefinitionType.EnumListLevelType.toString(), item);
                        put(classDefinitionType.EnumListType.toString(), item);
                        put(classDefinitionType.EdgeDef.toString(), item);
                        put(classDefinitionType.PropertyType.toString(), item);
                        put(classDefinitionType.Domain.toString(), item);
                        put(classDefinitionType.GraphDef.toString(), item);
                        put(classDefinitionType.UoMListType.toString(), item);
                        put(classDefinitionType.ViewDef.toString(), item);
                    }};

                    IObject adminDomain = this.itemByUIDOrOBID(domainInfo.ADMIN.toString(), classDefinitionType.Domain.toString());
                    defaultClassDefDomainInfo.put(classDefinitionType.CIMPlant.name(), adminDomain);
                    defaultClassDefDomainInfo.put(classDefinitionType.CIMUser.name(), adminDomain);
                    defaultClassDefDomainInfo.put(classDefinitionType.CIMRevisionScheme.name(), adminDomain);
                }
                if (defaultClassDefDomainInfo.containsKey(classDefinitionUID))
                    return defaultClassDefDomainInfo.get(classDefinitionUID);

                IObject item = this.item(classDefinitionUID, null, false);
                if (item != null) {
                    IRel rel = item.GetEnd1Relationships().GetRel(relDefinitionType.classDefDomainInfo.toString(), true);
                    if (rel != null) {
                        return rel.GetEnd2();
                    }
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
        return null;
    }

    @Override
    public List<String> getScopeWisedDomains() {
        List<String> result = new ArrayList<>();
        IObjectCollection domains = this.getObjectsByClassDefCache(classDefinitionType.Domain.toString());
        if (domains != null && domains.hasValue()) {
            Iterator<IObject> iObjectIterator = domains.GetEnumerator();
            while (iObjectIterator.hasNext()) {
                IDomain domain = iObjectIterator.next().toInterface(IDomain.class);
                if (domain != null && domain.ScopeWiseInd()) {
                    String tablePrefix = domain.TablePrefix();
                    if (!StringUtils.isEmpty(tablePrefix) && !result.contains(tablePrefix))
                        result.add(tablePrefix);
                }
            }
        }
        return result;
    }

    @Override
    public int level() {
        return 0;
    }

    @Override
    public void reInitialize() {
        this.mblnInitialized.set(false);
        this.reset();
    }

    @Override
    public void onInitializing() throws Exception {
        IObjectCollection objects = this.queryObjectsByClassDefinitions(this.sharedCacheService.getForceCachedClassDefs());
        IObjectCollection relationships = this.queryRelsByRelDefs(this.sharedCacheService.getForceCachedRelDefs());
        this.addLocally(objects);
        this.addLocally(relationships);
    }

    @Override
    public IObjectCollection queryObjectsByClassDefinitions(List<String> classDefinitions) {
        if (CommonUtility.hasValue(classDefinitions)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitions.stream().distinct().collect(Collectors.joining(",")));
            queryRequest.getDomains().add(domainInfo.SCHEMA.toString());
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    @Override
    public IObjectCollection queryRelsByRelDefs(List<String> relDefs) throws Exception {
        if (CommonUtility.hasValue(relDefs)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().setQueryForRelationship(queryRequest, true);
            CIMContext.Instance.QueryEngine().addRelOrEdgeDefForQuery(queryRequest, null, interfaceDefinitionType.IRel.toString(), propertyDefinitionType.RelDefUID.toString(), operator.in, relDefs.stream().distinct().collect(Collectors.joining(",")), ExpansionMode.none);
            queryRequest.getDomains().add(domainInfo.SCHEMA.toString());
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    @Override
    public boolean isPropertyDefinitionHistoryRetained(String pstrPropertyDefinitionUID) {
        if (!StringUtils.isEmpty(pstrPropertyDefinitionUID)) {
            try {
                IObject item = this.item(pstrPropertyDefinitionUID, domainInfo.SCHEMA.toString(), false);
                if (item != null) {
                    IPropertyDef propertyDef = item.toInterface(IPropertyDef.class);
                    if (propertyDef != null)
                        return propertyDef.HistoryNotRetained();
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
        return false;
    }

    @Override
    public IObjectCollection getIDefRelDefs(String interfaceDefinitionUID, relDirection direction) {
        if (!StringUtils.isEmpty(interfaceDefinitionUID)) {
            IObjectCollection relDefs = this.getObjectsByClassDefCache(classDefinitionType.RelDef.toString());
            if (relDefs != null && relDefs.size() > 0) {
                IObjectCollection result = new ObjectCollection();
                try {
                    Iterator<IObject> iObjectIterator = relDefs.GetEnumerator();
                    while (iObjectIterator.hasNext()) {
                        IRelDef relDef = iObjectIterator.next().toInterface(IRelDef.class);
                        if (relDef.UID1().equalsIgnoreCase(interfaceDefinitionUID) || relDef.UID2().equalsIgnoreCase(interfaceDefinitionUID))
                            result.append(relDef);
                    }
                } catch (Exception exception) {
                    log.error(exception.getMessage());
                }
                return result;
            }
        }
        return null;
    }

    @Override
    public IObjectCollection getExposedPropDefsForInterfaceDef(String interfaceDef, boolean onlyRequired) {
        if (!StringUtils.isEmpty(interfaceDef)) {
            try {
                IObject item = this.item(interfaceDef, null, false);
                if (item != null) {
                    IRelCollection relCollection = item.GetEnd1Relationships().GetRels(relDefinitionType.exposes.toString(), true);
                    if (relCollection != null && relCollection.hasValue()) {
                        IObjectCollection result = new ObjectCollection();
                        Iterator<IObject> iObjectIterator = relCollection.GetEnumerator();
                        while (iObjectIterator.hasNext()) {
                            IRel rel = iObjectIterator.next().toInterface(IRel.class);
                            if (onlyRequired) {
                                if (rel.IsRequired()) {
                                    result.append(rel.GetEnd2());
                                }
                            } else
                                result.append(rel.GetEnd2());
                        }
                        return result;
                    }
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
        return null;
    }
}
