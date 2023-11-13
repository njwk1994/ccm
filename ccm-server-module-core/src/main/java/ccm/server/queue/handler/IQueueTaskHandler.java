package ccm.server.queue.handler;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public interface IQueueTaskHandler extends Serializable{

    String UUID();

    String creationUser();

    String type();

    String config();

    Callable<?> Task();

    Object execute() throws ExecutionException, InterruptedException;
}
