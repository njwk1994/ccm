package ccm.server.cache.impl;

import ccm.server.cache.IApplicationCache;
import ccm.server.cache.ICache;
import ccm.server.cache.ICacheConfigurationService;
import ccm.server.cache.IProcessCache;
import ccm.server.context.CIMContext;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.*;
import ccm.server.helper.HardCodeHelper;
import ccm.server.model.CacheWrapper;
import ccm.server.model.DynamicalDefinitionObj;
import ccm.server.models.query.QueryCriteria;
import ccm.server.models.query.QueryRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.shared.ISharedLocalService;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import ccm.server.util.ReentrantLockUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service("processCache")
@Slf4j
public class ProcessCache extends CacheBase implements IProcessCache {
    private final static ThreadLocal<ICIMLoader> threadLocalLoader = new ThreadLocal<>();
    @Autowired
    private IApplicationCache applicationCache;
    @Autowired
    private ISharedLocalService sharedLocalService;
    @Autowired
    private IQueryEngine queryEngine;
    @Autowired
    private ICacheConfigurationService cacheExtension;

    public final static ConcurrentHashMap<String, DynamicalDefinitionObj> dynDefinitions = new ConcurrentHashMap<>();

    @Override
    public void reInitialize() throws Exception {
        this.applicationCache.reInitialize();
        super.reInitialize();
    }

    @Override
    public String parseExpectedValue(String propertyDefinitionUID, String value) throws Exception {
        if (!StringUtils.isEmpty(value) && !StringUtils.isEmpty(propertyDefinitionUID)) {
            IEnumEnum enumEnum = CIMContext.Instance.ProcessCache().getEnumListLevelType(propertyDefinitionUID, value);
            if (enumEnum != null)
                return enumEnum.UID();
        }
        return value;
    }

    @Override
    public String getTablePrefixForClassDefinition(String classDefinitionUID) {
        return this.applicationCache.getTablePrefixForClassDefinition(classDefinitionUID);
    }

    @Override
    public IDomain getDomainForClassDef(String classDefinitionUID) throws Exception {
        IObject domain = this.applicationCache.getDomainForClassDef(classDefinitionUID);
        IDomain domain1 = domain != null ? domain.toInterface(IDomain.class) : null;
        if (domain1 == null)
            throw new Exception("invalid domain info for " + classDefinitionUID);
        return domain1;
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
        return this.applicationCache.initialized();
    }

    private final List<String> partialCachedClassDefs = new ArrayList<String>() {{
        this.add(classDefinitionType.CIMUser.toString());
        this.add(classDefinitionType.CIMPlant.toString());
        this.add(classDefinitionType.CIMRevisionScheme.toString());
    }};

    private final List<String> partialCachedRelDefs = new ArrayList<String>() {
        {
            this.add(relDefinitionType.user2ConfigurationItem.toString());
            this.add(relDefinitionType.User2QueryConfig.toString());
        }
    };

    @Override
    public Map<String, String> schemaObjectImpliedBy() {
        return this.applicationCache.schemaObjectImpliedBy();
    }

    @Override
    public void initialize() throws Exception {
        this.addMember(this.applicationCache);
        this.applicationCache.initialize();
        this.addCachedClassDefs(this.partialCachedClassDefs);
        this.addCachedRelDefs(this.partialCachedRelDefs);
        super.initialize();
    }

    @Override
    public void onInitialized() throws Exception {
        super.onInitialized();
        CIMContext.Instance.ensureAllTablesForConfigurationItemsAndDomains();
        this.onInitializeCachedDefinitions();
    }

    protected void onInitializeCachedDefinitions() {
        StopWatch stopWatch = PerformanceUtility.start();
        IObjectCollection definitions = new ObjectCollection();
        IObjectCollection classDefs = this.getObjectsByClassDefCache(classDefinitionType.ClassDef.toString());
        if (classDefs != null && classDefs.hasValue())
            definitions.addRangeUniquely(classDefs);
        IObjectCollection relDefs = this.getObjectsByClassDefCache(classDefinitionType.RelDef.toString());
        if (relDefs != null && relDefs.hasValue())
            definitions.addRangeUniquely(relDefs);

        List<ICacheInfo> cacheInfos = definitions.toList(ICacheInfo.class);
        if (CommonUtility.hasValue(cacheInfos)) {
            List<CacheWrapper> cacheWrappers = new ArrayList<>();
            for (ICacheInfo cacheInfo : cacheInfos) {
                if (cacheInfo.CachedInd()) {
                    String value = cacheInfo.UID();
                    String key = cacheInfo.CachedKey();
                    if (StringUtils.isEmpty(key))
                        key = CacheWrapper.IDENTITY_HARD_CODE;
                    CacheWrapper cacheWrapper = new CacheWrapper();
                    cacheWrapper.setIdentity(key);
                    cacheWrapper.setKey(CacheWrapper.ValueOf(cacheInfo.ClassDefinitionUID()));
                    cacheWrapper.setValue(value);
                    cacheWrappers.add(cacheWrapper);
                }
            }
            CacheConfigurationService.Instance.addCachedWrappers(cacheWrappers);
        }
        int size = definitions.size();
        log.info("complete to amount cache setting:***" + size + "***" + PerformanceUtility.stop(stopWatch));
    }

