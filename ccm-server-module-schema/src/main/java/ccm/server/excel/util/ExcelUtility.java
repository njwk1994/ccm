package ccm.server.excel.util;

import ccm.server.context.CIMContext;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.excel.config.ExcelSystemSheetConfig;
import ccm.server.excel.entity.ColumnToClassUid;
import ccm.server.excel.entity.ColumnToProperty;
import ccm.server.excel.entity.ExcelDataContent;
import ccm.server.excel.entity.SheetRelationship;
import ccm.server.excel.mapper.ClassInterfaceMapper;
import ccm.server.excel.mapper.SheetClassMapper;
import ccm.server.excel.mapper.SheetColumnPropertyMapper;
import ccm.server.excel.mapper.SheetRelationshipMapper;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.util.CommonUtility;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileTypeUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.jeecgframework.poi.excel.entity.enmus.ExcelType.XSSF;

/**
 * Excel工具类
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/27 14:12
 */
@Slf4j
@Component("excelUtility")
public class ExcelUtility {

    /**
     * Root层名称
     */
    public static final String ITEMS = "items";

    /**
     * classDefinitionUID-对应的ClassDef
     */
    public static final String CLASS_DEFINITION_UID = "classDefinitionUID";

    /**
     * interfaces-包含的接口
     */
    public static final String INTERFACES = "interfaces";

    /**
     * properties-包含的属性:值
     */
    public static final String PROPERTIES = "properties";

    /**
     * Relationship json 的默认 classDefinitionUID
     */
    private static final String CLASS_DEFINITION_UID_REL = "Rel";

    /**
     * Relationship json 的默认 interfaces之一 - IRel
     */
    private static final String INTERFACES_I_REL = "IRel";

    /**
     * Relationship json 的默认 interfaces之一 - IObject
     */
    private static final String INTERFACES_I_OBJECT = "IObject";

    /**
     * Relationship json 的默认 interfaces之一 - IRelCustom
     */
    // 2022.06.28 HT 修复找不到 IRelCustom 接口问题
    //private static final String INTERFACES_I_REL_CUSTOM = "IRelCustom";

    /**
     * Relationship json 的默认 properties之一 - UID1
     */
    private static final String PROPERTIES_UID1 = "UID1";

    /**
     * Relationship json 的默认 properties之一 - UID2
     */
    private static final String PROPERTIES_UID2 = "UID2";

    /**
     * Relationship json 的默认 properties之一 - RelDefUID (关联关系名称)
     */
    private static final String PROPERTIES_REL_DEF_UID = "RelDefUID";

    /**
     * Relationship json 的默认 properties之一 - Name1
     */
    private static final String PROPERTIES_NAME1 = "Name1";

    /**
     * Relationship json 的默认 properties之一 - Name2
     */
    private static final String PROPERTIES_NAME2 = "Name2";

    private ExcelUtility() {
    }

