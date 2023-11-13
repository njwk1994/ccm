package ccm.server.excel.entity;

import ccm.server.excel.config.ExcelSystemSheetConfig;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * 关系配置
 * <p>二端对象Sheet页和一端对象Sheet页及系统关联关系</p>
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/28 11:11
 */
@Data
public class SheetRelationship {

    private String relDefUID;
    private String end2SheetName;
    private String uid2ColumnName;
    private String rel1UidInRel2ColumnName;
    private String end1SheetName;
    private String uid1ColumnName;
    private String relDefName;


    public static SheetRelationship generateSheetRelationShipByRowData(@NotNull Map<String, Object> rowData) {
        SheetRelationship sheetRelationship = new SheetRelationship();
        for (Map.Entry<String, Object> entry : rowData.entrySet()) {
            String lstrTitle = entry.getKey();
            String lstrValue = entry.getValue().toString();
            switch (lstrTitle){
                case ExcelSystemSheetConfig.TITLE_REL_DEFINITION_UID:
                    sheetRelationship.setRelDefUID(lstrValue);
                    break;
                case ExcelSystemSheetConfig.TITLE_REL2_SHEET_NAME:
                    sheetRelationship.setEnd2SheetName(lstrValue);
                    break;
                case ExcelSystemSheetConfig.TITLE_REL1_UID_IN_REL2_COLUMN_NAME:
                    sheetRelationship.setRel1UidInRel2ColumnName(lstrValue);
                    break;
                case ExcelSystemSheetConfig.TITLE_REL1_SHEET_NAME:
                    sheetRelationship.setEnd1SheetName(lstrValue);
                    break;
                case ExcelSystemSheetConfig.TITLE_REL_DEFINITION_NAME:
                    sheetRelationship.setRelDefName(lstrValue);
                    break;
            }
        }
        return sheetRelationship;
    }

    public void toSetValue(String titleName, Object value) {
        if (value != null) {
            String strValue = value.toString().trim();
            switch (titleName) {
                case ExcelSystemSheetConfig.TITLE_REL_DEFINITION_UID:
                    this.relDefUID = strValue;
                    break;
                case ExcelSystemSheetConfig.TITLE_REL2_SHEET_NAME:
                    this.end2SheetName = strValue;
                    break;
                case ExcelSystemSheetConfig.TITLE_REL1_UID_IN_REL2_COLUMN_NAME:
                    this.rel1UidInRel2ColumnName = strValue;
                    break;
                case ExcelSystemSheetConfig.TITLE_REL1_SHEET_NAME:
                    this.end1SheetName = strValue;
                    break;
                case ExcelSystemSheetConfig.TITLE_REL_DEFINITION_NAME:
                    this.relDefName = strValue;
                    break;
            }
        }
    }
}
