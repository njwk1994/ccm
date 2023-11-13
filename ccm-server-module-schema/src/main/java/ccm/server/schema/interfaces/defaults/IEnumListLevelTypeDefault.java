package ccm.server.schema.interfaces.defaults;

import ccm.server.schema.interfaces.IEnumListType;
import ccm.server.schema.interfaces.IRel;
import ccm.server.schema.interfaces.IRelDef;
import ccm.server.schema.interfaces.generated.IEnumListLevelTypeBase;
import ccm.server.enums.relDefinitionType;

public class IEnumListLevelTypeDefault extends IEnumListLevelTypeBase {
    public IEnumListLevelTypeDefault(boolean instantiateRequiredProperties) {
        super(instantiateRequiredProperties);
    }

    @Override
    public IEnumListType getBaseEnumListForEnumLevel() throws Exception {
        IRel rel = this.GetEnd1Relationships().GetRel(relDefinitionType.baseEnumListTypeForEnumListLevelType.toString());
        if (rel != null) {
            return rel.toInterface(IEnumListType.class);
        }
        return null;
    }

    @Override
    public IRelDef getRelDefForEnumLevel() throws Exception {
        IRel rel = this.GetEnd1Relationships().GetRel(relDefinitionType.usedEdgeDefForEnumListLevelType.toString());
        if (rel != null) {
            return rel.toInterface(IRelDef.class);
        }
        return null;
    }
}
