package ccm.server.cache;

import ccm.server.models.query.QueryRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IClassDef;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public interface ICache extends Comparator<ICache> {

    int level();

    String getCurrentScope();

    boolean allCache();

    void setAllCache(boolean flag);

    String identity();

    Comparator<ICache> comparator();

    void addMember(ICache cache);

    void removeMember(ICache cache);

    List<ICache> members();

    boolean initialized();

    void onInitialized() throws Exception;

    ReentrantReadWriteLock Lock();

    IObject getObjectByNameAndClassDefinitionUIDFromUIDCache(String name, String classDefinitionUID);

    IObject getObjectByUIDAndDomainUIDCache(String uid, String domainUID);

    IObject getObjectByUIDAndDomainUIDCache(String uid, String domainUID, String config,Boolean fromDb);

    IObjectCollection queryRelsByRelDefs(List<String> relDefinitions) throws Exception;

    IObjectCollection queryObjectsByClassDefinitions(List<String> classDefinitions);

    IObject getObjectByOBIDCache(String obid);

    IObject getByUIDAndDomainUID(String uid, String domainUID) throws Exception;

    boolean isValid(IObject object);

    IObject itemByUIDOrOBID(String uidOrObid, String classDefinitionUID) throws Exception;

    IObject getItemByUIDAndClassDefinition(String uid, String classDefinitionUID);

    IObject getItemByOBIDAndClassDefinition(String obid, String classDefinitionUID);

    IObjectCollection getObjectsByUIDs(Collection<String> uids) throws Exception;

    IObjectCollection getObjectByUID(Collection<String> uids);

    IObject getObjectByOBID(String obid, String classDefinitionUid) throws Exception;

    IObject itemByName(String name, String classDefinitionUID) throws Exception;

    void onRemoveByUID(String uid);

    void removeByUID(String uid);

    boolean cachedOrNot(IObject object) throws Exception;

    boolean cachedOrNot(String identity, IObject object) throws Exception;

    IObjectCollection getByUID(String uid) throws Exception;

    void initialize() throws Exception;

    boolean containsByOBID(String obid);

    IObject getObjectByOBID(String obid);

    IObjectCollection getItems();

    IObject getObjectByClassDefCache(String uid, String classDef);

    void remove(IObject object);

    void remove(IObject object, boolean removeFromProcessCacheOnly);

    void remove(Collection<IObject> objects);

    void remove(Collection<IObject> objects, boolean removeFromProcessCacheOnly);

    void onRefreshUIDCollectionCache(IObject object);

    IObjectCollection getObjectsByClassDefCache(String classDef);

    void onAddRelLocally(IRel rel) throws Exception;

    void onAddLocally(IObject object) throws Exception;

    void onAddLocally(String identity, IObject object) throws Exception;

    void onCleanRel(IRel rel) throws Exception;

    void refresh(IObjectCollection collection);

    void clear(IObjectCollection collection);

    void onRefreshOBIDCollectionCache(IObject object);

    IObject itemByUIDAndDomainUID(String uid, String domainUID);

    void onRefresh(IObject object);

    void refresh(IObject object);

    IObject item(String uid, String domainUID, boolean fromDb);

    <T> T item(String uid, String domainUID, boolean fromDb, Class<T> clazz);

    IObject item(String uid, String domainUID);

    IObject item(String uid, boolean fromDb);

    IObject item(String uid);

    IObjectCollection item(String[] uids) throws Exception;

    IClassDef Rel() throws Exception;

    IObjectCollection queryObjectsByUIDAndClassDefinition(Collection<String> uids, String classDefinition) throws Exception;

    IObjectCollection queryObjectsByOBIDAndClassDefinition(Collection<String> obdis, String classDefinition) throws Exception;

    IObject queryObjectsByUIDAndClassDefinition(String uid, String classDefinition);

    IObject queryObjectsByUIDAndClassDefinition(String uid, String domainUID, String classDefinition);

    void addLocally(String identity, IObjectCollection objectCollection) throws Exception;

    void addLocally(IObjectCollection objectCollection) throws Exception;

    void onAddLocally(IObjectCollection objectCollection) throws Exception;

    void onAddLocally(String identity, IObjectCollection objectCollection) throws Exception;

    void addLocally(IObject object) throws Exception;

    void removeByOBID(String obid);

    void onRemoveByOBID(String obid);

    void reset();

    void onReset();

    void reInitialize() throws Exception;

    IObject getObjectByNameAndClassDefinitionUIDFromOBIDCache(String name, String classDefinitionUID);

    void onInitializing() throws Exception;

    void setScopePrefixForQueryRequestHandler(QueryRequest queryRequest);

    IObjectCollection getObjectsByOBIDCache(String[] obids);

    void setScopePrefixForQueryRequest(IObjectCollection configurationItems, QueryRequest queryRequest);
}
