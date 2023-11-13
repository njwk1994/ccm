package ccm.server.schema.model;

import ccm.server.entity.MetaDataObjInterface;
import ccm.server.enums.interfaceUpdateState;
import ccm.server.schema.collections.IPropertyCollection;
import ccm.server.schema.interfaces.IClassDef;
import ccm.server.schema.interfaces.IInterfaceDef;
import ccm.server.schema.interfaces.IObject;

public interface IInterface extends IObject {

    int InterfaceSequence();

    boolean Dynamical();

    void setDynamical(boolean value);

    boolean isIObjectOrIRel();

    String InterfaceDefinitionUID();

    IPropertyCollection Properties();

    String InterfaceOBID();

    void setInterfaceOBID(String interfaceObid);

    String InterfaceCreationDate();

    void setInterfaceCreationDate(String interfaceCreationDate);

    String InterfaceTerminationDate();

    void setInterfaceTerminationDate(String interfaceTerminationDate);

    String InterfaceTerminationUser();

    void setInterfaceTerminationUser(String interfaceTerminationUser);

    String InterfaceCreationUser();

    void setInterfaceCreationUser(String interfaceCreationUser);

    interfaceUpdateState UpdateState();

    boolean CreateAndTerminated();

    void setInterfaceUpdateState(interfaceUpdateState interfaceUpdateState);

    void setClass(ClassBase classBase) throws Exception;

    void terminateInterface() throws Exception;

    void deleteInterface() throws Exception;

    IInterfaceDef getInterfaceDefinition() throws Exception;

    void setPropertyValue(String interfaceDefinitionUid, String propertyDefinitionUid, Object value) throws Exception;

    boolean hasInterface(String interfaceDefinitionUid);

    boolean hasProperty(String propertyDefinition);

    ClassBase ClassBase();

    IClassDef GetClassDefinition() throws Exception;

    MetaDataObjInterface toDataInterface();

    boolean terminatedOrDeleted();
}
