package ccm.server.excel.entity;

import ccm.server.excel.config.ExcelSystemSheetConfig;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性字段关系表
 * <p>属性的属性</p>
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/27 16:08
 */
@Data
public class ColumnToProperty {

    private String sheetName = "";
    private String columnName;
    private String propertyDefUID;
    private Boolean must = false;
    private String defaultValue = "";
    private List<String> defaultValueItems = new ArrayList<>();
    private String defaultValueWithItems = "";

    public void toSetValue(String titleName, Object value) throws Exception {
        if (value != null) {
            String strValue = value.toString().trim();
            switch (titleName) {
                case ExcelSystemSheetConfig.TITLE_SHEET_NAME:
                    this.sheetName = strValue;
                    break;
                case ExcelSystemSheetConfig.TITLE_COLUMN_NAME:
                    this.columnName = strValue;
                    break;
                case ExcelSystemSheetConfig.TITLE_PROPERTY_NAME:
                    this.propertyDefUID = strValue;
                    break;
                case ExcelSystemSheetConfig.TITLE_MUST:
                    this.must = ExcelSystemSheetConfig.VALUE_YES.equals(strValue);
                    break;
                case ExcelSystemSheetConfig.TITLE_DEFAULT_VALUE:
                    this.defaultValueWithItems = strValue;
                    StringBuffer sb = new StringBuffer();
                    parseDefaultValue(strValue, sb, this.defaultValueItems);
                    this.defaultValue = sb.toString();
                    break;
            }
        }
    }

    /**
     * 是否有默认值
     *
     * @return
     */
    public boolean hasDefaultValue() {
        return StringUtils.isNotBlank(this.defaultValue);
    }

    /**
     * 是否需要参数填充
     *
     * @return
     */
    public boolean needParams() {
        return !defaultValueItems.isEmpty();
    }

    /**
     * 填充默认值占位符参数
     *
     * @param param
     * @param index
     * @param result
     * @return
     */
    public static String createDefaultValue(String param, int index, String result) {
        return StringUtils.replace(result, "{{" + index + "}}", param);
    }

    /**
     * 带占位符的默认值提取占位参数
     *
     * @param defaultValue
     * @param sb
     * @param items
     * @throws Exception
     */
    public static void parseDefaultValue(String defaultValue, StringBuffer sb, List<String> items) throws Exception {
        int start = defaultValue.indexOf("{{");
        int end = defaultValue.indexOf("}}");
        if ((start == -1 && end != -1) || start != -1 && end == -1) {
            throw new Exception("默认值配置错误!");
        }
        if (start != -1) {
            String s1 = defaultValue.substring(0, start);
            String item = defaultValue.substring(start + 2, end);
            defaultValue = defaultValue.substring(end + 2);
            sb.append(s1).append("{{").append(items.size()).append("}}");
            items.add(item);
            parseDefaultValue(defaultValue, sb, items);
        } else {
            sb.append(defaultValue);
        }
    }

}
