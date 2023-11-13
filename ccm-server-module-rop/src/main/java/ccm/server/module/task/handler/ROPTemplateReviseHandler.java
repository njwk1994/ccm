package ccm.server.module.task.handler;

import ccm.server.model.LoaderReport;
import ccm.server.module.task.ROPTemplateReviseTask;
import ccm.server.processors.ThreadsProcessor;
import ccm.server.queue.handler.IQueueTaskHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Data
public class ROPTemplateReviseHandler implements IQueueTaskHandler {

    private static final Long serializableId = 1L;
    private ROPTemplateReviseTask callable;
    private String uuid;
    private String type;
    private String creationUser;
    private Subject subject;
    private String config;

    public ROPTemplateReviseHandler(ROPTemplateReviseTask callable, String uuid, String creationUser, String type, Subject subject, String config) {
        this.callable = callable;
        this.uuid = uuid;
        this.creationUser = creationUser;
        this.type = type;
        this.subject = subject;
        this.config = config;
    }


    @Override
    public String UUID() {
        return this.uuid;
    }

    @Override
    public String creationUser() {
        return this.creationUser;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String config() {
        return this.config;
    }

    @Override
    public Callable<?> Task() {
        return this.callable;
    }

    @Override
    public Object execute() throws ExecutionException, InterruptedException {
        return this.subject.execute(this.callable);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ROPTemplateReviseHandler that = (ROPTemplateReviseHandler) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
