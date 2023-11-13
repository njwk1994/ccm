package ccm.server.enums;

import java.util.ArrayList;
import java.util.List;

public enum orderMode {
    asc(true),
    desc(false),
    none(null);

    private final Boolean ascOrNot;

    private orderMode(Boolean ascOrNot) {
        this.ascOrNot = ascOrNot;
    }

    public List<String> getString() {
        return new ArrayList<String>() {{
            this.add(toString());
            this.add(ascOrNot != null ? ascOrNot.toString() : "null");
        }};
    }
}
