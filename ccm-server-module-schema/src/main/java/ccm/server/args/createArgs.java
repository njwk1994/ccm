package ccm.server.args;

import ccm.server.schema.interfaces.ICIMConfigurationItem;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class createArgs extends suppressibleArgs {
    private boolean generateUIDs;

    public createArgs(boolean suppressEvents, String username, ICIMConfigurationItem configurationItem) {
        super(suppressEvents, username, configurationItem);
    }
}
