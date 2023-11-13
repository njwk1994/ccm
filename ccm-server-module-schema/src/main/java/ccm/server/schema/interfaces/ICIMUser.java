package ccm.server.schema.interfaces;

import ccm.server.model.KeyValuePair;
import ccm.server.models.scope.ScopeConfiguration;
import ccm.server.schema.collections.IObjectCollection;

import java.util.List;

public interface ICIMUser extends IObject {

    ICIMConfigurationItem getCreateConfig(boolean cacheOnly) throws Exception;

    List<KeyValuePair> getUserDefaultInfo() throws Exception;

    IObjectCollection getDefaultInfo() throws Exception;

    void clearScope() throws Exception;

    void saveScope(ScopeConfiguration scopeConfiguration) throws Exception;

    void saveScope(IObject createConfig, IObjectCollection queryConfigs) throws Exception;

    IObjectCollection getQueryConfig() throws Exception;

    ScopeConfiguration getCurrentScope() throws Exception;
}
