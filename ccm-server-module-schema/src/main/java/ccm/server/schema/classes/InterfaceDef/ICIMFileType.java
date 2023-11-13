package ccm.server.schema.classes.InterfaceDef;

import ccm.server.schema.interfaces.defaults.IInterfaceDefDefault;
import ccm.server.schema.interfaces.defaults.ISchemaObjectDefault;

public class ICIMFileType extends Default {
    public ICIMFileType(boolean instantiateRequiredItems) throws Exception {
        super(instantiateRequiredItems);
        if (!this.hasInterface("ISchemaObject"))
            this.Interfaces().add(new ISchemaObjectDefault(instantiateRequiredItems));
        if (!this.hasInterface("IInterfaceDef"))
            this.Interfaces().add(new IInterfaceDefDefault(instantiateRequiredItems));
    }
}
