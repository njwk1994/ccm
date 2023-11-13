package ccm.server.enums;

import java.util.ArrayList;

public enum formPurpose {
    Create,
    Update,
    Query,
    Info,
    List,
    Revise;

    public static String foFillingString() {
        return String.join(",", new ArrayList<String>() {{
            add(Create.toString());
            add(Update.toString());
            add(Query.toString());
            add(Info.toString());
            add(List.toString());
        }});
    }
}
