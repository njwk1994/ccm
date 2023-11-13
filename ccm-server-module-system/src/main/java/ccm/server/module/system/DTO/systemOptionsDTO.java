package ccm.server.module.system.DTO;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.entity.MetaDataObj;
import ccm.server.entity.MetaDataObjProperty;

import java.util.List;

public class systemOptionsDTO extends ObjectDTO {
    public systemOptionsDTO() {
        super();
    }

    public systemOptionsDTO(MetaDataObj metaObj, List<? extends MetaDataObjProperty> objPRs) {
        super(metaObj, objPRs);
    }
}
