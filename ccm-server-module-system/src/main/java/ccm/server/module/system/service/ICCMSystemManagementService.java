package ccm.server.module.system.service;

import ccm.server.module.system.DTO.systemOptionsDTO;

public interface ICCMSystemManagementService {

    systemOptionsDTO getSystemOptions() throws Exception;

    String getOption(String defUID);
}
