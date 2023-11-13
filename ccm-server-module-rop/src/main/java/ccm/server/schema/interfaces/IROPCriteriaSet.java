package ccm.server.schema.interfaces;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;

public interface IROPCriteriaSet extends IROPGeneral {

    IObjectCollection getROPCriteria(PageRequest pageRequest, String constructionTypeObid) throws Exception;

    IObject getROPCriteriaByUid(String uid) throws Exception;

    IObject createROPCriteriaSet(ObjectDTO ROPCriteriaSet) throws Exception;

    void updateROPCriteriaSet(ObjectDTO ROPCriteriaSet) throws Exception;

    void deleteROPCriteriaSet(String uid) throws Exception;

    String getROPComponentClasses();

    void setROPComponentClasses(String ROPComponentClasses) throws Exception;

    String getROPCondition();

    void setROPCondition(String ROPCondition) throws Exception;
}
