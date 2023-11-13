package ccm.server.agents.impl;

import ccm.server.agents.ITablePrefixAgent;
import ccm.server.context.CIMContext;
import ccm.server.enums.domainInfo;
import ccm.server.enums.relCollectionTypes;
import ccm.server.enums.relDirection;
import ccm.server.models.query.QueryCriteria;
import ccm.server.models.query.QueryInterface;
import ccm.server.models.query.QueryRequest;
import ccm.server.module.impl.general.InternalServiceImpl;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IClassDef;
import ccm.server.schema.interfaces.IInterfaceDef;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRelDef;
import ccm.server.util.CommonUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service("tablePrefixAgent")
@Slf4j
public class TablePrefixAgent extends InternalServiceImpl implements ITablePrefixAgent {

    protected void getDomainsForQueryByClassDef(List<String> domains, String classDefinition) throws Exception {
        String[] strings = classDefinition.split(",");
        if (strings.length > 1) {
            for (String string : strings) {
                IObject item = CIMContext.Instance.ProcessCache().item(string, domainInfo.SCHEMA.toString(), false);
                if (item != null) {
                    domains.addAll(item.toInterface(IClassDef.class).getUsedDomain());
                }
//                else
//                    log.trace("invalid class definition UID " + string + " from cache");
            }
        } else {
            IObject item = CIMContext.Instance.ProcessCache().item(classDefinition, domainInfo.SCHEMA.toString());
            if (item != null) {
                domains.addAll(item.toInterface(IClassDef.class).getUsedDomain());
            }
            else
                log.trace("invalid class definition UID " + classDefinition + " from cache");
        }
      //  log.info("domain scope(s) by class definition:" + domains.stream().distinct().collect(Collectors.joining(",")));
    }

    protected void getDomainsForQueryForRelationship(List<String> domains, Map<String, String> relDefUID) throws Exception {
        if (relDefUID.size() > 0) {
            for (Map.Entry<String, String> stringEntry : relDefUID.entrySet()) {
                IObject item = CIMContext.Instance.ProcessCache().item(stringEntry.getKey(), domainInfo.SCHEMA.toString(), false);
                if (item != null) {
                    IRelDef relDef = item.toInterface(IRelDef.class);
                    List<String> directionChars = Arrays.stream(stringEntry.getValue().split(",")).distinct().collect(Collectors.toList());
                    for (String directionChar : directionChars) {
                        if (directionChar.equalsIgnoreCase("+")) {
                            List<String> currentDomains = relDef.getUsedDomainsTablePrefix(relCollectionTypes.End1s);
                            if (currentDomains != null && currentDomains.size() > 0)
                                domains.addAll(currentDomains);
                        } else if (directionChar.equalsIgnoreCase("-")) {
                            List<String> c = relDef.getUsedDomainsTablePrefix(relCollectionTypes.End2s);
                            if (c != null && c.size() > 0)
                                domains.addAll(c);
                        }
                    }
                }
                else
                    log.trace("invalid relationship definition " + stringEntry.getKey() + " from cache");
            }
           // log.info("domain scope(s) by relationship definition:" + domains.stream().distinct().collect(Collectors.joining(",")));
        }
    }

    protected void getDomainsForQueryByInterfaces(List<String> domains, List<String> interfaceDefs) throws Exception {
        List<IClassDef> classDefs = new ArrayList<>();
        if (interfaceDefs.size() > 0) {
            for (String interfaceDef : interfaceDefs) {
                IInterfaceDef iInterfaceDef = CIMContext.Instance.ProcessCache().item(interfaceDef, domainInfo.SCHEMA.toString()).toInterface(IInterfaceDef.class);
                IObjectCollection realizedClassDefinition = iInterfaceDef.getRealizedClassDefinition();
                if (realizedClassDefinition != null && realizedClassDefinition.hasValue()) {
                    Iterator<IObject> objectIterator = realizedClassDefinition.GetEnumerator();
                    while (objectIterator.hasNext()) {
                        IClassDef classDef = objectIterator.next().toInterface(IClassDef.class);
                        if (!classDefs.contains(classDef))
                            classDefs.add(classDef);
                    }
                }
            }
        }
        if (classDefs.size() > 0) {
            for (IClassDef classDef : classDefs) {
                List<String> currentDomains = classDef.getUsedDomain();
                if (CommonUtility.hasValue(currentDomains))
                    domains.addAll(currentDomains);
            }
          //  log.info("domain scope(s) by interface definition:" + domains.stream().distinct().collect(Collectors.joining(",")));
        }
    }

