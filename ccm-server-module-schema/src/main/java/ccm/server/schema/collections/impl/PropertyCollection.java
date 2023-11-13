package ccm.server.schema.collections.impl;

import ccm.server.context.CIMContext;
import ccm.server.enums.domainInfo;
import ccm.server.util.CommonUtility;
import ccm.server.util.ReentrantLockUtility;
import ccm.server.schema.collections.IPropertyCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.model.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Data
@Slf4j
public class PropertyCollection implements IPropertyCollection {
    private final ConcurrentHashMap<String, IProperty> mcolProperties = new ConcurrentHashMap<>();
    private IInterface parent;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Iterator<Map.Entry<String, IProperty>> GetEnumerator() {
        Iterator<Map.Entry<String, IProperty>> iterator = null;
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            iterator = this.mcolProperties.entrySet().iterator();
        } catch (Exception exception) {
            log.error("get enumerator failed", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
        return iterator;
    }

    public PropertyCollection(IInterface parent) {
        this.parent = parent;
    }

    @Override
    public IProperty item(String propertyDefUid) throws Exception {
        if (!StringUtils.isEmpty(propertyDefUid)) {
            IProperty result = this.mcolProperties.getOrDefault(propertyDefUid, null);
            if (result == null)
                result = this.item(propertyDefUid, this.getParent().ClassBase().Lock().isWriteLocked());
            return result;
        }
        return null;
    }

    @Override
    public IProperty item(String propertyDefUid, boolean activate) throws Exception {
        if (!StringUtils.isEmpty(propertyDefUid)) {
            IObject item = CIMContext.Instance.ProcessCache().item(propertyDefUid, domainInfo.SCHEMA.toString());
            if (item == null)
                throw new Exception("invalid property definition UID " + propertyDefUid);
            IProperty result = null;
            result = this.mcolProperties.getOrDefault(propertyDefUid, null);
            if (result == null && activate) {
                ClassBase current = this.getParent().ClassBase();
                if (current.Lock().isWriteLocked()) {
                    result = this.mcolProperties.getOrDefault(propertyDefUid, null);
                    if (result == null && activate) {
                        try {
                            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                            result = new PropertyDefault(propertyDefUid);
                            if (!this.mcolProperties.containsKey(propertyDefUid))
                                result.setParent(this.getParent());
                            this.mcolProperties.putIfAbsent(propertyDefUid, result);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        } finally {
                            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
                        }
                    } else
                        throw new Exception("unable to update as object is out of date");
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public void add(IProperty property) {
        if (property != null) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                property.setParent(this.getParent());
                this.mcolProperties.remove(property.getPropertyDefinitionUid());
                this.mcolProperties.putIfAbsent(property.getPropertyDefinitionUid(), property);
            } catch (Exception exception) {
                log.error("add property failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    @Override
    public void addDynamical(String propertyDefUid, Object value) {
        if (!StringUtils.isEmpty(propertyDefUid)) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                IProperty result = new PropertyDefault(propertyDefUid);
                result.setDynamical(true);
                if (!this.mcolProperties.containsKey(propertyDefUid))
                    result.setParent(this.getParent());
                result.setValue(value);
                this.mcolProperties.putIfAbsent(propertyDefUid, result);
            } catch (Exception exception) {
                log.error("add dyn property failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    @Override
    public void add(Collection<IProperty> propertyCollection) {
        if (CommonUtility.hasValue(propertyCollection)) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                IInterface lobjInterface = this.getInterface();
                for (IProperty property : propertyCollection) {
                    if (!this.mcolProperties.containsKey(property.getPropertyDefinitionUid())) {
                        property.setParent(lobjInterface);
                        this.mcolProperties.putIfAbsent(property.getPropertyDefinitionUid(), property);
                    }
                }
            } catch (Exception exception) {
                log.error("add properties failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    @Override
    public void clear() {
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            this.mcolProperties.clear();
        } catch (Exception exception) {
            log.error("clear failed", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
    }

    @Override
    public IPropertyValue CurrentValue(String propertyDefUID) {
        if (!StringUtils.isEmpty(propertyDefUID)) {
            IPropertyValue result = null;
            IProperty property = this.get(propertyDefUID);
            if (property != null)
                result = property.CurrentValue();
            return result;
        }
        return null;
    }

    @Override
    public IInterface getInterface() {
        return this.parent;
    }

    @Override
    public void remove(IProperty property) {
        if (property != null) {
            this.remove(property.getPropertyDefinitionUid());
        }
    }

    @Override
    public void remove(String propertyDefUid) {
        if (!StringUtils.isEmpty(propertyDefUid)) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                this.mcolProperties.remove(propertyDefUid);
            } catch (Exception exception) {
                log.error("remove by property definition uid failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    @Override
    public boolean hasProperty(String propertyDefinition) {
        if (!StringUtils.isEmpty(propertyDefinition)) {
            return this.mcolProperties.containsKey(propertyDefinition);
        }
        return false;
    }

    @Override
    public boolean hasProperty() {
        return this.mcolProperties.size() > 0;
    }

    @Override
    public List<IProperty> toArrayList() {
        List<IProperty> result = new ArrayList<>();
        Iterator<Map.Entry<String, IProperty>> entryIterator = this.GetEnumerator();
        while (entryIterator.hasNext()) {
            result.add(entryIterator.next().getValue());
        }
        return result;
    }

    @Override
    public IProperty get(String propertyDefinitionUid) {
        if (!StringUtils.isEmpty(propertyDefinitionUid)) {
            return this.mcolProperties.getOrDefault(propertyDefinitionUid, null);
        }
        return null;
    }
}