    /**
     * 普通Excel导入
     * <p>
     * key:sheet页名称,
     * value:每行数据(value-key:列名 value-value:列数据)
     * </p>
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static Map<String, List<Map<String, Object>>> importNormalExcel(MultipartFile file) throws Exception {
        fileSimpleCheck(file);
        Map<String, List<Map<String, Object>>> dataSheetsData = new HashMap<>();
        try (InputStream inputStream = file.getInputStream()) {
            // 获取文件后缀
            String filename = file.getOriginalFilename();
            String fileType = filename.substring(filename.lastIndexOf("."));
            Workbook importBook = switchWorkBookType(fileType, inputStream);

            // 获取导入数据Sheet页
            Map<String, Sheet> dataSheets = getSheets(importBook);
            dataSheetsData = analyzingSheets(dataSheets);
        } catch (IOException e) {
            throw new IOException("读取Excel文件出错,请检查文件完整性!");
        }
        return dataSheetsData;
    }

    public static JSONObject getRelatedEndObjByRelatedValue(@NotNull String pstrRelationValue, @NotNull List<JSONObject> pcolObject, @NotNull String pstrOtherEndSheetName, @NotNull SheetClassMapper classDefMapper, @NotNull SheetColumnPropertyMapper sheetColumnPropertyMapper) {
        ColumnToClassUid columnToClassUid = classDefMapper.get(pstrOtherEndSheetName);
        String lstrUIDPropDef = columnToClassUid.getUidTargetColumnName();
        if ("UID".equals(lstrUIDPropDef)) {
            return pcolObject.stream().filter(r -> pstrRelationValue.equalsIgnoreCase(getSpecialPropFormJSONObject(r, "Name"))).findFirst().orElse(null);
        } else {
            List<ColumnToProperty> columnToProperties = sheetColumnPropertyMapper.get(pstrOtherEndSheetName);
            ColumnToProperty columnToProperty = columnToProperties.stream().filter(r -> r.getColumnName().equals(lstrUIDPropDef)).findFirst().orElse(null);
            if (columnToProperty != null) {
                String propertyDefUID = columnToProperty.getPropertyDefUID();
                return pcolObject.stream().filter(r -> pstrRelationValue.equals(getSpecialPropFormJSONObject(r, propertyDefUID))).findFirst().orElse(null);
            }
            return null;
        }
    }

    public static String getSpecialPropFormJSONObject(@NotNull JSONObject jsonObject, @NotNull String pstrPropertyDefUID) {
        JSONObject propJson = jsonObject.getJSONObject(CommonUtility.JSON_FORMAT_PROPERTIES);
        if (propJson.containsKey(pstrPropertyDefUID)) {
            return propJson.getString(pstrPropertyDefUID);
        }
        return "";
    }

    public static List<JSONObject> getPointedClassDefJSONObjectFromItems(@NotNull String pstrClassDef, @NotNull JSONArray items) {
        List<JSONObject> jsonObjects = CommonUtility.toJSONObjList(items);
        return jsonObjects.stream().filter(r -> r.getString(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID).equals(pstrClassDef)).collect(Collectors.toList());
    }

    public static String getPropDefFromPropMapperByColumnName(@NotNull SheetColumnPropertyMapper sheetColumnPropertyMapper, @NotNull SheetRelationship relMap) {
        List<ColumnToProperty> columnToProperties = sheetColumnPropertyMapper.get(relMap.getEnd2SheetName());
        ColumnToProperty columnToProperty = columnToProperties.stream().filter(r -> {
            String lstrMapColName = r.getMust() ? "*" + r.getColumnName() : r.getColumnName();
            return lstrMapColName.equalsIgnoreCase(relMap.getRel1UidInRel2ColumnName());
        }).findFirst().orElse(null);
        return columnToProperty != null ? columnToProperty.getPropertyDefUID() : "UID";
    }

    public static List<SheetRelationship> generateRelationshipMapper(@NotNull Map<String, List<Map<String, Object>>> ldicSheetData) {
        List<Map<String, Object>> lcolRelMaps = ldicSheetData.get(ExcelSystemSheetConfig.MAPPER_SHEET_RELATIONSHIP);
        if (CommonUtility.hasValue(lcolRelMaps)) {
            List<SheetRelationship> lcolResult = new ArrayList<>();
            for (Map<String, Object> mapData : lcolRelMaps) {
                lcolResult.add(SheetRelationship.generateSheetRelationShipByRowData(mapData));
            }
            return lcolResult;
        }
        return null;
    }


    private static JSONObject generatePropJSONStructure(@NotNull SheetColumnPropertyMapper propertyMapData, @NotNull String pstrSheetName, @NotNull Map<String, Object> rowData, String pstrClassDef) throws Exception {
        JSONObject propJson = new JSONObject();

        List<ColumnToProperty> columnToProperties = propertyMapData.get(pstrSheetName);
        if (!CommonUtility.hasValue(columnToProperties)) {
            throw new Exception("未找到sheet:" + pstrSheetName + "的属性映射项");
        }
        for (Map.Entry<String, Object> columnData : rowData.entrySet()) {
            Object value = columnData.getValue();
            String lstrColumnName = columnData.getKey();
            ColumnToProperty columnToProperty = columnToProperties.stream().filter(r -> r.getColumnName().equals(lstrColumnName)).findFirst().orElse(null);
            if (columnToProperty != null) {
                if (isPropValueRequired(columnToProperty) && (value == null || StringUtils.isEmpty(value.toString()))) {
                    throw new Exception("列:" + lstrColumnName + "没有给出必须的值");
                }
                String lstrPropDef = columnToProperty.getPropertyDefUID();
                String fixedValueFromPropMap = columnToProperty.getDefaultValue();
                if (!StringUtils.isEmpty(fixedValueFromPropMap)) {
                    propJson.put(lstrPropDef, fixedValueFromPropMap);
                } else {
                    propJson.put(lstrPropDef, value);
                }
            }
        }
        if (columnToProperties.stream().noneMatch(r -> r.getPropertyDefUID().equals("UID"))) {

        }
        return propJson;
    }

    public static boolean isPropValueRequired(@NotNull ColumnToProperty pointedPropMap) {
        return pointedPropMap.getMust();
    }


    public static String getClassDefUIDFromClassDefMapData(@NotNull SheetClassMapper classDefMapData, @NotNull String pstrSheetName) {
        ColumnToClassUid columnToClassUid = classDefMapData.get(pstrSheetName);
        return columnToClassUid.getTargetClassDef();
    }

    public static Map<String, Sheet> getSheetDataByWorkBook(@NotNull Workbook workbook, boolean needCheckSheet) throws Exception {
        Map<String, Sheet> lcolResult = new HashMap<>();
        // 获取所有Sheet页 (从左到右依次获取)
        int lintSheetNum = workbook.getNumberOfSheets();
        List<String> sheetNames = new ArrayList<>();
        for (int i = 0; i < lintSheetNum; i++) {
            String sheetName = workbook.getSheetName(i);
            sheetNames.add(sheetName);
        }
        if (needCheckSheet) {
            if (!checkSystemSheet(sheetNames))
                throw new Exception("Excel模板sheet页不完整!");
        }
        for (String lstrSystemSheetName : sheetNames) {
            Sheet sheet = workbook.getSheet(lstrSystemSheetName);
            if (sheet == null) throw new Exception("未找到" + lstrSystemSheetName + "的sheet页,请检查模板!");
            lcolResult.put(lstrSystemSheetName, sheet);
        }
        return lcolResult;
    }

    public static Map<String, Sheet> getSheetDataByWorkBook(@NotNull Workbook workbook) throws Exception {
        return getSheetDataByWorkBook(workbook, true);
    }

    public static boolean checkFileType(@NotNull String pstrFileName, @NotNull String pstrFileType) {
        String fileNameExtension = CommonUtility.getFileNameExtWithoutPoint(pstrFileName);
        return pstrFileType.equalsIgnoreCase(fileNameExtension);
    }

    /**
     * 导入模板Excel文件
     * <p>支持".xls"或".xlsx"格式</p>
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static JSONObject importTemplateExcel(MultipartFile file) throws Exception {
        JSONObject result = new JSONObject();
        // 检查文件及文件类型
        if (file == null || file.isEmpty() || !checkFileType(file)) {
            throw new Exception("文件为空或文件类型不是\".xls\"或\".xlsx\"类型!");
        }
        try (InputStream inputStream = file.getInputStream()) {
            // 获取文件后缀
            String filename = file.getOriginalFilename();
            String fileType = filename.substring(filename.lastIndexOf("."));
            Workbook importBook = switchWorkBookType(fileType, inputStream);
            // 2023.03.07 HT 删除说明页
            Sheet infoSheet = importBook.getSheet("说明");
            if (infoSheet != null){
                importBook.removeSheetAt(importBook.getSheetIndex(infoSheet));
            }
            // 2023.03.07 HT 删除说明页

            // 获取系统配置Sheet页
            Map<String, Sheet> systemSheets = getSystemSheets(importBook);
            // 获取导入数据Sheet页
            Map<String, Sheet> dataSheets = getDataSheets(importBook);
            Map<String, List<Map<String, Object>>> systemSheetsData = analyzingSheets(systemSheets);
            Map<String, List<Map<String, Object>>> dataSheetsData = analyzingSheets(dataSheets);
            // 生成 对象类型配置
            log.info("【Excel解析】-开始解析对象类型配置...");
            long s1 = System.currentTimeMillis();
            SheetClassMapper sheetClassMapper = genSheetClassMapper(systemSheetsData);
            //log.debug("SheetClassMapper:" + sheetClassMapper);
            log.info("【Excel解析】-解析对象类型配置耗时{}ms", System.currentTimeMillis() - s1);
            // 生成 类型接口配置
            log.info("【Excel解析】-开始解析类型接口配置...");
            long s2 = System.currentTimeMillis();
            ClassInterfaceMapper classInterfaceMapper = generateClassInterfaceMapper(systemSheetsData);
            //log.debug("ClassInterfaceMapper:" + classInterfaceMapper);
            log.info("【Excel解析】-解析类型接口配置耗时{}ms", System.currentTimeMillis() - s2);
            // 生成 字段映射配置
            log.info("【Excel解析】-开始解析字段映射配置...");
            long s3 = System.currentTimeMillis();
            SheetColumnPropertyMapper sheetColumnPropertyMapper = genSheetColumnPropertyMapper(systemSheetsData);
            //log.debug("SheetColumnPropertyMapper:" + sheetColumnPropertyMapper);
            log.info("【Excel解析】-解析字段映射配置耗时{}ms", System.currentTimeMillis() - s3);
            // 生成 关系配置
            log.info("【Excel解析】-开始解析关系配置...");
            long s4 = System.currentTimeMillis();
            SheetRelationshipMapper sheetRelationshipMapper = genSheetRelationshipMapper(systemSheetsData);
            log.info("【Excel解析】-解析关系配置耗时{}ms", System.currentTimeMillis() - s4);
            //log.debug("SheetRelationshipMapper:" + sheetRelationshipMapper);
            log.info("【Excel解析】-开始补全关系配置...");
            long s5 = System.currentTimeMillis();
            completingSheetRelationshipMapper(sheetRelationshipMapper, sheetClassMapper);
            log.info("【Excel解析】-补全关系配置耗时{}ms", System.currentTimeMillis() - s5);
            // 生成 导入格式数据
            log.info("【Excel解析】-开始生成导入格式数据...");
            long s6 = System.currentTimeMillis();
            result = genImportData(dataSheetsData,
                    sheetClassMapper, classInterfaceMapper,
                    sheetColumnPropertyMapper, sheetRelationshipMapper);
            log.info("【Excel解析】-生成导入格式数据耗时{}ms", System.currentTimeMillis() - s6);
        } catch (Exception e) {
            log.error("【Excel解析错误】-读取Excel文件出错,{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            throw new Exception("【Excel解析错误】-读取Excel文件出错,请检查文件完整性!" + ExceptionUtil.getSimpleMessage(e));
        }
        return result;
    }

    /**
     * 将数据按mapper封装成可导入的json数据
     *
     * @param dataSheetsData
     * @param sheetClassMapper
     * @param classInterfaceMapper
     * @param sheetColumnPropertyMapper
     * @param sheetRelationshipMapper
     * @return JSONObject
     */
    private static JSONObject genImportData(Map<String, List<Map<String, Object>>> dataSheetsData,
                                            SheetClassMapper sheetClassMapper,
                                            ClassInterfaceMapper classInterfaceMapper,
                                            SheetColumnPropertyMapper sheetColumnPropertyMapper,
                                            SheetRelationshipMapper sheetRelationshipMapper) throws Exception {
        // 2022.07.13 HT 获取项目名称
        ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(null);
        String configName = configurationItem.Name();
        // 2022.07.13 HT 获取项目名称

        JSONObject items = new JSONObject();
        JSONArray objects = new JSONArray();
        items.put(ITEMS, objects);
        // 所有涉及到关联关系的Sheet页
        Set<String> sheetNamesWithRel = sheetRelationshipMapper.getSheetNamesWithRel();
        /*
        数据对象填充
         */
        for (String sheetName : dataSheetsData.keySet()) {
            // 获取对应Sheet和ClassDef对应
            ColumnToClassUid columnToClassUid = sheetClassMapper.get(sheetName);
            if (null == columnToClassUid) {
                log.error("【Excel解析错误】-获取Sheet页对应ClassDef信息失败!SheetName:[{}].", sheetName);
                throw new Exception("【Excel解析错误】-获取Sheet页对应ClassDef信息失败!SheetName:[" + sheetName + "].");
            }
            String targetClassDef = columnToClassUid.getTargetClassDef();
            boolean isUnderControl = CIMContext.Instance.ProcessCache().isDefinitionUnderConfigControl(targetClassDef);
            // 获取Sheet页对应对象所包含的所有接口
            List<String> interfaceDefs = classInterfaceMapper.get(targetClassDef);
            // 获取 Sheet页 列 属性名 等相关对应
            List<ColumnToProperty> columnToProperties = sheetColumnPropertyMapper.get(sheetName);
            // 获取 Sheet页 关联关系 等相关对应
            List<SheetRelationship> sheetRelationships = sheetRelationshipMapper.get(sheetName);
            if (null == columnToProperties) {
                throw new RuntimeException("未找到Sheet页名称为[" + sheetName + "]的属性配置项,请检查Excel配置!");
            }
            boolean isRel2Obj = sheetNamesWithRel.contains(sheetName);
            // 数据Map集合
            List<Map<String, Object>> dataMap = dataSheetsData.get(sheetName);
            if (null == dataMap) {
                log.error("【Excel解析错误】-获取数据Map集合信息失败!SheetName:[{}]", sheetName);
                throw new Exception("【Excel解析错误】-获取数据Map集合信息失败!SheetName:[" + sheetName + "].");
            }
            /*
            遍历每条数据并插入properties
             */
            for (int i = 0; i < dataMap.size(); i++) {
                // 单行数据
                Map<String, Object> rowDataMap = dataMap.get(i);
                // 构建 ObjectJson 并填充 classDefinitionUID
                JSONObject dataObj = genObjectDataJson(targetClassDef);
                // 填充 INTERFACES
                JSONArray interfacesJsonArray = dataObj.getJSONArray(INTERFACES);
                interfacesJsonArray.addAll(interfaceDefs);
                // 填充 PROPERTIES
                JSONObject propertiesJsonObject = dataObj.getJSONObject(PROPERTIES);
                // 遍历对应Sheet页拥有的属性
                for (ColumnToProperty columnToProperty : columnToProperties) {
                    // 字段名称
                    String columnName = columnToProperty.getColumnName();
                    log.debug("【Excel解析】-开始属性[{}]值填充...", columnName);
                    // 属性值
                    Object value = rowDataMap.get(columnName);
                    // 2023.01.12 HT 添加属性匹配多表头方法
                    if (columnName.contains(",")) {
                        String[] split = columnName.split(",");
                        log.debug("【Excel解析】-属性对应表头包含',',进行数据匹配...");
                        for (String s : split) {
                            value = rowDataMap.get(s);
                            log.debug("【Excel解析】-表头:{},值:{}.", s, value);
                            if (value != null) {
                                log.debug("【Excel解析】-匹配到属性对应表头值.");
                                break;
                            }
                        }
                        log.debug("【Excel解析】-属性匹配多表头结束.");
                    }
                    // 2023.01.12 HT 添加属性匹配多表头方法
                    // 默认值
                    String defaultValue = columnToProperty.getDefaultValue();

                    // 属性值填充判断
                    if (StringUtils.isEmpty(defaultValue)) {
                        if ((value == null || StringUtils.isEmpty(value)) && columnToProperty.getMust()) {
                            log.error("【Excel解析错误】-Sheet页[{}]的第[{}]行数据的[{}]字段为必填,但值为空并且没有默认值,请检查Excel数据!", sheetName, i + 1, columnName);
                            throw new Exception("【Excel解析错误】-Sheet页[" + sheetName + "]的第[" + (i + 1) + "]行数据的[" + columnName + "]字段为必填,但值为空并且没有默认值,请检查Excel数据!");
                        }
                        // 2022.07.13 HT 判断ClassDef是否为受控对象,添加项目标识 start
                        if (columnToProperty.getPropertyDefUID().equalsIgnoreCase("UID")
                                && isUnderControl) {
                            if (!value.toString().contains(configName)) {
                                value = value + "_" + configName;
                            }
                        }
                        // 2022.07.13 HT 判断ClassDef是否为受控对象,添加项目标识 end
                        // 2023.04.03 HT 添加属性字段类型检测 start
                        String propertyValueTypeClassDefForPropertyDefinition = CIMContext.Instance.ProcessCache().getPropertyValueTypeClassDefForPropertyDefinition(columnToProperty.getPropertyDefUID());
                        if (null == propertyValueTypeClassDefForPropertyDefinition) {
                            log.error("【Excel解析错误】-Sheet页[{}]的第[{}]行数据的[{}]字段未获取到对应的属性类型,请检查系统数据!", sheetName, i + 1, columnName);
                            throw new Exception("【Excel解析错误】-Sheet页[" + sheetName + "]的第[" + (i + 1) + "]行数据的[" + columnName + "]字段未获取到对应的属性类型,请检查系统数据!");
                        }
                        if (null != value && StringUtils.hasText(value.toString())) {
                            if (propertyDefinitionType.IntegerType.name().equals(propertyValueTypeClassDefForPropertyDefinition)
                                    || propertyDefinitionType.DoubleType.name().equals(propertyValueTypeClassDefForPropertyDefinition)) {
                                try {
                                    Double aDouble = Double.valueOf(value.toString());
                                } catch (NumberFormatException e) {
                                    log.error("【Excel解析错误】-Sheet页[{}]的第[{}]行数据的[{}]字段类型为数字类型,但值为[{}],解析失败,请检查Excel数据!", sheetName, i + 1, columnName, value);
                                    throw new Exception("【Excel解析错误】-Sheet页[" + sheetName + "]的第[" + (i + 1) + "]行数据的[" + columnName + "]字段类型为数字类型,但值为[" + value + "],请检查Excel数据!");
                                }
                            }
                            if (propertyDefinitionType.BooleanType.name().equals(propertyValueTypeClassDefForPropertyDefinition)){
                                if ("是".equals(value.toString())){
                                    value = "true";
                                }
                                if ("否".equals(value.toString())) {
                                    value = "false";
                                }
                            }
                        }
                        // 2023.04.03 HT 添加属性字段类型检测 end
                        propertiesJsonObject.put(columnToProperty.getPropertyDefUID(), value);
                    } else {// 有默认值的如果为空则填写默认值
                        if (StringUtils.isEmpty(value)) {
                            if (columnToProperty.needParams()) {
                                // 存在占位符的默认值处理
                                for (int j = 0; j < columnToProperty.getDefaultValueItems().size(); j++) {
                                    Object param = null;
                                    // 2023.01.13 HT 默认值项添加多字段检查
                                    String defaultValueItem = columnToProperty.getDefaultValueItems().get(j);
                                    if (defaultValueItem.contains(",")) {
                                        String[] split = defaultValueItem.split(",");
                                        log.debug("【Excel解析】-默认值占位引用对应表头包含',',进行数据匹配...");
                                        for (String s : split) {
                                            param = rowDataMap.get(s);
                                            log.debug("【Excel解析】-默认值占位引用对应表头:{},值:{}.", s, param);
                                            if (param != null) {
                                                log.debug("【Excel解析】-匹配到默认值占位引用对应表头值.");
                                                break;
                                            }
                                        }
                                        log.debug("【Excel解析】-填充默认值占位引用对应表头结束.");
                                    } else {
                                        param = rowDataMap.get(defaultValueItem);
                                    }
                                    // 2023.01.13 HT 默认值项添加多字段检查
                                    if (StringUtils.isEmpty(param)) {
                                        log.error("【Excel解析错误】-默认值填充失败,默认值引用参数为空!属性名称:{}字段属性信息:{}.", columnToProperty.getColumnName(), columnToProperty);
                                        throw new Exception("【Excel解析错误】-默认值填充失败,默认值引用参数为空!属性名称:"
                                                + columnToProperty.getColumnName() + "字段属性信息:" + columnToProperty);
                                    }
                                    defaultValue = ColumnToProperty.createDefaultValue(param.toString(), j, defaultValue);
                                }
                            }
                            // 2022.07.13 HT 判断ClassDef是否为受控对象,添加项目标识
                            if (columnToProperty.getPropertyDefUID().equalsIgnoreCase("UID")
                                    && isUnderControl) {
                                if (!defaultValue.contains(configName)) {
                                    defaultValue = defaultValue + "_" + configName;
                                }
                            }
                            // 2022.07.13 HT 判断ClassDef是否为受控对象,添加项目标识
                            propertiesJsonObject.put(columnToProperty.getPropertyDefUID(), defaultValue);
                        } else {
                            // 2022.07.13 HT 判断ClassDef是否为受控对象,添加项目标识
                            if (columnToProperty.getPropertyDefUID().equalsIgnoreCase("UID")
                                    && isUnderControl) {
                                if (!value.toString().contains(configName)) {
                                    value = value + "_" + configName;
                                }
                            }
                            // 2022.07.13 HT 判断ClassDef是否为受控对象,添加项目标识
                            propertiesJsonObject.put(columnToProperty.getPropertyDefUID(), value);
                        }
                    }
                }
                objects.add(dataObj);
                // 当次Sheet页存在关联关系时
                if (isRel2Obj) {
                    for (SheetRelationship sheetRelationship : sheetRelationships) {
                        JSONObject relObj = genRelDataJson();
                        JSONObject relProperties = relObj.getJSONObject(PROPERTIES);
                        String uid2ColumnName = sheetRelationship.getUid2ColumnName();
                        // 二端对象uid
                        Object rel2Value = rowDataMap.get(uid2ColumnName);
                        if (StringUtils.isEmpty(rel2Value)) {
                            // 2022.03.30 HT 添加根据属性做为标识的支持
                            rel2Value = propertiesJsonObject.get(uid2ColumnName);
                            if (StringUtils.isEmpty(rel2Value)) {
                                log.error("【Excel解析错误】-生成关联关系[{}]失败,二端对象[{}]标识列/标识属性[{}]值为空,请检查Excel配置!",
                                        sheetRelationship.getRelDefUID(), sheetRelationship.getEnd2SheetName(), uid2ColumnName);
                                throw new Exception("【Excel解析错误】-生成关联关系["
                                        + sheetRelationship.getRelDefUID()
                                        + "]失败,二端对象["
                                        + sheetRelationship.getEnd2SheetName()
                                        + "]标识列/标识属性["
                                        + uid2ColumnName
                                        + "]值为空,请检查Excel配置!");
                            }
                        }
                        String rel2Uid = rel2Value.toString();
                        // 一段对象 uid
                        String rel1UidInRel2ColumnName = sheetRelationship.getRel1UidInRel2ColumnName();
                        Object rel1ValueInRel2 = rowDataMap.get(rel1UidInRel2ColumnName);
                        if (StringUtils.isEmpty(rel1ValueInRel2)) {
                            // 2022.07.22 HT 如果一段对象UID配置的不是标题列,从属性中查询
                            if (rel1UidInRel2ColumnName.contains("{{")) {
                                StringBuffer sb = new StringBuffer();
                                List<String> itemList = new ArrayList<>();
                                ColumnToProperty.parseDefaultValue(rel1UidInRel2ColumnName, sb, itemList);
                                for (int j = 0; j < itemList.size(); j++) {
                                    Object param = rowDataMap.get(itemList.get(j));
                                    if (StringUtils.isEmpty(param)) {
                                        log.error("【Excel解析错误】-一段对象UID填充失败,未找到引用参数!");
                                        throw new Exception("【Excel解析错误】-一段对象UID填充失败,未找到引用参数!");
                                    }
                                    rel1ValueInRel2 = ColumnToProperty.createDefaultValue(param.toString(), j, sb.toString());
                                }
                            }
                            // 2022.07.22 HT 如果一段对象UID配置的不是标题列,从属性中查询
                            if (StringUtils.isEmpty(rel1ValueInRel2)) {
                                log.error("【Excel解析错误】-生成关联关系[{}]失败,一段端对象[{}]的标识列/标识属性对应在二端对象[{}]的列[{}]的值为空,请检查Excel配置!",
                                        sheetRelationship.getRelDefName(), sheetRelationship.getEnd1SheetName(), sheetRelationship.getEnd2SheetName(), rel1UidInRel2ColumnName);
                                throw new Exception("【Excel解析错误】-生成关联关系["
                                        + sheetRelationship.getRelDefName()
                                        + "]失败,一段端对象["
                                        + sheetRelationship.getEnd1SheetName()
                                        + "]的标识列/标识属性对应在二端对象["
                                        + sheetRelationship.getEnd2SheetName()
                                        + "]的列["
                                        + rel1UidInRel2ColumnName
                                        + "]的值为空,请检查Excel配置!");
                            }
                        }
                        String rel1UidInRel2 = rel1ValueInRel2.toString();
                        // 2022.07.13 HT 判断ClassDef是否为受控对象,添加项目标识
                        if (isUnderControl) {
                            if (!rel1UidInRel2.contains(configName)) {
                                rel1UidInRel2 = rel1UidInRel2 + "_" + configName;
                            }
                            if (!rel2Uid.contains(configName)) {
                                rel2Uid = rel2Uid + "_" + configName;
                            }
                        }
                        // 2022.07.13 HT 判断ClassDef是否为受控对象,添加项目标识
                        relProperties.put(PROPERTIES_UID1, rel1UidInRel2);
                        relProperties.put(PROPERTIES_UID2, rel2Uid);
                        relProperties.put(PROPERTIES_REL_DEF_UID, sheetRelationship.getRelDefUID());
                        objects.add(relObj);
                    }
                }
            }
        }
        /*
        关系对象填充
         */
        /*for (String sheetName : sheetRelationshipMapper.keySet()) {
            List<SheetRelationship> sheetRelationships = sheetRelationshipMapper.get(sheetName);
            for (SheetRelationship sheetRelationship : sheetRelationships) {
                String uid2SheetName = sheetRelationship.getEnd2SheetName();
                String uid1SheetName = sheetRelationship.getEnd1SheetName();
            }
        }*/
        return items;
    }

