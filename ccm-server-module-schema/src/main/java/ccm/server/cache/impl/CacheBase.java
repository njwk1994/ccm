package ccm.server.cache.impl;

import ccm.server.cache.ICache;
import ccm.server.context.CIMContext;
import ccm.server.enums.*;
import ccm.server.helper.HardCodeHelper;
import ccm.server.model.CacheWrapper;
import ccm.server.models.query.QueryRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.collections.impl.RelCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import ccm.server.util.ReentrantLockUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Slf4j
public abstract class CacheBase implements ICache {
    public final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    public final ConcurrentHashMap<String, IObject> objectsByOBID = new ConcurrentHashMap<>();
    public final AtomicBoolean mblnInitialized = new AtomicBoolean(false);
    public final ConcurrentHashMap<String, Map<String, IObject>> objectsByUID = new ConcurrentHashMap<>();
    private final List<ICache> members = new ArrayList<>();
    private final AtomicBoolean mblnAllCache = new AtomicBoolean(false);

    @Override
    public String getCurrentScope() {
        ICIMConfigurationItem currentConfigurationItem = null;
        try {
            currentConfigurationItem = CIMContext.Instance.getMyConfigurationItem(null);
        } catch (Exception e) {
            log.trace("get current scope failed");
        }
        return currentConfigurationItem != null ? currentConfigurationItem.UID() : "";
    }

    @Override
    public void setScopePrefixForQueryRequest(IObjectCollection configurationItems, QueryRequest queryRequest) {
        if (queryRequest != null) {
            String scopePrefixes = null;
            if (configurationItems != null && configurationItems.hasValue()) {
                scopePrefixes = configurationItems.toList(ICIMConfigurationItem.class).stream().filter(Objects::nonNull).map(ICIMConfigurationItem::TablePrefix).filter(c -> !StringUtils.isEmpty(c)).collect(Collectors.joining(";"));
                queryRequest.setScopePrefix(scopePrefixes);
            }
        }
    }

    @Override
    public <T> T item(String uid, String domainUID, boolean fromDb, Class<T> clazz) {
        IObject item = this.item(uid, domainUID, fromDb);
        if (item != null) {
            return item.toInterface(clazz);
        }
        return null;
    }

    @Override
    public boolean allCache() {
        return this.mblnAllCache.get();
    }

    @Override
    public void setAllCache(boolean flag) {
        this.mblnAllCache.set(flag);
    }

    @Override
    public abstract String identity();

    @Override
    public int compare(ICache o1, ICache o2) {
        if (o1 == null || o2 == null)
            return -1;
        return o1.identity().compareTo(o2.identity());
    }

    @Override
    public Comparator<ICache> comparator() {
        return (o1, o2) -> {
            if (o1 == null || o2 == null)
                return -1;
            return o1.identity().compareTo(o2.identity());
        };
    }

    @Override
    public List<ICache> members() {
        return this.members;
    }

    @Override
    public void addMember(ICache cache) {
        if (cache != null) {
            this.members.add(cache);
        }
    }

    @Override
    public void removeMember(ICache cache) {
        this.members.remove(cache);
    }

    @Override
    public IObjectCollection getItems() {
        IObjectCollection result = new ObjectCollection();
        result.addRange(new ArrayList<>(this.objectsByOBID.values()));
        if (this.members().size() > 0) {
            for (ICache cache : this.members()) {
                result.addRangeUniquely(cache.getItems());
            }
        }
        return result;
    }

    @Override
    public void setScopePrefixForQueryRequestHandler(QueryRequest queryRequest) {

    }

