package ccm.server.utils;

import ccm.server.context.CIMContext;
import ccm.server.convert.impl.ValueConvertService;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.*;
import ccm.server.excel.util.ExcelUtility;
import ccm.server.helper.HardCodeHelper;
import ccm.server.model.LoaderReport;
import ccm.server.models.query.QueryRequest;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.*;
import ccm.server.util.AssemblyUtility;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
public class SchemaUtility {

    public static final String CLASS_DEFINITION_UID = "classDefinitionUID";
    public static final String INTERFACES = "interfaces";
    public static final String PROPERTIES = "properties";
    public static final String HEADER = "header";
    public static final String ITEMS = "items";
    public static final String CONTAINERS = "containers";

    private static final String[] scanPackages = new String[]{
            "ccm.server.schema.interfaces.defaults",
            "ccm.server.schema.interfaces.generated",
            "ccm.server.schema.interfaces",
            "ccm.server.module.schema.interfaces.defaults",
            "ccm.server.module.schema.interfaces.generated",
            "ccm.server.module.schema.interfaces",
            "ccm.server.schema.model",
            "ccm.server.schema.classes.ClassDef",
            "ccm.server.schema.classes.InterfaceDef",
            "ccm.server.schema.classes.PropertyDef",
            "ccm.server.schema.classes.Rel",
            "ccm.server.schema.classes.RelDef"
    };

    private static final ConcurrentMap<String, Class<?>> typeCache = new ConcurrentHashMap<>();

    private static Map<String, Class<?>> types = buildTypeCache();

    public static Map<String, Class<?>> Types() {
        if (types == null)
            types = buildTypeCache();
        return types;
    }

    private static boolean isValidExtension(Class<?> aClass) {
        if (aClass != null) {
            if (Modifier.isAbstract(aClass.getModifiers()))
                return false;
            if (InterfaceBase.class.isAssignableFrom(aClass))
                return true;
            if (ClassBase.class.isAssignableFrom(aClass))
                return true;
            if (PropertyBase.class.isAssignableFrom(aClass))
                return true;
        }
        return false;
    }

    private static Map<String, Class<?>> buildTypeCache() {
        Map<String, Class<?>> result = new HashMap<>();
        if (CommonUtility.hasValue(scanPackages)) {
            for (String scanPackage : scanPackages) {
                Set<Class<?>> classSet = AssemblyUtility.getClassSet(scanPackage);
                if (CommonUtility.hasValue(classSet)) {
                    for (Class<?> aClass : classSet) {
                        if (isValidExtension(aClass))
                            result.putIfAbsent(aClass.getName(), aClass);
                    }
                }
            }
        }
        return result;
    }


    public static String getEnumEnumName(@NotNull String enumEnumUid) throws Exception {
        if (!StringUtils.isEmpty(enumEnumUid)) {
            IObject enumEnum = CIMContext.Instance.ProcessCache().queryObjectsByUIDAndClassDefinition(enumEnumUid, domainInfo.SCHEMA.toString(), classDefinitionType.EnumEnum.toString());
            if (enumEnum == null)
                throw new Exception("未找到UID:" + enumEnumUid + "的EnumEnum对象!");
            return enumEnum.Name();
        }
        return "";
    }

    public static IObjectCollection getObjectsByClassDef(String pstrClassDef) {
        if (!StringUtils.isEmpty(pstrClassDef)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, pstrClassDef);
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    public static Class<?> getTopLevelType(String pstrType) {
        Class<?> result = typeCache.getOrDefault(pstrType, null);
        if (result == null) {
            Class<?> topLevelType = null;
            String lstrSuffix = "";
            int index = 0;
            while (true) {
                Class<?> tempType = getType(pstrType + lstrSuffix);
                if (tempType == null)
                    break;
                topLevelType = tempType;
                index++;
                lstrSuffix = Integer.toString(index);
            }
            result = topLevelType;
            if (result != null)
                typeCache.putIfAbsent(pstrType, result);
        }
        return result;
    }

    public static <T> List<T> toObjectList(IObjectCollection objectCollection, Class<T> tClass) {
        if (hasValue(objectCollection)) {
            List<T> lcolResult = new ArrayList<>();
            Iterator<IObject> e = objectCollection.GetEnumerator();
            while (e.hasNext()) {
                IObject object = e.next();
                lcolResult.add(object.toInterface(tClass));
            }
            return lcolResult;
        }
        return null;
    }

    public static List<IObject> toObjectList(IObjectCollection objectCollection) {
        if (hasValue(objectCollection)) {
            List<IObject> lcolResult = new ArrayList<>();
            Iterator<IObject> e = objectCollection.GetEnumerator();
            while (e.hasNext()) {
                lcolResult.add(e.next());
            }
            return lcolResult;
        }
        return null;
    }

    public static Class<?> getType(String pstrType) {
        return Types().getOrDefault(pstrType, null);
    }

    public static boolean hasValue(IObjectCollection source) {
        return source != null && source.hasValue();
    }

    public static IRel newRelationship(String relDef, String uid1, String domain1, String name1, String obid1, String classDefinition1, String uid2, String domain2, String name2, String obid2, String classDefinition2, boolean isRequired) throws Exception {
        if (StringUtils.isEmpty(relDef))
            throw new Exception("invalid relationship definition as it is null");

        IClassDef relClassDef = CIMContext.Instance.ProcessCache().Rel();
        if (relClassDef == null)
            throw new Exception("invalid Rel class definition as it is not initialized in system");

        IObject relationship = relClassDef.BeginCreate(true);
        IRel rel = relationship.toInterface(IRel.class);
        rel.setOBID1(obid1);
        rel.setOBID2(obid2);
        rel.setDomainUID1(domain1);
        rel.setDomainUID2(domain2);
        rel.setUID1(uid1);
        rel.setUID2(uid2);
        rel.setName1(name1);
        rel.setName2(name2);
        rel.setRelDefUID(relDef);
        rel.setClassDefinitionUID1(classDefinition1);
        rel.setClassDefinitionUID2(classDefinition2);
        rel.setIsRequired(isRequired);
        rel.setOBID(CIMContext.Instance.generateOBIDForRel());
        relationship.setClassDefinitionUID(HardCodeHelper.CLASSDEF_REL);
        return rel;
    }

    public static IRel newRelationship(String relDef, IObject end1, IObject end2, boolean pblnIsRequired) throws Exception {
        if (StringUtils.isEmpty(relDef))
            throw new Exception("invalid relationship definition as it is null");

        IObject relDef1 = CIMContext.Instance.ProcessCache().item(relDef, null, false);
        if (relDef1 == null)
            throw new Exception("invalid relationship definition as it is not valid");

        if (end1 == null || end2 == null)
            throw new Exception("invalid end object for relationship construction");

        IClassDef relClassDef = CIMContext.Instance.ProcessCache().Rel();
        if (relClassDef == null)
            throw new Exception("invalid Rel class definition as it is not initialized in system");

        IObject relationship = relClassDef.BeginCreate(true);
        IRel rel = relationship.toInterface(IRel.class);
        rel.setUID1(end1.UID());
        rel.setUID2(end2.UID());
        rel.setOBID1(end1.OBID());
        rel.setOBID2(end2.OBID());
        rel.setDomainUID1(end1.DomainUID());
        rel.setDomainUID2(end2.DomainUID());
        rel.setRelDefUID(relDef);
        rel.setName1(end1.Name());
        rel.setName2(end2.Name());
        rel.setClassDefinitionUID1(end1.ClassDefinitionUID());
        rel.setClassDefinitionUID2(end2.ClassDefinitionUID());
        rel.setIsRequired(pblnIsRequired);
        rel.setOBID(CIMContext.Instance.generateOBIDForRel());
        end1.GetEnd1Relationships().add(rel);
        end2.GetEnd2Relationships().add(rel);
        relationship.setClassDefinitionUID(HardCodeHelper.CLASSDEF_REL);
        return rel;
    }

    /*
     * @Descriptions :  查询系统中已经存在的对象
     * @Author: Chen Jing
     * @Date: 2022/4/24 14:56
     * @param jsonArray 保存所有对象JSON格式的数组
     * @param pcolExcludeClassDef 不需要查询的对象类型定义集合
     * @Return:ccm.server.schema.collections.IObjectCollection
     */
    public static IObjectCollection queryExistObjectsFormJSONArray(JSONArray jsonArray, List<String> excludeClassDefs) throws Exception {
        IObjectCollection lcolContainer = new ObjectCollection();
        if (jsonArray != null) {
            List<JSONObject> jsonObjects = CommonUtility.toJSONObjList(jsonArray);
            Map<String, List<JSONObject>> collect = jsonObjects.stream().collect(Collectors.groupingBy(r -> r.getString(SchemaUtility.CLASS_DEFINITION_UID)));
            for (Map.Entry<String, List<JSONObject>> entry : collect.entrySet()) {
                String classDefinitionUid = entry.getKey();
                if (!classDefinitionType.Rel.toString().equals(classDefinitionUid)) {
                    if (CommonUtility.hasValue(excludeClassDefs) && excludeClassDefs.contains(classDefinitionUid))
                        continue;
                    IObjectCollection objectCollection = CIMContext.Instance.ProcessCache().queryObjectsByUIDAndClassDefinition(entry.getValue().stream().map(r -> SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.UID.toString())).collect(Collectors.toList()), classDefinitionUid);
                    if (objectCollection.hasValue()) {
                        lcolContainer.addRangeUniquely(objectCollection);
                    }
                }
            }
        }
        return lcolContainer;
    }

