package ccm.server.schema.interfaces;

import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;

public interface ICIMFileComposition extends IObject {

    IObjectCollection getAllFiles() throws Exception;
}
