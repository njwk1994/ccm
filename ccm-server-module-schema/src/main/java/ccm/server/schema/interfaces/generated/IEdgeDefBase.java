package ccm.server.schema.interfaces.generated;

import ccm.server.schema.interfaces.IEdgeDef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class IEdgeDefBase extends ISchemaObjectBase implements IEdgeDef {
    public IEdgeDefBase(boolean instantiateRequiredProperties) {
        super("IEdgeDef", instantiateRequiredProperties);
    }

    public IEdgeDefBase(String interfaceDefinitionUid, boolean instantiateRequiredProperties) {
        super(interfaceDefinitionUid, instantiateRequiredProperties);
    }
}
