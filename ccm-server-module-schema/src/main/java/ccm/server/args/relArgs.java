package ccm.server.args;

import ccm.server.enums.relDirection;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.IRel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class relArgs extends cancelArgs {
    private IRel rel;

    private ccm.server.enums.relDirection relDirection;

    public relArgs(IRel pblnRel, relDirection relDirection, String username, ICIMConfigurationItem configurationItem) {
        super(false, username, configurationItem);
        this.rel = pblnRel;
        this.relDirection = relDirection;
    }
}
