package ccm.server.engine.impl;

import ccm.server.context.CIMContext;
import ccm.server.context.DBContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.*;
import ccm.server.models.LiteObject;
import ccm.server.models.page.PageResult;
import ccm.server.models.query.QueryCriteria;
import ccm.server.models.query.QueryRequest;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.collections.impl.RelCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import ccm.server.utils.IObjectConversion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service("queryEngine")
@Slf4j
public class QueryEngine implements IQueryEngine {
    //    protected static final ThreadLocal<QueryRequest> queryRequestThreadLocal = new ThreadLocal<>();
//    protected static final ThreadLocal<IObjectConversion> objectConversionThreadLocal = new ThreadLocal<>();
    private final IObjectConversion objectConversion = new IObjectConversion();
    @Autowired
    private DBContext dbContext;

    private QueryRequest onStart() {
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setQueryForRelationship(false);
        queryRequest.getScopeWisedDomains().addAll(CIMContext.Instance.ProcessCache().getScopeWisedDomains());
        return queryRequest;
    }

    @Override
    public QueryRequest start() {
        QueryRequest queryRequest = this.onStart();
        try {
            queryRequest.setScopePrefix(CIMContext.Instance.getMyConfigurationItemTablePrefix());
        } catch (Exception e) {
            log.trace(e.getMessage());
        }
        return queryRequest;
    }

