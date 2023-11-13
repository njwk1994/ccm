package ccm.server.module.utils;

import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.enums.*;
import ccm.server.excel.config.ExcelSystemSheetConfig;
import ccm.server.excel.entity.ColumnToClassUid;
import ccm.server.excel.entity.ColumnToProperty;
import ccm.server.excel.entity.ExcelDataContent;
import ccm.server.excel.entity.SheetRelationship;
import ccm.server.excel.mapper.ClassInterfaceMapper;
import ccm.server.excel.mapper.SheetClassMapper;
import ccm.server.excel.mapper.SheetColumnPropertyMapper;
import ccm.server.excel.util.ExcelUtility;
import ccm.server.models.query.QueryRequest;
import ccm.server.module.task.ROPTemplateReviseTask;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.util.CommonUtility;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ROPUtils {

    private ROPUtils() {
    }

    public static final String JSON_TEMPLATE_ITEMS = "items";
    public final static String CLASSDEF_ROP_GROUP = "ROPRuleGroup";
    public final static String CLASSDEF_ROP_GROUPITEM = "ROPRuleGroupItem";
    public final static String CLASSDEF_ROP_WORKSTEP = "ROPWorkStep";
    /* =========================================== ROP接口定义 start =========================================== */

    /**
     * ROP configuration 配置条件
     */
    public static final String IROPCriteriaSet_InterfaceDef = "IROPCriteriaSet";

    public static final String ROPCriteriaSet_ClassDef = "ROPCriteriaSet";

    /**
     * ROP work stage step
     */
    public static final String IROPStepSet_InterfaceDef = "IROPStepSet";

    public static final String ROPStepSet_ClassDef = "ROPStepSet";

    public static final String IROPCalculate_InterfaceDef = "IROPCalculate";

    public static final String IConstructionStep_InterfaceDef = "IConstructionStep";

    public static final String ConstructionStep_ClassDef = "ConstructionStep";
    /* =========================================== ROP接口定义 end =========================================== */

    /* =========================================== 关联关系 start =========================================== */

    /**
     * 关联关系：设计数据施工分类-ROP条件配置
     */
    public static final String ConstructionType2ROPCriteria_Rel = "CCMConstructionType2ROPCriteria";

    /**
     * 关联关系：设计数据施工分类-ROP施工步骤配置
     */
    public static final String ConstructionType2ROPStep_Rel = "CCMConstructionType2ROPStep";
    /* =========================================== 关联关系 end =========================================== */


    public static List<ExcelDataContent> generateROPTemplateDefaultSheetContent() {
        List<ExcelDataContent> lcolResult = new ArrayList<>();
        //对象类型配置项
        ExcelDataContent classDef = new ExcelDataContent();
        classDef.setSheetName(ExcelSystemSheetConfig.MAPPER_SHEET_CLASS);
        classDef.setHeaderList(new ArrayList<String>() {{
            add(ExcelSystemSheetConfig.TITLE_SHEET_NAME);
            add(ExcelSystemSheetConfig.TITLE_TARGET_CLASS_NAME);
            add(ExcelSystemSheetConfig.TITLE_UID_COLUMN_NAME);
        }});
        classDef.setContent(new ArrayList<List<String>>() {{
            add(new ArrayList<String>() {{
                add("ROP规则组");
                add("ROPRuleGroup");
                add("UID");
            }});
            add(new ArrayList<String>() {{
                add("ROP规则条目");
                add("ROPRuleGroupItem");
                add("UID");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("ROPWorkStep");
                add("UID");
            }});
        }});
        lcolResult.add(classDef);
        //类型接口配置页
        ExcelDataContent interfaceDef = new ExcelDataContent();
        interfaceDef.setSheetName(ExcelSystemSheetConfig.MAPPER_CLASS_INTERFACE);
        interfaceDef.setHeaderList(new ArrayList<String>() {{
            add(ExcelSystemSheetConfig.TITLE_CLASS_NAME);
            add(ExcelSystemSheetConfig.TITLE_INTERFACE_NAME);
        }});
        interfaceDef.setContent(new ArrayList<List<String>>() {{
            add(new ArrayList<String>() {{
                add("ROPRuleGroup");
                add("IROPRuleGroup");
            }});
            add(new ArrayList<String>() {{
                add("ROPRuleGroup");
                add("IObject");
            }});
            add(new ArrayList<String>() {{
                add("ROPRuleGroupItem");
                add("IROPRuleGroupItem");
            }});
            add(new ArrayList<String>() {{
                add("ROPRuleGroupItem");
                add("IObject");
            }});
            add(new ArrayList<String>() {{
                add("ROPWorkStep");
                add("IObject");
            }});
            add(new ArrayList<String>() {{
                add("ROPWorkStep");
                add("IROPWorkStep");
            }});
        }});
        lcolResult.add(interfaceDef);
        //字段映射页
        ExcelDataContent propertyDef = new ExcelDataContent();
        propertyDef.setSheetName(ExcelSystemSheetConfig.MAPPER_SHEET_COLUMN_PROPERTY);
        propertyDef.setHeaderList(new ArrayList<String>() {{
            add(ExcelSystemSheetConfig.TITLE_SHEET_NAME);
            add(ExcelSystemSheetConfig.TITLE_COLUMN_NAME);
            add(ExcelSystemSheetConfig.TITLE_PROPERTY_NAME);
            add(ExcelSystemSheetConfig.TITLE_MUST);
            add(ExcelSystemSheetConfig.TITLE_DEFAULT_VALUE);
        }});
        propertyDef.setContent(new ArrayList<List<String>>() {{
            add(new ArrayList<String>() {{
                add("ROP规则组");
                add("名称");
                add("Name");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP规则组");
                add("描述");
                add("Description");
                add("否");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP规则组");
                add("生效的对象定义");
                add("ROPGroupClassDefinitionUID");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP规则组");
                add("规则组顺序号");
                add("ROPGroupOrder");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP规则条目");
                add("规则项名称");
                add("Name");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP规则条目");
                add("所属ROP规则组名称");
                add("ROPGroupName");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP规则条目");
                add("所需计算对象的属性");
                add("ROPTargetPropertyDefinitionUID");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP规则条目");
                add("过滤条件值");
                add("ROPCalculationValue");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP规则条目");
                add("单位");
                add("ROPTargetPropertyValueUoM");
                add("否");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("任务包施工阶段");
                add("ROPWorkStepTPPhase");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("工作包施工阶段");
                add("ROPWorkStepWPPhase");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("工作步骤");
                add("ROPWorkStepName");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("工作步骤类别");
                add("ROPWorkStepType");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("阶段放行步骤");
                add("ROPWorkStepAllowInd");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("消耗材料");
                add("ROPWorkStepConsumeMaterialInd");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("材料下发方式");
                add("ROPWorkStepMaterialIssue");
                add("否");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("步骤生成方式");
                add("ROPWorkStepGenerateMode");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("用于计算权重的属性项");
                add("ROPWorkStepWeightCalculateProperty");
                add("否");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("权重(模板设置的权重)");
                add("ROPWorkStepBaseWeight");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("顺序");
                add("ROPWorkStepOrderValue");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("步骤名称");
                add("Name");
                add("是");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("步骤描述");
                add("Description");
                add("否");
                add("");
            }});
            add(new ArrayList<String>() {{
                add("ROP工作步骤");
                add("所属ROP规则组名称");
                add("ROPGroupName");
                add("是");
                add("");
            }});
        }});
        lcolResult.add(propertyDef);
        //关联关系
        ExcelDataContent relation = new ExcelDataContent();
        relation.setSheetName(ExcelSystemSheetConfig.MAPPER_SHEET_RELATIONSHIP);
        relation.setHeaderList(new ArrayList<String>() {{
            add(ExcelSystemSheetConfig.TITLE_REL_DEFINITION_NAME);
            add(ExcelSystemSheetConfig.TITLE_REL1_SHEET_NAME);
            add(ExcelSystemSheetConfig.TITLE_REL2_SHEET_NAME);
            add(ExcelSystemSheetConfig.TITLE_REL1_UID_IN_REL2_COLUMN_NAME);
            add(ExcelSystemSheetConfig.TITLE_REL_DEFINITION_UID);
        }});
        relation.setContent(new ArrayList<List<String>>() {{
            add(new ArrayList<String>() {{
                add("ROP规则组与规则条目");
                add("ROP规则组");
                add("ROP规则条目");
                add("*所属ROP规则组名称");
                add("ROPRuleGroup2Item");
            }});
            add(new ArrayList<String>() {{
                add("ROP规则组与工作步骤");
                add("ROP规则组");
                add("ROP工作步骤");
                add("*所属ROP规则组名称");
                add("ROPRuleGroup2ROPWorkStep");
            }});
        }});
        lcolResult.add(relation);
        return lcolResult;
    }

    public static void setROPRuleGroupHeader(@NotNull ExcelDataContent excelDataContent) {
        excelDataContent.setHeaderList(new ArrayList<String>() {{
            add("*名称");
            add("描述");
            add("*生效的对象定义");
            add("*规则组顺序号");
        }});
    }

    public static void setROPGroupItemHeader(@NotNull ExcelDataContent excelDataContent) {
        excelDataContent.setHeaderList(new ArrayList<String>() {{
            add("*规则项名称");
            add("*所属ROP规则组名称");
            add("*所需计算对象的属性");
            add("*过滤条件值");
            add("单位");
            add(SKIP_LOGO + "对象定义类型");
        }});
    }

    public static void setROPRuleWorkStepHeader(@NotNull ExcelDataContent excelDataContent) {
        excelDataContent.setHeaderList(new ArrayList<String>() {{
            add("*所属ROP规则组名称");
            add("*步骤名称");
            add("步骤描述");
            add("*工作包施工阶段");
            add("*任务包施工阶段");
            add("*工作步骤");
            add("*工作步骤类别");
            add("*阶段放行步骤");
            add("*消耗材料");
            add("材料下发方式");
            add("*步骤生成方式");
            add("用于计算权重的属性项");
            add("*权重(模板设置的权重)");
            add("*顺序");
        }});
    }

    public static void setROPRuleGroupContent(@NotNull ExcelDataContent ropGroup, @NotNull IObjectCollection pcolROPGroups) {
        if (ropGroup.getContent() == null) {
            ropGroup.setContent(new ArrayList<>());
        }
        List<List<String>> lcolContent = ropGroup.getContent();
        Iterator<IObject> e = pcolROPGroups.GetEnumerator();
        while (e.hasNext()) {
            IROPRuleGroup ruleGroup = e.next().toInterface(IROPRuleGroup.class);
            List<String> lcolRow = new ArrayList<>();
            lcolRow.add(ruleGroup.Name());
            lcolRow.add(ruleGroup.Description());
            lcolRow.add(ruleGroup.ROPGroupClassDefinitionUID());
            lcolRow.add(ruleGroup.ROPGroupOrder().toString());
            lcolContent.add(lcolRow);
        }
    }

    public static ExcelDataContent generateROPGroupItemDataContent() {
        ExcelDataContent excelDataContent = new ExcelDataContent();
        excelDataContent.setSheetName(ExcelSystemSheetConfig.SHEET_NAME_ROP_GROUP_ITEM);
        setROPGroupItemHeader(excelDataContent);
        return excelDataContent;
    }

    public static ExcelDataContent generateROPWorkStepDataContent() {
        ExcelDataContent excelDataContent = new ExcelDataContent();
        excelDataContent.setSheetName(ExcelSystemSheetConfig.SHEET_NAME_ROP_WORK_STEP);
        setROPRuleWorkStepHeader(excelDataContent);
        return excelDataContent;
    }


    public static void setROPRuleGroupItemsContent(@NotNull ExcelDataContent ropGroupItems, IObjectCollection pcolGroups) throws Exception {
        List<List<String>> lcolContent = new ArrayList<>();
        if (SchemaUtility.hasValue(pcolGroups)) {
            Iterator<IObject> e = pcolGroups.GetEnumerator();
            while (e.hasNext()) {
                IROPRuleGroup ruleGroup = e.next().toInterface(IROPRuleGroup.class);
                IObjectCollection lcolItems = ruleGroup.getItems();
                if (SchemaUtility.hasValue(lcolItems)) {
                    Iterator<IObject> e1 = lcolItems.GetEnumerator();
                    while (e1.hasNext()) {
                        IROPRuleGroupItem groupItem = e1.next().toInterface(IROPRuleGroupItem.class);
                        List<String> lcolRow = new ArrayList<>();
                        lcolRow.add(groupItem.Name());
                        lcolRow.add(ruleGroup.Name());
                        lcolRow.add(groupItem.ROPTargetPropertyDefinitionUID());
                        lcolRow.add(groupItem.ROPCalculationValue());
                        lcolRow.add(groupItem.ROPTargetPropertyValueUoM());
                        lcolRow.add(ruleGroup.ROPGroupClassDefinitionUID());
                        lcolContent.add(lcolRow);
                    }
                }
            }
        }
        ropGroupItems.setContent(lcolContent);
    }

    public static ExcelDataContent generateROPGroupContent(IObjectCollection pcolROPGroups) {
        ExcelDataContent ropGroup = new ExcelDataContent();
        ropGroup.setSheetName(ExcelSystemSheetConfig.SHEET_NAME_ROP_RULE_GROUP);
        ROPUtils.setROPRuleGroupHeader(ropGroup);
        if (SchemaUtility.hasValue(pcolROPGroups)) {
            ROPUtils.setROPRuleGroupContent(ropGroup, pcolROPGroups);
        }
        return ropGroup;
    }


    public static void setROPRuleWorkStepContent(@NotNull ExcelDataContent workStepContent, IObjectCollection pcolROPGroups) throws Exception {
        List<List<String>> content = new ArrayList<>();
        if (SchemaUtility.hasValue(pcolROPGroups)) {
            Iterator<IObject> e = pcolROPGroups.GetEnumerator();
            while (e.hasNext()) {
                IROPRuleGroup ruleGroup = e.next().toInterface(IROPRuleGroup.class);
                IObjectCollection lcolWorkSteps = ruleGroup.getROPWorkSteps();
                if (SchemaUtility.hasValue(lcolWorkSteps)) {
                    Iterator<IObject> e1 = lcolWorkSteps.GetEnumerator();
                    while (e1.hasNext()) {
                        IROPWorkStep step = e1.next().toInterface(IROPWorkStep.class);
                        List<String> row = new ArrayList<>();
                        row.add(ruleGroup.Name());
                        row.add(step.Name());
                        row.add(step.Description());
                        row.add(SchemaUtility.getEnumEnumName(step.ROPWorkStepTPPhase()));
                        row.add(SchemaUtility.getEnumEnumName(step.ROPWorkStepWPPhase()));
                        row.add(SchemaUtility.getEnumEnumName(step.ROPWorkStepName()));
                        row.add(SchemaUtility.getEnumEnumName(step.ROPWorkStepType()));
                        row.add(step.ROPWorkStepAllowInd() ? "是" : "否");
                        row.add(step.ROPWorkStepConsumeMaterialInd() ? "是" : "否");
                        row.add(SchemaUtility.getEnumEnumName(step.ROPWorkStepMaterialIssue()));
                        row.add(SchemaUtility.getEnumEnumName(step.ROPWorkStepGenerateMode()));
                        row.add(step.ROPWorkStepWeightCalculateProperty());
                        row.add(step.ROPWorkStepBaseWeight() + "");
                        row.add(step.ROPWorkStepOrderValue() + "");
                        content.add(row);
                    }
                }
            }
        }
        workStepContent.setContent(content);
    }

    /*
     * @Descriptions : 判定为boolean类型的属性 解析后的bool  因为有的值为:是 | 否
     * @Author: Chen Jing
     * @Date: 2022/4/24 16:38
     * @param jsonObject ROPWorkStep 对象
     * @param pstrPropDef  需要判断的属性定义
     * @Return:boolean
     */
    public static boolean checkBooleanTypeProp(@NotNull JSONObject jsonObject, @NotNull String pstrPropDef) {
        if (!StringUtils.isEmpty(pstrPropDef)) {
            String specialPropertyValue = SchemaUtility.getSpecialPropertyValue(jsonObject, pstrPropDef);
            if (!StringUtils.isEmpty(specialPropertyValue)) {
                if ("是".equalsIgnoreCase(specialPropertyValue)) {
                    return true;
                } else if ("否".equalsIgnoreCase(specialPropertyValue)) {
                    return false;
                } else {
                    return Boolean.parseBoolean(specialPropertyValue);
                }
            }
        }
        return false;
    }

    public static void validateCalculatePropDef(@NotNull JSONObject jsonObject) throws Exception {
        String specialPropertyValue = SchemaUtility.getSpecialPropertyValue(jsonObject, propertyDefinitionType.ROPWorkStepWeightCalculateProperty.toString());
        if (!StringUtils.isEmpty(specialPropertyValue)) {
            try {
                int i = Double.valueOf(specialPropertyValue).intValue();
            } catch (Exception ex) {
                IObject propDef = SchemaUtility.verifyPropertyDef(specialPropertyValue);
                if (propDef == null) {
                    throw new Exception("invalid propertyDef:" + specialPropertyValue);
                }
            }
        }
    }

    public static String validateCalculatePropDef(@NotNull List<ObjectItemDTO> pcolObjsProps) {
        ObjectItemDTO objectItemDTO = pcolObjsProps.stream().filter(r -> r.getDefUID().equalsIgnoreCase(propertyDefinitionType.ROPWorkStepWeightCalculateProperty.toString())).findFirst().orElse(null);
        if (objectItemDTO != null) {
            Object displayValue = objectItemDTO.getDisplayValue();
            if (!StringUtils.isEmpty(displayValue)) {
                String lstrValue = displayValue.toString();
                try {
                    int i = Double.valueOf(lstrValue).intValue();
                    return null;
                } catch (Exception ex) {
                    IObject propDef = SchemaUtility.verifyPropertyDef(lstrValue);
                    if (propDef == null) {
                        return "invalid propDefinition:" + lstrValue;
                    }
                    return null;
                }
            }
        }
        return null;
    }

    private static void generateJSONObjectStructure(@NotNull Map<String, Sheet> sheetDataByWorkBook, @NotNull JSONObject pobjContainer) throws Exception {
        JSONArray items = new JSONArray();
        pobjContainer.put("items", items);
        ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(null);
        if (configurationItem == null)
            throw new Exception("未找到有效的项目信息!");
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
                    lobjData.put(CommonUtility.JSON_FORMAT_PROPERTIES, generatePropJSONStructure(sheetColumnPropertyMapper, lstrSheetName, rowData, lstrClassDef, configurationItem.Name()));
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
                        if (StringUtils.isEmpty(lstrRelationValue)) throw new Exception("1端对象与2端对象关联的值不能为空!");
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

    private static JSONObject generatePropJSONStructure(@NotNull SheetColumnPropertyMapper propertyMapData, @NotNull String pstrSheetName, @NotNull Map<String, Object> rowData, String pstrClassDef, String configName) throws Exception {
        JSONObject propJson = new JSONObject();
        List<ColumnToProperty> columnToProperties = propertyMapData.get(pstrSheetName);
        if (!CommonUtility.hasValue(columnToProperties)) {
            throw new Exception("未找到sheet:" + pstrSheetName + "的属性映射项");
        }
        for (Map.Entry<String, Object> columnData : rowData.entrySet()) {
            Object value = columnData.getValue();
            String lstrColumnName = columnData.getKey();
            if (lstrColumnName.startsWith(SKIP_LOGO)) continue;
            ColumnToProperty columnToProperty = columnToProperties.stream().filter(r -> {
                String lstrMapColName = r.getMust() ? REQUIRED_LOGO + r.getColumnName() : r.getColumnName();
                return lstrMapColName.equalsIgnoreCase(lstrColumnName);
            }).findFirst().orElse(null);
            if (columnToProperty != null) {
                if (ExcelUtility.isPropValueRequired(columnToProperty) && (value == null || StringUtils.isEmpty(value.toString()))) {
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
            propJson.put("UID", SchemaUtility.generateUIDByClassDefAndProps(pstrClassDef, propJson, configName,true));
        }
        return propJson;
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
        removeROPGroupNameProp(jsonObject);
        return jsonObject;
    }

    /*
     * @Descriptions :  移除ROPGroupName这个属性
     * @Author: Chen Jing
     * @Date: 2022/4/25 11:28
     * @param jsonObject 从Excel模板解析获取的所有对象
     * @Return:
     */
    private static void removeROPGroupNameProp(@NotNull JSONObject jsonObject) {
        JSONArray items = jsonObject.getJSONArray("items");
        if (items != null && items.size() > 0) {
            List<JSONObject> jsonObjects = CommonUtility.toJSONObjList(items);
            List<JSONObject> objects = jsonObjects.stream().filter(r -> !r.getString("classDefinitionUID").equalsIgnoreCase("Rel")).collect(Collectors.toList());
            if (CommonUtility.hasValue(objects)) {
                objects.forEach(r -> {
                    JSONObject properties = r.getJSONObject("properties");
                    properties.remove("ROPGroupName");
                });
            }
        }
    }

    /*
     * @Descriptions : 校验ROP工作步骤的枚举值是否有效
     * @Author: Chen Jing
     * @Date: 2022/4/25 11:27
     * @param workStep ROPWorkStep JSON对象
     * @Return:void
     */
    public static void validateWorkStepEnumListTypePropValue(@NotNull JSONObject workStep) throws Exception {
        JSONObject props = workStep.getJSONObject(SchemaUtility.PROPERTIES);
        for (Map.Entry<String, Object> entry : props.entrySet()) {
            String lstrPropDef = entry.getKey();
            Object lobjValue = entry.getValue();
            if (lobjValue != null && !StringUtils.isEmpty(lobjValue)) {
                String lstrValue = lobjValue.toString();
                IObject lobjProp = CIMContext.Instance.ProcessCache().item(lstrPropDef, domainInfo.SCHEMA.toString());
                if (lobjProp == null) throw new Exception("无效的属性定义:" + lstrPropDef);
                IPropertyDef lobjPropDef = lobjProp.toInterface(IPropertyDef.class);
                if (lobjPropDef.isEnumListType()) {
                    IEnumListType enumListType = lobjPropDef.getScopedBy().toInterface(IEnumListType.class);
                    IObjectCollection entries = enumListType.getEntries();
                    if (SchemaUtility.hasValue(entries)) {
                        boolean flag = false;
                        Iterator<IObject> e = entries.GetEnumerator();
                        while (e.hasNext()) {
                            IObject enumEnum = e.next();
                            if (enumEnum.Name().toLowerCase().contains(lstrValue.toLowerCase()) || enumEnum.Description().toLowerCase().contains(lstrValue.toLowerCase()) || lstrValue.toLowerCase().equalsIgnoreCase(enumEnum.Name() + "," + enumEnum.Description())) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            throw new Exception("枚举属性定义:" + lstrPropDef + ",值:" + lstrValue + "未在系统中定义!");
                        }
                    } else {
                        throw new Exception("枚举属性定义:" + lstrPropDef + ",值:" + lobjValue + "未在系统中定义!");
                    }
                }
            }
        }
    }

    /*
     * @Descriptions : 根据设计对象的ClassDefUID 获取 ROPRuleGroup
     * @Author: Chen Jing
     * @Date: 2022/4/25 11:26
     * @param parrClassDefUIDs ROGRuleGroup生效的ClassDef数组
     * @param fromCache  是否从获取获取
     * @Return:ccm.server.schema.collections.IObjectCollection
     */
    public static IObjectCollection getROPGroupByROPGroupClassDefinitionUID(String[] parrClassDefUIDs, boolean fromCache) {
        if (parrClassDefUIDs != null && parrClassDefUIDs.length > 0) {
            if (fromCache) {
                IObjectCollection objectCollection = CIMContext.Instance.ProcessCache().getObjectsByClassDefCache(classDefinitionType.ROPRuleGroup.toString());
                if (SchemaUtility.hasValue(objectCollection)) {
                    Iterator<IObject> iObjectIterator = objectCollection.GetEnumerator();
                    IObjectCollection result = new ObjectCollection();
                    while (iObjectIterator.hasNext()) {
                        IObject current = iObjectIterator.next();
                        String groupClassDefinitionUID = ValueConversionUtility.toString(current.getProperty(propertyDefinitionType.ROPGroupClassDefinitionUID.toString()));
                        if (Arrays.asList(parrClassDefUIDs).contains(groupClassDefinitionUID))
                            result.append(current);
                    }
                    return result;
                }
            }
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionType.ROPRuleGroup.toString());
            CIMContext.Instance.QueryEngine().addPropertyForQuery(queryRequest, IROPRuleGroup.class.getSimpleName(), propertyDefinitionType.ROPGroupClassDefinitionUID.toString(), operator.in, String.join(",", parrClassDefUIDs));
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    /*
     * @Descriptions : 根据设计对象的ClassDefUID 获取 ROPRuleGroup
     * @Author: Chen Jing
     * @Date: 2022/4/25 11:26
     * @param pstrROPGroupClassDefinitionUID ROGRuleGroup生效的ClassDef
     * @param fromCache  是否从获取获取
     * @Return:ccm.server.schema.collections.IObjectCollection
     */
    public static IObjectCollection getROPGroupByROPGroupClassDefinitionUID(String pstrROPGroupClassDefinitionUID, boolean fromCache) {
        if (!StringUtils.isEmpty(pstrROPGroupClassDefinitionUID)) {
            if (fromCache) {
                IObjectCollection objectCollection = CIMContext.Instance.ProcessCache().getObjectsByClassDefCache(classDefinitionType.ROPRuleGroup.toString());
                if (SchemaUtility.hasValue(objectCollection)) {
                    Iterator<IObject> iObjectIterator = objectCollection.GetEnumerator();
                    IObjectCollection result = new ObjectCollection();
                    while (iObjectIterator.hasNext()) {
                        IObject current = iObjectIterator.next();
                        String groupClassDefinitionUID = ValueConversionUtility.toString(current.getProperty(propertyDefinitionType.ROPGroupClassDefinitionUID.toString()));
                        if (groupClassDefinitionUID.equalsIgnoreCase(pstrROPGroupClassDefinitionUID))
                            result.append(current);
                    }
                    return result;
                }
            }
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionType.ROPRuleGroup.toString());
            CIMContext.Instance.QueryEngine().addPropertyForQuery(queryRequest, IROPRuleGroup.class.getSimpleName(), propertyDefinitionType.ROPGroupClassDefinitionUID.toString(), operator.equal, pstrROPGroupClassDefinitionUID);
            return CIMContext.Instance.QueryEngine().query(queryRequest);
        }
        return null;
    }

    /*
     * @Descriptions : 通过解析Excel模板的关联关系,获取ROPGroup关联的对象UID集合
     * @Author: Chen Jing
     * @Date: 2022/4/25 11:24
     * @param pstrROPGroupUID
     * @param pcolRels 关联关系集合
     * @param relDirection
     * @Return:java.util.List<java.lang.String>
     */
    public static List<String> getROPGroupRelatedItemUIDsFromTemplateData(String pstrROPGroupUID, List<JSONObject> pcolRels, relDirection relDirection) {
        if (relDirection == ccm.server.enums.relDirection._unknown) {
            return null;
        }
        if (!StringUtils.isEmpty(pstrROPGroupUID) && CommonUtility.hasValue(pcolRels)) {
            List<JSONObject> rels = null;
            switch (relDirection) {
                case _1To2:
                    rels = pcolRels.stream().filter(r -> pstrROPGroupUID.equalsIgnoreCase(SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.UID1.toString()))).collect(Collectors.toList());
                    break;
                case _2To1:
                    rels = pcolRels.stream().filter(r -> pstrROPGroupUID.equalsIgnoreCase(SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.UID2.toString()))).collect(Collectors.toList());
                    break;
            }
            if (CommonUtility.hasValue(rels)) {
                return rels.stream().map(r -> SchemaUtility.getSpecialPropertyValue(r, relDirection == ccm.server.enums.relDirection._1To2 ? propertyDefinitionType.UID2.toString() : propertyDefinitionType.UID1.toString())).collect(Collectors.toList());
            }
        }
        return null;
    }

    public static List<Integer> getROPGroupOrders(IObjectCollection pcolSameTargetClassDefGroups) {
        if (SchemaUtility.hasValue(pcolSameTargetClassDefGroups)) {
            List<Integer> lcolResult = new ArrayList<>();
            Iterator<IObject> e = pcolSameTargetClassDefGroups.GetEnumerator();
            while (e.hasNext()) {
                IROPRuleGroup ruleGroup = e.next().toInterface(IROPRuleGroup.class);
                lcolResult.add(ruleGroup.ROPGroupOrder());
            }
            return lcolResult;
        }
        return null;
    }

    public static boolean containsByRuleJSON(IObjectCollection pcolWorkSteps, String pJsonCompareRuleStr) {
        if (pJsonCompareRuleStr != null && SchemaUtility.hasValue(pcolWorkSteps)) {
            Iterator<IObject> e = pcolWorkSteps.GetEnumerator();
            while (e.hasNext()) {
                IROPWorkStep workStep = e.next().toInterface(IROPWorkStep.class);
                Map<String, Object> ruleJSON = workStep.generateIdentity();
                if (JSON.parse(pJsonCompareRuleStr).equals(JSON.parse(JSON.toJSONString(ruleJSON)))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String generateROPRuleGroupItemCompareStr(JSONObject ropGroupItemJSON) {
        JSONObject jsonObject = new JSONObject();
        JSONObject props = ropGroupItemJSON.getJSONObject(CommonUtility.JSON_FORMAT_PROPERTIES);

        jsonObject.put(propertyDefinitionType.ROPCalculationValue.toString(), props.getString(propertyDefinitionType.ROPCalculationValue.toString()));
        jsonObject.put(propertyDefinitionType.ROPTargetPropertyDefinitionUID.toString(), props.get(propertyDefinitionType.ROPTargetPropertyDefinitionUID.toString()));
        return jsonObject.toJSONString();
    }

    private final static String ROP_WORK_STEP_PREFIX = "ROPWorkStep";

    public static String generateRuleJSONForWorkStepJSONObjStr(@NotNull JSONObject ropWorkStep) throws Exception {
        Map<String, Object> jsonObject = new HashMap<>();
        JSONObject props = ropWorkStep.getJSONObject(CommonUtility.JSON_FORMAT_PROPERTIES);
        for (Map.Entry<String, Object> entry : props.entrySet()) {
            String lstrPropDef = entry.getKey();
            if (lstrPropDef.startsWith(ROP_WORK_STEP_PREFIX))
                jsonObject.put(lstrPropDef, SchemaUtility.getPropValue(lstrPropDef, entry.getValue()));
        }
        return JSON.toJSONString(jsonObject);
    }

    /*
     * @Description: 设置ROP升版执行过程的信息
     * @param task ROP升版执行任务
     * @param pstrPercentage 进度百分比
     * @param pstrProcessingMsg 执行过程信息
     * @return: void
     * @Author: Chen Jing
     * @Date: 2022-05-129 15:41:49
     */
    public static void setROPTemplateReviseProcessingInfo(ROPTemplateReviseTask task, Integer pintPercentage, String pstrProcessingMsg) {
        if (task != null) {
            task.setProcessingMsg(pstrProcessingMsg);
            task.setPercentage(pintPercentage);
        }
    }

    public static void setROPTemplateReviseProcessingInfo(ROPTemplateReviseTask task, String pstrProcessingMsg) {
        if (task != null) {
            task.setProcessingMsg(pstrProcessingMsg);
        }
    }


}
