package ccm.server.module.system.service.impl;

import ccm.server.module.service.fdn.IFdnBusinessService;
import ccm.server.module.service.impl.fdn.models.entityPackage;
import ccm.server.module.system.DTO.systemOptionsDTO;
import ccm.server.module.system.service.ICCMSystemManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CCMSystemManagementService implements ICCMSystemManagementService {

    @Autowired
    private IFdnBusinessService fdnBusinessService;

    public final static String CLASSDEF_SYSTEM_OPTIONS = "CCMOption";

    @Override
    public systemOptionsDTO getSystemOptions() throws Exception {
        entityPackage entity = this.fdnBusinessService.getBaseQueryWrapper().getEntity(false, null, CLASSDEF_SYSTEM_OPTIONS);
        return entity.toObjectDTO().toDTO(systemOptionsDTO.class);
    }

    @Override
    public String getOption(String defUID) {
        return null;
    }
}