    @Override
    public QueryRequest start(String configurationUid) {
        QueryRequest queryRequest = this.onStart();
        IObject item = null;
        try {
            item = CIMContext.Instance.ProcessCache().item(configurationUid, null, false);
            if (item != null) {
                ICIMConfigurationItem configurationItem = item.toInterface(ICIMConfigurationItem.class);
                queryRequest.setScopePrefix(configurationItem.TablePrefix());
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return queryRequest;
    }

    @Override
    public void changeJoinMode(QueryRequest queryRequest, joinMode joinMode) {
        if (queryRequest != null) queryRequest.setJoinMode(joinMode);
    }

    @Override
    public void setOrderBy(QueryRequest queryRequest, orderMode orderMode, String definition) {
        if (queryRequest != null) queryRequest.addOrderBy(orderMode, definition);
    }

    @Override
    public void setOrderBys(QueryRequest queryRequest, Map<String, orderMode> orders) {
        if (orders != null && orders.size() > 0) {
            if (queryRequest != null) {
                for (Map.Entry<String, orderMode> modeEntry : orders.entrySet()) {
                    queryRequest.addOrderBy(modeEntry.getValue(), modeEntry.getKey());
                }
            }
        }
    }

    @Override
    public void addDomain(QueryRequest queryRequest, String domain) {
        if (queryRequest != null && !StringUtils.isEmpty(domain)) queryRequest.getDomains().add(domain);
    }

    @Override
    public void addDomains(QueryRequest queryRequest, Collection<String> domains) {
        if (queryRequest != null && CommonUtility.hasValue(domains)) queryRequest.getDomains().addAll(domains);
    }


    @Override
    public IObjectConversion getIObjectConversion() {
        return this.objectConversion;
    }

    @Override
    public void setPageRequest(QueryRequest queryRequest, PageRequest pageRequest) {
        if (queryRequest != null && pageRequest != null) queryRequest.setPageRequest(pageRequest);
    }

    @Override
    public IObjectCollection query(QueryRequest queryRequest) {
        IObjectCollection result = null;
        try {
            if (queryRequest != null) {
                CIMContext.Instance.SwitchEngine().setDomainScopeForQueryRequest(queryRequest);
                PageResult<LiteObject> liteObjects = this.dbContext.onQuery(queryRequest);
                if (liteObjects != null) {
                    StopWatch stopWatch = PerformanceUtility.start();
                    IObjectCollection objectCollection = this.getIObjectConversion().convertThread(liteObjects.getResultList());
                    objectCollection.sortByObidList(liteObjects.getResultList().stream().map(LiteObject::getOBID).distinct().collect(Collectors.toList()));
                    objectCollection.retrievePageResultInformation(liteObjects);
                    result = objectCollection;
                    CIMContext.Instance.ProcessCache().refresh(objectCollection);
                    log.trace("finish to conversion after querying and refresh cache" + PerformanceUtility.stop(stopWatch));
                }
            }
        } catch (Exception exception) {
            log.error("do query failed", exception);
        } finally {
            queryRequest = null;
        }
        if (result == null) result = new ObjectCollection();
        return result;
    }

    @Override
    public IObject queryOne(QueryRequest queryRequest) {
        IObject result = null;
        try {
            if (queryRequest != null) {
                queryRequest.setQueryOne(true);
                CIMContext.Instance.SwitchEngine().setDomainScopeForQueryRequest(queryRequest);
                PageResult<LiteObject> liteObjects = this.dbContext.onQuery(queryRequest);
                IObjectConversion objectConversion = this.getIObjectConversion();
                if (liteObjects != null && liteObjects.hasValue()) {
                    StopWatch stopWatch = PerformanceUtility.start();
                    result = objectConversion.convert(liteObjects.getResultList().get(0));
                    CIMContext.Instance.ProcessCache().refresh(result);
                    log.trace("finish to conversion after querying and refresh cache" + PerformanceUtility.stop(stopWatch));
                }
            }
        } catch (Exception exception) {
            log.error("do query one failed", exception);
        } finally {
            queryRequest = null;
        }
        return result;
    }

    @Override
    public void setQueryForRelationship(QueryRequest queryRequest, boolean queryForRelationship) {
        if (queryRequest != null) queryRequest.setQueryForRelationship(queryForRelationship);
    }

    @Override
    public IObjectCollection getByOBIDAndClassDef(List<String> obids, List<String> classDefs, String configurationUid) throws Exception {
        if (CommonUtility.hasValue(obids)) {
            QueryRequest queryRequest = this.start();
            this.addOBIDForQuery(queryRequest, operator.in, obids.stream().distinct().collect(Collectors.joining(",")));
            if (CommonUtility.hasValue(classDefs))
                this.addClassDefForQuery(queryRequest, classDefs.stream().distinct().collect(Collectors.joining(",")));
            if (!StringUtils.isEmpty(configurationUid))
                queryRequest.setScopePrefix(CIMContext.Instance.getConfigTablePrefix(configurationUid));
            else
                queryRequest.setScopePrefix(CIMContext.Instance.getMyConfigurationItemTablePrefix());
            return this.query(queryRequest);
        }
        return null;
    }

    @Override
    public IObject getObjectByUIDAndClassDefinitionUID(String uid, String classDefinitionUID) {
        if (!StringUtils.isEmpty(uid)) {
            QueryRequest queryRequest = this.start();
            this.addUIDForQuery(queryRequest, null, uid);
            if (!StringUtils.isEmpty(classDefinitionUID)) this.addClassDefForQuery(queryRequest, classDefinitionUID);
            return this.queryOne(queryRequest);
        }
        return null;
    }

    @Override
    public IObject getObjectByUIDAndClassDefinitionUID(String uid, String classDefinitionUID, IObject configurationItem) {
        if (!StringUtils.isEmpty(uid)) {
            QueryRequest queryRequest = this.start();
            if (configurationItem != null)
                queryRequest.setScopePrefix(configurationItem.toInterface(ICIMConfigurationItem.class).TablePrefix());
            this.addUIDForQuery(queryRequest, null, uid);
            if (!StringUtils.isEmpty(classDefinitionUID)) this.addClassDefForQuery(queryRequest, classDefinitionUID);
            return this.queryOne(queryRequest);
        }
        return null;
    }

    @Override
    public IObjectCollection getObjectByUIDsAndInterfaceDefinitionUID(List<String> uids, String interfaceDefinitionUID) {
        if (CommonUtility.hasValue(uids) && !StringUtils.isEmpty(interfaceDefinitionUID)) {
            QueryRequest queryRequest = this.start();
            this.addUIDForQuery(queryRequest, operator.in, String.join(",", uids));
            this.addInterfaceDefsForQuery(queryRequest, interfaceDefinitionUID);
            return this.query(queryRequest);
        }
        return null;
    }

    @Override
    public IObjectCollection getObjectByOBIDsAndInterfaceDefinitionUID(List<String> obids, String interfaceDefinitionUID) {
        if (CommonUtility.hasValue(obids) && !StringUtils.isEmpty(interfaceDefinitionUID)) {
            QueryRequest queryRequest = this.start();
            this.addOBIDForQuery(queryRequest, operator.in, String.join(",", obids));
            this.addInterfaceDefsForQuery(queryRequest, interfaceDefinitionUID);
            return this.query(queryRequest);
        }
        return null;
    }

    @Override
    public IObject getObjectByOBIDAndClassDefinitionUID(String obid, String classDefinitionUID) {
        if (!StringUtils.isEmpty(obid)) {
            QueryRequest queryRequest = this.start();
            this.addOBIDForQuery(queryRequest, null, obid);
            if (!StringUtils.isEmpty(classDefinitionUID)) this.addClassDefForQuery(queryRequest, classDefinitionUID);
            return this.queryOne(queryRequest);
        }
        return null;
    }

    @Override
    public IObject getObjectByOBIDAndClassDefinitionUID(String obid, String classDefinitionUID, IObject configurationItem) {
        if (!StringUtils.isEmpty(obid)) {
            QueryRequest queryRequest = this.start();
            if (configurationItem != null)
                queryRequest.setScopePrefix(configurationItem.toInterface(ICIMConfigurationItem.class).TablePrefix());
            this.addOBIDForQuery(queryRequest, null, obid);
            if (!StringUtils.isEmpty(classDefinitionUID)) this.addClassDefForQuery(queryRequest, classDefinitionUID);
            return this.queryOne(queryRequest);
        }
        return null;
    }

    @Override
    public IObjectCollection getObjectByOBIDAndClassDefinitionUID(List<String> obids, String classDefinitionUID) {
        if (CommonUtility.hasValue(obids) && !StringUtils.isEmpty(classDefinitionUID)) {
            QueryRequest queryRequest = this.start();
            this.addOBIDForQuery(queryRequest, operator.in, String.join(",", obids));
            this.addClassDefForQuery(queryRequest, classDefinitionUID);
            return this.query(queryRequest);
        }
        return null;
    }

    @Override
    public IObject getRelationshipByOBIDAndRelDef(String obid, String relDef) throws Exception {
        if (!StringUtils.isEmpty(obid)) {
            QueryRequest queryRequest = this.start();
            this.addOBIDForQuery(queryRequest, null, obid);
            if (!StringUtils.isEmpty(relDef)) {
                IRelDef relDef1 = CIMContext.Instance.ProcessCache().item(relDef, domainInfo.SCHEMA.toString()).toInterface(IRelDef.class);
            }
        }
        return null;
    }

    @Override
    public IObjectCollection getRelationshipsByRelDefs(Collection<String> relDefs) throws Exception {
        if (CommonUtility.hasValue(relDefs)) {
            QueryRequest queryRequest = this.start();
            this.setQueryForRelationship(queryRequest, true);
            this.addRelOrEdgeDefForQuery(queryRequest, null, interfaceDefinitionType.IRel.toString(), propertyDefinitionType.RelDefUID.toString(), operator.in, String.join(",", relDefs));
            return this.query(queryRequest);
        }
        return null;
    }

    @Override
    public void addInterfaceForQuery(QueryRequest queryRequest, String interfaceDef) {
        if (!StringUtils.isEmpty(interfaceDef)) {
            if (queryRequest != null) queryRequest.addQueryInterface(interfaceDef);
        }
    }

    @Override
    public void addClassDefForQuery(QueryRequest queryRequest, String classDef) {
        if (!StringUtils.isEmpty(classDef)) {
            if (queryRequest != null) queryRequest.useClassDefForQuery(classDef);
        }
    }

    @Override
    public void addInterfaceDefsForQuery(QueryRequest queryRequest, String... interfaceDefs) {
        if (interfaceDefs != null && interfaceDefs.length > 0) {
            if (queryRequest != null) queryRequest.addQueryInterface(interfaceDefs);
        }
    }

    @Override
    public void addOBIDForQuery(QueryRequest queryRequest, operator operator, String obid) {
        if (!StringUtils.isEmpty(obid)) {
            this.addPropertyForQuery(queryRequest, "", propertyDefinitionType.OBID.toString(), operator, obid);
        }
    }

    @Override
    public void addOBIDForQuery(QueryRequest queryRequest, Collection<String> obids) {
        if (queryRequest != null && CommonUtility.hasValue(obids)) {
            this.addPropertyForQuery(queryRequest, "", propertyDefinitionType.OBID.toString(), operator.in, String.join(",", obids));
        }
    }

    @Override
    public void addPropertyForQuery(QueryRequest queryRequest, String interfaceDef, String propertyDef, operator operator, String value) {
        if (queryRequest != null) {
            String propertyType = this.getPropertyValueType(propertyDef);
            queryRequest.addQueryCriteria("", interfaceDef, propertyDef, propertyType, operator, value, ExpansionMode.none);
        }

    }

    @Override
    public void addNameForQuery(QueryRequest queryRequest, operator operator, String value) {
        if (!StringUtils.isEmpty(value))
            this.addPropertyForQuery(queryRequest, "", propertyDefinitionType.Name.toString(), operator, value);
    }

    @Override
    public void addUIDForQuery(QueryRequest queryRequest, operator operator, String value) {
        if (queryRequest != null)
            this.addPropertyForQuery(queryRequest, "", propertyDefinitionType.UID.toString(), operator, value);
    }

    @Override
    public void addRelDefUidForQuery(QueryRequest queryRequest, operator operator, String value) {
        if (queryRequest != null) {
            this.addPropertyForQuery(queryRequest, null, propertyDefinitionType.RelDefUID.toString(), operator, value);
        }
    }

    public void addUIDAndDomainUIDForQuery(QueryRequest queryRequest, List<String> uids, String domainUIDs) {
        if (queryRequest != null && CommonUtility.hasValue(uids)) {
            this.addPropertyForQuery(queryRequest, "", propertyDefinitionType.UID.toString(), operator.in, String.join(",", uids));
            this.addPropertyForQuery(queryRequest, "", propertyDefinitionType.DomainUID.toString(), operator.in, String.join(",", new ArrayList<String>() {{
                add(domainUIDs);
                add("");
            }}));
        }
    }

    @Override
    public void addDomainUIDForQuery(QueryRequest queryRequest, operator operator, String value) {
        if (queryRequest != null)
            this.addPropertyForQuery(queryRequest, "", propertyDefinitionType.DomainUID.toString(), operator, value);
    }


    @Override
    public void addDescriptionForQuery(QueryRequest queryRequest, operator operator, String value) {
        if (queryRequest != null)
            this.addPropertyForQuery(queryRequest, "", propertyDefinitionType.Description.toString(), operator, value);
    }

    protected String getPropertyValueType(String propertyDefinitionUid) {
        if (!StringUtils.isEmpty(propertyDefinitionUid)) {
            IObject item = CIMContext.Instance.ProcessCache().item(propertyDefinitionUid, domainInfo.SCHEMA.name());
            if (item != null) {
                IPropertyType scopedByPropertyType = item.toInterface(IPropertyDef.class).getScopedByPropertyType();
                if (scopedByPropertyType != null)
                    return scopedByPropertyType.Name();
            }
        }
        return propertyValueType.StringType.name();
    }

    @Override
    public QueryCriteria addRelOrEdgeDefForQuery(QueryRequest queryRequest, String relOrEdgeDef, String interfaceDefUid, String propertyDefUid, operator pstrOperator, String value, ExpansionMode expansionMode) throws Exception {
        if (queryRequest != null) {
            String propertyType = this.getPropertyValueType(propertyDefUid);
            QueryCriteria queryCriteria = queryRequest.addQueryCriteria(relOrEdgeDef, interfaceDefUid, propertyDefUid, propertyType, pstrOperator, value, expansionMode);
            if (queryCriteria != null) {
                if (operator.leftJoinIndicator(pstrOperator) && !StringUtils.isEmpty(relOrEdgeDef))
                    queryCriteria.setJoinMode(joinMode.Left);
                CIMContext.Instance.ProcessCache().setCriteriaAppendTable(queryCriteria);
            }
            return queryCriteria;
        }
        return null;
    }

    @Override
    public QueryCriteria addRelOrEdgeDefForQuery(QueryRequest queryRequest, String pstrRelOrEdge, String interfaceDefUid, String propertyDefUid, operator pstrOperator, String pstrValue) throws Exception {
        if (queryRequest != null) {
            ExpansionMode expansionMode = ExpansionMode.none;
            if (!StringUtils.isEmpty(pstrRelOrEdge)) expansionMode = ExpansionMode.relatedObject;
            String propertyType = this.getPropertyValueType(propertyDefUid);
            QueryCriteria queryCriteria = queryRequest.addQueryCriteria(pstrRelOrEdge, interfaceDefUid, propertyDefUid, propertyType, pstrOperator, pstrValue, expansionMode);
            if (queryCriteria != null) {
                if (operator.leftJoinIndicator(pstrOperator) && !StringUtils.isEmpty(pstrRelOrEdge))
                    queryCriteria.setJoinMode(joinMode.Left);
                CIMContext.Instance.ProcessCache().setCriteriaAppendTable(queryCriteria);
            }
            return queryCriteria;
        }
        return null;
    }

    @Override
    public QueryCriteria getCriteria(QueryRequest queryRequest, String relOrEdge, String interfaceDef, String propertyDef) {
        if (!StringUtils.isEmpty(relOrEdge) && !StringUtils.isEmpty(propertyDef)) {
            if (queryRequest != null) {
                return queryRequest.getCriteria(relOrEdge, interfaceDef, propertyDef);
            }
        }
        return null;
    }

    protected List<String> getScopes(String scope) {
        List<String> result = new ArrayList<>();
        if (!StringUtils.isEmpty(scope)) result.add(scope);
        result.add("");
        return result;
    }

    protected List<String> getTablePrefixes(String scope, List<IDomain> domains) {
        List<String> result = new ArrayList<>();
        if (CommonUtility.hasValue(domains)) {
            List<String> scopes = this.getScopes(scope);
            List<IDomain> notScopedDomains = new ArrayList<>();
            for (String s : scopes) {
                for (IDomain domain : domains) {
                    if (domain.ScopeWiseInd()) {
                        String tablePrefix = domain.TablePrefix();
                        if (!StringUtils.isEmpty(tablePrefix) && !StringUtils.isEmpty(s)) result.add(s + tablePrefix);
                    } else notScopedDomains.add(domain);
                }
            }
            if (notScopedDomains.size() > 0) {
                for (IDomain domain : notScopedDomains) {
                    String tablePrefix = domain.TablePrefix();
                    if (!StringUtils.isEmpty(tablePrefix)) result.add(tablePrefix);
                }
            }
        }
        return result;
    }

    @Override
    public IRelCollection expandRelationship(IObjectCollection startObjects, relDirection direction, IRelDef relDef, String configurationUid) throws Exception {
        IRelCollection result = null;
        if (startObjects != null && startObjects.hasValue() && relDef != null) {
            String relDefUID = relDef.UID();
            String scope = null;
            if (StringUtils.isEmpty(configurationUid)) scope = CIMContext.Instance.getMyConfigurationItemTablePrefix();
            else scope = CIMContext.Instance.getConfigTablePrefix(configurationUid);

            List<IDomain> domains = CIMContext.Instance.getDomainsByRelDef(relDef, relDirection.toRelCollectionTypesForStart(direction));
            List<LiteObject> liteObjects = this.dbContext.onExpansion(this.getTablePrefixes(scope, domains), startObjects.listOfOBID(), relDefUID, direction);
            if (CommonUtility.hasValue(liteObjects)) {
                StopWatch stopWatch = PerformanceUtility.start();
                IObjectConversion objectConversion = this.getIObjectConversion();
                IObjectCollection objectCollection = null;
                try {
                    objectCollection = objectConversion.convert(liteObjects);
                } catch (Exception exception) {
                    log.error("convert lite object to IObject failed", exception);
                } finally {
                    if (objectCollection != null && objectCollection.hasValue()) {
                        Iterator<IObject> e = objectCollection.GetEnumerator();
                        result = new RelCollection(relDirection.toRelCollectionTypesForStart(direction));
                        ConcurrentHashMap<String, List<IRel>> tempRelMap = new ConcurrentHashMap<>();
                        while (e.hasNext()) {
                            IRel object = e.next().toInterface(IRel.class);
                            result.add(object);
                            this.doCollectingFoundRel(tempRelMap, object, direction);
                        }
                        if (tempRelMap.size() > 0) {
                            Iterator<IObject> c = startObjects.GetEnumerator();
                            while (c.hasNext()) {
                                IObject r = c.next();
                                this.setRelIntoIObjectCache(r, tempRelMap, direction);
                            }
                        }
                    }
                    log.trace("finish to conversion and refresh cache" + PerformanceUtility.stop(stopWatch));
                }
            }
        }
        return result;
    }


    protected void setRelIntoIObjectCache(IObject currentItem, Map<String, List<IRel>> pMapItems, relDirection direction) throws Exception {
        List<IRel> relList = pMapItems.getOrDefault(currentItem.OBID(), null);
        if (CommonUtility.hasValue(relList)) {
            for (IRel rel : relList) {
                switch (direction) {
                    case _1To2:
                        currentItem.GetEnd1Relationships().add(rel);
                        break;
                    case _2To1:
                        currentItem.GetEnd2Relationships().add(rel);
                        break;
                }
            }
        }
    }

    private void doCollectingFoundRel(Map<String, List<IRel>> pMapContainer, IRel rel, relDirection relDirection) throws Exception {
        if (pMapContainer != null && rel != null) {
            String key = "";
            switch (relDirection) {
                case _2To1:
                    key = rel.OBID2();
                    break;
                case _1To2:
                    key = rel.OBID1();
                    break;
            }
            if (!StringUtils.isEmpty(key)) {
                List<IRel> mapOrDefault = pMapContainer.getOrDefault(key, new ArrayList<>());
                if (mapOrDefault.size() > 0) {
                    mapOrDefault.add(rel);
                    pMapContainer.replace(key, mapOrDefault);
                } else {
                    mapOrDefault.add(rel);
                    pMapContainer.put(key, mapOrDefault);
                }
            }
        }
    }

    @Override
    public void setRenderStyle(QueryRequest queryRequest, ObjectDTO objectDTO) {
        if (objectDTO != null) {
            if (queryRequest != null) {
                queryRequest.setRenderStyle(objectDTO);
            }
        }
    }
}
