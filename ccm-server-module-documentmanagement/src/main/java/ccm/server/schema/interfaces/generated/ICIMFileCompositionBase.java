package ccm.server.schema.interfaces.generated;


import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.relDefinitionType;
import ccm.server.schema.interfaces.ICIMFileComposition;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.model.InterfaceDefault;

public abstract class ICIMFileCompositionBase extends InterfaceDefault implements ICIMFileComposition {
    public ICIMFileCompositionBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.ICIMFileComposition.toString(), instantiateRequiredProperties);
    }


    @Override
    public IObjectCollection getAllFiles() throws Exception {
        return this.GetEnd2Relationships().GetRels(relDefinitionType.CIMFileComposition.toString(), false).GetEnd1s();
    }
}
