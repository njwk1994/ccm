package ccm.server.util;

import ccm.server.convert.impl.ValueConvertService;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
import ccm.server.entity.MetaData;
import ccm.server.entity.MetaDataObj;
import ccm.server.enums.*;
import ccm.server.model.LiteCriteria;
import ccm.server.module.vo.ResultVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CommonUtility {

    public static final String TABLE_PREFIX_ = "CCM_";
    public static final String SHEET_NAMES = "sheetNames";
    public static final String RELDEF_PROJECT_USER = "ccm_project_user";
    public static final String RELDEF_PROJECT_ROLE = "ccm_project_role";
    public static final String RELDEF_PROJECT_OPTION = "ccm_project_option";
    public static final String RELDEF_PROJECT_COMMAND = "ccm_project_command";
    public static final String RELDEF_PROJECT_CLASSDEF = "ccm_project_classdef";
    public static final String RELDEF_ROLE_USER = "ccm_role_user";
    public static final String RELDEF_ROLE_COMMAND = "ccm_role_command";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String ROP_REVISE_TASK_KEY = "system:queueTasks:history:ropTemplateRevise";
    public static final String DOC_RETRIEVE_TASK_KEY = "system:queueTasks:history:docRetrieve";
    public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";
    public static final String REV_SCHEME_UID = "RS_Rev01A";
    public static final String JSON_FORMAT_PROPERTIES = "properties";
    public static final String JSON_FORMAT_REL_DEF_UID = "RelDefUID";
    public static final String JSON_FORMAT_INTERFACES = "interfaces";
    public static final String JSON_FORMAT_ITEMS = "items";
    public static final String JSON_FORMAT_UID1 = "UID1";
    public static final String JSON_FORMAT_UID2 = "UID2";
    public static final String JSON_FORMAT_NAME1 = "Name1";
    public static final String JSON_FORMAT_NAME2 = "Name2";
    public static final String JSON_FORMAT_CLASS_DEFINITION_UID = "classDefinitionUID";
    public static final String REL_DEF_DOCUMENT_MASTER_TO_REVISION = "CIDocumentRevisions";
    public static final String REL_DEF_DOCUMENT_REVISION_TO_VERSION = "CIDocumentRevisionVersions";
    public static final String PROPERTY_GROUP_GENERAL = "基础 信息";
    public static final String RELATIONSHIP_GROUP_GENERAL = "关联关系 信息";

    public static final String SPLITTER = "$_$";

    public static String replaceValueFromSTARIntoPERCENT(String value) {
        if (value != null)
            return value.replace(StringPool.STAR, StringPool.PERCENT);
        return null;
    }

    public static String[] valueArray(String value) {
        if (value != null) {
            return value.split("[,|]");
        }
        return new String[]{};
    }

    public static boolean containsJson(@NotNull List<JSONObject> container, @NotNull JSONObject obj) {
        if (hasValue(container)) {
            return container.stream().map(r -> getSpecialPropertyValue(r, propertyDefinitionType.UID.name())).collect(Collectors.toList()).contains(getSpecialPropertyValue(obj, propertyDefinitionType.UID1.name()));
        }
        return false;
    }

    /**
     * 通过UID比对
     * 2023.03.16 HT 对象判断属性使用错误导致UID重复问题修复
     *
     * @param container
     * @param obj
     * @return
     */
    public static boolean containsJsonWithoutRel(@NotNull List<JSONObject> container, @NotNull JSONObject obj) {
        if (hasValue(container)) {
            return container.stream().map(r -> getSpecialPropertyValue(r, propertyDefinitionType.UID.name())).collect(Collectors.toList()).contains(getSpecialPropertyValue(obj, propertyDefinitionType.UID.name()));
        }
        return false;
    }

    public static Object[] valueArrayWith2Elements(String value) {
        if (value != null) {
            List<String> strings = CommonUtility.toList(value.split("[,|]"));
            if (strings.size() > 0) {
                return new String[]{strings.get(0), strings.get(strings.size() - 1)};
            }
        }
        return new String[]{"", ""};
    }


    public static boolean SystemUpgradeDeleteData(@NotNull JSONObject jsonObject) {
        String systemUpgradeDeleteData = getSpecialPropertyValue(jsonObject, "SystemUpgradeDeleteData");
        if (StringUtils.isEmpty(systemUpgradeDeleteData)) return false;
        return Boolean.parseBoolean(systemUpgradeDeleteData);
    }

    public static String valueToString(Object value) {
        if (value != null) {
            if (value instanceof Date) {
                return DateUtils.formatDate((Date) value, ValueConvertService.SUPPORTED_DATE_FORMATS[0]);
            } else
                return value.toString();
        }
        return "";
    }

    public static boolean isJSONObjStrSameAsOtherJSONObjStr(@NotNull String pstrJSONObjectStr1, @NotNull String pstrJSONObjectStr2) {
        // 2022.04.07 HT 数据比对空指针问题修复
        if (StringUtils.isEmpty(pstrJSONObjectStr1) || StringUtils.isEmpty(pstrJSONObjectStr2)) {
            return false;
        }
        return JSONObject.parseObject(pstrJSONObjectStr1).equals(JSONObject.parseObject(pstrJSONObjectStr2));
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        try {
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = localDateTime.atZone(zoneId);
            return Date.from(zdt.toInstant());
        } catch (Exception e) {
            log.error(e.getCause().getMessage());
        }
        return null;
    }

    public static String toYMDStr(String pstrDateTime) throws ParseException {
        if (!StringUtils.isEmpty(pstrDateTime)) {
            Date date = parseStrToDate(pstrDateTime);
            if (date != null) {
                DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_YMD);
                return dateFormat.format(date);
            }
        }
        return "";
    }


    public static Date Now() {
        return localDateTimeToDate(LocalDateTime.now());
    }

    public static relDirection toRelDirection(String pstrRelDef) {
        if (pstrRelDef.startsWith("+") || pstrRelDef.endsWith("_12"))
            return relDirection._1To2;
        else if (pstrRelDef.startsWith("-") || pstrRelDef.endsWith("_21"))
            return relDirection._2To1;
        return relDirection._1To2;
    }

    public static boolean isDocument(String pstrClassDefUID) {
        return !StringUtils.isEmpty(pstrClassDefUID) && (pstrClassDefUID.equalsIgnoreCase(classDefinitionType.CIMDocumentMaster.toString()) || pstrClassDefUID.equalsIgnoreCase(classDefinitionType.CIMDocumentRevision.toString()) || pstrClassDefUID.equalsIgnoreCase(classDefinitionType.CIMDocumentVersion.toString()));
    }

    public static LiteCriteria getLiteCriteria(ExpansionMode expansionMode, String identity) {
        relDirection relDirection = ccm.server.enums.relDirection._1To2;
        String pathDefinition = "";
        String interfaceDefinition = "";
        String propertyDefinition = "";
        String[] strings = identity.split("\\.");
        if (strings.length == 1) {
            if (expansionMode != ExpansionMode.none) {
                relDirection = CommonUtility.toRelDirection(strings[0]);
                propertyDefinition = propertyDefinitionType.Name.toString();
                pathDefinition = CommonUtility.toActualDefinition(identity);
            } else {
                propertyDefinition = strings[0];
            }
        } else {
            propertyDefinition = strings[1];
            relDirection = CommonUtility.toRelDirection(strings[0]);
            pathDefinition = CommonUtility.toActualDefinition(strings[0]);
        }
        return new LiteCriteria(pathDefinition, relDirection, interfaceDefinition, propertyDefinition, null, expansionMode);
    }

    public static String toActualDefinition(String pstrRelDef) {
        String result = pstrRelDef;
        if (!StringUtils.isEmpty(result)) {
            if (result.startsWith("+") || result.startsWith("-"))
                result = pstrRelDef.substring(1);
            if (result.endsWith("_12") || result.endsWith("_21")) {
                result = result.substring(0, result.lastIndexOf("_"));
            }
        }
        return result;
    }

    public final static String CHAR_REPLACE_FOR_COMMA = "AAAAAA";

    public static String replaceCommaToChar(String string) {
        if (!StringUtils.isEmpty(string)) {
            return string.replace(",", CHAR_REPLACE_FOR_COMMA);
        }
        return "";
    }

    public static String replaceCharToComma(String string) {
        if (!StringUtils.isEmpty(string)) {
            return string.replace(CHAR_REPLACE_FOR_COMMA, ",");
        }
        return "";
    }

    public static <K, V> void doAddElementGeneral(Map<K, List<V>> mapContainer, K key, V value) {
        if (mapContainer != null && key != null) {
            List<V> vList = mapContainer.getOrDefault(key, new ArrayList<V>());
            vList.add(value);
            if (mapContainer.containsKey(key))
                mapContainer.replace(key, vList);
            else
                mapContainer.put(key, vList);
        }
    }

    public static <K, V extends MetaData> void doAddElement(Map<K, List<V>> mapContainer, K key, V value) {
        if (mapContainer != null && key != null) {
            List<V> vList = mapContainer.getOrDefault(key, new ArrayList<V>());
            V metaData = (V) value.copy();
            metaData.setTablePrefix(key.toString());
            vList.add(metaData);
            if (mapContainer.containsKey(key))
                mapContainer.replace(key, vList);
            else
                mapContainer.put(key, vList);
        }
    }

    public static boolean checkObjectHasOBIDValueFromPropertyArrayStr(@NotNull List<ObjectItemDTO> pcolProps) {
        ObjectItemDTO objectItemDTO = pcolProps.stream().filter(r -> propertyDefinitionType.OBID.toString().equalsIgnoreCase(r.getDefUID())).findFirst().orElse(null);
        if (objectItemDTO != null) {
            Object displayValue = objectItemDTO.getDisplayValue();
            return displayValue != null;
        }
        return false;
    }

    public static boolean containsProp(@NotNull List<ObjectItemDTO> pcolProps, @NotNull String pstrPropDefUID) {
        return pcolProps.stream().anyMatch(r -> pstrPropDefUID.equalsIgnoreCase(r.getDefUID()));
    }

    public static String getSpecialValueFromProperties(@NotNull List<ObjectItemDTO> pcolProperties, @NotNull String pstrProperDefUID) {
        ObjectItemDTO objectItemDTO = pcolProperties.stream().filter(r -> pstrProperDefUID.equalsIgnoreCase(r.getDefUID())).findFirst().orElse(null);
        if (objectItemDTO != null) {
            Object displayValue = objectItemDTO.getDisplayValue();
            return displayValue != null ? displayValue.toString() : "";
        }
        return "";
    }

    public static List<ObjectItemDTO> converterPropertiesToItemDTOList(@NotNull String pstrProperties) {
        JSONArray properties = JSONObject.parseArray(pstrProperties);
        List<ObjectItemDTO> lcolResult = new ArrayList<>();
        for (Object object : properties) {
            lcolResult.add(JSONObject.parseObject(object.toString(), ObjectItemDTO.class));
        }
        return lcolResult;
    }

    public static String parseCamel(String pstrTableFieldName) {
        if (!StringUtils.isEmpty(pstrTableFieldName)) {
            String[] arrItems = pstrTableFieldName.split("_");
            StringBuilder str = new StringBuilder();
            str.append(arrItems[0].toLowerCase());
            for (int i = 1; i < arrItems.length; i++) {
                String temp = arrItems[i];
                char ch = temp.charAt(0);
                temp = Character.toUpperCase(ch) + temp.substring(1);
                str.append(temp);
            }
            return str.toString();
        }
        return "";
    }

    public static Boolean contains(String[] arrayItems, String value) {
        if (arrayItems != null) {
            return Arrays.stream(arrayItems).anyMatch(c -> c.equalsIgnoreCase(value));
        }
        return false;
    }

    public static String[] convertToArray(Collection<String> items) {
        String[] result = null;
        if (CommonUtility.hasValue(items)) {
            result = new String[items.size()];
            items.toArray(result);
        }
        return result;
    }

    public static String getPropertyValueFromInfoContainer(@NotNull Map<String, Map<String, String>> objInterfaceAndProps, @NotNull String propertyDef, @NotNull String interfaceDef) {
        if (objInterfaceAndProps.containsKey(interfaceDef)) {
            Map<String, String> propertyValue = objInterfaceAndProps.get(interfaceDef);
            if (propertyValue.containsKey(propertyDef)) {
                return propertyValue.get(propertyDef);
            }
        }
        return null;
    }

    public static boolean hasValue(Collection<?> items) {
        if (items != null && items.size() > 0)
            return items.stream().anyMatch(Objects::nonNull);
        return false;
    }


    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String[] getDateFolder() {
        String[] retVal = new String[3];

        LocalDate localDate = LocalDate.now();
        retVal[0] = localDate.getYear() + "";

        int month = localDate.getMonthValue();
        retVal[1] = month < 10 ? "0" + month : month + "";

        int day = localDate.getDayOfMonth();
        retVal[2] = day < 10 ? "0" + day : day + "";

        return retVal;
    }

    public static final String URI_DELIMITER = "/";

    public static String getOriginalFileNameFromMinioObjName(String minioObjName) {
        if (!StringUtils.isEmpty(minioObjName)) {
            int indexOf = minioObjName.lastIndexOf(URI_DELIMITER);
            if (indexOf != -1) {
                return minioObjName.substring(indexOf + 1);
            } else {
                return minioObjName;
            }
        }
        return null;
    }

    public static String getFileNameExtWithoutPoint(String fileName) {
        if (!StringUtils.isEmpty(fileName)) {
            int index = fileName.lastIndexOf(".");
            if (index != -1) {
                String extension = fileName.substring(index + 1);
                if (!extension.isEmpty()) {
                    return extension;
                }
            }
        }
        return "";
    }

    public static String getFileNameExtWithPoint(String fileName) {
        if (!StringUtils.isEmpty(fileName)) {
            int index = fileName.lastIndexOf(".");
            if (index != -1) {
                String extension = fileName.substring(index);
                if (!StringUtils.isEmpty(extension)) {
                    return extension;
                }
            }
        }
        return "";
    }

    public static void raiseProgress(int currentIndex, int totalSize, String message) {
        if (currentIndex % 2000 == 0 || currentIndex == totalSize) {
            log.info(message + ",progress is " + currentIndex + " / " + totalSize);
        }
    }

    public static String toJsonString(Result<?> result) {
        if (result != null) {
            return JSON.toJSONString(result, SerializerFeature.IgnoreErrorGetter, SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat);
        }
        return "";
    }


    public static String toJsonString(ResultVo<?> resultVo) {
        return JSON.toJSONString(resultVo, SerializerFeature.IgnoreErrorGetter, SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat);
    }

    public static boolean hasValue(JSONArray jsonArray) {
        return jsonArray != null && jsonArray.size() > 0;
    }

    public static boolean validMinioObjName(String minioObjName) {
        return !StringUtils.isEmpty(minioObjName) && !minioObjName.startsWith("error");
    }

    public static String getSpecialPropertyValue(JSONObject jsonObject, String propertyDef) {
        if (jsonObject != null && !StringUtils.isEmpty(propertyDef)) {
            JSONObject properties = jsonObject.getJSONObject(JSON_FORMAT_PROPERTIES);
            if (properties != null && properties.containsKey(propertyDef)) {
                return properties.getString(propertyDef);
            }
        }
        return "";
    }

    public static List<JSONObject> splitDocJSONStructure(JSONObject allObjects) {
        List<JSONObject> lcolResult = new ArrayList<>();
        if (allObjects != null) {
            //获取Items 数组
            JSONArray jsonArray = allObjects.getJSONArray(JSON_FORMAT_ITEMS);
            if (jsonArray != null && jsonArray.size() > 0) {
                List<JSONObject> jsonObjects = toJSONObjList(jsonArray);
                //获取文档对象
                List<JSONObject> lcolDocuments = jsonObjects.stream().filter(r -> r.getString(JSON_FORMAT_CLASS_DEFINITION_UID).equalsIgnoreCase(classDefinitionType.CIMDocumentMaster.toString())).collect(Collectors.toList());
                List<JSONObject> lcolRels = jsonObjects.stream().filter(r -> r.getString(JSON_FORMAT_CLASS_DEFINITION_UID).equalsIgnoreCase(classDefinitionType.Rel.toString())).collect(Collectors.toList());
                List<JSONObject> lcolDesignObjs = jsonObjects.stream().filter(r -> !classDefinitionType.CIMDocumentMaster.toString().equalsIgnoreCase(r.getString(JSON_FORMAT_CLASS_DEFINITION_UID)) && !classDefinitionType.Rel.toString().equalsIgnoreCase(r.getString(JSON_FORMAT_CLASS_DEFINITION_UID))).collect(Collectors.toList());
                if (hasValue(lcolDocuments)) {
                    for (JSONObject doc : lcolDocuments) {
                        //遍历每个文档
                        JSONObject docContainer = new JSONObject();
                        List<JSONObject> containsDesignObjs = new ArrayList<>();
                        containsDesignObjs.add(doc);
                        String lstrDocUID = getSpecialPropertyValue(doc, propertyDefinitionType.UID.toString());
                        if (CommonUtility.hasValue(lcolRels)) {
                            //匹配这个文档包含的设计对象的关联关系
                            List<JSONObject> lcolPointedRels = lcolRels.stream().filter(r -> lstrDocUID.equalsIgnoreCase(getSpecialPropertyValue(r, propertyDefinitionType.UID1.toString()))).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
                            if (hasValue(lcolPointedRels)) {
                                containsDesignObjs.addAll(lcolPointedRels);
                                if (hasValue(lcolDesignObjs)) {
                                    for (JSONObject rel : lcolPointedRels) {
                                        String lstrDesignObjUID = getSpecialPropertyValue(rel, propertyDefinitionType.UID2.toString());
                                        assert !StringUtils.isEmpty(lstrDesignObjUID);
                                        //添加设计对象
                                        JSONObject designObj = lcolDesignObjs.stream().filter(r -> getSpecialPropertyValue(r, propertyDefinitionType.UID.toString()).equalsIgnoreCase(lstrDesignObjUID)).findFirst().orElse(null);
                                        if (designObj != null) {
                                            containsDesignObjs.add(designObj);
                                            //添加设计对象层级关联关系
                                            List<JSONObject> lcolHierarchyRels = lcolRels.stream().filter(r -> lstrDesignObjUID.equalsIgnoreCase(getSpecialPropertyValue(r, propertyDefinitionType.UID1.toString()))).collect(Collectors.toList());
                                            if (CommonUtility.hasValue(lcolHierarchyRels)) {
                                                containsDesignObjs.addAll(lcolHierarchyRels);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        JSONArray value = new JSONArray();
                        List<JSONObject> distinctObjs = containsDesignObjs.stream().distinct().collect(Collectors.toList());
                        value.addAll(distinctObjs);
                        docContainer.put(JSON_FORMAT_ITEMS, value);
                        lcolResult.add(docContainer);
                    }
                }
            }
        }
        return lcolResult;
    }

    public static List<JSONObject> toJSONObjList(JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.size() > 0) {
            // 2022.03.10 HT 原 JSON.toJSONString 将时间转为Long问题修复
            return jsonArray.toJavaList(JSONObject.class);
            // 2022.03.10 HT 原 JSON.toJSONString 将时间转为Long问题修复
        }
        //  2022.03.29 HT 修复返回 null 导致的空指针问题
        return new ArrayList<>();
    }

    public static boolean hasValue(Map<?, ?> map) {
        return map != null && map.size() > 0;
    }

    public static <T> boolean hasValue(T[] items) {
        return items != null && items.length > 0;
    }

    public static ObjectDTO toObjectDTO(List<ObjectItemDTO> items) {
        if (items != null && items.size() > 0) {
            ObjectDTO result = new ObjectDTO();
            result.add(items);
            return result;
        }
        return null;
    }

    public static List<String> toList(String... str) {
        List<String> result = new ArrayList<>();
        if (str != null && str.length > 0) {
            result.addAll(Arrays.asList(str));
        }
        return result;
    }

    public static List<ObjectDTO> toList(ObjectDTO... objs) {
        List<ObjectDTO> result = new ArrayList<>();
        if (objs != null && objs.length > 0) {
            result.addAll(Arrays.asList(objs));
        }
        return result;
    }


    public static ObjectDTO generateBaseObjectDTO(String pstrId, String pstrName, String pstrClassDef) {
        ObjectDTO objectDTO = new ObjectDTO();
        if (!StringUtils.isEmpty(pstrId))
            objectDTO.toSetValue(propertyDefinitionType.OBID.toString(), pstrId);
        if (!StringUtils.isEmpty(pstrName))
            objectDTO.toSetValue(propertyDefinitionType.Name.toString(), pstrName);
        if (!StringUtils.isEmpty(pstrClassDef))
            objectDTO.toSetValue(propertyDefinitionType.ClassDefinitionUID.toString(), pstrClassDef);
        return objectDTO;
    }

    public static List<ObjectItemDTO> getItems(List<ObjectItemDTO> properties, String... propertyDefUID) {
        if (properties != null && properties.size() > 0 && propertyDefUID != null && propertyDefUID.length > 0) {
            List<ObjectItemDTO> items = new ArrayList<>();
            for (String property : propertyDefUID) {
                Optional<ObjectItemDTO> current = properties.stream().filter(c -> c.getDefUID().equalsIgnoreCase(property)).findFirst();
                current.ifPresent(items::add);
            }
            return items;
        }
        return null;
    }

    private static final ThreadLocal<LoginUser> loginUserThreadLocal = new ThreadLocal<>();

    public static LoginUser getLoginUser() {
        if (loginUserThreadLocal.get() == null)
            loginUserThreadLocal.set((LoginUser) SecurityUtils.getSubject().getPrincipal());
        return loginUserThreadLocal.get();
    }

    public static String getLoginUserName() {
        LoginUser lobjCurrentUser = getLoginUser();
        return lobjCurrentUser != null ? lobjCurrentUser.getUsername() : "";
    }

    public static String toSQLPart(String alias, String field, String value, Boolean forceUpper) {
        StringBuilder result = new StringBuilder();
        if (!StringUtils.isEmpty(value)) {
            result.append(" ");
            if (forceUpper)
                result.append("UPPER(").append(alias).append(".").append(field).append(")");
            else
                result.append(alias).append(".").append(field);

            boolean flag = true;
            if (value.contains("*")) {
                value = value.replace("*", "%");
                result.append(" LIKE ");
            } else if (value.contains(",")) {
                value = toINStyle(value.split(","));
                if (!StringUtils.isEmpty(value))
                    value = "(" + value + ")";
                flag = false;
                result.append(" IN ");
            } else
                result.append("=");

            if (flag)
                result.append("'").append(MessageFormat.format("{0}", value)).append("'");
            else
                result.append(MessageFormat.format("{0}", value));
        }
        return result.toString();
    }

    public static List<ObjectDTO> toObjectDTOFromOBJ(List<MetaDataObj> metaObjs) {
        if (CommonUtility.hasValue(metaObjs))
            return metaObjs.stream().map(c -> new ObjectDTO(c, null)).collect(Collectors.toList());
        return null;
    }

    public static String toINStyle(List<String> items) {
        if (CommonUtility.hasValue(items))
            return items.stream().filter(c -> !StringUtils.isEmpty(c)).map(c -> MessageFormat.format("''{0}''", c)).collect(Collectors.joining(","));
        return null;
    }

    public static String toINStyle(String... items) {
        if (items != null && items.length > 0) {
            return Arrays.stream(items).map(c -> {
                if (c.startsWith("'") && c.endsWith("'"))
                    return c;
                return MessageFormat.format("''{0}''", c);
            }).collect(Collectors.joining(","));
        }
        return "";
    }

    public static String tryToGetName(String value) {
        if (!StringUtils.isEmpty(value)) {
            if (value.contains(":"))
                return value.substring(value.indexOf(":") + 1);
            return value;
        }
        return "";
    }

    public static String tryToGetId(String value) {
        if (!StringUtils.isEmpty(value)) {
            if (value.contains(":"))
                return value.substring(0, value.indexOf(":"));
            return value;
        }
        return "";
    }

    public static List<ObjectDTO> getObjectsByClassDef(List<ObjectDTO> items, String... classDefs) {
        if (CommonUtility.hasValue(items))
            return items.stream().filter(c -> Arrays.stream(classDefs).anyMatch(m -> m.equalsIgnoreCase(c.getClassDefinitionUID()))).collect(Collectors.toList());
        return new ArrayList<ObjectDTO>();
    }

    @NotNull
    public static String parsePattern(String pattern) {
        if (!StringUtils.isEmpty(pattern)) {
            if (pattern.contains("*")) {
                pattern = pattern.replace("*", "%");
                pattern = pattern.startsWith("%") ? pattern.substring(1) : pattern;
                pattern = pattern.endsWith("%") ? pattern.substring(0, pattern.length() - 1) : pattern;
            }
            return pattern;
        }
        return "";
    }

    public static final String REQUEST_BODY_FORMPURPOSE = "formPurpose";
    public static final String REQUEST_BODY_OBJECTDTO = "obj";
    public static final String REQUEST_BODY_OBJECTDTOS = "objs";
    public static final String REQUEST_BODY_OBJECTDTO_ITEMS = "items";
    public static final String REQUEST_BODY_ID = "id";
    public static final String REQUEST_BODY_OBID = "obid";
    public static final String REQUEST_BODY_CLASS_DEFINITION_UID = "classDefinitionUID";

    public static String getId(JSONObject requestBody) {
        if (requestBody != null)
            return requestBody.getString(REQUEST_BODY_ID);
        return "";
    }

    public static int getMathFloor(int a, int b) {
        if (b == 0 || a == 0) {
            return 0;
        }
        BigDecimal aBig = new BigDecimal(a);
        BigDecimal bBig = new BigDecimal(b);
        //向下取整
        return (int) Math.floor(aBig.divide(bBig).doubleValue());
    }

    public static String getClassDefinitionUID(JSONObject requestBody) {
        if (requestBody != null)
            return requestBody.getString(REQUEST_BODY_CLASS_DEFINITION_UID);
        return "";
    }

    public static String getFormPurpose(JSONObject requestBody) {
        if (requestBody != null)
            return requestBody.getString(REQUEST_BODY_FORMPURPOSE);
        return "";
    }

    public static List<ObjectDTO> parseObjectDTOsFromJSON(JSONObject jsonObject) {
        List<ObjectDTO> result = new ArrayList<>();
        if (jsonObject != null) {
            JSONArray arrObjs = jsonObject.getJSONArray(REQUEST_BODY_OBJECTDTOS);
            for (int i = 0; i < arrObjs.size(); i++) {
                ObjectDTO currentObjectDTO = parseObjectDTOFromJSON(arrObjs.getJSONObject(i));
                result.add(currentObjectDTO);
            }
        }
        return result;
    }

    public static List<ObjectItemDTO> parseObjectItemDTOsFromJSON(JSONObject jsonObject) {
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.getJSONArray(REQUEST_BODY_OBJECTDTO_ITEMS);
            if (jsonArray == null)
                jsonArray = jsonObject.getJSONArray("objProperties");
            if (jsonArray == null)
                jsonArray = ((JSONObject) jsonObject.get(REQUEST_BODY_OBJECTDTO)).getJSONArray(REQUEST_BODY_OBJECTDTO_ITEMS);
            return new ArrayList<>(generateObjectItemDTOs(jsonArray));
        }
        return null;
    }


    private static List<ObjectItemDTO> generateObjectItemDTOs(JSONArray jsonArray) {
        List<ObjectItemDTO> result = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                String jsonObject1 = jsonArray.getJSONObject(i).toJSONString();
                ObjectItemDTO lobjPropertyItem = JSONObject.parseObject(jsonObject1, ObjectItemDTO.class);
                result.add(lobjPropertyItem);
            }
        }
        return result;
    }

    public static ObjectDTO parseObjectDTOFromJSON(JSONObject jsonObject) {
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.getJSONArray(REQUEST_BODY_OBJECTDTO_ITEMS);
            if (jsonArray == null)
                jsonArray = jsonObject.getJSONArray("objProperties");

            if (jsonArray == null)
                jsonArray = jsonObject.getJSONObject(REQUEST_BODY_OBJECTDTO).getJSONArray(REQUEST_BODY_OBJECTDTO_ITEMS);

            ObjectDTO lobjDTO = new ObjectDTO();
            lobjDTO.add(generateObjectItemDTOs(jsonArray));
            return lobjDTO;
        }
        return null;
    }


    public static Integer calculatePercentage(int current, int total) {
        if (current == 0) return 0;
        Double ldbCurrent = current * 1.0;
        Double ldbTotal = total * 1.0;
        Double percentage = (ldbCurrent / ldbTotal) * 100;
        return percentage.intValue();
    }

    public static HierarchyObjectDTO parseHierarchyObjectDTOFromJSON(JSONObject jsonObject) {
        if (null != jsonObject) {
            JSONObject o = jsonObject.getJSONObject(REQUEST_BODY_OBJECTDTO);
            if (null != o)
                return JSONObject.parseObject(o.toJSONString(), HierarchyObjectDTO.class);
        }
        return null;
    }

    public static int getSize(Collection<?> items) {
        return items != null ? items.size() : 0;
    }

    public static int getSize(Map<?, ?> items) {
        return items != null ? items.size() : 0;
    }

    public static String formatDateWithToString(Date date) {
        if (date != null)
            return date.toString();
        return "";
    }

    public static <T> List<List<T>> createList(List<T> target) {
        return createList(target, 1000);
    }

    public static <T> List<List<T>> createList(List<T> target, int size) {
        List<List<T>> result = new ArrayList<>();
        int arrSize = target.size() % size == 0 ? target.size() / size : target.size() / size + 1;
        for (int i = 0; i < arrSize; i++) {
            List<T> sub = new ArrayList<>();
            for (int j = i * size; j <= size * (i + 1) - 1; j++) {
                if (j <= target.size() - 1) {
                    sub.add(target.get(j));
                }
            }
            result.add(sub);
        }
        return result;
    }


    public static String getObjectClass(Object o) {
        if (o != null)
            return o.getClass().toString();
        return "NULL as Object null";
    }

    public static String getToString(Object o) {
        if (o != null)
            return o.toString();
        return "NULL";
    }

    public static String formatDateWithDateFormat(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.format(date);
    }

    public static Date parseStrToDate(String date) throws ParseException {
        if (!StringUtils.isEmpty(date)) {
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            return dateFormat.parse(date);
        }
        return null;
    }

    public static <T> List<List<T>> partitionList(List<T> pcolContainer, int size) {
        if (CommonUtility.hasValue(pcolContainer) && size > 0) {
            return Lists.partition(pcolContainer, size);
        }
        return null;
    }

    public static boolean isCCMPipe(String pstrClassDefUID) {
        return !StringUtils.isEmpty(pstrClassDefUID) && classDefinitionType.CCMPipe.toString().equals(pstrClassDefUID);
    }

    public static boolean isCCMPipeComponent(String pstrClassDefUID) {
        return !StringUtils.isEmpty(pstrClassDefUID) && classDefinitionType.CCMPipeComponent.toString().equals(pstrClassDefUID);
    }

    public static boolean isCCMSpool(String pstrClassDefUID) {
        return !StringUtils.isEmpty(pstrClassDefUID) && classDefinitionType.CCMSpool.toString().equals(pstrClassDefUID);
    }

    public static boolean isCCMSupport(String pstrClassDefUID) {
        return !StringUtils.isEmpty(pstrClassDefUID) && classDefinitionType.CCMSupport.toString().equals(pstrClassDefUID);
    }

    public static boolean isCCMWeld(String pstrClassDefUID) {
        return !StringUtils.isEmpty(pstrClassDefUID) && classDefinitionType.CCMWeld.toString().equals(pstrClassDefUID);
    }

    public static String getTempFolder() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * 将InputStream写入本地文件
     *
     * @param localFilePath 本地文件路径
     * @param inputStream   输入流
     * @throws IOException
     */
    public static void writeInputStreamToLocalFile(String localFilePath, InputStream inputStream)
            throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = new FileOutputStream(localFilePath);
        while ((index = inputStream.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        downloadFile.close();
        inputStream.close();
    }

    public static List<String> getUIDsFromJSONObjects(List<JSONObject> pcolDesignObjs) {
        if (hasValue(pcolDesignObjs)) {
            return pcolDesignObjs.stream().map(r -> r.getJSONObject(JSON_FORMAT_PROPERTIES).getString(propertyDefinitionType.UID.toString())).collect(Collectors.toList());
        }
        return null;
    }

    public static void resetJSONObjectUIDValue(@NotNull JSONObject jsonObject, String pstrNewUID) {
        JSONObject props = jsonObject.getJSONObject(JSON_FORMAT_PROPERTIES);
        props.put(propertyDefinitionType.UID.toString(), pstrNewUID);
        jsonObject.put(JSON_FORMAT_PROPERTIES, props);
    }

    public static String[] getUIDsFromJSONObjectsWithArr(List<JSONObject> pcolDesignObjs) {
        if (hasValue(pcolDesignObjs)) {
            return convertToArray(pcolDesignObjs.stream().map(r -> r.getJSONObject(JSON_FORMAT_PROPERTIES).getString(propertyDefinitionType.UID.toString())).collect(Collectors.toList()));
        }
        return null;
    }


    public static File saveXmlDocumentToFile(String pstrFilePath, Document domDocument) throws IOException {
        if (!StringUtils.isEmpty(pstrFilePath)) {
            File file = new File(pstrFilePath);
            if (file.exists()) {
                boolean success = file.delete();
            }
            OutputFormat format = new OutputFormat();
            format.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(Files.newOutputStream(file.toPath()), format);
            writer.write(domDocument);
            writer.close();
            return file;
        }
        return null;
    }

    public static Object getMapValue(Map<String, Object> map, String key) {
        if (map != null && key != null) {
            Object mapOrDefault = map.getOrDefault(key, null);
            return mapOrDefault != null ? mapOrDefault : "";
        }
        return "";
    }

    public static String getTimeSpan(StopWatch stopWatch) {
        return stopWatch.getLastTaskTimeMillis() / 1000 + "秒";
    }

    public static boolean isDocMaster(@NotNull JSONObject object) {
        return object.getString(JSON_FORMAT_CLASS_DEFINITION_UID).equalsIgnoreCase(classDefinitionType.CIMDocumentMaster.name());
    }

    public static boolean isDesignObj(@NotNull JSONObject object) {
        JSONArray jsonArray = object.getJSONArray(JSON_FORMAT_INTERFACES);
        return jsonArray.contains(interfaceDefinitionType.ICCMDesignObj.name());
    }

    public static boolean isRel(@NotNull JSONObject object) {
        return object.getString(JSON_FORMAT_CLASS_DEFINITION_UID).equalsIgnoreCase(classDefinitionType.Rel.name());
    }

    /**
     * 生成除文档到设计对象关联关系以外的数据结构
     *
     * @param jsonObject json对象
     * @return {@link JSONObject}
     */
    public static JSONObject generateNormalObjectsStructure(@NotNull JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        result.put("type", "other");
        JSONArray jsonArray = jsonObject.getJSONArray(JSON_FORMAT_ITEMS);
        List<JSONObject> items = CommonUtility.toJSONObjList(jsonArray);
        if (CommonUtility.hasValue(items)) {
            //过滤非关联关系对象
            List<JSONObject> excludeRels = items.stream().filter(r -> !isRel(r)).collect(Collectors.toList());
            //过滤出所有出了图纸到设计对象的关联relDefinitionType.CCMDesignObjHierarchy
            List<JSONObject> rels = items.stream().filter(r -> isRel(r) && !getSpecialPropertyValue(r, propertyDefinitionType.RelDefUID.name()).equalsIgnoreCase(relDefinitionType.CCMDocument2DesignObj.name()) && !getSpecialPropertyValue(r, propertyDefinitionType.RelDefUID.name()).equalsIgnoreCase(relDefinitionType.CCMDesignObjHierarchy.name())).collect(Collectors.toList());
            if (hasValue(excludeRels)) {
                log.info("添加对象......" + excludeRels.size());
                List<JSONObject> newItems = new ArrayList<>(excludeRels);
                if (hasValue(rels)) {
                    log.info("添加对象关联关系......" + excludeRels.size());
                    newItems.addAll(rels);
                }
                List<JSONObject> collect = newItems.stream().distinct().collect(Collectors.toList());
                result.put(JSON_FORMAT_ITEMS, collect);
            }
        }
        return result;
    }


    public static String getUidFromJson(@NotNull JSONObject object) {
        return getSpecialPropertyValue(object, propertyDefinitionType.UID.name());
    }

    public static boolean isDeleteObj(@NotNull JSONObject object) {
        JSONObject props = object.getJSONObject(JSON_FORMAT_PROPERTIES);
        if (props.containsKey("SystemToDeleteData")) {
            return props.getBoolean("SystemToDeleteData");
        }
        return false;
    }
}
