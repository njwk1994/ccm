package ccm.server.excel.mapper;

import ccm.server.enums.propertyDefinitionType;
import ccm.server.excel.entity.SheetRelationship;
import lombok.Data;

import java.util.*;

/**
 * 关系配置 Map
 * <p>
 * key:Sheet页,
 * value:关联关系信息
 * </p>
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/28 21:34
 */
@Data
public class SheetRelationshipMapper {

    /**
     * 系统内置属性 UID
     */
    public static final String UID = propertyDefinitionType.UID.toString();
    public static final String NAME = propertyDefinitionType.Name.toString();

    private Set<String> sheetNamesWithRel = new HashSet<>();
    private Map<String, List<SheetRelationship>> mapper = new HashMap<String, List<SheetRelationship>>();

    public SheetRelationshipMapper() {
    }

    public void addSheetNameWithRel(String sheetName) {
        this.sheetNamesWithRel.add(sheetName);
    }

    public SheetRelationshipMapper(String sheetName, List<SheetRelationship> relationships) {
        this.mapper.put(sheetName, relationships);
    }

    public void put(String sheetName, List<SheetRelationship> relationships) {
        this.mapper.put(sheetName, relationships);
    }

    public List<SheetRelationship> get(String key) {
        return this.mapper.get(key);
    }

    public Set<String> keySet() {
        return this.mapper.keySet();
    }

    public boolean containsKey(String key) {
        return this.mapper.containsKey(key);
    }

    public boolean isEmpty() {
        return this.mapper.isEmpty();
    }
}
