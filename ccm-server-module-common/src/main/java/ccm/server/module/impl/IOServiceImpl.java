package ccm.server.module.impl;

import ccm.server.module.impl.general.UtilityServiceImpl;
import ccm.server.module.service.IIOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("iOServiceImpl")
@Slf4j
public class IOServiceImpl extends UtilityServiceImpl implements IIOService {
}