    /**
     * 生成Object的Json
     *
     * @return
     */
    public static JSONObject genObjectDataJson(String classDef) {
        JSONObject object = new JSONObject();
        object.put(CLASS_DEFINITION_UID, classDef);
        object.put(INTERFACES, new JSONArray());
        object.put(PROPERTIES, new JSONObject());
        return object;
    }

    /**
     * 生成Rel的Json
     *
     * @return
     */
    public static JSONObject genRelDataJson() {
        JSONObject object = new JSONObject();
        // 填充 CLASS_DEFINITION_UID
        object.put(CLASS_DEFINITION_UID, CLASS_DEFINITION_UID_REL);
        // 填充 INTERFACES
        JSONArray interfaces = new JSONArray();
        interfaces.add(INTERFACES_I_REL);
        interfaces.add(INTERFACES_I_OBJECT);
        // 2022.06.28 HT 修复找不到 IRelCustom 接口问题
        //interfaces.add(INTERFACES_I_REL_CUSTOM);
        object.put(INTERFACES, interfaces);
        object.put(PROPERTIES, new JSONObject());
        return object;
    }

    /**
     * 补全关系配置
     *
     * @param sheetRelationshipMapper
     * @param sheetClassMapper
     */
    public static void completingSheetRelationshipMapper(SheetRelationshipMapper sheetRelationshipMapper, SheetClassMapper sheetClassMapper) {
        for (String sheetName : sheetRelationshipMapper.keySet()) {
            // Sheet页标识列和ClassDefUID的对应关系
            ColumnToClassUid columnToClassUid = sheetClassMapper.get(sheetName);
            List<SheetRelationship> sheetRelationships = sheetRelationshipMapper.get(sheetName);
            sheetRelationships.forEach(sr -> {
                // 补全 Rel2 的标识列
                sr.setUid2ColumnName(columnToClassUid.getUidTargetColumnName());
                // 补全 Rel1 的标识列
                sr.setUid1ColumnName(sheetClassMapper.get(sr.getEnd1SheetName()).getUidTargetColumnName());
            });
        }
    }