    /*
     * @Descriptions : 从对象属性集合中获取指定属性定义的属性值
     * @Author: Chen Jing
     * @Date: 2022/4/24 14:59
     * @param pcolProps 属性集合
     * @param pstrPropDefUID  需要获取的属性定义
     * @Return:java.lang.Object
     */
    public static Object getSpecialPropValue(@NotNull List<ObjectItemDTO> properties, @NotNull String propertyDefinitionUid) {
        ObjectItemDTO objectItemDTO = properties.stream().filter(r -> r.getDefUID().equalsIgnoreCase(propertyDefinitionUid)).findFirst().orElse(null);
        if (objectItemDTO != null) {
            return objectItemDTO.getDisplayValue();
        }
        return null;
    }

    /*
     * @Descriptions : 判断类型定义是否设置了UID生成规则
     * @Author: Chen Jing
     * @Date: 2022/4/24 15:00
     * @param pstrClassDefUID 对象类型定义
     * @Return:boolean
     */
    public static boolean hasSystemIdPattern(@NotNull String pstrClassDefUID) {
        IClassDef iClassDef = CIMContext.Instance.ProcessCache().item(pstrClassDefUID, domainInfo.SCHEMA.toString(), false).toInterface(IClassDef.class);
        if (iClassDef != null) {
            return !StringUtils.isEmpty(iClassDef.SystemIDPattern());
        }
        return false;
    }

    /*
     * @Descriptions : 创建对象
     * @Author: Chen Ning
     * @Date: 2022/4/24 15:01
     * @param classDef 类型定义 必填
     * @param name 对象名称
     * @param description 描述
     * @param domainUID 域UID 可为NULL, NULL时自动设置为类型定义关联的Domain
     * @param uid  可为NULL NULL时自动根据规则生成
     * @Return:ccm.server.schema.interfaces.IObject
     */
    public static IObject newIObject(String classDefinitionUid, String name, String description, String domainUID, String uid) throws Exception {
        if (!StringUtils.isEmpty(classDefinitionUid)) {
            boolean blnInTransaction = CIMContext.Instance.Transaction().inTransaction();
            if (!blnInTransaction)
                throw new Exception("transaction is not started, you have to start transaction firstly before DML operation");

            IClassDef classDef = CIMContext.Instance.ProcessCache().item(classDefinitionUid, domainInfo.SCHEMA.toString(), false, IClassDef.class);
            if (classDef == null)
                throw new Exception("invalid class definition " + classDefinitionUid + " found in system");

            IObject instantiate = classDef.BeginCreate(true);
            if (StringUtils.isEmpty(instantiate.OBID()))
                instantiate.setOBID(CIMContext.Instance.generateOBIDForObject());
            instantiate.setName(name);
            instantiate.setDescription(description);
            if (StringUtils.isEmpty(domainUID))
                domainUID = CIMContext.Instance.ProcessCache().getDomainUIDForClassDefinition(classDefinitionUid);
            instantiate.setDomainUID(domainUID);
            if (!StringUtils.isEmpty(uid))
                instantiate.setUID(uid);
            instantiate.setCreationUser(CIMContext.Instance.getLoginUserName());
            return instantiate;
        }
        return null;
    }

    /*
     * @Descriptions : 根据类型定义生成UID
     * @Author: Chen Jing
     * @Date: 2022/4/24 15:03
     * @param jsonObject JSON格式的对象
     * @Return:java.lang.String
     */
    public static String generateUIDByJSONObject(@NotNull JSONObject jsonObject, String configName, boolean withSchemaUidRule) {
        try {
            String classDefinitionUid = jsonObject.getString(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID);
            JSONObject properties = jsonObject.getJSONObject(CommonUtility.JSON_FORMAT_PROPERTIES);
            return generateUIDByClassDefAndProps(classDefinitionUid, properties, configName, withSchemaUidRule);
        } catch (Exception ex) {
            log.error("根据类型定义生成UID异常!异常信息:{}", ExceptionUtil.getMessage(ex), ExceptionUtil.getRootCause(ex));
            throw new RuntimeException("根据类型定义生成UID异常!异常信息:" + ExceptionUtil.getSimpleMessage(ex));
        }
    }

    /*
     * pobjDocJson:所属文档的JSON格式对象
     * jsonObject:设计对象的JSON格式对象
     */
    public static String generateUIDByJSONObjectForDesignObject(@NotNull JSONObject jsonObject, @NotNull JSONObject pobjDocJson, String configName) {
        try {
            String classDefinitionUid = jsonObject.getString(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID);
            JSONObject props = jsonObject.getJSONObject(CommonUtility.JSON_FORMAT_PROPERTIES);
            String lstrDesignObjUniqueKey = generateUIDByClassDefAndProps(classDefinitionUid, props, configName, true);
            String lstrDocClassDef = pobjDocJson.getString(CLASS_DEFINITION_UID);
            IClassDef classDef = CIMContext.Instance.ProcessCache().item(lstrDocClassDef, domainInfo.SCHEMA.toString(), false).toInterface(IClassDef.class);
            if (classDef == null) throw new Exception(lstrDocClassDef + "未在缓存中找到!");
            String systemIDPattern = classDef.SystemIDPattern();
            if (StringUtils.isEmpty(systemIDPattern)) {
                throw new Exception("CIMDocumentMaster的系统唯一编码生成规则未配置!");
            }
            JSONObject docProps = pobjDocJson.getJSONObject(PROPERTIES);
            String[] larrUniquePattern = systemIDPattern.split(",");
            StringBuilder stringBuilder = new StringBuilder();
            for (String lstrPattern : larrUniquePattern) {
                if (docProps.containsKey(lstrPattern) && !lstrPattern.equalsIgnoreCase("NAME")) {
                    stringBuilder.append("_").append(getPropertyValueFromJSONPropsForUniqueKey(lstrPattern, docProps));
                }
            }
            if (!lstrDesignObjUniqueKey.endsWith(stringBuilder.toString())) {
                return lstrDesignObjUniqueKey + stringBuilder;
            }
            return lstrDesignObjUniqueKey;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
        }
        return "";
    }

