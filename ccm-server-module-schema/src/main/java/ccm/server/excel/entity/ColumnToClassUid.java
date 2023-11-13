package ccm.server.excel.entity;

import ccm.server.excel.config.ExcelSystemSheetConfig;
import lombok.Data;

/**
 * 对象类型配置
 * <p>目标类型和对应的标识列</p>
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/28 9:58
 */
@Data
public class ColumnToClassUid {

    private String sheetName;
    private String targetClassDef;
    private String uidTargetColumnName;

    public void toSetValue(String titleName, Object value) {
        if (value != null) {
            String strValue = value.toString();
            switch (titleName) {
                case ExcelSystemSheetConfig.TITLE_TARGET_CLASS_NAME:
                    this.targetClassDef = strValue;
                    break;
                case ExcelSystemSheetConfig.TITLE_UID_COLUMN_NAME:
                    this.uidTargetColumnName = strValue;
                    break;
                case ExcelSystemSheetConfig.TITLE_SHEET_NAME:
                    this.sheetName = strValue;
                    break;
            }
        }
    }
}
