package ccm.server.cache;

import ccm.server.enums.relDirection;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;

import java.util.List;
import java.util.Map;

public interface IApplicationCache extends ICache {

    boolean isSchemaControlledByConfig(String definitionUID);

    String getTablePrefixForClassDefinition(String classDefinitionUID);

    IObject getDomainForClassDef(String classDefinitionUID);

    List<String> getScopeWisedDomains();

    boolean initialized();

    void amount() throws Exception;

    Map<String, String> schemaObjectImpliedBy();

    String getPropertyValueTypeClassDefForPropertyDefinition(String propertyDefinitionUid);

    IObject getScopedByForPropertyDefinition(String propertyDefinitionUID);

    List<String> getImpliedByIDef(String pstrIDef);

    boolean getRequiredOrNotForPropertyExposesInterfaceDef(String propertyDefinitionUID);

    String getExposedInterfaceByPropertyDef(String propertyDef);

    List<String> getRealizedInterfaceDefByClassDef(String classDefUID, boolean onlyRequired);

    List<String> getRealizesClassDefsByInterfaceDef(String interfaceDefinitionUID);

    boolean isStringTypeThatCannotBeZeroLength(String propertyDefinitionUID);

    boolean isPropertyDefinitionHistoryRetained(String pstrPropertyDefinitionUID);

    IObjectCollection getIDefRelDefs(String interfaceDefinitionUID, relDirection direction);

    IObjectCollection getExposedPropDefsForInterfaceDef(String interfaceDef, boolean onlyRequried);
}
