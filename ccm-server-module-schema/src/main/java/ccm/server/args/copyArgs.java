package ccm.server.args;

import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.IObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class copyArgs extends suppressibleArgs {
    private IObject oldObject;
    private IObject newObject;

    public copyArgs(IObject sourceObject, String username, ICIMConfigurationItem configurationItem) {
        super(false, username, configurationItem);
        this.setOldObject(sourceObject);
    }

}
