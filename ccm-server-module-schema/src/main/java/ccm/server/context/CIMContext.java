package ccm.server.context;

import ccm.server.activators.SchemaActivator;
import ccm.server.agents.ITablePrefixAgent;
import ccm.server.agents.impl.TransactionAgent;
import ccm.server.business.IDbManagementBusinessService;
import ccm.server.cache.IProcessCache;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.engine.IGraphExpansionEngine;
import ccm.server.engine.IKeyGeneration;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.*;
import ccm.server.models.db.DbWrapper;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.shared.ISharedLocalService;
import ccm.server.transactions.ITransaction;
import ccm.server.util.CommonUtility;
import ccm.server.util.DbUtility;
import ccm.server.util.ReentrantLockUtility;
import ccm.server.utils.IObjectConversion;
import ccm.server.utils.SchemaUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service("cIMContext")
@Slf4j
public class CIMContext {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private SchemaActivator schemaActivator;
    private final IObjectConversion objectConversion = new IObjectConversion();
    @Autowired
    private IDbManagementBusinessService dbManagementBusinessService;
    @Autowired
    IProcessCache cache;
    @Autowired
    private IKeyGeneration keyGeneration;
    @Autowired
    private ISharedLocalService sharedLocalService;
    @Autowired
    private TransactionAgent transactionAgent;
    @Autowired
    private IQueryEngine queryEngine;
    @Autowired
    private ITablePrefixAgent tableNameSwitchEngine;
    @Autowired
    private IGraphExpansionEngine graphExpansionEngine;

    public List<IDomain> getDefaultDomains() {
        List<IDomain> result = new ArrayList<>();
        IObject schemaDomain = this.cache.item(domainInfo.SCHEMA.toString(), null, false);
        if (schemaDomain != null)
            result.add(schemaDomain.toInterface(IDomain.class));
        IObject adminDomain = this.cache.item(domainInfo.ADMIN.toString(), null, false);
        if (adminDomain != null)
            result.add(adminDomain.toInterface(IDomain.class));
        IObject dataDomain = this.cache.item(domainInfo.DATA.toString(), null, false);
        if (dataDomain != null)
            result.add(dataDomain.toInterface(IDomain.class));
        return result;
    }


    public ICIMUser getLoginUser(String userName) throws Exception {
        if (StringUtils.isEmpty(userName))
            userName = this.getLoginUserName();

        if (StringUtils.isEmpty(userName))
            throw new Exception("cannot identify login user name info as it is NULL or empty");

        IObject currentUser = null;
        String userNameUid = this.parseUserUid(userName);
        try {
            currentUser = this.cache.item(userNameUid, null, false);
        } catch (Exception e) {
            log.trace("get login user failed", e);
        }
        if (currentUser == null) {
            log.trace("get login user failed and try to create it now", new Exception("no user object found in system with Uid->" + userName));
        } else
            return currentUser.toInterface(ICIMUser.class);
        return null;
    }

    public boolean dropLoginUser(String loginUserName) throws Exception {
        if (StringUtils.isEmpty(loginUserName))
            loginUserName = CIMContext.Instance.getLoginUserName();
        if (!StringUtils.isEmpty(loginUserName)) {
            String userName = CIMContext.Instance.getLoginUserName();
            if (!StringUtils.isEmpty(userName)) {
                if (loginUserName.equalsIgnoreCase(userName))
                    throw new Exception("current user " + loginUserName + " now is connecting, you cannot drop it");
            }
            loginUserName = this.parseUserUid(loginUserName);
            IObject iObject = CIMContext.Instance.ProcessCache().item(loginUserName, domainInfo.ADMIN.toString(), false);
            if (iObject != null) {
                try {
                    CIMContext.Instance.Transaction().start();
                    iObject.Delete();
                    CIMContext.Instance.Transaction().commit();
                    CIMContext.Instance.ProcessCache().remove(iObject);
                    return true;
                } catch (Exception e) {
                    log.error("drop user failed", e);
                    CIMContext.Instance.Transaction().rollBack();
                }
            } else
                log.warn("no user found in database with " + loginUserName + ", drop progress will be terminated");
        }
        return false;
    }

