package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
import ccm.server.enums.PackageRevProcessingMode;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
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
 * @since 2022/1/11 14:35
 */
public interface ICCMWorkPackageService {

    /* ******************************************************* 工作包方法 Start ******************************************************* */
    ObjectDTO getWorkPackageForm(String formPurpose) throws Exception;

    IObjectCollection getWorkPackages(PageRequest pageRequest) throws Exception;

    IObjectCollection getWorkPackagesWithItems(ObjectDTO items, PageRequest pageRequest) throws Exception;

    IObject getWorkPackageByOBID(String obid) throws Exception;

    IObject getWorkPackageByUID(String uid) throws Exception;

    IObject getWorkPackagesByName(String name) throws Exception;

    void updateWorkPackage(ObjectDTO objectDTO) throws Exception;

    void deleteWorkPackage(String workPackageOBID) throws Exception;

    IObject createWorkPackage(ObjectDTO toCreateWorkPackage) throws Exception;

    IObject createWorkPackageWithRelFromTaskPackage(String taskPackageOBID, ObjectDTO toCreateWorkPackage) throws Exception;

    /**
     * 移除任务包和工作包关联
     *
     * @param taskPackageOBIDs
     * @param workPackageOBID
     * @throws Exception
     */
    void removeTaskPackage2WorkPackageRel(String taskPackageOBIDs, String workPackageOBID) throws Exception;

    /**
     * 更新工作包状态
     *
     * @param obid
     * @param twpStatus
     * @throws Exception
     */
    void updateTWPStatus(String obid, String twpStatus) throws Exception;

    void updateTWPStatusByUID(String uid, String twpStatus) throws Exception;

    /* ******************************************************* 工作包方法 End ******************************************************* */
    /* ******************************************************* 工作包-图纸方法 Start ******************************************************* */
    void assignDocumentsToWorkPackage(String workPackageOBID, List<String> documentOBIDs) throws Exception;

    IObjectCollection getRelatedDocuments(String workPackageOBID) throws Exception;

    IObjectCollection getRelatedRevisedDocuments(String obid, PageRequest pageRequest) throws Exception;

    IObjectCollection getSelectableDocuments(String obid, FiltersParam filtersParam, OrderByParam orderByParam, PageRequest pageRequest) throws Exception;

    Boolean removeDocumentsFromWorkPackage(String packageId, String documentIds) throws Exception;

    /* ******************************************************* 工作包-图纸方法 End ******************************************************* */
    /* *****************************************  工作包材料方法 start  ***************************************** */
    IObjectCollection getRelatedComponents(String workPackageOBID) throws Exception;

    /**
     * 获取和工作包相同阶段并且有材料消耗的设计数据
     *
     * @param workPackageOBID
     * @return
     * @throws Exception
     */
    IObjectCollection getDesignDataByPurposeAndConsumeMaterial(String workPackageOBID, String classDefinitionUID, PageRequest pageRequest) throws Exception;

    /**
     * 验证是否存在工作包相同阶段并且有材料消耗的设计数据
     *
     * @param taskPackageOBID
     * @return
     * @throws Exception
     */
    IObjectCollection verifyDesignDataByPurposeAndConsumeMaterial(String taskPackageOBID, String classDefinitionUID, PageRequest pageRequest, boolean needConsumeMaterial) throws Exception;

    Boolean removeComponentsUnderWorkPackage(String packageId, String componentIds) throws Exception;

    /* *****************************************  工作包材料方法 end  ***************************************** */
    /* *****************************************  工作包工作步骤方法 start  ***************************************** */
    IObjectCollection getRelatedWorkStep(String workPackageOBID) throws Exception;

    void removeWorkStepUnderWorkPackage(String workPackageOBID, String workStepOBIDs) throws Exception;

    /**
     * 刷新工作步骤
     *
     * @param packageOBID
     * @return
     * @throws Exception
     */
    void refreshWorkStep(String packageOBID) throws Exception;

    /* *****************************************  工作包工作步骤方法 end  ***************************************** */
    /* *****************************************  资源方法 start  ***************************************** */
    ObjectDTO getResourcesForm(String formPurpose) throws Exception;

    IObjectCollection getRelatedResources(String workPackageOBID) throws Exception;

    IObject createResources(String packageId, ObjectDTO resources) throws Exception;

    Boolean deleteResources(String resourcesIds) throws Exception;

