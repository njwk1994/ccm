package ccm.server.cache.impl;

import ccm.server.cache.IApplicationCache;
import ccm.server.cache.ICacheRunner;
import ccm.server.cache.IProcessCache;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component("cacheRunnerImpl")
@Slf4j
@Order(value = 1)
public class CacheRunner implements ICacheRunner, CommandLineRunner {
    @Override
    public void run(String... args) {
        try {
            SpringContextUtils.getBean(IProcessCache.class).initialize();
        } catch (Exception exception) {
            log.error("cache runner impl running failed", exception);
        } finally {
            log.info("central information management system initialization progress finished");
        }
    }
}
