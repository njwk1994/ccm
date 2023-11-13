package ccm.server.module.materials.enums;

public enum DataBaseType {
    oracle,
    sqlServer,
    mySQL;

    public static DataBaseType getType(String type) {
        DataBaseType[] values = DataBaseType.values();
        for (DataBaseType value : values) {
            if (value.toString().equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }
}
