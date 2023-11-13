package ccm.server.schema.interfaces.generated;

import ccm.server.enums.relDefinitionType;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.ICIMColumnSet;
import ccm.server.schema.model.InterfaceDefault;

public abstract class ICIMColumnSetBase extends InterfaceDefault implements ICIMColumnSet {
    public ICIMColumnSetBase(boolean instantiateRequiredProperties) {
        super("ICIMColumnSet", instantiateRequiredProperties);
    }

    @Override
    public IObjectCollection getColumnItems() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.columnSet2Items.toString(), true);
        if (relCollection != null && relCollection.hasValue())
            return relCollection.GetEnd2s();
        return null;
    }
}
