package ccm.server.excel.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/28 13:50
 */
public class ExcelSystemSheetConfig {

    /**
     * ROP模版相关
     */
    public static final String SHEET_NAME_ROP_RULE_GROUP = "ROP规则组";
    public static final String SHEET_NAME_ROP_GROUP_ITEM = "ROP规则条目";
    public static final String SHEET_NAME_ROP_WORK_STEP = "ROP工作步骤";

    //试压包材料模板相关
    public static final String SHEET_NAME_PTP_MATERIAL_SPECIFICATION = "试压包材料规格";
    public static final String SHEET_NAME_PTP_MATERIAL_TEMPLATE = "试压包材料模板";

    /**
     * 默认Sheet页-对象类型配置
     * <p>用于配置 Sheet页 - 导入对象(ClassDef) - 导入对象的标识列(UID/Name) 的关联关系</p>
     */
    public static final String MAPPER_SHEET_CLASS = "对象类型配置";

    /**
     * 默认Sheet页-类型接口配置
     * <p>用于配置 导入对象(ClassDef) - 接口(InterfaceDef) 的关联关系</p>
     */
    public static final String MAPPER_CLASS_INTERFACE = "类型接口配置";

    /**
     * 默认Sheet页-字段映射配置
     * <p>用于配置 Sheet页 - Sheet页列名(Column) - ClassDef的属性(Property) - 是否必需(必需的需要检测属性值是否为空) - 默认值 的关联关系</p>
     */
    public static final String MAPPER_SHEET_COLUMN_PROPERTY = "字段映射配置";

    /**
     * 默认Sheet页-关系配置
     * <p>用于配置 关联关系名称(仅表格展示用字段) - 二端对象Sheet页(Column) - ClassDef的属性(Property) 的关联关系</p>
     */
    public static final String MAPPER_SHEET_RELATIONSHIP = "关系配置";

    /**
     * Title-Excel页
     */
    public static final String TITLE_SHEET_NAME = "Excel页";
    /**
     * Title-目标类型
     */
    public static final String TITLE_TARGET_CLASS_NAME = "目标类型";
    /**
     * Title-标识列
     */
    public static final String TITLE_UID_COLUMN_NAME = "标识列";
    /**
     * Title-类型
     */
    public static final String TITLE_CLASS_NAME = "类型";
    /**
     * Title-接口
     */
    public static final String TITLE_INTERFACE_NAME = "接口";
    /**
     * Title-列
     */
    public static final String TITLE_COLUMN_NAME = "列";
    /**
     * Title-属性
     */
    public static final String TITLE_PROPERTY_NAME = "属性";
    /**
     * Title-必须
     */
    public static final String TITLE_MUST = "必须";
    /**
     * Title-默认值
     */
    public static final String TITLE_DEFAULT_VALUE = "默认值";
    /**
     * Title-关系名
     */
    public static final String TITLE_REL_DEFINITION_UID = "关联定义";
    /**
     * Title-二端对象页
     */
    public static final String TITLE_REL2_SHEET_NAME = "二端对象页";
    /**
     * Title-二端对象中对应一端标识列的字段
     * <p>在二端对象字段中对应一端标识列的字段名称</p>
     */
    public static final String TITLE_REL1_UID_IN_REL2_COLUMN_NAME = "二端对象关联一端对象标识列";
    /**
     * Title-一端对象页
     */
    public static final String TITLE_REL1_SHEET_NAME = "一端对象页";
    /**
     * Title-关联名称
     */
    public static final String TITLE_REL_DEFINITION_NAME = "关联名称";
    /**
     * Value-是
     */
    public static final String VALUE_YES = "是";


    /**
     * 系统默认Sheet页名称
     */
    public static final List<String> SYSTEM_SHEET_NAMES = new ArrayList<String>() {
        {
            add(MAPPER_SHEET_CLASS);
            add(MAPPER_CLASS_INTERFACE);
            add(MAPPER_SHEET_COLUMN_PROPERTY);
            add(MAPPER_SHEET_RELATIONSHIP);
        }
    };

    /**
     * 对象类型配置 页标题名称
     */
    public static final List<String> SYSTEM_TITLES_SHEET_CLASS = new ArrayList<String>() {
        {
            add(TITLE_SHEET_NAME);
            add(TITLE_TARGET_CLASS_NAME);
            add(TITLE_UID_COLUMN_NAME);
        }
    };

    /**
     * 类型接口配置 页标题名称
     */
    public static final List<String> SYSTEM_TITLES_CLASS_INTERFACE = new ArrayList<String>() {
        {
            add(TITLE_CLASS_NAME);
            add(TITLE_INTERFACE_NAME);
        }
    };

    /**
     * 字段映射配置 页标题名称
     */
    public static final List<String> SYSTEM_TITLES_COLUMN_PROPERTY = new ArrayList<String>() {
        {
            add(TITLE_SHEET_NAME);
            add(TITLE_COLUMN_NAME);
            add(TITLE_PROPERTY_NAME);
            add(TITLE_MUST);
            add(TITLE_DEFAULT_VALUE);
        }
    };

    /**
     * 关系配置 页标题名称
     */
    public static final List<String> SYSTEM_TITLES_SHEET_RELATIONSHIP = new ArrayList<String>() {
        {

            add(TITLE_REL_DEFINITION_NAME);
            add(TITLE_REL1_SHEET_NAME);
            add(TITLE_REL2_SHEET_NAME);
            add(TITLE_REL1_UID_IN_REL2_COLUMN_NAME);
            add(TITLE_REL_DEFINITION_UID);
        }
    };

}
