package ccm.server.schema.interfaces;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;

public interface IROPStepSet extends IROPGeneral {

    IObjectCollection getROPStep(PageRequest pageRequest, String constructionTypeObid) throws Exception;

    IObject getROPStepByUid(String uid) throws Exception;

    IObject createROPStepSet(ObjectDTO ROPStepSet) throws Exception;

    void updateROPStepSet(ObjectDTO ROPStepSet) throws Exception;

    void deleteROPStepSet(String uid) throws Exception;
}
