package ccm.server.excel.mapper;

import ccm.server.excel.entity.ColumnToClassUid;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 对象类型配置
 * <p>Sheet页和目标ClassDef及Sheet页标识列对应</p>
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/27 16:19
 */
@Data
public class SheetClassMapper {

    private Map<String, ColumnToClassUid> mapper = new HashMap<String, ColumnToClassUid>();

    public SheetClassMapper() {
    }

    public SheetClassMapper(String sheetName, ColumnToClassUid columnToClassUid) {
        this.mapper.put(sheetName, columnToClassUid);
    }

    public SheetClassMapper(String sheetName, String targetClassDef, String uidTargetColumnName) {
        ColumnToClassUid columnToClassUid = new ColumnToClassUid();
        columnToClassUid.setTargetClassDef(targetClassDef);
        columnToClassUid.setUidTargetColumnName(uidTargetColumnName);
        this.mapper.put(sheetName, columnToClassUid);
    }

    public void put(String sheetName, ColumnToClassUid columnToClassUid) {
        this.mapper.put(sheetName, columnToClassUid);
    }

    public void put(String sheetName, String targetClassDef, String uidTargetColumnName) {
        ColumnToClassUid columnToClassUid = new ColumnToClassUid();
        columnToClassUid.setTargetClassDef(targetClassDef);
        columnToClassUid.setUidTargetColumnName(uidTargetColumnName);
        this.mapper.put(sheetName, columnToClassUid);
    }

    public ColumnToClassUid get(String key) {
        return this.mapper.get(key);
    }

    public boolean containsKey(String key) {
        return this.mapper.containsKey(key);
    }

    public boolean isEmpty() {
        return this.mapper.isEmpty();
    }
}