    @Override
    public IObjectCollection queryObjectsByClassDefinitions(List<String> classDefinitions) {
        if (CommonUtility.hasValue(classDefinitions)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitions.stream().distinct().collect(Collectors.joining(",")));
            this.setScopePrefixForQueryRequestHandler(queryRequest);
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    @Override
    public void removeByUID(String uid) {
        log.trace("enter to remove by UID");
        if (!StringUtils.isEmpty(uid)) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.Lock());
                this.onRemoveByUID(uid);
            } catch (Exception exception) {
                log.error("remove by uid failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
            }
        }
        log.trace("complete to remove by UID");
    }

    @Override
    public IObject getObjectByNameAndClassDefinitionUIDFromUIDCache(String name, String classDefinitionUID) {
        if (!StringUtils.isEmpty(name)) {
            String currentScope = this.getCurrentScope();
            if (this.objectsByUID.size() > 0) {
                for (Map.Entry<String, Map<String, IObject>> mapEntry : this.objectsByUID.entrySet()) {
                    for (Map.Entry<String, IObject> objectEntry : mapEntry.getValue().entrySet()) {
                        IObject object = objectEntry.getValue();
                        boolean flag = false;
                        if (object.Name().equalsIgnoreCase(name)) {
                            if (!StringUtils.isEmpty(classDefinitionUID)) {
                                if (object.ClassDefinitionUID().equalsIgnoreCase(classDefinitionUID))
                                    flag = true;
                            } else
                                flag = true;
                        }
                        if (flag) {
                            if (object.underConfigInd(currentScope))
                                return object;
                        }
                    }
                }
            }
            if (this.members.size() > 0) {
                for (ICache parentCache : this.members) {
                    IObject iObject = parentCache.getObjectByNameAndClassDefinitionUIDFromUIDCache(name, classDefinitionUID);
                    if (iObject != null)
                        return iObject;
                }
            }
        }
        return null;
    }

    public IObject getObjectByNameAndClassDefinitionUID(String name, String classDefinitionUID) {
        IObject result = null;
        if (!StringUtils.isEmpty(name)) {
            try {
                result = this.getObjectByNameAndClassDefinitionUIDFromOBIDCache(name, classDefinitionUID);
                if (result == null)
                    result = this.getObjectByNameAndClassDefinitionUIDFromUIDCache(name, classDefinitionUID);
            } catch (Exception exception) {
                log.error("get object by name and class definition uid failed", exception);
            } finally {
                log.trace("complete to get object by name and class definition uid");
            }
        }
        return result;
    }

    @Override
    public IObject getObjectByUIDAndDomainUIDCache(String uid, String domainUID) {
        if (!StringUtils.isEmpty(uid)) {
            Map<String, IObject> objectMap = this.objectsByUID.getOrDefault(uid, null);
            if (objectMap != null) {
                IObject result;
                if (!StringUtils.isEmpty(domainUID) && !domainUID.equalsIgnoreCase(domainInfo.UNKNOWN.toString())) {
                    result = objectMap.getOrDefault(domainUID, null);
                } else {
                    result = new ArrayList<>(objectMap.values()).get(0);
                }
                if (result != null)
                    return result;
            }
            if (this.members.size() > 0) {
                for (ICache cache : this.members) {
                    IObject iObject = cache.getObjectByUIDAndDomainUIDCache(uid, domainUID);
                    if (iObject != null)
                        return iObject;
                }
            }
        }
        return null;
    }

    @Override
    public IObject getByUIDAndDomainUID(String uid, String domainUID) throws Exception {
        IObject item = this.item(uid, domainUID);
        if (item != null && this.isValid(item))
            return item;
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, null, uid);
        CIMContext.Instance.QueryEngine().addDomainUIDForQuery(queryRequest, null, domainUID);
        IObject iObject = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        this.refresh(iObject);
        return iObject;
    }

    protected List<String> getScopeIndicators() {
        String currentScope = this.getCurrentScope();
        List<String> result = new ArrayList<>();
        if (!StringUtils.isEmpty(currentScope))
            result.add(currentScope);
        result.add("");
        return result;
    }

    @Override
    public IObjectCollection getByUID(String uid) throws Exception {
        if (!StringUtils.isEmpty(uid)) {
            IObjectCollection items = this.getObjectsByUIDs(CommonUtility.toList(uid));
            if (items != null && items.hasValue()) {
                IObjectCollection result = new ObjectCollection();
                Iterator<IObject> e = items.GetEnumerator();
                while (e.hasNext()) {
                    IObject current = e.next();
                    if (this.isValid(current))
                        result.append(current);
                }
                if (result.size() > 0)
                    return result;
            }
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, null, uid);
            IObjectCollection objectCollection = CIMContext.Instance.QueryEngine().query(queryRequest);
            this.refresh(objectCollection);
            return objectCollection;
        }
        return null;
    }

    @Override
    public IObject getObjectByOBID(String obid) {
        if (!StringUtils.isEmpty(obid)) {
            IObject object = this.getObjectByOBIDCache(obid);
            if (object != null && this.isValid(object))
                return object;

            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, null, obid);
            IObject iObject = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
            this.refresh(iObject);
            return iObject;
        }
        return null;
    }

    protected boolean isIObjectOrIRelProperty(String propertyDefinitionUid) {
        if (!StringUtils.isEmpty(propertyDefinitionUid)) {
            String[] hardProperties = new String[]{
                    propertyDefinitionType.Description.toString(),
                    propertyDefinitionType.DomainUID.toString(),
                    propertyDefinitionType.ClassDefinitionUID.toString(),
                    propertyDefinitionType.ClassDefinitionUID1.toString(),
                    propertyDefinitionType.ClassDefinitionUID2.toString(),
                    propertyDefinitionType.DomainUID1.toString(),
                    propertyDefinitionType.DomainUID2.toString(),
                    propertyDefinitionType.CreationUser.toString(),
                    propertyDefinitionType.CreationDate.toString(),
                    propertyDefinitionType.LastUpdateUser.toString(),
                    propertyDefinitionType.LastUpdateDate.toString(),
                    propertyDefinitionType.UID.toString(),
                    propertyDefinitionType.UID1.toString(),
                    propertyDefinitionType.UID2.toString(),
                    propertyDefinitionType.Name.toString(),
                    propertyDefinitionType.Name1.toString(),
                    propertyDefinitionType.Name2.toString(),
                    propertyDefinitionType.Config.toString(),
                    propertyDefinitionType.RelDefUID.toString(),
                    propertyDefinitionType.Prefix.toString(),
                    propertyDefinitionType.OBID.toString(),
                    propertyDefinitionType.OBID1.toString(),
                    propertyDefinitionType.OBID2.toString()};
            return Arrays.stream(hardProperties).anyMatch(c -> c.equalsIgnoreCase(propertyDefinitionUid));
        }
        return false;
    }

    @Override
    public IObjectCollection queryRelsByRelDefs(List<String> relDefs) throws Exception {
        if (CommonUtility.hasValue(relDefs)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().setQueryForRelationship(queryRequest, true);
            CIMContext.Instance.QueryEngine().addRelOrEdgeDefForQuery(queryRequest, null, interfaceDefinitionType.IRel.toString(), propertyDefinitionType.RelDefUID.toString(), operator.in, relDefs.stream().distinct().collect(Collectors.joining(",")), ExpansionMode.none);
            this.setScopePrefixForQueryRequestHandler(queryRequest);
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    @Override
    public ReentrantReadWriteLock Lock() {
        return this.lock;
    }

    @Override
    public boolean initialized() {
        return this.mblnInitialized.get();
    }

    @Override
    public void onInitialized() throws Exception {

    }


    @Override
    public IObjectCollection getObjectsByClassDefCache(String classDef) {
        IObjectCollection result = new ObjectCollection();
        if (!StringUtils.isEmpty(classDef)) {
            String currentScope = this.getCurrentScope();
            for (Map.Entry<String, IObject> entry : this.objectsByOBID.entrySet()) {
                if (entry.getValue().ClassDefinitionUID().equalsIgnoreCase(classDef) && entry.getValue().underConfigInd(currentScope))
                    result.append(entry.getValue());
            }
            if (this.members.size() > 0) {
                for (ICache cache : this.members) {
                    IObjectCollection objectCollection = cache.getObjectsByClassDefCache(classDef);
                    if (objectCollection != null && objectCollection.size() > 0)
                        result.addRangeUniquely(objectCollection);
                }
            }
        }
        return result;
    }

    @Override
    public IObject getObjectByOBIDCache(String obid) {
        if (!StringUtils.isEmpty(obid)) {
            IObject result = this.objectsByOBID.getOrDefault(obid, null);
            if (result != null && result.fromDb())
                return result;
            if (this.members.size() > 0) {
                for (ICache cache : this.members) {
                    IObject iObject = cache.getObjectByOBIDCache(obid);
                    if (iObject != null)
                        return iObject;
                }
            }
        }

        return null;
    }

    @Override
    public void initialize() throws Exception {
        StopWatch stopWatch = PerformanceUtility.start();
        Exception exception = null;
        if (!this.mblnInitialized.get()) {
            this.mblnInitialized.set(true);
            log.trace("enter to " + this.identity() + " cache progress");
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                this.onInitializing();
            } catch (Exception ex) {
                log.error(this.getClass().getSimpleName() + " cached failed", ex);
                exception = ex;
                this.mblnInitialized.set(false);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
                if (exception == null)
                    this.onInitialized();
            }
        }
        log.info("======" + this.identity() + "======progress completed" + PerformanceUtility.stop(stopWatch));
        if (exception != null)
            throw exception;
    }


    @Override
    public IObjectCollection queryObjectsByUIDAndClassDefinition(Collection<String> uids, String classDefinition) {
        if (CommonUtility.hasValue(uids)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinition);
            CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, operator.in, String.join(",", uids));
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    @Override
    public IObjectCollection queryObjectsByOBIDAndClassDefinition(Collection<String> obids, String classDefinition) {
        if (CommonUtility.hasValue(obids)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinition);
            CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.in, String.join(",", obids));
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    @Override
    public IObject getObjectByClassDefCache(String uid, String classDef) {
        if (!StringUtils.isEmpty(uid)) {
            Map<String, IObject> objects = this.objectsByUID.getOrDefault(uid, null);
            if (CommonUtility.hasValue(objects)) {
                IObject result = objects.values().stream().filter(c -> c.ClassDefinitionUID().equalsIgnoreCase(classDef)).findFirst().orElse(null);
                if (result != null)
                    return result;
            }
            if (this.members.size() > 0) {
                for (ICache cache : this.members) {
                    IObject iObject = cache.getObjectByClassDefCache(uid, classDef);
                    if (iObject != null)
                        return iObject;
                }
            }
        }
        return null;
    }

    @Override
    public void remove(IObject object) {
        if (object != null) {
            this.remove(new ArrayList<IObject>() {{
                add(object);
            }});
        }
    }

    @Override
    public void remove(Collection<IObject> objects) {
        this.remove(objects, false);
    }

    @Override
    public void remove(IObject object, boolean removeFromProcessCacheOnly) {
        if (object != null) {
            this.remove(new ArrayList<IObject>() {{
                add(object);
            }}, removeFromProcessCacheOnly);
        }
    }

    @Override
    public void onRefreshUIDCollectionCache(IObject object) {
        if (object != null) {
            Map<String, IObject> objByDomain = this.objectsByUID.getOrDefault(object.UID(), null);
            if (objByDomain != null) {
                objByDomain.remove(object.DomainUID());
                objByDomain.put(object.DomainUID(), object);
                this.objectsByUID.replace(object.UID(), objByDomain);
            } else
                this.objectsByUID.put(object.UID(), new HashMap<String, IObject>() {{
                    this.put(object.DomainUID(), object);
                }});
        }
    }

    public void onClearObjectEndRels(IObject object) throws Exception {
        if (object != null) {
            Iterator<IObject> iObjectIterator = object.GetEnd1Relationships().copy().GetEnumerator();
            while (iObjectIterator.hasNext()) {
                IRel rel = iObjectIterator.next().toInterface(IRel.class);
                this.onTidyUpEnd1s(rel);
                this.onTidyUpRelToRels(rel, true);
            }

            Iterator<IObject> iObjectIterator1 = object.GetEnd2Relationships().copy().GetEnumerator();
            while (iObjectIterator1.hasNext()) {
                IRel rel = iObjectIterator1.next().toInterface(IRel.class);
                this.onTidyUpEnd2s(rel);
                this.onTidyUpRelToRels(rel, false);
            }
        }
    }

    public void onTidyUpEnd1s(IRel rel) throws Exception {
        if (rel != null) {
            IObject end1 = rel.GetEnd1();
            IObject end2 = rel.GetEnd2();
            if (end2 != null)
                end2.GetEnd2Relationships().remove(rel);
            if (end1 != null)
                end1.GetEnd1Relationships().remove(rel);
            this.remove(rel, true);
        }
    }

    public void onTidyUpEnd2s(IRel rel) throws Exception {
        if (rel != null) {
            IObject end1 = rel.GetEnd1();
            IObject end2 = rel.GetEnd2();
            if (end1 != null)
                end1.GetEnd1Relationships().remove(rel);
            if (end2 != null)
                end2.GetEnd2Relationships().remove(rel);
            this.remove(rel, true);
        }
    }

    public void onTidyUpRelToRels(IRel rel, boolean pbln1To2) throws Exception {
        if (pbln1To2) {
            IRelCollection relCollection = new RelCollection(relCollectionTypes.End1s);
            relCollection.addRange(rel.GetEnd1Relationships());
            Iterator<IObject> iObjectIterator = relCollection.GetEnumerator();
            while (iObjectIterator.hasNext()) {
                this.onTidyUpEnd1s(iObjectIterator.next().toInterface(IRel.class));
            }
        } else {
            IRelCollection relCollection = new RelCollection(relCollectionTypes.End2s);
            relCollection.addRange(rel.GetEnd2Relationships());
            Iterator<IObject> iObjectIterator = relCollection.GetEnumerator();
            while (iObjectIterator.hasNext()) {
                this.onTidyUpEnd2s(iObjectIterator.next().toInterface(IRel.class));
            }
        }
    }

    @Override
    public void remove(Collection<IObject> iObjects, boolean removeFromProcessCacheOnly) {
        if (iObjects != null && iObjects.size() > 0) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                List<IObject> objects = iObjects.stream().filter(c -> !c.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)).collect(Collectors.toList());
                List<IObject> rels = iObjects.stream().filter(c -> c.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)).collect(Collectors.toList());
                if (objects.size() > 0) {
                    for (IObject object : objects) {
                        this.onRemove(object);
                    }
                }
                if (rels.size() > 0) {
                    for (IObject rel : rels) {
                        this.onCleanRel(rel.toInterface(IRel.class));
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
                if (!removeFromProcessCacheOnly) {
                    if (this.members.size() > 0) {
                        for (ICache groupMember : this.members) {
                            groupMember.remove(iObjects);
                        }
                    }
                }
            }
        }
    }

    public void onRemove(IObject iObject) {
        if (iObject != null) {
            if (!StringUtils.isEmpty(iObject.UID())) {
                this.objectsByUID.remove(iObject.UID());
            }
            if (!StringUtils.isEmpty(iObject.OBID()))
                this.objectsByOBID.remove(iObject.OBID());
        }
    }

    @Override
    public IObject queryObjectsByUIDAndClassDefinition(String uid, String classDefinition) {
        if (!StringUtils.isEmpty(uid)) {
            IObject iObject = this.getObjectByClassDefCache(uid, classDefinition);
            if (iObject != null && this.isValid(iObject))
                return iObject;

            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinition);
            CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, null, uid);
            return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        }
        return null;
    }

    @Override
    public IObject queryObjectsByUIDAndClassDefinition(String uid, String domainUID, String classDefinition) {
        if (!StringUtils.isEmpty(uid)) {
            try {
                IObject item = this.item(uid, domainUID, false);
                if (item != null && item.fromDb()) {
                    if (!StringUtils.isEmpty(classDefinition)) {
                        if (item.ClassDefinitionUID().equalsIgnoreCase(classDefinition))
                            return item;
                    } else
                        return item;
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }

            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinition);
            CIMContext.Instance.QueryEngine().addDomainUIDForQuery(queryRequest, null, domainUID);
            CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, null, uid);
            return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        }
        return null;
    }

    @Override
    public IClassDef Rel() throws Exception {
        IObject item = this.item(HardCodeHelper.CLASSDEF_REL, domainInfo.SCHEMA.toString(), false);
        if (item != null)
            return item.toInterface(IClassDef.class);
        return null;
    }

    @Override
    public IObject item(String uid, String domainUID, boolean fromDb) {
        IObject result = null;
        if (!StringUtils.isEmpty(uid)) {
            result = this.getObjectByUIDAndDomainUIDCache(uid, domainUID);
            if (result != null && this.isValid(result))
                return result;
            if (fromDb) {
                if (!StringUtils.isEmpty(uid)) {
                    QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
                    if (!StringUtils.isEmpty(domainUID))
                        CIMContext.Instance.QueryEngine().addDomainUIDForQuery(queryRequest, null, domainUID);
                    CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, null, uid);
                    if (queryRequest.getDomains().size() == 0)
                        queryRequest.getDomains().addAll(domainInfo.defaultDomainScopes());
                    IObject object = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
                    if (object != null)
                        this.refresh(object);
                }
            }
        }
        return result;
    }

    @Override
    public IObject item(String uid, String domainUID) {
        return this.item(uid, domainUID, false);
    }

    @Override
    public IObject getObjectByUIDAndDomainUIDCache(String uid, String domainUID, String config, Boolean fromDb) {
        IObject result = null;
        if (!StringUtils.isEmpty(uid)) {
            result = this.getObjectByUIDAndDomainUIDCache(uid, domainUID);
            if (result != null && this.isValid(result)) {
                if (!StringUtils.isEmpty(config)) {
                    if (result.underConfigInd(config))
                        return result;
                    else
                        result = null;
                } else
                    return result;
            }
            if (fromDb) {
                if (!StringUtils.isEmpty(uid)) {
                    QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
                    if (!StringUtils.isEmpty(domainUID))
                        CIMContext.Instance.QueryEngine().addDomainUIDForQuery(queryRequest, null, domainUID);
                    CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, null, uid);
                    if (queryRequest.getDomains().size() == 0)
                        queryRequest.getDomains().addAll(domainInfo.defaultDomainScopes());
                    IObject object = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
                    if (object != null)
                        this.refresh(object);
                }
            }
        }
        return result;
    }

    @Override
    public IObject item(String uid, boolean fromDb) {
        return this.item(uid, "", fromDb);
    }

    @Override
    public IObject item(String uid) {
        return this.item(uid, "", false);
    }

    @Override
    public IObjectCollection item(String[] uids) throws Exception {
        IObjectCollection result = new ObjectCollection();
        if (uids != null && uids.length > 0) {
            for (String objUID : uids) {
                IObject item = this.item(objUID, false);
                if (item != null)
                    result.append(item);
            }
        }
        return result;
    }

    @Override
    public void refresh(IObjectCollection collection) {
        if (collection != null && collection.hasValue()) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.Lock());
                Iterator<IObject> e = collection.GetEnumerator();
                List<IObject> rels = new ArrayList<>();
                while (e.hasNext()) {
                    IObject object = e.next();
                    String relDefOrClassDef = object.ClassDefinitionUID();
                    if (relDefOrClassDef.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                        rels.add(object.toInterface(IRel.class));
                    } else {
                        if (this.cachedOrNot(object)) {
                            this.onRefresh(object);
                        }
                    }
                }
                if (CommonUtility.hasValue(rels)) {
                    for (IObject rel : rels) {
                        if (this.cachedOrNot(rel))
                            this.onAddRelLocally(rel.toInterface(IRel.class));
                    }
                }
            } catch (Exception exception) {
                log.error("refresh by object collection failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
            }
        }
    }

    @Override
    public void refresh(IObject object) {
        if (object != null) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.Lock());
                String classDef = object.ClassDefinitionUID();
                if (classDef.equalsIgnoreCase(classDefinitionType.Rel.toString())) {
                    if (this.cachedOrNot(object))
                        this.onAddRelLocally(object.toInterface(IRel.class));
                } else {
                    if (this.cachedOrNot(object)) {
                        this.onRefresh(object);
                    }
                }
            } catch (Exception exception) {
                log.error("refresh with object failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
            }
        }
    }

    @Override
    public IObject getObjectByOBID(String obid, String classDefinitionUid) throws Exception {
        if (!StringUtils.isEmpty(obid)) {
            IObject object = this.getObjectByOBIDCache(obid);
            if (object != null && this.isValid(object))
                return object;

            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, null, obid);
            if (!StringUtils.isEmpty(classDefinitionUid)) {
                CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionUid);
                if (HardCodeHelper.CLASSDEF_REL.equalsIgnoreCase(classDefinitionUid))
                    CIMContext.Instance.QueryEngine().setQueryForRelationship(queryRequest, true);
            }
            IObject iObject = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
            this.refresh(iObject);
            return iObject;
        }
        return null;
    }

    @Override
    public IObjectCollection getObjectsByUIDs(Collection<String> uids) throws Exception {
        IObjectCollection result = new ObjectCollection();
        if (CommonUtility.hasValue(uids)) {
            for (String uid : uids) {
                IObject item = this.item(uid, null, false);
                if (item != null && item.fromDb())
                    result.append(item);
            }
        }
        return result;
    }

    @Override
    public IObjectCollection getObjectByUID(Collection<String> uids) {
        if (CommonUtility.hasValue(uids)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, operator.in, String.join(",", uids));
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    @Override
    public IObject getItemByUIDAndClassDefinition(String uid, String classDefinitionUID) {
        if (!StringUtils.isEmpty(uid) && !StringUtils.isEmpty(classDefinitionUID)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, null, uid);
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionUID);
            return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        }
        return null;
    }

    @Override
    public IObject getItemByOBIDAndClassDefinition(String obid, String classDefinitionUID) {
        if (!StringUtils.isEmpty(obid) && !StringUtils.isEmpty(classDefinitionUID)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, null, obid);
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionUID);
            return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        }
        return null;
    }

    @Override
    public IObject itemByUIDOrOBID(String uidOrObid, String classDefinitionUID) throws Exception {
        if (!StringUtils.isEmpty(uidOrObid) && !StringUtils.isEmpty(classDefinitionUID)) {
            IObject result = this.getObjectByOBIDCache(uidOrObid);
            if (result == null)
                result = this.item(uidOrObid, false);
            if (result != null && this.isValid(result))
                return result;
            if (result == null)
                result = this.getItemByOBIDAndClassDefinition(uidOrObid, classDefinitionUID);
            if (result == null)
                result = this.getItemByUIDAndClassDefinition(uidOrObid, classDefinitionUID);
            return result;
        }
        return null;
    }

    @Override
    public IObject itemByName(String name, String classDefinitionUID) throws Exception {
        if (!StringUtils.isEmpty(name)) {
            IObject result = this.getObjectByNameAndClassDefinitionUID(name, classDefinitionUID);
            if (result != null && this.isValid(result))
                return result;
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, null, name);
            if (!StringUtils.isEmpty(classDefinitionUID))
                CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionUID);
            IObjectCollection objectCollection = CIMContext.Instance.QueryEngine().query(queryRequest);
            if (objectCollection != null && objectCollection.hasValue())
                return objectCollection.get(0);
        }
        return null;
    }

    @Override
    public boolean containsByOBID(String obid) {
        if (!StringUtils.isEmpty(obid)) {
            boolean result = this.objectsByOBID.getOrDefault(obid, null) != null;
            if (!result) {
                if (this.members().size() > 0) {
                    for (ICache cache : this.members()) {
                        boolean flag = cache.containsByOBID(obid);
                        if (flag)
                            return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isValid(IObject object) {
        if (object != null) {
            if (object.fromDb())
                return true;
            else
                return CIMContext.Instance.Transaction().contains(object);
        }
        return false;
    }

    @Override
    public IObject itemByUIDAndDomainUID(String uid, String domainUID) {
        if (!StringUtils.isEmpty(uid)) {
            Map<String, IObject> objects = this.objectsByUID.getOrDefault(uid, null);
            if (CommonUtility.hasValue(objects)) {
                if (!StringUtils.isEmpty(domainUID)) {
                    IObject object = objects.values().stream().filter(c -> c.DomainUID().equalsIgnoreCase(domainUID)).findFirst().orElse(null);
                    if (object != null)
                        return object;
                }
                return new ArrayList<IObject>(objects.values()).get(0);
            } else {
                for (ICache cache : this.members) {
                    IObject current = cache.itemByUIDAndDomainUID(uid, domainUID);
                    if (current != null)
                        return current;
                }
            }
        }
        return null;
    }

    @Override
    public void onAddRelLocally(IRel rel) throws Exception {
        if (rel != null) {
            String uid1 = rel.UID1();
            String uid2 = rel.UID2();
            String domainUID1 = rel.DomainUID1();
            String domainUID2 = rel.DomainUID2();
            String obid1 = rel.OBID1();
            String obid2 = rel.OBID2();
            IObject end1 = null;
            IObject end2 = null;
            if (!StringUtils.isEmpty(obid1))
                end1 = this.getObjectByOBIDCache(obid1);
            if (end1 != null)
                end1.GetEnd1Relationships().replace(rel);

            if (!StringUtils.isEmpty(obid2))
                end2 = this.getObjectByOBIDCache(obid2);
            if (end2 != null)
                end2.GetEnd2Relationships().replace(rel);

            end1 = this.itemByUIDAndDomainUID(uid1, domainUID1);
            if (end1 != null)
                end1.GetEnd1Relationships().replace(rel);

            end2 = this.itemByUIDAndDomainUID(uid2, domainUID2);
            if (end2 != null)
                end2.GetEnd2Relationships().replace(rel);
        }
    }

    @Override
    public void onAddLocally(String identity, IObject object) throws Exception {
        if (StringUtils.isEmpty(identity))
            identity = this.identity();
        if (object != null) {
            if (StringUtils.isEmpty(object.OBID())) {
                log.error("invalid Object as OBID is null");
            } else {
                if (this.cachedOrNot(identity, object)) {
                    String classDefinitionUID = object.ClassDefinitionUID();
                    if (classDefinitionUID.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                        this.onAddRelLocally(object.toInterface(IRel.class));
                    } else
                        this.onRefresh(object);
                }
            }
        }
    }

    @Override
    public void onAddLocally(IObject object) throws Exception {
        if (object != null) {
            if (StringUtils.isEmpty(object.OBID())) {
                log.error("invalid Object as OBID is null");
            } else {
                if (this.cachedOrNot(object)) {
                    String classDefinitionUID = object.ClassDefinitionUID();
                    if (classDefinitionUID.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                        this.onAddRelLocally(object.toInterface(IRel.class));
                    } else
                        this.onRefresh(object);
                }
            }
        }
    }

    @Override
    public void onCleanRel(IRel rel) throws Exception {
        if (rel != null) {
            String uid1 = rel.UID1();
            String uid2 = rel.UID2();
            String domainUID1 = rel.DomainUID1();
            String domainUID2 = rel.DomainUID2();
            String obid1 = rel.OBID1();
            String obid2 = rel.OBID2();
            IObject end1 = null;
            IObject end2 = null;
            if (!StringUtils.isEmpty(obid1))
                end1 = this.objectsByOBID.getOrDefault(obid1, null);
            if (end1 != null)
                end1.GetEnd1Relationships().remove(rel);

            if (!StringUtils.isEmpty(obid2))
                end2 = this.objectsByOBID.getOrDefault(obid2, null);
            if (end2 != null)
                end2.GetEnd2Relationships().remove(rel);

            end1 = this.itemByUIDAndDomainUID(uid1, domainUID1);
            if (end1 != null)
                end1.GetEnd1Relationships().remove(rel);

            end2 = this.itemByUIDAndDomainUID(uid2, domainUID2);
            if (end2 != null)
                end2.GetEnd2Relationships().remove(rel);
        }
    }

    @Override
    public void clear(IObjectCollection collection) {
        if (collection != null && collection.hasValue()) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                Iterator<IObject> e = collection.GetEnumerator();
                while (e.hasNext()) {
                    IObject next = e.next();
                    if (next.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                        IRel rel = next.toInterface(IRel.class);
                        this.onCleanRel(rel);
                    } else {
                        this.onRemoveByOBID(next.OBID());
                        this.onRemoveByUID(next.OBID());
                    }
                }
            } catch (Exception exception) {
                log.error("clean up with object collection failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    @Override
    public void onRefreshOBIDCollectionCache(IObject object) {
        if (object != null) {
            String obid = object.OBID();
            IObject obidOrDefault = this.objectsByOBID.getOrDefault(obid, null);
            if (obidOrDefault == null)
                this.objectsByOBID.put(obid, object);
            else
                this.objectsByOBID.replace(obid, object);
        }
    }

    @Override
    public void onRefresh(IObject object) {
        this.onRefreshOBIDCollectionCache(object);
        this.onRefreshUIDCollectionCache(object);
    }

    @Override
    public void addLocally(String identity, IObjectCollection objectCollection) throws Exception {
        if (StringUtils.isEmpty(identity))
            identity = this.identity();
        Exception ex = null;
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            this.onAddLocally(identity, objectCollection);
        } catch (Exception exception) {
            ex = exception;
            log.error("add collection locally failed", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
        if (ex != null)
            throw ex;

    }

    @Override
    public void addLocally(IObjectCollection objectCollection) throws Exception {
        this.addLocally(null, objectCollection);
    }

    @Override
    public void onAddLocally(String identity, IObjectCollection objectCollection) throws Exception {
        if (StringUtils.isEmpty(identity))
            identity = this.identity();
        if (objectCollection != null && objectCollection.size() > 0) {
            Iterator<IObject> e = objectCollection.GetEnumerator();
            while (e.hasNext()) {
                IObject next = e.next();
                this.onAddLocally(identity, next);
            }
        }
    }

    @Override
    public void onAddLocally(IObjectCollection objectCollection) throws Exception {
        if (objectCollection != null && objectCollection.size() > 0) {
            Iterator<IObject> e = objectCollection.GetEnumerator();
            while (e.hasNext()) {
                IObject next = e.next();
                this.onAddLocally(next);
            }
        }
    }

    @Override
    public void addLocally(IObject object) throws Exception {
        if (object != null) {
            log.trace("enter to add locally for " + object.toErrorPop());
            Exception ex = null;
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                this.onAddLocally(object);
            } catch (Exception exception) {
                ex = exception;
                log.error("add locally progress failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
                log.trace("complete to add locally for " + object.toErrorPop());
            }
            if (ex != null)
                throw ex;
        }
    }

    @Override
    public void removeByOBID(String pstrOBID) {
        log.trace("enter to remove by OBID");
        if (!StringUtils.isEmpty(pstrOBID)) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                this.onRemoveByOBID(pstrOBID);
            } catch (Exception exception) {
                log.error("remove by obid failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
        log.trace("complete to remove by OBID");
    }

    @Override
    public void onRemoveByUID(String uid) {
        if (!StringUtils.isEmpty(uid)) {
            Map<String, IObject> objectList = this.objectsByUID.getOrDefault(uid, null);
            if (CommonUtility.hasValue(objectList)) {
                for (Map.Entry<String, IObject> iObjectEntry : objectList.entrySet())
                    this.objectsByOBID.remove(iObjectEntry.getValue().OBID());
            }
            this.objectsByUID.remove(uid);
        }
    }

    @Override
    public void onRemoveByOBID(String obid) {
        if (!StringUtils.isEmpty(obid)) {
            IObject iObject = this.getObjectByOBIDCache(obid);
            if (iObject != null) {
                if (!StringUtils.isEmpty(iObject.UID())) {
                    this.objectsByUID.remove(iObject.UID());
                }
            }
            this.objectsByOBID.remove(obid);
        }
    }

    @Override
    public void reset() {
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.Lock());
            this.members().clear();
            this.onReset();
        } catch (Exception exception) {
            log.error("clean up " + this.identity() + " cache failed", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
        }
    }

    public void addCachedClassDefs(String identity, List<String> classDefs) {
        if (CommonUtility.hasValue(classDefs))
            CacheConfigurationService.Instance.setClassDefsToBeCached(identity, classDefs);
    }

    public void addCachedClassDefs(List<String> classDefs) {
        this.addCachedClassDefs(this.identity(), classDefs);
    }

    public void addCachedRelDefs(List<String> relDefs) {
        this.addCachedRelDefs(this.identity(), relDefs);
    }

    public void addCachedRelDefs(String identity, List<String> relDefs) {
        if (CommonUtility.hasValue(relDefs))
            CacheConfigurationService.Instance.setRelDefsToBeCached(identity, relDefs);
    }

    @Override
    public boolean cachedOrNot(String identity, IObject object) throws Exception {
        if (object != null) {
            if (this.allCache())
                return true;
            if (StringUtils.isEmpty(identity))
                identity = this.identity();
            String classDefinitionUID = object.ClassDefinitionUID();
            if (classDefinitionUID.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                IRel rel = object.toInterface(IRel.class);
                return CacheConfigurationService.Instance.cacheOrNot(identity, rel.RelDefUID());
            } else
                return CacheConfigurationService.Instance.cacheOrNot(identity, classDefinitionUID);
        }
        return false;
    }

    @Override
    public boolean cachedOrNot(IObject object) throws Exception {
        return this.cachedOrNot(null, object);
    }

    @Override
    public abstract int level();

    @Override
    public void onReset() {
        this.objectsByUID.clear();
        this.objectsByOBID.clear();
    }

    @Override
    public void reInitialize() throws Exception {
        log.info("enter to refresh " + this.identity() + "cache for context");
        StopWatch stopWatch = PerformanceUtility.start();
        this.mblnInitialized.set(false);
        this.reset();
        this.initialize();
        log.info("complete to refresh " + this.identity() + " cache" + PerformanceUtility.stop(stopWatch));
    }

    @Override
    public IObjectCollection getObjectsByOBIDCache(String[] obids) {
        IObjectCollection result = new ObjectCollection();
        if (obids != null && obids.length > 0) {
            for (String obid : obids) {
                IObject object = this.getObjectByOBIDCache(obid);
                if (object != null)
                    result.append(object);
            }
            if (result.size() != obids.length) {
                if (this.members().size() > 0) {
                    for (ICache cache : this.members()) {
                        IObjectCollection collection = cache.getObjectsByOBIDCache(obids);
                        result.addRangeUniquely(collection);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public abstract void onInitializing() throws Exception;

    @Override
    public IObject getObjectByNameAndClassDefinitionUIDFromOBIDCache(String name, String classDefinitionUID) {
        if (!StringUtils.isEmpty(name)) {
            String currentScope = this.getCurrentScope();
            if (this.objectsByOBID.size() > 0) {
                for (Map.Entry<String, IObject> iObjectEntry : this.objectsByOBID.entrySet()) {
                    boolean flag = false;
                    if (iObjectEntry.getValue().Name().equalsIgnoreCase(name)) {
                        if (!StringUtils.isEmpty(classDefinitionUID)) {
                            if (iObjectEntry.getValue().ClassDefinitionUID().equalsIgnoreCase(classDefinitionUID))
                                flag = true;
                        } else
                            flag = true;
                    }
                    if (flag) {
                        if (iObjectEntry.getValue().underConfigInd(currentScope))
                            return iObjectEntry.getValue();
                    }
                }
            }
            if (this.members.size() > 0) {
                for (ICache cache : this.members) {
                    IObject iObject = cache.getObjectByNameAndClassDefinitionUIDFromOBIDCache(name, classDefinitionUID);
                    if (iObject != null)
                        return iObject;
                }
            }
        }
        return null;
    }

    protected void amountCachedConfiguration(classDefinitionType definitionType) {
        IObjectCollection classDefinitions = this.getObjectsByClassDefCache(definitionType.toString());
        if (classDefinitions != null && classDefinitions.hasValue()) {
            Map<String, List<String>> lcolClassDefs = new HashMap<>();
            Iterator<IObject> e = classDefinitions.GetEnumerator();
            while (e.hasNext()) {
                ISchemaObject current = e.next().toInterface(ISchemaObject.class);
                boolean flag = current.Cached();
                if (flag) {
                    String cachedLevel = current.CachedLevel();
                    CommonUtility.doAddElementGeneral(lcolClassDefs, cachedLevel, current.UID());
                }
            }
            switch (definitionType) {
                case ClassDef:
                    CacheConfigurationService.Instance.setDefinitionToBeCached(CacheWrapper.CacheWrapperType.classDef, lcolClassDefs);
                    break;
                case RelDef:
                    CacheConfigurationService.Instance.setDefinitionToBeCached(CacheWrapper.CacheWrapperType.relDef, lcolClassDefs);
                    break;
            }
        }
    }

    protected void amountCacheConfigurationFromSharedCacheService() {
        this.amountCachedConfiguration(classDefinitionType.ClassDef);
        this.amountCachedConfiguration(classDefinitionType.RelDef);
    }
}
