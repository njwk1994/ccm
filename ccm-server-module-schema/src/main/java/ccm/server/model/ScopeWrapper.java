package ccm.server.model;

import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.IObject;
import lombok.Data;

@Data
public class ScopeWrapper {
    private ICIMConfigurationItem createConfig;
    private IObjectCollection queryConfigs;
}
