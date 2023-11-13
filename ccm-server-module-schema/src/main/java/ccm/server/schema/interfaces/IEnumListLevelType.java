package ccm.server.schema.interfaces;

public interface IEnumListLevelType extends ISchemaObject, IPropertyType {

    IEnumListType getBaseEnumListForEnumLevel() throws Exception;

    IRelDef getRelDefForEnumLevel() throws Exception;
}
