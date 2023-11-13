package ccm.server.schema.interfaces;

public interface ICIMMinioConfig extends IObject {

    void setCIMMinioUsername(String username) throws Exception;

    String getCIMMinioUsername();

    void setCIMMinioPassword(String password) throws Exception;

    String getCIMMinioPassword();

    void setCIMMinioIP(String ip) throws Exception;

    String getCIMMinioIP();

    void setCIMMinioInterIP(String interIP) throws Exception;

    String getCIMMinioInterIP();

    void setCIMMinioPort(Integer port) throws Exception;

    Integer getCIMMinioPort();

    void setCIMMinioSSL(Boolean ssl) throws Exception;

    Boolean getCIMMinioSSL();

    void setCIMMinioBucket(String bucket) throws Exception;

    String getCIMMinioBucket();


}
