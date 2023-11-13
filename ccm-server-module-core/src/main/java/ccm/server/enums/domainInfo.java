package ccm.server.enums;

import java.util.ArrayList;
import java.util.List;

public enum domainInfo {
    SCHEMA,
    ADMIN,
    DATA,
    DOC,
    WF,
    PTPACKAGEMATERIAL,
    CCMROPCONDIFS,
    UNKNOWN;

    public static List<String> defaultDomainScopes() {
        List<String> result = new ArrayList<>();
        result.add(SCHEMA.toString());
        result.add(ADMIN.toString());
        result.add(DATA.toString());
        return result;
    }
}
