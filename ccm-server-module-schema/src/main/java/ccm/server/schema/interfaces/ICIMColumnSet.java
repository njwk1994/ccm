package ccm.server.schema.interfaces;

import ccm.server.schema.collections.IObjectCollection;

public interface ICIMColumnSet extends IObject {

    IObjectCollection getColumnItems() throws Exception;
}
