package ccm.server.schema.collections.impl;

import ccm.server.comparers.InterfaceComparer;
import ccm.server.context.CIMContext;
import ccm.server.enums.domainInfo;
import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.interfaceUpdateState;
import ccm.server.enums.objectUpdateState;
import ccm.server.schema.collections.IInterfaceCollection;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.model.ClassBase;
import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.util.ReentrantLockUtility;
import ccm.server.utils.ValueConversionUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class InterfaceCollection implements IInterfaceCollection {
    private final ConcurrentHashMap<String, IInterface> mcolInterfaces = new ConcurrentHashMap<>();
    private final ClassBase classBase;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean allowSorting = true;

    @Override
    public Iterator<Map.Entry<String, IInterface>> GetEnumerator() {
        Iterator<Map.Entry<String, IInterface>> result = null;
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            result = this.mcolInterfaces.entrySet().iterator();
        } catch (Exception exception) {
            log.error("get enumerator failed", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
        return result;
    }

    @Override
    public int indexOf(IInterface i) {
        if (i != null)
            return new ArrayList<>(this.mcolInterfaces.keySet()).indexOf(i.InterfaceDefinitionUID());
        return -1;
    }

    @Override
    public void remove(IInterface i) {
        if (i != null) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                this.mcolInterfaces.remove(i.InterfaceDefinitionUID());
            } catch (Exception exception) {
                log.error("remove interface failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    @Override
    public void sort() {
        this.sortInnerCollectionBySpecializationValue();
    }

    @Override
    public int size() {
        return this.mcolInterfaces.size();
    }

    @Override
    public boolean hasInterface() {
        return this.mcolInterfaces.size() > 0;
    }

    public InterfaceCollection(ClassBase classBase) {
        this.classBase = classBase;
    }

    private IInterface baseInterface;

    @Override
    public boolean AllowSorting() {
        return this.allowSorting;
    }

    @Override
    public List<IInterface> toArrayList() {
        List<IInterface> result = new ArrayList<>();
        Iterator<Map.Entry<String, IInterface>> iterator = this.GetEnumerator();
        while (iterator.hasNext()) {
            result.add(iterator.next().getValue());
        }
        return result;
    }

    @Override
    public void setAllowSorting(boolean allowSorting) {
        this.allowSorting = allowSorting;
    }

    @Override
    public ClassBase GetClass() {
        return this.classBase;
    }

    @Override
    public IInterface get(int index) {
        if (index >= 0)
            return new ArrayList<>(this.mcolInterfaces.values()).get(index);
        return null;
    }

    public IInterface IObject() {
        return this.baseInterface;
    }

    public IInterface item(String uid) throws Exception {
        if (uid.equalsIgnoreCase(interfaceDefinitionType.IObject.toString()) && this.baseInterface != null)
            return this.baseInterface;
        IInterface result = this.mcolInterfaces.getOrDefault(uid, null);
        if (result == null)
            result = this.item(uid, this.classBase.Lock().isWriteLocked());
        return result;
    }

    public IInterface item(String uid, boolean activate) throws Exception {
        if (uid.equalsIgnoreCase(interfaceDefinitionType.IObject.toString()) && this.baseInterface != null)
            return this.baseInterface;
        return this.item(uid, activate, false);
    }

    public IInterface item(String uid, boolean activate, boolean instantiateRequiredItems) throws Exception {
        if (uid.equalsIgnoreCase(interfaceDefinitionType.IObject.toString()) && this.baseInterface != null)
            return this.baseInterface;
        IInterface result = null;
        result = this.mcolInterfaces.getOrDefault(uid, null);
        if (result == null && activate) {
            ClassBase current = this.classBase;
            if (current.Lock().isWriteLocked()) {
                result = this.mcolInterfaces.getOrDefault(uid, null);
                if (result == null) {
                    if (activate) {
                        IObject item = CIMContext.Instance.ProcessCache().item(uid, domainInfo.SCHEMA.toString(), false);
                        if (item == null)
                            log.trace("invalid interface definition " + uid + " from cache");

                        result = (IInterface) CIMContext.Instance.getSchemaActivator().newInstance(
                                "ccm.server.schema.interfaces.defaults." + uid + "Default",
                                "ccm.server.schema.model.InterfaceDefault", new Object[]{false});
                        if (result != null) {
                            if (result instanceof InterfaceDefault)
                                ((InterfaceDefault) result).setInterfaceDefinitionUID(uid);
                            this.tryToSetInterfaceUpdateState(result);
                            this.add(result);
                        } else
                            throw new Exception("cannot find any class in code");
                    } else
                        throw new Exception("write access is not granted for adding " + uid);
                }
            } else
                throw new Exception("write access is not granted for adding " + uid);
        }
        return result;
    }

    protected void tryToSetInterfaceUpdateState(IInterface pointedInterface) throws Exception {
        objectUpdateState objectUpdateState = this.classBase.IObject().ObjectUpdateState();
        switch (objectUpdateState) {
            case created:
            case updated:
            case none:
            case terminated:
                pointedInterface.setInterfaceUpdateState(interfaceUpdateState.created);
                break;
            case deleted:

            case instantiated:
                break;
        }
    }

    public void add(IInterface objectInterface) {
        this.add(objectInterface, this.allowSorting);
    }

    @Override
    public IInterface addDynInterface(String interfaceDefinitionUID) {
        if (!StringUtils.isEmpty(interfaceDefinitionUID)) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                if (!this.mcolInterfaces.containsKey(interfaceDefinitionUID)) {
                    IInterface result = (IInterface) CIMContext.Instance.getSchemaActivator().newInstance(
                            "ccm.server.schema.interfaces.defaults." + interfaceDefinitionUID + "Default",
                            "ccm.server.schema.model.InterfaceDefault", new Object[]{false});
                    result.setDynamical(true);
                    result.setClass(this.classBase);
                    if (result instanceof InterfaceDefault)
                        ((InterfaceDefault) result).setInterfaceDefinitionUID(interfaceDefinitionUID);
                    this.mcolInterfaces.put(interfaceDefinitionUID, result);
                    if (this.classBase.IObject().ObjectUpdateState() == objectUpdateState.created)
                        result.setInterfaceUpdateState(interfaceUpdateState.created);
                    return result;
                } else
                    return this.mcolInterfaces.getOrDefault(interfaceDefinitionUID, null);
            } catch (Exception exception) {
                log.error("add dynamical interface with sort option failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
        return null;
    }

    @Override
    public void add(IInterface objectInterface, boolean sortOrNot) {
        if (objectInterface != null) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                if (!this.mcolInterfaces.containsKey(objectInterface.InterfaceDefinitionUID())) {
                    objectInterface.setClass(this.classBase);
                    this.mcolInterfaces.putIfAbsent(objectInterface.InterfaceDefinitionUID(), objectInterface);
                    if (objectInterface.InterfaceDefinitionUID().equalsIgnoreCase(interfaceDefinitionType.IObject.toString()))
                        this.baseInterface = objectInterface;
                }
            } catch (Exception exception) {
                log.error("add interface with sort option failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
            if (sortOrNot && this.mcolInterfaces.size() > 1) {
                this.sortInnerCollectionBySpecializationValue();
            }
        }
    }

    private void sortInnerCollectionBySpecializationValue() {
        if (this.mcolInterfaces.size() > 1) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                List<IInterface> interfaceList = new ArrayList<>(this.mcolInterfaces.values());
                InterfaceComparer interfaceComparer = new InterfaceComparer();
                interfaceList.sort(interfaceComparer);
                this.mcolInterfaces.clear();
                for (IInterface iInterface : interfaceList) {
                    this.mcolInterfaces.put(iInterface.InterfaceDefinitionUID(), iInterface);
                }
            } catch (Exception exception) {
                log.error("sort inner collection by interface sequence failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    @Override
    public IInterface get(String interfaceDefinitionUid) {
        if (!StringUtils.isEmpty(interfaceDefinitionUid)) {
            if (interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.IObject.toString()) && this.baseInterface != null)
                return this.baseInterface;
            return this.mcolInterfaces.getOrDefault(interfaceDefinitionUid, null);
        }
        return null;
    }

    @Override
    public String getPropertyValue(String interfaceDefinitionUid, String propertyDefinitionUid) {
        IProperty entityProperty = this.getProperty(interfaceDefinitionUid, propertyDefinitionUid);
        return ValueConversionUtility.toString(entityProperty);
    }

    @Override
    public IProperty getProperty(String interfaceDefinitionUid, String propertyDefinitionUid) {
        if (!StringUtils.isEmpty(interfaceDefinitionUid)) {
            IInterface entityInterface = this.get(interfaceDefinitionUid);
            if (entityInterface != null) {
                return entityInterface.Properties().get(propertyDefinitionUid);
            }
        }
        return null;
    }

    @Override
    public IProperty getProperty(String propertyDefinitionUID) {
        if (!StringUtils.isEmpty(propertyDefinitionUID)) {
            if (this.baseInterface != null && this.baseInterface.Properties().hasProperty(propertyDefinitionUID))
                return this.baseInterface.Properties().get(propertyDefinitionUID);
            Iterator<Map.Entry<String, IInterface>> entryIterator = this.GetEnumerator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, IInterface> interfaceEntry = entryIterator.next();
                if (interfaceEntry.getValue().hasProperty(propertyDefinitionUID)) {
                    return interfaceEntry.getValue().Properties().get(propertyDefinitionUID);
                }
            }
        }
        return null;
    }

    public IObjectCollection convertToIObjectCollection() throws Exception {
        IObjectCollection result = new ObjectCollection();
        Iterator<Map.Entry<String, IInterface>> entryIterator = this.GetEnumerator();
        while (entryIterator.hasNext()) {
            result.append(entryIterator.next().getValue().getInterfaceDefinition());
        }
        return result;
    }

    @Override
    public boolean hasInterface(String interfaceDefinitionUid) {
        if (!StringUtils.isEmpty(interfaceDefinitionUid)) {
            return this.mcolInterfaces.containsKey(interfaceDefinitionUid);
        }
        return false;
    }
}
