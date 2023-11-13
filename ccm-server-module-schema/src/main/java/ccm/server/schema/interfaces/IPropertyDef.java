package ccm.server.schema.interfaces;

import ccm.server.dto.base.OptionItemDTO;
import ccm.server.enums.propertyValueType;

import java.util.List;

public interface IPropertyDef extends ISchemaObject {

    Object DefaultValue() throws Exception;

    void setDefaultValue(Object value) throws Exception;

    String TypeData() throws Exception;

    void setTypeData(String value) throws Exception;

    IPropertyType getScopedByPropertyType()  ;

    IInterfaceDef getExposesInterfaceDef() throws Exception;

    boolean HistoryNotRetained() throws Exception;

    void setHistoryNotRetained(boolean value) throws Exception;

    IObject getScopedBy() throws Exception;

    propertyValueType checkPropertyValueType() ;

    boolean checkMandatory() throws Exception;

    IRel getExposedInterfaceDefReturnRel() throws Exception;

    IInterfaceDef getExposedInterfaceDef() throws Exception;

    List<OptionItemDTO> generateOptions() throws Exception;

    boolean isEnumListType() throws Exception;


}
