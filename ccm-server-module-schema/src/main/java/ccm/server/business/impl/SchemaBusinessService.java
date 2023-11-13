package ccm.server.business.impl;

import ccm.server.business.IDataProviderService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.cache.IProcessCache;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.base.ObjectXmlDTO;
import ccm.server.dto.base.OptionItemDTO;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.*;
import ccm.server.excel.util.ExcelUtility;
import ccm.server.executors.PropertySummaryExecutor;
import ccm.server.model.*;
import ccm.server.models.query.QueryRequest;
import ccm.server.module.impl.general.InternalServiceImpl;
import ccm.server.params.PageRequest;
import ccm.server.processors.ThreadsProcessor;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.interfaces.defaults.ICIMLoaderDefault;
import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.relEndObj;
import ccm.server.util.CollectionUtility;
import ccm.server.util.CommonUtility;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.dom.DOMDocument;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
@Service("schemaBusinessService")
public class SchemaBusinessService extends InternalServiceImpl implements ISchemaBusinessService {
    @Autowired
    public IProcessCache processCache;
    @Autowired
    private IDataProviderService dataProviderService;
    @Autowired
    private ThreadsProcessor threadsProcessor;
    public static ISchemaBusinessService Instance;


    @PostConstruct
    public void doInit() {
        Instance = this;
    }

    @Override
    public void reloadCache() throws Exception {
        this.processCache.reInitialize();
    }

    @Override
    public IClassDef createClassDef(String name, String description, String systemIDPattern, String uniqueKeyDef, boolean isConfigControlled) throws Exception {
        if (StringUtils.isEmpty(name))
            throw new Exception("invalid name as it is NULL");

        CIMContext.Instance.Transaction().start();
        IObject iObject = SchemaUtility.newIObject(classDefinitionType.ClassDef.toString(), name, description, domainInfo.SCHEMA.toString(), name);
        IClassDef classDef = null;
        if (iObject != null) {
            classDef = iObject.toInterface(IClassDef.class);
            //            classDef.setControlledByConfig(isConfigControlled);
            classDef.setUniqueKeyPattern(uniqueKeyDef);
            classDef.setSystemIDPattern(systemIDPattern);
            iObject.ClassDefinition().FinishCreate(iObject);
        }
        CIMContext.Instance.Transaction().commit();
        return classDef;
    }

    @Override
    public IInterfaceDef createInterfaceDef(String name, String description, String displayAs) throws Exception {
        if (StringUtils.isEmpty(name))
            throw new Exception("invalid name as it is NULL");

        IInterfaceDef result = null;
        CIMContext.Instance.Transaction().start();
        IObject iObject = SchemaUtility.newIObject(classDefinitionType.InterfaceDef.toString(), name, description, domainInfo.SCHEMA.toString(), name);

        if (iObject != null) {
            IInterface anInterface = iObject.Interfaces().item(IInterfaceDef.class.getSimpleName(), true);
            if (anInterface == null)
                throw new Exception("create interface definition failed as missing required interface instance");
            result = (IInterfaceDef) anInterface;
            ISchemaObject schemaObject = (ISchemaObject) iObject.Interfaces().item(ISchemaObject.class.getSimpleName(), true);
            schemaObject.setDisplayName(displayAs);
            schemaObject.setByCustom(false);
            schemaObject.setSchemaRev(1);
            schemaObject.setSchemaVer(1);
            iObject.ClassDefinition().FinishCreate(iObject);
        }
        CIMContext.Instance.Transaction().commit();
        return result;
    }

    @Override
    public IPropertyDef createPropertyDef(String name, String description, String exposedInterfaceDef, String scopedByPropertyType) throws Exception {
        if (StringUtils.isEmpty(name))
            throw new Exception("invalid name info as it is NULL");
        if (StringUtils.isEmpty(exposedInterfaceDef))
            throw new Exception("invalid interface definition for property to be exposed");
        if (StringUtils.isEmpty(scopedByPropertyType)) {
            log.warn("property type is not provided, and use StringType as default to set");
            scopedByPropertyType = propertyValueType.StringType.toString();
        }
        IPropertyDef result = null;
        CIMContext.Instance.Transaction().start();
        IObject iObject = SchemaUtility.newIObject(classDefinitionType.PropertyDef.toString(), name, description, domainInfo.SCHEMA.toString(), name);
        if (iObject != null) {
            ISchemaObject schemaObject = (ISchemaObject) iObject.Interfaces().item(ISchemaObject.class.getSimpleName(), true);
            schemaObject.setSchemaVer(1);
            schemaObject.setSchemaRev(1);
            schemaObject.setByCustom(false);
            schemaObject.setDisplayName(description);
            iObject.ClassDefinition().FinishCreate(iObject);

            result = (IPropertyDef) iObject.Interfaces().item(IPropertyDef.class.getSimpleName(), true);
            IObject interfaceDef = CIMContext.Instance.ProcessCache().item(exposedInterfaceDef, domainInfo.SCHEMA.toString());
            if (interfaceDef == null)
                throw new Exception("invalid interface definition exist in database");

            IRel rel = SchemaUtility.newRelationship(relDefinitionType.exposes.toString(), interfaceDef, iObject, true);
            rel.ClassDefinition().FinishCreate(rel);

            IObject scopedBy = CIMContext.Instance.ProcessCache().item(scopedByPropertyType, domainInfo.SCHEMA.toString());
            if (scopedBy == null)
                throw new Exception("invalid property type with " + scopedByPropertyType + " as it was not defined in system");

            IRel rel1 = SchemaUtility.newRelationship(relDefinitionType.scopedBy.toString(), iObject, scopedBy, true);
            rel1.ClassDefinition().FinishCreate(rel1);
        }
        CIMContext.Instance.Transaction().commit();
        return result;
    }

    @Override
    public IRelDef createRelDef(String name, String description, String uid1, String uid2, String role1, String role2, int min1, String min2, String max1, String max2) throws Exception {
        if (StringUtils.isEmpty(name))
            throw new Exception("invalid name for RelDef creation progress");
        if (StringUtils.isEmpty(uid1) || StringUtils.isEmpty(uid2))
            throw new Exception("invalid UID1 or UID2 for RelDef creation progress");
        CIMContext.Instance.Transaction().start();
        IObject iObject = SchemaUtility.newIObject(classDefinitionType.RelDef.toString(), name, description, domainInfo.SCHEMA.toString(), name);
        if (iObject != null) {
            IRel rel = (IRel) iObject.Interfaces().item(IRel.class.getSimpleName(), true);
            if (rel == null)
                throw new Exception("failed to initialize IRel Interface during creation");
            rel.setUID1(uid1);
            rel.setUID2(uid2);

            IRelDef relDef = (IRelDef) iObject.Interfaces().item(IRelDef.class.getSimpleName(), true);
            if (relDef == null)
                throw new Exception("failed to initialize IRelDef interface during creation");

            relDef.setRole1(role1);
            relDef.setRole1DisplayName(role1);
            relDef.setRole2(role2);
            relDef.setRole2DisplayName(role2);
            relDef.setMin1(min1);
            relDef.setMin2(min2);
            relDef.setMax1(max1);
            relDef.setMax2(max2);
            iObject.ClassDefinition().FinishCreate(iObject);
        }
        CIMContext.Instance.Transaction().commit();
        assert iObject != null;
        return iObject.toInterface(IRelDef.class);
    }

    @Override
    public IEnumListType createEnumListType(String name, String description, int enumNumber) throws Exception {
        if (StringUtils.isEmpty(name))
            throw new Exception("invalid name for enumerated list entry as it is NULL");
        CIMContext.Instance.Transaction().start();
        IObject iObject = SchemaUtility.newIObject(classDefinitionType.EnumListType.toString(), name, description, domainInfo.SCHEMA.toString(), "");
        if (iObject != null) {
            IEnumEnum enumEnum = (IEnumEnum) iObject.Interfaces().item(IEnumEnum.class.getSimpleName(), true);
            if (enumEnum == null)
                throw new Exception("failed to create Enumerated List Type as IEnumEnum initialized failed");
            enumEnum.setEnumNumber(enumNumber);
            iObject.ClassDefinition().FinishCreate(iObject);
        }
        CIMContext.Instance.Transaction().commit();
        assert iObject != null;
        return iObject.toInterface(IEnumListType.class);
    }

