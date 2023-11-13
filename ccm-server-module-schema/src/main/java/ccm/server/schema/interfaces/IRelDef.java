package ccm.server.schema.interfaces;

import ccm.server.dto.base.OptionItemDTO;
import ccm.server.enums.propertyValueType;
import ccm.server.enums.relCollectionTypes;
import ccm.server.enums.relDirection;
import ccm.server.schema.collections.IObjectCollection;

import java.util.List;

public interface IRelDef extends IRel, ICacheInfo {

    List<String> getUsedDomainsTablePrefix(relCollectionTypes collectionTypes) throws Exception;

    List<IDomain> getUsedDomains(relCollectionTypes collectionTypes) throws Exception;

    String Role1() throws Exception;

    void setRole1(String role1) throws Exception;

    String Role1DisplayName() throws Exception;

    void setRole1DisplayName(String role1DisplayName) throws Exception;

    String End1Locality() throws Exception;

    void setEnd1Locality(String end1Locality) throws Exception;

    int Min1() throws Exception;

    void setMin1(int min1) throws Exception;

    String Max1() throws Exception;

    void setMax1(String max1) throws Exception;

    String SpecRelDefUID() throws Exception;

    void setSpecRelDefUID(String specRelDefUID) throws Exception;

    boolean IsAbstract() throws Exception;

    void setIsAbstract(boolean isAbstract) throws Exception;

    String LinkInterfaces() throws Exception;

    void setLinkInterfaces(String linkInterfaces) throws Exception;

    int Min2() throws Exception;

    void setMin2(String min2) throws Exception;

    String Max2() throws Exception;

    void setMax2(String max2) throws Exception;

    String End2Locality() throws Exception;

    void setEnd2Locality(String end2Locality) throws Exception;

    String Role2() throws Exception;

    void setRole2(String role2) throws Exception;

    String Role2DisplayName() throws Exception;

    void setRole2DisplayName(String role2DisplayName) throws Exception;

    IRelDef getSpecializationOf();

    IObjectCollection getSpecializesRelDefs();

    IObjectCollection getLinkInterfaceDefs();

    boolean Delete12() throws Exception;

    void setDelete12(boolean delete12) throws Exception;

    boolean Delete21() throws Exception;

    void setDelete21(boolean delete21) throws Exception;

    String Copy12() throws Exception;

    void setCopy12(String copy12) throws Exception;

    String Copy21() throws Exception;

    void setCopy21(String copy21) throws Exception;

    boolean Owner12() throws Exception;

    boolean Owner21() throws Exception;

    void setOwner12(boolean owner12) throws Exception;

    void setOwner21(boolean owner21) throws Exception;

    propertyValueType checkPropertyValueType(relDirection direction) throws Exception;

    boolean checkMandatory(relDirection relDirection) throws Exception;

    String getTargetInterface(relDirection relDirection) throws Exception;

    List<OptionItemDTO> generateOptions(relDirection relDirection) throws Exception;
}
