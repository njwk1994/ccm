package ccm.server.schema.interfaces;

import ccm.server.enums.classDefinitionType;
import ccm.server.enums.propertyValueType;
import ccm.server.enums.relDirection;

public interface ICIMDisplayItem extends IObject {

    ICIMSection getSection() throws Exception;

    String ItemType();

    void setItemType(String value) throws Exception;

    String SchemaDefinitionUID();

    void setSchemaDefinitionUID(String schemaDefinitionUID) throws Exception;

    boolean checkMandatory() throws Exception;

    relDirection checkRelDirection();

    classDefinitionType checkDefType();

    propertyValueType checkPropertyValueType() throws Exception;

    String DefaultQueryValue();

    void setDefaultQueryValue(String value) throws Exception;

    String DefaultCreateValue();

    void setDefaultCreateValue(String value) throws Exception;

    String DefaultUpdateValue();

    void setDefaultUpdateValue(String value) throws Exception;
}
