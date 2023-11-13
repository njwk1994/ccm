package ccm.server.schema.interfaces;

import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;

import java.util.List;
import java.util.Map;

public interface IInterfaceDef extends ISchemaObject {

    int InterfaceSequence();

    void setInterfaceSequence(Integer value) throws Exception;

    List<String> getUsedDomainTablePrefix() throws Exception;

    List<IDomain> getUsedDomain() throws Exception;

    Map<String, List<IClassDef>> getRealizedClassDefDomainInfo() throws Exception;

    IObjectCollection getEnd1RelDefs() throws Exception;

    IObjectCollection getEnd2RelDefs() throws Exception;

    boolean isImplied(String pstrInterfaceDefUID, boolean pblnDeep);

    boolean isImpliedBy(String pstrInterfaceDefUID, boolean pblnDeep);

    boolean checkMandatory(String classDefinitionUID) throws Exception;

    IRelCollection getRealized() throws Exception;

    IObjectCollection getRealizedClassDefinition() throws Exception;

    IObjectCollection getExposesPropertyDefinition() throws Exception;
}