    /**
     * 获取 关系配置
     *
     * @param systemSheetsData
     * @return
     */
    public static SheetRelationshipMapper genSheetRelationshipMapper(Map<String, List<Map<String, Object>>> systemSheetsData) {
        List<Map<String, Object>> sheetClassData = systemSheetsData.get(ExcelSystemSheetConfig.MAPPER_SHEET_RELATIONSHIP);
        SheetRelationshipMapper sheetRelationshipMapper = new SheetRelationshipMapper();
        List<SheetRelationship> allSR = new ArrayList<SheetRelationship>();
        // 用来生成 Map key 的SheetName集合
        List<String> rel2SheetNames = new ArrayList<>();
        for (Map<String, Object> dataMap : sheetClassData) {
            SheetRelationship sheetRelationship = new SheetRelationship();
            for (String titleName : dataMap.keySet()) {
                Object value = dataMap.get(titleName);
                // Sheet页名称集合
                if (titleName.equals(ExcelSystemSheetConfig.TITLE_REL2_SHEET_NAME)) {
                    String rel2SheetName = value.toString().trim();
                    rel2SheetNames.add(rel2SheetName);
                    sheetRelationshipMapper.addSheetNameWithRel(rel2SheetName);
                }
                sheetRelationship.toSetValue(titleName, value);
            }
            allSR.add(sheetRelationship);
        }
        // 提取生成 Map (根据 Rel2 生成Map)
        for (String sheetName : rel2SheetNames) {
            List<SheetRelationship> collect = allSR.stream().filter(
                    columnToProperty ->
                            columnToProperty.getEnd2SheetName().equals(sheetName)
            ).distinct().collect(Collectors.toList());
            sheetRelationshipMapper.put(sheetName, collect);
        }
        return sheetRelationshipMapper;
    }

    /**
     * 获取 字段映射配置
     *
     * @param systemSheetsData
     * @return
     */
    public static SheetColumnPropertyMapper genSheetColumnPropertyMapper(Map<String, List<Map<String, Object>>> systemSheetsData) throws Exception {
        List<Map<String, Object>> sheetClassData = systemSheetsData.get(ExcelSystemSheetConfig.MAPPER_SHEET_COLUMN_PROPERTY);
        SheetColumnPropertyMapper sheetColumnPropertyMapper = new SheetColumnPropertyMapper();
        List<ColumnToProperty> allCTP = new ArrayList<ColumnToProperty>();
        ArrayList<String> sheetNames = new ArrayList<>();
        for (Map<String, Object> dataMap : sheetClassData) {
            ColumnToProperty columnToProperty = new ColumnToProperty();
            for (String titleName : dataMap.keySet()) {
                // Sheet页名称集合
                if (titleName.equals(ExcelSystemSheetConfig.TITLE_SHEET_NAME)) {
                    sheetNames.add(dataMap.get(titleName).toString().trim());
                }
                columnToProperty.toSetValue(titleName, dataMap.get(titleName));
            }
            if (StringUtils.isEmpty(columnToProperty.getColumnName())
                    && columnToProperty.getMust()
                    && StringUtils.isEmpty(columnToProperty.getDefaultValue())) {
                throw new Exception("【Excel解析错误】-生成字段映射配置失败,必填属性["
                        + columnToProperty.getPropertyDefUID()
                        + "]字段名和默认值为空,请检查Excel配置!");
            }
            allCTP.add(columnToProperty);
        }
        // 提取生成 Map
        for (String sheetName : sheetNames) {
            List<ColumnToProperty> collect = allCTP.stream().filter(
                    columnToProperty ->
                            columnToProperty.getSheetName().equals(sheetName)
            ).distinct().collect(Collectors.toList());
            sheetColumnPropertyMapper.put(sheetName, collect);
        }
        return sheetColumnPropertyMapper;
    }

