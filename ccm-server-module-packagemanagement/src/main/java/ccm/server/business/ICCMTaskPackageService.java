package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
import ccm.server.enums.PackageRevProcessingMode;
import ccm.server.enums.operationPurpose;
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
 * @since 2022/1/11 14:36
 */
public interface ICCMTaskPackageService {

    /* ******************************************************* 任务包方法 Start ******************************************************* */
    IObject getTaskPackageForm(operationPurpose formPurpose, ObjectDTO mainObject) throws Exception;

    IObjectCollection getTaskPackages(PageRequest pageRequest) throws Exception;

    IObject getTaskPackagesByUID(String uid) throws Exception;

    IObject getTaskPackagesByOBID(String obid) throws Exception;

    IObjectCollection getTaskPackagesWithItems(ObjectDTO items, PageRequest pageRequest) throws Exception;

    void updateTaskPackage(ObjectDTO objectDTO) throws Exception;

    void deleteTaskPackage(String taskPackageOBID) throws Exception;

    IObject createTaskPackage(ObjectDTO toCreateTaskPackage) throws Exception;

    /**
     * 更新任务包状态
     *
     * @param obid
     * @param ttpStatus
     * @throws Exception
     */
    void updateTTPStatus(String obid, String ttpStatus) throws Exception;

    void updateTTPStatusByUID(String uid, String ttpStatus) throws Exception;

    /**
     * 获取任务包同施工阶段并且有材料消耗的工作步骤
     *
     * @return
     * @throws Exception
     */
    List<IObject> getWorkStepsWithSamePurposeAndConsumeMaterial(IObject taskPackages) throws Exception;

    /* ******************************************************* 任务包方法 End ******************************************************* */
    /* ******************************************************* 任务包-材料方法 Start ******************************************************* */

    /**
     * 获取和任务包相同阶段并且有材料消耗的设计数据
     *
     * @param taskPackageOBID
     * @return
     * @throws Exception
     */
    IObjectCollection getDesignDataByPurposeAndConsumeMaterial(String taskPackageOBID, String classDefinitionUID, PageRequest pageRequest) throws Exception;

    /**
     * 验证是否存在和任务包相同阶段并且有材料消耗的设计数据
     *
     * @param taskPackageOBID
     * @return
     * @throws Exception
     */
    IObjectCollection verifyDesignDataByPurposeAndConsumeMaterial(String taskPackageOBID, String classDefinitionUID, PageRequest pageRequest, boolean needConsumeMaterial) throws Exception;

    /* ******************************************************* 任务包-材料方法 End ******************************************************* */
    /* ******************************************************* 任务包-图纸方法 Start ******************************************************* */
    IObjectCollection getRelatedRevisedDocuments(String obid, PageRequest pageRequest) throws Exception;

    IObjectCollection getSelectableDocumentsForTaskPackage(String obid, FiltersParam filtersParam, OrderByParam orderByParam, PageRequest pageRequest) throws Exception;

    void assignDocumentsToTaskPackage(String taskPackageOBID, List<ObjectDTO> providedDocuments) throws Exception;

    IObjectCollection getRelatedDocuments(String taskPackageOBID) throws Exception;

    IObject getDocumentForm(operationPurpose formPurpose, ObjectDTO mainObject) throws Exception;

    Boolean removeDocumentsFromTaskPackage(String packageId, String documentIds) throws Exception;

    /* ******************************************************* 任务包-图纸方法 End ******************************************************* */
    /* ******************************************************* 任务包-树方法 Start ******************************************************* */
    IObject getTaskPackageHierarchyConfigurationForm(operationPurpose formPurpose, ObjectDTO existItemForInfoAndUpdatePurpose) throws Exception;

    List<ObjectDTO> getTaskPackageFormPropertiesForConfigurationItem() throws Exception;

    Map<String, Object> getTaskPackageHierarchyConfigurationFormWithItem(String formPurpose) throws Exception;

    IObject createTaskPackageHierarchyConfigurationWithItems(JSONObject requestBody) throws Exception;

    void deleteTaskPackageHierarchyConfiguration(String obid) throws Exception;

    void updateTaskPackageHierarchyConfiguration(ObjectDTO hierarchyConfiguration) throws Exception;

    IObjectCollection getMyTaskPackageHierarchyConfigurations(HttpServletRequest request, PageRequest pageRequest) throws Exception;

    IObjectCollection getTaskPackageHierarchyConfigurationItems(String obid) throws Exception;

    IObject createTaskPackageHierarchyConfigurationItem(String hierarchyConfigurationOBID, ObjectDTO
            hierarchyConfigurationItemDTO) throws Exception;

    HierarchyObjectDTO generateHierarchyByTaskPackagesAndConfiguration(String hierarchyConfigurationOBID) throws Exception;

    IObjectCollection getTaskPackagesFromHierarchyNode(HierarchyObjectDTO selectedNode, PageRequest pageRequest) throws Exception;

    /* ******************************************************* 任务包-树方法 End ******************************************************* */
    /* ******************************************************* 任务包-父计划方法 Start ******************************************************* */
    Map<String, Object> getTaskPackageFatherPlan(String packageId) throws Exception;

    Double refreshPlanWeight(String packageOBID) throws Exception;

    /**
     * 刷新进度
     *
     * @param packageOBID
     * @return
     * @throws Exception
     */
    @Deprecated
    Double refreshProgress(String packageOBID) throws Exception;

    /**
     * 新刷新进度方法
     *
     * @param packageOBID
     * @throws Exception
     */
    Double newRefreshProgress(String packageOBID) throws Exception;
    /* ******************************************************* 任务包-父计划方法 End ******************************************************* */
    /* ******************************************************* 任务包-预测预留方法 Start ******************************************************* */

    /**
     * 检测图纸 并预测/预留 获取预测数据
     *
     * @param projectId   项目号
     * @param requestName 任务包名称
     * @param requestType FR是预测,RR是预留
     * @throws Exception
     */
    Map<String, Object> existAndCreateNewStatusRequest(String projectId, String requestName, String requestType,
                                                       String searchColumn, String searchValue) throws Exception;

    /**
     * 检测图纸 并部分预测/预留 获取预测数据
     *
     * @param packageId    任务包OBID
     * @param projectId    项目号
     * @param requestName  预测单号
     * @param requestType  FR是预测,RR是预留
     * @param searchColumn
     * @param searchValue
     * @throws Exception
     */
    Map<String, Object> existAndCreatePartialStatusRequest(String packageId, String projectId,
                                                           String requestName, String requestType, String drawingNumbers,
                                                           String searchColumn, String searchValue) throws Exception;

    /**
     * 检测图纸 并部分预测/预留 获取预测数据 33
     *
     * @param packageId    任务包OBID
     * @param projectId    项目号
     * @param requestName  预测单号
     * @param requestType  FR是预测,RR是预留
     * @param searchColumn
     * @param searchValue
     * @throws Exception
     */
    Map<String, Object> existAndCreatePartialStatusRequest33(String packageId, String projectId,
                                                             String requestName, String requestType, String warehouses,
                                                             String drawingNumbers,
                                                             String searchColumn, String searchValue) throws Exception;

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
    /* ******************************************************* 任务包-预测预留方法 End ******************************************************* */

    /* ******************************************************* 任务包-升版方法 Start ******************************************************* */

    /**
     * 任务包升版处理
     *
     * @param taskPackageOBID
     * @param mode
     * @throws Exception
     */
    void taskPackageRevisionHandler(String taskPackageOBID, PackageRevProcessingMode mode) throws Exception;
    /* ******************************************************* 任务包-升版方法 End ******************************************************* */
}
