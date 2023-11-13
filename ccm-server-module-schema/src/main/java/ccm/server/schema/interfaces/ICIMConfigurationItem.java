package ccm.server.schema.interfaces;

public interface ICIMConfigurationItem extends IObject {

    String generateIObjectConfig();

    String TablePrefix();

    void setTablePrefix(String value) throws Exception;
}
