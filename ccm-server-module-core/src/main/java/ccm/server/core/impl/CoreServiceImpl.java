package ccm.server.core.impl;

import ccm.server.core.ICoreService;
import ccm.server.module.impl.general.InternalServiceImpl;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service("coreServiceImpl")
@Slf4j
public class CoreServiceImpl extends InternalServiceImpl implements ICoreService {
    protected Map<String, Class<?>> registerEntities = new HashMap<>();
    protected Map<String, String> entityTypes = new HashMap<>();
    @Override
    public void registerEntity(Class<?> entityClass) {
        if (entityClass != null) {
            String simpleName = entityClass.getSimpleName();
            if (!this.registerEntities.containsKey(simpleName)) {
                {
                    this.registerEntities.put(simpleName, entityClass);
                    if (entityClass.isAnnotationPresent(ApiModel.class)) {
                        ApiModel annotation = entityClass.getAnnotation(ApiModel.class);
                        this.entityTypes.put(simpleName, annotation.description());
                    }
                }
            }
        }
    }

    @Override
    public List<String> getEntityProperties(String className) {
        List<String> result = new ArrayList<>();
        if (!StringUtils.isEmpty(className)) {
            if (this.registerEntities.containsKey(className)) {
                Class<?> entityClass = this.registerEntities.get(className);
                if (entityClass != null) {
                    Field[] fields = entityClass.getDeclaredFields();
                    if (fields.length > 0) {
                        for (Field f : fields) {
                            if (f.isAnnotationPresent(ApiModelProperty.class)) {
                                ApiModelProperty annotation = f.getAnnotation(ApiModelProperty.class);
                                result.add(annotation.name() + "," + annotation.value());

                            }
                        }
                    }
                }
            } else {
                if (this.entityTypes.containsValue(className)) {
                    for (Map.Entry<String, String> e : this.entityTypes.entrySet()) {
                        if (e.getValue().equalsIgnoreCase(className)) {
                            className = e.getKey();
                            break;
                        }
                    }
                    return this.getEntityProperties(className);
                }
            }
        }
        return result;
    }

    @Override
    public List<String> getSupportedEntities() {
        ArrayList<String> result = new ArrayList<>(this.entityTypes.values());
        result.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        return result;
    }

    private boolean allowCustomCode = true;

    @Override
    public boolean allowCustomCode() {
        return allowCustomCode;
    }

    @Override
    public void setAllowCustomCode(boolean allowCustomCode) {
        this.allowCustomCode = allowCustomCode;
    }
}
