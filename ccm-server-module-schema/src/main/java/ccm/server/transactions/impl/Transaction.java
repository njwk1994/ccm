package ccm.server.transactions.impl;

import ccm.server.args.cancelArgs;
import ccm.server.args.createArgs;
import ccm.server.args.relArgs;
import ccm.server.context.CIMContext;
import ccm.server.context.DBContext;
import ccm.server.enums.*;
import ccm.server.helper.HardCodeHelper;
import ccm.server.model.QueueWrapper;
import ccm.server.model.ValidationMsg;
import ccm.server.models.LiteObject;
import ccm.server.models.LiteObjectCollection;
import ccm.server.module.impl.general.InternalServiceImpl;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.IDomain;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;
import ccm.server.schema.model.*;
import ccm.server.schema.model.pointer.MethodPointer;
import ccm.server.shared.ISharedLocalService;
import ccm.server.transactions.ITransaction;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import ccm.server.util.ReentrantLockUtility;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service("transaction")
@Slf4j
public class Transaction extends InternalServiceImpl implements ITransaction {
    private final AtomicBoolean inTransaction = new AtomicBoolean(false);
    private final ConcurrentHashMap<String, IObject> objects = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, IObject> relationships = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ConcurrentLinkedQueue<IObject> finalItems = new ConcurrentLinkedQueue<>();
    private final Stack<MethodPointer> deferredOnRelationRemoving = new Stack<>();
    private final AtomicBoolean deferredError = new AtomicBoolean(false);
    private Date mdtEffectiveDate = null;
    private String mstrLoginUser = null;
    private ICIMConfigurationItem mobjConfigurationItem = null;
    private final ConcurrentHashMap<String, HashSet<IObject>> marrCreatesAndUpdatesByUniqueKey = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, IObject> marrUnEditedItems = new ConcurrentHashMap<>();
    private boolean mblnUseENS = false;
    private boolean mblnAutoCommitInProgress;
    private int mintAutoCommitLimit;
    private final AtomicBoolean committing = new AtomicBoolean(false);
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private long mlngToBeCreated;
    private long mlngToBeDeleted;
    private long mlngToBeTerminated;
    private long mlngToBeUpdated;
    private long mlngToProcessed;
    private long mlngRelToBeCreated;
    private long mlngRelToBeUpdated;
    private long mlngRelToBeTerminated;
    private long mlngRelToBeDeleted;
    private long mlngRelProcessed;
    private long mlngTotalProcessed;
    private final ConcurrentHashSet<IObject> mcolNewItemsToSerialize = new ConcurrentHashSet<IObject>();
    private final ConcurrentHashSet<IObject> mcolRefreshItemsToSerialize = new ConcurrentHashSet<IObject>();
    private final ConcurrentHashSet<IObject> mcolTerminatedItemsToSerialize = new ConcurrentHashSet<IObject>();
    private final ConcurrentHashSet<IObject> mcolDeletedItemsToSerialize = new ConcurrentHashSet<IObject>();
    private final Map<Class<?>, Object> registeredServices = new HashMap<>();

    public String getLoginUser() {
        return this.mstrLoginUser;
    }

    public Date getEffectiveDate() {
        return this.mdtEffectiveDate;
    }

    @Override
    public ITransaction instantiate() {
        return new Transaction();
    }

    @Override
    public <T> T getService(Class<T> classz) {
        if (classz != null) {
            for (Map.Entry<Class<?>, Object> entry : this.registeredServices.entrySet()) {
                Class<?> key = entry.getKey();
                if (classz.isAssignableFrom(key))
                    return (T) entry.getValue();
            }
        }
        return null;
    }

    @Override
    public void setService(Object object) {
        if (object != null) {
            Class<?> aClass = object.getClass();
            if (this.registeredServices.containsKey(aClass))
                this.registeredServices.replace(aClass, object);
            else
                this.registeredServices.put(aClass, object);
        }
    }

    @Override
    public String getTransactionId() {
        return "";
    }

    public boolean inTransaction() {
        return this.inTransaction.get();
    }

    @Override
    public void setLoginUser(String username) {
        this.mstrLoginUser = username;
    }

    @Override
    public void setConfigurationItem(ICIMConfigurationItem configurationItem) {
        this.mobjConfigurationItem = configurationItem;
    }

    @Override
    public ICIMConfigurationItem getConfigurationItem() {
        return this.mobjConfigurationItem;
    }

    @Override
    public void start() throws Exception {
        if (!this.inTransaction.get()) {
            this.reset();
            this.inTransaction.set(true);
            this.mdtEffectiveDate = CommonUtility.Now();
            this.mstrLoginUser = null;
            try {
                this.mstrLoginUser = this.getService(ISharedLocalService.class).getCurrentLoginUser();
                this.mobjConfigurationItem = CIMContext.Instance.getMyConfigurationItem(this.mstrLoginUser);
            } catch (Exception ex) {
                log.warn("事务机制启动存在异常，原因是当前用户和项目配置没有获取到，请在此之后使用setLoginUser(String username)和setConfigurationItem(ICIMConfigurationItem.class)进行设置");
            }
        }
    }

    @Override
    public boolean contains(IObject object) {
        boolean result = false;
        if (this.objects.size() > 0) {
            result = this.objects.containsKey(object.OBID());
        }
        if (!result) {
            result = this.relationships.containsKey(object.OBID());
        }
        return result;
    }

