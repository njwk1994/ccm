package ccm.server.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum tableSuffix {
    OBJ,
    OBJPR,
    OBJIF,
    OBJREL,
    OBJPRDETAILS,
    unknown;

    public static List<tableSuffix> getTables() {
        List<tableSuffix> result = new ArrayList<>();
        result.add(tableSuffix.OBJ);
        result.add(tableSuffix.OBJIF);
        result.add(tableSuffix.OBJPR);
        result.add(tableSuffix.OBJREL);
        result.add(tableSuffix.OBJPRDETAILS);
        return result;
    }
}