    @Override
    public int level() {
        return 1;
    }

    @Override
    public void onInitializing() throws Exception {
        IObjectCollection objects = this.queryObjectsByClassDefinitions(this.partialCachedClassDefs);
        IObjectCollection rels = this.queryRelsByRelDefs(this.partialCachedRelDefs);
        this.addLocally(objects);
        this.addLocally(rels);
    }

    @Override
    public IObjectCollection getEnumListTypeAndEnums() {
        IObjectCollection result = new ObjectCollection();
        if (CommonUtility.hasValue(this.objectsByOBID)) {
            for (Map.Entry<String, IObject> objectEntry : this.objectsByOBID.entrySet()) {
                result.append(objectEntry.getValue());
            }
        }
        return result;
    }

    @Override
    public void setScopePrefixForQueryRequestHandler(QueryRequest queryRequest) {
        queryRequest.setScopePrefix("");
    }

    @Override
    public IObjectCollection getByNameOrDescription(String name, String description) {
        QueryRequest queryRequest = this.queryEngine.start();
        boolean flag = false;
        if (!StringUtils.isEmpty(description)) {
            flag = true;
            if (description.contains("*"))
                this.queryEngine.addDescriptionForQuery(queryRequest, operator.like, description);
            else
                this.queryEngine.addDescriptionForQuery(queryRequest, operator.equal, description);
        }
        if (!StringUtils.isEmpty(name)) {
            flag = true;
            if (name.contains("*"))
                this.queryEngine.addNameForQuery(queryRequest, operator.like, name);
            else
                this.queryEngine.addNameForQuery(queryRequest, operator.equal, name);
        }
        if (flag)
            return this.queryEngine.query(queryRequest);
        return null;
    }

    @Override
    public String getPropertyValueTypeClassDefForPropertyDefinition(String propertyDefinitionUid) {
        return this.applicationCache.getPropertyValueTypeClassDefForPropertyDefinition(propertyDefinitionUid);
    }

    @Override
    public IObject getScopedByForPropertyDefinition(String propertyDefinitionUID) {
        return this.applicationCache.getScopedByForPropertyDefinition(propertyDefinitionUID);
    }

    @Override
    public List<String> getImpliedByIDef(String pstrIDef) {
        return this.applicationCache.getImpliedByIDef(pstrIDef);
    }

    @Override
    public boolean getRequiredOrNotForPropertyExposesInterfaceDef(String propertyDefinitionUID) {
        return this.applicationCache.getRequiredOrNotForPropertyExposesInterfaceDef(propertyDefinitionUID);
    }

    @Override
    public String getExposedInterfaceByPropertyDef(String propertyDef) throws Exception {
        return this.applicationCache.getExposedInterfaceByPropertyDef(propertyDef);
    }

    @Override
    public String getDomainUIDForClassDefinition(String classDef) throws Exception {
        if (!StringUtils.isEmpty(classDef)) {
            IObject item = this.item(classDef, domainInfo.SCHEMA.toString());
            if (item != null) {
                IClassDef iClassDef = item.toInterface(IClassDef.class);
                IObject domainInfo = iClassDef.getDomainInfo();
                if (domainInfo != null) {
                    return domainInfo.Name();
                }
            }
        }
        throw new Exception("cannot find any domain configuration for " + classDef);
    }

    @Override
    public List<String> getRealizedInterfaceDefByClassDef(String classDefUID, boolean onlyRequired) {
        return this.applicationCache.getRealizedInterfaceDefByClassDef(classDefUID, onlyRequired);
    }

    @Override
    public List<String> getRealizesClassDefsByInterfaceDef(String interfaceDefinitionUID) {
        return this.applicationCache.getRealizesClassDefsByInterfaceDef(interfaceDefinitionUID);
    }

