package ccm.server.schema.interfaces.generated;

import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.IEnumListType;
import ccm.server.enums.relDefinitionType;

public abstract class IEnumListTypeBase extends IEnumEnumBase implements IEnumListType {
    public IEnumListTypeBase(boolean instantiateRequiredProperties) {
        super("IEnumListType", instantiateRequiredProperties);
    }

    public IEnumListTypeBase(String interfaceDefinitionUid, boolean instantiateRequiredProperties) {
        super(interfaceDefinitionUid, instantiateRequiredProperties);
    }

    @Override
    public IObjectCollection getEntries() throws Exception {
        return this.getEntries(true);
    }

    @Override
    public IObjectCollection getEntries(boolean pblnCacheOnly) throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.contains.toString(), pblnCacheOnly);
        if (relCollection != null && relCollection.size() > 0) {
            return relCollection.GetEnd2s();
        }
        return null;
    }
}
