package ccm.server.context;

import ccm.server.cache.ICacheRunner;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(value = 3)
public class PriorityCacheRunner implements ICacheRunner {
    @Override
    public void run(String... args) throws Exception {
        try {
            PriorityCache cache = SpringContextUtils.getBean(PriorityCache.class);
            if (!cache.initialized()) {
                cache.initialize();
            }
        } catch (Exception exception) {
            log.error("ROP cache context failed", exception);
        }
    }
}
