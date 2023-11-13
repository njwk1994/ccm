package ccm.server.module.broker;

import ccm.server.module.service.base.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("serviceBroker")
@Slf4j
public class ServiceBroker {
    @Autowired
    private Map<String, IService> supportServices;
}
