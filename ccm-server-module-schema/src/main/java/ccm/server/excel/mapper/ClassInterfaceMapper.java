package ccm.server.excel.mapper;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类型接口配置
 * <p>
 * ClassDef与InterfaceDef对应关系,
 * key:ClassDef,
 * value:InterfaceDef
 * </p>
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/28 10:28
 */
@Data
public class ClassInterfaceMapper {

    /**
     * ClassDef与InterfaceDef对应关系
     * <p>key-ClassDef value-InterfaceDef</p>
     */
    private Map<String, List<String>> mapper = new HashMap<String, List<String>>();

    public ClassInterfaceMapper() {
    }

    public void put(String classDef, List<String> interfaceDefs) {
        this.mapper.put(classDef, interfaceDefs);
    }

    public void putOneInterfaces(String key, String interfaceDef) {
        if (!containsKey(key)) {
            this.mapper.put(key, new ArrayList<String>() {{
                add(interfaceDef);
            }});
        } else {
            this.mapper.get(key).add(interfaceDef);
        }
    }

    public List<String> get(String key) {
        return this.mapper.get(key);
    }

    public boolean containsKey(String key) {
        return this.mapper.containsKey(key);
    }

    public boolean isEmpty() {
        return this.mapper.isEmpty();
    }
}
