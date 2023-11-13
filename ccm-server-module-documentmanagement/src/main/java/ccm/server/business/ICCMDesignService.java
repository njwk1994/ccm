package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/22 9:01
 */
public interface ICCMDesignService {

    /* ******************************************************* 设计数据-树方法 Start ******************************************************* */

    List<ObjectDTO> getDocumentFormPropertiesForConfigurationItem() throws Exception;

    Map<String, Object> getDocumentHierarchyConfigurationFormWithItem(String formPurpose) throws Exception;

    ObjectDTO getHierarchyConfigurationForm(String formPurpose) throws Exception;

    IObject createHierarchyConfiguration(JSONObject requestBody) throws Exception;

    IObject createHierarchyConfigurationWithItems(JSONObject requestBody) throws Exception;

    void deleteHierarchyConfiguration(String hierarchyConfigurationOBID) throws Exception;

    void updateHierarchyConfiguration(ObjectDTO hierarchyConfiguration) throws Exception;

    IObjectCollection getMyHierarchyConfigurations(HttpServletRequest request,PageRequest pageRequest) throws Exception;

    IObjectCollection getHierarchyConfigurationItems(String hierarchyConfigurationOBID) throws Exception;

    ObjectDTO getHierarchyConfigurationItemForm(String formPurpose) throws Exception;

    IObject createHierarchyConfigurationItemByConfigurationOBID(String configurationOBID, ObjectDTO toCreateItem) throws Exception;

    void deleteHierarchyConfigurationItem(String itemOBID) throws Exception;

    void updateHierarchyConfigurationItem(ObjectDTO hierarchyConfigurationItem) throws Exception;

    HierarchyObjectDTO generateHierarchy(String hierarchyConfigurationOBID) throws Exception;

    HierarchyObjectDTO generateHierarchyByDocumentsAndConfiguration(String hierarchyConfigurationOBID) throws Exception;

    IObjectCollection getDocumentsFromHierarchyNode(HierarchyObjectDTO selectedNode, PageRequest pageRequest) throws Exception;

    /* ******************************************************* 设计数据-树方法 End ******************************************************* */
    /* ******************************************************* 设计数据方法 Start ******************************************************* */
    IObject getComponentByOBID(String obid)throws Exception;
    /* ******************************************************* 设计数据方法 End ******************************************************* */


}