    @Override
    public void inflateCachedIRelFromDataBase(IRel rel) throws Exception {
        if (rel != null) {
            IObject objectByOBID = this.queryEngine.getRelationshipByOBIDAndRelDef(rel.OBID(), rel.RelDefUID());
            if (objectByOBID != null) {
                rel.toInterface(IRel.class).resetWithProvidedIObjectAsNewCache(objectByOBID);
            } else
                throw new Exception("there is relationship with OBID:" + rel.OBID() + " exist in database");
        }
    }

    @Override
    public void inflateCachedIObjectFromDataBase(IObject iObject) throws Exception {
        if (iObject != null) {
            if (iObject.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
                this.inflateCachedIRelFromDataBase(iObject.toInterface(IRel.class));
            else {
                IObject object = this.queryEngine.getObjectByOBIDAndClassDefinitionUID(iObject.OBID(), iObject.ClassDefinitionUID());
                iObject.resetWithProvidedIObjectAsNewCache(object);
            }
        }
    }

    @Override
    public void inflateCachedIObjectFromDataBase(IObjectCollection iObjects) {
        if (iObjects != null && iObjects.hasValue()) {
            Map<String, IObjectCollection> collectionMap = iObjects.mapByClassDefOrRelDef();
            if (collectionMap != null && collectionMap.size() > 0) {
                for (Map.Entry<String, IObjectCollection> collectionEntry : collectionMap.entrySet()) {
                    if (!collectionEntry.getKey().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                        IObjectCollection objectCollection = this.queryEngine.getObjectByOBIDAndClassDefinitionUID(collectionEntry.getValue().listOfOBID(), collectionEntry.getKey());
                        if (objectCollection != null && objectCollection.size() > 0) {
                            Iterator<IObject> iObjectIterator = objectCollection.GetEnumerator();
                            while (iObjectIterator.hasNext()) {
                                IObject current = iObjectIterator.next();
                                IObject item = iObjects.itemByOBID(current.OBID());
                                item.resetWithProvidedIObjectAsNewCache(current);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isDefinitionUnderConfigControl(String uidOrObid) {
        return this.applicationCache.isSchemaControlledByConfig(uidOrObid);
    }

    @Override
    public boolean isEnumListTypeProperty(String propertyDefinitionUid) {
        IObject scopedByForPropertyDefinition = this.getScopedByForPropertyDefinition(propertyDefinitionUid);
        if (scopedByForPropertyDefinition != null) {
            String classDefinitionUID = scopedByForPropertyDefinition.ClassDefinitionUID();
            return classDefinitionUID.equalsIgnoreCase(classDefinitionType.UoMListType.toString()) || classDefinitionUID.equalsIgnoreCase(classDefinitionType.EnumListType.toString()) || classDefinitionUID.equalsIgnoreCase(classDefinitionType.EnumListLevelType.toString());
        }
        return false;
    }

    @Override
    public void doAfterChangeScope() {
//        IObjectCollection cachedItems = this.getItems();
//        if (cachedItems != null && cachedItems.hasValue()) {
//            Map<String, IObjectCollection> mapByClassDefOrRelDef = cachedItems.mapByClassDefOrRelDef();
//            if (mapByClassDefOrRelDef != null && mapByClassDefOrRelDef.size() > 0) {
//                for (Map.Entry<String, IObjectCollection> entry : mapByClassDefOrRelDef.entrySet()) {
//                    boolean configControl = this.isDefinitionUnderConfigControl(entry.getKey());
//                    if (configControl) {
//                        this.remove(entry.getValue().toList());
//                    }
//                }
//            }
//        }
    }

    @Override
    public ICacheConfigurationService getCacheExtension() {
        return this.cacheExtension;
    }

    @Override
    public boolean isStringTypeThatCannotBeZeroLength(String propertyDefinitionUID) {
        return this.applicationCache.isStringTypeThatCannotBeZeroLength(propertyDefinitionUID);
    }

    private static final String EXPANSION_PATH = "EDG_Contains";

    @Override
    public IEnumEnum getEnumListLevelType(String pstrPropertyDef, String pstrValue) throws Exception {
        if (!StringUtils.isEmpty(pstrPropertyDef)) {
            IObject item = this.item(pstrPropertyDef, domainInfo.SCHEMA.toString());
            if (item != null) {
                IPropertyDef propertyDef = item.toInterface(IPropertyDef.class);
                if (propertyDef != null) {
                    IPropertyType propertyType = propertyDef.getScopedByPropertyType();
                    String s = propertyType.ClassDefinitionUID();
                    if (s.equalsIgnoreCase(classDefinitionType.EnumListType.toString()) || s.equalsIgnoreCase(classDefinitionType.UoMListType.toString())) {
                        return this.onGetEnumListEntry(pstrValue, propertyType.toInterface(IEnumListType.class));
                    } else if (s.equalsIgnoreCase(classDefinitionType.EnumListLevelType.toString())) {
                        IEnumListLevelType enumListLevelType = propertyType.toInterface(IEnumListLevelType.class);
                        IEnumListType enumListForEnumLevel = enumListLevelType.getBaseEnumListForEnumLevel();
                        IRelDef defForEnumLevel = enumListLevelType.getRelDefForEnumLevel();
                        if (enumListForEnumLevel != null && defForEnumLevel != null) {
                            if (defForEnumLevel.UID().startsWith(EXPANSION_PATH)) {
                                String s1 = defForEnumLevel.UID().substring(EXPANSION_PATH.length());
                                return this.onGetEnumListLevelType(pstrValue, enumListForEnumLevel, Integer.parseInt(s1), 0);
                            }
                        }
                    } else
                        log.warn(pstrPropertyDef + " is not enum property type, cannot get entry from it");
                }
            }
        }
        return null;
    }

    private IEnumEnum onGetEnumListLevelType(String pstrValue, IPropertyType propertyType, int targetLevel, int currentLevel) throws Exception {
        if (propertyType != null && !StringUtils.isEmpty(pstrValue)) {
            currentLevel++;
            if (targetLevel == currentLevel)
                return this.onGetEnumListEntry(pstrValue, propertyType);
            else if (currentLevel < targetLevel) {
                IEnumListType enumListType = propertyType.toInterface(IEnumListType.class);
                IObjectCollection entries = enumListType.getEntries();
                if (entries != null && entries.hasValue()) {
                    Iterator<IObject> iObjectIterator = entries.GetEnumerator();
                    while (iObjectIterator.hasNext()) {
                        IObject object = iObjectIterator.next();
                        if (object != null && object.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.EnumListType.toString())) {
                            IEnumEnum iEnumEnum = this.onGetEnumListLevelType(pstrValue, object.toInterface(IPropertyType.class), targetLevel, currentLevel);
                            if (iEnumEnum != null)
                                return iEnumEnum;
                        }
                    }
                }
            }
        }
        return null;
    }

    private IEnumEnum onGetEnumListEntry(String pstrValue, IPropertyType propertyType) throws Exception {
        if (propertyType != null) {
            IEnumListType enumListType = propertyType.toInterface(IEnumListType.class);
            if (enumListType != null) {
                IObjectCollection entries = enumListType.getEntries();
                if (entries != null && entries.size() > 0) {
                    Iterator<IObject> iObjectIterator = entries.GetEnumerator();
                    while (iObjectIterator.hasNext()) {
                        IEnumEnum iObject = iObjectIterator.next().toInterface(IEnumEnum.class);
                        if (iObject.isHint(pstrValue))
                            return iObject;
                        else {
                            if (pstrValue.contains(":")) {
                                String lstrValue = pstrValue.substring(0, pstrValue.indexOf(":"));
                                if (iObject.UID().equalsIgnoreCase(lstrValue))
                                    return iObject.toInterface(IEnumEnum.class);
                                else if (iObject.Name().equalsIgnoreCase(lstrValue))
                                    return iObject.toInterface(IEnumEnum.class);
                            } else if (pstrValue.startsWith("@")) {
                                String lstrValue = pstrValue.substring(1);
                                if (iObject.Name().equalsIgnoreCase(lstrValue))
                                    return iObject.toInterface(IEnumEnum.class);
                                else if (iObject.UID().equalsIgnoreCase(lstrValue))
                                    return iObject.toInterface(IEnumEnum.class);
                            } else if (pstrValue.startsWith("#")) {
                                String lstrValue = pstrValue.substring(1);
                                try {
                                    int i = Integer.parseInt(lstrValue);
                                    IEnumEnum iEnumEnum = iObject.toInterface(IEnumEnum.class);
                                    if (iEnumEnum.EnumNumber() == i)
                                        return iEnumEnum;
                                } catch (Exception exception) {
                                    log.warn(exception.getMessage(), exception);
                                }

                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void setCriteriaAppendTable(QueryCriteria criteria) throws Exception {
        if (criteria != null && criteria.useRelationshipCustomProperty()) {
            relDirection direction = criteria.direction();
            if (direction == null || direction == relDirection._unknown)
                throw new Exception("system cannot identity path direction for " + criteria.getRelOrEdgeDefinitionUID());

            String relOrEdgeDefUID = criteria.relOrEdgeDefUID();
            IObject item = CIMContext.Instance.ProcessCache().item(relOrEdgeDefUID, null, false);
            if (item == null)
                throw new Exception("invalid schema definition " + relOrEdgeDefUID + " in database");

            IRelDef relDef = item.toInterface(IRelDef.class);
            List<IDomain> usedDomains = relDef.getUsedDomains(relDirection.toRelCollectionTypesForEnd(direction));
            if (CommonUtility.hasValue(usedDomains)) {
                List<String> tablePrefixes = new ArrayList<>();
                String myConfigurationItemTablePrefix = CIMContext.Instance.getMyConfigurationItemTablePrefix();
                for (IDomain usedDomain : usedDomains) {
                    String tablePrefix = usedDomain.TablePrefix();
                    if (!StringUtils.isEmpty(tablePrefix)) {
                        if (usedDomain.ScopeWiseInd()) {
                            tablePrefixes.add(myConfigurationItemTablePrefix + tablePrefix);
                        } else
                            tablePrefixes.add(tablePrefix);
                    }
                }
                criteria.getTableNames().addAll(tablePrefixes);
            }
        }
    }

    @Override
    public void addDynamicalPropertyDefinition(String propertyDefinitionUID, String displayAs, propertyValueType propertyValueType) {
        if (!dynDefinitions.containsKey(propertyDefinitionUID)) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                DynamicalDefinitionObj dynamicalDefinitionObj = new DynamicalDefinitionObj();
                dynamicalDefinitionObj.setClassDefinitionType(classDefinitionType.PropertyDef);
                dynamicalDefinitionObj.setDescription("dyn definition object");
                dynamicalDefinitionObj.setDisplayAs(displayAs);
                dynamicalDefinitionObj.setUid(propertyDefinitionUID);
                dynamicalDefinitionObj.setName(propertyDefinitionUID);
                dynamicalDefinitionObj.setPropertyValueType(propertyValueType);
                dynDefinitions.remove(propertyDefinitionUID);
                dynDefinitions.put(propertyDefinitionUID, dynamicalDefinitionObj);

            } catch (Exception exception) {
                log.error("add dynamical definition object", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }

    }

    @Override
    public DynamicalDefinitionObj getDynamicalDefinitionObj(String defUID) {
        if (!StringUtils.isEmpty(defUID) && dynDefinitions.containsKey(defUID)) {
            return dynDefinitions.getOrDefault(defUID, null);
        }
        return null;
    }

    @Override
    public List<String> getScopeWisedDomains() {
        return this.applicationCache.getScopeWisedDomains();
    }

    @Override
    public boolean isPropertyDefinitionHistoryRetained(String pstrPropertyDefinitionUID) {
        return this.applicationCache.isPropertyDefinitionHistoryRetained(pstrPropertyDefinitionUID);
    }

    @Override
    public IObjectCollection getIDefRelDefs(String interfaceDefinitionUID, relDirection direction) {
        return this.applicationCache.getIDefRelDefs(interfaceDefinitionUID, direction);
    }

    @Override
    public IObjectCollection getExposedPropDefsForInterfaceDef(String pstrInterfaceDef, boolean pblnOnlyRequired) {
        return this.applicationCache.getExposedPropDefsForInterfaceDef(pstrInterfaceDef, pblnOnlyRequired);
    }

    @Override
    public List<String> getClassDefsForDesignObj() throws Exception {
        IObject designObj = this.item("ICCMDesignObj", domainInfo.SCHEMA.toString(), false);
        if (designObj != null) {
            IObjectCollection classDefinitions = designObj.toInterface(IInterfaceDef.class).getRealizedClassDefinition();
            if (classDefinitions != null && classDefinitions.hasValue()) {
                List<String> result = new ArrayList<>();
                Iterator<IObject> e = classDefinitions.GetEnumerator();
                while (e.hasNext()) {
                    IClassDef classDef = e.next().toInterface(IClassDef.class);
                    if (classDef != null) {
                        result.add(classDef.UID());
                    }
                }
                return result;
            }
        }
        return null;
    }
}
