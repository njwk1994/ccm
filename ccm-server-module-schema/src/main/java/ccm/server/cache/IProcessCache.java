package ccm.server.cache;

import ccm.server.enums.propertyValueType;
import ccm.server.enums.relDirection;
import ccm.server.model.DynamicalDefinitionObj;
import ccm.server.models.query.QueryCriteria;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IDomain;
import ccm.server.schema.interfaces.IEnumEnum;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IProcessCache extends ICache {

    boolean isDefinitionUnderConfigControl(String uidOrObid);

    boolean isEnumListTypeProperty(String propertyDefinitionUid);

    void doAfterChangeScope();

    ICacheConfigurationService getCacheExtension();

    IObjectCollection queryObjectsByUIDAndClassDefinition(Collection<String> uids, String classDefinition) throws Exception;

    IDomain getDomainForClassDef(String classDefinitionUID) throws Exception;

    String getTablePrefixForClassDefinition(String classDefinitionUID);

    Map<String, String> schemaObjectImpliedBy();

    IObjectCollection getByNameOrDescription(String name, String description) throws Exception;

    IObjectCollection getEnumListTypeAndEnums();

    String parseExpectedValue(String propertyDefinitionUID, String value) throws Exception;

    String getPropertyValueTypeClassDefForPropertyDefinition(String propertyDefinitionUid);

    IObject getScopedByForPropertyDefinition(String propertyDefinitionUID);

    List<String> getImpliedByIDef(String pstrIDef);

    boolean getRequiredOrNotForPropertyExposesInterfaceDef(String propertyDefinitionUID);

    String getExposedInterfaceByPropertyDef(String propertyDef) throws Exception;

    String getDomainUIDForClassDefinition(String classDef) throws Exception;

    List<String> getRealizedInterfaceDefByClassDef(String classDefUID, boolean onlyRequired);

    List<String> getRealizesClassDefsByInterfaceDef(String interfaceDefinitionUID);

    void inflateCachedIRelFromDataBase(IRel rel) throws Exception;

    void inflateCachedIObjectFromDataBase(IObject iObject) throws Exception;

    void inflateCachedIObjectFromDataBase(IObjectCollection iObjects) throws Exception;

    boolean isStringTypeThatCannotBeZeroLength(String propertyDefinitionUID);

    IEnumEnum getEnumListLevelType(String pstrPropertyDef, String pstrValue) throws Exception;

    boolean isPropertyDefinitionHistoryRetained(String pstrPropertyDefinitionUID);

    IObjectCollection getIDefRelDefs(String interfaceDefinitionUID, relDirection direction);

    IObjectCollection getExposedPropDefsForInterfaceDef(String pstrInterfaceDef, boolean pblnOnlyRequired) throws Exception;

    List<String> getClassDefsForDesignObj() throws Exception;

    void setCriteriaAppendTable(QueryCriteria criteria) throws Exception;

    void addDynamicalPropertyDefinition(String propertyDefinitionUID, String displayAs, propertyValueType propertyValueType);

    DynamicalDefinitionObj getDynamicalDefinitionObj(String defUID);

    List<String> getScopeWisedDomains();
}