    public ICIMUser getUserWithDefaultSettings(String userName) throws Exception {
        if (!StringUtils.isEmpty(userName))
            userName = CIMContext.Instance.getLoginUserName();
        String userNameUid = this.parseUserUid(userName);
        ICIMUser icimUser = null;
        IObject currentUser = CIMContext.Instance.getLoginUser(userNameUid);
        if (currentUser == null) {
            currentUser = this.createLoginUser(userName);
            if (currentUser != null) {
                icimUser = currentUser.toInterface(ICIMUser.class);
            }
        }
        if (icimUser != null) {

        } else
            throw new Exception("no user with " + userName + " found in database");
        return icimUser;
    }

    public IObject createConfigurationItem(ObjectDTO objectDTO) throws Exception {
        if (objectDTO != null) {
            String obid = objectDTO.getObid();
            String uid = objectDTO.getUid();
            if (StringUtils.isEmpty(uid))
                uid = "PL_" + objectDTO.getName();

            IObject existObject = null;
            if (!StringUtils.isEmpty(obid)) {
                existObject = CIMContext.Instance.ProcessCache().getObjectByOBIDCache(obid);
                if (existObject != null) {
                    throw new Exception("provided configuration with name==" + objectDTO.getName() + "== obid==" + obid + "== has already exist in database");
                }
            }
            existObject = CIMContext.Instance.ProcessCache().item(uid, null, false);
            if (existObject != null) {
                throw new Exception("provided configuration with name==" + objectDTO.getName() + "== uid==" + uid + "== has already exist in database");
            }
            boolean flag = false;
            IObject configurationItem = null;
            try {
                CIMContext.Instance.Transaction().start();
                configurationItem = SchemaUtility.newIObject(classDefinitionType.CIMPlant.toString(), objectDTO.getName(), objectDTO.getDescription(), null, uid);
                if (configurationItem != null) {
                    ICIMConfigurationItem configurationItem1 = (ICIMConfigurationItem) configurationItem.Interfaces().item(ICIMConfigurationItem.class.getSimpleName(), true);
                    if (configurationItem1 == null)
                        throw new Exception("invalid configuration item during creation progress as it is NULL");
                    ICIMPlant plant = (ICIMPlant) configurationItem1.Interfaces().item(ICIMPlant.class.getSimpleName(), true);
                    if (plant == null)
                        throw new Exception("invalid plant item during creation progress as it is NULL");
                    List<ObjectItemDTO> items = objectDTO.getItems();
                    if (items != null && items.size() > 0) {
                        List<String> propertiesAlreadySet = new ArrayList<String>() {{
                            this.add(propertyDefinitionType.Name.toString());
                            this.add(propertyDefinitionType.Description.toString());
                            this.add(propertyDefinitionType.UID.toString());
                            this.add(propertyDefinitionType.DomainUID.toString());
                        }};
                        for (ObjectItemDTO item : items) {
                            if (item.getDefType() == classDefinitionType.PropertyDef && !propertiesAlreadySet.contains(item.getDefUID())) {
//                                String displayValue = configurationItem.getDisplayValue(item.getDefUID());
//                                if (StringUtils.isEmpty(displayValue))
                                configurationItem.setValue(item.getDefUID(), item.getDisplayValue());
                            }
                        }
                    }
                    configurationItem.ClassDefinition().FinishCreate(configurationItem);
                }
                CIMContext.Instance.Transaction().commit();
                flag = true;
                return configurationItem;
            } catch (Exception e) {
                log.error("crete configuration failed", e);
                CIMContext.Instance.Transaction().rollBack();
            } finally {
                if (flag && configurationItem != null) {
                    CIMContext.Instance.ProcessCache().refresh(configurationItem);
                }
            }
        }
        return null;
    }

