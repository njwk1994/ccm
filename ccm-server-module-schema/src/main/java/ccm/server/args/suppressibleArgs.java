package ccm.server.args;

import ccm.server.schema.interfaces.ICIMConfigurationItem;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class suppressibleArgs {
    private boolean suppressEvents;

    private String username;
    private ICIMConfigurationItem configurationItem;

    public suppressibleArgs() {

    }

    public suppressibleArgs(boolean pblnSuppressEvents, String username, ICIMConfigurationItem configurationItem) {
        this();
        this.suppressEvents = pblnSuppressEvents;
        this.username = username;
        this.configurationItem = configurationItem;
    }
}

