package ccm.server.language.impl;

import ccm.server.language.ILanguageService;
import ccm.server.model.LanguageEntity;
import ccm.server.module.impl.general.UtilityServiceImpl;
import ccm.server.util.ReentrantLockUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service("languageServiceImpl")
@Slf4j
public class LanguageServiceImpl extends UtilityServiceImpl implements ILanguageService {
    private final ThreadLocal<String> localizationThreadLocal = new ThreadLocal<>();
    private final ConcurrentHashMap<Integer, Map<String, LanguageEntity>> entries = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final String DEFAULT_LOCALIZATION = "zh-CN";

    @Override
    public String getCurrentLocalization() {
        String result = localizationThreadLocal.get();
        if (result == null || StringUtils.isEmpty(result))
            result = DEFAULT_LOCALIZATION;
        return result;
    }

    @Override
    public void amount() {

    }

    protected void onAdd(int code, String local, String value) {
        LanguageEntity languageEntity = new LanguageEntity() {
            {
                this.setLocal(local);
                this.setValue(value);
            }
        };
        Map<String, LanguageEntity> languageEntityMap = this.entries.getOrDefault(code, new HashMap<>());
        languageEntityMap.remove(local);
        languageEntityMap.put(local, languageEntity);
        if (this.entries.containsKey(code))
            this.entries.replace(code, languageEntityMap);
        else
            this.entries.put(code, languageEntityMap);
    }

    @Override
    public String get(Integer code) {
        Map<String, LanguageEntity> languageEntityMap = this.entries.getOrDefault(code, null);
        if (languageEntityMap != null) {
            LanguageEntity languageEntity = languageEntityMap.getOrDefault(this.getCurrentLocalization(), null);
            if (languageEntity != null)
                return languageEntity.getValue();
        }
        return "";
    }

    @Override
    public void add(Integer code, String local, String value) {
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            this.onAdd(code, local, value);
        } catch (Exception exception) {
            log.error("add failed", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
    }

    protected void onRemove(int code) {
        this.entries.remove(code);
    }

    @Override
    public void remove(Integer code) {
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            this.onRemove(code);
        } catch (Exception exception) {
            log.error("remove failed", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
    }

    @Override
    public void save() {
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
        } catch (Exception exception) {
            log.error("save error", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
    }
}
