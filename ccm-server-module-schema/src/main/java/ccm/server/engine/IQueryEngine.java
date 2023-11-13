package ccm.server.engine;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.enums.*;
import ccm.server.models.query.QueryCriteria;
import ccm.server.models.query.QueryRequest;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRelDef;
import ccm.server.utils.IObjectConversion;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IQueryEngine {
    QueryRequest start();

    QueryRequest start(String configurationUid);

    void changeJoinMode(QueryRequest queryRequest, joinMode joinMode);

    void setOrderBy(QueryRequest queryRequest, orderMode orderMode, String definition);

    void setOrderBys(QueryRequest queryRequest, Map<String, orderMode> orders);

    void addDomain(QueryRequest queryRequest, String domain);

    void addDomains(QueryRequest queryRequest, Collection<String> domains);

    IObjectConversion getIObjectConversion();

    void setPageRequest(QueryRequest queryRequest, PageRequest page);

    IObjectCollection query(QueryRequest queryRequest);

    IObject queryOne(QueryRequest queryRequest);

    void setQueryForRelationship(QueryRequest queryRequest, boolean queryForRelationship);

    IObjectCollection getByOBIDAndClassDef(List<String> obids, List<String> classDefs, String configurationUid) throws Exception;

    IObject getObjectByUIDAndClassDefinitionUID(String uid, String classDefinitionUID);

    IObject getObjectByUIDAndClassDefinitionUID(String uid, String classDefinitionUID, IObject configurationItem);

    IObjectCollection getObjectByUIDsAndInterfaceDefinitionUID(List<String> uids, String interfaceDefinitionUID);

    IObjectCollection getObjectByOBIDsAndInterfaceDefinitionUID(List<String> obids, String interfaceDefinitionUID);

    IObject getObjectByOBIDAndClassDefinitionUID(String obid, String classDefinitionUID);

    IObject getObjectByOBIDAndClassDefinitionUID(String obid, String classificationUid, IObject configurationItem);

    IObjectCollection getObjectByOBIDAndClassDefinitionUID(List<String> obids, String classDefinitionUID);

    IObject getRelationshipByOBIDAndRelDef(String obid, String relDef) throws Exception;

    IObjectCollection getRelationshipsByRelDefs(Collection<String> relDefs) throws Exception;

    void addInterfaceForQuery(QueryRequest queryRequest, String pstrInterfaceDef);

    void addClassDefForQuery(QueryRequest queryRequest, String pstrClassDef);

    void addInterfaceDefsForQuery(QueryRequest queryRequest, String... parrInterfaceDefs);

    void addOBIDForQuery(QueryRequest queryRequest, Collection<String> pstrOBID);

    void addOBIDForQuery(QueryRequest queryRequest, operator operator, String pstrOBID);

    void addPropertyForQuery(QueryRequest queryRequest, String pstrInterfaceDef, String pstrPropertyDef, operator pstrOperator, String pstrValue);

    void addNameForQuery(QueryRequest queryRequest, operator pstrOperator, String pstrValue);

    void addUIDForQuery(QueryRequest queryRequest, operator operator, String pstrValue);

    void addRelDefUidForQuery(QueryRequest queryRequest, operator operator, String pstrValue);

    void addDomainUIDForQuery(QueryRequest queryRequest, operator operator, String pstrValue);

    void addDescriptionForQuery(QueryRequest queryRequest, operator pstrOperator, String pstrValue);

    QueryCriteria addRelOrEdgeDefForQuery(QueryRequest queryRequest, String pstrRelOrEdge, String interfaceDefUid, String propertyDefUid, operator pstrOperator, String pstrValue, ExpansionMode expansionMode) throws Exception;

    QueryCriteria addRelOrEdgeDefForQuery(QueryRequest queryRequest, String pstrRelOrEdge, String interfaceDefUid, String propertyDefUid, operator pstrOperator, String pstrValue) throws Exception;

    QueryCriteria getCriteria(QueryRequest queryRequest, String relOrEdge, String interfaceDef, String propertyDef);

    IRelCollection expandRelationship(IObjectCollection startObjects, relDirection relDirection, IRelDef relDef, String configurationUid) throws Exception;

    void setRenderStyle(QueryRequest queryRequest, ObjectDTO objectDTO);
}