    protected void getDomainsForQueryByCriterions(List<String> domains, List<QueryCriteria> queryCriteriaList) throws Exception {
        for (QueryCriteria queryCriteria : queryCriteriaList) {
            String relOrEdgeUID = queryCriteria.getRelOrEdgeDefinitionUID();
            String interfaceDefinitionUID = queryCriteria.getInterfaceDefinitionUID();
            if (!StringUtils.isEmpty(relOrEdgeUID)) {
                String schemaDefinitionUID = CommonUtility.toActualDefinition(relOrEdgeUID);
                relDirection relDirection = CommonUtility.toRelDirection(relOrEdgeUID);
                IObject item = CIMContext.Instance.ProcessCache().item(schemaDefinitionUID, domainInfo.SCHEMA.toString(), false);
                if (item != null) {
                    IRelDef relDef = item.toInterface(IRelDef.class);
                    switch (relDirection) {
                        case _1To2:
                            List<String> domain1s = relDef.getUsedDomainsTablePrefix(relCollectionTypes.End1s);
                            if (domain1s != null && domain1s.size() > 0)
                                domains.addAll(domain1s);
                            break;
                        case _2To1:
                            List<String> domain2s = relDef.getUsedDomainsTablePrefix(relCollectionTypes.End2s);
                            if (domain2s != null && domain2s.size() > 0)
                                domains.addAll(domain2s);
                            break;
                    }
                }
//                else
//                    log.trace("invalid relationship definition UID " + schemaDefinitionUID + " from cache");
            } else if (!StringUtils.isEmpty(interfaceDefinitionUID)) {
                IObject item = CIMContext.Instance.ProcessCache().item(interfaceDefinitionUID, domainInfo.SCHEMA.toString(), false);
                if (item != null) {
                    IInterfaceDef anInterface = item.toInterface(IInterfaceDef.class);
                    List<String> usedDomain = anInterface.getUsedDomainTablePrefix();
                    if (CommonUtility.hasValue(usedDomain))
                        domains.addAll(usedDomain);
                }
                else
                    log.trace("invalid interface definition UID " + interfaceDefinitionUID + " from cache");
            }
        }
       // log.info("domain scope(s) by criteria(s):" + domains.stream().distinct().collect(Collectors.joining(",")));
    }

    @Override
    public void setDomainScopeForQueryRequest(QueryRequest queryRequest) throws Exception {
        List<String> domains = new ArrayList<>();
        if (queryRequest != null) {
            if (queryRequest.queryByClassDefinition()) {
                this.getDomainsForQueryByClassDef(domains, queryRequest.getQueryClassDef().getClassDefinition());
            } else if (queryRequest.getQueryForRelationship()) {
                this.getDomainsForQueryForRelationship(domains, queryRequest.getRelDefUids());
            } else if (queryRequest.getQueryInterface().size() > 0) {
                this.getDomainsForQueryByInterfaces(domains, queryRequest.getQueryInterface().stream().map(QueryInterface::getInterfaceDefinition).filter(c -> !StringUtils.isEmpty(c)).distinct().collect(Collectors.toList()));
            } else if (queryRequest.getQueryCriterions().size() > 0) {
                this.getDomainsForQueryByCriterions(domains, queryRequest.getQueryCriterions());
            }
            queryRequest.getDomains().addAll(domains.stream().distinct().collect(Collectors.toList()));
        }
    }
}
