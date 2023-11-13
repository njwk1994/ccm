package ccm.server.schema.interfaces;

public interface ICacheInfo extends IObject {
    boolean CachedInd();

    void setCachedInd(boolean value) throws Exception;

    String CachedKey();

    void setCachedKey(String value) throws Exception;
}
