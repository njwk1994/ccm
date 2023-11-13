package ccm.server.module.service.base;

import ccm.server.module.enums.serviceCategory;

public interface IService {

    Class<?> getServiceType();

    String getServiceUniqueKey();

    boolean getActivated();

    void setActivated(boolean flag);

    serviceCategory getCategory();

    int getPriority();

    void initialize();

    void reset();

    void initializing();
}
