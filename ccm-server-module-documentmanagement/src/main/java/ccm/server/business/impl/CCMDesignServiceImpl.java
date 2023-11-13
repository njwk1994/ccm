package ccm.server.business.impl;

import ccm.server.business.ICCMDesignService;
import ccm.server.business.ICCMDocumentService;
import ccm.server.business.IHierarchyService;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
import ccm.server.enums.operator;
import ccm.server.models.query.QueryRequest;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.utils.DataRetrieveUtils;
import ccm.server.utils.DocumentUtils;
import ccm.server.utils.HierarchyUtils;
import ccm.server.utils.ObjectDTOUtility;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/22 9:01
 */
@Service
public class CCMDesignServiceImpl implements ICCMDesignService {

    @Autowired
    private ICCMDocumentService documentService;

    @Autowired
    private IHierarchyService hierarchyService;

    /* ******************************************************* 设计数据-树方法 Start ******************************************************* */


    @Override
    public List<ObjectDTO> getDocumentFormPropertiesForConfigurationItem() throws Exception {
        return hierarchyService.getObjectFormPropertiesForConfigurationItem(DocumentUtils.CCM_DOCUMENT);
    }

    @Override
    public Map<String, Object> getDocumentHierarchyConfigurationFormWithItem(String formPurpose) throws Exception {
        return hierarchyService.getObjectHierarchyConfigurationFormWithItem(formPurpose, DocumentUtils.CCM_DOCUMENT);
    }

    @Override
    public ObjectDTO getHierarchyConfigurationForm(String formPurpose) throws Exception {
        return hierarchyService.getObjectHierarchyConfigurationForm(formPurpose, DocumentUtils.CCM_DOCUMENT);
    }

    @Override
    public IObject createHierarchyConfiguration(JSONObject requestBody) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject hierarchyConfiguration = hierarchyService.createHierarchyConfiguration(requestBody, DocumentUtils.CCM_DOCUMENT);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return hierarchyConfiguration;
    }

    @Override
    public IObject createHierarchyConfigurationWithItems(JSONObject requestBody) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject hierarchyConfigurationWithItems = hierarchyService.createHierarchyConfigurationWithItems(requestBody, DocumentUtils.CCM_DOCUMENT,false);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return hierarchyConfigurationWithItems;
    }

    @Override
    public void deleteHierarchyConfiguration(String hierarchyConfigurationOBID) throws Exception {
        hierarchyService.deleteHierarchyConfiguration(hierarchyConfigurationOBID);
    }

    @Override
    public void updateHierarchyConfiguration(ObjectDTO hierarchyConfiguration) throws Exception {
        hierarchyService.updateHierarchyConfiguration(hierarchyConfiguration);
    }

    @Override
    public IObjectCollection getMyHierarchyConfigurations(HttpServletRequest request, PageRequest pageRequest) throws Exception {
        return hierarchyService.getMyHierarchyConfigurations(request, DocumentUtils.CCM_DOCUMENT, pageRequest);
    }

    @Override
    public IObjectCollection getHierarchyConfigurationItems(String hierarchyConfigurationOBID) throws Exception {
        return hierarchyService.getItemsByHierarchyConfigurationOBID(hierarchyConfigurationOBID);
    }

    @Override
    public ObjectDTO getHierarchyConfigurationItemForm(String formPurpose) throws Exception {
        return hierarchyService.getHierarchyConfigurationItemForm(formPurpose);
    }

    @Override
    public IObject createHierarchyConfigurationItemByConfigurationOBID(String configurationOBID, ObjectDTO toCreateItem) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject hierarchyConfigurationItemWithConfigurationOBID = hierarchyService.createHierarchyConfigurationItemWithConfigurationOBID(configurationOBID, toCreateItem);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return hierarchyConfigurationItemWithConfigurationOBID;
    }

    @Override
    public void deleteHierarchyConfigurationItem(String itemOBID) throws Exception {
        hierarchyService.deleteHierarchyConfigurationItem(itemOBID);
    }

    @Override
    public void updateHierarchyConfigurationItem(ObjectDTO hierarchyConfigurationItem) throws Exception {
        hierarchyService.updateHierarchyConfigurationItem(hierarchyConfigurationItem);
    }

    @Override
    public HierarchyObjectDTO generateHierarchy(String hierarchyConfigurationOBID) throws Exception {
        ObjectDTO hierarchyConfiguration;
        // 例 阶段->专业->图纸类型
        if (StringUtils.isEmpty(hierarchyConfigurationOBID)) {
            // 获取默认内置配置
            hierarchyConfiguration = hierarchyService.getDefaultHierarchyConfiguration(DocumentUtils.CCM_DOCUMENT).toObjectDTO();
        } else {
            hierarchyConfiguration = hierarchyService.getHierarchyConfigurationByOBID(hierarchyConfigurationOBID).toObjectDTO();
        }
        IObjectCollection hierarchyConfigurationItemsCollection = getHierarchyConfigurationItems(hierarchyConfiguration.getObid());
        List<ObjectDTO> itemsDTOs = ObjectDTOUtility.convertToObjectDTOList(hierarchyConfigurationItemsCollection);
        List<ObjectDTO> hierarchyConfigurationItems = HierarchyUtils.sortByHierarchyLevel(itemsDTOs);

        HierarchyObjectDTO rootTree = new HierarchyObjectDTO();
        List<HierarchyObjectDTO> children = rootTree.getChildren();
        // 旧树生成方法
        /*List<ObjectDTO> topEnumListTypes = new ArrayList<>();
        IObjectCollection allDocuments = documentService.getDocumentsWithPage(new PageRequest(0, 0));
        List<ObjectDTO> allDocumentDTOs = ObjectDTOUtility.convertToObjectDTOList(allDocuments);
        HierarchyUtils.generateTreeByObjectsAndConfiguration(children, allDocumentDTOs, hierarchyConfigurationItems, topEnumListTypes);*/
        // 2022.08.04 HT 替换新树生成方法
        List<String> classDefUIDList = new ArrayList<>();
        classDefUIDList.add(DocumentUtils.CCM_DOCUMENT);
        HierarchyUtils.generateTreeByConfiguration(children, classDefUIDList, hierarchyConfigurationItems);
        return rootTree;
    }

    @Deprecated
    @Override
    public HierarchyObjectDTO generateHierarchyByDocumentsAndConfiguration(String hierarchyConfigurationOBID) throws Exception {
        return generateHierarchy(hierarchyConfigurationOBID);
    }

    @Override
    public IObjectCollection getDocumentsFromHierarchyNode(HierarchyObjectDTO selectedNode, PageRequest pageRequest) throws Exception {
        List<HierarchyObjectDTO> parents = HierarchyUtils.getParents(new ArrayList<>(), selectedNode);
        ObjectDTO objectDTO = new ObjectDTO();
        for (HierarchyObjectDTO parent : parents) {
            ObjectItemDTO objectItemDTO = new ObjectItemDTO();
            objectItemDTO.setDefUID(parent.getName());
            objectItemDTO.setDisplayValue(parent.getId() + ":" + parent.getName());
            objectDTO.add(objectItemDTO);
        }
        return documentService.getAllDocumentsWithItems(objectDTO, pageRequest);

    }

    /* ******************************************************* 设计数据-树方法 End ******************************************************* */
    /* ******************************************************* 设计数据方法 Start ******************************************************* */
    @Override
    public IObject getComponentByOBID(String obid) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DataRetrieveUtils.I_COMPONENT);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, obid);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }
    /* ******************************************************* 设计数据方法 End ******************************************************* */
}