    /* *****************************************  资源方法 end  ***************************************** */
    /* ******************************************************* 工作包-树方法 Start ******************************************************* */
    List<ObjectDTO> getWorkPackageFormPropertiesForConfigurationItem() throws Exception;

    Map<String, Object> getWorkPackageHierarchyConfigurationFormWithItem(String formPurpose) throws Exception;

    IObject createWorkPackageHierarchyConfigurationWithItems(JSONObject requestBody) throws Exception;

    void deleteWorkPackageHierarchyConfiguration(String obid) throws Exception;

    void updateWorkPackageHierarchyConfiguration(ObjectDTO hierarchyConfiguration) throws Exception;

    IObjectCollection getMyWorkPackageHierarchyConfigurations(HttpServletRequest request, PageRequest pageRequest) throws Exception;

    IObjectCollection getWorkPackageHierarchyConfigurationItems(String obid) throws Exception;

    IObject createWorkPackageHierarchyConfigurationItem(String hierarchyConfigurationOBID, ObjectDTO
            hierarchyConfigurationItemDTO) throws Exception;

    HierarchyObjectDTO generateHierarchyByWorkPackagesAndConfiguration(String hierarchyConfigurationOBID) throws Exception;

    IObjectCollection getWorkPackagesFromHierarchyNode(HierarchyObjectDTO selectedNode, PageRequest pageRequest) throws Exception;

    /* ******************************************************* 工作包-树方法 End ******************************************************* */
    /* ******************************************************* 工作包-父计划方法 Start ******************************************************* */
    Map<String, Object> getWorkPackageFatherPlan(String packageId) throws Exception;

    Double refreshPlanWeight(String packageOBID) throws Exception;

    /**
     * 刷新进度
     *
     * @param packageOBID
     * @return
     * @throws Exception
     */
    Double refreshProgress(String packageOBID) throws Exception;
    /* ******************************************************* 工作包-树方法End ******************************************************* */
    /* ******************************************************* 工作包-预测预留方法 Start ******************************************************* */

    /**
     * 检测图纸 并预测/预留 获取预测数据
     *
     * @param projectId   项目号
     * @param requestName 工作包名称
     * @param requestType FR是预测,RR是预留
     * @throws Exception
     */
    Map<String, Object> existAndCreateNewStatusRequest(String projectId, String requestName, String requestType,
                                                       String searchColumn, String searchValue) throws Exception;

    /**
     * 检测图纸 并部分预测/预留 获取预测数据
     *
     * @param projectId   项目号
     * @param requestName 工作包名称
     * @param requestType FR是预测,RR是预留
     * @throws Exception
     */
    Map<String, Object> existAndCreatePartialStatusRequest(String packageId, String projectId,
                                                           String requestName, String requestType, String drawingNumbers,
                                                           String searchColumn, String searchValue) throws Exception;

    /**
     * 检测图纸 并部分预测/预留 获取预测数据 33
     *
     * @param projectId    项目号
     * @param requestName  工作包名称
     * @param requestType  FR是预测,RR是预留
     * @param searchColumn
     * @param searchValue
     * @throws Exception
     */
    Map<String, Object> existAndCreatePartialStatusRequest33(String packageId, String projectId, String requestName, String requestType,String warehouses,
                                                             String drawingNumbers, String searchColumn, String searchValue) throws Exception;

    /**
     * 按阶段进行材料预测预留
     *
     * @param projectId      项目ID
     * @param requestName    TWP编号
     * @param requestType    FR是预测，RR是预留
     * @param lpAttrCode     SPM中阶段对于的属性字段
     * @param drawingNumbers 图纸号集合数组
     * @return
     * @throws Exception
     */
    Map<String, Object> createFAWithExtraFilter(String packageId, String projectId, String lpAttrCode,
                                                String requestName, String requestType, String drawingNumbers,
                                                String searchColumn, String searchValue) throws Exception;
    /* ******************************************************* 工作包-预测预留方法 End ******************************************************* */
    /* ******************************************************* 工作包-升版方法 Start ******************************************************* */

    /**
     * 工作包升版处理
     *
     * @param workPackageOBID
     * @param mode
     * @throws Exception
     */
    void workPackageRevisionHandler(String workPackageOBID, PackageRevProcessingMode mode) throws Exception;
    /* ******************************************************* 工作包-升版方法 End ******************************************************* */
}
