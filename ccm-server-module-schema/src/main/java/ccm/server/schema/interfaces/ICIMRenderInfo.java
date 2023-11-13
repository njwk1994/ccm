package ccm.server.schema.interfaces;

public interface ICIMRenderInfo extends IObject {

    int ColumnSpan();

    void setColumnSpan(Integer value) throws Exception;

    String DisplayAs();

    void setDisplayAs(String value) throws Exception;

    double Width();

    void setWidth(double value) throws Exception;

    boolean Visible();

    void setVisible(boolean value) throws Exception;

    double Length();

    void setLength(double value) throws Exception;

    boolean ReadOnly();

    void setReadOnly(boolean value) throws Exception;
}
