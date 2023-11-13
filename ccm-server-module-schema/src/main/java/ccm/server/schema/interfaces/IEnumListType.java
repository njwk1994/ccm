package ccm.server.schema.interfaces;

import ccm.server.schema.collections.IObjectCollection;

public interface IEnumListType extends IEnumEnum, IPropertyType {

    IObjectCollection getEntries() throws Exception;

    IObjectCollection getEntries(boolean pblnCacheOnly) throws Exception;
}