    @Override
    public Map<String, IObject> ObjectsInTransaction() {
        return this.objects;
    }

    protected void cleanUpTransactionItems() {
        this.relationships.clear();
        this.objects.clear();
        this.mcolTerminatedItemsToSerialize.clear();
        this.mcolDeletedItemsToSerialize.clear();
        this.mcolRefreshItemsToSerialize.clear();
        this.mcolNewItemsToSerialize.clear();
        this.committedLiteObjects.clear();
        log.trace("complete to clean up transaction items include object(s) and relationship(s)");
    }

    protected void resetRecordingStatus() {
        this.mlngToBeCreated = 0L;
        this.mlngToBeDeleted = 0L;
        this.mlngToBeTerminated = 0L;
        this.mlngToBeUpdated = 0L;
        this.mlngToProcessed = 0L;
        this.mlngRelToBeCreated = 0L;
        this.mlngRelToBeDeleted = 0L;
        this.mlngRelToBeTerminated = 0L;
        this.mlngRelToBeUpdated = 0L;
        this.mlngRelProcessed = 0L;
        this.mlngTotalProcessed = 0L;
    }

    protected void resetSubmittingCache() {
        this.finalItems.clear();
        log.trace("complete to reset submitting cache of final items");
    }

    protected void resetProgressIndicator() {
        this.committing.set(false);
        this.processing.set(false);
        this.inTransaction.set(false);
        this.deferredError.set(false);
        log.trace("complete to reset progress indicator(s)");
    }

    @Override
    public void reset() {
        StopWatch stopWatch = PerformanceUtility.start();
        this.cleanUpTransactionItems();
        this.resetRecordingStatus();
        this.resetSubmittingCache();
        this.resetProgressIndicator();
        log.trace("complete to reset application transaction" + PerformanceUtility.stop(stopWatch));
    }

    protected void preProcess() {
        if (this.objects.size() > 0) {
            for (Map.Entry<String, IObject> objectEntry : this.objects.entrySet()) {
                this.onPreProcess(objectEntry.getValue());
            }
        }
        if (this.relationships.size() > 0) {
            for (Map.Entry<String, IObject> objectEntry : this.relationships.entrySet()) {
                this.onPreProcess(objectEntry.getValue());
            }
        }
    }

    @Override
    public void commit(boolean isGeneratedUids) throws Exception {
        if (this.inTransaction.get()) {
            Exception exception = null;
            this.processing.set(true);
            try {
                this.preProcess();
                if (this.finalItems.size() > 0) {
                    this.invokePreProcess(isGeneratedUids);
                    this.invokeProcess(isGeneratedUids);
                    this.invokeRelationshipRemoving();
                    this.invokeProcessed(isGeneratedUids);
                    this.invokePostProcessed(isGeneratedUids);
                    this.invokeProcessing();
                }
            } catch (Exception ex) {
                exception = ex;
                this.rollBack();
            }
            this.submitToDatabase();
            if (exception != null)
                throw exception;
        }
    }

    public void submitToDatabase() throws Exception {
        Exception exception = null;
        if (this.finalItems.size() > 0) {
            try {
                this.committing.set(true);
                Map<String, List<IObject>> objectsByConfig = new HashMap<>();
                int i = 1;
                for (IObject item : this.finalItems) {
                    String config = item.Config();
                    if (StringUtils.isEmpty(config))
                        config = "NULL";
                    List<IObject> iObjects = objectsByConfig.getOrDefault(config, new ArrayList<>());
                    iObjects.add(item);
                    if (objectsByConfig.containsKey(config))
                        objectsByConfig.replace(config, iObjects);
                    else
                        objectsByConfig.put(config, iObjects);
                    i++;
                }
                if (this.onWriteChangeToDataBase(objectsByConfig)) {
                    this.committing.set(false);
                    this.invokeCompleted();
                    this.writeChangesToCache();
                }
            } catch (Exception ex) {
                exception = ex;
                this.rollBack();
            } finally {
                this.reset();
            }
        }
        if (exception != null)
            throw exception;
    }

    private void invokeCompleted() {
        for (IObject item : this.finalItems) {
            Stack<MethodPointer> methodPointers = item.ClassBase().CompletedMethods();
            try {
                while (methodPointers.size() > 0) {
                    methodPointers.pop().invoke();
                }
            } catch (Exception exception) {
                log.warn(exception.getMessage());
            }
        }
    }

    private final int raiseCount = 2000;

    protected void setValueForInterface(IInterface anInterface, String propertyDefinitionUID, Object value) throws
            Exception {
        if (anInterface != null && !StringUtils.isEmpty(propertyDefinitionUID)) {
            IProperty property = anInterface.Properties().get(propertyDefinitionUID);
            if (property != null) {
                IPropertyValue propertyValue = property.CurrentValue();
                if (propertyValue != null) {
                    propertyValue.setValue(value);
                } else {
                    property.PropertyValues().add(new PropertyValue(property, value, null, null, null, null, propertyValueUpdateState.none));
                }
            } else {
                property = new PropertyDefault(propertyDefinitionUID);
                property.PropertyValues().add(new PropertyValue(property, value, null, null, null, null, propertyValueUpdateState.none));
            }
        }
    }

