package ccm.server.transactions;

import ccm.server.context.DBContext;
import ccm.server.module.service.base.IInternalService;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.IObject;
import ccm.server.shared.ISharedLocalService;
import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ITransaction extends IInternalService {

    ITransaction instantiate();

    <T> T getService(Class<T> classz);

    void setService(Object object);

    String getTransactionId();

    boolean inTransaction();

    void start() throws Exception;

    Date getEffectiveDate();

    String getLoginUser();

    boolean contains(IObject object);

    Map<String, IObject> ObjectsInTransaction();

    void commit() throws Exception;

    void commit(boolean pblnGenerateUIDs) throws Exception;

    void add(IObject object) throws Exception;

    void addRange(IObjectCollection objectCollection) throws Exception;

    void addDeferredOnRelationshipRemoving(IObject object, String pstrInterfaceDef, String pstrMethodDef, Object[] pArrArgs);

    ConcurrentHashMap<String, HashSet<IObject>> CreatesAndUpdatesByUniqueKey();

    ConcurrentHashSet<IObject> TerminatedItemsToSerialize();

    ConcurrentHashSet<IObject> DeletedItemsToSerialize();

    ConcurrentHashSet<IObject> NewItemsToSerialize();

    ConcurrentHashSet<IObject> RefreshItemsToSerialize();

    ConcurrentHashMap<String, IObject> UnEditedItems();

    boolean isENSActivated();

    void rollBack();

    void setENSActivated(boolean ensActivated);

    boolean hasOBIDInDeletedOrTerminatedItems(String obid);

    IObject getByObid(String obid);

    void setLoginUser(String username);

    void setConfigurationItem(ICIMConfigurationItem configurationItem);

    ICIMConfigurationItem getConfigurationItem();
}
