package ccm.server.schema.collections.impl;

import ccm.server.comparers.IObjectComparator;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.queryTypes;
import ccm.server.enums.relCollectionTypes;
import ccm.server.helper.HardCodeHelper;
import ccm.server.models.LiteObject;
import ccm.server.models.page.PageResult;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;
import ccm.server.util.CommonUtility;
import ccm.server.util.ReentrantLockUtility;
import ccm.server.utils.SchemaUtility;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Slf4j
public class ObjectCollection implements IObjectCollection {
    private Boolean sorted = false;
    private IObject parent;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ConcurrentHashMap<String, IObject> mcolInnerOBIDs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> mcolUIDToOBIDMapping = new ConcurrentHashMap<>();
    private IObjectCollection parentCollection;
    private Comparator<IObject> comparator;
    private final CopyOnWriteArrayList<IObject> innerSortList = new CopyOnWriteArrayList<>();
    private IRelCollection end1Relationships;
    private IRelCollection end2Relationships;

    public static IObjectCollection toObjectCollection(Set<IObject> pcolObjects) {
        IObjectCollection result = new ObjectCollection();
        if (CommonUtility.hasValue(pcolObjects)) {
            for (IObject object : pcolObjects) {
                result.append(object);
            }
        }
        return result;
    }

    public IObject item(String uid) {
        return this.item(uid, null);
    }

    public IObject item(String uid, String domainUID) {
        return this.item(uid, domainUID, null);
    }

