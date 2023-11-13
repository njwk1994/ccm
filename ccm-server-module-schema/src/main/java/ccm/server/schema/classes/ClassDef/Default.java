package ccm.server.schema.classes.ClassDef;

import ccm.server.schema.interfaces.defaults.IClassDefDefault;
import ccm.server.schema.interfaces.defaults.ISchemaObjectDefault;
import ccm.server.schema.model.ClassBase;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public class Default extends ClassBase implements Serializable {
    private static final long serializableId = 1L;

    public Default(boolean instantiateRequiredItems) throws Exception {
        super(instantiateRequiredItems);
        this.setClassDefinitionUid("ClassDef");
        if (!this.hasInterface("ISchemaObject"))
            this.Interfaces().add(new ISchemaObjectDefault(instantiateRequiredItems));
        if (!this.hasInterface("IClassDef"))
            this.Interfaces().add(new IClassDefDefault(instantiateRequiredItems));
    }
}
