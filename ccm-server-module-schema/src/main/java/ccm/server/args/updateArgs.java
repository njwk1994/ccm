package ccm.server.args;

import ccm.server.schema.interfaces.ICIMConfigurationItem;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class updateArgs extends suppressibleArgs {
    public updateArgs(boolean suppressEvent, String username, ICIMConfigurationItem configurationItem) {
        super(suppressEvent, username, configurationItem);
    }
}