    public boolean deleteConfigurationItem(String obid) {
        if (!StringUtils.isEmpty(obid)) {
            IObject object = null;
            if (!StringUtils.isEmpty(obid))
                object = this.cache.getItemByOBIDAndClassDefinition(obid, classDefinitionType.CIMPlant.toString());
            if (object != null) {
                try {
                    CIMContext.Instance.Transaction().start();
                    object.Delete();
                    CIMContext.Instance.Transaction().commit();
                    return true;
                } catch (Exception e) {
                    log.error(e.getMessage());
                    CIMContext.Instance.Transaction().rollBack();
                }
            }
        }
        return false;
    }

    public boolean deleteConfigurationItem(ObjectDTO objectDTO) {
        if (objectDTO != null) {
            String obid = objectDTO.getObid();
            return this.deleteConfigurationItem(obid);
        }
        return false;
    }

    public IObject updateConfigurationItem(ObjectDTO objectDTO) throws Exception {
        if (objectDTO != null) {
            String obid = objectDTO.getObid();
            String uid = objectDTO.getUid();
            String name = objectDTO.getName();
            if (StringUtils.isEmpty(uid) && !StringUtils.isEmpty(name)) {
                uid = "PL_" + name;
            }
            if (StringUtils.isEmpty(obid) && StringUtils.isEmpty(uid))
                throw new Exception("cannot update specified configuration item as no required field(s) such as uid or obid provided");

            IObject existItem = null;
            if (!StringUtils.isEmpty(obid))
                existItem = this.cache.getObjectByOBIDCache(obid);
            if (existItem == null && !StringUtils.isEmpty(uid))
                existItem = this.cache.item(uid, null, false);
            if (existItem != null) {
                try {
                    List<ObjectItemDTO> items = objectDTO.getItems();
                    if (CommonUtility.hasValue(items)) {
                        boolean flag = false;
                        List<String> propertiesThatCannotBeUpdated = existItem.toInterface(ICIMConfigurationItem.class).getPropertiesThatCannotBeUpdated();
                        for (ObjectItemDTO item : items) {
                            if (item.getDefType() == classDefinitionType.PropertyDef) {
                                if (propertiesThatCannotBeUpdated.stream().anyMatch(c -> c.equalsIgnoreCase(item.getDefUID())))
                                    continue;
                                if (!flag) {
                                    SchemaUtility.beginTransaction();
                                    existItem.BeginUpdate();
                                    flag = true;
                                }
                                existItem.setValue(item.getDefUID(), item.getDisplayValue());
                            }
                        }
                        if (flag) {
                            existItem.FinishUpdate();
                            SchemaUtility.commitTransaction();
                        }
                    }
                } catch (Exception e) {
                    if (CIMContext.Instance.Transaction().inTransaction())
                        CIMContext.Instance.Transaction().rollBack();
                    ReentrantLockUtility.tryToUnlockWriteLock(existItem.Lock());
                    throw e;
                }
            }
            return existItem;
        }
        return null;
    }

    public void cleanScope(String loginUserName) throws Exception {
        ICIMUser loginUser = this.getLoginUser(loginUserName);
        if (loginUser != null) {
            loginUser.clearScope();
        }
    }

    public ICIMUser createLoginUser(String loginUserName) throws Exception {
        if (StringUtils.isEmpty(loginUserName))
            loginUserName = CIMContext.Instance.getLoginUserName();

        if (StringUtils.isEmpty(loginUserName))
            throw new Exception("invalid login user as it is NULL");

        Exception exception = null;

        ICIMUser loginUser = CIMContext.Instance.getLoginUser(loginUserName);
        if (loginUser == null) {
            try {
                CIMContext.Instance.Transaction().start();
                IObject lobjUser = SchemaUtility.newIObject(classDefinitionType.CIMUser.toString(), loginUserName, "auto-generated user", null, PREFIX_USER_UID + "_" + loginUserName);
                if (lobjUser != null) {
                    loginUser = (ICIMUser) lobjUser.Interfaces().item(ICIMUser.class.getSimpleName(), true);
                    lobjUser.ClassDefinition().FinishCreate(lobjUser);
                }
                CIMContext.Instance.Transaction().commit();
            } catch (Exception e) {
                exception = e;
                CIMContext.Instance.Transaction().rollBack();
                loginUser = null;
            }
        } else{
            log.trace(loginUserName + "'s user has been exist, no need to create it again");

        }


        if (loginUser == null)
            throw new Exception("create login user failed", exception);

        return loginUser;
    }

