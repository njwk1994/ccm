package ccm.server.schema.collections.impl;

import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.enums.*;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;
import ccm.server.schema.interfaces.IRelDef;
import ccm.server.schema.model.relEndObj;
import ccm.server.util.CommonUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class RelCollection extends ObjectCollection implements IRelCollection {
    //    private final ConcurrentHashMap<String, IRel> relsByUniqueKey = new ConcurrentHashMap<>();
    private relCollectionTypes menumRelCollectionTypes = relCollectionTypes.Unknown;

    public RelCollection(relCollectionTypes relCollectionTypes) {
        this.menumRelCollectionTypes = relCollectionTypes;
    }

    public relCollectionTypes RelCollectionType() {
        return this.menumRelCollectionTypes;
    }

    @Override
    public void add(IRel rel) {
        super.append(rel);
    }

    @Override
    public void replace(IRel rel) throws Exception {
        super.replace(rel);
    }

    @Override
    public void addRange(IRelCollection relCollection) {
        super.addRange(relCollection);
    }

    @Override
    public Map<relEndObj, IRel> toMapByUIDAndOBID(relCollectionTypes relCollectionTypes) throws Exception {
        Map<relEndObj, IRel> result = new HashMap<>();
        if (relCollectionTypes == null)
            relCollectionTypes = ccm.server.enums.relCollectionTypes.End1s;
        Iterator<IObject> iObjectIterator = this.GetEnumerator();
        while (iObjectIterator.hasNext()) {
            IRel rel = iObjectIterator.next().toInterface(IRel.class);
            result.put(new relEndObj(rel, relCollectionTypes), rel);
        }
        return result;
    }

    @Override
    public IObjectCollection createCollection() {
        return new RelCollection(this.menumRelCollectionTypes);
    }

    @Override
    public List<ObjectDTO> toObjectDTOs() throws Exception {
        return super.toObjectDTOs();
    }

    @Override
    public void addRangeUniquely(IRelCollection relCollection) {
        super.addRangeUniquely(relCollection);
    }

    @Override
    public IObjectCollection GetEnd1s() throws Exception {
        return this.GetEnd1s(super.createCollection());
    }

    @Override
    public IObjectCollection GetEnd1s(IObjectCollection targetCollection) throws Exception {
        return this.GetEnd1s(targetCollection, null);
    }

    private List<relEndObj> getRemainingRelObjs(List<relEndObj> relEndObjList, IObjectCollection targetCollection) {
        List<relEndObj> result = new ArrayList<>();
        if (targetCollection != null && targetCollection.size() > 0) {
            if (relEndObjList != null && relEndObjList.size() > 0) {
                for (relEndObj relEndObj : relEndObjList) {
                    if (targetCollection.containsByOBID(relEndObj.getObid())) {
                        continue;
                    }
                    result.add(relEndObj);
                }
            }
        } else
            result.addAll(relEndObjList);
        return result;
    }

    private void doGetEnds(IObjectCollection targetCollection, List<relEndObj> relEndObjs, String configurationUid) throws Exception {
        int requiredSize = this.size();
        IObjectCollection objectCollection = null;
        this.getEndsFromCache(relEndObjs, targetCollection);
        if (targetCollection.size() != requiredSize) {
            List<relEndObj> remainingRelObjs = this.getRemainingRelObjs(relEndObjs, targetCollection);
            log.trace("get end(s) of relationship from db from " + relEndObjs.size() + " into " + remainingRelObjs.size());
            List<String> classDefs = remainingRelObjs.stream().map(relEndObj::getClassDefinitionUid).distinct().collect(Collectors.toList());
            objectCollection = CIMContext.Instance.QueryEngine().getByOBIDAndClassDef(remainingRelObjs.stream().map(relEndObj::getObid).distinct().collect(Collectors.toList()), classDefs, configurationUid);
        }
        if (objectCollection != null && objectCollection.size() > 0) {
            targetCollection.addRangeUniquely(objectCollection);
            CIMContext.Instance.ProcessCache().refresh(objectCollection);
        }
    }

    private void setEnds(IObjectCollection targetCollection, relCollectionTypes collectionTypes) throws Exception {
        if (targetCollection != null && targetCollection.size() > 0) {
            Iterator<IObject> r = this.GetEnumerator();
            while (r.hasNext()) {
                IRel rel = r.next().toInterface(IRel.class);
                String uid = "";
                String obid = "";
                String domainUID = "";
                String classDefinitionUID = "";
                String name = "";
                IObject end = null;
                switch (collectionTypes) {
                    case End2s:
                        uid = rel.UID2();
                        domainUID = rel.DomainUID2();
                        obid = rel.OBID2();
                        classDefinitionUID = rel.ClassDefinitionUID2();
                        name = rel.Name2();
                        rel.setEnd2(obid, uid, domainUID, classDefinitionUID, name);
                        end = targetCollection.itemByOBID(obid);
                        if (end != null) {
                            end.GetEnd2Relationships().replace(rel);
                        }
                        break;
                    case End1s:
                        uid = rel.UID1();
                        domainUID = rel.DomainUID1();
                        obid = rel.OBID1();
                        classDefinitionUID = rel.ClassDefinitionUID1();
                        name = rel.Name1();
                        rel.setEnd1(obid, uid, domainUID, classDefinitionUID, name);
                        end = targetCollection.itemByOBID(obid);
                        if (end != null) {
                            end.GetEnd1Relationships().replace(rel);
                        }
                        break;
                }
            }
        }
    }

    @Override
    public IObjectCollection GetEnd2s() throws Exception {
        return this.GetEnd2s(super.createCollection());
    }

    protected void getEndsFromCacheWithUID(List<Map.Entry<String, String>> uidAndClassDefs, IObjectCollection container) throws Exception {
        if (CommonUtility.hasValue(uidAndClassDefs)) {
            for (Map.Entry<String, String> uidAndClassDef : uidAndClassDefs) {
                IObject object = CIMContext.Instance.ProcessCache().getObjectByClassDefCache(uidAndClassDef.getKey(), uidAndClassDef.getValue());
                if (object != null)
                    container.append(object);
            }
        }
    }

    protected void getEndsFromCache(List<relEndObj> objs, IObjectCollection container) {
        if (CommonUtility.hasValue(objs)) {
            for (relEndObj obj : objs) {
                IObject object = CIMContext.Instance.ProcessCache().getObjectByOBIDCache(obj.getObid());
                if (object != null && object.fromDb())
                    container.append(object);
            }
        }
    }

    @Override
    public IObjectCollection GetEnd2s(IObjectCollection targetCollection) throws Exception {
        return this.GetEnd2s(targetCollection, null);
    }

    @Override
    public IObjectCollection GetEnd1s(String configurationUid) throws Exception {
        return this.GetEnd1s(super.createCollection(), configurationUid);
    }

    @Override
    public IObjectCollection GetEnd1s(IObjectCollection targetCollection, String configurationUid) throws Exception {
        Iterator<IObject> e = this.GetEnumerator();
        List<relEndObj> relEndObjs = new ArrayList<>();
        while (e.hasNext()) {
            IRel rel = e.next().toInterface(IRel.class);
            relEndObjs.add(new relEndObj(rel, relCollectionTypes.End1s));
        }
        this.doGetEnds(targetCollection, relEndObjs, configurationUid);
        this.setEnds(targetCollection, relCollectionTypes.End1s);
        return targetCollection;
    }

    @Override
    public IObjectCollection GetEnd2s(String configurationUid) throws Exception {
        return this.GetEnd2s(super.createCollection(), configurationUid);
    }

    @Override
    public IObjectCollection GetEnd2s(IObjectCollection targetCollection, String configurationUid) throws Exception {
        Iterator<IObject> enumerator = this.GetEnumerator();
        List<relEndObj> relEndObjs = new ArrayList<>();
        while (enumerator.hasNext()) {
            IRel rel = enumerator.next().toInterface(IRel.class);
            relEndObjs.add(new relEndObj(rel, relCollectionTypes.End2s));
        }
        this.doGetEnds(targetCollection, relEndObjs, configurationUid);
        this.setEnds(targetCollection, relCollectionTypes.End2s);
        return targetCollection;
    }

    @Override
    public IRel GetRel(String relDefUID, String configurationUid, boolean cacheOnly) throws Exception {
        IRel result = null;
        IRelCollection lcolRels = this.GetRels(relDefUID, configurationUid, cacheOnly);
        if (lcolRels != null && lcolRels.hasValue()) {
            result = (IRel) lcolRels.firstOrDefault().Interfaces().item(interfaceDefinitionType.IRel.toString());
        }
        return result;
    }

    @Override
    public IRel GetRel(String relDefUID, String configurationUid) throws Exception {
        return this.GetRel(relDefUID, configurationUid, false);
    }

    @Override
    public IRel GetRel(String relDefUID, String pstrEnd2UID, String configurationUid) throws Exception {
        IRel result = this.OnGetRel(relDefUID, pstrEnd2UID);
        if (result == null) {
            this.GetRels(relDefUID, configurationUid, false);
            result = this.OnGetRel(relDefUID, pstrEnd2UID);
        }
        return result;
    }

    private IRel OnGetRel(String relDefUID, String pstrEnd2UID) throws Exception {
        IRel result = null;
        if (!StringUtils.isEmpty(relDefUID) && !StringUtils.isEmpty(pstrEnd2UID)) {
            Iterator<IObject> enumerator = this.GetEnumerator();
            while (enumerator.hasNext()) {
                IObject iObject = enumerator.next();
                IRel rel = iObject.toInterface(IRel.class);
                if (rel.RelDefUID().equalsIgnoreCase(relDefUID) && rel.UID2().equalsIgnoreCase(pstrEnd2UID)) {
                    result = rel;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public IRelCollection GetRels(String relDefUID, String configurationUid) {
        return this.GetRels(relDefUID, configurationUid, false);
    }

    public boolean IsParentCollection() {
        return this.Parent() != null;
    }

    protected IRelCollection onGetRelsFromCache(String relDefUID) throws Exception {
        if (this.hasValue()) {
            IRelCollection currentResult = new RelCollection(this.menumRelCollectionTypes);
            Iterator<IObject> objectIterator = this.GetEnumerator();
            List<String> parentOBIDs = new ArrayList<>();
            if (this.IsParentACollection()) {
                IObjectCollection parentCollection = this.ParentCollection();
                if (parentCollection != null && parentCollection.hasValue())
                    parentOBIDs = parentCollection.listOfOBID();
            }

            while (objectIterator.hasNext()) {
                IObject current = objectIterator.next();
                IRel rel = current.toInterface(IRel.class);
                if (rel.RelDefUID().equalsIgnoreCase(relDefUID)) {
                    switch (this.menumRelCollectionTypes) {
                        case End1s:
                            if (this.IsParentCollection() && this.Parent().OBID().equalsIgnoreCase(rel.OBID1()))
                                currentResult.add(rel);
                            else if (this.IsParentACollection()) {
                                if (parentOBIDs.contains(rel.OBID1()))
                                    currentResult.add(rel);
                            }
                            break;
                        case End2s:
                            if (this.IsParentCollection() && this.Parent().OBID().equalsIgnoreCase(rel.OBID2()))
                                currentResult.add(rel);
                            else if (this.IsParentACollection()) {
                                if (parentOBIDs.contains(rel.OBID2()))
                                    currentResult.add(rel);
                            }
                            break;
                    }
                }
            }
            if (currentResult.hasValue())
                return currentResult;
        }
        return null;
    }

    @Override
    public IRelCollection GetRels(String relDefUID, String configurationUid, boolean cacheOnly) {
        IRelCollection result = null;
        if (!StringUtils.isEmpty(relDefUID)) {
            try {
                IObject relDef = CIMContext.Instance.ProcessCache().item(relDefUID, null, false);
                if (relDef == null) {
                    log.error("invalid relationship definition as it is not exist in system ******" + relDefUID + "******");
                    return null;
                }


                IRelDef relDefinition = relDef.toInterface(IRelDef.class);
                if (cacheOnly) {
                    IRelCollection relCollection = this.onGetRelsFromCache(relDefUID);
                    if (relCollection != null && relCollection.hasValue())
                        return relCollection;
                    return null;
                }

                if (this.IsParentACollection()) {
                    IObjectCollection queryItems = new ObjectCollection();
                    Iterator<IObject> iterator = this.ParentCollection().GetEnumerator();
                    while (iterator.hasNext()) {
                        queryItems.append(iterator.next());
                    }
                    if (queryItems.hasValue())
                        result = this.getRelsForCollection(relDefinition, queryItems, configurationUid);
                } else
                    result = this.getRelsForObject(relDefinition, this.Parent(), configurationUid);
            } catch (Exception exception) {
                log.error(exception.getMessage(), exception);
            } finally {
                if (result == null)
                    result = new RelCollection(this.menumRelCollectionTypes);
            }
        }
        return result;
    }

    @Override
    public IRel GetRel(String relDefUID, boolean cacheOnly) throws Exception {
        return this.GetRel(relDefUID, null, cacheOnly);
    }

    @Override
    public IRel GetRel(String relDefUID) throws Exception {
        return this.GetRel(relDefUID, null);
    }

    @Override
    public IRelCollection GetRels(String relDefUID) {
        return this.GetRels(relDefUID, null);
    }

    @Override
    public IRelCollection GetRels(String relDefUID, boolean cacheOnly) {
        return this.GetRels(relDefUID, null, cacheOnly);
    }

    @Override
    public void remove(IRel rel) throws Exception {
        super.remove(rel);
    }

    private IObjectCollection removeObjectsCannotHaveTheRel(IObjectCollection lcolQueryItems, IRelDef relDef) {
        return lcolQueryItems;
    }

    protected IRelCollection getRelsForCollection(IRelDef relDef, IObjectCollection pcolItems, String configurationUid) throws Exception {
        if (relDef != null && pcolItems != null && pcolItems.hasValue()) {
            IObjectCollection queryItems = new ObjectCollection();
            Iterator<IObject> e = pcolItems.GetEnumerator();
            while (e.hasNext()) {
                IObject currentObject = e.next();
                if (currentObject.ObjectUpdateState() != objectUpdateState.created && currentObject.ObjectUpdateState() != objectUpdateState.instantiated)
                    queryItems.append(currentObject);
            }
//            lcolQueryItems = this.removeObjectsCannotHaveTheRel(lcolQueryItems, relDef);
            String classDefinitionUIDOfRelDef = relDef.ClassDefinitionUID();
            if (classDefinitionUIDOfRelDef.equalsIgnoreCase(classDefinitionType.SpecialRelDef.toString()))
                return new RelCollection(this.menumRelCollectionTypes);
            if (classDefinitionUIDOfRelDef.equalsIgnoreCase(classDefinitionType.RelDef.toString())) {
                IRelCollection relCollection = CIMContext.Instance.QueryEngine().expandRelationship(queryItems, relCollectionTypes.toRelDirection(this.menumRelCollectionTypes), relDef, configurationUid);
                if (relCollection != null && relCollection.hasValue())
                    this.addRange(relCollection);
                return relCollection;
            }
            if (classDefinitionUIDOfRelDef.equalsIgnoreCase(classDefinitionType.EdgeDef.toString()))
                throw new Exception("not implemented");
        }
        return null;
    }

    protected IRelCollection getRelsForObject(IRelDef relDef, IObject pobjObject, String configurationUid) throws Exception {
        if (relDef != null && pobjObject != null) {
            String classDefinitionUIDOfRelDef = relDef.ClassDefinitionUID();
            if (classDefinitionUIDOfRelDef.equalsIgnoreCase(classDefinitionType.SpecialRelDef.toString()))
                return new RelCollection(this.RelCollectionType());
            if (classDefinitionUIDOfRelDef.equalsIgnoreCase(classDefinitionType.RelDef.toString())) {
                if (pobjObject.ObjectUpdateState() != objectUpdateState.created && pobjObject.ObjectUpdateState() != objectUpdateState.instantiated) {
                    IRelCollection relCollection = CIMContext.Instance.QueryEngine().expandRelationship(pobjObject.toIObjectCollection(), relCollectionTypes.toRelDirection(this.menumRelCollectionTypes), relDef, configurationUid);
                    if (relCollection != null && relCollection.hasValue())
                        this.addRange(relCollection);
                    return relCollection;
                }
            }
            if (classDefinitionUIDOfRelDef.equalsIgnoreCase(classDefinitionType.EdgeDef.toString()))
                throw new Exception("not implement");
        }
        return null;
    }

    @Override
    public boolean containsUid1(String uid1) throws Exception {
        if (this.size() > 0 && !StringUtils.isEmpty(uid1)) {
            Iterator<IObject> iterator = this.GetEnumerator();
            while (iterator.hasNext()) {
                IRel relObj = iterator.next().toInterface(IRel.class);
                if (uid1.equals(relObj.UID1())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsUid2(String uid2) throws Exception {
        if (this.size() > 0 && !StringUtils.isEmpty(uid2)) {
            Iterator<IObject> iterator = this.GetEnumerator();
            while (iterator.hasNext()) {
                IRel relObj = iterator.next().toInterface(IRel.class);
                if (uid2.equals(relObj.UID2())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsOBID1(String OBID1) throws Exception {
        if (this.size() > 0 && !StringUtils.isEmpty(OBID1)) {
            Iterator<IObject> iterator = this.GetEnumerator();
            while (iterator.hasNext()) {
                IRel relObj = iterator.next().toInterface(IRel.class);
                if (OBID1.equals(relObj.OBID1())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsOBID2(String OBID2) throws Exception {
        if (this.size() > 0 && !StringUtils.isEmpty(OBID2)) {
            Iterator<IObject> iterator = this.GetEnumerator();
            while (iterator.hasNext()) {
                IRel relObj = iterator.next().toInterface(IRel.class);
                if (OBID2.equals(relObj.OBID2())) {
                    return true;
                }
            }
        }
        return false;
    }
}
