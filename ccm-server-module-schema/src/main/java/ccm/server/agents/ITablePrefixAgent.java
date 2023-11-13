package ccm.server.agents;

import ccm.server.models.query.QueryRequest;
import ccm.server.module.service.base.IInternalService;

public interface ITablePrefixAgent extends IInternalService {
    void setDomainScopeForQueryRequest(QueryRequest queryRequest) throws Exception;
}
