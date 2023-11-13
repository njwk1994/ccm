package ccm.server.core;

import ccm.server.module.service.base.IInternalService;

import java.util.List;

public interface ICoreService extends IInternalService {
    void initialize();

    void registerEntity(Class<?> entityClass);

    List<String> getEntityProperties(String simpleName);

    List<String> getSupportedEntities();

    boolean allowCustomCode();

    void setAllowCustomCode(boolean allowCustomCode);
}