    /**
     * 获取 类型接口配置
     *
     * @param systemSheetsData
     * @return
     */
    public static ClassInterfaceMapper generateClassInterfaceMapper(Map<String, List<Map<String, Object>>> systemSheetsData) {
        List<Map<String, Object>> sheetClassData = systemSheetsData.get(ExcelSystemSheetConfig.MAPPER_CLASS_INTERFACE);
        ClassInterfaceMapper classInterfaceMapper = new ClassInterfaceMapper();
        for (Map<String, Object> dataMap : sheetClassData) {
            classInterfaceMapper.putOneInterfaces(
                    dataMap.get(ExcelSystemSheetConfig.TITLE_CLASS_NAME).toString(),
                    dataMap.get(ExcelSystemSheetConfig.TITLE_INTERFACE_NAME).toString()
            );
        }
        return classInterfaceMapper;
    }

    /**
     * 获取 对象类型配置
     *
     * @param systemSheetsData
     * @return
     */
    public static SheetClassMapper genSheetClassMapper(Map<String, List<Map<String, Object>>> systemSheetsData) {
        List<Map<String, Object>> sheetClassData = systemSheetsData.get(ExcelSystemSheetConfig.MAPPER_SHEET_CLASS);
        SheetClassMapper sheetClassMapper = new SheetClassMapper();
        for (Map<String, Object> dataMap : sheetClassData) {
            ColumnToClassUid columnToClassUid = new ColumnToClassUid();
            for (String titleName : dataMap.keySet()) {
                columnToClassUid.toSetValue(titleName, dataMap.get(titleName));
            }
            sheetClassMapper.put(columnToClassUid.getSheetName(), columnToClassUid);
        }
        return sheetClassMapper;
    }

    /**
     * 解析复数Sheet页
     * <p>
     * key:sheet页名称,
     * value:每行数据(value-key:列名 value-value:列数据)
     * </p>
     *
     * @param sheets
     * @throws Exception
     */
    public static Map<String, List<Map<String, Object>>> analyzingSheets(Map<String, Sheet> sheets) throws Exception {
        Map<String, List<Map<String, Object>>> sheetsData = new HashMap<>();
        for (String sheetName : sheets.keySet()) {
            Sheet sheet = sheets.get(sheetName);
            List<Map<String, Object>> sheetData = getSheetData(sheet);
            sheetsData.put(sheetName, sheetData);
        }
        return sheetsData;
    }

