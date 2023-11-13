package ccm.server.schema.classes.Rel;

import ccm.server.schema.interfaces.defaults.IRelDefault;
import ccm.server.schema.model.ClassBase;

import java.io.Serializable;

public class Default extends ClassBase implements Serializable {
    private static final long serializableId = 1L;

    public Default(boolean instantiateRequiredItems) throws Exception {
        super(instantiateRequiredItems);
        this.setClassDefinitionUid("Rel");
        if (!this.hasInterface("IRel"))
            this.Interfaces().add(new IRelDefault(instantiateRequiredItems));
    }
}
