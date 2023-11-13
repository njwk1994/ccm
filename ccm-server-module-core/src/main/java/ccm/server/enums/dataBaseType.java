package ccm.server.enums;

public enum dataBaseType {
    oracle,
    sqlServer,
    mySQL;

    public static dataBaseType getType(String type) {
        dataBaseType[] values = dataBaseType.values();
        for (dataBaseType value : values) {
            if (value.toString().equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }
}
