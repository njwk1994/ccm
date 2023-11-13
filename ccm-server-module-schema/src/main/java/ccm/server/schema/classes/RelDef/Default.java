package ccm.server.schema.classes.RelDef;

import ccm.server.schema.interfaces.defaults.IRelDefDefault;
import ccm.server.schema.interfaces.defaults.IRelDefault;
import ccm.server.schema.interfaces.defaults.ISchemaObjectDefault;
import ccm.server.schema.model.ClassBase;

import java.io.Serializable;

public class Default extends ClassBase implements Serializable {
    private static final long serializableId = 1L;

    public Default(boolean instantiateRequiredItems) throws Exception {
        super(instantiateRequiredItems);
        this.setClassDefinitionUid("RelDef");
        if (!this.hasInterface("IRelDef"))
            this.Interfaces().add(new IRelDefDefault(instantiateRequiredItems));
        if (!this.hasInterface("IRel"))
            this.Interfaces().add(new IRelDefault(instantiateRequiredItems));
        if (!this.hasInterface("ISchemaObject"))
            this.Interfaces().add(new ISchemaObjectDefault(instantiateRequiredItems));
    }
}
