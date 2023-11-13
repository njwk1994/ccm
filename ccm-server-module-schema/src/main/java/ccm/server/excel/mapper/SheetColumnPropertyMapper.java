package ccm.server.excel.mapper;

import ccm.server.excel.entity.ColumnToProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字段映射配置
 * <p>
 * Sheet页和属性映射关系,
 * key:Sheet名称,
 * value:字段属性映射关系
 * </p>
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/27 16:05
 */
@Data
public class SheetColumnPropertyMapper implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, List<ColumnToProperty>> mapper = new HashMap<String, List<ColumnToProperty>>();

    public SheetColumnPropertyMapper() {
    }

    public SheetColumnPropertyMapper(String sheetName, List<ColumnToProperty> properties) {
        this.mapper.put(sheetName, properties);
    }

    public void put(String sheetName, List<ColumnToProperty> properties) {
        this.mapper.put(sheetName, properties);
    }

    public List<ColumnToProperty> get(String key) {
        return this.mapper.get(key);
    }

    public boolean containsKey(String key) {
        return this.mapper.containsKey(key);
    }

    public boolean isEmpty() {
        return this.mapper.isEmpty();
    }

    /**
     * 序列化克隆
     *
     * @return 返回序列化后的对象
     */
    public SheetColumnPropertyMapper cloneObject() {
        SheetColumnPropertyMapper result = new SheetColumnPropertyMapper();
        for (String key : this.mapper.keySet()) {
            result.put(key, get(key));
        }
        return result;
    }
}
