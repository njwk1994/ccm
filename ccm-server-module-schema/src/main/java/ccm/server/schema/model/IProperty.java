package ccm.server.schema.model;

import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.*;

public interface IProperty {

    String getPropertyDefinitionUid();

    boolean Dynamical();

    void setDynamical(boolean value);

    boolean isTerminated();

    IPropertyValueCollection PropertyValues();

    IPropertyValue CurrentValue();

    Object Value();

    String getObid();

    void setObid(String obid);

    void setValue(Object value) throws Exception;

    void setValue(Object value, String uom) throws Exception;

    String getUom();

    void setUom(String uom);

    String getTerminationDate();

    void setTerminationDate(String terminationDate);

    IInterface getParent();

    void setParent(IInterface objInterface);

    void terminateProperty();

    void deleteProperty();

    IPropertyDef getPropertyDefinition();

    IEnumEnum getEnumListEntry() throws Exception;

    IEnumEnum getEnumListEntry(String pstrValue) throws Exception;

    IEnumEnum getEnumListEntry(String pstrValue, IObjectCollection pcolCollection);

    IEnumEnum getEnumListLevelType() throws Exception;

    IEnumEnum getEnumListLevelType(IPropertyValue propertyValue) throws Exception;

    IEnumEnum getEnumListLevelType(String pstrValue) throws Exception;

    IEnumEnum getEnumListLevelType(IEnumListType pobjEnumList, String pstrValue, int pintTargetLevel, int pintCurrentLevel) throws Exception;

    IEnumEnum getUoMEntry() throws Exception;

    IUoMEnum getUoMEntry(String pstrValue) throws Exception;

    boolean isEnumList();

    boolean isEnumListLevel();

    boolean isUoM();

    boolean isBoolean();

    boolean isDouble();

    boolean isDate();

    boolean isYMD();

    boolean isInteger();

    boolean isValidValue();

    boolean isUpdatable() throws Exception;

    String toShortDisplayValue() throws Exception;

    String toDisplayValue();

    IObjectCollection getEnumEntries() throws Exception;
}
