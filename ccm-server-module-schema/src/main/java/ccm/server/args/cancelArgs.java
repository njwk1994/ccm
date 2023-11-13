package ccm.server.args;

import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.util.CommonUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class cancelArgs extends suppressibleArgs {
    private boolean cancel;
    private Exception exception;
    private String cancelMessage;
    private final List<Exception> cancelExceptions = new ArrayList<>();
    private final List<String> cancelMessages = new ArrayList<>();

    public void raiseError() throws Exception {
        if (CommonUtility.hasValue(cancelExceptions)) {
            throw this.cancelExceptions.get(0);
        }
    }

    public void setException(Exception exception) {
        if (exception != null) {
            this.exception = exception;
            this.cancelExceptions.add(exception);
        }
    }

    public void setCancelMessage(String cancelMessage) {
        this.cancelMessage = cancelMessage;
        this.cancelMessages.add(cancelMessage);
    }

    public cancelArgs(boolean suppressEvents, String username, ICIMConfigurationItem configurationItem) {
        super(suppressEvents, username, configurationItem);
    }

    public cancelArgs() {
        super();
    }
}