    protected void syncUserAndDate(IObject item, LiteObject liteObject) throws Exception {
        if (item != null && liteObject != null) {
            IInterface objectInterface = item.Interfaces().get(interfaceDefinitionType.IObject.toString());
            if (objectInterface != null) {
                this.setValueForInterface(objectInterface, propertyDefinitionType.CreationDate.toString(), liteObject.getCreationDate());
                this.setValueForInterface(objectInterface, propertyDefinitionType.CreationUser.toString(), liteObject.getCreationUser());
                this.setValueForInterface(objectInterface, propertyDefinitionType.TerminationDate.toString(), liteObject.getTerminationDate());
                this.setValueForInterface(objectInterface, propertyDefinitionType.TerminationUser.toString(), liteObject.getTerminationUser());
                this.setValueForInterface(objectInterface, propertyDefinitionType.LastUpdateDate.toString(), liteObject.getLastUpdateDate());
                this.setValueForInterface(objectInterface, propertyDefinitionType.LastUpdateUser.toString(), liteObject.getLastUpdateUser());
            }
        }
    }

    protected void writeChangesToCache() throws Exception {
        this.removeDeleteOrTerminatedProperties();
        StopWatch stopWatch = PerformanceUtility.start();
        int i = 1;
        IObjectCollection refreshItems = new ObjectCollection();
        IObjectCollection removeItems = new ObjectCollection();
        for (IObject item : this.finalItems) {
            this.syncUserAndDate(item, this.committedLiteObjects.getOrDefault(item.OBID(), null));
            item.commit();
            if (item.ObjectUpdateState() == objectUpdateState.none)
                refreshItems.append(item);
            else
                removeItems.append(item);
            i++;
        }
        if (refreshItems.hasValue())
            CIMContext.Instance.ProcessCache().refresh(refreshItems);

        if (removeItems.hasValue())
            CIMContext.Instance.ProcessCache().clear(refreshItems);

        log.trace("complete to refresh process cache" + PerformanceUtility.stop(stopWatch));
    }

    protected boolean isObjectUpdated(IObject item) {
        if (item != null) {
            Iterator<Map.Entry<String, IInterface>> entryIterator = item.Interfaces().GetEnumerator();
            while (entryIterator.hasNext()) {
                IInterface anInterface = entryIterator.next().getValue();
                if (anInterface.UpdateState() == interfaceUpdateState.created)
                    return true;
                Iterator<Map.Entry<String, IProperty>> entryIterator1 = anInterface.Properties().GetEnumerator();
                while (entryIterator1.hasNext()) {
                    IProperty property = entryIterator1.next().getValue();
                    IPropertyValue propertyValue = property.CurrentValue();
                    if (propertyValue != null && propertyValue.UpdateState() != propertyValueUpdateState.none)
                        return true;
                }
            }
        }
        return false;
    }