    public IObject item(String uid, String domainUID, Date terminateDate) {
        if (!StringUtils.isEmpty(uid)) {
            IObject result = null;
            List<String> lcolOBIDs = this.mcolUIDToOBIDMapping.getOrDefault(uid, null);
            if (lcolOBIDs != null) {
                if (lcolOBIDs.size() == 1 && domainUID == null && terminateDate == null)
                    result = this.itemByOBID(lcolOBIDs.get(0));
                else {
                    for (String obid : lcolOBIDs) {
                        IObject current = this.itemByOBID(obid);
                        if (result == null) {
                            boolean flag = true;
                            if (!StringUtils.isEmpty(domainUID) && !domainUID.equalsIgnoreCase(current.DomainUID()))
                                flag = false;
                            if (flag && terminateDate != null && !current.TerminationDate().equals(terminateDate))
                                flag = false;
                            if (flag) {
                                result = current;
                            }
                        }
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public IObjectCollection getItemsByInterfaceDefUID(@NotNull String pstrInterfaceDefUID) {
        if (SchemaUtility.hasValue(this)) {
            IObjectCollection container = new ObjectCollection();
            Iterator<IObject> e = this.GetEnumerator();
            while (e.hasNext()) {
                IObject obj = e.next();
                if (obj.Interfaces().hasInterface(pstrInterfaceDefUID)) {
                    container.append(obj);
                }
            }
            return container;
        }
        return null;
    }

    @Override
    public IObject get(queryTypes queryTypes, Object value) {
        if (value != null && !StringUtils.isEmpty(value.toString())) {
            switch (queryTypes) {
                case name:
                    return this.mcolInnerOBIDs.values().stream().filter(c -> c.Name().equalsIgnoreCase(value.toString())).findFirst().orElse(null);
                case uid:
                    return this.item(value.toString());
                case obid:
                    return this.itemByOBID(value.toString());
                case classDefinitionUID:
                    return this.mcolInnerOBIDs.values().stream().filter(c -> c.ClassDefinitionUID().equalsIgnoreCase(value.toString())).findFirst().orElse(null);
            }
        }
        return null;
    }

    @Override
    public IObjectCollection Items(String classDefinitionUID) {
        if (!StringUtils.isEmpty(classDefinitionUID) && this.hasValue()) {
            IObjectCollection result = this.createCollection();
            List<IObject> objects = this.mcolInnerOBIDs.values().stream().filter(c -> c.ClassDefinitionUID().equalsIgnoreCase(classDefinitionUID)).collect(Collectors.toList());
            result.addRange(objects);
            return result;
        }
        return null;
    }

    @Override
    public IRelCollection GetEnd1Relationships() {
        if (this.end1Relationships == null) {
            this.end1Relationships = new RelCollection(relCollectionTypes.End1s);
            this.end1Relationships.setParentCollection(this);
        }
        return this.end1Relationships;
    }

    @Override
    public IRelCollection GetEnd2Relationships() {
        if (this.end2Relationships == null) {
            this.end2Relationships = new RelCollection(relCollectionTypes.End2s);
            this.end2Relationships.setParentCollection(this);
        }
        return this.end2Relationships;
    }

    @Override
    public IObjectCollection getByClassDefinitionUids(String... classDefinitionUids) {
        if (classDefinitionUids != null && classDefinitionUids.length > 0) {
            IObjectCollection result = new ObjectCollection();
            Iterator<IObject> iterator = this.GetEnumerator();
            while (iterator.hasNext()) {
                IObject next = iterator.next();
                if (Arrays.stream(classDefinitionUids).anyMatch(c -> c.equalsIgnoreCase(next.ClassDefinitionUID())))
                    result.addRangeUniquely(next);
            }
            return result;
        }
        return null;
    }

    @Override
    public IObject itemByOBID(String pstrOBID) {
        if (!StringUtils.isEmpty(pstrOBID)) {
            return this.mcolInnerOBIDs.getOrDefault(pstrOBID, null);
        }
        return null;
    }

    @Override
    public IObjectCollection itemsByOBIDs(List<String> pcolOBIDs) {
        if (CommonUtility.hasValue(pcolOBIDs)) {
            IObjectCollection lcolContainer = new ObjectCollection();
            pcolOBIDs.forEach(r -> {
                IObject lobj = this.mcolInnerOBIDs.getOrDefault(r, null);
                if (lobj != null) {
                    lcolContainer.addRangeUniquely(lobj);
                }
            });
            return lcolContainer;
        }
        return null;
    }


    @Override
    public void addRange(IObjectCollection collection) {
        if (collection != null && collection.hasValue()) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                Iterator<IObject> e = collection.GetEnumerator();
                while (e.hasNext()) {
                    IObject iObject = e.next();
                    if (iObject != null) {
                        String obid = iObject.OBID();
                        String uid = iObject.UID();
                        if (!this.mcolInnerOBIDs.containsKey(obid))
                            onAdd(iObject, obid, uid);
                    }
                }
            } catch (Exception exception) {
                log.error("add range with collection failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }

        }
    }

    @Override
    public void addRange(Collection<IObject> collection) {
        if (CommonUtility.hasValue(collection)) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                for (IObject iObject : collection) {
                    if (iObject != null) {
                        this.onRemove(iObject);
                        this.onAdd(iObject, iObject.OBID(), iObject.UID());
                    }
                }
            } catch (Exception exception) {
                log.error("add range with collection failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    @Override
    public void addRangeUniquely(IObjectCollection collection) {
        if (collection != null && collection.hasValue()) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                Iterator<IObject> e = collection.GetEnumerator();
                while (e.hasNext()) {
                    IObject iObject = e.next();
                    this.onRemove(iObject);
                    this.onAdd(iObject, iObject.OBID(), iObject.UID());
                }
            } catch (Exception exception) {
                log.error("add range uniquely with collection", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    @Override
    public void addRangeUniquely(IObject o) {
        if (o != null)
            this.addRangeUniquely(o.toIObjectCollection());
    }

    @Override
    public IObjectCollection addItems(IObjectCollection value) {
        if (value != null && value.hasValue()) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                Iterator<IObject> enumerator = value.GetEnumerator();
                while (enumerator.hasNext()) {
                    IObject object = enumerator.next();
                    if (!this.contains(object))
                        this.append(object);
                }
            } catch (Exception exception) {
                log.error("add items failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
        return this;
    }

    public void onAdd(IObject iObject, String obid, String uid) {
        this.mcolInnerOBIDs.putIfAbsent(obid, iObject);
        if (this.Sorted())
            this.innerSortList.add(iObject);
        if (!StringUtils.isEmpty(uid)) {
            List<String> lcolOBIDs = this.mcolUIDToOBIDMapping.getOrDefault(uid, new ArrayList<>());
            if (!lcolOBIDs.contains(obid))
                lcolOBIDs.add(obid);
            else
                log.trace("duplicate mapping from " + obid + " to " + uid);
            if (this.mcolUIDToOBIDMapping.containsKey(uid))
                this.mcolUIDToOBIDMapping.replace(uid, lcolOBIDs);
            else
                this.mcolUIDToOBIDMapping.put(uid, lcolOBIDs);
        }
    }

    @Override
    public boolean contains(IObject o) {
        if (o != null) {
            return this.containsByOBID(o.OBID());
        }
        return false;
    }

    @Override
    public boolean containsByOBID(String pstrOBID) {
        if (!StringUtils.isEmpty(pstrOBID))
            return this.mcolInnerOBIDs.containsKey(pstrOBID);
        return false;
    }

    @Override
    public boolean containsByUID(String pstrUID) {
        if (!StringUtils.isEmpty(pstrUID)) {
            return this.mcolUIDToOBIDMapping.containsKey(pstrUID);
        }
        return false;
    }

    @Override
    public boolean contains(String uid) {
        return this.item(uid) != null;
    }

    @Override
    public boolean contains(String uid, String domainUID) {
        return this.item(uid, domainUID) != null;
    }

    @Override
    public boolean contains(String uid, String domainUID, Date terminationDate) {
        return this.item(uid, domainUID, terminationDate) != null;
    }

    @Override
    public boolean validate() throws Exception {
        if (this.Sorted()) {
            for (IObject iObject : this.innerSortList) {
                if (!iObject.Validate())
                    return false;
            }
        } else {
            for (IObject value : this.mcolInnerOBIDs.values()) {
                if (!value.Validate())
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasValue() {
        return this.Size() > 0;
    }

    @Override
    public int Size() {
        return this.mcolInnerOBIDs.size();
    }

    @Override
    public IObject get(int index) {
        if (index >= 0)
            return new ArrayList<>(this.mcolInnerOBIDs.values()).get(index);
        return null;
    }

    @Override
    public IObject Parent() {
        return this.parent;
    }

    @Override
    public void Commit() {
        for (Map.Entry<String, IObject> entry : this.mcolInnerOBIDs.entrySet()) {
            entry.getValue().commit();
        }
    }

    @Override
    public void setParent(IObject o) {
        this.parent = o;
    }

    @Override
    public IObjectCollection ParentCollection() {
        return this.parentCollection;
    }

    @Override
    public void setParentCollection(IObjectCollection parentCollection) {
        this.parentCollection = parentCollection;
    }

    @Override
    public boolean IsParentACollection() {
        return this.parentCollection != null;
    }

    @Override
    public IObjectCollection copy() {
        IObjectCollection collection = this.createCollection();
        Iterator<IObject> iObjectIterator = this.GetEnumerator();
        while (iObjectIterator.hasNext()) {
            IObject next = iObjectIterator.next();
            collection.append(next);
        }
        return collection;
    }

    private final PageResult<IObject> pageResult = new PageResult<>();

    @Override
    public ObjectDTOCollection toObjectDTOCollection() throws Exception {
        return new ObjectDTOCollection(this.toObjectDTOs());
    }

    @Override
    public void retrievePageResultInformation(PageResult<LiteObject> pageResult) {
        if (pageResult != null) {
            this.pageResult.setCurrent(pageResult.getCurrent());
            this.pageResult.setSize(pageResult.getSize());
            this.pageResult.setTotal(pageResult.getTotal());
        }
    }

    @Override
    public PageResult<IObject> PageResult() {
        return this.pageResult;
    }

    @Override
    public void sortByObidList(List<String> obids) {
        if (obids != null && obids.size() > 0) {
            this.sorted = true;
            this.innerSortList.clear();
            for (String obid : obids) {
                IObject iObject = this.itemByOBID(obid);
                this.innerSortList.add(iObject);
            }
        }
    }

    @Override
    public Comparator<IObject> Comparator() {
        return this.comparator;
    }

    @Override
    public <T> List<T> toList(Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (clazz != null) {
            Iterator<IObject> objectIterator = this.GetEnumerator();
            while (objectIterator.hasNext()) {
                T t = objectIterator.next().toInterface(clazz);
                if (t != null)
                    result.add(t);
            }
        }
        return result;
    }

    @Override
    public boolean Sorted() {
        return this.sorted;
    }

    @Override
    public void Delete() throws Exception {
        Iterator<IObject> iObjectIterator = this.GetEnumerator();
        while (iObjectIterator.hasNext()) {
            IObject iObject = iObjectIterator.next();
            iObject.Delete();
        }
    }

    @Override
    public Map<String, IObjectCollection> mapByClassDefOrRelDef() {
        Map<String, IObjectCollection> mapResult = new HashMap<>();
        Iterator<IObject> iObjectIterator = this.GetEnumerator();
        while (iObjectIterator.hasNext()) {
            IObject current = iObjectIterator.next();
            String definitionUID = current.ClassDefinitionUID();
            if (definitionUID.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                try {
                    definitionUID = current.toInterface(IRel.class).RelDefUID();
                } catch (Exception e) {
                    log.error("get object's relDefUID failed", e);
                }
            }
            IObjectCollection resultOrDefault = mapResult.getOrDefault(definitionUID, new ObjectCollection());
            resultOrDefault.append(current);
            if (mapResult.containsKey(definitionUID))
                mapResult.replace(definitionUID, resultOrDefault);
            else
                mapResult.put(definitionUID, resultOrDefault);
        }
        return mapResult;
    }

    @Override
    public IObjectCollection excludeFrom(IObjectCollection items) {
        if (items != null && items.size() > 0) {
            IObjectCollection result = new ObjectCollection();
            Iterator<IObject> objectIterator = this.GetEnumerator();
            while (objectIterator.hasNext()) {
                IObject current = objectIterator.next();
                if (!items.contains(current))
                    result.append(current);
            }
            return result;
        }
        return this;
    }


    protected void onReplace(IObject object) {
        if (object != null) {
            this.onRemove(object);
            this.onAdd(object, object.OBID(), object.UID());
        }
    }

    @Override
    public void replace(IObject object) {
        if (object != null) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                this.onReplace(object);
            } catch (Exception exception) {
                log.error("replace failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    @Override
    public ReentrantReadWriteLock Lock() {
        return this.lock;
    }

    @Override
    public int size() {
        return this.mcolInnerOBIDs.size();
    }

    @Override
    public List<IObject> toList() {
        List<IObject> result = new ArrayList<>();
        for (Map.Entry<String, IObject> objectEntry : this.mcolInnerOBIDs.entrySet()) {
            result.add(objectEntry.getValue());
        }
        return result;
    }


    @Override
    public void setSorted(boolean sorted) {
        if (sorted)
            this.onSort();
        this.sorted = sorted;
    }

    protected void onSorting() {
        if (!this.sorted) {
            this.innerSortList.clear();
            this.innerSortList.addAll(new ArrayList<>(this.mcolInnerOBIDs.values()));
        }
        this.innerSortList.sort(this.comparator);
        this.sorted = true;
    }

    protected void onSort() {
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            this.onSorting();
        } catch (Exception exception) {
            log.error("on sort failed", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
    }

    public void append(IObject iObject) {
        if (iObject != null) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                this.onAdd(iObject, iObject.OBID(), iObject.UID());
            } catch (Exception exception) {
                log.error("add with object failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    @Override
    public IObject firstOrDefault() {
        IObject result = null;
        if (this.hasValue())
            result = this.get(0);
        return result;
    }

    protected void onClear() {
        this.mcolInnerOBIDs.clear();
        this.mcolUIDToOBIDMapping.clear();
        this.innerSortList.clear();
        this.setSorted(false);
    }

    @Override
    public void clear() {
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            this.onClear();
        } catch (Exception exception) {
            log.error("clear failed", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }

    }

    @Override
    public void remove(String obid) {
        this.remove(this.itemByOBID(obid));
    }

    protected void onRemove(IObject iObject) {
        if (iObject != null) {
            this.mcolInnerOBIDs.remove(iObject.OBID());
            if (!StringUtils.isEmpty(iObject.UID())) {
                List<String> lcolOBIDs = this.mcolUIDToOBIDMapping.getOrDefault(iObject.UID(), null);
                if (CommonUtility.hasValue(lcolOBIDs)) {
                    lcolOBIDs.remove(iObject.OBID());
                    if (lcolOBIDs.size() == 0)
                        this.mcolUIDToOBIDMapping.remove(iObject.UID());
                }
            }
            if (this.Sorted()) {
                this.innerSortList.stream().filter(c -> c.OBID().equalsIgnoreCase(iObject.OBID())).findAny().ifPresent(this.innerSortList::remove);
            }
        }
    }

    @Override
    public void remove(IObject iObject) {
        if (iObject != null) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                this.onRemove(iObject);
            } catch (Exception exception) {
                log.error("remove object failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }

        }
    }

    @Override
    public IObjectCollection createCollection() {
        return new ObjectCollection();
    }

    @Override
    public void commit() throws Exception {
        if (!CIMContext.Instance.Transaction().inTransaction())
            CIMContext.Instance.Transaction().start();
        CIMContext.Instance.Transaction().addRange(this);
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public List<ObjectDTO> toObjectDTOs() throws Exception {
        List<ObjectDTO> result = new ArrayList<>();
        for (Map.Entry<String, IObject> objectEntry : this.mcolInnerOBIDs.entrySet()) {
            ObjectDTO objectDTO = objectEntry.getValue().toObjectDTO();
            result.add(objectDTO);
        }
        return result;
    }

    @Override
    public List<String> listOfOBID() {
        if (this.hasValue()) {
            List<String> result = new ArrayList<>();
            for (Map.Entry<String, List<String>> listEntry : this.mcolUIDToOBIDMapping.entrySet()) {
                result.addAll(listEntry.getValue());
            }
            return result.stream().distinct().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public Iterator<IObject> GetEnumerator() {
        Iterator<IObject> e = null;
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            if (this.Sorted())
                e = this.innerSortList.iterator();
            else
                e = new ArrayList<>(this.mcolInnerOBIDs.values()).iterator();
        } catch (Exception exception) {
            log.error("get enumerator failed under object collection", exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
        return e;
    }

    @Override
    public void sort() {
        this.sort(interfaceDefinitionType.IObject.toString(), "Name");
    }

    @Override
    public void sort(String interfaceDefUID, String propertyDefUID) {
        this.comparator = new IObjectComparator(interfaceDefUID, propertyDefUID);
        this.onSort();
    }

    @Override
    public void sorting(Comparator<IObject> comparator) {
        this.comparator = comparator;
        this.onSort();
    }
}
