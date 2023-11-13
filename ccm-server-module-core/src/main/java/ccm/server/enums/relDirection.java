package ccm.server.enums;

import ccm.server.helper.HardCodeHelper;
import org.springframework.util.StringUtils;

public enum relDirection {
    _unknown("未知", ""),
    _1To2("由一端展开到二端", "+"),
    _2To1("由二端展开到一端", "-");

    private final String displayAs;
    private final String uid;
    private final String prefix;

    public static relCollectionTypes toRelCollectionTypesForStart(relDirection direction) {
        switch (direction) {
            case _1To2:
                return relCollectionTypes.End1s;
            case _2To1:
                return relCollectionTypes.End2s;
        }
        return relCollectionTypes.Unknown;
    }

    public static relCollectionTypes toRelCollectionTypesForEnd(relDirection direction) {
        switch (direction) {
            case _1To2:
                return relCollectionTypes.End2s;
            case _2To1:
                return relCollectionTypes.End1s;
        }
        return relCollectionTypes.Unknown;
    }

    private relDirection(String displayAs, String prefix) {
        this.displayAs = displayAs;
        this.prefix = prefix;
        this.uid = "relDirection_" + this;
    }

    public String getDisplayAs() {
        return this.displayAs;
    }

    public String getUid() {
        return this.uid;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public static relDirection toEnumByPrefix(String prefix) {
        if (!StringUtils.isEmpty(prefix)) {
            for (relDirection value : relDirection.values()) {
                if (value.getPrefix().equalsIgnoreCase(prefix))
                    return value;
            }
        }
        return relDirection._unknown;
    }

    public static String toActualRelDef(String relDef) {
        if (relDef.startsWith("+") || relDef.startsWith("-"))
            relDef = relDef.substring(1);
        return relDef;
    }


    public static relDirection toEnumByRelDef(String relDef) {
        if (!StringUtils.isEmpty(relDef)) {
            if (relDef.startsWith("+"))
                return relDirection._1To2;
            else if (relDef.startsWith("-"))
                return relDirection._2To1;
            else if (relDef.startsWith(HardCodeHelper.UNIQUE_EDGE_UID_PREFIX))
                return relDirection._1To2;
        }
        //change unknown to 1_to_2 as default info
        return relDirection._1To2;
    }
}
