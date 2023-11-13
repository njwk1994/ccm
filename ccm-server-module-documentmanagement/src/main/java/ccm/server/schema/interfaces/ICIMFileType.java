package ccm.server.schema.interfaces;

import ccm.server.schema.interfaces.IObject;

public interface ICIMFileType extends IObject {

    void setCIMFileExtension(String fileExtension) throws Exception;

    String getCIMFileExtension();

    void setCIMFileViewable(boolean fileViewable) throws Exception;

    boolean getCIMFileViewable();

    void setCIMFileEditable(boolean fileEditable) throws Exception;

    boolean getCIMFileEditable();
}
