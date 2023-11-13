package ccm.server.schema.interfaces;

public interface IEnumEnum extends ISchemaObject {
    int EnumNumber();

    void setEnumNumber(int value) throws Exception;

    ICIMObjClass getObjClass() throws Exception;

    boolean isHint(String value);
}
