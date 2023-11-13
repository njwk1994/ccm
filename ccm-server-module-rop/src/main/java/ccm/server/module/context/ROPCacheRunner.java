package ccm.server.module.context;

import ccm.server.cache.ICacheRunner;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(value = 2)
public class ROPCacheRunner implements ICacheRunner {
    @Override
    public void run(String... args) throws Exception {
        try {
            ROPCache ropCache = SpringContextUtils.getBean(ROPCache.class);
            ropCache.initialize();
        } catch (Exception exception) {
            log.error("ROP cache context failed", exception);
        }

    }
}
