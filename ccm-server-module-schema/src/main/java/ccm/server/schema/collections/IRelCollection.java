package ccm.server.schema.collections;

import ccm.server.enums.relCollectionTypes;
import ccm.server.schema.interfaces.IRel;
import ccm.server.schema.model.relEndObj;

import java.util.Map;

public interface IRelCollection extends IObjectCollection {
    void add(IRel rel);

    void replace(IRel rel) throws Exception;

    void addRange(IRelCollection relCollection) throws Exception;

    Map<relEndObj, IRel> toMapByUIDAndOBID(relCollectionTypes relCollectionTypes) throws Exception;

    void addRangeUniquely(IRelCollection relCollection) throws Exception;

    IObjectCollection GetEnd1s() throws Exception;

    IObjectCollection GetEnd1s(IObjectCollection targetCollection) throws Exception;

    IObjectCollection GetEnd2s() throws Exception;

    IObjectCollection GetEnd2s(IObjectCollection targetCollection) throws Exception;


    IObjectCollection GetEnd1s(String configurationUid) throws Exception;

    IObjectCollection GetEnd1s(IObjectCollection targetCollection, String configurationUid) throws Exception;

    IObjectCollection GetEnd2s(String configurationUid) throws Exception;

    IObjectCollection GetEnd2s(IObjectCollection targetCollection, String configurationUid) throws Exception;


    IRel GetRel(String relDefUID, String configurationUid, boolean cacheOnly) throws Exception;

    IRel GetRel(String relDefUID, String configurationUid) throws Exception;

    IRel GetRel(String relDefUID, String pstrEnd2UID, String configurationUid) throws Exception;

    IRelCollection GetRels(String relDefUID, String configurationUid);

    IRelCollection GetRels(String relDefUID, String configurationUid, boolean cacheOnly);

    IRel GetRel(String relDefUID, boolean cacheOnly) throws Exception;

    IRel GetRel(String relDefUID) throws Exception;

    IRelCollection GetRels(String relDefUID);

    IRelCollection GetRels(String relDefUID, boolean cacheOnly);

    void remove(IRel rel) throws Exception;

    boolean containsUid1(String uid1) throws Exception;

    boolean containsUid2(String uid2) throws Exception;

    boolean containsOBID1(String uid1) throws Exception;

    boolean containsOBID2(String uid2) throws Exception;

}
