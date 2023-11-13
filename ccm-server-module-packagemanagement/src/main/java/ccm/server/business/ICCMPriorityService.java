package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.OptionItemDTO;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;

import java.util.List;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/25 8:57
 */
public interface ICCMPriorityService {

    List<OptionItemDTO> getPropertiesForPriorityItem() throws Exception;

    List<OptionItemDTO> getOperators() throws Exception;

    ObjectDTO getPriorityForm(String formPurpose) throws Exception;

    ObjectDTO getPriorityItemForm(String formPurpose) throws Exception;

    void createPriority(ObjectDTO objectDTO) throws Exception;

    void createPriorityItem(String priorityId, ObjectDTO priorityItemDTO) throws Exception;

    IObjectCollection getPriorities(PageRequest pageRequest) throws Exception;

    IObject getPriorityByOBID(String priorityId) throws Exception;

    IObject getPriorityItemByOBID(String priorityId) throws Exception;

    IObjectCollection getPriorityItems(String priorityId) throws Exception;

    void deletePriority(String priorityId) throws Exception;

    void deletePriorityItem(String priorityItemOBID) throws Exception;

    void updatePriority(ObjectDTO priorityDTO) throws Exception;

    void updatePriorityItem(ObjectDTO priorityItemDTO) throws Exception;

    boolean startToExecutePriority(String priorityId) throws Exception;

    List<ObjectDTO> executePriority(String taskPackageId, String priorityId) throws Exception;
}