    public final static String PREFIX_USER_UID = "USR";

    protected String parseUserUid(String userName) {
        if (!StringUtils.isEmpty(userName)) {
            if (userName.toUpperCase().startsWith(PREFIX_USER_UID))
                return userName;
            else {
                return PREFIX_USER_UID + "_" + userName;
            }
        }
        return "";
    }

    public IObjectCollection getConfigurationItems() {
        return this.cache.getObjectsByClassDefCache(classDefinitionType.CIMPlant.toString());
    }

    public String getMyConfigurationItemTablePrefix() throws Exception {
        String userName = CIMContext.Instance.getLoginUserName();
        if (!StringUtils.isEmpty(userName)) {
            userName = this.parseUserUid(userName);
            ICIMConfigurationItem result = this.getMyConfigurationItem(userName);
            if (result != null) {
                String tablePrefix = result.TablePrefix();
                if (!StringUtils.isEmpty(tablePrefix))
                    return tablePrefix;
            }
        }
        return "";
    }

    public ICIMConfigurationItem getMyConfigurationItem(String userName) throws Exception {
        if (StringUtils.isEmpty(userName))
            userName = CIMContext.Instance.getLoginUserName();

        if (!StringUtils.isEmpty(userName)) {
            userName = this.parseUserUid(userName);
            IObject user = this.cache.item(userName, null, false);
            if (user == null)
                log.trace(userName + " login user as it is not exist in database");
            else {
                ICIMConfigurationItem result = user.toInterface(ICIMUser.class).getCreateConfig(true);
                if (result != null)
                    return result;
                else
                    log.trace("cannot get create scope for specified login user " + userName);
            }
        } else
            throw new Exception("invalid login user info as it is NULL");
        return null;
    }

