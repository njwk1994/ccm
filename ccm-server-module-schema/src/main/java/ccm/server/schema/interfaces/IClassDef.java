package ccm.server.schema.interfaces;

import ccm.server.schema.collections.IObjectCollection;

import java.util.List;

public interface IClassDef extends ISchemaObject, ICacheInfo {

    void tryToSetIObjectConfig(IObject object) throws Exception;

    String getTablePrefixForInstantiating();

    IDomain getDomainForInstantiating() throws Exception;

    List<String> getUsedDomain();

    boolean isScopeWised() throws Exception;

    String UniqueKeyPattern();

    void setUniqueKeyPattern(String uniqueKeyDefinition) throws Exception;

    String SystemIDPattern();

    void setSystemIDPattern(String systemIDPattern) throws Exception;
    IObject instantiate(boolean instantiateRequiredItems) throws Exception;

    IObject BeginCreate() throws Exception;

    IObject BeginCreate(boolean pblnInstantiateRequiredItems) throws Exception;

    void FinishCreate(IObject pobjObject);

    IObjectCollection getRealizedInterfaceDefs() throws Exception;

    IObjectCollection getENSDefinitions() throws Exception;

    IObjectCollection getForms() throws Exception;

    IObject getDomainInfo() throws Exception;

    IObjectCollection getRequiredRealizedInterfaceDefs() throws Exception;

    IObjectCollection generateForm() throws Exception;

    IObjectCollection getAllPropertyDefs() throws Exception;
}