    public static boolean objContainsProp(@NotNull IObject pobjSource, @NotNull String pstrPropDefUID) {
        boolean result = false;
        Iterator<Map.Entry<String, IInterface>> e = pobjSource.Interfaces().GetEnumerator();
        while (e.hasNext()) {
            IInterface lobjInterface = e.next().getValue();
            if (lobjInterface.Properties().hasProperty(pstrPropDefUID)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static String generateUIDByJSONObjectForDesignObject(String configName, @NotNull JSONObject jsonObject, @NotNull IObject pobjDocMaster) {
        try {
            String lstrClassDef = jsonObject.getString(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID);
            JSONObject props = jsonObject.getJSONObject(CommonUtility.JSON_FORMAT_PROPERTIES);
            String lstrDesignObjUniqueKey = generateUIDByClassDefAndProps(lstrClassDef, props, configName, true);
            String lstrDocClassDef = pobjDocMaster.ClassDefinitionUID();
            IClassDef classDef = CIMContext.Instance.ProcessCache().item(lstrDocClassDef, domainInfo.SCHEMA.toString(), false
            ).toInterface(IClassDef.class);
            if (classDef == null) throw new Exception(lstrDocClassDef + "未在缓存中找到!");
            String systemIDPattern = classDef.SystemIDPattern();
            if (StringUtils.isEmpty(systemIDPattern)) {
                throw new Exception("CIMDocumentMaster的系统唯一编码生成规则未配置!");
            }
            String[] larrUniquePattern = systemIDPattern.split(",");
            StringBuilder stringBuilder = new StringBuilder();
            for (String lstrPattern : larrUniquePattern) {
                if (!lstrPattern.equalsIgnoreCase("NAME") && objContainsProp(pobjDocMaster, lstrPattern)) {
                    IProperty property = pobjDocMaster.getProperty(lstrPattern);
                    if (property != null) {
                        stringBuilder.append("_").append(property.Value().toString());
                    }
                }
            }
            return lstrDesignObjUniqueKey + stringBuilder.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
        }
        return "";
    }

    public static String generateUIDByClassDefAndProps(@NotNull String pstrClassDef, @NotNull JSONObject props, String configName, boolean withSchemaUidRule) {
        try {
            String uid;
            IClassDef classDef = CIMContext.Instance.ProcessCache().item(pstrClassDef, domainInfo.SCHEMA.toString(), false).toInterface(IClassDef.class);
            if (classDef == null) throw new Exception(pstrClassDef + "未在缓存中找到!");
            String systemIDPattern = classDef.SystemIDPattern();
            if (withSchemaUidRule && !StringUtils.isEmpty(systemIDPattern)) {
                List<String> parts = new ArrayList<>();
                String[] larrUIDPattern = systemIDPattern.split(",");
                for (String lstrPattern : larrUIDPattern) {
                    if (props.containsKey(lstrPattern)) {
                        parts.add(getPropertyValueFromJSONPropsForUniqueKey(lstrPattern, props));
                    } else if (lstrPattern.equalsIgnoreCase("CURRENTPROJECT")) {
                        ICIMConfigurationItem currentScope = CIMContext.Instance.getMyConfigurationItem(null);
                        if (currentScope == null)
                            parts.add("");
                        else
                            parts.add(currentScope.UID());
                    } else
                        parts.add(lstrPattern);
                }
                uid = String.join("_", parts);
            } else {
                if (props.containsKey(propertyDefinitionType.UID.toString())) {
                    uid = props.getString(propertyDefinitionType.UID.toString());
                } else {
                    uid = CommonUtility.generateUUID();
                }
            }
            if (CIMContext.Instance.ProcessCache().isDefinitionUnderConfigControl(pstrClassDef)) {
                if (!uid.contains(configName)) {
                    uid = uid + "_" + configName;
                }
            }
            return uid;
        } catch (Exception ex) {
            log.error("根据类型定义生成UID异常!异常信息:{}", ExceptionUtil.getMessage(ex), ExceptionUtil.getRootCause(ex));
            throw new RuntimeException("根据类型定义生成UID异常!异常信息:" + ExceptionUtil.getSimpleMessage(ex));
        }
    }

    public static String getPropertyValueFromJSONPropsForUniqueKey(@NotNull String pstrPattern, JSONObject props) throws Exception {
        IPropertyDef propDef = CIMContext.Instance.ProcessCache().item(pstrPattern, domainInfo.SCHEMA.toString(), false).toInterface(IPropertyDef.class);
        if (propDef == null) {
            throw new Exception(pstrPattern + "未在系统中定义!");
        }
        String lstrResult = "";
        IPropertyType scopedByPropertyType = propDef.getScopedByPropertyType();
        if (propertyValueType.EnumListType.toString().equals(scopedByPropertyType.ClassDefinitionUID())) {
            String lstrValue = props.getString(pstrPattern);
            if (!StringUtils.isEmpty(lstrValue)) {
                IEnumListType enumListType = propDef.getScopedBy().toInterface(IEnumListType.class);
                IObjectCollection entries = enumListType.getEntries();
                if (SchemaUtility.hasValue(entries)) {
                    Iterator<IObject> e = entries.GetEnumerator();
                    while (e.hasNext()) {
                        IObject lobjEnum = e.next();
                        if (lstrValue.contains(lobjEnum.Name()) || lstrValue.contains(lobjEnum.Description())) {
                            lstrResult = lobjEnum.UID();
                        }
                    }
                }
            }
        } else {
            lstrResult = props.getString(pstrPattern);
        }
        return lstrResult;
    }

    @SneakyThrows
    public static void beginTransaction() {
        if (!CIMContext.Instance.Transaction().inTransaction())
            CIMContext.Instance.Transaction().start();
    }

    public static void commitTransaction() throws Exception {
        if (CIMContext.Instance.Transaction().inTransaction())
            CIMContext.Instance.Transaction().commit();
    }

    public static void rollBackTransaction() {
        if (CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().rollBack();
        }
    }

    public static IObject getObjectWithInterfaceDefAndCriteria(String interfaceDef, String nameCriteria) {
        if (!StringUtils.isEmpty(interfaceDef)) {
            IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = queryEngine.start();
            queryEngine.addInterfaceForQuery(queryRequest, interfaceDef);
            if (!StringUtils.isEmpty(nameCriteria) && !"*".equals(nameCriteria))
                queryEngine.addNameForQuery(queryRequest, operator.equal, nameCriteria);
            try {
                return queryEngine.queryOne(queryRequest);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
        return null;
    }

    public static IObjectCollection getObjectsWithInterfaceDefAndCriteria(String interfaceDef, String nameCriteria) {
        if (!StringUtils.isEmpty(interfaceDef)) {
            IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = queryEngine.start();
            queryEngine.addInterfaceForQuery(queryRequest, interfaceDef);
            if (!StringUtils.isEmpty(nameCriteria) && !"*".equals(nameCriteria))
                queryEngine.addNameForQuery(queryRequest, operator.equal, nameCriteria);
            try {
                return queryEngine.query(queryRequest);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
        return null;
    }

    public static IObject getObjectByUIDAndClassDef(@NotNull String uid, @NotNull String classDefinitionUid) {
        IObject item = CIMContext.Instance.ProcessCache().item(uid, null, false);
        if (item != null && item.fromDb()) {
            if (!StringUtils.isEmpty(classDefinitionUid)) {
                if (item.ClassDefinitionUID().equalsIgnoreCase(classDefinitionUid)) {
                    return item;
                }
            } else
                return item;
        }

        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, operator.equal, uid);
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionUid);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    public static IObjectCollection getObjectByUIDsAndClassDef(@NotNull List<String> uids, @NotNull String classDefinitionUid) {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, operator.in, String.join(",", uids));
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionUid);
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    public static IObject getObjectByUid(String uid, String interfaceDef) {
        if (!StringUtils.isEmpty(uid)) {
            IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = queryEngine.start();
            if (!StringUtils.isEmpty(interfaceDef))
                queryEngine.addInterfaceForQuery(queryRequest, interfaceDef);
            else
                queryEngine.addInterfaceForQuery(queryRequest, interfaceDefinitionType.IObject.toString());
            queryEngine.addUIDForQuery(queryRequest, operator.equal, uid);
            if (interfaceDef.equals(IRel.class.getSimpleName()))
                queryEngine.setQueryForRelationship(queryRequest, true);
            try {
                return queryEngine.queryOne(queryRequest);
            } catch (Exception ex) {
                log.error(ex.toString());
            }
        }
        return null;
    }

    public static IObject getObjectByUid(String uid) {
        return getObjectByUid(uid, interfaceDefinitionType.IObject.toString());
    }

    public static IObjectCollection queryObjsWithInterfaceDef(String interfaceDef, String nameCriteria, PageRequest pageRequest) {
        if (!StringUtils.isEmpty(interfaceDef)) {
            IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = queryEngine.start();
            queryEngine.addInterfaceForQuery(queryRequest, interfaceDef);
            if (pageRequest != null)
                queryEngine.setPageRequest(queryRequest, pageRequest);
            if (!StringUtils.isEmpty(nameCriteria) && !"*".equals(nameCriteria))
                queryEngine.addPropertyForQuery(queryRequest, interfaceDefinitionType.IObject.toString(), propertyDefinitionType.Name.toString(), operator.like, nameCriteria);
            return queryEngine.query(queryRequest);
        }
        return null;
    }

    public static void setObjPropertyValue(IObject object, String interfaceDef, String propertyDef, Object value, boolean isRequired, boolean needCommit) throws Exception {
        if (object != null && !StringUtils.isEmpty(interfaceDef) && !StringUtils.isEmpty(propertyDef)) {
            if (isRequired) {
                if (validateSchemaDef(interfaceDef))
                    throw new Exception(interfaceDef + " is  inValid interfaceDef");
                SchemaUtility.beginTransaction();
                IInterface iInterfaceItem = object.Interfaces().item(interfaceDef, true);
                if (validateSchemaDef(propertyDef))
                    throw new Exception(propertyDef + " is inValid propertyDef");
                IProperty iProperty = iInterfaceItem.Properties().item(propertyDef, true);
                iProperty.setValue(value);
                if (needCommit)
                    SchemaUtility.commitTransaction();
            } else {
                if (object.Interfaces().hasInterface(interfaceDef)) {
                    IInterface iInterface = object.Interfaces().get(interfaceDef);
                    if (iInterface.Properties().hasProperty(propertyDef)) {
                        IProperty iProperty = iInterface.Properties().get(propertyDef);
                        iProperty.setValue(value);
                    }
                }
            }
        }
    }


    public static boolean validatePropDefNeedFilling(String propertyDefinitionUid) {
        //!propertyDefinitionType.Name.toString().equals(propertyDefinitionUid) name 属性可更新 2023/3/26 CHEN JING
        if (!StringUtils.isEmpty(propertyDefinitionUid)) {
            return !propertyDefinitionType.UID.toString().equals(propertyDefinitionUid) &&
                    !propertyDefinitionType.OBID.toString().equals(propertyDefinitionUid) &&
                    !propertyDefinitionType.DomainUID.toString().equals(propertyDefinitionUid) &&
                    !propertyDefinitionType.ContainerID.toString().equals(propertyDefinitionUid) &&
                    !"SystemUpgradeDeleteData".equalsIgnoreCase(propertyDefinitionUid);
        }
        return true;
    }


    public static String getSpecialProperty(String pstrPropertyDef, List<Map.Entry<String, Object>> properties) {
        if (properties != null && properties.size() > 0 && !StringUtils.isEmpty(pstrPropertyDef)) {
            for (Map.Entry<String, Object> property : properties) {
                if (property.getKey().equalsIgnoreCase(pstrPropertyDef)) {
                    Object value = property.getValue();
                    if (value != null)
                        return value.toString();
                }
            }
        }
        return "";
    }

    public static JSONObject getJSONObjByUidFromJSONArray(List<JSONObject> jsonObjects, String uid) {
        if (CommonUtility.hasValue(jsonObjects) && !StringUtils.isEmpty(uid)) {
            return jsonObjects.stream().filter(r -> getSpecialPropertyValue(r, propertyDefinitionType.UID.toString()).equalsIgnoreCase(uid)).findFirst().orElse(null);
        }
        return null;
    }

    public static String getSpecialPropertyValue(JSONObject jsonObject, String propertyDef) {
        if (jsonObject != null && !StringUtils.isEmpty(propertyDef)) {
            JSONObject properties = jsonObject.getJSONObject(PROPERTIES);
            if (properties != null && properties.containsKey(propertyDef)) {
                return properties.getString(propertyDef);
            }
        }
        return "";
    }

    public static JSONObject getDocumentJSONObjectFromJSONArray(JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.size() > 0) {
            return jsonArray.stream().map(r -> (JSONObject) r).filter(r -> classDefinitionType.CIMDocumentMaster.toString().equals(r.getString(CLASS_DEFINITION_UID))).findFirst().orElse(null);
        }
        return null;
    }

    public static String[] parseInterfaces(JSONObject jsonObject) {
        List<String> result = new ArrayList<>();
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.getJSONArray(SchemaUtility.INTERFACES);
            if (jsonArray != null && jsonArray.size() > 0) {
                for (Object o : jsonArray) {
                    result.add(o.toString());
                }
            }
        }
        return CommonUtility.convertToArray(result);
    }

    public static List<Map.Entry<String, Object>> parseProperties(JSONObject jsonObject) {
        List<Map.Entry<String, Object>> result = new ArrayList<>();
        if (jsonObject != null) {
            JSONObject jsonObject1 = jsonObject.getJSONObject(SchemaUtility.PROPERTIES);
            if (jsonObject1 != null) {
                for (Map.Entry<String, Object> stringObjectEntry : jsonObject1.entrySet()) {
                    Map.Entry<String, Object> stringObjectSimpleEntry = new AbstractMap.SimpleEntry<>(stringObjectEntry.getKey(), stringObjectEntry.getValue());
                    result.add(stringObjectSimpleEntry);
                }
            }
        }
        return result;
    }

    public static void verifyInterfaces(String[] parrInterfaces) throws Exception {
        if (parrInterfaces != null && parrInterfaces.length > 0) {
            for (String anInterface : parrInterfaces) {
                IObject item = CIMContext.Instance.ProcessCache().item(anInterface, domainInfo.SCHEMA.toString());
                if (item == null)
                    throw new Exception("invalid interface definition:" + anInterface);
            }
        }
    }

    public static void verifyInterfaces(List<String> pcolInterfaces) throws Exception {
        if (pcolInterfaces != null && pcolInterfaces.size() > 0) {
            for (String anInterface : pcolInterfaces) {
                IObject item = CIMContext.Instance.ProcessCache().item(anInterface, domainInfo.SCHEMA.toString());
                if (item == null)
                    throw new Exception("invalid interface definition:" + anInterface);
            }
        }
    }

    public static void verifyClassDef(String classDefUID) throws Exception {
        if (!StringUtils.isEmpty(classDefUID)) {
            IObject item = CIMContext.Instance.ProcessCache().item(classDefUID, domainInfo.SCHEMA.toString());
            if (item == null) {
                throw new Exception("invalid class definition:" + classDefUID);
            }
        }
    }

    public static void verifyClassDefs(List<String> pcolClassDefs) throws Exception {
        if (CommonUtility.hasValue(pcolClassDefs)) {
            for (String classDef : pcolClassDefs) {
                verifyClassDef(classDef);
            }
        }
    }

    public static void verifyProperties(List<String> pcolPropertyDefs) throws Exception {
        if (pcolPropertyDefs != null && pcolPropertyDefs.size() > 0) {
            for (String propertyDef : pcolPropertyDefs) {
                if (propertyDef.equalsIgnoreCase("SystemUpgradeDeleteData"))
                    continue;
                IObject item = CIMContext.Instance.ProcessCache().item(propertyDef, domainInfo.SCHEMA.toString());
                if (item == null)
                    throw new Exception("invalid property definition:" + propertyDef);
            }
        }
    }

    public static IObject verifyPropertyDef(@NotNull String pstrPropDef) {
        try {
            return CIMContext.Instance.ProcessCache().item(pstrPropDef, domainInfo.SCHEMA.toString());
        } catch (Exception ex) {
            return null;
        }
    }

    public static void verifySchema(String classDefinitionUid, String[] interfaces, List<Map.Entry<String, Object>> properties) throws Exception {
        verifyClassDef(classDefinitionUid);
        verifyInterfaces(interfaces);
        verifyProperties(properties.stream().map(Map.Entry::getKey).collect(Collectors.toList()));
    }

    public static Object getSpecialPropertyValue(IObject object, String interfaceDef, String propertyDef) {
        if (!StringUtils.isEmpty(interfaceDef) && !StringUtils.isEmpty(propertyDef) && object != null) {
            if (object.Interfaces().hasInterface(interfaceDef)) {
                IInterface iInterface = object.Interfaces().get(interfaceDef);
                if (iInterface.Properties().hasProperty(propertyDef)) {
                    return iInterface.Properties().get(propertyDef).Value();
                }
            }
        }
        return null;
    }

    public static void updateObjPropertyValue(IObject obj, String interfaceDef, String propertyDef, Object newValue) throws Exception {
        if (obj != null && !StringUtils.isEmpty(interfaceDef) && !StringUtils.isEmpty(propertyDef)) {
            obj.Interfaces().item(interfaceDef, true).Properties().item(propertyDef, true).setValue(newValue);
        }
    }

    public static IRel createRelationShip(@NotNull String pstrRelDefUID, @NotNull IObject pobjEnd1, @NotNull IObject pobjEnd2, boolean pblnNeedTransaction) throws Exception {
        if (pblnNeedTransaction)
            SchemaUtility.beginTransaction();
        IRel iRel = newRelationship(pstrRelDefUID, pobjEnd1, pobjEnd2, true);
        iRel.ClassDefinition().FinishCreate(iRel);
        if (pblnNeedTransaction)
            SchemaUtility.commitTransaction();
        return iRel;
    }

    public static boolean validateSchemaDef(String schemaDef) {
        boolean invalid = true;
        if (!StringUtils.isEmpty(schemaDef)) {
            try {
                IObject item = CIMContext.Instance.ProcessCache().item(schemaDef, null, false);
                invalid = item == null;
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }

        }
        return !invalid;
    }

    public static IObjectCollection getObjectsByUID(Map<String, List<String>> mapSource, boolean relOrNot) throws Exception {
        if (mapSource != null) {
            IObjectCollection result = new ObjectCollection();
            for (Map.Entry<String, List<String>> listEntry : mapSource.entrySet()) {
                List<List<String>> listList = CommonUtility.createList(listEntry.getValue().stream().distinct().collect(Collectors.toList()));
                String relDefOrClassDef = listEntry.getKey();
                for (List<String> stringList : listList) {
                    stringList = stringList.stream().map(c -> CommonUtility.replaceCommaToChar(c)).distinct().collect(Collectors.toList());
                    QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
                    CIMContext.Instance.QueryEngine().setQueryForRelationship(queryRequest, relOrNot);
                    if (relOrNot) {
                        CIMContext.Instance.QueryEngine().addRelOrEdgeDefForQuery(queryRequest, null, interfaceDefinitionType.IRel.toString(), propertyDefinitionType.UID.toString(), operator.in, String.join(",", stringList));
                        CIMContext.Instance.QueryEngine().addRelOrEdgeDefForQuery(queryRequest, null, interfaceDefinitionType.IRel.toString(), propertyDefinitionType.RelDefUID.toString(), operator.equal, relDefOrClassDef);
                    } else {
                        if (!relDefOrClassDef.equalsIgnoreCase(interfaceDefinitionType.IObject.toString()))
                            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, relDefOrClassDef);
                        CIMContext.Instance.QueryEngine().addRelOrEdgeDefForQuery(queryRequest, null, null, propertyDefinitionType.UID.toString(), operator.in, String.join(",", stringList));
                    }
                    IObjectCollection query1 = CIMContext.Instance.QueryEngine().query(queryRequest);
                    if (query1 != null && query1.hasValue())
                        result.addRangeUniquely(query1);
                }
            }
            return result;
        }
        return null;
    }

    protected static void setEnd1ForQueryCache(IRelDef relDef, Map<String, Map<String, String>> interfaceAndProps, Map<String, List<String>> mapQueryContainer) throws Exception {
        if (mapQueryContainer != null && relDef != null && interfaceAndProps != null) {
            IInterfaceDef interfaceDef = CIMContext.Instance.ProcessCache().item(relDef.UID1(), domainInfo.SCHEMA.toString()).toInterface(IInterfaceDef.class);
            Map<String, List<IClassDef>> classDefDomainInfo = interfaceDef.getRealizedClassDefDomainInfo();
            if (classDefDomainInfo != null) {
                for (Map.Entry<String, List<IClassDef>> listEntry : classDefDomainInfo.entrySet())
                    for (IClassDef classDef : listEntry.getValue()) {
                        CommonUtility.doAddElementGeneral(mapQueryContainer,
                                classDef.UID(),
                                CommonUtility.getPropertyValueFromInfoContainer(interfaceAndProps, propertyDefinitionType.UID1.toString(), interfaceDefinitionType.IRel.toString()));
                    }
            }
        }

    }

    protected static void setEnd2ForQueryCache(IRelDef relDef, Map<String, Map<String, String>> interfaceAndProps, Map<String, List<String>> mapQueryContainer) throws Exception {
        if (mapQueryContainer != null && relDef != null && interfaceAndProps != null) {
            IInterfaceDef interfaceDef = CIMContext.Instance.ProcessCache().item(relDef.UID2(), domainInfo.SCHEMA.toString()).toInterface(IInterfaceDef.class);
            Map<String, List<IClassDef>> classDefDomainInfo = interfaceDef.getRealizedClassDefDomainInfo();
            if (classDefDomainInfo != null) {
                for (Map.Entry<String, List<IClassDef>> listEntry : classDefDomainInfo.entrySet()) {
                    for (IClassDef classDef : listEntry.getValue()) {
                        CommonUtility.doAddElementGeneral(mapQueryContainer,
                                classDef.UID(),
                                CommonUtility.getPropertyValueFromInfoContainer(interfaceAndProps, propertyDefinitionType.UID2.toString(), interfaceDefinitionType.IRel.toString()));
                    }
                }
            }
        }
    }

    public static IObjectCollection getExistObjByXmlInfoFromSystem(@NotNull List<Map<String, Map<String, Map<String, String>>>> objInfoContainer) throws Exception {
        IObjectCollection container = new ObjectCollection();
        Map<String, List<String>> mapObjectUIDs = new HashMap<>();
        Map<String, List<String>> mapRelUIDs = new HashMap<>();
        StopWatch stopWatch = PerformanceUtility.start();
        log.info("start to identify existed object(s)");
        for (Map<String, Map<String, Map<String, String>>> objInfos : objInfoContainer) {
            for (Map.Entry<String, Map<String, Map<String, String>>> obj : objInfos.entrySet()) {
                String classDef = obj.getKey();
                Map<String, Map<String, String>> interfaceAndProps = obj.getValue();
                String uid = CommonUtility.getPropertyValueFromInfoContainer(interfaceAndProps, propertyDefinitionType.UID.toString(), interfaceDefinitionType.IObject.toString());
                if (classDef.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                    String relDef = CommonUtility.getPropertyValueFromInfoContainer(interfaceAndProps, propertyDefinitionType.RelDefUID.toString(), interfaceDefinitionType.IRel.toString());
                    IRelDef relDef1 = CIMContext.Instance.ProcessCache().item(relDef, domainInfo.SCHEMA.toString()).toInterface(IRelDef.class);
                    CommonUtility.doAddElementGeneral(mapRelUIDs, relDef, uid);
                    setEnd1ForQueryCache(relDef1, interfaceAndProps, mapObjectUIDs);
                    setEnd2ForQueryCache(relDef1, interfaceAndProps, mapObjectUIDs);
                } else
                    CommonUtility.doAddElementGeneral(mapObjectUIDs, classDef, uid);
            }
        }
        IObjectCollection existObjects = getObjectsByUID(mapObjectUIDs, false);
        if (existObjects != null && existObjects.size() > 0)
            container.addRange(existObjects);
        IObjectCollection existRels = getObjectsByUID(mapRelUIDs, true);
        if (existRels != null && existRels.size() > 0)
            container.addRange(existRels);
        log.info("retrieved possible object(s) " + container.size() + PerformanceUtility.stop(stopWatch));
        return container;
    }

    public static IObjectCollection convertJSONArrayToIObjectCollection(@NotNull JSONArray pcolSelObject) throws Exception {
        IObjectCollection lcolContainer = new ObjectCollection();
        List<JSONObject> lcolObjects = CommonUtility.toJSONObjList(pcolSelObject);
        for (JSONObject jsonObject : lcolObjects) {
            IObject lobjObject = getObjectByJSONObject(jsonObject);
            if (lobjObject == null)
                throw new Exception("未找到OBID:" + jsonObject.getString(propertyDefinitionType.OBID.toString()) + ",classDef:" + jsonObject.getString(propertyDefinitionType.ClassDefinitionUID.toString()) + "的对象!");
            lcolContainer.append(lobjObject);
        }
        return lcolContainer;
    }

    public static IObject getObjectByJSONObject(@NotNull JSONObject jsonObject) throws Exception {
        String lstrOBID = jsonObject.getString(propertyDefinitionType.OBID.toString());
        String lstrClassDefUID = jsonObject.getString(propertyDefinitionType.ClassDefinitionUID.toString());
        if (!StringUtils.isEmpty(lstrClassDefUID) && !StringUtils.isEmpty(lstrOBID)) {
            return CIMContext.Instance.ProcessCache().getObjectByOBID(lstrOBID, lstrClassDefUID);
        }
        return null;
    }

    public static List<ObjectDTO> converterIObjectCollectionToDTOList(IObjectCollection pcolSource) throws Exception {
        List<ObjectDTO> lcolResult = new ArrayList<>();
        if (hasValue(pcolSource)) {
            Iterator<IObject> iterator = pcolSource.GetEnumerator();
            while (iterator.hasNext()) {
                IObject lobj = iterator.next();
                lcolResult.add(lobj.toObjectDTO());
            }
        }
        return lcolResult;
    }


    public static void addXmlStructureInfoContainer(@NotNull Element container, @NotNull IObject pobj) throws Exception {
        Element classElement = container.addElement(pobj.ClassDefinitionUID());
        Iterator<Map.Entry<String, IInterface>> e = pobj.Interfaces().GetEnumerator();
        while (e.hasNext()) {
            Map.Entry<String, IInterface> iNext = e.next();
            IInterface lobjInterface = iNext.getValue();
            String lstrInterfaceDefUID = iNext.getKey();
            Element interfaceElement = classElement.addElement(lstrInterfaceDefUID);
            Iterator<Map.Entry<String, IProperty>> e1 = lobjInterface.Properties().GetEnumerator();
            while (e1.hasNext()) {
                Map.Entry<String, IProperty> pNext = e1.next();
                IProperty lobjProp = pNext.getValue();
                String lstrPropDefUID = pNext.getKey();
                if (propertyDefinitionType.PropertiesNotToExport().contains(lstrPropDefUID)) continue;
                interfaceElement.addAttribute(lstrPropDefUID, lobjProp.toDisplayValue());
            }
        }
    }

    public static Object getPropValue(String pstrPropDef, Object propValue) throws Exception {
        IObject lobjPropDef = CIMContext.Instance.ProcessCache().item(pstrPropDef, domainInfo.SCHEMA.toString());
        if (lobjPropDef == null) throw new Exception("无效的属性定义:" + pstrPropDef);
        IPropertyType scopedByPropertyType = lobjPropDef.toInterface(IPropertyDef.class).getScopedByPropertyType();
        propertyValueType valueType = null;
        if (scopedByPropertyType.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.PropertyType.toString()))
            valueType = propertyValueType.valueOf(scopedByPropertyType.Name());
        else
            valueType = propertyValueType.valueOf(scopedByPropertyType.ClassDefinitionUID());
        switch (valueType) {
            case StringType:
            case EnumListLevelType:
                propValue = propValue != null ? propValue.toString() : "";
                break;
            case EnumListType:
                propValue = propValue != null ? ValueConversionUtility.toEnumUID(propValue, scopedByPropertyType.toInterface(IEnumListType.class)) : "";
                break;
            case BooleanType:
                propValue = ValueConversionUtility.toBoolean(propValue);
                break;
            case IntegerType:
                propValue = ValueConversionUtility.toInteger(propValue);
                break;
            case DoubleType:
                propValue = ValueConversionUtility.toDouble(propValue);
                break;
            case YMDType:
            case DateTimeType:
                propValue = ValueConvertService.Instance.Date(propValue);
                break;
        }
        return propValue;
    }


    public static void createObjectsWithJSONArray(@NotNull JSONArray jsonArray, @NotNull IObjectCollection pcolExistObjects, @NotNull LoaderReport loaderReport) throws Exception {
        List<JSONObject> jsonObjects = CommonUtility.toJSONObjList(jsonArray);
        List<JSONObject> lcolRels = new ArrayList<>();
        for (JSONObject obj : jsonObjects) {
            String classDef = obj.getString(SchemaUtility.CLASS_DEFINITION_UID);
            if ("Rel".equals(classDef)) {
                lcolRels.add(obj);
                continue;
            }
            String[] interfaces = parseInterfaces(obj);
            List<Map.Entry<String, Object>> properties = parseProperties(obj);
            SchemaUtility.verifySchema(classDef, interfaces, properties);
            String objName = getSpecialProperty(propertyDefinitionType.Name.toString(), properties);
            String objDesc = getSpecialProperty(propertyDefinitionType.Description.toString(), properties);
            String objUid = getSpecialProperty(propertyDefinitionType.UID.toString(), properties);
            if (StringUtils.isEmpty(objUid))
                throw new Exception("未找到UID的信息!");
            if (StringUtils.isEmpty(objName))
                throw new Exception("未找到Name信息!");
            IObject existObject = pcolExistObjects.item(objUid);
            if (existObject == null) {
                existObject = newIObject(classDef, objName, objDesc, "", objUid);
                if (existObject == null) throw new Exception("创建对象:" + objName + ",classDef:" + classDef + "失败!");
                existObject.fillingInterfaces(interfaces);
                existObject.fillingProperties(true, properties);
                existObject.ClassDefinition().FinishCreate(existObject);
            } else {
                existObject.BeginUpdate();
                existObject.fillingInterfaces(interfaces);
                existObject.fillingProperties(false, properties);
                existObject.FinishUpdate();
            }
            loaderReport.addObject(existObject);
        }
        if (lcolRels.size() > 0) {
            for (JSONObject objRel : lcolRels) {
                IObject lobjEnd1 = loaderReport.getObjectByUID(getSpecialPropertyValue(objRel, propertyDefinitionType.UID1.toString()));
                if (lobjEnd1 == null) throw new Exception("未找到一端对象!");
                IObject lobjEnd2 = loaderReport.getObjectByUID(getSpecialPropertyValue(objRel, propertyDefinitionType.UID2.toString()));
                if (lobjEnd2 == null) throw new Exception("未找到二端对象!");
                String lstrRelDef = getSpecialPropertyValue(objRel, propertyDefinitionType.RelDefUID.toString());
                if (StringUtils.isEmpty(lstrRelDef)) throw new Exception("关联关系定义不能为空!");
                IRelCollection relCollection = lobjEnd1.GetEnd1Relationships().GetRels(lstrRelDef);
                if ((hasValue(relCollection) && !relCollection.containsUid2(lobjEnd2.UID())) || !hasValue(relCollection)) {
                    IRel relationShip = createRelationShip(lstrRelDef, lobjEnd1, lobjEnd2, false);
                    loaderReport.addRelationships(relationShip);
                }
            }
        }
    }


    public static IObject getObjectByOBIDAndClassDef(@NotNull String pstrOBID, @NotNull String pstrClassDef) throws Exception {
        return CIMContext.Instance.ProcessCache().getObjectByOBID(pstrOBID, pstrClassDef);
    }

    public static void setValueForUpdatePurpose(@NotNull ObjectDTO objectDTO, @NotNull String obid, @NotNull String classDefinitionUID) throws Exception {
        IObject lobjExist = getObjectByOBIDAndClassDef(obid, classDefinitionUID);
        if (lobjExist == null)
            log.error("未找到OBID:" + obid + ",classDef:" + classDefinitionUID + "的对象!");
        if (lobjExist != null) {
            List<ObjectItemDTO> items = objectDTO.getItems();
            for (ObjectItemDTO objectItemDTO : items) {
                String interfaceDefinitionUID = "";
                String propertyDefinitionUID = "";
                try {
                    propertyDefinitionUID = objectItemDTO.getDefUID();
                    interfaceDefinitionUID = CIMContext.Instance.ProcessCache().getExposedInterfaceByPropertyDef(propertyDefinitionUID);
                    if (!StringUtils.isEmpty(interfaceDefinitionUID)) {
                        IInterface anInterface = lobjExist.Interfaces().get(interfaceDefinitionUID);
                        if (anInterface != null) {
                            IProperty property = anInterface.Properties().get(propertyDefinitionUID);
                            if (property != null) {
                                setDisplayValueForObjectItemDTO(property, objectItemDTO);
                            }
                        } else {
                            // 特殊处理文档对象的属性
                            if (classDefinitionUID.equalsIgnoreCase(classDefinitionType.CIMDocumentMaster.toString()) && propertyDefinitionUID.equalsIgnoreCase(propertyDefinitionType.CIMRevisionSchema.toString())) {
                                objectItemDTO.setDisplayValue(getDocumentUsedRevisionSchemeUID(lobjExist));
                            }
                        }
                    }
                } catch (Exception exception) {
                    log.error("filling value error with " + interfaceDefinitionUID + " for " + propertyDefinitionUID, exception);
                }
            }
        }
    }

    public static String getDocumentUsedRevisionSchemeUID(@NotNull IObject pobjDocMaster) throws Exception {
        String lstrCIMRevisionSchema = "";
        IRelCollection relCollection = pobjDocMaster.GetEnd1Relationships().GetRels(relDefinitionType.CIMDocumentRevisions.toString(), false);
        if (hasValue(relCollection)) {
            IObjectCollection lcolRevisions = relCollection.GetEnd2s();
            Iterator<IObject> e = lcolRevisions.GetEnumerator();
            while (e.hasNext()) {
                IObject lobjRevision = e.next();
                String lstrRevState = lobjRevision.getValue(propertyDefinitionType.CIMRevState.toString());
                if (!StringUtils.isEmpty(lstrRevState) && lstrRevState.contains("Current")) {
                    lstrCIMRevisionSchema = lobjRevision.getValue(propertyDefinitionType.CIMRevisionSchema.toString());
                    break;
                }
            }
        }
        return lstrCIMRevisionSchema;
    }

    protected static void setDisplayValueForObjectItemDTO(@NotNull IProperty property, @NonNull ObjectItemDTO objectItemDTO) {
        if (property.Value() != null) {
            propertyValueType propertyValueType = ccm.server.enums.propertyValueType.valueOf(objectItemDTO.getPropertyValueType());
            switch (propertyValueType) {
                case BooleanType:
                    objectItemDTO.setDisplayValue(ValueConversionUtility.toBoolean(property));
                    break;
                case IntegerType:
                    objectItemDTO.setDisplayValue(ValueConversionUtility.toInteger(property));
                    break;
                case DoubleType:
                    objectItemDTO.setDisplayValue(ValueConversionUtility.toDouble(property));
                    break;
                case StringType:
                case YMDType:
                case DateTimeType:
                case EnumListType:
                default:
                    objectItemDTO.setDisplayValue(property.toDisplayValue());
                    break;
            }
        }
    }

    public static void copyObjProps(@NotNull IObject target, @NotNull IObject source) throws Exception {
        Iterator<Map.Entry<String, IInterface>> e = source.Interfaces().GetEnumerator();
        while (e.hasNext()) {
            Map.Entry<String, IInterface> next = e.next();
            String lstrInterfaceDef = next.getKey();
            IInterface lobjInterface = next.getValue();
            IInterface copyInterface = target.Interfaces().get(lstrInterfaceDef);
            if (copyInterface == null) {
                copyInterface = target.Interfaces().item(lstrInterfaceDef, true);
            }
            if (copyInterface == null) {
                throw new Exception("实例化:" + lstrInterfaceDef + "失败!");
            }
            Iterator<Map.Entry<String, IProperty>> e1 = lobjInterface.Properties().GetEnumerator();
            while (e1.hasNext()) {
                Map.Entry<String, IProperty> propertyEntry = e1.next();
                String lstrPropDef = propertyEntry.getKey();
                if (lstrPropDef.equalsIgnoreCase(propertyDefinitionType.OBID.toString()) || lstrPropDef.equalsIgnoreCase(propertyDefinitionType.UID.toString()) || lstrPropDef.equalsIgnoreCase(propertyDefinitionType.ContainerID.toString()) || lstrPropDef.equalsIgnoreCase(propertyDefinitionType.Config.toString()))
                    continue;
                IProperty lobjProp = propertyEntry.getValue();
                IProperty copyProp = copyInterface.Properties().get(lstrPropDef);
                if (copyProp == null) {
                    copyProp = copyInterface.Properties().item(lstrPropDef, true);
                }
                if (copyProp == null) {
                    throw new Exception("实例化属性定义:" + lstrPropDef + "失败!");
                }
                copyProp.setValue(lobjProp.Value());
            }
        }
    }

    public static List<ObjectDTO> toObjectDTOList(IObjectCollection container) throws Exception {
        List<ObjectDTO> lcolResult = new ArrayList<>();
        if (hasValue(container)) {
            Iterator<IObject> e = container.GetEnumerator();
            while (e.hasNext()) {
                lcolResult.add(e.next().toObjectDTO());
            }
        }
        return lcolResult;
    }

    public static List<Integer> converterStringToInteger(@NotNull List<String> groupOrders) {
        List<Integer> lcolResult = new ArrayList<>();
        for (String value : groupOrders) {
            lcolResult.add(Integer.parseInt(value));
        }
        return lcolResult;
    }

    public static IObject getObjectByClassDefinitionAndName(@NotNull String name, @NotNull String classDefUid) {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefUid);
        CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.equal, name);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }


    public static IObjectCollection getObjectsByNamesAndClassDef(String classDefUid, List<String> names) {
        if (!StringUtils.isEmpty(classDefUid) && CommonUtility.hasValue(names)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefUid);
            CIMContext.Instance.QueryEngine().addNameForQuery(queryRequest, operator.in, names.stream().map(r -> "\"" + r + "\"").collect(Collectors.joining(",")));
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    public static IObject getNewestRevision(@NotNull IObject docMaster) throws Exception {
        IObject lobjResult = null;
        IObjectCollection lcolRevisions = docMaster.GetEnd1Relationships().GetRels(relDefinitionType.CIMDocumentRevisions.toString()).GetEnd2s();
        if (SchemaUtility.hasValue(lcolRevisions)) {
            log.info("找到revision版本对象,个数:{}", lcolRevisions.size());
            if (lcolRevisions.Size() == 1) {
                return lcolRevisions.get(0);
            }
            IObjectCollection lcolWorkingContainer = new ObjectCollection();
            IObjectCollection lcolCurrentContainer = new ObjectCollection();
            IObjectCollection lcolNewContainer = new ObjectCollection();
            IObjectCollection lcolMigrationContainer = new ObjectCollection();
            Iterator<IObject> iterator = lcolRevisions.GetEnumerator();
            while (iterator.hasNext()) {
                IObject documentRevision = iterator.next();
                String lstrRevState = ValueConversionUtility.toString(documentRevision.getProperty(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMRevState.toString()));
                if (revState.EN_Working.toString().equalsIgnoreCase(lstrRevState)) {
                    lcolWorkingContainer.append(documentRevision);
                } else if (revState.EN_Current.toString().equalsIgnoreCase(lstrRevState)) {
                    lcolCurrentContainer.append(documentRevision);
                } else if (revState.EN_Migration.toString().equalsIgnoreCase(lstrRevState)) {
                    lcolMigrationContainer.append(documentRevision);
                } else if (revState.EN_New.toString().equalsIgnoreCase(lstrRevState)) {
                    lcolNewContainer.append(documentRevision);
                }
            }
            if (lcolCurrentContainer.hasValue()) {
                lobjResult = lcolCurrentContainer.get(0);
            }
            if (lobjResult == null) {
                if (lcolWorkingContainer.hasValue()) {
                    lobjResult = lcolWorkingContainer.get(0);
                }
            }
            if (lobjResult == null) {
                if (lcolNewContainer.hasValue()) {
                    lobjResult = lcolNewContainer.get(0);
                }
            }
            if (lobjResult == null) {
                if (lcolMigrationContainer.hasValue()) {
                    lobjResult = lcolMigrationContainer.get(0);
                }
            }
        }
        if (lobjResult == null)
            throw new Exception("未找到最新的版本对象,文档:" + docMaster.Name() + ",uid:" + docMaster.UID());
        return lobjResult;
    }

    public static IObject getNewestDocumentVersion(@NotNull IObject pobjDocRevision) throws Exception {
        IObjectCollection lcolVersions = pobjDocRevision.GetEnd1Relationships().GetRels(relDefinitionType.CIMDocumentRevisionVersions.toString()).GetEnd2s();
        IObject newestDocVersion = null;
        if (SchemaUtility.hasValue(lcolVersions)) {
            if (lcolVersions.size() == 1) {
                return lcolVersions.get(0);
            }
            lcolVersions.sorting(new Comparator<IObject>() {
                @Override
                public int compare(IObject o1, IObject o2) {
                    return o1.CreationDate().compareTo(o2.CreationDate());
                }
            });
            newestDocVersion = lcolVersions.get(lcolVersions.Size() - 1);
        }
        return newestDocVersion;
    }

    public static void fillDocumentPropForObjectDTO(@NotNull ObjectDTO currentForm, @NotNull IObject pobjDocMaster) throws
            Exception {
        if (CommonUtility.hasValue(currentForm.getItems())) {
            IObject newestRevision = getNewestRevision(pobjDocMaster);
            if (newestRevision == null)
                throw new Exception("未找到有效的Revision对象!");
            IObject documentVersion = getNewestDocumentVersion(newestRevision);
            if (documentVersion == null)
                throw new Exception("未找到Version对象!");
            for (ObjectItemDTO propItem : currentForm.getItems()) {
                String lstrPropDefUID = propItem.getDefUID();
                String lstrInterfaceDef = CIMContext.Instance.ProcessCache().getExposedInterfaceByPropertyDef(lstrPropDefUID);
                if (!pobjDocMaster.Interfaces().hasInterface(lstrInterfaceDef)) {
                    if (newestRevision.Interfaces().hasInterface(lstrInterfaceDef)) {
                        propItem.setDisplayValue(newestRevision.Interfaces().getPropertyValue(lstrInterfaceDef, lstrPropDefUID));
                    } else {
                        if (documentVersion.Interfaces().hasInterface(lstrInterfaceDef)) {
                            propItem.setDisplayValue(documentVersion.Interfaces().getPropertyValue(lstrInterfaceDef, lstrPropDefUID));
                        }
                    }
                }
            }
        }
    }

    public static IObjectCollection toIObjectDictionary(List<IObject> objects) {
        if (CommonUtility.hasValue(objects)) {
            IObjectCollection lcolResult = new ObjectCollection();
            for (IObject object : objects) {
                lcolResult.append(object);
            }
            return lcolResult;
        }
        return null;
    }

    /**
     * 组装form和数据信息返回给前端
     *
     * @param formPurpose
     * @param formBase
     * @param objectCollection
     * @return
     * @throws Exception
     */
    public static ObjectDTOCollection toObjectDTOCollection(String formPurpose, ObjectDTO
            formBase, IObjectCollection objectCollection) throws Exception {
        if (formPurpose != null) {
            if (objectCollection != null && objectCollection.hasValue()) {
                List<ObjectDTO> collections = new ArrayList<>();
                Iterator<IObject> objectIterator = objectCollection.GetEnumerator();
                while (objectIterator.hasNext()) {
                    IObject next = objectIterator.next();
                    ObjectDTO currentForm = formBase.copyTo();
                    next.fillingForObjectDTO(currentForm);
                    collections.add(currentForm);
                }
                ObjectDTOCollection oc = new ObjectDTOCollection(collections);
                oc.setCurrent(objectCollection.PageResult().getCurrent());
                oc.setSize(objectCollection.PageResult().getSize());
                oc.setTotal(objectCollection.PageResult().getTotal());
                return oc;
            }
        }
        return null;
    }

    /**
     * 重置json对象中的items 下所有对象的uid (添加项目标识)
     *
     * @param configName        项目名称
     * @param jsonObject        json对象
     * @param withSchemaUidRule 是否使用SCHEMA配置的UID规则
     */
    public static void resetJSONObjectUids(String configName, JSONObject jsonObject, boolean withSchemaUidRule) {
        JSONArray jsonArray = jsonObject.getJSONArray(ExcelUtility.ITEMS);
        List<JSONObject> jsonObjects = CommonUtility.toJSONObjList(jsonArray);
        List<JSONObject> rels = jsonObjects.stream().filter(CommonUtility::isRel).collect(Collectors.toList());
        List<JSONObject> objs = jsonObjects.stream().filter(r -> !CommonUtility.isRel(r)).collect(Collectors.toList());
        for (JSONObject toChangeUIDObj : objs) {
            JSONObject properties = toChangeUIDObj.getJSONObject(CommonUtility.JSON_FORMAT_PROPERTIES);
            String oriUid = properties.getString(propertyDefinitionType.UID.name());
            String newUid = SchemaUtility.generateUIDByJSONObject(toChangeUIDObj, configName, withSchemaUidRule);
            properties.put(propertyDefinitionType.UID.toString(), newUid);
            //改变rels中所有使用这个uid的rel
            if (CommonUtility.hasValue(rels)) {
                List<JSONObject> uid1s = rels.stream().filter(r -> CommonUtility.getSpecialPropertyValue(r, propertyDefinitionType.UID1.name()).equalsIgnoreCase(oriUid)).collect(Collectors.toList());
                if (CommonUtility.hasValue(uid1s)) {
                    for (JSONObject uid1Rel : uid1s) {
                        JSONObject relProperties = uid1Rel.getJSONObject(CommonUtility.JSON_FORMAT_PROPERTIES);
                        relProperties.put(propertyDefinitionType.UID1.toString(), newUid);
                    }
                }
                List<JSONObject> uid2s = rels.stream().filter(r -> CommonUtility.getSpecialPropertyValue(r, propertyDefinitionType.UID2.name()).equalsIgnoreCase(oriUid)).collect(Collectors.toList());
                if (CommonUtility.hasValue(uid2s)) {
                    for (JSONObject relUid2 : uid2s) {
                        JSONObject relProperties = relUid2.getJSONObject(CommonUtility.JSON_FORMAT_PROPERTIES);
                        relProperties.put(propertyDefinitionType.UID2.toString(), newUid);
                    }
                }
            }
        }
    }

}