    public void dropTables(ICIMConfigurationItem configurationItem) throws Exception {
        if (configurationItem != null) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                IObjectCollection domains = this.cache.getObjectsByClassDefCache(classDefinitionType.Domain.toString());
                List<String> tables = this.getDomainAndConfigurationTables(configurationItem, domains);
                if (CommonUtility.hasValue(tables)) {
                    this.dropTables(tables);
                }
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    public void dropTables(List<String> tables) throws Exception {
        if (CommonUtility.hasValue(tables)) {
            DbWrapper dbWrapper = this.dbUtility.getDefaultDbWrapper();
            List<Exception> exceptions = new ArrayList<>();
            List<tableSuffix> lcolSuffix = tableSuffix.getTables();
            for (String table : tables) {
                for (tableSuffix suffix : lcolSuffix) {
                    String tableName = table + suffix.toString();
                    try {
                        if (!this.dbManagementBusinessService.tableExistOrNot(dbWrapper, tableName)) {
                            boolean flag = this.dbManagementBusinessService.dropTable(dbWrapper, tableName);
                            log.info("drop table for " + tableName + " and result is ===" + flag + "=====");
                        }
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                }
            }
            if (exceptions.size() > 0) {
                throw new Exception("table progress failed and details info:" + exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining("/r/n")));
            }
        }
    }

    @Autowired
    private DbUtility dbUtility;

    public void ensureTables(List<String> tables) throws Exception {
        if (tables != null && tables.size() > 0) {
            DbWrapper dbWrapper = this.dbUtility.getDefaultDbWrapper();
            List<Exception> exceptions = new ArrayList<>();
            List<tableSuffix> lcolSuffix = tableSuffix.getTables();
            for (String table : tables) {
                for (tableSuffix suffix : lcolSuffix) {
                    String tableName = table + suffix.toString();
                    try {
                        if (!this.dbManagementBusinessService.tableExistOrNot(dbWrapper, tableName)) {
                            boolean flag = this.dbManagementBusinessService.createTable(dbWrapper, tableName);
                            log.info("create table for " + tableName + " and result is ===" + flag + "=====");
                        }
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                }
            }
            if (exceptions.size() > 0) {
                throw new Exception("table progress failed and details info:" + exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining("/r/n")));
            }
        }
    }

    public void ensureTables(IDomain domain) throws Exception {
        if (domain != null) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                String tablePrefix = domain.TablePrefix();
                if (!StringUtils.isEmpty(tablePrefix)) {
                    List<String> tables = new ArrayList<>();
                    if (domain.ScopeWiseInd()) {
                        IObjectCollection configurationItems = this.cache.getObjectsByClassDefCache(classDefinitionType.Domain.toString());
                        if (configurationItems != null && configurationItems.hasValue()) {
                            List<ICIMConfigurationItem> configurationItems1 = configurationItems.toList(ICIMConfigurationItem.class);
                            for (ICIMConfigurationItem configurationItem : configurationItems1) {
                                String tablePrefix1 = configurationItem.TablePrefix();
                                if (!StringUtils.isEmpty(tablePrefix1)) {
                                    tables.add(tablePrefix1 + tablePrefix);
                                }
                            }
                        }
                    } else {
                        tables.add(tablePrefix);
                    }
                    if (CommonUtility.hasValue(tables)) {
                        this.ensureTables(tables);
                    }
                }
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    public void ensureAllTablesForConfigurationItemsAndDomains() throws Exception {
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            IObjectCollection domains = this.cache.getObjectsByClassDefCache(classDefinitionType.Domain.toString());
            if (domains != null && domains.hasValue()) {
                List<IDomain> domainList = domains.toList(IDomain.class);
                List<String> singleTables = new ArrayList<>();
                List<String> scopedTables = new ArrayList<>();
                for (IDomain domain : domainList) {
                    if (domain != null) {
                        String tablePrefix = domain.TablePrefix();
                        if (!StringUtils.isEmpty(tablePrefix)) {
                            if (domain.ScopeWiseInd()) {
                                scopedTables.add(tablePrefix);
                            } else {
                                singleTables.add(tablePrefix);
                            }
                        }
                    }
                }
                List<String> configurationTables = new ArrayList<>();
                if (CommonUtility.hasValue(scopedTables)) {
                    IObjectCollection configurationItems = this.cache.getObjectsByClassDefCache(classDefinitionType.CIMPlant.toString());
                    if (configurationItems != null && configurationItems.hasValue()) {
                        List<ICIMConfigurationItem> configurationItems1 = configurationItems.toList(ICIMConfigurationItem.class);
                        for (ICIMConfigurationItem configurationItem : configurationItems1) {
                            String tablePrefix = configurationItem.TablePrefix();
                            if (!StringUtils.isEmpty(tablePrefix)) {
                                for (String table : scopedTables) {
                                    configurationTables.add(tablePrefix + table);
                                }
                            }
                        }
                    }
                }
                if (CommonUtility.hasValue(singleTables))
                    CIMContext.Instance.ensureTables(singleTables);
                if (CommonUtility.hasValue(configurationTables))
                    CIMContext.Instance.ensureTables(configurationTables);
            }
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }

    }

    protected List<String> getDomainAndConfigurationTables(IObject configurationItem, IObjectCollection domains) {
        List<String> result = new ArrayList<>();
        if (configurationItem != null) {
            List<String> domainTables = null;
            List<String> configurationTables = new ArrayList<>();
            ICIMConfigurationItem configurationItem1 = configurationItem.toInterface(ICIMConfigurationItem.class);
            if (configurationItem1 != null && !StringUtils.isEmpty(configurationItem1.TablePrefix())) {
                if (domains != null && domains.hasValue()) {
                    List<String> tempList = new ArrayList<>();
                    Set<String> uniqueValues = new HashSet<>();
                    for (IDomain iDomain : domains.toList(IDomain.class)) {
                        if (iDomain.ScopeWiseInd()) {
                            String str = iDomain.TablePrefix();
                            if (!StringUtils.isEmpty(str)) {
                                if (uniqueValues.add(str)) {
                                    tempList.add(str);
                                }
                            }
                        }
                    }
                    uniqueValues = null;
                    domainTables = tempList;
                }
                configurationTables.add(configurationItem1.TablePrefix());
                if (domainTables != null && domainTables.size() > 0) {
                    for (String configurationTable : configurationTables) {
                        for (String domainTable : domainTables) {
                            String currentTable = configurationTable + domainTable;
                            result.add(currentTable);
                        }
                    }
                }
            }
        }
        return result;
    }

    public void ensureTables(ICIMConfigurationItem configurationItem) throws Exception {
        if (configurationItem != null) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                IObjectCollection domains = this.cache.getObjectsByClassDefCache(classDefinitionType.Domain.toString());
                List<String> tables = this.getDomainAndConfigurationTables(configurationItem, domains);
                if (CommonUtility.hasValue(tables)) {
                    this.ensureTables(tables);
                }
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    public void changeScope(String userName, IObject configurationItem) throws Exception {
        if (configurationItem != null) {
            IObjectCollection lcolQueryConfig = new ObjectCollection() {{
                this.append(configurationItem);
            }};
            ICIMUser loginUser = CIMContext.Instance.createLoginUser(userName);
            if (loginUser != null) {
                loginUser.saveScope(configurationItem, lcolQueryConfig);
                this.ensureTables(configurationItem.toInterface(ICIMConfigurationItem.class));
                this.cache.doAfterChangeScope();
            }
        }
    }


    public List<String> getDomainsByRelDefs(List<String> relDefs, relCollectionTypes collectionTypes) throws Exception {
        if (CommonUtility.hasValue(relDefs)) {
            for (String relDef : relDefs) {
                IObject item = CIMContext.Instance.ProcessCache().item(relDef, domainInfo.SCHEMA.toString(), false);
                if (item == null)
                    throw new Exception("invalid relationship definition with " + relDef + " as it is not defined in database");

                return this.getDomainsTablePrefixByRelDef(item.toInterface(IRelDef.class), collectionTypes);
            }
        }
        return null;
    }

    public List<String> getDomainsByClassDefs(List<String> classDefinitionUIDs) throws Exception {
        if (CommonUtility.hasValue(classDefinitionUIDs)) {
            List<String> usedDomains = new ArrayList<>();
            for (String classDefinitionUID : classDefinitionUIDs) {
                IClassDef classDef = CIMContext.Instance.ProcessCache().item(classDefinitionUID, domainInfo.SCHEMA.toString(), false).toInterface(IClassDef.class);
                List<String> domains = classDef.getUsedDomain();
                if (CommonUtility.hasValue(domains))
                    usedDomains.addAll(domains);
            }
            return usedDomains;
        }
        return null;
    }

    public List<String> getDomainsTablePrefixByRelDef(String relDef, relCollectionTypes collectionTypes) throws Exception {
        IObject item = CIMContext.Instance.ProcessCache().item(relDef, domainInfo.SCHEMA.toString());
        if (item == null)
            throw new Exception("invalid relationship definition with " + relDef + " as it is not defined in database");
        return item.toInterface(IRelDef.class).getUsedDomainsTablePrefix(collectionTypes);
    }


    public List<String> getDomainsTablePrefixByRelDef(IRelDef relDefinition, relCollectionTypes collectionTypes) throws Exception {
        if (relDefinition != null) {
            return relDefinition.getUsedDomainsTablePrefix(collectionTypes);
        }
        return null;
    }

    public List<IDomain> getDomainsByRelDef(IRelDef relDefinition, relCollectionTypes collectionTypes) throws Exception {
        if (relDefinition != null) {
            return relDefinition.getUsedDomains(collectionTypes);
        }
        return null;
    }

    private ConcurrentHashMap<String, IObjectCollection> mcolIDefRelDefs;

    public ConcurrentHashMap<String, IObjectCollection> IDefRelDefs() {
        if (this.mcolIDefRelDefs == null)
            this.mcolIDefRelDefs = new ConcurrentHashMap<>();
        return this.mcolIDefRelDefs;
    }

    public IObjectCollection getIDefRelDefsWithDirection(String interfaceDefinitionUID, relDirection direction) throws Exception {
        IObjectCollection result = new ObjectCollection();
        if (!StringUtils.isEmpty(interfaceDefinitionUID)) {
            String key = interfaceDefinitionUID + "~" + direction.toString();
            if (this.IDefRelDefs().containsKey(key)) {
                result = this.IDefRelDefs().get(key);
            } else {
                IObjectCollection iDefRelDefs = this.ProcessCache().getIDefRelDefs(interfaceDefinitionUID, direction);
                if (iDefRelDefs != null && iDefRelDefs.hasValue()) {
                    result = iDefRelDefs;
                    if (this.IDefRelDefs().containsKey(key))
                        this.IDefRelDefs().replace(key, result);
                    else
                        this.IDefRelDefs().put(key, result);
                }

            }
        }
        return result;
    }

    public String getConfigTablePrefix(String configUID) throws Exception {
        if (!StringUtils.isEmpty(configUID)) {
            IObject item = this.ProcessCache().item(configUID, null, false);
            if (item != null) {
                String prefix = item.toInterface(ICIMPlant.class).TablePrefix();
                if (prefix != null)
                    return prefix;
            }
        }
        return "";
    }

    public ITransaction Transaction() {
        return this.transactionAgent.currentTransaction();
    }

    public String getLoginUserName() {
        return this.sharedLocalService.getCurrentLoginUser();
    }

    public boolean initialized() {
        return this.cache.initialized();
    }

    public IProcessCache ProcessCache() {
        return cache;
    }

    public ITablePrefixAgent SwitchEngine() {
        return this.tableNameSwitchEngine;
    }

    public IQueryEngine QueryEngine() {
        return this.queryEngine;
    }

    public IGraphExpansionEngine GraphExpansion() {
        return this.graphExpansionEngine;
    }

    public static CIMContext Instance;

    private boolean logTransactionItem;

    public boolean LogTransactionItem() {
        return this.logTransactionItem;
    }

    public void setLogTransactionItem(boolean logTransactionItem) {
        this.logTransactionItem = logTransactionItem;
    }

    @PostConstruct
    public void doInit() {
        log.info("start to do init for context amount");
        Instance = this;
        Instance.keyGeneration = this.keyGeneration;
        Instance.transactionAgent = this.transactionAgent;
        Instance.queryEngine = this.queryEngine;
        Instance.graphExpansionEngine = this.graphExpansionEngine;
        Instance.tableNameSwitchEngine = this.tableNameSwitchEngine;
        log.info("amount finish and cache initialized:" + this.initialized());
    }

    public SchemaActivator getSchemaActivator() {
        if (this.schemaActivator == null)
            this.schemaActivator = new SchemaActivator();
        return this.schemaActivator;
    }

    public IObjectConversion getObjectConversion() {
        return this.objectConversion;
    }

    public String generateOBIDForProperty() {
        return this.keyGeneration.getNextOBID(OBIDSequenceTypes.PropertyOBIDs);
    }

    public String generateOBIDForRel() {
        return this.keyGeneration.getNextOBID(OBIDSequenceTypes.RelOBIDs);
    }

    public String generateOBIDForInterface() {
        return this.keyGeneration.getNextOBID(OBIDSequenceTypes.InterfaceOBIDs);
    }

    public String generateOBIDForObject() {
        return this.keyGeneration.getNextOBID(OBIDSequenceTypes.ObjectOBIDs);
    }
}
