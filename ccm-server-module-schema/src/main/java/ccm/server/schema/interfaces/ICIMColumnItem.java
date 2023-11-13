package ccm.server.schema.interfaces;

public interface ICIMColumnItem extends IObject {

    String RelOrEdgeDefUID();

    void setRelOrEdgeDefUID(String value) throws Exception;

    String PropertyAsValueSource();

    void setPropertyAsValueSource(String value) throws Exception;

    String ValuePattern();

    void setValuePattern(String value) throws Exception;

    ICIMColumnSet getColumnSet() throws Exception;
}
