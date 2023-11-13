package ccm.server.schema.interfaces;

public interface ISchemaObject extends IObject {
    boolean ByCustom() throws Exception;

    void setByCustom(boolean byCustom) throws Exception;

    String DisplayName();

    void setDisplayName(String displayName) throws Exception;

    int SchemaRev();

    void setSchemaRev(int schemaRev) throws Exception;

    int SchemaVer();

    void setSchemaVer(int schemaVer) throws Exception;

    String SchemaRevVer();

    boolean Cached();

    void setCached(boolean value) throws Exception;

    String CachedLevel();

    void setCachedLevel(String value) throws Exception;

    Object Instantiate(boolean instantiateRequiredItems) throws Exception;

    Object Instantiate(String pstrOBID, String pstrUID, boolean instantiateRequiredItems) throws Exception;
}
