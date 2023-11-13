package ccm.server.module.impl.general;

import ccm.server.module.enums.serviceCategory;
import ccm.server.module.impl.base.ServiceImpl;
import ccm.server.module.service.base.IInternalService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InternalServiceImpl extends ServiceImpl implements IInternalService {
    @Override
    public serviceCategory getCategory() {
        return serviceCategory.internal;
    }
}
