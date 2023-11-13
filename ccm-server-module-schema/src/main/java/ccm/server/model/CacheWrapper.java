package ccm.server.model;

import ccm.server.enums.classDefinitionType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Comparator;

@Data
@Slf4j
public class CacheWrapper implements Comparator<CacheWrapper> {

    public static final String IDENTITY_HARD_CODE = "hardCode";

    private String identity = "";

    public enum CacheWrapperType {
        classDef,
        relDef,
    }

    public static CacheWrapperType ValueOf(String str) {
        if (!StringUtils.isEmpty(str)) {
            if (str.equalsIgnoreCase(classDefinitionType.RelDef.toString()))
                return CacheWrapperType.relDef;
            else if (str.equalsIgnoreCase(classDefinitionType.ClassDef.toString()))
                return CacheWrapperType.classDef;
        }
        return null;
    }

    private CacheWrapperType key;
    private String value;

    public CacheWrapper() {

    }

    public CacheWrapper(CacheWrapperType type, String value) {
        this.key = type;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return this.key.toString() + "->" + this.value;
    }

    @Override
    public int compare(CacheWrapper o1, CacheWrapper o2) {
        return o1.toString().toUpperCase().compareTo(o2.toString().toUpperCase());
    }
}
