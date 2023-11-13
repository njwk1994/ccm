package ccm.server.schema.interfaces;

import ccm.server.entity.MetaDataRel;

import java.util.List;

public interface IRel extends ISchemaObject, IObject {

    String ClassDefinitionUID1();

    void setClassDefinitionUID1(String value) throws Exception;

    String ClassDefinitionUID2();

    void setClassDefinitionUID2(String value) throws Exception;

    String generateUniqueKey() throws Exception;

    String generateUID() throws Exception;

    String RelDefUID() throws Exception;

    void setRelDefUID(String relDefUID) throws Exception;

    String DomainUID1() throws Exception;

    void setDomainUID1(String domainUID1) throws Exception;

    String DomainUID2() throws Exception;

    void setDomainUID2(String domainUID2) throws Exception;

    String UID1() throws Exception;

    void setUID1(String uid1) throws Exception;

    String UID2() throws Exception;

    void setUID2(String uid2) throws Exception;

    String OBID1() throws Exception;

    void setOBID1(String obid) throws Exception;

    String OBID2() throws Exception;

    void setOBID2(String obid) throws Exception;

    String Prefix() throws Exception;

    void setPrefix(String prefix) throws Exception;

    boolean IsRequired() throws Exception;

    void setIsRequired(boolean isRequired) throws Exception;

    Integer OrderValue();

    void setOrderValue(int orderValue) throws Exception;

    String Name1() throws Exception;

    void setName1(String name1) throws Exception;

    String Name2() throws Exception;

    void setName2(String name2) throws Exception;

    IObject GetEnd1() throws Exception;

    IObject GetEnd1(String configurationUid) throws Exception;
    @Override
    List<String> getPropertiesThatCannotBeUpdated();

    IObject GetEnd2() throws Exception;

    IObject GetEnd2(String configurationUid) throws Exception;

    IRelDef GetRelationshipDefinition() throws Exception;

    void setEnd1(String pstrOBID, String pstrUID, String domainUID, String classDefinitionUID, String name) throws Exception;

    void setEnd2(String pstrOBID, String pstrUID, String domainUID, String classDefinitionUID, String name) throws Exception;

    MetaDataRel toMetaDataRel() throws Exception;

    boolean isOwner(IObject pobjIObject) throws Exception;
}