    protected void removeDeleteOrTerminatedProperties() {
        StopWatch stopWatch = PerformanceUtility.start();
        if (this.finalItems.size() > 0) {
            for (IObject currentObject : this.finalItems) {
                if (!currentObject.Terminated() && !currentObject.Deleted()) {
                    for (IInterface anInterface : currentObject.Interfaces().toArrayList()) {
                        if (anInterface.terminatedOrDeleted()) {
                            if (!anInterface.isIObjectOrIRel())
                                currentObject.Interfaces().remove(anInterface);
                        } else {
                            for (IProperty property : anInterface.Properties().toArrayList()) {
                                IPropertyValue propertyValue = property.CurrentValue();
                                if (propertyValue != null) {
                                    if (propertyValue.terminatedOrDeleted())
                                        anInterface.Properties().remove(property);
                                    else {
                                        IPropertyValueCollection valueCollection = property.PropertyValues();
                                        IPropertyValue[] tempValueCollection = new PropertyValue[valueCollection.size()];
                                        valueCollection.copyTo(tempValueCollection, 0);
                                        for (IPropertyValue value : tempValueCollection) {
                                            if (value.terminatedOrDeleted())
                                                property.PropertyValues().remove(value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //  log.info("remove delete or terminate properties from submitting collection" + PerformanceUtility.stop(stopWatch));
    }

    protected void onWriteChangesToDatabase(Map<String, LiteObjectCollection> liteObjectCollection) throws Exception {
        log.trace("enter to write changes into database: *****" + (liteObjectCollection != null ? liteObjectCollection.size() : 0) + "********");
        if (liteObjectCollection != null) {
            StopWatch stopWatch = PerformanceUtility.start();
            this.getService(DBContext.class).commit(liteObjectCollection);
            log.trace("final writing changes into database completed" + PerformanceUtility.stop(stopWatch));
        }
    }

    private void setDateForMeta(Map<String, LiteObjectCollection> liteObjectCollection) {
        if (liteObjectCollection != null) {
            StopWatch stopWatch = PerformanceUtility.start();
            Date now = this.getEffectiveDate();
            for (Map.Entry<String, LiteObjectCollection> collectionEntry : liteObjectCollection.entrySet()) {
                for (Map.Entry<String, LiteObject> objectEntry : collectionEntry.getValue().entrySet()) {
                    LiteObject liteObject = objectEntry.getValue();
                    liteObject.setDateForMeta(now);
                }
            }
            log.trace("complete to set date for lite object(s)" + PerformanceUtility.stop(stopWatch));
        }
    }

    private void doAddIntoMapLiteObjectCollection(Map<String, LiteObjectCollection> mapContainer, String
            key, LiteObject liteObject, String loginUserName) {
        LiteObjectCollection currentLiteObjects = mapContainer.getOrDefault(key, new LiteObjectCollection(loginUserName));
        currentLiteObjects.add(liteObject);
        if (mapContainer.containsKey(key))
            mapContainer.replace(key, currentLiteObjects);
        else
            mapContainer.put(key, currentLiteObjects);
    }

    private final ConcurrentHashMap<String, LiteObject> committedLiteObjects = new ConcurrentHashMap<>();

    public boolean onWriteChangeToDataBase(Map<String, List<IObject>> objectsPerConfig) throws Exception {
        if (objectsPerConfig != null && objectsPerConfig.size() > 0) {
            if (this.mintAutoCommitLimit > 0) {
                // add logic in future
            } else {
                String loginUserName = this.getLoginUser();
                Map<String, LiteObjectCollection> liteObjectCollectionByClassDef = new HashMap<>();
                Map<String, LiteObjectCollection> liteObjectCollectionByRelDef = new HashMap<>();
                for (Map.Entry<String, List<IObject>> listEntry : objectsPerConfig.entrySet()) {
                    List<IObject> currentObjects = listEntry.getValue();
                    String config = listEntry.getKey();
                    if (config != null && config.equalsIgnoreCase("NULL"))
                        config = "";
                    for (IObject currentObject : currentObjects) {
                        LiteObject liteObject = CIMContext.Instance.getObjectConversion().createLiteObjectForTransaction(currentObject);
                        if (liteObject == null)
                            throw new Exception("convert IObject to LiteObject failed");

                        String classDefinitionUid = currentObject.ClassDefinitionUID();
                        String key = config + "," + classDefinitionUid;
                        if (classDefinitionUid.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                            key = config + "," + liteObject.getREL().getRelDefUid();
                            this.doAddIntoMapLiteObjectCollection(liteObjectCollectionByRelDef, key, liteObject, loginUserName);
                        } else
                            this.doAddIntoMapLiteObjectCollection(liteObjectCollectionByClassDef, key, liteObject, loginUserName);
                    }
                }
                this.setTablePrefixForObj(liteObjectCollectionByClassDef);
                this.setTablePrefixForRel(liteObjectCollectionByRelDef);
                Map<String, LiteObjectCollection> finalSubmittedCollections = new HashMap<>();
                finalSubmittedCollections.putAll(liteObjectCollectionByClassDef);
                finalSubmittedCollections.putAll(liteObjectCollectionByRelDef);
                this.onWritingChangeToDataBase(finalSubmittedCollections);
                return true;
            }
        }
        return false;
    }

    public void onWritingChangeToDataBase(Map<String, LiteObjectCollection> liteObjectCollection) throws Exception {
        if (liteObjectCollection != null && liteObjectCollection.size() > 0) {
            StopWatch stopWatch = PerformanceUtility.start();
            this.setDateForMeta(liteObjectCollection);
            this.onWriteChangesToDatabase(liteObjectCollection);
            log.trace("complete to writing to DB" + PerformanceUtility.stop(stopWatch));
        }
    }

    protected String getIObjectConfigTable(String configInfo) {
        if (!StringUtils.isEmpty(configInfo)) {
            if (configInfo.contains("<<>>")) {
                String[] strings = configInfo.split("<<>>");
                if (strings.length > 1) {
                    return strings[1];
                }
            } else {
                IObject object = CIMContext.Instance.ProcessCache().item(configInfo, null, false);
                if (object != null) {
                    String tablePrefix = object.toInterface(ICIMConfigurationItem.class).TablePrefix();
                    if (!StringUtils.isEmpty(tablePrefix))
                        return tablePrefix;
                    else
                        log.trace(configInfo + "'s table prefix is empty");
                }
            }
        }
        return "";
    }

    protected void setTablePrefixForObj(Map<String, LiteObjectCollection> liteObjectCollectionByClassDef) throws Exception {
        if (liteObjectCollectionByClassDef != null && liteObjectCollectionByClassDef.size() > 0) {
            StopWatch stopWatch = PerformanceUtility.start();
            for (Map.Entry<String, LiteObjectCollection> collectionEntry : liteObjectCollectionByClassDef.entrySet()) {
                String key = collectionEntry.getKey();
                String[] prefixes = key.split(",");
                String classDefinitionUID = prefixes[1];
                IDomain domain = CIMContext.Instance.ProcessCache().getDomainForClassDef(classDefinitionUID);
                String tablePrefix = "";
                if (domain.ScopeWiseInd())
                    tablePrefix = this.getIObjectConfigTable(prefixes[0]) + domain.TablePrefix();
                else
                    tablePrefix = domain.TablePrefix();

                for (Map.Entry<String, LiteObject> objectEntry : collectionEntry.getValue().entrySet()) {
                    LiteObject liteObject = objectEntry.getValue();
                    liteObject.setTablePrefix(tablePrefix);
                    if (committedLiteObjects.containsKey(liteObject.getOBID()))
                        committedLiteObjects.replace(liteObject.getOBID(), liteObject);
                    else
                        committedLiteObjects.put(liteObject.getOBID(), liteObject);
                }
            }
            log.trace("complete to pre-process OBJ(s) about table prefix" + PerformanceUtility.stop(stopWatch));
        }
    }

    protected void setTablePrefixForRel(Map<String, LiteObjectCollection> relLiteObjectCollection) throws Exception {
        log.trace("enter to pre-process REL(s) for table prefix");
        if (relLiteObjectCollection != null && relLiteObjectCollection.size() > 0) {
            StopWatch stopWatch = PerformanceUtility.start();
            for (Map.Entry<String, LiteObjectCollection> objectCollectionEntry : relLiteObjectCollection.entrySet()) {
                String key = objectCollectionEntry.getKey();
                String configTable = this.getIObjectConfigTable(key.split(",")[0]);
                // log.info("final configuration table for " + key + " is " + configTable + " during processing for rel");
                for (Map.Entry<String, LiteObject> stringLiteObjectEntry : objectCollectionEntry.getValue().entrySet()) {
                    LiteObject liteObject = stringLiteObjectEntry.getValue();

                    String domainUid = liteObject.getREL().getDomainUid();
                    if (domainUid == null || StringUtils.isEmpty(domainUid) || domainUid.equalsIgnoreCase(domainInfo.UNKNOWN.toString()))
                        liteObject.getREL().setDomainUid(liteObject.getREL().getDomainUid1());
                    if (domainUid == null || StringUtils.isEmpty(domainUid) || domainUid.equalsIgnoreCase(domainInfo.UNKNOWN.toString()))
                        liteObject.getREL().setDomainUid(liteObject.getREL().getDomainUid2());

                    IDomain domain1 = CIMContext.Instance.ProcessCache().getDomainForClassDef(liteObject.getREL().getClassDefinitionUid1());
                    IDomain domain2 = CIMContext.Instance.ProcessCache().getDomainForClassDef(liteObject.getREL().getClassDefinitionUid2());
                    if (domain1 != null) {
                        if (domain1.ScopeWiseInd())
                            liteObject.setTablePrefix(configTable + domain1.TablePrefix());
                        else
                            liteObject.setTablePrefix(domain1.TablePrefix());
                    }
                    if (domain2 != null) {
                        if (domain2.ScopeWiseInd())
                            liteObject.setTablePrefix(configTable + domain2.TablePrefix());
                        else
                            liteObject.setTablePrefix(domain2.TablePrefix());
                    }
                    if (committedLiteObjects.containsKey(liteObject.getOBID()))
                        committedLiteObjects.replace(liteObject.getOBID(), liteObject);
                    else
                        committedLiteObjects.put(liteObject.getOBID(), liteObject);
                }
            }
            log.trace("complete to pre-process REL(s) about table prefix" + PerformanceUtility.stop(stopWatch));
        }
    }

    protected void invokePostProcessed(boolean pblnGenerateUIDs) throws Exception {
        ConcurrentHashSet<ValidationMsg> result = new ConcurrentHashSet<>();
        QueueWrapper queueWrapper = new QueueWrapper(this.finalItems, deferredMethodType.PostProcessed);
        if (queueWrapper.getSize() > 0) {
            for (IObjectCollection objectCollection : queueWrapper.getQueues()) {
                Iterator<IObject> objectIterator = objectCollection.GetEnumerator();
                while (objectIterator.hasNext()) {
                    IObject iObject = objectIterator.next();
                    this.invokePostProcessed(iObject, pblnGenerateUIDs, result);
                }
            }
            this.remountSubmittingItems(queueWrapper.getProcessedItems());
            this.raiseError(result);
        }
    }

    protected void invokeProcessed(boolean isGenerateUids) throws Exception {
        ConcurrentHashSet<ValidationMsg> result = new ConcurrentHashSet<>();
        QueueWrapper queueWrapper = new QueueWrapper(this.finalItems, deferredMethodType.Processed);
        if (queueWrapper.getSize() > 0) {
            for (IObjectCollection objectCollection : queueWrapper.getQueues()) {
                Iterator<IObject> iterator = objectCollection.GetEnumerator();
                while (iterator.hasNext()) {
                    IObject object = iterator.next();
                    this.invokeProcessed(object, isGenerateUids, result);
                    if (result.size() > 0)
                        break;
                }
                if (result.size() > 0)
                    break;

            }
            this.remountSubmittingItems(queueWrapper.getProcessedItems());
            this.raiseError(result);
        }
    }

    protected void invokeRelationshipRemoving() throws Exception {
        List<ValidationMsg> result = new ArrayList<>();
        while (this.deferredOnRelationRemoving.size() > 0) {
            try {
                MethodPointer pop = this.deferredOnRelationRemoving.pop();
                pop.invoke();
                if (pop.getArgs().length > 0 && pop.getArgs()[0].getClass().isInstance(relArgs.class)) {
                    relArgs arg = (relArgs) pop.getArgs()[0];
                    if (arg.isCancel() && (arg.getException() != null || arg.getCancelMessage() != null)) {
                        for (String cancelMessage : arg.getCancelMessages()) {
                            ValidationMsg validationMsg = new ValidationMsg(pop.Object(), cancelMessage);
                            result.add(validationMsg);
                        }
                    }
                }
                if (result.size() > 0)
                    break;
            } catch (Exception exception) {
                log.error("deferred On Relationship Removing error", exception);
            }
        }
        this.raiseError(result);
    }

    @Override
    public void commit() throws Exception {
        this.commit(false);
    }

    private void raiseError(Collection<ValidationMsg> result) throws Exception {
        if (result != null && result.size() > 0) {
            throw new Exception(result.stream().map(ValidationMsg::getMessage).filter(c -> !StringUtils.isEmpty(c)).collect(Collectors.joining(StringPool.CRLF)));
        }
    }

    protected void invokePreProcess(boolean isGenerateUids) throws Exception {
        ConcurrentHashSet<ValidationMsg> result = new ConcurrentHashSet<>();
        QueueWrapper queueWrapper = new QueueWrapper(this.finalItems, deferredMethodType.PreProcess);
        if (queueWrapper.getSize() > 0) {
            for (IObjectCollection objectCollection : queueWrapper.getQueues()) {
                Iterator<IObject> e = objectCollection.GetEnumerator();
                while (e.hasNext()) {
                    IObject object = e.next();
                    this.invokePreProcess(object, isGenerateUids, result);
                    if (result.size() > 0)
                        break;
                }
                if (result.size() > 0)
                    break;
            }
            this.remountSubmittingItems(queueWrapper.getProcessedItems());
            if (result.size() == 0) {
                for (IObject finalItem : this.finalItems) {
                    finalItem.ClassBase().SetObjectOBIDs();
                }
            }
            this.raiseError(result);
        }
    }

    protected void remountSubmittingItems(HashSet<IObject> objects) {
        if (CommonUtility.hasValue(objects)) {
            this.finalItems.clear();
            for (IObject object : objects) {
                this.finalItems.offer(object);
            }
        }
    }

    protected Boolean isObjectScopeWised(IObject object) {
        if (object != null) {
            String classDefinitionUid = object.ClassDefinitionUID();
            if (classDefinitionUid.equalsIgnoreCase(classDefinitionType.Rel.toString())) {
                IRel rel = object.toInterface(IRel.class);
                if (rel != null)
                    return CIMContext.Instance.ProcessCache().isDefinitionUnderConfigControl(rel.ClassDefinitionUID1()) | CIMContext.Instance.ProcessCache().isDefinitionUnderConfigControl(rel.ClassDefinitionUID2());
                return false;
            } else {
                return CIMContext.Instance.ProcessCache().isDefinitionUnderConfigControl(classDefinitionUid);
            }
        }
        return null;
    }

    protected void enhanceIObjectConfigurationInfo(IObject object) throws Exception {
        if (object != null) {
            if (this.isObjectScopeWised(object)) {
                String config = object.Config();
                String configurationUid = config.split("<<>>")[0];
                if (this.mobjConfigurationItem != null) {
                    String currentConfig = this.mobjConfigurationItem.generateIObjectConfig();
                    String currentConfigUid = currentConfig.split("<<>>")[0];
                    if (StringUtils.isEmpty(config)) {
                        object.setConfig(currentConfig);
                    } else {
                        if (!currentConfigUid.equalsIgnoreCase(configurationUid)) {
                            throw new Exception("transaction's configuration uid:" + currentConfigUid + ",it is not matched with object's configuration uid:" + configurationUid);
                        }
                    }
                } else
                    throw new Exception("cannot find any project or scope for object");
            }
        }
    }

    protected void invokePreProcess(IObject obj, boolean generateUids, ConcurrentHashSet<ValidationMsg> exceptions) {
        if (obj != null) {
            Stack<MethodPointer> methodPointers = obj.ClassBase().PreProcessMethods();
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(obj.Lock());
                this.enhanceIObjectConfigurationInfo(obj);
                while (methodPointers.size() > 0) {
                    MethodPointer methodPointer = methodPointers.pop();
                    if (methodPointer.getArgs().length > 0) {
                        Object pointerArg = methodPointer.getArgs()[0];
                        if (obj.getClass().isInstance(createArgs.class))
                            ((createArgs) pointerArg).setGenerateUIDs(generateUids);
                    }
                    methodPointer.invoke();
                }
            } catch (Exception exception) {
                exceptions.add(new ValidationMsg(obj, ExceptionUtil.getMessage(exception)));
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(obj.Lock());
            }
        }
    }

    protected void invokeProcessed(IObject obj, boolean generateUids, ConcurrentHashSet<ValidationMsg> exceptions) {
        if (obj != null) {
            Stack<MethodPointer> methodPointers = obj.ClassBase().ProcessedMethods();
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(obj.Lock());
                while (methodPointers.size() > 0) {
                    MethodPointer methodPointer = methodPointers.pop();
                    if (methodPointer.getArgs().length > 0 && methodPointer.getArgs()[0].getClass().isInstance(createArgs.class)) {
                        ((createArgs) methodPointer.getArgs()[0]).setGenerateUIDs(generateUids);
                    }
                    methodPointer.invoke();
                }
            } catch (Exception exception) {
                exceptions.add(new ValidationMsg(obj, exception.getMessage()));
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(obj.Lock());
            }
        }
    }

    protected void invokePostProcessed(IObject obj, boolean generateUids, ConcurrentHashSet<
            ValidationMsg> exceptions) throws Exception {
        if (obj != null) {
            Stack<MethodPointer> methodPointers = obj.ClassBase().PostProcessedMethods();
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(obj.Lock());
                while (methodPointers.size() > 0) {
                    MethodPointer methodPointer = methodPointers.pop();
                    if (methodPointer.getArgs().length > 0 && methodPointer.getArgs()[0].getClass().isInstance(createArgs.class)) {
                        ((createArgs) methodPointer.getArgs()[0]).setGenerateUIDs(generateUids);
                    }
                    methodPointer.invoke();
                }
            } catch (Exception exception) {
                exceptions.add(new ValidationMsg(obj, exception.getMessage()));
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(obj.Lock());
            }
        }
    }

    protected void invokeProcessing(IObject sourceObject, ConcurrentHashSet<ValidationMsg> exceptions) {
        if (sourceObject != null) {
            Stack<MethodPointer> methodPointers = sourceObject.ClassBase().CancelMethods();
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(sourceObject.Lock());
                while (methodPointers.size() > 0) {
                    MethodPointer methodPointer = methodPointers.pop();
                    methodPointer.invoke();
                    if (methodPointer.getArgs().length > 0 && methodPointer.getArgs()[0].getClass().isInstance(cancelArgs.class)) {
                        cancelArgs cancelArgs = (cancelArgs) methodPointer.getArgs()[0];
                        if (cancelArgs.isCancel()) {
                            if (cancelArgs.getCancelExceptions().size() > 0) {
                                for (Exception cancelException : cancelArgs.getCancelExceptions()) {
                                    ValidationMsg validationMsg = new ValidationMsg(sourceObject, cancelException.getMessage());
                                    exceptions.add(validationMsg);
                                }
                            }
                            if (cancelArgs.getCancelMessages().size() > 0) {
                                for (String cancelMessage : cancelArgs.getCancelMessages()) {
                                    ValidationMsg validationMsg = new ValidationMsg(sourceObject, cancelMessage);
                                    exceptions.add(validationMsg);
                                }
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                String message = exception.getMessage();
                // 2023.03.30  HT 异常消息内容为空问题修复
                if (StringUtils.isEmpty(message)) {
                    message = exception.getCause().getMessage();
                }
                exceptions.add(new ValidationMsg(sourceObject, message));
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(sourceObject.Lock());
            }
        }
    }

    protected void invokeProcess(boolean generationUids) throws Exception {
        ConcurrentHashSet<ValidationMsg> result = new ConcurrentHashSet<>();
        QueueWrapper queueWrapper = new QueueWrapper(this.finalItems, deferredMethodType.Process);
        if (queueWrapper.getSize() > 0) {
            for (IObjectCollection objectCollection : queueWrapper.getQueues()) {
                Iterator<IObject> iterator = objectCollection.GetEnumerator();
                while (iterator.hasNext()) {
                    IObject object = iterator.next();
                    this.invokeProcess(object, generationUids, result);
                    if (result.size() > 0)
                        break;
                }
                if (result.size() > 0)
                    break;
            }
            this.remountSubmittingItems(queueWrapper.getProcessedItems());
            if (result.size() == 0) {
                for (IObject item : this.finalItems) {
                    item.ClassBase().SetObjectOBIDs();
                }
            }
            this.raiseError(result);
        }
    }

    protected void invokeProcess(IObject obj, boolean generationUids, ConcurrentHashSet<ValidationMsg> exceptions) {
        if (obj != null) {
            Stack<MethodPointer> methodPointers = obj.ClassBase().ProcessMethods();
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(obj.Lock());
                while (methodPointers.size() > 0) {
                    MethodPointer methodPointer = methodPointers.pop();
                    if (methodPointer.getArgs().length > 0 && methodPointer.getArgs()[0].getClass().isInstance(createArgs.class))
                        ((createArgs) methodPointer.getArgs()[0]).setGenerateUIDs(generationUids);
                    methodPointer.invoke();
                }
            } catch (Exception exception) {
                exceptions.add(new ValidationMsg(obj, ExceptionUtil.getMessage(exception)));
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(obj.Lock());
            }
        }
    }

    protected void invokeProcessing() throws Exception {
        ConcurrentHashSet<ValidationMsg> result = new ConcurrentHashSet<>();
        QueueWrapper queueWrapper = new QueueWrapper(this.finalItems, deferredMethodType.Processing);
        if (queueWrapper.getSize() > 0) {
            for (IObjectCollection objectCollection : queueWrapper.getQueues()) {
                Iterator<IObject> e = objectCollection.GetEnumerator();
                while (e.hasNext()) {
                    IObject iObject = e.next();
                    this.invokeProcessing(iObject, result);
                    if (result.size() > 0)
                        break;
                }
                if (result.size() > 0)
                    break;
            }
            this.remountSubmittingItems(queueWrapper.getProcessedItems());
            if (result.size() == 0) {
                for (IObject item : this.finalItems) {
                    item.ClassBase().SetObjectOBIDs();
                }
            }
            this.raiseError(result);
        }
    }

    private void onPreProcess(IObject iObject) {
        if (iObject != null) {
            boolean flag = false;
            boolean relOrNot = iObject.ClassDefinitionUID().equals(HardCodeHelper.CLASSDEF_REL);
            switch (iObject.ObjectUpdateState()) {
                case none:
                    flag = true;
                    if (relOrNot) {
                        this.mlngRelToBeUpdated++;
                    } else
                        this.mlngToBeUpdated++;
                    break;
                case created:
                    flag = true;
                    if (relOrNot) {
                        this.mlngRelToBeCreated++;
                    } else
                        this.mlngToBeCreated++;
                    break;
                case deleted:
                    flag = true;
                    if (relOrNot)
                        this.mlngRelToBeDeleted++;
                    else
                        this.mlngToBeDeleted++;
                    break;
                case terminated:
                    flag = true;
                    if (relOrNot)
                        this.mlngRelToBeTerminated++;
                    else
                        this.mlngToBeTerminated++;
                    break;
            }
            if (flag) {
                this.finalItems.offer(iObject);
                if (relOrNot) {
                    this.mlngRelProcessed++;
                } else
                    this.mlngToProcessed++;
                this.mlngTotalProcessed++;
            }
        }
    }

    @Override
    public void add(IObject object) {
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            if (object != null) {
                if (object.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
                    this.doAddRel(object.toInterface(IRel.class));
                else {
                    this.objects.remove(object.OBID());
                    this.objects.put(object.OBID(), object);
                }
            }
        } catch (Exception exception) {
            log.error("add object into transaction failed", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
    }

    @Override
    public void addRange(IObjectCollection objectCollection) {
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            if (objectCollection != null && objectCollection.hasValue()) {
                Iterator<IObject> iterator = objectCollection.GetEnumerator();
                while (iterator.hasNext()) {
                    IObject next = iterator.next();
                    if (next.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
                        this.doAddRel(next.toInterface(IRel.class));
                    else {
                        if (!this.objects.containsKey(next.OBID()))
                            this.objects.remove(next.OBID());
                        this.objects.put(next.OBID(), next);
                    }
                }
            }
        } catch (Exception exception) {
            log.error("add range with object collection", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
    }

    @Override
    public void addDeferredOnRelationshipRemoving(IObject object, String interfaceDefUid, String methodDef, Object[]
            args) {
        MethodPointer methodPointer = new MethodPointer(object, interfaceDefUid, methodDef, args);
        this.deferredOnRelationRemoving.push(methodPointer);
    }

    @Override
    public ConcurrentHashMap<String, HashSet<IObject>> CreatesAndUpdatesByUniqueKey() {
        return this.marrCreatesAndUpdatesByUniqueKey;
    }

    @Override
    public ConcurrentHashSet<IObject> TerminatedItemsToSerialize() {
        return this.mcolTerminatedItemsToSerialize;
    }

    @Override
    public ConcurrentHashSet<IObject> DeletedItemsToSerialize() {
        return this.mcolDeletedItemsToSerialize;
    }

    @Override
    public ConcurrentHashSet<IObject> NewItemsToSerialize() {
        return this.mcolNewItemsToSerialize;
    }

    @Override
    public ConcurrentHashSet<IObject> RefreshItemsToSerialize() {
        return this.mcolRefreshItemsToSerialize;
    }

    @Override
    public ConcurrentHashMap<String, IObject> UnEditedItems() {
        return this.marrUnEditedItems;
    }

    @Override
    public boolean isENSActivated() {
        return this.mblnUseENS;
    }

    @Override
    public void rollBack() {
        StopWatch stopWatch = PerformanceUtility.start();
        if (this.finalItems.size() > 0) {
            IObjectCollection itemsToBeRemovedFromCache = new ObjectCollection();
            IObjectCollection itemsToBeRefreshUnderCache = new ObjectCollection();
            for (IObject item : this.finalItems) {
                try {
                    item.rollback();
                    objectUpdateState updateState = item.ClassBase().UpdateState();
                    if (updateState == objectUpdateState.deleted) {
                        itemsToBeRemovedFromCache.append(item);
                    } else {
                        itemsToBeRefreshUnderCache.append(item);
                    }
                } catch (Exception exception) {
                    log.error("roll back failed for " + item.toErrorPop(), exception);
                }
            }
            if (itemsToBeRemovedFromCache.hasValue())
                CIMContext.Instance.ProcessCache().clear(itemsToBeRemovedFromCache);
            if (itemsToBeRefreshUnderCache.hasValue())
                CIMContext.Instance.ProcessCache().refresh(itemsToBeRefreshUnderCache);
        }
        this.reset();
        log.trace("application transaction roll back completed" + PerformanceUtility.stop(stopWatch));
    }

    @Override
    public void setENSActivated(boolean ensActivated) {
        this.mblnUseENS = ensActivated;
    }

    @Override
    public boolean hasOBIDInDeletedOrTerminatedItems(String obid) {
        if (!StringUtils.isEmpty(obid)) {
            return this.mcolDeletedItemsToSerialize.stream().anyMatch(c -> c.OBID().equalsIgnoreCase(obid)) || this.mcolTerminatedItemsToSerialize.stream().anyMatch(c -> c.OBID().equalsIgnoreCase(obid));
        }
        return false;
    }

    @Override
    public IObject getByObid(String obid) {
        if (!StringUtils.isEmpty(obid)) {
            if (this.objects.containsKey(obid))
                return this.objects.get(obid);
            else if (this.relationships.containsKey(obid))
                return this.relationships.get(obid);
        }
        return null;
    }

    protected void doAddRel(IRel rel) throws Exception {
        if (rel != null) {
            String uniqueKey = rel.generateUniqueKey();
            if (!this.relationships.containsKey(uniqueKey)) {
                this.relationships.remove(uniqueKey);
            }
            this.relationships.put(uniqueKey, rel);
        }
    }


    protected void addRel(IRel rel) {
        if (rel != null) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                this.doAddRel(rel);
            } catch (Exception exception) {
                log.error("add rel failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    protected void addRelRange(IRelCollection relCollection) {
        if (relCollection != null && relCollection.hasValue()) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                Iterator<IObject> iterator = relCollection.GetEnumerator();
                while (iterator.hasNext()) {
                    this.doAddRel(iterator.next().toInterface(IRel.class));
                }
            } catch (Exception exception) {
                log.error("add rel range with rel collection", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }
}
