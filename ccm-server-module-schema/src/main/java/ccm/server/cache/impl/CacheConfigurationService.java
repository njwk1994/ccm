package ccm.server.cache.impl;

import ccm.server.cache.IApplicationCache;
import ccm.server.cache.ICache;
import ccm.server.cache.ICacheConfigurationService;
import ccm.server.cache.IProcessCache;
import ccm.server.enums.classDefinitionType;
import ccm.server.enums.relDefinitionType;
import ccm.server.model.CacheWrapper;
import ccm.server.module.impl.general.InternalServiceImpl;
import ccm.server.util.CommonUtility;
import ccm.server.util.ReentrantLockUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service("cacheExtension")
@Slf4j
public class CacheConfigurationService extends InternalServiceImpl implements ICacheConfigurationService {
    @Autowired
    private ICache[] caches;
    private final Map<String, List<CacheWrapper>> cacheSettingWrapper = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public static ICacheConfigurationService Instance;

    public CacheConfigurationService() {
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            List<CacheWrapper> wrapperList = new ArrayList<>();
            List<String> classDefinitionsShallBeCached = new ArrayList<>(classDefinitionType.getRequiredToCachedClassDefinitionForRunner());
            for (String s : classDefinitionsShallBeCached) {
                CacheWrapper cacheWrapper = new CacheWrapper() {{
                    this.setKey(CacheWrapperType.classDef);
                    this.setValue(s);
                    this.setIdentity(CacheWrapper.IDENTITY_HARD_CODE);
                }};
                wrapperList.add(cacheWrapper);
            }
            List<String> relDefinitionShallBeCached = new ArrayList<>(relDefinitionType.getRequiredToCachedRelDefs());
            for (String s : relDefinitionShallBeCached) {
                CacheWrapper cacheWrapper = new CacheWrapper() {{
                    this.setKey(CacheWrapperType.relDef);
                    this.setValue(s);
                    this.setIdentity(CacheWrapper.IDENTITY_HARD_CODE);
                }};
                wrapperList.add(cacheWrapper);
            }
            this.cacheSettingWrapper.put(IApplicationCache.class.getSimpleName(), wrapperList);
        } catch (Exception exception) {
            log.error("cache extension failed", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
    }

    @PostConstruct
    public void doInit() {
        Instance = this;
    }

    @Override
    public boolean cacheOrNot(String relDefOrClassDef) {
        return this.cacheOrNot(null, relDefOrClassDef);
    }

    @Override
    public boolean cacheOrNot(String key, String relDefOrClassDef) {
        List<CacheWrapper> cacheWrappers = new ArrayList<>();
        if (!StringUtils.isEmpty(key)) {
            String[] strings = key.split(",");
            for (String string : strings) {
                List<CacheWrapper> wrappers = this.cacheSettingWrapper.getOrDefault(string, null);
                if (CommonUtility.hasValue(wrappers))
                    cacheWrappers.addAll(wrappers);
            }
        } else {
            cacheWrappers = new ArrayList<>();
            for (Map.Entry<String, List<CacheWrapper>> stringListEntry : this.cacheSettingWrapper.entrySet()) {
                cacheWrappers.addAll(stringListEntry.getValue());
            }
        }
        if (CommonUtility.hasValue(cacheWrappers)) {
            return cacheWrappers.stream().anyMatch(c -> c.getValue().equalsIgnoreCase(relDefOrClassDef));
        }
        return false;
    }

    @Override
    public void setClassDefsToBeCached(String key, List<String> classDefs) {
        if (CommonUtility.hasValue(classDefs)) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                String[] strings = key.split(",");
                for (String string : strings) {
                    List<CacheWrapper> cacheWrappers = this.cacheSettingWrapper.getOrDefault(string, new ArrayList<>());
                    for (String classDef : classDefs) {
                        cacheWrappers.add(new CacheWrapper() {{
                            this.setKey(CacheWrapperType.classDef);
                            this.setValue(classDef);
                            this.setIdentity(string);
                        }});
                    }
                    cacheWrappers = cacheWrappers.stream().distinct().collect(Collectors.toList());
                    if (this.cacheSettingWrapper.containsKey(string))
                        this.cacheSettingWrapper.replace(string, cacheWrappers);
                    else
                        this.cacheSettingWrapper.put(string, cacheWrappers);
                }
            } catch (Exception exception) {
                log.error("add cached class def error", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    protected void onAddCachedDefinition(String key, CacheWrapper.CacheWrapperType cacheWrapperType, String definitionUID) {
        List<CacheWrapper> cacheWrappers = this.cacheSettingWrapper.getOrDefault(key, new ArrayList<>());
        CacheWrapper cacheWrapper = new CacheWrapper(cacheWrapperType, definitionUID);
        cacheWrappers.add(cacheWrapper);
        cacheWrappers = cacheWrappers.stream().distinct().collect(Collectors.toList());
        if (this.cacheSettingWrapper.containsKey(key)) {
            this.cacheSettingWrapper.replace(key, cacheWrappers);
        } else {
            this.cacheSettingWrapper.put(key, cacheWrappers);
        }
    }

    @Override
    public void setDefinitionToBeCached(String key, CacheWrapper.CacheWrapperType cacheWrapperType, String definitionUID) {
        if (StringUtils.isEmpty(key)) {
            key = IProcessCache.class.getSimpleName();
        }
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            this.onAddCachedDefinition(key, cacheWrapperType, definitionUID);
        } catch (Exception exception) {
            log.error("add cached definition error", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
    }

    @Override
    public void setDefinitionToBeCached(CacheWrapper.CacheWrapperType cacheWrapperType, Map<String, List<String>> details) {
        if (details != null && details.size() > 0) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                for (Map.Entry<String, List<String>> listEntry : details.entrySet()) {
                    List<CacheWrapper> wrappers = this.cacheSettingWrapper.getOrDefault(listEntry.getKey(), new ArrayList<>());
                    for (String v : listEntry.getValue()) {
                        CacheWrapper cacheWrapper = new CacheWrapper(cacheWrapperType, v);
                        cacheWrapper.setIdentity(listEntry.getKey());
                        wrappers.remove(cacheWrapper);
                        wrappers.add(cacheWrapper);
                    }
                    wrappers = wrappers.stream().distinct().collect(Collectors.toList());
                    if (this.cacheSettingWrapper.containsKey(listEntry.getKey()))
                        this.cacheSettingWrapper.replace(listEntry.getKey(), wrappers);
                    else
                        this.cacheSettingWrapper.put(listEntry.getKey(), wrappers);
                }
            } catch (Exception exception) {
                log.error("add cached definition error", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    @Override
    public void setRelDefsToBeCached(String key, List<String> relDefs) {
        if (CommonUtility.hasValue(relDefs)) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                String[] strings = key.split(",");
                for (String string : strings) {
                    List<CacheWrapper> cacheWrappers = this.cacheSettingWrapper.getOrDefault(string, new ArrayList<>());
                    for (String relDef : relDefs) {
                        cacheWrappers.add(new CacheWrapper() {{
                            this.setKey(CacheWrapperType.relDef);
                            this.setValue(relDef);
                            this.setIdentity(string);
                        }});
                    }
                    cacheWrappers = cacheWrappers.stream().distinct().collect(Collectors.toList());
                    if (this.cacheSettingWrapper.containsKey(string))
                        this.cacheSettingWrapper.replace(string, cacheWrappers);
                    else
                        this.cacheSettingWrapper.put(string, cacheWrappers);
                }
            } catch (Exception exception) {
                log.error("add cached rel def error", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    @Override
    public void resetApplicationCacheConfiguration() {
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            for (Map.Entry<String, List<CacheWrapper>> stringListEntry : this.cacheSettingWrapper.entrySet()) {
                List<CacheWrapper> cacheWrapperList = stringListEntry.getValue().stream().filter(c -> !c.getIdentity().equalsIgnoreCase(CacheWrapper.IDENTITY_HARD_CODE)).collect(Collectors.toList());
                stringListEntry.getValue().removeAll(cacheWrapperList);
            }
        } catch (Exception exception) {
            log.error("reset application cache configuration failed", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
    }

    @Override
    public void addCachedWrappers(List<CacheWrapper> cacheWrappers) {
        if (CommonUtility.hasValue(cacheWrappers)) {
            Map<String, List<CacheWrapper>> mapCachedWrappers = new HashMap<>();
            for (CacheWrapper cacheWrapper : cacheWrappers) {
                CommonUtility.doAddElementGeneral(mapCachedWrappers, cacheWrapper.getIdentity(), cacheWrapper);
            }
            if (mapCachedWrappers.size() > 0) {
                for (Map.Entry<String, List<CacheWrapper>> listEntry : mapCachedWrappers.entrySet()) {
                    List<CacheWrapper> tempWrappers = this.cacheSettingWrapper.getOrDefault(listEntry.getKey(), new ArrayList<>());
                    tempWrappers.addAll(listEntry.getValue());
                    if (this.cacheSettingWrapper.containsKey(listEntry.getKey())) {
                        this.cacheSettingWrapper.replace(listEntry.getKey(), tempWrappers);
                    } else {
                        this.cacheSettingWrapper.put(listEntry.getKey(), tempWrappers);
                    }
                }
            }
        }
    }

    @Override
    public ICache[] caches() {
        return this.caches;
    }
}
