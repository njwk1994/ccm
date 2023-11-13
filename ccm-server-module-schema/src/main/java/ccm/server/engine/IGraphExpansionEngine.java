package ccm.server.engine;

import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;

public interface IGraphExpansionEngine {

    IObjectCollection getRelatedInfoForObject(IObject object) throws Exception;

    void getRelatedInfoForObject(IObjectCollection objectCollection, IObjectCollection container) throws Exception;
}
