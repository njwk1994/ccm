package ccm.server.schema.model;

import ccm.server.entity.MetaDataObjProperty;
import ccm.server.enums.propertyValueUpdateState;
import ccm.server.schema.interfaces.IEnumEnum;
import ccm.server.schema.interfaces.IUoMEnum;

public interface IPropertyValue extends Comparable<IPropertyValue> {

    propertyValueUpdateState UpdateState();

    void setUpdateState(propertyValueUpdateState updateState);

    boolean isTerminated();

    IProperty getParent();

    String OBID();

    void setOBID(String obid);

    Object Value();

    void setValue(Object value);

    double FloatValue();

    void setFloatValue(double floatValue);

    String UoM();

    void setUoM(String uoM);

    boolean isTemporaryValue();

    void setIsTemporaryValue(boolean isTemporaryValue);

    boolean isValidValue();

    String toString();

    boolean CreatedAndTerminated();

    String TerminationDate();

    void setTerminationDate(String pstrTerminationDate);

    IEnumEnum getEnumListEntry() throws Exception;

    IEnumEnum getEnumListLevelType() throws Exception;

    IUoMEnum getUoMEntry() throws Exception;

    String CreationDate();

    void setCreationDate(String pstrCreationDate);

    MetaDataObjProperty toDataProperty();

    boolean terminatedOrDeleted();
}