    @Override
    public boolean setImpliesForInterfaceDefs(String parentInterfaceDef, String impliesInterfaceDef) throws Exception {
        if (!StringUtils.isEmpty(parentInterfaceDef) && !StringUtils.isEmpty(impliesInterfaceDef)) {
            IObject parentItem = CIMContext.Instance.ProcessCache().item(parentInterfaceDef, domainInfo.SCHEMA.toString());
            IObject subItem = CIMContext.Instance.ProcessCache().item(impliesInterfaceDef, domainInfo.SCHEMA.toString());
            if (parentItem == null || subItem == null)
                throw new Exception("invalid parent interface definition or sub interface definition as one or both of them are not exist in database");
            CIMContext.Instance.Transaction().start();
            IRel rel = SchemaUtility.newRelationship(relDefinitionType.implies.toString(), parentItem, subItem, true);
            rel.ClassDefinition().FinishCreate(rel);
            CIMContext.Instance.Transaction().commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean setRealizes(String classDef, String interfaceDef) throws Exception {
        if (!StringUtils.isEmpty(classDef) && !StringUtils.isEmpty(interfaceDef)) {
            IObject classDefinition = CIMContext.Instance.ProcessCache().item(classDef, domainInfo.SCHEMA.toString());
            IObject interfaceDefinition = CIMContext.Instance.ProcessCache().item(interfaceDef, domainInfo.SCHEMA.toString());
            if (classDefinition == null || interfaceDefinition == null)
                throw new Exception("invalid class Definition or Interface Definition as one or both of them are not exist in database");
            CIMContext.Instance.Transaction().start();
            IRel rel = SchemaUtility.newRelationship(relDefinitionType.realizes.toString(), classDefinition, interfaceDefinition, true);
            rel.ClassDefinition().FinishCreate(rel);
            CIMContext.Instance.Transaction().commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean setContains(String parentEnumListType, String subEntry) throws Exception {
        if (!StringUtils.isEmpty(parentEnumListType) && !StringUtils.isEmpty(subEntry)) {
            IObject parentEnum = CIMContext.Instance.ProcessCache().item(parentEnumListType, domainInfo.SCHEMA.toString());
            IObject subEnum = CIMContext.Instance.ProcessCache().item(subEntry, domainInfo.SCHEMA.toString());
            if (parentEnum == null)
                throw new Exception("parent or sub Entry is not exist in database, cannot create relationship between them");
            CIMContext.Instance.Transaction().start();
            IRel rel = SchemaUtility.newRelationship(relDefinitionType.contains.toString(), parentEnum, subEnum, true);
            rel.ClassDefinition().FinishCreate(rel);
            CIMContext.Instance.Transaction().commit();
            return true;
        }
        return false;
    }

    @Override
    public void loadSchemaXml(MultipartFile file) throws Exception {
        if (file == null) throw new Exception("文件不能为空!");
        String filename = file.getOriginalFilename();
        String extension = CommonUtility.getFileNameExtWithoutPoint(filename);
        if (StringUtils.isEmpty(extension)) throw new Exception("无效的扩展名!");
        if (!"xml".equalsIgnoreCase(extension)) throw new Exception("不支持加载xml格式以外的文件!");
        ICIMLoader defaultLoader = new ICIMLoaderDefault(true);
        LoaderReport loaderReport = defaultLoader.loadDataByXml(file);
    }

    @Override
    public IObject getSchemaObjectByUID(String pstrUID) throws Exception {
        if (!StringUtils.isEmpty(pstrUID)) {
            return CIMContext.Instance.ProcessCache().item(pstrUID, null);
        }
        return null;
    }

    @Override
    public IObjectCollection getClassDefs() {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, IClassDef.class.getSimpleName());
        CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, new PageRequest(1, 10));
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getInterfaceDefs() {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, IInterfaceDef.class.getSimpleName());
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getPropertyDefs() {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, IPropertyDef.class.getSimpleName());
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getRelDefs() {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, IRelDef.class.getSimpleName());
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getEnumListTypes() {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, IEnumListType.class.getSimpleName());
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getEnumEnums() {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, IEnumEnum.class.getSimpleName());
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getClassDefs(String pstrNameCriteria, PageRequest pageRequest) {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionType.ClassDef.toString());
        if (!StringUtils.isEmpty(pstrNameCriteria) && !"*".equalsIgnoreCase(pstrNameCriteria)) {
            CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.like, pstrNameCriteria);
        }
        CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getInterfaceDefs(String pstrNameCriteria, PageRequest pageRequest) {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionType.InterfaceDef.toString());
        if (!StringUtils.isEmpty(pstrNameCriteria) && !"*".equalsIgnoreCase(pstrNameCriteria)) {
            CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.like, pstrNameCriteria);
        }
        CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getPropertyDefs(String pstrNameCriteria, PageRequest pageRequest) {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionType.PropertyDef.toString());
        if (!StringUtils.isEmpty(pstrNameCriteria) && !"*".equalsIgnoreCase(pstrNameCriteria)) {
            CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.like, pstrNameCriteria);
        }
        CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getRelDefs(String pstrNameCriteria, PageRequest pageRequest) {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionType.RelDef.toString());
        if (!StringUtils.isEmpty(pstrNameCriteria) && !"*".equalsIgnoreCase(pstrNameCriteria)) {
            CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.like, pstrNameCriteria);
        }
        CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getEnumListTypes(String pstrNameCriteria, PageRequest pageRequest) {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionType.EnumListType.toString());
        if (!StringUtils.isEmpty(pstrNameCriteria) && !"*".equalsIgnoreCase(pstrNameCriteria)) {
            CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.like, pstrNameCriteria);
        }
        CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObjectCollection getEnumEnums(String pstrNameCriteria, PageRequest pageRequest) {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionType.EnumEnum.toString());
        if (!StringUtils.isEmpty(pstrNameCriteria) && !"*".equalsIgnoreCase(pstrNameCriteria)) {
            CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.like, pstrNameCriteria);
        }
        CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public boolean deleteObject(String obid, String classDefinitionUID, boolean needTransaction) throws Exception {
        if (!StringUtils.isEmpty(obid)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionUID);
            CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, null, obid);
            IObject queryOne = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
            if (queryOne != null) {
                if (needTransaction)
                    CIMContext.Instance.Transaction().start();
                queryOne.Delete();
                if (needTransaction)
                    CIMContext.Instance.Transaction().commit();
                return true;
            } else
                throw new Exception("no object found with " + obid + "," + classDefinitionUID + ",delete progress terminated");
        }
        return false;
    }

    @Override
    public boolean deleteObjects(String pstrOBIDs, String pstrClassDefinitionUID) throws Exception {
        if (!StringUtils.isEmpty(pstrOBIDs) && !StringUtils.isEmpty(pstrClassDefinitionUID)) {
            IObjectCollection lcolObjects = CIMContext.Instance.ProcessCache().queryObjectsByOBIDAndClassDefinition(Arrays.asList(pstrOBIDs.split(",")), pstrClassDefinitionUID);
            if (SchemaUtility.hasValue(lcolObjects)) {
                SchemaUtility.beginTransaction();
                Iterator<IObject> e = lcolObjects.GetEnumerator();
                while (e.hasNext()) {
                    IObject lobj = e.next();
                    lobj.Delete();
                }
                SchemaUtility.commitTransaction();
            }
        }
        return true;
    }

    @Override
    public ICIMUser createUser(String loginUserName) throws Exception {
        return CIMContext.Instance.createLoginUser(loginUserName);
    }

    @Override
    public void cleanScope(String userName) throws Exception {
        CIMContext.Instance.cleanScope(userName);
    }

    @Override
    public boolean dropUser(String loginUserName) throws Exception {
        return CIMContext.Instance.dropLoginUser(loginUserName);
    }

    @Override
    public IObject createConfigurationItem(ObjectDTO objectDTO) throws Exception {
        return CIMContext.Instance.createConfigurationItem(objectDTO);
    }

    @Override
    public IObject updateConfigurationItem(ObjectDTO objectDTO) throws Exception {
        return CIMContext.Instance.updateConfigurationItem(objectDTO);
    }

    @Override
    public boolean deleteConfigurationItem(ObjectDTO objectDTO) throws Exception {
        return CIMContext.Instance.deleteConfigurationItem(objectDTO);
    }

    protected List<String> getDomainAndConfigurationTables(IObjectCollection configurations, IObjectCollection domains) {
        List<String> result = new ArrayList<>();
        List<String> domainTables = null;
        List<String> configurationTables = null;
        if (domains != null && domains.size() > 0)
            domainTables = domains.toList(IDomain.class).stream().map(IDomain::TablePrefix).distinct().collect(Collectors.toList());
        if (configurations != null)
            configurationTables = configurations.toList(ICIMConfigurationItem.class).stream().map(ICIMConfigurationItem::TablePrefix).distinct().collect(Collectors.toList());
        if (domainTables != null && domainTables.size() > 0) {
            result.addAll(domainTables);
        }
        if (configurationTables != null && configurations.size() > 0) {
            if (domainTables != null && domainTables.size() > 0) {
                for (String configurationTable : configurationTables) {
                    for (String domainTable : domainTables) {
                        String currentTable = configurationTable + domainTable;
                        result.add(currentTable);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void ensureTables() throws Exception {
        IObjectCollection configurationItems = CIMContext.Instance.getConfigurationItems();
        IObjectCollection domains = CIMContext.Instance.ProcessCache().getObjectsByClassDefCache(classDefinitionType.Domain.toString());
        List<String> tables = this.getDomainAndConfigurationTables(configurationItems, domains);
        if (tables != null && tables.size() > 0) {
            CIMContext.Instance.ensureTables(tables);
        }
    }

    @Override
    public List<KeyValuePair> getUserWithDefaultSettings(String userName) throws Exception {
        if (StringUtils.isEmpty(userName))
            userName = CIMContext.Instance.getLoginUserName();
        ICIMUser icimUser = null;
        IObject currentUser = CIMContext.Instance.createLoginUser(userName);
        if (currentUser != null) {
            icimUser = currentUser.toInterface(ICIMUser.class);
        }
        if (icimUser != null) {
            return icimUser.getUserDefaultInfo();
        } else
            throw new Exception("no user with " + userName + " found in database");
    }

    @Override
    public List<PropertyHierarchyVo> summaryProperty(List<String> classDefinitionUids, String... propertyDefinitionUids) throws Exception {
        List<PropertyHierarchyVo> result = new ArrayList<>();
        if (propertyDefinitionUids != null && propertyDefinitionUids.length > 0) {
            boolean flag = classDefinitionUids == null || classDefinitionUids.size() == 0;
            LinkedHashMap<String, List<IDomain>> domainInfoPerPropertyDef = new LinkedHashMap<>();
            for (String propertyDefinitionUid : propertyDefinitionUids) {
                IObject iObject = CIMContext.Instance.ProcessCache().item(propertyDefinitionUid, domainInfo.SCHEMA.toString(), false);
                if (iObject == null || !iObject.IsTypeOf(IPropertyDef.class.getSimpleName()))
                    throw new Exception("cannot execution summary report for property " + propertyDefinitionUid + " as it is not defined in system");
                IPropertyDef propertyDef = iObject.toInterface(IPropertyDef.class);
                IInterfaceDef exposedInterfaceDef = propertyDef.getExposesInterfaceDef();
                IObjectCollection classDefinitions = exposedInterfaceDef.getRealizedClassDefinition();
                List<IClassDef> currentClassDefs = classDefinitions.toList(IClassDef.class).stream().filter(s -> {
                    if (flag)
                        return true;
                    return classDefinitionUids.stream().anyMatch(c -> c.equalsIgnoreCase(s.UID()));
                }).collect(Collectors.toList());
                if (currentClassDefs.size() == 0)
                    throw new Exception("cannot execute summary report for property " + propertyDefinitionUid + " as not class definition(s) found from provided container or system");
                List<IDomain> currentDomains = new ArrayList<>();
                for (IClassDef currentClassDef : currentClassDefs) {
                    currentDomains.add(currentClassDef.getDomainForInstantiating());
                }
                domainInfoPerPropertyDef.put(propertyDefinitionUid, currentDomains);
            }
            List<PropertySummaryVo> propertySummaryVos = this.doSummaryProperty(domainInfoPerPropertyDef);
            if (propertySummaryVos.size() > 0) {
                for (PropertySummaryVo propertySummaryVo : propertySummaryVos) {
                    result.addAll(propertySummaryVo.generateItems());
                }
            }
        }
        if (result.size() > 0)
            result = this.renderAsHierarchy(result);
        for (PropertyHierarchyVo propertyHierarchyVo : result) {
            propertyHierarchyVo.release();
        }
        return result;
    }

    private List<PropertyHierarchyVo> renderAsHierarchy(List<PropertyHierarchyVo> items) {
        List<PropertyHierarchyVo> result = new ArrayList<>();
        if (items != null && items.size() > 0) {
            int level = 0;
            boolean flag = true;
            List<PropertyHierarchyVo> parentLevels = null;
            while (flag) {
                int finalLevel = level;
                List<PropertyHierarchyVo> currentItems = items.stream().filter(c -> c.getLevel() == finalLevel).collect(Collectors.toList());
                flag = currentItems.size() > 0;
                if (!flag)
                    break;
                if (result.size() == 0) {
                    parentLevels = currentItems;
                    result.addAll(parentLevels);
                } else {
                    List<PropertyHierarchyVo> currentLevels = new ArrayList<>();
                    for (PropertyHierarchyVo parentItem : parentLevels) {
                        List<String> objectIds = parentItem.getObjectIds();
                        for (PropertyHierarchyVo currentItem : currentItems) {
                            Collection<String> receiveCollectionList = CollectionUtility.receiveCollectionList(currentItem.getObjectIds(), objectIds);
                            if (CommonUtility.hasValue(receiveCollectionList)) {
                                PropertyHierarchyVo subItem = new PropertyHierarchyVo(currentItem.getPropertyDefinitionUid(), currentItem.getDisplayName(), new ArrayList<>(receiveCollectionList), currentItem.getLevel());
                                parentItem.append(subItem);
                            }
                        }
                        currentLevels.addAll(parentItem.getChildren());
                    }
                    parentLevels = currentLevels;
                }
                level++;
            }
        }
        return result;
    }

    private List<PropertySummaryVo> doSummaryProperty(LinkedHashMap<String, List<IDomain>> domainInfoPerPropertyDef) throws Exception {
        List<PropertySummaryVo> result = new ArrayList<>();
        if (domainInfoPerPropertyDef != null && domainInfoPerPropertyDef.size() > 0) {
            String configurationItemTablePrefix = CIMContext.Instance.getMyConfigurationItemTablePrefix();
            List<Callable<PropertySummaryVo>> executors = new ArrayList<>();
            int level = 0;
            for (Map.Entry<String, List<IDomain>> stringListEntry : domainInfoPerPropertyDef.entrySet()) {
                Callable<PropertySummaryVo> summaryVoCallable = new PropertySummaryExecutor(this.dataProviderService, stringListEntry.getKey(), configurationItemTablePrefix, stringListEntry.getValue(), level);
                executors.add(summaryVoCallable);
                level++;
            }
            if (executors.size() > 0) {
                List<PropertySummaryVo> propertySummaryVos = this.threadsProcessor.execute(executors);
                result.addAll(propertySummaryVos);
            }
        }
        return result;
    }

    @Override
    public IObject generateForm(String classDefinitionUID) throws Exception {
        if (!StringUtils.isEmpty(classDefinitionUID)) {
            IObject item = CIMContext.Instance.ProcessCache().item(classDefinitionUID);
            if (item != null) {
                IClassDef classDef = item.toInterface(IClassDef.class);
                CIMContext.Instance.Transaction().start();
                IObjectCollection formCollection = classDef.generateForm();
                CIMContext.Instance.Transaction().commit();
                return formCollection.get(queryTypes.classDefinitionUID, "CIMForm");
            }
        }
        return null;
    }

    @Override
    public IObjectCollection expandRelationships(String pstrNameOrUID, String classDefinitionUID, String relDef) throws
            Exception {
        if (classDefinitionUID == null || StringUtils.isEmpty(classDefinitionUID))
            classDefinitionUID = classDefinitionType.EnumListType.toString();
        if (StringUtils.isEmpty(relDef))
            relDef = "+" + relDefinitionType.contains;
        IObject object = CIMContext.Instance.ProcessCache().itemByName(pstrNameOrUID, classDefinitionUID);
        if (object == null)
            object = CIMContext.Instance.ProcessCache().itemByUIDOrOBID(pstrNameOrUID, classDefinitionUID);
        if (object != null) {
            relDirection direction = CommonUtility.toRelDirection(relDef);
            switch (direction) {
                case _1To2:
                    IRelCollection relCollection = object.GetEnd1Relationships().GetRels(CommonUtility.toActualDefinition(relDef));
                    if (relCollection != null)
                        return relCollection.GetEnd2s();
                    return null;
                case _2To1:
                    IRelCollection relCollection1 = object.GetEnd2Relationships().GetRels(CommonUtility.toActualDefinition(relDef));
                    if (relCollection1 != null)
                        return relCollection1.GetEnd1s();
                    return null;
            }
        }
        return null;
    }

    @Override
    public ObjectDTO generateDefaultPopup(String classDefinitionUID) throws Exception {
        if (!StringUtils.isEmpty(classDefinitionUID)) {
            IObject item = CIMContext.Instance.ProcessCache().item(classDefinitionUID, domainInfo.SCHEMA.toString(), false);
            if (item != null) {
                IClassDef classDef = item.toInterface(IClassDef.class);
                ObjectDTO result = new ObjectDTO();
                result.add(result.generateProperties());
                IObjectCollection interfaceDefs = classDef.getRealizedInterfaceDefs();
                if (interfaceDefs != null && interfaceDefs.hasValue()) {
                    Iterator<IObject> iObjectIterator = interfaceDefs.GetEnumerator();
                    while (iObjectIterator.hasNext()) {
                        IInterfaceDef interfaceDef = iObjectIterator.next().toInterface(IInterfaceDef.class);
                        if (interfaceDef != null) {
                            IObjectCollection propertyDefinitions = interfaceDef.getExposesPropertyDefinition();
                            if (propertyDefinitions != null && propertyDefinitions.size() > 0) {
                                Iterator<IObject> iObjectIterator1 = propertyDefinitions.GetEnumerator();
                                int orderValue = 10;
                                while (iObjectIterator1.hasNext()) {
                                    IPropertyDef propertyDef = iObjectIterator1.next().toInterface(IPropertyDef.class);
                                    if (propertyDef != null) {
                                        ObjectItemDTO itemDTO = new ObjectItemDTO();
                                        itemDTO.setWidth(100.0);
                                        itemDTO.setDefUID(propertyDef.UID());
                                        itemDTO.setLabel(propertyDef.DisplayName());
                                        itemDTO.setMandatory(propertyDef.checkMandatory());
                                        itemDTO.setOrderValue(orderValue);
                                        itemDTO.setGroupHeader(interfaceDef.DisplayName());
                                        itemDTO.setPropertyValueType(propertyDef.checkPropertyValueType().toString());
                                        itemDTO.setOptions(propertyDef.generateOptions());
                                        itemDTO.specialProgressForDisplayItem();
                                        result.add(itemDTO);
                                    }
                                    orderValue += 10;
                                }
                            }
                        }
                    }
                }
                return result;
            }
        }
        return null;
    }

    public IObjectCollection getRelsForObject(String obid, String classDefinition, relCollectionTypes
            collectionTypes) throws Exception {
        if (!StringUtils.isEmpty(obid)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().setQueryForRelationship(queryRequest, true);
            if (!StringUtils.isEmpty(classDefinition)) {
                IClassDef classDef = CIMContext.Instance.ProcessCache().item(classDefinition, domainInfo.SCHEMA.toString(), false).toInterface(IClassDef.class);
                if (classDef == null)
                    throw new Exception("invalid class definition " + classDefinition + " as it is not exist in database");
                queryRequest.getDomains().addAll(classDef.getUsedDomain());
            }
            switch (collectionTypes) {
                case End2s:
                    CIMContext.Instance.QueryEngine().addRelOrEdgeDefForQuery(queryRequest, null, interfaceDefinitionType.IRel.toString(), propertyDefinitionType.OBID2.toString(), null, obid);
                    break;
                case End1s:
                    CIMContext.Instance.QueryEngine().addRelOrEdgeDefForQuery(queryRequest, null, interfaceDefinitionType.IRel.toString(), propertyDefinitionType.OBID1.toString(), null, obid);
                    break;
            }
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    @Override
    public ICIMForm getForm(String formPurpose, String classDefinitionUID) throws Exception {
        if (formPurpose == null || StringUtils.isEmpty(formPurpose))
            formPurpose = ccm.server.enums.formPurpose.Info.toString();
        if (!StringUtils.isEmpty(classDefinitionUID)) {
            IObject item = CIMContext.Instance.ProcessCache().item(classDefinitionUID, domainInfo.SCHEMA.toString(), false);
            if (item != null) {
                IClassDef classDef = item.toInterface(IClassDef.class);
                if (classDef == null)
                    throw new Exception("invalid class definition " + classDefinitionUID);
                IObjectCollection forms = classDef.getForms();
                if (forms == null || forms.size() == 0) {
                    return null;
                }
                if (forms.size() > 0) {
                    Iterator<IObject> iObjectIterator = forms.GetEnumerator();
                    while (iObjectIterator.hasNext()) {
                        IObject next = iObjectIterator.next();
                        if (next.IsTypeOf(ICIMForm.class.getSimpleName())) {
                            ICIMForm form = next.toInterface(ICIMForm.class);
                            String[] strings = form.FormPurpose().split(",");
                            String finalFormPurpose = formPurpose;
                            if (Arrays.stream(strings).anyMatch(c -> c.equalsIgnoreCase(finalFormPurpose)))
                                return form;
                        }
                    }
                }
            } else
                throw new Exception("cannot find class definition " + classDefinitionUID + " in database");
        }
        return null;
    }

    @Override
    public ICIMForm getFormByName(String formPurpose, String formName) {
        return null;
    }

    @Override
    public IObjectCollection generalQuery(String classDefinitionUID, String name, String description, int pageIndex,
                                          int pageSize) {
        if (!StringUtils.isEmpty(classDefinitionUID)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionUID);
            if (!StringUtils.isEmpty(name)) {
                if (!StringUtils.isEmpty(name.replace("*", ""))) {
                    if (name.contains("*"))
                        CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.like, name);
                    else
                        CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.equal, name);
                }
            }
            if (!StringUtils.isEmpty(description)) {
                if (!StringUtils.isEmpty(description.replace("*", ""))) {
                    if (description.contains("*"))
                        CIMContext.Instance.QueryEngine().addDescriptionForQuery(queryRequest, operator.like, description);
                    else
                        CIMContext.Instance.QueryEngine().addDescriptionForQuery(queryRequest, null, description);
                }
            }
            CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, new PageRequest(pageIndex, pageSize));
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    @Override
    public void expansionPaths(IObjectCollection pcolItems, List<String> pcolExpansionPaths) throws Exception {
        if (pcolItems != null && pcolItems.hasValue() && CommonUtility.hasValue(pcolExpansionPaths)) {
            for (String expansionPath : pcolExpansionPaths) {
                String[] strings = expansionPath.split("->>>");
                ExpansionMode expansionMode = ExpansionMode.relatedObject;
                String path = expansionPath;
                if (strings.length > 1) {
                    expansionMode = ExpansionMode.valueOf(strings[0]);
                    path = strings[1];
                }

                relDirection relDirection = CommonUtility.toRelDirection(path);
                if (relDirection == null || relDirection == ccm.server.enums.relDirection._unknown)
                    relDirection = ccm.server.enums.relDirection._1To2;

                String[] strings1 = path.split("\\.");
                if (strings1.length > 1)
                    path = strings1[0];
                path = CommonUtility.toActualDefinition(path);
                switch (relDirection) {
                    case _1To2:
                        pcolItems.GetEnd1Relationships().GetRels(path, false).GetEnd2s();
                        break;
                    case _2To1:
                        pcolItems.GetEnd2Relationships().GetRels(path, false).GetEnd1s();
                        break;
                }
            }
        }
    }

    protected boolean identifyOperatorIn(String value) {
        if (value != null && value.startsWith("(") && value.endsWith(")"))
            return true;
        else if (value != null) {
            // 2022.02.27 HT StackOverflow 问题修复
            if (value.startsWith(NOT_CHAR)) {
                value = value.substring(1);
                return this.identifyOperatorIn(value);
            }
            // 2022.02.27 HT StackOverflow 问题修复
        }
        return false;
    }

    protected boolean identifyOperatorNot(String value) {
        return value != null && value.startsWith(NOT_CHAR);
    }

    protected String ensureValueForQuery(String value) {
        if (this.identifyOperatorNot(value))
            value = value.substring(1);
        if (this.identifyOperatorIn(value))
            value = value.substring(value.indexOf("(") + 1, value.lastIndexOf(")"));
        return value;
    }

    protected operator determineOperator(String value) {
        boolean useNot = this.identifyOperatorNot(value);
        boolean useIn = this.identifyOperatorIn(value);
        boolean useLike = value.contains("*");
        if (useLike) {
            if (useNot)
                return operator.notLike;
            else
                return operator.like;
        } else if (useIn) {
            if (useNot)
                return operator.notIn;
            else
                return operator.in;
        } else {
            if (useNot)
                return operator.notEqual;
        }
        return operator.equal;
    }


    @Override
    public LiteCriteria liteCriteria(String expansionModePlusIdentity) throws Exception {
        LiteCriteria result = new LiteCriteria(null, null, null, null, null, null);
        if (!StringUtils.isEmpty(expansionModePlusIdentity)) {
            String[] strings = expansionModePlusIdentity.split("->>>");
            ExpansionMode expansionMode = ExpansionMode.none;
            String identity = expansionModePlusIdentity;
            if (strings.length > 1) {
                expansionMode = ExpansionMode.valueOf(strings[0]);
                identity = strings[1];
            }
            String propertyDefinitionUID = null;
            String relOrEdgeDefinitionUID = null;
            String interfaceDefinitionUID = null;
            relDirection direction = null;

            String pathInfo = identity;
            if (identity.startsWith("+")) {
                direction = relDirection._1To2;
                pathInfo = pathInfo.substring(1);
            } else if (identity.startsWith("-")) {
                direction = relDirection._2To1;
                pathInfo = pathInfo.substring(1);
            }
            String[] ss = pathInfo.split("\\.");
            IObject schemaObj;
            if (ss.length == 1) {
                schemaObj = CIMContext.Instance.ProcessCache().item(pathInfo, null, false);
            } else {
                schemaObj = CIMContext.Instance.ProcessCache().item(ss[0], null, false);
                relOrEdgeDefinitionUID = ss[0];
                propertyDefinitionUID = ss[1];
            }

            if (schemaObj == null)
                throw new Exception("invalid schema definition with " + pathInfo + " as it was not exist in database");
            else {
                if (schemaObj.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.PropertyDef.toString())) {
                    propertyDefinitionUID = pathInfo;
                    relOrEdgeDefinitionUID = "";
                    direction = null;
                } else if (schemaObj.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.RelDef.toString()) || schemaObj.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.EdgeDef.toString())) {
                    if (StringUtils.isEmpty(propertyDefinitionUID)) {
                        propertyDefinitionUID = propertyDefinitionType.OBID.toString();
                        interfaceDefinitionUID = interfaceDefinitionType.IObject.toString();
                    }
                    if (StringUtils.isEmpty(relOrEdgeDefinitionUID))
                        relOrEdgeDefinitionUID = pathInfo;
                    if (expansionMode == ExpansionMode.none)
                        expansionMode = ExpansionMode.relatedObject;
                }
            }
            if (StringUtils.isEmpty(interfaceDefinitionUID) && !StringUtils.isEmpty(propertyDefinitionUID))
                interfaceDefinitionUID = CIMContext.Instance.ProcessCache().getExposedInterfaceByPropertyDef(propertyDefinitionUID);
            result.setPathDefinition(relOrEdgeDefinitionUID);
            result.setInterfaceDefinitionUID(interfaceDefinitionUID);
            result.setPropertyDefinition(propertyDefinitionUID);
            result.setRelDirection(direction);
            result.setExpansionMode(expansionMode);
        }
        return result;
    }

    protected void initOrderParams(QueryRequest queryRequest, List<OrderByWrapper> orderInfos) {
        if (orderInfos != null && orderInfos.size() > 0) {
            for (OrderByWrapper orderInfo : orderInfos) {
                CIMContext.Instance.QueryEngine().setOrderBy(queryRequest, orderInfo.getOrderMode(), orderInfo.expansionModePlusIdentity());
            }
        }
    }

    @Override
    public IObjectCollection generalQuery(String classDefinitionUID, int pageIndex, int pageSize, List<
            OrderByWrapper> orderInfos, Map<String, String> criterions) throws Exception {
        if (!StringUtils.isEmpty(classDefinitionUID)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionUID);
            if (pageIndex > 0 && pageSize > 0)
                CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, new PageRequest(pageIndex, pageSize));
            this.initOrderParams(queryRequest, orderInfos);
            if (CommonUtility.hasValue(criterions)) {
                for (Map.Entry<String, String> entry : criterions.entrySet()) {
                    LiteCriteria liteCriteria = this.liteCriteria(entry.getKey());
                    ValueWithOperator valueWithOperator = new ValueWithOperator(entry.getValue());
                    String valuePattern = this.convertValueForExactQuery(valueWithOperator.getValuePattern(), liteCriteria.getPropertyDefinition());
                    valueWithOperator.setValuePattern(valuePattern);
                    CIMContext.Instance.QueryEngine().addRelOrEdgeDefForQuery(queryRequest, liteCriteria.expansionPath(), liteCriteria.getInterfaceDefinitionUID(), liteCriteria.getPropertyDefinition(), valueWithOperator.getOperator(), valuePattern, liteCriteria.getExpansionMode());
                }
            }
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    public IObjectCollection expandObjs(IObjectCollection startObjs, String relDefUid, String... classDefinitionUids) throws Exception {
        if (startObjs != null && startObjs.size() > 0 && !StringUtils.isEmpty(relDefUid)) {
            relDirection relDirection = ccm.server.enums.relDirection.toEnumByRelDef(relDefUid);
            relDefUid = ccm.server.enums.relDirection.toActualRelDef(relDefUid);
            IObjectCollection result = null;
            switch (relDirection) {
                case _2To1:
                    result = startObjs.GetEnd1Relationships().GetRels(relDefUid, false).GetEnd2s();
                    break;
                default:
                    result = startObjs.GetEnd2Relationships().GetRels(relDefUid, false).GetEnd1s();
                    break;
            }
            if (classDefinitionUids != null && classDefinitionUids.length > 0 && result != null) {
                result = result.getByClassDefinitionUids(classDefinitionUids);
            }
            return result;
        }
        return null;
    }

    @Override
    public IObjectCollection liteQuery(String classDefinitionUID, int pageIndex, int pageSize, List<
            OrderByWrapper> orderInfos, Map<String, String> criterions, ObjectDTO form) throws Exception {
        if (!StringUtils.isEmpty(classDefinitionUID)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionUID);
            if (pageIndex > 0 && pageSize > 0)
                CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, new PageRequest(pageIndex, pageSize));
            this.initOrderParams(queryRequest, orderInfos);
            if (CommonUtility.hasValue(criterions)) {
                for (Map.Entry<String, String> entry : criterions.entrySet()) {
                    LiteCriteria liteCriteria = this.liteCriteria(entry.getKey());
                    ValueWithOperator valueWithOperator = new ValueWithOperator(entry.getValue());
                    String valuePattern = this.convertValueForExactQuery(valueWithOperator.getValuePattern(), liteCriteria.getPropertyDefinition());
                    valueWithOperator.setValuePattern(valuePattern);
                    CIMContext.Instance.QueryEngine().addRelOrEdgeDefForQuery(queryRequest, liteCriteria.expansionPath(), liteCriteria.getInterfaceDefinitionUID(), liteCriteria.getPropertyDefinition(), valueWithOperator.getOperator(), valuePattern, liteCriteria.getExpansionMode());
                }
            }
            CIMContext.Instance.QueryEngine().setRenderStyle(queryRequest, form);
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    @Override
    public IObjectCollection generalQuery(String classDefinitionUID, int pageIndex, int pageSize, List<
            OrderByWrapper> sortWrappers, Map<String, String> criterions, List<String> expansionPaths) throws
            Exception {
        IObjectCollection objectCollection = this.generalQuery(classDefinitionUID, pageIndex, pageSize, sortWrappers, criterions);
        if (objectCollection != null && objectCollection.size() > 0)
            this.expansionPaths(objectCollection, expansionPaths);
        return objectCollection;
    }

    private static final String NOT_CHAR = "!";

    protected void verifyValidObjectForRelationship(IObject object, IRelDef relDef, relDirection relDirection) throws
            Exception {
        if (object != null && relDef != null) {
            String interfaceDef = relDirection == ccm.server.enums.relDirection._1To2 ? relDef.UID1() : relDef.UID2();
            if (StringUtils.isEmpty(interfaceDef))
                throw new Exception("invalid relationship definition as UID1 or UID2 is missing");
            // 2022.06.06 HT 通用更新创建关联关系失败问题修复
            /*if (!object.Interfaces().hasInterface(interfaceDef))
                throw new Exception("invalid object " + object.toErrorPop() + " for relationship construction:" + relDef.UID());*/

        }
    }

    protected void processRelationships(IObject startObj, List<ObjectItemDTO> relItems) throws Exception {
        if (startObj != null && CommonUtility.hasValue(relItems)) {
            for (ObjectItemDTO relItem : relItems) {
                String relDefUID = relItem.getDefUID();
                relDirection relDirection = relItem.getRelDirection();
                if (relDirection == null || relDirection == ccm.server.enums.relDirection._unknown)
                    relDirection = ccm.server.enums.relDirection.toEnumByRelDef(relDefUID);
                relDefUID = CommonUtility.toActualDefinition(relDefUID);
                String[] relDefPlusProperty = relDefUID.split("\\.");
                relDefUID = relDefPlusProperty[0];
                String pathProperty = propertyDefinitionType.OBID.toString();
                if (relDefPlusProperty.length > 1)
                    pathProperty = relDefPlusProperty[1];
                IRelDef relDef = CIMContext.Instance.ProcessCache().item(relDefUID, domainInfo.SCHEMA.toString()).toInterface(IRelDef.class);
                this.verifyValidObjectForRelationship(startObj, relDef, relDirection);
                IRelCollection existRels = null;
                Object displayValue = relItem.getDisplayValue();
                String[] identities = displayValue != null ? displayValue.toString().split(",") : new String[]{};
                List<String> lcolToBeProcessing = new ArrayList<>();
                List<IRel> lcolToDeleted = new ArrayList<>();
                Map<relEndObj, IRel> existMapRels = null;
                switch (relDirection) {
                    case _1To2:
                        existRels = startObj.GetEnd1Relationships().GetRels(relDefUID);
                        if (existRels != null && existRels.hasValue()) {
                            existMapRels = existRels.toMapByUIDAndOBID(relCollectionTypes.End2s);
                        }
                        break;
                    case _2To1:
                        existRels = startObj.GetEnd2Relationships().GetRels(relDefUID);
                        if (existRels != null && existRels.hasValue()) {
                            existMapRels = existRels.toMapByUIDAndOBID(relCollectionTypes.End1s);
                        }
                        break;
                }

                for (String identity : identities) {
                    if (existMapRels != null) {
                        String finalPathProperty = pathProperty;
                        relEndObj stringEntry = existMapRels.keySet().stream().filter(c -> c.isHint(finalPathProperty, identity)).findFirst().orElse(null);
                        if (stringEntry != null)
                            existMapRels.remove(stringEntry);
                        else
                            lcolToBeProcessing.add(identity);
                    } else
                        lcolToBeProcessing.add(identity);
                }
                if (existMapRels != null) {
                    for (Map.Entry<relEndObj, IRel> entryIRelEntry : existMapRels.entrySet()) {
                        String finalPathProperty1 = pathProperty;
                        if (Arrays.stream(identities).noneMatch(c -> entryIRelEntry.getKey().isHint(finalPathProperty1, c))) {
                            lcolToDeleted.add(entryIRelEntry.getValue());
                        }
                    }
                }
                if (lcolToDeleted.size() > 0) {
                    for (IRel rel : lcolToDeleted) {
                        rel.Delete();
                    }
                }
                this.buildRelationship(startObj, relDefUID, relDirection, String.join(",", lcolToBeProcessing));
            }
        }
    }

    @Override
    public IObject generalUpdate(ObjectDTO objectDTO) throws Exception {
        IObject object = null;
        if (objectDTO != null) {
            String obid = objectDTO.getObid();
            String classDefinitionUID = objectDTO.getClassDefinitionUID();
            String domainUID = objectDTO.toGetValue(propertyDefinitionType.DomainUID.toString());
            String uid = objectDTO.getUid();
            if (StringUtils.isEmpty(classDefinitionUID))
                throw new Exception("invalid class definition UID from provided ObjectDTO");
            if (!StringUtils.isEmpty(obid))
                object = CIMContext.Instance.ProcessCache().getObjectByOBID(obid, classDefinitionUID);
            if (object == null)
                object = CIMContext.Instance.ProcessCache().queryObjectsByUIDAndClassDefinition(uid, domainUID, classDefinitionUID);
            if (object == null)
                throw new Exception("invalid object with provide UID and Domain -> " + uid + "," + domainUID);
            try {
                CIMContext.Instance.Transaction().start();
                object.BeginUpdate();
                List<ObjectItemDTO> relItems = new ArrayList<>();
                for (ObjectItemDTO item : objectDTO.getItems()) {
                    String propertyDefinitionUID = item.getDefUID();
                    if (item.getDefType() == classDefinitionType.PropertyDef) {
                        if (this.propertyCanBeUpdate(propertyDefinitionUID)) {
                            object.setValue(propertyDefinitionUID, item.getDisplayValue());
                        }
                    } else if (item.getDefType() == classDefinitionType.RelDef) {
                        relItems.add(item);
                    }
                }
                object.FinishUpdate();
                this.processRelationships(object, relItems);
                CIMContext.Instance.Transaction().commit();
            } catch (Exception exception) {
                CIMContext.Instance.Transaction().rollBack();
                throw exception;
            }
        }
        return object;
    }

    @Override
    public IObjectCollection generalUpdate(List<ObjectDTO> objectDTOs) throws Exception {
        IObjectCollection result = new ObjectCollection();
        if (objectDTOs != null && objectDTOs.size() > 0) {
            try {
                List<String> classDefinitions = objectDTOs.stream().map(ObjectDTO::getClassDefinitionUID).collect(Collectors.toList());
                for (String classDefinition : classDefinitions) {
                    List<ObjectDTO> dtoList = objectDTOs.stream().filter(c -> c.getClassDefinitionUID().equalsIgnoreCase(classDefinition)).collect(Collectors.toList());
                    List<String> obids = dtoList.stream().map(ObjectDTO::getObid).distinct().collect(Collectors.toList());
                    if (CommonUtility.hasValue(obids) && obids.stream().noneMatch(StringUtils::isEmpty)) {
                        IObjectCollection objectCollection = CIMContext.Instance.ProcessCache().queryObjectsByOBIDAndClassDefinition(obids, classDefinition);
                        if (objectCollection == null || objectCollection.size() != obids.size())
                            throw new Exception("one ore more object(s) cannot be found in database, update progress will be terminated");
                        result.addRangeUniquely(objectCollection);
                    }
                }
                if (result.size() > 0) {
                    CIMContext.Instance.Transaction().start();
                    Iterator<IObject> iterator = result.GetEnumerator();
                    while (iterator.hasNext()) {
                        IObject iObject = iterator.next();
                        ObjectDTO objectDTO = objectDTOs.stream().filter(c -> c.getObid().equalsIgnoreCase(iObject.OBID())).findFirst().orElse(null);
                        if (objectDTO != null) {
                            objectDTOs.remove(objectDTO);
                            iObject.BeginUpdate();
                            List<ObjectItemDTO> relItems = new ArrayList<>();
                            for (ObjectItemDTO item : objectDTO.getItems()) {
                                String propertyDefinitionUID = item.getDefUID();
                                if (item.getDefType() == classDefinitionType.PropertyDef) {
                                    if (this.propertyCanBeUpdate(propertyDefinitionUID)) {
                                        iObject.setValue(propertyDefinitionUID, item.getDisplayValue());
                                    }
                                } else if (item.getDefType() == classDefinitionType.RelDef) {
                                    relItems.add(item);
                                }
                            }
                            iObject.FinishUpdate();
                            this.processRelationships(iObject, relItems);
                        }
                    }
                    CIMContext.Instance.Transaction().commit();
                    CIMContext.Instance.ProcessCache().inflateCachedIObjectFromDataBase(result);
                }
            } catch (Exception exception) {
                CIMContext.Instance.Transaction().rollBack();
                throw exception;
            }
        }
        return result;
    }

    protected void buildRelationship(IObject startObject, String relDefUID, relDirection relDirection, Object value) throws
            Exception {
        if (startObject != null && !StringUtils.isEmpty(relDefUID) && value != null && !StringUtils.isEmpty(value.toString())) {
            String actualRelDefUid = CommonUtility.toActualDefinition(relDefUID);
            actualRelDefUid = actualRelDefUid.split("\\.")[0];
            IObjectCollection targetObjects = this.getEndObjects(actualRelDefUid, relDirection, value);
            if (targetObjects == null || targetObjects.size() == 0)
                throw new Exception("no object exist in database for " + relDefUID + " with " + value);
            Iterator<IObject> e = targetObjects.GetEnumerator();
            IRelDef relDef = CIMContext.Instance.ProcessCache().item(actualRelDefUid, domainInfo.SCHEMA.toString(), false).toInterface(IRelDef.class);
            if (relDef == null)
                throw new Exception("invalid relationship definition **" + relDefUID + "**");
            String targetInterface = relDef.getTargetInterface(relDirection);
            while (e.hasNext()) {
                IObject rel = null;
                IObject endObject = e.next();
                if (!endObject.Interfaces().hasInterface(targetInterface))
                    throw new Exception("invalid object with " + endObject.toErrorPop() + " as missing interface definition " + targetInterface);
                switch (relDirection) {
                    case _1To2:
                        rel = SchemaUtility.newRelationship(relDefUID, startObject, endObject, true);
                        break;
                    case _2To1:
                        rel = SchemaUtility.newRelationship(relDefUID, endObject, startObject, true);
                        break;
                }
                if (rel != null)
                    rel.ClassDefinition().FinishCreate(rel);
            }
        }
    }

    protected void buildRelationship(IObject startObject, String relDefUID, Object value) throws Exception {
        if (startObject != null && !StringUtils.isEmpty(relDefUID) && value != null && !StringUtils.isEmpty(value.toString())) {
            relDirection relDirection = ccm.server.enums.relDirection.toEnumByRelDef(relDefUID);
            relDefUID = CommonUtility.toActualDefinition(relDefUID);
            if (relDirection == null)
                relDirection = ccm.server.enums.relDirection._1To2;
            this.buildRelationship(startObject, relDefUID, relDirection, value);
        }
    }

    protected void buildRelationshipForProvidedItemDTOs(List<ObjectItemDTO> relItems, IObject startObject) throws
            Exception {
        if (CommonUtility.hasValue(relItems) && startObject != null) {
            for (ObjectItemDTO relItem : relItems) {
                String relDefUID = relItem.getDefUID();
                Object displayValue = relItem.getDisplayValue();
                relDirection relDirection = relItem.getRelDirection();
                this.buildRelationship(startObject, relDefUID, relDirection, displayValue);
            }
        }
    }

    protected List<String> getInitializedPropertiesDuringNewObject() {
        return new ArrayList<String>() {{
            this.add(propertyDefinitionType.Name.toString());
            this.add(propertyDefinitionType.Description.toString());
            this.add(propertyDefinitionType.UID.toString());
            this.add(propertyDefinitionType.OBID.toString());
            this.add(propertyDefinitionType.DomainUID.toString());
            this.add(propertyDefinitionType.ClassDefinitionUID.toString());
        }};
    }

    @Override
    public IObject generalCreate(ObjectDTO objectDTO) throws Exception {
        IObject iObject;
        if (objectDTO != null) {
            String classDefinitionUID = objectDTO.getClassDefinitionUID();
            String obid = objectDTO.getObid();
            if (!StringUtils.isEmpty(obid))
                throw new Exception("OBID shall be null during creation progress");
            IObject item = CIMContext.Instance.ProcessCache().item(classDefinitionUID, domainInfo.SCHEMA.toString());
            if (item == null)
                throw new Exception("invalid class definition with " + classDefinitionUID + " in database");
            try {
                CIMContext.Instance.Transaction().start();
                iObject = SchemaUtility.newIObject(classDefinitionUID, objectDTO.getName(), objectDTO.getDescription(), null, objectDTO.getUid());
                if (iObject != null) {
                    List<ObjectItemDTO> relItems = new ArrayList<>();
                    List<String> propertiesDuringNewObject = this.getInitializedPropertiesDuringNewObject();
                    for (ObjectItemDTO objectDTOItem : objectDTO.getItems()) {
                        String propertyDefinitionUID = objectDTOItem.getDefUID();
                        if (objectDTOItem.getDefType() == classDefinitionType.PropertyDef) {
                            if (this.propertyCanBeUpdate(propertyDefinitionUID) && !propertiesDuringNewObject.contains(propertyDefinitionUID))
                                iObject.setValue(propertyDefinitionUID, objectDTOItem.getDisplayValue());
                        } else if (objectDTOItem.getDefType() == classDefinitionType.RelDef) {
                            relItems.add(objectDTOItem);
                        }
                    }
                    iObject.ClassDefinition().FinishCreate(iObject);
                    this.buildRelationshipForProvidedItemDTOs(relItems, iObject);
                }
                CIMContext.Instance.Transaction().commit();
            } catch (Exception exception) {
                CIMContext.Instance.Transaction().rollBack();
                throw exception;
            }
            return iObject;
        }
        return null;
    }

    protected IObjectCollection getEndObjects(String relDefUID, relDirection relDirection, Object displayValue) throws Exception {
        IObjectCollection result = new ObjectCollection();
        if (!StringUtils.isEmpty(relDefUID) && displayValue != null && !StringUtils.isEmpty(displayValue.toString())) {
            if (relDirection == null)
                relDirection = ccm.server.enums.relDirection._1To2;
            String[] obidOrUIDs = displayValue.toString().split(",");
            List<String> lcolToBeRetrievedFromDB = new ArrayList<>();
            for (String obidOrUID : obidOrUIDs) {
                IObject item = CIMContext.Instance.ProcessCache().getObjectByOBIDCache(obidOrUID);
                if (item == null)
                    item = CIMContext.Instance.ProcessCache().item(obidOrUID, false);
                if (item != null)
                    result.append(item);
                else
                    lcolToBeRetrievedFromDB.add(obidOrUID);
            }
            IRelDef relDef = CIMContext.Instance.ProcessCache().item(relDefUID, domainInfo.SCHEMA.toString()).toInterface(IRelDef.class);
            String interfaceDefForQuery = relDirection == ccm.server.enums.relDirection._1To2 ? relDef.UID2() : relDef.UID1();
            if (StringUtils.isEmpty(interfaceDefForQuery))
                throw new Exception("invalid interface definition for query progress as it is NULL");
            if (lcolToBeRetrievedFromDB.size() > 0) {
                IObjectCollection objectCollection = CIMContext.Instance.QueryEngine().getObjectByOBIDsAndInterfaceDefinitionUID(lcolToBeRetrievedFromDB, interfaceDefForQuery);
                if (objectCollection != null && objectCollection.size() == lcolToBeRetrievedFromDB.size())
                    result.addRangeUniquely(objectCollection);
                else {
                    IObjectCollection iObjectCollection = CIMContext.Instance.QueryEngine().getObjectByUIDsAndInterfaceDefinitionUID(lcolToBeRetrievedFromDB, interfaceDefForQuery);
                    if (iObjectCollection != null)
                        result.addRangeUniquely(iObjectCollection);
                }
            }
        }
        return result;
    }

    @Override
    public IObjectCollection getSchemaObjects(@NotNull String pstrSchemaType, String pstrNameCriteria, String
            pstrClassDefinitionUID, @NotNull PageRequest pageRequest) throws Exception {
        IObjectCollection lcolContainer = null;
        switch (pstrSchemaType) {
            case "ClassDef":
                lcolContainer = this.getClassDefs(pstrNameCriteria, pageRequest);
                break;
            case "InterfaceDef":
                lcolContainer = this.getInterfaceDefs(pstrNameCriteria, pageRequest);
                break;
            case "RelDef":
                lcolContainer = this.getRelDefs(pstrNameCriteria, pageRequest);
                break;
            case "EnumListType":
                lcolContainer = this.getEnumListTypes(pstrNameCriteria, pageRequest);
                break;
            case "PropertyDef":
                lcolContainer = this.getPropertyDefs(pstrNameCriteria, pageRequest);
                break;
            case "EnumEnum":
                lcolContainer = this.getEnumEnums(pstrNameCriteria, pageRequest);
                break;
            case "Object":
                lcolContainer = this.searchObjectsWithClassDefAndNameCriteria(pstrClassDefinitionUID, pstrNameCriteria, pageRequest);
                break;
        }
        return lcolContainer;
    }

    public IObjectCollection searchObjectsWithClassDefAndNameCriteria(@NotNull String
                                                                              pstrClassDefinitionUID, String pstrNameCriteria, PageRequest pageRequest) {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, pstrClassDefinitionUID);
        CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        if (!StringUtils.isEmpty(pstrNameCriteria) && !"*".equalsIgnoreCase(pstrNameCriteria)) {
            CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.like, pstrNameCriteria);
        }
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public String getObjectsXmlInfo(JSONArray jsonArray) throws Exception {
        if (jsonArray != null && jsonArray.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            int count = 1;
            for (Object object : jsonArray) {
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(object));
                String lstrOBID = jsonObject.getString(propertyDefinitionType.OBID.toString());
                IObject lobjPointedObj = CIMContext.Instance.ProcessCache().getObjectByOBID(lstrOBID, jsonObject.getString(propertyDefinitionType.ClassDefinitionUID.toString()));
                if (lobjPointedObj == null)
                    throw new Exception("未找到OBID: " + lstrOBID + " 的对象");
                stringBuilder.append(lobjPointedObj.toXml());
                if (count != jsonArray.size()) {
                    stringBuilder.append("\r\n");
                    stringBuilder.append("\r\n");
                }
                count++;
            }
            return stringBuilder.toString();
        }
        return null;
    }

    @Override
    public List<ObjectXmlDTO> getObjRelatedObjsAndRels(@NotNull JSONObject jsonObject) throws Exception {
        String lstrOBID = jsonObject.getString(propertyDefinitionType.OBID.toString());
        String lstrClassDefUID = jsonObject.getString(propertyDefinitionType.ClassDefinitionUID.toString());
        IObjectCollection relsForObject = this.getRelsForObject(lstrOBID, lstrClassDefUID, relCollectionTypes.End1s);
        List<ObjectXmlDTO> lcolResult = new ArrayList<>();
        if (SchemaUtility.hasValue(relsForObject)) {
            Iterator<IObject> iterator = relsForObject.GetEnumerator();
            while (iterator.hasNext()) {
                IObject next = iterator.next();
                ObjectXmlDTO xmlDTO = next.toObjectXmlDTO();
                lcolResult.add(xmlDTO);
                IRel lobjRel = next.toInterface(IRel.class);
                if (lobjRel.GetEnd2() != null) {
                    xmlDTO.appendChild(lobjRel.GetEnd2().toObjectXmlDTO());
                }
            }
        }
        return lcolResult;
    }

    @Override
    public void generateXmlFile(@NotNull JSONArray pcolSelObject, HttpServletResponse response) throws Exception {
        IObjectCollection lcolObjects = SchemaUtility.convertJSONArrayToIObjectCollection(pcolSelObject);
        if (SchemaUtility.hasValue(lcolObjects)) {
            Document domDocument = new DOMDocument();
            Element container = domDocument.addElement("Container");
            Iterator<IObject> e = lcolObjects.GetEnumerator();
            while (e.hasNext()) {
                IObject lobj = e.next();
                SchemaUtility.addXmlStructureInfoContainer(container, lobj);
            }
            String lstrFilePath = CommonUtility.getTempFolder() + "/schemaContainer.xml";
            File file = CommonUtility.saveXmlDocumentToFile(lstrFilePath, domDocument);
            if (file != null && file.exists()) {
                InputStream fis = new BufferedInputStream(Files.newInputStream(file.toPath()));
                response.reset();
                String fileName = "schemaContainer.xml";
                response.setContentType("application/octet-stream;charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.addHeader("Pargam", "no-cache");
                response.addHeader("Cache-Control", "no-cache");
                ServletOutputStream out = response.getOutputStream();
                //读取文件流
                int len;
                byte[] buffer = new byte[1024 * 10];
                while ((len = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.close();
                fis.close();
            }
        }
    }

    @Override
    public IObjectCollection getRelsByRelDef(List<String> relDefUIDs) throws Exception {
        if (CommonUtility.hasValue(relDefUIDs)) {
            return CIMContext.Instance.QueryEngine().getRelationshipsByRelDefs(relDefUIDs);
        }
        return null;
    }

    @Override
    public IObject createOrUpdateObjectByProperties(String pstrPropertiesArray, boolean pblnNeedTransaction) throws
            Exception {
        if (!StringUtils.isEmpty(pstrPropertiesArray)) {
            return this.createOrUpdateObjectByProperties(CommonUtility.converterPropertiesToItemDTOList(pstrPropertiesArray), pblnNeedTransaction);
        }
        return null;
    }

    @Override
    public IObject createOrUpdateObjectByProperties(List<ObjectItemDTO> pcolProperties,
                                                    boolean pblnNeedTransaction) throws Exception {
        if (!CommonUtility.hasValue(pcolProperties)) throw new Exception("未解析到对象的属性信息!");
        String lstrOBID = CommonUtility.getSpecialValueFromProperties(pcolProperties, propertyDefinitionType.OBID.toString());
        String lstrClassDefUID = CommonUtility.getSpecialValueFromProperties(pcolProperties, propertyDefinitionType.ClassDefinitionUID.toString());
        if (StringUtils.isEmpty(lstrClassDefUID)) throw new Exception("未解析到对象的对象定义信息!");
        if (pblnNeedTransaction) SchemaUtility.beginTransaction();
        IObject lobj;
        if (!StringUtils.isEmpty(lstrOBID)) {
            lobj = CIMContext.Instance.ProcessCache().getObjectByOBID(lstrOBID, lstrClassDefUID);
            if (lobj == null)
                throw new Exception("未找到OBID:" + lstrOBID + ",classDef:" + lstrClassDefUID + "的对象信息!");
            lobj.BeginUpdate();
            lobj.fillingProperties(pcolProperties, true);
            lobj.FinishUpdate();
        } else {
            lobj = SchemaUtility.newIObject(lstrClassDefUID, CommonUtility.getSpecialValueFromProperties(pcolProperties, propertyDefinitionType.Name.toString()), CommonUtility.getSpecialValueFromProperties(pcolProperties, propertyDefinitionType.Description.toString()), null, null);
            if (lobj == null)
                throw new Exception("创建对象失败,classDef:" + lstrClassDefUID);
            lobj.fillingProperties(pcolProperties, false);
            lobj.ClassDefinition().FinishCreate(lobj);
        }
        if (pblnNeedTransaction) SchemaUtility.commitTransaction();
        return lobj;
    }

    @Override
    public boolean deleteRelationship(String relDefUID, String obid) throws Exception {
        if (!StringUtils.isEmpty(relDefUID) && !StringUtils.isEmpty(obid)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, null, obid);
            CIMContext.Instance.QueryEngine().addPropertyForQuery(queryRequest, interfaceDefinitionType.IRel.toString(), propertyDefinitionType.RelDefUID.toString(), null, relDefUID);
            CIMContext.Instance.QueryEngine().setQueryForRelationship(queryRequest, true);
            IObject iObject = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
            if (iObject != null) {
                try {
                    CIMContext.Instance.Transaction().start();
                    iObject.Delete();
                    CIMContext.Instance.Transaction().commit();
                } catch (Exception exception) {
                    log.error("delete relationship failed", exception);
                    CIMContext.Instance.Transaction().rollBack();
                }
            } else
                throw new Exception("cannot find any relationship with " + relDefUID + " under " + obid);
        }
        return true;
    }

    @Override
    public LoaderReport loadObjectsByJSONObject(JSONObject jsonObject) throws Exception {
        if (jsonObject != null) {
            LoaderReport loaderReport = new LoaderReport();
            JSONArray jsonArray = jsonObject.getJSONArray(SchemaUtility.ITEMS);
            if (jsonArray != null && jsonArray.size() > 0) {
                //检索系统中存在的对象
                IObjectCollection lcolExistObjects = SchemaUtility.queryExistObjectsFormJSONArray(jsonArray, null);
                SchemaUtility.beginTransaction();
                SchemaUtility.createObjectsWithJSONArray(jsonArray, lcolExistObjects, loaderReport);
                SchemaUtility.commitTransaction();
                return loaderReport;
            }
        }
        return null;
    }

    protected boolean propertyCanBeUpdate(String propertyDefinitionUID) {
        if (!StringUtils.isEmpty(propertyDefinitionUID)) {
            return !propertyDefinitionType.PropertiesCannotBeUpdated().contains(propertyDefinitionUID);
        }
        return true;
    }

    protected String convertValueForExactQuery(String currentValue, String propertyDefinitionUID) throws Exception {
        if (currentValue != null) {
            if (currentValue.contains("*"))
                return currentValue;
            if (!StringUtils.isEmpty(propertyDefinitionUID)) {
                IObject scopedBy = CIMContext.Instance.ProcessCache().getScopedByForPropertyDefinition(propertyDefinitionUID);
                if (scopedBy.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.EnumListType.toString())) {
                    IObjectCollection entries = scopedBy.toInterface(IEnumListType.class).getEntries();
                    List<String> values = CommonUtility.toList(currentValue.split("\\|"));
                    if (entries != null) {
                        List<String> requiredValues = new ArrayList<>();
                        Iterator<IObject> r = entries.GetEnumerator();
                        while (r.hasNext()) {
                            IObject current = r.next();
                            if (values.stream().anyMatch(c -> c.equalsIgnoreCase(current.Name()))) {
                                values.remove(current.Name());
                                requiredValues.add(current.UID());
                            } else if (values.stream().anyMatch(c -> c.equalsIgnoreCase(current.Name() + "," + current.Description()))) {
                                values.remove(current.Name() + "," + current.Description());
                                requiredValues.add(current.UID());
                            } else if (values.stream().anyMatch(c -> c.equalsIgnoreCase(current.Description()))) {
                                values.remove(current.Description());
                                requiredValues.add(current.UID());
                            } else if (values.stream().anyMatch(c -> c.equalsIgnoreCase(current.UID()))) {
                                values.remove(current.UID());
                                requiredValues.add(current.UID());
                            }
                        }
                        if (CommonUtility.hasValue(values))
                            requiredValues.addAll(values);
                        currentValue = requiredValues.stream().distinct().collect(Collectors.joining("|"));
                    }
                } else if (scopedBy.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.UoMListType.toString())) {
                    log.error("currently not support for UoMListType value conversion");
                } else if (scopedBy.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.EnumListLevelType.toString())) {
                    log.error("currently not support for EnumListLevelType value conversion");
                }
            }
        }
        return currentValue;
    }


    @Override
    public void generateForms() throws Exception {
        IObjectCollection classDefs = this.getClassDefs();
        if (classDefs != null && classDefs.hasValue()) {
            CIMContext.Instance.Transaction().start();
            Iterator<IObject> iObjectIterator = classDefs.GetEnumerator();
            while (iObjectIterator.hasNext()) {
                IClassDef classDef = iObjectIterator.next().toInterface(IClassDef.class);
                IObjectCollection collection = classDef.generateForm();
            }
            CIMContext.Instance.Transaction().commit();
        }
    }

    /**
     * 根据关联关系一端和二端的uid删除关联关系
     *
     * @param relDef
     * @param uid1
     * @param uid2
     * @return
     */
    @Override
    public boolean deleteRelByUid(String relDef, String uid1, String uid2) {
        IQueryEngine iQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = iQueryEngine.start();
        iQueryEngine.setQueryForRelationship(queryRequest, true);
        iQueryEngine.addPropertyForQuery(queryRequest, "", propertyDefinitionType.RelDefUID.toString(), operator.equal, relDef);
        iQueryEngine.addPropertyForQuery(queryRequest, "", propertyDefinitionType.UID1.name(), operator.equal, uid1);
        iQueryEngine.addPropertyForQuery(queryRequest, "", propertyDefinitionType.UID2.name(), operator.equal, uid2);
        IObjectCollection query = iQueryEngine.query(queryRequest);
        if (null == query || !query.hasValue()) {
            throw new RuntimeException("未能获取到对应关联关系!relDef:" + relDef + ",uid1:" + uid1 + ",uid2:" + uid2 + ".");
        }
        Iterator<IObject> iObjectIterator = query.GetEnumerator();
        SchemaUtility.beginTransaction();
        while (iObjectIterator.hasNext()) {
            IObject next = iObjectIterator.next();
            try {
                next.Delete();
            } catch (Exception e) {
                throw new RuntimeException("删除对象失败!");
            }
        }
        try {
            SchemaUtility.commitTransaction();
        } catch (Exception e) {
            throw new RuntimeException("事务提交失败!");
        }
        return true;
    }

    /**
     * 通用根据Excel模板导入标准数据
     *
     * @param file
     * @return
     * @throws Exception
     */
    @Override
    public LoaderReport importDataByExcelTemplate(MultipartFile file, Boolean withSchemaUidRule) {
        // 默认为不使用
        if (null == withSchemaUidRule) {
            withSchemaUidRule = false;
        }
        log.info("【通用Excel模板导入】-开始解析Excel数据...");
        long l = System.currentTimeMillis();
        JSONObject jsonObject = null;
        try {
            jsonObject = ExcelUtility.importTemplateExcel(file);
        } catch (Exception e) {
            log.error("解析Excel数据异常!{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            throw new RuntimeException("解析Excel数据异常!" + ExceptionUtil.getSimpleMessage(e));
        }
        log.info("【通用Excel模板导入】-解析Excel数据完成,耗时{}ms.", System.currentTimeMillis() - l);

        log.info("【通用Excel模板导入】-开始给UID填充项目信息...");
        long l1 = System.currentTimeMillis();
        ICIMConfigurationItem configurationItem = null;
        try {
            configurationItem = CIMContext.Instance.getMyConfigurationItem(null);
        } catch (Exception e) {
            log.error("获取项目信息异常!{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            throw new RuntimeException("获取项目信息异常!" + ExceptionUtil.getSimpleMessage(e));
        }
        if (configurationItem == null) {
            throw new RuntimeException("未获取到有效的项目信息!");
        }
        try {
            SchemaUtility.resetJSONObjectUids(configurationItem.Name(), jsonObject, withSchemaUidRule);
        } catch (Exception e) {
            log.error("给UID填充项目信息异常!{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            throw new RuntimeException("给UID填充项目信息异常!" + ExceptionUtil.getSimpleMessage(e));
        }
        log.info("【通用Excel模板导入】-给UID填充项目信息结束,耗时{}ms.", System.currentTimeMillis() - l1);

        log.info("【通用Excel模板导入】-开始通过JSON写入数据...");
        long l2 = System.currentTimeMillis();
        LoaderReport loaderReport = null;
        try {
            loaderReport = this.loadObjectsByJSONObject(jsonObject);
            log.info("【通用Excel模板导入】-通过JSON写入数据结束,耗时{}ms.", System.currentTimeMillis() - l2);
        } catch (Exception e) {
            log.error("通过JSON写入数据异常!{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            throw new RuntimeException("通过JSON写入数据异常!" + ExceptionUtil.getSimpleMessage(e));
        }
        return loaderReport;
    }

    /**
     * 根据通用查询条件和Form导出数据到Excel
     *
     * @param jsonObject
     * @param response
     */
    @Override
    public void exportExcelByForm(JSONObject jsonObject, HttpServletResponse response) {

        int pageSize = ValueConversionUtility.toInteger(jsonObject.getString("pageSize"));
        int pageIndex = ValueConversionUtility.toInteger(jsonObject.getString("pageIndex"));
        String classDefinitionUID = jsonObject.getString("classDefinitionUID");
        if (StringUtils.isEmpty(classDefinitionUID)) {
            throw new RuntimeException("classDefinitionUID为必填参数,不可为空!");
        }
        FiltersParam filtersParam = new FiltersParam(jsonObject);
        OrderByParam orderByParam = new OrderByParam(jsonObject);
        String formPurpose = jsonObject.getString("formPurpose");
        if (StringUtils.isEmpty(formPurpose)) {
            // formPurpose默认为INFO
            formPurpose = ccm.server.enums.formPurpose.Info.toString();
        }
        // 获取form
        ObjectDTO formPopupTemplate = null;
        try {
            ICIMForm form = this.getForm(formPurpose, classDefinitionUID);
            if (null == form) {
                formPopupTemplate = this.generateDefaultPopup(classDefinitionUID);
            } else {
                formPopupTemplate = form.generatePopup(formPurpose);
            }
            if (null == formPopupTemplate) {
                throw new RuntimeException("未获取到对应的form!");
            }
        } catch (Exception e) {
            log.error("获取对应Form失败!classDefinitionUID:{},formPurpose:{},错误信息:{}.", classDefinitionUID, formPurpose, ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            throw new RuntimeException("获取对应Form失败!classDefinitionUID:" + classDefinitionUID + ",formPurpose:" + formPurpose + ".");
        }

        List<ObjectDTO> collections = new ArrayList<>();
        List<String> headerUIDList = new ArrayList<>();
        formPopupTemplate.getItems().forEach(s -> {
            if (!s.getDefUID().equals(propertyDefinitionType.OBID.name()) && !s.getDefUID().equals(propertyDefinitionType.ClassDefinitionUID.name())) {
                headerUIDList.add(s.getDefUID());
            }
        });
        Map<String, String> defUidToLabelMap = new HashMap<>();
        formPopupTemplate.getItems().forEach(o -> {
            defUidToLabelMap.put(o.getDefUID(), o.getLabel());
        });
        try {
            List<String> expansionPaths = formPopupTemplate.getExpansionPaths();
            IObjectCollection objectCollection = this.generalQuery(classDefinitionUID, pageIndex, pageSize, orderByParam.getOrderByWrappers(), filtersParam.getFilters(), expansionPaths);
            if (objectCollection != null && objectCollection.hasValue()) {

                Iterator<IObject> objectIterator = objectCollection.GetEnumerator();
                while (objectIterator.hasNext()) {
                    IObject next = objectIterator.next();
                    ObjectDTO currentForm = formPopupTemplate.copyTo();
                    next.fillingForObjectDTO(currentForm);
                    if (CommonUtility.isDocument(next.ClassDefinitionUID())) {
                        //特殊处理文档属性
                        SchemaUtility.fillDocumentPropForObjectDTO(currentForm, next);
                    }
                    collections.add(currentForm);
                }
            } else {
                throw new RuntimeException("未查询到数据!");
            }
        } catch (Exception exception) {
            throw new RuntimeException(ExceptionUtil.getSimpleMessage(exception));
        }

        try (Workbook workbook = new XSSFWorkbook();
             OutputStream os = response.getOutputStream()) {
            Sheet sheet = workbook.createSheet();
            Row row0 = sheet.createRow(0);
            for (int i = 0; i < headerUIDList.size(); i++) {
                Cell cell = row0.createCell(i);
                cell.setCellValue(defUidToLabelMap.get(headerUIDList.get(i)));
            }
            for (int i = 0; i < collections.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                for (int j = 0; j < headerUIDList.size(); j++) {
                    Cell cell = dataRow.createCell(j);
                    cell.setCellValue(collections.get(i).toGetValue(headerUIDList.get(j)));
                }
            }
            workbook.write(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateUserInfo(String userName, JSONArray properties) throws Exception {
        if (StringUtils.isEmpty(userName))
            userName = CIMContext.Instance.getLoginUserName();
        ICIMUser icimUser = null;
        IObject currentUser = CIMContext.Instance.getLoginUser(userName);
        if (currentUser != null) {
            icimUser = currentUser.toInterface(ICIMUser.class);
            if (properties != null) {
                CIMContext.Instance.Transaction().start();
                icimUser.BeginUpdate();
                for (int i = 0; i < properties.size(); i++) {
                    JSONObject jsonObject = properties.getJSONObject(i);
                    String property = jsonObject.getString("property");
                    Object value = jsonObject.get("value");
                    icimUser.setValue(property, value);
                }
                icimUser.FinishUpdate();
                CIMContext.Instance.Transaction().commit();
            }
        }
    }

    @Override
    public List<OptionItemDTO> getSearchRelationshipOptions(JSONObject param) throws Exception {
        String relDirection = param.getString("relDirection");
        String defUid = param.getString("defUID");
        if (StringUtils.isEmpty(defUid) || StringUtils.isEmpty(relDirection)) {
            throw new Exception("参数无效, 不能为空值");
        }
        String actualDefinition = CommonUtility.toActualDefinition(defUid);
        if (actualDefinition.contains(".")) {
            actualDefinition = actualDefinition.split("\\.")[0];
        }
        IObject item = CIMContext.Instance.ProcessCache().item(actualDefinition, domainInfo.SCHEMA.toString(), false);
        if (null == item) {
            throw new Exception("无效的定义:" + actualDefinition);
        }
        IRelDef relDef = item.toInterface(IRelDef.class);
        return relDef.generateOptions(ccm.server.enums.relDirection.valueOf(relDirection));
    }
}
