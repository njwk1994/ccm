package ccm.server.args;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class validateArgs extends suppressibleArgs {
    private boolean isValid;
    private final Exception error;

    public validateArgs(Exception exception) {
        this.error = exception;
    }

}
