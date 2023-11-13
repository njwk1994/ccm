package ccm.server.cache;

import ccm.server.model.CacheWrapper;
import ccm.server.module.service.base.IInternalService;

import java.util.List;
import java.util.Map;

public interface ICacheConfigurationService extends IInternalService {

    boolean cacheOrNot(String relDefOrClassDef);

    boolean cacheOrNot(String key, String relDefOrClassDef);

    void setClassDefsToBeCached(String key, List<String> classDefs);

    void setDefinitionToBeCached(String key, CacheWrapper.CacheWrapperType cacheWrapperType, String definitionUID);

    void setDefinitionToBeCached(CacheWrapper.CacheWrapperType cacheWrapperType, Map<String, List<String>> details);

    void setRelDefsToBeCached(String key, List<String> relDefs);

    void resetApplicationCacheConfiguration();

    void addCachedWrappers(List<CacheWrapper> cacheWrappers);

    ICache[] caches();
}
