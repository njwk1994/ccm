package ccm.server.schema.interfaces.defaults;

import ccm.server.schema.interfaces.generated.ICIMColumnSetBase;
import org.springframework.stereotype.Service;

public class ICIMColumnSetDefault extends ICIMColumnSetBase {
    public ICIMColumnSetDefault(boolean instantiateRequiredProperties) {
        super(instantiateRequiredProperties);
    }
}
