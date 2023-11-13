package ccm.server.schema.classes.InterfaceDef;

import ccm.server.schema.interfaces.defaults.IInterfaceDefDefault;
import ccm.server.schema.interfaces.defaults.ISchemaObjectDefault;
import ccm.server.schema.model.ClassBase;

import java.io.Serializable;

public class Default extends ClassBase implements Serializable {
    private static final long serializableId = 1L;
    public Default(boolean instantiateRequiredItems) throws Exception {
        super(instantiateRequiredItems);
        this.setClassDefinitionUid("InterfaceDef");
        if (!this.hasInterface("ISchemaObject"))
            this.Interfaces().add(new ISchemaObjectDefault(instantiateRequiredItems));
        if (!this.hasInterface("IInterfaceDef"))
            this.Interfaces().add(new IInterfaceDefDefault(instantiateRequiredItems));
    }
}
