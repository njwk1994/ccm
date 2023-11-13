package ccm.server.module.impl.base;

import ccm.server.module.enums.serviceCategory;
import ccm.server.module.service.base.IService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
public class ServiceImpl implements IService {
    private Boolean activated;

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @Override
    public Class<?> getServiceType() {
        return this.getClass();
    }

    @Override
    public String getServiceUniqueKey() {
        return null;
    }

    @Override
    public boolean getActivated() {
        return this.activated;
    }

    @Override
    public void setActivated(boolean flag) {
        this.activated = flag;
    }

    @Override
    public serviceCategory getCategory() {
        return serviceCategory.notInitialized;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void initialize() {
        if (!this.initialized.get()) {
            this.initializing();
            this.initialized.set(true);
        }
    }

    @Override
    public void reset() {
        this.initialized.set(false);
    }

    @Override
    public void initializing() {

    }
}
