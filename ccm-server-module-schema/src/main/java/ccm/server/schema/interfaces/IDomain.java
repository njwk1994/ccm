package ccm.server.schema.interfaces;

import ccm.server.schema.collections.IObjectCollection;

public interface IDomain extends IObject {

    boolean ScopeWiseInd();

    void setScopeWiseInd(boolean value) throws Exception;

    String TablePrefix();

    void setTablePrefix(String value) throws Exception;

    IObjectCollection getIncludeClassDefs() throws Exception;

}