    /**
     * 获取Sheet页数据
     * <p>
     * 解析的sheet页的数据,
     * key:列名,value:列数据
     * </p>
     *
     * @param sheet
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> getSheetData(Sheet sheet) throws Exception {
        Row titleRow = sheet.getRow(0);
        if (titleRow == null) {
            log.error("【Excel解析错误】-Sheet页{}的标题行为空,请检查数据!", sheet.getSheetName());
            throw new Exception("【Excel解析错误】-Sheet页{" + sheet.getSheetName() + "}的标题行为空,请检查数据!");
        }
        // 获取列标题
        String[] titles = getTitles(titleRow);
        return getRowDataList(sheet, titles);
    }

    /**
     * 获取Sheet页数据并与Title对应
     *
     * @param sheet
     * @param titles
     * @return
     */
    private static List<Map<String, Object>> getRowDataList(Sheet sheet, String[] titles) {
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        log.debug("Sheet名称:" + sheet.getSheetName() + ",总行数:" + sheet.getLastRowNum());
        for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
            Row dataRow = sheet.getRow(i);
            // 空行跳过
            if (isRowEmpty(dataRow)) continue;
            log.debug("当前行:" + dataRow.getRowNum() + ",总列数:" + dataRow.getLastCellNum());
            Map<String, Object> columnData = new HashMap<>();
            for (int j = 0; j < titles.length; j++) {
                Cell cell = dataRow.getCell(j);
                String title = titles[j];
                columnData.put(title, getCellValue(cell));
            }
            log.debug("数据项:" + columnData);
            dataList.add(columnData);
        }
        return dataList;
    }

    /**
     * 检测是否为空行
     *
     * @param row
     * @return
     */
    private static boolean isRowEmpty(Row row) {
        if (null == row) {
            return true;
        }
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                Object cellValueObj = getCellValue(cell);
                if (null != cellValueObj) {
                    String cellValue = String.valueOf(cellValueObj);
                    if (StringUtils.hasText(cellValue)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 获取列标题
     *
     * @param titleRow
     * @return
     */
    private static String[] getTitles(Row titleRow) {
        String[] titles = new String[titleRow.getLastCellNum()];
        for (int i = 0; i < titleRow.getLastCellNum(); i++) {
            Cell titleCell = titleRow.getCell(i);
            titles[i] = getCellValue(titleCell).toString();
        }
        return titles;
    }

    /**
     * 获取单元格数据
     *
     * @param cell
     * @return
     */
    public static Object getCellValue(Cell cell) {
        Object value = null;
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case FORMULA:
                try {
                    value = cell.getNumericCellValue();
                } catch (IllegalStateException e) {
                    value = cell.getRichStringCellValue().getString().trim();
                }
                break;
            case STRING:
                value = cell.getRichStringCellValue().getString().trim();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 日期类型 date
                    value = cell.getDateCellValue();
                } else {
                    // 数值类型 double
                    value = cell.getNumericCellValue();
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case BLANK:
            default:
                value = "";
                break;
        }
        return value;
    }

    /**
     * 根据Sheet页名称获取所有的Sheet
     * <p>
     * key:Sheet页名称,value:Sheet页对象
     * </p>
     *
     * @param importBook
     * @return
     */
    public static Map<String, Sheet> getSheets(Workbook importBook) {
        // 获取所有Sheet页 (从左到右依次获取)
        int sheetNumber = importBook.getNumberOfSheets();
        List<String> sheetNames = new ArrayList<>();
        for (int i = 0; i < sheetNumber; i++) {
            String sheetName = importBook.getSheetName(i);
            // 2023.03.07 HT 跳过说明页
            if ("说明".equalsIgnoreCase(sheetName)) {
                continue;
            }
            sheetNames.add(sheetName);
        }
        Map<String, Sheet> dataSheets = new HashMap<>();
        for (String sheetName : sheetNames) {
            Sheet sheet = importBook.getSheet(sheetName);
            dataSheets.put(sheetName, sheet);
        }
        return dataSheets;
    }

    /**
     * 获取系统Sheet页
     * <p>
     * 根据 {@link  ExcelSystemSheetConfig#SYSTEM_SHEET_NAMES}
     * 中的系统Sheet名称获取Sheet页对象
     * </p>
     *
     * @param importBook
     * @return
     */
    public static Map<String, Sheet> getSystemSheets(Workbook importBook) throws Exception {
        List<String> sheetNames = getSheetNames(importBook);
        boolean b = checkSystemSheet(sheetNames);
        if (!b) {
            throw new Exception("Excel文件配置页不全,请检查文件完整性!");
        }
        Map<String, Sheet> systemSheets = new HashMap<>();
        for (String systemSheetName : ExcelSystemSheetConfig.SYSTEM_SHEET_NAMES) {
            Sheet sheet = importBook.getSheet(systemSheetName);
            systemSheets.put(systemSheetName, sheet);
        }
        return systemSheets;
    }

    /**
     * 获取数据Sheet页
     * <p>
     * 获取除 {@link  ExcelSystemSheetConfig#SYSTEM_SHEET_NAMES}
     * 中的系统Sheet名称以外Sheet页对象
     * </p>
     *
     * @param importBook
     * @return
     */
    public static Map<String, Sheet> getDataSheets(Workbook importBook) throws Exception {
        List<String> sheetNames = getSheetNames(importBook);
        // 去除所有系统Sheet页名称
        sheetNames.removeAll(ExcelSystemSheetConfig.SYSTEM_SHEET_NAMES);
        // 获取导入数据Sheet页
        Map<String, Sheet> dataSheets = new HashMap<>();
        for (String sheetName : sheetNames) {
            Sheet sheet = importBook.getSheet(sheetName);
            dataSheets.put(sheetName, sheet);
        }
        return dataSheets;
    }

    /**
     * 获取所有Sheet页
     *
     * @param importBook
     * @return
     */
    public static List<String> getSheetNames(Workbook importBook) {
        // 获取所有Sheet页 (从左到右依次获取)
        int sheetNumber = importBook.getNumberOfSheets();
        List<String> sheetNames = new ArrayList<>();
        for (int i = 0; i < sheetNumber; i++) {
            String sheetName = importBook.getSheetName(i);
            sheetNames.add(sheetName);
        }
        return sheetNames;
    }

    /**
     * 检查Sheet页完整性
     * <p>
     * 检查是否确实系统默认Sheet页
     * </p>
     *
     * @param sheetNames
     * @return
     */
    private static boolean checkSystemSheet(List<String> sheetNames) {
        for (String systemSheetName : ExcelSystemSheetConfig.SYSTEM_SHEET_NAMES) {
            boolean contains = sheetNames.contains(systemSheetName);
            if (!contains) {
                return false;
            }
        }
        return true;
    }

    /**
     * 文件类型 "zip"
     */
    public static final String FILE_ZIP = "zip";

    /**
     * 文件后缀名 ".xls"
     */
    public static final String FILE_SUFFIX_XLS = ".xls";

    /**
     * 文件后缀名 ".xlsx"
     */
    public static final String FILE_SUFFIX_XLSX = ".xlsx";

    /**
     * 根据文件后缀切换使用的Workbook类型
     * <p>
     * Excel2003及以下使用 {@link HSSFWorkbook},
     * Excel2007及以上使用{@link XSSFWorkbook}
     * </p>
     *
     * @param fileType
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static Workbook switchWorkBookType(String fileType, InputStream inputStream) throws Exception {
        Workbook workbook;
        if (FILE_SUFFIX_XLS.equalsIgnoreCase(fileType)) {
            // 2003及以下版本使用 HSSFWorkbook
            workbook = new HSSFWorkbook(inputStream);
        } else if (FILE_SUFFIX_XLSX.equals(fileType)) {
            // 2007及以上版本使用 XSSFWorkbook
            workbook = new XSSFWorkbook(inputStream);
        } else {
            throw new Exception("解析文件格式有误!");
        }
        return workbook;
    }

    /**
     * 根据文件后缀切换使用的Workbook计算公式类型
     * <p>
     * Excel2003及以下使用 {@link HSSFWorkbook},
     * Excel2007及以上使用{@link XSSFWorkbook}
     * </p>
     *
     * @param fileType
     * @param importBook
     * @return
     * @throws Exception
     */
    public static FormulaEvaluator switchFormulaEvaluator(String fileType, Workbook importBook) throws Exception {
        FormulaEvaluator formulaEvaluator;
        if (FILE_SUFFIX_XLS.equalsIgnoreCase(fileType)) {
            // 2003及以下版本使用 HSSFWorkbook
            formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) importBook);
        } else if (FILE_SUFFIX_XLSX.equals(fileType)) {
            // 2007及以上版本使用 XSSFWorkbook
            formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) importBook);
        } else {
            throw new Exception("解析文件格式有误!");
        }
        return formulaEvaluator;
    }

    /**
     * 文件检查
     *
     * @param file
     * @throws Exception
     */
    private static void fileSimpleCheck(MultipartFile file) throws Exception {
        // 检查文件及文件类型
        if (file == null || file.isEmpty() || !checkFileType(file)) {
            throw new Exception("文件为空或文件类型不是\".xls\"或\".xlsx\"类型!");
        }
    }

    /**
     * 检查文件类型
     * <p>
     * 检查是否为.xls或.xlsx文件
     * </p>
     *
     * @param file
     * @return
     */
    private static boolean checkFileType(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (!StringUtils.isEmpty(filename)) {
            String fileType = filename.substring(filename.lastIndexOf("."));
            // 先根据后缀判断是否为 xls 或 xlsx
            if (FILE_SUFFIX_XLS.equalsIgnoreCase(fileType) || FILE_SUFFIX_XLSX.equalsIgnoreCase(fileType)) {
                String typeName;
                try {
                    // 根据文件头判断是否为 xlsx(zip类型) 或 xls
                    typeName = FileTypeUtil.getType(file.getInputStream());
                    if (FILE_ZIP.contains(typeName) || FILE_SUFFIX_XLS.contains(typeName)) {
                        return true;
                    }
                } catch (Exception e) {
                    log.error("读取Excel文件出错!", e);
                }
            }
        }
        return false;
    }

    /**
     * 下载模板
     */
    public static void downloadTemplate(HttpServletResponse response, InputStream inStr) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(inStr);
             ServletOutputStream out = response.getOutputStream();) {

            String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + XSSF;
            response.setHeader("content-disposition", "attachment;filename=" + fileName);

            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            log.error("下载模板出错!", e);
            e.printStackTrace();
        }
    }

    /**
     * 导出Excel为文件
     *
     * @param filePath
     * @param fileName
     * @param workbook
     * @throws Exception
     */
    public static File exportExcelFile(String filePath, String fileName, Workbook workbook) throws Exception {
        File file = new File(filePath, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
        } catch (Exception e) {
            throw new Exception("创建Excel文件失败!" + e.getMessage());
        }
        try (OutputStream os = new FileOutputStream(file)) {
            workbook.write(os);
        } catch (Exception e) {
            throw new Exception("导出Excel文件失败!" + e.getMessage());
        }
        return file;
    }


    private static String DIR_PATH;

    @Value("${excel.template.path}")
    private String dirPath;

    @PostConstruct
    public void init() {
        if (!dirPath.endsWith("/") && !dirPath.endsWith("\\")) {
            dirPath = dirPath + "/";
        }
        DIR_PATH = dirPath;
    }


    /**
     * 下载模版
     *
     * @param templateName 文件名称,不带文件类型
     * @return
     * @throws Exception
     */
    public static void downloadTemplate(String templateName, HttpServletResponse response) throws Exception {
        String filePath = DIR_PATH + templateName + FILE_SUFFIX_XLSX;
        File file = checkTemplate(filePath);
        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(file.toPath()));
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
             OutputStream os = response.getOutputStream()) {
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(templateName, "UTF-8") + FILE_SUFFIX_XLSX);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            workbook.write(os);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception("获取模版[" + templateName + "]失败!" + e.getMessage());
        }
    }

    private static File checkTemplate(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("未找到模板!");
        }
        return file;
    }

    /**
     * 获取模版
     *
     * @param templateName
     * @return
     * @throws Exception
     */
    public static XSSFWorkbook getTemplate(String templateName) throws Exception {
        String filePath = DIR_PATH + templateName + FILE_SUFFIX_XLSX;
        XSSFWorkbook workbook;
        File file = checkTemplate(filePath);
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file));) {
            workbook = new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception("获取模版[" + templateName + "]失败!" + e.getMessage());
        }
        return workbook;
    }

    /**
     * 写入系统配置Sheet页
     *
     * @param exportExcel
     * @param sheetClassMapper
     * @param classInterfaceMapper
     * @param sheetColumnPropertyMapper
     * @param sheetRelationshipMapper
     */
    public static void writeExcelSystemSheets(XSSFWorkbook exportExcel,
                                              SheetClassMapper sheetClassMapper,
                                              ClassInterfaceMapper classInterfaceMapper,
                                              SheetColumnPropertyMapper sheetColumnPropertyMapper,
                                              SheetRelationshipMapper sheetRelationshipMapper,
                                              boolean isSysSheetHidden) {
        Sheet sheetClassSheet = exportExcel.createSheet(ExcelSystemSheetConfig.MAPPER_SHEET_CLASS);
        Sheet classInterfaceSheet = exportExcel.createSheet(ExcelSystemSheetConfig.MAPPER_CLASS_INTERFACE);
        Sheet columnPropertySheet = exportExcel.createSheet(ExcelSystemSheetConfig.MAPPER_SHEET_COLUMN_PROPERTY);
        Sheet sheetRelationshipSheet = exportExcel.createSheet(ExcelSystemSheetConfig.MAPPER_SHEET_RELATIONSHIP);

        // 标题默认字体为 14黑体白色
        XSSFFont font = exportExcel.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.index);
        // 标题单元格默认样式为 蓝底白字
        XSSFCellStyle cellStyle = exportExcel.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(91, 155, 213), new DefaultIndexedColorMap()));
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row scsTitleRow = sheetClassSheet.createRow(0);
        setTitleRowCell(sheetClassSheet, cellStyle, scsTitleRow, ExcelSystemSheetConfig.SYSTEM_TITLES_SHEET_CLASS);
        writeSheetClassSheetData(sheetClassSheet, sheetClassMapper);

        Row clsTitleRow = classInterfaceSheet.createRow(0);
        setTitleRowCell(classInterfaceSheet, cellStyle, clsTitleRow, ExcelSystemSheetConfig.SYSTEM_TITLES_CLASS_INTERFACE);
        writeClassInterfaceSheetData(classInterfaceSheet, classInterfaceMapper);

        Row cpsTitleRow = columnPropertySheet.createRow(0);
        setTitleRowCell(columnPropertySheet, cellStyle, cpsTitleRow, ExcelSystemSheetConfig.SYSTEM_TITLES_COLUMN_PROPERTY);
        writeColumnPropertySheetData(columnPropertySheet, sheetColumnPropertyMapper);

        Row srsTitleRow = sheetRelationshipSheet.createRow(0);
        setTitleRowCell(sheetRelationshipSheet, cellStyle, srsTitleRow, ExcelSystemSheetConfig.SYSTEM_TITLES_SHEET_RELATIONSHIP);
        writeSheetRelationshipSheetData(sheetRelationshipSheet, sheetRelationshipMapper);

        if (isSysSheetHidden) {
            for (String systemSheetName : ExcelSystemSheetConfig.SYSTEM_SHEET_NAMES) {
                exportExcel.setSheetHidden(exportExcel.getSheetIndex(systemSheetName), true);
            }
        }
    }

    /**
     * 生成对象类型配置页
     *
     * @param sheetClassSheet
     * @param sheetClassMapper
     */
    private static void writeSheetClassSheetData(Sheet sheetClassSheet, SheetClassMapper sheetClassMapper) {
        Map<String, ColumnToClassUid> mapper = sheetClassMapper.getMapper();
        int i = 0;
        for (String sheetName : mapper.keySet()) {
            Row row = sheetClassSheet.createRow(i + 1);
            ColumnToClassUid columnToClassUid = mapper.get(sheetName);
            row.createCell(0).setCellValue(columnToClassUid.getSheetName());
            row.createCell(1).setCellValue(columnToClassUid.getTargetClassDef());
            row.createCell(2).setCellValue(columnToClassUid.getUidTargetColumnName());
            i++;
        }
    }

    /**
     * 生成类型接口配置页
     *
     * @param classInterfaceSheet
     * @param classInterfaceMapper
     */
    private static void writeClassInterfaceSheetData(Sheet classInterfaceSheet, ClassInterfaceMapper classInterfaceMapper) {
        Map<String, List<String>> mapper = classInterfaceMapper.getMapper();
        int i = 0;
        for (String sheetName : mapper.keySet()) {
            List<String> interfaces = mapper.get(sheetName);
            for (String anInterface : interfaces) {
                Row row = classInterfaceSheet.createRow(i + 1);
                row.createCell(0).setCellValue(sheetName);
                row.createCell(1).setCellValue(anInterface);
                i++;
            }
        }
    }

    /**
     * 生成字段映射配置页
     *
     * @param columnPropertySheet
     * @param sheetColumnPropertyMapper
     */
    private static void writeColumnPropertySheetData(Sheet columnPropertySheet, SheetColumnPropertyMapper sheetColumnPropertyMapper) {
        Map<String, List<ColumnToProperty>> mapper = sheetColumnPropertyMapper.getMapper();
        int i = 0;
        for (String sheetName : mapper.keySet()) {
            List<ColumnToProperty> columnToProperties = mapper.get(sheetName);
            for (ColumnToProperty columnToProperty : columnToProperties) {
                Row row = columnPropertySheet.createRow(i + 1);
                row.createCell(0).setCellValue(sheetName);
                if (StringUtils.isEmpty(columnToProperty.getColumnName())) {
                    row.createCell(1).setBlank();
                } else {
                    row.createCell(1).setCellValue(columnToProperty.getColumnName());
                }
                row.createCell(2).setCellValue(columnToProperty.getPropertyDefUID());
                row.createCell(3).setCellValue(columnToProperty.getMust());
                if (StringUtils.isEmpty(columnToProperty.getDefaultValueWithItems())) {
                    row.createCell(4).setBlank();
                } else {
                    row.createCell(4).setCellValue(columnToProperty.getDefaultValueWithItems());
                }
                i++;
            }
        }
    }

    /**
     * 生成关系配置页
     *
     * @param sheetRelationshipSheet
     * @param sheetRelationshipMapper
     */
    private static void writeSheetRelationshipSheetData(Sheet sheetRelationshipSheet, SheetRelationshipMapper sheetRelationshipMapper) {
        Map<String, List<SheetRelationship>> mapper = sheetRelationshipMapper.getMapper();
        int i = 0;
        for (String sheetName : mapper.keySet()) {
            List<SheetRelationship> sheetRelationships = mapper.get(sheetName);
            for (SheetRelationship sheetRelationship : sheetRelationships) {
                Row row = sheetRelationshipSheet.createRow(i + 1);
                row.createCell(0).setCellValue(sheetRelationship.getRelDefName());
                row.createCell(1).setCellValue(sheetRelationship.getEnd1SheetName());
                row.createCell(2).setCellValue(sheetRelationship.getEnd2SheetName());
                row.createCell(3).setCellValue(sheetRelationship.getRel1UidInRel2ColumnName());
                row.createCell(4).setCellValue(sheetRelationship.getRelDefUID());
                i++;
            }

        }
    }

    /**
     * 设置标题行数据及样式
     *
     * @param cellStyle
     * @param row
     * @param titles
     */
    private static void setTitleRowCell(Sheet sheet, XSSFCellStyle cellStyle, Row row, List<String> titles) {
        row.setHeightInPoints((float) 20.00);
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            // 数据写入需要在样式之后,否则样式无法生效
            cell.setCellValue(titles.get(i));
            sheet.setColumnWidth(i, 28 * 256);
        }
    }

    /**
     * 根据模版写入数据
     *
     * @param templateName
     * @param jsonObject
     */
    public static XSSFWorkbook writeExcel(String templateName, Map<String, JSONArray> jsonObject) throws Exception {
        XSSFWorkbook workbook;
        try {
            workbook = getTemplate(templateName);
            // 获取系统配置Sheet页
            Map<String, Sheet> systemSheets = getSystemSheets(workbook);
            Map<String, List<Map<String, Object>>> systemSheetsData = analyzingSheets(systemSheets);
            // 生成 对象类型配置
            SheetClassMapper sheetClassMapper = genSheetClassMapper(systemSheetsData);
            log.debug("SheetClassMapper:" + sheetClassMapper);
            // 生成 类型接口配置
            ClassInterfaceMapper classInterfaceMapper = generateClassInterfaceMapper(systemSheetsData);
            log.debug("ClassInterfaceMapper:" + classInterfaceMapper);
            // 生成 字段映射配置
            SheetColumnPropertyMapper sheetColumnPropertyMapper = genSheetColumnPropertyMapper(systemSheetsData);
            log.debug("SheetColumnPropertyMapper:" + sheetColumnPropertyMapper);
            // 生成 关系配置
            SheetRelationshipMapper sheetRelationshipMapper = genSheetRelationshipMapper(systemSheetsData);
            log.debug("SheetRelationshipMapper:" + sheetRelationshipMapper);
            completingSheetRelationshipMapper(sheetRelationshipMapper, sheetClassMapper);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception("根据模版写入数据失败!" + e.getMessage());
        }
        return workbook;
    }

    /**
     * 写入Excel值 限xlsx
     */
    public static void writeExcel(HttpServletResponse response, InputStream inStr, Integer sheet, Integer staRow, Integer staCell, Boolean hasNum, List<?> importData) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inStr);
            XSSFSheet sheet1 = workbook.getSheetAt(sheet);
            sheet1.setForceFormulaRecalculation(true);
            if (importData != null && importData.stream().count() > 0) {
                if (hasNum) {
                    staCell = staCell + 1;
                }
                for (int i = 0; i < importData.stream().count(); i++) {
                    if (importData.get(i) != null) {
                        if (hasNum) {
                            ExcelUtility.setCellData(sheet1, i + 1, staRow + i, staCell - 1);
                        }
                        Class<? extends Object> obj = importData.get(i).getClass();
                        Field[] fields = obj.getDeclaredFields();
                        for (int j = 0; j < fields.length; j++) {
                            //设置可以访问私有变量
                            fields[j].setAccessible(true);
                            String name = fields[j].getName();
                            //将属性名字的首字母大写
                            name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                            //整合出 getId() 属性这个方法
                            Method m = obj.getMethod("get" + name);
                            try {
                                Object value = m.invoke(importData.get(i));
                                if (value != null) {
                                    ExcelUtility.setCellData(sheet1, value, staRow + i, staCell + j);
                                } else {
                                    ExcelUtility.setCellData(sheet1, "", staRow + i, staCell + j);
                                }
                            } catch (Exception e) {
                                ExcelUtility.setCellData(sheet1, e.getMessage(), staRow + i, staCell + j);
                            }
                        }
                    }
                }
            }
            String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + XSSF;
            response.setHeader("content-disposition", "attachment;filename=" + fileName);
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写Excel值
     */
    public static void setCellData(XSSFSheet sheet, Object data, int rowNo, int colNo) {
        try {
            XSSFRow row = sheet.getRow(rowNo - 1);
            if (row == null) {
                row = sheet.createRow(rowNo - 1);
            }
            XSSFCell cell = row.getCell(colNo - 1);
            if (cell == null) {
                cell = row.createCell(colNo - 1);
            }
            if (data == null) {
                cell.setCellValue("");
            } else if (data instanceof String) {
                cell.setCellValue(data.toString());
            } else if (data instanceof Integer) {
                cell.setCellValue((Integer) data);
            } else if (data instanceof Float) {
                cell.setCellValue((Float) data);
            } else if (data instanceof Double) {
                cell.setCellValue((Double) data);
            } else if (data instanceof BigDecimal) {
                cell.setCellValue(Double.parseDouble(data.toString()));
            } else {
                cell.setCellValue(data.getClass().getSimpleName() + "数据类型有误");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void writeFileIntoHttpResponse(@NotNull HttpServletResponse response, @NotNull String fileName, @NotNull XSSFWorkbook excelFile) throws IOException {
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        ServletOutputStream out = response.getOutputStream();
        excelFile.write(out);
    }

    /**
     * getWorkBook
     *
     * @return xwk
     */
    public static XSSFWorkbook getWorkBook(@NotNull List<ExcelDataContent> contents) {
        XSSFWorkbook xwk = new XSSFWorkbook();
        XSSFDataFormat format = xwk.createDataFormat();
        XSSFCellStyle cellStyle = xwk.createCellStyle();
        if (contents.size() > 0) {
            for (ExcelDataContent dataContent : contents) {
                XSSFSheet xssfSheet = xwk.createSheet(dataContent.getSheetName());
                cellStyle.setDataFormat(format.getFormat("@"));//文本格式
                int j = 0;
                List<String> headerList = dataContent.getHeaderList();
                createHeader(xssfSheet, cellStyle, headerList, j);
                if (CommonUtility.hasValue(dataContent.getContent())) {
                    for (int i = 0; i < dataContent.getContent().size(); i++) {
                        List<String> row = dataContent.getContent().get(i);
                        createContent(xssfSheet, cellStyle, row, i + 1);
                    }
                }
            }
        }
        return xwk;
    }

    /**
     * createHeader
     *
     * @param xssfSheet
     * @param titleList
     */
    private static void createHeader(XSSFSheet xssfSheet, XSSFCellStyle cellStyle, List<String> titleList, int j) {
        XSSFRow rowTitle = xssfSheet.createRow(j);
        for (int cellTitle = 0; cellTitle < titleList.size(); cellTitle++) {
            Cell cellIndex = rowTitle.createCell(cellTitle);
            cellIndex.setCellStyle(cellStyle);
            cellIndex.setCellValue(titleList.get(cellTitle));
        }
    }

    /**
     * createHeader
     *
     * @param xssfSheet
     */
    private static void createContent(XSSFSheet xssfSheet, XSSFCellStyle cellStyle, List<String> rowData, int rowIndex) {
        XSSFRow rowContent = xssfSheet.createRow(rowIndex);
        for (int i = 0; i < rowData.size(); i++) {
            Cell cellIndex = rowContent.createCell(i);
            cellIndex.setCellStyle(cellStyle);
            cellIndex.setCellValue(rowData.get(i));
        }
    }


    public static JSONObject converterExcelFileToJSONObject(@NotNull MultipartFile file) throws Exception {
        JSONObject jsonObject = new JSONObject();
        try {
            InputStream inputStream = file.getInputStream();
            String fileName = file.getOriginalFilename();
            if (fileName == null || StringUtils.isEmpty(file))
                throw new Exception("无效的文件名!");
            if (!ExcelUtility.checkFileType(fileName, "xlsx"))
                throw new Exception("需要解析的文件扩展名错误!");
            Workbook importBook = ExcelUtility.switchWorkBookType(CommonUtility.getFileNameExtWithPoint(fileName), inputStream);
            Map<String, Sheet> sheetDataByWorkBook = ExcelUtility.getSheetDataByWorkBook(importBook);
            generateJSONObjectStructure(sheetDataByWorkBook, jsonObject);
        } finally {
            file.getInputStream().close();
        }
        return jsonObject;
    }

    private static void generateJSONObjectStructure(@NotNull Map<String, Sheet> sheetDataByWorkBook, @NotNull JSONObject pobjContainer) throws Exception {
        JSONArray items = new JSONArray();
        pobjContainer.put("items", items);
        Map<String, List<Map<String, Object>>> ldicSheetData = ExcelUtility.analyzingSheets(sheetDataByWorkBook);
        SheetClassMapper classDefMapper = ExcelUtility.genSheetClassMapper(ldicSheetData);
        ClassInterfaceMapper classInterfaceMapper = ExcelUtility.generateClassInterfaceMapper(ldicSheetData);
        SheetColumnPropertyMapper sheetColumnPropertyMapper = ExcelUtility.genSheetColumnPropertyMapper(ldicSheetData);
        List<SheetRelationship> lcolRelationMaps = ExcelUtility.generateRelationshipMapper(ldicSheetData);
        //加入所有对象
        for (Map.Entry<String, Sheet> entry : sheetDataByWorkBook.entrySet()) {
            String lstrSheetName = entry.getKey();
            //过滤掉系统页
            if (ExcelSystemSheetConfig.SYSTEM_SHEET_NAMES.contains(lstrSheetName)) continue;
            //当前sheet页的所有数据
            List<Map<String, Object>> sheetData = ldicSheetData.get(lstrSheetName);
            if (CommonUtility.hasValue(sheetData)) {
                //获取ClassDef
                ColumnToClassUid columnToClassUid = classDefMapper.get(lstrSheetName);
                if (columnToClassUid == null) throw new Exception("未找到sheet页:" + lstrSheetName + "的映射对象类型!");
                String lstrClassDef = columnToClassUid.getTargetClassDef();
                //获取所有接口信息
                List<String> lcolInterfaceDefs = classInterfaceMapper.get(lstrClassDef);
                for (Map<String, Object> rowData : sheetData) {
                    //获取属性数据
                    JSONObject lobjData = new JSONObject();
                    lobjData.put(CommonUtility.JSON_FORMAT_INTERFACES, lcolInterfaceDefs.toArray(new String[]{}));
                    lobjData.put(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID, lstrClassDef);
                    lobjData.put(CommonUtility.JSON_FORMAT_PROPERTIES, generatePropJSONStructure(sheetColumnPropertyMapper, lstrSheetName, rowData, lstrClassDef));
                    items.add(lobjData);
                }
            }
        }
        //加入关联信息
        if (CommonUtility.hasValue(lcolRelationMaps)) {
            // 2022.06.28 HT 修复找不到 IRelCustom 接口问题
            String[] larrInterface = new String[]{"IObject", "IRel"};
            for (SheetRelationship sheetRelationship : lcolRelationMaps) {
                String lstrRelDefUID = sheetRelationship.getRelDefUID();
                String lstrEnd1ClassDef = ExcelUtility.getClassDefUIDFromClassDefMapData(classDefMapper, sheetRelationship.getEnd1SheetName());
                String lstrEnd2ClassDef = ExcelUtility.getClassDefUIDFromClassDefMapData(classDefMapper, sheetRelationship.getEnd2SheetName());
                String lstrRel2To1PropDef = ExcelUtility.getPropDefFromPropMapperByColumnName(sheetColumnPropertyMapper, sheetRelationship);
                List<JSONObject> lcolEnd2s = ExcelUtility.getPointedClassDefJSONObjectFromItems(lstrEnd2ClassDef, items);
                List<JSONObject> lcolEnd1s = ExcelUtility.getPointedClassDefJSONObjectFromItems(lstrEnd1ClassDef, items);
                if (CommonUtility.hasValue(lcolEnd2s) && CommonUtility.hasValue(lcolEnd1s)) {
                    for (JSONObject lobjEnd2 : lcolEnd2s) {
                        JSONObject lobjRel = new JSONObject();
                        lobjRel.put(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID, "Rel");
                        lobjRel.put(CommonUtility.JSON_FORMAT_INTERFACES, larrInterface);
                        JSONObject lobjProp = new JSONObject();
                        lobjProp.put(CommonUtility.JSON_FORMAT_REL_DEF_UID, lstrRelDefUID);
                        String lstrEnd2Name = ExcelUtility.getSpecialPropFormJSONObject(lobjEnd2, propertyDefinitionType.Name.toString());
                        if (StringUtils.isEmpty(lstrEnd2Name))
                            throw new Exception("2端对象ClassDefUID:" + lstrEnd2ClassDef + ",Name不能为空!");
                        String lstrEnd2UID = ExcelUtility.getSpecialPropFormJSONObject(lobjEnd2, propertyDefinitionType.UID.toString());
                        if (StringUtils.isEmpty(lstrEnd2UID))
                            throw new Exception("2端对象ClassDefUID:" + lstrEnd2ClassDef + ",UID 不能为空!");
                        lobjProp.put(CommonUtility.JSON_FORMAT_NAME2, lstrEnd2Name);
                        lobjProp.put(CommonUtility.JSON_FORMAT_UID2, lstrEnd2UID);
                        String lstrRelationValue = ExcelUtility.getSpecialPropFormJSONObject(lobjEnd2, lstrRel2To1PropDef);
                        if (StringUtils.isEmpty(lstrRelationValue))
                            throw new Exception("1端对象与2端对象关联的值不能为空!");
                        JSONObject lobjEnd1 = ExcelUtility.getRelatedEndObjByRelatedValue(lstrRelationValue, lcolEnd1s, sheetRelationship.getEnd1SheetName(), classDefMapper, sheetColumnPropertyMapper);
                        if (lobjEnd1 == null)
                            throw new Exception("未找到2端对象,名称" + lstrEnd2Name + "关联的1端对象!关联的值:" + lstrRelationValue);
                        String lstrEnd1UID = ExcelUtility.getSpecialPropFormJSONObject(lobjEnd1, propertyDefinitionType.UID.toString());
                        if (StringUtils.isEmpty(lstrEnd1UID)) throw new Exception("End1 UID 不能为空!");
                        String lstrEnd1Name = ExcelUtility.getSpecialPropFormJSONObject(lobjEnd1, propertyDefinitionType.Name.toString());
                        if (StringUtils.isEmpty(lstrEnd1Name)) throw new Exception("End1 Name 不能为空!");
                        lobjProp.put(CommonUtility.JSON_FORMAT_NAME1, lstrEnd1Name);
                        lobjProp.put(CommonUtility.JSON_FORMAT_UID1, lstrEnd1UID);
                        lobjRel.put(CommonUtility.JSON_FORMAT_PROPERTIES, lobjProp);
                        items.add(lobjRel);
                    }
                }
            }
        }
    }

    public static final String SKIP_LOGO = "#";
    public static final String REQUIRED_LOGO = "*";

}
